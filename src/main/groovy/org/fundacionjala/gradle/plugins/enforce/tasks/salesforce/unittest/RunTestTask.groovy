/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.unittest

import com.sforce.soap.apex.*
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.SalesforceTask
import org.fundacionjala.gradle.plugins.enforce.testselector.TestSelectorModerator
import org.fundacionjala.gradle.plugins.enforce.unittest.Apex.ApexClass
import org.fundacionjala.gradle.plugins.enforce.unittest.Apex.ApexClasses
import org.fundacionjala.gradle.plugins.enforce.unittest.Apex.ApexRunTestResult
import org.fundacionjala.gradle.plugins.enforce.unittest.RunTestListener
import org.fundacionjala.gradle.plugins.enforce.unittest.TestResultReport
import org.fundacionjala.gradle.plugins.enforce.unittest.report.HtmlManager
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.ToolingAPI
import org.fundacionjala.gradle.plugins.enforce.wsc.soap.ApexAPI
import org.gradle.api.logging.LogLevel
import org.gradle.logging.ProgressLoggerFactory

import java.nio.file.Paths

/**
 * This class generates the unit tests and coverage files
 * GP is a gradle properties
 */
class RunTestTask extends SalesforceTask {
    private String pathClasses
    private HtmlManager htmlManager
    public static final String TEST_MESSAGE = RunTestTaskConstants.UNIT_TEST_RESULT
    Boolean async
    String jsonByClasses
    String jsonCoverageLines
    String jsonByTriggers
    FileWriter fileWriterReport
    String folderReport
    ToolingAPI toolingAPI
    ApexAPI apexAPI
    RunTestListener runTestListener
    TestResultReport testResultReport
    ArrayList<ApexRunTestResult> apexTestResultArrayList
    ArrayList<String> classesToExecute

    def factory = services.get(ProgressLoggerFactory)
    def progressLogger = factory.newOperation(RunTestTaskConstants.SOME_LOG_CATEGORY)

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    RunTestTask() {
        super(RunTestTaskConstants.DESCRIPTION_TASK, RunTestTaskConstants.TEST_GROUP)
        classesToExecute = []
    }

    /**
     * Prepares directory for reports
     */
    @Override
    void setup() {
        pathClasses = Paths.get(projectPath, RunTestTaskConstants.CLASS_DIRECTORY).toString()
        folderReport = getFolderReportPath()
        fileManager.createNewDirectory(folderReport)
        String folderPages = Paths.get(folderReport, RunTestTaskConstants.NAME_FOLDER_PAGES).toString()
        fileManager.createNewDirectory(folderPages)
        fileWriterReport = new FileWriter(Paths.get(folderReport, RunTestTaskConstants.NAME_FILE).toString())
        testResultReport = new TestResultReport(folderReport)
        apexTestResultArrayList = new ArrayList<ApexRunTestResult>()

        htmlManager = new HtmlManager(fileWriterReport)
        htmlManager.pathReport = folderReport
        htmlManager.sourceCode = projectPath
        htmlManager.folderPages = folderPages

        toolingAPI = new ToolingAPI(credential)
        apexAPI = new ApexAPI(credential)

        jsonByClasses = toolingAPI.httpAPIClient.executeQuery(RunTestTaskConstants.QUERY_CLASSES)
        jsonByTriggers = toolingAPI.httpAPIClient.executeQuery(RunTestTaskConstants.QUERY_TRIGGERS)
    }

    /**
     * Loads async parameter and test classes that will be executed
     */
    @Override
    void loadParameters() {
        if (!ApexClasses.checkForRecords(jsonByClasses)) {
            throw new Exception(RunTestTaskConstants.NOT_FOUND_ANY_CLASS)
        }
        if (Util.isValidProperty(project, RunTestTaskConstants.PARAMETER_ASYNC) &&
                project.properties[RunTestTaskConstants.PARAMETER_ASYNC].toString().equals(RunTestTaskConstants.TRUE_VALUE)) {
            async = true
        }
        runTestSelector()
    }

    /**
     * Initializes the TestSelector process for test purposes
     */
    protected void runTestSelector() {
        if (!pathClasses) { //TODO: remove or improve just for test purposes
            pathClasses = Paths.get((project.enforce.srcPath as String), "test").toString()
        }
        TestSelectorModerator testModerator = new TestSelectorModerator(project, ((toolingAPI) ? toolingAPI.httpAPIClient : null), pathClasses, async)
        testModerator.setLogger(logger)
        classesToExecute = testModerator.getTestClassNames()
    }

    /**
     * Generates the unit test and coverage files
     */
    @Override
    void runTask() {
        if (async) {
            if (classesToExecute.empty) {
                logger.error(RunTestTaskConstants.NOT_HAVE_UNIT_TEST_MESSAGE)
                return
            }
            runTestAsynchronous()
        } else {
            runTestsSynchronous()
        }
        writeJenkinsPluginJson()
        generateUnitTestReportXml()
        deleteTemporaryFiles()
    }

    /**
     * Runs test synchronously
     * @param classes is type array of classes name
     */
    def runTestsSynchronous() {
        RunTestsRequest request = new RunTestsRequest()
        if (classesToExecute && !classesToExecute.isEmpty()) {
            logger.quiet("${classesToExecute.size()} ${RunTestTaskConstants.TEST_CLASSES_WILL_BE_EXECUTED}")
            request.classes = classesToExecute
        } else {
            logger.quiet(RunTestTaskConstants.ALL_UNIT_TEST_WILL_BE_EXECUTED)
            request.allTests = true
        }
        if (!request.allTests) {
            request.namespace = toolingAPI.getPrefixName()
        }
        logger.log(LogLevel.INFO, String.format(RunTestTaskConstants.START_TIME, new Date().format(RunTestTaskConstants.HOUR_FORMAT)))
        RunTestsResult runTestResult = apexAPI.runTests(request)

        logger.log(LogLevel.INFO, String.format(RunTestTaskConstants.FINISH_TIME, new Date().format(RunTestTaskConstants.HOUR_FORMAT)))
        String timeResult = Util.formatDurationHMS(runTestResult.totalTime as long)

        logger.log(LogLevel.INFO, "${RunTestTaskConstants.TOTAL_TIME}  $timeResult")

        if (runTestResult && runTestResult.numFailures && runTestResult.numFailures > RunTestTaskConstants.ZERO_NUMBER) {
            logger.log(LogLevel.LIFECYCLE, "${TEST_MESSAGE}:")
        }

        runTestResult.failures.each { testFailures ->
            ApexRunTestResult apexRunTestResult = new ApexRunTestResult()
            apexRunTestResult.outcome = RunTestTaskConstants.UNIT_TEST_FAIL
            apexRunTestResult.stackTrace = testFailures.stackTrace
            apexRunTestResult.TestTimestamp = testFailures.time
            apexRunTestResult.methodName = testFailures.methodName
            apexRunTestResult.apexClassId = testFailures.id
            apexRunTestResult.message = testFailures.message
            apexRunTestResult.className = testFailures.name
            printTestFailure(testFailures)
            apexTestResultArrayList.push(apexRunTestResult)
        }

        if (runTestResult.numFailures && (runTestResult.numFailures > 0)) {
            logger.quiet(Constants.SEPARATOR)
        }

        runTestResult.successes.each { testSuccesses ->
            ApexRunTestResult apexRunTestResult = new ApexRunTestResult()
            apexRunTestResult.outcome = RunTestTaskConstants.UNIT_TEST_SUCCESS
            apexRunTestResult.stackTrace = ""
            apexRunTestResult.TestTimestamp = testSuccesses.time
            apexRunTestResult.methodName = testSuccesses.methodName
            apexRunTestResult.apexClassId = testSuccesses.id
            apexRunTestResult.className = testSuccesses.name
            apexTestResultArrayList.push(apexRunTestResult)
        }

        logger.log(LogLevel.INFO, RunTestTaskConstants.GENERATE_XML_REPORT)
        htmlManager.generateReport(verifyExistFileCoverage(runTestResult.codeCoverage),
                apexTestResultArrayList, runTestResult.failures.size(), runTestResult.successes.size())
        fileWriterReport.close()
    }

    /**
     * Prints one test failure result from RunTestResult object
     * @param runTestFailure the test failure object to print its properties
     */
    void printTestFailure(RunTestFailure runTestFailure) {
        if (runTestFailure) {
            String errorMessage = runTestFailure.message?"\n-------- Message --------\n${runTestFailure.message}":""
            errorMessage = runTestFailure.stackTrace?"${errorMessage}\n-------- Stacktrace --------\n${runTestFailure.stackTrace}":errorMessage
            if(!errorMessage.empty) {
                String message = "${Constants.LINE_SEPARATOR}${runTestFailure.name}.${runTestFailure.methodName}${errorMessage}"
                logger.quiet(message)
            }
        }
    }

    /**
     * Writes a new json file with coverage data
     */
    private void writeJenkinsPluginJson() {
        if (htmlManager && htmlManager.coverageLoader) {
            logger.log(LogLevel.INFO,RunTestTaskConstants.STARTING_WRITE_JSON_FOR_JENKINS)
            JsonBuilder jsonBuilder = new JsonBuilder()
            String chartName = Constants.JENKINS_CHART_NAME
            jsonBuilder.call(title: chartName, data: htmlManager.coverageLoader.loadArrayChartPie(),
                    coverageData: htmlManager.coverageLoader.loadArrayChartCoverage())
            File jsonFile = new File(Paths.get(folderReport, Constants.JENKINS_JSON_FILE_NAME).toString())
            jsonFile.write(jsonBuilder.toPrettyString().replace("\'", ""))
            logger.log(LogLevel.INFO, "${RunTestTaskConstants.JSON_CREATED_AT} ${jsonFile.path}")
        } else {
            logger.log(LogLevel.INFO, RunTestTaskConstants.NO_DATA_TO_WRITE_JSON_FOR_JENKINS)
        }
    }

    CodeCoverageResult[] verifyExistFileCoverage(CodeCoverageResult[] coverageResult) {
        ArrayList<CodeCoverageResult> codeCoverageResults = []
        coverageResult.each { coverageToValidate ->

            String path
            String extension

            if (coverageToValidate.type.equals(Constants.TYPE_CLASS)) {
                path = Paths.get(projectPath, MetadataComponents.CLASSES.getDirectory()).toString()
                extension = MetadataComponents.CLASSES.getExtension()
            } else {
                path = Paths.get(projectPath, MetadataComponents.TRIGGERS.getDirectory()).toString()
                extension = MetadataComponents.TRIGGERS.getExtension()
            }

            path = Paths.get(path, "${coverageToValidate.getName()}.$extension").toString()

            if (new File(path).exists()) {
                codeCoverageResults.push(coverageToValidate)
            }
        }
        return codeCoverageResults.toArray()
    }

    CodeCoverageResult[] getCodeCoverageResult() {
        ArrayList<CodeCoverageResult> coverageResultArrayList = []
        JsonSlurper jsonSlurper = new JsonSlurper()
        jsonSlurper.parseText(jsonCoverageLines).records.each { coverageResult ->
            if (coverageResult.NumLinesUncovered || coverageResult.NumLinesCovered) {
                CodeCoverageResult codeCoverageResult = new CodeCoverageResult()
                codeCoverageResult.numLocations = coverageResult.NumLinesCovered + coverageResult.NumLinesUncovered
                codeCoverageResult.numLocationsNotCovered = coverageResult.NumLinesUncovered
                String nameApex = Util.getApexNameByJson(coverageResult.ApexClassOrTriggerId as String, jsonByClasses)

                if (!nameApex.isEmpty()) {
                    codeCoverageResult.name = nameApex
                    codeCoverageResult.type = Constants.TYPE_CLASS
                } else {
                    nameApex = Util.getApexNameByJson(coverageResult.ApexClassOrTriggerId as String, jsonByTriggers)
                    codeCoverageResult.name = nameApex
                }
                ArrayList<CodeLocation> codeLocationArrayList = []
                coverageResult.Coverage.uncoveredLines.each { line ->
                    CodeLocation codeLocation = new CodeLocation()
                    codeLocation.line = line
                    codeLocationArrayList.push(codeLocation)
                }

                codeCoverageResult.locationsNotCovered = codeLocationArrayList.toArray()
                coverageResultArrayList.push(codeCoverageResult)
            }
        }

        return coverageResultArrayList.toArray()
    }

    /**
     * Runs test asynchronously
     * @param classes is type array of classes name
     */
    def runTestAsynchronous() {
        if (classesToExecute && !classesToExecute.size()) {
            throw new Exception(RunTestTaskConstants.NOT_FOUND_CLASS_TO_EXECUTE_UNIT_TEST)
        }
        org.fundacionjala.gradle.plugins.enforce.wsc.soap.ToolingAPI toolingAPISoap
        toolingAPISoap = new org.fundacionjala.gradle.plugins.enforce.wsc.soap.ToolingAPI(credential)
        ApexClasses apexClasses = new ApexClasses()
        apexClasses.load(jsonByClasses, classesToExecute)

        runTestListener = new RunTestListener(toolingAPISoap, System.out, apexClasses)
        runTestListener.startUnitTestExecution()

        progressLogger.description = RunTestTaskConstants.SLEEPING
        progressLogger.started()

        while (!runTestListener.done) {
            sleep(RunTestTaskConstants.TIME_RUN_TEST_ASYNC)
            progressLogger.progress("$toolingAPISoap.numberUnitTest/$toolingAPISoap.currentUnitTestCompleted")
        }

        progressLogger.completed()
        jsonCoverageLines = toolingAPI.httpAPIClient.executeQuery(RunTestTaskConstants.QUERY_COVERAGE)
        apexTestResultArrayList = runTestListener.apexTestItem.apexTestResults
        generateHtmlReportCoverageAsync(apexClasses)
        generateCoverageReportXml()
    }

    /**
     * Generates report coverage in html format using json objects
     */
    void generateHtmlReportCoverageAsync(ApexClasses apexClasses) {
        int unitTestFail = Constants.ZERO
        int unitTestSuccess = Constants.ZERO

        apexTestResultArrayList.each { apexTestResult ->
            if (apexTestResult.outcome == RunTestTaskConstants.UNIT_TEST_FAIL) {
                unitTestFail++
            } else {
                unitTestSuccess++
            }
        }
        apexTestResultArrayList.each {apexTestResult->
            ApexClass apexClass = apexClasses.getClass(apexTestResult.apexClassId)
            apexTestResult.className = apexClass?apexClass.name:apexTestResult.className
        }
        htmlManager.generateReport(verifyExistFileCoverage(getCodeCoverageResult()),
                apexTestResultArrayList, unitTestFail, unitTestSuccess)
        fileWriterReport.close()
    }

    /**
     * Generates report unit test in xml format using json objects
     */
    void generateUnitTestReportXml() {
        testResultReport.loadInformationUnitTest(apexTestResultArrayList, jsonByClasses)
        FileWriter unitTestXML = new FileWriter(Paths.get(folderReport, RunTestTaskConstants.NAME_FILE_UNIT_TEXT_XML).toString())
        testResultReport.generateUnitTestXML(unitTestXML)
        unitTestXML.close()
    }

    /**
     * Generates report coverage in xml format using json objects
     */
    void generateCoverageReportXml() {
        testResultReport.loadInformationCoverage(jsonCoverageLines, jsonByClasses, jsonByTriggers, projectPath)
        FileWriter coverageReportXML = new FileWriter(Paths.get(folderReport,
                RunTestTaskConstants.NAME_FILE_COVERAGE_REPORT_XML).toString())
        testResultReport.generateCoverageReportXML(coverageReportXML)
        coverageReportXML.close()
    }

    /**
     * Gets report path to save unit test reports by default return build/report directory
     * @return report path
     */
    private String getFolderReportPath() {
        String folderReportPath = Paths.get(buildFolderPath, RunTestTaskConstants.NAME_FOLDER_REPORT).toString()
        if (Util.isValidProperty(project, RunTestTaskConstants.DESTINATION_PARAMETER)) {
            String destinationValue = project.properties[RunTestTaskConstants.DESTINATION_PARAMETER]
            folderReportPath = Paths.get(destinationValue).isAbsolute()? destinationValue :
                    Paths.get(project.projectDir.absolutePath, destinationValue).toString()
        }
        return folderReportPath
    }

    /**
     * Gets all test class names to be executed
     */
    public ArrayList<String> getClassNames() {
        return classesToExecute
    }
}
