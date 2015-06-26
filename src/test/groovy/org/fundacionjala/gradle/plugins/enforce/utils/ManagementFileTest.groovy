/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.utils

import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.FileValidator
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

class ManagementFileTest extends Specification {

    @Shared
    ManagementFile managementFile

    @Shared
    String targetPath

    def setup() {
        targetPath = Paths.get(System.getProperty("user.dir"), 'src', 'test', 'groovy', 'org', 'fundacionjala', 'gradle', 'plugins','enforce', 'utils', 'resources').toString()
        managementFile = new ManagementFile(targetPath)
    }

    def "Test get valid elements with path source"() {
        when:
            ArrayList<File> arrayResult = managementFile.getValidElements(targetPath)
            ArrayList<File> arrayExpected = [new File(Paths.get(targetPath, 'classes', 'class1.cls').toString()),
                                             new File(Paths.get(targetPath, 'classes', 'class1.cls-meta.xml').toString()),
                                             new File(Paths.get(targetPath, 'objects', 'Account.object').toString()),
                                             new File(Paths.get(targetPath, 'objects', 'Object1__c.object').toString()),
                                             new File(Paths.get(targetPath, 'objects', 'Object2__c.object').toString()),
                                             new File(Paths.get(targetPath, 'reports', 'testFolder', 'testReport.report').toString()),
                                             new File(Paths.get(targetPath, 'reports', 'testFolder-meta.xml').toString()),
                                             new File(Paths.get(targetPath, 'documents', 'mydocs/doc1.doc').toString()),
                                             new File(Paths.get(targetPath, 'documents', 'mydocs/image.png').toString()),
                                             new File(Paths.get(targetPath, 'documents', 'mydocs/doc1.doc-meta.xml').toString()),
                                             new File(Paths.get(targetPath, 'documents', 'mydocs/image.png-meta.xml').toString()),
                                             new File(Paths.get(targetPath, 'package.xml').toString())]
        then:
            arrayResult.sort() == arrayExpected.sort()
    }

    def "Test get valid elements with path source excluding type of files"() {
        given:
        ArrayList<String> typesToExclude = ['xml']
        when:
        ArrayList<File> arrayResult = managementFile.getValidElements(targetPath, typesToExclude)
        then:
        arrayResult.sort() == [new File(Paths.get(targetPath, 'classes', 'class1.cls').toString()),
                               new File(Paths.get(targetPath, 'objects', 'Account.object').toString()),
                               new File(Paths.get(targetPath, 'objects', 'Object1__c.object').toString()),
                               new File(Paths.get(targetPath, 'objects', 'Object2__c.object').toString()),
                               new File(Paths.get(targetPath, 'documents', 'mydocs/doc1.doc').toString()),
                               new File(Paths.get(targetPath, 'documents', 'mydocs/image.png').toString()),
                               new File(Paths.get(targetPath, 'reports', 'testFolder', 'testReport.report').toString())].sort()
    }

    def "Test should return all the files because there isn't files to exclude"() {
        given:
        ArrayList<String> typesToExclude = []
        when:
        ArrayList<File> arrayResult = managementFile.getValidElements(targetPath, typesToExclude)
        then:
        arrayResult.sort() == [new File(Paths.get(targetPath, 'classes', 'class1.cls').toString()),
                               new File(Paths.get(targetPath, 'classes', 'class1.cls-meta.xml').toString()),
                               new File(Paths.get(targetPath, 'objects', 'Account.object').toString()),
                               new File(Paths.get(targetPath, 'objects', 'Object1__c.object').toString()),
                               new File(Paths.get(targetPath, 'objects', 'Object2__c.object').toString()),
                               new File(Paths.get(targetPath, 'documents', 'mydocs/doc1.doc').toString()),
                               new File(Paths.get(targetPath, 'documents', 'mydocs/image.png').toString()),
                               new File(Paths.get(targetPath, 'documents', 'mydocs/doc1.doc-meta.xml').toString()),
                               new File(Paths.get(targetPath, 'documents', 'mydocs/image.png-meta.xml').toString()),
                               new File(Paths.get(targetPath, 'package.xml').toString()),
                               new File(Paths.get(targetPath, 'reports', 'testFolder', 'testReport.report').toString()),
                               new File(Paths.get(targetPath, 'reports', 'testFolder-meta.xml').toString())].sort()
    }

    def "Test should return only a class, the others are excluded"() {
        given:
            ArrayList<String> typesToExclude = ['object', 'xml', 'report']
        when:
            ArrayList<File> arrayResult = managementFile.getValidElements(targetPath, typesToExclude)
        then:
            arrayResult.sort() == [new File(Paths.get(targetPath, 'classes', 'class1.cls').toString()),
                                   new File(Paths.get(targetPath, 'documents', 'mydocs/doc1.doc').toString()),
                                   new File(Paths.get(targetPath, 'documents', 'mydocs/image.png').toString())].sort()
    }

    def "Test should copy from source path"() {
        given:
            def pathFrom = targetPath

            def pathDeploy = Paths.get(targetPath, 'deploy').toString()
            new File(pathDeploy).mkdir()
            def pathFolderClasses = Paths.get(pathDeploy, 'classes').toString()
            def pathClass1 = Paths.get(pathFolderClasses, 'class1.cls').toString()
            def pathOther = Paths.get(pathFolderClasses, 'other.data').toString()

            def pathFolderObjects = Paths.get(pathDeploy, 'objects').toString()
            def pathAccount = Paths.get(pathFolderObjects, 'Account.object').toString()
            def pathObject1 = Paths.get(pathFolderObjects, 'Object1__c.object').toString()
            def pathObject2 = Paths.get(pathFolderObjects, 'Object2__c.object').toString()
        when:
            managementFile.copy(pathFrom, pathDeploy)
        then:
        new File(pathDeploy).exists()
            new File(pathFolderClasses).exists()
            new File(pathClass1).exists()
            !new File(pathOther).exists()

            new File(pathFolderObjects).exists()
            new File(pathAccount).exists()
            new File(pathObject1).exists()
            new File(pathObject2).exists()

            new File(pathDeploy).deleteDir()
    }

    def "Test copy from array file path"() {

        given:
            def pathFrom = targetPath
            def pathDeploy = Paths.get(targetPath, 'deploy').toString()
            new File(pathDeploy).mkdir()
            def pathClass1 = Paths.get(pathDeploy, 'classes', 'class1.cls').toString()
            def pathOther = Paths.get(pathDeploy, 'classes', 'other.data').toString()

            def pathAccount = Paths.get(pathDeploy, 'objects', 'Account.object').toString()
            def pathObject1 = Paths.get(pathDeploy, 'objects', 'Object1__c.object').toString()
            def pathPackage = Paths.get(pathDeploy, 'package.xml').toString()

            ArrayList<File> arrayFile = [new File(Paths.get(pathFrom, 'classes', 'class1.cls').toString()),
                                         new File(Paths.get(pathFrom, 'classes', 'other.data').toString()),
                                         new File(Paths.get(pathFrom, 'objects', 'Account.object').toString()),
                                         new File(Paths.get(pathFrom, 'objects', 'Object1__c.object').toString()),
                                         new File(Paths.get(pathFrom, 'package.xml').toString()),]

        when:
            managementFile.copy(pathFrom, arrayFile, pathDeploy)
        then:
            File fileClass = new File(pathClass1)
            File fileOther = new File(pathOther)
            File fileAccount = new File(pathAccount)
            File fileObject1 = new File(pathObject1)
            File filePackage = new File(pathPackage)
            fileClass.exists()
            fileClass.isFile()
            fileOther.exists()
            fileOther.isFile()
            fileAccount.exists()
            fileAccount.isFile()
            fileObject1.exists()
            fileObject1.isFile()
            filePackage.exists()
            filePackage.isFile()
            new File(pathDeploy).deleteDir()
    }

    def "Test copy from array file that contains sub folders to target path"() {

        given:
            def pathFrom = targetPath
            def pathDeploy = Paths.get(targetPath, 'deploy').toString()
            new File(pathDeploy).mkdir()
            def pathClass1 = Paths.get(pathDeploy, 'classes', 'class1.cls').toString()
            def pathOther = Paths.get(pathDeploy, 'classes', 'other.data').toString()

            def pathAccount = Paths.get(pathDeploy, 'objects', 'Account.object').toString()
            def pathObject1 = Paths.get(pathDeploy, 'objects', 'Object1__c.object').toString()

            def pathTestReport = Paths.get(pathDeploy, 'reports', 'testFolder', 'testReport.report').toString()
            def pathTestReportXML = Paths.get(pathDeploy, 'reports', 'testFolder-meta.xml').toString()
            def pathPackage = Paths.get(pathDeploy, 'package.xml').toString()

            ArrayList<File> arrayFile = [new File(Paths.get(pathFrom, 'classes', 'class1.cls').toString()),
                                         new File(Paths.get(pathFrom, 'classes', 'other.data').toString()),
                                         new File(Paths.get(pathFrom, 'objects', 'Account.object').toString()),
                                         new File(Paths.get(pathFrom, 'objects', 'Object1__c.object').toString()),
                                         new File(Paths.get(pathFrom, 'reports', 'testFolder', 'testReport.report').toString()),
                                         new File(Paths.get(pathFrom, 'reports', 'testFolder-meta.xml').toString()),
                                         new File(Paths.get(pathFrom, 'package.xml').toString()),]

        when:
            managementFile.copy(pathFrom, arrayFile, pathDeploy)
        then:

            new File(pathClass1).exists()
            new File(pathOther).exists()
            new File(pathAccount).exists()
            new File(pathObject1).exists()
            new File(pathPackage).exists()
            new File(pathTestReport).exists()
            new File(pathTestReportXML).exists()
            new File(pathDeploy).deleteDir()
    }

    def "Test load directories not deploy"() {
        expect:
            managementFile.getFoldersNotDeploy(targetPath).sort() == ["web", "encoding"].sort()
    }

    def "should throw a exception because managementFie constructor value is nothing"() {
        given:
            managementFile = new ManagementFile(targetPath)
        when:
            managementFile.validatePackage(new File(''))
        then:
            thrown(Exception)
    }

    def "should not throw an exception because managementFie has a value in it's constructor"() {
        when:
            managementFile.validatePackage(new File(Paths.get(targetPath, 'classes', 'package.xml').toString()))
        then:
            noExceptionThrown()
    }

    def "should be the correct path of package.xml"() {
        when:
            def value = managementFile.validatePackage(new File(Paths.get(targetPath, 'package.xml').toString()))
        then:
            value
    }

    def "should be null because is not the correct path of package.xml"() {
        when:
            def value = managementFile.validatePackage(new File(Paths.get(targetPath, 'class', 'package.xml').toString()))
        then:
            !value
    }

    def "should be return array files by array folders"() {
        given:
            ArrayList<File> arrayFile = [new File(Paths.get(targetPath, 'classes', 'class1.cls').toString()),
                                         new File(Paths.get(targetPath, 'classes', 'class1.cls-meta.xml').toString()),
                                         new File(Paths.get(targetPath, 'objects', 'Account.object').toString()),
                                         new File(Paths.get(targetPath, 'objects', 'Object1__c.object').toString()),
                                         new File(Paths.get(targetPath, 'objects', 'Object2__c.object').toString()),
                                         new File(Paths.get(targetPath, 'reports', 'testFolder','testReport.report').toString()),
                                         new File(Paths.get(targetPath, 'reports', 'testFolder-meta.xml').toString())]
            def arrayFolders = ['classes', 'objects', 'reports']
        when:
            def arrayResult = managementFile.getFilesByFolders(targetPath, arrayFolders)
        then:
            arrayResult.sort() == arrayFile.sort()
    }

    def "should get a xml file"() {
        given:
            def file = new File(Paths.get(targetPath, "classes", "class1.cls").toString())
        when:
            def xmlFile = managementFile.getValidateXmlFile(file)
        then:
            xmlFile == new File(Paths.get(targetPath, "classes", "class1.cls-meta.xml").toString())
    }

    def "should return null because there isn't a xml file"() {
        given:
            def file = new File(Paths.get(targetPath, "objects", "Account.object").toString())
        when:
            def xmlFile = managementFile.getValidateXmlFile(file)
        then:
            xmlFile == null
    }


    def "should get files form source directory by file extension"() {
        given:
        def fileNames = []
        when:
        def files = managementFile.getFilesByFileExtension('object')
        files.each {
            fileNames.add(it.name)
        }
        then:
        fileNames.sort() == ['Account.object', 'Object1__c.object', 'Object2__c.object'].sort()
    }

    def "should get subdirectories form source directory by file extension"() {
        given:
            def dirNames = []
        when:
            def files = managementFile.getSubdirectories()
            files.each {
                dirNames.add(it.name)
            }
        then:
            dirNames.sort() == ['classes', 'documents', 'encoding', 'objects', 'reports', 'web'].sort()

    }

    def "Test should create the directories if it doesn't exist"() {
        expect:
        Path path = Paths.get(targetPath, 'test', 'testOne', 'testTwo', 'testThree')
        ManagementFile.createDirectories(path.toString())
        new File(path.toString()).exists()
    }

    def cleanupSpec() {
        new File(Paths.get(targetPath, 'test').toString()).deleteDir()
    }
}