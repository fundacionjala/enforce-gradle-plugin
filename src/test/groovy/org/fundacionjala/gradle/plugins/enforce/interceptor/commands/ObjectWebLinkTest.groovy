/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.gradle.api.Project
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class ObjectWebLinkTest extends Specification {
    @Shared
    String ROOT_PATH = System.properties['user.dir']
    @Shared
    String RESOURCE_PATH = "${System.properties['user.dir']}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/objects"
    @Shared
    String TRUNCATED_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/interceptor"
    @Shared
    Project project

    def setupSpec() {
        new AntBuilder().copy(todir: TRUNCATED_PATH) {
            fileset(dir: RESOURCE_PATH) {
            }
        }
    }

    def "Should replace all webLinks each one by basic webLink in the objects"() {
        given:
        def pathObject = Paths.get(TRUNCATED_PATH, "ObjectWebLink.object").toString()
        def pathObjectTruncated = Paths.get(TRUNCATED_PATH, "ObjectWebLinkTruncated.object").toString()
        File fileObject = new File(pathObject)
        File fileObjectTruncated = new File(pathObjectTruncated)
        ObjectWebLink objectWebLink = new ObjectWebLink()
        when:
        objectWebLink.execute(fileObject)
        XMLUnit.ignoreWhitespace = true
        def xmlDiff = new Diff(fileObject.text, fileObjectTruncated.text)
        then:
        xmlDiff.similar()
    }

    def cleanupSpec() {
        new File(TRUNCATED_PATH).deleteDir()
    }
}
