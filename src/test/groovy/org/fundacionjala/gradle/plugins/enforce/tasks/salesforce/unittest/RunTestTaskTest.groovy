/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.unittest

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths


class RunTestTaskTest extends Specification {
    static final RUN_TEST_TASK_NAME = "runTest"
    @Shared
    def SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala",
            "gradle", "plugins","enforce", "tasks", "salesforce", "resources").toString()
    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        project.enforce {
            srcPath = "${File.separator}home${File.separator}user${File.separator}project${File.separator}one"
            standardObjects = ["Q2w_Test__c.object"]
            tool = "metadata"
            poll = 200
            waitTime = 10
        }
    }

    def "Applies plugin and sets extension values"() {
        expect:
        project.tasks.findByName(RUN_TEST_TASK_NAME) != null
        project.plugins.hasPlugin(EnforcePlugin)
        Task runTestTask = project.tasks.findByName(RUN_TEST_TASK_NAME)
        runTestTask != null
        project.extensions.findByName('enforce') != null
    }

    def "Should get the src path assigned"() {
        expect:
        project.extensions.findByName('enforce').srcPath ==
                "${File.separator}home${File.separator}user${File.separator}project${File.separator}one"
    }

    def "Should get the standard objects assigned"() {
        expect:
        project.extensions.findByName('enforce').standardObjects == ["Q2w_Test__c.object"]
    }

    def "Should get the tool assigned"() {
        expect:
        project.extensions.findByName('enforce').tool == "metadata"
    }

    def "Should get the class names from a wildcard"() {
        given:
        project.enforce {
            srcPath = SRC_PATH
            standardObjects = ["Q2w_Test__c.object"]
            tool = "metadata"
            poll = 200
            waitTime = 10
        }
        def classNames = []
        when:
        RunTestTask runTestTask = project.tasks.findByName(RUN_TEST_TASK_NAME) as RunTestTask
        def path = Paths.get(SRC_PATH, "test").toString()
        classNames = runTestTask.getClassNames(path, "*Test*")
        then:
        classNames.sort() == ["FGW_Console_CTRLTest", "FGW_APIFactoryTest"].sort()
    }
}
