package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files
/**
 * Defines the different kind of validate a file
 * based in Salesforce definitions
 */
public interface SalesforceValidator {
    boolean validateFile(File file, String folderComponent)
    boolean validateFileContainsXML(File file, String folderComponent)
}