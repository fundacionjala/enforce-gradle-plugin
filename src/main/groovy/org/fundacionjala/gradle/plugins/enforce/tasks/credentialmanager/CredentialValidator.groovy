/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager

import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialEncrypter
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialMessage
import org.fundacionjala.gradle.plugins.enforce.wsc.Connector
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential

import java.nio.file.Paths

/**
 * This class performs/validates SFDC credentials.
 */
class CredentialValidator {
    private static final String SECRET_KEY_PATH = Paths.get(System.properties['user.home'].toString(), 'keyGenerated.txt').toString()

    private static final String NULL_MESSAGE = "Can not validate a null credential"

    private CredentialEncrypter credentialEncrypter

    private String secretKeyPath

    /**
     * Default constructor that initializes the encryptor.
     */
    public CredentialValidator() {
        credentialEncrypter = new CredentialEncrypter()
        secretKeyPath = SECRET_KEY_PATH
    }

    /**
     * Validates a encrypt/decrypt credential on Enforce.
     *
     * @param credential to be validated.
     * @exception IllegalArgumentException if credential is null.
     */
    public void validateCredential(Credential credential) throws Exception {
        if (credential == null) {
            throw new IllegalArgumentException(NULL_MESSAGE)
        }
        validateCredential(credential, new Connector(credential.loginFormat))
    }

    /**
     * Validates a encrypt/decrypt credential on Enforce only available for unit test.
     *
     * @param credential to be validated.
     * @param connector to perform the login with salesforce.
     */
    void validateCredential(Credential credential, Connector connector)  throws Exception {
        String encrypted = CredentialMessage.ENCRYPTED.value()
        if (encrypted.equals(credential.type)) {
            credential = decryptCredential(credential)
        }
        connector.login(credential)
    }

    /**
     * Sets the credential encrypter only available for unit test.
     *
     * @param credentialEncrypter to be set.
     */
    void setCredentialEncrypter(CredentialEncrypter credentialEncrypter) {
        this.credentialEncrypter = credentialEncrypter
    }

    /**
     * Sets the secret key path only available for unit test.
     *
     * @param secretKeyPath to be set.
     */
    void setSecretKeyPath(String secretKeyPath) {
        this.secretKeyPath = secretKeyPath
    }

    /**
     * Returns the decrypted credential.
     *
     * @param credential to be decrypted.
     * @return the decrypt credential.
     */
    private Credential decryptCredential(Credential credential) {
        String secretKey = new File(secretKeyPath).text
        return credentialEncrypter.decryptCredential(credential, secretKey)
    }
}
