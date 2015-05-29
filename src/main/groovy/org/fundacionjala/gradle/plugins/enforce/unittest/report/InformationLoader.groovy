/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest.report

import com.sforce.soap.apex.CodeCoverageResult
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.json.StringEscapeUtils
import org.fundacionjala.gradle.plugins.enforce.unittest.Apex.ApexRunTestResult
import org.fundacionjala.gradle.plugins.enforce.utils.Constants

/**
 * Process information to coverage for the report
 */
class InformationLoader {
    private final String STATE_PROGRESS_CHART_PIE_DANGER = 'Danger'
    private final String STATE_PROGRESS_CHART_PIE_RISK = 'Risk'
    private final String STATE_PROGRESS_CHART_PIE_ACCPETABLE = 'Acceptable'
    private final String STATE_PROGRESS_CHART_PIE_SAFE = 'Safe'


    private final String STATE_PROGRESS_BAR_DANGER = 'danger'
    private final String STATE_PROGRESS_BAR_WARNING = 'warning'
    private final String STATE_PROGRESS_BAR_INFO = 'info'
    private final String STATE_PROGRESS_BAR_SUCCESS = 'success'


    private final String COMPONENT_LINE = "'Lines'"
    private final String COMPONENT_NUMBER = "'Number'"

    private final String COMPONENT_COVERED = "Covered"
    private final String COMPONENT_NOT_COVERED = "Not Covered"

    private final String STATE_FAIL = "Fail"

    private final String RANGER_DANGER = "(0% - 74%)"
    private final String RANGER_WARNING = "(75% - 79%)"
    private final String RANGER_INFO = "(80% - 94%)"
    private final String RANGER_SUCCESS = "(95% - 100%)"

    private final Integer NUMBER_HUNDRED = 100
    private final Integer NUMBER_NINETY_FIVE = 95
    private final Integer NUMBER_EIGHTY = 80
    private final Integer NUMBER_SEVENTY_FIVE = 75
    private final Integer NUMBER_ZERO = 0

    private Integer numberLinesCovered
    private Integer numberLinesNotCovered
    private Integer allLines

    private Map<String, Long> mapCoverageForClass
    private Map<String, Long> mapCoverageForTriggers

    private Integer elementsDanger
    private Integer elementsWarning
    private Integer elementsInfo
    private Integer elementsSuccess

    public Informer coverageInformer

    /**
     * Initializes values of the class
     */
    public InformationLoader() {
        numberLinesCovered = NUMBER_ZERO
        numberLinesNotCovered = NUMBER_ZERO

        elementsDanger = NUMBER_ZERO
        elementsWarning = NUMBER_ZERO
        elementsInfo = NUMBER_ZERO
        elementsSuccess = NUMBER_ZERO

        allLines = NUMBER_ZERO

        mapCoverageForClass = new HashMap<String, Long>()
        mapCoverageForTriggers = new HashMap<String, Long>()
    }

    /**
     * Process information about coverage that will use for generate coverage report
     * @param codeCoverageResult contains the information about code coverage from organization
     * @param apexTestResultArrayList contains the information about unit test from organization
     * @return an object CoverageInformer
     */
    public Informer getCoverageInformer(CodeCoverageResult[] codeCoverageResult, ArrayList<ApexRunTestResult> apexTestResultArrayList) {
        coverageInformer = new Informer()

        double numLinesCovered = NUMBER_ZERO
        double numLocation = NUMBER_ZERO
        double numLocationNotCovered = NUMBER_ZERO
        codeCoverageResult.each { coverageResult ->

            numLocation = coverageResult.numLocations
            numLocationNotCovered = coverageResult.numLocationsNotCovered
            Double coverageForApex = NUMBER_ZERO
            if (numLocation != NUMBER_ZERO) {
                numLinesCovered = numLocation - numLocationNotCovered
                coverageForApex = numLinesCovered / numLocation
                coverageForApex = coverageForApex * NUMBER_HUNDRED
            } else {
                numLinesCovered = NUMBER_ZERO
            }

            if (coverageResult.type.equals(Constants.TYPE_CLASS)) {
                mapCoverageForClass.put(coverageResult.getName(), Math.round(coverageForApex))
            } else {
                mapCoverageForTriggers.put(coverageResult.getName(), Math.round(coverageForApex))
            }

            numberLinesCovered += numLinesCovered
            numberLinesNotCovered += numLocationNotCovered

        }

        loadArrayChartCoverage()
        loadControllerClass()
        loadControllerTriggers()
        coverageInformer.arrayJsonUnitTest = loadInformationCoverage(apexTestResultArrayList)
        loadArrayChartPie()
        loadArrayChartCoverage()
        loadResultCoverage()
        return coverageInformer
    }

    /**
     * Seeks an Id in the object Json
     * @param Id is a identifier of class or trigger
     * @param json is the result of a query to salesforce
     * @return a name class or trigger
     */
    public static String getApexNameByJson(String Id, String json) {
        String nameApex = ""
        JsonSlurper jsonSlurper = new JsonSlurper()
        for (elementSalesforce in jsonSlurper.parseText(json).records) {
            if (elementSalesforce.Id == Id) {
                nameApex = elementSalesforce.Name
                break
            }
        }

        return nameApex
    }

    /**
     * Calculates the coverage result
     */
    private void loadResultCoverage() {
        if (allLines) {
            coverageInformer.resultCoverage = Math.round((numberLinesCovered / allLines) * NUMBER_HUNDRED)
        }
    }

    /**
     * Builds an array for pie chart
     * @return An array with pie chart data
     */
    public ArrayList<Collection> loadArrayChartPie() {
        ArrayList<Collection> arrayDataCharPie = [[COMPONENT_LINE, COMPONENT_NUMBER]]
        arrayDataCharPie.push(["'${STATE_PROGRESS_CHART_PIE_DANGER} ${RANGER_DANGER}'", elementsDanger])
        arrayDataCharPie.push(["'${STATE_PROGRESS_CHART_PIE_RISK} ${RANGER_WARNING}'", elementsWarning])
        arrayDataCharPie.push(["'${STATE_PROGRESS_CHART_PIE_ACCPETABLE} ${RANGER_INFO}'", elementsInfo])
        arrayDataCharPie.push(["'${STATE_PROGRESS_CHART_PIE_SAFE} ${RANGER_SUCCESS}'", elementsSuccess])
        coverageInformer.arrayDataCharPie = arrayDataCharPie.toString()
        return arrayDataCharPie
    }

    /**
     * Builds an array for coverage chart
     * @return An array with coverage chart data
     */
    public ArrayList<Collection> loadArrayChartCoverage() {
        ArrayList<Collection> arrayDataCharCoverage = [[COMPONENT_LINE, COMPONENT_NUMBER]]
        arrayDataCharCoverage.push(["'${COMPONENT_COVERED}'", numberLinesCovered])
        arrayDataCharCoverage.push(["'${COMPONENT_NOT_COVERED}'", numberLinesNotCovered])

        coverageInformer.arrayDataCharCoverage = arrayDataCharCoverage.toString()
        return arrayDataCharCoverage
    }

    /**
     * Builds an array for class controller
     */
    private void loadControllerClass() {

        coverageInformer.arrayJsonClass = convertToMapJson(mapCoverageForClass)
    }

    /**
     * Builds an array for trigger controller
     */
    private void loadControllerTriggers() {

        coverageInformer.arrayJsonTriggers = convertToMapJson(mapCoverageForTriggers)
    }

    /**
     * Split the class and triggers in ranges success, info, warning and danger
     * @param mapCoverageElements contains element name and percentage coverage
     * @return an array json formatter
     */
    public ArrayList convertToMapJson(Map mapCoverageElements) {

        ArrayList<String> arrayJson = new ArrayList<String>()

        mapCoverageElements.each { name, percentage ->
            String stateProgressBar = STATE_PROGRESS_BAR_SUCCESS
            if (percentage < NUMBER_SEVENTY_FIVE) {
                stateProgressBar = STATE_PROGRESS_BAR_DANGER
                elementsDanger++
            } else {
                if (percentage >= NUMBER_SEVENTY_FIVE && percentage < NUMBER_EIGHTY) {
                    stateProgressBar = STATE_PROGRESS_BAR_WARNING
                    elementsWarning++
                } else {
                    if (percentage >= NUMBER_EIGHTY && percentage < NUMBER_NINETY_FIVE) {
                        stateProgressBar = STATE_PROGRESS_BAR_INFO
                        elementsInfo++
                    } else {
                        elementsSuccess++
                    }
                }
            }

            String objectJson = "{name: '${name}', percentage: [${percentage}, '${stateProgressBar}']}"
            arrayJson.push(objectJson)
        }

        JsonBuilder json = new JsonBuilder()
        return json(arrayJson)
    }

    /**
     * Builds a string in json format array for unit test
     * @param apexTestResultArrayList contains the information about unit test from organization
     * @return a string in json format array
     */
    private String loadInformationCoverage(ArrayList<ApexRunTestResult> apexTestResultArrayList) {
        ArrayList<String> arrayJson = new ArrayList<String>()
        int id = 0
        apexTestResultArrayList.each {apexTestResult ->
            String filter = STATE_PROGRESS_BAR_DANGER
            if(apexTestResult.outcome != STATE_FAIL) {
                filter = STATE_PROGRESS_BAR_SUCCESS
            }
            String message = StringEscapeUtils.escapeJavaScript(apexTestResult.message)
            String stackTrace = StringEscapeUtils.escapeJavaScript(apexTestResult.stackTrace)
            String unitTest = apexTestResult.className?"${apexTestResult.className}.${apexTestResult.methodName}":apexTestResult.methodName
            String objectJson = "{id: ${id} ,name: '${unitTest}', status: '${apexTestResult.outcome}', stackTrace: '${stackTrace}', message: '${message}', filter: '$filter', details:[{label: 'Stacktrace', content: '$stackTrace'}, {label: 'Message', content: '$message'}]}"
            id++
            arrayJson.push(objectJson)
        }
        JsonBuilder json = new JsonBuilder()
        return json(arrayJson).toString()
    }
}
