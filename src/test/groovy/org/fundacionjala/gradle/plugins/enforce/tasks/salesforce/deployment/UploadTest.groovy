/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentStates
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ResultTracker
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class UploadTest extends Specification {
    @Shared
    def uploadInstance

    @Shared
    Util util = Spy(Util)

    @Shared
    Project project

    @Shared
    def SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
            "fundacionjala", "gradle", "plugins", "enforce",
            "tasks", "salesforce", "resources").toString()

    @Shared
    Credential credential

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        project.enforce.srcPath = SRC_PATH
        uploadInstance = project.tasks.upload
        uploadInstance.project.enforce.deleteTemporalFiles = false
        uploadInstance.fileManager = new ManagementFile(SRC_PATH)
        uploadInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build').toString())
        credential = new Credential()
        credential.id = 'id'
        credential.username = 'salesforce2014.test@gmail.com'
        credential.password = '123qwe2014'
        credential.token = 'UO1Jx5vDQl97xCKkwXBH8tg3T'
        credential.loginFormat = LoginType.DEV.value()
        credential.type = 'normal'
    }

    def "Test shouldn't create a package xml file if specificFilesToUpload Array is empty "() {
        given:
            uploadInstance.pathUpload = Paths.get(SRC_PATH, 'build').toString()
            uploadInstance.specificFilesToUpload = []
        when:
            uploadInstance.createPackage()
        then:
            !new File(Paths.get(SRC_PATH, 'build', 'package.xml').toString()).exists()
    }

    def "Test should create a package xml file"() {
        given:
            uploadInstance.pathUpload = Paths.get(SRC_PATH, 'build').toString()
            uploadInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            uploadInstance.specificFilesToUpload = [new File(Paths.get(SRC_PATH, "src", "classes", "Class1.cls").toString()),
                                                    new File(Paths.get(SRC_PATH, "src", "classes", "Class1.cls-meta.xml").toString()),
                                                    new File(Paths.get(SRC_PATH, "src", "objects", "Object1__c.object").toString()),
                                                    new File(Paths.get(SRC_PATH, "src", "objects", "Account.object").toString())]
        when:
            uploadInstance.createPackage()
        then:
            new File(Paths.get(SRC_PATH, 'build', 'package.xml').toString()).exists()
    }

    def "Test should copy files to upload"() {
        given:
            uploadInstance.pathUpload = Paths.get(SRC_PATH, 'build').toString()
            uploadInstance.specificFilesToUpload = [new File(Paths.get(SRC_PATH, "src", "classes", "Class1.cls").toString()),
                                                    new File(Paths.get(SRC_PATH, "src", "classes", "Class1.cls-meta.xml").toString()),
                                                    new File(Paths.get(SRC_PATH, "src", "objects", "Object1__c.object").toString())]
            uploadInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
        when:
            uploadInstance.copyFilesToUpload()
        then:
            new File(Paths.get(SRC_PATH, 'build', "classes", "Class1.cls").toString()).exists()
            new File(Paths.get(SRC_PATH, 'build', "classes", "Class1.cls-meta.xml").toString()).exists()
            new File(Paths.get(SRC_PATH, 'build', "objects", "Object1__c.object").toString()).exists()
    }

    def "Test should return a map with changed files"() {
        given:
            def changedFilePath = Paths.get(SRC_PATH, "classes", "class1.cls").toString()
            uploadInstance.specificFilesToUpload = [new File(changedFilePath),
                                                    new File(Paths.get(SRC_PATH, "classes", "class1.cls-meta.xml").toString()),
                                                    new File(Paths.get(SRC_PATH, "objects", "object1.object").toString())]
            uploadInstance.packageGenerator.fileTrackerMap = [:]
            uploadInstance.packageGenerator.fileTrackerMap.put(changedFilePath, new ResultTracker(ComponentStates.ADDED))
            def filesChangedExpect = [:]
            filesChangedExpect.put(changedFilePath, new ResultTracker(ComponentStates.ADDED))
        when:
            def filesChangedToUpload = uploadInstance.filterMapFilesChanged()
        then:
            filesChangedToUpload.containsKey(changedFilePath)
            filesChangedExpect.containsKey(changedFilePath)
            filesChangedToUpload.get(changedFilePath).state == filesChangedExpect.get(changedFilePath).state
    }

    def "Test should return a empty Map if there isn't changed file"() {
        given:
            def changedFilePath = Paths.get(SRC_PATH, "classes", "class2.cls").toString()
            uploadInstance.specificFilesToUpload = [new File(Paths.get(SRC_PATH, "classes", "class1.cls").toString()),
                                                    new File(Paths.get(SRC_PATH, "classes", "class1.cls-meta.xml").toString()),
                                                    new File(Paths.get(SRC_PATH, "objects", "object1.object").toString())]
            uploadInstance.packageGenerator.fileTrackerMap = [:]
            uploadInstance.packageGenerator.fileTrackerMap.put(changedFilePath, "")
        when:
            def filesChangedToUpload = uploadInstance.filterMapFilesChanged()
        then:
            filesChangedToUpload == [:]
    }

    def "Test should return an exception if there is a invalid folder"() {
        given:
            def foldersName = ['classes', 'invalid']
        when:
            uploadInstance.validateFolders(foldersName)
        then:
            thrown(Exception)
    }

    def "Test should return an exception if there is a invalid file"() {
        given:
            def filesName = ["classes${File.separator}Class1.txy", "triggers${File.separator}TriggerOne.trigger"]
        when:
            uploadInstance.validateFiles(filesName)
        then:
            thrown(Exception)
    }

    def "Test should fill specificFilesToUpload with files sent"() {
        given:
            uploadInstance.files = "classes${File.separator}class1.cls,objects${File.separator}Object1__c.object"
            uploadInstance.projectPath = SRC_PATH
        when:
            uploadInstance.loadParameter()
        then:
            def classFile = new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString())
            def classFileXml = new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString())
            def objectFile = new File(Paths.get(SRC_PATH, 'objects', 'Object1__c.object').toString())
            uploadInstance.specificFilesToUpload.sort() == [classFile, objectFile, classFileXml].sort()
    }

    def "Test should fill specificFilesToUpload with folders sent"() {
        given:
            uploadInstance.files = 'classes,triggers'
            uploadInstance.projectPath = SRC_PATH
        when:
            uploadInstance.loadParameter()
        then:
            def classFile = new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString())
            def classFileXml = new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString())
            def triggerFile = new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger').toString())
            def triggerFileXml = new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger-meta.xml').toString())
            uploadInstance.specificFilesToUpload.sort() == [classFile, triggerFile, classFileXml, triggerFileXml].sort()
    }

    def "Test should fill specificFilesToUpload with folders and files sent"() {
        given:
            uploadInstance.files = "classes,triggers${File.separator}LunesTrigger.trigger"
            uploadInstance.projectPath = SRC_PATH
        when:
            uploadInstance.loadParameter()
        then:
            def classFile = new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString())
            def classFileXml = new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString())
            def triggerFile = new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger').toString())
            def triggerFileXml = new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger-meta.xml').toString())
            uploadInstance.specificFilesToUpload.sort() == [classFile, triggerFile, classFileXml, triggerFileXml].sort()
    }

    def "Test should fill specificFilesToUpload array following a wildcard sent '*/class1.cls,objects/Object1__c.object' "() {
        given:
            uploadInstance.files = "*${File.separator}class1.cls,objects${File.separator}Object1__c.object"
            uploadInstance.projectPath = SRC_PATH
        when:
            uploadInstance.loadParameter()
        then:
            def classFile = new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString())
            def classFileXml = new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString())
            def objectFile = new File(Paths.get(SRC_PATH, 'objects', 'Object1__c.object').toString())
            uploadInstance.specificFilesToUpload.sort() == [classFile, objectFile, classFileXml].sort()
    }

    def "Test should fill specificFilesToUpload array following a wildcard sent 'objects/*.object' "() {
        given:
            uploadInstance.files = "objects${File.separator}*.object"
            uploadInstance.projectPath = SRC_PATH
            new File(Paths.get(SRC_PATH, 'objects', 'object1.object').toString())
        when:
            uploadInstance.loadParameter()
        then:
            uploadInstance.specificFilesToUpload.sort() == [new File(Paths.get(SRC_PATH, 'objects', 'Account.object').toString()),
                                                            new File(Paths.get(SRC_PATH, 'objects', 'Object1__c.object').toString()),
                                                            new File(Paths.get(SRC_PATH, 'objects', 'Object2__c.object').toString())].sort()
    }

    def "Test should fill specificFilesToUpload array following a wildcard sent '**/*Account*/**' "() {
        given:
            uploadInstance.files = "**${File.separator}*Account*${File.separator}**"
            uploadInstance.projectPath = SRC_PATH
        when:
            uploadInstance.loadParameter()
        then:
            uploadInstance.specificFilesToUpload.sort() == [new File(Paths.get(SRC_PATH, 'objects', 'Account.object').toString()),
                                                        new File(Paths.get(SRC_PATH, 'src', 'objects', 'Account.object').toString())].sort()
    }

    def "Test should fill specificFilesToUpload array following a wildcard sent '**/*.cls' "() {
        given:
            uploadInstance.files = "**${File.separator}*.cls"
            uploadInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
        when:
            uploadInstance.loadParameter()
        then:
            uploadInstance.specificFilesToUpload.sort() == [new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString()),
                                                        new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls-meta.xml').toString())].sort()
    }

    def cleanupSpec() {
        new File(Paths.get(SRC_PATH, 'classes', 'class2.cls').toString()).delete()
        new File(Paths.get(SRC_PATH, 'build').toString()).deleteDir()
    }
}
