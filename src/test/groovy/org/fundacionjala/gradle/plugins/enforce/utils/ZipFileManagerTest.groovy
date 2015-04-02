/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.utils

import spock.lang.Shared
import spock.lang.Specification

class ZipFileManagerTest extends Specification {

    @Shared
    ZipFileManager zipFileManagement

    def setup() {
        String zipFileName = 'file.zip'
        String inputDirectory = System.getProperty("user.dir") + "/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/utils/resources/classes/"
        String targetPath = System.getProperty("user.dir") + "/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/utils/resources/"
        zipFileManagement = new ZipFileManager()
    }

    def "Test what ever" () {
        expect:
         true
    }
}
