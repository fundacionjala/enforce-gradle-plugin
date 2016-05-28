/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager

import org.eclipse.jdt.internal.compiler.impl.Constant
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialManagerInput
import org.fundacionjala.gradle.plugins.enforce.tasks.ForceTask
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.file.Paths

abstract class CredentialManagerTask extends ForceTask {
    private final String SECRET_KEY_PATH = Paths.get(System.properties['user.home'].toString(), 'keyGenerated.txt').toString()
    private final String LOCATION = "location"

    public CredentialManagerInput credentialManagerInput
    public String location = 'home'
    public String status = ""

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

        String statusTemp = project.properties[ShowCredentialOptions.STATUS.value()].toString()
        switch (statusTemp) {
            case ShowCredentialOptions.VALID_STATUS.value() : status = ShowCredentialOptions.VALID_STATUS.value()
                break
            case ShowCredentialOptions.INVALID_STATUS.value() : status = ShowCredentialOptions.INVALID_STATUS.value()
                break
            case Constants.EMPTY : status = ShowCredentialOptions.ALL_STATUS.value()
                break
        }
    }

    /**
     * Abstract method: When implement a method can select steps for file monitor task
     */
    abstract void runTask()
}