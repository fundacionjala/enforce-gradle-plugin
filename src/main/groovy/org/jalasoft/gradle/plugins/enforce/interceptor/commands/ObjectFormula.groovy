/*
 * Copyright (c) Jalasoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.jalasoft.gradle.plugins.enforce.interceptor.commands

import java.util.regex.Matcher

/**
 * Implements the truncated algorithm to replace a formula by default for all formulas
 * that matches with the regex in an object file
 */
class ObjectFormula {
    private final String FIELDS_REGEX = /<fields>.*([^\n]*?\n+?)*?.*<\/fields>/
    private final String FORMULA_REGEX = /<formula>(([^\n]*?\n+?)*?.*)<\/formula>/
    private final String FORMULA_TYPE_REGEX = /<type>(([^\n]*?\n+?)*?.*)<\/type>/
    private final int CONTENT_MATCHED_INDEX = 0
    private final int TYPE_INDEX = 1
    private final String NUMBER_BY_DEFAULT = '0'
    private final String DATE_BY_DEFAULT = 'NOW()'
    private final String CHECKBOK_BY_DEFAULT = 'true'
    private final String TAG_FORMULA = '<formula>%s</formula>'

    /**
     * A closure to replace a formula by default for all formulas that matches with the regex in an object file
     */
    Closure execute = { file ->
        if (!file) return
        Matcher fieldMatcher = file.text =~ FIELDS_REGEX
        fieldMatcher.each { fieldIt->
            String field = fieldIt[CONTENT_MATCHED_INDEX]
            String formula
            String type
            Matcher formulaMatcher = field =~ FORMULA_REGEX
            formulaMatcher.each { formulaIt->
                formula = formulaIt[CONTENT_MATCHED_INDEX]
            }
            Matcher typeMatcher = field =~ FORMULA_TYPE_REGEX
            typeMatcher.each { typeIt->
                type = typeIt[TYPE_INDEX]
            }
            if (formula) {
                type = type.trim()
                String target = field
                String replacement
                switch (type) {
                    case FormulaType.PERCENT.value():
                        replacement = field.replace(formula, String.format(TAG_FORMULA, NUMBER_BY_DEFAULT))
                        file.text = file.text.replace(target, replacement)
                        break
                    case FormulaType.NUMBER.value():
                        replacement = field.replace(formula, String.format(TAG_FORMULA, NUMBER_BY_DEFAULT))
                        file.text = file.text.replace(target, replacement)
                        break
                    case FormulaType.TIME.value():
                        replacement = field.replace(formula, String.format(TAG_FORMULA, DATE_BY_DEFAULT))
                        file.text = file.text.replace(target, replacement)
                        break
                    case FormulaType.DATE.value():
                        replacement = field.replace(formula, String.format(TAG_FORMULA, DATE_BY_DEFAULT))
                        file.text = file.text.replace(target, replacement)
                        break
                    case FormulaType.CURRENCY.value():
                        replacement = field.replace(formula, String.format(TAG_FORMULA, NUMBER_BY_DEFAULT))
                        file.text = file.text.replace(target, replacement)
                        break
                    case FormulaType.CHECKBOK.value():
                        replacement = field.replace(formula, String.format(TAG_FORMULA, CHECKBOK_BY_DEFAULT))
                        file.text = file.text.replace(target, replacement)
                        break
                    case FormulaType.TEXT.value():
                        replacement = field.replace(formula, String.format(TAG_FORMULA, "\"\""))
                        file.text = file.text.replace(target, replacement)
                        break
                }
            }
        }
    }
}
