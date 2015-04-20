## 1.0.0 - 2015-04-06


## Features

* Store encrypted Salesforce credentials identified by an Id which it can be used in the EnForce Gradle tasks.
* Deploy Salesforce project code, no matter if there are dependencies between objects, pages, classes, etc., this task will try to break those dependencies in order to make possible to deploy the code into an organization.
* Upload Salesforce project code as it is, similar to the functionality that the Migration Tool provides.
* Undeploy code from an organization, that means remove the source code from a Salesforce organization.
* Track local changes on project files and upload them with one task.
* Run unit tests and provide a code coverage and unit test reports.
* Generate compatible code coverage(Cobertura format) and unit test(JUnit format) results for CI servers.
* A way to change the source code before the upload cycle(interceptor pattern).
* Install or uninstall Salesforce packages.
