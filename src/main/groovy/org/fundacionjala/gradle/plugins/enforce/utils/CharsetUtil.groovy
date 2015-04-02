/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.utils

import java.nio.charset.Charset

class CharsetUtil {
    public static final Charset UTF_8 = Charset.forName("UTF-8")

    public static String getTextWithEncoding(File file) {
        CharsetToolkit toolkit = new CharsetToolkit(file)
        String text = new String(file.text.bytes, toolkit.getCharset())
        text
    }

    public static void writeTextWithSourceEncoding(File file) {
        CharsetToolkit toolkit = new CharsetToolkit(file)
        new File(file.absolutePath).write(file.text, toolkit.getCharset().name())
    }
}
