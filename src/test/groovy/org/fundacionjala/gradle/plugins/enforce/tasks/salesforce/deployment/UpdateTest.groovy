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
        updateInstance.project.enforce.deleteTemporaryFiles = false
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
        def trigger = new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger').toString())
        def triggerXml = new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString())

        def mapMock = componentMonitor.getComponentsSignature([class1, class1Cls, class1ClsXml, object1__c, object1__c, trigger, triggerXml])
        componentSerializer.save(mapMock)

        credential = new Credential()
        credential.id = 'id'
        credential.username = 'salesforce2014.test@gmail.com'
        credential.password = '123qwe2014'
        credential.token = 'UO1Jx5vDQl97xCKkwXBH8tg3T'
        credential.loginFormat = LoginType.DEV.value()
        credential.type = 'normal'
    }


    def createTestFiles() {
        ArrayList<File> filesToTest = new ArrayList<File>()
        filesToTest.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class1.cls').toString()))
        filesToTest.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class1.cls-meta.xml').toString()))
        filesToTest.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class2.cls').toString()))
        filesToTest.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class2.cls-meta.xml').toString()))
        filesToTest.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class3.cls').toString()))
        filesToTest.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class3.cls-meta.xml').toString()))
        filesToTest.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger1.trigger').toString()))
        filesToTest.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger1.trigger-meta.xml').toString()))
        filesToTest.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger2.trigger').toString()))
        filesToTest.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger2.trigger-meta.xml').toString()))
        filesToTest.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger3.trigger').toString()))
        filesToTest.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger3.trigger-meta.xml').toString()))
        filesToTest.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object1__c.object').toString()))
        filesToTest.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object2__c.object').toString()))
        filesToTest.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object3__c.object').toString()))
        filesToTest.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object4__c.object').toString()))
        filesToTest.add(new File(Paths.get(SRC_PATH,'src_delete','objects','Object5__c.object').toString()))

        ArrayList<File> folders = new ArrayList<File>()
        folders.add(new File(Paths.get(SRC_PATH,'src_delete').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'src_delete','classes').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'src_delete','triggers').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'src_delete','objects').toString()))

        folders.each { folder->
            new File(folder.getAbsolutePath()).mkdir()
        }

        filesToTest.each { file->
            new File(file.getAbsolutePath()).createNewFile()
        }
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
            lineAux.size() == 1
            lineAux[0].contains('There are not files changed')
    }

    def "Test should create a package XML file" () {
        given:
            updateInstance.packageGenerator.fileTrackerMap = ['classes/Class1.cls':new ResultTracker(ComponentStates.ADDED)]
            updateInstance.packageGenerator.projectPath = Paths.get(SRC_PATH, 'src').toString()
            updateInstance.taskFolderPath = Paths.get(SRC_PATH, 'build', 'update').toString()
            updateInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            updateInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', 'package.xml').toString()
            updateInstance.taskPackagePath = Paths.get(SRC_PATH, 'build', 'update', 'package.xml').toString()
        when:
            updateInstance.createPackage()
        then:
            new File(Paths.get(Paths.get(SRC_PATH, 'build', 'update', 'package.xml').toString()).toString()).exists()
    }

    def "Test should create a package XML file empty if status is deleted" () {
        given:
            updateInstance.packageGenerator.fileTrackerMap = ['classes/Class1.cls':new ResultTracker(ComponentStates.ADDED)]
            updateInstance.packageGenerator.projectPath = Paths.get(SRC_PATH, 'src').toString()
            updateInstance.taskFolderPath = Paths.get(SRC_PATH, 'build', 'update').toString()
            updateInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            updateInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', 'package.xml').toString()
            updateInstance.taskPackagePath = Paths.get(SRC_PATH, 'build', 'update', 'package.xml').toString()
        when:
            updateInstance.createPackage()
        then:
            new File(Paths.get(Paths.get(SRC_PATH, 'build', 'update', 'package.xml').toString()).toString()).exists()
    }

    def "Test should create a package empty" () {
        given:
            updateInstance.packageGenerator.fileTrackerMap = [:]
            updateInstance.taskFolderPath = Paths.get(SRC_PATH, 'build', 'update').toString()
            updateInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            updateInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', 'package.xml').toString()
            updateInstance.taskPackagePath = Paths.get(SRC_PATH, 'build', 'update', 'package.xml').toString()
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
            updateInstance.taskFolderPath = Paths.get(SRC_PATH, 'build', 'update').toString()
            updateInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', 'package.xml').toString()
            updateInstance.taskPackagePath = Paths.get(SRC_PATH, 'build', 'update', 'destructiveChanges.xml').toString()
            updateInstance.credential = credential
            updateInstance.packageGenerator.credential = credential
            updateInstance.packageGenerator.project = project
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
            updateInstance.taskFolderPath = Paths.get(SRC_PATH, 'build').toString()
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
            updateInstance.showFilesChanged()
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
            def trigger = new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger').toString())
            componentMonitor.srcProject = Paths.get(SRC_PATH,'src').toString()
            componentSerializer.sourcePath = Paths.get(SRC_PATH,'src','.fileTracker.data').toString()
            def mapMock = componentMonitor.getComponentsSignature([class1Cls, object1__c,trigger])
            componentSerializer.save(mapMock)
            updateInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            updateInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            updateInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', 'package.xml').toString()
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
            updateInstance.setup()
            updateInstance.createDeploymentDirectory(updateInstance.taskFolderPath)
            updateInstance.loadFilesChanged()
            updateInstance.loadParameters()
            updateInstance.filterFiles()
            updateInstance.showFilesChanged()
            updateInstance.createDestructive()
            updateInstance.createPackage()
            updateInstance.copyFilesChanged()
            updateInstance.showFilesExcludes()
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
            updateInstance.project.enforce.deleteTemporaryFiles = true
            componentMonitor.srcProject = Paths.get(SRC_PATH,'src').toString()
            updateInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', 'package.xml').toString()
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
            def updateFolderPath = Paths.get(updateInstance.buildFolderPath, "update").toString()
            File updateFileZip = new File(updateFileZipPath)
            File updateFolder = new File(updateFolderPath)
        when:
            updateInstance.setup()
            updateInstance.createDeploymentDirectory(updateInstance.taskFolderPath)
            updateInstance.loadFilesChanged()
            updateInstance.loadParameters()
            updateInstance.filterFiles()
            updateInstance.showFilesChanged()
            updateInstance.createDestructive()
            updateInstance.createPackage()
            updateInstance.copyFilesChanged()
            updateInstance.showFilesExcludes()
            updateInstance.deleteTemporaryFiles()
        then:
            !updateFileZip.exists()
            !updateFolder.exists()
    }

    def "should upload the new reports, documents or dashboard added to the organization with package.xml updated"() {
        given:
            updateInstance.packageGenerator.projectPath = Paths.get(SRC_PATH, 'src').toString()
            updateInstance.packageGenerator.fileTrackerMap = [:]
            def class1Cls = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString())
            def object1__c = new File(Paths.get(SRC_PATH, 'src', 'objects', 'Object1__c.object').toString())
            def trigger = new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger').toString())
            componentMonitor.srcProject = Paths.get(SRC_PATH,'src').toString()
            componentSerializer.sourcePath = Paths.get(SRC_PATH,'src','.fileTracker.data').toString()
            def mapMock = componentMonitor.getComponentsSignature([class1Cls, object1__c,trigger])
            componentSerializer.save(mapMock)
            updateInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
            updateInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            updateInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', 'package.xml').toString()
            ManagementFile.createDirectories(Paths.get(SRC_PATH, 'src', 'reports/MyReports').toString())
            ManagementFile.createDirectories(Paths.get(SRC_PATH, 'src', 'dashboards/MyDashboards').toString())
            ManagementFile.createDirectories(Paths.get(SRC_PATH, 'src', 'documents/MyDocuments').toString())
            def newReportPath = Paths.get(SRC_PATH, 'src', 'reports/MyReports', 'newReport.report').toString()
            def newDashboardPath = Paths.get(SRC_PATH, 'src', 'dashboards/MyDashboards', 'newDashboard.dashboard').toString()
            def newDocumentPath = Paths.get(SRC_PATH, 'src', 'documents/MyDocuments', 'newDocument.txt').toString()
            def newDocumentXmlPath = Paths.get(SRC_PATH, 'src', 'documents/MyDocuments', 'newDocument.txt-meta.xml').toString()
            def myReportFolderXmlPath = Paths.get(SRC_PATH, 'src', 'reports/MyReports-meta.xml').toString()
            def myDashboardFolderXmlPath = Paths.get(SRC_PATH, 'src', 'dashboards/MyDashboards-meta.xml').toString()
            def myDocumentFolderXmlPath = Paths.get(SRC_PATH, 'src', 'documents/MyDocuments-meta.xml').toString()
            FileWriter newReportFile = new FileWriter(newReportPath)
            FileWriter newDashboardFile = new FileWriter(newDashboardPath)
            FileWriter newDocumentFile = new FileWriter(newDocumentPath)
            FileWriter newDocumentXmlFile = new FileWriter(newDocumentXmlPath)
            FileWriter myReportFolderXmlFile = new FileWriter(myReportFolderXmlPath)
            FileWriter myDashboardFolderXmlFile = new FileWriter(myDashboardFolderXmlPath)
            FileWriter myDocumentFolderXmlFile = new FileWriter(myDocumentFolderXmlPath)
            def reportContent = "<Report xmlns=\"http://soap.sforce.com/2006/04/metadata\">\n" +
                    "    <columns>\n" +
                    "        <field>USERS.NAME</field>\n" +
                    "    </columns>\n" +
                    "    <columns>\n" +
                    "        <field>ACCOUNT.NAME</field>\n" +
                    "    </columns>\n" +
                    "    <format>Tabular</format>\n" +
                    "    <name>newReport</name>\n" +
                    "    <params>\n" +
                    "        <name>co</name>\n" +
                    "        <value>1</value>\n" +
                    "    </params>\n" +
                    "    <reportType>AccountList</reportType>\n" +
                    "    <scope>user</scope>\n" +
                    "    <showDetails>true</showDetails>\n" +
                    "    <timeFrameFilter>\n" +
                    "        <dateColumn>CREATED_DATE</dateColumn>\n" +
                    "        <interval>INTERVAL_CUSTOM</interval>\n" +
                    "        <startDate>2015-05-18</startDate>\n" +
                    "    </timeFrameFilter>\n" +
                    "</Report>"
            def dashboardContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<Dashboard xmlns=\"http://soap.sforce.com/2006/04/metadata\">\n" +
                    "    <backgroundEndColor>#FFFFFF</backgroundEndColor>\n" +
                    "    <backgroundFadeDirection>Diagonal</backgroundFadeDirection>\n" +
                    "    <backgroundStartColor>#FFFFFF</backgroundStartColor>\n" +
                    "    <dashboardType>SpecifiedUser</dashboardType>\n" +
                    "    <leftSection>\n" +
                    "        <columnSize>Medium</columnSize>\n" +
                    "    </leftSection>\n" +
                    "    <middleSection>\n" +
                    "        <columnSize>Medium</columnSize>\n" +
                    "    </middleSection>\n" +
                    "    <rightSection>\n" +
                    "        <columnSize>Medium</columnSize>\n" +
                    "    </rightSection>\n" +
                    "    <runningUser>alex.rv11@gmail.com</runningUser>\n" +
                    "    <textColor>#000000</textColor>\n" +
                    "    <title>newDashboard</title>\n" +
                    "    <titleColor>#000000</titleColor>\n" +
                    "    <titleSize>12</titleSize>\n" +
                    "</Dashboard>"
            def documentContent = "the test adds this document"
            def documentXmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<Document xmlns=\"http://soap.sforce.com/2006/04/metadata\">\n" +
                    "    <internalUseOnly>false</internalUseOnly>\n" +
                    "    <name>newDocument</name>\n" +
                    "    <public>false</public>\n" +
                    "</Document>"
            def myReportFolderXmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<ReportFolder xmlns=\"http://soap.sforce.com/2006/04/metadata\">\n" +
                    "    <folderShares>\n" +
                    "        <accessLevel>Manage</accessLevel>\n" +
                    "        <sharedTo>alex.rv11@gmail.com</sharedTo>\n" +
                    "        <sharedToType>User</sharedToType>\n" +
                    "    </folderShares>\n" +
                    "    <name>testFolder</name>\n" +
                    "</ReportFolder>"
            def myDashboardFolderXmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<DashboardFolder xmlns=\"http://soap.sforce.com/2006/04/metadata\">\n" +
                    "    <folderShares>\n" +
                    "        <accessLevel>Manage</accessLevel>\n" +
                    "        <sharedTo>alex.rv11@gmail.com</sharedTo>\n" +
                    "        <sharedToType>User</sharedToType>\n" +
                    "    </folderShares>\n" +
                    "    <name>DashboardFolder</name>\n" +
                    "</DashboardFolder>"
            def myDocumentFolderXmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<DocumentFolder xmlns=\"http://soap.sforce.com/2006/04/metadata\">\n" +
                    "    <accessType>Public</accessType>\n" +
                    "    <name>DocumentTest</name>\n" +
                    "    <publicFolderAccess>ReadWrite</publicFolderAccess>\n" +
                    "</DocumentFolder>"
            newReportFile.write(reportContent)
            newDashboardFile.write(dashboardContent)
            newDocumentFile.write(documentContent)
            newDocumentXmlFile.write(documentXmlContent)
            myReportFolderXmlFile.write(myReportFolderXmlContent)
            myDashboardFolderXmlFile.write(myDashboardFolderXmlContent)
            myDocumentFolderXmlFile.write(myDocumentFolderXmlContent)
            newReportFile.close()
            newDashboardFile.close()
            newDocumentFile.close()
            newDocumentXmlFile.close()
            myReportFolderXmlFile.close()
            myDashboardFolderXmlFile.close()
            myDocumentFolderXmlFile.close()
            def packageExpect = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Package xmlns=\"http://soap.sforce.com/2006/04/metadata\">\n" +
                    "\t<types>\n" +
                    "\t\t<members>MyDashboards/newDashboard</members>\n" +
                    "\t\t<name>Dashboard</name>\n" +
                    "\t</types>\n" +
                    "\t<types>\n" +
                    "\t\t<members>MyDocuments/newDocument</members>\n" +
                    "\t\t<name>Document</name>\n" +
                    "\t</types>\n" +
                    "\t<types>\n" +
                    "        <members>MyReports/newReport</members>\n" +
                    "    <name>Report</name>\n" +
                    "\t</types>\n" +
                    "\t<version>32.0</version>\n" +
                    "</Package>"
        when:
            updateInstance.setup()
            updateInstance.createDeploymentDirectory(updateInstance.taskFolderPath)
            updateInstance.loadFilesChanged()
            updateInstance.loadParameters()
            updateInstance.filterFiles()
            updateInstance.showFilesChanged()
            updateInstance.createPackage()
            updateInstance.copyFilesChanged()
            updateInstance.showFilesExcludes()
            def packageXml =  new File(Paths.get(SRC_PATH, 'build', 'update', 'package.xml').toString()).text
            XMLUnit.ignoreWhitespace = true
            def packageXmlDifference = new Diff(packageXml, packageExpect)
        then:
            packageXmlDifference.similar()
            new File((Paths.get(SRC_PATH, 'build', 'update', 'reports/MyReports', 'newReport.report').toString())).exists()
            new File((Paths.get(SRC_PATH, 'build', 'update', 'dashboards/MyDashboards', 'newDashboard.dashboard').toString())).exists()
            new File((Paths.get(SRC_PATH, 'build', 'update', 'documents/MyDocuments', 'newDocument.txt').toString())).exists()
            new File((Paths.get(SRC_PATH, 'build', 'update', 'documents/MyDocuments', 'newDocument.txt-meta.xml').toString())).exists()
            new File((Paths.get(SRC_PATH, 'build', 'update', 'reports/MyReports-meta.xml').toString())).exists()
            new File((Paths.get(SRC_PATH, 'build', 'update', 'dashboards/MyDashboards-meta.xml').toString())).exists()
            new File((Paths.get(SRC_PATH, 'build', 'update', 'documents/MyDocuments-meta.xml').toString())).exists()
    }

    def "Test should return the all cls files excludes with parameter ['excludes','classes/**'] " () {
        given:
            ArrayList<File> expectedFiles = []
            expectedFiles.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class1.cls').toString()))
            expectedFiles.add(new File(Paths.get(SRC_PATH,'src_delete','classes','Class2.cls').toString()))

            Map<String, ResultTracker> trackerMap = [:]
            trackerMap.put('classes/Class1.cls',new ResultTracker(ComponentStates.ADDED))
            trackerMap.put('classes/Class2.cls',new ResultTracker(ComponentStates.ADDED))
            trackerMap.put('classes/Class3.cls',new ResultTracker(ComponentStates.DELETED))
            trackerMap.put('classes/Class4.cls',new ResultTracker(ComponentStates.DELETED))
            trackerMap.put('classes/Class5.cls',new ResultTracker(ComponentStates.DELETED))
            trackerMap.put('classes/Class6.cls',new ResultTracker(ComponentStates.DELETED))
            trackerMap.put('triggers/Trigger1.trigger',new ResultTracker(ComponentStates.ADDED))
            trackerMap.put('triggers/Trigger2.trigger',new ResultTracker(ComponentStates.DELETED))
            trackerMap.put('triggers/Trigger3.trigger',new ResultTracker(ComponentStates.DELETED))
            trackerMap.put('triggers/Trigger4.trigger',new ResultTracker(ComponentStates.DELETED))
            trackerMap.put('triggers/Trigger5.trigger',new ResultTracker(ComponentStates.DELETED))
            trackerMap.put('triggers/Trigger6.trigger',new ResultTracker(ComponentStates.DELETED))

            updateInstance.setup()
            updateInstance.credential = credential
            updateInstance.parameters.put('excludes','classes/**')
            updateInstance.loadFilesChanged()
            updateInstance.loadParameters()
            updateInstance.projectPath = Paths.get(SRC_PATH, 'src_delete').toString()
            updateInstance.filter.projectPath = Paths.get(SRC_PATH, 'src_delete').toString()
            updateInstance.packageGenerator.projectPath = Paths.get(SRC_PATH, 'src_delete').toString()
            updateInstance.packageGenerator.credential = credential
            updateInstance.packageGenerator.project = project
            updateInstance.packageGenerator.fileTrackerMap = trackerMap;
        when:
            createTestFiles()
            updateInstance.filterFiles()
            ArrayList<File> filesExcludes = updateInstance.filesExcludes
        then:
            filesExcludes.sort() == expectedFiles.sort()
    }


    def "Test should return the all cls files excludes with parameter ['excludes','triggers/**'] " () {
        given:
            ArrayList<File> expectedFiles = []
            expectedFiles.add(new File(Paths.get(SRC_PATH,'src_delete','triggers','Trigger1.trigger').toString()))

            Map<String, ResultTracker> trackerMap = [:]
            trackerMap.put('classes/Class1.cls',new ResultTracker(ComponentStates.DELETED))
            trackerMap.put('classes/Class2.cls',new ResultTracker(ComponentStates.ADDED))
            trackerMap.put('classes/Class3.cls',new ResultTracker(ComponentStates.ADDED))
            trackerMap.put('triggers/Trigger1.trigger',new ResultTracker(ComponentStates.ADDED))
            trackerMap.put('triggers/Trigger5.trigger',new ResultTracker(ComponentStates.DELETED))
            trackerMap.put('triggers/Trigger6.trigger',new ResultTracker(ComponentStates.DELETED))

            updateInstance.setup()
            updateInstance.credential = credential
            updateInstance.parameters.put('excludes','triggers/**')
            updateInstance.loadFilesChanged()
            updateInstance.loadParameters()
            updateInstance.projectPath = Paths.get(SRC_PATH, 'src_delete').toString()
            updateInstance.filter.projectPath = Paths.get(SRC_PATH, 'src_delete').toString()
            updateInstance.packageGenerator.projectPath = Paths.get(SRC_PATH, 'src_delete').toString()
            updateInstance.packageGenerator.credential = credential
            updateInstance.packageGenerator.project = project
            updateInstance.packageGenerator.fileTrackerMap = trackerMap;
        when:
            createTestFiles()
            updateInstance.filterFiles()
            ArrayList<File> filesExcludes = updateInstance.filesExcludes
        then:
            filesExcludes.sort() == expectedFiles.sort()
    }

    def cleanup() {
        new File(Paths.get(SRC_PATH,'src_delete').toString()).deleteDir()
        new File(Paths.get(SRC_PATH, 'classes', 'Class2.cls').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls-meta.xml').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', '.fileTracker.data').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'reports/MyReports', 'newReport.report').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'dashboards/MyDashboards', 'newDashboard.dashboard').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'documents/MyDocuments', 'newDocument.txt-meta.xml').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'documents/MyDocuments', 'newDocument.txt').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'reports/MyReports-meta.xml').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'dashboards/MyDashboards-meta.xml').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'documents/MyDocuments-meta.xml').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'reports/MyReports').toString()).deleteDir()
        new File(Paths.get(SRC_PATH, 'src', 'dashboards/MyDashboards').toString()).deleteDir()
        new File(Paths.get(SRC_PATH, 'src', 'documents/MyDocuments').toString()).deleteDir()
    }
}