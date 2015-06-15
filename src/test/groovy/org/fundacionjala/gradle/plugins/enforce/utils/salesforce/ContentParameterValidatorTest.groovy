package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import spock.lang.Specification

import java.nio.file.Paths

class ContentParameterValidatorTest extends Specification {

    def "Test should returns all invalid files" () {
        given:
            String invalidFolderByDocument = Paths.get('reports/mydocs/doc1.doc').toString()
            String invalidExtensionFile = Paths.get('classes/Class1.cls-meta.xml').toString()
            String invalidFileWithoutExtension = Paths.get('classes/Class1').toString()
            ArrayList<String> files = []
            files.add(Paths.get('documents/mydocs/doc1.doc').toString())
            files.add(Paths.get('documents/mydocs/image.png').toString())
            files.add(invalidFolderByDocument)
            files.add('reports/myreports/report1.report')
            files.add('objects/obj__c.object')
            files.add('classes/Class1.cls')
            files.add(invalidExtensionFile)
            files.add(invalidFileWithoutExtension)
            String projectPath = 'src'
            Map<String, ArrayList<String>> expected = [:]
            ArrayList<String> invalidFiles = []
            invalidFiles.add(invalidFolderByDocument)
            invalidFiles.add(invalidFileWithoutExtension)
            invalidFiles.add(invalidExtensionFile)
            expected.put(Constants.INVALID_FILE, invalidFiles)
        when:
            Map<String, ArrayList<String>> result = ContentParameterValidator.validateFiles(projectPath, files)
        then:
            result[Constants.INVALID_FILE].sort() == expected[Constants.INVALID_FILE].sort()
    }
}
