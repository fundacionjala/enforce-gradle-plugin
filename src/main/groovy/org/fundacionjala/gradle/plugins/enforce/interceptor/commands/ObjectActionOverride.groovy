/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

import groovy.util.logging.Slf4j
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.charset.StandardCharsets

/**
 * Implements the truncated algorithm to replace an "action override" by default for all "action overrides"
 * that matches with the regex in an object file
 */
@Slf4j
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
    String encoding

    ObjectActionOverride() {
        this.encoding = StandardCharsets.UTF_8.displayName()
    }

    /**
     * A closure to replace an "action override" by default for all "action override"
     * that matches with the regex in an object file
     */
    Closure execute = { file ->
        if (!file) return
        String charset = Util.getCharset(file)
        String content = file.text.replaceAll(REGEX_ACTION, REPLACEMENT_TEXT)
        Util.writeFile(file, content, charset, encoding)
    }
}
