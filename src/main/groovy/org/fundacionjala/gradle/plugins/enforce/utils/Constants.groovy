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
    public static final String REPORT_FOLDER_NAME = "report"

    public static final int ZERO = 0

    public static final String COMMA = ","
    public static final String POINT = "."
    public static final String WILDCARD = "*"
    public static final String SLASH = '/'
    public static final String BACK_SLASH = '\\\\'
    public static final String EMPTY = ""

    public static final String OBJECT_EXTENSION = 'object'
    public static final String FILE_TRACKER_NAME = ".fileTracker.data"

    //Deployment task constants
    public static final String PARAMETER_FOLDERS = 'folders'
    public static final String PARAMETER_FILES = 'files'
    public static final String PARAMETER_EXCLUDES = 'excludes'
    public static final String PARAMETER_VALIDATE_ORG = 'validate'
    public static final String YES_OPTION = 'y'
    public static final String TRUE_OPTION = 'true'
    public static final String FALSE_OPTION = 'false'

    //Truncate constants
    public static final String TRUNCATE_DESCRIPTION = 'This task truncates classes, objects, triggers, pages, components, workflows and tabs from your code'
    public static final String TRUNCATE_FOLDER_NAME = 'truncate'

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
    public static final boolean CONTAINS_XML_FILE = true;

    public static final ArrayList<String> FOLDERS_TO_TRUNCATE = ['classes', 'objects', 'triggers', 'pages', 'components', 'workflows']

    //I/O constants
    public static final String IO_MESSAGE_TEMP_DIR = 'Could not create temp directory'

    //UnDeploy task constants
    public static final String UN_DEPLOY_DESCRIPTION = 'This task removes all components in your organization according to local repository'
    public static final String START_MESSAGE_TRUNCATE = 'Starting truncate proccess...'
    public static final String SUCCESS_MESSAGE_TRUNCATE = 'All components truncated were successfully uploaded'
    public static final String SUCCESS_MESSAGE_DELETE = 'The files were successfully deleted'
    public static final String START_MESSAGE_UNDEPLOY = 'Starting undeploy proccess...'
    public static final String FILE_NAME_DESTRUCTIVE = "destructiveChanges.xml"
    public static final String DIR_UN_DEPLOY = "undeploy"
    public static final String LOOKUP_NAME = 'Lookup'
    public static final String FILE_NOT_FOUND = "these files can't be deleted from your organization, because these weren't found!"

    //Upload task constants
    public static final String UPLOAD_DESCRIPTION = "This task uploads all specific files or folders as user wants"
    public static final String ALL_FILES_UPLOAD = "All files will be uploaded from: "
    public static final String QUESTION_CONTINUE = "Do you want to continue? (y/n) :"
    public static final String QUESTION_CONTINUE_DELETE = "Do you want delete this files into your organization? (y/n) :"
    public static final String UPLOAD_CANCELED ='Upload all files was canceled!!'
    public static final String DIR_UPLOAD_FOLDER = "upload"
    public static final String FILES_TO_UPLOAD = "files"
    public static final String ALL_FILES_TO_UPLOAD = "all"
    public static final String START_UPLOAD_TASK_MESSAGE = "Starting upload files process..."
    public static final String SUCCESS_UPLOAD_TASK_MESSAGE = "The files were successfully uploaded"

    //Update task constants
    public static final String UPDATE_DESCRIPTION = "This task deploys just the files that were changed"
    public static final String DIR_UPDATE_FOLDER = "update"
    public static final String NOT_FILES_CHANGED = "There are not files changed"
    public static final String START_UPDATE_TASK_MESSAGE = "Starting update proccess..."
    public static final String SUCCESS_UPDATE_TASK_MESSAGE = "The files were successfully updated!"

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

    //Unit test task
    public static final String CLASS_PARAM = 'cls'
    public static final String CLASS_DIRECTORY = 'classes'
    public static final String WILDCARD_ALL_TEST = '*'
    public static final String NAME_FOLDER_REPORT = 'report'
    public static final String NAME_FOLDER_PAGES = 'pages'
    public static final String NAME_FILE = 'index.html'
    public static final String NAME_FILE_UNIT_TEXT_XML = 'unitTest.xml'
    public static final String NAME_FILE_COVERAGE_REPORT_XML = 'coverage.xml'
    public static final String PARAMETER_ASYNC = 'async'
    public static final String QUERY_CLASSES = "SELECT Id, Name FROM ApexClass"
    public static final String QUERY_TRIGGERS = "SELECT Id, Name FROM ApexTrigger"
    public static final String NOT_HAVE_UNIT_TEST_MESSAGE = "You don't have any test class to execute in your local repository"
    public static final String IS_TEST = "@isTest"
    public static final String UNIT_TEST_SUCCESS = 'Success'
    public static final String UNIT_TEST_FAIL = 'Fail'
    public static final String QUERY_COVERAGE = "SELECT NumLinesCovered, NumLinesUncovered, ApexClassorTriggerId, Coverage" +
            " FROM ApexCodeCoverageAggregate"
    public static final int TIME_RUN_TEST_ASYNC = 1000
    public static final int ZERO_NUMBER = 0

    //Execute task
    public static final String APEX_CODE = 'input'
    public static final String APEX_OUTPUT_FILE_PATH = 'output'
    public static final String DESCRIPTION_EXECUTE_TASK = "This task executes apex code from a file or inline code"
    public static final String UTILS_GROUP = "Utils"
    public static final String OUTPUT_RESULT = "Output result:\n"
    public static final String INVALID_INPUT_PARAMETER = "parameter invalid"

    //Truncate task
    public static final String START_TRUNCATE_PROCCESS_MESSAGE = "Starting truncate process"
    public static final String SUCCESS_TRUNCATE_MESSAGE = "The files were successfully truncated"
}