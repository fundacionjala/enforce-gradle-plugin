/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce

import org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager.CredentialAdder
import org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager.CredentialGiver
import org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager.CredentialUpdater
import org.fundacionjala.gradle.plugins.enforce.tasks.filemonitor.FilesStatus
import org.fundacionjala.gradle.plugins.enforce.tasks.filemonitor.Reset
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment.Deploy
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment.Truncate
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment.Undeploy
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment.Update
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment.Upload
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment.Delete
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment.Validate
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.execute.ApexExecutor
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.managepackage.InstallPackageTask
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.managepackage.UninstallPackageTask
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.retrieve.Retrieve
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.unittest.RunTestTask
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * This class creates a Force Plugin that represents an extension to Gradle.
 */
class EnforcePlugin implements Plugin<Project> {

    private static final String FORCE_EXTENSION_NAME = "enforce"
    private static final String CREDENTIAL_EXTENSION_NAME = "credential"

    /**
     * Apply this plugin to the project.
     * @param project The project object.
     */
    def void apply(Project project) {

        project.extensions.create(FORCE_EXTENSION_NAME, EnforcePluginExtension)

        project.extensions.create(CREDENTIAL_EXTENSION_NAME, Credential)

        project.task('status', type: FilesStatus)
        project.task('reset', type: Reset)

        project.task("runTest", type: RunTestTask)
        project.task("execute", type: ApexExecutor)
        project.task('deploy', type: Deploy)
        project.task('undeploy', type: Undeploy)
        project.task('update', type: Update)
        project.task('upload', type: Upload)
        project.task('delete', type: Delete)
        project.task('truncate', type: Truncate)
        project.task('validate', type: Validate)

        project.task("addCredential", type: CredentialAdder)
        project.task("updateCredential", type: CredentialUpdater)
        project.task("showCredentials", type: CredentialGiver)

        project.task("retrieve", type: Retrieve)

        project.task("installPackage", type: InstallPackageTask)
        project.task("uninstallPackage", type: UninstallPackageTask)
    }
}


