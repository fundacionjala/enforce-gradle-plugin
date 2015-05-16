/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager

import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

class CredentialUpdaterTest extends Specification {

    @Shared
    Project project

    @Shared
    CredentialUpdater credentialUpdater

    @Shared
    Credential credential

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        credentialUpdater = project.task('updateCredentialTest', type: CredentialUpdater)
    }

    def "Test should be instance of CredentialEncryptionExecutor"() {
        expect:
        credentialUpdater instanceof CredentialUpdater
    }
}
