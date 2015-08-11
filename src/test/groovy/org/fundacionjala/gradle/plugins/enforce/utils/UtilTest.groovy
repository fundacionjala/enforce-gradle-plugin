/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.utils

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentHash
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class UtilTest extends Specification {
    @Shared
        String RESOURCES_PATH

    def setup() {
        RESOURCES_PATH = Paths.get(System.getProperty("user.dir"), 'src', 'test', 'groovy', 'org', 'fundacionjala',
                'gradle', 'plugins','enforce', 'utils', 'resources').toString()
    }

    def "Test should return true if emails are valid"() {
        given:
            String emailCommon = 'Juan.Perez@gmail.com'
            String emailWithManyDotsAfterAtSign = 'lucas.cdlv@gmail.com.lucas01.juan.com.cls'
            String emailWithUnderscoreBeforeAtSign = 'lucas.cdlv_12312@gmail.com'
            String emailWithUnderscoreAfterAtSign = 'lucas.cdlv@gmail.com_m.cdfre'
            String emailWithManyDotsBeforeAtSign = 'lucas.cdlv.dev@gmail.com'
            String emailWithHyphen = 'luc-asd@gmail.com'
            String emailWithHyphenBeforeAndAfterAtSign = 'Liz-juan.perez.cochabamba@ohoho.com-jala.extension'
        when:
            Boolean resultEmailCommon = Util.validEmail(emailCommon)
            Boolean resultWithManyDotsAfterAtSign = Util.validEmail(emailWithManyDotsAfterAtSign)
            Boolean resultWithUnderscoreBeforeAtSign = Util.validEmail(emailWithUnderscoreBeforeAtSign)
            Boolean resultWithUnderscoreAfterAtSign = Util.validEmail(emailWithUnderscoreAfterAtSign)
            Boolean resultWithManyDotsBeforeAtSign = Util.validEmail(emailWithManyDotsBeforeAtSign)
            Boolean resultWithHyphen = Util.validEmail(emailWithHyphen)
            Boolean resultWithHyphenBeforeAndAfterAtSign = Util.validEmail(emailWithHyphenBeforeAndAfterAtSign)
        then:
            resultEmailCommon
            resultWithManyDotsAfterAtSign
            resultWithUnderscoreBeforeAtSign
            resultWithUnderscoreAfterAtSign
            resultWithManyDotsBeforeAtSign
            resultWithHyphen
            resultWithHyphenBeforeAndAfterAtSign
    }

    def "Test should return false if email are invalid"() {
        given:
            String emailWithoutAtSign = 'Juan.Perezmail.com'
            String emailWithoutDotsAfterAtSign = 'Juan.Perezmail@com'
            String emailWithBlankSpaceBeforeAtSign = 'pedro blanco@jalasoft.com'
            String emailWithBlankSpaceAfterAtSign = 'juan.lucas@jala.open test.com'
        when:
            Boolean resultWithoutAtSign = Util.validEmail(emailWithoutAtSign)
            Boolean resultWithoutDotsAfterAtSign = Util.validEmail(emailWithoutDotsAfterAtSign)
            Boolean resultWithBlankSpaceBeforeAtSign = Util.validEmail(emailWithBlankSpaceBeforeAtSign)
            Boolean resultWithBlankSpaceAfterAtSign = Util.validEmail(emailWithBlankSpaceAfterAtSign)
        then:
            !resultWithoutAtSign
            !resultWithoutDotsAfterAtSign
            !resultWithBlankSpaceBeforeAtSign
            !resultWithBlankSpaceAfterAtSign
    }

    def "Test Should return hour format" () {

        expect:
            "01:00:00:00" == Util.formatDurationHMS(3600000)

    }

    def "Test should return a empty arrayList if folders are valid" () {
        given:
            def foldersName = ['classes', 'objects', 'triggers']
        when:
            def result = Util.getInvalidFolders(foldersName)
        then:
            result.empty
    }

    def "Test should return an arrayList with invalid folders" () {
        given:
            def foldersName = ['invalidFolder', 'objects', 'exit']
        when:
            def result = Util.getInvalidFolders(foldersName)
        then:
            result.sort() == ['invalidFolder', 'exit'].sort()
    }

    def "Test should return an arrayList empty if the folders aren't empty" () {
        given:
            def foldersName = ['objects', 'classes']
            def projectPath = RESOURCES_PATH
        when:
            def result = Util.getEmptyFolders(foldersName, projectPath)
        then:
            result.empty
    }

    def "Test should return an arrayList with folders empty" () {
        given:
            new File(Paths.get(RESOURCES_PATH, 'triggers').toString()).mkdir()
            def foldersName = ['objects', 'classes', 'triggers']
            def projectPath = RESOURCES_PATH
        when:
            def result = Util.getEmptyFolders(foldersName, projectPath)
        then:
            result == ['triggers']
    }

    def "Test should return an extension from a file" () {
        given:
            def file = new File(Paths.get(RESOURCES_PATH, 'objects', 'Maintenance_Ticket_WO__c.object').toString())
        when:
            def extension = Util.getFileExtension(file)
        then:
            extension == 'object'
    }

    def "Test should return an extension from a file if there is more than one dot" () {
        given:
            def file = new File(Paths.get(RESOURCES_PATH, 'objects', 'My_New.CustomObject__c.object').toString())
        when:
            def extension = Util.getFileExtension(file)
        then:
            extension == "object"
    }

    def "Test should get mac address with size equals 12" () {
        when:
            def macAddress = Util.getMacAddress()
        then:
            macAddress.size() == 12
    }

    def "Test should get developerName from a fullName" () {
        when:
        def developerName = Util.getDeveloperName('TwilioConfig__c.AuthTokenAPI__c.sbc')
        then:
        developerName == 'AuthTokenAPI'
    }

    def "Test should get developerName from a member" () {
        when:
            def developerName = Util.getDeveloperNameByMember('TwilioConfig__c.AuthTokenAPI__c', 'CustomField')
        then:
            developerName == 'AuthTokenAPI'
    }

    def "Test should get developerName from a member of type componentLayout" () {
        when:
        def developerName = Util.getDeveloperNameByMember('Object5__c.myCompactLayout', 'CompactLayout')
        then:
        developerName == 'myCompactLayout'
    }

    def "Test get relative path"() {
        given:
        String basePath = Paths.get(RESOURCES_PATH, 'relativeTest')
        File file = new File(Paths.get(basePath, "classes/class1").toString())
        when:
        def relativePath = Util.getRelativePath(file, basePath)
        then:
        relativePath == Paths.get("classes", "class1").toString()
    }

    def "Test should gets relative path when the path has a space"() {
        given:
            String basePath = Paths.get(RESOURCES_PATH, 'relativeTest')
            File file = new File(Paths.get(basePath, "classes/class 1").toString())
        when:
            def relativePath = Util.getRelativePath(file, basePath)
        then:
            relativePath == Paths.get("classes", "class 1").toString()
    }

    def "Test should return object name from sub component member" () {
        when:
            def objectName = Util.getObjectName('Object5__c.MyCustomField')
        then:
            objectName == 'Object5__c'
    }

    def "Test should return true if api name has prefix" () {
        when:
            def result = Util.isPackaged('myprefix__CustomObject__c')
        then:
            result
    }

    def "Test should return false if api name hasn't perfix" () {
        when:
            def result = Util.isPackaged('CustomObject__c')
        then:
            !result
    }

    def "Test should get file encoding" () {
        given:
            def path = Paths.get(RESOURCES_PATH, 'encoding').toString()
            def fileEncodingUTF16BE = new File(Paths.get(path, "Client.cls").toString())
            def fileEncodingUTF16LE = new File(Paths.get(path, "Client.cls-meta.xml").toString())
            def fileEncodingUTF8 = new File(Paths.get(path, "Opportunity.cls").toString())
            def fileEncodingUTF16LE1 = new File(Paths.get(path, "Opportunity.cls-meta.xml").toString())
        when:
            def encodingUTF16BE = Util.getCharset(fileEncodingUTF16BE)
            def encodingUTF16LE = Util.getCharset(fileEncodingUTF16LE)
            def encodingUTF8 = Util.getCharset(fileEncodingUTF8)
            def encodingUTF16LE1 = Util.getCharset(fileEncodingUTF16LE1)
        then:
            encodingUTF16BE
            encodingUTF16LE
            encodingUTF8
            encodingUTF16LE1
    }

    def "Test should get components with wildcard"() {
        given:
            def standardComponents = ['Account.object', 'Opportunity.object', 'Contact.object', 'Admin.profile', 'CMC.app']
        when:
            def result = Util.getComponentsWithWildcard(standardComponents)
        then:
            result == ['**/Account.object', "**/Opportunity.object", "**/Contact.object", "**/Admin.profile", "**/CMC.app"]
    }

    def 'Should get the custom fields from an standard object file'() {
        given:
            def expected = ["Account.MyLookupField1__c", "Account.MyLookupField2__c"]
            def path = Paths.get(RESOURCES_PATH, "objects", "Account.object").toString()
        when:
            def result = Util.getCustomFields(new File(path))
        then:
            result.size() == 2
            expected == result
    }

    def "Test should get includes value by folder only of files updated" () {
        given:
            ArrayList<File> files = [new File(Paths.get(RESOURCES_PATH, 'classes', 'Class1.cls').toString()),
                                     new File(Paths.get(RESOURCES_PATH, 'classes', 'Class2.cls').toString()),
                                     new File(Paths.get(RESOURCES_PATH, 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(RESOURCES_PATH, 'objects', 'Object2__c.object').toString()),
                                     new File(Paths.get(RESOURCES_PATH, 'pages', 'Page1.page').toString()),
                                     new File(Paths.get(RESOURCES_PATH, 'triggers', 'Trigger1.trigger').toString())]
            String parameterValue = "classes,objects"
        when:
            String result = Util.getIncludesValueByFolderFromFilesUpdated(files, parameterValue,RESOURCES_PATH)
        then:
            result == ["${'classes'}${File.separator}${'Class1.cls'}",
                       "${'classes'}${File.separator}${'Class2.cls'}",
                       "${'objects'}${File.separator}${'Object1__c.object'}",
                       "${'objects'}${File.separator}${'Object2__c.object'}"].join(', ')
    }

    def "Test should get includes value by folder only of files updated when you sent reports or documents" () {
        given:
            ArrayList<File> files = [new File(Paths.get(RESOURCES_PATH, 'reports', 'MyFolder', 'MyReport.report').toString()),
                                     new File(Paths.get(RESOURCES_PATH, 'documents', 'MyDocuments', 'MyDocument.txt').toString()),
                                     new File(Paths.get(RESOURCES_PATH, 'objects', 'Object2__c.object').toString()),
                                     new File(Paths.get(RESOURCES_PATH, 'pages', 'Page1.page').toString()),]
            String parameterValue = "reports,documents"
            String projectPath = RESOURCES_PATH
        when:
            String result = Util.getIncludesValueByFolderFromFilesUpdated(files, parameterValue,projectPath)
        then:
            result == ["${'reports'}${File.separator}${'MyFolder'}${File.separator}${'MyReport.report'}",
                       "${'documents'}${File.separator}${'MyDocuments'}${File.separator}${'MyDocument.txt'}"].join(', ')
    }

    def "Test should return a map with their relative paths as key" () {
        given:
            Map<String, ComponentHash> recoveryFileHashCode = [:]
            String objectPath = Paths.get(RESOURCES_PATH, 'objects', 'Object2__c.object')
            String documentPath = Paths.get(RESOURCES_PATH, 'documents', 'MyDocuments', 'MyDocument.txt')

            ComponentHash componentHash = new ComponentHash()
            componentHash.fileName = objectPath
            componentHash.hash = "objectHash123qweads"

            ComponentHash componentHashDocument = new ComponentHash()
            componentHashDocument.fileName = documentPath
            componentHashDocument.hash = "documentHash123qweads"

            recoveryFileHashCode.put(objectPath, componentHash)
            recoveryFileHashCode.put(documentPath, componentHashDocument)

            String projectPath = RESOURCES_PATH
            String fileNameExpect = Paths.get('objects', 'Object2__c.object').toString()
            String documentNameExpect = Paths.get('documents', 'MyDocuments', 'MyDocument.txt').toString()
        when:
            Map<String, ComponentHash> result = Util.getFilesWithTheirRelativePaths(recoveryFileHashCode, projectPath)
        then:
            result.containsKey(fileNameExpect)
            result.get(Paths.get(fileNameExpect).toString()).fileName == fileNameExpect
            result.get(Paths.get(fileNameExpect).toString()).hash == componentHash.hash

            result.containsKey(documentNameExpect)
            result.get(Paths.get(documentNameExpect).toString()).fileName == documentNameExpect
            result.get(Paths.get(documentNameExpect).toString()).hash == componentHashDocument.hash
    }

    def "Test should return a map with their relative paths as key when map sent has relative path as key" () {
        given:
            Map<String, ComponentHash> recoveryFileHashCode = [:]
            String objectPath = Paths.get('objects', 'Object2__c.object')
            String documentPath = Paths.get('documents', 'MyDocuments', 'MyDocument.txt')

            ComponentHash componentHash = new ComponentHash()
            componentHash.fileName = objectPath
            componentHash.hash = "objectHash123qweads"

            ComponentHash componentHashDocument = new ComponentHash()
            componentHashDocument.fileName = documentPath
            componentHashDocument.hash = "documentHash123qweads"

            recoveryFileHashCode.put(objectPath, componentHash)
            recoveryFileHashCode.put(documentPath, componentHashDocument)

            String projectPath = RESOURCES_PATH
            String fileNameExpect = Paths.get('objects', 'Object2__c.object').toString()
            String documentNameExpect = Paths.get('documents', 'MyDocuments', 'MyDocument.txt').toString()
        when:
            Map<String, ComponentHash> result = Util.getFilesWithTheirRelativePaths(recoveryFileHashCode, projectPath)
        then:
            result.containsKey(fileNameExpect)
            result.get(Paths.get(fileNameExpect).toString()).fileName == fileNameExpect
            result.get(Paths.get(fileNameExpect).toString()).hash == componentHash.hash

            result.containsKey(documentNameExpect)
            result.get(Paths.get(documentNameExpect).toString()).fileName == documentNameExpect
            result.get(Paths.get(documentNameExpect).toString()).hash == componentHashDocument.hash
    }

    def "Test should return an exception if there is a invalid file"() {
        given:
            String filesParameter = ["classes${File.separator}Class1.txy", "triggers${File.separator}TriggerOne.trigger"]
        when:
            Util.validateContentParameter(RESOURCES_PATH, filesParameter)
        then:
            thrown(Exception)
    }

    def "Test should return an exception if there is a invalid folder"() {
        given:
            String filesParameter = ['classes', 'invalid']
        when:
            Util.validateContentParameter(RESOURCES_PATH, filesParameter)
        then:
            thrown(Exception)
    }

    def "Test should return a apexName from Json file " () {
        given:
            String json = '''{"size": 4,
                              "totalSize": 4,
                              "done": true,
                              "queryLocator": null,
                              "entityTypeName": "ApexClass",
                              "records":   [
                                    {
                                  "attributes":       {
                                    "type": "ApexClass",
                                    "url": "/services/data/v32.0/tooling/sobjects/ApexClass/01pj0000003pIJaAAM"
                                  },
                                  "Id": "01pj0000003pIJaAAM",
                                  "Name": "Class3"
                                },
                                    {
                                  "attributes":       {
                                    "type": "ApexClass",
                                    "url": "/services/data/v32.0/tooling/sobjects/ApexClass/01pj0000003pIJbAAM"
                                  },
                                  "Id": "01pj0000003pIJbAAM",
                                  "Name": "TestClass"
                                }
                              ]
                            } '''
            String id = '01pj0000003pIJaAAM'
        when:
            String result = Util.getApexNameByJson(id,json)
        then:
            result == 'Class3'
    }

    def cleanupSpec() {
        new File(Paths.get(RESOURCES_PATH, 'triggers').toString()).deleteDir()
        new File(Paths.get(RESOURCES_PATH, 'relativeTest').toString()).deleteDir()
    }
}
