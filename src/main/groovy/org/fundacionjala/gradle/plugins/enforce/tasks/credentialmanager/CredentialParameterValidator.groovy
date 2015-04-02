/*
 * Copyright (c) Fundación Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager

import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialParameter
import org.fundacionjala.gradle.plugins.enforce.exceptions.CredentialException
import org.gradle.api.Project
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialMessage
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType

/**
 * Validates the parameters of a credential sent to run a task
 */
class CredentialParameterValidator {
    /**
     * Returns a credential using parameters sent
     */
    static Credential getCredentialInserted(Project project) {
        Credential credential = new Credential()
        credential.id = project.properties[CredentialMessage.ID_PARAM.value()]
        credential.username = project.properties[CredentialParameter.USER_NAME.value()]
        credential.password = project.properties[CredentialParameter.PASSWORD.value()]
        credential.token = project.properties[CredentialParameter.TOKEN.value()]
        credential.loginFormat = getLoginType(project)
        credential.type = CredentialMessage.NORMAL.value()
        return credential
    }

    /**
     * Validates credential fields
     * @return true if fields are validate
     */
    static boolean validateFieldsCredential(Project project) {
        CredentialParameter.each { CredentialParameter parameter ->
            String value = parameter.value()
            if (Util.isEmptyProperty(project, value)) {
                throw new CredentialException("${value} ${CredentialMessage.MESSAGE_EMPTY_PARAMETER.value()}")
            }
            if(!Util.isValidProperty(project, value)) {
                throw new CredentialException("${value} ${CredentialMessage.MESSAGE_EXCEPTION_INVALID_PARAMETERS.value()}")
            }
        }
        if (!Util.validEmail(project.properties[CredentialParameter.USER_NAME.value()].toString())) {
            throw new CredentialException( "${CredentialMessage.MESSAGE_EXCEPTION_USER_NAME.value()} '${project.properties[CredentialParameter.USER_NAME.value()].toString()}'")
        }
        return true
    }

    /**
     * Verifies if there are parameters to add a new credential
     * @param project is type Project gradle
     * @return true if there are parameters
     */
    static boolean haveParameters(Project project) {
        return project.hasProperty(CredentialParameter.USER_NAME.value()) || project.hasProperty(CredentialParameter.PASSWORD.value()) ||
               project.hasProperty(CredentialParameter.TOKEN.value()) || project.hasProperty(CredentialMessage.LOGIN.value())
    }

    /**
     * Verifies if exist a username property
     * @param project is type project
     * @return true if exist
     */
    static boolean hasUserName(Project project) {
        return project.hasProperty(CredentialParameter.USER_NAME.value())
    }

    /**
     * Verifies if exist a id credential property
     * @param project is type project
     * @return true if exist
     */
    static boolean hasIdCredential(Project project) {
        return project.hasProperty(CredentialMessage.ID_PARAM.value())
    }

    /**
     * Gets a login type by default is DEV
     * @return a login type
     */
    static String getLoginType(Project project) {
        def loginType = LoginType.DEV.value()
        if(project.properties[CredentialMessage.LOGIN.value()] == LoginType.TEST.value()) {
            loginType = LoginType.TEST.value()
        }
        return loginType
    }

    /**
     * Gets a credential type by default is encrypted
     * @return a credential type (encrypted or not)
     */
    static String getCredentialType(Project project) {
        def credentialType = CredentialMessage.ENCRYPTED.value()
        if(project.properties[CredentialMessage.ENCRYPTED.value()] == CredentialMessage.OPTION_NO.value()) {
            credentialType = CredentialMessage.NORMAL.value()
        }
        return credentialType
    }
}
