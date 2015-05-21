/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.metadata.DeployMetadata
import org.fundacionjala.gradle.plugins.enforce.undeploy.PackageComponent
import org.fundacionjala.gradle.plugins.enforce.undeploy.SmartFilesValidator
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class UndeployTest extends Specification {
    @Shared
    def undeployInstance

    @Shared
    def SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
            "fundacionjala", "gradle", "plugins","enforce","tasks", "salesforce", "resources").toString()
    @Shared
    static final UNDEPLOY_TASK_NAME = "undeploy"

    @Shared
    Project project

    @Shared
    Credential credential

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
        undeployInstance = project.tasks.undeploy
        undeployInstance.fileManager = new ManagementFile(SRC_PATH)
        credential = new Credential()
        credential.id = 'id'
        credential.username = 'salesforce2014.test@gmail.com'
        credential.password = '123qwe2014'
        credential.token = 'UO1Jx5vDQl97xCKkwXBH8tg3T'
        credential.loginFormat = LoginType.DEV.value()
        credential.type = 'normal'
    }

    def "Test should apply plugin and sets extension values"() {
        expect:
        project.tasks.findByName(UNDEPLOY_TASK_NAME) != null
        project.plugins.hasPlugin(EnforcePlugin)
        Task undeployTask = project.tasks.findByName(UNDEPLOY_TASK_NAME)
        undeployTask != null
        project.extensions.findByName('enforce') != null
    }

    def "Test should get the src path assigned"() {
        expect:
        project.extensions.findByName('enforce').srcPath ==
                "${File.separator}home${File.separator}user${File.separator}project${File.separator}one"
    }

    def "Test should get the standard objects assigned"() {
        expect:
        project.extensions.findByName('enforce').standardObjects == ["Q2w_Test__c.object"]
    }

    def "Test should get the tool assigned"() {
        expect:
        project.extensions.findByName('enforce').tool == "metadata"
    }

    def "Test should get components with wildcard"() {
        given:
            def standardComponents = ['Account.object', 'Opportunity.object', 'Contact.object', 'Admin.profile', 'CMC.app']
        when:
            def result = undeployInstance.getComponentsWithWildcard(standardComponents)
        then:
            result == ['**/Account.object', "**/Opportunity.object", "**/Contact.object", "**/Admin.profile", "**/CMC.app"]
    }

    def "Integration test should deploy truncate components"() {
        given:
            undeployInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build').toString())
            def undeployDirectory = Paths.get(SRC_PATH, 'build', 'undeploy').toString()
            undeployInstance.unDeployPackagePath = Paths.get(undeployDirectory,'package.xml').toString()
            undeployInstance.filesToTruncate = [new File(Paths.get(SRC_PATH,'src', 'classes','Class1.cls').toString()),
                                                new File(Paths.get(SRC_PATH,'src', 'classes','Class1.cls-meta.xml').toString())]
            undeployInstance.folderUnDeploy = undeployDirectory
            undeployInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            undeployInstance.createDeploymentDirectory(undeployDirectory)
            undeployInstance.fileManager.copy(undeployInstance.filesToTruncate, undeployDirectory)
            undeployInstance.poll = 200
            undeployInstance.waitTime = 10
            undeployInstance.credential = credential
        when:
            undeployInstance.deployTruncatedComponents()
        then:
            new File(Paths.get(SRC_PATH,'build', 'undeploy', 'package.xml').toString()).exists()
            new File(Paths.get(SRC_PATH,'build', 'undeploy.zip').toString()).exists()
    }

    def "Integration test should deploy To Delete Components"() {
        given:
            undeployInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build').toString())
            def srcpath = Paths.get(SRC_PATH, 'src').toString()
            def undeployDirectory = Paths.get(SRC_PATH, 'build', 'undeploy').toString()
            undeployInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            undeployInstance.componentDeploy = new DeployMetadata()
            undeployInstance.poll = 200
            undeployInstance.waitTime = 10
            undeployInstance.credential = credential
            undeployInstance.executeDeploy(srcpath)
            undeployInstance.unDeployPackagePath = Paths.get(undeployDirectory,'package.xml').toString()
            undeployInstance.folderUnDeploy = undeployDirectory
            undeployInstance.projectPath = srcpath
            undeployInstance.createDeploymentDirectory(undeployDirectory)
            undeployInstance.setupFilesToUnDeploy()
            undeployInstance.smartFilesValidator = new SmartFilesValidator(undeployInstance.getJsonQueries())
            undeployInstance.truncateFiles()
            Files.copy(Paths.get(SRC_PATH, 'src', 'package.xml' ), Paths.get(undeployDirectory, 'package.xml'), StandardCopyOption.REPLACE_EXISTING)
            undeployInstance.packageComponent = new PackageComponent(Paths.get(undeployDirectory,'package.xml').toString())
            def destructiveExpect = "${"<Package xmlns='http://soap.sforce.com/2006/04/metadata'>"}${"<types><members>Class1</members><name>ApexClass</name></types>"}${"<types><members>Object1__c</members><name>CustomObject</name></types>"}${"<types><members>Trigger1</members><name>ApexTrigger</name></types>"}${"<types><members>Account.MyLookupField1__c</members><name>CustomField</name>"}${"</types><version>32.0</version></Package>"}"
            def packageExpect = "${"<?xml version='1.0' encoding='UTF-8'?>"}${"<Package xmlns='http://soap.sforce.com/2006/04/metadata'>"}${"<version>32.0</version></Package>"}"
        when:
            undeployInstance.addNewStandardObjects()
            undeployInstance.createDeploymentDirectory(undeployDirectory)
            undeployInstance.deployToDeleteComponents()
            def destructiveXmlContent =  new File(Paths.get(SRC_PATH, 'build', 'undeploy', 'destructiveChanges.xml').toString()).text
            def packageXmlContent =  new File(Paths.get(SRC_PATH, 'build', 'undeploy', 'package.xml').toString()).text
            XMLUnit.ignoreWhitespace = true
            def destructiveXmlDiff = new Diff(destructiveXmlContent, destructiveExpect)
            def packageXmlDiff = new Diff(packageXmlContent, packageExpect)
        then:
            destructiveXmlDiff.similar()
            packageXmlDiff.similar()
    }

    def "Integration test should undeploy files"() {
        given:
            undeployInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build').toString())
            undeployInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            undeployInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            undeployInstance.unDeployPackagePath = Paths.get(SRC_PATH, 'build', 'undeploy').toString()
            undeployInstance.componentDeploy = new DeployMetadata()
            undeployInstance.poll = 200
            undeployInstance.waitTime = 10
            undeployInstance.credential = credential
            undeployInstance.executeDeploy(Paths.get(SRC_PATH, 'src').toString())
            def destructiveExpect = "${"<Package xmlns='http://soap.sforce.com/2006/04/metadata'>"}${"<types><members>Class1</members><name>ApexClass</name></types>"}${"<types><members>Object1__c</members><name>CustomObject</name></types>"}${"<types><members>Trigger1</members><name>ApexTrigger</name></types>"}${"<types><members>Account.MyLookupField1__c</members><name>CustomField</name>"}${"</types><version>32.0</version></Package>"}"
            def packageExpect = "${"<?xml version='1.0' encoding='UTF-8'?>"}${"<Package xmlns='http://soap.sforce.com/2006/04/metadata'>"}${"<version>32.0</version></Package>"}"
        when:
            undeployInstance.runTask()
            def destructiveXmlContent =  new File(Paths.get(SRC_PATH, 'build', 'undeploy', 'destructiveChanges.xml').toString()).text
            def packageXmlContent =  new File(Paths.get(SRC_PATH, 'build', 'undeploy', 'package.xml').toString()).text
            XMLUnit.ignoreWhitespace = true
            def destructiveXmlDiff = new Diff(destructiveXmlContent, destructiveExpect)
            def packageXmlDiff = new Diff(packageXmlContent, packageExpect)
        then:
            destructiveXmlDiff.similar()
            packageXmlDiff.similar()
    }
    
    def "Test should filter the trigger because the queries said that there isn't any trigger in org" () {
            def unDeployPath = Paths.get(SRC_PATH, 'build', 'undeploy').toString()
            undeployInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build').toString())
            undeployInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            undeployInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            undeployInstance.unDeployPackagePath = unDeployPath
            undeployInstance.credential = credential
        when:
            def jsonString1 = """{"entityTypeName":"ApexClass","records": [{"Name" : "Class1","attributes":{"type":"ApexClass"}},{"Name" : "Class2", "attributes":{"type":"ApexClass"}}]"""
            def jsonString2 = """{"entityTypeName":"ApexTrigger","records": [{"Name" : "Trigger2", "attributes":{"type":"ApexTrigger"}}]"""
            def jsonArrays = new ArrayList<String>()
            jsonArrays.push(jsonString1)
            jsonArrays.push(jsonString2)
            undeployInstance.smartFilesValidator = new SmartFilesValidator(jsonArrays)
            undeployInstance.setupFilesToUnDeploy()
            undeployInstance.truncateFiles()
            File classFile = new File(Paths.get(unDeployPath, 'classes', 'Class1.cls').toString())
            File triggerFile = new File(Paths.get(unDeployPath, 'triggers', 'Trigger1.trigger').toString())
            File objectFile = new File(Paths.get(unDeployPath, 'objects', 'Object1__c.object').toString())
        then:
            classFile.exists()
            !triggerFile.exists()
            objectFile.exists()
    }

    def 'Should get the custom fields from an standard object file'() {
        given:
        def expected = ["Account.MyLookupField1__c", "Account.MyLookupField2__c"]
        def path = Paths.get(SRC_PATH, "objects", "Account.object").toString()
        when:
        def result = undeployInstance.getCustomFields(new File(path))
        then:
        result.size() == 2
        expected == result
    }

    def cleanupSpec() {
        new File(Paths.get(SRC_PATH, 'build').toString()).deleteDir()
    }
}