---
layout: gradle
title: Quick start
permalink: /docs/quickstart/
---

Here you can find required information  that is enough to have a Gradle project with Enforce running. If you are interested in developing new features  or just compile and pack the source code, you will have helpful information  at <a href="{{ site.url }}/docs/development/" target="_blank">Development</a> page

- Create build.gradle file on your source code project. Below, you have an example
{% highlight groovy %}
   buildscript {
       repositories {
         mavenCentral()
         jcenter()
       }
       dependencies {
           classpath 'org.fundacionjala.gradle.plugins.enforce:enforce-gradle-plugin:1.0.1'
       }
   }

   apply plugin: 'enforce'
   
   enforce {
       srcPath = 'src'
   }   
{% endhighlight %}


- Register a Salesforce credential to be used. [generate token security](http://www.salesforcegeneral.com/salesforce-articles/salesforce-security-token.html)
{% highlight bash %}
   $ gradle addCredential -Pid=default
                         -Pusername=<USER NAME>
                         -Ppassword=<PASSWORD> 
                         -Ptoken=<SECURITY TOKEN>
                         
   #=> Credential id should be saved with 'default' value.                      
{% endhighlight %}


- When you want upload your local code to your salesforce organization:
{% highlight bash %}
   $ gradle deploy
   
   #=> This command in the first time break the dependencies and then upload all your code.
{% endhighlight %}


- When you want upload only changed files to your salesforce organization:
{% highlight bash %}
   $ gradle update
{% endhighlight %}

Now you can start using all the Enforce features.
