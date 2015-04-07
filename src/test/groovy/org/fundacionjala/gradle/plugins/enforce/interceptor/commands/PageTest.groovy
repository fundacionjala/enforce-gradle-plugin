/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

import org.gradle.api.Project
import spock.lang.Shared
import spock.lang.Specification

class PageTest extends Specification {
    @Shared
    String ROOT_PATH = System.properties['user.dir']
    @Shared
    String RESOURCE_PATH = "${System.properties['user.dir']}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/pages"
    @Shared
    String TRUNCATED_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/interceptor"
    @Shared
    Project project
    @Shared
    Page pageContent

    def setupSpec() {
        new AntBuilder().copy(todir: TRUNCATED_PATH) {
            fileset(dir: RESOURCE_PATH) {
            }
        }
        pageContent = new Page()
    }

    def "Should truncate a page without controller"() {
        setup:
        File file = new File("${TRUNCATED_PATH}/PageWithoutController.page")
        when:
        pageContent.execute(file)
        then:
        file.text == Page.EMPTY_PAGE
    }

    def "Should truncate a page with controller"() {
        setup:
        File file = new File("${TRUNCATED_PATH}/PageExample.page")
        when:
        pageContent.execute(file)
        then:
        file.text == Page.EMPTY_PAGE
    }

    def "Should truncate a page without new lines in its header"() {
        setup:
        File file = new File("${TRUNCATED_PATH}/PageController.page")
        when:
        pageContent.execute(file)
        then:
        file.text == Page.EMPTY_PAGE
    }

    def "Should truncate a page with new lines in its header"() {
        setup:
        File file = new File("${TRUNCATED_PATH}/PageStandardController.page")
        when:
        pageContent.execute(file)
        then:
        file.text == "<apex:page standardController = \"Quote__c\" > </apex:page>"
    }
    def cleanupSpec() {
        new File(TRUNCATED_PATH).deleteDir()
    }
}
