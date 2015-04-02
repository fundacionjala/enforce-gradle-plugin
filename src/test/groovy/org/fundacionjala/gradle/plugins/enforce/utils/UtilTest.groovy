/*
 * Copyright (c) Fundación Jala. All rights reserved.
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

    def "Test should return true if email is valid"() {
        given:
            String email = 'Juan.Perez@gmail.com'
        when:
            Boolean result = Util.validEmail(email)
        then:
            result
    }

    def "Test should return false if email is invalid"() {
        given:
            String email = 'Juan.Perezmail.com'
        when:
            Boolean result = Util.validEmail(email)
        then:
            !result
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

    def cleanupSpec() {
        new File(Paths.get(resourcesPath, 'triggers').toString()).deleteDir()
    }
}
