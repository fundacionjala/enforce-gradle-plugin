/*
 * Copyright (c) Jalasoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.jalasoft.gradle.plugins.enforce.wsc

/**
 * Represents a login type in a salesforce org
 * DEV as value: login
 * TEST as value: test
 */
enum LoginType {
    DEV("login"), TEST("test")

    LoginType(String value) {
        this.value = value
    }

    private final String value

    public String value() { return value }
}
