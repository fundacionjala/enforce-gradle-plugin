/*
 * Copyright (c) Jalasoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.jalasoft.gradle.plugins.enforce.tasks.filemonitor

/**
 * Resets file monitor tracker
 */
class Reset extends FileMonitorTask{

    private static final String DESCRIPTION_REFRESH = "Reset the file monitor"

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    Reset() {
        super(DESCRIPTION_REFRESH, FileMonitorTask.GROUP_FILE_MONITOR_TASK)
    }

    @Override
    void runTask() {
        fileMonitorSerializer.mapRefresh(fileArray)
    }
}
