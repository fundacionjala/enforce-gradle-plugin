package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files.SalesforceValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files.SalesforceValidatorManager

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Validates files and folders defined in Salesforce
 */
class FileValidator {

    /**
     * @Returns a list with valid files
     * @param projectPath is a String that contains source path code for a project
     * @param files is type array list
     */
    public static ArrayList<File> getValidFiles(String projectPath, ArrayList<File> files) {
        Map<String,ArrayList<File>> mapFiles = validateFiles(projectPath, files)
        return mapFiles[Constants.VALID_FILE]
    }

    /**
     * Validates names of files
     * @param projectPath is a String that contains source path code for a project
     * @param files is type array list
     */
    public static ClassifiedFile validateFiles(String projectPath, ArrayList<File> files) {
        ClassifiedFile classifiedFile = new ClassifiedFile()

        files.each { File file ->
            Path path = Paths.get(file.getPath())
            String relativePath = Util.getRelativePath(file, projectPath)
            String parentFileName = Util.getFirstPath(relativePath)
            if (file.getName() == Constants.PACKAGE_FILE_NAME) {
                classifiedFile.validFiles.add(file)
                return
            }
            if (!isValidFolder(parentFileName)) {
                classifiedFile.invalidFiles.add(file)
                return
            }

            SalesforceValidator validator = SalesforceValidatorManager.getValidator(parentFileName)
            boolean isValid = true
            if (!Files.exists(path)) {
                classifiedFile.notFoundFiles.add(file)
                isValid = false
            }
            if (!validator.validateFile(file, parentFileName)) {
                classifiedFile.invalidFiles.add(file)
                isValid = false
            }
            if (!validator.validateFileContainsXML(file, parentFileName)) {
                classifiedFile.filesWithoutXml.add(file)
                isValid = false
            }
            if(isValid){
                classifiedFile.validFiles.add(file)
            }
        }
        return classifiedFile
    }

    /**
     * Validates if the folder is defined in the Salesforce's Metadata types
     * @param folderComponent is a string
     * @return boolean value
     */
    public static boolean isValidFolder(String folderComponent) {
        MetadataComponents component = MetadataComponents.getComponentByFolder(folderComponent)
        return component != null
    }
}
