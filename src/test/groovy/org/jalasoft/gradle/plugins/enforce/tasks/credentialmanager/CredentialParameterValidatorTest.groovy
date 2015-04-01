/*
 * Copyright (c) Jalasoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.jalasoft.gradle.plugins.enforce.tasks.credentialmanager

import org.gradle.api.Project
import spock.lang.Shared
import spock.lang.Specification

class CredentialParameterValidatorTest extends Specification{
    @Shared
    Project project
    @Shared
    CredentialParameterValidator credentialParameterValidator

    def setup() {
        credentialParameterValidator = Mock(CredentialParameterValidator)
        project = Mock(Project)
    }

    def "Test should be an instance of CredentialValidator" () {
        expect:
        credentialParameterValidator instanceof CredentialParameterValidator
    }
}
