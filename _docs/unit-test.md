---
layout: gradle
title: Unit tests and Reporting
permalink: /docs/unit-test/
---
## RunTest task

#### **Objective**

This task executes all unit test classes in your organization but it is also possible execute some unit tests using wildcards or using an engine to infer what tests run. It generates reports about:

 * Code coverage report
 * Unit test report

#### **Parameters**
This task has several parameters in order to be accurate at time to run test classes 
 
 * Available test selectors:
   * ***tests*** is used to choose specific test classes using wildcards
   * ***files*** is used to enter test class names and infer their related test classes using an engine test selector
     * ***refreshMapping*** is used to reset the mapping that the engine uses to select test classes, if it is not present, default it is false
   
 * Other available params:
   * ***async*** is used to run test asynchronously, by default it runs test synchronously
   * ***destination*** is used to choose a target directory in where save the run test results, by default they are saved in ***build/report*** directory

## Executing RunTest task

### Synchronously

Once runTest task is executed without parameters, it will execute all test classes from your organization and it is synchronously by default.

	$ gradle runTest


###  Using a wildcard synchronously

This task runs only unit test classes according to wildcard.

	$ gradle runTest -Ptests=Test*

### Asynchronously

To execute tests asynchronously you should use parameter called ***async*** and send value equals true.

	$ gradle runTest -Pasync=true

###  Using a wildcard asynchronously

This task run only unit test classes according to wildcard and asynchronously way 

	$ gradle runTest -Ptests=Test* -Pasync=true

###  Using Test Selector engine <span style="color:red; font-size:10pt;background-color:yellow">New!</span>

In addition to run the specific test classes using the ***test*** param, you can take advantage of the **Test Selector** engine to infer test classes related to a set of specific apex classes passing in the ***file*** param the class names than you want to get their associated test classes
This is a new approach to select test classes, it helps to you to save time by passing just the updated classes in which you are working on and infer their test classes

It will run all associated test classes to the set of Apex Classes defined in the ***file*** param value

	$ gradle runTest -Pfiles=Class1.cls

It will run all associated tests to the last updated Apex Classes, (Enforce will track all the changes that are being done on the project)
	
	$ gradle runTest -Pfiles=allUpdated


In addition, you can combine both select test variants in a single command.
This task runs the *TestClass1* Test Class **plus** all associated test classes to the *Class1* Apex Class

    $ gradle runTest -Ptests=TestClass1.cls -Pfiles=Class1.cls


**Test Selector control**
To achieve that Enforce knows what test classes select, the engine will build a mapping of components between the Apex Classes and Test Classes, then if you want to reset that mapping due your updates, you have to run the task and specify a boolean value for the ***refreshMapping*** param

    $ gradle runTest -Pfiles=Class1.cls -PrefreshMapping=true

###  Saving the run test task results

By default the task generates XML reports of results obtained from server in ***build/report*** directory, you can choose a different directory by passing a directory path into the ***destination*** param

    $ gradle runTest -Ptests=TestClass1.cls -Pdestination=/home/temporalReports/


## Examples

###  Using a wildcard synchronously

Scenario:

In this case I want to run test classes that have *'Test'* word in their names and I want to run those unit test synchronously.

command:

	$ gradle runTest -Ptests=Test*

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

	$ gradle runTest -Ptests=Test* -Pasync=true

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
