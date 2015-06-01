package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators
/**
 * Defines the different kind of validate a file
 * based in Salesforce definitions
 */
public interface SalesforceValidator {
    boolean validateFileByFolder(String folderName, File file)
}