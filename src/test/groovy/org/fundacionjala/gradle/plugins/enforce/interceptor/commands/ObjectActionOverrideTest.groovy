/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

import org.gradle.api.Project
import spock.lang.Shared
import spock.lang.Specification

class ObjectActionOverrideTest extends Specification {
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

    def "Should truncate a object and gets a content with all action name by default"() {
        setup:
        File file = new File("${TRUNCATED_PATH}/ObjectActionOverride.object")
        ObjectActionOverride objectActionOverride = new ObjectActionOverride()
        when:
        objectActionOverride.execute(file)
        then:
        file.text.contains('<type>Default</type>')

    }


    def cleanupSpec() {
        new File(TRUNCATED_PATH).deleteDir()
    }
}
