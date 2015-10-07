---
layout: gradle
title: Quick start
permalink: /docs/quickstart/
---

### 1 Installation

Step | Links | Description
1 | <a href="http://java.com/en/" target="_blank">Java JDK or JRE</a>| Install Java JDK or JRE, version 7 or higher
2 | <a href="https://gradle.org/docs/current/userguide/installation.html" target="_blank">Gradle</a> | Install Gradle 2.0 version or higher
3 | <a href="https://gradle.org/docs/current/userguide/build_environment.html" target="_blank">Configure Gradle</a> | Configure gradle if you are using a Proxy
{: .table-quick-start-tutorials }


### 2 Register credentials
Enforce requires access to a Salesforce Organization, so you need to provide the credentials and Security Token for an user with System Administrator rights.
Enforce provides a task for create credentials, which are stored on your home folder and can be encrypted also.

Step | Examples | Description
1 | ![Con titulo](../../img/quick_start/credential-step-1.png )| **Create build.gradle file**<br> Create build.gradle file on your folder project. <br>Below, you have an example. <br><br> <a href="{{ site.url }}/docs/code-build-gradle/" target="_blank">build.gradle file</a> 
2 | ![Con titulo](../../img/quick_start/credential-step-2.png )| **Create a source code folder** <br>Create a folder in which to store your source code, preferably give a src name.
3 | $ gradle addCredential<br> -Pid=default<br> -Pusername=USER NAME<br> -Ppassword=PASSWORD<br> -Ptoken=SECURITY TOKEN   | **Add SalesForce credential** <br>Open a terminal and run the following command with the next parameters<br><br> id:  *is necessary to management your credential.* <br> username:  *is a SalesForce account.* <br>  password: *is your SalesForce password* <br> token:  *is your SalesForce token.*  <br><br>id: *should be saved with 'default' value*<br><br>For more information about managing credentials see the following link<br><a href="{{ site.url }}/docs/credentials/" target="_blank">Credentials management</a> <br><br>For more information about generate your security token see the following link<br>[Generate token security](http://www.salesforcegeneral.com/salesforce-articles/salesforce-security-token.html)
4 | ![Con titulo](../../img/quick_start/credential-step-3.png ) | **View SalesForce credential** <br>A file is created in the  ./home folder with yours credentials . <br>
{: .table-quick-start-tutorials }

### 3 Retrive your code

This task downloads files from your organization is based on your .xml package. If this file is missing, the system downloads files by default.

Step | Examples | Description
1 | ![Con titulo](../../img/quick_start/retrieve-task-1.png )| **Open a terminal** <br>Open a terminal in the same folder as your project
2 | ![Con titulo](../../img/quick_start/retrieve-task-2.png )| **Execute retrieve task** <br>Execute the following command<br>  *> gradle retrieve*<br> This task downloaded the salesforce components contained in your organization. <br><br> For more information about the *retrieve task* see the following link <br> <a href="{{ site.url }}/docs/retrieve/" target="_blank">Retrieve code</a>
3 | ![Con titulo](../../img/quick_start/retrieve-task-3.png ) | **Results** <br>This task downloads files from your organization is based on your .xml package. If this file is missing, the system downloads files by default.<br> - objects<br> - staticresources<br> - classes<br> - pages<br> - triggers<br> - components <br><br> For more information about the *package.xml* file see the following link <br> [package.xml information](https://developer.salesforce.com/docs/atlas.en-us.api_meta.meta/api_meta/manifest_samples.htm)
{: .table-quick-start-tutorials }

### 4 Deploy your code

This task deploys all code from your local code to Salesforce organization, for this as a first step It truncates your local code and deploys those files truncated to Salesforce organization, as second step It deploys your local code to SatesForce organization.

Step | Examples | Description
1 | ![Con titulo](../../img/quick_start/deploy-task-1.png )| **Open a terminal** <br> Open a terminal in the same folder as your project <br><br> **Execute deploy task** <br>Execute the following command<br>  *> gradle deploy*<br> This task deployed all code from your local code to Salesforce organization. <br><br> For more information about the *deploy task* see the following link <br> <a href="{{ site.url }}/docs/deployment/" target="_blank">Deployed code</a>
2 | ![Con titulo](../../img/quick_start/deploy-task-2.png )| **Results** <br> All code will be deployed to organization using a smart deploy. That means that when you execute the task deploy:<br><br> a) First step is truncate the next elements and deploy it.<br> - classes <br> - objects <br> - triggers <br> - components <br> - pages <br> - workFlows <br><br> b) Second step is deploy all local code to your organization
{: .table-quick-start-tutorials }


### 5 Update your code

This task just deploys changed code as: deleted code, added code and updated code, for example if you changed a class called MyClass.cls and you deleted a trigger called MyTrigger.trigger This deletes MyTrigger.trigger and it updates MyClass.cls according file tracker in your organization.

Step | Examples | Description
1 | ![Con titulo](../../img/quick_start/update-task-4.png )| **Open a terminal** <br> Open a terminal in the same folder as your project <br><br> **Execute update task** <br>Execute the following command<br>  *> gradle update*<br> This task just deploys changed code as: deleted code, added code and updated code. <br><br> For more information about the *update task* see the following link <br> <a href="{{ site.url }}/docs/update/" target="_blank">Update code</a>
{: .table-quick-start-tutorials }


### Other features

#### Credentials management

Task | Description 
<a href="{{ site.url }}/docs/credentials/" target="_blank">addCredential</a> | This task adds a new credential into credentials.dat located in user home directory by default |
<a href="{{ site.url }}/docs/credentials/" target="_blank">updateCredential</a> | This task updates a credential from credentials.dat file located in user home directory and project directory by default is home. |
{: .table-quick-start-features }

#### Monitoring local changes

Task | Description 
<a href="{{ site.url }}/docs/file-monitor/" target="_blank">status</a> | Status task lets the user see what files have been changed as (new file, deleted file and updated file | 
<a href="{{ site.url }}/docs/file-monitor/" target="_blank">reset</a> | File monitor tracker will be reset. |
{: .table-quick-start-features }

#### Deployment tasks

Task | Description 
<a href="{{ site.url }}/docs/deployment/" target="_blank">deploy</a> | This task deploys all code from your local code to Salesforce organization, for this as a first step It truncates your local code and deploys those files truncated to Salesforce organization, as second step It deploys your local code to SatesForce organization. | 
<a href="{{ site.url }}/docs/deployment/" target="_blank">upload</a> | This task uploads your code from your local repository to SalesForce organization directly as it is. |
<a href="{{ site.url }}/docs/undeploy/" target="_blank">undeploy</a> | This task is able to undeploy code from Salesforce organization. To avoid dependency problems It truncates code and it deploys code truncated as first step and then It will try to delete all code from Salesforce organization according to local code. | 
<a href="{{ site.url }}/docs/truncate/" target="_blank">truncate</a> | This task just truncates your code from local repository to Salesforce organization. | 
<a href="{{ site.url }}/docs/delete/" target="_blank">delete</a> | This task removes the components that we have in our SalesForce organization, the components that will be removed should have no dependences.<br>It is suggested to use the truncate task previously |
{: .table-quick-start-features }

#### Retrieve tasks

Task | Description 
<a href="{{ site.url }}/docs/retrieve/" target="_blank">retrieve</a> | This task downloads files from your organization is based on your .xml package. If this file is missing, the system downloads files by default. |
{: .table-quick-start-features }

#### Unit-test tasks

Task | Description 
<a href="{{ site.url }}/docs/unit-test/" target="_blank">runTest</a> | This task executes all unit test classes in your organization but it is also possible execute some unit tests using wildcards or using an engine to infer what tests run. |
{: .table-quick-start-features }

#### Package Management tasks

Task | Description 
<a href="{{ site.url }}/docs/package-management/" target="_blank">installPackage</a> | Installs a package to an organization. |
<a href="{{ site.url }}/docs/package-management/" target="_blank">uninstallPackage</a> | Uninstalls a package from an organization. |
{: .table-quick-start-features }

#### Utils tasks

Task | Description 
<a href="{{ site.url }}/docs/utils/" target="_blank">execute</a> | This task is able to execute code apex. |
{: .table-quick-start-features }

### Development
Here you can find required information  that is enough to have a Gradle project with Enforce running. If you are interested in developing new features  or just compile and pack the source code, you will have helpful information  at <a href="{{ site.url }}/docs/development/" target="_blank">Development</a> page
