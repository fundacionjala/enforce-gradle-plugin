/*
 * Copyright (c) Fundación Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

/**
 * Implements the truncated algorithm to truncate the workflow content
 */
class Workflow {

    private final String FORMULA_BY_DEFAULT = '<formula>true</formula>'
    private final String REGEX_FORMULA = /<formula>.*<\/formula>/
    private final String REGEX_ACTIVE = /<\/active>/
    private final String REGEX_CRITERIA_ITEM = /<criteriaItems>.*([^\n]*?\n+?)+?.*<\/criteriaItems>/

    /**
     * A closure to truncate the workflow content
     */
    Closure execute = { file ->
        def truncatedContent = file.text
        truncatedContent = truncatedContent.replaceAll(REGEX_CRITERIA_ITEM, '') //Cleans criteria item
        truncatedContent = truncatedContent.replaceAll(REGEX_FORMULA, '')      //Cleans formula
        truncatedContent = truncatedContent.replaceAll(REGEX_ACTIVE, "</active>${FORMULA_BY_DEFAULT}")
        file.text = truncatedContent
    }
}
