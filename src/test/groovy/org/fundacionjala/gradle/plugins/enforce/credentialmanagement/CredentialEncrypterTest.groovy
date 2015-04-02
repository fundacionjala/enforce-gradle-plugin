/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.credentialmanagement

import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType
import spock.lang.Shared
import spock.lang.Specification

import javax.crypto.spec.SecretKeySpec
import java.nio.file.Paths
import java.security.Key

class CredentialEncrypterTest extends Specification {
    @Shared
    Credential credential

    @Shared
    CredentialEncrypter credentialEncrypter

    @Shared
    def secretKey

    @Shared
    def path = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala", "gradle",
            "plugins","enforce", "credentialmanagement", "resources").toString()

    @Shared
    Key secretKeyGenerated

    def setup() {
        credential = new Credential()
        credential.id = 'id'
        credential.username = 'username@email.com'
        credential.password = 'password'
        credential.token = 'token'
        credential.loginFormat = LoginType.TEST
        credential.type = 'encrypted'
        def mockMacAddress = '94de80a07008'
        def mockSALT = "CODD"
        secretKey = "${mockMacAddress}${mockSALT}"
        secretKeyGenerated = new SecretKeySpec(secretKey.getBytes(), 'AES')
        CredentialEncrypter.generateSecretKey(secretKey) >> secretKeyGenerated
        credentialEncrypter = new CredentialEncrypter()
    }

    def "Test should be instance of CredentialEncryption class"() {
        expect:
        credentialEncrypter instanceof CredentialEncrypter
    }

    def "Test should generate a secret key"() {
        given:
            def secretKey = '1111111111111111'
        when:
            def result = CredentialEncrypter.generateSecretKey(secretKey)
        then:
            result.encoded.encodeBase64().toString() == 'MTExMTExMTExMTExMTExMQ=='
    }

    def "Test should return a exception if secret key isn't 16 bytes "() {
        given:
            def secretKey = 'secretKey'
        when:
            CredentialEncrypter.generateSecretKey(secretKey)
        then:
            thrown(Exception)
    }

    def "Test should return a exception if secret key is null"() {
        when:
            CredentialEncrypter.generateSecretKey(null)
        then:
            thrown(Exception)
    }

    def "Test should encrypt a value and return a value encrypted"() {
        given:
            def valueToEncrypt = 'password'
        when:
            def valueEncrypted = credentialEncrypter.encryptData(valueToEncrypt, CredentialEncrypter.generateSecretKey(secretKey))
        then:
            valueEncrypted == '0bdzKV14J7bs2h1yw01eMQ=='
    }

    def "Test should return a exception if there is a problem"() {
        given:
            def valueToEncrypt = 'password'
            def secretKey = 'invalidKey'
        when:
            credentialEncrypter.encryptData(valueToEncrypt, CredentialEncrypter.generateSecretKey(secretKey))
        then:
            thrown(Exception)
    }

    def "Test should return credential encrypted "() {
        given:
            String passwordEncrypted = '0bdzKV14J7bs2h1yw01eMQ=='
            String tokenEncrypted = 'xthkbHWGppQTkrqFhHzpzw=='
        when:
            Credential credentialEncrypted = credentialEncrypter.encryptCredential(credential, secretKeyGenerated.encoded.encodeBase64().toString())
        then:
            credentialEncrypted.id == credential.id
            credentialEncrypted.username == credential.username
            credentialEncrypted.password == passwordEncrypted
            credentialEncrypted.token == tokenEncrypted
            credentialEncrypted.loginFormat == credential.loginFormat
            credentialEncrypted.type == 'encrypted'
    }

    def "Test should return a exception if you send a invalid secret key"() {
        given:
            def secretKeyString = 'invalidKey'
        when:
            credentialEncrypter.encryptCredential(credential, secretKeyString)
        then:
            thrown(Exception)
    }

    def "Test should return a exception if credential is null in credential encrypted"() {
        given:
            def secretKeyString = 'invalidKey'
        when:
            credentialEncrypter.encryptCredential(null, secretKeyString)
        then:
            thrown(Exception)
    }

    def "Test should decrypt a value"() {
        given:
            def valueEncrypted = '0bdzKV14J7bs2h1yw01eMQ=='
        when:
            def valueDecrypted = credentialEncrypter.decryptData(valueEncrypted, CredentialEncrypter.generateSecretKey(secretKey))
        then:
            valueDecrypted == 'password'
    }

    def "Test should return an exception if there is problem when you are decrypting a value"() {
        given:
            def valueEncrypted = '0bdzKV14J7bs2h1yw01eMQ=='
            def secretKeyString = 'invalidKey'
        when:
            credentialEncrypter.decryptData(valueEncrypted, CredentialEncrypter.convertStringToKey(secretKeyString))
        then:
            thrown(Exception)
    }

    def "Test should return a credential decrypted"() {
        given:
            Credential credentialSpec = new Credential()
            credentialSpec.id = credential.id
            credentialSpec.username = credential.username
            credentialSpec.password = '0bdzKV14J7bs2h1yw01eMQ=='
            credentialSpec.token = 'xthkbHWGppQTkrqFhHzpzw=='
            credentialSpec.loginFormat = credential.loginFormat
            credentialSpec.type = credential.type
        when:
            def credentialDecrypted = credentialEncrypter.decryptCredential(credentialSpec, secretKeyGenerated.encoded.encodeBase64().toString())
        then:
            credentialDecrypted.id == credentialSpec.id
            credentialDecrypted.username == credentialSpec.username
            credentialDecrypted.password == credential.password
            credentialDecrypted.token == credential.token
            credentialDecrypted.loginFormat == credentialSpec.loginFormat
            credentialDecrypted.type == credentialSpec.type
    }

    def "Test should return a exception if you send a invalid secret key when you is decrypting credential"() {
        given:
            def secretKeyString = 'invalidKey'
        when:
            credentialEncrypter.decryptCredential(credential, secretKeyString)
        then:
            thrown(Exception)
    }

    def "Test should return a exception if credential is null"() {
        given:
            def secretKeyString = 'invalidKey'
        when:
            credentialEncrypter.decryptCredential(null, secretKeyString)
        then:
            thrown(Exception)
    }

    def "Test should convert a string in a key type"() {
        given:
            String secretKeyString = 'OTRkZTgwYTA3MDA4Q09ERA=='
        when:
            Key valueConverted = CredentialEncrypter.convertStringToKey(secretKeyString)
        then:
            valueConverted == CredentialEncrypter.generateSecretKey(secretKey)
    }
}


