package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files.BasicOrgComponentsValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files.DefaultOrgComponentsValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files.SalesforceValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.org.BasicOrgSubcomponentsValidator
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.org.OrgInterfaceValidator


/**
 * Validates files into your org SalesForce
 */
class OrgValidator {

    public static Map<ArrayList<String>, SalesforceValidator> validators = [
        ['classes','components','pages','triggers','resources']: BasicOrgComponentsValidator.class,
        ['fields','compactLayouts']: BasicOrgSubcomponentsValidator.class,
    ]

    /**
     * @Returns a list with valid files
     * @param credential  contains the data needed to connect with the API sales force
     * @param filesToValidate ia a list of files that need to validate
     * @param projectPath our repository
     */
    public static ArrayList<File> getValidFiles(Credential credential,ArrayList<File> filesToValidate, String projectPath) {
        Map<String,ArrayList<File>> mapFiles = validateFiles(credential, filesToValidate, projectPath)
        return mapFiles[Constants.VALID_FILE] + mapFiles[Constants.FILE_WITHOUT_VALIDATOR]
    }


    /**
     * @return Map with all files validated and its states
     * @param credential  contains the data needed to connect with the API sales force
     * @param filesToValidate ia a list of files that need to validate
     * @param projectPath our repository
     */
    public static Map<String,ArrayList<File>> validateFiles(Credential credential,ArrayList<File> filesToValidate, String projectPath) {
        Map<String, ArrayList<File>> mapFiles = filesToValidate.groupBy {
            Util.getFirstPath(Util.getRelativePath(it,projectPath))
        }

        Map<String,ArrayList<File>> mapResponse = [:]
        Map<String,ArrayList<File>> mapResponseFolder = [:]
        mapResponse.put(Constants.VALID_FILE, new ArrayList<File>())
        mapResponse.put(Constants.DOES_NOT_EXIST_FILES, new ArrayList<File>())
        mapResponse.put(Constants.FILE_WITHOUT_VALIDATOR, new ArrayList<File>())

        mapFiles.each { folderComponent, files ->
            OrgInterfaceValidator val = getValidator(folderComponent)
            mapResponseFolder = val.validateFiles(credential, files, folderComponent, projectPath)
            mapResponseFolder.each { groupFile, filesValidates ->
                mapResponse[groupFile].addAll(filesValidates)
            }
        }
        return mapResponse
    }

    /**
     * Return the necessary validator
     * @param componentType is the component that need be validated
     */
    public static OrgInterfaceValidator getValidator(String componentType) {
        def orgValidator = new DefaultOrgComponentsValidator()
        validators.each { key, validator ->
            if(key.contains(componentType)){
                orgValidator = validator.newInstance()
            }
        }
        return orgValidator
    }
}
