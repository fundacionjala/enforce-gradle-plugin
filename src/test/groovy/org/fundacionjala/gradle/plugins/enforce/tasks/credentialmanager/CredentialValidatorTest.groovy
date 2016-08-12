/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager

import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialEncrypter
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialFileManager
import org.fundacionjala.gradle.plugins.enforce.wsc.Connector
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.Session
import spock.lang.Shared
import spock.lang.Specification
import java.nio.file.Paths

class CredentialValidatorTest extends Specification {
    @Shared
    String path = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala", "gradle",
            "plugins", "enforce", "tasks", "credentialmanager", "resources").toString()

    @Shared
    String pathCredentials = Paths.get(path, "credentials.dat").toString()

    @Shared
    String pathSecretKeyGenerated = Paths.get(path, "secretKeyGenerated.text").toString()

    @Shared
    CredentialFileManager credentialFileManager

    @Shared
    CredentialEncrypter credentialEncrypter

    @Shared
    CredentialValidator credentialValidator

    @Shared
    Connector connector

    @Shared
    Session session

    def setup() {
        File secretKeyGeneratedFile = new File(pathSecretKeyGenerated)
        secretKeyGeneratedFile.write("")
        credentialFileManager = new CredentialFileManager(pathCredentials, pathSecretKeyGenerated)
        credentialValidator = new CredentialValidator()
        credentialEncrypter = Mock(CredentialEncrypter)
        credentialValidator.setCredentialEncrypter(credentialEncrypter)
        credentialValidator.setSecretKeyPath(pathSecretKeyGenerated)
        credentialEncrypter.decryptCredential(_ as Credential, _ as String) >> { Credential credential, String token ->
            if (credential.type == "encrypted") {
                if (credential.id == "invalidencrypted") {
                    credential.password = "invalid"
                } else {
                    credential.password = "valid"
                }
                credential.token = token
            }
            return credential
        }
        connector = Mock(Connector)
        session = Mock(Session)
        connector.login(_ as Credential) >> { Credential credential ->
            Credential validCredential = credentialFileManager.getCredentialById("valid")
            if (credential.username == validCredential.username &&
                    credential.passwordToken == validCredential.passwordToken) {
                return session
            }
            throw new Exception()
        }
    }

    def "Test should throw exception if credential is null"() {
        when:
        credentialValidator.validateCredential(null)
        then:
        thrown(IllegalArgumentException)
    }

    def "Test should throw exception if credential is not active"() {
        given:
        Credential credential = credentialFileManager.getCredentialById("invalid")
        when:
        credentialValidator.validateCredential(credential, connector)
        then:
        thrown(Exception)
    }

    def "Test should throw if the encrypted credential is not active"() {
        given:
        Credential credential = credentialFileManager.getCredentialById("invalidencrypted")
        when:
        credentialValidator.validateCredential(credential, connector)
        then:
        1 * credentialEncrypter.decryptCredential(_ as Credential, _ as String)

        when:
        credentialValidator.validateCredential(credential, connector)
        then:
        thrown(Exception)
    }

    def "Test should verify that the connection is successful for a activated credential"() {
        given:
        Credential credential = credentialFileManager.getCredentialById("valid")
        when:
        credentialValidator.validateCredential(credential, connector)
        then:
        1 * connector.login(_ as Credential)
    }


    def "Test should verify that the connection is successful for a activated encrypted credential"() {
        given:
        Credential credential = credentialFileManager.getCredentialById("validencrypted")
        when:
        credentialValidator.validateCredential(credential, connector)
        then:
        1 * credentialEncrypter.decryptCredential(_ as Credential, _ as String)

        when:
        credentialValidator.validateCredential(credential, connector)
        then:
        1 * connector.login(_ as Credential)
    }

    def cleanupSpec() {
        new File(pathSecretKeyGenerated).delete()
    }
}
