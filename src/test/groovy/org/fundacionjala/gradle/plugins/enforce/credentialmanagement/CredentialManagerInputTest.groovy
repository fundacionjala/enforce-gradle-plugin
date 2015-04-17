/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.credentialmanagement

import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class CredentialManagerInputTest extends Specification {
    @Shared
    CredentialManagerInput credentialAdderInput

    @Shared
    Credential credential

    @Shared
    def path = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala", "gradle",
               "plugins","enforce","tasks","credentialmanager","resources").toString()

    @Shared
    def pathCredentials = Paths.get(path, "credentials.dat").toString()

    @Shared
    def pathSecretKeyGenerated = Paths.get(path, "secretKeyGenerated.text").toString()

    @Shared
    String idInput

    @Shared
    String userNameInput

    @Shared
    String passwordInput

    @Shared
    String tokenInput

    @Shared
    String loginTypeInput

    @Shared
    String typeInput

    def setup() {
        credentialAdderInput = new CredentialManagerInput(pathCredentials, pathSecretKeyGenerated)
        credential = new Credential()

        idInput = 'id'
        userNameInput = 'username@email.com'
        passwordInput = 'password'
        tokenInput = 'token'
        loginTypeInput = 'login'
        typeInput = 'normal'

        credential.id = idInput
        credential.username = userNameInput
        credential.password = passwordInput
        credential.token = tokenInput
        credential.loginFormat = loginTypeInput == 'login' ? LoginType.DEV.value() : LoginType.TEST.value()
        credential.type = typeInput
    }

    def "Test should be an instance of CredentialAdderInput"() {
        expect:
        credentialAdderInput instanceof CredentialManagerInput
    }

    def "Test should insert values inputs and return a credential"() {
        given:
            credentialAdderInput.idInput = idInput
            credentialAdderInput.userNameInput = userNameInput
            credentialAdderInput.passwordInput = passwordInput
            credentialAdderInput.tokenInput = tokenInput
            credentialAdderInput.loginTypeInput = loginTypeInput
            credentialAdderInput.typeInput = typeInput
        when:
            def newCredential = credentialAdderInput.getCredentialInserted()
        then:
            newCredential.id == credential.id
            newCredential.username == credential.username
            newCredential.password == credential.password
            newCredential.token == credential.token
            newCredential.loginFormat == credential.loginFormat
            newCredential.type == credential.type
    }

    def "Test should get a login type by default"() {
        given:
        credentialAdderInput.idInput = idInput
        credentialAdderInput.userNameInput = userNameInput
        credentialAdderInput.passwordInput = passwordInput
        credentialAdderInput.tokenInput = tokenInput
        credentialAdderInput.loginTypeInput = ""
        credentialAdderInput.typeInput = typeInput
        when:
        def newCredential = credentialAdderInput.getCredentialInserted()
        credential.loginFormat = 'login'
        then:
        newCredential.id == credential.id
        newCredential.username == credential.username
        newCredential.password == credential.password
        newCredential.token == credential.token
        newCredential.loginFormat == credential.loginFormat
        newCredential.type == credential.type
    }

    def "Test should get a  custom login type"() {
        given:
            credentialAdderInput.idInput = idInput
            credentialAdderInput.userNameInput = userNameInput
            credentialAdderInput.passwordInput = passwordInput
            credentialAdderInput.tokenInput = tokenInput
            credentialAdderInput.loginTypeInput = 'my.custom-domain'
            credentialAdderInput.typeInput = typeInput
        when:
            def newCredential = credentialAdderInput.getCredentialInserted()
            credential.loginFormat = 'my.custom-domain'
        then:
            newCredential.id == credential.id
            newCredential.username == credential.username
            newCredential.password == credential.password
            newCredential.token == credential.token
            newCredential.loginFormat == credential.loginFormat
            newCredential.type == credential.type
    }

    def "Test should false if there is a empty input"() {
        given:
            credentialAdderInput.idInput = idInput
            credentialAdderInput.passwordInput = ''
            credentialAdderInput.tokenInput = tokenInput
            credentialAdderInput.loginTypeInput = loginTypeInput
            credentialAdderInput.typeInput = typeInput
        when:
            def result = credentialAdderInput.validateFields()
        then:
            !result
    }

    def "Test should true if there isn't a empty input"() {
        given:
            credentialAdderInput.idInput = idInput
            credentialAdderInput.passwordInput = passwordInput
            credentialAdderInput.tokenInput = tokenInput
            credentialAdderInput.loginTypeInput = loginTypeInput
            credentialAdderInput.typeInput = typeInput
        when:
            def result = credentialAdderInput.validateFields()
        then:
            result
    }

    def "Test should true if exist an id credential"() {
        given:
            credentialAdderInput.pathCredentials = pathCredentials
            credentialAdderInput.idInput = idInput
            credentialAdderInput.passwordInput = passwordInput
            credentialAdderInput.tokenInput = tokenInput
            credentialAdderInput.loginTypeInput = loginTypeInput
            credentialAdderInput.typeInput = typeInput
        when:
            def result = credentialAdderInput.hasCredential(idInput)
        then:
            result
    }

    def "Test should false if doesn't exist an id credential"() {
        given:
            credentialAdderInput.pathCredentials = pathCredentials
            credentialAdderInput.idInput = '123qwe'
            credentialAdderInput.passwordInput = passwordInput
            credentialAdderInput.tokenInput = tokenInput
            credentialAdderInput.loginTypeInput = loginTypeInput
            credentialAdderInput.typeInput = typeInput
        when:
            def result = credentialAdderInput.hasCredential(credentialAdderInput.idInput)
        then:
            !result
    }
}