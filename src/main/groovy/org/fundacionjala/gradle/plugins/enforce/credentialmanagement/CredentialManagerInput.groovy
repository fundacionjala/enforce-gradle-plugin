/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.credentialmanagement

import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import org.fundacionjala.gradle.plugins.enforce.exceptions.CredentialException
import org.fundacionjala.gradle.plugins.enforce.utils.AnsiColor
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType

import java.nio.file.Paths

class CredentialManagerInput {
    private final String HOME_PATH = Paths.get(System.properties['user.home'].toString(), 'credentials.dat').toString()
    private final String PROJECT_PATH = Paths.get(System.getProperty("user.dir"), 'credentials.dat').toString()
    private final String PROJECT = 'project'
    private CredentialManager credentialManager
    private String pathSecretKey
    private String option

    public String locationInput
    public String idInput
    public String userNameInput
    public String passwordInput
    public String tokenInput
    public String loginTypeInput
    public String typeInput = CredentialMessage.ENCRYPTED.value()
    public Console console
    public boolean finished
    public  String pathCredentials

    /**
     * Constructs an object of credential manager
     * @param location can be project or home to management credentials
     * @param pathSecretKey is directory of secretKey
     */
    CredentialManagerInput(String location, String pathSecretKey) {
        this.pathCredentials = location == PROJECT? PROJECT_PATH:HOME_PATH
        this.pathSecretKey = pathSecretKey
        credentialManager = new CredentialManager(pathCredentials, pathSecretKey)
        typeInput = CredentialMessage.OPTION_YES.value()
        option = CredentialMessage.OPTION_NO.value()
        console = System.console()
        finished = true
    }

    /**
     * Adds a new credential by console
     */
    public void addCredentialByConsole() {
        try {
            pathCredentials = getCredentialFile()
            CredentialManager addCredentialByConsole = new CredentialManager(pathCredentials, pathSecretKey)
            validCredentialFields()
            addCredentialByConsole.addCredential(getCredentialInserted())
            print AnsiColor.ANSI_GREEN.value()
            print CredentialMessage.MESSAGE_ADD_SUCCESSFULLY.value()
            print AnsiColor.ANSI_RESET.value()
            finished = false

        } catch (Exception exception) {
            print AnsiColor.ANSI_RED.value()
            println exception
            print AnsiColor.ANSI_RESET.value()
            showOption()
        }
    }

    /**
     * Updates a credential by console
     */
    public void updateCredentialByConsole() {
        try {
            updateCredential()
            print AnsiColor.ANSI_GREEN.value()
            print CredentialMessage.MESSAGE_UPDATE_SUCCESSFULLY.value()
            print AnsiColor.ANSI_RESET.value()
            finished = false
        } catch (Exception exception) {
            print AnsiColor.ANSI_RED.value()
            println exception
            print AnsiColor.ANSI_RESET.value()
            showOption()
        }
    }

    /**
     * Validates fields entered to add credential
     */
    public void validCredentialFields() {
        showIdInput()
        if (hasCredential(idInput)) {
            throw new CredentialException("${CredentialMessage.MESSAGE_ID_CREDENTIAL_EXIST.value()} '${idInput}'")
        }
        showUserNameInput()
        if (!Util.validEmail(userNameInput)) {
            throw new CredentialException("${CredentialMessage.MESSAGE_EXCEPTION_USER_NAME.value()} '${userNameInput}'")
        }
        showConsoleInputs()
        showConsoleTypeInput()
        if (!validateFields()) {
            throw new CredentialException(CredentialMessage.MESSAGE_EXCEPTION_EMPTY_FILES.value())
        }
        typeInput = getCredentialType()
    }

    /**
     * Adds a credential
     */
    public void addCredential(Credential credential) {
        credentialManager.addCredential(credential)
    }

    /**
     * Updates a credential
     */
    public void updateCredential(Credential credential) {
        credentialManager.updateCredential(credential)
    }

    /**
     * Updates a credential by console
     */
    public void updateCredential() {
        pathCredentials = getCredentialFile()
        CredentialManager updateCredentialByConsole = new CredentialManager(pathCredentials, pathSecretKey)
        showIdInput()
        if (!hasCredential(idInput)) {
            throw new CredentialException("${idInput} ${CredentialMessage.MESSAGE_ID_CREDENTIAL_DOES_NOT_EXIST.value()}")
        }
        showUserNameInput()
        if (!Util.validEmail(userNameInput)) {
            throw new CredentialException("${CredentialMessage.MESSAGE_EXCEPTION_USER_NAME.value()} '${userNameInput}'")
        }
        showConsoleInputs()
        if (!validateFields()) {
            throw new CredentialException(CredentialMessage.MESSAGE_EXCEPTION_EMPTY_FILES.value())
        }
        typeInput = updateCredentialByConsole.getCredentialById(idInput).type
        updateCredentialByConsole.updateCredential(getCredentialInserted())
    }

    /**
     * Gets a credential to update
     * @param credentialId is type String
     * @return a credential
     */
    Credential getCredentialToUpdate(String credentialId) {
        return credentialManager.getCredentialById(credentialId)
    }

    /**
     * Sets credential fields
     */
    public void showConsoleInputs() {
        passwordInput = console.readLine(CredentialMessage.PASSWORD.value()).toString()
        tokenInput = console.readLine(CredentialMessage.TOKEN_OPTION.value()).toString()
        loginTypeInput = console.readLine(CredentialMessage.LOGIN_TYPE.value()).toString()
    }

    /**
     * Sets a type credential
     */
    public void showConsoleTypeInput() {
        typeInput = console.readLine(CredentialMessage.TYPE.value()).toString()
    }

    /**
     * Sets a id credential
     */
    public void showIdInput() {
        idInput = console.readLine(CredentialMessage.ID.value()).toString()
    }

    /**
     * Sets user name credential
     */
    public void showUserNameInput() {
        userNameInput = console.readLine(CredentialMessage.USER_NAME.value()).toString()
    }

    /**
     * Show option to continue
     */
    public void showOption() {
        print AnsiColor.ANSI_BLUE.value()
        println CredentialMessage.MESSAGE_QUESTION_TRY_AGAIN.value()
        println AnsiColor.ANSI_RESET.value()
        option = System.console().readLine(' : ')
        if (option != CredentialMessage.OPTION_YES.value()) {
            finished = false
        }
    }

    /**
     * Creates a new credential with inputs fields
     * @return a credential
     */
    public Credential getCredentialInserted() {
        Credential credentialInserted = new Credential()
        credentialInserted.id = idInput
        credentialInserted.username = userNameInput
        credentialInserted.password = passwordInput
        credentialInserted.token = tokenInput
        credentialInserted.loginFormat = loginTypeInput?:LoginType.DEV.value()
        credentialInserted.type = typeInput
        return credentialInserted
    }

    /**
     * Validates that input fields
     * @return true if are validates
     */
    public boolean validateFields() {
        return !idInput.isEmpty() && !passwordInput.isEmpty()
    }

    /**
     * Verifies if exist an id credential
     * @param idCredential is id credential
     * @return true if there is a id credential
     */
    public boolean hasCredential(String idCredential) {
        File credentialsFile = new File(pathCredentials)
        if (!credentialsFile.exists() || credentialsFile.getText().isEmpty()) {
            return false
        }
        LazyMap credentials = new JsonSlurper().parseText(credentialsFile.getText())
        return credentials[idCredential]
    }

    /**
     * Gets a credential type by default is encrypted
     * @return a credential type (encrypted or not)
     */
    public String getCredentialType() {
        String credentialType = CredentialMessage.ENCRYPTED.value()
        if(typeInput == CredentialMessage.OPTION_NO.value()) {
            credentialType = CredentialMessage.NORMAL.value()
        }
        return credentialType
    }

    /**
     * Gets credentials.dat file path
     * @return a credentials.dat path
     */
    private String getCredentialFile() {
        locationInput = console.readLine(CredentialMessage.LOCATION.value()).toString()
        return locationInput == PROJECT? PROJECT_PATH:HOME_PATH
    }
}