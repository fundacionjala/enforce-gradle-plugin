/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.credentialmanagement

import groovy.json.JsonSlurper
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType
import spock.lang.Shared
import spock.lang.Specification

import javax.crypto.spec.SecretKeySpec
import java.nio.file.Paths

class CredentialManagerTest extends Specification {
    @Shared
    CredentialManager credentialManager
    @Shared
    Credential credential
    @Shared
    def path = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala", "gradle",
               "plugins","enforce", "credentialmanagement", "resources").toString()
    @Shared
    def pathCredentials = Paths.get(path, "credentialSaved.dat").toString()
    @Shared
    def pathSecretKeyGenerated = Paths.get(path, "secretKeyGenerated.dat").toString()
    @Shared
    def secretKey
    @Shared
    def secretKeyGenerated

    def setup() {
        credentialManager = new CredentialManager(pathCredentials, pathSecretKeyGenerated)
        credential = new Credential()
        credential.id = 'id2'
        credential.username = 'newusername@email.com'
        credential.password = 'password'
        credential.token = 'token'
        credential.loginFormat = LoginType.TEST.value()
        credential.type = 'normal'
        def mockMacAddress = '94de80a07008'
        def mockSALT = "CODD"
        secretKey = "${mockMacAddress}${mockSALT}"
        secretKeyGenerated = new SecretKeySpec(secretKey.getBytes(), 'AES')
        CredentialEncrypter.generateSecretKey(secretKey) >> secretKeyGenerated
    }

    def "Test Should be instance of CredentialManager"() {
        expect:
        credentialManager instanceof CredentialManager
    }

    def "Test Should add a new credential in credentials file"() {
        when:
        credentialManager.addCredential(credential)
        def credentials = new JsonSlurper().parseText(new File(pathCredentials).text)
        def credentialAdded = credentials[credential.id]
        then:
        credentialAdded.username == credential.username
        credentialAdded.password == credential.password
        credentialAdded.token == credential.token
        credentialAdded.sfdcType == credential.loginFormat
        credentialAdded.type == credential.type
    }

    def "Test Should add a new credential encrypted in credentials file"() {
        given:
            credential.id = 'id3'
            credential.type = 'encrypted'
            def passwordEncrypted = '0bdzKV14J7bs2h1yw01eMQ=='
            def tokenEncrypted = 'xthkbHWGppQTkrqFhHzpzw=='
            def pathCredentials = Paths.get(path, "credentials.dat").toString()
            credentialManager = new CredentialManager(pathCredentials, pathSecretKeyGenerated)
            credentialManager.secretKeyGenerated = secretKeyGenerated.encoded.encodeBase64().toString()
        when:
            credentialManager.addCredential(credential)
            def credentials = new JsonSlurper().parseText(new File(pathCredentials).text)
            def credentialAdded = credentials[credential.id]
        then:
            credentialAdded.username == credential.username
            credentialAdded.password == passwordEncrypted
            credentialAdded.token == tokenEncrypted
            credentialAdded.sfdcType == credential.loginFormat
            credentialAdded.type == credential.type
    }

    def "Test Should encrypt a credential if it have type equal encrypted"() {
        given:
            credential.id = 'id4'
            credential.type = 'encrypted'
            def passwordEncrypted = '0bdzKV14J7bs2h1yw01eMQ=='
            def tokenEncrypted = 'xthkbHWGppQTkrqFhHzpzw=='
            def pathCredentials = Paths.get(path, "credentials.dat").toString()
            credentialManager = new CredentialManager(pathCredentials, pathSecretKeyGenerated)
            credentialManager.secretKeyGenerated = secretKeyGenerated.encoded.encodeBase64().toString()
        when:
            credentialManager.addCredential(credential)
            def credentials = new JsonSlurper().parseText(new File(pathCredentials).text)
            def credentialEncrypted = credentials[credential.id]
        then:
            credentialEncrypted.username == credential.username
            credentialEncrypted.password == passwordEncrypted
            credentialEncrypted.token == tokenEncrypted
            credentialEncrypted.sfdcType == credential.loginFormat
    }

    def "Test Shouldn't encrypt a credential if it haven't type equal encrypted"() {
        given:
            credential.id = 'id5'
            credential.type = 'normal'
        when:
            credentialManager.addCredential(credential)
            def credentials = new JsonSlurper().parseText(new File(pathCredentials).text)
            def credentialEncrypted = credentials[credential.id]
        then:
            credentialEncrypted.username == credential.username
            credentialEncrypted.password == credential.password
            credentialEncrypted.token == credential.token
            credentialEncrypted.sfdcType == credential.loginFormat
    }

    def "Test Should update a credential in credentials file"() {
        given:
            Credential credentialToUpdate = new Credential()
            credentialToUpdate.id = 'id2'
            credentialToUpdate.username = 'update@email.com'
            credentialToUpdate.password = 'updatePassword'
            credentialToUpdate.token = 'updateToken'
            credentialToUpdate.loginFormat = LoginType.TEST
            credentialToUpdate.type = 'normal'
        when:
            credentialManager.updateCredential(credentialToUpdate)
            def credentials = new JsonSlurper().parseText(new File(pathCredentials).text)
            def credentialUpdated = credentials[credentialToUpdate.id]
        then:
            credentialUpdated.username == credentialToUpdate.username
            credentialUpdated.password == credentialToUpdate.password
            credentialUpdated.token == credentialToUpdate.token
            credentialUpdated.sfdcType == credentialToUpdate.loginFormat
    }

    def "Test Should update a credential encrypted in credentials file"() {
        given:
            Credential credentialToUpdate = new Credential()
            credentialToUpdate.id = 'id3'
            credentialToUpdate.username = 'update@email.com'
            credentialToUpdate.password = 'password'
            credentialToUpdate.token = 'token'
            credentialToUpdate.loginFormat = LoginType.TEST
            credentialToUpdate.type = 'encrypted'
            def passwordEncrypted = '0bdzKV14J7bs2h1yw01eMQ=='
            def tokenEncrypted = 'xthkbHWGppQTkrqFhHzpzw=='
            def pathCredentials = Paths.get(path, "credentials.dat").toString()
            credentialManager = new CredentialManager(pathCredentials, pathSecretKeyGenerated)
            credentialManager.secretKeyGenerated = secretKeyGenerated.encoded.encodeBase64().toString()
        when:
            credentialManager.updateCredential(credentialToUpdate)
            def credentials = new JsonSlurper().parseText(new File(pathCredentials).text)
            def credentialUpdated = credentials[credentialToUpdate.id]
        then:
            credentialUpdated.username == credentialToUpdate.username
            credentialUpdated.password == passwordEncrypted
            credentialUpdated.token == tokenEncrypted
            credentialUpdated.sfdcType == credentialToUpdate.loginFormat
    }

    def "Test Should return an exception if there isn't a credential"() {
        given:
            credential.id = '11111'
        when:
            credentialManager.updateCredential(credential)
        then:
            thrown(Exception)
    }

    def "Test Should return a credential to authenticate in salesForce"() {
        given:
            def credentialId = 'idNormal'
            def pathUserHome = Paths.get(path, "credentialUser.dat").toString()
            def pathProject = Paths.get(path, "credentialProject.dat").toString()
            ArrayList<String> credentialPaths = new ArrayList<String>()
            credentialPaths.add(pathUserHome)
            credentialPaths.add(pathProject)
        when:
            def credentialsObtained = credentialManager.getCredentialToAuthenticate(credentialId, credentialPaths)
        then:
            credentialsObtained.username == "juan@email.com"
            credentialsObtained.password == "password"
            credentialsObtained.token == "token"
            credentialsObtained.loginFormat == credential.loginFormat
            credentialsObtained.type == 'normal'
    }

    def "Test Should return a credential decrypted if it is encrypted"() {
        given:
            def credentialId = 'id'
            def pathUserHome = Paths.get(path, "credentialUser.dat").toString()
            def pathProject = Paths.get(path, "credentialProject.dat").toString()
            ArrayList<String> credentialPaths = new ArrayList<String>()
            credentialPaths.add(pathUserHome)
            credentialPaths.add(pathProject)
            def pathCredentials = Paths.get(path, "credentials.dat").toString()
            credentialManager = new CredentialManager(pathCredentials, pathSecretKeyGenerated)
            credentialManager.secretKeyGenerated = secretKeyGenerated.encoded.encodeBase64().toString()
        when:
            def credentialsObtained = credentialManager.getCredentialToAuthenticate(credentialId, credentialPaths)
        then:
            credentialsObtained.username == "username@email.com"
            credentialsObtained.password == "password"
            credentialsObtained.token == "token"
            credentialsObtained.loginFormat == credential.loginFormat
            credentialsObtained.type == 'encrypted'
    }

    def "Test Should return an exception if id credential doesn't exist"() {
        given:
            def credentialId = 'qwe123'
            def pathUserHome = Paths.get(path, "credentialUser.dat").toString()
            def pathProject = Paths.get(path, "credentialProject.dat").toString()
            ArrayList<String> credentialPaths = new ArrayList<String>()
            credentialPaths.add(pathUserHome)
            credentialPaths.add(pathProject)
        when:
            credentialManager.getCredentialToAuthenticate(credentialId, credentialPaths)
        then:
            thrown(Exception)
    }

    def cleanupSpec() {
        new File(pathSecretKeyGenerated).delete()
    }
}
