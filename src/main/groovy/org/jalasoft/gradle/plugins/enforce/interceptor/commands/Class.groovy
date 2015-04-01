/*
 * Copyright (c) Jalasoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.jalasoft.gradle.plugins.enforce.interceptor.commands

import org.jalasoft.gradle.plugins.enforce.utils.Util

/**
 * Implements the truncated algorithm to truncate the class content
 */
class Class {
    private final String CLASS_DECLARATION = 'public class %s {}'
    private final String EXCEPTION_FILE_NAME = 'Exception.cls'
    private final String CLASS_DECLARATION_EXCEPTION = 'public class %s extends %s {}'

    /**
     * A closure to truncate the class content
     */
    Closure execute = { file ->
        if (!file) return
        String className = Util.getFileName(file.getName())
        file.text = String.format(CLASS_DECLARATION, className)
        if (file.getName().contains(EXCEPTION_FILE_NAME)) {
            String exceptionName = Util.getFileName(EXCEPTION_FILE_NAME)
            file.text = String.format(CLASS_DECLARATION_EXCEPTION, className, exceptionName)
        }

    }
}
