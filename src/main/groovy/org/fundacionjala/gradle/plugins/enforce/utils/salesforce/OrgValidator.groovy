package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files.BasicOrgComponentsValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files.DefaultOrgComponentsValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files.SalesforceValidator
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.org.OrgInterfaceValidator


/**
 * Validates files into your org SalesForce
 */
class OrgValidator {

    public Map<String, SalesforceValidator> validators = [
            'classes':new BasicOrgComponentsValidator(),
            'components':new BasicOrgComponentsValidator(),
            'pages':new BasicOrgComponentsValidator(),
            'triggers':new BasicOrgComponentsValidator(),
            'resources':new BasicOrgComponentsValidator()
    ]

    /**
     * Validates names of files
     * @param credential  contains the data needed to connect with the API sales force
     * @param filesToValidate ia a list of files that need to validate
     * @param path orn repository
     */
    public static Map<String,ArrayList<File>> validateFiles(Credential credential,ArrayList<File> filesToValidate, String path) {
        OrgValidator validator = new OrgValidator()
        return validator.getValidationMap(credential, filesToValidate, path)
    }

    /**
     * @return a list with de valid files
     * @param credential  contains the data needed to connect with the API sales force
     * @param filesToValidate ia a list of files that need to validate
     * @param path orn repository
     */
    public static ArrayList<File> getValidFiles(Credential credential,ArrayList<File> filesToValidate, String path) {
        Map<String,ArrayList<File>> mapFiles = OrgValidator.getValidationMap(credential, filesToValidate, path)
        return mapFiles[Constants.VALID_FILE]
    }


    /**
     * @return Map with all files validated and its states
     * @param credential  contains the data needed to connect with the API sales force
     * @param filesToValidate ia a list of files that need to validate
     * @param path orn repository
     */
    public  Map<String,ArrayList<File>> getValidationMap(Credential credential,ArrayList<File> filesToValidate, String path) {
        Map<String, ArrayList<File>> mapFiles = filesToValidate.groupBy {
            Util.getFirstPath(Util.getRelativePath(it,path))
        }

        Map<String,ArrayList<File>> mapResponse = [:]
        Map<String,ArrayList<File>> mapResponseFolder = [:]
        mapResponse.put(Constants.VALID_FILE, new ArrayList<File>())
        mapResponse.put(Constants.DOES_NOT_EXIST_FILES, new ArrayList<File>())
        mapResponse.put(Constants.FILE_WITHOUT_VALIDATOR, new ArrayList<File>())

        mapFiles.each { folderComponent, files ->
            OrgInterfaceValidator val = getValidator(folderComponent)
            mapResponseFolder = val.validateFiles(credential, files, folderComponent, path)
            mapResponseFolder.each { groupFile, filesValidates ->
                mapResponse[groupFile].addAll(filesValidates)
            }
        }
        return mapResponse
    }

    /**
     * Return the necesary validator
     * @param componentType is the component that need be validated
     */
    public OrgInterfaceValidator getValidator(String componentType) {
        if(validators.containsKey(componentType)) {
            return validators.get(componentType)
        }
        return new DefaultOrgComponentsValidator()
    }
}
