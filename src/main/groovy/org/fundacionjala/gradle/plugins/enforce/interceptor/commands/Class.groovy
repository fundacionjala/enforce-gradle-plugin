/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

import groovy.util.logging.Slf4j
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.charset.StandardCharsets

/**
 * Implements the truncated algorithm to truncate the class content
 */
@Slf4j
class Class {
    private final String CLASS_DECLARATION = 'public class %s {}'
    private final String EXCEPTION_FILE_NAME = 'Exception.cls'
    private final String CLASS_DECLARATION_EXCEPTION = 'public class %s extends %s {}'
    String encoding

    Class() {
        this.encoding = StandardCharsets.UTF_8.displayName()
    }

    /**
     * A closure to truncate the class content
     */
    Closure execute = { file ->
        if (!file) return
        String charset = Util.getCharset(file)
        String className = Util.getFileName(file.getName())
        String content = String.format(CLASS_DECLARATION, className)
        if (file.getName().contains(EXCEPTION_FILE_NAME)) {
            String exceptionName = Util.getFileName(EXCEPTION_FILE_NAME)
            content = String.format(CLASS_DECLARATION_EXCEPTION, className, exceptionName)
        }
        Util.writeFile(file, content, charset, encoding)
    }
}
