---
layout: gradle
title: Utils
permalink: /docs/utils/
---
## Execute group has the next task:

#### **Objective**

This task is able to execute code apex.

#### **Parameters**

This task has two parameters called ***input*** and ***output*** where the first is used to set a file or inline code that you want to execute, another one is used to set a file path and get results in this file.

## Execute task

This task executes a code block of Apex using input parameter to set file path of source code

	$ gradle execute -Pinput="src/classes/ClassOne.cls"
	
> **Note:** Input parameter supports absolute and relative path

output:

```bash
:execute
___________________________________________
        Username: juan.perez@mail.com
        Login type: login
___________________________________________

Executing Apex code at: src/classes/ClassOne.cls
Output result:
29.0 ALL,ERROR;APEX_CODE,ERROR;DB,INFO
Execute Anonymous: public with sharing class ClassOne {
Execute Anonymous:
Execute Anonymous:   public ObjectOne__c objectOne = new ObjectOne__c();
Execute Anonymous:
Execute Anonymous:   public ClassOne()
Execute Anonymous:   {
Execute Anonymous:     objectOne.Field1__c = 'field1';
Execute Anonymous:     insert objectOne;
Execute Anonymous:   }
Execute Anonymous: }
18:46:33.046 (46811716)|EXECUTION_STARTED
18:46:33.046 (46819740)|CODE_UNIT_STARTED|[EXTERNAL]|execute_anonymous_apex
18:46:33.047 (47049936)|CODE_UNIT_FINISHED|execute_anonymous_apex
18:46:33.048 (48084935)|EXECUTION_FINISHED


BUILD SUCCESSFUL
```

### Execute task with inline code and output parameter

This task executes a code block of Apex using input parameter to set inline code and output parameter to set a file path and get results in this file.

	$ gradle execute -Pinput="system.debug('hello world');"
			 -Poutput=build/hello.txt
			 
> **Note:** The output path must exist

output:

```bash
:execute
___________________________________________
        Username: juan.perez@mail.com
        Login type: login
___________________________________________

Apex output available at:build/hello.txt

BUILD SUCCESSFUL
```
