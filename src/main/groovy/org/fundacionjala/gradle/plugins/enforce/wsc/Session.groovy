/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc

import com.sforce.soap.partner.GetUserInfoResult
import com.sforce.soap.partner.LoginResult
import groovy.transform.ToString

/**
 * The Session class wraps the values of a login result.
 */
@ToString(includeNames = true)
class Session {
    String sessionId
    String serverUrl
    String userId
    String userFullName
    String userEmail
    String metadataServerUrl

    Session(LoginResult loginResult) {
        sessionId = loginResult.sessionId
        serverUrl = loginResult.serverUrl
        metadataServerUrl = loginResult.metadataServerUrl
        GetUserInfoResult userInfo = loginResult.getUserInfo()
        userId = userInfo.userId
        userFullName = userInfo.userFullName
        userEmail = userInfo.userEmail
    }
}
