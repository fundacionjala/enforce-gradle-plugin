package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.suggestions

import spock.lang.Specification

class SuggestionManagerTest extends Specification {


    def "Should returns a suggestion when the error is Processing DocumentTest/documenttest.docx"() {
        given:
            String stateDetail = 'Processing DocumentTest/documenttest.docx'
            String expected = 'Make sure the DocumentTest folder has xml file and it is defined in the package.xml'
        when:
            String sugesstion = SuggestionManager.processStateDetail(stateDetail)
        then:
            expected == sugesstion
    }

    def "Should returns a suggestion when the error is Processing ReportTest/reporttest.docx"() {
        given:
        String stateDetail = 'Processing ReportTest/reporttest.docx'
        String expected = 'Make sure the ReportTest folder has xml file and it is defined in the package.xml'
        when:
        String sugesstion = SuggestionManager.processStateDetail(stateDetail)
        then:
        expected == sugesstion
    }
}
