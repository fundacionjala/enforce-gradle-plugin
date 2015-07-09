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
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.FileValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageBuilder
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageGenerator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.filter.Filter
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
        String SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
            "fundacionjala", "gradle", "plugins", "enforce", "tasks", "salesforce", "resources").toString()

    @Shared
        String pathUpload = Paths.get(SRC_PATH, 'build', Constants.DIR_UPLOAD_FOLDER)

    @Shared
        Map<String, ArrayList<String>> packagedExpected

    @Shared
        FileValidator fileValidator

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        project.enforce.srcPath = SRC_PATH
        uploadInstance = project.tasks.upload
        uploadInstance.project.enforce.deleteTemporaryFiles = false
        uploadInstance.fileManager = new ManagementFile(SRC_PATH)
        uploadInstance.packageGenerator = new PackageGenerator()
        uploadInstance.filter = Mock(Filter)
        fileValidator = Mock(FileValidator)
        packagedExpected = [:]
        uploadInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
        uploadInstance.buildFolderPath = Paths.get(SRC_PATH, 'build').toString()
        uploadInstance.projectPackagePath = Paths.get(SRC_PATH, 'src', Constants.PACKAGE_FILE_NAME).toString()
    }

    def "Test shouldn't create a package xml file if specificFilesToUpload Array is empty "() {
        given:
            uploadInstance.createDeploymentDirectory(pathUpload)
            uploadInstance.taskPackagePath = Paths.get(SRC_PATH, 'build', 'upload', 'package.xml').toString()
            uploadInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            uploadInstance.specificFilesToUpload = [:]
            uploadInstance.specificFilesToUpload.put(Constants.VALID_FILE, [])
        when:
            uploadInstance.createPackage()
        then:
            !new File(Paths.get(SRC_PATH, 'build', 'upload', 'package.xml').toString()).exists()
    }

    def "Test should create a package xml file with valid files"() {
         given:
             uploadInstance.createDeploymentDirectory(pathUpload)
             uploadInstance.taskPackagePath = Paths.get(SRC_PATH, 'build', 'package.xml').toString()
             uploadInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
             uploadInstance.specificFilesToUpload = [:]
             File fileOne = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString())
             File fileThree = new File(Paths.get(SRC_PATH, 'src', 'objects', 'Object1__c.object').toString())
            uploadInstance.specificFilesToUpload.put(Constants.VALID_FILE, [fileOne, fileThree])
         when:
             uploadInstance.createPackage()
         then:
             !new File(Paths.get(SRC_PATH, 'build', 'upload', 'package.xml').toString()).exists()
    }

    def "Test should copy files to upload"() {
        given:
            uploadInstance.createDeploymentDirectory(pathUpload)
            uploadInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            uploadInstance.taskFolderPath = Paths.get(SRC_PATH, 'build', 'upload').toString()
            File fileOne = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString())
            File fileTwo = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls-meta.xml').toString())
            File fileThree = new File(Paths.get(SRC_PATH, 'src', 'objects', 'Object1__c.object').toString())
            uploadInstance.specificFilesToUpload = [:]
            uploadInstance.specificFilesToUpload.put(Constants.VALID_FILE, [fileOne, fileThree, fileTwo])
        when:
            uploadInstance.copyFilesToUpload()
        then:
            new File(Paths.get(SRC_PATH, 'build', 'upload', 'classes', 'Class1.cls').toString()).exists()
            new File(Paths.get(SRC_PATH, 'build', 'upload', 'classes', 'Class1.cls-meta.xml').toString()).exists()
            new File(Paths.get(SRC_PATH, 'build', 'upload', 'objects', 'Object1__c.object').toString()).exists()
    }

    def "Test should return a map with changed files"() {
        given:
            def changedFilePath = Paths.get(SRC_PATH, "classes", "class1.cls").toString()
            uploadInstance.specificFilesToUpload = [:]
            uploadInstance.specificFilesToUpload.put(Constants.VALID_FILE,[ new File(changedFilePath),
                                                    new File(Paths.get(SRC_PATH, "classes", "class1.cls-meta.xml").toString()),
                                                    new File(Paths.get(SRC_PATH, "objects", "object1.object").toString())])
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
            ArrayList<File> validFiles = [new File(Paths.get(SRC_PATH, "classes", "class1.cls").toString()),
                                          new File(Paths.get(SRC_PATH, "classes", "class1.cls-meta.xml").toString()),
                                          new File(Paths.get(SRC_PATH, "objects", "object1.object").toString())]
            uploadInstance.specificFilesToUpload = [:]
            uploadInstance.specificFilesToUpload.put(Constants.VALID_FILE, validFiles)
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

    def "Test should load files and excludes parameter"() {
        given:
            String filesParameterValue = "classes,objects${File.separator}Object1__c.object"
            String excludesParameterValue = "classes${File.separator}Class1.cls"
            uploadInstance.parameters.put('files', filesParameterValue)
            uploadInstance.parameters.put('excludes', excludesParameterValue)
        when:
            uploadInstance.loadParameters()
        then:
            println uploadInstance.files
            uploadInstance.excludes == excludesParameterValue
            uploadInstance.files == filesParameterValue
    }

    def "Test should return of files by folder send into files parameter"() {
        given:
            uploadInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            File classFile = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString())
            File classFileXml = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls-meta.xml').toString())
            File objectFile = new File(Paths.get(SRC_PATH, 'src', 'objects', 'Object1__c.object').toString())
        when:
            uploadInstance.files = "classes,objects"
            uploadInstance.excludes = ""
            uploadInstance.filter.getFiles(_,_) >> [classFile, classFileXml, objectFile]
            fileValidator.validateFiles(_,_) >> [Constants.VALID_FILE, [classFile, classFileXml, objectFile]]
            Map<String,ArrayList<File>> result = uploadInstance.getClassifiedFiles(uploadInstance.files, uploadInstance.excludes)
        then:
            result.get(Constants.VALID_FILE).sort() == [classFile, classFileXml, objectFile].sort()

    }

    def "Test should return all classes and Trigger1.trigger"() {
        given:
            uploadInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            File classFile = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString())
            File classFileXml = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls-meta.xml').toString())
            File objectFile = new File(Paths.get(SRC_PATH, 'src', 'objects', 'Object1__c.object').toString())
        when:
            uploadInstance.files = "classes,triggers${File.separator}Trigger1.trigger"
            uploadInstance.excludes = ""
            uploadInstance.filter.getFiles(_,_) >> [classFile, classFileXml, objectFile]
            fileValidator.validateFiles(_,_) >> [Constants.VALID_FILE, [classFile, classFileXml, objectFile]]
            Map<String,ArrayList<File>> result = uploadInstance.getClassifiedFiles(uploadInstance.files, uploadInstance.excludes)
        then:
            result.get(Constants.VALID_FILE).sort() == [classFile, classFileXml, objectFile].sort()
    }

    def "Test should return  files following a wildcard sent '*/class1.cls,objects/Object1__c.object' "() {
        given:
            uploadInstance.projectPath = Paths.get(SRC_PATH, 'src').toString()
            File classFile = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString())
            File classFileXml = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls-meta.xml').toString())
            File triggerFile = new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger').toString())
            File triggerFileXml = new File(Paths.get(SRC_PATH, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString())
        when:
            uploadInstance.files = "*${File.separator}Class1.cls,objects${File.separator}Object1__c.object"
            uploadInstance.excludes = ""
            uploadInstance.filter.getFiles(_,_) >> [classFile, classFileXml, triggerFile, triggerFileXml]
            fileValidator.validateFiles(_,_) >> [Constants.VALID_FILE, [classFile, classFileXml, triggerFile, triggerFileXml]]
            Map<String,ArrayList<File>> result = uploadInstance.getClassifiedFiles(uploadInstance.files, uploadInstance.excludes)
        then:
            result.get(Constants.VALID_FILE).sort() == [classFile, classFileXml, triggerFile, triggerFileXml].sort()
    }

    def "Test should load files into build folder before to execute deploy to 'gradle upload -Pall=true' command"() {
        given:
            uploadInstance.parameters.put('all', 'true')
        when:
            uploadInstance.setup()
            uploadInstance.createDeploymentDirectory(pathUpload)
            uploadInstance.loadFilesChangedToUpload()
            uploadInstance.loadParameters()
            uploadInstance.loadFilesToUpload()
            uploadInstance.copyFilesToUpload()
            uploadInstance.createPackage()
            uploadInstance.combinePackageToUpdate(uploadInstance.taskPackagePath)
            uploadInstance.addInterceptor()

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
            uploadInstance.parameters.put('files', 'classes,objects')
        when:
            uploadInstance.setup()
            uploadInstance.createDeploymentDirectory(pathUpload)
            uploadInstance.loadFilesChangedToUpload()
            uploadInstance.loadParameters()
            uploadInstance.loadFilesToUpload()
            uploadInstance.copyFilesToUpload()
            uploadInstance.createPackage()
            uploadInstance.combinePackageToUpdate(uploadInstance.taskPackagePath)
            uploadInstance.addInterceptor()

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
            uploadInstance.parameters.put('excludes', 'classes')
        when:
            uploadInstance.setup()
            uploadInstance.createDeploymentDirectory(pathUpload)
            uploadInstance.loadFilesChangedToUpload()
            uploadInstance.loadParameters()
            uploadInstance.loadFilesToUpload()
            uploadInstance.copyFilesToUpload()
            uploadInstance.createPackage()
            uploadInstance.combinePackageToUpdate(uploadInstance.taskPackagePath)
            uploadInstance.addInterceptor()

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
            uploadInstance.parameters.put('excludes', "classes${File.separator}Class1.cls")
            uploadInstance.parameters.put('files', 'classes')

            File class2 = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls').toString())
            File classXml2 = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class2.cls-meta.xml').toString())
            class2.createNewFile()
            classXml2.createNewFile()
            File class3 = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class3.cls').toString())
            File classXml3 = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class3.cls-meta.xml').toString())
            class3.createNewFile()
            classXml3.createNewFile()
        when:
            uploadInstance.setup()
            uploadInstance.createDeploymentDirectory(pathUpload)
            uploadInstance.loadFilesChangedToUpload()
            uploadInstance.loadParameters()
            uploadInstance.loadFilesToUpload()
            uploadInstance.copyFilesToUpload()
            uploadInstance.createPackage()
            uploadInstance.combinePackageToUpdate(uploadInstance.taskPackagePath)
            uploadInstance.addInterceptor()

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
