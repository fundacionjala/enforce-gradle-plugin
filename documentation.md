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

* It has a Credentials profile
* It deploys code and update code from Organization
* It undeploys code from Organization
* It tracks file changes
* It runs unit test
* It provides compatible results for CI servers
* It managements credentials adding and updating them
* It encrypts credentials
