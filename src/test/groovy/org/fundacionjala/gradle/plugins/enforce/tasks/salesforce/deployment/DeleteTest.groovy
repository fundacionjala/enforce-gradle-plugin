/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentMonitor
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentSerializer
import org.fundacionjala.gradle.plugins.enforce.metadata.DeployMetadata
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.ToolingAPI
import org.fundacionjala.gradle.plugins.enforce.wsc.soap.ApexAPI
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

class DeleteTest extends Specification {
    @Shared
    Project project

    @Shared
    def deleteInstance

    @Shared
    def SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
            "fundacionjala", "gradle", "plugins","enforce","tasks", "salesforce", "resources").toString()
    @Shared
    def DIR_DELETE_FOLDER = 'delete'

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
        deleteInstance = project.tasks.delete
        deleteInstance.fileManager = new ManagementFile(SRC_PATH)
        deleteInstance.project.enforce.deleteTemporaryFiles = false
        deleteInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build').toString())
        deleteInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build', 'delete').toString())
        deleteInstance.projectPath = SRC_PATH
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


        ArrayList<File> files = new ArrayList<File>()

        files.add(new File(Paths.get(SRC_PATH, 'src','classes', 'class1.cls').toString()))
        files.add(new File(Paths.get(SRC_PATH, 'src','classes', '').toString()))
        files.add(new File(Paths.get(SRC_PATH, 'src','classes', '').toString()))
        files.add(new File(Paths.get(SRC_PATH, 'src','classes', '').toString()))
        files.add(new File(Paths.get(SRC_PATH, 'src','classes', '').toString()))
        files.add(new File(Paths.get(SRC_PATH, 'src','classes', '').toString()))
        files.add(new File(Paths.get(SRC_PATH, 'src','classes', '').toString()))


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

    def "Integration testing must list all the files to delete"() {
        given:
            deleteInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            deleteInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            deleteInstance.componentDeploy = new DeployMetadata()
            deleteInstance.poll = 200
            deleteInstance.waitTime = 10
            deleteInstance.credential = credential
            deleteInstance.project.enforce.deleteTemporaryFiles = true

            ArrayList<File> filesExpected = new ArrayList<File>();
            filesExpected.add(new File(Paths.get(SRC_PATH,'src','classes','Class1.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src','classes','Class1.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src','objects','Account.object').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src','objects','Object1__c.object').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src','package.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src','triggers','Trigger1.trigger').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src','triggers','Trigger1.trigger-meta.xml').toString()))

        when:
            deleteInstance.pathDetele = Paths.get(deleteInstance.buildFolderPath, DIR_DELETE_FOLDER).toString()
            deleteInstance.createDeploymentDirectory(deleteInstance.pathDetele)
            deleteInstance.addAllFiles()
            deleteInstance.addFoldersToDeleteFiles()
            deleteInstance.addFilesToDelete()
            deleteInstance.excludeFilesToDelete()
            deleteInstance.createDestructive()
            deleteInstance.createPackage()

        then:
            filesExpected.sort{ it.getAbsolutePath() }.equals( deleteInstance.filesToDeleted.sort{ it.getAbsolutePath() })
    }

    def "Integration testing must list files filtered for folders to delete"() {
        given:
            deleteInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            deleteInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            deleteInstance.componentDeploy = new DeployMetadata()
            deleteInstance.poll = 200
            deleteInstance.waitTime = 10
            deleteInstance.credential = credential
            deleteInstance.project.enforce.deleteTemporaryFiles = true
            deleteInstance.parameters.put('folders','classes,triggers')

            ArrayList<File> filesExpected = new ArrayList<File>();
            filesExpected.add(new File(Paths.get(SRC_PATH,'src','classes','Class1.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src','classes','Class1.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src','triggers','Trigger1.trigger').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src','triggers','Trigger1.trigger-meta.xml').toString()))

        when:
            deleteInstance.pathDetele = Paths.get(deleteInstance.buildFolderPath, DIR_DELETE_FOLDER).toString()
            deleteInstance.createDeploymentDirectory(deleteInstance.pathDetele)
            deleteInstance.addAllFiles()
            deleteInstance.addFoldersToDeleteFiles()
            deleteInstance.addFilesToDelete()
            deleteInstance.excludeFilesToDelete()
            deleteInstance.createDestructive()
            deleteInstance.createPackage()

        then:
            filesExpected.sort{ it.getAbsolutePath() }.equals( deleteInstance.filesToDeleted.sort{ it.getAbsolutePath() })
    }

    def "Integration testing must list files filtered for files to delete"() {
        given:
            deleteInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            deleteInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            deleteInstance.componentDeploy = new DeployMetadata()
            deleteInstance.poll = 200
            deleteInstance.waitTime = 10
            deleteInstance.credential = credential
            deleteInstance.project.enforce.deleteTemporaryFiles = true
            deleteInstance.parameters.put('files','classes/Class1.cls')

            ArrayList<File> filesExpected = new ArrayList<File>();
            filesExpected.add(new File(Paths.get(SRC_PATH,'src','classes','Class1.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src','classes','Class1.cls-meta.xml').toString()))

        when:
            deleteInstance.pathDetele = Paths.get(deleteInstance.buildFolderPath, DIR_DELETE_FOLDER).toString()
            deleteInstance.createDeploymentDirectory(deleteInstance.pathDetele)
            deleteInstance.addAllFiles()
            deleteInstance.addFoldersToDeleteFiles()
            deleteInstance.addFilesToDelete()
            deleteInstance.excludeFilesToDelete()
            deleteInstance.createDestructive()
            deleteInstance.createPackage()

        then:
            filesExpected.sort{ it.getAbsolutePath() }.equals( deleteInstance.filesToDeleted.sort{ it.getAbsolutePath() })
    }

    def "Integration testing must list all the files less exclude to delete"() {
        given:
            deleteInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            deleteInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            deleteInstance.componentDeploy = new DeployMetadata()
            deleteInstance.poll = 200
            deleteInstance.waitTime = 10
            deleteInstance.credential = credential
            deleteInstance.project.enforce.deleteTemporaryFiles = true

            deleteInstance.parameters.put('excludes','classes/Class1.cls,triggers/Trigger1.trigger')

            ArrayList<File> filesExpected = new ArrayList<File>();
            filesExpected.add(new File(Paths.get(SRC_PATH,'src','objects','Account.object').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src','objects','Object1__c.object').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src','package.xml').toString()))

        when:
            deleteInstance.pathDetele = Paths.get(deleteInstance.buildFolderPath, DIR_DELETE_FOLDER).toString()
            deleteInstance.createDeploymentDirectory(deleteInstance.pathDetele)
            deleteInstance.addAllFiles()
            deleteInstance.addFoldersToDeleteFiles()
            deleteInstance.addFilesToDelete()
            deleteInstance.excludeFilesToDelete()
            deleteInstance.createDestructive()
            deleteInstance.createPackage()

        then:
            filesExpected.sort{ it.getAbsolutePath() }.equals( deleteInstance.filesToDeleted.sort{ it.getAbsolutePath() })
    }

    def cleanupSpec() {
        new File(Paths.get(SRC_PATH, 'build').toString()).deleteDir()
        new File(Paths.get(SRC_PATH, 'classes', 'Class2.cls').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls-meta.xml').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class3.cls').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class3.cls-meta.xml').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', '.fileTracker.data').toString()).delete()
    }
}