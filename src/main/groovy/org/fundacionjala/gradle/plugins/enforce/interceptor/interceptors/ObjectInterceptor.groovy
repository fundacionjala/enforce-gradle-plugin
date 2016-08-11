/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors

import org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor
import org.fundacionjala.gradle.plugins.enforce.interceptor.MetadataInterceptor
import org.fundacionjala.gradle.plugins.enforce.interceptor.commands.*
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import groovy.util.logging.Slf4j
/**
 * Implements methods to manage interceptors and load the objects to truncate
 */
@Slf4j
class ObjectInterceptor extends MetadataInterceptor {

    /**
     * Loads the object files to truncate
     */
    @Override
    void loadFiles(String sourcePath) {
        ManagementFile managementFile = new ManagementFile(sourcePath)
        files = managementFile.getFilesByFileExtension(MetadataComponents.OBJECTS.extension)
    }

    /**
     * Loads interceptors by default
     */
    @Override
    void loadInterceptors() {
        ObjectFormula objectFormula = new ObjectFormula()
        objectFormula.encoding = encoding
        addInterceptor(Interceptor.TRUNCATE_FORMULAS.id, objectFormula.execute)
        ObjectWebLink objectWebLink = new ObjectWebLink()
        objectWebLink.encoding = encoding
        addInterceptor(Interceptor.TRUNCATE_WEB_LINKS.id, objectWebLink.execute)
        ObjectFieldSet objectFieldSet = new ObjectFieldSet()
        objectFieldSet.encoding = encoding
        addInterceptor(Interceptor.TRUNCATE_FIELD_SETS.id, objectFieldSet.execute)
        ObjectActionOverride objectActionOverride = new ObjectActionOverride()
        objectActionOverride.encoding = encoding
        addInterceptor(Interceptor.TRUNCATE_ACTION_OVERRIDES.id, objectActionOverride.execute)
        ObjectField objectField = new ObjectField()
        objectField.encoding = encoding
        addInterceptor(Interceptor.TRUNCATE_FIELD.id, objectField.execute)
    }
}
