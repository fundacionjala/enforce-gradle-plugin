---
layout: gradle
title: Monitoring local changes
permalink: /docs/file-monitor/
---
## File monitor tasks

***Status task*** let's user see what files have been changed as (new file, deleted file and updated file).

	$ gradle status

***Reset task*** let's user restarted the file monitor.

	$ gradle reset

## Status task

### Changed file

Task status shows files which have been added, modified or deleted. In the point above the file Force_com.app was modified but not updated to the Organization so the status of that file still on Changed file.

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

## Reset task

File monitor tracker will be reset.

command:

	$ gradle reset

output:

```bash
   :reset

    BUILD SUCCESSFUL
```
