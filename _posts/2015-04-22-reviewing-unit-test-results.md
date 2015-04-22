---
layout: post
title: Reviewing unit test results
categories: []
tags: []
published: True
comments: True
---

Run unit test on salesforce it's really easy, you can do it in two ways:

- Using the options Run all test in Apex Classes page.
- Using the developer console that salesforce provides.

In both cases I had a small problem, it happened when I had many unit tests with their results 'Fail', it was not easily accessible see all errors messages especially because most of them were really long.

If you have a unit test with its failed result , you need to read the errors message to know what exactly is going on, to do that, using any of the two ways that current Salesforce provides, you have to increase the size of error fields with the mouse. As a beginner in salesforce developer world, it was exhausting and distressing.

Fortunately with EnForce it became more friendly, it has a gradle Task which it's called runTest,
it can be executed synchronously or asynchronously since a command prompt on Windows or in a terminal On Linux, EnForce plugin in both cases shows the error messages one by one in the same window making easier the read them.

Even though currently, Salesforce community has others plugins to do this in a really cool way like MavensMate, Enforce has features related to this gradle task runTest which are very awesome as coverage, or a jenkins plugin, too watch them see please visit EnForce documentation.
