---
layout: post
title: How to use an Organization with custom domain
categories: []
tags: []
published: True

---

## Scenario
 
When the user create an account using wizard of plugin only has two options about  organization domain:
-	login
-	test
 
The problem that I found when I was using plugin, is that some projects uses sandboxes and create accounts in that instances, if you try login, for example :
 
-	test.salesforce.com, some accounts can't login
 
But if you use another domain for example
 
-	cs15.salesforce.com, this works
 
What I did is I change sfdcType domain of credential.dat 
 
default credential

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
 
modified credential

```json 
{
"default": {
        "username": "user@org.com",
        "password": "mypassword",
        "token": "2B9UvHKK2E1ky1EkkYBPFvbLM",
        "sfdcType" : "cs15",
        "type": "ecrypted"
    }
}
```
