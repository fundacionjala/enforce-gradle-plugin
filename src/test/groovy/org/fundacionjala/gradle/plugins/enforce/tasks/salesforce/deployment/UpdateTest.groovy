/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentMonitor
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentSerializer
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentStates
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ResultTracker
import org.fundacionjala.gradle.plugins.enforce.metadata.DeployMetadata
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class UpdateTest extends Specification {
    @Shared
    Project project

    @Shared
    def updateInstance

    @Shared
    def SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
            "fundacionjala", "gradle", "plugins","enforce","tasks", "salesforce", "resources").toString()

    @Shared
    Credential credential

    @Shared
    ComponentSerializer componentSerializer

    @Shared
    ComponentMonitor componentMonitor

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        project.enforce.srcPath = SRC_PATH
        updateInstance = project.tasks.update
        updateInstance.fileManager = new ManagementFile(SRC_PATH)
        updateInstance.project.enforce.deleteTemporalFiles = false
        updateInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build').toString())
        updateInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build', 'update').toString())
        updateInstance.projectPath = SRC_PATH
        def fileTrackerPath = Paths.get(SRC_PATH,'.fileTracker.data').toString()
        componentSerializer = new ComponentSerializer(fileTrackerPath)
        componentMonitor = new ComponentMonitor(SRC_PATH)
        def class1 = new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString())

        def class1Cls = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString())
        def class1ClsXml = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls-meta.xml').toString())
        def object1__c = new File(Paths.get(SRC_PATH, 'src', 'objects', 'Object1__c.object').toString())
        def account = new File(Paths.get(SRC_PATH, 'src', 'objects', 'Account.object').toString())
        def trigger = new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger').toString())
        def triggerXml = new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString())

        def mapMock = componentMonitor.getComponentsSignature([class1, class1Cls, class1ClsXml, object1__c, object1__c, account, trigger, triggerXml])
        componentSerializer.save(mapMock)

        credential = new Credential()
        credential.id = 'id'
        credential.username = 'salesforce2014.test@gmail.com'
        credential.password = '123qwe2014'
        credential.token = 'UO1Jx5vDQl97xCKkwXBH8tg3T'
        credential.loginFormat = LoginType.DEV.value()
        credential.type = 'normal'
    }

    def "Test should show files changed" () {
        given:
        updateInstance.packageGenerator.fileTrackerMap = ["two.txt":"New file"]

        when:
        def stdOut = System.out
        def os = new ByteArrayOutputStream()
        System.out = new PrintStream(os)

        updateInstance.showFilesChanged()
        def array = os.toByteArray()
        def is = new ByteArrayInputStream(array)
        System.out = stdOut
        def lineAux = is.readLines()
        then:
        lineAux[0].contains("*********************************************")
        lineAux[1].contains("              Status Files Changed             ")
        lineAux[2].contains("*********************************************")
        lineAux[3].contains("two.txt - New file")
        lineAux[4].contains("*********************************************")
    }

    def "Test should show nothing" () {
        given:
        updateInstance.packageGenerator.fileTrackerMap = [:]
        when:
        def stdOut = System.out
        def os = new ByteArrayOutputStream()
        System.out = new PrintStream(os)
        updateInstance.showFilesChanged()
        def array = os.toByteArray()
        def is = new ByteArrayInputStream(array)
        System.out = stdOut
        def lineAux = is.readLines()
        then:
        lineAux == []
    }

    def "Test should create a package XML file" () {
        given:
            updateInstance.packageGenerator.fileTrackerMap = ['classes/Class1.cls':new ResultTracker(ComponentStates.ADDED)]
            updateInstance.packageGenerator.projectPath = Paths.get(SRC_PATH, 'src').toString()
            updateInstance.pathUpdate = Paths.get(SRC_PATH, 'build', 'update').toString()
            updateInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
        when:
            updateInstance.createPackage()
        then:
            new File(Paths.get(Paths.get(SRC_PATH, 'build', 'update', 'package.xml').toString()).toString()).exists()
    }

    def "Test should create a package XML file empty if status is deleted" () {
        given:
            updateInstance.packageGenerator.fileTrackerMap = ['classes/Class1.cls':new ResultTracker(ComponentStates.ADDED)]
            updateInstance.packageGenerator.projectPath = Paths.get(SRC_PATH, 'src').toString()
            updateInstance.pathUpdate = Paths.get(SRC_PATH, 'build', 'update').toString()
            updateInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
        when:
            updateInstance.createPackage()
        then:
            new File(Paths.get(Paths.get(SRC_PATH, 'build', 'update', 'package.xml').toString()).toString()).exists()
    }

    def "Test should create a package empty" () {
        given:
            updateInstance.packageGenerator.fileTrackerMap = [:]
            updateInstance.pathUpdate = Paths.get(SRC_PATH, 'build', 'update').toString()
            updateInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
        when:
            updateInstance.createPackage()
        then:
            new File(Paths.get(Paths.get(SRC_PATH, 'build', 'update', 'package.xml').toString()).toString()).exists()
    }

    def "Test should create a destructive XML file" () {
        given:
            updateInstance.packageGenerator.fileTrackerMap = ['classes/Class1.cls':new ResultTracker(ComponentStates.DELETED)]
            updateInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            updateInstance.packageGenerator.projectPath = Paths.get(SRC_PATH, 'src').toString()
            updateInstance.pathUpdate = Paths.get(SRC_PATH, 'build', 'update').toString()
            updateInstance.credential = credential
            updateInstance.packageGenerator.credential = credential
        when:
            updateInstance.createDestructive()
        then:
            new File(Paths.get(Paths.get(SRC_PATH, 'build', 'update', 'destructiveChanges.xml').toString()).toString()).exists()
    }

    def "Test should load new file" () {
        given:
            updateInstance.projectPath = SRC_PATH
            updateInstance.packageGenerator.componentMonitor = new ComponentMonitor(SRC_PATH)
            String newRelativeFilePath = Paths.get('classes', 'Class2.cls').toString()
            String newFilePath = Paths.get(SRC_PATH, newRelativeFilePath).toString()
            FileWriter newFile = new FileWriter(newFilePath)
            newFile.write('test')
            newFile.close()
            updateInstance.credential = credential
        when:
            updateInstance.loadFilesChanged()
        then:
            updateInstance.packageGenerator.fileTrackerMap.containsKey(newRelativeFilePath)
            updateInstance.packageGenerator.fileTrackerMap.get(newRelativeFilePath).state == ComponentStates.ADDED

    }

    def "Test should copy changed files" () {
        given:
            updateInstance.filesToCopy = [new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString()),
                                          new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString())]
            updateInstance.pathUpdate = Paths.get(SRC_PATH, 'build').toString()
        when:
            updateInstance.copyFilesChanged()
        then:
            new File(Paths.get(SRC_PATH, 'build', 'classes', 'class1.cls').toString()).exists()
            new File(Paths.get(SRC_PATH, 'build', 'classes', 'class1.cls-meta.xml').toString()).exists()
    }

    def "Integration test should show a message if there are not changes"() {
        given:
            updateInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            updateInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            updateInstance.packageGenerator.fileTrackerMap = [:]
            updateInstance.componentDeploy = new DeployMetadata()
            updateInstance.poll = 200
            updateInstance.waitTime = 10
            updateInstance.credential = credential
        when:
            def stdOut = System.out
            def os = new ByteArrayOutputStream()
            System.out = new PrintStream(os)
            updateInstance.runTask()
            def array = os.toByteArray()
            def is = new ByteArrayInputStream(array)
            System.out = stdOut
            def lineAux = is.readLines()
        then:
            lineAux[lineAux.size() - 1].toString().contains("There are not files changed")
    }

    def "Integration test should update (New file)"() {
        given:
            updateInstance.packageGenerator.projectPath = Paths.get(SRC_PATH, 'src').toString()
            updateInstance.packageGenerator.fileTrackerMap = [:]
            def class1Cls = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString())
            def object1__c = new File(Paths.get(SRC_PATH, 'src', 'objects', 'Object1__c.object').toString())
            def account = new File(Paths.get(SRC_PATH, 'src', 'objects', 'Account.object').toString())
            def trigger = new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger').toString())
            componentMonitor.srcProject = Paths.get(SRC_PATH,'src').toString()
            componentSerializer.sourcePath = Paths.get(SRC_PATH,'src','.fileTracker.data').toString()
            def mapMock = componentMonitor.getComponentsSignature([class1Cls, object1__c, account, trigger])
            componentSerializer.save(mapMock)
            updateInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            updateInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            def newFilePath = Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls').toString()
            def newXmlFilePath = Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls-meta.xml').toString()
            FileWriter newFile = new FileWriter(newFilePath)
            FileWriter newXmlFile = new FileWriter(newXmlFilePath)
            def class2Content = "public with sharing class Class2 {public Class2(Integer a, Integer b){ }}"
            newFile.write(class2Content)
            def class2XmlContent = "${"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"}${"<ApexClass xmlns=\"http://soap.sforce.com/2006/04/metadata\">"}${"<apiVersion>24.0</apiVersion><status>Active</status></ApexClass>"}"
            newXmlFile.write(class2XmlContent)
            newFile.close()
            newXmlFile.close()
            updateInstance.componentDeploy = new DeployMetadata()
            updateInstance.poll = 200
            updateInstance.waitTime = 10
            updateInstance.credential = credential
            def packageExpect = "${"<?xml version='1.0' encoding='UTF-8'?>"}${"<Package xmlns='http://soap.sforce.com/2006/04/metadata'>"}${"<types><members>Class2</members><name>ApexClass</name></types><version>32.0</version></Package>"}"
            def destructiveExpect = "${"<?xml version='1.0' encoding='UTF-8'?>"}${"<Package xmlns='http://soap.sforce.com/2006/04/metadata'>"}${"<version>32.0</version>"}${"</Package>"}"
        when:
            updateInstance.runTask()
            println 'package ' + new File(Paths.get(SRC_PATH, 'build', 'update', 'package.xml').toString()).exists()
            def packageXml =  new File(Paths.get(SRC_PATH, 'build', 'update', 'package.xml').toString()).text
            def destructiveXml =  new File(Paths.get(SRC_PATH, 'build', 'update', 'destructiveChanges.xml').toString()).text
            def class2Xml =  new File(Paths.get(SRC_PATH, 'build', 'update', 'classes', 'Class2.cls-meta.xml').toString()).text
            XMLUnit.ignoreWhitespace = true
            def packageXmlDifference = new Diff(packageXml, packageExpect)
            def destructiveXmlDifference = new Diff(destructiveXml, destructiveExpect)
            def classXmlDifference = new Diff(class2Xml, class2XmlContent)
        then:
            packageXmlDifference.similar()
            destructiveXmlDifference.similar()
            classXmlDifference.similar()
            class2Content == new File(Paths.get(SRC_PATH, 'build', 'update', 'classes', 'Class2.cls').toString()).text
    }

    def "Integration testing must update the organization and delete temporary files generated"() {
        given:
            updateInstance.packageGenerator.fileTrackerMap = [:]
            updateInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            updateInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            updateInstance.componentDeploy = new DeployMetadata()
            updateInstance.poll = 200
            updateInstance.waitTime = 10
            updateInstance.credential = credential
            updateInstance.project.enforce.deleteTemporalFiles = true
            componentMonitor.srcProject = Paths.get(SRC_PATH,'src').toString()
            componentSerializer.sourcePath = Paths.get(SRC_PATH,'src','.fileTracker.data').toString()
            componentSerializer.save(componentMonitor.getComponentsSignature([]))
            def newTemporalClassPath = Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls').toString()
            def newTemporalXmlPath = Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls-meta.xml').toString()
            FileWriter writerClass = new FileWriter(newTemporalClassPath)
            FileWriter writerXml   = new FileWriter(newTemporalXmlPath)
            def contentTemporalClass = "public with sharing class Class2 {public Class2(Integer a, Integer b){ }}"
            def contentTemporalXml = "${"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"}${"<ApexClass xmlns=\"http://soap.sforce.com/2006/04/metadata\">"}${"<apiVersion>24.0</apiVersion><status>Active</status></ApexClass>"}"
            writerClass.write(contentTemporalClass)
            writerXml.write(contentTemporalXml)
            writerClass.close()
            writerXml.close()
            def updateFileZipPath = Paths.get(SRC_PATH,'build','update.zip').toString()
            def updateFolderPath = Paths.get(SRC_PATH,'build','update').toString()
            File updateFileZip = new File(updateFileZipPath)
            File updateFolder = new File(updateFolderPath)
        when:
            updateInstance.runTask()
        then:
            !updateFileZip.exists()
            !updateFolder.exists()
    }

    def cleanupSpec() {
        new File(Paths.get(SRC_PATH, 'build').toString()).deleteDir()
        new File(Paths.get(SRC_PATH, 'classes', 'Class2.cls').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls-meta.xml').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', '.fileTracker.data').toString()).delete()
    }
}