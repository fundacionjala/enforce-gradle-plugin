/*
 * Copyright (c) Jalasoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.jalasoft.gradle.plugins.enforce.exceptions

/**
 * Are exceptions of credentials
 */
class CredentialException extends RuntimeException{

    /**
     *  Sets an credential exception message
     * @param message is type String
     */
    CredentialException(String message) {
        super(message)
    }
}

