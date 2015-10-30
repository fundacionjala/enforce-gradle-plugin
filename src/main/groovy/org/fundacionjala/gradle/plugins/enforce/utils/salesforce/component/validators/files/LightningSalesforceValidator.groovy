package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

class LightningSalesforceValidator implements SalesforceValidator {
    private static final ArrayList<String> VALID_EXTENSION_FILES = ['cmp', 'app', 'evt', 'intf', 'xml']
    private static final ArrayList<String> VALID_EXTENSION_WITHOUT_METADATA = ['js', 'css', 'auradoc', 'svg', 'design']

    @Override
    boolean validateFile(File file, String folderComponent) {
        String extensionFile = Util.getFileExtension(file)
        return folderComponent == MetadataComponents.AURADEFINITIONBUNDLE.getDirectory() &&
                !file.isDirectory() && (VALID_EXTENSION_FILES.contains(extensionFile)||
                VALID_EXTENSION_WITHOUT_METADATA.contains(extensionFile))
    }

    @Override
    boolean hasMetadataFile(File file, String folderComponent) {
        String fileName = file.getName()
        String extensionFile = Util.getFileExtension(file)
        if (!fileName.endsWith(Constants.META_XML) && !VALID_EXTENSION_WITHOUT_METADATA.contains(extensionFile)) {
            String xmlFileName = "${file.getAbsolutePath()}${Constants.META_XML}".toString()
            File xmlFile = new File(xmlFileName)
            return xmlFile.exists()
        }
        return true
    }
}
