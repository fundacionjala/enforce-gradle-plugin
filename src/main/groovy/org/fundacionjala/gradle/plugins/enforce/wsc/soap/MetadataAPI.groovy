/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc.soap

import com.sforce.soap.metadata.*
import org.fundacionjala.gradle.plugins.enforce.exceptions.deploy.DeployException
import org.fundacionjala.gradle.plugins.enforce.exceptions.deploy.InfoDeploy
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.ForceAPI
import org.fundacionjala.gradle.plugins.enforce.wsc.InspectorResults

/**
 * This class wraps Metadata Api and exposes WSDL methods
 */
class MetadataAPI extends ForceAPI {
    private static final int EMPTY = 0
    private final int THOUSAND = 1000
    private static final String LINE_TEXT = "line"
    private static final String COLUMN_TEXT = "column"
    private static final String AT_TEXT = "at"
    private final String STARTING_RETRIEVE = "Starting retrieve..."
    private final String WAITING_RETRIEVE = 'Waiting for retrieve result...'
    private final String RETRIEVE_COMPLETED = 'Retrieve result completed'
    MetadataConnection metadataConnection
    InspectorResults inspectorResults
    int poll = 200
    int waitTime = 10
    /**
     * Constructs an api connection from the user credential
     * @param credential
     */
    MetadataAPI(Credential credential) {
        super(credential)
        inspectorResults = new InspectorResults(metadataConnection, System.out)
    }

    /**
     * Constructs an api connection from the user credential and connector
     * @param credential
     */
    MetadataAPI(Credential credential, org.fundacionjala.gradle.plugins.enforce.wsc.Connector connector) {
        super(credential, connector)
        inspectorResults = new InspectorResults(metadataConnection, System.out)
    }

    /**
     * Gets the metadata server url
     */
    @Override
    String getUrl() {
        return connector.getMetadataServerUrl()
    }

    /**
     * Creates a metadata connection
     */
    @Override
    void createConnection() {
        metadataConnection = new MetadataConnection(connectorConfig)
        metadataConnection.setDebuggingHeader(getLogInfoCollectors(), LogType.Detail)
    }

    /**
     * Gets logs info for the metadata connection
     */
    private LogInfo[] getLogInfoCollectors() {
        LogInfo infoAll = new LogInfo()
        infoAll.setCategory(LogCategory.All)
        infoAll.setLevel(LogCategoryLevel.Error)

        LogInfo infoApex = new LogInfo()
        infoApex.setCategory(LogCategory.Apex_code)
        infoApex.setLevel(LogCategoryLevel.Error)

        LogInfo infoDB = new LogInfo()
        infoDB.setCategory(LogCategory.Db)
        infoDB.setLevel(LogCategoryLevel.Info)
        return [infoAll, infoApex, infoDB]
    }

    /**
     * Deploys asynchronously all salesforce components from a zip file without performing any validation
     * @param sourcePath the zip file to deploy
     * @return the deploy result obtained from server
     */
    public DeployResult deploy(String sourcePath, boolean checkOnly) {
        return genericDeploy(sourcePath, checkOnly)
    }

    /**
     * Deploys asynchronously all salesforce components from a zip file
     * @param sourcePath the zip file to deploy
     * @param checkOnly whether or not to perform a validation
     * @return the deploy result obtained from server
     */
    private DeployResult genericDeploy(String sourcePath, boolean checkOnly) {
        def byteArray = new File(sourcePath).getBytes()
        DeployOptions deployOptions = new DeployOptions()
        deployOptions.setPerformRetrieve(false)
        deployOptions.setRollbackOnError(true)
        deployOptions.setSinglePackage(true)
        deployOptions.setAllowMissingFiles(true)
        deployOptions.setPurgeOnDelete(true)
        deployOptions.setIgnoreWarnings(false)
        deployOptions.setCheckOnly(checkOnly)
        AsyncResult asyncResult = metadataConnection.deploy(byteArray, deployOptions)
        DeployResult deployResult = inspectorResults.waitForDeployResult(asyncResult.id, poll, waitTime * THOUSAND)

        return deployResult
    }

    /**
     * Retrieve salesforce components in a zip
     * @param sourcePath the zip file
     * @return the retrieve result
     */
    public RetrieveResult retrieve(Package metaPackage) {
        retrieve(metaPackage, null)
    }

    public RetrieveResult retrieve(Package metaPackage, ArrayList<String> specificFiles) {
        RetrieveRequest retrieveRequest = new RetrieveRequest()
        retrieveRequest.setUnpackaged(metaPackage)
        if (specificFiles && !specificFiles.isEmpty()) {
            retrieveRequest.setSpecificFiles(specificFiles.toArray() as String[])
            retrieveRequest.setSinglePackage(true)
        }
        println STARTING_RETRIEVE
        AsyncResult asyncResult = metadataConnection.retrieve(retrieveRequest)
        println WAITING_RETRIEVE
        RetrieveResult retrieveResult = inspectorResults.waitForRetrieveResult(asyncResult.getId(), poll, waitTime * THOUSAND)
        println RETRIEVE_COMPLETED
        return retrieveResult
    }

    /**
     * Print out all errors related to the deploy
     * @param result - DeployResult
     */
    static void printDeployResult(DeployResult result) {
        DeployDetails deployDetails = result.getDetails()

        StringBuilder errorMessageBuilder = new StringBuilder()
        ArrayList<InfoDeploy> deployInfoArray = []
        if (deployDetails != null) {
            DeployMessage[] componentFailures = deployDetails.getComponentFailures()
            for (DeployMessage message : componentFailures) {
                InfoDeploy infoDeploy = new InfoDeploy()
                infoDeploy.setFileName(message.getFileName())
                infoDeploy.setLine(message.getLineNumber())
                infoDeploy.setColumn(message.getColumnNumber())
                infoDeploy.setProblem(message.getProblem())
                deployInfoArray.push(infoDeploy)
                String lineAndColumn = (message.getLineNumber() == EMPTY ? "" :
                        ("${LINE_TEXT} ${message.getLineNumber()}, ${COLUMN_TEXT} ${message.getColumnNumber()}"))

                if (lineAndColumn.length() == EMPTY && !message.getFileName().equals(message.getFullName())) {
                    lineAndColumn = "(${message.getFullName()})"
                }
                errorMessageBuilder.append("${message.getFileName()}: ${message.getProblem()} ${AT_TEXT} ${lineAndColumn}.").
                        append('\n')
            }
        }
        if (errorMessageBuilder.length() != EMPTY) {
            throw new DeployException(errorMessageBuilder.toString(), deployInfoArray)
        }
    }
}
