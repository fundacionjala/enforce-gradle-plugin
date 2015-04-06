---
layout: gradle
title: Retrieve code
permalink: /docs/retrieve/
---

## Retrieve task

#### **Objective**
This task download files from your organization, based in your package xml, if there isn't this file, it downloads files by default:

* objects
* staticresources
* classes
* pages
* triggers
* components

If you don't have a package xml and you want to download only classes and triggers you should use the next task property into your build.gradle:

	foldersToDownload = "classes,triggers"
	
>***Note:*** This property only support folders name.

#### **Parameters**

This task has two parameters called ***files*** and ***destination*** , where the first parameter is used to download specifics files it supports files and folders, another one is used to indicate destination of files that will download.

## Executing Retrieve task

### Without parameters

Once retrieve task is executed without parameters It uses package xml to retrieve files from your organization.

	$ gradle retrieve

### Without package xml file

Once retrieve task is executed and there isn't a package xml it retrieves files by default:

* objects
* staticresources
* classes
* pages
* triggers
* components

We can set those values for ONLY THE FOLDERS that we want to retrieve.
The only thing that we have to do is:

* Add in the build.gradle file, the line with the next content.
```
	foldersToDownload = "classes,pages"
```
 
 * This means that folders which will be retrieved are classes and pages, you can add folders as you want to recover them.
```
        foldersToDownload = classes,triggers,pages...
```

### Using files parameter

This task retrieves the files that we want to retrieve, that means that we can recover a file o many specific files from the organization. This parameter support files and folders.

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

This task retrieves the files that you want to retrieve in a specific folder from your organization.

    $ gradle retrieve -Pfiles=classes,objects -Pdestination=relative/path
    $ gradle retrieve -Pfiles=classes/Class.cls -Pdestination=/absolute/path

When you use destination parameter you can use relative or absolute path.


>***Notes:***
>Once that task is executed successfully all the files are copied inside the your local repository replacing the existing files.
>If you want to see only the files retrieved you can find them on build folder as a zip file with the name zipRecovered.zip

## Examples

### Without parameters

Scenario:

In the case what you want to retrieve all files of classes, objects, triggers, pages, components and staticresources folders.

To cover this scenario you should execute the next command and package.xml should be in your local repository:

	$ gradle retrieve

Output:

```bash
    :retrieve
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________

    Warning: your files will be replaced
    Do you want continue? (y/n):
    > Building 0% > :retrieve y
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

In this case this task will retrieve all pages, Class1.cls and Trigger1.trigger from your organization to relative/path.

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

When you want to retrieve a file and it doesn't exist in your organization it shows a warning message.

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
