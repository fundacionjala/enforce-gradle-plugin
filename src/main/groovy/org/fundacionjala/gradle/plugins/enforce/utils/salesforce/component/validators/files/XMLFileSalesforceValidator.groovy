package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import java.nio.file.Paths

/**
 * This class validates the salesforce components that contains xml file
 */
class XMLFileSalesforceValidator implements SalesforceValidator {

    /**
     * Validates the file component if it has xml file
     * @param file is based in the salesforce metadata types
     * @param folderComponent is a String, based in Salesforce folders metadata types
     * @return returns true if the file is valid
     */
    public boolean validateFile(File file, String folderComponent) {
        String componentExtension = MetadataComponents.getExtensionByFolder(folderComponent)
        if (!componentExtension) {
            return false
        }

        String fileName = file.getName()
        if (fileName.endsWith(Constants.META_XML)) {
            MetadataComponents component = MetadataComponents.getComponentByFolder(folderComponent)
            String name = fileName.replace(Constants.META_XML, '')
            File fileComponent = new File(Paths.get(file.getParent(), name).toString())
            return component.extension.equals(Util.getFileExtension(fileComponent))
        }

        return  Util.getFileExtension(file).equals(componentExtension)
    }

    /**
     * Validates the file validate defines and contains a xml file
     * @param file is based in the salesforce metadata types
     * @param folderComponent is a String, based in Salesforce folders definitions
     * @return returns true if the file contains a xml file
     */
    @Override
    public boolean validateFileContainsXML(File file, String folderComponent) {
        if (!file.getName().endsWith(Constants.META_XML) && file.exists()) {
            String xmlFileName = "${file.getAbsolutePath()}${Constants.META_XML}".toString()
            File xmlFile = new File(xmlFileName)
            return xmlFile.exists()
        }
        return true
    }
}
