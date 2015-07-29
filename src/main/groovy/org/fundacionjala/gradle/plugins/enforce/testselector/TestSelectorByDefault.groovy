/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.testselector

import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.unittest.RunTestTaskConstants
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

class TestSelectorByDefault extends TestSelector {

    private String clsParamValue

    public TestSelectorByDefault(ArrayList<String> testClassNameList, String clsParamValue) {
        super(testClassNameList)
        this.clsParamValue = clsParamValue ? clsParamValue.replace(RunTestTaskConstants.WILD_CARD_SIGN, "").replace(".${MetadataComponents.CLASSES.getExtension()}", "") : null
    }

    /**
     * Returns the list of test classes according the fileParamValue form the parameters
     * @return an resultTracker with status
     */
    @Override
    ArrayList<String> getTestClassNames() {
        if (this.clsParamValue) {
            ArrayList<String> testClassList = new ArrayList<String>()
            testClassNameList.each { testClassName ->
                this.clsParamValue.tokenize(RunTestTaskConstants.FILE_SEPARATOR_SIGN).each { wildCard ->
                    if (testClassName.contains(wildCard)) {
                        testClassList.add(testClassName)
                    }
                }
            }
            return testClassList
        }
        return testClassNameList
    }
}
