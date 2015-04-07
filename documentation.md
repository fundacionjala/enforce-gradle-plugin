---
layout: page
title: Documentation
---

Here you can find a comprenhensive guide to EnForce tools for Salesforce, which will guide you on the process to have a Gradle script that can help you on tasks related to develop an application on Salesforce platform.

## What is Enforce tools for Salesforce?
It is a Gradle plugin which provides usefull tasks that can help on code deployment on organizations, unit test execution, reporting for unit tests and code coverage. Also there is Jenkins plugin that shows useful unit test and code coverage information.

## Why Enforce tool does exist?
Currently Salesforce platform provides the Migration Tool which allows upload changes to an Organization, but it has limitations due to dependency between Salesforce elements (Objects, pages, classes, components).
Enforce tools for Salesforce tries to make easy to deploy and undeploy a project source code to a Salesforce Organization, additionally it tries to help on implement a Continuous Integration process for a Salesforce project.

## Features

* Store encrypted Salesforce credentials identified by an Id which it can be used in the EnForce Gradle tasks.
* Deploy Salesforce project code, no matter if there are dependencies between objects, pages, classes, etc., this task will try to break those dependencies in order to make possible to deploy the code into an organization.
* Upload Salesforce project code as it is, similar to the functionality that the Migration Tool provides.
* Undeploy code from an organization, that means remove the source code from a Salesforce organization.
* Track local changes on project files and upload them with one task.
* Run unit tests and provide a code coverage and unit test reports.
* Generate compatible code coverage(Cobertura format) and unit test(JUnit format) results for CI servers.
* A way to change the source code before the upload cycle(interceptor pattern).
* Install or uninstall Salesforce packages.
