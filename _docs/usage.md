---
layout: gradle
title: Basic usage
permalink: /docs/usage/
---

Once you have a Gradle script file as part of your project, you can start using the available tasks provided by EnForce. You can use the tasks from command line or configuring custom tasks on your Gradle file.

## Executing tasks on Command line
All the tasks follow the next convention:

{% highlight bash %}
   $ gradle <TASK_NAME> -P<PROPERTY_NAME>=<PROPERTY_VALUE> ... -P<PROPERTY_NAME_N>=<PROPERTY_VALUE_N>
{% endhighlight %}


Remember that it is required to have configured salesforce
