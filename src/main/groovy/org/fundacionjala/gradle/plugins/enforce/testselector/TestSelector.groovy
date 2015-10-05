/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.testselector

import org.gradle.api.logging.Logger

public abstract class TestSelector implements ITestSelector {

    protected ArrayList<String> testClassNameList
    protected Logger logger

    /**
     * TestSelector class constructor
     * @param testClassNameList list of all available test class names
     */
    protected TestSelector(ArrayList<String> testClassNameList) {
        this.testClassNameList = testClassNameList
    }

    /**
     * Returns the list of test classes according the available TestSelector
     */
    public abstract ArrayList<String> getTestClassNames()

    /**
     * Sets the logger to allow display messages
     * @param logger instance reference of the current Logger
     */
    public void setLogger(Logger logger) {
        this.logger = logger
    }


    /**
     * Displays a quiet log message
     * @param msg message to display
     */
    protected void displayMessage(String msg) {
        displayMessage(msg, false)
    }

    /**
     * Displays a quiet or error log message
     * @param msg message to display
     * @param isError specifies the kind of message quiet/error
     */
    protected void displayMessage(String msg, Boolean isError) {
        if (logger) {
            if (isError) {
                logger.error(msg)
            } else {
                logger.quiet(msg)
            }
        }
    }
}
