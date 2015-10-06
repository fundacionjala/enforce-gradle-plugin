package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

class ReportSalesforceValidator implements SalesforceValidator {

    /**
     * Validates the report file that belongs to reports folder defined in Salesforce
     * @param file is File that will be validate
     * @param folderComponent is a String
     * @return true if file is valid
     */
    @Override
    boolean validateFile(File file, String folderComponent) {
        return ((Util.getFileExtension(file).equals(MetadataComponents.REPORTS.getExtension()) ||
                file.getName().endsWith(Constants.META_XML)) && (folderComponent.equals(MetadataComponents.REPORTS.getDirectory()) &&
                !file.isDirectory()))
    }

    /**
     * Validates the report file with xml extension
     * @param file is File that will be validate
     * @param folderComponent is a String
     * @return true if file is valid
     */
    @Override
    boolean hasMetadataFile(File file, String folderComponent) {
        return true
    }
}
