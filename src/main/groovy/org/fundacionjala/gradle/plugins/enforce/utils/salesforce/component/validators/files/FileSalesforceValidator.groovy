package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

public class FileSalesforceValidator extends XMLFileSalesforceValidator implements SalesforceValidator{

    /**
     * Validates the file based in the folder name who belongs, following the Saleforce definitions
     * @param file is a File
     * @param folderComponent is a String, based in Salesforce folders definitions
     * @return boolean
     */
    @Override
    public boolean validateFile(File file, String folderComponent) {
        String componentExtension = MetadataComponents.getExtensionByFolder(folderComponent)
        if (!componentExtension) {
            return false
        }

        return Util.getFileExtension(file).equals(componentExtension) ||
                validateXMLFile(file, folderComponent)
    }
}
