/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc.rest

public interface IArtifactGenerator {

    String executeQuery(String soql)

    Map createContainer(String containerName)

    void deleteContainer(String containerName)

    ArrayList<String> createApexClassMember(String containerId, ArrayList<String> classNameList)

    String createContainerAsyncRequest(String containerId)
}