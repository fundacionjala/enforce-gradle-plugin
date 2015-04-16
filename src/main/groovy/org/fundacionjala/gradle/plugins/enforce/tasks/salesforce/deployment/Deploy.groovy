/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.filemonitor.FileMonitorSerializer
import org.fundacionjala.gradle.plugins.enforce.utils.AnsiColor
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 * Deploy all project source
 */
class Deploy extends Deployment {
    private static final String DESCRIPTION_OF_TASK = 'This task deploys all the project'
    private final String FOLDERS_DEPLOY = "folders"
    private final String FILE_TRACKING = '/.fileTracker.data'
    private final String FOLDER_DEPLOY = 'deploy'
    private final String TURN_OFF_TRUNCATE = 'turnOffTruncate'
    private final String TRUNCATE_DEPRECATE = 'deprecate'
    private final String TRUNCATE_CODE = 'sourceCode'
    private final String DEPLOYING_TRUNCATED_CODE = 'Deploying truncated code'
    private final String DEPLOYING_TRUNCATED_CODE_SUCCESSFULLY = 'Truncated code were successfully deployed'
    private final String DEPLOYING_CODE = 'Starting deploy'
    private final String DEPLOYING_CODE_SUCCESSFULLY = 'Code were successfully deployed'
    private final String TRUNCATE_DEPRECATE_TURNED_OFF = 'truncate deprecate statement has been deactivated'
    private final String TRUNCATE_CODE_TURNED_OFF = 'truncate code has been deactivated'
    private final Integer NOT_FOUND = -1
    private boolean deprecateTruncateOn
    private boolean codeTruncateOn
    private final ArrayList<String> FOLDERS_TO_TRUNCATE = ['classes', 'objects', 'triggers', 'pages', 'components', 'workflows']
    String folders
    String folderDeploy
    String packagePathDeploy
    ArrayList<String> foldersNotDeploy

    /**
     * Sets description task and its group
     */
    Deploy() {
        super(DESCRIPTION_OF_TASK, Constants.DEPLOYMENT)
        deprecateTruncateOn = true
        codeTruncateOn = true
    }

    /**
     * Execute the steps for deploy
     */
    @Override
    void runTask() {
        folderDeploy = Paths.get(buildFolderPath, FOLDER_DEPLOY).toString()
        packagePathDeploy = Paths.get(folderDeploy, PACKAGE_NAME).toString()
        createDeploymentDirectory(folderDeploy)
        if (Util.isValidProperty(project, FOLDERS_DEPLOY)) {
            deployByFolder()
        } else {
            deployTruncateFiles()
        }
    }

    /**
     * Deploys by folder
     */
    def deployByFolder() {
        folders = project.folders
        if (folders) {
            ArrayList<String> foldersName = folders.split(Constants.COMMA)
            validateFolders(foldersName)
            ArrayList<String> invalidFolders = new ArrayList<String>()
            invalidFolders = Util.getInvalidFolders(foldersName)

            if (!invalidFolders.empty) {
                throw new Exception("${Constants.INVALID_FOLDER}: ${invalidFolders}")
            }
            ArrayList<String> emptyFolders = new ArrayList<String>()
            emptyFolders = Util.getEmptyFolders(foldersName, projectPath)
            if (!emptyFolders.empty) {
                throw new Exception("${Constants.NOT_FILES}: ${emptyFolders}")
            }
            ArrayList<File> filesByFolders = fileManager.getFilesByFolders(projectPath, foldersName)
            filesByFolders = excludeFiles(filesByFolders)
            fileManager.copy(filesByFolders, folderDeploy)
            writePackage(packagePathDeploy, filesByFolders)
            deployToSalesForce()
        }
    }

    /**
     * Deploys files truncated and files no truncated
     */
    def deployTruncateFiles() {
        checkStatusTruncate()
        displayFolderNoDeploy()
        if (codeTruncateOn) {
            ArrayList<File> filesToTruncate = excludeFiles(fileManager.getFilesByFolders(projectPath, FOLDERS_TO_TRUNCATE))
            Files.copy(Paths.get(projectPath, PACKAGE_NAME), Paths.get(packagePathDeploy), StandardCopyOption.REPLACE_EXISTING)
            fileManager.copy(filesToTruncate, folderDeploy)
            writePackage(packagePathDeploy, filesToTruncate)
            truncateComponents()
            componentDeploy.startMessage = DEPLOYING_TRUNCATED_CODE
            componentDeploy.successMessage = DEPLOYING_TRUNCATED_CODE_SUCCESSFULLY
            executeDeploy(folderDeploy)
            createDeploymentDirectory(folderDeploy)
        }
        fileManager.copy(excludeFiles(fileManager.getValidElements(projectPath)), folderDeploy)
        componentDeploy.startMessage = DEPLOYING_CODE
        componentDeploy.successMessage = DEPLOYING_CODE_SUCCESSFULLY
        deployToSalesForce()
    }

    /**
     * Checks if property turn off truncate exists and sets respective values
     */
    private void checkStatusTruncate() {
        if (!Util.isValidProperty(project, TURN_OFF_TRUNCATE)) {
            return
        }
        String turnOffOptionTruncate = project.turnOffTruncate
        if (turnOffOptionTruncate.indexOf(TRUNCATE_DEPRECATE) != NOT_FOUND) {
            deprecateTruncateOn = false
            logger.quiet(TRUNCATE_DEPRECATE_TURNED_OFF)
        }
        if (turnOffOptionTruncate.indexOf(TRUNCATE_CODE) != NOT_FOUND) {
            codeTruncateOn = false
            logger.quiet(TRUNCATE_CODE_TURNED_OFF)
        }
    }

    /**
     * Deploy code to salesForce Organization
     */
    private void deployToSalesForce() {
        if (deprecateTruncateOn) {
            interceptorsToExecute = [Interceptor.REMOVE_DEPRECATE.id]
            interceptorsToExecute += interceptors
            truncateComponents(folderDeploy)
        }
        executeDeploy(folderDeploy)
        updateFileTracker()
    }

    /**
     * Displays folders that will not be deployed
     */
    def displayFolderNoDeploy() {
        foldersNotDeploy = fileManager.getFoldersNotDeploy(folderDeploy)
        def index = 1
        if (foldersNotDeploy.size() > 0) {
            println ''
            println AnsiColor.ANSI_YELLOW.value()
            println("Folders not deployed ")
            println('___________________________________________')
            println ''
            foldersNotDeploy.each { nameFolder ->
                println("${"\t"}${index}${".- "}${nameFolder}")
                index++
            }
            println('___________________________________________')
            println AnsiColor.ANSI_RESET.value()
        }
    }

    /**
     * Updates a file tracker
     */
    def updateFileTracker() {
        String pathFileTracker = Paths.get(projectPath, FILE_TRACKING)
        FileMonitorSerializer fileMonitorSerializer = new FileMonitorSerializer(pathFileTracker)
        Map initMapSave = fileMonitorSerializer.loadSignatureForFilesInDirectory(fileManager.getValidElements(projectPath, excludeFilesToMonitor))
        fileMonitorSerializer.saveMap(initMapSave)
    }

    /**
     * Truncates the classes, objects, triggers, pages and workflows
     */
    def truncateComponents() {
        String srcPath = Paths.get(buildFolderPath, FOLDER_DEPLOY).toString()
        interceptorsToExecute = [Interceptor.TRUNCATE_CLASSES.id, Interceptor.TRUNCATE_FIELD_SETS.id, Interceptor.TRUNCATE_ACTION_OVERRIDES.id,
                                 Interceptor.TRUNCATE_FIELD.id,Interceptor.TRUNCATE_FORMULAS.id, Interceptor.TRUNCATE_WEB_LINKS.id,
                                 Interceptor.TRUNCATE_PAGES.id, Interceptor.TRUNCATE_TRIGGERS.id, Interceptor.TRUNCATE_WORKFLOWS.id,
                                 Interceptor.TRUNCATE_COMPONENTS.id]
        interceptorsToExecute += interceptors
        truncateComponents(srcPath)
    }
}
