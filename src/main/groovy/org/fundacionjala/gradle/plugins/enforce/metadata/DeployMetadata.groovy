/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.metadata

import com.sforce.soap.metadata.DeployResult
import org.fundacionjala.gradle.plugins.enforce.wsc.Connector
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.soap.MetadataAPI

/**
 * Deploys an org using metadata API
 */
class DeployMetadata {

    public static final String START_MESSAGE = "Starting deploy..."
    public static final String ERROR_MESSAGE = "Deploy errors:"
    public static final String SUCCESS_MESSAGE = "The files were successfully deployed"

    String startMessage
    String errorMessage
    String successMessage
    String path

    /**
     * Constructor: sets defaults
     */
    DeployMetadata(){
        startMessage = START_MESSAGE
        successMessage = SUCCESS_MESSAGE
    }

    /**
     * Deploys an org using metadata API in the source path specified
     */
    void deploy(int poll, int waitTime, Credential credential, String apiVersion, boolean checkOnly) {
        MetadataAPI metadataAPI = new MetadataAPI(credential, new Connector(credential.loginFormat, apiVersion))
        metadataAPI.poll = poll
        metadataAPI.waitTime = waitTime
        println startMessage
        DeployResult deployResult = metadataAPI.deploy(path, checkOnly)
        checkStatusDeploy(deployResult)
    }

    /**
     * Checks if the deploy result is executed successfully
     */
    void checkStatusDeploy(DeployResult deployResult) {
        if (!deployResult.isSuccess()) {
            MetadataAPI.printDeployResult(deployResult)
        }
        println ("\r${successMessage}\n")
    }
}
