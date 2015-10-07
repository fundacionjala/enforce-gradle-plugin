---
layout: gradle
title: Delete
permalink: /docs/delete/
---
## Delete task

#### **Objective**
This task removes the components that we have in our SalesForce organization, the components that will be removed should have no dependences.
It is suggested to use the truncate task previously

#### **Parameters**
This task has four parameters called **excludes**, **files**, **validate** and **help**. 

## Executing delete task

#### **Without parameters**

User can remove all components by default. If you want to run this task, you should write the next command:

	$ gradle delete


#### **Using files parameter**

A user can remove a files that you want.

	$ gradle delete -Pfiles=classes/Class1.cls
	$ gradle delete -Pfiles=classes,objects,triggers/Trigger2.trigger

> **Note:** Many files, folders and wildcards can be added.

#### **Using excludes parameter**

A user can remove files from an organization excluding files by their name, by folders, or using wildcards.

This parameter can exclude files by:

 ***folder*** if you want to exclude a folder or folders, you should write the following parameters:

        $ gradle delete -Pexcludes=classes
        $ gradle delete -Pexcludes=classes,objects.

 ***file*** if you want to exclude a file or files, You should write the following parameters:

        $ gradle delete -Pexcludes=classes/Class1.cls
        $ gradle delete -Pexcludes=classes/Class1.cls,objects/Object1__c.object

 ***wildcard*** if you want to exclude using wildcard, you should write the following parameters:

        $ gradle delete -Pexcludes=classes/**
        $ gradle delete -Pexcludes=objects/*.object
        $ gradle delete -Pexcludes=**/*Account*/**
        $ gradle delete -Pexcludes=**/*.cls

#### **Using validate parameter**
Validates that files to deleted exist within their Org (Default value is false)

		$gradle delete -Pvalidate=false
		
#### **Using help parameter**
A user can view the description and the parameters that contains the task by using the help

		$gradle delete -Phelp

Examples:

Without parameters
When you run this command, all files are removed to an organization.

    $ gradle delete

Output:

    :delete
    ___________________________________________
    	Username: juan.perez@mail.com
    	Login type: login
    ___________________________________________


    Starting delete process
    [==================================================]   89/89(100%)
    The files were successfully delete

    BUILD SUCCESSFUL


Using files parameter
This command just delete classes and triggers files.

    $ gradle delete -Pfiles=classes,triggers

Output:

    :delete
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________


    Starting delete process
    [==================================================]   70/70(100%)
    The files were successfully deleted

    BUILD SUCCESSFUL


Note: Many Folders can be added.

This command delete class1.cls and trigger1.trigger

    $ gradle delete -Pfiles=classes/class1.cls,triggers/trigger1.trigger

Output:

    :delete
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________


    Starting delete process
    [==================================================]   2/2(100%)
    The files were successfully deleted

    BUILD SUCCESSFUL


Note: Many Files can be added.

This command delete Class1.cls and all triggers and all objects.

    $ gradle delete -Pfiles=classes/Class1.cls,triggers,objects

Output:

    :delete
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________


    Starting delete process
    [==================================================]   76/76(100%)
    The files were successfully deleted

    BUILD SUCCESSFUL

This command delete all classes all triggers and all objects.

    $ gradle delete -Pfiles=classes/**,triggers,objects

Output:

    :delete
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________


    Starting delete process
    [==================================================]   76/76(100%)
    The files were successfully deleted

    BUILD SUCCESSFUL


Using excludes parameter
This command deletes all files excluding all classes.

    $ gradle delete -Pexcludes=classes

Output:

    :delete
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________

     Starting delete process
     [==================================================]   56/56(100%)
     The files were successfully deleted

     BUILD SUCCESSFUL


This command delete all files excluding Class1.cls

    $ gradle delete -Pexcludes=classes/Class1.cls

Output:

    :delete
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________

   Starting delete process
   [==================================================]   75/75(100%)
   The files were successfully deleted

    BUILD SUCCESSFUL


This command deletes all files excluding all classes.

    $ gradle delete -Pexcludes=classes/**

Output:

    :delete
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________

    Starting delete process
    [==================================================]   100%
    The files were successfully deleted

    BUILD SUCCESSFUL


Using help parameter

    $ gradle delete -Phelp

Output:
	
	**************************************************************************
    
                                   delete task
    
    **************************************************************************
    
    Description   :
        Delete files that exist in our org
    
    Documentation : 
        fundacionjala.github.io/enforce-gradle-plugin/docs/delete
    
    Parameters :
        -Pfiles : Select which files will be executed by the process
            > gradle delete -Pfiles=classes/Class1.cls
            > gradle delete -Pfiles=classes/Class1.cls,classes/Class2.cls
            > gradle delete -Pfiles=classes/*.cls,triggers/*.trigger
            > gradle delete -Pfiles=classes/**
        -Pexcludes : Select which files they will be ignored by the process
            > gradle delete -Pexcludes=classes/Class1.cls
            > gradle delete -Pexcludes=classes/Class1.cls,classes/Class2.cls
            > gradle delete -Pexcludes=classes/*.cls,triggers/*.trigger
            > gradle delete -Pexcludes=classes/**
        -Pvalidation : Validates that files to deleted exist within their Org
            > gradle delete -Pvalidate=true
            > gradle delete -Pvalidate=false
    
    **************************************************************************
    
    BUILD SUCCESSFUL



