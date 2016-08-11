/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc.soap

import com.sforce.soap.tooling.*
import com.sforce.ws.ConnectionException
import org.fundacionjala.gradle.plugins.enforce.unittest.Apex.ApexRunTestResult
import org.fundacionjala.gradle.plugins.enforce.unittest.Apex.ApexTestItem
import org.fundacionjala.gradle.plugins.enforce.wsc.Connector
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.ForceAPI

/**
 * A wrapper around soap client that execute queries
 */
class ToolingAPI extends ForceAPI {

    private final String COMMA_SEPARATOR = ", "
    SoapConnection soapConnection
    private final String QUERY_QUEUE_ITEM = "Select Id, ApexClassId, Status " +
                                            "FROM ApexTestQueueItem WHERE ParentJobId = "

    private final String QUERY_TEST_RESULT = "SELECT StackTrace, Message, MethodName, Outcome, ApexClassId " +
                                             "FROM ApexTestResult WHERE AsyncApexJobId = "

    private final String STATUS_COMPLETED = "Completed"
    private final String STATUS_ABORTED = "Aborted"
    private final String STATUS_FAILES = "Failed"

    int numberUnitTest
    int currentUnitTestCompleted
    RunTestsResult runTestResult

    /**
     * Constructs an api connection from the user credential
     * @param credential
     */
    ToolingAPI(Credential credential) {
        super(credential)
    }

    /**
     * Constructs an api connection from the user credential and connector
     * @param credential
     */
    ToolingAPI(Credential credential, Connector connector) {
        super(credential, connector)
        numberUnitTest = 0
        currentUnitTestCompleted = 0
    }

    /**
     * Gets the tooling url to create the connector
     */
    @Override
    String getUrl() {
        return connector.getToolingServerUrl()
    }

    /**
     * Creates a soap connection
     */
    @Override
    void createConnection() {
        soapConnection = com.sforce.soap.tooling.Connector.newConnection(connectorConfig)
        soapConnection.setDebuggingHeader(getLogInfoCollectors(), LogType.Detail)
    }

    /**
     * Gets a log info categories for debugging header of the metadata connection
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
     * Runs unit test asynchronously
     * @param apexTestClassIds a class ids array
     */
    public void runTests(ArrayList<String> ids) {
        try {
            soapConnection.runTestsAsynchronous(ids.join(COMMA_SEPARATOR))
        } catch (ConnectionException e) {
            throw new Exception("Error to run the async unit tests", e)
        }
    }

    /**
     * Runs unit test synchronously
     * @param classesNames a class names array
     */
    public void runTestsSynchronous(ArrayList<String> classesNames) {

        RunTestsRequest request = new RunTestsRequest()
        request.classes = classesNames.toArray()
        runTestResult = soapConnection.runTests(request)
    }

    /**
     * Gets an apex test item array with all apex test queue items completed from server
     * @param asyncApexJobId the async apex job id from server
     * @return a object apex test item
     */
    public ApexTestItem getTestQueueItems(String jobId) {

        String queryByJob = "${QUERY_QUEUE_ITEM}'${jobId}'"
        ApexTestItem apexTestItem = new ApexTestItem()
        apexTestItem.complete= verifyStatusRunTest(soapConnection.query(queryByJob))
        apexTestItem.apexTestResults= getApexTestResult(jobId)

        return apexTestItem
    }

    /**
     * Verify if unit test is finished in the server and count many test is completed, aborted and failes
     * @param queryResult is result of query
     * @return a boolean result
     */
    public boolean verifyStatusRunTest(QueryResult queryResult) {

        currentUnitTestCompleted = queryResult.getSize()
        numberUnitTest = 0
        for (SObject sObject : queryResult.getRecords()) {
            ApexTestQueueItem apexTestQueueItem = sObject as ApexTestQueueItem
            if(apexTestQueueItem.getStatus() == STATUS_COMPLETED || apexTestQueueItem.getStatus() == STATUS_ABORTED ||
                    apexTestQueueItem.getStatus() == STATUS_FAILES ) {
                numberUnitTest++
            }
        }

        return (currentUnitTestCompleted == numberUnitTest)
    }

    /**
     * Gets an apex test result array from server
     * @param asyncApexJobId the async apex job id from server
     * @return a array contain information about apex run test result
     */
    private ArrayList<ApexRunTestResult> getApexTestResult(String jobId) {

        String queryByJob = "${QUERY_TEST_RESULT} '${jobId}'"
        QueryResult queryResult = soapConnection.query(queryByJob)
        ArrayList<ApexRunTestResult> apexRunTestResultArrayList = new ArrayList<ApexRunTestResult>()

        for (SObject sObject : queryResult.getRecords()) {
            ApexTestResult apexTestQueueItem = sObject as ApexTestResult
            ApexRunTestResult apexRunTestResult = new ApexRunTestResult()
            apexRunTestResult.apexClassId = apexTestQueueItem.getApexClassId()
            apexRunTestResult.methodName = apexTestQueueItem.getMethodName()
            if(apexTestQueueItem.getStackTrace()) {
                apexRunTestResult.stackTrace = apexTestQueueItem.getStackTrace()
            }
            if(apexTestQueueItem.getMessage()) {
                apexRunTestResult.message = apexTestQueueItem.getMessage()
            }
            apexRunTestResult.outcome = apexTestQueueItem.getOutcome()
            apexRunTestResultArrayList.push(apexRunTestResult)
        }

        return apexRunTestResultArrayList
    }
}
