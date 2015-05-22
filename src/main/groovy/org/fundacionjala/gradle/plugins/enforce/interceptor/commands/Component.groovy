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
    private final String ATTRIBUTE_REGEX = /<apex:attribute(.|\s)*?\/>/
    private final String ATTRIBUTE_BY_DEFAULT = "<apex:attribute name=%s type=\"Object\" required=\"false\" description=\"Description\"/>"
    private final String NAME_ATTRIBUTE = "name"
    private final String COMPONENT_BY_DEFAULT = "<apex:component>%s\n</apex:component>"
    private final int INDEX_ATTRIBUTE = 0

    /**
     * A closure to truncate the component content
     */
    Closure execute = { file ->
        if (!file) {
            return
        }
        String component = file.text
        String newAttributes = ""
        String attribute = ""
        String attributeName = ""
        int indexName
        int indexIniName
        int indexEndName
        Matcher attributeMatcher = component =~ ATTRIBUTE_REGEX
        attributeMatcher.each { attributeIt ->
            attribute = attributeIt[INDEX_ATTRIBUTE]
            indexName = attribute.indexOf(NAME_ATTRIBUTE)
            indexIniName = attribute.indexOf("\"", indexName)
            indexEndName = attribute.indexOf("\"", indexIniName + 1)
            attributeName = attribute.substring(indexIniName, indexEndName + 1)
            newAttributes += "\n${String.format(ATTRIBUTE_BY_DEFAULT, attributeName)}"
        }

        file.text = String.format(COMPONENT_BY_DEFAULT, newAttributes)
    }
}
