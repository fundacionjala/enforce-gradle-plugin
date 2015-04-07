/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest.coverage
/**
 * Loads information about lines covered and lines not covered in collections
 */
class CoverageAnalyzer {
    ArrayList<Integer> linesNotCovered
    ArrayList<Integer> linesCovered
    Map<String, ArrayList<Integer>> linesCoveredMethods
    Map<String, ArrayList<Integer>> linesNotCoveredMethods
    Map rangedMapOfComponent
    CodeAnalyzer codeAnalyzer
    String path
    private
    final String REG_EXP_CLASS = ".*(public|protected|private)([ ])([A-Za-z0-9]{1,64}([ ])[A-Za-z0-9]{1,64}|[A-Za-z0-9]{1,64}.*)([ ])([A-Za-z0-9]{1,64})[(].*"
    private
    final String REG_EXP_TRIGGER = ".*(trigger)([ ])(([A-Za-z0-9]{1,64})|([A-Za-z0-9]{1,64})([\\w_])([\\w_])([A-Za-z0-9]{1,64}))([ ])(on)([ ])(([A-Za-z0-9]{1,64})|([A-Za-z0-9]{1,64})([\\w_])([\\w_])([A-Za-z0-9]{1,64}))([ ])([(]).*"
    private final String TYPE_CLASS = 'Class'
    private final String TYPE_TRIGGER = 'Trigger'
    private final int NAME_TRIGGER = 3
    private final int NAME_CLASS_METHOD = 6
    String fileText

    /**
     * Initializes collections to save lines covered and lines not covered
     * @param inputData is type CodeCoverageResult
     */
    public CoverageAnalyzer() {
        linesNotCovered = new ArrayList<Integer>()
        linesCovered = new ArrayList<Integer>()
        rangedMapOfComponent = new HashMap<String, ArrayList<Integer>>()
        linesCoveredMethods = [:]
        linesNotCoveredMethods = [:]
        codeAnalyzer = new CodeAnalyzer()
        fileText = ""
    }

    /**
     * Loads the method ranges of class
     */
    public void loadRangedMapOfClassMethods() {
        rangedMapOfComponent = codeAnalyzer.getRangeByFile(REG_EXP_CLASS, fileText, NAME_CLASS_METHOD)
    }

    /**
     * Loads triggers ranges
     */
    public void loadRangedMapOfTriggers() {
        rangedMapOfComponent = codeAnalyzer.getRangeByFile(REG_EXP_TRIGGER, fileText, NAME_TRIGGER)
    }

    /**
     * Loads a map with lines verifies with lines covered or lines not covered
     * @param linesVerified are lines verifies as lines covered or lines not covered
     * @param linesVerifyRangeMap is a map with ranges by method or by triggers
     */
    public Map<String, ArrayList<Integer>> loadLinesVerified(ArrayList<Integer> linesVerified) {

        Map<String, ArrayList<Integer>> mapResult = [:]
        ArrayList<Integer> arrayLines
        rangedMapOfComponent.each { methodName, rank ->
            arrayLines = new ArrayList<Integer>()
            int indexInit = rank.get(0)
            int indexEnd = rank.get(1)
            for (int line = indexInit; line < indexEnd; line++) {
                if (linesVerified.contains(line)) {
                    arrayLines.push(line)
                }
            }
            mapResult.put(methodName as String, arrayLines)
        }

        return mapResult
    }

    /**
     * Loads lines covered, lines not covered, lines covered by methods and lines not covered by methods
     * @param typeComponent of sales force : classes or triggers
     */
    public void loadDataCoverage(String typeComponent) {
        File file = new File(path)

        if(!file.exists()){
            return
        }

        fileText = file.getText()

        if (typeComponent == TYPE_CLASS) {
            loadRangedMapOfClassMethods()
        }
        if (typeComponent == TYPE_TRIGGER) {
            loadRangedMapOfTriggers()
        }
        linesCoveredMethods = loadLinesVerified(linesCovered)
        linesNotCoveredMethods = loadLinesVerified(linesNotCovered)
    }
}