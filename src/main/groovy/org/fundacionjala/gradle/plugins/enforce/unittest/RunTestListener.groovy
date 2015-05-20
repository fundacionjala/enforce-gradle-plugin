/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest

import org.cometd.bayeux.Channel
import org.cometd.bayeux.Message
import org.cometd.bayeux.client.ClientSessionChannel
import org.cometd.bayeux.client.ClientSessionChannel.MessageListener
import org.fundacionjala.gradle.plugins.enforce.streaming.StreamingClient
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.unittest.RunTestTask
import org.fundacionjala.gradle.plugins.enforce.unittest.Apex.ApexClasses
import org.fundacionjala.gradle.plugins.enforce.unittest.Apex.ApexTestItem
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.wsc.soap.ToolingAPI

/**
 * Subscribes to the TestResult system topic and prints out the test results using ApexTestQueueItem and ApexTestResult
 */
class RunTestListener {
    private static final String CHARSET_UTF_8 = "UTF-8"
    private static final String CHANNEL = "/systemTopic/TestResult"
    private static final String SUCCESS_MESSAGE = "All unit tests were executed successfully."
    private static final String SOBJECT_NAME = "sobject"
    private static final String ID_NAME = "Id"
    ToolingAPI toolingAPI
    StreamingClient streamingClient
    OutputStream outputStream
    ArrayList<String> ids
    ApexTestItem apexTestItem
    private Map methodInClass
    private ApexClasses apexClasses
    public boolean done
    public long startTime
    public long endTime

    /**
     * Creates a run test listener to subscribes to the TestResult system topic
     * @param toolingAPI the tooling API class that wraps Salesforce tooling API
     * @param outputStream the output stream to write the test results
     * @param ids is a collection of classes ids
     */
    RunTestListener(ToolingAPI toolingAPI, OutputStream outputStream, ApexClasses apexClasses) {
        this.toolingAPI = toolingAPI
        this.outputStream = outputStream
        this.done = false
        this.apexClasses = apexClasses
        methodInClass = [:]
        streamingClient = new StreamingClient(this.toolingAPI.session.sessionId, this.toolingAPI.session.serverUrl)
    }

    /**
     * Starts the unit test execution, adds and subscribes a message listener
     */
    public void startUnitTestExecution(){
        streamingClient.start()
        addMessageListener()
        subscribeOnChannel()
    }

    /**
     * Adds a message listener to run unit tests
     * @param classes a class names array
     */
    public void addMessageListener(){
        MessageListener messageListenerRunTest = new MessageListener() {
            @Override
            void onMessage(ClientSessionChannel channel, Message message) {
                boolean success = message.isSuccessful()
                if (success) {
                    toolingAPI.runTests(apexClasses.ids as ArrayList<String>)
                }
            }
        }
        this.streamingClient.addMessageListener(Channel.META_SUBSCRIBE, messageListenerRunTest)
    }

    /**
     * Subscribes a message listener to get test apex queue items from server
     */
    public void subscribeOnChannel(){
        startTime = System.currentTimeMillis();
        outputStream.write(Util.getBytes("${RunTestTask.TEST_MESSAGE}:\n", CHARSET_UTF_8))
        outputStream.flush()
        MessageListener messageListenerResult = new MessageListener() {
            @Override
            public void onMessage(ClientSessionChannel channel, Message message) {
                HashMap data = (HashMap) message.getData()
                HashMap sobject = (HashMap) data.get(SOBJECT_NAME)
                String id = (String) sobject.get(ID_NAME)
                writeApexTestItems(toolingAPI.getTestQueueItems(id))
            }
        }
        streamingClient.subscribeOnChannel(CHANNEL, messageListenerResult)
    }

    /**
     * Writes the test results from ApexTestQueueItem and ApexTestResult
     * @param apexTestItems it wraps ApexTestQueueItem and ApexTestResult classes
     */
    public void writeApexTestItems(ApexTestItem apexTestItems) {
        boolean testsCompleted
        String messageTest

        apexTestItems.apexTestResults.each { apexTestResult ->

            String key = "${apexTestResult.getApexClassId()}${apexTestResult.getMethodName()}"

            if ((apexTestResult.getOutcome().equals("Fail") || apexTestResult.getOutcome().equals("CompileFail")) &&
                !methodInClass.containsKey(key)) {
                methodInClass.put(key, apexTestResult.getMethodName())
                apexTestResult.className = apexClasses.getClass(apexTestResult.getApexClassId()).name
                String errorMessage = apexTestResult.message?"\r\t\tMessage: ${apexTestResult.message}":""
                errorMessage = apexTestResult.stackTrace?"${errorMessage}\n\r\t\tStacktrace: ${apexTestResult.stackTrace}":errorMessage
                if(!errorMessage.empty) {
                    messageTest = "\r\t${apexTestResult.className}.${apexTestResult.getMethodName()}\n${errorMessage}\n\n"
                    outputStream.write(Util.getBytes(messageTest, CHARSET_UTF_8))
                    outputStream.flush()
                }
            }
        }

        testsCompleted = apexTestItems.complete

        done = testsCompleted
        if (done) {
            endTime = System.currentTimeMillis()
            long timeExecution = (endTime - startTime)
            if (methodInClass.size() == 0) {
                outputStream.write(Util.getBytes("\r\t${SUCCESS_MESSAGE}\n", CHARSET_UTF_8))
                outputStream.flush()
            }
            outputStream.write(Util.getBytes("\rTotal time: ${Util.formatDurationHMS(timeExecution)}\n", CHARSET_UTF_8))
            outputStream.flush()
            outputStream.close()
            streamingClient.client.disconnect()
            this.apexTestItem = apexTestItems
        }
    }
}
