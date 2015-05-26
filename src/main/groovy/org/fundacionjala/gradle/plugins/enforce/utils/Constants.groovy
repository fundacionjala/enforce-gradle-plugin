/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.utils

/**
 * Contains common constants
 */
public class Constants {
    public static final String PACKAGE_FILE_NAME = "package.xml"
    public static final String DESTRUCTIVE_FILE_NAME = "destructiveChanges.xml"
    public static final String META_XML_NAME = "-meta.xml"
    public static final String JENKINS_JSON_FILE_NAME = "coverage.json"
    public static final String FORCE_EXTENSION = "enforce"
    public static final String TYPE_CLASS = "Class"
    public static final String DEPLOYMENT = "Deployment"
    public static final String INVALID_FOLDER = "Invalid folders"
    public static final String INVALID_FILE = "Invalid files"
    public static final String DOES_NOT_EXIST_FILES = "Not exist files:"
    public static final String DOES_NOT_EXIST_FOLDER = "Not exist folders:"
    public static final String NOT_FILES = "There are not files into folders"
    public static final String EMPTY_FOLDERS = "Empty folders:"
    public static final String META_XML = "-meta.xml"
    public static final String NULL_PARAM_EXCEPTION = "the argument %s "
    public static final String UNSUPPORTED_FOLDERS = "unsupported folders"
    public static final String JENKINS_CHART_NAME = "Code coverage"
    public static final String FALSE = "false"

    public static final int ZERO = 0

    public static final String COMMA = ","
    public static final String POINT = "."
    public static final String WILDCARD = "*"

    public static final String OBJECT_EXTENSION = 'object'

    //Deploy task constants
    public static final String DEPLOYING_TRUNCATED_CODE = 'Deploying truncated code'
    public static final String DEPLOYING_TRUNCATED_CODE_SUCCESSFULLY = 'Truncated code were successfully deployed'
    public static final String DEPLOYING_CODE = 'Starting deploy'
    public static final String DEPLOYING_CODE_SUCCESSFULLY = 'Code were successfully deployed'
    public static final String TRUNCATE_DEPRECATE_TURNED_OFF = 'truncate deprecate statement has been deactivated'
    public static final String TRUNCATE_CODE_TURNED_OFF = 'truncate code has been deactivated'
    public static final String DESCRIPTION_OF_TASK = 'This task deploys all the project'
    public static final String FOLDERS_DEPLOY = "folders"
    public static final String FOLDER_DEPLOY = 'deploy'
    public static final String TURN_OFF_TRUNCATE = 'turnOffTruncate'
    public static final String TRUNCATE_DEPRECATE = 'deprecate'
    public static final String TRUNCATE_CODE = 'sourceCode'

    public static final Integer NOT_FOUND = -1

    public static final ArrayList<String> FOLDERS_TO_TRUNCATE = ['classes', 'objects', 'triggers', 'pages', 'components', 'workflows']
}
