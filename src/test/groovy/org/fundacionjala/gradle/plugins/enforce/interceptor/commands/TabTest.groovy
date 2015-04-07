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

class TabTest extends Specification {
    @Shared
    String ROOT_PATH = System.properties['user.dir']

    @Shared
    String RESOURCE_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/tabs"

    @Shared
    String TRUNCATED_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/interceptor"

    @Shared
    Project project

    @Shared
    Tab tabContent

    def setupSpec() {
        new AntBuilder().copy(todir: TRUNCATED_PATH) {
            fileset(dir: RESOURCE_PATH) {
            }
        }
        tabContent = new Tab()
    }

    def "Must convert from Visualforce tabs to web tabs"() {

        given:
        def pathVisualForce = Paths.get(TRUNCATED_PATH, "VisualForce.tab").toString()
        def pathWeb = Paths.get(TRUNCATED_PATH, "BecomeVisualForce.tab").toString()

        File fileVisualForce = new File(pathVisualForce)
        File fileWeb = new File(pathWeb)
        when:
        tabContent.execute(fileVisualForce)
        XMLUnit.ignoreWhitespace = true
        def xmlDiff = new Diff(fileVisualForce.text, fileWeb.text)
        then:
        xmlDiff.similar()
    }

    def "Should not convert Object tab to web tab"() {

        given:
        def pathObjectTabTruncate = Paths.get(TRUNCATED_PATH, "Object1__c.tab").toString()
        def pathObjectTabSource = Paths.get(RESOURCE_PATH, "Object1__c.tab").toString()

        File fileObjectTabTruncate = new File(pathObjectTabTruncate)
        File fileObjectTabSource = new File(pathObjectTabSource)
        when:
        tabContent.execute(fileObjectTabTruncate)
        XMLUnit.ignoreWhitespace = true
        def xmlDiff = new Diff(fileObjectTabTruncate.text, fileObjectTabSource.text)
        then:
        xmlDiff.similar()
    }
    def cleanupSpec() {
        new File(TRUNCATED_PATH).deleteDir()
    }
}
