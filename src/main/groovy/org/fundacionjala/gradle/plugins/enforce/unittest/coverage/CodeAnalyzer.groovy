/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest.coverage

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 *  Validates the lines covered and not covered of component file of sales force
 */
class CodeAnalyzer {

    private final String REGEX_INVALID_LINE = "(else|[{]|[}]|try|if|while|[/]|(public|private)([ ])([A-Za-z0-9]{1,64}([(])))"

    /**
     * Validates that a line is content of method
     * @param lineFile
     * @return
     */
    public boolean validateLines(String lineFile) {
        String auxLine = lineFile.trim()
        boolean result = true
        Pattern pattern = Pattern.compile(REGEX_INVALID_LINE)
        Matcher mat = pattern.matcher(auxLine)
        if (!auxLine || mat.find()) {
            result = false
        }
        return result
    }

    /**
     * Validates lines covered
     * @param line is number of line
     * @param linesNotCovered is an array list that contain lines not covered
     * @return true if the line is covered
     */
    public boolean linesNotCovered(int line, ArrayList<Integer> linesNotCovered) {

        boolean result = true

        if (linesNotCovered.contains(line)) {
            result = false
        }
        return result
    }

    /**
     * Returns a Hash Map with ranges of components of sales force
     * @param regExp is a regular expression
     * @param content of a component of sales force
     * @param group is name of method
     * @return a Hash map with its ranges by method
     */
    public Map getRangeByFile(String regExp, String content, int group) {
        Map methods = [:]
        Pattern pattern = Pattern.compile(regExp)
        Matcher matcher
        int numberLine = 1
        String methodName
        int initIndex
        int endIndex
        String[] lines = content.split('\n')
        lines.each() { line ->
            matcher = pattern.matcher(line)
            if (matcher.find()) {
                methodName = matcher.group(group)
                initIndex = numberLine
                endIndex = getEndLine(lines, initIndex)
                methods.put(methodName, [initIndex, endIndex])
            }
            numberLine++
        }
        return methods
    }

    /**
     * Get the end line of the method
     * @param lines An lines array  of the method
     * @param initIndex the initial line of the method
     */
    private int getEndLine(def lines, int initIndex) {
        int index = initIndex - 1
        int endIndex
        def line
        int count = 0
        while (index < lines.size()) {
            line = lines[index]
            if (line.contains('{')) {
                count += countChar(line, '{')
            }
            if (line.contains('}')) {
                count -= countChar(line, '}')
                if (count == 0) {
                    break
                }
            }
            index++
        }
        endIndex = index + 1
        return endIndex
    }

    /**
     * Count the times that a char is in the string
     */
    private int countChar(String line, String ch) {
        int count = 0
        line.each { lineCh ->
            if (lineCh == ch) {
                count++
            }
        }
        return count
    }
}