/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest.coverage

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class CoverageElementTest extends Specification {
    @Shared
    def path = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala", "gradle",
            "plugins","enforce", "unittest", "resources").toString()
    @Shared
    def coverageAnalyzer

    @Shared
    def codeAnalyzer

    @Shared
    CoverageElement coverageElement

    @Shared
    File fileClass1

    def setup() {
        coverageElement = Spy(CoverageElement, constructorArgs: ["class1", [1, 2], [3, 4]])

        coverageAnalyzer = Spy(CoverageAnalyzer)
        codeAnalyzer = Spy(CodeAnalyzer)

        fileClass1 = new File(Paths.get(path, "class1.cls").toString())
        coverageElement.path = fileClass1.path
    }

    def "Test get file name"() {
        when:
        def fileNameResult = coverageElement.getFileName('cls')
        then:
        fileNameResult == "class1.cls"
    }

    def "Test load data coverage"() {
        when:
            coverageElement.loadDataCoverage("Class")
        then:
            coverageElement.coverageAnalyzer.path == fileClass1.path
            coverageElement.coverageAnalyzer.linesCovered == [1,2]
            coverageElement.coverageAnalyzer.linesNotCovered == [3,4]
    }

    def "Test get line rate"() {
        given:
        def rate = 0.5
        when:
        def rateResult = coverageElement.getLineRate()
        then:
        rate == rateResult
    }
}
