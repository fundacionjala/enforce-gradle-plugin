/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.fundacionjala.gradle.plugins.enforce.unittest.Apex.ApexRunTestResult
import org.fundacionjala.gradle.plugins.enforce.unittest.coverage.Component
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class TestResultReportTest extends Specification {

    @Shared
    TestResultReport testResultReport
    @Shared
    ApexRunTestResult apexResult1
    @Shared
    ApexRunTestResult apexResult2
    @Shared
    ApexRunTestResult apexResult3
    @Shared
    ArrayList<ApexRunTestResult> arrayApexResult
    @Shared
    String jsonClassName
    @Shared
    String pathProject = Paths.get("src", "test", "groovy", "org", "fundacionjala", "gradle", "plugins","enforce",
                                  "unittest", "resources").toString()
    @Shared
    def path = Paths.get(System.getProperty("user.dir"), pathProject).toString()

    def setup() {
        testResultReport = new TestResultReport(path)

        apexResult1 = new ApexRunTestResult()
        apexResult1.outcome = 'Fail'
        apexResult1.ApexLogId = '1'
        apexResult1.methodName = 'one'
        apexResult1.className = 'classOne'
        apexResult1.testTimestamp = '0'
        apexResult1.message = 'error one'
        apexResult1.stackTrace = 'problem in line 1'

        apexResult2 = new ApexRunTestResult()
        apexResult2.outcome = 'Fail'
        apexResult2.apexClassId = '2'
        apexResult2.methodName = 'two'
        apexResult2.className = 'ClassTwo'
        apexResult2.testTimestamp = '0'
        apexResult2.message = 'error Two'
        apexResult2.stackTrace = 'problem in line 1'

        apexResult3 = new ApexRunTestResult()
        apexResult3.outcome = 'Pass'
        apexResult3.apexClassId = '3'
        apexResult3.methodName = 'tree'
        apexResult3.className = 'ClassTree'
        apexResult3.testTimestamp = '0'

        arrayApexResult = [apexResult1, apexResult2, apexResult3]
        jsonClassName = """{"records":[{"Id" : "1", "Name": "1" }, {"Id": "2", "Name": "2" }, {"Id": "3", "Name": "3" }]}"""
    }

    def "Test should instance of TestResultReport class" () {
        expect:
            testResultReport instanceof TestResultReport
    }

    def 'Test should load information about run test'() {
        when:
            testResultReport.loadInformationUnitTest(arrayApexResult, jsonClassName)
        then:
            testResultReport.unitTestInformerFails.size() == 2
            testResultReport.unitTestInformerPass.size() == 1
    }

    def "Test should generate a unit test xml file" () {
        given:
        def writerXml = new StringWriter()
        testResultReport.unitTestInformerPass = [apexResult3]
        testResultReport.unitTestInformerFails = [apexResult1, apexResult2]
        when:
        testResultReport.generateUnitTestXML(writerXml)
        def xmlExpected =  new File(Paths.get(path, "unitTestResult.xml").toString()).text
        XMLUnit.ignoreWhitespace = true
        def xmlDiff = new Diff(xmlExpected, writerXml.toString())
        then:
        xmlDiff.similar()
    }


    def 'Test should load information about coverage'() {
        given:

        String coverageResult = """{ "records":[
                                    {"ApexClassOrTriggerId":"1",
                                     "Coverage" : { "coveredLines" : [1,2],
                                                    "uncoveredLines" : [3,4]
                                                  }
                                     }
                                     ] }"""
        String jsonByTriggers = """"""

        when:
        testResultReport.loadInformationCoverage(coverageResult, jsonClassName, jsonByTriggers, path)
        then:
        testResultReport.componentArrayList.size() == 2
        testResultReport.componentArrayList.get(0).type == 'Class'
        testResultReport.componentArrayList.get(0).extension == 'cls'
        testResultReport.componentArrayList.get(0).path == Paths.get(path, "classes").toString()
        testResultReport.componentArrayList.get(0).elements.size() ==  1
        testResultReport.componentArrayList.get(0).elements.get(0).coveredLines ==  [1,2]
        testResultReport.componentArrayList.get(0).elements.get(0).uncoveredLines ==  [3,4]

        testResultReport.componentArrayList.get(1).type == 'Trigger'
        testResultReport.componentArrayList.get(1).extension == 'trigger'
        testResultReport.componentArrayList.get(1).path == Paths.get(path, "triggers").toString()
        testResultReport.componentArrayList.get(1).elements.size() ==  0
    }

    def "Test should generate a coverage xml file" () {
        given:
        def writerXml = new StringWriter()
        Component component1 = new Component("Class", "classes", "cls", [])
        Component component2 = new Component("Trigger", "triggers", "trigger", [])
        testResultReport.componentArrayList = [component1, component2]
        when:
        testResultReport.generateCoverageReportXML(writerXml)
        def xmlExpected =  new File(Paths.get(path, "coverageTest.xml").toString()).text
        XMLUnit.ignoreWhitespace = true
        def xmlDiff = new Diff(xmlExpected, writerXml.toString())
        then:
        xmlDiff.similar()
    }
}
