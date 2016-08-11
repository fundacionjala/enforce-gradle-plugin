/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.retrieve
import org.fundacionjala.gradle.plugins.enforce.metadata.RetrieveMetadata
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.SalesforceTask
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.ZipFileManager
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.ClassifiedFile
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.FileValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager.Package
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager.PackageBuilder
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.filter.Filter

import java.nio.file.Paths

abstract class Retrieval extends SalesforceTask {
    private final String ZIP_FILE_NAME = 'retrieve.zip'
    private final String UNPACKAGE_FOLDER = 'unpackaged'
    private final String SPECIFIC_FILES_NOT_FOUND = "Valid files were not found, retrieve task was canceled!!"
    protected final int CODE_TO_EXIT = 0
    public RetrieveMetadata retrieveMetadata
    public PackageBuilder packageBuilder
    public String unPackageFolder
    public String packageFromSourcePath
    public String packageFromBuildPath
    private final String FILES_RETRIEVE = 'files'
    private final String SPECIFIC_FILES_RETRIEVE = 'specificFiles'
    private final String ALL_PARAMETER = 'all'
    public String files
    public String all = Constants.FALSE_OPTION
    public Boolean specificFiles = Constants.FALSE_OPTION
    public ArrayList<String> specificFileNames

    /**
     * Sets description and group task
     * @param descriptionTask is description tasks
     * @param groupTask is the group typeName the task
     */
    Retrieval(String descriptionTask, String groupTask) {
        super(descriptionTask, groupTask)
        packageBuilder = new PackageBuilder()
        unPackageFolder = Paths.get(buildFolderPath, UNPACKAGE_FOLDER).toString()
    }

    /**
     * Sets package path from build directory
     * Sets package path from source directory
     */
    @Override
    void setup() {
        packageFromSourcePath = Paths.get(projectPath, Constants.PACKAGE_FILE_NAME).toString()
        packageFromBuildPath = Paths.get(unPackageFolder, Constants.PACKAGE_FILE_NAME).toString()
    }

    /**
     * Loads the files and all parameters
     */
    @Override
    void loadParameters() {
        if (!files) {
            if (Util.isValidProperty(project, FILES_RETRIEVE)) {
                files = project.property(FILES_RETRIEVE) as String
            }
            specificFiles = project.hasProperty(SPECIFIC_FILES_RETRIEVE)
        }
        if (Util.isValidProperty(project, ALL_PARAMETER)) {
            all = project.property(ALL_PARAMETER) as String
        }
    }

    /**
     * Executes metadataAPI retrieve action
     */
    void executeRetrieve(Package metaPackage) {
        retrieveMetadata = new RetrieveMetadata(metaPackage)
        if (specificFiles) {
            readSpecificFilesToRetrieve()
            retrieveMetadata.setSpecificFiles(specificFileNames)
        }
        retrieveMetadata.executeRetrieve(poll, waitTime, credential)
    }

    /**
     * Saves on disk zip file doing a flush from memory and unzipped it
     * @param zipFile contains the zip file retrieved in byte format
     */
    void saveOnDiskFileUnzipped(byte[] zipFile) {
        ZipFileManager zipFileManager = new ZipFileManager()
        zipFileManager.flushZipFile(zipFile, buildFolderPath, ZIP_FILE_NAME)
        if (!specificFiles) {
            unZip(Paths.get(buildFolderPath, ZIP_FILE_NAME).toString(), buildFolderPath)
        } else {
            unZip(Paths.get(buildFolderPath, ZIP_FILE_NAME).toString(), projectPath)
        }
    }

    /**
     * Shows warnings messages of retrieve action
     */
    void showWarningsMessages() {
        if (retrieveMetadata.getWarningsMessages().empty) {
            return
        }
        retrieveMetadata.getWarningsMessages().each { message ->
            logger.warn(message)
        }
    }
    /**
     * Loads specific files to be retrieved, their file names must be encoded by URLEncoder logic in order to avoid SOAP exceptions.
     **/
    void readSpecificFilesToRetrieve() {
        specificFileNames = []
        Filter filter = new Filter(project, projectPath)
        ClassifiedFile classifiedFile = null
        if (files) {
            ArrayList<File> filesFiltered = filter.getFiles(files, null)
            classifiedFile = FileValidator.validateFiles(projectPath, filesFiltered)
        }
        if (!classifiedFile || !classifiedFile.validFiles || classifiedFile.validFiles.isEmpty()) {
            logger.error(SPECIFIC_FILES_NOT_FOUND)
            System.exit(CODE_TO_EXIT)
        }
        println "*********************************************\t"
        println "        Specific files to retrieve\t"
        println "*********************************************\t"
        if (classifiedFile.validFiles) {
            classifiedFile.validFiles.each { file ->
                String fileNameToRetrieve = getSpecificFileFormattedName(file)
                println(fileNameToRetrieve)
                specificFileNames.add(fileNameToRetrieve)
            }
        }
        println "*********************************************\t"
    }

    /**
     * Returns file's relative path and applies URLEncoder to file names, it is in order to ensure safe SOAP requests.
     * @return String, relative path with encoded name.
     */
    String getSpecificFileFormattedName(File file) {
        String fileName = file.getName()
        String fileNameToRetrieve = file.getAbsolutePath().replace(projectPath, "").replace(fileName, URLEncoder.encode(fileName, "UTF-8"))
        return fileNameToRetrieve.substring(1).replaceAll("\\\\", "/")
    }
}