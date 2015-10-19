package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.org

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential

/**
 * This class defines a default validate for some salesforce component
 */
public class DefaultOrgComponentsValidator implements OrgInterfaceValidator {

    @Override
    public Map<String, ArrayList<File>> validateFiles(Credential credential, ArrayList<File> filesToVerify, String folderComponent, String projectPath) {

        Map<String, ArrayList<File>> mapFiles = [:]
        mapFiles.put(Constants.VALID_FILE, new ArrayList<File>())
        mapFiles.put(Constants.FILES_NOT_FOUND, new ArrayList<File>())
        mapFiles.put(Constants.FILE_WITHOUT_VALIDATOR, new ArrayList<File>())

        mapFiles[Constants.FILE_WITHOUT_VALIDATOR].addAll(filesToVerify)

        return mapFiles
    }
}
