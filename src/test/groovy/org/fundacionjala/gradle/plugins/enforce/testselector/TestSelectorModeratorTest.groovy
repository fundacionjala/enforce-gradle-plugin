/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.testselector

import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.unittest.RunTestTaskConstants
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.ToolingAPI
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class TestSelectorModeratorTest extends Specification {
    @Shared
    def SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala",
            "gradle", "plugins","enforce", "tasks", "salesforce", "resources", "test").toString()
    Project project


    def classNames = []

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        project.enforce {
            srcPath = "${SRC_PATH}"
            tool = "metadata"
            poll = 200
            waitTime = 10
        }
    }

    def "Should get all the test class names"() {
        given:
            project.enforce {
                srcPath = SRC_PATH
                tool = "metadata"
                poll = 200
                waitTime = 10
            }
        when:
            TestSelectorModerator moderator = new TestSelectorModerator(project, null, SRC_PATH)
            classNames = moderator.getTestClassNames()
        then:
            classNames.size() == 2
    }

    def "Should get all the test class names according wildcard"() {
      given:
            project.enforce {
                srcPath = SRC_PATH
                tool = "metadata"
                poll = 200
                waitTime = 10
            }
        when:
            project.ext[RunTestTaskConstants.CLASS_PARAM] = "*Test*"
            TestSelectorModerator moderator = new TestSelectorModerator(project, null, SRC_PATH)
            classNames = moderator.getTestClassNames()
        then:
            classNames.size() == 2
    }

    def "Should get all the test class names according test class name"() {
      given:
            project.enforce {
                srcPath = SRC_PATH
                tool = "metadata"
                poll = 200
                waitTime = 10
            }
        when:
            project.ext[RunTestTaskConstants.CLASS_PARAM] = "FGW_APIFactoryTest.cls"
            TestSelectorModerator moderator = new TestSelectorModerator(project, null, SRC_PATH)
            classNames = moderator.getTestClassNames()
        then:
            classNames.size() == 1
    }

    def "Should get the test class names related to a class"() {
        given:
        ArtifactGeneratorMock artifactGenerator = new ArtifactGeneratorMock()
        project.enforce {
            srcPath = SRC_PATH
            tool = "metadata"
            poll = 200
            waitTime = 10
        }
        when:
        project.ext[RunTestTaskConstants.FILE_PARAM] = "Class1.cls"
        TestSelectorModerator moderator = new TestSelectorModerator(project, artifactGenerator, SRC_PATH)
        classNames = moderator.getTestClassNames()
        then:
        classNames.size() == 1
    }

    def cleanupSpec() {
    }
}
