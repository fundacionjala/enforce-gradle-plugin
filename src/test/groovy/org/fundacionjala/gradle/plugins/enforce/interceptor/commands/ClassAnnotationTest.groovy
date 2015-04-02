/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

import org.gradle.api.Project
import spock.lang.Shared
import spock.lang.Specification

class ClassAnnotationTest extends Specification {

    @Shared
    String ROOT_PATH = System.properties['user.dir']
    @Shared
    String RESOURCE_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/classes"
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


    def "Should truncate a class"() {
        setup:
        File file = new File("${TRUNCATED_PATH}/ClassContent.cls")
        Class classContent = new Class()
        when:
        classContent.execute(file)
        then:
        file.text == 'public class ClassContent {}'
    }

    def "Should truncate a exception class"() {
        setup:
        File file = new File("${TRUNCATED_PATH}/ClassException.cls")
        Class classContent = new Class()
        when:
        classContent.execute(file)
        then:
        file.text == 'public class ClassException extends Exception {}'
    }

    def "Should remove all @deprecated annotations"() {
        given:
        File file = new File("${TRUNCATED_PATH}/ClassAnnotation.cls")
        ClassAnnotation classAnnotation = new ClassAnnotation(annotation: '@deprecated')
        when:
        classAnnotation.execute(file)
        then:
        !file.text.contains('@deprecated') || !file.text.contains('@Deprecated')
    }


    def cleanupSpec() {
        new File(TRUNCATED_PATH).deleteDir()
    }
}
