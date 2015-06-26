/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentStates
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ResultTracker
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageBuilder
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class UploadTest extends Specification {
    @Shared
        def uploadInstance

    @Shared
        Project project

    @Shared
        def SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
            "fundacionjala", "gradle", "plugins", "enforce", "tasks", "salesforce", "resources").toString()

    @Shared
        String pathUpload = Paths.get(SRC_PATH, 'build', Constants.DIR_UPLOAD_FOLDER)

    @Shared
        Map<String, ArrayList<String>> packagedExpected

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        project.enforce.srcPath = SRC_PATH
        uploadInstance = project.tasks.upload
        uploadInstance.project.enforce.deleteTemporaryFiles = false
        uploadInstance.fileManager = new ManagementFile(SRC_PATH)
        packagedExpected = [:]
    }

    def "Test shouldn't create a package xml file if specificFilesToUpload Array is empty "() {
        given:
            uploadInstance.pathUpload = Paths.get(SRC_PATH, 'build').toString()
            uploadInstance.specificFilesToUpload = []
        when:
            uploadInstance.createPackage()
        then:
            !new File(Paths.get(SRC_PATH, 'build', 'package.xml').toString()).exists()
    }

    def "Test should create a package xml file"() {
        given:
            uploadInstance.createDeploymentDirectory(pathUpload)
            uploadInstance.pathUpload = Paths.get(SRC_PATH, 'build').toString()
            uploadInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            uploadInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', 'package.xml').toString()
            uploadInstance.uploadPackagePath = Paths.get(SRC_PATH, 'build', 'package.xml').toString()
            uploadInstance.specificFilesToUpload = [new File(Paths.get(SRC_PATH, "src", "classes", "Class1.cls").toString()),
                                                    new File(Paths.get(SRC_PATH, "src", "classes", "Class1.cls-meta.xml").toString()),
                                                    new File(Paths.get(SRC_PATH, "src", "objects", "Object1__c.object").toString())]
        when:
            uploadInstance.createPackage()
        then:
            new File(Paths.get(SRC_PATH, 'build', 'package.xml').toString()).exists()
    }

    def "Test should copy files to upload"() {
        given:
            uploadInstance.createDeploymentDirectory(pathUpload)
            uploadInstance.pathUpload = Paths.get(SRC_PATH, 'build').toString()
            uploadInstance.specificFilesToUpload = [new File(Paths.get(SRC_PATH, "src", "classes", "Class1.cls").toString()),
                                                    new File(Paths.get(SRC_PATH, "src", "classes", "Class1.cls-meta.xml").toString()),
                                                    new File(Paths.get(SRC_PATH, "src", "objects", "Object1__c.object").toString())]
            uploadInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
        when:
            uploadInstance.copyFilesToUpload()
        then:
            new File(Paths.get(SRC_PATH, 'build', "classes", "Class1.cls").toString()).exists()
            new File(Paths.get(SRC_PATH, 'build', "classes", "Class1.cls-meta.xml").toString()).exists()
            new File(Paths.get(SRC_PATH, 'build', "objects", "Object1__c.object").toString()).exists()
    }

    def "Test should return a map with changed files"() {
        given:
            def changedFilePath = Paths.get(SRC_PATH, "classes", "class1.cls").toString()
            uploadInstance.specificFilesToUpload = [new File(changedFilePath),
                                                    new File(Paths.get(SRC_PATH, "classes", "class1.cls-meta.xml").toString()),
                                                    new File(Paths.get(SRC_PATH, "objects", "object1.object").toString())]
            uploadInstance.packageGenerator.fileTrackerMap = [:]
            uploadInstance.packageGenerator.fileTrackerMap.put(changedFilePath, new ResultTracker(ComponentStates.ADDED))
            def filesChangedExpect = [:]
            filesChangedExpect.put(changedFilePath, new ResultTracker(ComponentStates.ADDED))
        when:
            def filesChangedToUpload = uploadInstance.filterMapFilesChanged()
        then:
            filesChangedToUpload.containsKey(changedFilePath)
            filesChangedExpect.containsKey(changedFilePath)
            filesChangedToUpload.get(changedFilePath).state == filesChangedExpect.get(changedFilePath).state
    }

    def "Test should return a empty Map if there isn't changed file"() {
        given:
            def changedFilePath = Paths.get(SRC_PATH, "classes", "class2.cls").toString()
            uploadInstance.specificFilesToUpload = [new File(Paths.get(SRC_PATH, "classes", "class1.cls").toString()),
                                                    new File(Paths.get(SRC_PATH, "classes", "class1.cls-meta.xml").toString()),
                                                    new File(Paths.get(SRC_PATH, "objects", "object1.object").toString())]
            uploadInstance.packageGenerator.fileTrackerMap = [:]
            uploadInstance.packageGenerator.fileTrackerMap.put(changedFilePath, "")
        when:
            def filesChangedToUpload = uploadInstance.filterMapFilesChanged()
        then:
            filesChangedToUpload == [:]
    }

    def "Test should return an exception if there is a invalid folder"() {
        given:
            def foldersName = ['classes', 'invalid']
        when:
            uploadInstance.validateFolders(foldersName)
        then:
            thrown(Exception)
    }

    def "Test should return an exception if there is a invalid file"() {
        given:
            def filesName = ["classes${File.separator}Class1.txy", "triggers${File.separator}TriggerOne.trigger"]
        when:
            uploadInstance.validateFiles(filesName)
        then:
            thrown(Exception)
    }

    def "Test should fill specificFilesToUpload with files sent"() {
        given:
            uploadInstance.files = "classes${File.separator}class1.cls,objects${File.separator}Object1__c.object"
            uploadInstance.projectPath = SRC_PATH
        when:
            uploadInstance.loadParameter()
        then:
            def classFile = new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString())
            def classFileXml = new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString())
            def objectFile = new File(Paths.get(SRC_PATH, 'objects', 'Object1__c.object').toString())
            uploadInstance.specificFilesToUpload.sort() == [classFile, objectFile, classFileXml].sort()
    }

    def "Test should fill specificFilesToUpload with folders sent"() {
        given:
            uploadInstance.files = 'classes,triggers'
            uploadInstance.projectPath = SRC_PATH
        when:
            uploadInstance.loadParameter()
        then:
            def classFile = new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString())
            def classFileXml = new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString())
            def triggerFile = new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger').toString())
            def triggerFileXml = new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger-meta.xml').toString())
            uploadInstance.specificFilesToUpload.sort() == [classFile, triggerFile, classFileXml, triggerFileXml].sort()
    }

    def "Test should fill specificFilesToUpload with folders and files sent"() {
        given:
            uploadInstance.files = "classes,triggers${File.separator}LunesTrigger.trigger"
            uploadInstance.projectPath = SRC_PATH
        when:
            uploadInstance.loadParameter()
        then:
            def classFile = new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString())
            def classFileXml = new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString())
            def triggerFile = new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger').toString())
            def triggerFileXml = new File(Paths.get(SRC_PATH, 'triggers', 'LunesTrigger.trigger-meta.xml').toString())
            uploadInstance.specificFilesToUpload.sort() == [classFile, triggerFile, classFileXml, triggerFileXml].sort()
    }

    def "Test should fill specificFilesToUpload array following a wildcard sent '*/class1.cls,objects/Object1__c.object' "() {
        given:
            uploadInstance.files = "*${File.separator}class1.cls,objects${File.separator}Object1__c.object"
            uploadInstance.projectPath = SRC_PATH
        when:
            uploadInstance.loadParameter()
        then:
            def classFile = new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString())
            def classFileXml = new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString())
            def objectFile = new File(Paths.get(SRC_PATH, 'objects', 'Object1__c.object').toString())
            uploadInstance.specificFilesToUpload.sort() == [classFile, objectFile, classFileXml].sort()
    }

    def "Test should fill specificFilesToUpload array following a wildcard sent 'objects/*.object' "() {
        given:
            uploadInstance.files = "objects${File.separator}*.object"
            uploadInstance.projectPath = SRC_PATH
        when:
            uploadInstance.loadParameter()
        then:
            uploadInstance.specificFilesToUpload.sort() == [new File(Paths.get(SRC_PATH, 'objects', 'Account.object').toString()),
                                                            new File(Paths.get(SRC_PATH, 'objects', 'Object1__c.object').toString()),
                                                            new File(Paths.get(SRC_PATH, 'objects', 'Object2__c.object').toString())].sort()
    }

    def "Test should fill specificFilesToUpload array following a wildcard sent '**/*Account*/**' "() {
        given:
            uploadInstance.files = "**${File.separator}*Account*${File.separator}**"
            uploadInstance.projectPath = SRC_PATH
        when:
            uploadInstance.loadParameter()
        then:
            uploadInstance.specificFilesToUpload == [new File(Paths.get(SRC_PATH, 'objects', 'Account.object').toString())]
    }

    def "Test should fill specificFilesToUpload array following a wildcard sent '**/*.cls' "() {
        given:
            uploadInstance.files = "**${File.separator}*.cls"
            uploadInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
        when:
            uploadInstance.loadParameter()
        then:
            uploadInstance.specificFilesToUpload.sort() == [new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString()),
                                                            new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls-meta.xml').toString())].sort()
    }

    def "Test should load files into build folder before to execute deploy to 'gradle upload -Pall=true' command"() {
        given:
            uploadInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            uploadInstance.pathUpload = pathUpload
            uploadInstance.uploadPackagePath = Paths.get(pathUpload, Constants.PACKAGE_FILE_NAME).toString()
            uploadInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', Constants.PACKAGE_FILE_NAME).toString()
            uploadInstance.parameters.put('all', 'true')
        when:
            uploadInstance.createDeploymentDirectory(pathUpload)
            uploadInstance.loadFilesChangedToUpload()
            uploadInstance.loadParameter()
            uploadInstance.loadAllFiles()
            uploadInstance.loadFiles()
            uploadInstance.copyFilesToUpload()
            uploadInstance.createPackage()
            uploadInstance.truncate(pathUpload)

            File packageFromBuildDirectory = new File(Paths.get(pathUpload, Constants.PACKAGE_FILE_NAME).toString())
            PackageBuilder packageBuilder = new PackageBuilder()
            FileReader fileReader = new FileReader(packageFromBuildDirectory)
            packageBuilder.read(fileReader)

            packageBuilder.metaPackage.types.each{ type->
                packagedExpected.put(type.name as String, type.members as ArrayList<String>)
            }
        then:
            new File(Paths.get(pathUpload, 'classes', 'Class1.cls').toString()).exists()
            new File(Paths.get(pathUpload, 'classes', 'Class1.cls-meta.xml').toString()).exists()
            new File(Paths.get(pathUpload, 'objects', 'Object1__c.object').toString()).exists()
            new File(Paths.get(pathUpload, 'triggers', 'Trigger1.trigger').toString()).exists()
            new File(Paths.get(pathUpload, 'triggers', 'Trigger1.trigger-meta.xml').toString()).exists()
            packagedExpected.containsKey('CustomObject')
            packagedExpected.get('CustomObject') == ['Object1__c']
            packagedExpected.containsKey('ApexTrigger')
            packagedExpected.get('ApexTrigger') == ['Trigger1']
            packagedExpected.containsKey('ApexClass')
            packagedExpected.get('ApexClass') == ['Class1']
    }

    def "Test should load files into build folder before to execute deploy to 'gradle upload -Pfiles=classes,objects' command"() {
        given:
            uploadInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            uploadInstance.pathUpload = pathUpload
            uploadInstance.uploadPackagePath = Paths.get(pathUpload, Constants.PACKAGE_FILE_NAME).toString()
            uploadInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', Constants.PACKAGE_FILE_NAME).toString()
            uploadInstance.parameters.put('files', 'classes,objects')
        when:
            uploadInstance.createDeploymentDirectory(pathUpload)
            uploadInstance.loadFilesChangedToUpload()
            uploadInstance.loadParameter()
            uploadInstance.loadAllFiles()
            uploadInstance.loadFiles()
            uploadInstance.copyFilesToUpload()
            uploadInstance.createPackage()
            uploadInstance.truncate(pathUpload)

            File packageFromBuildDirectory = new File(Paths.get(pathUpload, Constants.PACKAGE_FILE_NAME).toString())
            PackageBuilder packageBuilder = new PackageBuilder()
            FileReader fileReader = new FileReader(packageFromBuildDirectory)
            packageBuilder.read(fileReader)

            packageBuilder.metaPackage.types.each{ type->
                packagedExpected.put(type.name as String, type.members as ArrayList<String>)
            }
        then:
            new File(Paths.get(pathUpload, 'classes', 'Class1.cls').toString()).exists()
            new File(Paths.get(pathUpload, 'classes', 'Class1.cls-meta.xml').toString()).exists()
            new File(Paths.get(pathUpload, 'objects', 'Object1__c.object').toString()).exists()
            !new File(Paths.get(pathUpload, 'triggers', 'Trigger1.trigger').toString()).exists()
            !new File(Paths.get(pathUpload, 'triggers', 'Trigger1.trigger-meta.xml').toString()).exists()
            packagedExpected.containsKey('CustomObject')
            packagedExpected.get('CustomObject') == ['Object1__c']
            packagedExpected.containsKey('ApexClass')
            packagedExpected.get('ApexClass') == ['Class1']

    }

    def "Test should load files into build folder before to execute deploy to 'gradle upload -Pexcludes=classes' command"() {
        given:
            uploadInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            uploadInstance.pathUpload = pathUpload
            uploadInstance.uploadPackagePath = Paths.get(pathUpload, Constants.PACKAGE_FILE_NAME).toString()
            uploadInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', Constants.PACKAGE_FILE_NAME).toString()
            uploadInstance.parameters.put('excludes', 'classes')
        when:
            uploadInstance.createDeploymentDirectory(pathUpload)
            uploadInstance.loadFilesChangedToUpload()
            uploadInstance.loadParameter()
            uploadInstance.loadAllFiles()
            uploadInstance.loadFiles()
            uploadInstance.copyFilesToUpload()
            uploadInstance.createPackage()
            uploadInstance.truncate(pathUpload)

            File packageFromBuildDirectory = new File(Paths.get(pathUpload, Constants.PACKAGE_FILE_NAME).toString())
            PackageBuilder packageBuilder = new PackageBuilder()
            FileReader fileReader = new FileReader(packageFromBuildDirectory)
            packageBuilder.read(fileReader)

            packageBuilder.metaPackage.types.each{ type->
                packagedExpected.put(type.name as String, type.members as ArrayList<String>)
            }
        then:
            !new File(Paths.get(pathUpload, 'classes', 'Class1.cls').toString()).exists()
            !new File(Paths.get(pathUpload, 'classes', 'Class1.cls-meta.xml').toString()).exists()
            new File(Paths.get(pathUpload, 'objects', 'Object1__c.object').toString()).exists()
            new File(Paths.get(pathUpload, 'triggers', 'Trigger1.trigger').toString()).exists()
            new File(Paths.get(pathUpload, 'triggers', 'Trigger1.trigger-meta.xml').toString()).exists()
            packagedExpected.containsKey('CustomObject')
            packagedExpected.get('CustomObject') == ['Object1__c']
            packagedExpected.containsKey('ApexTrigger')
            packagedExpected.get('ApexTrigger') == ['Trigger1']
            !packagedExpected.containsKey('ApexClass')

    }

    def "Test should load files into build folder before to execute deploy to 'gradle upload -Pfiles=classes -Pexcludes=classes/Class1.cls' command"() {
        given:
            File class2 = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls').toString())
            File classXml2 = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls-meta.xml').toString())
            class2.createNewFile()
            classXml2.createNewFile()
            File class3 = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class3.cls').toString())
            File classXml3 = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class3.cls-meta.xml').toString())
            class3.createNewFile()
            classXml3.createNewFile()
            uploadInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            uploadInstance.pathUpload = pathUpload
            uploadInstance.uploadPackagePath = Paths.get(pathUpload, Constants.PACKAGE_FILE_NAME).toString()
            uploadInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', Constants.PACKAGE_FILE_NAME).toString()
            uploadInstance.parameters.put('excludes', "classes${File.separator}Class1.cls")
            uploadInstance.parameters.put('files', 'classes')
        when:
            uploadInstance.createDeploymentDirectory(pathUpload)
            uploadInstance.loadFilesChangedToUpload()
            uploadInstance.loadParameter()
            uploadInstance.loadAllFiles()
            uploadInstance.loadFiles()
            uploadInstance.copyFilesToUpload()
            uploadInstance.createPackage()
            uploadInstance.truncate(pathUpload)

            File packageFromBuildDirectory = new File(Paths.get(pathUpload, Constants.PACKAGE_FILE_NAME).toString())
            PackageBuilder packageBuilder = new PackageBuilder()
            FileReader fileReader = new FileReader(packageFromBuildDirectory)
            packageBuilder.read(fileReader)
            packageBuilder.metaPackage.types.each{ type->
                packagedExpected.put(type.name as String, type.members as ArrayList<String>)
            }

        then:
            !new File(Paths.get(pathUpload, 'classes', 'Class1.cls').toString()).exists()
            !new File(Paths.get(pathUpload, 'classes', 'Class1.cls-meta.xml').toString()).exists()
            new File(Paths.get(pathUpload, 'classes', 'Class2.cls').toString()).exists()
            new File(Paths.get(pathUpload, 'classes', 'Class2.cls-meta.xml').toString()).exists()
            new File(Paths.get(pathUpload, 'classes', 'Class3.cls').toString()).exists()
            new File(Paths.get(pathUpload, 'classes', 'Class3.cls-meta.xml').toString()).exists()
            packagedExpected.containsKey('ApexClass')
            packagedExpected.get('ApexClass').sort() == ['Class2', 'Class3'].sort()
    }

    def cleanupSpec() {
        new File(Paths.get(SRC_PATH, 'classes', 'class2.cls').toString()).delete()
        new File(Paths.get(SRC_PATH, 'build').toString()).deleteDir()
        new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls-meta.xml').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class3.cls').toString()).delete()
        new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class3.cls-meta.xml').toString()).delete()
    }
}
