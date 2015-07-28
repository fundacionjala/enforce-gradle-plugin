/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */
package org.fundacionjala.gradle.plugins.enforce.metadata

import com.sforce.soap.metadata.RetrieveMessage
import com.sforce.soap.metadata.RetrieveResult
import com.sforce.soap.metadata.RetrieveStatus
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.ForceApiType
import org.fundacionjala.gradle.plugins.enforce.wsc.ForceFactory
import org.fundacionjala.gradle.plugins.enforce.wsc.soap.MetadataAPI

/**
 * Retrieve files from an organization
 */
public class RetrieveMetadata {

    private org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager.Package metaPackage
    private byte[] zipFileRetrieved
    private ArrayList<String> warningMessages
    private String RETRIEVE_RESULT_NULL = "Retrieve result instance is NULL"

    /**
     * Constructor of RetrieveMetadata
     * @param filePath contains the file xml
     */
    RetrieveMetadata(org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager.Package metaPackage) {
        this.metaPackage = metaPackage
        warningMessages = new ArrayList<String>()
    }

    /**
     * Checks if status succeeded of retrieve
     * @param retrieveResult contains the respond of SalesForce server
     * @return a boolean
     */
    private void checkStatusSucceeded(RetrieveResult retrieveResult) {
        if (retrieveResult == null) {
            throw new Exception(RETRIEVE_RESULT_NULL)
        }
        if (retrieveResult.getStatus() != RetrieveStatus.Succeeded) {
            throw new Exception("${retrieveResult.getErrorStatusCode()}  msg:   ${retrieveResult.getErrorMessage()}")
        }
    }

    /**
     * Shows all warnings messages that Metadata throw
     * @param strings
     */
    private void loadWarningsMessages(RetrieveMessage[] messages) {
        if (messages != null) {
            for (RetrieveMessage warningMessage : messages) {
                warningMessages.push("WARNING: ${warningMessage.getProblem()}")
            }
        }
    }

    /**
     * Deploys an org using metadata API in the source path specified
     */
    public void executeRetrieve(int poll, int waitTime, Credential credential) {
        MetadataAPI metadataAPI = (MetadataAPI) ForceFactory.getForceAPI(ForceApiType.METADATA, credential)
        metadataAPI.poll = poll
        metadataAPI.waitTime = waitTime
        RetrieveResult retrieveResult = metadataAPI.retrieve(metaPackage)
        checkStatusSucceeded(retrieveResult)
        loadWarningsMessages(retrieveResult.getMessages())
        zipFileRetrieved = retrieveResult.getZipFile()
    }

    /**
     * Gets the zip file as byte[]
     * @return zip file
     */
    public byte[] getZipFileRetrieved() {
        return zipFileRetrieved
    }

    /**
     * Gets a stringBuilder of warning messages
     * @return stringBuilder of warning messages
     */
    public ArrayList<String> getWarningsMessages() {
        return warningMessages
    }
}
