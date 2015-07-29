/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.testselector

import org.gradle.api.logging.Logger

public abstract class TestSelector implements ITestSelector {

    protected ArrayList<String> testClassNameList
    protected Logger logger

    protected TestSelector(ArrayList<String> testClassNameList) {
        this.testClassNameList = testClassNameList
    }

    public abstract ArrayList<String> getTestClassNames();

    public void setLogger(Logger logger) {
        this.logger = logger
    }
}
