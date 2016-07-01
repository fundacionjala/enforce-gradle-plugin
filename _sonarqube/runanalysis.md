---
layout: sonarqube
title: Run the Analysis
permalink: /sonarqube/runanalysis/
---

# Running a SonarQube analysis on your project

These following steps will help you run a SonarQube analysis in one of your local projects, and then see the report of the issues found.

# Initial configuration

First off, you'll need to configure the [SonarQube scanner](https://sonarsource.bintray.com/Distribution/sonar-scanner-cli/sonar-scanner-2.6.1.zip) as an enviroment variable

               SONAR_RUNNER_HOME=home/sonar-scanner-2.5.1
               PATH=$PATH:$SONAR_RUNNER_HOME/bin

Next, you are going to need to add the [Apex plugin](https://bintray.com/fundacionjala/enforce/enforce-sonar-plugin/view) jar file (apex-plugin.jar) to the plugins of Sonarqube, in the following subfolder "/sonarqube/extensions/plugins/"

# Start the SonarQube server

	
In the folder where you installed SonarQube, you need to go to the sub-folder `bin` and then to the sub-folder corresponding to your OS. 

Once inside, run `./sonar.sh console` to start the SonarQube server.



# Scanning projects


1. Create a sonar-project.properties file at the root of your project

           sonar.projectKey=my:project
           sonar.projectName=My project
           sonar.projectVersion=1.0
           sonar.language=apex
           sonar.sources=.

      > **sonar.projectKey**: must be unique in a given SonarQube instance

      > **sonar.projectName**: this is the name displayed in the SonarQube UI

      > **sonar.language**: specifies the language for analysis

      > **sonar.sources**: this "dot" indicates to sonnar scanner to scan all files from the current level of directories, this is, all files that have an extension ".cls". This extension is defined by plugin scanner.

2. Run 'sonar-runner' command from the project root dir.

3. Follow the link provided at the end of the analysis to browse your project's quality in SonarQube UI.
