[![Build Status](https://travis-ci.org/fundacionjala/enforce-gradle-plugin.svg)](https://travis-ci.org/fundacionjala/enforce-gradle-plugin) [![license](http://img.shields.io/badge/license-MIT-brightgreen.svg?style=flat)](https://github.com/fundacionjala/enforce-gradle-plugin/blob/master/LICENSE)

---
Quick start
---

## General description

This project is a Gradle plugin that provides useful tasks for implement a Continuous Integration process for SalesForce projects.

## How can you use this plugin?

Here you can find the enought required information to have running a Gradle project with Enforce.

1.Create build.gradle file on your source code project, below you have an example:

```groovy
buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath 'org.fundacionjala.gradle.plugins.enforce:enforce-gradle-plugin:1.1.5'
    }
}

apply plugin: 'enforce'
enforce {
	srcPath = 'src'
}
```

2.Register a Salesforce credential to be used

```groovy
   $ gradle addCredential -Pid=default
			  	-Pusername=<USER NAME>
			  	-Ppassword=<PASSWORD>
			  	-Ptoken=<SECURITY TOKEN>
```

In this step is recomendable put credential id as ***default*** value, to run the EnForce tasks without using ***credentialId*** parameter.

Executing a task without ***credentialId*** parameter:

	$ gradle <TASK NAME>

It uses the default credential.

Executing a task with ***credentialId*** parameter:

	$ gradle <TASK NAME> -PcredentialId=<CREDENTIAL ID>

This credential will be saved in credentials.dat file located in user HOME directory.

3.Start executing available commands, for example, deploy all classes from your local repository to your organization.

```
   $ gradle deploy
```

Now you can start using all the Enforce features.

---
Reports
---
- These reports are generated on `{PROJECT_FOLDER}/build/report/` after execute `gradle runTest` command.
- They are able to be integration with Jenkins enviroments

### JUnit test result report
*Configuration*
 * Select your job.
 * Go to ``Configure`` -> ``Add post-build action``
 * Select ``Publish JUnit test result report`` option

![Unit test result configuration](https://cloud.githubusercontent.com/assets/8682892/9668384/322f9e9a-524f-11e5-8664-a47427821371.png)

*Preview*

![Unit test result preview](https://cloud.githubusercontent.com/assets/8682892/9668395/423cc2e0-524f-11e5-94c6-dffcd836b0dd.png)

### Enforce code coverage and test result report
* Summary, contains charts with the coverage and test results.
* Unit Test, contains unit test execution results and also it has options to filter its results.
* Coverage, contains a list of all classes categorized by coverage status(Danger, Risk, Acceptable, Safe) and also it is possible to review the covered and uncovered lines by file(click on file name).

*Configuration*
* Select your job.
 * Go to ``Configure`` -> ``Add post-build action``
 * Select ``Publish HTML reports`` option

![Code coverage result configuration](https://cloud.githubusercontent.com/assets/8682892/9668401/477f41ec-524f-11e5-95b9-1b004d5616d8.png)

*Preview*
![Enforce code coverage and test result report preview](https://cloud.githubusercontent.com/assets/8682892/9668406/4d35753e-524f-11e5-938b-9d9edcc79435.png)

---
Development
---

### Requirements
 * [Java JDK or JRE, version 7 or higher](http://java.com/en/).
 * [Gradle version 2.0 or higher](https://gradle.org/docs/current/userguide/installation.html).
 * Internet access ([configure Gradle if you are using a Proxy](https://gradle.org/docs/current/userguide/build_environment.html)).

### Using the plugin in your gradle build script

You can use your own gradle version but to avoid unexpected results with the compatibility we recommend to use the gradle wrapper. You can modify the build.gradle file and specify your version there.

#### It is desired a basic knowledge about Gradle and its plugin mechanism, as starting point you can review:

 * [Writing custom plugins](http://www.gradle.org/docs/current/userguide/custom_plugins.html)
 * [Gradle Custom Plugin](http://www.javacodegeeks.com/2012/08/gradle-custom-plugin.html)

#### Once you have the source code, open the source code in a console and execute:

```
   $ gradle wrapper
```

#### Please, make sure that your changes are not breaking any functionality running the unit test:
```
   $ ./gradlew test
```
