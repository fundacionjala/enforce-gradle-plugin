/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.undeploy

import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class SmartFilesValidatorTest extends Specification {

    @Shared
    Object jsonObject

    @Shared
    def SRC_PATH = System.getProperty("user.dir") + "/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/undeploy/resources"

    @Shared
    File fileReader

    def setupSpec() {
        JsonSlurper jsonSlurper = new JsonSlurper()
        fileReader = new File(Paths.get(SRC_PATH,"JsonAppexClass.json").toString())
        jsonObject = jsonSlurper.parse(fileReader)
    }

    def "Test should create a json format since string" () {
        given:
            def jsonString1 = """{"entityTypeName":"ApexClass","records": [{"Name" : "Class1","attributes":{"type":"ApexClass"}},{"Name" : "Class2", "attributes":{"type":"ApexClass"}}]"""
            def jsonString2 = """{"entityTypeName":"ApexTrigger","records": [{"Name" : "Trigger1", "attributes":{"type":"Trigger"}},{"Name" : "Trigger2", "attributes":{"type":"Trigger"}}]"""
            def jsonArrays = new ArrayList<String>()
            jsonArrays.push(jsonString1)
            jsonArrays.push(jsonString2)
        when:
            SmartFilesValidator filesValidator = new SmartFilesValidator(jsonArrays)
        then:
            noExceptionThrown()
    }

    def "Test should fill a map with thw files on org" () {
        given:
            def jsonString1 = """{"entityTypeName":"ApexClass","records": [{"Name" : "Class1","attributes":{"type":"ApexClass"}},{"Name" : "Class2", "attributes":{"type":"ApexClass"}}]"""
            def jsonString2 = """{"entityTypeName":"ApexTrigger","records": [{"Name" : "Trigger1", "attributes":{"type":"ApexTrigger"}},{"Name" : "Trigger2", "attributes":{"type":"ApexTrigger"}}]"""
            def jsonArrays = new ArrayList<String>()
            jsonArrays.push(jsonString1)
            jsonArrays.push(jsonString2)
        when:
            SmartFilesValidator filesValidator = new SmartFilesValidator(jsonArrays)
            def spec = ["ApexClass": ["Class1", "Class2"], "ApexTrigger": ["Trigger1", "Trigger2"] ]
        then:
            filesValidator.queryResult == spec
    }

    def "Test should return only files that exist in organization" () {
        given:
            def jsonString1 = """{"entityTypeName":"ApexClass","records": [{"Name" : "Class1","attributes":{"type":"ApexClass"}},{"Name" : "Class2", "attributes":{"type":"ApexClass"}}]"""
            def jsonString2 = """{"entityTypeName":"ApexTrigger","records": [{"Name" : "Trigger1", "attributes":{"type":"ApexTrigger"}},{"Name" : "Trigger2", "attributes":{"type":"ApexTrigger"}}]"""
            def jsonArrays = new ArrayList<String>()
            jsonArrays.push(jsonString1)
            jsonArrays.push(jsonString2)
        when:
            SmartFilesValidator filesValidator = new SmartFilesValidator(jsonArrays)
            def filesToEvaluate = new ArrayList<File>()
            filesToEvaluate.push(new File(Paths.get(SRC_PATH, "classes/Class1.cls").toString()))
            filesToEvaluate.push(new File(Paths.get(SRC_PATH, "classes/Class2.cls").toString()))
            filesToEvaluate.push(new File(Paths.get(SRC_PATH, "classes/Class3.cls").toString()))
            filesToEvaluate.push(new File(Paths.get(SRC_PATH, "invalidFolder/Class3.cls").toString()))
            def result = filesValidator.filterFilesAccordingOrganization(filesToEvaluate, SRC_PATH)
            def expected = new ArrayList<File>()
            expected.push(new File(Paths.get(SRC_PATH, "classes/Class1.cls").toString()))
            expected.push(new File(Paths.get(SRC_PATH, "classes/Class2.cls").toString()))
        then:
            result == expected
    }

    def "Test should verify if profile is accepted" () {
        given:
            def jsonString1 = """{"entityTypeName":"ApexClass","records": [{"Name" : "Class1","attributes":{"type":"ApexClass"}},{"Name" : "Class2", "attributes":{"type":"ApexClass"}}]"""
            def jsonString2 = """{"entityTypeName":"ApexTrigger","records": [{"Name" : "Trigger1", "attributes":{"type":"ApexTrigger"}},{"Name" : "Trigger2", "attributes":{"type":"ApexTrigger"}}]"""
            def jsonString3 = """{"entityTypeName":"Profile","records": [{"Name" : "Profile__custom", "attributes":{"type":"Profile"}}]"""
            def jsonArrays = new ArrayList<String>()
            jsonArrays.push(jsonString1)
            jsonArrays.push(jsonString2)
            jsonArrays.push(jsonString3)
        when:
            SmartFilesValidator filesValidator = new SmartFilesValidator(jsonArrays)
            def filesToEvaluate = new ArrayList<File>()
            filesToEvaluate.push(new File(Paths.get(SRC_PATH, "classes/Class1.cls").toString()))
            filesToEvaluate.push(new File(Paths.get(SRC_PATH, "classes/Class2.cls").toString()))
            filesToEvaluate.push(new File(Paths.get(SRC_PATH, "classes/Class3.cls").toString()))
            filesToEvaluate.push(new File(Paths.get(SRC_PATH, "invalidFolder/Class3.cls").toString()))
            filesToEvaluate.push(new File(Paths.get(SRC_PATH, "profiles/Profile__custom.profile").toString()))
            def result = filesValidator.filterFilesAccordingOrganization(filesToEvaluate, SRC_PATH)
            def expected = new ArrayList<File>()
            expected.push(new File(Paths.get(SRC_PATH, "classes/Class1.cls").toString()))
            expected.push(new File(Paths.get(SRC_PATH, "classes/Class2.cls").toString()))
            expected.push(new File(Paths.get(SRC_PATH, "profiles/Profile__custom.profile").toString()))
        then:
            result == expected
    }
}
