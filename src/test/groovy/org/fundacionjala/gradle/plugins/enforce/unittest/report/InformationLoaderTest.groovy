/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest.report

import com.sforce.soap.apex.CodeCoverageResult
import groovy.json.JsonBuilder
import spock.lang.Shared
import spock.lang.Specification

class InformationLoaderTest extends Specification {

    @Shared
    InformationLoader coverageLoader

    def setup() {
        coverageLoader = new InformationLoader()
    }

    def "Test convert a map to an array of json"() {

        given:
        Map mapClass = ["class1.cls": 74, "class2.cls": 90, "class3.cls": 100, "class4.cls": 78]
        def json = new JsonBuilder()
        def mapJson = json(["{name: 'class1.cls', percentage: [74, 'danger']}",
                            "{name: 'class2.cls', percentage: [90, 'info']}",
                            "{name: 'class3.cls', percentage: [100, 'success']}",
                            "{name: 'class4.cls', percentage: [78, 'warning']}"])
        when:
        def resultJson = coverageLoader.convertToMapJson(mapClass)
        then:
        resultJson == mapJson
    }

    def "Test convert a map to an array of json if the map is empty"() {

        given:
        Map mapClass = [:]
        def json = new JsonBuilder()
        def mapJson = json([])
        when:
        def resultJson = coverageLoader.convertToMapJson(mapClass)
        then:
        resultJson == mapJson
    }

    def "Test get coverage informer" () {
        given:

        CodeCoverageResult codeCoverageResultClass = Mock(CodeCoverageResult)
        CodeCoverageResult codeCoverageResultTrigger = Mock(CodeCoverageResult)
        CodeCoverageResult[] codeCoverageResult = [codeCoverageResultClass, codeCoverageResultTrigger]

        when:

        def coverageInformer = coverageLoader.getCoverageInformer(codeCoverageResult, [])

        then:
        codeCoverageResultClass.numLocations >> 99
        codeCoverageResultClass.numLocationsNotCovered >> 1
        codeCoverageResultClass.getName() >> "TwilioAPI"
        codeCoverageResultClass.type >> "Class"

        codeCoverageResultTrigger.numLocations >> 100
        codeCoverageResultTrigger.numLocationsNotCovered >> 30
        codeCoverageResultTrigger.getName() >> "TriggerAPI"
        codeCoverageResultTrigger.type >> "Triggers"

        coverageInformer.arrayDataCharCoverage == "[['Lines', 'Number'], ['Covered', 168], ['Not Covered', 31]]"
        coverageInformer.arrayDataCharPie == "[['Lines', 'Number'], ['Danger (0% - 74%)', 1], ['Risk (75% - 79%)', 0], ['Acceptable (80% - 94%)', 0], ['Safe (95% - 100%)', 1]]"
        coverageInformer.arrayJsonClass == "[{name: 'TwilioAPI', percentage: [99, 'success']}]"
        coverageInformer.arrayJsonTriggers == "[{name: 'TriggerAPI', percentage: [70, 'danger']}]"
    }


    def "Test get coverage informer empty records" () {
        given:

        CodeCoverageResult[] codeCoverageResult = []

        when:

        def coverageInformer = coverageLoader.getCoverageInformer(codeCoverageResult, [])

        then:

        coverageInformer.arrayDataCharCoverage == "[['Lines', 'Number'], ['Covered', 0], ['Not Covered', 0]]"
        coverageInformer.arrayDataCharPie == "[['Lines', 'Number'], ['Danger (0% - 74%)', 0], ['Risk (75% - 79%)', 0], ['Acceptable (80% - 94%)', 0], ['Safe (95% - 100%)', 0]]"
        coverageInformer.arrayJsonClass == "[]"
        coverageInformer.arrayJsonTriggers == "[]"
    }
}
