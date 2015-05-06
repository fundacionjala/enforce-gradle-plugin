/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.credentialmanagement

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import org.fundacionjala.gradle.plugins.enforce.exceptions.CredentialException
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential

/**
 * Reads and writes credentials in credentials file
 */
class CredentialFileManager implements ICredentialManager {
    private String pathCredentials
    private String pathSecretKeyGenerated
    private File credentialFile

    /**
     * Gets an instance to File and sets path credentials and path secret key generated
     * @param pathCredentials is target of credentials file
     * @param pathSecretKeyGenerated is target of secret key generated
     */
    CredentialFileManager(String pathCredentials, String pathSecretKeyGenerated) {
        this.pathCredentials = pathCredentials
        this.pathSecretKeyGenerated = pathSecretKeyGenerated
        credentialFile = new File(pathCredentials)
    }

    /**
     * Saves a credential in credentials file
     * @param credential is a object
     */
    public void saveCredential(Credential credential) {
        if (!credential) {
            throw new CredentialException(CredentialMessage.MESSAGE_EXCEPTION_CREDENTIAL.value())
        }
        JsonBuilder newCredentialJson = new JsonBuilder(convertCredentialLazyMap(credential))

        if (credentialFile.exists()) {

            if (!credentialFile) {
                throw new CredentialException(CredentialMessage.MESSAGE_FILE_INVALID.value())
            }
            LazyMap credentials = new JsonSlurper().parseText(credentialFile.getText())
            credentials.putAll(convertCredentialLazyMap(credential))
            newCredentialJson = new JsonBuilder(credentials)
        }
        credentialFile.text = newCredentialJson.toPrettyString()
    }

    /**
     * Gets a list of credentials
     * @return an arrayList with credentials
     */
    public ArrayList<Credential> getCredentials() {
        ArrayList<Credential> credentials = new ArrayList<Credential>()
        if (!credentialFile) {
            throw new CredentialException(CredentialMessage.MESSAGE_FILE_INVALID.value())
        }
        LazyMap credentialsMap = new JsonSlurper().parseText(credentialFile.getText())

        credentialsMap.each { credential ->
            credentials.add(convertToCredential(credential.key, credential.value))
        }
        return credentials
    }

    /**
     * Gets a credential from credentials file by its id
     * @param id of credential is a string
     * @return a credential found
     */
    public Credential getCredentialById(String idCredential) {
        if (!credentialFile) {
            throw new CredentialException(CredentialMessage.MESSAGE_FILE_DOES_NOT_EXIST.value())
        }
        LazyMap credentialsJson = getCredentialsMap(credentialFile)
        LazyMap credential = credentialsJson[idCredential]

        if (!credential) {
            throw new CredentialException("${idCredential}${CredentialMessage.MESSAGE_ID_CREDENTIAL_DOES_NOT_EXIST.value()}")
        }
        return convertToCredential(idCredential, credential)
    }
    /**
     * Gets a credential from credentials file from project directory and user home directory
     * @param idCredential to find
     * @param fileCredentials is list of credentials file
     * @return a credential
     */

    public Credential getCredentialById(String idCredential, ArrayList<String> fileCredentials) {
        Credential credentialObtained
        File credentialsFile
        if (!idCredential) {
            idCredential = CredentialMessage.DEFAULT_CREDENTIAL_NAME.value()
        }

        if (!fileCredentials) {
            throw new CredentialException(CredentialMessage.MESSAGE_EXCEPTION_ARRAY_CREDENTIALS_FILE_NULL.value())
        }

        for (String pathCredential in fileCredentials) {
            if (new File(pathCredential).exists()) {
                credentialsFile = new File(pathCredential)
                LazyMap credentialsJson = getCredentialsMap(credentialsFile)
                LazyMap credential = credentialsJson[idCredential]

                if (credential) {
                    credentialObtained = convertToCredential(idCredential, credential)
                    break
                }
            }
        }

        if (!credentialsFile) {
            throw new CredentialException(CredentialMessage.MESSAGE_FILE_DOES_NOT_EXIST.value())
        }

        if (!credentialObtained) {
            throw new CredentialException("Credential id: ${idCredential}, ${CredentialMessage.MESSAGE_ID_CREDENTIAL_DOES_NOT_EXIST.value()}")
        }
        return credentialObtained
    }

    /**
     * Returns a LazyMap with credentials
     * @param file is type File
     * @return return a LazyMap
     */
    private LazyMap getCredentialsMap(File file) {
        LazyMap CredentialsMap = new LazyMap()
        if (file && !file.text.isEmpty()) {
            CredentialsMap = new JsonSlurper().parseText(file.text) as LazyMap
        }
        return CredentialsMap
    }

    /**
     * Saves a secretKey generated
     * @param secretKey is type string
     */
    public void saveSecretKeyGenerated(String secretKey) {
        File file = new File(pathSecretKeyGenerated)
        file.text = secretKey
    }

    /**
     * Converts a value of LazyMap in type Credential
     * @param id of credential
     * @param credentialValues are values of a credential
     * @return a credential
     */
    private Credential convertToCredential(String id, LazyMap credentialValues) {
        Credential credential = new Credential()
        credential.id = id
        credential.username = credentialValues.username
        credential.password = credentialValues.password
        credential.token = credentialValues.token
        credential.loginFormat = credentialValues.sfdcType
        credential.type = credentialValues.type
        return credential
    }

    /**
     * Converts a credential in a LazyMap value
     * @param credential is a credential to convert
     * @return a LazyMap
     */
    private LazyMap convertCredentialLazyMap(Credential credential) {
        JsonBuilder credentialEncrypted = new JsonBuilder()
        credentialEncrypted {
            "${credential.id}"(
                    type: credential.type,
                    username: credential.username,
                    password: credential.password,
                    token: credential.token,
                    sfdcType: credential.loginFormat
            )
        }
        return new JsonSlurper().parseText(credentialEncrypted.toString())
    }
}
