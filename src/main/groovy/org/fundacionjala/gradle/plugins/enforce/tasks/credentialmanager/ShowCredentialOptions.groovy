/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager

enum ShowCredentialOptions {
    STATUS("status"),
    VALID_STATUS("valid"),
    INVALID_STATUS("invalid"),
    ALL_STATUS("allStatus"),
    LOCATION("location")

    ShowCredentialOptions(String value) {
        this.value = value
    }

    private final String value

    public String value() {
        return value
    }
}
