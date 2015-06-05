/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import java.nio.file.Paths

/**
 * Updates an org using metadata API
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
        createDeploymentDirectory(pathDelete)
        addAllFiles()
        addFoldersToDeleteFiles()
        addFilesToDelete()
        excludeFilesToDelete()
        showFilesToDelete()

        if( System.console().readLine(Constants.QUESTION_CONTINUE) == Constants.YES_OPTION ) {
            createDestructive()
            createPackage()
            executeDeploy(pathDelete)
        }
    }

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
        logger.quiet("\nFILES TO DELETE\n")
        filesToDeleted.each { file->
            println "->"+file
        }
        logger.quiet(filesToDeleted.size() + " files \n")
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
        writePackage(Paths.get(pathDelete, PACKAGE_NAME_DESTRUCTIVE).toString(), filesToDeleted)
    }

    /**
     * Create a package empty
     */
    def createPackage() {
        writePackage(Paths.get(pathDelete, PACKAGE_NAME).toString(), [])
    }
}