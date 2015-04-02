/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

/**
 * Implements the truncated algorithm to truncate the component content
 */
class Component {
    public static final String EMPTY_COMPONENT = '<apex:component></apex:component>'

    /**
     * A closure to truncate the component content
     */
    Closure execute = { file ->
        file.text = EMPTY_COMPONENT
    }
}
