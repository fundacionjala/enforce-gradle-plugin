/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce

import java.nio.charset.StandardCharsets

/**
 * This class creates a plugin extension to assign properties of the plugin.
 */
class EnforcePluginExtension {

    Boolean saveMapCurrent = true
    /**
     * The source directory path to monitor all changes on it.
     */
    String srcPath = "."
    /**
     * The temporal directory path to copy all files changed.
     */
    String srcTempCopyFiles = ""
    /**
     * A map with file path as key and signature as value of a file
     */
    Map<String, String> mapFilesChanged = [:]

    Boolean deleteTemporaryFiles = true

    /**
     * A list with wildcards that customize that subComponents can be deleted
     */
    ArrayList<String> deleteSubComponents = ['*']

    ArrayList<String> standardObjects = []

    String tool = ""

    int poll = 200

    int waitTime = 10

    String integration = "no"

    String encoding = StandardCharsets.UTF_8.displayName()

    String foldersToDownload = "objects,staticresources,classes,pages,triggers,components"

    Map<String, Map<String, Closure>> interceptors

    EnforcePluginExtension() {
        interceptors = [:]
    }

    /**
     * Register a new global interceptor
     * @param metadataGroup the metadata group name
     * @param interceptorName the interceptor name
     * @param interceptorAction the new interceptor
     */
    void globalInterceptor(String metadataGroup, String interceptorName = '', Closure interceptorAction) {
        Map<String, Closure> interceptor = interceptors.get(metadataGroup)
        interceptor = interceptor ?: [:]
        interceptorName = interceptorName ?: interceptorAction.hashCode().toString()
        interceptor.put(interceptorName, interceptorAction)
        interceptors.put(metadataGroup, interceptor)
    }

}