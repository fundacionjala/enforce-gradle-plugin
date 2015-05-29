package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators

import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

/**
 * Manages the different kind of validate a file
 * based in Salesforce definitions
 */
public class SalesforceValidatorManager {
    private static Map<String, SalesforceValidator> validatorMap = [
            MetadataComponents.DOCUMENTS.getDirectory(): new DocumentSalesforceValidator()
    ]

    /**
     * Returns a object validator based in the folderName
     * defined in Salesforce
     */
    public static SalesforceValidator getValidator(String folderName) {
        if (validatorMap.containsKey(folderName)) {
           return validatorMap.get(folderName)
        }
        return new FileSalesforceValidator()
    }
}
