/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.filemonitor

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ResultTracker

import java.nio.file.Paths

/**
 * Create a file tracker and display files status
 */
class FilesStatus extends FileMonitorTask {

    private static final String DESCRIPTION_STATUS = "You can display elements that were changed"
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
        /*if (fileMonitorSerializer.verifyFileMap()) {
            filesChangedMap = fileMonitorSerializer.getFileChangedExclude(fileArray)
            displayFileChanged()
        } else {
            Map initMapSave = fileMonitorSerializer.loadSignatureForFilesInDirectory(fileArray)
            fileMonitorSerializer.saveMap(initMapSave)
        }*/
        if (componentMonitor.verifyFileMap()) {
            filesChangedMap = componentMonitor.getComponentChanged(fileArray)
            displayFileChanged()
        } else {
            Map initMapSave = componentMonitor.getComponentsSignature(fileArray)
            componentMonitor.componentSerializer.save(initMapSave)
        }
    }

    /**
     * Display typeName file and description status
     * @param fileArray files for verify
     */
    def displayFileChanged() {

        if (filesChangedMap.size() > 0) {
            println "*********************************************"
            println "              Status Files Changed             "
            println "*********************************************"
            filesChangedMap.each { String componentPath, ResultTracker resultTracker ->
                println "${Paths.get(componentPath).getFileName()}${" - "}${resultTracker.state.value()}"
            }
            println "*********************************************"
        }
    }
}
