/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc

import com.sforce.ws.ConnectorConfig

/**
 * An abstract representation that wraps the API to web services of salesforce.
 */
abstract class ForceAPI {
    Credential credential
    Session session
    Connector connector
    ConnectorConfig connectorConfig


    /**
     * Constructs a connector config to create api connection from a user credential
     * @param credential the user credential of a salesforce org
     */

    ForceAPI(Credential credential) {
        this(credential, new Connector(credential.loginFormat))
    }

    /**
     * Constructs a connector config to create api connection from a user credential and a connector
     * @param credential the user credential of a salesforce org
     * @param connector the connector to login and logout to the web services of salesforce
     */
    ForceAPI(Credential credential, Connector connector) {
        this.credential = credential
        this.connector = connector
        createConnector()
        createConnection()
    }

    /**
     * Creates a connector config to create api connection
     */
    void createConnector() {
        session = connector.login(credential)
        connectorConfig = connector.createConnectorConfig(session.sessionId, getUrl())
    }

    /**
     * Gets the server url, it is implemented for extended class
     * @return the server url
     */
    abstract String getUrl()

    /**
     * Creates a specific api connection, it is implemented for extended class
     */
    abstract void createConnection()
}
