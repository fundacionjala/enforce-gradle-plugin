/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.exceptions.deploy

/**
 * Exception wrapper for deploy errors
 */
class DeployException extends RuntimeException {

    private final String KEY_ERRORS = 'errors'
    private final String KEY_FILE_NAME = 'fileName'
    private final String KEY_PROBLEM = 'problem'
    private final String KEY_LINE = 'line'
    private final String KEY_COLUMN = 'column'
    private ArrayList<InfoDeploy> infoDeployArrayList

    /**
     * Constructor: sets values to message and array info deploy
     * @param message is information about problem
     * @param infoDeployArrayList is a array contains objects wrapper of details failure
     */
    DeployException(String message, ArrayList<InfoDeploy> infoDeployArrayList) {
        super(message)
        this.infoDeployArrayList = infoDeployArrayList
    }

    /**
     * Returns errors message in Json format inside a string.
     * @return a string
     */
    public String getMessageJsonFormat () {
        StringBuilder objectParsed = StringBuilder.newInstance()
        objectParsed.append("""{"${KEY_ERRORS}":[""")
        infoDeployArrayList.each { infoDeploy ->
            String fileName = """{"${KEY_FILE_NAME}":"${infoDeploy.fileName}","""
            String problem = """"${KEY_PROBLEM}":"${infoDeploy.problem}","""
            String line = """"${KEY_LINE}":${infoDeploy.line},"""
            String column =""""${KEY_COLUMN}":${infoDeploy.column}}"""
            objectParsed.append("${fileName}${problem}${line}${column}${','}")
        }
        objectParsed.deleteCharAt(objectParsed.length() - 1 )
        objectParsed.append("]}")
        return objectParsed.toString()
    }
}
