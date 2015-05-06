/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce

import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class EnforcePluginTest extends Specification {

    def 'force extensions exist'() {
        setup:
        Project project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)

        expect:
        project.extensions.getByName('enforce') instanceof EnforcePluginExtension
    }

    def 'credential extensions exist'() {
        setup:
        Project project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)

        expect:
        project.extensions.getByName('credential') instanceof Credential
    }
}
