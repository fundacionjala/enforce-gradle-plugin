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

import java.nio.file.Paths

class CredentialFileManagerTest extends Specification {

    @Shared
    CredentialFileManager credentialFileManager

    @Shared
    def path = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala", "gradle",
            "plugins","enforce", "credentialmanagement", "resources").toString()

    @Shared
    def pathCredentials = Paths.get(path, "credentialSaved.dat").toString()

    @Shared
    def pathSecretKeyGenerated = Paths.get(path, "secretKeyGenerated.dat").toString()

    @Shared
    Credential credential

    def setup() {
        credentialFileManager = new CredentialFileManager(pathCredentials, pathSecretKeyGenerated)
        credential = new Credential()
        credential.id = 'id'
        credential.username = 'username@email.com'
        credential.password = 'password'
        credential.token = 'token'
        credential.loginFormat = LoginType.TEST.value()
        credential.type = 'normal'
    }

    def "Test should be a instance of CredentialWriting class"() {
        expect:
        credentialFileManager instanceof CredentialFileManager
    }

    def "Test should return a exception if a credentials file is empty"() {
        given:
            def pathEmptyFile = Paths.get(path, "emptyFile.dat").toString()
            def credentialWriting = new CredentialFileManager(pathEmptyFile, pathSecretKeyGenerated)
        when:
            credentialWriting.saveCredential(credential)
        then:
            thrown(Exception)
    }

    def "Test should create a new credentials file if a credentials file doesn't exist"() {
        given:
            def pathCredentialsNotExist = Paths.get(path, "credentialsNotExist.dat").toString()
            def credentialWriting = new CredentialFileManager(pathCredentialsNotExist, pathSecretKeyGenerated)
        when:
            credentialWriting.saveCredential(credential)
            def credentials = new JsonSlurper().parseText(new File(pathCredentialsNotExist).getText())
            def credentialSaved = credentials[credential.id]
        then:
            credentialSaved.username == credential.username
            credentialSaved.password == credential.password
            credentialSaved.token == credential.token
            credentialSaved.sfdcType == credential.loginFormat
            credentialSaved.type == credential.type
    }

    def "Test should return an exception if credentials file content is invalid"() {
        given:
            def pathCredentialsFileInvalid = Paths.get(path, "invalidFile.dat").toString()
            def credentialWriting = new CredentialFileManager(pathCredentialsFileInvalid, pathSecretKeyGenerated)
        when:
            credentialWriting.saveCredential(credential)
        then:
            thrown(Exception)
    }

    def "Test should add a new credential in credentials file if this exist"() {
        when:
            credentialFileManager.saveCredential(credential)
            def credentials = new JsonSlurper().parseText(new File(pathCredentials).getText())
            def credentialSaved = credentials[credential.id]
        then:
            credentialSaved.username == credential.username
            credentialSaved.password == credential.password
            credentialSaved.token == credential.token
            credentialSaved.sfdcType == credential.loginFormat
            credentialSaved.type == credential.type
    }

    def "Test should return a exception if a credential is null"() {
        when:
            credentialFileManager.saveCredential(null)
        then:
            thrown(Exception)
    }

    def "Test should write a secretKey generate in a path specified"() {
        given:
            def mockMacAddress = '94de80a07008'
            def mockSALT = "CODD"
            def secretKey = "${mockMacAddress}${mockSALT}"
        when:
            credentialFileManager.saveSecretKeyGenerated(secretKey)
        def secretKeyGenerated = new File(pathSecretKeyGenerated).getText()
        then:
            secretKeyGenerated
    }

    def "Test should return a credential if credential exist"() {
        given:
            def idCredential = 'id'
        when:
            def credentialObtained = credentialFileManager.getCredentialById(idCredential)
        then:
            credentialObtained.username == credential.username
            credentialObtained.password == credential.password
            credentialObtained.token == credential.token
            credentialObtained.loginFormat == credential.loginFormat
            credentialObtained.type == credential.type
    }

    def "Test should return an exception if credential doesn't exist"() {
        given:
            def idCredential = "id1000"
        when:
            credentialFileManager.getCredentialById(idCredential)
        then:
           thrown(Exception)
    }

    def "Test should return an exception if credentials file doesn't exist"() {
        given:
            def idCredential = 'id200'
            def pathCredentialsNotExist = Paths.get(path, "credentialsNotExist.dat").toString()
            def credentialFileManager = new CredentialFileManager(pathCredentialsNotExist, pathSecretKeyGenerated)
        when:
            credentialFileManager.getCredentialById(idCredential)
        then:
            thrown(Exception)
    }

    def "Test should return an exception if file credentials file is not valid"() {
        given:
            def idCredential = 'id200'
            def pathEmptyCredentials = Paths.get(path, "emptyFile.dat").toString()
            def credentialFileManager = new CredentialFileManager(pathEmptyCredentials, pathSecretKeyGenerated)
        when:
            credentialFileManager.getCredentialById(idCredential)
        then:
            thrown(Exception)
    }

    def "Test should return an exception if credentials file doesn't exist when you want to get credentials"() {
        given:
            ArrayList<Credential> listCredentials = new ArrayList<Credential>()
            listCredentials.add(credential)
            def pathCredentialsInvalid = Paths.get(path, "credentialsInvalid.dat").toString()
            def credentialFileManager = new CredentialFileManager(pathCredentialsInvalid, pathSecretKeyGenerated)
        when:
            credentialFileManager.getCredentials()
        then:
            thrown(Exception)
    }

    def "Test should return an exception if credentials file is empty when you want to get credentials"() {
        given:
            ArrayList<Credential> listCredentials = new ArrayList<Credential>()
            listCredentials.add(credential)
            def pathCredentialsInvalid = Paths.get(path, "emptyFile.dat").toString()
            def credentialFileManager = new CredentialFileManager(pathCredentialsInvalid, pathSecretKeyGenerated)
        when:
            credentialFileManager.getCredentials()
        then:
            thrown(Exception)
    }
    def "Test should return an exception if credentials file is invalid when you want to get credentials"() {
        given:
            ArrayList<Credential> listCredentials = new ArrayList<Credential>()
            listCredentials.add(credential)
            def pathCredentialsInvalid = Paths.get(path, "invalidFile.dat").toString()
            def credentialFileManager = new CredentialFileManager(pathCredentialsInvalid, pathSecretKeyGenerated)
        when:
            credentialFileManager.getCredentials()
        then:
            thrown(Exception)
    }

    def "Test should get a credential from more than one path"() {
        given:
            def idCredential = 'id'
            def pathUserHome = Paths.get(path, "credentialUser.dat").toString()
            def pathProject = Paths.get(path, "credentialProject.dat").toString()
            ArrayList<String> credentialPaths = new ArrayList<String>()
            credentialPaths.add(pathUserHome)
            credentialPaths.add(pathProject)
        when:
            def credentialsObtained = credentialFileManager.getCredentialById(idCredential, credentialPaths)
        then:
            credentialsObtained.username == credential.username
            credentialsObtained.password == "0bdzKV14J7bs2h1yw01eMQ=="
            credentialsObtained.token == "xthkbHWGppQTkrqFhHzpzw=="
            credentialsObtained.loginFormat == credential.loginFormat
            credentialsObtained.type == 'encrypted'
    }

    def "Test should get a credential by default if you send a empty id"() {
        given:
            def idCredential = ''
            def pathUserHome = Paths.get(path, "credentialUser.dat").toString()
            def pathProject = Paths.get(path, "credentialProject.dat").toString()
            ArrayList<String> credentialPaths = new ArrayList<String>()
            credentialPaths.add(pathUserHome)
            credentialPaths.add(pathProject)
        when:
            def credentialsObtained = credentialFileManager.getCredentialById(idCredential, credentialPaths)
        then:
            credentialsObtained.username == credential.username
            credentialsObtained.password == "0bdzKV14J7bs2h1yw01eMQ=="
            credentialsObtained.token == "xthkbHWGppQTkrqFhHzpzw=="
            credentialsObtained.loginFormat == credential.loginFormat
            credentialsObtained.type == 'encrypted'
    }

    def "Test should return an exception if a credential doesn't exit"() {
        given:
            def idCredential = 'qweasd'
            def pathUserHome = Paths.get(path, "credentialUser.dat").toString()
            def pathProject = Paths.get(path, "credentialProject.dat").toString()
            ArrayList<String> credentialPaths = new ArrayList<String>()
            credentialPaths.add(pathUserHome)
            credentialPaths.add(pathProject)
        when:
            credentialFileManager.getCredentialById(idCredential, credentialPaths)
        then:
            thrown(Exception)
    }

    def "Test should return a credential if exit in the second credentials file"() {
        given:
            def idCredential = 'user1'
            def pathUserHome = Paths.get(path, "credentialUser.dat").toString()
            def pathProject = Paths.get(path, "credentialProject.dat").toString()
            ArrayList<String> credentialPaths = new ArrayList<String>()
            credentialPaths.add(pathUserHome)
            credentialPaths.add(pathProject)
        when:
            def credentialObtained = credentialFileManager.getCredentialById(idCredential, credentialPaths)
        then:
            credentialObtained.username == credential.username
            credentialObtained.password == "0bdzKV14J7bs2h1yw01eMQ=="
            credentialObtained.token == "xthkbHWGppQTkrqFhHzpzw=="
            credentialObtained.loginFormat == credential.loginFormat
            credentialObtained.type == 'encrypted'
    }


    def "Test should return a credential from credentials file of project directory if credential exit and if the file exit "() {
        given:
            def idCredential = 'user1'
            def pathUserHome = Paths.get(path, "credentialUserNotExist.dat").toString()
            def pathProject = Paths.get(path, "credentialProject.dat").toString()
            ArrayList<String> credentialPaths = new ArrayList<String>()
            credentialPaths.add(pathProject)
            credentialPaths.add(pathUserHome)
        when:
            def credentialObtained = credentialFileManager.getCredentialById(idCredential, credentialPaths)
        then:
            credentialObtained.username == credential.username
            credentialObtained.password == "0bdzKV14J7bs2h1yw01eMQ=="
            credentialObtained.token == "xthkbHWGppQTkrqFhHzpzw=="
            credentialObtained.loginFormat == credential.loginFormat
            credentialObtained.type == 'encrypted'
    }

    def "Test should return a credential from credentials file of home directory if credential exit and if the file exit "() {
        given:
            def idCredential = 'id'
            def pathUserHome = Paths.get(path, "credentialUser.dat").toString()
            def pathProject = Paths.get(path, "credentialProjectNotExist.dat").toString()
            ArrayList<String> credentialPaths = new ArrayList<String>()
            credentialPaths.add(pathProject)
            credentialPaths.add(pathUserHome)
        when:
            def credentialObtained = credentialFileManager.getCredentialById(idCredential, credentialPaths)
        then:
            credentialObtained.username == credential.username
            credentialObtained.password == "0bdzKV14J7bs2h1yw01eMQ=="
            credentialObtained.token == "xthkbHWGppQTkrqFhHzpzw=="
            credentialObtained.loginFormat == credential.loginFormat
            credentialObtained.type == 'encrypted'
    }

    def "Test should return an exception if the credentials files don't exit"() {
        given:
            def idCredential = 'user1'
            def pathUserHome = Paths.get(path, "credentialUser1.dat").toString()
            def pathProject = Paths.get(path, "credentialProject1.dat").toString()
            ArrayList<String> credentialPaths = new ArrayList<String>()
            credentialPaths.add(pathProject)
            credentialPaths.add(pathUserHome)
        when:
            credentialFileManager.getCredentialById(idCredential, credentialPaths)
        then:
            thrown(Exception)
    }

    def "Test should return an exception if the credentials files are invalid"() {
        given:
            def idCredential = 'user1'
            def pathUserHome = Paths.get(path, "emptyFile.dat").toString()
            def pathProject = Paths.get(path, "emptyFile.dat").toString()
            ArrayList<String> credentialPaths = new ArrayList<String>()
            credentialPaths.add(pathProject)
            credentialPaths.add(pathUserHome)
        when:
            credentialFileManager.getCredentialById(idCredential, credentialPaths)
        then:
            thrown(Exception)
    }

    def "Test should return an exception if array of credentials path is null"() {
        given:
            def idCredential = 'user1'
        when:
            credentialFileManager.getCredentialById(idCredential, null)
        then:
            thrown(Exception)
    }

    def "Test should get a credential from project directory how to priority"() {
        given:
            def idCredential = 'user3'
            def pathUserHome = Paths.get(path, "credentialUser.dat").toString()
            def pathProject = Paths.get(path, "credentialProject.dat").toString()
            ArrayList<String> credentialPaths = new ArrayList<String>()
            credentialPaths.add(pathProject)
            credentialPaths.add(pathUserHome)
        when:
            def credentialObtained = credentialFileManager.getCredentialById(idCredential, credentialPaths)
        then:
            credentialObtained.id == 'user3'
            credentialObtained.username == 'username.priority@email.com'
            credentialObtained.password == 'passwordPriority'
            credentialObtained.token == 'tokenPriority'
            credentialObtained.loginFormat == LoginType.TEST.value()
            credentialObtained.type == 'normal'
    }

    def cleanupSpec() {
        new File(pathSecretKeyGenerated).delete()
    }
}
