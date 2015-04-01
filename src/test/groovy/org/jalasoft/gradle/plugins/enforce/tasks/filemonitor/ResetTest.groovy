/*
 * Copyright (c) Jalasoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.jalasoft.gradle.plugins.enforce.tasks.filemonitor

import com.twmacinta.util.MD5
import org.gradle.testfixtures.ProjectBuilder
import org.jalasoft.gradle.plugins.enforce.EnforcePlugin
import org.jalasoft.gradle.plugins.enforce.filemonitor.FileMonitorSerializer
import spock.lang.Shared
import spock.lang.Specification
import org.gradle.api.Project

import java.nio.file.Paths

class ResetTest extends Specification {

    @Shared
    Project project

    @Shared
    def instanceReset

    @Shared
    def SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
            "jalasoft", "gradle", "plugins","enforce","tasks", "filemonitor", "resources").toString()

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
            instanceReset.fileMonitorSerializer = new FileMonitorSerializer(pathFileTracker)
            def signature = MD5.asHex(MD5.getHash(new File(class1Path)))
        when:
            instanceReset.runTask()
            fileWriter.write('new change')
            fileWriter.close()
        then:
            instanceReset.fileMonitorSerializer.readMap(pathFileTracker).get(class1Path) == signature
    }

    def cleanupSpec() {
        new File(Paths.get(SRC_PATH).toString()).deleteDir()
    }
}
