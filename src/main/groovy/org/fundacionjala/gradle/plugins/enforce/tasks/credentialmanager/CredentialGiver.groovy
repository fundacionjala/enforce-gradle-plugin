package org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager

import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialFileManager
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialManager
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialMessage
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential

/**
 * Gives all credentials registered on Enforce.
 */
class CredentialGiver extends CredentialManagerTask {

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    CredentialGiver() {
        super(CredentialMessage.GET_CREDENTIALS_DESCRIPTION.value(), CredentialMessage.CREDENTIAL_MANAGER_GROUP.value())
    }

    @Override
    void runTask() {
        CredentialManager credentialManager = new CredentialManager()
        CredentialFileManager credentialFileManager = new CredentialFileManager(credentialManager.pathCredentials,'');
        logger.quiet("*********************************************")
        logger.quiet("                Credentials                  ")
        logger.quiet("*********************************************")
        for (Credential credential in credentialFileManager.getCredentials()) {
            logger.quiet("")
            logger.quiet("Id : $credential.id")
            logger.quiet("User name : $credential.username")
            logger.quiet("Type : ${getOrganizationType(credential.loginFormat)}")
        }
        logger.quiet("*********************************************")
    }

    /**
     * Sets an organization type message
     * @param loginFormat contains the login format from credentials.dat
     * @return a friendly message that represents credential type.
     */
    private String getOrganizationType(String loginFormat) {
        String enviorement
        switch (loginFormat) {
            case CredentialMessage.LOGIN.value():
                enviorement = CredentialMessage.DEVELOPER_ENVIRONMENT.value()
                break
            case CredentialMessage.TEST.value():
                enviorement = CredentialMessage.TEST_ENVIRONMENT.value()
                break
            default:
                enviorement = "$loginFormat ${CredentialMessage.OTHER_ENVIRONMENT.value()}"
        }
        return enviorement
    }
}
