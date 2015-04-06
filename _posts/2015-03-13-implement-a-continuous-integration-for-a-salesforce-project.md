---
layout: post 
title: Implement a Continuous Integration for a Salesforce Project
categories: []
tags: [continuous integration, integration, build process]
published: True
comments: True

---
Implement a Continuous Integration process for a Salesforce project is quite difficult due to the cyclic dependencies between project components, for example, an `Object` can have a `weblink` to a `Custom Page`, which has a Class Controller that uses the previuous `Object`. These dependencies makes difficult to deploy project source code in a simple way, it is true that the `Migration Tool` can help you to upload code to an Organization, but it is not able to resolve those dependencies problems.
`EnForce Gradle plugin` provides tasks that makes possible to implement a Continuous Integration process using `Gradle`.
This article tries to show an example of a Gradle build script which implements a Continuos Integration process with the next steps:

+ clean environment
+ deploy code
+ runt unit test

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
