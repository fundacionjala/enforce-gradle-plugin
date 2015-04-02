/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

import org.gradle.api.Project
import spock.lang.Shared
import spock.lang.Specification

class WorkflowTest extends Specification {
    @Shared
    String ROOT_PATH = System.properties['user.dir']

    @Shared
    String RESOURCE_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/workflows"

    @Shared
    String TRUNCATED_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/interceptor"

    @Shared
    Project project

    @Shared
    Workflow workflowContent

    def setupSpec() {
        new AntBuilder().copy(todir: TRUNCATED_PATH) {
            fileset(dir: RESOURCE_PATH) {
            }
        }
        workflowContent = new Workflow()
    }

    def "Should truncate a workflow with criteriaItems and gets a content with a formula equals to true"() {
        setup:
        File file = new File("${TRUNCATED_PATH}/CriteriaItem.workflow")
        when:
        workflowContent.execute(file)
        then:
        !file.text.contains('<criteriaItems>')
        file.text.contains('<formula>true</formula>')
    }

    def "Should truncate a workflow with criteriaItems and formula"() {
        setup:
        File file = new File("${TRUNCATED_PATH}/CriteriaItemFormula.workflow")
        when:
        workflowContent.execute(file)
        then:
        !file.text.contains('<formula>false</formula>')
        !file.text.contains('<criteriaItems>')
        file.text.contains('<formula>true</formula>')

    }


    def cleanupSpec() {
        new File(TRUNCATED_PATH).deleteDir()
    }
}
