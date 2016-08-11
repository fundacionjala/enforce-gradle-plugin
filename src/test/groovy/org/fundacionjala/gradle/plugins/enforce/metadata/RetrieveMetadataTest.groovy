/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.metadata

import com.sforce.soap.metadata.RetrieveMessage
import com.sforce.soap.metadata.RetrieveResult
import com.sforce.soap.metadata.RetrieveStatus
import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager.Package
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

class RetrieveMetadataTest extends Specification {

    @Shared
    def retrieveResult = new RetrieveResult()

    @Shared
    def retrieveMetadata = new RetrieveMetadata(new Package())

    @Shared
    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
    }

    def "Test should return exception because status is failed"() {
        when:
            retrieveResult.setStatus(RetrieveStatus.Failed)
            retrieveMetadata.checkStatusSucceeded(retrieveResult)
        then:
            thrown(Exception)
    }

    def "Test should return exception because status is in Progress"() {
        when:
            retrieveResult.setStatus(RetrieveStatus.InProgress)
            retrieveMetadata.checkStatusSucceeded(retrieveResult)
        then:
            thrown(Exception)
    }

    def "Test should return exception because status is Pending"() {
        when:
            retrieveResult.setStatus(RetrieveStatus.Pending)
            retrieveMetadata.checkStatusSucceeded(retrieveResult)
        then:
            thrown(Exception)
    }

    def "Test should not return exception because status is succeeded"() {
        when:
            retrieveResult.setStatus(RetrieveStatus.Succeeded)
            retrieveMetadata.checkStatusSucceeded(retrieveResult)
        then:
            noExceptionThrown()
    }

    def "Test should throw exception because retrieveResult is null"() {
        when:
            retrieveResult = null
            retrieveMetadata.checkStatusSucceeded(retrieveResult)
        then:
            thrown(Exception)
    }

    def "Test should push warnings message because there is at least one"() {
        given:
            RetrieveMessage[] warningMessage = new RetrieveMessage()
            warningMessage[0].setFileName("SomeFile")
            warningMessage[0].setProblem("file Class1.cls in ApexClass has not been found")
        when:
            retrieveMetadata.loadWarningsMessages (warningMessage)
        then:
            !retrieveMetadata.getWarningsMessages().empty
    }
}
