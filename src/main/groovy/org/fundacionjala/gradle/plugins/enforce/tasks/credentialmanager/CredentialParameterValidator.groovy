/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager

import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialMessage
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialParameter
import org.fundacionjala.gradle.plugins.enforce.exceptions.CredentialException
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType
import org.gradle.api.Project

/**
 * Validates the parameters of a credential sent to run a task
 */
class CredentialParameterValidator {
    /**
     * Gets a credential with parameters inserted
     * @param project is type project
     * @param credentialType is type String can be 'normal' or 'encrypted'
     * @return a credential
     */
    public static Credential getCredentialInserted(Project project, String credentialType) {
        Credential credential = new Credential()
        credential.id = project.properties[CredentialMessage.ID_PARAM.value()]
        credential.username = project.properties[CredentialParameter.USER_NAME.value()]
        credential.password = project.properties[CredentialParameter.PASSWORD.value()]
        credential.token = getToken(project)
        String loginInserted = project.properties[CredentialMessage.LOGIN.value()] ?: null
        credential.loginFormat = getLoginType(loginInserted)
        credential.type = credentialType
        return credential
    }

    /**
     * Gets a token it is empty by default.
     * @param tokenInserted is type String
     * @return a token
     */
    private static String getToken(Project project) {
        String token = ''
        if (Util.isValidProperty(project, CredentialMessage.TOKEN.value())) {
            token = project.properties[CredentialMessage.TOKEN.value()]
        }
        return token
    }

    /**
     * Validates credential fields
     * @return true if fields are validate
     */
    public static boolean validateFieldsCredential(Project project) {
        CredentialParameter.each { CredentialParameter parameter ->
            String value = parameter.value()
            if (Util.isEmptyProperty(project, value)) {
                throw new CredentialException("${value} ${CredentialMessage.MESSAGE_EMPTY_PARAMETER.value()}")
            }
            if (!Util.isValidProperty(project, value)) {
                throw new CredentialException("${value} ${CredentialMessage.MESSAGE_EXCEPTION_INVALID_PARAMETERS.value()}")
            }
        }
        if (!Util.validEmail(project.properties[CredentialParameter.USER_NAME.value()].toString())) {
            throw new CredentialException("${CredentialMessage.MESSAGE_EXCEPTION_USER_NAME.value()} '${project.properties[CredentialParameter.USER_NAME.value()].toString()}'")
        }
        return true
    }

    /**
     * Verifies if there are parameters to add a new credential
     * @param project is type Project gradle
     * @return true if there are parameters
     */
    public static boolean haveParameters(Project project) {
        return project.hasProperty(CredentialParameter.USER_NAME.value()) || project.hasProperty(CredentialParameter.PASSWORD.value()) ||
                project.hasProperty(CredentialMessage.TOKEN.value()) || project.hasProperty(CredentialMessage.LOGIN.value())
    }

    /**
     * Verifies if exist a username property
     * @param project is type project
     * @return true if exist
     */
    public static boolean hasUserName(Project project) {
        return project.hasProperty(CredentialParameter.USER_NAME.value())
    }

    /**
     * Verifies if exist a id credential property
     * @param project is type project
     * @return true if exist
     */
    public static boolean hasIdCredential(Project project) {
        return project.hasProperty(CredentialMessage.ID_PARAM.value())
    }

    /**
     * Gets a login type by default is DEV
     * @return a login type
     */
    public static String getLoginType(String loginInserted) {
        return loginInserted ?: LoginType.DEV.value()
    }

    /**
     * Gets a credential type by default is encrypted
     * @param option is type String can be 'y' or 'n'
     * @return a credential type (encrypted or not)
     */
    public static String getCredentialType(String option) {
        String credentialType = CredentialMessage.ENCRYPTED.value()
        if (option == CredentialMessage.OPTION_NO.value()) {
            credentialType = CredentialMessage.NORMAL.value()
        }
        return credentialType
    }
}
