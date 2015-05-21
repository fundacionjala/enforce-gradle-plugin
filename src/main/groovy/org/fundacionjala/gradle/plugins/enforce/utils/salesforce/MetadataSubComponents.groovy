package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

/**
 * Represents all subComponents types of Objects
 */
public enum MetadataSubComponents {
    CUSTOM_FIELD("CustomField")

    private final String fieldName

    MetadataSubComponents(String fieldName) {
        this.fieldName = fieldName
    }


}
