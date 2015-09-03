---
layout: gradle
title: Retrieve code
permalink: /docs/retrieve/
---

## Retrieve task

#### **Objective**

This task download files from your organization is based on your .xml package. If this file is missing, the system downloads files by default:

* objects
* staticresources
* classes
* pages
* triggers
* components

If you don’t have an xml package and you want to download classes and triggers only, you should use the next task property into your build.gradle:

	foldersToDownload = "classes,triggers"
	
>***Note:*** This property only support folders name.

#### **Parameters**

This task has three parameters called ***files***, ***destination*** and ***all***, where the first parameter is used to download  specifics files. It supports files and folders, the second one is used to indicate destination of files that will be downloaded and the third one is used to avoid confirm message and overwrite existing files.

## Executing Retrieve task

### Without parameters

Once retrieve task is executed without parameters, it uses xml package to retrieve files from your organization.

	$ gradle retrieve

### Without package xml file

Once retrieve task is executed and there isn’t an xml package, it retrieves files by default:

* objects
* staticresources
* classes
* pages
* triggers
* components

We can set those values ONLY FOR THE FOLDERS that we want to retrieve.

The only thing that we have to do is:

* To add the line with the next content to the build.gradle file.
```
	foldersToDownload = "classes,pages"
```

 * This means that folders which will be retrieved are classes and pages, you can add folders if you want to recover them.
```
        foldersToDownload = classes,triggers,pages...
```

### Using files parameter

This task retrieves the files that we want to retrieve, that means that we can retrieve a file with many specific files from the organization. This parameter support files and folders.

Files:      
      
      $ gradle retrieve -Pfiles=classes/Class1.cls
      $ gradle retrieve -Pfiles=classes/Class1.cls,objects/Account.object
      
Folders:      
      
      $ gradle retrieve -Pfiles=objects
      $ gradle retrieve -Pfiles=objects,triggers
      
Files and folders:

      $ gradle retrieve -Pfiles=objects,classes/Class1.cls
      $ gradle retrieve -Pfiles=objects,triggers,triggers/Trigger1.trigger,classes/Class1.cls
      
You can add many files or many folders as you want also you can use files an folders at the same time.

### Using destination parameter

This task retrieves the files that you want to retrieve in a specific folder of your organization.

    $ gradle retrieve -Pfiles=classes,objects -Pdestination=relative/path
    $ gradle retrieve -Pfiles=classes/Class.cls -Pdestination=/absolute/path

When you use destination parameter you can use a relative or absolute path.

>***Notes:***
>Once that task is executed successfully all the files are copied inside the your local repository replacing the existing files. 

>There is a warning message to confirm if you want to overwrite your local files, if you don't want to see this message you should use a parameter called ***all*** with 'true' value.

>If you want to see only the retrieved files, you can find them on a build folder as a zip file with the name zipRecovered.zip


#### **Using help parameter**

		$gradle truncate -Phelp
		
## Examples

### Without parameters

Scenario:

You want to retrieve all files of classes, objects, triggers, pages, components and static resources folders.

To cover this scenario, you should execute the following command and the xml package should be in your local repository:

	$ gradle retrieve

Output:

```bash
    :retrieve
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________

    Warning: All files according to package will be downloaded
    > Building 0% > :retrieve  Do you want to continue? (y/n) : y
    Starting retrieve...
    Waiting for retrieve result...
    Retrieve result completed

    BUILD SUCCESSFUL
```

### Using files parameter

Scenario:

In the case that you want to retrieve Class1.cls and Trigger1.cls 

Command:

	$ gradle retrieve -Pfiles=classes/Class1.cls,triggers/Trigger1.trigger
	
Output:


```bash
    :retrieve
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________

    Starting retrieve...
    Waiting for retrieve result...
    Retrieve result completed

    BUILD SUCCESSFUL
```


### Using destination parameter

Scenario:

In this case, this task will retrieve all pages, Class1.cls and Trigger1.trigger from your organization to a relative path.

Command:

	 $ gradle retrieve -Pfiles=pages,components,classes/Class1.cls,
	                   triggers/Trigger1.trigger -Pdestination=relative/path

Output:

```bash
    :retrieve
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________

    Starting retrieve...
    Waiting for retrieve result...
    Retrieve result completed

	BUILD SUCCESSFUL
```

Scenario:

When you want to retrieve a file and it doesn’t exist in your organization, the system shows a warning message.

command:

	$ gradle retrieve -Pfiles=classes/class1.cls

output:

```bash
    :retrieve
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________

    Starting retrieve...
    Waiting for retrieve result...
    Retrieve result completed
    WARNING: Entity of type 'ApexClass' named 'class1' cannot be found

    BUILD SUCCESSFUL
```


Using help parameter

    $ gradle delete -Phelp

Output:

    **************************************************************************
    
                                  retrieve task
    
    **************************************************************************
    
    Description   :
        Download files from your organization is based on your .xml package
    
    Documentation : 
        http://fundacionjala.github.io/enforce-gradle-plugin/docs/retrieve
    
    Parameters :
        -Pfiles : Select which files will be executed by the process
            > gradle retrieve -Pfiles=classes/Class1.cls
            > gradle retrieve -Pfiles=classes/Class1.cls,classes/Class2.cls
            > gradle retrieve -Pfiles=classes/*.cls,triggers/*.trigger
            > gradle retrieve -Pfiles=classes/**
        -Pdestination : Choose a directory in where save the run test results, 
        by default they are saved in build/report directory
            > gradle retrieve -Pdestination=/home/temporalReports/
    
    **************************************************************************
    
    BUILD SUCCESSFUL

