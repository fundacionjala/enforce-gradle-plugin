/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.testselector

import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.unittest.RunTestTaskConstants
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.runtesttask.CustomComponentTracker
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class TestSelectorModeratorTest extends Specification {
    @Shared
    def SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala",
            "gradle", "plugins","enforce", "tasks", "salesforce", "resources").toString()
    @Shared
    def SRC_CLASSES_PATH = Paths.get(SRC_PATH, "test").toString()

    Project project
    def classNames = []

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
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
            TestSelectorModerator moderator = new TestSelectorModerator(project, null, SRC_CLASSES_PATH, true)
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
            TestSelectorModerator moderator = new TestSelectorModerator(project, null, SRC_CLASSES_PATH, false)
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
            TestSelectorModerator moderator = new TestSelectorModerator(project, null, SRC_CLASSES_PATH, false)
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
        TestSelectorModerator moderator = new TestSelectorModerator(project, artifactGenerator, SRC_CLASSES_PATH, false)
        classNames = moderator.getTestClassNames()
        then:
        classNames.size() == 1
    }

    def "Should get the test class names related to a class from last updated classes, no changes"() {
        given:
        ArtifactGeneratorMock artifactGenerator = new ArtifactGeneratorMock()
        project.enforce {
            srcPath = SRC_PATH
            tool = "metadata"
            poll = 200
            waitTime = 10
        }
        when:
        project.ext[RunTestTaskConstants.FILE_PARAM] = "*"
        TestSelectorModerator moderator = new TestSelectorModerator(project, artifactGenerator, SRC_CLASSES_PATH, false)
        classNames = moderator.getTestClassNames()
        then:
        classNames.size() == 0
    }

    def "Should get the test class names related to a class from last updated classes, add a new class"() {
        given:
        ArtifactGeneratorMock artifactGenerator = new ArtifactGeneratorMock()
        project.enforce {
            srcPath = SRC_PATH
            tool = "metadata"
            poll = 200
            waitTime = 10
        }
        CustomComponentTracker customComponentTracker = new CustomComponentTracker(SRC_PATH)
        File classFile = new File(Paths.get(SRC_PATH, 'classes', 'Class1.cls').toString())
        classFile.write("some text")
        when:
        project.ext[RunTestTaskConstants.FILE_PARAM] = RunTestTaskConstants.RUN_ALL_UPDATED_PARAM_VALUE
        TestSelectorModerator moderator = new TestSelectorModerator(project, artifactGenerator, SRC_CLASSES_PATH, false)
        classNames = moderator.getTestClassNames()
        then:
        classNames.size() == 1
    }

    def cleanupSpec() {
        new File(Paths.get(SRC_PATH, '.customComponentTracker.data').toString()).delete()
        new File(Paths.get(SRC_PATH, 'classes', 'Class1.cls').toString()).delete()
    }
}
