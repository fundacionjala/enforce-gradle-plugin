/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc.rest

import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.DELETE
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST

/**
 * A wrapper around HTTP Builder that handles errors and makes requests to the tooling API with Rest
 */
class HttpAPIClient implements IArtifactGenerator {

    public  static final String URL_COLON = ":"
    public  static final String URL_DOUBLE_SLASH = "//"
    private static final String OAUTH_NAME = "OAuth"

    private final String PATH_QUERY = '/services/data/v32.0/tooling/query/'
    private final String PATH_RUN_TEST = '/services/data/v32.0/tooling/runTestsAsynchronous/'
    private final String PATH_METADATA_CONTAINER = '/services/data/v32.0/tooling/sobjects/MetadataContainer/'
    private final String PATH_APEXCLASSMEMBER_CONTAINER = '/services/data/v32.0/tooling/sobjects/ApexClassMember/'
    private final String PATH_CONTAINERASYNCREQUEST_CONTAINER = '/services/data/v32.0/tooling/sobjects/ContainerAsyncRequest/'

    private final String SELECT_CONTAINER_QUERY = 'SELECT ID FROM MetadataContainer WHERE Name=\'%1$s\''
    private final String SELECT_APEX_CLASS_QUERY = 'SELECT Id, Body FROM ApexClass WHERE name IN (\'%1$s\')'

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

    public Map createContainer(String containerName) {
        try {
            String containerId
            Map toReturn = [:]
            HTTPBuilder http = new HTTPBuilder(getEndPoint(host))

            JsonSlurper jsonSlurper = new JsonSlurper()
            Object resultJson = jsonSlurper.parseText(executeQuery(sprintf(SELECT_CONTAINER_QUERY, [containerName])))
            if(resultJson.records.size() > 0) {
                toReturn.put("Id", resultJson.records[0].Id)
                toReturn.put("isNew", false)
            } else {
                http.request( POST, JSON ) {
                    uri.path = PATH_METADATA_CONTAINER
                    body = [name: containerName ]
                    headers.Authorization = authorization
                    response.success = { resp, json ->
                        toReturn.put("Id", jsonSlurper.parseText(json.toString(TEXT_FORMAT)).id.toString())
                        toReturn.put("isNew", true)
                    }
                    response.failure = { resp ->
                        String message = "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
                        throw new Exception(message)
                    }
                }
            }
            return toReturn
        } catch (e){
            throw new Exception("createContainer()->Unexpected error: ${e.toString()}")
        }
    }

    public void deleteContainer(String containerName) {
        try {
            HTTPBuilder http = new HTTPBuilder(getEndPoint(host))
            JsonSlurper jsonSlurper = new JsonSlurper()
            Object resultJson = jsonSlurper.parseText(executeQuery(sprintf(SELECT_CONTAINER_QUERY, [containerName])))
            if(resultJson.records.size() > 0) {
                http.request( DELETE, TEXT ) {
                    uri.path = PATH_METADATA_CONTAINER + "${resultJson.records[0].Id}"
                    headers.Authorization = authorization
                }
            }
        } catch (e){
            throw new Exception("deleteContainer()->Unexpected error: ${e.toString()}")
        }
    }

    public ArrayList<String> createApexClassMember(String containerId, ArrayList<String> classNameList) {
        try {
            ArrayList<String> apexClassMember = []
            HTTPBuilder http = new HTTPBuilder(getEndPoint(host))

            JsonSlurper jsonSlurper = new JsonSlurper()
            Object resultJson = jsonSlurper.parseText(executeQuery(sprintf(SELECT_APEX_CLASS_QUERY, [classNameList.join("', '")])))
            if(resultJson.records.size() > 0) {
                resultJson.records.each { classRecord ->
                    http.request(POST, JSON) {
                        uri.path = PATH_APEXCLASSMEMBER_CONTAINER
                        body = [ContentEntityId: classRecord.Id, Body: classRecord.Body, MetadataContainerId: containerId] //TODO: is it possible send all in one?
                        headers.Authorization = authorization
                        response.success = { resp, json ->
                            apexClassMember.add(jsonSlurper.parseText(json.toString(TEXT_FORMAT)).id.toString())
                        }
                        response.failure = { resp ->
                            String message = "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
                            throw new Exception(message)
                        }
                    }
                }
            }
            return apexClassMember
        } catch (e){
            throw new Exception("createApexClassMember()->Unexpected error: ${e.toString()}")
        }
    }

    public String createContainerAsyncRequest(String containerId) {
        try {
            String containerAsyncRequest = ""
            HTTPBuilder http = new HTTPBuilder(getEndPoint(host))
            JsonSlurper jsonSlurper = new JsonSlurper()
            http.request( POST, JSON ) {
                uri.path = PATH_CONTAINERASYNCREQUEST_CONTAINER
                body = [MetadataContainerId: containerId, IsCheckOnly: 1]
                headers.Authorization = authorization
                response.success = { resp, json ->
                    containerAsyncRequest = jsonSlurper.parseText(json.toString(TEXT_FORMAT)).id.toString()
                }
                response.failure = { resp ->
                    String message = "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
                    throw new Exception(message)
                }
            }
            return containerAsyncRequest;
        } catch (e){
            throw new Exception("createContainerAsyncRequest()->Unexpected error: ${e.toString()}")
        }
    }
}
