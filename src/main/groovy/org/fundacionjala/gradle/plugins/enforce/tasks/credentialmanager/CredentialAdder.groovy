/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager

import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialManagerInput
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialMessage
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialParameter
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential

import java.nio.file.Paths

class CredentialAdder extends CredentialManagerTask {
    private final String SECRET_KEY_PATH = Paths.get(System.properties['user.home'], 'keyGenerated.txt').toString()
    private final String PATH_FILE_CREDENTIALS = Paths.get(System.properties['user.home'], 'credentials.dat').toString()
    CredentialManagerInput credentialAdderInput

    /**
     * Constructor add description an group of task
     */
    CredentialAdder() {
        super("You can add a credential", "Credential Manager")
    }

    @Override
    void runTask() {
        credentialAdderInput = new CredentialManagerInput(PATH_FILE_CREDENTIALS, SECRET_KEY_PATH)
        if (CredentialParameterValidator.hasIdCredential(project)) {
            addCredentialByParameters()
        }
        if (!CredentialParameterValidator.hasIdCredential(project) || !CredentialParameterValidator.hasUserName(project)) {
            while (credentialAdderInput.finished) {
                credentialAdderInput.addCredentialByConsole()
            }
        }
    }

    /**
     * Set inputs by parameters
     */
    public Credential getCredential() {
        Credential credential = new Credential()
        credential.id = project.properties[CredentialMessage.ID_PARAM.value()]
        credential.username = project.properties[CredentialParameter.USER_NAME.value()]
        credential.password = project.properties[CredentialParameter.PASSWORD.value()]
        credential.token = project.properties[CredentialParameter.TOKEN.value()]
        credential.loginFormat = CredentialParameterValidator.getLoginType(project)
        credential.type = CredentialParameterValidator.getCredentialType(project)
        return credential
    }

    /**
     * Adds a credential by parameters
     */
    void addCredentialByParameters() {
        if (credentialAdderInput.hasCredential(project.properties[CredentialMessage.ID_PARAM.value()])) {
            throw new Exception(CredentialMessage.MESSAGE_ID_CREDENTIAL_EXIST.value())
        }
        if(CredentialParameterValidator.validateFieldsCredential(project)) {
            credentialAdderInput.addCredential(getCredential())
        }
    }
}