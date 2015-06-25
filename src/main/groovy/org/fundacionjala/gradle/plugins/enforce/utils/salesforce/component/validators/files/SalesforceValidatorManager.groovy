package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files

import org.codehaus.groovy.runtime.GStringImpl
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

/**
 * Manages the different kind of validate a file
 * based in Salesforce definitions
 */
public class SalesforceValidatorManager {
    private static final String COMPONENTS_SUPPORT_XML = 'components support xml'
    private static Map<String, SalesforceValidator> validatorMap = [
            "${MetadataComponents.DOCUMENTS.getDirectory()}": new DocumentSalesforceValidator(),
            "${COMPONENTS_SUPPORT_XML}": new XMLFileSalesforceValidator()
    ]

    /**
     * Returns a object validator based in the folderName
     * defined in Salesforce
     * @return SalesforceValidator
     */
    public static SalesforceValidator getValidator(String folderName) {
        GString key = createKey(folderName)
        if (validatorMap.containsKey(key)) {
           return validatorMap.get(key)
        }
        return new FileSalesforceValidator()
    }

    /**
     * Returns a valid key for validator map
     * @param folderName defines the folder that contains the salesforces components
     * @return GString that contains key map
     */
    private static GString createKey(String folderName) {
        GString key = "${folderName}"
        MetadataComponents component = MetadataComponents.getComponent(folderName)
        if(component != null) {
            if (component.containsXMLFile() &&
                !folderName.equals(MetadataComponents.DOCUMENTS.directory)) {
                key = "${COMPONENTS_SUPPORT_XML}"
            }
        }
        return key
    }
}
