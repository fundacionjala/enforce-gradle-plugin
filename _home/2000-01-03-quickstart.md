---
title: "quickstart"
id: "quickstart"
bg: green2
color: black
fa-icon: toggle-on
---

## Setup a `gradle` script file

- Create `build.gradle` file on your source code project, below you have an example

{% highlight groovy linenos=table%}
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

- Register a Salesforce credential to be used
{% highlight bash linenos=table%}
   $ gradle addCredential  -Pid=<default> 
                        -Puser=<USER NAME> 
                        -Ppassword=<PASSWORD> 
                        -Ptoken=<SECURITY TOKEN>
   #=> Creates an entry on the credentials store, credential id with 'default' value
{% endhighlight %}

- Start executing available commands, for example:
{% highlight bash linenos=table%}
   $ gradle retrieve -Pfiles=classes
   #=> Retrieves all the classes from a Salesforce organization
{% endhighlight %}

Now you can start using all the EnForce features reviwing the [Documentation](documentation.html).

