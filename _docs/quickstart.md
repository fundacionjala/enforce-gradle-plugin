---
layout: gradle
title: Quick start
permalink: /docs/quickstart/
---

Here you can find the enought required information to have running a Gradle project with Enforce, if you are interested on develop new features or just compile and package the source code, you will have helpful information at <a href="{{ site.url }}/docs/development/" target="_blank">Development</a> page

1. Create build.gradle file on your source code project, below you have an example
{% highlight groovy %}
   buildscript {
       repositories {
         mavenCentral()
         maven {
            url "https://dl.bintray.com/fundacionjala/enforce"
         }
       }
       dependencies {
           classpath 'org.fundacionjala.gradle.plugins.enforce:enforce-gradle-plugin:1.0.0'
       }
   }

   apply plugin: 'enforce'
   
   enforce {
       srcPath = 'src'
   }   
{% endhighlight %}

2. Register a Salesforce credential to be used
{% highlight bash %}
   $ gradle addCredential -Pid=default
                         -Pusername=<USER NAME>
                         -Ppassword=<PASSWORD> 
                         -Ptoken=<SECURITY TOKEN>
{% endhighlight %}
Credential id should be saved with 'default' value.

3. Start executing available commands, for example, retrieve all the classes from the Salesforce Organization
{% highlight bash %}
   $ gradle retrieve -Pfiles=classes
{% endhighlight %}

Now you can start using all the Enforce features.
