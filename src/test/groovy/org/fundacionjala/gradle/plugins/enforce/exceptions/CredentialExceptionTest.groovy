/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.exceptions

import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialFileManager
import spock.lang.Shared
import spock.lang.Specification

class CredentialExceptionTest extends Specification {

    @Shared
    CredentialFileManager credentialFileManager

    def setup() {
        def pathCredentials = 'home/user/credentials.dat'
        def pathSecretKey = 'home/user/secretKey.dat'
        credentialFileManager = new CredentialFileManager(pathCredentials, pathSecretKey)
    }

    def "Test should return an CredentialException exception" () {
        when:
            credentialFileManager.saveCredential(null)
        then:
            thrown(CredentialException)
    }
}
