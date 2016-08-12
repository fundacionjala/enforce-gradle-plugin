package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.utils.Constants

/**
 * Deploy performing validation in all project source
 */
class Validate extends Deploy {
    private static final String VALIDATING_CODE = 'Starting validation'

    /**
     * Sets description task and its group
     */
    Validate() {
        super()
        this.description = VALIDATING_CODE
        this.group = Constants.DEPLOYMENT
    }

    @Override
    void runTask() {
        checkOnly = true
        super.runTask()
    }
}
