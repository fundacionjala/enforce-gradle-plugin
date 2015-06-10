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
    public static final String TEMP_DIR_PATH = "java.io.tmpdir"
    public static final String TEMP_FOLDER_NAME = "temp_enforce"
    public static final String LOGS_FOLDER_NAME = "logs"

    public static final int ZERO = 0

    public static final String COMMA = ","
    public static final String POINT = "."
    public static final String WILDCARD = "*"
    public static final String SLASH = '/'
    public static final String BACK_SLASH = '\\\\'

    public static final String OBJECT_EXTENSION = 'object'


    //Deployment task constants
    public static final String PARAMETER_FOLDERS = 'folders'
    public static final String PARAMETER_FILES = 'files'
    public static final String PARAMETER_EXCLUDES = 'excludes'

    //Deploy task constants
    public static final String DEPLOYING_TRUNCATED_CODE = 'Deploying truncated code'
    public static final String DEPLOYING_TRUNCATED_CODE_SUCCESSFULLY = 'Truncated code were successfully deployed'
    public static final String DEPLOYING_CODE = 'Starting deploy'
    public static final String DEPLOYING_CODE_SUCCESSFULLY = 'Code were successfully deployed'
    public static final String TRUNCATE_DEPRECATE_TURNED_OFF = 'truncate deprecate statement has been deactivated'
    public static final String TRUNCATE_CODE_TURNED_OFF = 'truncate code has been deactivated'
    public static final String DEPLOY_DESCRIPTION = 'This task deploys all the project'
    public static final String FOLDERS_DEPLOY = "folders"
    public static final String FOLDER_DEPLOY = 'deploy'
    public static final String TURN_OFF_TRUNCATE = 'turnOffTruncate'
    public static final String TRUNCATE_DEPRECATE = 'deprecate'
    public static final String TRUNCATE_CODE = 'sourceCode'

    public static final Integer NOT_FOUND = -1

    public static final ArrayList<String> FOLDERS_TO_TRUNCATE = ['classes', 'objects', 'triggers', 'pages', 'components', 'workflows']

    //I/O constants
    public static final String IO_MESSAGE_TEMP_DIR = 'Could not create temp directory'

    //UnDeploy task constants
    public static final String UN_DEPLOY_DESCRIPTION = 'This task removes all components in your organization according to local repository'
    public static final String START_MESSAGE_TRUNCATE = 'Starting undeploy...'
    public static final String SUCCESS_MESSAGE_TRUNCATE = 'All components truncated were successfully uploaded'
    public static final String SUCCESS_MESSAGE_DELETE = 'The files were successfully deleted'
    public static final String FILE_NAME_DESTRUCTIVE = "destructiveChanges.xml"
    public static final String CUSTOM_FIELD_NAME = 'CustomField'
    public static final String WORK_FLOW_RULE_NAME = 'WorkflowRule'
    public static final String DIR_UN_DEPLOY = "undeploy"
    public static final String LOOKUP_NAME = 'Lookup'

    //Upload task constants
    public static final String UPLOAD_DESCRIPTION = "This task uploads all specific files or folders as user wants"
    public static final String ALL_FILES_UPLOAD = "All files will be uploaded from: "
    public static final String QUESTION_CONTINUE = "Do you want to continue? (y/n) :"
    public static final String QUESTION_CONTINUE_DELETE = "Do you want delete this files into your organization? (y/n) :"
    public static final String UPLOAD_CANCELED ='Upload all files was canceled!!'
    public static final String DIR_UPLOAD_FOLDER = "upload"
    public static final String FILES_TO_UPLOAD = "files"
    public static final String ALL_FILES_TO_UPLOAD = "all"
    public static final String YES_OPTION = 'y'

    //Update task constants
    public static final String UPDATE_DESCRIPTION = "This task deploys just the files that were changed"
    public static final String DIR_UPDATE_FOLDER = "update"
    public static final String NOT_FILES_CHANGED = "There are not files changed"

    //Retrieve task constants
    public static final String RETRIEVE_DESCRIPTION_OF_TASK = 'This task recover specific files from an organization'
    public static final String RETRIEVE_MESSAGE_WARNING = 'Warning: All files will be downloaded according to the package'
    public static final String RETRIEVE_MESSAGE_CANCELED = 'Retrieve task was canceled!!'
    public static final String RETRIEVE_QUESTION_TO_CONTINUE = 'Do you want to continue? (y/n) : '

    //Delete task constants
    public static final String START_DELETE_TASK = 'Start process to deleted'
    public static final String SUCCESSFULLY_DELETE_TASK  = 'The files were successfully deleted'
    public static final String DESCRIPTION_DELETE_TASK = "This task deploys just the files that were changed"
    public static final String DIR_DELETE_FOLDER = "delete"
    public static final String PROCCES_DELETE_CANCELLED = "The delete process was canceled"
    public static final String NOT_FILES_DELETED = "There are not files to delete"
}
