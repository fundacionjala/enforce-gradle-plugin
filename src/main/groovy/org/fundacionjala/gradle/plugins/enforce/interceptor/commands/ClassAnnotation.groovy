/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

import groovy.util.logging.Slf4j
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.charset.StandardCharsets

/**
 * Implements the truncated algorithm remove any annotation in a class file
 */
@Slf4j
class ClassAnnotation {
    private final int INDEX_AT_SIGN = 0
    private final int INDEX_FIRST_LETTER = 1
    private final int INDEX_NEXT = 1
    String annotation
    String encoding

    ClassAnnotation() {
        this.encoding = StandardCharsets.UTF_8.displayName()
    }

    /**
     * A closure to remove any annotation in a class file
     */
    Closure execute = { file ->
        if (!file) return
        String charset = Util.getCharset(file)
        String regex = "${annotation[INDEX_AT_SIGN]}[${annotation[INDEX_FIRST_LETTER].toUpperCase()}"
        regex = "${regex}${annotation[INDEX_FIRST_LETTER].toLowerCase()}]${annotation.substring(INDEX_FIRST_LETTER + INDEX_NEXT)}"
        String content = file.text.replaceAll(regex, '')
        Util.writeFile(file, content, charset, encoding)
    }
}
