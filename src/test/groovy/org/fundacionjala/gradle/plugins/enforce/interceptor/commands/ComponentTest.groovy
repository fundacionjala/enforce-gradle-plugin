/*
 * Copyright (c) Fundación Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

import org.gradle.api.Project
import spock.lang.Shared
import spock.lang.Specification

class ComponentTest extends Specification {
    @Shared
    String ROOT_PATH = System.properties['user.dir']

    @Shared
    String RESOURCE_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/components"

    @Shared
    String TRUNCATED_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/interceptor"

    @Shared
    Project project

    @Shared
    Component componentContent

    def setupSpec() {
        new AntBuilder().copy(todir: TRUNCATED_PATH) {
            fileset(dir: RESOURCE_PATH) {
            }
        }
        componentContent = new Component()
    }


    def "Should truncate a component"() {
        setup:
        File file = new File("${TRUNCATED_PATH}/Component1.component")
        when:
        componentContent.execute(file)
        then:
        file.text == Component.EMPTY_COMPONENT
    }

    def cleanupSpec() {
        new File(TRUNCATED_PATH).deleteDir()
    }
}
