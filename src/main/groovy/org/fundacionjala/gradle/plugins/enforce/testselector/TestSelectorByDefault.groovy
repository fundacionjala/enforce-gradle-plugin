/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.testselector

import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.unittest.RunTestTaskConstants
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

class TestSelectorByDefault extends TestSelector {

    private String clsParamValue

    /**
     * TestSelectorByDefault class constructor
     * @param testClassNameList list of all available test class names
     * @param clsParamValue value provided by the user to filter the test class names
     */
    public TestSelectorByDefault(ArrayList<String> testClassNameList, String clsParamValue) {
        super(testClassNameList)
        this.clsParamValue = null;
        if (clsParamValue) {
            this.clsParamValue = clsParamValue.replace(RunTestTaskConstants.WILD_CARD_SIGN, "")
            this.clsParamValue = this.clsParamValue.replace(".${MetadataComponents.CLASSES.getExtension()}", "")
        }
    }

    @Override
    ArrayList<String> getTestClassNames() {
        ArrayList<String> testClassList = new ArrayList<String>()
        if (this.clsParamValue) {
            testClassNameList.each { testClassName ->
                this.clsParamValue.tokenize(RunTestTaskConstants.FILE_SEPARATOR_SIGN).each { wildCard ->
                    if (testClassName.contains(wildCard)) {
                        testClassList.add(testClassName)
                    }
                }
            }
        } else {
            testClassList = testClassNameList
        }
        return testClassList
    }
}
