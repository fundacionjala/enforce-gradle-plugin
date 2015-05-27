package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators

import spock.lang.Shared
import spock.lang.Specification

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
            def result = validator.validateFileByFolder(folder, file)
        then:
            !result
    }

    def "Test should validate a document in the valid folder"() {
        given:
            def file = new File('mydocuments/doc1.docx')
            def folder = 'documents'
        when:
            def result = validator.validateFileByFolder(folder, file)
        then:
            result
    }
}
