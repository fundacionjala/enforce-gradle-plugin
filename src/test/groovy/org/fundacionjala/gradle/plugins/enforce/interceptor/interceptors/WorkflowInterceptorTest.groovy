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
    String RESOURCE_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources"

    def "Should create gets workflows from source path"(){
        given:
            WorkflowInterceptor workflowInterceptor = new WorkflowInterceptor()
            String path = Paths.get(RESOURCE_PATH, 'workflows').toString()
        when:
            workflowInterceptor.loadFiles(path)
        then:
            workflowInterceptor.files.size() == 2

    }
}
