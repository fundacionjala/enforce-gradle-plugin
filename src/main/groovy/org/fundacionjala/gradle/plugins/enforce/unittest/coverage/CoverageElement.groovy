/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest.coverage


/**
 * Process information about lines covered and lines not covered to get coverage report
 */
class CoverageElement {

    String path
    String name
    CoverageAnalyzer coverageAnalyzer
    ArrayList<Integer> coveredLines
    ArrayList<Integer> uncoveredLines

    /**
     * Sets some parameters and initialize coverage analyzer
     * @param name is the component name
     * @param coveredLines is a array covered lines
     * @param uncoveredLines is array uncovered lines
     */
    public CoverageElement(String name, ArrayList<Integer> coveredLines, ArrayList<Integer> uncoveredLines) {
        this.name = name
        this.coveredLines = coveredLines
        this.uncoveredLines = uncoveredLines
        coverageAnalyzer = new CoverageAnalyzer()
    }

    /**
     * Gets file name sales force component
     * @param extension is extension of components
     * @return file name component
     */
    public String getFileName(String extension) {
        return "${this.name}.${extension}"
    }

    /**
     * Loads information of coverage
     * @param type is type of component
     */
    public void loadDataCoverage(String type) {
        coverageAnalyzer.setPath(this.path)
        coverageAnalyzer.setLinesCovered(coveredLines)
        coverageAnalyzer.setLinesNotCovered(uncoveredLines)
        coverageAnalyzer.loadDataCoverage(type)
    }

    /**
     * Calculate line rate of CodeCoverageResult
     * @return lineRate
     */
    public double getLineRate() {
        double percent = 1
        double numLocation = coveredLines.size() + uncoveredLines.size()
        if (numLocation > 0) {
            percent -= ((double) uncoveredLines.size() / numLocation)
        }
        return percent
    }
}
