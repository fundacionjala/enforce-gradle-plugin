/*
 * Copyright (c) Jalasoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.jalasoft.gradle.plugins.enforce.tasks.credentialmanager

import org.jalasoft.gradle.plugins.enforce.tasks.ForceTask

abstract class CredentialManagerTask extends ForceTask {
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
        runTask()
    }

    /**
     * Abstract method: When implement a method can select steps for file monitor task
     */
    abstract void runTask()
}
