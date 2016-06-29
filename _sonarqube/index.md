---
layout: sonarqube
title: Welcome
permalink: /sonarqube/home/
---

With this plugin you can run a [SonarQube](http://www.sonarqube.org) analysis over your source apex code that will generate a dynamic and customizable report indicating code issues caused by bad practices, parsing errors or unfollowed code conventions. You can see more about it in the [EnForce SonarQube plugin](https://github.com/fundacionjala/enforce-sonarqube-plugin) repo.

# How does it help with continuous integration?

The Sonarqube analysis becomes a "quality standar", which the team members must fulfill before pushing their changes. Running the analysis prior to code review allows the developer to see code flaws and bad practices before-hand, making the process move quicklier and more smoothly.

# Prerequisites

To be able to run a SonarQube analysis on your project, you will need:

* A running instance of SonarQube Server, which you can download from [here](http://www.sonarqube.org/downloads).
* To install [SonarQube scanner](https://sonarsource.bintray.com/Distribution/sonar-scanner-cli/sonar-scanner-2.6.1.zip) and configure sonar-runner as an enviroment variable
* The [Enforce SonarQube Apex Plug-in](https://bintray.com/fundacionjala/enforce/enforce-sonar-plugin/view).

Now you'll be able to run the analysis for a project, following these next [steps](http://fundacionjala.github.io/enforce-gradle-plugin/sonarqube/runanalysis).