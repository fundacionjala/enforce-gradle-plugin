package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.org

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class BasicOrgSubcomponentsValidatorTest extends Specification {

    @Shared
    String projectPath = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
            "fundacionjala", "gradle", "plugins","enforce","tasks", "salesforce", "resources").toString()

    @Shared
    BasicOrgSubcomponentsValidator validator

    @Shared
    String typeSubcomponent

    @Shared
    String folderComponent

    @Shared
    Map<String, ArrayList<File>> expectedMap

    @Shared
    def credential

    def setup() {

        credential = new Credential()
        credential.id = 'id'
        credential.username = 'salesforce2014.test@gmail.com'
        credential.password = '123qwe2014'
        credential.token = 'UO1Jx5vDQl97xCKkwXBH8tg3T'
        credential.loginFormat = LoginType.DEV.value()
        credential.type = 'normal'

        validator = new BasicOrgSubcomponentsValidator()
        folderComponent = ""

        expectedMap = [:]
        expectedMap.put(Constants.VALID_FILE, new ArrayList<File>())
        expectedMap.put(Constants.DOES_NOT_EXIST_FILES, new ArrayList<File>())
        expectedMap.put(Constants.FILE_WITHOUT_VALIDATOR, new ArrayList<File>())
    }

    def "Test should return a boolean true for each CustomField validated"() {
        given:
            typeSubcomponent = "fields"
            ArrayList<File> filesToTest = []
            filesToTest.add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field1__c.sbc').toString()))
            filesToTest.add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field2__c.sbc').toString()))

        when:
            boolean result1  = validator.existFileInOrg(credential, filesToTest[0], typeSubcomponent)
            boolean result2  = validator.existFileInOrg(credential, filesToTest[1], typeSubcomponent)

        then:
            result1
            result2
    }

    def "Test should return a boolean false for each CustomField validated"() {
        given:
            typeSubcomponent = "fields"
            ArrayList<File> filesToTest = []
            filesToTest.add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field4__c.sbc').toString()))
            filesToTest.add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field5__c.sbc').toString()))

        when:
            boolean result1  = validator.existFileInOrg(credential, filesToTest[0], typeSubcomponent)
            boolean result2  = validator.existFileInOrg(credential, filesToTest[1], typeSubcomponent)

        then:
            result1 == false
            result2 == false
    }

    def "Test should return a boolean true for each CompactLayout validated"() {
        given:
            typeSubcomponent = "compactLayouts"
            ArrayList<File> filesToTest = []
            filesToTest.add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout1.sbc').toString()))
            filesToTest.add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout2.sbc').toString()))

        when:
            boolean result1  = validator.existFileInOrg(credential, filesToTest[0], typeSubcomponent)
            boolean result2  = validator.existFileInOrg(credential, filesToTest[1], typeSubcomponent)

        then:
            result1
            result2
    }

    def "Test should return a boolean false for each CompactLayout validated"() {
        given:
            typeSubcomponent = "compactLayouts"
            ArrayList<File> filesToTest = []
            filesToTest.add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout4.sbc').toString()))
            filesToTest.add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout5.sbc').toString()))

        when:
            boolean result1  = validator.existFileInOrg(credential, filesToTest[0], typeSubcomponent)
            boolean result2  = validator.existFileInOrg(credential, filesToTest[1], typeSubcomponent)

        then:
            result1 == false
            result2 == false
    }

    def "Test should return a list with de CustomFiles validated"() {
        given:
            typeSubcomponent = "fields"
            ArrayList<File> filesToTest = []
            filesToTest.add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field1__c.sbc').toString()))
            filesToTest.add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field2__c.sbc').toString()))

            ArrayList<File> expectedFiles = filesToTest.clone()
        when:
            ArrayList<File> resultFiles  = validator.getSubcomponentsInOrg(credential, filesToTest, typeSubcomponent)
        then:
            resultFiles.sort() == expectedFiles.sort()
    }


    def "Test should return a list with zero CustomFiles validated"() {
        given:
            typeSubcomponent = "fields"
            ArrayList<File> filesToTest = []
            filesToTest.add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field4__c.sbc').toString()))
            filesToTest.add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field5__c.sbc').toString()))
            filesToTest.add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field6__c.sbc').toString()))

            ArrayList<File> expectedFiles = []
        when:
            ArrayList<File> resultFiles  = validator.getSubcomponentsInOrg(credential, filesToTest, typeSubcomponent)
        then:
            resultFiles.sort() == expectedFiles.sort()
    }


    def "Test should return a list with de CompactLayouts validated"() {
        given:
            typeSubcomponent = "compactLayouts"
            ArrayList<File> filesToTest = []
            filesToTest.add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout1.sbc').toString()))
            filesToTest.add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout2.sbc').toString()))
            filesToTest.add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout3.sbc').toString()))

            ArrayList<File> expectedFiles = filesToTest.clone()
        when:
            ArrayList<File> resultFiles  = validator.getSubcomponentsInOrg(credential, filesToTest, typeSubcomponent)
        then:
            resultFiles.sort() == expectedFiles.sort()
    }


    def "Test should return a list with zero CompactLayouts validated"() {
        given:
        typeSubcomponent = "compactLayouts"
            ArrayList<File> filesToTest = []
            filesToTest.add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout4.sbc').toString()))
            filesToTest.add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout5.sbc').toString()))
            filesToTest.add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout6.sbc').toString()))

            ArrayList<File> expectedFiles = []
        when:
            ArrayList<File> resultFiles  = validator.getSubcomponentsInOrg(credential, filesToTest, typeSubcomponent)
        then:
            resultFiles.sort() == expectedFiles.sort()
    }

    def "Test should return a map with the all CustomFiles validated"() {
        given:
            validator = new BasicOrgSubcomponentsValidator()
            folderComponent = "fields"
            ArrayList<File> filesToVerify = []
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field1__c.sbc').toString()))
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field2__c.sbc').toString()))
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field3__c.sbc').toString()))
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field4__c.sbc').toString()))
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field5__c.sbc').toString()))
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field6__c.sbc').toString()))

            expectedMap[Constants.VALID_FILE].add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field1__c.sbc').toString()))
            expectedMap[Constants.VALID_FILE].add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field2__c.sbc').toString()))
            expectedMap[Constants.VALID_FILE].add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field3__c.sbc').toString()))
            expectedMap[Constants.DOES_NOT_EXIST_FILES].add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field4__c.sbc').toString()))
            expectedMap[Constants.DOES_NOT_EXIST_FILES].add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field5__c.sbc').toString()))
            expectedMap[Constants.DOES_NOT_EXIST_FILES].add(new File(Paths.get(projectPath,'src', 'fields', 'Object1__c.Field6__c.sbc').toString()))

        when:
            Map<String,ArrayList<File>> resultMap = validator.validateFiles(credential, filesToVerify, folderComponent, projectPath)

        then:
            resultMap[Constants.VALID_FILE].sort() == expectedMap[Constants.VALID_FILE].sort()
            resultMap[Constants.DOES_NOT_EXIST_FILES].sort() ==  expectedMap[Constants.DOES_NOT_EXIST_FILES].sort()
            resultMap[Constants.FILE_WITHOUT_VALIDATOR].sort() == expectedMap[Constants.FILE_WITHOUT_VALIDATOR].sort()
    }

    def "Test should return a map with the all CompactLayouts validated"() {
        given:
            validator = new BasicOrgSubcomponentsValidator()
            folderComponent = "compactLayouts"
            ArrayList<File> filesToVerify = []
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout1.sbc').toString()))
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout2.sbc').toString()))
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout3.sbc').toString()))
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout4.sbc').toString()))
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout5.sbc').toString()))
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout6.sbc').toString()))

            expectedMap[Constants.VALID_FILE].add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout1.sbc').toString()))
            expectedMap[Constants.VALID_FILE].add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout2.sbc').toString()))
            expectedMap[Constants.VALID_FILE].add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout3.sbc').toString()))
            expectedMap[Constants.DOES_NOT_EXIST_FILES].add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout4.sbc').toString()))
            expectedMap[Constants.DOES_NOT_EXIST_FILES].add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout5.sbc').toString()))
            expectedMap[Constants.DOES_NOT_EXIST_FILES].add(new File(Paths.get(projectPath,'src', 'compactLayouts', 'Object1__c.compactLayout6.sbc').toString()))

        when:
            Map<String,ArrayList<File>> resultMap = validator.validateFiles(credential, filesToVerify, folderComponent, projectPath)

        then:
            resultMap[Constants.VALID_FILE].sort() == expectedMap[Constants.VALID_FILE].sort()
            resultMap[Constants.DOES_NOT_EXIST_FILES].sort() ==  expectedMap[Constants.DOES_NOT_EXIST_FILES].sort()
            resultMap[Constants.FILE_WITHOUT_VALIDATOR].sort() == expectedMap[Constants.FILE_WITHOUT_VALIDATOR].sort()
    }

    def "Test should return a map with the all ValidationRules validated"() {
        given:
        validator = new BasicOrgSubcomponentsValidator()
        folderComponent = "validationRules"
        ArrayList<File> filesToVerify = []
        filesToVerify.add(new File(Paths.get(projectPath,'src', 'validationRules', 'Object1__c.Validation1.sbc').toString()))
        filesToVerify.add(new File(Paths.get(projectPath,'src', 'validationRules', 'Object1__c.Validation2.sbc').toString()))
        filesToVerify.add(new File(Paths.get(projectPath,'src', 'validationRules', 'Object1__c.Validation3.sbc').toString()))
        filesToVerify.add(new File(Paths.get(projectPath,'src', 'validationRules', 'Object1__c.Validation4.sbc').toString()))
        filesToVerify.add(new File(Paths.get(projectPath,'src', 'validationRules', 'Object1__c.Validation5.sbc').toString()))
        filesToVerify.add(new File(Paths.get(projectPath,'src', 'validationRules', 'Object1__c.Validation6.sbc').toString()))

        expectedMap[Constants.VALID_FILE].add(new File(Paths.get(projectPath,'src', 'validationRules', 'Object1__c.Validation1.sbc').toString()))
        expectedMap[Constants.VALID_FILE].add(new File(Paths.get(projectPath,'src', 'validationRules', 'Object1__c.Validation2.sbc').toString()))
        expectedMap[Constants.VALID_FILE].add(new File(Paths.get(projectPath,'src', 'validationRules', 'Object1__c.Validation3.sbc').toString()))
        expectedMap[Constants.DOES_NOT_EXIST_FILES].add(new File(Paths.get(projectPath,'src', 'validationRules', 'Object1__c.Validation4.sbc').toString()))
        expectedMap[Constants.DOES_NOT_EXIST_FILES].add(new File(Paths.get(projectPath,'src', 'validationRules', 'Object1__c.Validation5.sbc').toString()))
        expectedMap[Constants.DOES_NOT_EXIST_FILES].add(new File(Paths.get(projectPath,'src', 'validationRules', 'Object1__c.Validation6.sbc').toString()))

        when:
        Map<String,ArrayList<File>> resultMap = validator.validateFiles(credential, filesToVerify, folderComponent, projectPath)

        then:
        resultMap[Constants.VALID_FILE].sort() == expectedMap[Constants.VALID_FILE].sort()
        resultMap[Constants.DOES_NOT_EXIST_FILES].sort() ==  expectedMap[Constants.DOES_NOT_EXIST_FILES].sort()
        resultMap[Constants.FILE_WITHOUT_VALIDATOR].sort() == expectedMap[Constants.FILE_WITHOUT_VALIDATOR].sort()
    }

    def "Test should return a map with the all RecordType validated"() {
        given:
            validator = new BasicOrgSubcomponentsValidator()
            folderComponent = "recordTypes"
            ArrayList<File> filesToVerify = []
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'recordTypes', 'Object1__c.recordType1.sbc').toString()))
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'recordTypes', 'Object1__c.recordType2.sbc').toString()))
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'recordTypes', 'Object1__c.recordType3.sbc').toString()))
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'recordTypes', 'Object1__c.recordType4.sbc').toString()))
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'recordTypes', 'Object1__c.recordType5.sbc').toString()))
            filesToVerify.add(new File(Paths.get(projectPath,'src', 'recordTypes', 'Object1__c.recordType6.sbc').toString()))

            expectedMap[Constants.VALID_FILE].add(new File(Paths.get(projectPath,'src', 'recordTypes', 'Object1__c.recordType1.sbc').toString()))
            expectedMap[Constants.VALID_FILE].add(new File(Paths.get(projectPath,'src', 'recordTypes', 'Object1__c.recordType2.sbc').toString()))
            expectedMap[Constants.VALID_FILE].add(new File(Paths.get(projectPath,'src', 'recordTypes', 'Object1__c.recordType3.sbc').toString()))
            expectedMap[Constants.DOES_NOT_EXIST_FILES].add(new File(Paths.get(projectPath,'src', 'recordTypes', 'Object1__c.recordType4.sbc').toString()))
            expectedMap[Constants.DOES_NOT_EXIST_FILES].add(new File(Paths.get(projectPath,'src', 'recordTypes', 'Object1__c.recordType5.sbc').toString()))
            expectedMap[Constants.DOES_NOT_EXIST_FILES].add(new File(Paths.get(projectPath,'src', 'recordTypes', 'Object1__c.recordType6.sbc').toString()))

        when:
            Map<String,ArrayList<File>> resultMap = validator.validateFiles(credential, filesToVerify, folderComponent, projectPath)

        then:
            resultMap[Constants.VALID_FILE].sort() == expectedMap[Constants.VALID_FILE].sort()
            resultMap[Constants.DOES_NOT_EXIST_FILES].sort() ==  expectedMap[Constants.DOES_NOT_EXIST_FILES].sort()
            resultMap[Constants.FILE_WITHOUT_VALIDATOR].sort() == expectedMap[Constants.FILE_WITHOUT_VALIDATOR].sort()
    }
}
