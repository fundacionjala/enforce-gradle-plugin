/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.streaming

import spock.lang.Specification

class StreamingClientTest extends Specification {

    def "Should create a new streaming client" (){
        expect:
        String token = 'AdsfFSFDf4757745654132212'
        String serverUrl = 'http://n14.salesforce.com'
        StreamingClient streamingClient = new StreamingClient(token, serverUrl)
        streamingClient.client != null
    }

    def "Should get a salesforce endpoint" (){
        given:
        String token = 'AdsfFSFDf4757745654132212'
        String serverUrl = 'http://n14.salesforce.com'
        StreamingClient streamingClient = new StreamingClient(token, serverUrl)
        when:
        def endpoint = streamingClient.getSalesforceStreamingEndpoint(serverUrl)
        then:
        endpoint == 'http://n14.salesforce.com/cometd/32.0'
    }

}
