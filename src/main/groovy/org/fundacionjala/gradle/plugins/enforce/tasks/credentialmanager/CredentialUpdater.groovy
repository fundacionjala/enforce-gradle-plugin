/*
 * Copyright (c) Fundación Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager

import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialManagerInput
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialMessage
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialParameter
import org.fundacionjala.gradle.plugins.enforce.exceptions.CredentialException
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential

import java.nio.file.Paths

class CredentialUpdater extends CredentialManagerTask {
    private final String SECRET_KEY_PATH = Paths.get(System.properties['user.home'], 'keyGenerated.txt').toString()
    private final String PATH_FILE_CREDENTIALS = Paths.get(System.properties['user.home'], 'credentials.dat').toString()
    private CredentialManagerInput credentialUpdaterInput

    CredentialUpdater() {
        super("You can update a credential", "Credential Manager")
    }

    @Override
    void runTask() {
        credentialUpdaterInput = new CredentialManagerInput(PATH_FILE_CREDENTIALS, SECRET_KEY_PATH)
        if (CredentialParameterValidator.hasIdCredential(project)) {
            updateCredentialByParameters()
        }
        if (!CredentialParameterValidator.hasIdCredential(project) || !CredentialParameterValidator.hasUserName(project)) {
            while (credentialUpdaterInput.finished) {
                credentialUpdaterInput.updateCredentialByConsole()
            }
        }
    }

    /**
     * Updates a credential by parameters
     */
    public void updateCredentialByParameters() {
        if (!credentialUpdaterInput.hasCredential(project.properties[CredentialMessage.ID_PARAM.value()])) {
            throw new CredentialException(CredentialMessage.MESSAGE_ID_CREDENTIAL_DOES_NOT_EXIST.value())
        }
        if(CredentialParameterValidator.validateFieldsCredential(project)) {
            credentialUpdaterInput.updateCredential(getCredential())
        }
    }

    /**
     * Gets a credential inserted by parameters
     * @return a credential
     */
    public Credential getCredential() {
        Credential credential = new Credential()
        credential.id = project.properties[CredentialMessage.ID_PARAM.value()]
        credential.username = project.properties[CredentialParameter.USER_NAME.value()]
        credential.password = project.properties[CredentialParameter.PASSWORD.value()]
        credential.token = project.properties[CredentialParameter.TOKEN.value()]
        credential.loginFormat = CredentialParameterValidator.getLoginType(project)
        credential.type = credentialUpdaterInput.getCredentialToUpdate(credential.id).type
        return credential
    }
}
