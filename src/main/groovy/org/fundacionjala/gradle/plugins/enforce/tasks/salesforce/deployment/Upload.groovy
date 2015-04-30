/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.gradle.api.file.FileTree
import org.fundacionjala.gradle.plugins.enforce.filemonitor.FileMonitorSerializer
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.file.Paths

/**
 * Uploads files to an organization using metadata API without truncate values
 */
class Upload extends Deployment {
    private static final String DESCRIPTION_OF_TASK = "This task uploads all specific files or folders as user wants"
    private final String ALL_FILES_UPLOAD = "All files will be uploaded from: "
    private final String QUESTION_CONTINUE = " Do you want to continue? (y/n) :"
    private final String UPLOAD_CANCELED ='Upload all files was canceled!!'
    private final String DIR_UPLOAD_FOLDER = "upload"
    private final String FILES_TO_UPLOAD = "files"
    private final String ALL_FILES_TO_UPLOAD = "all"

    public ArrayList<File> specificFilesToUpload
    public ArrayList<File> filesToUpload
    public FileMonitorSerializer objSerializer
    public String pathUpload
    public Map filesChanged
    public String files
    public String option = 'y'
    public final String YES_OPTION = 'y'
    public String all = Constants.FALSE

    /**
     * Sets description and group task
     */
    Upload() {
        super(DESCRIPTION_OF_TASK, Constants.DEPLOYMENT)
        specificFilesToUpload = new ArrayList<File>()
        objSerializer = new FileMonitorSerializer()
        filesToUpload = new ArrayList<File>()
        filesChanged = [:]
        interceptorsToExecute = []
    }

    /**
     * Executes the steps for Upload
     */
    @Override
    void runTask() {
        pathUpload = Paths.get(buildFolderPath, DIR_UPLOAD_FOLDER).toString()
        createDeploymentDirectory(pathUpload)
        loadFilesChangedToUpload()
        loadParameter()
        loadAllFiles()
        if (specificFilesToUpload.empty && !Util.isValidProperty(project, EXCLUDES) && all == Constants.FALSE) {
            logger.warn("${ALL_FILES_UPLOAD}${projectPath}")
            option = System.console().readLine(QUESTION_CONTINUE)
        }
        if (option == YES_OPTION) {
            loadFiles()
            copyFilesToUpload()
            createPackage()
            truncate(pathUpload)
            executeDeploy(pathUpload)
            saveMapOfFilesChanged()
        } else {
            logger.error(UPLOAD_CANCELED)
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
        if (filesChanged.isEmpty()) {
            return
        }

        if (specificFilesToUpload.empty) {
            objSerializer.saveMapUpdated(filesChanged)
            return
        }
        objSerializer.saveMapUpdated(filterMapFilesChanged())
    }

    /**
     * Filters the specific files changed to update
     * @return
     */
    Map filterMapFilesChanged() {
        Map auxiliaryMap = [:]
        specificFilesToUpload.each { file ->
            if (filesChanged.get(file.toString())) {
                auxiliaryMap.put(file.toString(), filesChanged.get(file.toString()))
            }
        }
        return auxiliaryMap
    }

    /**
     * Loads all files which has been changed to be updated once user execute upload Task
     */
    void loadFilesChangedToUpload() {
        objSerializer.setSrcProject(projectPath)
        ArrayList<File> fileArray = fileManager.getValidElements(projectPath, excludeFilesToMonitor)
        if (!objSerializer.verifyFileMap()) {
            objSerializer.mapRefresh(fileArray)
            return
        }
        filesChanged = objSerializer.getFileTrackerMap(fileArray)
    }

    /**
     * Loads 'all' variable with true to upload all files from your local repository to your organization.
     * By default the 'all' variable has the value equals to false.
     */
    void loadAllFiles() {
        if (Util.isValidProperty(project, ALL_FILES_TO_UPLOAD) && !Util.isEmptyProperty(project, ALL_FILES_TO_UPLOAD)) {
            all = project.properties[ALL_FILES_TO_UPLOAD].toString()
        }
    }

    /**
     * Loads files that will be uploaded into specificFilesToUpload array.
     */
    def loadParameter() {
        if (Util.isValidProperty(project, FILES_TO_UPLOAD) && !Util.isEmptyProperty(project, FILES_TO_UPLOAD)) {
            files = project.properties[FILES_TO_UPLOAD].toString()
        }
        ArrayList<String> filesName = new ArrayList<String>()
        if (files == null) {
            return
        }
        validateParameter(files)
        files.split(Constants.COMMA).each {String fileName ->
            def fileNameChanged = fileName.replaceAll(BACKSLASH, SLASH)
            if (!fileNameChanged.contains(SLASH)) {
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
        }
    }

    /**
     * Copies files to build folder to upload
     */
    public void copyFilesToUpload() {
        specificFilesToUpload = excludeFiles(specificFilesToUpload)
        fileManager.copy(specificFilesToUpload, pathUpload)
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