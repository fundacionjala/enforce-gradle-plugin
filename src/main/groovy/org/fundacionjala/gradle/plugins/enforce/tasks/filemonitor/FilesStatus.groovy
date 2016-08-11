/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.filemonitor

import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.file.Paths

/**
 * Create a file tracker and display files status
 */
class FilesStatus extends FileMonitorTask {
    private static final String DESCRIPTION_STATUS = "You can display elements that were changed"
    private static final String PARAMETER_SORT_BY = "sort"
    private static final String VALUE_SORT_BY_NAME = "name"

    Map filesChangedMap

    /**
     * Initializes the instance managementFile
     */
    FilesStatus() {
        super(DESCRIPTION_STATUS, FileMonitorTask.GROUP_FILE_MONITOR_TASK)
    }

    /**
     * Execute the steps for file status
     */
    @Override
    void runTask() {
        if (componentMonitor.verifyFileMap()) {
            filesChangedMap = componentMonitor.getComponentChanged(sourceComponents)
            displayFileChanged()
        } else {
            componentMonitor.saveCurrentComponents(sourceComponents)
        }
    }

    /**
     * Display typeName file and description status
     * @param fileArray files for verify
     */
    def displayFileChanged() {

        filesChangedMap = filesChangedMap.sort{it.key}
        filesChangedMap = filesChangedMap.sort{it.value.state}

        def hasSortParameter = Util.isValidProperty(project, PARAMETER_SORT_BY)

        if ( hasSortParameter && VALUE_SORT_BY_NAME.equals(project.properties[PARAMETER_SORT_BY]) ) {
            filesChangedMap = filesChangedMap.sort{it.key}
        }

        if (filesChangedMap.size() > 0) {
            println "*********************************************"
            println "              Status Files Changed             "
            println "*********************************************"
            filesChangedMap.each { componentPath, resultTracker ->
                println "${Paths.get(componentPath).getFileName()}${" - "}${resultTracker.toString()}"
            }
            println "*********************************************"
        }
    }
}
