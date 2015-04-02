/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.credentialmanagement

enum CredentialParameter {
    USER_NAME("username"),
    PASSWORD("password"),
    TOKEN("token")

    CredentialParameter(String value) {
        this.value = value
    }

    private final String value

    public String value() {
        return value
    }
}
