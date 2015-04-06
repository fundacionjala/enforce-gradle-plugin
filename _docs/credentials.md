---
layout: gradle
title: Credentials management
permalink: /docs/credentials/
---
## Management credential profile

To deploy code into an organization you need a credential, for so you should create an account in SalesForce.

To management your credentials there are two files called credentials.dat located one in your project directory another one in user home directory where your credentials are saved as json format with the next fields:
<ol>
	<ul>
		<li><strong>id</strong> is necessary to management your credential.</li>
		<li><strong>username</strong> is a SalesForce account.</li>
		<li><strong>password</strong> is your SalesForce password</li>
		<li><strong>token</strong> is your SalesForce token</li>
		<li><strong>sfdcType</strong> is a login type.</li>
		<li><strong>type</strong> is define if your credential is encrypted or no.</li>
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

In this file are saved all credentials with respective fields. It should be located into project directory or user home directory as priority use file that is in project directory.

## AddCredential task

This task adds a new credential into credentials.dat located in user home directory. There are two ways to add credentials one is by console another one is by parameters.

###  By console

You are able to add credentials encrypted and credentials decrypted. If you want to add a credential encrypted you should put option ***y*** in option: ***Encrypt credential(y/n, by default is encrypted):***

To add a new credential you should write the next command:

	$ gradle addCredential

### By parameters
When you add credential by parameters it is encrypted by default. To add a new credential by parameter you should use the next parameters:

	 -Pid is id of credential
	 -Pusername is your account
	 -Ppassword is your password
	 -Ptoken is your token

The command to add is:

	$ gradle addCredential -Pid=myidLZ 
			       -Pusername=juana@gmail.com
	                       -Ppassword=123456 
	                       -Ptoken=as:addCredential



## UpdateCredential  task
This task updates a credential from credentials.dat file located in user home directory.

### By console

To update a credential by console you should write  the next command and filling credentials fields by console.

	$ gradle updateCredential


### By parameters
If you want to update credential by parameters you should use the next parameters:

	 -Pid is id of credential.
	 -Pusername is your account.
	 -Ppassword is your password.
	 -Ptoken is your token.

Command:

	$ gradle updateCredential -Pid=myId 
			          -Pusername=user@organization.com
	                          -Ppassword=myPassword 
	                          -Ptoken=myToken


## Examples

### Add credential by console

Command:

	$ gradle addCredential

Output:

```bash
> Building 0% > :addCredential
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

Output:

```bash
:updateCredential

BUILD SUCCESSFUL
```

> **Note:** These credentials are added and updated in *credentials.dat* file that is located in user HOME directory.
