/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentStates
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageCombiner
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageGenerator

import java.nio.file.Paths

/**
 * Updates an org using metadata API
 */
class Update extends Deployment {
    public String pathUpdate
    public String updatePackagePath
    ArrayList<File> filesToCopy
    ArrayList<File> filesToUpdate
    String folders
    ArrayList<File> filesExcludes
    PackageGenerator packageGenerator

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    Update() {
        super(Constants.UPDATE_DESCRIPTION, Constants.DEPLOYMENT)
        filesToCopy = new ArrayList<File>()
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
        pathUpdate = Paths.get(buildFolderPath, Constants.DIR_UPDATE_FOLDER).toString()
        updatePackagePath = Paths.get(pathUpdate, PACKAGE_NAME).toString()
        createDeploymentDirectory(pathUpdate)
        loadFilesChanged()
        verifyParameter()
        excludeFilesFromFilesChanged()
        showFilesChanged()
        if(isEmptyChangedFiles()) {
            return;
        }
        createDestructive()
        createPackage()
        copyFilesChanged()
        showFilesExcludes()
        truncate()
        executeDeploy(pathUpdate)
        packageGenerator.saveFileTrackerMap()
    }

    def truncate() {
        interceptorsToExecute += interceptors
        truncateComponents(pathUpdate)
    }

    /**
     * Creates packages to all files which has been changed
     */
    def createPackage() {
        packageGenerator.fileTrackerMap.each { nameFile, resultTracker ->
            if (resultTracker.state != ComponentStates.DELETED) {
                filesToCopy.add(new File(Paths.get(projectPath, nameFile).toString()))
            }
        }
        packageGenerator.buildPackage(updatePackagePath)
        combinePackageToUpdate(updatePackagePath)
    }

    /**
     * Creates package to all files which has been deleted
     */
    def createDestructive() {
        String destructivePath = Paths.get(pathUpdate, Constants.FILE_NAME_DESTRUCTIVE).toString()
        packageGenerator.buildDestructive(destructivePath)
        combinePackageToUpdate(destructivePath)
    }

    /**
     * Loads all files which has been changed on filesChanged
     */
    def loadFilesChanged() {
        ArrayList<File> validatedFiles = fileManager.getValidElements(projectPath, excludeFilesToMonitor)
        packageGenerator.init(projectPath, validatedFiles, credential)
    }

    /**
     * Verifies if there is files changed in folders inserted by user
     */
    def verifyParameter() {
        if (Util.isValidProperty(project, Constants.FOLDERS_DEPLOY)) {
            folders = project.folders
        }

        if (folders) {
            ArrayList<String> foldersName = folders.split(Constants.COMMA)
            ArrayList<String> invalidFolders = Util.getInvalidFolders(foldersName)
            validateFolders(foldersName)
            if (!invalidFolders.empty) {
                throw new Exception("${Constants.INVALID_FOLDER}: ${invalidFolders}")
            }
            packageGenerator.updateFileTrackerMap(foldersName)
        }
    }

    /**
     * Copies files using fileManager
     */
    def copyFilesChanged() {
        filesToCopy.each { file ->
            File xmlFile = fileManager.getValidateXmlFile(file)
            File xmlFolder = fileManager.getValidateXmlFile(file.getParentFile())
            if (xmlFile) {
                filesToUpdate.push(xmlFile)
            }
            if (xmlFolder) {
                filesToUpdate.push(xmlFolder)
            }
            filesToUpdate.push(file)
        }
        fileManager.copy(projectPath, filesToUpdate, pathUpdate)
    }

    /**
     * Prints files changed
     */
    public void showFilesChanged() {
        if (isEmptyChangedFiles()) {
            logger.quiet(Constants.NOT_FILES_CHANGED)
        }
        if (packageGenerator.fileTrackerMap.size() > 0) {
            logger.quiet("*********************************************")
            logger.quiet("              Status Files Changed             ")
            logger.quiet("*********************************************")
            packageGenerator.fileTrackerMap.each { nameFile, status ->
                logger.quiet("${Paths.get(nameFile).getFileName().toString()}${" - "}${status.toString()}")
            }
            logger.quiet("*********************************************")
        }
    }

    private boolean isEmptyChangedFiles() {
        if(packageGenerator.fileTrackerMap.isEmpty()) {
            return true
        }
        return false
    }

    /**
     * Shows files excluded if there are more than five this shows just a message.
     * @return
     */
    def showFilesExcludes() {
        if (filesExcludes.empty) {
            return
        }
        if (filesExcludes.size() < 5) {
            logger.quiet("*********************************************")
            logger.quiet("              Files excluded                 ")
            logger.quiet("*********************************************")
            filesExcludes.each { File file ->
                logger.quiet("${file.getName()}${" - "} excluded")
            }
            logger.quiet("*********************************************")
        } else {
            logger.quiet("${filesExcludes.size()}${' files were excluded\n'}")
        }
    }

    /**
     * ExcludeFiles from filesExcludes map
     */
    private void excludeFilesFromFilesChanged() {
        ArrayList<File> files = packageGenerator.getFiles(projectPath)
        ArrayList<File> filesFiltered = excludeFiles(files)
        packageGenerator.updateFileTracker(filesFiltered)
        filesExcludes = files - filesFiltered
    }
}