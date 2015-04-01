/*
 * Copyright (c) Jalasoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.jalasoft.gradle.plugins.enforce.interceptor.interceptors

import org.jalasoft.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.jalasoft.gradle.plugins.enforce.interceptor.MetadataInterceptor
import org.jalasoft.gradle.plugins.enforce.interceptor.commands.Workflow
import org.jalasoft.gradle.plugins.enforce.utils.ManagementFile

/**
 * Implements methods to manage interceptors and load the workflows to truncate
 */
class WorkflowInterceptor extends MetadataInterceptor {

    /**
     * Loads the workflow files to truncate
     */
    @Override
    void loadFiles(String sourcePath) {
        ManagementFile managementFile = new ManagementFile(sourcePath)
        files = managementFile.getFilesByFileExtension(MetadataComponents.WORKFLOWS.extension)
    }

    /**
     * Loads interceptors by default
     */
    @Override
    void loadInterceptors() {
        addInterceptor(org.jalasoft.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_WORKFLOWS.id, new Workflow().execute)
    }
}
