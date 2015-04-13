/*
 * Copyright (c) Fundacion Jala. All rights reserved.
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

    /**
     * This class Updates credential from credentials.dat file
     */
    CredentialUpdater() {
        super("You can update a credential", "Credential Manager")
    }

    @Override
    void runTask() {
        if (CredentialParameterValidator.hasIdCredential(project)) {
            updateCredentialByParameters()
        }
        if (!CredentialParameterValidator.hasIdCredential(project) || !CredentialParameterValidator.hasUserName(project)) {
            while (credentialManagerInput.finished) {
                credentialManagerInput.updateCredentialByConsole()
            }
        }
    }

    /**
     * Updates a credential by parameters
     */
    public void updateCredentialByParameters() {
        if (!credentialManagerInput.hasCredential(project.properties[CredentialMessage.ID_PARAM.value()].toString())) {
            throw new CredentialException(CredentialMessage.MESSAGE_ID_CREDENTIAL_DOES_NOT_EXIST.value())
        }
        if(CredentialParameterValidator.validateFieldsCredential(project)) {
            def credentialId = project.properties[CredentialMessage.ID_PARAM.value()].toString()
            credentialManagerInput.updateCredential(getCredential(credentialManagerInput.getCredentialToUpdate(credentialId).type))
        }
    }
}
