/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

import groovy.util.logging.Slf4j
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.charset.StandardCharsets

/**
 * Implements the truncated algorithm to truncate the workflow content
 */
@Slf4j
class Workflow {

    private final String FORMULA_BY_DEFAULT = '<formula>true</formula>'
    private final String REGEX_FORMULA = /<formula>.*<\/formula>/
    private final String REGEX_ACTIVE = /<\/active>/
    private final String REGEX_CRITERIA_ITEM = /<criteriaItems>.*([^\n]*?\n+?)+?.*<\/criteriaItems>/
    String encoding

    Workflow() {
        this.encoding = StandardCharsets.UTF_8.displayName()
    }
    /**
     * A closure to truncate the workflow content
     */
    Closure execute = { file ->
        if (!file) return
        String charset = Util.getCharset(file)
        def truncatedContent = file.text
        truncatedContent = truncatedContent.replaceAll(REGEX_CRITERIA_ITEM, '') //Cleans criteria item
        truncatedContent = truncatedContent.replaceAll(REGEX_FORMULA, '')      //Cleans formula
        truncatedContent = truncatedContent.replaceAll(REGEX_ACTIVE, "</active>${FORMULA_BY_DEFAULT}")
        Util.writeFile(file, truncatedContent, charset, encoding)
    }
}
