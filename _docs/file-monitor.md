---
layout: gradle
title: Monitoring local changes
permalink: /docs/file-monitor/
---
## File monitor tasks

***Status task*** lets the user see what files have been changed as (new file, deleted file and updated file).

	$ gradle status

***Reset task*** lets the user restart the file monitor.

	$ gradle reset

## Status task

### Changed file

Task status shows files which have been added, modified or deleted. In the point below the file Force_com.app was modified, but not updated to the Organization, so the status  of that file is still “Changed file.”

command:

	$ gradle status

output:

```bash
    *********************************************
                  Status Files Changed
    *********************************************
    Force_com.app - Changed file
    *********************************************

    BUILD SUCCESSFUL
```

### Added, changed and deleted files

We have the same results if we deleted a file or we created a file, just the status of each file is different.

command:

	$ gradle status

output:

```bash
    > gradle status
    :status
    *********************************************
                  Status Files Changed
    *********************************************
    My-Layout.layout - New file
    TestClass.cls - Deleted file
    TestClass.cls-meta.xml - Deleted file
    *********************************************

    BUILD SUCCESSFUL
```

### No change on the files

If there isn't file modified the result should be like this:

command:

	$ gradle status

output:

```bash
    > gradle status
    :status

    BUILD SUCCESSFUL
```


### Using sort parameter:

Scenario:

We can change the way in which we see the updated files, sorting by file name.

command:

	$ gradle status -Psort=name

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
    Class1.cls - Deleted file
    Class2.cls - Changed file
    Trigger1.trigger - new file
    *********************************************

    Starting deploy...
    [==================================================]   100%
    The files were successfully deployed

    BUILD SUCCESSFUL
```

### Using help parameter:

Scenario:
A user is able to view the description and the parameters that contains the task by using the help.

command:

    $gradle status -Phelp

output:

```bash
    **********************************************************************
    
                               status task
    
    **********************************************************************
    
    Description   :
        Shows files which have been added, modified or deleted
    
    Documentation : 
        http://fundacionjala.github.io/enforce-gradle-plugin/docs/file-
        monitor
    
    Parameters :
        -Psort : Sort the files by name
            > gradle status -Psort=name
    
    **********************************************************************
    
    BUILD SUCCESSFUL

```

## Reset task

File monitor tracker will be reset.

command:

	$ gradle reset

output:

```bash
   :reset

    BUILD SUCCESSFUL
```


### Using help parameter:

Scenario:
A user is able to  view the description and the parameters that contains the task by using the help.

command:

    $gradle reset -Phelp

output:

```
    **********************************************************************
    
                                 reset task
    
    **********************************************************************
    
    Description   :
        File monitor tracker will be reset
    
    Documentation : 
        http://fundacionjala.github.io/enforce-gradle-plugin/docs/file-
        monitor/
    
    Parameters :
    
    **********************************************************************

    BUILD SUCCESSFUL


```
