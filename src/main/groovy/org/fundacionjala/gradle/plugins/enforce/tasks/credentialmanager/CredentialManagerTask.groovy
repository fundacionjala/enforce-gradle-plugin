/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager

import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialManagerInput
import org.fundacionjala.gradle.plugins.enforce.tasks.ForceTask
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.file.Paths

abstract class CredentialManagerTask extends ForceTask {
    private final String SECRET_KEY_PATH = Paths.get(System.properties['user.home'].toString(), 'keyGenerated.txt').toString()
    private final String LOCATION = "location"

    public CredentialManagerInput credentialManagerInput
    public String location = 'home'

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    CredentialManagerTask(String description, String group) {
        super(description, group)
        loadLocationParameter()
    }

    /**
     * execute the method run
     */
    @Override
    void executeTask() {
        credentialManagerInput = new CredentialManagerInput(location, SECRET_KEY_PATH)
        runTask()
    }

    void loadLocationParameter() {
        if (Util.isValidProperty(project, LOCATION) && !Util.isEmptyProperty(project, LOCATION)) {
            location = project.properties[LOCATION].toString()
        }
    }

    /**
     * Abstract method: When implement a method can select steps for file monitor task
     */
    abstract void runTask()
}
