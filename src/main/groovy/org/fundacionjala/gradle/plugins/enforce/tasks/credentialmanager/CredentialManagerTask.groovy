/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager

import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialManagerInput
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialMessage
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialParameter
import org.fundacionjala.gradle.plugins.enforce.tasks.ForceTask
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential

import java.nio.file.Paths

abstract class CredentialManagerTask extends ForceTask {
    private final String SECRET_KEY_PATH = Paths.get(System.properties['user.home'].toString(), 'keyGenerated.txt').toString()
    private final String PATH_FILE_CREDENTIALS = Paths.get(System.properties['user.home'].toString(), 'credentials.dat').toString()
    public CredentialManagerInput credentialManagerInput

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    CredentialManagerTask(String description, String group) {
        super(description, group)
    }

    /**
     * execute the method run
     */
    @Override
    void executeTask() {
        credentialManagerInput = new CredentialManagerInput(PATH_FILE_CREDENTIALS, SECRET_KEY_PATH)
        runTask()
    }

    /**
     * Abstract method: When implement a method can select steps for file monitor task
     */
    abstract void runTask()
}
