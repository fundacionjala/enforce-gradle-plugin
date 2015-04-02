/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.exception

import org.gradle.api.GradleException

/**
 * Exception wrapper for deploy errors
 */
class GradleDeployException extends GradleException {

    /**
     * Constructor: sets values to message, throwable and array info deploy
     * @param message is information about problem
     * @param throwable is the exception object
     * @param infoDeployArrayList is a array contains objects wrapper of details failure
     */
    GradleDeployException(String message, Throwable throwable) {
        super(message, throwable)
    }
}
