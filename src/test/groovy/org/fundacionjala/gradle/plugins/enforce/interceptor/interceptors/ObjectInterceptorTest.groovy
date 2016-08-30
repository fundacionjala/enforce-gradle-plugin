/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class ObjectInterceptorTest extends Specification {
    @Shared
    String ROOT_PATH = System.properties['user.dir']
    @Shared
    String RESOURCE_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/objects"
    @Shared
    String TRUNCATED_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/interceptor"

    def setup() {
        new AntBuilder().copy(todir: TRUNCATED_PATH) {
            fileset(dir: RESOURCE_PATH) {
            }
        }
    }

    def "Should load objects from source path"() {
        given:
        ObjectInterceptor objectInterceptor = new ObjectInterceptor()
        String path = Paths.get(RESOURCE_PATH).toString()
        when:
        objectInterceptor.loadFiles(path)
        then:
        objectInterceptor.files.size() == 9

    }

    def "Should execute the commands of object truncator"() {
        given:
        ObjectInterceptor objectInterceptor = new ObjectInterceptor()
        String path = Paths.get(TRUNCATED_PATH).toString()
        objectInterceptor.interceptorsToExecute = [org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_ACTION_OVERRIDES.id, org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_FIELD_SETS.id,
                                                   org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_FORMULAS.id, org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_WEB_LINKS.id]
        when:

        objectInterceptor.loadFiles(path)
        objectInterceptor.loadInterceptors()
        objectInterceptor.executeInterceptors()
        then:
        objectInterceptor.files.each { file ->
            assert !file.text.contains("actionOverrides") || !file.text.contains("fieldSets") ||
                    !file.text.contains("formula") || !file.text.contains("webLinks")
        }
    }

    def "Should add new command as a first command"() {
        given:
        ObjectInterceptor objectInterceptor = new ObjectInterceptor()
        objectInterceptor.interceptorsToExecute = [org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_ACTION_OVERRIDES.id, org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_FIELD_SETS.id,
                                                   org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_FORMULAS.id, org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_WEB_LINKS.id]
        when:
        objectInterceptor.loadInterceptors()
        def commands = objectInterceptor.interceptors.keySet().toArray()
        objectInterceptor.addInterceptor(0, 'firstCmd', {
            println it
        })
        commands = objectInterceptor.interceptors.keySet().toArray()
        then:
        commands[0] == 'firstCmd'
        commands[1] == org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_FORMULAS.id
    }

    def "Should add new command as a last command"() {
        given:
        ObjectInterceptor objectInterceptor = new ObjectInterceptor()
        objectInterceptor.interceptorsToExecute = [org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_ACTION_OVERRIDES.id, org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_FIELD_SETS.id,
                                                   org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_FORMULAS.id, org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_WEB_LINKS.id]

        when:
        objectInterceptor.loadInterceptors()
        def commands = objectInterceptor.interceptors.keySet().toArray()
        objectInterceptor.addInterceptor(commands.length, 'lastCmd', {
            println it
        })
        commands = objectInterceptor.interceptors.keySet().toArray()
        then:
        commands[commands.length - 3] == org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_ACTION_OVERRIDES.id
        commands[commands.length - 1] == 'lastCmd'
    }

    def "Should add new command in the third index"() {
        given:
        ObjectInterceptor objectInterceptor = new ObjectInterceptor()
        objectInterceptor.interceptorsToExecute = [org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_ACTION_OVERRIDES.id, org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_FIELD_SETS.id,
                                                   org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_FORMULAS.id, org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_WEB_LINKS.id]

        when:
        objectInterceptor.loadInterceptors()
        objectInterceptor.addInterceptor(2, 'middleCmd', {
            println it
        })
        def commands = objectInterceptor.interceptors.keySet().toArray()
        then:
        commands[3] == org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_FIELD_SETS.id
        commands[2] == 'middleCmd'
        commands[1] == org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_WEB_LINKS.id
    }

    def cleanup() {
        new File(TRUNCATED_PATH).deleteDir()
    }
}
