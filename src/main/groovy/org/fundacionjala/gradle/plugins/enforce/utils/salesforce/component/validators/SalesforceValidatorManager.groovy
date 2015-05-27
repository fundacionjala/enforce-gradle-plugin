package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators

public class SalesforceValidatorManager {
    private static Map<String, SalesforceValidator> validatorMap = [
            'documents': new DocumentSalesforceValidator()
    ]

    public static SalesforceValidator getValidator(String folderName) {
        if (validatorMap.containsKey(folderName)) {
           return validatorMap.get(folderName)
        }
        return new FileSalesforceValidator()
    }
}
