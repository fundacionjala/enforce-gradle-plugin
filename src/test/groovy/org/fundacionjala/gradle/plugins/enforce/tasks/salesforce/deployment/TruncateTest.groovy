package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
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

    @Shared
    def RESOURCES_PATH = "src/test/groovy/org/fundacionjala/gradle/plugins/enforce/tasks/salesforce/deployment/resources"

    @Shared
    def SRC_PATH = Paths.get(System.getProperty("user.dir"), "$RESOURCES_PATH/src_truncate").toString()

    @Shared
    def BUILD_PATH = Paths.get(System.getProperty("user.dir"), "$RESOURCES_PATH/build_truncate").toString()

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        project.enforce.srcPath = SRC_PATH
        truncateInstance = project.tasks.truncate
        truncateInstance.fileManager = new ManagementFile(SRC_PATH)
        truncateInstance.project.enforce.deleteTemporaryFiles = false
        truncateInstance.buildFolderPath = BUILD_PATH
        truncateInstance.projectPath = SRC_PATH
        truncateInstance.projectPackagePath = Paths.get(SRC_PATH, 'package.xml').toString()
        truncateInstance.taskFolderName = "truncate"
        truncateInstance.parameters = [:]
    }

    def "Should setup resources"() {
        expect:
        truncateInstance.setup()
        truncateInstance.createDeploymentDirectory(truncateInstance.taskFolderPath)
        new File(BUILD_PATH).exists()
    }

    def "Should get 3 objects, 3 classes, 3 xml files and it should exclude Object1__c.object"() {
        given:
            truncateInstance.files = 'objects,classes'
            truncateInstance.excludes = 'objects/Object1__c.object'
            def expectedResult = []
        when:
            truncateInstance.setup()
            truncateInstance.loadClassifiedFiles(truncateInstance.files, truncateInstance.excludes)
            truncateInstance.loadFilesToTruncate()
        then:
            truncateInstance.filesToTruncate.size() == 11
            truncateInstance.filesToTruncate.each { file ->
                expectedResult += [file.name]
            }
            expectedResult.sort() == ['Class1.cls', 'Class1.cls-meta.xml', 'Class2.cls', 'Class2.cls-meta.xml',
                                      'Class3.cls', 'Class3.cls-meta.xml', 'TestClass.cls', 'TestClass.cls-meta.xml',
                                      'Account.object', 'CustomSetting1__c.object', 'Object2__c.object'].sort()
    }

    def "Should copy objects, classes, components, pages and triggers and it should exclude Account.object to build file"() {
        given:
            truncateInstance.excludes = 'objects/Account.object'
            def componentsToCopy = ['objects', 'classes', 'components', 'triggers', 'pages']
            def componentsFiles = ['objects': 3, 'classes': 8, 'components': 2, 'triggers': 4, 'pages': 6]
        when:
            truncateInstance.setup()
            truncateInstance.createDeploymentDirectory(truncateInstance.taskFolderPath)
            truncateInstance.loadClassifiedFiles(truncateInstance.files, truncateInstance.excludes)
            truncateInstance.loadFilesToTruncate()
            truncateInstance.copyFilesToTaskDirectory(truncateInstance.filesToTruncate)

        then:
            new File(truncateInstance.taskFolderPath as String).eachDir { directory ->
                assert componentsToCopy.contains(directory.name)
                assert componentsFiles[directory.name] == directory.listFiles().size()
            }
    }

    def "Should build package file"() {
        given:
            truncateInstance.excludes = 'objects/Account.object'
        when:
            truncateInstance.setup()
            truncateInstance.createDeploymentDirectory(truncateInstance.taskFolderPath)
            truncateInstance.loadClassifiedFiles(truncateInstance.files, truncateInstance.excludes)
            truncateInstance.loadFilesToTruncate()
            truncateInstance.copyFilesToTaskDirectory(truncateInstance.filesToTruncate)
            truncateInstance.writePackage(truncateInstance.taskPackagePath, truncateInstance.filesToTruncate)
            def packageFile = new File(truncateInstance.taskPackagePath as String)
            def packageContent = new XmlSlurper().parseText(packageFile.text)
        then:
            packageFile.exists()
            packageContent.types.size() == 5
    }

    def cleanup() {
        new File(Paths.get(BUILD_PATH).toString()).deleteDir()
    }
}
