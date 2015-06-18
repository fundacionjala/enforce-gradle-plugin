package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.SalesforceValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.SalesforceValidatorManager

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Validates files and folders defined in Salesforce
 */
class FileValidator {

    /**
     * Validates names of files
     * @param projectPath is a String that contains source path code for a project
     * @param filesName is type array list contents names of files
     */
    public static Map<String,ArrayList<File>> validateFiles(String projectPath, ArrayList<File> files) {
        Map<String,ArrayList<File>> filesState = [:]
        filesState.put(Constants.INVALID_FILE, new ArrayList<File>())
        filesState.put(Constants.VALID_FILE, new ArrayList<File>())
        filesState.put(Constants.DOES_NOT_EXIST_FILES, new ArrayList<File>())
        files.each { File file ->
            Path path = Paths.get(file.getPath())
            String relativePath = Util.getRelativePath(file, projectPath)
            String parentFileName = Util.getFirstPath(relativePath)
            SalesforceValidator validator = SalesforceValidatorManager.getValidator(parentFileName)
            boolean isValid = true
            if (!Files.exists(path)) {
                filesState[Constants.DOES_NOT_EXIST_FILES].add(file)
                isValid = false
            }
            if (!validator.validateFileByFolder(parentFileName, file)) {
                filesState[Constants.INVALID_FILE].add(file)
                isValid = false
            }
            if(isValid){
                filesState[Constants.VALID_FILE].add(file)
            }
        }
        return filesState
    }
}
