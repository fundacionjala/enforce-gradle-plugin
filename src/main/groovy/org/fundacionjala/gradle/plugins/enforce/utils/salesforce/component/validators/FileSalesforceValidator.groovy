package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators

import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

public class FileSalesforceValidator implements SalesforceValidator{

    /**
     * Validates the file based in the folder name who belongs, following the Saleforce definitions
     * @param folderName
     * @param file
     * @return boolean
     */
    @Override
    public boolean validateFileByFolder(String folderName, File file) {
        String componentExtension = MetadataComponents.getExtensionByFolder(folderName)
        if (!componentExtension) {
            return false
        }
        return Util.getFileExtension(file).equals(componentExtension)
    }
}
