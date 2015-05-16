/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest.report

import com.sforce.soap.apex.CodeCoverageResult
import groovy.text.SimpleTemplateEngine
import org.fundacionjala.gradle.plugins.enforce.unittest.Apex.ApexRunTestResult
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

import java.nio.file.Paths

/**
 * writes a xml file based on a template
 */
class HtmlManager {
    private final String FOLDER_CLASSES = 'classes'
    private final String FOLDER_TRIGGERS = 'triggers'
    private final String FILE_HTML_EXTENSION = '.html'

    public String pathReport
    public String folderPages
    public String sourceCode
    public Writer fileWriter
    public String textTemplate

    public InformationLoader coverageLoader
    public Informer informer
    public ReportResourceManager reportResourceManager

    /**
     * Initializes values of the class
     * @param writer is object writing
     */
    public HtmlManager(Writer writer) {
        fileWriter = writer
        coverageLoader = new InformationLoader()
        reportResourceManager = new ReportResourceManager()
    }

    /**
     * Reads file template from the resources
     */
    private void loadTextTemplate() {
        textTemplate = reportResourceManager.getIndexTemplate()
    }

    private void copyResources() {
        reportResourceManager.pathReport = pathReport
        reportResourceManager.copyResourceScripts()
        reportResourceManager.copyResourceStyles()
        reportResourceManager.copyFonts()
    }

    /**
     * Processes the coverage  information to write in xml file
     * @param jsonCoverageLines is the result of a query to salesforce about coverage
     * @param jsonByClasses is the result of a query to salesforce about classes
     * @param jsonByTriggers is the result of a query to salesforce about triggers
     */
    public void generateReport(CodeCoverageResult[] codeCoverageResult, ArrayList<ApexRunTestResult> apexTestResultArrayList, int unitTestFail, int unitTestSuccess) {
        informer = coverageLoader.getCoverageInformer(codeCoverageResult, apexTestResultArrayList)
        writeHtmlFiles(codeCoverageResult)
        copyResources()
        loadTextTemplate()
        writeHtmlWithTemplate(unitTestFail, unitTestSuccess)
    }

    /**
     * Sets values in the text template
     * @param unitTestFail is the number of failed unit test
     * @param unitTestSuccess is the number of success unit test
     */
    public void writeHtmlWithTemplate(int unitTestFail, int unitTestSuccess) {
        Map binding = ["arrayChartPie"     : informer.arrayDataCharPie,
                       "arrayChartCoverage": informer.arrayDataCharCoverage,
                       "arrayClass"        : informer.arrayJsonClass,
                       "arrayTrigger"      : informer.arrayJsonTriggers,
                       "arrayUnitTest"    : informer.arrayJsonUnitTest.replaceAll("\n", ""),
                       "resultCoverage"    : informer.resultCoverage,
                       "unitTestFail": unitTestFail,
                       "unitTestPass": unitTestSuccess
        ]
        SimpleTemplateEngine engine = new SimpleTemplateEngine()
        Writable template = engine.createTemplate(textTemplate).make(binding)
        fileWriter.write(template.toString())
        fileWriter.close()
    }

    /**
     * Writes in file html the code and apply highlighter
     * @param jsonCoverageLines is the result of a query to salesforce about coverage
     * @param jsonByClasses is the result of a query to salesforce about classes
     * @param jsonByTriggers is the result of a query to salesforce about triggers
     */
    public void writeHtmlFiles(CodeCoverageResult[] codeCoverageResult) {

        String fileTemplate = reportResourceManager.getFileTemplate()
        String fileContent
        codeCoverageResult.each { coverageResult ->
            String fileName
            File fileSource
            String filePath

            if (coverageResult.type.equals(Constants.TYPE_CLASS)) {
                filePath = Paths.get(sourceCode, FOLDER_CLASSES).toString()
                fileName = "${coverageResult.getName()}.${MetadataComponents.getExtensionByFolder(FOLDER_CLASSES)}"
                fileSource = new File(Paths.get(filePath, fileName).toString())
            } else {
                filePath = Paths.get(sourceCode, FOLDER_TRIGGERS).toString()
                fileName = "${coverageResult.getName()}.${MetadataComponents.getExtensionByFolder(FOLDER_TRIGGERS)}"
                fileSource = new File(Paths.get(filePath, fileName).toString())
            }

            if (fileSource.exists()) {
                ArrayList<Integer> linesNoCovered = new ArrayList<Integer>()
                coverageResult.locationsNotCovered.each { codeLocation ->
                    linesNoCovered.push(codeLocation.line)
                }
                String arrayLines = linesNoCovered.toString()
                Map binding = ["nameFile": coverageResult.getName(), "fileCode": fileSource.text, "arrayLines": arrayLines]
                SimpleTemplateEngine engine = new SimpleTemplateEngine()
                Writable template = engine.createTemplate(fileTemplate).make(binding)
                fileContent = template.toString()
            } else {
                fileContent = reportResourceManager.getFileTemplateError()
            }

            FileWriter fileHtml = new FileWriter(Paths.get(folderPages, "${coverageResult.getName()}${FILE_HTML_EXTENSION}").toString())
            fileHtml.write(fileContent)
            fileHtml.close()
        }
    }
}
