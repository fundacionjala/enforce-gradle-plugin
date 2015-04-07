/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.credentialmanagement

import org.fundacionjala.gradle.plugins.enforce.wsc.Credential

/**
 * Interface to implement read and write credentials
 */
public interface ICredentialManager {

    /**
     * Writes credentials
     * @param credential is type credential to write in a credentials collection
     */
    public void saveCredential(Credential credential)

    /**
     * Gets a credential
     * @param id of credential
     */
    public Credential getCredentialById(String id)

    /**
     * Gets a credential
     * @param id of credential
     * @param pathCredentials is source of credentials
     */
    public Credential getCredentialById(String id, ArrayList<String> paths)

    /**
     * Gets credentials
     * @param pathCredentials is source of credentials
     */
    public ArrayList<Credential> getCredentials()
}
