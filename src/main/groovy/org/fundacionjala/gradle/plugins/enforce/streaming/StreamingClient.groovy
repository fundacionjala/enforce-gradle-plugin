/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.streaming

import org.cometd.bayeux.client.ClientSessionChannel.MessageListener
import org.cometd.client.BayeuxClient
import org.cometd.client.transport.ClientTransport
import org.cometd.client.transport.LongPollingTransport
import org.eclipse.jetty.client.ContentExchange
import org.eclipse.jetty.client.HttpClient
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.HttpAPIClient

/**
 * The implementation of Streaming API client to subscribe to the channel and receive notifications
 */
class StreamingClient {

    private static final String STREAMING_ENDPOINT_URI = "/cometd/32.0"
    private static final String REQUEST_HEADER_AUTH = "Authorization"
    private static final String REQUEST_HEADER_OAUTH = "OAuth"
    private static final int CONNECTION_TIMEOUT = 20000   // milliseconds
    private static final int READ_TIMEOUT = 120000 // milliseconds
    private static final int HANDSHAKE_TIMEOUT = 10000
    BayeuxClient client
    String sessionId
    String serverUrl

    /**
     * Constructs a streaming client with the sessionId and server url
     * @param sessionId session id from login result
     * @param serverUrl server url from login result
     */
    StreamingClient(String sessionId, String serverUrl) {
        this.sessionId = sessionId
        this.serverUrl = serverUrl
        client = createClient(sessionId, serverUrl)
    }

    /**
     * Starts the streaming connection between client and server
     */
    void start(){
        client.handshake()
        println "Waiting reply from server: ${HttpAPIClient.getEndPoint(serverUrl)}"
        boolean handshaken = client.waitFor(HANDSHAKE_TIMEOUT, BayeuxClient.State.CONNECTED)
        if (!handshaken) {
            throw new Exception('Connection failed: Stream setup failed')
        }
    }

    /**
     * Adds a listener to this channel
     * @param channel specific channel name
     * @param messageListener the listener to add
     */
    public void addMessageListener(String channel, MessageListener messageListener) {
        client.getChannel(channel).addListener(messageListener)
    }

    /**
     * Subscribes the given listener to receive messages sent to this channel
     * @param channel specific channel name
     * @param messageListener the listener to register and invoke when a message arrives on this channel
     */
    public void subscribeOnChannel(String channel, MessageListener messageListener) {
        client.getChannel(channel).subscribe(messageListener)
    }

    /**
     * Create a new client instance with the sessionId and server url
     * @param sessionId session id from login result
     * @param serverUrl server url from login result
     * @return the Bayeux client
     * @throws Exception if the http client can not start
     */
    private BayeuxClient createClient(String sessionId, String serverUrl) throws Exception {
        HttpClient httpClient = new HttpClient()
        httpClient.setConnectTimeout(CONNECTION_TIMEOUT)
        httpClient.setTimeout(READ_TIMEOUT)
        httpClient.start()

        String endpoint = HttpAPIClient.getEndPoint(serverUrl)
        Map<String, Object> options = new HashMap<String, Object>()
        options.put(ClientTransport.TIMEOUT_OPTION, READ_TIMEOUT)
        LongPollingTransport transport = new LongPollingTransport(
                options, httpClient) {

            @Override
            protected void customize(ContentExchange exchange) {
                super.customize(exchange)
                exchange.addRequestHeader(REQUEST_HEADER_AUTH, "${REQUEST_HEADER_OAUTH} ${sessionId}")
            }
        }

        BayeuxClient client = new BayeuxClient(getSalesforceStreamingEndpoint(endpoint), transport)

        return client
    }

    /**
     * Gets a Salesforce streaming endpoint
     * @param endpoint  Endpoint gotten from server url
     * @throws MalformedURLException if the url is malformed
     */
    public String getSalesforceStreamingEndpoint(String endpoint) throws MalformedURLException {
        return new URL("${endpoint}${STREAMING_ENDPOINT_URI}").toExternalForm()
    }
}
