---
layout: gradle
title: Update
permalink: /docs/update/
---
## Update task

#### **Objective**
This task just deploys changed code as: deleted code, added code and updated code, for example if you changed a class called MyClass.cls and you deleted a trigger called MyTrigger.trigger This deletes MyTrigger.trigger and It updates MyClass.cls according file tracker in your organization.

#### **Parameters**
This task has two parameters called **excludes** and **folders**. You can choose folder o folders to update it. Also you are able to exclude files by name by folder and using wildcards.

Those parameters can be used as task property or into your build script.

## Executing update task

#### **Without parameters**

User can update all files that are listed on file tracker like file changed, deleted or new one. If you want to run this task, you should write the next command:

	$ gradle update

> **Note:** This command executed deploys files listed by file tracker to your organization.

#### **Using folders parameter**

User can update files that are within the folder user wants and listed on file tracker like file changed, deleted or new one.

	$ gradle update -Pfolders=classes
	$ gradle update -Pfolders=classes,objects,triggers

> **Note:** Many Folders can be added

#### **Using excludes parameter**

User can update all files that are listed on file tracker like file changed, deleted or new one, excluding files by its name, by folders or using wildcards.

This parameter can exclude files by:

 ***folder*** if you want to exclude a folder or folders, You should write the next parameter:

        $ gradle update -Pexcludes=classes
        $ gradle update -Pexcludes=classes,objects.

 ***file*** if you want to exclude a file or files, You should write the next parameter:

        $ gradle update -Pexcludes=classes/Class1.cls

 ***wildcard*** if you want to exclude using wildcard, You should write the next parameter:

        $ gradle update  -Pexcludes=classes/**
        $ gradle update  -Pexcludes=objects/*.object
        $ gradle update  -Pexcludes=**/*Account*/**
        $ gradle update  -Pexcludes=**/*.cls

## Examples:

### without parameters:

Scenario:

In this case four files were changed from my local directory (object1__c.object, Page1.page, Trigger1.trigger and Class1.cls) and I want to deploy only files updated to my organization and not all files.


command:

	$ gradle update

output:

```bash
    :update
    ___________________________________________
            Username: juan.perez.f@gmail.com
            Login type: login
    ___________________________________________

    *********************************************
                  Status Files Changed
    *********************************************
    Object1__c.object - Changed file
    Page1.page - Changed file
    Trigger1.trigger - Changed file
    Class1.cls - Changed file
    *********************************************

    Starting deploy...
    [==================================================]   100%
    The files were successfully deployed

    BUILD SUCCESSFUL
```

### Using folders parameter:

Scenario:

In this case Class1.cls was changed and Class2.cls was deleted, Trigger1.trigger was added and Object1__c.object was changed. and I just want to deploy classes and triggers updated and not the others files changed. 

To cover this scenario you should write the next command:


	$ gradle update -Pfolders=classes,triggers

output:

```bash
    :update
    ___________________________________________
            Username: juan.perez.f@gmail.com
            Login type: login
    ___________________________________________

    *********************************************
                  Status Files Changed
    *********************************************
    Trigger1.trigger - new file
    Class1.cls - Deleted file
    Class2.cls - Changed file
    *********************************************

    Starting deploy...
    [==================================================]   100%
    The files were successfully deployed

    BUILD SUCCESSFUL
```

### Using excludes parameter:

Scenario:

In this case Class1.cls was added, Class2.cls and Class3.cls was deleted and Class4.cls was changed and I want to deploy all classes updated less Class4.cls

To cover this scenario you should use the next command:

	$ gradle update -Pexcludes=**/Class4.cls

output:

```bash
    > gradle update
    :update
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________

    *********************************************
              Status Files Changed
    *********************************************
    Class1.cls - new file
    Class2.cls - deleted file
    Class3.cls - deleted file
    *********************************************
    *********************************************
                  Files excluded
    *********************************************
    Class4.cls -  excluded
    *********************************************
    Starting deploy...
    [==================================================]   100%
    The files were successfully deployed

    BUILD SUCCESSFUL

```

Scenario:

In this case classes and obejcts were updated and I just want to deploy objects. 

To cover this scenario you should write the next command:

	$ gradle update -Pexcludes=classes

output:

```bash
    > gradle update
    :update
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________

   *********************************************
              Status Files Changed
    *********************************************
    Account.object - Changed file
    *********************************************
    *********************************************
                  Files excluded
    *********************************************
    Class1.cls -  excluded
    Class2.cls -  excluded
    *********************************************
    Starting deploy...
    [==================================================]   100%
    The files were successfully deployed

BUILD SUCCESSFUL

```
