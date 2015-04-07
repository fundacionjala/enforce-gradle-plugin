/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.filemonitor

import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification


class FilesStatusTest extends Specification {

    @Shared
    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
    }

    def 'Test should show files changed'() {
        given:
            project.tasks.status.filesChangedMap = ["two.txt":"New file"]
        when:
            def stdOut = System.out
            def os = new ByteArrayOutputStream()
            System.out = new PrintStream(os)

            project.tasks.status.displayFileChanged()
            def array = os.toByteArray()
            def is = new ByteArrayInputStream(array)
            System.out = stdOut
            def lineAux = is.readLines()
        then:
            lineAux[0] == "*********************************************"
            lineAux[1] == "              Status Files Changed             "
            lineAux[2] == "*********************************************"
            lineAux[3] == "two.txt - New file"
            lineAux[4] == "*********************************************"
    }

    def 'Test should show nothing'() {
        given:
            project.tasks.status.filesChangedMap = [:]
        when:
            def stdOut = System.out
            def os = new ByteArrayOutputStream()
            System.out = new PrintStream(os)

            project.tasks.status.displayFileChanged()
            def array = os.toByteArray()
            def is = new ByteArrayInputStream(array)
            System.out = stdOut
            def lineAux = is.readLines()
        then:
            lineAux == []
    }
}
