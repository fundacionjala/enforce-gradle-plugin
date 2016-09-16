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
    private static final String UPDATE_DESCRIPTION = "This task deploys just the files that were changed"
    private static final String DIR_UPDATE_FOLDER = "update"
    private static final String NOT_FILES_CHANGED = "There are not files changed"
    private static final String START_UPDATE_TASK_MESSAGE = "Starting update proccess..."
    private static final String SUCCESS_UPDATE_TASK_MESSAGE = "The files were successfully updated!"
    ArrayList<File> filesToCopy
    Set<String> auraFolderToCopy
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
        super(UPDATE_DESCRIPTION, Constants.DEPLOYMENT)
        filesToCopy = []
        auraFolderToCopy = []
        filesToUpdate = []
        filesExcludes = []
        packageGenerator = new PackageGenerator()
        interceptorsToExecute = [org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.REMOVE_DEPRECATE.id]
        taskFolderName = DIR_UPDATE_FOLDER
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
        executeDeploy(taskFolderPath, START_UPDATE_TASK_MESSAGE, SUCCESS_UPDATE_TASK_MESSAGE)
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
        File fileToCopy
        packageGenerator.fileTrackerMap.each { nameFile, resultTracker ->
            if (resultTracker.state != ComponentStates.DELETED) {
                fileToCopy = new File(Paths.get(projectPath, nameFile).toString())
                filesToCopy.add(fileToCopy)
                if (nameFile.startsWith("aura/")) {
                    auraFolderToCopy.add(fileToCopy.getParentFile().getPath())
                }
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
        packageGenerator.init(projectPath, validatedFiles, credential, project)
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
        auraFolderToCopy.each { dir ->
            ((new File(dir)).listFiles().findAll { item -> item.name.endsWith('.cmp') || item.name.endsWith('.app')}).each {file ->
                filesToUpdate.push(file)
            }
        }
        copyFilesToTaskDirectory(filesToUpdate)
    }

    /**
     * Prints files changed
     */
    public void showFilesChanged() {
        if (isEmptyChangedFiles()) {
            println (NOT_FILES_CHANGED)
        }

        if (packageGenerator.fileTrackerMap.size() > 0) {
            println ("*********************************************")
            println ("              Status Files Changed             ")
            println ("*********************************************")
            packageGenerator.fileTrackerMap.each { nameFile, status ->
                println ("${Paths.get(nameFile).getFileName().toString()}${" - "}${status.toString()}")
            }
            println ("*********************************************")
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
        ArrayList<File> trackedFiles = packageGenerator.getFiles(projectPath)
        ArrayList<File> deletedFiles = packageGenerator.getFiles(ComponentStates.DELETED)

        String includes = Util.getIncludesValueByFolderFromFilesUpdated(trackedFiles, folders, projectPath)
        ArrayList<File> filesFiltered = filter.getFiles(includes, excludes)

        filesFiltered.addAll(deletedFiles)
        packageGenerator.updateFileTracker(filesFiltered)
        filesExcludes = trackedFiles - filesFiltered
    }

    public void loadParameters() {
        if (Util.isValidProperty(parameters, Constants.PARAMETER_FOLDERS) && !Util.isEmptyProperty(parameters, Constants.PARAMETER_FOLDERS)) {
            folders = parameters.get(Constants.PARAMETER_FOLDERS)
        }
        loadCommonParameters()
    }
}