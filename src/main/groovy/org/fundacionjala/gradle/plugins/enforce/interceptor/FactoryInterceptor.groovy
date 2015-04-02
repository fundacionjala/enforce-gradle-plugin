/*
 * Copyright (c) Fundación Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor

import com.jalasoft.sfdc.devtool.interceptor.interceptors.*
import org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors.ObjectInterceptor
import org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors.WorkflowInterceptor
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors.ClassInterceptor
import org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors.ComponentInterceptor
import org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors.PageInterceptor
import org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors.TabInterceptor
import org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors.TriggerInterceptor

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
        }
        return interceptor
    }
}
