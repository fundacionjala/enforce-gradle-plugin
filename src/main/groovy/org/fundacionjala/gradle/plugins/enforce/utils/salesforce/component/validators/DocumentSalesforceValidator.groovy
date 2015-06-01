package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators

import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

public class DocumentSalesforceValidator implements SalesforceValidator{

    /**
     * Validates the document file that belongs to documents folder defined in Salesforce
     * @param folderName
     * @param file
     * @return
     */
    @Override
    boolean validateFileByFolder(String folderName, File file) {
        if (folderName == MetadataComponents.DOCUMENTS.getDirectory() && !file.isDirectory()) {
            return true
        }

        return false
    }
}
