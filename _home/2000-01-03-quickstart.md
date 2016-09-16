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

- Register a Salesforce credential to be used. [generate token security](http://www.salesforcegeneral.com/salesforce-articles/salesforce-security-token.html)
{% highlight bash linenos=table%}
   $ gradle addCredential  -Pid=default
                        -Pusername=<USER NAME>
                        -Ppassword=<PASSWORD> 
                        -Ptoken=<SECURITY TOKEN>
   #=> Creates an entry on the credentials store, credential id with 'default' value
{% endhighlight %}

- When you want to upload your local code to your salesforce organization:
{% highlight bash linenos=table%}
   $ gradle deploy
   #=> This command in the first time break the dependencies and then upload all your code.
{% endhighlight %}

- When you want to upload only files changed to your salesforce organization:
{% highlight bash linenos=table%}
   $ gradle update
{% endhighlight %}

Now you can start using all the EnForce features reviewing the [Documentation](documentation.html).

