package org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager

import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialFileManager
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialManagerInput
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialMessage
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential

/**
 * Gives all credentials registered on Enforce.
 */
class CredentialGiver extends CredentialManagerTask {
    private CredentialFileManager credentialFileManager
    private final String PROJECT_OPTION = 'project'
    private final String CREDENTIAL_LOCATION_INFO = 'Those credentials are located at'
    private final String WARNING_TAG = "[Warning]"
    private
    final String WARNING_MESSAGE = "doesn't exist, \nYou should add credential here using parameter location: " +
            "\n\t${'$'}gradle addCredential -Pid=my -Pusername=john@enforce.com -Ppassword=qweasd456fgh -Plocation="

    private final String VALID_STATUS_MESSAGE = "is valid"
    private final String INVALID_STATUS_MESSAGE = "is invalid"

    private CredentialValidator credentialValidator
    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    CredentialGiver() {
        super(CredentialMessage.GET_CREDENTIALS_DESCRIPTION.value(), CredentialMessage.CREDENTIAL_MANAGER_GROUP.value())
        credentialValidator = new CredentialValidator()
    }

    @Override
    void runTask() {
        String credentialsFilePath = getCredentialsFilePath()
        if (!new File(credentialsFilePath).exists()) {
            throw new Exception("${WARNING_TAG} ${credentialsFilePath} ${WARNING_MESSAGE}${location}")
        }
        credentialFileManager = new CredentialFileManager(credentialsFilePath, Constants.EMPTY)

        showCredentials()
    }

    /**
     * Shows credentials from home or project directory
     */
    private void showCredentials() {
        logger.quiet("*********************************************")
        logger.quiet("                Credentials                  ")
        logger.quiet("*********************************************")

        Map<Credential, String> credentialsResult = filterCredentials(status)

        for (mapItem in credentialsResult) {
            printCredential(mapItem.key)
            logger.quiet(mapItem.value)
            logger.quiet("")
            logger.quiet("*********************************************")
        }
        logger.quiet("${CREDENTIAL_LOCATION_INFO} ${getCredentialsFilePath()}")
    }

    Map<Credential, String> filterCredentials(String type) {
        Map<Credential, String> credentialsMap = new HashMap<>()
        for (Credential credential in credentialFileManager.getCredentials()) {
            if (type.empty) {
                credentialsMap.put(credential, Constants.EMPTY)
            } else {
                try {
                    credentialValidator.validateCredential(credential)
                    if (type == ShowCredentialOptions.VALID_STATUS.value() ||
                            type == ShowCredentialOptions.ALL_STATUS.value()) {
                        credentialsMap.put(credential, "Status: $VALID_STATUS_MESSAGE")
                    }
                } catch (Exception e) {
                    if (type == ShowCredentialOptions.INVALID_STATUS.value() ||
                            type == ShowCredentialOptions.ALL_STATUS.value()) {
                        credentialsMap.put(credential, "Status: $INVALID_STATUS_MESSAGE - ${e.message}")
                    }
                }
            }
        }
        return credentialsMap
    }

    void setCredentialValidator(CredentialValidator credentialValidator) {
        this.credentialValidator = credentialValidator
    }

    void setCredentialFileManager (CredentialFileManager credentialFileManager) {
        this.credentialFileManager = credentialFileManager
    }

    private void printCredential(Credential credential) {
        logger.quiet("Id : $credential.id")
        logger.quiet("User name : $credential.username")
        logger.quiet("Type : ${getOrganizationType(credential.loginFormat)}")
    }

    /**
     * Sets an organization type message
     * @param loginFormat contains the login format from credentials.dat
     * @return a friendly message that represents credential type.
     */
    private String getOrganizationType(String loginFormat) {
        String environment
        switch (loginFormat) {
            case CredentialMessage.LOGIN.value():
                environment = CredentialMessage.DEVELOPER_ENVIRONMENT.value()
                break
            case CredentialMessage.TEST.value():
                environment = CredentialMessage.TEST_ENVIRONMENT.value()
                break
            default:
                environment = "$loginFormat ${CredentialMessage.OTHER_ENVIRONMENT.value()}"
        }
        return environment
    }

    /**
     * Gets the path of credentials.dat file, by default it gets home directory
     * @return the path of credentials.dat file
     */
    private String getCredentialsFilePath() {
        String credentialsFilePath = CredentialManagerInput.HOME_PATH
        if (location == PROJECT_OPTION) {
            credentialsFilePath = CredentialManagerInput.PROJECT_PATH
        }
        return credentialsFilePath
    }
}