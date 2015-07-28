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
    public static final String INVALID_FOLDER = "Invalid folder"
    public static final String INVALID_FILE = "Invalid files"
    public static final String VALID_FILE = "Valid files"
    public static final String INVALID_FILE_BY_FOLDER = "Invalid file by folder"
    public static final String FILE_WITHOUT_XML = "File without xml"
    public static final String FILE_WITHOUT_VALIDATOR = "File without validator"

    public static final String DOES_NOT_EXIST_FILES = "Not exist files:"
    public static final String DOES_NOT_EXIST_FOLDER = "Not exist folders:"
    public static final String EMPTY_FOLDERS = "Empty folders:"
    public static final String META_XML = "-meta.xml"
    public static final String NULL_PARAM_EXCEPTION = "the argument %s "
    public static final String UNSUPPORTED_FOLDERS = "unsupported folders"
    public static final String JENKINS_CHART_NAME = "Code coverage"
    public static final String FALSE = "false"
    public static final String TEMP_DIR_PATH = "java.io.tmpdir"
    public static final String TEMP_FOLDER_NAME = "temp_enforce"
    public static final String LOGS_FOLDER_NAME = "logs"
    public static final String REPORT_FOLDER_NAME = "report"
    public static final String DIR_DELETE_FOLDER = "delete"
    public static final String FILE_NAME_DESTRUCTIVE = "destructiveChanges.xml"
    public static final String LOOKUP_NAME = 'Lookup'

    public static final int ZERO = 0

    public static final String COMMA = ","
    public static final String POINT = "."
    public static final String WILDCARD = "*"
    public static final String SLASH = '/'
    public static final String BACK_SLASH = '\\\\'
    public static final String EMPTY = ""

    public static final String OBJECT_EXTENSION = 'object'
    public static final String OBJECTS_FOLDER = 'objects'
    public static final String FILE_TRACKER_NAME = ".fileTracker.data"
    public static final String CUSTOM_FIELD = "CustomField"

    public static final boolean CONTAINS_XML_FILE = true

    //Deployment task constants
    public static final String PARAMETER_FOLDERS = 'folders'
    public static final String PARAMETER_FILES = 'files'
    public static final String PARAMETER_EXCLUDES = 'excludes'
    public static final String PARAMETER_VALIDATE_ORG = 'validate'
    public static final String YES_OPTION = 'y'
    public static final String FALSE_OPTION = 'false'

    //I/O constants
    public static final String IO_MESSAGE_TEMP_DIR = 'Could not create temp directory'

}