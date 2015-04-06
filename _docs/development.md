---
layout: gradle
title: Development
permalink: /docs/development/
---

### Requirements
 * [Java JDK or JRE, version 7 or higher](http://java.com/en/).
 * [Gradle version 2.0 or higher](https://gradle.org/docs/current/userguide/installation.html).
 * Internet access ([configure Gradle if you are using a Proxy](https://gradle.org/docs/current/userguide/build_environment.html)).

It is desired a basic knowledge about Gradle and its plugin mechanism, as starting point you can review:

 * [Writing custom plugins](http://www.gradle.org/docs/current/userguide/custom_plugins.html)
 * [Gradle Custom Plugin](http://www.javacodegeeks.com/2012/08/gradle-custom-plugin.html)

The source code is available at: http://gitlab.limbo.local/cpq1/sfdc-development-tool.
Once you have the source code, open the source code in a console and execute:

{% highlight bash %}
   $ gradle build
{% endhighlight %}

Please, make sure that your changes are not breaking any functionality running the unit test:
{% highlight bash %}
   $ gradle test
{% endhighlight %}

