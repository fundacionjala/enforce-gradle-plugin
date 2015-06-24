package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class DocumentSalesforceValidatorTest extends Specification {

    @Shared
    SalesforceValidator validator


    def setup() {
        validator = new DocumentSalesforceValidator()
    }

    def "Test should validate is not valid the folder that contains the document"() {
        given:
            def file = new File('account.docx')
            def folder = 'classes'
        when:
            def result = validator.validateFile(file, folder)
        then:
            !result
    }

    def "Test should validate a document in the valid folder"() {
        given:
            def file = new File(Paths.get('mydocuments/doc1.docx').toString())
            def folder = 'documents'
        when:
            def result = validator.validateFile(file, folder)
        then:
            result
    }
}
