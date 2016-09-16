/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class ClassInterceptorTest extends Specification {
    @Shared
    String ROOT_PATH = System.properties['user.dir']
    @Shared
    String RESOURCE_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/classes"
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
        ClassInterceptor classInterceptor = new ClassInterceptor()
        String path = Paths.get(RESOURCE_PATH).toString()
        when:
        classInterceptor.loadFiles(path)
        then:
        classInterceptor.files.size() == 3
    }

    def "Should execute the commands of class truncator"() {
        given:
        ClassInterceptor classInterceptor = new ClassInterceptor()
        String path = Paths.get(TRUNCATED_PATH).toString()
        classInterceptor.interceptorsToExecute = [org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_CLASSES.id]
        when:
        classInterceptor.loadFiles(path)
        classInterceptor.loadInterceptors()
        classInterceptor.executeInterceptors()
        then:
        classInterceptor.files.each { file ->
            assert !file.text.contains("@deprecated")
        }
    }

    def cleanup() {
        new File(TRUNCATED_PATH).deleteDir()
    }
}
