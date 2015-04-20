---
layout: page
title: Documentation
---

Here you can find a comprehensive guide to EnForce tools for Salesforce, which will guide you on the process to have a Gradle script that can help you on tasks related to develop an application on Salesforce platform.

## What is an Enforce tool for Salesforce?
It is a Gradle plugin which provides useful tasks that can help with code deployment to organizations, unit test execution, reporting for unit tests and code coverage. Also there is a Jenkins plugin that shows useful unit tests and code coverage information.

## Why Enforce tool does exist?
Currently Salesforce platform provides a Migration Tool which allows us to upload changes to an Organization, but it has some limitations due to the dependency among Salesforce elements (Objects, pages, classes, components).
Enforce tools for Salesforce makes it easier to deploy and undeploy a project source code to a Salesforce Organization, additionally it helps to implement a Continuous Integration process for a Salesforce project.

## Features

* Store encrypted Salesforce credentials identified by an Id which it can be used in the EnForce Gradle tasks.
* Deploy Salesforce project code, no matter if there are dependencies between objects, pages, classes, etc., this task will try to break those dependencies in order to make possible to deploy the code to an organization.
* Upload Salesforce project code as it is, similar to the functionality that the Migration Tool provides.
* Undeploy code from an organization, that means remove the source code from a Salesforce organization.
* Track local changes on project files and upload them with one task.
* Run unit tests and provide a code coverage and unit test reports.
* Generate compatible code coverage(Cobertura format) and unit test(JUnit format) results for CI servers.
* Change the source code before the upload cycle (interceptor pattern).
* Install or uninstall Salesforce packages.
