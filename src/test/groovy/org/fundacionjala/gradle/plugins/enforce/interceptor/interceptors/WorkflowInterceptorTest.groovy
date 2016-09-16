/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class WorkflowInterceptorTest extends Specification {
    @Shared
    String ROOT_PATH = System.properties['user.dir']

    @Shared
    String RESOURCE_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/workflows"
    @Shared
    String TRUNCATED_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/interceptor"

    def setup() {
        new AntBuilder().copy(todir: TRUNCATED_PATH) {
            fileset(dir: RESOURCE_PATH) {
            }
        }
    }

    def "Should create gets workflows from source path"() {
        given:
        WorkflowInterceptor workflowInterceptor = new WorkflowInterceptor()
        String path = Paths.get(RESOURCE_PATH).toString()
        when:
        workflowInterceptor.loadFiles(path)
        then:
        workflowInterceptor.files.size() == 2

    }

    def "Should execute the commands of workflow truncator"() {
        given:
        WorkflowInterceptor workflowInterceptor = new WorkflowInterceptor()
        String path = Paths.get(TRUNCATED_PATH).toString()
        workflowInterceptor.interceptorsToExecute = [org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_WORKFLOWS.id]
        when:
        workflowInterceptor.loadFiles(path)
        workflowInterceptor.loadInterceptors()
        workflowInterceptor.executeInterceptors()
        then:
        workflowInterceptor.files.each { file ->
            assert !file.text.contains("criteriaItems") || file.text.contains("<formula>true</formula>")
        }
    }

    def cleanup() {
        new File(TRUNCATED_PATH).deleteDir()
    }
}
