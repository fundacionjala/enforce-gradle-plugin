
## 1.0.3 - 2015-05-25

### Features

* There is an option to clean the temporary folders/files generated when tasks are executed. 
Since now the build folder will be deleted after any operation unless the flag ***"deleteTemporaryFiles"*** is set to false, default value is true.

* Improve status tasks report grouping and sorting results. When you run the task status, the list of files should be grouped by states, also it is possible to order by filename using the parameter ***"sort"***

### Bugs fixed

* Undeploy task does not take in account the package.xml info for packaged objects.
* Coverage report should be red when under 75%
* Class name in unit test report is showing as null value 
* Upload/Deploy tasks don’t support Reports. Additionally. It supports documents and dashboards.


### Known issues

* The undeploy task doesn’t support document components.
* Not related files to Report components are not filtered.
* The build folder is not deleted automatically when the the retrieve task is executed.


## 1.0.2 - 2015-05-15

### Features

* Update task now is able to track custom fields, it uploads custom fields that were added, removed and updated.  This task only was able to track components like objects, classes, triggers, etc. 

It is able to track object elements like: 

	custom fields, fields, fieldsets, compactLayouts, businessProcesses and validationRules.
	
 Not supported elements:
 
	    actionOverrides, searchLayouts, sharingRecalculations	


### Enhancements

* Truncate process was improved. Now the file content is copied before starting truncate process, after all this process the truncate content is written in file.

* Custom permission component now is supported by update, upload, deploy, undeploy and retrieve tasks.

### Bugs fixed

* Component's truncate process should keep attributes.


## 1.0.1 - 2015-04-21

### Features

* ***location*** parameter was added for credential management from user home directory or project directory. Now you are able to choose a credentials.dat file that you want to management credentials. It has home directory by default.

### Enhancements

* ***username*** field validation was improved now support double dot after @.
* To add credentials and, to login using parameters now their user token isn't mandatory it's optional.
* ***all*** parameter was added  into Upload task It has two values 'true' and 'false' by default is false.
* Sets the same code coverage value on jenkins plugin and html report.
* Unit test report was improved now It shows you the methods with its class. And Its description also was improved with more details.
* Warning message of Retrieve task was improved.

### Bugs fixed
* The issue about UTF-8 encoding on objects files was fixed.


## 1.0.0 - 2015-04-06

### Features

* Store encrypted Salesforce credentials identified by an Id which it can be used in the EnForce Gradle tasks.
* Deploy Salesforce project code, no matter if there are dependencies between objects, pages, classes, etc., this task will try to break those dependencies in order to make possible to deploy the code into an organization.
* Upload Salesforce project code as it is, similar to the functionality that the Migration Tool provides.
* Undeploy code from an organization, that means remove the source code from a Salesforce organization.
* Track local changes on project files and upload them with one task.
* Run unit tests and provide a code coverage and unit test reports.
* Generate compatible code coverage(Cobertura format) and unit test(JUnit format) results for CI servers.
* A way to change the source code before the upload cycle(interceptor pattern).
* Install or uninstall Salesforce packages.
