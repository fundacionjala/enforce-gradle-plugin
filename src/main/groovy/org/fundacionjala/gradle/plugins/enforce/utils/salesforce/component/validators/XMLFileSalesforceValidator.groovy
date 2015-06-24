package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

class XMLFileSalesforceValidator {

    public boolean validateXMLFileByFolder(String folderName, File file) {
        MetadataComponents component = MetadataComponents.getComponentByFolder(folderName);
        String fileName = file.getName()
        if(component.containsXMLFile() && fileName.endsWith(Constants.META_XML)) {
            return true
        }
        return false
    }
}
