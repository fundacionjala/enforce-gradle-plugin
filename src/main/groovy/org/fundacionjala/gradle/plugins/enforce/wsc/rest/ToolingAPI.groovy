/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc.rest

import groovy.json.JsonSlurper
import org.fundacionjala.gradle.plugins.enforce.unittest.Apex.ApexRunTestResult
import org.fundacionjala.gradle.plugins.enforce.unittest.Apex.ApexTestItem
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.ForceAPI

/**
 * A wrapper around http client that execute queries
 */
class ToolingAPI extends ForceAPI {

    private final String QUERY_QUEUE_ITEM = "Select Id, ApexClassId, Status " +
                                            "FROM ApexTestQueueItem WHERE ParentJobId = "

    private final String QUERY_TEST_RESULT = "SELECT StackTrace, Message, MethodName, Outcome, ApexClassId " +
                                             "FROM ApexTestResult WHERE AsyncApexJobId = "

    private final String STATUS_COMPLETED = "Completed"
    private final String STATUS_ABORTED = "Aborted"
    private final String STATUS_FAILES = "Failed"
    HttpAPIClient httpAPIClient



    int numberUnitTest
    int currentUnitTestCompleted

    ToolingAPI(Credential credential) {
        super(credential)
        numberUnitTest = 0
        currentUnitTestCompleted = 0
    }

    @Override
    String getUrl() {
        return connector.getApexServerUrl()
    }

    @Override
    void createConnection() {
        httpAPIClient = new HttpAPIClient(session.serverUrl, session.sessionId)
    }

    /**
     * Creates a apex test item using information ApexTestQueueItem and ApexTestResult
     * @param jobId is a job id that is running
     * @return a apex test item
     */
    public ApexTestItem getTestQueueItems(String jobId) {

        String queryByJob = "${QUERY_QUEUE_ITEM}'${jobId}'"

        String jsonResult =  httpAPIClient.executeQuery(queryByJob)
        JsonSlurper jsonSlurper = new JsonSlurper()
        Object apexTestItems = jsonSlurper.parseText(jsonResult)

        ApexTestItem apexTestItem = new ApexTestItem()
        apexTestItem.complete = verifyStatusRunTest(apexTestItems)
        apexTestItem.apexTestResults = getApexTestResult(jobId)

        return apexTestItem
    }

    /**
     * verifies if task run unit test is finished in salesforce server
     * @param apexTestItems is json object contain information about unit test
     * @return a result boolean
     */
    public boolean verifyStatusRunTest(Object apexTestItems) {

        currentUnitTestCompleted = apexTestItems.records.size()
        numberUnitTest = 0
        for ( record in apexTestItems.records) {
            if(record.Status == STATUS_COMPLETED || record.Status == STATUS_ABORTED ||
               record.Status == STATUS_FAILES ) {
                numberUnitTest++
            }
        }

        return (currentUnitTestCompleted == numberUnitTest)
    }

    /**
     * Creates an array of apex test result using information ApexTestResult
     * @param jobId is a job id that is running
     * @return an array of apex test result
     */
    private ArrayList<ApexRunTestResult> getApexTestResult(String jobId) {

        String queryByJob = "${QUERY_TEST_RESULT} '${jobId}'"
        String jsonResult =  httpAPIClient.executeQuery(queryByJob)
        JsonSlurper jsonSlurper = new JsonSlurper()
        Object apexTestResults = jsonSlurper.parseText(jsonResult)
        ArrayList<ApexRunTestResult> apexTestResultsArrayList = new ArrayList<ApexRunTestResult>()
        apexTestResults.records.each { apexTestResultJson ->
            ApexRunTestResult apexTestResult = new ApexRunTestResult()
            apexTestResult.apexClassId = apexTestResultJson.ApexClassId
            apexTestResult.methodName = apexTestResultJson.MethodName
            apexTestResult.stackTrace = apexTestResultJson.StackTrace
            apexTestResult.message = apexTestResultJson.Message
            apexTestResult.outcome = apexTestResultJson.Outcome
            apexTestResultsArrayList.push(apexTestResult)
        }

        return apexTestResultsArrayList
    }
}
