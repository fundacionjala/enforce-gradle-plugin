/*
 * Copyright (c) Jalasoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.jalasoft.gradle.plugins.enforce.interceptor.interceptors

import org.jalasoft.gradle.plugins.enforce.interceptor.commands.Tab
import org.jalasoft.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.jalasoft.gradle.plugins.enforce.interceptor.MetadataInterceptor
import org.jalasoft.gradle.plugins.enforce.utils.ManagementFile

/**
 * Implements methods to manage interceptors and load the tabs to truncate
 */
class TabInterceptor extends MetadataInterceptor {

    /**
     * Loads the tab files to truncate
     */
    @Override
    void loadFiles(String sourcePath) {
        ManagementFile managementFile = new ManagementFile(sourcePath)
        files = managementFile.getFilesByFileExtension(MetadataComponents.TABS.extension)
    }

    /**
     * Loads interceptors by default
     */
    @Override
    void loadInterceptors() {
        addInterceptor(org.jalasoft.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_TABS.id, new Tab().execute)
    }
}
