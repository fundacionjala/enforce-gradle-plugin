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
import groovy.util.logging.Slf4j
/**
 * Implements methods to manage interceptors and load the classes to truncate
 */
@Slf4j
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
        ClassAnnotation classAnnotation = new ClassAnnotation()
        classAnnotation.encoding = encoding
        classAnnotation.annotation = DEPRECATE_ANNOTATION
        addInterceptor(Interceptor.REMOVE_DEPRECATE.id, classAnnotation.execute)
        Class classCmd = new Class()
        classCmd.encoding = encoding
        addInterceptor(Interceptor.TRUNCATE_CLASSES.id, classCmd.execute)
    }
}
