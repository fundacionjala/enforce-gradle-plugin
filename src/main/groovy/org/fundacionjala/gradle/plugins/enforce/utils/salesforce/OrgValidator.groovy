package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files.BasicOrgComponentsValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files.DefaultOrgComponentsValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files.SalesforceValidator
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.org.OrgInterfaceValidator


/**
 * Validates files and folders defined in Salesforce
 */
class OrgValidator {

    public Map<String, SalesforceValidator> validators = [
            'classes':new BasicOrgComponentsValidator(),
            'components':new BasicOrgComponentsValidator(),
            'pages':new BasicOrgComponentsValidator(),
            'triggers':new BasicOrgComponentsValidator(),
            'resources':new BasicOrgComponentsValidator()
    ]

    public  Map<String,ArrayList<File>> validateFiles(Credential credential,ArrayList<File> filesToValidate, String path) {
        Map<String, ArrayList<File>> mapFiles = filesToValidate.groupBy {
            Util.getFirstPath(Util.getRelativePath(it,path))
        }

        Map<String,ArrayList<File>> mapResponse = [:]
        Map<String,ArrayList<File>> mapResponseFolder = [:]
        mapResponse.put(Constants.VALID_FILE, new ArrayList<File>())
        mapResponse.put(Constants.DOES_NOT_EXIST_FILES, new ArrayList<File>())
        mapResponse.put(Constants.FILE_WHITOUT_VALIDATOR, new ArrayList<File>())

        mapFiles.each { folderComponent, files ->
            OrgInterfaceValidator val = getValidator(folderComponent)
            mapResponseFolder = (val.validateFiles(credential, files, folderComponent, path))
            mapResponseFolder.each { groupFile, filesValidates ->
                mapResponse[groupFile].addAll(filesValidates)
            }
        }
        return mapResponse
    }

    public OrgInterfaceValidator getValidator(String typeComponent) {
        if(validators.containsKey(typeComponent)) {
            return (new BasicOrgComponentsValidator())
        }
        else {
            return (new DefaultOrgComponentsValidator())
        }
    }
}
