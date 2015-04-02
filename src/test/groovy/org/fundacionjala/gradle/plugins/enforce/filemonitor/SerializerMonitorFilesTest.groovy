/*
 * Copyright (c) Fundación Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.filemonitor

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths


class SerializerMonitorFilesTest extends Specification {

    @Shared
    def SRC_PATH = System.getProperty("user.dir") + "/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/filemonitor/resources"

    @Shared
    File file1

    @Shared
    File file2

    @Shared
    private Map mapFileChanged

    @Shared
    FileMonitorSerializer serializerMonitorFiles

    @Shared
    ArrayList<File> arrayFiles

    @Shared
    String relativePath = "./resources"

    @Shared
    String NAME_FILE_TRACKER = "./file.data"

    def setup() {
        serializerMonitorFiles = Spy(FileMonitorSerializer, constructorArgs: [NAME_FILE_TRACKER])
        arrayFiles = new ArrayList<File>()
        file1 = new File(Paths.get(SRC_PATH, "/one.txt").toString())
        file2 = new File(Paths.get(SRC_PATH, "/two.txt").toString())

        mapFileChanged = new HashMap()
        mapFileChanged.put(Paths.get(relativePath, "/one.txt").toString(), "123dasd")
        mapFileChanged.put(Paths.get(relativePath, "/two.txt").toString(), "059e0d992ee883bd390612888db75b03")
    }

    def "Test set a project path"() {
        given:
            def sourceProject = "src"
            def fileName = '.fileTracker.data'
        when:
            serializerMonitorFiles.setSrcProject(sourceProject)
        then:
            serializerMonitorFiles.srcProject == sourceProject
            serializerMonitorFiles.nameFile == Paths.get(sourceProject, fileName).toString()
    }

    def "Test using no parameters constructor"() {

        when:
            FileMonitorSerializer fileMonitorSerializer = new FileMonitorSerializer()
        then:
            fileMonitorSerializer instanceof FileMonitorSerializer
            fileMonitorSerializer instanceof TemplateFileMonitor
    }

    def "Test load Signature for files in directory"() {
        given:
            Map signatureFileMap
        when:
            arrayFiles.push(file1)
            arrayFiles.push(file2)
            signatureFileMap = serializerMonitorFiles.loadSignatureForFilesInDirectory(arrayFiles)
        then:
        2 == signatureFileMap.size()
        signatureFileMap.get(Paths.get(relativePath, "/one.txt").toString()) == "87ee732d831690f45b8606b1547bd09e"
        signatureFileMap.get(Paths.get(relativePath, "/two.txt").toString()) == "059e0d992ee883bd390612888db75b03"
    }

    def "Test get file changed"() {
        given:
            Map<String, String> MapMock = new HashMap()
            MapMock.put(Paths.get(relativePath, "/one.txt").toString(), "Changed file")
            arrayFiles.push(file1)
            arrayFiles.push(file2)
        when:
            def mapResult = serializerMonitorFiles.getFileChangedExclude(arrayFiles)
        then:
            1 * serializerMonitorFiles.readMap(NAME_FILE_TRACKER) >> mapFileChanged
            MapMock == mapResult
    }

    def "Test save and read map "() {
        given:
            def path = System.getProperty("user.dir") + "/src/file.data"
            serializerMonitorFiles.setNameFile(path)
            File fileMap = new File(path)
            arrayFiles.push(file1)
            arrayFiles.push(file2)
            Map mapLoader = serializerMonitorFiles.loadSignatureForFilesInDirectory(arrayFiles)
            Map recoveryMap
        when:
            serializerMonitorFiles.saveMap(mapLoader)
            recoveryMap = serializerMonitorFiles.readMap(path)
        then:
            serializerMonitorFiles.verifyFileMap()
            recoveryMap == mapLoader
            fileMap.delete()
    }

    def "Test state delete"() {
        given:
            def state
        when:
            state = "Deleted file"
        then:
            state == serializerMonitorFiles.getStateDelete()
    }

    def "Test get relative path"() {
        given:
            File fileRelativePath = new File("user/project/src/class/class1")
        when:
            def relativePath = serializerMonitorFiles.getPathRelative(fileRelativePath)
        then:
            relativePath == Paths.get(".", "class", "class1").toString()
    }

    def "Test new File"() {
        given:
            Map mapFilePros = ["src/class/class1.cls": "123456789"]
            Map mapCurrent = ["src/class/class1.cls": "123456789", "src/class/class2.cls": "1234567890"]
        when:
            serializerMonitorFiles.findNewFiles(mapFilePros, mapCurrent)
        then:
            serializerMonitorFiles.mapFilesChanged == ["src/class/class2.cls": "New file"]
    }

    def "Test new File not exits"() {
        given:
            Map mapFilePros = ["src/class/class1.cls": "123456789"]
            Map mapCurrent = [:]
        when:
            serializerMonitorFiles.findNewFiles(mapFilePros, mapCurrent)
        then:
            serializerMonitorFiles.mapFilesChanged == [:]
    }

    def "Test changed File"() {
        given:
            Map mapFilePros = ["src/class/class1.cls": "123456789"]
            Map mapCurrent = ["src/class/class1.cls": "1234567890"]
        when:
            serializerMonitorFiles.findChangedFiles(mapFilePros, mapCurrent)
        then:
            serializerMonitorFiles.mapFilesChanged == ["src/class/class1.cls": "Changed file"]
    }

    def "Test deleted File"() {
        given:
            Map mapFilePros = ["src/class/class1.cls": "123456789", "src/class/class2.cls": "1234567890"]
            Map mapCurrent = ["src/class/class1.cls": "123456789"]
        when:
            serializerMonitorFiles.findDeleteFiles(mapFilePros, mapCurrent)
        then:
            serializerMonitorFiles.mapFilesChanged == ["src/class/class2.cls": "Deleted file"]
    }

    def "Test verify exist map"() {
        when:
            serializerMonitorFiles.setNameFile(SRC_PATH + "/file.data")
        then:
            true == serializerMonitorFiles.verifyFileMap()
    }

    def "Test verify not exist map "() {
        when:
            serializerMonitorFiles.setNameFile(SRC_PATH + "/file1.data")
        then:
            false == serializerMonitorFiles.verifyFileMap()
    }

    def "Test select files changed according folders"() {
        given:
            def arrayFolders = ["classes"]
            def mapFilesChanged = ["classes/class1.cls": "file Changed", "triggers/trigger1.trigger": "file Changed"]
        when:
            def mapResult = serializerMonitorFiles.getFoldersFiltered(arrayFolders, mapFilesChanged)
        then:
         mapResult == ["classes/class1.cls": "file Changed"]
    }

    def "Test should update the map to save it "() {
        given:
            Map recoveryMap = ["class1.cls": "123", "class2.cls": "345", "class3.cls": "789"]
            serializerMonitorFiles.setRecoveryFileHashCode(recoveryMap)
            Map currentMap = ["class1.cls": "123", "class2.cls": "999"]
            serializerMonitorFiles.setCurrentFileHashCode(currentMap)
            Map mapFileChanged = ["class1.cls": "123", "class2.cls": "file_changed", "class3.cls": serializerMonitorFiles.getStateDelete()]
        when:
            serializerMonitorFiles.saveMapUpdated(mapFileChanged)
        then:
            serializerMonitorFiles.recoveryFileHashCode == ["class1.cls": "123", "class2.cls": "999"]
    }
}
