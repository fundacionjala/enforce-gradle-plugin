/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.filemonitor

import com.twmacinta.util.MD5
import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentMonitor
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class ResetTest extends Specification {

    @Shared
    Project project

    @Shared
    def instanceReset

    @Shared
    def SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
            "fundacionjala", "gradle", "plugins","enforce","tasks", "filemonitor", "resources").toString()

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        new File(SRC_PATH).mkdir()
        project.enforce.srcPath = SRC_PATH
        instanceReset = project.tasks.reset
    }

    def "Test should reset fileTracker" () {
        given:
            new File(Paths.get(SRC_PATH, 'src').toString()).mkdir()
            new File(Paths.get(SRC_PATH, 'src', 'classes').toString()).mkdir()
            def class1Path = Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString()
            FileWriter fileWriter = new FileWriter(new File(class1Path))
            fileWriter.write('test')
            def pathFileTracker = Paths.get(SRC_PATH, 'src', '.fileTracker.data').toString()
            new File(pathFileTracker).createNewFile()
            instanceReset.fileArray = [new File(class1Path)]
            instanceReset.componentMonitor = new ComponentMonitor(Paths.get(SRC_PATH, 'src').toString())
            def signature = MD5.asHex(MD5.getHash(new File(class1Path)))
        when:
            instanceReset.runTask()
            fileWriter.write('new change')
            fileWriter.close()
        then:
            instanceReset.componentMonitor.componentSerializer.read().get(class1Path).hash == signature
    }

    def cleanupSpec() {
       new File(Paths.get(SRC_PATH).toString()).deleteDir()

    }
}
