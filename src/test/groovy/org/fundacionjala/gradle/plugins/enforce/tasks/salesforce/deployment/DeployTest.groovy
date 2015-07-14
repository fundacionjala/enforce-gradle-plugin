/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
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
    }

    def "Test should show folders that aren't deployed"() {
        given:
            instanceDeploy.taskFolderPath = Paths.get(SRC_PATH, "build", "deploy").toString()
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
            instanceDeploy.taskFolderPath = Paths.get(SRC_PATH, "build", "deploy", "folderOne").toString()
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
            String srcPath = Paths.get(SRC_PATH, 'src').toString()
            instanceDeploy.projectPath = srcPath
            String FILE_TRACKING = '/.fileTracker.data'
        when:
            instanceDeploy.updateFileTracker()
        then:
            new File(Paths.get(srcPath, FILE_TRACKING).toString()).exists()
    }

    def "Test should create a directory in build directory"() {
        given:
            def newDirectoryPath = Paths.get(SRC_PATH, 'build', 'deployDirectory').toString()
        when:
            instanceDeploy.createDeploymentDirectory(newDirectoryPath)
        then:
            new File(newDirectoryPath).exists()
    }

    def "Test should setup files into build/deploy directory to deploy"() {
        given:
            def taskFolderPath = Paths.get(SRC_PATH, 'build', 'deploy').toString()
            instanceDeploy.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            instanceDeploy.projectPath = Paths.get(SRC_PATH, 'src').toString()
            instanceDeploy.projectPackagePath = Paths.get(SRC_PATH, 'src', 'package.xml').toString()
        when:
            instanceDeploy.setup()
            instanceDeploy.loadParameters()
            instanceDeploy.loadClassifiedFiles(instanceDeploy.folders, instanceDeploy.excludes)
            instanceDeploy.loadFilesToDeploy()
            instanceDeploy.createDeploymentDirectory(taskFolderPath)
            instanceDeploy.displayFolderNoDeploy()
            instanceDeploy.deployAllComponents()
            def packageXmlToDeployDirectory =  new File(Paths.get(SRC_PATH, 'build', 'deploy', 'package.xml').toString()).text
            def packageXmlToSrcDirectory =  new File(Paths.get(SRC_PATH, 'build', 'deploy', 'package.xml').toString()).text
            XMLUnit.ignoreWhitespace = true
            def xmlDiff = new Diff(packageXmlToDeployDirectory, packageXmlToSrcDirectory)
        then:
            xmlDiff.similar()
            new File(Paths.get(taskFolderPath, 'classes', 'Class1.cls').toString()).exists()
            new File(Paths.get(taskFolderPath, 'classes', 'Class1.cls-meta.xml').toString()).exists()
            new File(Paths.get(taskFolderPath, 'objects', 'Object1__c.object').toString()).exists()
            new File(Paths.get(taskFolderPath, 'triggers', 'Trigger1.trigger').toString()).exists()
            new File(Paths.get(taskFolderPath, 'triggers', 'Trigger1.trigger-meta.xml').toString()).exists()
    }

    def "Test should return an exception if folders parameter is not valid" () {
        given:
            instanceDeploy.parameters.put('folders','invalidFolder')
        when:
            instanceDeploy.loadParameters()
            instanceDeploy.deployAllComponents()
        then:
            thrown(Exception)
    }

    def "Test should return an exception if folders parameter is empty" () {
        given:
            instanceDeploy.parameters.put('folders','')
        when:
            instanceDeploy.loadParameters()
            instanceDeploy.deployAllComponents()
        then:
            thrown(Exception)
    }

    def "Test should setup files to deploy at build directory and delete temporary files generated"() {
        given:
            instanceDeploy.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            instanceDeploy.projectPath = Paths.get(SRC_PATH, 'src').toString()
            instanceDeploy.project.enforce.deleteTemporaryFiles = true
            instanceDeploy.projectPackagePath = Paths.get(SRC_PATH, 'src', 'package.xml').toString()
            def deployFileZipPath = Paths.get(SRC_PATH,'build','deploy.zip').toString()
            def deployFolderPath = Paths.get(SRC_PATH,'build','deploy').toString()
            File deployFileZip = new File(deployFileZipPath)
            File deployFolder = new File(deployFolderPath)
            String folderDeploy = Paths.get(SRC_PATH, 'build', 'deploy').toString()
        when:
            instanceDeploy.setup()
            instanceDeploy.createDeploymentDirectory(folderDeploy)
            instanceDeploy.displayFolderNoDeploy()
            instanceDeploy.deployAllComponents()
            instanceDeploy.deleteTemporaryFiles()
        then:
            !deployFileZip.exists()
            !deployFolder.exists()
    }

    def cleanupSpec() {
        new File(Paths.get(SRC_PATH, '/.fileTracker.data').toString()).delete()
        new File(Paths.get(SRC_PATH, 'build').toString()).deleteDir()
    }
}
