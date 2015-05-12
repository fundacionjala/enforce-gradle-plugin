/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

import java.util.regex.Matcher

/**
 * Implements the truncated algorithm to truncate the component content
 */
class Component {
    public static final String EMPTY_COMPONENT = '<apex:component></apex:component>'
    private final String COMPONENT_REGEX = /<apex:component.*>(.*([^\n]*?\n+?)*?.*)<\/apex:component>/
    private final String ATTRIBUTE_REGEX = /<apex:attribute([^\n]*?\n+?)*?.*\/>/
    private
    final String ATTRIBUTE_BY_DEFAULT = "<apex:attribute name=%s type=\"Object\" required=\"true\" description=\"Description\"/>"
    private final String NAME_ATTRIBUTE = "name"
    private final int INDEX_COMPONENT = 1
    private final int INDEX_ATTRIBUTE = 0

    /**
     * A closure to truncate the component content
     */
    Closure execute = { file ->
        if (!file) {
            return
        }
        String component = file.text
        Matcher componentMatcher = component =~ COMPONENT_REGEX
        String content = ""
        String newContent = ""
        String attribute = ""
        String attributeName = ""
        int indexName
        int indexIniName
        int indexEndName
        componentMatcher.each { componentIt ->
            content = componentIt[INDEX_COMPONENT]
            if (content) {
                Matcher attributeMatcher = component =~ ATTRIBUTE_REGEX
                attributeMatcher.each { attributeIt ->
                    attribute = attributeIt[INDEX_ATTRIBUTE]
                    indexName = attribute.indexOf(NAME_ATTRIBUTE)
                    indexIniName = attribute.indexOf("\"", indexName)
                    indexEndName = attribute.indexOf("\"", indexIniName + 1)
                    attributeName = attribute.substring(indexIniName, indexEndName + 1)
                    newContent += "\n${String.format(ATTRIBUTE_BY_DEFAULT, attributeName)}"
                }
                component = component.replace(content, "${newContent}\n")
            }
        }
        file.text = component
    }
}
