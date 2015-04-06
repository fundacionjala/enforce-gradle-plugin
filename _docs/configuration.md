---
layout: gradle
title: Configuration
permalink: /docs/configuration/
---
Enforce Dev Tool Plugin
=============

This is an open source plugin

## Plugin Requirements
<ol>
	<ul>
		<li> Java JDK 1.7</li>
		<li> Gradle 2.0 or greater</li>
	    <li> Operation System:</li>
			<ul>
				<li>Windows 7</li>
				<li>Linux Ubuntu</li>
				<li>Mac OS</li>
			</ul>
		<li> build.gradle file </li>
		<li> gradle.properties file (Optional) </li>
		<li> credentials.dat file </li>
		<li> User account in a Salesforce Organization and the related security token </li>
   </ul>
</ol>

## Tentative files configuration for User

### Files Gradle

* build.gradle has the configuration of gradle for user.
* gradle.properties has values that build.gradle will use.
* credentials.dat has all credentials to use.

##### build.gradle
```groovy
   buildscript {
       repositories {
           mavenCentral()
           maven {
                url "https://dl.bintray.com/fundacionjala/enforce"
           }
       }
       dependencies {
           classpath 'org.fundacionjala.gradle.plugins.enforce:enforce-gradle-plugin:1.0.0'
       }
   }

   apply plugin: 'enforce'

   enforce {
       srcPath = 'src'
   }
```

#####  gradle.properties

```groovy
    credentialId=myCustomId
```

#####  credentials.dat

```json
{
 "default": {
        "username": "user@org.com",
        "password": "mypassword",
        "token": "2B9UvHKK2E1ky1EkkYBPFvbLM",
        "sfdcType" : "login",
        "type": "ecrypted"
    }
}
```

<h3> <strong>Setup Salesforce organization credentials </strong></h3>

<div class="note info">
  <h5>Credential support</h5>
  <p>Managing of credentials can create and update credentials in 'credentials.dat' file it is located in HOME directory, a format was showed in Files gradle that is on last point.</p>
   <p> 'AddCredential task' creates a credential in 'credentials.dat' file located in HOME directory it is saved as encrypted by default and its 'sfdcType' field is saved with login value by default.</p>
   <p> 'UpdateCredential task ' updates a credential by key from 'credentials.dat' file.</p>
</div>


> **Note:** Notice that the example uses the "default" key which by convention is the one to be used by default when a task is executed. You can have several entries to different salesforce accounts but each entry needs to have an unique key.

## Project gradle tasks

The gradle project contains tasks that will help on development and packaging. You have to execute the gradle tasks from the project directory it will show you all tasks runnable from root project.

Command:

	$ gradle tasks
	
Output:

### Build Setup tasks

  * init - Initializes a new Gradle build. [incubating]
  * wrapper - Generates Gradle wrapper files. [incubating]

### Credential Manager tasks

   * addCredential - You can add a credential
   * updateCredential - You can update a credential

### Deployment tasks

   * deploy - This task deploys all the project
   * undeploy - This task removes all components in your organization according to local repository
   * update - This task deploys just the files that were changed
   * upload - This task uploads all specific files or folders as user wants

### File Monitor tasks

   * reset - Reset the file monitor
   * status - You can display elements that were changed

### Help tasks

   * dependencies - Displays all dependencies declared in root project 'user'.
   * dependencyInsight - Displays the insight into a specific dependency in root project 'user'.
   * help - Displays a help message
   * projects - Displays the sub-projects of root project 'user'.
   * properties - Displays the properties of root project 'user'.
   * tasks - Displays the tasks runnable from root project 'user'.

### Package Management tasks

   * installPackage - Installs a package to an organization.
   * uninstallPackage - Uninstalls a package from an organization.

### Retrieve tasks

   * retrieve - This task recover specific files from an organization

### Test tasks

   * runTest - This task runs unit tests and it also generates results of unit test and coverage

### Utils tasks

   * execute - This task executes apex code from a file or inline code
