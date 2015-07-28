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
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.OrgValidator
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

    @Shared
    String destructiveExpect

    @Shared
    String packageExpect

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
        undeployInstance.project.enforce.deleteTemporaryFiles = false
        undeployInstance.taskFolderName = "undeploy"

        credential = new Credential()
        credential.id = 'id'
        credential.username = 'salesforce2014.test@gmail.com'
        credential.password = '123qwe2014'
        credential.token = 'UO1Jx5vDQl97xCKkwXBH8tg3T'
        credential.loginFormat = LoginType.DEV.value()
        credential.type = 'normal'

        destructiveExpect = '''<?xml version='1.0' encoding='UTF-8'?>
                                            <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                                <types>
                                                    <members>Class1</members>
                                                    <name>ApexClass</name>
                                                </types>
                                                <types>
                                                    <members>Object1__c</members>
                                                    <name>CustomObject</name>
                                                </types>
                                                <types>
                                                    <members>Trigger1</members>
                                                    <name>ApexTrigger</name>
                                                </types>
                                                <version>32.0</version>
                                            </Package>
                                        '''
        packageExpect =  '''<?xml version='1.0' encoding='UTF-8'?>
                                            <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                                <version>32.0</version>
                                            </Package>
                                        '''
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

    def "Test should create a package xml file into build directory to truncate components"() {
        given:
            undeployInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build').toString())
            def undeployDirectory = Paths.get(SRC_PATH, 'build', 'undeploy').toString()
            undeployInstance.taskPackagePath = Paths.get(undeployDirectory,'package.xml').toString()
            undeployInstance.filesToTruncate = [new File(Paths.get(SRC_PATH,'src', 'classes','Class1.cls').toString()),
                                                new File(Paths.get(SRC_PATH,'src', 'classes','Class1.cls-meta.xml').toString())]
            undeployInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            undeployInstance.taskFolderPath = undeployDirectory
            undeployInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            undeployInstance.createDeploymentDirectory(undeployDirectory)
            undeployInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', 'package.xml').toString()
        when:
            undeployInstance.writePackage(undeployInstance.taskPackagePath, undeployInstance.filesToTruncate)

        then:
            new File(Paths.get(SRC_PATH,'build', 'undeploy', 'package.xml').toString()).exists()
    }

    def "Test should build destructive before to execute deploy taking in account all function called into runTask() function"() {
        given:
            undeployInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build').toString())
            undeployInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            undeployInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            undeployInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', 'package.xml').toString()
            undeployInstance.taskPackagePath = Paths.get(SRC_PATH, 'build', 'undeploy').toString()
            undeployInstance.packageComponent = Mock(PackageComponent)
            undeployInstance.credential = credential
            String componentsToTruncate = ['classes', 'objects', 'triggers', 'pages', 'components', 'workflows', 'tabs'].join(',')
        when:
            undeployInstance.packageComponent.components >> ["classes/*.cls", "triggers/*.trigger", "objects/*.object"]
            undeployInstance.setup()
            undeployInstance.loadParameters()
            undeployInstance.loadClassifiedFiles(componentsToTruncate, undeployInstance.excludes)
            undeployInstance.createDeploymentDirectory(undeployInstance.taskFolderPath)
            undeployInstance.loadFilesToTruncate()
            undeployInstance.copyFilesToTaskDirectory(undeployInstance.filesToTruncate)
            undeployInstance.addInterceptor()
            undeployInstance.writePackage(undeployInstance.taskPackagePath, undeployInstance.filesToTruncate)
            undeployInstance.combinePackage(undeployInstance.taskPackagePath)
            undeployInstance.addNewStandardObjects()
            undeployInstance.createDeploymentDirectory(undeployInstance.taskFolderPath)
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

    def "Test should build a destructiveChanges xml file with Class2, Class1, Trigger2 and Trigger1 values"() {
        given:
            undeployInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build').toString())
            def undeployDirectory = Paths.get(SRC_PATH, 'build', 'undeploy').toString()
            undeployInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            undeployInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            undeployInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', 'package.xml').toString()
            undeployInstance.credential = credential
            undeployInstance.packageComponent = Mock(PackageComponent)
            File class2 = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls').toString())
            class2.write("public with sharing class Class2 {public Class2(Integer a, Integer b){ }}")
            File class2Xml = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls-meta.xml').toString())
            class2Xml.write('''<?xml version="1.0" encoding="UTF-8"?>
                                <ApexClass xmlns="http://soap.sforce.com/2006/04/metadata">
                                    <apiVersion>24.0</apiVersion>
                                    <status>Active</status>
                                </ApexClass> ''')

            File trigger2 = new File(Paths.get(SRC_PATH, 'src', 'triggers', 'trigger2.trigger').toString())
            trigger2.write("trigger trigger2 on Object2__c (before delete, before insert, before update) {}")
            File trigger2Xml = new File(Paths.get(SRC_PATH, 'src', 'triggers', 'trigger2.trigger-meta.xml').toString())
            trigger2Xml.write('''<?xml version="1.0" encoding="UTF-8"?>
                                    <ApexTrigger xmlns="http://soap.sforce.com/2006/04/metadata">
                                        <apiVersion>24.0</apiVersion>
                                        <status>Active</status>
                                    </ApexTrigger> ''')

            String destructiveExpect = '''<?xml version='1.0' encoding='UTF-8'?>
                                                <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                                    <types>
                                                        <members>Class1</members>
                                                        <members>Class2</members>
                                                        <name>ApexClass</name>
                                                    </types>
                                                    <types>
                                                        <members>Object1__c</members>
                                                        <name>CustomObject</name>
                                                      </types>
                                                    <types>
                                                        <members>Trigger1</members>
                                                        <members>trigger2</members>
                                                        <name>ApexTrigger</name>
                                                    </types>
                                                    <version>32.0</version>
                                                </Package>
                                            '''
            String componentsToTruncate = ['classes', 'objects', 'triggers', 'pages', 'components', 'workflows', 'tabs'].join(',')
        when:
            undeployInstance.packageComponent.components >> ["classes/*.cls", "triggers/*.trigger", "objects/*.object"]
            undeployInstance.setup()
            undeployInstance.loadParameters()
            undeployInstance.loadClassifiedFiles(componentsToTruncate, undeployInstance.excludes)
            undeployInstance.createDeploymentDirectory(undeployDirectory)
            undeployInstance.deployToDeleteComponents()
            def destructiveXmlContent =  new File(Paths.get(undeployDirectory, 'destructiveChanges.xml').toString()).text
            def packageXmlContent =  new File(Paths.get(undeployDirectory, 'package.xml').toString()).text
            XMLUnit.ignoreWhitespace = true
            def destructiveXmlDiff = new Diff(destructiveXmlContent, destructiveExpect)
            def packageXmlDiff = new Diff(packageXmlContent, packageExpect)
        then:
            packageXmlDiff.similar()
            destructiveXmlDiff.similar()
    }
    
    def "Test should filter the trigger because the queries said that there isn't any trigger in org" () {
        given:
            def projectPath = Paths.get(SRC_PATH, 'src').toString()
            undeployInstance.projectPath = projectPath
            undeployInstance.credential = credential
            ArrayList<File> files = [new File(Paths.get(projectPath, 'classes', 'Class1.cls').toString()),
                                     new File(Paths.get(projectPath, 'triggers', 'Trigger1.trigger').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Object1__c.object').toString())]
        when:
            ArrayList<File> result = undeployInstance.getValidFilesFromOrg(files)
        then:
            result.sort() == files.sort()
    }

    def cleanupSpec() {
        new File(Paths.get(SRC_PATH, 'build').toString()).deleteDir()
        new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls' ).toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls-meta.xml' ).toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'triggers', 'trigger2.trigger' ).toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'triggers', 'trigger2.trigger-meta.xml' ).toString()).delete()
    }
}