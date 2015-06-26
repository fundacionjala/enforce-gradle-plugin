package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.org.OrgInterfaceValidator
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential

public class DefaultOrgComponentsValidator implements OrgInterfaceValidator{

    @Override
    public Map<String,ArrayList<File>> validateFiles(Credential credential, ArrayList<File> filesToVerify, String folderComponent, String path) {

        Map<String,ArrayList<File>> mapFiles = [:]
        mapFiles.put(Constants.VALID_FILE, new ArrayList<File>())
        mapFiles.put(Constants.DOES_NOT_EXIST_FILES, new ArrayList<File>())
        mapFiles.put(Constants.FILE_WHITOUT_VALIDATOR, new ArrayList<File>())

        mapFiles[Constants.FILE_WHITOUT_VALIDATOR].addAll(filesToVerify)

        return mapFiles
    }
}
