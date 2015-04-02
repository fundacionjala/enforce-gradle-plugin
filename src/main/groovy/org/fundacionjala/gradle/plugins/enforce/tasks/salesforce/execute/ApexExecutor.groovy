/*
 * Copyright (c) Fundación Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.execute

import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.SalesforceTask
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.wsc.soap.ApexAPI

/**
 * This class executes apex code from a file
 */
class ApexExecutor extends SalesforceTask {

    private final String APEX_CODE = 'input'
    private final String APEX_OUTPUT_FILE_PATH = 'output'

    private ApexAPI apexAPI
    public String input
    public String output

    /**
     * Sets description and group task
     */
    ApexExecutor() {
        super("This task executes apex code from a file or inline code", "Utils")
    }

    /**
     * Executes apex code from a file and write the results in a file
     */
    @Override
    void runTask() {
        apexAPI = new ApexAPI(credential)
        if(!input) {
            loadInputProperties()
        }

        loadOutProperties()
        String resultExecute
        if (new File(input).exists()) {
            resultExecute = apexAPI.executeApexFile(input)
        } else {
            resultExecute = apexAPI.executeApex(input)
        }
        if(output) {
            FileWriter fileWriter = new FileWriter(output)
            fileWriter.write(resultExecute)
            fileWriter.close()
            return
        }

        logger.quiet("Output result:\n${resultExecute}")
    }

    /**
     * Loads input apex code from project properties
     */
    def loadInputProperties() {
        if (!Util.isValidProperty(project, APEX_CODE) || Util.isEmptyProperty(project, APEX_CODE)) {
            throw new Exception("Parameter ${APEX_CODE} not valid")
        }
        input = project.properties[APEX_CODE].toString()
    }

    /**
     * Loads path source file from project properties
     */
    def loadOutProperties() {
        if (Util.isValidProperty(project, APEX_OUTPUT_FILE_PATH) && !Util.isEmptyProperty(project, APEX_OUTPUT_FILE_PATH)) {
            output = project.properties[APEX_OUTPUT_FILE_PATH].toString()
        }
    }
}