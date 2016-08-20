/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class TriggerInterceptorTest extends Specification {
    @Shared
    String ROOT_PATH = System.properties['user.dir']

    @Shared
    String RESOURCE_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/triggers"
    @Shared
    String TRUNCATED_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/interceptor"

    def setup() {
        new AntBuilder().copy(todir: TRUNCATED_PATH) {
            fileset(dir: RESOURCE_PATH) {
            }
        }
    }

    def "Should load triggers from source path"() {
        given:
        TriggerInterceptor triggerInterceptor = new TriggerInterceptor()
        String path = Paths.get(RESOURCE_PATH).toString()
        when:
        triggerInterceptor.loadFiles(path)
        then:
        triggerInterceptor.files.size() == 6
    }

    def "Should execute the commands of trigger truncator"() {
        given:
        TriggerInterceptor triggerInterceptor = new TriggerInterceptor()
        String path = Paths.get(TRUNCATED_PATH).toString()
        triggerInterceptor.interceptorsToExecute = [org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_TRIGGERS.id]
        when:
        triggerInterceptor.loadFiles(path)
        triggerInterceptor.loadInterceptors()
        triggerInterceptor.executeInterceptors()
        then:
        triggerInterceptor.files.each { file ->
            assert file.text.contains("{}")
        }
    }
}
