---
layout: gradle
title: Authentication
permalink: /docs/auth/
---
## Authentication to run tasks

#### **Objective**
To run deployment tasks, you need login into your organization. To do so, you have three options for authentication:
<ul>
	<li>Using a credential by default, To do this you should add a credential with default id.</li>
	<li>Using a specific credential, To do this you should use credential id.</li>
	<li>Using parameters username and password also, optionally you can use user token.</li>
</ul>

#### **Parameter**

To use a credential, you should use a parameter called ***credentialId*** and send your credential id to run a deployment task.

## Forms of authentication

### Using credential by default

You are able to run task with a credential by default where you don’t need to use ***credentialId*** parameter. To do this, you should have a credential with default id into credentials.dat file.

For example when you run the next task you are using a credential by default,  You don't need write *'credentialId'* parameter.

	$ gradle deploy
	$ gradle undeploy

### Using a specific credential

You are able to run tasks using a specific credential. To do so, you should use a credential Id parameter with your id, in this case there is a credential in ‘credentials.dat’ file with an id called ‘myId’

	$ gradle deploy -PcredentialId=myId
	$ gradle undeploy -PcredentialId=myId

### Using parameters

You are able to run task using parameters of credential, to do this you should send the next parameter: *username and password and optionally token*.

	$ gradle deploy -Pusername=juan.perez@jalasoft.com
			-Ppassword=123qweJuan
			-Ptoken=qweyh65fd43789sw

## Examples:

### Using a credential by default

Scenario:

In this case, if you want to run *‘deploy task’* using your credential by default, you should have a credential saved into credentials.dat with ***‘default’*** id.

Command:

	$ gradle deploy

Output:

```bash
    :deploy
    ___________________________________________
    __________________________________________
            Username: juan.perez@jalasoft.com
            Login type: login
    ___________________________________________


    Starting deploy...
    [==================================================]   100%
    The files were successfully deployed

    BUILD SUCCESSFUL
```

### Using an specific credential

Scenario:

In this case, I want to run a task using my credential that is saved as *‘myId’* id

Command:

	$ gradle deploy -PcredentialId=myId

Output:

```bash
    :deploy
    ___________________________________________
    __________________________________________
            Username: juan.perez@jalasoft.com
            Login type: login
    ___________________________________________


    Starting deploy...
    [==================================================]   100%
    The files were successfully deployed

    BUILD SUCCESSFUL
```

### Using parameters

Scenario:

In this case, I want to run *‘deploy task’* with my new credential that were created on Salesforce, it isn’t saved in the credentials.dat file.

Command:

	$ gradle deploy -Pusername=juan.perez@jalasoft.com
			-Ppassword=123qweJuan
			-Ptoken=qweyh65fd43789sw

Output:

```bash
    :deploy
    ___________________________________________
    __________________________________________
            Username: juan.perez@jalasoft.com
            Login type: login
    ___________________________________________


    Starting deploy...
    [==================================================]   100%
    The files were successfully deployed

    BUILD SUCCESSFUL
```
