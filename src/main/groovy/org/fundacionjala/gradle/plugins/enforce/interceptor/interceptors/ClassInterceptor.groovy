/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors

import org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor
import org.fundacionjala.gradle.plugins.enforce.interceptor.MetadataInterceptor
import org.fundacionjala.gradle.plugins.enforce.interceptor.commands.Class
import org.fundacionjala.gradle.plugins.enforce.interceptor.commands.ClassAnnotation
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

/**
 * Implements methods to manage interceptors and load the classes to truncate
 */
class ClassInterceptor extends MetadataInterceptor {
    private final String DEPRECATE_ANNOTATION = '@deprecated'

    /**
     * Loads the class files to truncate
     */
    @Override
    void loadFiles(String sourcePath) {
        ManagementFile managementFile = new ManagementFile(sourcePath)
        files = managementFile.getFilesByFileExtension(MetadataComponents.CLASSES.extension)
    }

    /**
     * Loads interceptors by default
     */
    @Override
    void loadInterceptors() {
        ClassAnnotation annotationCmd = new ClassAnnotation()
        annotationCmd.annotation = DEPRECATE_ANNOTATION
        addInterceptor(Interceptor.REMOVE_DEPRECATE.id, annotationCmd.execute)
        Class contentCmd = new Class()
        addInterceptor(Interceptor.TRUNCATE_CLASSES.id, contentCmd.execute)
    }
}
