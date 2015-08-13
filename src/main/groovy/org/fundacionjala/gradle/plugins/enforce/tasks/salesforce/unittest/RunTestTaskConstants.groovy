package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.unittest

class RunTestTaskConstants {
    protected static final String CLASS_PARAM = 'test'
    protected static final String FILE_PARAM = 'files'
    protected static final String REFRESH_PARAM = 'refreshMapping'
    protected static final String CLASS_DIRECTORY = 'classes'
    protected static final String WILDCARD_ALL_TEST = '*'
    protected static final String NAME_FOLDER_REPORT = 'report'
    protected static final String NAME_FOLDER_PAGES = 'pages'
    protected static final String NAME_FILE = 'index.html'
    protected static final String NAME_FILE_UNIT_TEXT_XML = 'unitTest.xml'
    protected static final String NAME_FILE_COVERAGE_REPORT_XML = 'coverage.xml'
    protected static final String PARAMETER_ASYNC = 'async'
    protected static final String QUERY_CLASSES = "SELECT Id, Name FROM ApexClass"
    protected static final String QUERY_TRIGGERS = "SELECT Id, Name FROM ApexTrigger"
    protected static final String NOT_HAVE_UNIT_TEST_MESSAGE = "You don't have any test class to execute in your local repository"
    protected static final String IS_TEST = "@isTest"
    protected static final String UNIT_TEST_SUCCESS = 'Success'
    protected static final String UNIT_TEST_FAIL = 'Fail'
    protected static final String QUERY_COVERAGE = "SELECT NumLinesCovered, NumLinesUncovered, ApexClassorTriggerId, Coverage" +
            " FROM ApexCodeCoverageAggregate"
    protected static final int TIME_RUN_TEST_ASYNC = 1000
    protected static final int ZERO_NUMBER = 0
    protected static final String SOME_LOG_CATEGORY = 'some.log.category'
    protected static final String DESCRIPTION_TASK = "This task runs unit tests and it also generates results of unit test and coverage"
    protected static final String TEST_GROUP = "Test"
    protected static final String UNIT_TEST_RESULT = "Unit Test Results"
    protected static final String TRUE_VALUE = "true"
    protected static final String ENTER_VALID_PARAMETER = "Enter valid parameter"
    protected static final String NOT_FOUND_ANY_CLASS = "Not found any class in your organization"
    protected static final String ALL_UNIT_TEST_WILL_BE_EXECUTED = "All unit test will be executed"
    protected static final String TEST_CLASSES_WILL_BE_EXECUTED = "test class(es) will be executed"
    protected static final String HOUR_FORMAT = "HH:mm:ss.SSS"
    protected static final String START_TIME = "Start time: %s"
    protected static final String FINISH_TIME = "Finish time: %s"
    protected static final String TOTAL_TIME = "Total time:"
    protected static final String GENERATE_XML_REPORT = "Starting to generate report html"
    protected static final String STARTING_WRITE_JSON_FOR_JENKINS =  "Starting to write JSON for jenkins plugin..."
    protected static final String NO_DATA_TO_WRITE_JSON_FOR_JENKINS =  "No data to write JSON for jenkins plugin"
    protected static final String JSON_CREATED_AT =  "JSON created at:"
    protected static final String NOT_FOUND_CLASS_TO_EXECUTE_UNIT_TEST = 'Not found class for execute unit test in your local repository'
    protected static final String SLEEPING = "Sleeping"

    protected static final String METADATA_CONTAINER_NAME = "EnforceContainer_001"
    protected static final String WILD_CARD_SIGN = "*"
    protected static final String FILE_SEPARATOR_SIGN = ","
    protected static final String DESTINATION_PARAMETER = "destination"
    protected static final String RUN_ALL_UPDATED_PARAM_VALUE = "allUpdated"
}

