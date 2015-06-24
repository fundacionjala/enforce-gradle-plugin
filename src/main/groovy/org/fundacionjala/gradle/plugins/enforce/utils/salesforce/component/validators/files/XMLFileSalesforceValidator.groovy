package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

import java.nio.file.Paths

class XMLFileSalesforceValidator {

    public boolean validateXMLFile(File file, String folderComponent) {
        MetadataComponents component = MetadataComponents.getComponentByFolder(folderComponent);
        String fileName = file.getName()
        String name = fileName.replace(Constants.META_XML, '')
        File fileComponent = new File(Paths.get(file.getParent(), name).toString())

        return  component.containsXMLFile() &&
                fileName.endsWith(Constants.META_XML) &&
                fileComponent.exists() &&
                component.extension.equals(Util.getFileExtension(fileComponent))
    }
}
