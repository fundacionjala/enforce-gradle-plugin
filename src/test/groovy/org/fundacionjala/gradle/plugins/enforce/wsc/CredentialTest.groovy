/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc

import spock.lang.Shared
import spock.lang.Specification


class CredentialTest extends Specification {
    @Shared
    def credentialMock = Mock(Credential)
    @Shared
    def credentialSpy = Spy(Credential)

    def setupSpec() {
        credentialMock.username >> 'userMock'
        credentialMock.password >> '1234567'
        credentialMock.token >> 'abc'
        credentialMock.getPasswordToken() >> credentialMock.password + credentialMock.token
        credentialSpy.username = 'userSpy'
        credentialSpy.password = 'wsc'
        credentialSpy.token = '1234567'
    }

    def 'gets password and token with a mock'() {
        when:
        def passwordToken = credentialMock.getPasswordToken()
        then:
        passwordToken == '1234567abc'
    }

    def 'gets password and token with a spy'() {
        when:
        def passwordToken = credentialSpy.getPasswordToken()
        then:
        passwordToken == 'wsc1234567'

    }
}
