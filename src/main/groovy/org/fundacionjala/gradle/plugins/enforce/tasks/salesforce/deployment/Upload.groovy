/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ResultTracker
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager.PackageGenerator

/**
 * Uploads files to an organization using metadata API without truncate values
 */
class Upload extends Deployment {
    private static final String UPLOAD_DESCRIPTION = "This task uploads all specific files or folders as user wants"
    private static final String ALL_FILES_UPLOAD = "All files will be uploaded from: "
    private static final String QUESTION_CONTINUE = "Do you want to continue? (y/n) :"
    private static final String QUESTION_CONTINUE_DELETE = "Do you want delete this files into your organization? (y/n) :"
    private static final String UPLOAD_CANCELED ='Upload all files was canceled!!'
    private static final String DIR_UPLOAD_FOLDER = "upload"
    private static final String FILES_TO_UPLOAD = "files"
    private static final String ALL_FILES_TO_UPLOAD = "all"
    private static final String START_UPLOAD_TASK_MESSAGE = "Starting upload files process..."
    private static final String SUCCESS_UPLOAD_TASK_MESSAGE = "The files were successfully uploaded"

    public ArrayList<File> filesToUpload
    public PackageGenerator packageGenerator
    public String option = Constants.YES_OPTION
    public String all = Constants.FALSE
    String files = Constants.EMPTY

    Upload() {
        super(UPLOAD_DESCRIPTION, Constants.DEPLOYMENT)
        packageGenerator = new PackageGenerator()
        filesToUpload = []
        interceptorsToExecute = []
        taskFolderName = DIR_UPLOAD_FOLDER
    }

    /**
     * Executes the steps for Upload
     */
    @Override
    void runTask() {
        createDeploymentDirectory(taskFolderPath)
        loadFilesChangedToUpload()
        showWarningMessage()
        if (option == Constants.YES_OPTION) {
            loadClassifiedFiles(files, excludes)
            loadFilesToUpload()
            copyFilesToTaskDirectory(filesToUpload)
            createPackage()
            combinePackageToUpdate(taskPackagePath)
            addInterceptor()
            executeDeploy(taskFolderPath, START_UPLOAD_TASK_MESSAGE, SUCCESS_UPLOAD_TASK_MESSAGE)
            saveMapOfFilesChanged()
        } else {
            logger.error(UPLOAD_CANCELED)
        }
    }

    /**
     * Loads all, excludes and files parameters
     */
    public void loadParameters() {
        if (Util.isValidProperty(parameters, FILES_TO_UPLOAD) && !Util.isEmptyProperty(parameters, FILES_TO_UPLOAD)) {
            files = parameters[FILES_TO_UPLOAD].toString()
        }
        if (Util.isValidProperty(parameters, Constants.PARAMETER_EXCLUDES) && !Util.isEmptyProperty(parameters, Constants.PARAMETER_EXCLUDES)) {
            excludes = parameters[Constants.PARAMETER_EXCLUDES].toString()
        }
        if (Util.isValidProperty(parameters, ALL_FILES_TO_UPLOAD) && !Util.isEmptyProperty(parameters, ALL_FILES_TO_UPLOAD)) {
            all = parameters[ALL_FILES_TO_UPLOAD].toString()
        }
    }

    /**
     * Loads files classified at filesToUpload map
     */
    public void loadFilesToUpload() {
        filesToUpload = classifiedFile.validFiles
    }

    /**
     * Creates the package xml file
     */
    public void createPackage() {
        if (filesToUpload && !filesToUpload.isEmpty()) {
            writePackage(taskPackagePath, filesToUpload)
        }
    }

    /**
     * Shows a warning message to upload all files to org
     */
    public void showWarningMessage() {
        if (!super.isIntegrationMode() && (all == Constants.FALSE) && files.isEmpty()  && excludes.isEmpty()) {
            logger.warn("${ALL_FILES_UPLOAD}${projectPath}")
            option = System.console().readLine(QUESTION_CONTINUE)
        }
    }

    /**
    * Saves on file monitor the files which has been updated
    */
    public void saveMapOfFilesChanged() {
        if (packageGenerator.fileTrackerMap.isEmpty()) {
            return
        }
        if (filesToUpload.empty) {
            packageGenerator.saveFileTrackerMap()
            return
        }
        packageGenerator.fileTrackerMap = filterMapFilesChanged()
        packageGenerator.saveFileTrackerMap()
    }

    /**
    * Filters the specific files changed to update
    * @return a map with files that were updated
    */
    public Map filterMapFilesChanged() {
        Map<String, ResultTracker> fileChanged = [:]
        ArrayList<File> validFiles = filesToUpload
        validFiles.each { File file ->
            if (packageGenerator.fileTrackerMap.get(file.toString())) {
                fileChanged.put(file.toString(), packageGenerator.fileTrackerMap.get(file.toString()))
            }
        }
        return fileChanged
    }

    /**
    * Loads all files which has been changed to be updated once user execute upload Task
    */
    public void loadFilesChangedToUpload() {
        ArrayList<File> validatedFiles = fileManager.getValidElements(projectPath, excludeFilesToMonitor)
        packageGenerator.init(projectPath, validatedFiles, credential, project)
    }

    /**
     * Adds interceptors
     */
    public void addInterceptor() {
        interceptorsToExecute += interceptors
        truncateComponents(taskFolderPath)
    }
}