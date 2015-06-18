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
 * Implements the truncated algorithm to replace a "field set" by default for all "field sets"
 * that matches with the regex in an object file
 */
@Slf4j
class ObjectFieldSet {
    private final String REGEX_FIELD_SETS = /<fieldSets>\s*(<fullName>\w*<\/fullName>)\s*([^\n]*?\n+?)+?.*<\/fieldSets>/
    private final String TAG_FILE_SET_INI = "<fieldSets>"
    private final String TAG_FILE_SET_END = "</fieldSets>"
    private final String TAG_DESCRIPTION = "<description>Description</description>"
    private final String TAG_LABEL = "<label>Label</label>"
    private final String REGEX_END = /<\/CustomObject>/
    String encoding

    ObjectFieldSet() {
        this.encoding = StandardCharsets.UTF_8.displayName()
    }

    /**
     * A closure to replace a "field set" by default for all "field set" that matches with the regex in an object file
     */
    Closure execute = { file ->
        if (!file) return
        String charset = Util.getCharset(file)
        Matcher matcher = Pattern.compile(REGEX_FIELD_SETS).matcher(file.text as String)
        StringBuilder fieldNames = new StringBuilder()
        int groupFieldFullName = 1
        String contentResult = file.text.replaceAll(REGEX_FIELD_SETS, '')
        String fieldSetName
        while (matcher.find()) {
            fieldSetName = matcher.group(groupFieldFullName)
            fieldNames.append("${TAG_FILE_SET_INI}${fieldSetName}")
                    .append(TAG_DESCRIPTION)
                    .append(TAG_LABEL)
                    .append("${TAG_FILE_SET_END}\n")
        }
        fieldNames.append(REGEX_END)
        contentResult = contentResult.replaceAll(REGEX_END, fieldNames.toString())
        Util.writeFile(file, contentResult, charset, encoding)
    }
}
