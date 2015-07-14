package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files

import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
/**
 * Generic file validator based in the salesforce definitions
 */
public class FileSalesforceValidator implements SalesforceValidator {

    /**
     * Validates the file based in the folder name who belongs, following the Salesforce definitions
     * @param file is a File
     * @param folderComponent is a String, based in Salesforce folders definitions
     * @return returns true if the file is valid
     */
    @Override
    public boolean validateFile(File file, String folderComponent) {
        String componentExtension = MetadataComponents.getExtensionByFolder(folderComponent)
        if (!componentExtension) {
            return false
        }

        return Util.getFileExtension(file).equals(componentExtension)
    }

    /**
     * Validates the file defines and contains a xml file
     * @param file is a File
     * @param folderComponent is a String, based in Salesforce folders definitions
     * @return returns true if the file contains a xml file
     */
    @Override
    public boolean validateFileContainsXML(File file, String folderComponent) {
        MetadataComponents component = MetadataComponents.getComponentByFolder(folderComponent)
        return !component.containsXMLFile()
    }
}
