package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.SalesforceValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.SalesforceValidatorManager

import java.nio.file.Paths

/**
 * Validates the content of parameter for the enforce tasks
 */
class ContentParameterValidator {

    /**
     * Validates names of files
     * @param projectPath is a String that contains source path code for a project
     * @param filesName is type array list contents names of files
     */
    public static Map<String,ArrayList<String>> validateFiles(String projectPath, ArrayList<String> filesName) {
        Map<String,ArrayList<String>> filesState = [:]
        filesState.put(Constants.INVALID_FILE, new ArrayList<String>())
        filesName.each { String fileName ->
            File file = new File(Paths.get(projectPath, fileName).toString())
            String parentFileName = Util.getFirstPath(fileName)
            SalesforceValidator validator = SalesforceValidatorManager.getValidator(parentFileName)
            if (!validator.validateFileByFolder(parentFileName, file)) {
                filesState[Constants.INVALID_FILE].add(fileName)
            }
        }
        return filesState
    }
}
