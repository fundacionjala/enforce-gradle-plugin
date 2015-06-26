package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files

import spock.lang.Specification


class SalesforceValidatorManagerTest extends Specification {

    def "Test should return a FileSalesforceValidator"() {
        given:
            def folder = 'objects'
        when:
        def validator = SalesforceValidatorManager.getValidator(folder)
        then:
            validator instanceof FileSalesforceValidator
    }

    def "Test should return a DocumentSalesforceValidator"() {
        given:
        def folder = 'documents'
        when:
        def validator = SalesforceValidatorManager.getValidator(folder)
        then:
        validator instanceof DocumentSalesforceValidator
    }

    def "Test should return a XMLFileSalesforceValidator"() {
        given:
        def folder = 'classes'
        when:
        def validator = SalesforceValidatorManager.getValidator(folder)
        then:
        validator instanceof XMLFileSalesforceValidator
    }
}
