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

    private String fileParamValue
    private ToolingAPI toolingAPI
    private Map classAndTestMap = [:]
    private Boolean refreshClassAndTestMap = false

    public TestSelectorByReference(ArrayList<String> testClassNameList, ToolingAPI toolingAPI, String fileParamValue, Boolean refreshClassAndTestMap) {
        super(testClassNameList)
        this.toolingAPI = toolingAPI
        this.fileParamValue = fileParamValue ? fileParamValue.replace(".${MetadataComponents.CLASSES.getExtension()}", "") : null
        //TODO: if this.fileParamValue == * -> it can mean run all test related to last changes, get classNames from ComponentMonitor '.fileTracker.data' HISTORY FILE?
        this.refreshClassAndTestMap = refreshClassAndTestMap
    }

    private void buildReferences() {
        //TODO: persist and request the mapping in the local machine?
        JsonSlurper jsonSlurper = new JsonSlurper()
        if (this.refreshClassAndTestMap) {
            toolingAPI.httpAPIClient.deleteContainer(RunTestTaskConstants.METADATA_CONTAINER_NAME)
        }
        Map containerResp = toolingAPI.httpAPIClient.createContainer(RunTestTaskConstants.METADATA_CONTAINER_NAME)
        String containerId = containerResp["Id"]
//        logger.error('containerId: '+containerId)
        if (containerResp["isNew"]) {
//            logger.error('building apexClassMember for: '+testClassNameList)
            ArrayList<String> apexClassMemberId = toolingAPI.httpAPIClient.createApexClassMember(containerId, testClassNameList)
//            logger.error('apexClassMemberId:'+apexClassMemberId)
            String containerAsyncRequestId = toolingAPI.httpAPIClient.createContainerAsyncRequest(containerId)
//            logger.error('containerAsyncRequestId:'+containerAsyncRequestId)
            String requestStatus
            while (requestStatus != 'Completed') {
                sleep(1000)
                requestStatus = jsonSlurper.parseText(toolingAPI.httpAPIClient.executeQuery("SELECT State FROM ContainerAsyncRequest WHERE Id='${containerAsyncRequestId}'")).records[0].State.toString()
//                logger.error('requestStatus:'+requestStatus)
            }
        }

        String apexClassMemberQuery = "SELECT FullName, ContentEntityId, SymbolTable FROM ApexClassMember WHERE MetadataContainerId = '${containerId[0..14]}'"
//        logger.error('apexClassMemberQuery: '+apexClassMemberQuery)
        jsonSlurper.parseText(toolingAPI.httpAPIClient.executeQuery(apexClassMemberQuery)).records.each { classMember ->
            classMember.SymbolTable.each() { symbolTableResult ->
                symbolTableResult.every() { entry ->
                    if (entry.getKey() == "externalReferences") {
//                        logger.error('testClass: '+ classMember.FullName )
                        entry.getValue().each() {
                            if (it["namespace"] != "System") {
//                                logger.error('\t Class: '+ it["name"] )
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
//        logger.error('this.refreshClassAndTestMap: '+this.refreshClassAndTestMap)
        if (!classAndTestMap) {
            buildReferences()
        }

        if (this.fileParamValue) {
            ArrayList<String> testClassList = new ArrayList<String>()
            classAndTestMap.keySet().each { className ->
                this.fileParamValue.tokenize(',').each { wildCard ->
                    //if (className.contains(wildCard)) { //TODO: maybe we can work for wildCards at this point - if (contains("*") || startsWidth("*) endsWidth("*)) -> .replace("*", "")
                    if (className == wildCard ) {
                        testClassList.addAll(classAndTestMap.get(className))
                    }
                }
            }
            return testClassList.unique()
        }
        return []
    }
}
