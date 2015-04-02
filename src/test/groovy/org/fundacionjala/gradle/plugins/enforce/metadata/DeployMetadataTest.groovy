/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.metadata

import com.sforce.soap.metadata.DeployResult
import spock.lang.Shared
import spock.lang.Specification

class DeployMetadataTest extends Specification {

    @Shared
    DeployResult deployResult

    @Shared
    DeployMetadata deployMetadata

    def setup() {
        deployMetadata = Spy(DeployMetadata)
        deployResult = new DeployResult()
    }

    def "Shouldn't throw an exception when is succeeded"() {
        given:
            deployResult.success = true
        when:
            deployMetadata.checkStatusDeploy(deployResult)
        then:
            noExceptionThrown()
    }
}
