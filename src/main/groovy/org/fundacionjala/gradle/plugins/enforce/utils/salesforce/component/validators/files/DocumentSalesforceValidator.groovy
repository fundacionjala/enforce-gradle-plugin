package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files

import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

public class DocumentSalesforceValidator extends XMLFileSalesforceValidator implements SalesforceValidator{

    /**
     * Validates the document file that belongs to documents folder defined in Salesforce
     * @param file is a File
     * @param folderComponent is a String, based in Salesforce folders definitions
     * @return boolean
     */
    @Override
    boolean validateFile(File file, String folderComponent) {
        if (folderComponent == MetadataComponents.DOCUMENTS.getDirectory() && !file.isDirectory()) {
            return true
        }

        if(validateXMLFileByFolder(folderName, file)) {
            return true
        }

        return false
    }
}
