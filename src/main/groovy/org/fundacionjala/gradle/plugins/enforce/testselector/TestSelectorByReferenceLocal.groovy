/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.testselector

import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.unittest.RunTestTaskConstants
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

import java.nio.file.Paths


class TestSelectorByReferenceLocal extends TestSelector {

    private final String TEST_CLASSES_SUMMARY_MSG = "\n --- Test Class to run summary ---\n"
    private final String APEX_CLASS_RELATED_TESTS_MSG = "Apex Class: %s \n Related Test Class(es): %s\n"
    private String pathClasses
    private String filesParameterValue
    private ArrayList<String> allApexClassNameList
    private Map classAndTestMap = [:]

    /**
     * TestSelector class constructor
     * @param testClassNameList list of all available test class names
     */
    protected TestSelectorByReferenceLocal(String pathClasses, ArrayList<String> allApexClassNameList, ArrayList<String> testClassNameList, String filesParameterValue) {
        super(testClassNameList)
        this.pathClasses = pathClasses
        this.allApexClassNameList = allApexClassNameList
        this.filesParameterValue = filesParameterValue
    }

    private void init() {
        this.filesParameterValue = this.filesParameterValue.replace(".${MetadataComponents.CLASSES.getExtension()}", "")
        testClassNameList.each { testClass ->
            File file = new File(Paths.get(pathClasses, testClass + ".${MetadataComponents.CLASSES.getExtension()}").toString())
            if (file.exists()) {
                allApexClassNameList.each { apexClass ->
                    if (file.text.contains(apexClass)) {
                        if (!classAndTestMap.containsKey(apexClass)) {
                            classAndTestMap.put(apexClass, new ArrayList<String>())
                        }
                        classAndTestMap.get(apexClass).add(testClass)
                    }
                }
            }
        }
    }

    @Override
    ArrayList<String> getTestClassNames() {
        init()
        displayMessage(TEST_CLASSES_SUMMARY_MSG)
        ArrayList<String> testClassList = []
        classAndTestMap.keySet().each { String className ->
            if (this.filesParameterValue.tokenize(RunTestTaskConstants.FILE_SEPARATOR_SIGN).contains(className)) {
                displayMessage(sprintf(APEX_CLASS_RELATED_TESTS_MSG, [className, classAndTestMap.get(className).unique().toString()]))
                testClassList.addAll((classAndTestMap.get(className) as ArrayList<String>).unique())
            }
        }
        return testClassList.unique()
    }
}
