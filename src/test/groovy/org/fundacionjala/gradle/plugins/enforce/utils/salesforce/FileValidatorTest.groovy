package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class FileValidatorTest extends Specification {
    @Shared
    String projectPath

    def setup() {
        def userDir = System.getProperty("user.dir")
        projectPath = Paths.get(userDir, 'src/test/groovy/org/fundacionjala/gradle/plugins/enforce/utils/resources').toString()
    }

    def "Test should returns all invalid files" () {
        given:
            File invalidFolderByDocument = new File(Paths.get(projectPath, 'reports/mydocs/doc1.doc').toString())
            File invalidExtensionFile = new File(Paths.get(projectPath, 'classes/Class1.cls-meta.xml').toString())
            File invalidFileWithoutExtension = new File(Paths.get(projectPath, 'classes/Class1').toString())
            ArrayList<File> files = []
            files.add(new File(Paths.get(projectPath, 'documents/mydocs/doc1.doc').toString()))
            files.add(new File(Paths.get(projectPath, 'documents/mydocs/image.png').toString()))
            files.add(invalidFolderByDocument)
            files.add(new File(Paths.get(projectPath, 'reports/myreports/report1.report').toString()))
            files.add(new File(Paths.get(projectPath, 'objects/obj__c.object').toString()))
            files.add(new File(Paths.get(projectPath, 'classes/Class1.cls').toString()))
            files.add(invalidExtensionFile)
            files.add(invalidFileWithoutExtension)
            Map<String, ArrayList<File>> expected = [:]
            ArrayList<File> invalidFiles = []
            invalidFiles.add(invalidFolderByDocument)
            invalidFiles.add(invalidFileWithoutExtension)
            invalidFiles.add(invalidExtensionFile)
            expected.put(Constants.INVALID_FILE, invalidFiles)
        when:
            Map<String, ArrayList<File>> result = FileValidator.validateFiles(projectPath, files)
        then:
            result[Constants.INVALID_FILE].sort() == expected[Constants.INVALID_FILE].sort()
    }

    def "Test should returns a map that contains all files by states: invalid, not_found, and valid" () {
        given:
            //invalid files
            File invalidFolderFile = new File(Paths.get(projectPath, 'web/InvalidClass.cls').toString())
            File invalidObjectExtension = new File(Paths.get(projectPath, 'objects/InvalidObject__c.obj').toString())
            File invalidExtensionFile = new File(Paths.get(projectPath, 'classes/other.data').toString())

            // valid files
            File validDoc = new File(Paths.get(projectPath, 'documents/mydocs/doc1.doc').toString())
            File validPng = new File(Paths.get(projectPath, 'documents/mydocs/image.png').toString())
            File validReport = new File(Paths.get(projectPath, 'reports/testFolder/testReport.report').toString())
            File validObj = new File(Paths.get(projectPath, 'objects/Object1__c.object').toString())
            File validClass = new File(Paths.get(projectPath, 'classes/class1.cls').toString())

            // not found files
            File notFoundDoc = new File(Paths.get(projectPath, 'documents/mydocs/notFoundDoc.doc').toString())
            File notFoundReport = new File(Paths.get(projectPath, 'reports/testFolder/notFoundReport.report').toString())
            File notFoundClass = new File(Paths.get(projectPath, 'classes/notFoundClass1.cls').toString())

            ArrayList<File> files = []
            files.add(validDoc)
            files.add(validPng)
            files.add(invalidObjectExtension)
            files.add(invalidFolderFile)
            files.add(validReport)
            files.add(validObj)
            files.add(validClass)
            files.add(invalidExtensionFile)
            files.add(notFoundReport)
            files.add(notFoundClass)
            files.add(notFoundDoc)

            Map<String, ArrayList<File>> expected = [:]
            ArrayList<File> invalidFiles = []
            invalidFiles.add(invalidObjectExtension)
            invalidFiles.add(invalidExtensionFile)
            invalidFiles.add(invalidFolderFile)
            ArrayList<File> notFoundFiles = []
            notFoundFiles.add(notFoundClass)
            notFoundFiles.add(notFoundDoc)
            notFoundFiles.add(notFoundReport)

            ArrayList<File> validFiles = []
            validFiles.add(validPng)
            validFiles.add(validClass)
            validFiles.add(validDoc)
            validFiles.add(validObj)
            validFiles.add(validReport)
            expected.put(Constants.INVALID_FILE, invalidFiles)
            expected.put(Constants.VALID_FILE, validFiles)
            expected.put(Constants.DOES_NOT_EXIST_FILES, notFoundFiles)
        when:
            Map<String, ArrayList<File>> result = FileValidator.validateFiles(projectPath, files)
        then:
            result[Constants.INVALID_FILE].sort() == expected[Constants.INVALID_FILE].sort()
            result[Constants.VALID_FILE].sort() == expected[Constants.VALID_FILE].sort()
            result[Constants.DOES_NOT_EXIST_FILES].sort() == expected[Constants.DOES_NOT_EXIST_FILES].sort()
    }
}
