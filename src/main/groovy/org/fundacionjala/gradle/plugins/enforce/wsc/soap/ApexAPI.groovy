/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc.soap

import com.sforce.soap.apex.*
import com.sforce.ws.ConnectionException
import org.fundacionjala.gradle.plugins.enforce.wsc.Connector
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.ForceAPI

/**
 * This class wraps Apex Api and exposes the WSDL methods
 */
class ApexAPI extends ForceAPI {
    SoapConnection soapConnection

    /**
     * Constructs an api connection from the user credential
     * @param credential
     */
    ApexAPI(Credential credential) {
        super(credential)
    }

    /**
     * Constructs an api connection from the user credential and connector
     * @param credential the user credential to login
     */
    ApexAPI(Credential credential, Connector connector) {
        super(credential, connector)
    }

    /**
     * Gets the apex server url
     */
    @Override
    public String getUrl() {
        return connector.getApexServerUrl()
    }

    /**
     * Creates a soap connection
     */
    @Override
    void createConnection() {
        soapConnection = com.sforce.soap.apex.Connector.newConnection(connectorConfig)
        soapConnection.setDebuggingHeader(getLogInfoCollectors(), LogType.Detail)
    }

    /**
     * Gets a logs info for the soap connection
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
     * Executes anonymously code apex from a string
     * @param apexCode the apex code content
     * @throws ConnectionException if an connection error has occurred
     */
    public String executeApex(String apexCode) throws ConnectionException {

        ExecuteAnonymousResult executeAnonymousResult = soapConnection.executeAnonymous(apexCode)
        if (!executeAnonymousResult.isSuccess()) {
            String message = executeAnonymousResult.getCompileProblem()
            if (executeAnonymousResult.isCompiled()) {
                message = "${message}\n${executeAnonymousResult.getExceptionMessage()}\n${executeAnonymousResult.getExceptionStackTrace()}"
            }
            throw new RuntimeException("Compilation error:\n${message}")
        }
        soapConnection.getDebuggingInfo().getDebugLog()
    }

    /**
     * Writes the debug log in a file
     * @param debugLog the debug log content
     * @param filePath the file path where will be written the debug log
     */
    public void writeOutput(String debugLog, String filePath) {
        new File(filePath).withWriter { writer ->
            writer << debugLog
        }
        println "Apex output available at:${filePath}"
    }

    /**
     * Reads code apex from a file
     * @param apexFilePath the apex file path
     * @return the text read in UTF-8 format
     */
    private String readApexFile(String apexFilePath) {
        String content = new File(apexFilePath).getText('UTF-8')
        return content

    }

    /**
     * Executes code apex from a file
     * @param apexFilePath the apex file path
     */
    public String executeApexFile(String apexFilePath) {
        println "Executing Apex code at: ${apexFilePath}"
        String output = executeApex(readApexFile(apexFilePath))
        return output
    }

    /**
     * Executes unit tests from a request to run test
     * @param testsRequest the run tests request to run unit tests
     * @throws ConnectionException if a connection error has occurred
     */
    public RunTestsResult runTests(RunTestsRequest testsRequest) throws ConnectionException {
        RunTestsResult testsResult = soapConnection.runTests(testsRequest)
        testsResult
    }
}
