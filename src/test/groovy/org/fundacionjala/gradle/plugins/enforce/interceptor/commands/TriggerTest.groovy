/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

import org.gradle.api.Project
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class TriggerTest extends Specification {
    @Shared
    String ROOT_PATH = System.properties['user.dir']

    @Shared
    String RESOURCE_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/triggers"

    @Shared
    String TRUNCATED_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/interceptor"

    @Shared
    Project project

    @Shared
    Trigger triggerContent

    def setupSpec() {
        new AntBuilder().copy(todir: TRUNCATED_PATH) {
            fileset(dir: RESOURCE_PATH) {
            }
        }
        triggerContent = new Trigger()
    }

    def "Should truncate a trigger content with new lines in the method signature"() {
        setup:
        File file = new File("${RESOURCE_PATH}/trigger1.trigger")
        when:
        triggerContent.execute(file)
        then:
        def expectCode = new File(Paths.get(RESOURCE_PATH, "truncateTrigger1.trigger").toString()).getText()
        file.text == expectCode
    }

    def "Should truncate a trigger content without new lines in the method signature"() {
        setup:
        File file = new File("${RESOURCE_PATH}/trigger2.trigger")
        when:
        triggerContent.execute(file)
        then:
        def expectCode = new File(Paths.get(RESOURCE_PATH, "truncateTrigger2.trigger").toString()).getText()
        file.text == expectCode
    }
    def cleanupSpec() {
        new File(TRUNCATED_PATH).deleteDir()
    }
}
