package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.metadata.DeployMetadata
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class ValidateTest extends Specification {
    @Shared
    Project project

    @Shared
    def instanceDeploy

    def deployMetadata = Mock(DeployMetadata)

    @Shared
    def SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
            "fundacionjala", "gradle", "plugins","enforce","tasks", "salesforce", "resources").toString()

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        project.enforce.srcPath = SRC_PATH
        instanceDeploy = project.tasks.validate
        instanceDeploy.fileManager = new ManagementFile(SRC_PATH)
        instanceDeploy.project.enforce.deleteTemporaryFiles = false
        instanceDeploy.createDeploymentDirectory(Paths.get(SRC_PATH, 'build').toString())
        instanceDeploy.createDeploymentDirectory(Paths.get(SRC_PATH, 'build', 'deploy').toString())
        instanceDeploy.createDeploymentDirectory(Paths.get(SRC_PATH, 'build', 'deploy', 'folderOne').toString())
        instanceDeploy.componentDeploy = deployMetadata
    }

    def "should perform validation"() {
        given:
        instanceDeploy.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
        instanceDeploy.projectPath = Paths.get(SRC_PATH, 'src').toString()
        instanceDeploy.project.enforce.deleteTemporaryFiles = true
        instanceDeploy.projectPackagePath = Paths.get(SRC_PATH, 'src', 'package.xml').toString()
        String folderDeploy = Paths.get(SRC_PATH, 'build', 'deploy').toString()

        and:
        instanceDeploy.setup()
        instanceDeploy.createDeploymentDirectory(folderDeploy)
        instanceDeploy.displayFolderNoDeploy()
        instanceDeploy.deployAllComponents()
        instanceDeploy.deleteTemporaryFiles()

        when:
            instanceDeploy.runTask()
        then:
            2 * deployMetadata.deploy(_, _, _, _, true)
    }
}
