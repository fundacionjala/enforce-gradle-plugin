/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager

import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialFileManager
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class CredentialGiverTest extends Specification {
    @Shared
    String path = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala", "gradle",
            "plugins", "enforce", "tasks", "credentialmanager", "resources").toString()

    @Shared
    String pathCredentials = Paths.get(path, "credentials.dat").toString()

    @Shared
    String pathEmptyCredentials = Paths.get(path, "emptyCredentials.dat").toString()

    @Shared
    String pathSecretKeyGenerated = Paths.get(path, "secretKeyGenerated.text").toString()

    @Shared
    CredentialFileManager credentialFileManager

    @Shared
    Project project

    @Shared
    CredentialGiver credentialGiver

    @Shared
    CredentialValidator credentialValidator

    @Shared
    CredentialManagerTask credentialManagerTask

    def setup() {
        credentialFileManager = new CredentialFileManager(pathCredentials, pathSecretKeyGenerated)
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        credentialGiver = project.task('credentialGiverTest', type: CredentialGiver)
        credentialValidator = Mock(CredentialValidator)
        credentialValidator.validateCredential(_) >> { Credential credential ->
            if (credential.id == "invalid" || credential.id == "invalidencrypted") {
                throw new Exception("invalid user")
            }
        }
        credentialGiver.setCredentialValidator(credentialValidator)
        credentialManagerTask = credentialGiver
    }

    def "Test should be instance of CredentialGiver"() {
        expect:
        credentialGiver instanceof CredentialGiver
    }

    def "Test should by return a map that contains valid credentials when use 'isValid' Parameter"() {
        given:
        credentialGiver.credentialFileManager = this.credentialFileManager

        when:
        Map<Credential, String> resultCredential = credentialGiver.filterCredentials("isValid")

        then:
        resultCredential.size() == 3
    }

    def "Test should by return a map that contains invalid credentials when use 'isValid' Parameter"() {
        given:
        credentialGiver.credentialFileManager = this.credentialFileManager

        when:
        Map<Credential, String> resultCredential = credentialGiver.filterCredentials("isInvalid")

        then:
        resultCredential.size() == 2
    }

    def "Test should by return a map that contains invalid credentials when don't use Parameter"() {
        given:
        credentialGiver.credentialFileManager = this.credentialFileManager

        when:
        Map<Credential, String> resultCredential = credentialGiver.filterCredentials("allStatus")

        then:
        resultCredential.size() == 5
    }

    def "Test should by the return of an empty map when no credentials"() {
        given:
        credentialFileManager = new CredentialFileManager(pathEmptyCredentials, pathSecretKeyGenerated)
        credentialGiver.credentialFileManager = this.credentialFileManager

        when:
        Map<Credential, String> resultCredential = credentialGiver.filterCredentials("allStatus")

        then:
        resultCredential.size() == 0
    }
}