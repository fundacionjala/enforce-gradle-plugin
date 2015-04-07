/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc

/**
 * This class is a representation of a user account with the login type
 */
class Credential {
    String id
    String username
    String password
    String token
    String loginFormat
    String type

    String getPasswordToken() {
        return "${password}${token}"
    }
}
