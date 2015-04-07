/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest.coverage

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class CodeAnalyzerTest extends Specification{
    @Shared
        CodeAnalyzer codeAnalyzer
    @Shared
        def path = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala", "gradle",
                   "plugins","enforce", "unittest", "resources").toString()

    def setup() {
        codeAnalyzer = new CodeAnalyzer()
    }

    def "Test should instance of CodeAnalyzer" () {
        expect:
            codeAnalyzer instanceof CodeAnalyzer
    }

    def "Test should return a map with classes file ranges by method" () {
        given:
            String regExpClass = ".*(public|protected|private)([ ])([A-Za-z0-9]{1,64}([ ])[A-Za-z0-9]{1,64}|[A-Za-z0-9]{1,64}.*)([ ])([A-Za-z0-9]{1,64})[(].*"
            String content = new File(Paths.get(path, "class1.cls").toString()).text
            int group = 6
        when:
            def result = codeAnalyzer.getRangeByFile(regExpClass, content, group)
        then:
            result == ['main':[5, 7], 'getValueA':[9, 16]]
    }

    def "Test should return a map with trigger file ranges" () {
        given:
            String regExpTrigger = ".*(trigger)([ ])([A-Za-z0-9]{1,64})([ ])(on)([ ])([A-Za-z0-9]{1,64})([\\w_])([\\w_])([A-Za-z0-9]{1,64})([ ])([(]).*"
            String content = new File(Paths.get(path, "trigger1.trigger").toString()).text
            int group = 3
        when:
            def result = codeAnalyzer.getRangeByFile(regExpTrigger, content, group)
        then:
            result == ['trigger1':[1, 4]]
    }

    def "Test should return false if line is invalid" () {
        given:
            def lineFile = 'if()'
        when:
            def result = codeAnalyzer.validateLines(lineFile)
        then:
            !result
    }

    def "Test should return false if line is a comment" () {
        given:
            def lineFile = '//this is a comment'
        when:
            def result = codeAnalyzer.validateLines(lineFile)
        then:
            !result
    }

    def "Test should return false if line of clase" () {
        given:
            def lineFile = 'public Class1()'
        when:
            def result = codeAnalyzer.validateLines(lineFile)
        then:
         !result
    }

    def "Test should return true if line is valid" () {
        given:
            def lineFile = 'def sizeFile = 0'
        when:
            def result = codeAnalyzer.validateLines(lineFile)
        then:
            result
    }

    def "Test should return true if a line isn't covered" () {
        given:
            def lineFile = 1
            def linesNotCovered = new ArrayList<Integer>()
            linesNotCovered.add(2)
            linesNotCovered.add(3)
        when:
            def result = codeAnalyzer.linesNotCovered(lineFile,linesNotCovered )
        then:
            result
    }

    def "Test should return false if a line is covered" () {
        given:
            def lineFile = 2
            def linesNotCovered = new ArrayList<Integer>()
            linesNotCovered.add(2)
            linesNotCovered.add(3)
        when:
            def result = codeAnalyzer.linesNotCovered(lineFile,linesNotCovered )
        then:
            !result
    }

    def "Test validateLines"() {
        when:
            String tryLine = "try"
        then:
            false == codeAnalyzer.validateLines(tryLine)
            false == codeAnalyzer.validateLines("{")
            false == codeAnalyzer.validateLines("}")
            false == codeAnalyzer.validateLines("")
            true == codeAnalyzer.validateLines(" int a = b + 1")
    }
}
