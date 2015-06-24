package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.metadata.DeployMetadata
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class TruncateTest extends Specification {
    @Shared
    Project project

    @Shared
    def truncateInstance

    def pathPlugin =  "src/test/groovy/org/fundacionjala/gradle/plugins/enforce"

    def pathTruncateResources = "tasks/salesforce/deployment/resources/src_truncate"

    @Shared
    def SRC_PATH = Paths.get(System.getProperty("user.dir"), "$pathPlugin/$pathTruncateResources").toString()

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        project.enforce.srcPath = SRC_PATH
        truncateInstance = project.tasks.truncate
        truncateInstance.fileManager = new ManagementFile(SRC_PATH)
        truncateInstance.project.enforce.deleteTemporaryFiles = false
        truncateInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build').toString())
        truncateInstance.createDeploymentDirectory(Paths.get(SRC_PATH, 'build', 'truncate').toString())
        truncateInstance.projectPath = SRC_PATH
        truncateInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
        truncateInstance.projectPath = SRC_PATH
        truncateInstance.componentDeploy = new DeployMetadata()
        truncateInstance.projectPackagePath = Paths.get(SRC_PATH, 'package.xml').toString()
    }
}
