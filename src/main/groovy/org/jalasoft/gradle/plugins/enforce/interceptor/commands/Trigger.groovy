/*
 * Copyright (c) Jalasoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.jalasoft.gradle.plugins.enforce.interceptor.commands

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Implements the truncated algorithm to truncate the trigger content
 */
class Trigger {
    private final String REGEX_TRIGGER = /trigger\s*(\w*)\son\s*(\w*)\s*\(([^\n]*?\n*?)+?.*\{/

    /**
     * A closure to truncate the trigger content
     */
    Closure execute = { file ->
        Pattern pattern = Pattern.compile(REGEX_TRIGGER)
        Matcher matcher = pattern.matcher(file.text as String)
        String truncatedCode = ''
        matcher.each { trigger ->
            truncatedCode = trigger.toString()
            truncatedCode = truncatedCode.substring(1, truncatedCode.indexOf('{'))
            truncatedCode = "${truncatedCode}{}"
        }
        file.text = truncatedCode
    }
}
