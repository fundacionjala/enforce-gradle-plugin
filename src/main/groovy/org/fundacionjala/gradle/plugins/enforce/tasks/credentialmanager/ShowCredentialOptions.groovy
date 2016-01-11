package org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager

/**
 * Created by fernando_hurtado on 11-01-16.
 */
enum ShowCredentialOptions {
    STATUS("status"),
    VALID_STATUS("isValid"),
    INVALID_STATUS("isInvalid"),
    ALL_STATUS("allStatus"),
    LOCATION("location")

    ShowCredentialOptions(String value) {
        this.value = value
    }

    private final String value

    public String value() {
        return value
    }
}
