package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators

import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files.ReportSalesforceValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files.SalesforceValidator
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class ReportSalesforceValidatorTest extends Specification {

    @Shared
    SalesforceValidator validator

    def setup() {
        validator = new ReportSalesforceValidator()
    }

    def "Test should return false if file sent does not have report extension"() {
        given:
            def file = new File('myReports/invalidFile.txt')
            def folder = 'reports'
        when:
            def result = validator.validateFile(file, folder)
        then:
            !result
    }

    def "Test should return true if file sent have report extension"() {
        given:
            def file = new File(Paths.get('myReports/CustomReport.report').toString())
            def folder = 'reports'
        when:
            def result = validator.validateFile(file, folder)
        then:
            result
    }

    def "Test should return true if file sent have xml extension in the same level than report folder names"() {
        given:
            def file = new File('reports/MyReports-meta.xml')
            def folder = 'reports'
        when:
            def result = validator.validateFile(file, folder)
        then:
            result
    }

    def "Test should return false if file sent have xml extension"() {
        given:
            def file = new File(Paths.get('myReports/CustomReport.report-meta.xml').toString())
            def folder = 'reports'
        when:
            def result = validator.validateFileContainsXML(file, folder)
        then:
            result
    }
}
