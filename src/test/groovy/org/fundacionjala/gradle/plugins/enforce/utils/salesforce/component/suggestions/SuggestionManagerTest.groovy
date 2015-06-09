package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.suggestions

import spock.lang.Specification

class SuggestionManagerTest extends Specification {


    def "Should returns a suggestion when the error is Processing DocumentTest/documenttest.docx"() {
        given:
            String stateDetail = 'Processing DocumentTest/documenttest.docx'
            String expected = 'Salesforce has reported an unexpected error:\n  A common cause is about DocumentTest folder doesn´t have a respective XML file or it is not defined in the package.xml'
        when:
            String sugesstion = SuggestionManager.processStateDetail(stateDetail)
        then:
            expected == sugesstion
    }

    def "Should returns a suggestion when the error is Processing ReportTest/reporttest.docx"() {
        given:
            String stateDetail = 'Processing ReportTest/reporttest.docx'
            String expected = 'Salesforce has reported an unexpected error:\n  A common cause is about ReportTest folder doesn´t have a respective XML file or it is not defined in the package.xml'
        when:
            String sugesstion = SuggestionManager.processStateDetail(stateDetail)
        then:
            expected == sugesstion
    }
}
