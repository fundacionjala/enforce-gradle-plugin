/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.testselector

import org.fundacionjala.gradle.plugins.enforce.wsc.rest.IArtifactGenerator

class ArtifactGeneratorMock implements IArtifactGenerator {

    public String executeQuery(String soql) {
        String queryResult = ""
        if (soql.contains("ContainerAsyncRequest")) {
            queryResult = '{"records": [{"State": "Completed"}]}'
        } else if (soql.contains("ApexClassMember")) {
            queryResult = '{"records":[{"SymbolTable": [{"externalReferences": [{"namespace":"", "name":"Class1"}]}], "FullName":"TestClass1"}]}'
        }
        return queryResult
    }

    public Map createContainer(String containerName) {
        Map toReturn = [:]
        toReturn.put("Id", "000000000000000")
        toReturn.put("isNew", true)
        return toReturn
    }

    public void deleteContainer(String containerName) {

    }

    public ArrayList<String> createApexClassMember(String containerId, ArrayList<String> classNameList) {
        return ["none"]
    }

    public String createContainerAsyncRequest(String containerId) {
        return "none"
    }
}
