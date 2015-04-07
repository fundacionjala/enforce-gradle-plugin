/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest.report

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class HtmlManagerTest extends Specification {

    @Shared
    String ROOT_PATH = System.properties['user.dir']

    @Shared
    String RESOURCE_PATH = Paths.get(ROOT_PATH, 'src', 'test', 'groovy', 'com',
            'fundacionjala', 'gradle', 'plugins',
            'unittest', 'report', 'resources').toString()
    @Shared
    File fileIndexHtml

    @Shared
    StringWriter write

    @Shared
    HtmlManager htmlManager

    def setup() {
        write = new StringWriter()
        htmlManager = new HtmlManager(write)
        fileIndexHtml = new File(Paths.get(RESOURCE_PATH, 'index.html').toString())
    }

}
