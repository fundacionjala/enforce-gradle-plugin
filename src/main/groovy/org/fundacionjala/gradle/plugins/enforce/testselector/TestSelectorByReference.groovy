/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.testselector

import groovy.json.JsonSlurper
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.unittest.RunTestTaskConstants
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.runtesttask.CustomComponentTracker
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.IArtifactGenerator

class TestSelectorByReference extends TestSelector  {

    private String srcPath
    private String filesParameterValue
    private IArtifactGenerator artifactGenerator
    private Map classAndTestMap = [:]
    private Boolean refreshClassAndTestMap = false
    private Boolean displayNoChangesMessage = false

    private final String APEX_CLASS_MEMBER_QUERY = 'SELECT FullName, ContentEntityId, SymbolTable FROM ApexClassMember WHERE MetadataContainerId = \'%s\''
    private final String CONTAINER_ASYNC_REQUEST_QUERY = 'SELECT State FROM ContainerAsyncRequest WHERE Id=\'%s\''
    private final String NO_RECENT_CHANGE_MESSAGE = 'You do not have any recent change to use to select Test classes.'
    private final String GETTING_ELEMENTS_FROM_FILETRACKER_MSG = "Getting Apex classes from file tracker.."
    private final String NUM_ELEMENTS_FOUND_MSG = "%d elements found.."
    private final String REMOVE_METADATA_CONTAINER_MSG = "\tRemoving existing MetaData Container.."
    private final String GENERATE_METADATA_CONTAINER_MSG = "\tGenerating MetaData Container.."
    private final String GENERATE_APEX_CLASS_MEMBER_MSG = "\tGenerating Apex Class Members.."
    private final String PROCESSED_ELEMENTS_MSG = "\r\t\tProcessed %d/%d elements"
    private final String GENERATE_CONTAINER_REQUESTER_MSG = "\tGenerating Container Async Requester.."
    private final String REQUEST_SYMBOL_TABLE_MSG = "\tRequesting Symbol Tables.."
    private final String CONTAINER_ASYNC_REQUEST_DONE_QUERY = "\r\tRequesting Symbol Tables, done"
    private final String BUILD_TESTCLASS_MAP_MSG = "\tBuilding Tests/Classes mapping.."
    private final String BUILD_DEPENDENCIES_MSG = "Building Apex class dependencies from SFDC"
    private final String BUILD_DEPENDENCIES_DONE_MSG = "Building Apex class dependencies from SFDC, done"
    private final String TEST_CLASSES_SUMMARY_MSG = "\n --- Test Class to run summary ---\n"
    private final String APEX_CLASS_RELATED_TESTS_MSG = "Apex Class: %s \n Related Test Class(es): %s\n"

    /**
     * TestSelectorByReference class constructor
     * @param testClassNameList list of all available test class names
     * @param artifactGenerator instance reference of the current HttpAPIClient
     * @param filesParameterValue value provided by the user to filter the class names
     * @param refreshClassAndTestMap value provided by the user to specify refresh the class-test mapping
     */
    public TestSelectorByReference(String srcPath, ArrayList<String> testClassNameList, IArtifactGenerator artifactGenerator
                                   , String filesParameterValue, Boolean refreshClassAndTestMap) {
        super(testClassNameList)
        this.srcPath = srcPath
        this.artifactGenerator = artifactGenerator
        this.filesParameterValue = filesParameterValue
        this.refreshClassAndTestMap = refreshClassAndTestMap
    }

    /**
     * Initializes all local variables
     */
    private void init() {
        if (this.filesParameterValue) {
            this.filesParameterValue = this.filesParameterValue.replace(".${MetadataComponents.CLASSES.getExtension()}", "")
            if (this.filesParameterValue == RunTestTaskConstants.RUN_ALL_UPDATED_PARAM_VALUE) {
                displayMessage(GETTING_ELEMENTS_FROM_FILETRACKER_MSG)
                CustomComponentTracker customComponentTracker = new CustomComponentTracker(this.srcPath)
                this.filesParameterValue = (customComponentTracker.getFilesNameByExtension([MetadataComponents.CLASSES.getExtension()])).join("','")
                this.filesParameterValue = this.filesParameterValue.replace(".${MetadataComponents.CLASSES.getExtension()}", "")
                displayMessage(sprintf(NUM_ELEMENTS_FOUND_MSG, [this.filesParameterValue.size()]))
                if (!this.filesParameterValue) {
                    displayNoChangesMessage = true
                }
            }
        }
    }

    /**
     * Builds the class-test mapping
     */
    private void buildReferences() {
        JsonSlurper jsonSlurper = new JsonSlurper()
        if (this.refreshClassAndTestMap) {
            displayMessage(REMOVE_METADATA_CONTAINER_MSG)
            artifactGenerator.deleteContainer(RunTestTaskConstants.METADATA_CONTAINER_NAME)
        }
        displayMessage(GENERATE_METADATA_CONTAINER_MSG)
        Map containerResp = artifactGenerator.createContainer(RunTestTaskConstants.METADATA_CONTAINER_NAME)
        String containerId = containerResp["Id"]
        if (containerResp["isNew"]) {
            ArrayList<String> apexClassMemberId = []
            displayMessage(GENERATE_APEX_CLASS_MEMBER_MSG)
            def processedClasses = 0
            testClassNameList.collate(100).each {
                apexClassMemberId.addAll(artifactGenerator.createApexClassMember(containerId, it))
                processedClasses += it.size()
                displayMessage(sprintf(PROCESSED_ELEMENTS_MSG, [processedClasses, testClassNameList.size()]))
            }
            displayMessage(GENERATE_CONTAINER_REQUESTER_MSG)
            String containerAsyncRequestId = artifactGenerator.createContainerAsyncRequest(containerId)
            displayMessage(REQUEST_SYMBOL_TABLE_MSG)
            String requestStatus
            String requestStatusQuery = sprintf(CONTAINER_ASYNC_REQUEST_QUERY, [containerAsyncRequestId])
            while (requestStatus != 'Completed') {
                sleep(1000)
                requestStatus = jsonSlurper.parseText(artifactGenerator.executeQuery(requestStatusQuery)).records[0].State.toString()
            }
            displayMessage(CONTAINER_ASYNC_REQUEST_DONE_QUERY)
        }
        displayMessage(BUILD_TESTCLASS_MAP_MSG)
        String apexClassMemberQuery = sprintf(APEX_CLASS_MEMBER_QUERY, [containerId[0..14]])
        jsonSlurper.parseText(artifactGenerator.executeQuery(apexClassMemberQuery)).records.each { classMember ->
            classMember.SymbolTable.each() { symbolTableResult ->
                symbolTableResult.every() { entry ->
                    if (entry.getKey() == "externalReferences") {
                        entry.getValue().each() {
                            if (it["namespace"] != "System") {
                                String classToAdd
                                if (it["namespace"]) {
                                    classToAdd = it["namespace"]
                                } else {
                                    classToAdd = it["name"]
                                }
                                if (!classAndTestMap.containsKey(classToAdd)) {
                                    classAndTestMap.put(classToAdd, new ArrayList<String>())
                                }
                                classAndTestMap.get(classToAdd).add(classMember.FullName)
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    ArrayList<String> getTestClassNames() {
        init()
        ArrayList<String> testClassList = new ArrayList<String>()
        if (displayNoChangesMessage) {
            displayMessage(NO_RECENT_CHANGE_MESSAGE, true)
        }

        if (this.filesParameterValue) {
            if (!classAndTestMap) {
                displayMessage(BUILD_DEPENDENCIES_MSG)
                buildReferences()
                displayMessage(BUILD_DEPENDENCIES_DONE_MSG)
            }
            displayMessage(TEST_CLASSES_SUMMARY_MSG)
            classAndTestMap.keySet().each { className ->
                this.filesParameterValue.tokenize(RunTestTaskConstants.FILE_SEPARATOR_SIGN).each { wildCard ->
                    //if (className.contains(wildCard)) { //TODO: maybe we can work for wildCards at this point - if (contains("*") || startsWidth("*) endsWidth("*)) -> .replace("*", "")
                    if (className == wildCard ) {
                        displayMessage(sprintf(APEX_CLASS_RELATED_TESTS_MSG, [className, classAndTestMap.get(className).unique().toString()]))
                        testClassList.addAll(classAndTestMap.get(className))
                    }
                }
            }
        }

        return testClassList.unique()
    }

    /**
     * Displays a quiet log message
     * @param msg message to display
     */
    private void displayMessage(String msg) {
        displayMessage(msg, false)
    }

    /**
     * Displays a quiet or error log message
     * @param msg message to display
     * @param isError specifies the kind of message quiet/error
     */
    private void displayMessage(String msg, Boolean isError) {
        if (logger) {
            if (isError) {
                logger.error(msg)
            } else {
                logger.quiet(msg)
            }
        }
    }
}
