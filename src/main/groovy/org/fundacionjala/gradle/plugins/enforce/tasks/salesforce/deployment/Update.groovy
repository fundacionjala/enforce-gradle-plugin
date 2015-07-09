/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentStates
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager.PackageGenerator

import java.nio.file.Paths

/**
 * Updates an org using metadata API
 */
class Update extends Deployment {
    ArrayList<File> filesToCopy
    ArrayList<File> filesToUpdate
    String folders = ""
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
        taskFolderName = Constants.DIR_UPDATE_FOLDER
    }

    /**
     * Executes the task
     */
    @Override
    void runTask() {
        createDeploymentDirectory(taskFolderPath)
        loadFilesChanged()
        filterFiles()
        showFilesChanged()
        if(isEmptyChangedFiles()) {
            return
        }
        createDestructive()
        createPackage()
        copyFilesChanged()
        showFilesExcludes()
        truncate()
        executeDeploy(taskFolderPath, Constants.START_UPDATE_TASK_MESSAGE, Constants.SUCCESS_UPDATE_TASK_MESSAGE)
        packageGenerator.saveFileTrackerMap()
    }

    def truncate() {
        interceptorsToExecute += interceptors
        truncateComponents(taskFolderPath)
    }

    /**
     * Creates packages to all files which has been changed
     */
    void createPackage() {
        packageGenerator.fileTrackerMap.each { nameFile, resultTracker ->
            if (resultTracker.state != ComponentStates.DELETED) {
                filesToCopy.add(new File(Paths.get(projectPath, nameFile).toString()))
            }
        }
        packageGenerator.buildPackage(taskPackagePath)
        combinePackageToUpdate(taskPackagePath)
    }

    /**
     * Creates package to all files which has been deleted
     */
    def createDestructive() {
        String destructivePath = Paths.get(taskFolderPath, Constants.FILE_NAME_DESTRUCTIVE).toString()
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
        copyFilesToTaskDirectory(filesToUpdate)
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

    /**
     * Verifies if there are not changed files
     * @return boolean
     */
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

    public void filterFiles() {
        ArrayList<File> files = packageGenerator.getFiles(projectPath)
        String includes = Util.getIncludesValueByFolderFromFilesUpdated(files, folders, projectPath)
        ArrayList<File> filesFiltered = filter.getFiles(includes, excludes)
        packageGenerator.updateFileTracker(filesFiltered)
        filesExcludes = files - filesFiltered

    }

    public void loadParameters() {
        Map <String, String> parameters = getParameterWithTheirsValues([Constants.PARAMETER_EXCLUDES, Constants.PARAMETER_FOLDERS])
        if (parameters.containsKey(Constants.PARAMETER_EXCLUDES)) {
            excludes = parameters.get(Constants.PARAMETER_EXCLUDES)
        }

        if (parameters.containsKey(Constants.PARAMETER_FOLDERS)) {
            folders = parameters.get(Constants.PARAMETER_FOLDERS)
        }
    }
}