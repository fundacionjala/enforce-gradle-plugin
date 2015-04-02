/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.retrieve

import org.fundacionjala.gradle.plugins.enforce.metadata.RetrieveMetadata
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.SalesforceTask
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.ZipFileManager
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.Package
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageBuilder
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.file.Paths

abstract class Retrieval extends SalesforceTask {
    private final String ZIP_FILE_NAME = 'retrieve.zip'
    private final String UNPACKAGE_FOLDER = 'unpackaged'
    public RetrieveMetadata retrieveMetadata
    public PackageBuilder packageBuilder
    public String unPackageFolder

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
     * Executes metadataAPI retrieve action
     */
    void executeRetrieve(Package metaPackage) {
        retrieveMetadata = new RetrieveMetadata(metaPackage)
        retrieveMetadata.executeRetrieve(poll, waitTime, credential)
    }

    /**
     * Saves on disk zip file doing a flush from memory and unzipped it
     * @param zipFile contains the zip file retrieved in byte format
     */
    void saveOnDiskFileUnzipped(byte[] zipFile) {
        ZipFileManager zipFileManager = new ZipFileManager()
        zipFileManager.flushZipFile(zipFile, buildFolderPath, ZIP_FILE_NAME)
        zipFileManager.unzipZipRetrieved(Paths.get(buildFolderPath, ZIP_FILE_NAME).toString(), buildFolderPath)
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
     * Validates names of folders
     * @param foldersName is type array list contents names of folders
     */
    public void validateFolders(ArrayList<String> foldersName) {
        ArrayList<String> invalidFolders = new ArrayList<String>()
        invalidFolders = Util.getInvalidFolders(foldersName)
        if (!invalidFolders.empty) {
            throw new Exception("${Constants.INVALID_FOLDER}: ${invalidFolders}")
        }
    }

    /**
     * Validates names of files
     * @param filesName is type array list contents names of files
     */
    public void validateFiles(ArrayList<String> filesName) {
        ArrayList<String> invalidFiles = new ArrayList<String>()
        filesName.each { String fileName ->
            File file = new File(Paths.get(projectPath, fileName).toString())
            def extension = Util.getFileExtension(file)
            def parentFileName = file.getParentFile().getName()
            if (!MetadataComponents.validExtension(extension) || !MetadataComponents.validFolder(parentFileName)) {
                invalidFiles.push(fileName)
            }
        }
        if (!invalidFiles.isEmpty()) {
            throw new Exception("${Constants.INVALID_FILE}: ${invalidFiles}")
        }
    }
}
