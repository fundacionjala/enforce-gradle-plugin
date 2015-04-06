---
layout: post
title: Custom deployment with Interceptors
categories: []
tags: []
published: True

---

Scenario
--------
The problem that I found during the development, is that most of the developers have all their data, scenarios and configuration present in the organization, therefore they did not like the idea of undeploy the current code and then deploy the latest changes, however they always need the latest changes.

What I did is I implemented a custom task to update the current code that it's in the organization without performing an undeploy operation in order to keep all the data, configuration and so on. Here you have an example:

{% highlight groovy %}
task truncate(type:Upload) {
    group = 'Project Tasks'
    description = 'Truncates pages, classes, triggers and componenets'
    folders = "classes,pages,components,triggers"
    commands = ['classInterceptor', 'deprecate', 'triggerInterceptor', 'componentInterceptor', 'pageInterceptor']
}

task uploadDevOrg(type:Upload) {
    group = 'Project Tasks'
    description = '...'
    folders = "classes,pages,staticresources,components,triggers,objects,labels,tabs"
    commands = ['deprecate']
}

uploadDevOrg.dependsOn truncate
{% endhighlight %}

The reason to have the truncate task is because the organization may have old, updated or removed classes, methods or constructors, so in order to be able to upload the latest code in the organization, we need to truncate all the components.

Once we have everything truncated we will be able to upload the code into your organization and all the development team should be able to work with the latest changes without losing their data.
