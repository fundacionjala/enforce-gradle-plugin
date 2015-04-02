/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce;

/**
 * This class represents an org type on salesforce
 */
public enum OrganizationType {
    DEV('dev'),
    PROD('prod')

    private final String value

    OrganizationType(String value) {
        this.value = value
    }

    public String value() {
        return value
    }
}
