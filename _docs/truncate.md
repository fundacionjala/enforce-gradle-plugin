---
layout: gradle
title: Truncate
permalink: /docs/truncate/
---
## Truncate task

#### **Objective**
This task just truncates your code from local repository to Salesforce organization.

#### **Parameters**
This task has three parameters called **excludes**, **files** and **help**. You can choose file or files to truncate it. Also, you are able to exclude files by name by folder and using wildcards. Also for more information about this task you can use help parameter.

Those parameters can be used as task property or into your build script.

## Executing truncate task

#### **Without parameters**

User can truncate just components by default. If you want to run this task, you should write the next command:

	$ gradle truncate

> **Note:** This command , when executed, deploys files  truncated to your organization.

#### **Using files parameter**

A user can truncate a file or files that you want.

	$ gradle truncate -Pfiles=classes/Class1.cls
	$ gradle truncate -Pfiles=classes,objects,triggers/Trigger2.trigger

> **Note:** Many files, folders and wildcards can be added.

#### **Using excludes parameter**

A user can truncate files to organization excluding files by their name, by folders, or using wildcards.

This parameter can exclude files by:

 ***folder*** if you want to exclude a folder or folders, you should write the following parameter:

        $ gradle truncate -Pexcludes=classes
        $ gradle truncate -Pexcludes=classes,objects.

 ***file*** if you want to exclude a file or files, You should write the following parameter:

        $ gradle truncate -Pexcludes=classes/Class1.cls
        $ gradle truncate -Pexcludes=classes/Class1.cls,objects/Object1__c.object

 ***wildcard*** if you want to exclude using wildcard, you should write the following parameter:

        $ gradle truncate -Pexcludes=classes/**
        $ gradle truncate -Pexcludes=objects/*.object
        $ gradle truncate -Pexcludes=**/*Account*/**
        $ gradle truncate -Pexcludes=**/*.cls

#### **Using help parameter**

		$gradle truncate -Phelp

Examples:

Without parameters
When you run this command, all files are truncated to an organization.

    $ gradle truncate

Output:

    :truncate
    ___________________________________________
    	Username: juan.perez@mail.com
    	Login type: login
    ___________________________________________


    Starting truncate process
    [==================================================]   89/89(100%)
    The files were successfully truncated

    BUILD SUCCESSFUL


Using files parameter
This command just truncates classes and triggers files.

    $ gradle truncate -Pfiles=classes,triggers

Output:

    :truncate
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________


    Starting truncate process
    [==================================================]   70/70(100%)
    The files were successfully truncated

    BUILD SUCCESSFUL


Note: Many Folders can be added.

This command truncates class1.cls and trigger1.trigger

    $ gradle truncate -Pfiles=classes/class1.cls,triggers/trigger1.trigger

Output:

    :truncate
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________


    Starting truncate process
    [==================================================]   2/2(100%)
    The files were successfully truncated

    BUILD SUCCESSFUL


Note: Many Files can be added.

This command truncates Class1.cls and all triggers and all objects.

    $ gradle truncate -Pfiles=classes/Class1.cls,triggers,objects

Output:

    :truncate
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________


    Starting truncate process
    [==================================================]   76/76(100%)
    The files were successfully truncated

    BUILD SUCCESSFUL

This command truncates all classes all triggers and all objects.

    $ gradle truncate -Pfiles=classes/**,triggers,objects

Output:

    :truncate
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________


    Starting truncate process
    [==================================================]   76/76(100%)
    The files were successfully truncated

    BUILD SUCCESSFUL


Using excludes parameter
This commando truncates all files excluding all classes.

    $ gradle truncate -Pexcludes=classes

Output:

    :truncate
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________

     Starting truncate process
     [==================================================]   56/56(100%)
     The files were successfully truncated

     BUILD SUCCESSFUL


This command truncates all files excluding Class1.cls

    $ gradle truncate -Pexcludes=classes/Class1.cls

Output:

    :truncate
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________

   Starting truncate process
   [==================================================]   75/75(100%)
   The files were successfully truncated

    BUILD SUCCESSFUL


This command truncates all files excluding all classes.

    $ gradle truncate -Pexcludes=classes/**

Output:

    :truncate
    ___________________________________________
            Username: juan.perez@mail.com
            Login type: login
    ___________________________________________

    Starting truncate process
    [==================================================]   100%
    The files were successfully truncated

    BUILD SUCCESSFUL


Using help parameter

    $ gradle truncate -Phelp

Output:

	:truncate
	*******************************************************************

	                         truncate task

	*******************************************************************

	Description :
	  This task truncates classes, objects, triggers, pages, components, 
	  workflows and tabs from your code

	Documentation :
	    fundacionjala.github.io/enforce-gradle-plugin/docs/truncate

	Parameters:
	  -Pfiles : Select which files will be executed by the process
	     > gradle truncate files -Pfiles=classes/Class1.cls
	     > gradle truncate files -Pfiles=*/Class1.cls,classes/Class2.cls
	     > gradle truncate files -Pfiles=classes/*.cls,triggers/*.cls
	     > gradle truncate files -Pfiles=classes/**
	  -Pexcludes : Select which files they will be ignored by the process
	     > gradle truncate excludes -Pexcludes=classes/Class1.cls
	     > gradle truncate excludes -Pexcludes=*/Class1.cls,*/Class2.cls
	     > gradle truncate excludes -Pexcludes=classes/*.cls,*/*.trigger
	     > gradle truncate excludes -Pexcludes=classes/**

	*******************************************************************

	BUILD SUCCESSFUL


