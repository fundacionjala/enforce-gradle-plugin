/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ResultTracker
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.FileValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageGenerator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.filter.Filter

import java.nio.file.Paths

/**
 * Uploads files to an organization using metadata API without truncate values
 */
class Upload extends Deployment {
    public Map<String, ArrayList<File>> specificFilesToUpload
    public PackageGenerator packageGenerator
    public String pathUpload
    public String uploadPackagePath
    public String option = Constants.YES_OPTION
    public String all = Constants.FALSE
    public Filter filter
    String files = Constants.EMPTY

    Upload() {
        super(Constants.UPLOAD_DESCRIPTION, Constants.DEPLOYMENT)
    }

    /**
     * Executes the steps for Upload
     */
    @Override
    void runTask() {
        setup()
        createDeploymentDirectory(pathUpload)
        loadFilesChangedToUpload()
        loadParameters()
        showWarningMessage()
        if (option == Constants.YES_OPTION) {
            loadFilesToUpload()
            copyFilesToUpload()
            createPackage()
            combinePackageToUpdate(uploadPackagePath)
            addInterceptor()
            executeDeploy(pathUpload)
            saveMapOfFilesChanged()
        } else {
            logger.error(Constants.UPLOAD_CANCELED)
        }
    }

    /**
     * Setups paths of upload directory into of build folder
     * Sets path of package xml file into build directory
     * Creates an instance of PackageGenerator class
     * Creates an instance of Filter class
     * Initializes specificFilesToUpload map
     * Initializes interceptorsToExecute arrayList
     */
    void setup() {
        pathUpload = Paths.get(buildFolderPath, Constants.DIR_UPLOAD_FOLDER).toString()
        uploadPackagePath = Paths.get(pathUpload, PACKAGE_NAME).toString()
        componentDeploy.startMessage = "Starting upload files process"
        componentDeploy.successMessage = "The files were successfully uploaded"
        packageGenerator = new PackageGenerator()
        filter = new Filter(project, projectPath)
        specificFilesToUpload = [:]
        interceptorsToExecute = []
    }

    /**
     * Loads all, excludes and files parameters
     */
    public void loadParameters() {
        if (Util.isValidProperty(parameters, Constants.FILES_TO_UPLOAD) && !Util.isEmptyProperty(parameters, Constants.FILES_TO_UPLOAD)) {
            files = parameters[Constants.FILES_TO_UPLOAD].toString()
        }
        if (Util.isValidProperty(parameters, Constants.PARAMETER_EXCLUDES) && !Util.isEmptyProperty(parameters, Constants.PARAMETER_EXCLUDES)) {
            excludes = parameters[Constants.PARAMETER_EXCLUDES].toString()
        }
        if (Util.isValidProperty(parameters, Constants.ALL_FILES_TO_UPLOAD) && !Util.isEmptyProperty(parameters, Constants.ALL_FILES_TO_UPLOAD)) {
            all = parameters[Constants.ALL_FILES_TO_UPLOAD].toString()
        }
    }

    /**
     * Gets a Map of files classified by valid, invalid
     * @return a map with files classified
     */
    public Map<String, ArrayList<File>> getFilesValidated() {
        ArrayList<File> filesFiltered = filter.getFiles(files, excludes)
        Map<String, ArrayList> fileValidated = FileValidator.validateFiles(projectPath, filesFiltered)
        return fileValidated
    }

    /**
     * Loads files classified at specificFilesToUpload map
     */
    public void loadFilesToUpload() {
        specificFilesToUpload = getFilesValidated()
    }

    /**
     * Copies files at build/upload directory
     */
    public void copyFilesToUpload() {
        String exceptionMessage = Util.getExceptionMessage(specificFilesToUpload)
        if (!exceptionMessage.isEmpty()) {
            throw new Exception(exceptionMessage)
        }
        ArrayList<File> validFiles = specificFilesToUpload[Constants.VALID_FILE]
        fileManager.copy(projectPath, validFiles, pathUpload)
    }

    /**
     * Creates the package xml file
     */
    public void createPackage() {
        ArrayList<File> files = specificFilesToUpload[Constants.VALID_FILE]
        if (files && !files.isEmpty()) {
            writePackage(uploadPackagePath, files)
        }
    }

    /**
     * Shows a warning message to upload all files to org
     */
    public void showWarningMessage() {
        if (all == Constants.FALSE && files.isEmpty()  && excludes.isEmpty()) {
            logger.warn("${Constants.ALL_FILES_UPLOAD}${projectPath}")
            option = System.console().readLine(Constants.QUESTION_CONTINUE)
        }
    }

    /**
    * Saves on file monitor the files which has been updated
    */
    public void saveMapOfFilesChanged() {
        if (packageGenerator.fileTrackerMap.isEmpty()) {
            return
        }
        if (specificFilesToUpload.empty) {
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
        ArrayList<File> validFiles = specificFilesToUpload.get(Constants.VALID_FILE)
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
        packageGenerator.init(projectPath, validatedFiles, credential)
    }

    /**
     * Adds interceptors
     */
    public void addInterceptor() {
        interceptorsToExecute += interceptors
        truncateComponents(pathUpload)
    }
}