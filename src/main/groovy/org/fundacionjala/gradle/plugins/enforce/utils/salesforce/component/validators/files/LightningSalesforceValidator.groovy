package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

class LightningSalesforceValidator implements SalesforceValidator {
    private static final ArrayList<String> VALID_EXTENSION = ['js', 'css', 'auradoc', 'svg', 'design',
                                                              'cmp', 'app', 'evt', 'intf', 'xml']

    @Override
    boolean validateFile(File file, String folderComponent) {
        String extensionFile = Util.getFileExtension(file)
        return folderComponent == MetadataComponents.AURADEFINITIONBUNDLE.getDirectory() &&
                !file.isDirectory() && VALID_EXTENSION.contains(extensionFile)
    }

    @Override
    boolean hasMetadataFile(File file, String folderComponent) {
        return true
    }
}
