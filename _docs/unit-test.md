---
layout: gradle
title: Unit tests and Reporting
permalink: /docs/unit-test/
---
## RunTest task

#### **Objective**

This task executes all unit test classes in your organization but it is also possible execute some unit test using wildcards. It generates reports about:

 * Code coverage report.
 * Unit test report

#### **Parameters**
This task has two parameters called ***async*** and ***cls*** where the first is used to run test asynchronously by default it runs test synchronously another one is used to choose specific test classes using wildcards.

## Executing RunTest task

### Synchronously

Once runTest task is executed without parameters It will execute all test classes from your organization and It is synchronously by default.

	$ gradle runTest


###  Using a wildcard synchronously

This task runs only unit test classes according to wildcard and It generates XML reports of results obtained from server those reports are saved in ***build/report*** directory.

	$ gradle runTest -Pcls=Test*

### Asynchronously

To execute tests asynchronously you should use parameter called ***async*** and send value equals true.

	$ gradle runTest -Pasync=true

###  Using a wildcard asynchronously

This task run only unit test classes according to wildcard and it generates XML reports of results obtained from server in ***build/report*** directory.

	$ gradle runTest -Pcls=Test* -Pasync=true

## Examples

###  Using a wildcard synchronously

Scenario:

In this case I want to run test classes that have *'Test'* word in their names and I want to run those unit test synchronously.

command:

	$ gradle runTest -Pcls=Test*

output:


```bash
:runTest
___________________________________________
        Username: juan.perez@mail.com
        Login type: login
___________________________________________

Waiting reply from server: https://na17.salesforce.com
Unit Test Results:
        TestClass.test1
                Test Fail - System.AssertException: Assertion Failed: true | Class.TestClass.test1: line 26, column 1
        TestClass.test2
                Test Fail - System.AssertException: Assertion Failed: true | Class.TestClass.test2: line 33, column 1
        TestClass.test3
                Test Fail - System.AssertException: Assertion Failed: true | Class.TestClass.test3: line 39, column 1
        TestClass2.test3
                Test Fail - System.AssertException: Assertion Failed: true | Class.TestClass2.test3: line 39, column 1
        TestClass1.test3
                Test Fail - System.AssertException: Assertion Failed: true | Class.TestClass1.test3: line 39, column 1
Total time: 00:00:05:5315

BUILD SUCCESSFUL
```

###  Using a wildcard asynchronously

Scenario:

In this case I want to run all test classes and I want to run those unit test asynchronously.

command:

	$ gradle runTest -Pcls=Test* -Pasync=true

output:

```bash
:runTest
___________________________________________
        Username: juan.perez@mail.com
        Login type: login
___________________________________________

Waiting reply from server: https://na17.salesforce.com
Unit Test Results:
        TestClass.test1
                Test Fail - System.AssertException: Assertion Failed: true | Class.TestClass.test1: line 26, column 1
        TestClass.test2
                Test Fail - System.AssertException: Assertion Failed: true | Class.TestClass.test2: line 33, column 1
        TestClass.test3
                Test Fail - System.AssertException: Assertion Failed: true | Class.TestClass.test3: line 39, column 1
        TestClass2.test3
                Test Fail - System.AssertException: Assertion Failed: true | Class.TestClass2.test3: line 39, column 1
        TestClass1.test3
                Test Fail - System.AssertException: Assertion Failed: true | Class.TestClass1.test3: line 39, column 1
Total time: 00:00:05:5315

BUILD SUCCESSFUL
```
