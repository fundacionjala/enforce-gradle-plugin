/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class ComponentInterceptorTest extends Specification {
    @Shared
    String ROOT_PATH = System.properties['user.dir']

    @Shared
    String RESOURCE_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/components"
    @Shared
    String TRUNCATED_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/interceptor"

    def setup() {
        new AntBuilder().copy(todir: TRUNCATED_PATH) {
            fileset(dir: RESOURCE_PATH) {
            }
        }
    }

    def "Should load components from source path"() {
        given:
        ComponentInterceptor componentInterceptor = new ComponentInterceptor()
        String path = Paths.get(RESOURCE_PATH).toString()
        when:
        componentInterceptor.loadFiles(path)
        then:
        componentInterceptor.files.size() == 2
    }

    def "Should execute the commands of component truncator"() {
        given:
        ComponentInterceptor componentInterceptor = new ComponentInterceptor()
        String path = Paths.get(TRUNCATED_PATH).toString()
        componentInterceptor.interceptorsToExecute = [org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_COMPONENTS.id]
        when:
        componentInterceptor.loadFiles(path)
        componentInterceptor.loadInterceptors()
        componentInterceptor.executeInterceptors()
        then:
        componentInterceptor.files.each { file ->
            assert !file.text.contains("h1") || !file.text.contains("outputText")
        }
    }

    def cleanup() {
        new File(TRUNCATED_PATH).deleteDir()
    }
}
