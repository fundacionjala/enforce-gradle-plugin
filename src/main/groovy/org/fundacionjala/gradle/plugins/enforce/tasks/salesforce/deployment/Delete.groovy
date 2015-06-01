/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentStates
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
    private static final String PARAMETER_FOLDERS= "folders"
    private final String FILE_NAME_DESTRUCTIVE = "destructiveChanges.xml"
    public String pathUpdate
    ArrayList<File> filesToUpdate
    String folders
    ArrayList<File> filesExcludes
    PackageGenerator packageGenerator

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    Delete() {
        super(DESCRIPTION_OF_TASK, Constants.DEPLOYMENT)
        filesToUpdate = new ArrayList<File>()
        filesExcludes = new ArrayList<File>()
        packageGenerator = new PackageGenerator()
        interceptorsToExecute = [org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.REMOVE_DEPRECATE.id]
    }

    /**
     * Executes the task
     */
    @Override
    void runTask() {
        pathUpdate = Paths.get(buildFolderPath, DIR_DELETE_FOLDER).toString()
        createDeploymentDirectory(pathUpdate)
        loadFilesChanged()
        verifyParameter()
        excludeFilesFromFilesChanged()
        createDestructive()
        createPackage()
//        executeDeploy(pathUpdate)
    }

    def truncate(String pathToTruncate) {
        interceptorsToExecute += interceptors
        truncateComponents(pathToTruncate)
    }

    /**
     * Creates packages to all files which has been changed
     */
    def createPackage() {
        packageGenerator.buildPackage(Paths.get(pathUpdate, PACKAGE_NAME).toString())
    }

    /**
     * Creates package to all files which has been deleted
     */
    def createDestructive() {
        ArrayList<File> files = new ArrayList<File>();
        packageGenerator.fileTrackerMap.each { nameFile, resultTracker ->
            if (resultTracker.state == ComponentStates.DELETED) {
                files.add(new File(Paths.get(projectPath, nameFile).toString()))
            }
        }
        writePackage(Paths.get(pathUpdate, FILE_NAME_DESTRUCTIVE).toString(), files)
    }

    /**
     * Loads all files which has been changed on filesChanged
     */
    def loadFilesChanged() {
        packageGenerator.init(projectPath, credential)
    }

    /**
     * Verifies if there is files changed in folders inserted by user
     */
    def verifyParameter() {
        println "|*| : "+project
        println "|*| : "+Util.isValidProperty(project, PARAMETER_FOLDERS)
        if (Util.isValidProperty(project, PARAMETER_FOLDERS)) {
            folders = project.folders
        }
        println "|*| : "+folders
        ArrayList<File> validatedFiles
        if (folders) {
            ArrayList<String> foldersName = folders.split(Constants.COMMA)

            ArrayList<String> invalidFolders = Util.getInvalidFolders(foldersName)
            validateFolders(foldersName)
            if (!invalidFolders.empty) {
                throw new Exception("${Constants.INVALID_FOLDER}: ${invalidFolders}")
            }
            validatedFiles = fileManager.getValidElements(projectPath, excludeFilesToMonitor)
            packageGenerator.listFileToDelete(foldersName,validatedFiles)
        }

        println "File to delete : "+packageGenerator.fileTrackerMap
    }

    /**
     * ExcludeFiles from filesExcludes map
     */
    private void excludeFilesFromFilesChanged() {
        ArrayList<File> filesFiltered = excludeFiles(packageGenerator.getFiles())
        filesFiltered.add(new File("classes/Class2.cls"))
        filesExcludes = packageGenerator.excludeFiles(filesFiltered)
        println "\nFile to exclude : "+filesFiltered
        println "File to exclude : "+filesExcludes
    }


}