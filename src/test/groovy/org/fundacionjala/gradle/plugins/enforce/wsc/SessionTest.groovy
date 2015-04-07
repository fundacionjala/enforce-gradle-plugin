/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc

import com.sforce.soap.partner.GetUserInfoResult
import com.sforce.soap.partner.LoginResult
import spock.lang.Shared
import spock.lang.Specification

class SessionTest extends Specification {
    @Shared
    def loginResult = Mock(LoginResult)

    def setupSpec() {
        loginResult.sessionId >> 'ABDSFADFSFF4545FSDF54DS5F45SF54F5S4FD5111A'
        loginResult.serverUrl >> 'http://n15.salesforce.com/service/soap/29/'
        GetUserInfoResult userInfoResult = new GetUserInfoResult(userId: '1234567', userFullName: 'Juan Perez', userEmail: 'jperez@fundacionjala.com')
        loginResult.getUserInfo() >> userInfoResult
    }

    def 'gets session info'() {


        when:
        def session = new Session(loginResult)
        then:
        session.sessionId == 'ABDSFADFSFF4545FSDF54DS5F45SF54F5S4FD5111A'
        session.serverUrl == 'http://n15.salesforce.com/service/soap/29/'
        session.userId == '1234567'
        session.userFullName == 'Juan Perez'
        session.userEmail == 'jperez@fundacionjala.com'
    }
}
