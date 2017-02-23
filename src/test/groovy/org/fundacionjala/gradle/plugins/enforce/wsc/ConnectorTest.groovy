/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc

import com.sforce.soap.partner.GetUserInfoResult
import com.sforce.soap.partner.LoginResult
import com.sforce.soap.partner.PartnerConnection
import com.sforce.ws.ConnectorConfig
import spock.lang.Shared
import spock.lang.Specification

class ConnectorTest extends Specification {

    @Shared
    def loginResult = Mock(LoginResult)

    @Shared
    Credential credential

    @Shared
    Connector connector

    @Shared
    def partnerMock = Mock(PartnerConnection)


    def setupSpec() {
        loginResult.sessionId >> '00Ro0000000RVQi!ARkAQAl8IYoCx9UvCF92wYRh1mnxX67mam5IUfGaYLsZZ4r2sYDhbQ_W'
        loginResult.serverUrl >> 'https://na17.salesforce.com/services/Soap/u/29.0/00Ro0000000RVQi'
        loginResult.metadataServerUrl >> 'https://na17.salesforce.com/services/Soap/m/29.0/00Ro0000000RVQi'
        GetUserInfoResult userInfoResult = new GetUserInfoResult(userId: 'A00000017', userFullName: 'Juan Perez',
                userEmail: 'jperez@gradle.com')

        loginResult.getUserInfo() >> userInfoResult

        credential = new Credential(username: 'jperez@gradle.com', password: 'myPassword', token: 'gma96VK1pgmr',
                     loginFormat: LoginType.DEV)

        def apiVersion = '31.0'
        connector = Spy(Connector, constructorArgs: [LoginType.DEV.value(), apiVersion])

        partnerMock.login(credential.username, credential.getPasswordToken()) >> loginResult

        connector.setPartnerConnection(partnerMock)
    }

    def 'should get the url with the login format given to connector' () {
        given:
        Connector connector1 = new Connector("cs3")
        when:
        String resultUrl = connector1.loginUrl
        then:
        resultUrl == "https://cs3.salesforce.com/services/Soap/u/32.0"

    }

    def 'should login and gets a new session'() {
        when:
        def newSession = connector.login(credential)
        then:
        newSession.sessionId == '00Ro0000000RVQi!ARkAQAl8IYoCx9UvCF92wYRh1mnxX67mam5IUfGaYLsZZ4r2sYDhbQ_W'
        newSession.userId == 'A00000017'
        newSession.serverUrl == 'https://na17.salesforce.com/services/Soap/u/29.0/00Ro0000000RVQi'
        newSession.metadataServerUrl == 'https://na17.salesforce.com/services/Soap/m/29.0/00Ro0000000RVQi'
    }

    def 'should gets a connector config'() {
        when:
        Session newSession = connector.login(credential)
        ConnectorConfig connectorConfig = connector.createConnectorConfig(newSession.sessionId, connector.getMetadataServerUrl())
        then:
        connectorConfig.sessionId == '00Ro0000000RVQi!ARkAQAl8IYoCx9UvCF92wYRh1mnxX67mam5IUfGaYLsZZ4r2sYDhbQ_W'
        connectorConfig.authEndpoint == 'https://na17.salesforce.com/services/Soap/m/29.0/00Ro0000000RVQi'
        connectorConfig.serviceEndpoint == 'https://na17.salesforce.com/services/Soap/m/29.0/00Ro0000000RVQi'
    }

    def 'Should gets the partner server url'() {
        when:
        def newSession = connector.login(credential)
        then:
        newSession != null
        connector.getPartnerServerUrl() == 'https://na17.salesforce.com/services/Soap/u/29.0/00Ro0000000RVQi'
    }

    def 'Should gets the apex server url'() {
        when:
        def newSession = connector.login(credential)
        then:
        newSession != null
        connector.getApexServerUrl() == 'https://na17.salesforce.com/services/Soap/s/29.0/00Ro0000000RVQi'
    }

    def 'Should gets the metadata server url'() {
        when:
        def newSession = connector.login(credential)
        then:
        newSession != null
        connector.getMetadataServerUrl() == 'https://na17.salesforce.com/services/Soap/m/29.0/00Ro0000000RVQi'
    }

    def 'Should gets the tooling server url'() {
        when:
        def newSession = connector.login(credential)
        then:
        newSession != null
        connector.getToolingServerUrl() == 'https://na17.salesforce.com/services/Soap/T/29.0/00Ro0000000RVQi'
    }
}