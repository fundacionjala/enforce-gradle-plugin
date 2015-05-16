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

import java.nio.file.Paths

class CredentialAdderTest extends Specification {
    @Shared
    Project project

    @Shared
    CredentialAdder credentialAdder

    @Shared
    Credential credential

    @Shared
    def path = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "com", "fundacionjala", "gradle",
            "plugins", "tasks", "credentialmanager", "resources").toString()

    @Shared
    def pathCredentials = Paths.get(path, "credentials.dat").toString()

    @Shared
    def pathSecretKeyGenerated = Paths.get(path, "secretkeyGenerated.text").toString()

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        credentialAdder = project.task('addCredentialTest', type: CredentialAdder)

    }

    def "Test should be instance of CredentialEncryptionExecutor"() {
        expect:
        credentialAdder instanceof CredentialAdder
    }
}
