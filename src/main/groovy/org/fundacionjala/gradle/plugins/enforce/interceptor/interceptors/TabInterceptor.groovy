/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors

import org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor
import org.fundacionjala.gradle.plugins.enforce.interceptor.MetadataInterceptor
import org.fundacionjala.gradle.plugins.enforce.interceptor.commands.Tab
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import groovy.util.logging.Slf4j
/**
 * Implements methods to manage interceptors and load the tabs to truncate
 */
@Slf4j
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
        Tab tab = new Tab()
        tab.encoding = encoding
        addInterceptor(Interceptor.TRUNCATE_TABS.id, tab.execute)
    }
}
