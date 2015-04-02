/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

/**
 * Represents all web link types on Salesforce
 */
public enum LinkType {
    URL('url'),
    SCONTROL('sControl'),
    JAVASCRIPT('javascript'),
    PAGE('page'),
    FLOW('flow')

    private final String value

    public LinkType(String value) {
        this.value = value
    }

    public String value() {
        return value
    }
}
