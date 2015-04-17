/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager

import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialMessage

class CredentialAdder extends CredentialManagerTask {

    /**
     * Constructor add description an group of task
     */
    CredentialAdder() {
        super(CredentialMessage.ADD_CREDENTIAL_DESCRIPTION.value(), CredentialMessage.CREDENTIAL_MANAGER_GROUP.value())
    }

    @Override
    void runTask() {
        if (CredentialParameterValidator.hasIdCredential(project)) {
            addCredentialByParameters()
        }
        if (!CredentialParameterValidator.hasIdCredential(project) || !CredentialParameterValidator.hasUserName(project)) {
            while (credentialManagerInput.finished) {
                credentialManagerInput.addCredentialByConsole()
            }
        }
    }

    /**
     * Adds a credential by parameters
     */
    void addCredentialByParameters() {
        if (credentialManagerInput.hasCredential(project.properties[CredentialMessage.ID_PARAM.value()].toString())) {
            throw new Exception(CredentialMessage.MESSAGE_ID_CREDENTIAL_EXIST.value())
        }
        if (CredentialParameterValidator.validateFieldsCredential(project)) {
            String encryptedValue = project.properties[CredentialMessage.ENCRYPTED.value()]
            String credentialType = CredentialParameterValidator.getCredentialType(encryptedValue)
            credentialManagerInput.addCredential(CredentialParameterValidator.getCredentialInserted(project, credentialType))
        }
    }
}