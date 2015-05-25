/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.utils

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class UtilTest extends Specification {
    @Shared
        String resourcesPath

    def setup() {
        resourcesPath = Paths.get(System.getProperty("user.dir"), 'src', 'test', 'groovy', 'org', 'fundacionjala', 'gradle', 'plugins','enforce', 'utils', 'resources').toString()
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
            def projectPath = resourcesPath
        when:
            def result = Util.getEmptyFolders(foldersName, projectPath)
        then:
            result.empty
    }

    def "Test should return an arrayList with folders empty" () {
        given:
            new File(Paths.get(resourcesPath, 'triggers').toString()).mkdir()
            def foldersName = ['objects', 'classes', 'triggers']
            def projectPath = resourcesPath
        when:
            def result = Util.getEmptyFolders(foldersName, projectPath)
        then:
            result == ['triggers']
    }

    def "Test should return an extension from a file" () {
        given:
            def file = new File(Paths.get(resourcesPath, 'objects', 'Maintenance_Ticket_WO__c.object').toString())
        when:
            def extension = Util.getFileExtension(file)
        then:
            extension == 'object'
    }

    def "Test should return an extension from a file if there is more than one dot" () {
        given:
            def file = new File(Paths.get(resourcesPath, 'objects', 'My_New.CustomObject__c.object').toString())
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
            def developerName = Util.getDeveloperNameByMember('TwilioConfig__c.AuthTokenAPI__c')
        then:
            developerName == 'AuthTokenAPI'
    }

    def "Test get relative path"() {
        given:
        String basePath = Paths.get(resourcesPath, 'relativeTest')
        File file = new File(Paths.get(basePath, "classes/class1").toString())
        when:
        def relativePath = Util.getRelativePath(file, basePath)
        then:
        relativePath == Paths.get("classes", "class1").toString()
    }

    def cleanupSpec() {
        new File(Paths.get(resourcesPath, 'triggers').toString()).deleteDir()
        new File(Paths.get(resourcesPath, 'relativeTest').toString()).deleteDir()
    }
}
