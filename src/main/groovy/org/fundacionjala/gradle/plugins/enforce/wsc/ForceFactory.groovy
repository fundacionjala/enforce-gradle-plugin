/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc

import org.fundacionjala.gradle.plugins.enforce.wsc.soap.ApexAPI
import org.fundacionjala.gradle.plugins.enforce.wsc.soap.MetadataAPI

/**
 * Factory class for the APIs of web services Salesforce.
 */
class ForceFactory {

    /**
     * Creates a salesforce API of the specified type with the user credential provided
     * @param forceApiType the salesforce API type
     * @param credential the user credential of a salesforce org
     * @return the specified salesforce API
     */
    public static ForceAPI getForceAPI(ForceApiType forceApiType, Credential credential) {

        ForceAPI forceAPI
        switch (forceApiType) {
            case ForceApiType.APEX:
                forceAPI = new ApexAPI(credential)
                break
            case ForceApiType.METADATA:
                forceAPI = new MetadataAPI(credential)
                break
            default:
                forceAPI = new ApexAPI(credential)
        }
        return forceAPI
    }
}
