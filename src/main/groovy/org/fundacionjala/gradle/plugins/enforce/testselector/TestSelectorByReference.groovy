/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.testselector

import groovy.json.JsonSlurper
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.unittest.RunTestTaskConstants
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.ToolingAPI

class TestSelectorByReference extends TestSelector  {

    private String filesParameterValue
    private ToolingAPI toolingAPI
    private Map classAndTestMap = [:]
    private Boolean refreshClassAndTestMap = false

    private final String APEX_CLASS_MEMBER_QUERY = 'SELECT FullName, ContentEntityId, SymbolTable FROM ApexClassMember WHERE MetadataContainerId = \'%1$s\''
    private final String CONTAINER_ASYNC_REQUEST_QUERY = 'SELECT State FROM ContainerAsyncRequest WHERE Id=\'%1$s\''

    /**
     * TestSelectorByReference class constructor
     * @param testClassNameList list of all available test class names
     * @param toolingAPI instance reference of the current ToolingAPI
     * @param filesParameterValue value provided by the user to filter the class names
     * @param refreshClassAndTestMap value provided by the user to specify refresh the class-test mapping
     */
    public TestSelectorByReference(ArrayList<String> testClassNameList, ToolingAPI toolingAPI, String filesParameterValue, Boolean refreshClassAndTestMap) {
        super(testClassNameList)
        this.toolingAPI = toolingAPI
        this.filesParameterValue = null
        if (filesParameterValue) {
            this.filesParameterValue = filesParameterValue.replace(".${MetadataComponents.CLASSES.getExtension()}", "")
        }
        //TODO: if this.filesParameterValue == * -> it can mean run all test related to last changes, get classNames from ComponentMonitor '.fileTracker.data' HISTORY FILE?
        this.refreshClassAndTestMap = refreshClassAndTestMap
    }

    /**
     * Builds the class-test mapping
     */
    private void buildReferences() {
        //TODO: persist and request the mapping in the local machine?
        JsonSlurper jsonSlurper = new JsonSlurper()
        if (this.refreshClassAndTestMap) {
            toolingAPI.httpAPIClient.deleteContainer(RunTestTaskConstants.METADATA_CONTAINER_NAME)
        }
        Map containerResp = toolingAPI.httpAPIClient.createContainer(RunTestTaskConstants.METADATA_CONTAINER_NAME)
        String containerId = containerResp["Id"]
        if (containerResp["isNew"]) {
            ArrayList<String> apexClassMemberId = toolingAPI.httpAPIClient.createApexClassMember(containerId, testClassNameList)
            String containerAsyncRequestId = toolingAPI.httpAPIClient.createContainerAsyncRequest(containerId)
            String requestStatus
            String requestStatusQuery = sprintf( CONTAINER_ASYNC_REQUEST_QUERY, [containerAsyncRequestId])
            while (requestStatus != 'Completed') {
                sleep(1000)
                requestStatus = jsonSlurper.parseText(toolingAPI.httpAPIClient.executeQuery(requestStatusQuery)).records[0].State.toString()
            }
        }

        String apexClassMemberQuery = sprintf( APEX_CLASS_MEMBER_QUERY, [containerId[0..14]])
        jsonSlurper.parseText(toolingAPI.httpAPIClient.executeQuery(apexClassMemberQuery)).records.each { classMember ->
            classMember.SymbolTable.each() { symbolTableResult ->
                symbolTableResult.every() { entry ->
                    if (entry.getKey() == "externalReferences") {
                        entry.getValue().each() {
                            if (it["namespace"] != "System") {
                                if (!classAndTestMap.containsKey(it["name"])) {
                                    classAndTestMap.put(it["name"], new ArrayList<String>())
                                }
                                classAndTestMap.get(it["name"]).add(classMember.FullName)
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    ArrayList<String> getTestClassNames() {
        ArrayList<String> testClassList = new ArrayList<String>()
        if (!classAndTestMap) {
            buildReferences()
        }
        if (this.filesParameterValue) {
            classAndTestMap.keySet().each { className ->
                this.filesParameterValue.tokenize(RunTestTaskConstants.FILE_SEPARATOR_SIGN).each { wildCard ->
                    //if (className.contains(wildCard)) { //TODO: maybe we can work for wildCards at this point - if (contains("*") || startsWidth("*) endsWidth("*)) -> .replace("*", "")
                    if (className == wildCard ) {
                        testClassList.addAll(classAndTestMap.get(className))
                    }
                }
            }
        }
        return testClassList.unique()
    }
}
