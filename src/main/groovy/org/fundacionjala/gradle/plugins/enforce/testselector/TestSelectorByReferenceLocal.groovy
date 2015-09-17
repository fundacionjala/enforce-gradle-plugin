/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.testselector


class TestSelectorByReferenceLocal extends TestSelector {
    /**
     * TestSelector class constructor
     * @param testClassNameList list of all available test class names
     */
    protected TestSelectorByReferenceLocal(ArrayList<String> testClassNameList) {
        super(testClassNameList)
    }

    @Override
    ArrayList<String> getTestClassNames() {
        return null
    }
}
