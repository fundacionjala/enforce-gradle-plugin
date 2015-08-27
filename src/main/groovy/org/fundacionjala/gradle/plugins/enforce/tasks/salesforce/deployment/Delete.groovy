/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.OrgValidator

/**
 * Deletes files into an org using metadata API
 */
class Delete extends Deployment {
    private static final String START_DELETE_TASK = 'Starting delete process...'
    private static final String SUCCESSFULLY_DELETE_TASK  = 'The files were successfully deleted!'
    private static final String DESCRIPTION_DELETE_TASK = "This task deploys just the files that were changed"
    private static final String DIR_DELETE_FOLDER = "delete"
    private static final String PROCESS_DELETE_CANCELLED = "The delete process was canceled"
    private static final String NOT_FILES_DELETED = "There are not files to delete"
    private static final String QUESTION_CONTINUE_DELETE = "Do you want delete this files from your organization? (y/n) :"

    public ArrayList<File> filesToDeleted
    public String files = ""

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    Delete() {
        super(DESCRIPTION_DELETE_TASK, Constants.DEPLOYMENT)
        taskFolderName = DIR_DELETE_FOLDER
        filesToDeleted = []
    }

    /**
     * Executes the task
     */
    @Override
    void runTask() {
        createDeploymentDirectory(taskFolderPath)
        loadClassifiedFiles(files, excludes)
        loadFilesToDelete()
        validateFilesInOrg()
        showFilesToDelete()
        if(super.isIntegrationMode() || (System.console().readLine("\n"+QUESTION_CONTINUE_DELETE) == Constants.YES_OPTION) ) {
            createDestructive()
            createPackageEmpty()
            executeDeploy(taskFolderPath, START_DELETE_TASK, SUCCESSFULLY_DELETE_TASK)
        }
        else {
            logger.quiet(PROCESS_DELETE_CANCELLED)
        }
    }

    /**
     * Initializes all task parameters
     * @param properties the task properties
     * @return A map of all task parameters
     */
    void loadParameters() {
        if (Util.isValidProperty(parameters, Constants.PARAMETER_FILES)) {
            files = parameters[Constants.PARAMETER_FILES]
        }
        if (Util.isValidProperty(parameters, Constants.PARAMETER_EXCLUDES)) {
            excludes = parameters[Constants.PARAMETER_EXCLUDES]
        }
    }

    /**
     * Adds all files into an org
     */
    def loadFilesToDelete() {
        File packageFile = new File(projectPackagePath)
        filesToDeleted = classifiedFile.validFiles
        filesToDeleted.remove(packageFile)
    }

    /**
     * Filter the files into Org
     */
    def validateFilesInOrg() {
        if(!parameters.get(Constants.PARAMETER_VALIDATE_ORG).equals(Constants.FALSE_OPTION)) {
            filesToDeleted = OrgValidator.getValidFiles(credential, filesToDeleted, projectPath)
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
        def numberOfComponents = showFiles.size()

        logger.quiet("*********************************************")
        logger.quiet("            Components to delete             ")
        logger.quiet("*********************************************")
        if(numberOfComponents == 0) {
            logger.quiet(NOT_FILES_DELETED)
        }
        else if(numberOfComponents > limit) {
            showFiles.groupBy { File file ->
                file.getParentFile().getName()
            }.each { group, files ->
                logger.quiet("[ " + files.size() + " ] " + group)
            }
            logger.quiet( "Total: ${numberOfComponents} components")
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
        writePackage(taskDestructivePath, filesToDeleted)
        combinePackageToUpdate(taskDestructivePath)
    }

    /**
     * Create a package empty
     */
    def createPackageEmpty() {
        writePackage(taskPackagePath, [])
    }
}