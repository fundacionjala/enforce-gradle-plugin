/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageGenerator

import java.nio.file.Paths

/**
 * Updates an org using metadata API
 */
class Delete extends Deployment {
    private static final String DESCRIPTION_OF_TASK = "This task deploys just the files that were changed"
    private final String DIR_DELETE_FOLDER = "delete"
    private final String FILE_NAME_DESTRUCTIVE = "destructiveChanges.xml"
    private final String FILE_NAME_PACKAGE = "package.xml"
    public String pathDetele
    public String option
    public ArrayList<File> filesToDeleted

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    Delete() {
        super(DESCRIPTION_OF_TASK, Constants.DEPLOYMENT)
        filesToDeleted = new ArrayList<File>()
    }

    /**
     * Executes the task
     */
    @Override
    void runTask() {
        pathDetele = Paths.get(buildFolderPath, DIR_DELETE_FOLDER).toString()
        createDeploymentDirectory(pathDetele)
        addAllFiles()
        addFoldersToDeleteFiles()
        addFilesToDelete()
        excludeFilesToDelete()
        showFilesToDelete()

        option = System.console().readLine(Constants.QUESTION_CONTINUE)



        if( option == 'y' ) {
            createDestructive()
            createPackage()
//          executeDeploy(pathDetele)
        }
    }

    def addAllFiles() {
        filesToDeleted = addAllFilesInAFolder(filesToDeleted);
    }
    /**
     * Add all files that are inside the folders
     */
    def addFoldersToDeleteFiles() {
        filesToDeleted = addFilesFromFolders(filesToDeleted)
    }

    /**
     * Add files to file's list
     */
    def addFilesToDelete() {
        filesToDeleted = addFilesTo(filesToDeleted)
    }

    /**
     * Show files to delete
     */

    def showFilesToDelete() {
        println "\nFILES TO DELETE\n"
        filesToDeleted.each { file->
            println file
        }
        println filesToDeleted.size() + " files \n"
    }

    /**
     * ExcludeFiles from filesExcludes map
     */
    def excludeFilesToDelete() {
        filesToDeleted = excludeFiles(filesToDeleted)
    }

    /**
     * Creates package to all files which has been deleted
     */
    def createDestructive() {
        writePackage(Paths.get(pathDetele, FILE_NAME_DESTRUCTIVE).toString(), filesToDeleted)
    }

    /**
     * Creates packages to all files which has been changed
     */
    def createPackage() {
        writePackage(Paths.get(pathDetele, FILE_NAME_PACKAGE).toString(), [])
    }

}