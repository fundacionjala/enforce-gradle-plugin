---
layout: sonarqube
title: Rules
permalink: /sonarqube/rules/
---

The issue tracking functionality of the plugin is based on so called [Rules or Checks](http://docs.sonarqube.org/display/SONAR/Rules); these are applied over the source code to determine errors, reliabilities, logical flaws or code conventions broken, and processes them to determine remediaton cost and technical debt.

Rules are extremely manageable, since SonarQube allows you to choose the set of rules you want your project to follow and save them in [Quality Profiles](http://docs.sonarqube.org/display/SONAR/Quality+Profiles), where you can enable and disable them, besides you can also, if you feel it's needed, change their severity and remediation time, exclude them and mark them as false positives, and even [create your own rules](https://github.com/fundacionjala/enforce-sonarqube-plugin/wiki/Apex-Checks).