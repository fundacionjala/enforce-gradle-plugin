## 1.1.3 - 2015-10-12

### Features

* Add new parameter to show invalid files, not found files and files without xml. The new parameter is called ‘showValidatedFiles’ was added to deployment tasks, when it is with true value shows a list of invalid files, not found files and files without metadata, its value by default is true.

	This parameter can be used by parameter:

	>$gradle <Deployment_task_name> -PshowValidatedFiles=true

	Also It can be used into build.gradle file:

		 enforce {
		        srcPath = 'src'
		        showValidatedFiles = false
		  }

	Or into custom task :

		task simpleUpload(type:Upload) {
		         credentialId = "demo2015"
		         interceptors = ['truncateClasses']
		         excludes = "profiles"
		         showValidatedFiles = true
	     }

### Enhancements

* Add new TestSelector engine to infer TestClasses from ApexClass names by exploring class body. To infer TestClass from ApexClass names now there are two option:

	   to infer TestClasses from ApexClass from server:

	       -Pserver=true

   To infer TestClasses from ApexClass from local code:

	       -Pserver=false

	By default its value is false.



* Add 'specificFiles' parameter to Retrieve command. It is in order to retrieve components (vfcomponents, vfpages, classes, objects, profiles, permissionset, etc ) with the project's package.xml file in the Retrieve request and then download only the files/folders defined on '-Pfiles' parameter.

	E.G. Common use case: Retrieve profiles and permissionsets.

	Before having this parameter we were not able to retrieve only the profiles and permissionsets, because the following command does not send the project’s package.xml in the retrieve request, it generates a package.xml with the files defined on ‘Pfiles’ parameter.

	> $gradle retrieve -Pfiles=profiles,permissionsets


	It means that these components are retrieved with the SalesForce base permissions and does not with the ones defined on the project’s package.xml file (EG custom vfpages, custom objects, etc) that typically they are the components that we want to have in our local code.

	Now we are able to retrieve components based in the project’s package.xml file.

	> $gradle retrieve -Pfiles=profiles,permissionsets -PspecificFiles

	When we define -PspecificFiles parameter, the project’s package.xml goes in the Retrieve request and only the files/folders defined on -Pfiles parameters will be downloaded. It means that the retrieved components will be processed based in the

### Bugs fixed

* Upload task isn’t take in account metadata file of reports and dashboards folders.
* AddCredential task by console shows an exception on Mac OS.


## 1.1.2 - 2015-09-16

### Bugs fixed

* Once $gradle runTest -Pfiles=Class1.cls is executed, and in the inside of SalesForce Org there is a prefix, it is executing all unit test.
* Once $gradle runTest is executed. and there is a special character in test class,This  shows a "bad request" message.
* UninstallPackage does not uninstall package.
* Running uninstallPackage twice fails.

## 1.1.1 - 2015-08-21

### Bugs fixed

* Once upload task is executed and fileTracker files doesn't exist it shows an error message.
* Jenkins report plugin shows code coverage 0% once runTest task is executed.

## 1.1.0 - 2015-08-18

### Features

* A new task was added to see all credentials from home directory.

	    $gradle showCredentials

* New parameter called "help" was added to see the parameters that are supported by task.

	    $gradle <TASK_NAME> -Phelp

### Bugs fixed

* Once update task is executed and there isn't ".fileTracker.data" file in srcPath directory It shows an error.
* Task with type: ApexExecutor finished with error "input parameter invalid".
* Undeploy task fails when an SF object's field has a Default value referring to a SF object through a global variable like $Setup.

### Enhancements

* RunTest task - Test selector engine: It provides an engine to infer unit test classes according Apex class names by adding into the -Pfiles parameter, in order to reset the internal mapping used to infer each test class, there is a new param called ­-PrefreshMapping which takes a boolean value.
* RunTest task - Choose target report folder: It provides a new parameter to specify a folder path to save the test results.

### Known issues

* Just the following sub components are validated: CustomField, RecordType,  CompactLayout, ValidationRule from a Salesforce org.
* In the update task, During files filtering are not possible to exclude a deleted component.

## 1.0.9 - 2015-08-11

### Bugs fixed

* Once "execute task" is executed it shows a warning message that say input parameter is invalid.

## 1.0.8 - 2015-08-05

### Enhancements

* Take in account api version of Salesforce API from package.xml file to deploy code to an organization of Salesforce.


## 1.0.7 - 2015-07-28

### Features

* New option was added to disable subcomponents deletion once Update command is executed.

### Bugs fixed

* When the enforce version is changed and it is executed the task status shows all files as deleted and added.

### Known issues

* Once that files are deleted and update task is executed those files are not deleted from Salesforce organization.
* Deployment tasks are not deploying salesforce components when these are in sub folders into project directory.


## 1.0.6 - 2015-07-15

### Bugs fixed

*  400 Bad Request error is displayed when trying to delete validation rules, once update task is exceuted.


## 1.0.5 - 2015-06-22

### Bugs fixed

* Update command tries to delete packaged sub-components.
* @isTest annotation should be case insensitive.
* Special characters not deployed properly.
* The status task is showing empty space by every changed object .
* Update task doesn't support excludes parameter.
* Once that a document is added and the update task is executed, it didn't upload that document to your organization.
* Once that a document is deleted and the update task is executed, it deletes all documents from your organization.
* When I execute retrieve task using the -Pfiles with documents is not working.



### Known issues

* Once a folder of documents is deleted, it isn’t deleted from your organization.
* Update Command: 400 Bad Request error is displayed when trying to delete validation rules.


## 1.0.4 - 2015-06-10

### Features

* Delete task was created to delete components from your organization based in your package xml file or using folder parameter. It doesn't truncate.

### Bugs fixed

* The deploy task ends with percentage less than 100%.
* Support deleteTemporaryFiles parameter for Retrieve task.
* Update task fails for files that have spaces in their name.
* Update/Deploy/Upload command does not take in account the package.xml info for packaged objects.
* Excludes parameter into deploy task doesn't take in account documents component without extension into package xml file.
* The validation of files parameter value does not work when it sends documents, reports or dashboard.

### Enhancements

* Use unzip method of Gradle instead of unzip method of AntBuilder for the Retrieve task.

### Known issues

* When the enforce version is changed and status task is executed all files are shown as deleted for the moment to avoid it you should execute reset task.
* When you execute the delete task using files parameter and you use a folder name as a value, It is listing all files from the project.
* When we use the status task to show objects it shows an empty line between objects names.
* If a document or report or dashboard are deleted all documents or reports or dashboards are deleted for the update task.
* Update task does not support the excludes parameter.
* Once retrieve task is executed using files parameter with document file as value the validation doesn’t work.



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
