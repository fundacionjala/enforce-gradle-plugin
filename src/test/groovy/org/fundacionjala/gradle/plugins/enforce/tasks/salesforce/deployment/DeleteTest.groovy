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
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification
import java.nio.file.Paths

class DeleteTest extends Specification {

    @Shared
    def credential

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
    ComponentSerializer componentSerializer

    @Shared
    ComponentMonitor componentMonitor

    def setup() {

        credential = new Credential()
        credential.id = 'id'
        credential.username = 'salesforce2014.test@gmail.com'
        credential.password = '123qwe2014'
        credential.token = 'UO1Jx5vDQl97xCKkwXBH8tg3T'
        credential.loginFormat = LoginType.DEV.value()
        credential.type = 'normal'

        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        project.enforce.srcPath = SRC_PATH
        deleteInstance = project.tasks.delete
        deleteInstance.credential = credential
        deleteInstance.fileManager = new ManagementFile(SRC_PATH)
        deleteInstance.project.enforce.deleteTemporaryFiles = false
        deleteInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build').toString())
        deleteInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build', 'delete').toString())
        deleteInstance.projectPath = SRC_PATH
        deleteInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
        deleteInstance.projectPath = Paths.get(SRC_PATH, 'src_delete').toString()
        deleteInstance.componentDeploy = new DeployMetadata()
        deleteInstance.project.enforce.deleteTemporaryFiles = true
        deleteInstance.projectPackagePath = Paths.get(SRC_PATH,'src_delete', 'package.xml').toString()
        deleteInstance.taskFolderName = Constants.DIR_DELETE_FOLDER

        ArrayList<File> files = new ArrayList<File>()
        files.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class1.cls').toString()))
        files.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class1.cls-meta.xml').toString()))
        files.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class2.cls').toString()))
        files.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class2.cls-meta.xml').toString()))
        files.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class3.cls').toString()))
        files.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class3.cls-meta.xml').toString()))
        files.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger1.trigger').toString()))
        files.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger1.trigger-meta.xml').toString()))
        files.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger2.trigger').toString()))
        files.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger2.trigger-meta.xml').toString()))
        files.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger3.trigger').toString()))
        files.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger3.trigger-meta.xml').toString()))
        files.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object1__c.object').toString()))
        files.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object2__c.object').toString()))
        files.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object3__c.object').toString()))
        files.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object4__c.object').toString()))
        files.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object5__c.object').toString()))

        ArrayList<File> folders = new ArrayList<File>()
        folders.add(new File(Paths.get(SRC_PATH,'src_delete').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'src_delete','classes').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'src_delete','triggers').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'src_delete','objects').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'src_delete','test').toString()))

        folders.each { folder->
            new File(folder.getAbsolutePath()).mkdir()
        }

        files.each { file->
            new File(file.getAbsolutePath()).createNewFile()
        }

        String packageXmlPath = deleteInstance.projectPackagePath
        String packageXmlContent ='''<?xml version='1.0' encoding='UTF-8'?>
            <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                <types>
                    <members>*</members>
                    <name>ApexClass</name>
                </types>
                <types>
                    <members>*</members>
                    <name>ApexTrigger</name>
                </types>
                <types>
                    <members>*</members>
                    <name>CustomObject</name>
                </types>
                <version>32.0</version>
            </Package>
             '''
        File filePackageXml = new File(packageXmlPath)
        filePackageXml.write(packageXmlContent)
    }

    def "Integration testing must list all the files to delete"() {
        given:
            ArrayList<File> filesExpected = new ArrayList<File>();
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class1.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class1.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class2.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class2.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class3.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class3.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger1.trigger').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger1.trigger-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger2.trigger').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger2.trigger-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger3.trigger').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger3.trigger-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object1__c.object').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object2__c.object').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object3__c.object').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object4__c.object').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object5__c.object').toString()))

        when:
            deleteInstance.taskFolderPath = Paths.get(deleteInstance.buildFolderPath, Constants.DIR_DELETE_FOLDER).toString()
            deleteInstance.setup()
            deleteInstance.createDeploymentDirectory(deleteInstance.taskFolderPath)
            deleteInstance.loadClassifiedFiles(deleteInstance.files, deleteInstance.excludes)
            deleteInstance.loadFilesToDelete()
            deleteInstance.createDestructive()
            deleteInstance.createPackageEmpty()

        then:
             deleteInstance.filesToDeleted.sort() == filesExpected.sort()
    }

    def "Integration testing must list files filtered for folders to delete"() {
        given:
            deleteInstance.parameters.put('files','classes,triggers')

            ArrayList<File> filesExpected = new ArrayList<File>();
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class1.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class1.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class2.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class2.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class3.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class3.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger1.trigger').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger1.trigger-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger2.trigger').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger2.trigger-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger3.trigger').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger3.trigger-meta.xml').toString()))

        when:
            deleteInstance.taskFolderPath = Paths.get(deleteInstance.buildFolderPath, DIR_DELETE_FOLDER).toString()
            deleteInstance.createDeploymentDirectory(deleteInstance.taskFolderPath)
            deleteInstance.setup()
            deleteInstance.loadParameters()
            deleteInstance.loadClassifiedFiles(deleteInstance.files, deleteInstance.excludes)
            deleteInstance.loadFilesToDelete()
            deleteInstance.createDestructive()
            deleteInstance.createPackageEmpty()

        then:
            deleteInstance.filesToDeleted.sort() == filesExpected.sort()
    }

    def "Integration testing must list files filtered for files to delete"() {
        given:
            deleteInstance.parameters.put('files','classes/Class1.cls,triggers/Trigger1.trigger')

            ArrayList<File> filesExpected = new ArrayList<File>();
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class1.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class1.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger1.trigger').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger1.trigger-meta.xml').toString()))

        when:
            deleteInstance.taskFolderPath = Paths.get(deleteInstance.buildFolderPath, DIR_DELETE_FOLDER).toString()
            deleteInstance.createDeploymentDirectory(deleteInstance.taskFolderPath)
            deleteInstance.setup()
            deleteInstance.loadParameters()
            deleteInstance.loadClassifiedFiles(deleteInstance.files, deleteInstance.excludes)
            deleteInstance.loadFilesToDelete()
            deleteInstance.createDestructive()
            deleteInstance.createPackageEmpty()

        then:
            filesExpected.sort() == deleteInstance.filesToDeleted.sort()
    }

    def "Integration testing must list all the files less exclude to delete"() {
        given:
            deleteInstance.parameters.put('excludes','classes/Class1.cls,triggers/*.trigger')
            deleteInstance.parameters.put('files',"")

            ArrayList<File> filesExpected = new ArrayList<File>();
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class2.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class2.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class3.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class3.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object1__c.object').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object2__c.object').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object3__c.object').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object4__c.object').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object5__c.object').toString()))

        when:
            deleteInstance.taskFolderPath = Paths.get(deleteInstance.buildFolderPath, Constants.DIR_DELETE_FOLDER).toString()
            deleteInstance.createDeploymentDirectory(deleteInstance.taskFolderPath)
            deleteInstance.setup()
            deleteInstance.loadParameters()
            deleteInstance.loadClassifiedFiles(deleteInstance.files, deleteInstance.excludes)
            deleteInstance.loadFilesToDelete()
            deleteInstance.createDestructive()
            deleteInstance.createPackageEmpty()

        then:
            filesExpected.sort() == deleteInstance.filesToDeleted.sort()
    }

    def "Integration testing must list all the files less exclude all objects and classes"() {
        given:
            deleteInstance.parameters.put('folders','classes,triggers,objects')
            deleteInstance.parameters.put('excludes','classes/*.cls,objects/**')

            ArrayList<File> filesExpected = new ArrayList<File>();
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger1.trigger').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger1.trigger-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger2.trigger').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger2.trigger-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger3.trigger').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger3.trigger-meta.xml').toString()))

        when:
            deleteInstance.taskFolderPath = Paths.get(deleteInstance.buildFolderPath, Constants.DIR_DELETE_FOLDER).toString()
            deleteInstance.createDeploymentDirectory(deleteInstance.taskFolderPath)
            deleteInstance.setup()
            deleteInstance.loadParameters()
            deleteInstance.loadClassifiedFiles(deleteInstance.files, deleteInstance.excludes)
            deleteInstance.loadFilesToDelete()
            deleteInstance.createDestructive()
            deleteInstance.createPackageEmpty()

        then:
            filesExpected.sort() == deleteInstance.filesToDeleted.sort()
    }

    def "Integration testing must list all the files less exclude all files that contain the number 1 or 2"() {
        given:
            deleteInstance.parameters.put('folders','classes,triggers,objects')
            deleteInstance.parameters.put('excludes','*/*1*.*,*/*2*.*')

            ArrayList<File> filesExpected = new ArrayList<File>();
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class3.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class3.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger3.trigger').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger3.trigger-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object3__c.object').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object4__c.object').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object5__c.object').toString()))

        when:
            deleteInstance.taskFolderPath = Paths.get(deleteInstance.buildFolderPath, Constants.DIR_DELETE_FOLDER).toString()
            deleteInstance.createDeploymentDirectory(deleteInstance.taskFolderPath)
            deleteInstance.setup()
            deleteInstance.loadParameters()
            deleteInstance.loadClassifiedFiles(deleteInstance.files, deleteInstance.excludes)
            deleteInstance.loadFilesToDelete()
            deleteInstance.createDestructive()
            deleteInstance.createPackageEmpty()

        then:
            filesExpected.sort() == deleteInstance.filesToDeleted.sort()
    }

    def "Integration testing must list all the files less exclude all files"() {
        given:
            deleteInstance.parameters.put('excludes','*/**.*')
        when:
            deleteInstance.taskFolderPath = Paths.get(deleteInstance.buildFolderPath, Constants.DIR_DELETE_FOLDER).toString()
            deleteInstance.createDeploymentDirectory(deleteInstance.taskFolderPath)
            deleteInstance.setup()
            deleteInstance.loadParameters()
            deleteInstance.loadClassifiedFiles(deleteInstance.files, deleteInstance.excludes)
            deleteInstance.loadFilesToDelete()
            deleteInstance.createDestructive()
            deleteInstance.createPackageEmpty()

        then:
            deleteInstance.filesToDeleted.isEmpty()
    }

    def cleanupSpec() {
        new File(Paths.get(SRC_PATH, 'build').toString()).deleteDir()
        new File(Paths.get(SRC_PATH,'src_delete').toString()).deleteDir()
        new File(Paths.get(SRC_PATH, 'src_delete', '.fileTracker.data').toString()).delete()
    }
}