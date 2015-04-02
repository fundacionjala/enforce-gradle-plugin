/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc


import com.sforce.soap.partner.LoginResult
import com.sforce.soap.partner.PartnerConnection
import com.sforce.ws.ConnectorConfig

/**
 * This class  is able to login and logout to access to web services of salesforce with the user credential provided
 */
class Connector {
    public static final String API_VERSION = '32.0'
    private static final String LOGIN_URL = 'https://%s/services/Soap/u/%s'
    private static final String DEFAULT_LOGIN_BASE_URL = '%s.salesforce.com'
    private static final String EXCEPTION_PROPERTY_NAME = 'exceptionMessage'
    private String loginUrl
    private PartnerConnection partnerConnection
    Session session
    String apiVersion

    /**
     * Constructs a connector with the login url and partner connection
     * @param loginFormat contains the type of logging to salesForce org
     */
    Connector(String loginFormat, String apiVersion) {
        this.apiVersion = apiVersion
        String baseUrl = String.format(DEFAULT_LOGIN_BASE_URL, loginFormat)
        loginUrl = String.format(LOGIN_URL, baseUrl, this.apiVersion)
        ConnectorConfig connectorConfig = createConnectorConfig(null, loginUrl)
        partnerConnection = new PartnerConnection(connectorConfig)
    }

    /**
     * Constructs a connector with the login url and partner connection with the API version by default
     * @param loginFormat contains the login type to a salesforce org
     */
    Connector(String loginFormat) {
        this.apiVersion = apiVersion
        String baseUrl = String.format(DEFAULT_LOGIN_BASE_URL, loginFormat)
        loginUrl = String.format(LOGIN_URL, baseUrl, API_VERSION)
        ConnectorConfig connectorConfig = createConnectorConfig(null, loginUrl)
        partnerConnection = new PartnerConnection(connectorConfig)
    }

    /**
     * Login to access web services of salesforce with a user credential
     * @param credential the user credential of a salesforce org
     * @return a session with server url, session id and the user info
     */
    Session login(Credential credential) {
        try {
            LoginResult loginResult = partnerConnection.login(credential.username, credential.getPasswordToken())
            session = new Session(loginResult)
            return session
        }catch (Exception exception) {
            String message = exception.message
            if(exception.hasProperty(EXCEPTION_PROPERTY_NAME) && exception.exceptionMessage){
                message = exception.exceptionMessage
            }
            throw new Exception(message, exception)
        }
    }

    /**
     * Logout if exist an open session
     */
    void logout() {
        if (session) {
            ConnectorConfig connectorConfig = createConnectorConfig(session.sessionId, session.serverUrl)
            partnerConnection = new PartnerConnection(connectorConfig)
            partnerConnection.logout()
        } else {
            println 'No session is open'
        }
    }

    /**
     * Creates a connector config with the session id and server url
     * @param sessionId the session id provided by salesforce
     * @param serverUrl the server url provided by salesforce
     * @return a connector config to create api connection
     */
    ConnectorConfig createConnectorConfig(String sessionId, String serverUrl) {
        ConnectorConfig connectorConfig = new ConnectorConfig()
        if (sessionId) {
            connectorConfig.setSessionId(sessionId)
        }
        connectorConfig.setAuthEndpoint(serverUrl)
        connectorConfig.setServiceEndpoint(serverUrl)
        connectorConfig.setManualLogin(true)
        return connectorConfig
    }

    void setPartnerConnection(PartnerConnection partnerConnection) {
        this.partnerConnection = partnerConnection
    }

    /**
     * Gets the partner server url if exist an open session
     * @return a partner server url
     */
    String getPartnerServerUrl() {
        String url
        if (session) {
            url = session.metadataServerUrl.replaceAll("/m/", "/u/")
        }
        return url
    }

    /**
     * Gets the apex server url if exist an open session
     * @return a apex server url
     */
    String getApexServerUrl() {
        String url
        if (session) {
            url = session.metadataServerUrl.replaceAll("/m/", "/s/")
        }
        return url
    }

    /**
     * Gets the tooling server url if exist an open session
     * @return a tooling server url
     */
    String getToolingServerUrl() {
        String url
        if (session) {
            url = session.metadataServerUrl.replaceAll("/m/", "/T/")
        }
        return url
    }

    /**
     * Gets the metadata server url if exist an open session
     * @return a partner server url
     */
    String getMetadataServerUrl() {
        String url
        if (session) {
            url = session.metadataServerUrl
        }
        return url
    }

    /**
     * Gets the complete url
     * @return a string  the complete url to login
     */
    static String getUrlService(LoginType loginType, String apiVersion) {
        String baseUrl = String.format(DEFAULT_LOGIN_BASE_URL, loginType.value())
        String loginUrl = String.format(LOGIN_URL, baseUrl, apiVersion)
        return loginUrl
    }
}
