/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.exceptions

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

