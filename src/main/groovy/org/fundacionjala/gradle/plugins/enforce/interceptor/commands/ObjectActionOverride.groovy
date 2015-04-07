/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

/**
 * Implements the truncated algorithm to replace an "action override" by default for all "action overrides"
 * that matches with the regex in an object file
 */
class ObjectActionOverride {
    private final String ATTRIB_ACTION_NAME = 'actionName'
    private final String ATTRIB_TYPE = 'type'
    private final String ATTRIB_ACTION_OVERRIDE = 'actionOverrides'
    private final String REGEX_ACTION_NAME = /<${ATTRIB_ACTION_NAME}>(\w*)<\/${ATTRIB_ACTION_NAME}>/
    private final String REGEX_TYPE = /<${ATTRIB_TYPE}>Visualforce<\/${ATTRIB_TYPE}>/
    private final String REGEX_ACTION = /<${ATTRIB_ACTION_OVERRIDE}>\s*${REGEX_ACTION_NAME}.*\s*.*\s*.*\s*${REGEX_TYPE}/
    private final String REPLACEMENT_TYPE = "<${ATTRIB_TYPE}>Default</${ATTRIB_TYPE}>"
    private
    final String REPLACEMENT_TEXT = "<${ATTRIB_ACTION_OVERRIDE}><${ATTRIB_ACTION_NAME}>\$1</${ATTRIB_ACTION_NAME}>${REPLACEMENT_TYPE}"

    /**
     * A closure to replace an "action override" by default for all "action override"
     * that matches with the regex in an object file
     */
    Closure execute = { file ->
        if (!file) return
        file.text = file.text.replaceAll(REGEX_ACTION, REPLACEMENT_TEXT)
    }
}
