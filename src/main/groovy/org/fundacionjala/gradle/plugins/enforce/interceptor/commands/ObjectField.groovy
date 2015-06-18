/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

import groovy.util.logging.Slf4j
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.charset.StandardCharsets
import java.util.regex.Matcher

/**
 * Implements the truncated algorithm to replace a "field.description" and "field.inlineHelpText" by a default value
 */
@Slf4j
class ObjectField {
    private final String FIELDS_REGEX = /<fields>.*([^\n]*?\n+?)*?.*<\/fields>/
    private final String DESCRIPTION_REGEX = /<description>(([^\n]*?\n+?)*?.*<\/description>){0,1}/
    private final String HELP_TEXT_REGEX = /<inlineHelpText>(([^\n]*?\n+?)*?.*<\/inlineHelpText>){0,1}/
    private final int FIELD_INDEX = 0
    private final int DESCRIPTION_INDEX = 0
    private final int HELP_TEXT_INDEX = 0
    private final String DESCRIPTION_TAG = "<description>new description</description>"
    private final String HELP_TEXT_TAG = "<inlineHelpText>new help text</inlineHelpText>"
    String encoding

    ObjectField() {
        this.encoding = StandardCharsets.UTF_8.displayName()
    }

    /**
     * A closure to replace a "field.description" and "field.inlineHelpText" by default for all "fields" that matches
     * with the regex in an object file
     */
    Closure execute = { file ->
        if (!file) {
            return
        }
        String charset = Util.getCharset(file)
        String objectField = file.text
        Matcher fieldMatcher = objectField =~ FIELDS_REGEX
        fieldMatcher.each { fieldIt ->
            String field = fieldIt[FIELD_INDEX]
            if (field) {
                String newField = field
                Matcher descriptionMatcher = field =~ DESCRIPTION_REGEX
                descriptionMatcher.each { descriptionIt ->
                    newField = field.replace(descriptionIt[DESCRIPTION_INDEX].toString(), DESCRIPTION_TAG)
                }
                Matcher helpTextMatcher = field =~ HELP_TEXT_REGEX
                helpTextMatcher.each { helpTextIt ->
                    newField = newField.replace(helpTextIt[HELP_TEXT_INDEX].toString(), HELP_TEXT_TAG)
                }
                objectField = objectField.replace(field, newField)
            }
        }
        log.debug "[${file.name}]-->[charset:${charset}]"
        if (charset) {
            file.write(objectField, charset)
        } else {
            log.warn  "No encoding detected for ${file.name}. The encoding by default is ${encoding}."
            file.write(objectField, encoding)
        }
    }
}
