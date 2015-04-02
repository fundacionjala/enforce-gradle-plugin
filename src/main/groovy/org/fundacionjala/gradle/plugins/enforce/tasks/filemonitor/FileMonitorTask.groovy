/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.filemonitor

import org.fundacionjala.gradle.plugins.enforce.filemonitor.FileMonitorSerializer
import org.fundacionjala.gradle.plugins.enforce.tasks.ForceTask

import java.nio.file.Paths

/**
 * Base of the file monitor tasks
 */
abstract class FileMonitorTask extends ForceTask {

    private final String FILE_TRACKING = '/.fileTracker.data'
    FileMonitorSerializer fileMonitorSerializer
    public static final String GROUP_FILE_MONITOR_TASK = "File Monitor"
    ArrayList<File> fileArray

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
        def pathFileTracker = Paths.get(projectPath, FILE_TRACKING).toString()
        fileMonitorSerializer = new FileMonitorSerializer(pathFileTracker)
        fileArray = fileManager.getValidElements(projectPath, excludeFilesToMonitor)
        runTask()
    }

    /**
     * Abstract method: When implement a method can select steps for file monitor task
     */
    abstract void runTask()
}
