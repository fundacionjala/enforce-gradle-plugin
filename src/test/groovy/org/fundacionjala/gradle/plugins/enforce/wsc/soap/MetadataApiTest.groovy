/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc.soap

import com.sforce.soap.metadata.DeployDetails
import com.sforce.soap.metadata.DeployMessage
import com.sforce.soap.metadata.DeployResult
import com.sforce.soap.partner.GetUserInfoResult
import com.sforce.soap.partner.LoginResult
import com.sforce.soap.partner.PartnerConnection
import org.fundacionjala.gradle.plugins.enforce.exceptions.deploy.DeployException
import org.fundacionjala.gradle.plugins.enforce.exceptions.deploy.InfoDeploy
import org.fundacionjala.gradle.plugins.enforce.wsc.Connector
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.ForceAPI
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType
import spock.lang.Shared
import spock.lang.Specification

class MetadataApiTest extends Specification {

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

    def 'Test should create new metadata api instance'() {
        when:
            def metadataAPI = Spy(MetadataAPI, constructorArgs: [credential, connector])
        then:
            metadataAPI instanceof ForceAPI
    }

    def 'Test should get an apex url'() {
        when:
            def metadataAPI = Spy(MetadataAPI, constructorArgs: [credential, connector])
        then:
            metadataAPI.getUrl() == 'https://na17.salesforce.com/services/Soap/m/29.0/00Ro0000000RVQi'
    }

    def 'Test should return an error because exist exception in deploy result'() {
        given:
            def deployResult = Mock(DeployResult)
            def deployDetails = Mock(DeployDetails)
            def deployMessageOne = Mock(DeployMessage)
            def deployMessageTwo = Mock(DeployMessage)

        when:
            DeployException exceptionDeploy = MetadataAPI.printDeployResult(deployResult)
        then:
            deployResult.getDetails() >> deployDetails
            deployDetails.getComponentFailures() >> [deployMessageOne, deployMessageTwo]
            deployMessageOne.getFileName() >> 'Class1.cls'
            deployMessageOne.getLineNumber() >> 5
            deployMessageOne.getColumnNumber() >> 3
            deployMessageOne.getProblem() >> 'dependency'
            deployMessageOne.getFullName() >> 'Class1'

            deployMessageTwo.getFileName() >> 'Class2.cls'
            deployMessageTwo.getLineNumber() >> 4
            deployMessageTwo.getColumnNumber() >> 2
            deployMessageTwo.getProblem() >> 'dependency'
            deployMessageTwo.getFullName() >> 'Class2'

            thrown(DeployException)
    }

    def 'Test should return an error because exist exception in deploy result catch values array'() {
        given:
        def deployResult = Mock(DeployResult)
        def deployDetails = Mock(DeployDetails)
        def deployMessageOne = Mock(DeployMessage)
        def deployMessageTwo = Mock(DeployMessage)
        ArrayList<InfoDeploy> arrayResult = []
        when:
            try{
            MetadataAPI.printDeployResult(deployResult)
            } catch(DeployException deployException) {
                arrayResult = deployException.infoDeployArrayList
            }
        then:
            deployResult.getDetails() >> deployDetails
            deployDetails.getComponentFailures() >> [deployMessageOne, deployMessageTwo]
            deployMessageOne.getFileName() >> 'Class1.cls'
            deployMessageOne.getLineNumber() >> 5
            deployMessageOne.getColumnNumber() >> 3
            deployMessageOne.getProblem() >> 'dependency'
            deployMessageOne.getFullName() >> 'Class1'

            deployMessageTwo.getFileName() >> 'Class2.cls'
            deployMessageTwo.getLineNumber() >> 4
            deployMessageTwo.getColumnNumber() >> 2
            deployMessageTwo.getProblem() >> 'dependency'
            deployMessageTwo.getFullName() >> 'Class2'

            arrayResult[0].fileName == 'Class1.cls'
            arrayResult[0].line == 5
            arrayResult[0].column == 3
            arrayResult[0].problem == 'dependency'

            arrayResult[1].fileName == 'Class2.cls'
            arrayResult[1].line == 4
            arrayResult[1].column == 2
            arrayResult[1].problem == 'dependency'
    }
}
