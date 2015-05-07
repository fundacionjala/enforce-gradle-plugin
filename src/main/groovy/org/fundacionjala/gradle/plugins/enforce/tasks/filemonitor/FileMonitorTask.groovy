/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.filemonitor

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentMonitor
import org.fundacionjala.gradle.plugins.enforce.tasks.ForceTask

/**
 * Base of the file monitor tasks
 */
abstract class FileMonitorTask extends ForceTask {
    ComponentMonitor componentMonitor
    public static final String GROUP_FILE_MONITOR_TASK = "File Monitor"
    ArrayList<File> sourceComponents

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    FileMonitorTask(String description, String group) {
        super(description, group)
    }

    /**
     * execute the method run
     */
    @Override
    void executeTask() {
        componentMonitor = new ComponentMonitor(projectPath)
        sourceComponents = fileManager.getValidElements(projectPath, excludeFilesToMonitor)
        runTask()
    }

    /**
     * Abstract method: When implement a method can select steps for file monitor task
     */
    abstract void runTask()
}
