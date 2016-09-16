---
layout: gradle
title: Installation
permalink: /docs/installation/
---


### Requirements
 * [Java JDK or JRE, version 7 or higher](http://java.com/en/)
 * [Gradle version 2.0 or higher](https://gradle.org/docs/current/userguide/installation.html)
 * Internet access ([configure Gradle if you are using a Proxy](https://gradle.org/docs/current/userguide/build_environment.html))
 * Valid user account in a Salesforce Organization and the related [Security Token](https://help.salesforce.com/apex/HTViewHelpDoc?id=user_security_token.htm)

## Create a build.gradle file for your project

Enforce is a Gradle plugin, so it is required to have build.gradle file where the dependency to the Enforce plugin is defined, and makes it possible to start using Enforce features. Below you can find an example for a basic build.gradle  file:
{% highlight groovy %}
   buildscript {
       repositories {
           mavenCentral()
           jcenter()
       }
       dependencies {
           classpath 'org.fundacionjala.gradle.plugins.enforce:enforce-gradle-plugin:1.1.5'
       }
   }
   
   apply plugin: 'enforce'
   
   enforce {
     srcPath = 'src'
     deleteTemporaryFiles = true
   }
{% endhighlight %}

## Just a file, where is the program?
Gradle provides a dependency mechanism which allows us to download  Gradle plugins and its dependencies from a Maven Repository, the first time that a task from the build.gradle  is executed, Gradle will automatically download  the Enforce plugin and its dependencies, which will all be stored  in a cache in your machine. this mechanism  avoids to download  the same file more than once.

You can try executing the next command:

{% highlight bash %}
   $ gradle tasks
{% endhighlight %}


## Setup Salesforce organization credentials
Enforce requires access to a Salesforce Organization, so you need to provide the credentials and [Security Token](https://help.salesforce.com/apex/HTViewHelpDoc?id=user_security_token.htm) for an user with System Administrator rights.

Enforce provides a task for create credentials, which are stored on your home folder and can be encrypted also.

{% highlight bash %}
   $ gradle addCredential
{% endhighlight %}


The task will ask for the credential values to be introduced, the Id represents the key that is used to identify the credential. This way, it is possible to store credentials for several organizations but you need to keep in mind that the Id must be unique. You can find <a href="{{ site.url }}/docs/credentials/" target="_blank">here</a> more information about Credential  Management.

## Integrate your Salesforce project with Enforce

The easy way to integrate Enforce with your Salesforce project source code is making the build.gradle file part of your source code. That implies moving the build.gradle  file to your project folder.
Now, you can configure the path of your project code on Enforce, open your build.gradle file and add the next lines at the end:

{% highlight groovy %}
  enforce {
    srcPath = 'src'
  }
{% endhighlight %}

In this case, we are assuming the next folder structure for your project:

{% highlight text %}
MySalesforceProject
|- src
   |- classes
   |- pages
   |- package.xml
|- build.gradle
  
{% endhighlight %}

In this case, the _srcPath_ property is pointing at the _src_ folder, which contains the Salesforce project.

<div class="note info">
  <p>Remember that a Salesforce project is the one that contains at least an <em>package.xml</em> , and valid Salesforce folders like: classes, objects, etc.</p>
</div>

By default, the _srcPath_ property points to the build.gradle folder.

You can list the available tasks provided by Enforce executing:
{% highlight bash %}
   $ gradle tasks
{% endhighlight %}

Now you have a basic Gradle project (build.gradle) and you can start using all the features!
