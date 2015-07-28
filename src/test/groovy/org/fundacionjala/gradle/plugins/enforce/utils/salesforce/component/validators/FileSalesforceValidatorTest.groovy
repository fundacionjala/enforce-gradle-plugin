package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class FileSalesforceValidatorTest extends Specification {

    @Shared
    SalesforceValidator validator


    def setup() {
        validator = new FileSalesforceValidator()
    }

    def "Test get valid file true"() {
        given:
            def file = new File('class1.cls')
            def folder = 'classes'
        when:
            def result = validator.validateFile(file, folder)
        then:
            result
    }


    def "Test get valid file false"() {
        given:
            def file = new File('class1.data')
            def folder = 'classes'
        when:
            def result = validator.validateFile(file, folder)
        then:
            !result
    }

    def "Test get wrong file xml"() {
        given:
            def file = new File('package.xml')
            def folder = 'classes'
        when:
            def result = validator.validateFile(file, folder)
        then:
            !result
    }

    def "Test should validate the report file"() {
        given:
            def file = new File(Paths.get('testFolder/account.report').toString())
            def folder = 'reports'
        when:
            def result = validator.validateFile(file, folder)
        then:
            result
    }

    def "Test should validate is not areport file"() {
        given:
            def file = new File(Paths.get('testFolder/account.notreport').toString())
            def folder = 'reports'
        when:
            def result = validator.validateFile(file, folder)
        then:
            !result
    }
}
