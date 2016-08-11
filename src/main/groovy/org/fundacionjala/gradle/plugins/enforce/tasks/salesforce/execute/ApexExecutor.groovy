/*
 * Copyright (c) Fundacion Jala. All rights reserved.
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
    private static final String APEX_CODE = 'input'
    private static final String APEX_OUTPUT_FILE_PATH = 'output'
    private static final String DESCRIPTION_EXECUTE_TASK = "This task executes apex code from a file or inline code"
    private static final String UTILS_GROUP = "Utils"
    private static final String OUTPUT_RESULT = "Output result:\n"
    private static final String INPUT_VALUE_IS_REQUIRED = "input' parameter is required, it cannot be empty"
    private ApexAPI apexAPI
    public String input
    public String output

    /**
     * Sets description and group task
     */
    ApexExecutor() {
        super(DESCRIPTION_EXECUTE_TASK, UTILS_GROUP)
    }

    /**
     * Executes apex code from a file and write the results in a file
     */
    @Override
    void runTask() {
        String resultExecute
        if (input == null || input.isEmpty()) {
            throw new Exception(INPUT_VALUE_IS_REQUIRED)
        }
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
        logger.quiet("${OUTPUT_RESULT}${resultExecute}")
    }

    /**
     * Creates an instance of ApexApi
     */
    @Override
    void setup() {
        apexAPI = new ApexAPI(credential)
    }

    /**
     * Loads input and output parameter
     */
    @Override
    void loadParameters() {
        if (Util.isValidProperty(parameters, APEX_CODE)) {
            input = parameters[APEX_CODE].toString()
        }

        if (Util.isValidProperty(parameters, APEX_OUTPUT_FILE_PATH)) {
            output = parameters[APEX_OUTPUT_FILE_PATH].toString()
        }
    }
}