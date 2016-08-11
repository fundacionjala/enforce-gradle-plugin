/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors

import org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor
import org.fundacionjala.gradle.plugins.enforce.interceptor.MetadataInterceptor
import org.fundacionjala.gradle.plugins.enforce.interceptor.commands.Trigger
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import groovy.util.logging.Slf4j
/**
 * Implements methods to manage interceptors and load the triggers to truncate
 */
@Slf4j
class TriggerInterceptor extends MetadataInterceptor {

    /**
     * Loads the trigger files to truncate
     */
    @Override
    void loadFiles(String sourcePath) {
        ManagementFile managementFile = new ManagementFile(sourcePath)
        files = managementFile.getFilesByFileExtension(MetadataComponents.TRIGGERS.extension)
    }

    /**
     * Loads interceptors by default
     */
    @Override
    void loadInterceptors() {
        Trigger trigger = new Trigger()
        trigger.encoding = encoding
        addInterceptor(Interceptor.TRUNCATE_TRIGGERS.id, trigger.execute)
    }
}
