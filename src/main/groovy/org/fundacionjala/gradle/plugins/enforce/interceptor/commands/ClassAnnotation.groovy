/*
 * Copyright (c) Fundación Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

/**
 * Implements the truncated algorithm remove any annotation in a class file
 */
class ClassAnnotation {
    private final int INDEX_AT_SIGN = 0
    private final int INDEX_FIRST_LETTER = 1
    private final int INDEX_NEXT = 1
    String annotation

    /**
     * A closure to remove any annotation in a class file
     */
    Closure execute = { file ->
        if (!file) return
        String regex = "${annotation[INDEX_AT_SIGN]}[${annotation[INDEX_FIRST_LETTER].toUpperCase()}"
        regex = "${regex}${annotation[INDEX_FIRST_LETTER].toLowerCase()}]${annotation.substring(INDEX_FIRST_LETTER + INDEX_NEXT)}"
        file.text = file.text.replaceAll(regex, '')
    }
}
