/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor;

/**
 * Represents all interceptor types for truncated process
 */
public enum Interceptor {
    REMOVE_DEPRECATE("removeDeprecateAnnotation"),
    TRUNCATE_CLASSES("truncateClasses"),
    TRUNCATE_FIELD_SETS("truncateFieldSets"),
    TRUNCATE_FIELD("truncateField"),
    TRUNCATE_ACTION_OVERRIDES("truncateActionOverrides"),
    TRUNCATE_FORMULAS("truncateFormulas"),
    TRUNCATE_WEB_LINKS("truncateWebLinks"),
    TRUNCATE_PAGES("truncatePages"),
    TRUNCATE_TABS("truncateTabs"),
    TRUNCATE_TRIGGERS("truncateTriggers"),
    TRUNCATE_WORKFLOWS("truncateWorkflows"),
    TRUNCATE_COMPONENTS("truncateComponents")

    public final static Map<String, String> INTERCEPTORS

    static {
        INTERCEPTORS = new HashMap<String, String>()
        for (Interceptor interceptor: values()){
            INTERCEPTORS.put(interceptor.name(), interceptor.id)
        }
    }

    private final String id

    public String getId() {
        return id;
    }

    Interceptor(String id) {
        this.id = id
    }
}
