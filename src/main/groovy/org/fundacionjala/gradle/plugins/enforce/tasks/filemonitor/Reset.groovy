/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.filemonitor

import java.nio.file.Paths

/**
 * Resets file monitor tracker
 */
class Reset extends FileMonitorTask{
    private static final String DESCRIPTION_REFRESH = "Reset the file monitor"
    private final String FILE_TRACKER_NAME = '.fileTracker.data'

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
        removeFileTracker()
        componentMonitor.saveCurrentComponents(sourceComponents)
    }

    /**
     * Removes .fileTracker.data file form project directory
     */
    void removeFileTracker() {
        String fileTrackerPath = Paths.get(projectPath, FILE_TRACKER_NAME)
        new File(fileTrackerPath).delete()
    }
}
