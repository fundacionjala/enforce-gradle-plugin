/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.file.Paths

/**
 * Deletes files into an org using metadata API
 */
class Delete extends Deployment {
    public String pathDelete
    public ArrayList<File> filesToDeleted

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
        addAllFiles()
        addFoldersToDeleteFiles()
        addFilesToDelete()
        excludeFilesToDelete()
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
     * Adds all files into an org
     */
    def addAllFiles() {
        filesToDeleted = addAllFilesInAFolder(filesToDeleted)
    }

    /**
     * Adds all files that are inside the folders
     */
    def addFoldersToDeleteFiles() {
        filesToDeleted = addFilesFromFolders(filesToDeleted)
    }

    /**
     * Adds files to file's list
     */
    def addFilesToDelete() {
        filesToDeleted = addFilesTo(filesToDeleted)
    }

    /**
     * Shows files to delete
     */
    def showFilesToDelete() {
        def numComponentes = 0;
        def limit = 15;
        def nameFolder = ""
        ArrayList<File> showFiles = new ArrayList<File>();
        filesToDeleted.each { File file ->
            if (!file.getName().endsWith("xml")) {
                numComponentes++
                showFiles.add(file)
            }
        }
        showFiles.sort{it.getAbsolutePath()}

        logger.quiet("*********************************************")
        logger.quiet("            Components to delete             ")
        logger.quiet("*********************************************")
        if(numComponentes == 0) {
            logger.quiet("There are not files deleted")
        }
        else if(numComponentes > limit) {
            def numFiles = 0;
            def fatherName;
            nameFolder = showFiles[0].getParentFile().getName()
            showFiles.each { File file ->
                fatherName = file.getParentFile().getName()
                if(!fatherName.equals(nameFolder)) {
                    logger.quiet("[ " + numFiles + " ] " + nameFolder)
                    nameFolder = fatherName
                    numFiles = 0;
                }
                numFiles++;
            }
            logger.quiet("[ " + numFiles + " ] " + fatherName)
            logger.quiet(numComponentes+" components")
        }
        else
        {
            showFiles.each { File file ->
                logger.quiet( Util.getRelativePath(file, projectPath))
            }
            logger.quiet(numComponentes+" components")
        }
        logger.quiet("*********************************************")

    }

    /**
     * Excludes Files from filesExcludes map
     */
    def excludeFilesToDelete() {
        filesToDeleted = excludeFiles(filesToDeleted)
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