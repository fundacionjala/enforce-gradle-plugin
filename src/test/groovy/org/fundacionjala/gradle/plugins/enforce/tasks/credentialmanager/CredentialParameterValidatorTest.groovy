/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager

import org.gradle.api.Project
import spock.lang.Shared
import spock.lang.Specification

class CredentialParameterValidatorTest extends Specification{
    @Shared
    Project project
    @Shared
    CredentialParameterValidator credentialParameterValidator

    def setup() {
        credentialParameterValidator = Mock(CredentialParameterValidator)
        project = Mock(Project)
    }

    def "Test should be an instance of CredentialValidator" () {
        expect:
        credentialParameterValidator instanceof CredentialParameterValidator
    }

    def "Test should return type credential as 'encrypted' if option is equal to 'y'"() {
        given:
            String option = 'y'
        when:
            String credentialType = CredentialParameterValidator.getCredentialType(option)
        then:
            credentialType == "encrypted"
    }

    def "Test should return type credential as 'normal' if option is equal to 'n'"() {
        given:
            String option = 'n'
        when:
            String credentialType = CredentialParameterValidator.getCredentialType(option)
        then:
            credentialType == "normal"
    }

    def "Test should return type credential as 'encrypted' by default"() {
        given:
            String option = null
        when:
            String credentialType = CredentialParameterValidator.getCredentialType(option)
        then:
            credentialType == "encrypted"
    }

    def "Test should return type login as 'login' by default"() {
        given:
            String loginTypeInserted = null
        when:
            String credentialType = CredentialParameterValidator.getLoginType(loginTypeInserted)
        then:
            credentialType == "login"
    }

    def "Test should return type login as 'login' if type login is iqual to 'login'"() {
        given:
            String loginTypeInserted = 'login'
        when:
            String credentialType = CredentialParameterValidator.getLoginType(loginTypeInserted)
        then:
            credentialType == "login"
    }

    def "Test should return type login as 'test' if type login is iqual to 'test'"() {
        given:
            String loginTypeInserted = 'test'
        when:
            String credentialType = CredentialParameterValidator.getLoginType(loginTypeInserted)
        then:
            credentialType == "test"
    }

    def "Test should return type login as 'customDomain' if type login is iqual to 'customDomain'"() {
        given:
            String loginTypeInserted = 'customDomain'
        when:
            String credentialType = CredentialParameterValidator.getLoginType(loginTypeInserted)
        then:
            credentialType == "customDomain"
    }
}
