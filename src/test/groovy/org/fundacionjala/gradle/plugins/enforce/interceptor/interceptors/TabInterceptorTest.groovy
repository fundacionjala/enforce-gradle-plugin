/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class TabInterceptorTest extends Specification {
    @Shared
    String ROOT_PATH = System.properties['user.dir']

    @Shared
    String RESOURCE_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/tabs"
    @Shared
    String TRUNCATED_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/interceptor"

    def setup() {
        new AntBuilder().copy(todir: TRUNCATED_PATH) {
            fileset(dir: RESOURCE_PATH) {
            }
        }
    }

    def "Should load tabs from source path"() {
        given:
        TabInterceptor tabInterceptor = new TabInterceptor()
        String path = Paths.get(RESOURCE_PATH).toString()
        when:
        tabInterceptor.loadFiles(path)
        then:
        tabInterceptor.files.size() == 4
    }

    def "Should execute the commands of tab truncator"() {
        given:
        TabInterceptor tabInterceptor = new TabInterceptor()
        String path = Paths.get(TRUNCATED_PATH).toString()
        tabInterceptor.interceptorsToExecute = [org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_TABS.id]
        when:
        tabInterceptor.loadFiles(path)
        tabInterceptor.loadInterceptors()
        tabInterceptor.executeInterceptors()
        then:
        tabInterceptor.files.each { file ->
            assert !file.text.contains("customObject") || !file.text.contains("page")
        }
    }

    def cleanup() {
        new File(TRUNCATED_PATH).deleteDir()
    }
}
