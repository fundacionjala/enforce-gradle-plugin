/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.credentialmanagement

import org.fundacionjala.gradle.plugins.enforce.exceptions.CredentialException
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential

import java.nio.file.Paths
import java.security.Key

/**
 * Adds updates ang gets a credentials
 */
class CredentialManager {
    private String pathSecretKey = Paths.get(System.properties['user.home'].toString(), 'keyGenerated.txt').toString()
    private String secretKeyGenerated
    private Key secretKey
    private ICredentialManager credentialManager
    private CredentialEncrypter credentialEncrypter
    private final String SALT = 'CODD'
    public String pathCredentials = Paths.get(System.properties['user.home'].toString(), 'credentials.dat').toString()

    /**
     * Sets path credentials and path secret key also generated and saved secret key generated also gets a instance to read and write credentials
     * @param pathCredentials is target where the credentials will be saved
     * @param pathSecretKey is target where the secret key generated will be saved
     */
    CredentialManager(String pathCredentials, String pathSecretKey) {
        this.pathCredentials = pathCredentials
        this.pathSecretKey = pathSecretKey
        init()
    }

    /**
     * This constructor uses directories by default to save credentials file and secret key generated
     * Path by default is user's home directory
     */
    CredentialManager() {
        init()
    }

    /**
     * Creates an instance of CredentialManager class
     * Creates an instance of CredentialEncrypter class
     * Generates secret key
     * Saves secret key generated
     * Creates an instance to File
     */
    void init() {
        credentialManager = CredentialManagerFactory.getCredentialManagement(CredentialManagerType.FILE, this.pathCredentials, this.pathSecretKey)
        credentialEncrypter = new CredentialEncrypter()
        secretKey = CredentialEncrypter.generateSecretKey("${Util.getMacAddress()}${SALT}")
        credentialManager.saveSecretKeyGenerated(secretKey.encoded.encodeBase64().toString())
        secretKeyGenerated = new File(pathSecretKey).text
    }

    /**
     * Adds credentials encrypted and credentials decrypted
     * @param credential is a credential to add
     */
    void addCredential(Credential credential) {
        Credential newCredential = credential
        if (credential.type == CredentialMessage.ENCRYPTED.value()) {
            newCredential = getCredentialEncrypted(credential)
        }
        credentialManager.saveCredential(newCredential)
    }

    /**
     * Updates a credentials encrypted and credentials decrypted
     * @param credential is a credential to update
     */
    void updateCredential(Credential credential) {
        Credential credentialToUpdate
        try {
            credentialToUpdate = credentialManager.getCredentialById(credential.id)
        } catch (Exception exception) {
            throw new CredentialException(exception)
        }
        if (credential.type == CredentialMessage.ENCRYPTED.value()) {
            credentialToUpdate = getCredentialEncrypted(credential)
        } else {
            credentialToUpdate = credential
        }
        credentialManager.saveCredential(credentialToUpdate)
    }

    /**
     * Gets a credential decrypted to authenticate in salesForce
     * @param id of credential to get
     * @return a credential
     */
    Credential getCredentialToAuthenticate(String id, ArrayList<String> fileCredentials) {
        Credential credential = credentialManager.getCredentialById(id, fileCredentials)
        if (credential.type == CredentialMessage.ENCRYPTED.value()) {
            credential = credentialEncrypter.decryptCredential(credential, secretKeyGenerated)
        }
        return credential
    }

    /**
     * Gets a credential by id
     * @param idCredential id credential to find
     * @return a credential
     */
    Credential getCredentialById(String idCredential) {
        return credentialManager.getCredentialById(idCredential)
    }

    /**
     * Gets a credential encrypted
     * @param credential is a credential to encrypt
     * @return a credential encrypted
     */
    private Credential getCredentialEncrypted(Credential credential) {
        return credentialEncrypter.encryptCredential(credential, secretKeyGenerated)
    }
}
