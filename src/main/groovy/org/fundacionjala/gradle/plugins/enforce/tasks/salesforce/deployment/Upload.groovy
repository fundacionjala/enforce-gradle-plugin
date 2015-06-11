/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ResultTracker
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageGenerator
import org.gradle.api.file.FileTree

import java.nio.file.Paths

/**
 * Uploads files to an organization using metadata API without truncate values
 */
class Upload extends Deployment {
    public ArrayList<File> specificFilesToUpload
    public ArrayList<File> filesToUpload
    public PackageGenerator packageGenerator
    public String pathUpload
    public String uploadPackagePath
    public String files
    public String option = 'y'
    public String all = Constants.FALSE

    /**
     * Sets description and group task
     */
    Upload() {
        super(Constants.UPLOAD_DESCRIPTION, Constants.DEPLOYMENT)
        specificFilesToUpload = new ArrayList<File>()
        packageGenerator = new PackageGenerator()
        filesToUpload = new ArrayList<File>()
        interceptorsToExecute = []
    }

    /**
     * Executes the steps for Upload
     */
    @Override
    void runTask() {
        pathUpload = Paths.get(buildFolderPath, Constants.DIR_UPLOAD_FOLDER).toString()
        uploadPackagePath = Paths.get(pathUpload, PACKAGE_NAME).toString()
        createDeploymentDirectory(pathUpload)
        loadFilesChangedToUpload()
        loadParameter()
        loadAllFiles()
        if (specificFilesToUpload.empty && !Util.isValidProperty(project, EXCLUDES) && all == Constants.FALSE) {
            logger.warn("${Constants.ALL_FILES_UPLOAD}${projectPath}")
            option = System.console().readLine(Constants.QUESTION_CONTINUE)

        }
        if (option == Constants.YES_OPTION) {
            loadFiles()
            copyFilesToUpload()
            createPackage()
            truncate(pathUpload)
            executeDeploy(pathUpload)
            saveMapOfFilesChanged()
        } else {
            logger.error(Constants.UPLOAD_CANCELED)
        }
    }

    def truncate(String pathToTruncate) {
        interceptorsToExecute += interceptors
        truncateComponents(pathToTruncate)
    }

    /**
     * Saves on file monitor the files which has been updated
     */
    void saveMapOfFilesChanged() {
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
     * @return
     */
    Map filterMapFilesChanged() {
        Map<String, ResultTracker> fileChanged = [:]
        specificFilesToUpload.each { File file ->
            if (packageGenerator.fileTrackerMap.get(file.toString())) {
                fileChanged.put(file.toString(), packageGenerator.fileTrackerMap.get(file.toString()))
            }
        }
        return fileChanged
    }

    /**
     * Loads all files which has been changed to be updated once user execute upload Task
     */
    void loadFilesChangedToUpload() {
        ArrayList<File> validatedFiles = fileManager.getValidElements(projectPath, excludeFilesToMonitor)
        packageGenerator.init(projectPath, validatedFiles, credential)
    }

    /**
     * Loads 'all' variable with true to upload all files from your local repository to your organization.
     * By default the 'all' variable has the value equals to false.
     */
    void loadAllFiles() {
        if (Util.isValidProperty(project, Constants.ALL_FILES_TO_UPLOAD) && !Util.isEmptyProperty(project, Constants.ALL_FILES_TO_UPLOAD)) {
            all = project.properties[Constants.ALL_FILES_TO_UPLOAD].toString()
        }
    }

    /**
     * Loads files that will be uploaded into specificFilesToUpload array.
     */
    def loadParameter() {
        if (Util.isValidProperty(project, Constants.FILES_TO_UPLOAD) && !Util.isEmptyProperty(project, Constants.FILES_TO_UPLOAD)) {
            files = project.properties[Constants.FILES_TO_UPLOAD].toString()
        }
        ArrayList<String> filesName = new ArrayList<String>()
        if (files == null) {
            return
        }
        validateParameter(files)
        files.split(Constants.COMMA).each {String fileName ->
            def fileNameChanged = fileName.replaceAll(Constants.BACK_SLASH, Constants.SLASH)
            if (!fileNameChanged.contains(Constants.SLASH)) {
                filesName.push("${fileName}${File.separator}${Constants.WILDCARD}${Constants.WILDCARD}")
                return
            }
            filesName.push(fileName)
            filesName.push("${fileName}${Constants.META_XML}")
        }

        FileTree fileTree = project.fileTree(dir:projectPath, includes: filesName)
        fileTree.each {File file ->
            specificFilesToUpload.push(file)
        }
    }

    /**
     * Loads all files from project directory to specificFilesToUpload array
     */
    def loadFiles() {
        if (specificFilesToUpload.isEmpty()) {
            specificFilesToUpload = getFilesFiltered()
        }
    }

    /**
     * Creates packages of all files selected
     */
    public void createPackage() {
        if (!specificFilesToUpload.empty) {
            writePackage(Paths.get(pathUpload, PACKAGE_NAME).toString(), specificFilesToUpload)
            combinePackageToUpdate(uploadPackagePath)
        }
    }

    /**
     * Copies files to build folder to upload
     */
    public void copyFilesToUpload() {
        specificFilesToUpload = excludeFiles(specificFilesToUpload)
        fileManager.copy(projectPath, specificFilesToUpload, pathUpload)
    }

    /**
     * Filters files to upload files
     * @return ArrayList of files filtered
     */
    private ArrayList<File> getFilesFiltered() {
        ArrayList<File> sourceFiles = fileManager.getValidElements(projectPath)
        sourceFiles.remove(new File(Paths.get(projectPath, Constants.PACKAGE_FILE_NAME).toString()))
        return sourceFiles
    }
}