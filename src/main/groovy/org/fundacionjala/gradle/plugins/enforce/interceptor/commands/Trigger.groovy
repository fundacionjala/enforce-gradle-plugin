/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

import groovy.util.logging.Slf4j
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.charset.StandardCharsets
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Implements the truncated algorithm to truncate the trigger content
 */
@Slf4j
class Trigger {
    private final String REGEX_TRIGGER = /trigger\s*(\w*)\son\s*(\w*)\s*\(([^\n]*?\n*?)+?.*\{/
    String encoding

    Trigger() {
        this.encoding = StandardCharsets.UTF_8.displayName()
    }
    /**
     * A closure to truncate the trigger content
     */
    Closure execute = { file ->
        if (!file) return
        String charset = Util.getCharset(file)
        Pattern pattern = Pattern.compile(REGEX_TRIGGER)
        Matcher matcher = pattern.matcher(file.text as String)
        String truncatedCode = ''
        matcher.each { trigger ->
            truncatedCode = trigger.toString()
            truncatedCode = truncatedCode.substring(1, truncatedCode.indexOf('{'))
            truncatedCode = "${truncatedCode}{}"
        }
        Util.writeFile(file, truncatedCode, charset, encoding)
    }
}
