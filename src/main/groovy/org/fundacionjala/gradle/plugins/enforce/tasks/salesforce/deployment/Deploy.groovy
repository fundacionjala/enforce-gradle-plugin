/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentMonitor
import org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor
import org.fundacionjala.gradle.plugins.enforce.utils.AnsiColor
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 * Deploys all project source
 */
class Deploy extends Deployment {
    private static final String DEPLOYING_TRUNCATED_CODE = 'Deploying truncated code'
    private static final String DEPLOYING_TRUNCATED_CODE_SUCCESSFULLY = 'Truncated code were successfully deployed'
    private static final String DEPLOYING_CODE = 'Starting deploy'
    private static final String DEPLOYING_CODE_SUCCESSFULLY = 'Code were successfully deployed'
    private static final String TRUNCATE_DEPRECATE_TURNED_OFF = 'truncate deprecate statement has been deactivated'
    private static final String TRUNCATE_CODE_TURNED_OFF = 'Truncate code has been deactivated'
    private static final String DEPLOY_DESCRIPTION = 'This task deploys all the project'
    private static final String FOLDER_DEPLOY = 'deploy'
    private static final String TURN_OFF_TRUNCATE = 'turnOffTruncate'
    private static final String TRUNCATE_DEPRECATE = 'deprecate'
    private static final String TRUNCATE_CODE = 'sourceCode'

    private static final Integer NOT_FOUND = -1

    private boolean deprecateTruncateOn
    private boolean codeTruncateOn
    public ArrayList<String> foldersNotDeploy
    public String folders = ""
    public ArrayList<File> filesToDeploy

    /**
     * Sets description task and its group
     */
    Deploy() {
        super(DEPLOY_DESCRIPTION, Constants.DEPLOYMENT)
        deprecateTruncateOn = true
        codeTruncateOn = true
        taskFolderName = FOLDER_DEPLOY
    }

    /**
     * Executes the steps for deploy
     */
    @Override
    public void runTask() {
        loadClassifiedFiles(folders, excludes)
        loadFilesToDeploy()
        truncate()
        deploy()
        updateFileTracker()
    }

    /**
     * Steps to truncate files
     */
    void truncate() {
        createDeploymentDirectory(taskFolderPath)
        copyFilesToTaskDirectory(filesToDeploy)
        displayFolderNoDeploy()
        deployTruncateFiles()
    }

    /**
     * Steps to deploy files to org
     */
    void deploy() {
        deployAllComponents()
        deployTruncateDeprecateFiles()
        executeDeploy(taskFolderPath, DEPLOYING_CODE, DEPLOYING_CODE_SUCCESSFULLY)
    }

    /**
     * Initializes all task parameters
     */
    void loadParameters() {
        String turnOffOptionTruncate = parameters[TURN_OFF_TRUNCATE].toString()
        if (Util.isValidProperty(parameters, Constants.PARAMETER_FOLDERS)) {
            folders = parameters[Constants.PARAMETER_FOLDERS].toString()
        }
        if (Util.isValidProperty(parameters, Constants.PARAMETER_EXCLUDES)) {
            excludes = parameters[Constants.PARAMETER_EXCLUDES].toString()
        }
        if (!Util.isValidProperty(parameters, TURN_OFF_TRUNCATE)) {
            return
        }
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
     * Adds all files into files to deploy
     */
    def loadFilesToDeploy() {
        filesToDeploy = classifiedFile.validFiles
    }

    /**
     * Deploys files truncated and files no truncated
     */
    public void deployTruncateFiles() {
        if (codeTruncateOn) {
            Files.copy(Paths.get(projectPath, Constants.PACKAGE_FILE_NAME), Paths.get(taskPackagePath), StandardCopyOption.REPLACE_EXISTING)
            logger.debug('Generating package')
            writePackage(taskPackagePath, filesToDeploy)
            combinePackageToUpdate(taskPackagePath)
            truncateComponents()
            logger.debug("Deploying to truncate components from: $taskFolderPath")
            executeDeploy(taskFolderPath, DEPLOYING_TRUNCATED_CODE, DEPLOYING_TRUNCATED_CODE_SUCCESSFULLY)
        }
    }

    /**
     * Displays folders that will not be deployed
     */
    public void displayFolderNoDeploy() {
        foldersNotDeploy = fileManager.getFoldersNotDeploy(taskFolderPath)
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
    public void updateFileTracker() {
        ComponentMonitor componentMonitor = new ComponentMonitor(projectPath)
        logger.debug('Getting components signatures')
        Map initMapSave = componentMonitor.getComponentsSignature(fileManager.getValidElements(projectPath, excludeFilesToMonitor))
        logger.debug('Saving initial file tracker')
        componentMonitor.componentSerializer.save(initMapSave)
    }

    /**
     * Deploys the filtered components from project directory
     */
    public void deployAllComponents() {
        createDeploymentDirectory(taskFolderPath)
        copyFilesToTaskDirectory(filesToDeploy)
        writePackage(taskPackagePath, filesToDeploy)
        combinePackageToUpdate(taskPackagePath)
    }

    /**
     * Deploys code to salesForce Organization
     */
    private void deployTruncateDeprecateFiles() {
        if (deprecateTruncateOn) {
            interceptorsToExecute = [Interceptor.REMOVE_DEPRECATE.id]
            interceptorsToExecute += interceptors
            logger.debug("Truncating components from: $taskFolderPath")
            truncateComponents(taskFolderPath)
            logger.debug("Deploying all components from: $taskFolderPath")
        }
    }

    /**
     * Truncates the classes, objects, triggers, pages and workflows
     */
    public void truncateComponents() {
        interceptorsToExecute = [Interceptor.TRUNCATE_CLASSES.id, Interceptor.TRUNCATE_FIELD_SETS.id, Interceptor.TRUNCATE_ACTION_OVERRIDES.id,
                                 Interceptor.TRUNCATE_FIELD.id, Interceptor.TRUNCATE_FORMULAS.id, Interceptor.TRUNCATE_WEB_LINKS.id,
                                 Interceptor.TRUNCATE_PAGES.id, Interceptor.TRUNCATE_TRIGGERS.id, Interceptor.TRUNCATE_WORKFLOWS.id,
                                 Interceptor.TRUNCATE_COMPONENTS.id]
        interceptorsToExecute += interceptors
        logger.debug("Truncating components at: $taskFolderPath")
        truncateComponents(taskFolderPath)
    }
}