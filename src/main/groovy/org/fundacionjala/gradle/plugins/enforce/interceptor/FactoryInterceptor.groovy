/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor

import org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors.*
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

/**
 * Creates a new salesforce component interceptor
 */
class FactoryInterceptor {

    /**
     * Gets a new salesforce component interceptor
     * @param componentType the component type to build
     */
    public MetadataInterceptor getInterceptor(MetadataComponents componentType) {
        MetadataInterceptor interceptor
        switch (componentType) {
            case MetadataComponents.CLASSES:
                interceptor = new ClassInterceptor()
                break
            case MetadataComponents.OBJECTS:
                interceptor = new ObjectInterceptor()
                break
            case MetadataComponents.PAGES:
                interceptor = new PageInterceptor()
                break
            case MetadataComponents.TABS:
                interceptor = new TabInterceptor()
                break
            case MetadataComponents.TRIGGERS:
                interceptor = new TriggerInterceptor()
                break
            case MetadataComponents.WORKFLOWS:
                interceptor = new WorkflowInterceptor()
                break
            case MetadataComponents.COMPONENTS:
                interceptor = new ComponentInterceptor()
                break
            default:
                interceptor = new BaseInterceptor()
                break
        }
        return interceptor
    }
}
