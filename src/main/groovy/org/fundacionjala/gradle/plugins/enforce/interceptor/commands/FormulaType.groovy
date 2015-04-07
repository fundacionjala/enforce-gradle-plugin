/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

/**
 * Represents all formula types on Salesforce
 */
public enum FormulaType {
    TEXT('Text'),
    PERCENT('Percent'),
    NUMBER('Number'),
    DATE('Date'),
    TIME('DateTime'),
    CURRENCY('Currency'),
    CHECKBOK('Checkbox')

    private final String value

    public FormulaType(String value) {
        this.value = value
    }

    public String value() {
        return value
    }
}
