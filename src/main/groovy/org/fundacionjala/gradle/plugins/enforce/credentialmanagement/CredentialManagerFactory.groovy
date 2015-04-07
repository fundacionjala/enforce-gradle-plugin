/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.credentialmanagement

class CredentialManagerFactory {

    /**
     * Returns a instance with a type of CredentialManagement to write and read credentials
     * @param credentialManagementType is type that credential will be to management
     * @param pathCredential is path of credentials file
     * @param pathSecretKey is path of secret key
     * @return a instance of class to write and read credentials
     */
    public static ICredentialManager getCredentialManagement(CredentialManagerType credentialManagementType, String pathCredential, String pathSecretKey) {
        ICredentialManager credentialManager
        switch (credentialManagementType) {
            case CredentialManagerType.FILE:
                credentialManager = new CredentialFileManager(pathCredential, pathSecretKey)
                break
            default:
                null
        }
        return credentialManager
    }
}
