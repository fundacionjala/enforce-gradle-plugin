/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.metadata.DeployMetadata
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class DeployTest extends Specification {
    @Shared
    Project project

    @Shared
    def instanceDeploy

    @Shared
    def SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
                   "fundacionjala", "gradle", "plugins","enforce","tasks", "salesforce", "resources").toString()
    @Shared
    Credential credential

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        project.enforce.srcPath = SRC_PATH
        instanceDeploy = project.tasks.deploy
        instanceDeploy.fileManager = new ManagementFile(SRC_PATH)
        instanceDeploy.project.enforce.deleteTemporaryFiles = false
        instanceDeploy.createDeploymentDirectory(Paths.get(SRC_PATH, 'build').toString())
        instanceDeploy.createDeploymentDirectory(Paths.get(SRC_PATH, 'build', 'deploy').toString())
        instanceDeploy.createDeploymentDirectory(Paths.get(SRC_PATH, 'build', 'deploy', 'folderOne').toString())

        credential = new Credential()
        credential.id = 'id'
        credential.username = 'salesforce2014.test@gmail.com'
        credential.password = '123qwe2014'
        credential.token = 'UO1Jx5vDQl97xCKkwXBH8tg3T'
        credential.loginFormat = LoginType.DEV.value()
        credential.type = 'normal'
    }

    def "Test should show folders that aren't deployed"() {
        given:
            instanceDeploy.folderDeploy = Paths.get(SRC_PATH, "build", "deploy").toString()
        when:
            def stdOut = System.out
            def os = new ByteArrayOutputStream()
            System.out = new PrintStream(os)
            instanceDeploy.foldersNotDeploy = ['folderOne']
            instanceDeploy.displayFolderNoDeploy()
            def array = os.toByteArray()
            def is = new ByteArrayInputStream(array)
            System.out = stdOut
            def lineAux = is.readLines()
        then:
            lineAux[0] == ''
            lineAux[2] == "Folders not deployed "
            lineAux[3] == '___________________________________________'
            lineAux[4] == ''
            lineAux[5] == "${"\t"}${"1.- folderOne"}"
            lineAux[6] == '___________________________________________'
    }

    def "Test should display nothing"() {
        given:
            instanceDeploy.folderDeploy = Paths.get(SRC_PATH, "build", "deploy", "folderOne").toString()
        when:
            def stdOut = System.out
            def os = new ByteArrayOutputStream()
            System.out = new PrintStream(os)
            instanceDeploy.foldersNotDeploy = []
            instanceDeploy.displayFolderNoDeploy()
            def array = os.toByteArray()
            def is = new ByteArrayInputStream(array)
            System.out = stdOut
            def lineAux = is.readLines()
        then:
            lineAux == []
    }

    def "Test should update file tracker"() {
        given:
            instanceDeploy.projectPath = SRC_PATH
            String FILE_TRACKING = '/.fileTracker.data'
        when:
            instanceDeploy.updateFileTracker()
        then:
            new File(Paths.get(SRC_PATH, FILE_TRACKING).toString()).exists()
    }

    def "Test should create a directory in build directory"() {
        given:
            def newDirectoryPath = Paths.get(SRC_PATH, 'build', 'deployDirectory').toString()
        when:
            instanceDeploy.createDeploymentDirectory(newDirectoryPath)
        then:
            new File(newDirectoryPath).exists()
    }

    def "Integration test should deploy files"() {
        given:
            def deployPath = Paths.get(SRC_PATH, 'build', 'deploy').toString()
            instanceDeploy.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            instanceDeploy.projectPath = Paths.get(SRC_PATH, 'src').toString()
            instanceDeploy.componentDeploy = new DeployMetadata()
            instanceDeploy.poll = 200
            instanceDeploy.waitTime = 10
            instanceDeploy.credential = credential
        when:
            instanceDeploy.runTask()
            def packageXmlToDeployDirectory =  new File(Paths.get(SRC_PATH, 'build', 'deploy', 'package.xml').toString()).text
            def packageXmlToSrcDirectory =  new File(Paths.get(SRC_PATH, 'build', 'deploy', 'package.xml').toString()).text
            XMLUnit.ignoreWhitespace = true
            def xmlDiff = new Diff(packageXmlToDeployDirectory, packageXmlToSrcDirectory)
        then:
            xmlDiff.similar()
            new File(Paths.get(deployPath, 'classes', 'Class1.cls').toString()).exists()
            new File(Paths.get(deployPath, 'classes', 'Class1.cls-meta.xml').toString()).exists()
            new File(Paths.get(deployPath, 'objects', 'Object1__c.object').toString()).exists()
            new File(Paths.get(deployPath, 'objects', 'Account.object').toString()).exists()
            new File(Paths.get(deployPath, 'triggers', 'Trigger1.trigger').toString()).exists()
            new File(Paths.get(deployPath, 'triggers', 'Trigger1.trigger-meta.xml').toString()).exists()
            new File(Paths.get(SRC_PATH, 'build', 'deploy.zip').toString()).exists()
    }

    def "Test should exclude a file by file"() {
        given:
            ArrayList<File> files = [new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString()),
                                     new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger-meta.xml').toString())]
            String criterion = "classes${File.separator}class1.cls"
            instanceDeploy.projectPath = SRC_PATH
        when:
            def arrayFiltered = instanceDeploy.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger-meta.xml').toString())]
    }

    def "Test should exclude a files by folder"() {
        given:
            ArrayList<File> files = [new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString()),
                                     new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Account.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object2__c.object').toString())]
            String criterion = "classes"
            instanceDeploy.projectPath = SRC_PATH
        when:
            def arrayFiltered = instanceDeploy.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Account.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object2__c.object').toString())].sort()
    }

    def "Test should exclude a file when you sent as criterion a wilcard"() {
        given:
            ArrayList<File> files = [new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString()),
                                     new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Account.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object2__c.object').toString())]
            String criterion = "*${File.separator}class1.cls"
            instanceDeploy.projectPath = SRC_PATH
        when:
            def arrayFiltered = instanceDeploy.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Account.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object2__c.object').toString())].sort()
    }

    def "Test should exclude a files when you sent as criterion a wilcard equal to classes/*"() {
        given:
            ArrayList<File> files = [new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString()),
                                     new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Account.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object2__c.object').toString())]
            String criterion = "objects${File.separator}**"
            instanceDeploy.projectPath = SRC_PATH
        when:
            def arrayFiltered = instanceDeploy.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString()),
                                     new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger-meta.xml').toString())].sort()
    }

    def "Test should exclude a list of files"() {
        given:
            ArrayList<File> files = [new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString()),
                                     new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Account.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object2__c.object').toString())]
            String criterion = "classes${File.separator}class1.cls,triggers${File.separator}LunesTrigger.trigger"
            instanceDeploy.projectPath = SRC_PATH
        when:
            def arrayFiltered = instanceDeploy.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(SRC_PATH, 'objects', 'Account.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object2__c.object').toString())]
    }

    def "Test should exclude a list of files with it xml file"() {
        given:
            ArrayList<File> files = [new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString()),
                                     new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Account.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object2__c.object').toString())]
            String criterion = "classes${File.separator}class1.cls,triggers${File.separator}LunesTrigger.trigger"
            instanceDeploy.projectPath = SRC_PATH
        when:
            def arrayFiltered = instanceDeploy.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(SRC_PATH, 'objects', 'Account.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'objects', 'Object2__c.object').toString())]
    }

    def "Test should exclude files by wildcard sent 'classes/**'"() {
        given:
            ArrayList<File> files = [new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'classes', 'class2.cls').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'classes', 'class2.cls-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString())]
            String criterion = "classes${File.separator}**"
            instanceDeploy.projectPath = Paths.get(SRC_PATH, 'src').toString()
        when:
            def arrayFiltered = instanceDeploy.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString())].sort()
    }

    def "Test should exclude files by wildcard sent '**/*.object'"() {
        given:
            ArrayList<File> files = [new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'objects', 'Account.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'objects', 'Object1__c.object').toString())]
            String criterion = "**${File.separator}*.object"
            instanceDeploy.projectPath = Paths.get(SRC_PATH, 'src').toString()
        when:
            def arrayFiltered = instanceDeploy.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString())].sort()
    }

    def "Test should exclude files by wildcard sent '**/*.cls'"() {
        given:
            ArrayList<File> files = [new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'objects', 'Account.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'objects', 'Object1__c.object').toString())]
            String criterion = "**${File.separator}*.cls"
            instanceDeploy.projectPath = Paths.get(SRC_PATH, 'src').toString()
        when:
            def arrayFiltered = instanceDeploy.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'objects', 'Account.object').toString()),
                                     new File(Paths.get(SRC_PATH, 'src', 'objects', 'Object1__c.object').toString())].sort()

    }

    def "Test should exclude files by wildcard sent '**/*Account*/**'"() {
        given:
        ArrayList<File> files = [new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString()),
                                 new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls-meta.xml').toString()),
                                 new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                 new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString()),
                                 new File(Paths.get(SRC_PATH, 'src', 'objects', 'Account.object').toString()),
                                 new File(Paths.get(SRC_PATH, 'src', 'objects', 'Object1__c.object').toString())]
        String criterion = "**${File.separator}*Account*${File.separator}**"
        instanceDeploy.projectPath = Paths.get(SRC_PATH, 'src').toString()
        when:
        def arrayFiltered = instanceDeploy.excludeFilesByCriterion(files, criterion)
        then:
        arrayFiltered.sort() == [new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                 new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString()),
                                 new File(Paths.get(SRC_PATH, 'src', 'objects', 'Object1__c.object').toString()),
                                 new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString()),
                                 new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls-meta.xml').toString())].sort()
    }

    def "Test should return an exception if folders parameter is not valid" () {
        given:
            instanceDeploy.folders = 'invalidFolder'
        when:
            instanceDeploy.deployByFolder()
        then:
            thrown(Exception)
    }

    def "Test should return an exception if folders parameter is empty" () {
        given:
            instanceDeploy.folders = ''
        when:
            instanceDeploy.deployByFolder()
        then:
            thrown(Exception)
    }


    def "Integration testing must deploy the organization files and delete temporary files generated"() {
        given:
            instanceDeploy.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            instanceDeploy.projectPath = Paths.get(SRC_PATH, 'src').toString()
            instanceDeploy.project.enforce.deleteTemporaryFiles = true
            instanceDeploy.poll = 200
            instanceDeploy.waitTime = 10
            instanceDeploy.credential = credential
            def deployFileZipPath = Paths.get(SRC_PATH,'build','deploy.zip').toString()
            def deployFolderPath = Paths.get(SRC_PATH,'build','deploy').toString()
            File deployFileZip = new File(deployFileZipPath)
            File deployFolder = new File(deployFolderPath)
        when:
            instanceDeploy.runTask()
        then:
            !deployFileZip.exists()
            !deployFolder.exists()
    }

    def cleanupSpec() {
        new File(Paths.get(SRC_PATH, '/.fileTracker.data').toString()).delete()
        new File(Paths.get(SRC_PATH, 'build').toString()).deleteDir()
    }
}
