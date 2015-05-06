/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.credentialmanagement

import org.fundacionjala.gradle.plugins.enforce.exceptions.CredentialException
import org.fundacionjala.gradle.plugins.enforce.utils.CharsetUtil
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import sun.misc.BASE64Decoder

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.Key

/**
 * Encrypts and decrypts credentials and generate secret key
 */
class CredentialEncrypter {
    private static final String ALGORITHM = 'AES'
    private static final int BYTES = 16

    /**
     * Returns a secret key generated
     * @param secretKey is a string should have 16 bytes as size
     * @return a secret key
     */
    public static Key generateSecretKey(String secretKey) {
        byte[] bytes = secretKey.getBytes()
        if (!secretKey || bytes.length != BYTES) {
            throw new CredentialException(CredentialMessage.MESSAGE_EXCEPTION_SECRET_KEY.value())
        }
        return new SecretKeySpec(bytes, ALGORITHM)
    }

    /**
     * Returns a value encrypted
     * @param data is a value that you want encrypt
     * @param secretKey is secret key to encrypt value
     * @return a value encrypted
     */
    private String encryptData(String data, Key secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            byte[] encryptValue = cipher.doFinal(data.getBytes(CharsetUtil.UTF_8))
            return encryptValue.encodeBase64().toString()
        } catch (Exception exception) {
            throw new CredentialException(CredentialMessage.MESSAGE_EXCEPTION_ENCRYPTING.value(), exception)
        }
    }

    /**
     * Returns a credential encrypted using a key sent
     * @param credential is a credential that you want encrypt
     * @param secretKey is a key to decrypt a credential
     * @return a credential encrypted
     */
    public Credential encryptCredential(Credential credential, String secretKey) {
        if (!credential) {
            throw new CredentialException(CredentialMessage.MESSAGE_EXCEPTION_CREDENTIAL.value())
        }
        Key secretKeyEncode = convertStringToKey(secretKey)
        Credential credentialEncrypted = new Credential()
        credentialEncrypted.id = credential.id
        credentialEncrypted.username = credential.username
        credentialEncrypted.password = encryptData(credential.password, secretKeyEncode)
        credentialEncrypted.token = encryptData(credential.token, secretKeyEncode)
        credentialEncrypted.loginFormat = credential.loginFormat
        credentialEncrypted.type = CredentialMessage.ENCRYPTED.value()
        return credentialEncrypted
    }

    /**
     * Returns a value decrypted using a secret key sent
     * @param encryptedData is a value encrypted
     * @param secretKey is a key to decrypt value
     * @return a value decrypted
     */
    private String decryptData(String encryptedData, Key secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            byte[] decryptedValue = cipher.doFinal(new BASE64Decoder().decodeBuffer(encryptedData))
            return new String(decryptedValue)
        } catch (Exception exception) {
            throw new CredentialException(CredentialMessage.MESSAGE_EXCEPTION_DECRYPTING.value(), exception)
        }
    }

    /**
     * Returns a credential decrypted using a key
     * @param idCredential is id of credential that you want decrypt
     * @param pathCredentials is address of credentials
     * @param secretKey is a key to decrypt a credential
     * @return a credential decrypted
     */
    public Credential decryptCredential(Credential credential, String secretKey) {
        if (!credential) {
            throw new CredentialException(CredentialMessage.MESSAGE_EXCEPTION_CREDENTIAL.value())
        }
        Key secretKeyEncode = convertStringToKey(secretKey)
        Credential credentialDecrypted = new Credential()
        credentialDecrypted.id = credential.id
        credentialDecrypted.username = credential.username
        credentialDecrypted.password = decryptData(credential.password, secretKeyEncode)
        credentialDecrypted.token = decryptData(credential.token, secretKeyEncode)
        credentialDecrypted.loginFormat = credential.loginFormat
        credentialDecrypted.type = credential.type
        return credentialDecrypted
    }

    /**
     * Returns a secret key of type Key
     * @param secretKey is a string
     * @return a secretKey type key
     */
    private static Key convertStringToKey(String secretKey) {
        return new SecretKeySpec(secretKey.decodeBase64(), ALGORITHM)
    }
}