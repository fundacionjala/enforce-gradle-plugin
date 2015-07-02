/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.undeploy.SmartFilesValidator
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.filter.Filter
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.QueryBuilder
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.ToolingAPI

import java.nio.file.Paths

/**
 * Deletes files into an org using metadata API
 */
class Delete extends Deployment {
    public String pathDelete
    public ArrayList<File> filesToDeleted
    public String files, excludes

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    Delete() {
        super(Constants.DESCRIPTION_DELETE_TASK, Constants.DEPLOYMENT)
        filesToDeleted = new ArrayList<File>()
    }

    /**
     * Executes the task
     */
    @Override
    void runTask() {
        pathDelete = Paths.get(buildFolderPath, Constants.DIR_DELETE_FOLDER).toString()
        componentDeploy.startMessage = Constants.START_DELETE_TASK
        componentDeploy.successMessage = Constants.SUCCESSFULLY_DELETE_TASK
        createDeploymentDirectory(pathDelete)

        loadParameters(project.properties as Map<String, String>)
        addFiles()
        showFilesToDelete()

        if( System.console().readLine("\n"+Constants.QUESTION_CONTINUE_DELETE) == Constants.YES_OPTION ) {
            createDestructive()
            createPackageEmpty()
            executeDeploy(pathDelete)
        }
        else {
            logger.quiet(Constants.PROCCES_DELETE_CANCELLED)
        }
    }


    /**
     * Gets all task parameters
     * @param properties the task properties
     * @return A map of all task parameters
     */
    void loadParameters(Map<String, String> properties) {
        if (Util.isValidProperty(properties, Constants.PARAMETER_FILES)) {
            files = properties[Constants.PARAMETER_FILES]
        }
        if (Util.isValidProperty(properties, Constants.PARAMETER_EXCLUDES)) {
            excludes = properties[Constants.PARAMETER_EXCLUDES]
        }
    }

    /**
     * Adds all files into an org
     */
    def addFiles() {
        Filter filter = new Filter(project,projectPath)
        filesToDeleted = filter.getFiles(files, excludes)
    }

    /**
     * Filter the files into Org
     */
    def validateFilesInOrg() {
        ToolingAPI toolingAPI = new ToolingAPI(credential)
        QueryBuilder queryBuilder = new QueryBuilder()
        ArrayList<String> jsonQueries = []
        if(!parameters.get(Constants.PARAMETER_VALIDATE_ORG).equals(Constants.FALSE_OPTION)) {
            queryBuilder.createQueryFromPackage(projectPackagePath).each { query ->
                jsonQueries.push(toolingAPI.httpAPIClient.executeQuery(query as String))
            }
            SmartFilesValidator smartFilesValidator = new SmartFilesValidator(jsonQueries)
            filesToDeleted = smartFilesValidator.filterFilesAccordingOrganization(filesToDeleted, projectPath)
        }
    }

    /**
     * Shows files to delete
     */
    def showFilesToDelete() {
        def limit = 15
        ArrayList<File> showFiles = filesToDeleted.findAll { File file ->
            !file.getName().endsWith("xml")
        }
        def numComponentes = showFiles.size()

        logger.quiet("*********************************************")
        logger.quiet("            Components to delete             ")
        logger.quiet("*********************************************")
        if(numComponentes == 0) {
            logger.quiet(Constants.NOT_FILES_DELETED)
        }
        else if(numComponentes > limit) {
            showFiles.groupBy { File file ->
                file.getParentFile().getName()
            }.each { group, files ->
                logger.quiet("[ " + files.size() + " ] " + group)
            }
            logger.quiet(numComponentes+" components")
        }
        else {
            showFiles.each { File file ->
                logger.quiet( Util.getRelativePath(file, projectPath))
            }
        }
        logger.quiet("*********************************************")
    }

    /**
     * Creates packages to all files which has been deleted
     */
    def createDestructive() {
        String destructivePath = Paths.get(pathDelete, PACKAGE_NAME_DESTRUCTIVE).toString()
        writePackage(destructivePath, filesToDeleted)
        combinePackageToUpdate(destructivePath)
    }

    /**
     * Create a package empty
     */
    def createPackageEmpty() {
        writePackage(Paths.get(pathDelete, PACKAGE_NAME).toString(), [])
    }
}