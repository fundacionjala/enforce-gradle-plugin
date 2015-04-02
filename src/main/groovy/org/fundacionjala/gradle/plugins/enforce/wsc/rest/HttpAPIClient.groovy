/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc.rest

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.GET

/**
 * A wrapper around HTTP Builder that handles errors and makes requests to the tooling API with Rest
 */
class HttpAPIClient {

    public  static final String URL_COLON = ":"
    public  static final String URL_DOUBLE_SLASH = "//"
    private static final String OAUTH_NAME = "OAuth"

    private final String PATH_QUERY = '/services/data/v32.0/tooling/query/'
    private final String PATH_RUN_TEST = '/services/data/v32.0/tooling/runTestsAsynchronous/'

    private String authorization
    private String host
    private String oauthToken
    private final int TEXT_FORMAT = 2

    /**
     * Crates new http API client from host, oauth token / session Id and http client instance
     * @param host the host server url
     * @param oauthToken the oauth token / session Id
     */
    public HttpAPIClient(String host, String oauthToken) {
        this.host = host
        this.oauthToken = oauthToken

        authorization = "${OAUTH_NAME} ${oauthToken}"

    }

    /**
     * Executes a salesforce object query language using tooling API
     * @param soql the salesforce object query language
     * @return the result from server in JSON format
     */
    public String executeQuery(String soql) {

        String resultQuery = ""
        HTTPBuilder http = new HTTPBuilder(getEndPoint(host))
        http.request( GET, JSON ) {
            uri.path = PATH_QUERY
            uri.query = [q: """${soql}""" ]
            headers.Authorization = authorization

            response.success = { resp, json ->
                resultQuery = json.toString(TEXT_FORMAT)
            }

            response.failure = { resp ->
                String message = "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
                throw new Exception(message)
            }
        }
        return resultQuery
    }

    /**
     * Makes  run unit test the async way according to class ids
     * @param ids is a string contain ids class separate by commas
     */
    public void runTest(String ids) {
        HTTPBuilder http = new HTTPBuilder(getEndPoint(host))
        http.request( GET, TEXT ) {
            headers.Authorization = authorization
            uri.path = PATH_RUN_TEST
            uri.query = [classids: """${ids}""" ]
            response.failure = { resp ->
                throw new Exception("Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}")
            }
        }
    }

    /**
     * Gets the endpoint from server url
     * @param serverUrl server url from login result
     */
    public static String getEndPoint(String serverUrl) {
        URL soapEndpoint = new URL(serverUrl)
        StringBuilder endpoint = new StringBuilder()
                .append(soapEndpoint.getProtocol())
                .append("${URL_COLON}${URL_DOUBLE_SLASH}")
                .append(soapEndpoint.getHost())

        if (soapEndpoint.getPort() > 0){
            endpoint.append("${URL_COLON}").append(soapEndpoint.getPort())
        }

        return endpoint.toString()
    }
}
