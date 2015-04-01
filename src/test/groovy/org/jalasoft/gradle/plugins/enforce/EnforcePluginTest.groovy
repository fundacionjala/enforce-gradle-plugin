/*
 * Copyright (c) Jalasoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.jalasoft.gradle.plugins.enforce

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.jalasoft.gradle.plugins.enforce.EnforcePlugin
import org.jalasoft.gradle.plugins.enforce.EnforcePluginExtension
import org.jalasoft.gradle.plugins.enforce.wsc.Credential
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
