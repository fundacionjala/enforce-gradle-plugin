/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.execute

import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.SalesforceTask
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.wsc.soap.ApexAPI

/**
 * This class executes apex code from a file
 */
class ApexExecutor extends SalesforceTask {
    private ApexAPI apexAPI
    public String input
    public String output

    /**
     * Sets description and group task
     */
    ApexExecutor() {
        super(Constants.DESCRIPTION_EXECUTE_TASK, Constants.UTILS_GROUP)
    }

    /**
     * Executes apex code from a file and write the results in a file
     */
    @Override
    void runTask() {
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
        logger.quiet("${Constants.OUTPUT_RESULT}${resultExecute}")
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
        if (!Util.isValidProperty(parameters, Constants.APEX_CODE)) {
            throw new Exception("${Constants.APEX_CODE} ${Constants.INVALID_INPUT_PARAMETER}")
        }
        input = parameters[Constants.APEX_CODE].toString()

        if (Util.isValidProperty(parameters, Constants.APEX_OUTPUT_FILE_PATH)) {
            output = parameters[Constants.APEX_OUTPUT_FILE_PATH].toString()
        }
    }
}