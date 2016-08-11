/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.credentialmanagement

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class CredentialManagerFactoryTest extends Specification {
    @Shared
    def path = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala", "gradle",
            "plugins","enforce", "credentialmanagement", "resources").toString()

    @Shared
    def pathCredentials = Paths.get(path, "credentialSaved.dat").toString()

    @Shared
    def pathSecretKeyGenerated = Paths.get(path, "secretKeyGenerated.dat").toString()

    def "Test should return a instance of CredentialFileManagement class"() {
        given:
            def typeCredentialManagement = CredentialManagerType.FILE
        when:
            def instance = CredentialManagerFactory.getCredentialManagement(typeCredentialManagement, pathCredentials, pathSecretKeyGenerated)
        then:
            instance instanceof CredentialFileManager
    }

    def "Test should return null if there isn't  this type to credential manager"() {
        given:
            def typeCredentialManagement = CredentialManagerType.DEFAULT
        when:
            def instance = CredentialManagerFactory.getCredentialManagement(typeCredentialManagement, pathCredentials, pathSecretKeyGenerated)
        then:
            instance == null
    }

    def cleanupSpec() {
        new File(pathSecretKeyGenerated).delete()
    }
}
