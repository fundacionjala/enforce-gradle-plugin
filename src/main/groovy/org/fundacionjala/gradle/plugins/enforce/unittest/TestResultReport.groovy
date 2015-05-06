/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest

import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
import org.fundacionjala.gradle.plugins.enforce.unittest.Apex.ApexRunTestResult
import org.fundacionjala.gradle.plugins.enforce.unittest.coverage.Component
import org.fundacionjala.gradle.plugins.enforce.unittest.coverage.CoverageElement
import org.fundacionjala.gradle.plugins.enforce.unittest.report.InformationLoader
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

import java.nio.file.Paths

public class TestResultReport {

    private static final String SUITE_NAME = "Apex"
    private static final String VERSION = '1.0'
    private static final String ENCODING = 'UTF-8'
    private static final String TYPE_CLASS = 'Class'
    private static final String TYPE_TRIGGER = 'Trigger'

    private String path
    private MarkupBuilder xml
    private final String NUMBER_CONSTANT = "1.0"
    private final String COMPLEXITY_CONSTANT = "0.0"
    private final String VALUE_BRANCH = "false"
    private final String NAME_PACKAGE = "Coverage"
    private final String HITS_COVERED = "4"
    private final String HITS_NOT_COVERED = "0"
    private final String UNIT_TEST_FAIL = "Fail"


    ArrayList<ApexRunTestResult> unitTestInformerFails
    ArrayList<ApexRunTestResult> unitTestInformerPass

    ArrayList<Component> componentArrayList

    /**
     * Sets path and initialize some variables
     * @param pathResultReport is folder path report
     */
    public TestResultReport(String pathResultReport) {
        path = pathResultReport
        unitTestInformerFails = new ArrayList<ApexRunTestResult>()
        unitTestInformerPass = new ArrayList<ApexRunTestResult>()

        componentArrayList = new ArrayList<Component>()
    }

    /**
     * Iterates apex test result then split fails unit test and  unit test pass
     * @param apexTestResults is a array contain information about run unit test
     * @param jsonClasses is a json object contain information about id and name to class
     */
    public void loadInformationUnitTest(ArrayList<ApexRunTestResult> apexTestResults, String jsonClasses) {

        apexTestResults.each { apexTestResult ->
            apexTestResult.className = InformationLoader.getApexNameByJson(apexTestResult.apexClassId as String, jsonClasses)
            if (apexTestResult.outcome == UNIT_TEST_FAIL) {
                unitTestInformerFails.push(apexTestResult)
            } else {
                unitTestInformerPass.push(apexTestResult)
            }
        }
    }

    /**
     * Generates a unit test xml file
     * @param is the object of object File
     */
    public void generateUnitTestXML(Writer writer) {

        xml = new MarkupBuilder(writer)
        xml.mkp.xmlDeclaration(version: VERSION, encoding: ENCODING)
        xml.testsuites() {
            testsuite(name: SUITE_NAME, timestamp: '') {
                properties() {
                }
            }

            unitTestInformerFails.each { unitTest ->

                testcase(name: unitTest.methodName,
                        classname: unitTest.className,
                        time: String.valueOf(unitTest.testTimestamp)) {
                    failure(type: TYPE_CLASS, message: XmlUtil.escapeXml(unitTest.message)) {
                    }

                    mkp.yieldUnescaped "\n\t<system-err> ${XmlUtil.escapeXml(unitTest.stackTrace)}</system-err>"
                }
            }

            unitTestInformerPass.each { unitTest ->

                testcase(name: unitTest.methodName,
                        classname: unitTest.className,
                        time: String.valueOf(unitTest.testTimestamp)) {

                }
            }

        }
    }

    /**
     * Iterates coverage result then split information in  class and triggers
     * @param coverageResult is a json object contain information about coverage
     * @param jsonByClasses is a json object contain information about id and name to classes
     * @param jsonByTriggers is a json object contain information about id and name to triggers
     * @param pathProject is path of project
     */
    public void loadInformationCoverage(String coverageResult, String jsonByClasses, String jsonByTriggers, String pathProject) {

        ArrayList<CoverageElement> classComponents = new ArrayList<CoverageElement>()
        ArrayList<CoverageElement> triggerComponents = new ArrayList<CoverageElement>()

        JsonSlurper jsonSlurper = new JsonSlurper()
        jsonSlurper.parseText(coverageResult).records.each { coverage ->

            if (coverage.Coverage && coverage.Coverage.coveredLines && coverage.Coverage.uncoveredLines &&
                    coverage.ApexClassOrTriggerId) {
                String nameApex = InformationLoader.getApexNameByJson(coverage.ApexClassOrTriggerId as String, jsonByClasses)
                if (!nameApex.isEmpty()) {
                    classComponents.push(new CoverageElement(nameApex,
                            coverage.Coverage.coveredLines as ArrayList<Integer>,
                            coverage.Coverage.uncoveredLines as ArrayList<Integer>))
                } else {
                    nameApex = InformationLoader.getApexNameByJson(coverage.ApexClassOrTriggerId as String, jsonByTriggers)
                    triggerComponents.push(new CoverageElement(nameApex,
                            coverage.Coverage.coveredLines as ArrayList<Integer>,
                            coverage.Coverage.uncoveredLines as ArrayList<Integer>))
                }
            }
        }

        String pathClass = Paths.get(pathProject, MetadataComponents.CLASSES.directory).toString()
        String pathTrigger = Paths.get(pathProject, MetadataComponents.TRIGGERS.directory).toString()

        componentArrayList.push(new Component(TYPE_CLASS, pathClass, MetadataComponents.CLASSES.extension,
                classComponents))
        componentArrayList.push(new Component(TYPE_TRIGGER, pathTrigger, MetadataComponents.TRIGGERS.extension,
                triggerComponents))
    }

    /**
     * Generates a coverage xml file
     * @param is the object of object File
     */
    public void generateCoverageReportXML(Writer writer) {

        def xml = new MarkupBuilder(writer)
        xml.mkp.xmlDeclaration(version: VERSION, encoding: ENCODING)
        xml.coverage() {
            componentArrayList.each { component ->
                sources {
                    source(component.path)
                }

                packages() {
                    Package(name: NAME_PACKAGE, "line-rate": NUMBER_CONSTANT, "branch-rate": NUMBER_CONSTANT,
                            complexity: COMPLEXITY_CONSTANT) {
                        xml.classes() {
                            component.elements.each { coverageElement ->
                                coverageElement.setPath(Paths.get(component.path, coverageElement.getFileName(component.extension)).toString())
                                coverageElement.loadDataCoverage(component.type)
                                xml.class(name: coverageElement.getName(), filename: coverageElement.getFileName(component.extension),
                                        "line-rate": coverageElement.getLineRate(), "branch-rate": NUMBER_CONSTANT,
                                        complexity: NUMBER_CONSTANT) {
                                    xml.methods() {

                                        coverageElement.coverageAnalyzer.linesCoveredMethods.each { nameMethod, arrayLines ->

                                            xml.method(name: nameMethod,
                                                    signature: "()V", "line-rate": coverageElement.getLineRate(),
                                                    "branch-rate": NUMBER_CONSTANT) {
                                                xml.lines() {
                                                    arrayLines.each { lineMethod ->
                                                        xml.line(number: lineMethod, hits: HITS_COVERED,
                                                                branch: VALUE_BRANCH)
                                                    }

                                                    coverageElement.coverageAnalyzer.linesNotCoveredMethods.get(nameMethod).each { lineMethod ->
                                                        xml.line(number: lineMethod, hits: HITS_NOT_COVERED,
                                                                branch: VALUE_BRANCH)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    xml.lines() {
                                        coverageElement.coverageAnalyzer.linesNotCovered.each { line ->
                                            xml.line(number: line, hits: HITS_NOT_COVERED, branch: VALUE_BRANCH)
                                        }

                                        coverageElement.coverageAnalyzer.linesCovered.each { line ->
                                            xml.line(number: line, hits: HITS_COVERED, branch: VALUE_BRANCH)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}