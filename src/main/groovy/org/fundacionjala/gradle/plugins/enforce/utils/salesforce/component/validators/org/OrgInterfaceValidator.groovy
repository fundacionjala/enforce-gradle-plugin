package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.org

import org.fundacionjala.gradle.plugins.enforce.wsc.Credential

/**
 * Defines the different kind of validate a file
 * based in Salesforce definitions
 */
public interface OrgInterfaceValidator {
    Map<String,ArrayList<File>> validateFiles(Credential credential, ArrayList<File> filesToVerify, String folderComponent, String path)
}
