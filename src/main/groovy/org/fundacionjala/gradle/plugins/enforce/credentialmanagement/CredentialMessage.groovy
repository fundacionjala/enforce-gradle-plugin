/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.credentialmanagement

enum CredentialMessage {
    MESSAGE_EXCEPTION_ENCRYPTING("A problem occurred when you was encryption a credential"),
    MESSAGE_EXCEPTION_DECRYPTING("A problem occurred when you was decrypting a credential"),
    MESSAGE_EXCEPTION_SECRET_KEY("Invalid secretKey, it should have 16 characters "),
    MESSAGE_EXCEPTION_CREDENTIAL("Credential cannot be null"),
    MESSAGE_EXCEPTION_ARRAY_CREDENTIALS_FILE_NULL("Credentials paths cannot be null"),
    MESSAGE_FILE_INVALID("The content credentials file isn't valid"),
    MESSAGE_FILE_EMPTY("The content credentials is empty"),
    MESSAGE_FILE_DOES_NOT_EXIST("File credential not found"),
    MESSAGE_ID_CREDENTIAL_EXIST("This credential already exist"),
    MESSAGE_ID_CREDENTIAL_DOES_NOT_EXIST(" doesn't exist"),
    MESSAGE_EXCEPTION_USER_NAME("User name invalid"),
    MESSAGE_EXCEPTION_EMPTY_FILES("Cannot insert empty fields"),
    MESSAGE_EMPTY_PARAMETER("parameter cannot be empty"),
    MESSAGE_EXCEPTION_INVALID_PARAMETERS("parameter invalid"),
    MESSAGE_EXCEPTION_CREDENTIAL_NOT_FOUND("Credential not found"),
    MESSAGE_ADD_SUCCESSFULLY('Credential was added successfully'),
    MESSAGE_UPDATE_SUCCESSFULLY('Credential was updated successfully'),
    MESSAGE_QUESTION_TRY_AGAIN('Do you want to try again (y/n)?'),
    ADD_CREDENTIAL_DESCRIPTION("You can add a credential"),
    GET_CREDENTIALS_DESCRIPTION("You can obtain a credential registered"),
    CREDENTIAL_MANAGER_GROUP("Credential Manager"),
    UPDATE_CREDENTIAL_DESCRIPTION("You can update a credential"),
    LOCATION('\nCredential location (home/project by default is home):'),
    ID('\nId:'),
    USER_NAME('UserName(example@email.com):'),
    PASSWORD('Password:'),
    TOKEN_OPTION('Token:'),
    TYPE('Encrypt credential(y/n, by default is encrypted):'),
    LOGIN_TYPE('Login type (by default is login):'),
    ID_PARAM('id'),
    OPTION_YES('y'),
    OPTION_NO('n'),
    ENCRYPTED('encrypted'),
    NORMAL('normal'),
    LOGIN('login'),
    TEST('test'),
    DEVELOPER_ENVIRONMENT('Production/Developer'),
    TEST_ENVIRONMENT('Sandbox'),
    OTHER_ENVIRONMENT('(Specified)'),
    DEFAULT_CREDENTIAL_NAME('default'),
    TOKEN('token')

    CredentialMessage(String value) {
        this.value = value
    }

    private final String value

    public String value() {
        return value
    }
}


