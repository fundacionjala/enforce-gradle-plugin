/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest.coverage

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class CoverageAnalyzerTest extends Specification {

    @Shared
        def codeAnalyzer
    @Shared
        CoverageAnalyzer coverageAnalyzer
    @Shared
        def path = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala", "gradle",
                   "plugins","enforce", "unittest", "resources").toString()

    def setup() {
        coverageAnalyzer = Spy(CoverageAnalyzer)
        codeAnalyzer = Spy(CodeAnalyzer)
    }

    def "Test should set and get a path" () {
        given:
            def sourcePath = '"resources/class1.cls"'
        when:
            coverageAnalyzer.setPath(sourcePath)
        then:
            coverageAnalyzer.getPath() == sourcePath
    }

    def "Test should load range of methods of classes when the file exist"() {
        given:
            coverageAnalyzer.setPath(new File(Paths.get(path, "class1.cls").toString()).getAbsolutePath())
            Map mapClassMethods
        when:
            coverageAnalyzer.loadRangedMapOfClassMethods()
            mapClassMethods = ["main": [5, 8], "getValueA": [9, 16]]
            def mapClassMethodsResult = coverageAnalyzer.rangedMapOfComponent
        then:
            mapClassMethodsResult.Short == mapClassMethods.Short
    }

    def "Test LoadRangeMapOfClassMethod when the file is a empty"() {
        given:
            coverageAnalyzer.setPath(new File(Paths.get(path, "/emptyClass.cls").toString()).getAbsolutePath())
        when:
            coverageAnalyzer.loadRangedMapOfClassMethods()
            def mapClassMethodsResult = coverageAnalyzer.rangedMapOfComponent
        then:
            mapClassMethodsResult == new HashMap<String, ArrayList<Integer>>()
    }

    def "Test should load range of triggers when the file exist"() {
        given:
            coverageAnalyzer.setPath(new File(Paths.get(path, "trigger1.trigger").toString()).getAbsolutePath())
            Map mapClassMethods
        when:
            coverageAnalyzer.loadRangedMapOfTriggers()
            mapClassMethods = ["trigger1": [1, 4]]
            def mapClassMethodsResult = coverageAnalyzer.rangedMapOfComponent
        then:
           mapClassMethodsResult.Short == mapClassMethods.Short
    }

    def "Test should return a empty map if there isn't nothing in rangeMapOfComponent"() {
        when:
            coverageAnalyzer.loadLinesVerified([1,4])
        then:
           coverageAnalyzer.rangedMapOfComponent.isEmpty()
    }
}
