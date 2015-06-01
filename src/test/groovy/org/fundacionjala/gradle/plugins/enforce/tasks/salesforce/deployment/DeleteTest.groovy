/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentMonitor
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentSerializer
import org.fundacionjala.gradle.plugins.enforce.metadata.DeployMetadata
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.ToolingAPI
import org.fundacionjala.gradle.plugins.enforce.wsc.soap.ApexAPI
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

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

    def "Integration testing must create two files for the next test"() {
        given:
        deleteInstance.packageGenerator.fileTrackerMap = [:]
        deleteInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
        deleteInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
        deleteInstance.componentDeploy = new DeployMetadata()
        deleteInstance.poll = 200
        deleteInstance.waitTime = 10
        deleteInstance.credential = credential
        deleteInstance.project.enforce.deleteTemporaryFiles = true
        componentMonitor.srcProject = Paths.get(SRC_PATH,'src').toString()
        componentSerializer.save(componentMonitor.getComponentsSignature([]))

        def newTemporalClassPath2 = Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls').toString()
        def newTemporalXmlPath2 = Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls-meta.xml').toString()
        def newTemporalClassPath3 = Paths.get(SRC_PATH, 'src', 'classes', 'Class3.cls').toString()
        def newTemporalXmlPath3 = Paths.get(SRC_PATH, 'src', 'classes', 'Class3.cls-meta.xml').toString()

        FileWriter writerClass2 = new FileWriter(newTemporalClassPath2)
        FileWriter writerXml2   = new FileWriter(newTemporalXmlPath2)
        FileWriter writerClass3 = new FileWriter(newTemporalClassPath3)
        FileWriter writerXml3   = new FileWriter(newTemporalXmlPath3)

        def contentTemporalClass2 = "public with sharing class Class2 {public Class2(Integer a, Integer b){ }}"
        def contentTemporalClass3 = "public with sharing class Class2 {public Class3(Integer a, Integer b){ }}"
        def contentTemporalXml2 = "${"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"}${"<ApexClass xmlns=\"http://soap.sforce.com/2006/04/metadata\">"}${"<apiVersion>24.0</apiVersion><status>Active</status></ApexClass>"}"
        def contentTemporalXml3 = "${"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"}${"<ApexClass xmlns=\"http://soap.sforce.com/2006/04/metadata\">"}${"<apiVersion>24.0</apiVersion><status>Active</status></ApexClass>"}"

        writerClass2.write(contentTemporalClass2)
        writerClass3.write(contentTemporalClass3)
        writerXml2.write(contentTemporalXml2)
        writerXml3.write(contentTemporalXml3)

        writerClass2.close()
        writerClass3.close()
        writerXml2.close()
        writerXml3.close()

        File fileClass2  = new File(newTemporalClassPath2)
        File fileClass3  = new File(newTemporalClassPath3)
        File fileXML2    = new File(newTemporalXmlPath2)
        File fileXML3    = new File(newTemporalXmlPath3)

        ToolingAPI toolingAPI
        ApexAPI apexAPI
        String jsonByClasses
        String jsonCoverageLines
        String jsonByTriggers
        deleteInstance.folders = "classes"
        String QUERY_CLASSES = "SELECT Name FROM ApexPage"
        toolingAPI = new ToolingAPI(credential)
        apexAPI = new ApexAPI(credential)
        jsonByClasses = toolingAPI.httpAPIClient.executeQuery(QUERY_CLASSES)

        println "Js : "+jsonByClasses

        when:
            deleteInstance.runTask()
        then:
            fileClass2.exists()
            fileClass3.exists()
            fileXML2.exists()
            fileXML3.exists()
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