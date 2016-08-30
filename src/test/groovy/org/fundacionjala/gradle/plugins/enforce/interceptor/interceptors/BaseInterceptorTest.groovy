/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class BaseInterceptorTest extends Specification {
    @Shared
    String ROOT_PATH = System.properties['user.dir']
    @Shared
    String RESOURCE_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources"
    @Shared
    String TRUNCATED_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/interceptor"

    def setup() {
        new AntBuilder().copy(todir: TRUNCATED_PATH) {
            fileset(dir: RESOURCE_PATH) {
            }
        }
    }

    def "Should load classes from source path"() {
        given:
        BaseInterceptor baseInterceptor = new BaseInterceptor()
        baseInterceptor.directory = "profiles"
        String path = Paths.get(RESOURCE_PATH).toString()
        when:
        baseInterceptor.loadFiles(path)
        then:
        baseInterceptor.files.size() == 2
    }

    def "Should execute the commands of class truncator"() {
        given:
        BaseInterceptor baseInterceptor = new BaseInterceptor()
        baseInterceptor.directory = "profiles"
        String path = Paths.get(TRUNCATED_PATH).toString()
        baseInterceptor.addInterceptor("AddNewTag", { file ->
            file.text.concat("<tag></tag>")
        })
        baseInterceptor.interceptorsToExecute = []
        when:
        baseInterceptor.loadFiles(path)
        baseInterceptor.loadInterceptors()
        baseInterceptor.executeInterceptors()
        then:
        baseInterceptor.files.each { file ->
            assert !file.text.contains("<tag></tag>")
        }
    }

    def cleanup() {
        new File(TRUNCATED_PATH).deleteDir()
    }
}
