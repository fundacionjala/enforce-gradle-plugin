---
layout: gradle
title: Credentials management
permalink: /docs/credentials/
---
## Management credential profile

To deploy code to an organization, you need a credential. To do so you should create an account in SalesForce.


To manage your credentials, there are two files called credentials.dat that are located, one in your project directory, and the other in user home directory where your credentials are saved on json format with the following fields:
<ol>
	<ul>
		<li><strong>id</strong> is necessary to management your credential.</li>
		<li><strong>username</strong> is a SalesForce account.</li>
		<li><strong>password</strong> is your SalesForce password.</li>
		<li><strong>token</strong> is your SalesForce token.</li>
		<li><strong>sfdcType</strong> is a login type.</li>
		<li><strong>type</strong> defines whether  your credential is encrypted  or not..</li>
	</ul>
</ol>

### **credentials.dat file**
```json
{
 "default": {
        "username": "user@org.com",
        "password": "mypassword",
        "token": "2B9UvHKK2E1ky1EkkYBPFvbLM",
        "sfdcType" : "login",
        "type": "ecrypted"
    }
}
```

To choose a credentials.dat file you should use parameter called ***location*** where there are two option 'project' and 'home' by default is home.

The ***project*** option selected credentials.dat file located in your project directory.

The ***home*** option selected credentials.dat file located in your home directory.

## AddCredential task

This task adds a new credential into credentials.dat located in user home directory by default. There are two ways to add credentials one is by console another one is by parameters also, you are able to add credentials in home directory and project directory to do it you should use parameter called location.

###  By console

You are able to add credentials encrypted and credentials decrypted. If you want to add a credential encrypted you should put option ***y*** in option: ***Encrypt credential(y/n, by default is encrypted):*** also, you are able to choose where will save the credentials using the option: ***Credential location (home/project by default is home):***

To add a new credential you should write the next command:

	$ gradle addCredential

### By parameters

When you add credential by parameters it is encrypted by default also, you are able to choose the credentials.dat file where will add the credential using parameter called ***location*** by default is home directory. To add a new credential by parameter you should use the next parameters:

	 -Pid is id of credential
	 -Pusername is your account
	 -Ppassword is your password
	 -Ptoken is your token
	 -Pencrypted has 'y' and 'n' values (by default is 'y')
	 -Plocation is credentials.dat file location

The command to add is:

	$ gradle addCredential -Pid=myidLZ
					       -Pusername=juana@gmail.com
	                       -Ppassword=123456
	                       -Ptoken=asasddezcx10123
   	                       -Pencrypted=n
					       -Plocation=project


## UpdateCredential  task

This task updates a credential from credentials.dat file located in user home directory and project directory by default is home.

### By console

To update a credential by console you should write  the next command and filling credentials fields by console, if you want update a credential from project you should write ***'project'*** in the option: ***Credential location (home/project by default is home):***

	$ gradle updateCredential


### By parameters

If you want to update credential from project directory you should write ***'project'*** value in parameter called  ***location*** use the next parameters:

	 -Pid is id of credential.
	 -Pusername is your account.
	 -Ppassword is your password.
	 -Ptoken is your token.
	 -Plocation is credentials.dat file directory.

Command:

	$ gradle updateCredential -Pid=myId
			          -Pusername=user@organization.com
	                          -Ppassword=myPassword
	                          -Ptoken=myToken
	                          -Plocation=project

## ShowCredentials task

This task shows credentials registered at credentials.dat file located at home and project directories, it shows credentials from home directory by default, to see credentials from project directory you should use parameter location.

### Parameters

#### ***Location***

When you execute show credentials task, it shows credentials from home directory by default also, you are able to choose the credentials.dat file from project to choose this you should use parameter called ***location*** it has two values: 'project' and 'home'

	 -Plocation is credentials.dat file location

The command to show the credentials from project directory is:

	$ gradle showCredentials -Plocation=project

The command to show the credentials from home directory is:

	$ gradle showCredentials

or

	$ gradle showCredentials -Plocation=home

#### ***Status***

It is also possible to display the status of all credentials by the parameter ***status***

	$ gradle showCredentials -Pstatus

This parameter can filter credentials with two values: 'valid' and 'invalid'.

	$ gradle showCredentials -Pstatus=valid

or

	$ gradle showCredentials -Pstatus=invalid


## Examples

### Add credential by console

Command:

	$ gradle addCredential

Output:

```bash
> Building 0% > :addCredential
Credential location (home/project by default is home):project

Id:myId
UserName(example@email.com):john@email.com
Password:myPassword
Token:myToken
Login type (login/test/instance, by default login):login
Encrypt credential(y/n, by default is encrypted):y
:addCredential
	Credential was added successfully
BUILD SUCCESSFUL
```

### Add credential by parameters

Command:

	$ gradle addCredential -Pid=myidLZ
			       -Pusername=juana@gmail.com
			       -Ppassword=123456
			       -Ptoken=as:addCredential
			       -Plocation=project

Output:

```bash
:addCredential

BUILD SUCCESSFUL
```


### Update credential by console

Command:

	$ gradle updateCredential

Output:

```bash
:updateCredential
Credential location (home/project by default is home):project

id:mine
UserName(example@email.com):david@email.com
Password:myPassword
Token:MyToken
Login type (login/test/instance, by default login):login
:updateCredential
Credential was updated successfully
BUILD SUCCESSFUL
```

### Update credential by parameters

Command:

	$ gradle updateCredential -Pid=myId
			          -Pusername=user@organization.com
		    		  -Ppassword=myPassword
		    		  -Ptoken=myToken
		    		  -Plocation=project

Output:

```bash
:updateCredential

BUILD SUCCESSFUL
```

### Show credentials

Command:

	$ gradle showCredentials

Output:

```bash
:showCredentials
*********************************************
                Credentials
*********************************************

Id        : john.enforce
User name : john@enforce.com
Type      : Production/Developer

Id        : mine
User name : john@hotmail.com
Type      : Production/Developer

Id        : my
User name : john.cdlv@gmail.com
Type      : Production/Developer

Id        : recru
User name : admin@enforce.com
Type      : dev (Specified)

*********************************************
Those credentials are located at /home/john/credentials.dat

BUILD SUCCESSFUL
```

### Show credentials by status

Command:

	$ gradle showCredentials -Pstatus

Output:

```bash
:showCredentials
*********************************************
                Credentials
*********************************************

Id        : john.enforce
User name : john@enforce.com
Type      : Production/Developer
Status    : Ok

Id        : mine
User name : john@hotmail.com
Type      : Production/Developer
Status    : Ok

Id        : my
User name : john.cdlv@gmail.com
Type      : Production/Developer
Status    : Outdated
Message   : Invalid username, password, security token; or user locked out.

Id        : recru
User name : admin@enforce.com
Type      : dev (Specified)
Status    : Outdated
Message   : Invalid username, password, security token; or user locked out.

*********************************************
Those credentials are located at /home/john/credentials.dat

BUILD SUCCESSFUL
```

Or, using the command(*valid/invalid*):

	$ gradle showCredentials -Pstatus=valid

Output:

```bash
:showCredentials
*********************************************
                Credentials
*********************************************

Id        : john.enforce
User name : john@enforce.com
Type      : Production/Developer
Status    : Ok

Id        : mine
User name : john@hotmail.com
Type      : Production/Developer
Status    : Ok

*********************************************
Those credentials are located at /home/john/credentials.dat

BUILD SUCCESSFUL
```

> **Note:** You are able to choose the credentials.dat file for credentials management it can be from the home or project directory.
