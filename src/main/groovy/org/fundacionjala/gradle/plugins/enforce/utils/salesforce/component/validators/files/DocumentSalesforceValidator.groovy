package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

/**
 * This class defines how validate a document salesforce validator
 */
public class DocumentSalesforceValidator implements SalesforceValidator{

    /**
     * Validates the document file that belongs to documents folder defined in Salesforce
     * @param file is a File
     * @param folderComponent is a String, based in Salesforce folders definitions
     * @return returns true if the file is valid
     */
    @Override
    boolean validateFile(File file, String folderComponent) {

        return folderComponent == MetadataComponents.DOCUMENTS.getDirectory() &&
               !file.isDirectory()
    }

    /**
     * Validates the file validate defines and contains a xml file
     * @param file is a File
     * @param folderComponent is a String, based in Salesforce folders definitions
     * @return returns true if the file contains a xml file
     */
    @Override
    public boolean validateFileContainsXML(File file, String folderComponent) {
        String fileName = file.getName()
        if (!fileName.endsWith(Constants.META_XML)) {
            String xmlFileName = "${file.getAbsolutePath()}${Constants.META_XML}".toString()
            File xmlFile = new File(xmlFileName);
            return xmlFile.exists()
        }
        return true;
    }
}
