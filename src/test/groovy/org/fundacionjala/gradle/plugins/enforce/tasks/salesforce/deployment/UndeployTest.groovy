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
    SmartFilesValidator smartFilesValidator

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
        undeployInstance.smartFilesValidator = Mock(SmartFilesValidator)

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
                                                    <members>Trigger1</members>
                                                    <name>ApexTrigger</name>
                                                </types>
                                                <types>
                                                    <members>Object1__c</members>
                                                    <name>CustomObject</name>
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
            undeployInstance.unDeployPackagePath = Paths.get(undeployDirectory,'package.xml').toString()
            undeployInstance.filesToTruncate = [new File(Paths.get(SRC_PATH,'src', 'classes','Class1.cls').toString()),
                                                new File(Paths.get(SRC_PATH,'src', 'classes','Class1.cls-meta.xml').toString())]
            undeployInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            undeployInstance.folderUnDeploy = undeployDirectory
            undeployInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            undeployInstance.createDeploymentDirectory(undeployDirectory)
            undeployInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', 'package.xml').toString()
        when:
            undeployInstance.writePackage(undeployInstance.unDeployPackagePath, undeployInstance.filesToTruncate)

        then:
            new File(Paths.get(SRC_PATH,'build', 'undeploy', 'package.xml').toString()).exists()
    }

    def "Test should build a destructiveChanges xml file with Class1, Trigger1 and Object1__c values"() {
        given:
            undeployInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build').toString())
            def undeployDirectory = Paths.get(SRC_PATH, 'build', 'undeploy').toString()
            undeployInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            undeployInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            undeployInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', 'package.xml').toString()
            undeployInstance.credential = credential
            undeployInstance.smartFilesValidator = Mock(SmartFilesValidator)
            undeployInstance.packageComponent = Mock(PackageComponent)
            ArrayList<File> files = [new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'objects', 'Object1__c.object').toString()),]
        when:
            undeployInstance.packageComponent.components >> ["classes/*.cls", "triggers/*.trigger", "objects/*.object"]
            undeployInstance.smartFilesValidator.filterFilesAccordingOrganization(_,_) >> files
            undeployInstance.setup()
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

    def "Test should build destructive before to execute deploy taking in account all function called into runTask() function"() {
        given:
            undeployInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build').toString())
            undeployInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            undeployInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            undeployInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', 'package.xml').toString()
            undeployInstance.unDeployPackagePath = Paths.get(SRC_PATH, 'build', 'undeploy').toString()
            undeployInstance.packageComponent = Mock(PackageComponent)
            undeployInstance.credential = credential
            ArrayList<File> files = [new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'objects', 'Object1__c.object').toString()),]
        when:
            undeployInstance.packageComponent.components >> ["classes/*.cls", "triggers/*.trigger", "objects/*.object"]
            undeployInstance.smartFilesValidator.filterFilesAccordingOrganization(_,_) >> files
            undeployInstance.setup()
            undeployInstance.createDeploymentDirectory(undeployInstance.folderUnDeploy)
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
    
    def "Test should filter the trigger because the queries said that there isn't any trigger in org" () {
        given:
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
            undeployInstance.setup()
            undeployInstance.createDeploymentDirectory(undeployInstance.folderUnDeploy)
            undeployInstance.loadFilesToTruncate()
            undeployInstance.copyFilesToTruncate()
            File classFile = new File(Paths.get(unDeployPath, 'classes', 'Class1.cls').toString())
            File triggerFile = new File(Paths.get(unDeployPath, 'triggers', 'Trigger1.trigger').toString())
            File objectFile = new File(Paths.get(unDeployPath, 'objects', 'Object1__c.object').toString())
        then:
            classFile.exists()
            !triggerFile.exists()
            objectFile.exists()
    }

    def "Test should return a Map with parameter and their values" () {
        given:
            ArrayList<String> parameters = [Constants.PARAMETER_EXCLUDES, Constants.PARAMETER_FILES]
            undeployInstance.parameters = [:]
            undeployInstance.parameters.put(Constants.PARAMETER_EXCLUDES, "classes${File.separator}Class1.cls")
            undeployInstance.parameters.put(Constants.PARAMETER_FILES, "classes,objects")
        when:
            Map<String, String> result = undeployInstance.getParameterWithTheirsValues(parameters)
        then:
            result.containsKey(Constants.PARAMETER_FILES)
            result.get(Constants.PARAMETER_FILES) == "classes,objects"
            result.containsKey(Constants.PARAMETER_EXCLUDES)
            result.get(Constants.PARAMETER_EXCLUDES) == "classes${File.separator}Class1.cls"
            result.size() == 2
    }

    def cleanupSpec() {
        //new File(Paths.get(SRC_PATH, 'build').toString()).deleteDir()
    }
}