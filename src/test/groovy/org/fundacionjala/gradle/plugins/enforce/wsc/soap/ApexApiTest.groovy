/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc.soap

import com.sforce.soap.apex.DebuggingInfo_element
import com.sforce.soap.apex.ExecuteAnonymousResult
import com.sforce.soap.apex.SoapConnection
import com.sforce.soap.partner.GetUserInfoResult
import com.sforce.soap.partner.LoginResult
import com.sforce.soap.partner.PartnerConnection
import org.fundacionjala.gradle.plugins.enforce.wsc.Connector
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.ForceAPI
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType
import spock.lang.Shared
import spock.lang.Specification

class ApexApiTest extends Specification {
    @Shared
    def loginResult = Mock(LoginResult)

    @Shared
    Credential credential

    @Shared
    Connector connector

    @Shared
    def partnerMock = Mock(PartnerConnection)

    @Shared
    String RESOURCE_PATH = "${System.properties['user.dir']}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/wsc/resources"

    @Shared
    ApexAPI apexAPI

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


        apexAPI = Spy(ApexAPI, constructorArgs: [credential, connector])

    }

    def 'Should create new apex api instance'() {
        when:
        def apexAPI = Spy(ApexAPI, constructorArgs: [credential, connector])
        then:
        apexAPI instanceof ForceAPI
    }

    def 'Should get an apex url'() {
        when:
        def apexAPI = Spy(ApexAPI, constructorArgs: [credential, connector])
        then:
        apexAPI.getUrl() == 'https://na17.salesforce.com/services/Soap/s/29.0/00Ro0000000RVQi'
    }

    def 'Should execute apex code from code apex'() {
        given:
        ExecuteAnonymousResult anonymousResult = new ExecuteAnonymousResult()
        anonymousResult.success = true
        def apexCode = "System.Debug('Hello world!');"
        def output = new StringBuilder().append("29.0 ALL,ERROR;APEX_CODE,ERROR;DB,INFO")
                .append("Execute Anonymous: System.Debug('Hello world!');")
                .append("18:17:23.041 (41582785)|EXECUTION_STARTED").toString()
        when:
        SoapConnection soapConnection = Spy(SoapConnection, constructorArgs: [apexAPI.connectorConfig])
        soapConnection.executeAnonymous(apexCode) >> anonymousResult
        def debuggingInfo_element = Mock(DebuggingInfo_element)
        debuggingInfo_element.getDebugLog() >> output
        soapConnection.__setDebuggingInfo(debuggingInfo_element)
        apexAPI.soapConnection = soapConnection
        def result = apexAPI.executeApex(apexCode)
        then:
        result == output
    }

    def 'Should execute apex code from apex file'() {
        given:
        ExecuteAnonymousResult anonymousResult = new ExecuteAnonymousResult()
        anonymousResult.success = true
        def output = new StringBuilder().append("29.0 ALL,ERROR;APEX_CODE,ERROR;DB,INFO")
                .append("Execute Anonymous: public void print(String s){")
                .append("Execute Anonymous:  System.Debug('Hello world!');")
                .append("Execute Anonymous:  }")
                .append("18:17:23.041 (41582785)|EXECUTION_STARTED").toString()
        when:
        SoapConnection soapConnection = Spy(SoapConnection, constructorArgs: [apexAPI.connectorConfig])
        soapConnection.executeAnonymous(new File("${RESOURCE_PATH}/printMethod").text) >> anonymousResult
        def debuggingInfo_element = Mock(DebuggingInfo_element)
        debuggingInfo_element.getDebugLog() >> output
        soapConnection.__setDebuggingInfo(debuggingInfo_element)
        apexAPI.soapConnection = soapConnection
        def result = apexAPI.executeApexFile("${RESOURCE_PATH}/printMethod")
        then:
        result == output
    }

    def 'Should throw an exception from apex file invalid'() {
        when:
        def result = apexAPI.executeApexFile("${RESOURCE_PATH}/printMethod.cls")
        then:
        thrown(Exception)
    }
}
