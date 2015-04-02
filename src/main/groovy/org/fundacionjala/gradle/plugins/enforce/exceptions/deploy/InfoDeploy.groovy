/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.exceptions.deploy

/**
 * Information wrapper for deploy errors
 */
class InfoDeploy {
    String fileName
    String problem
    int line
    int column
}
