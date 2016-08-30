/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors

import groovy.util.logging.Slf4j
import org.fundacionjala.gradle.plugins.enforce.interceptor.MetadataInterceptor
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile

@Slf4j
class BaseInterceptor extends MetadataInterceptor {

    def directory

    @Override
    void loadFiles(String sourcePath) {
        files = []
        if (directory){
            files = getFiles(sourcePath, directory)
        }
    }

    def getFiles(String sourcePath, String folder) {
        ManagementFile managementFile = new ManagementFile(sourcePath)
        files = managementFile.getFilesByFolders(sourcePath, [folder])
        return files
    }

    @Override
    void loadInterceptors() {
        interceptors = [:]
    }
}
