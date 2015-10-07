---
layout: gradle
title: Build.gradle file
permalink: /docs/code-build-gradle/
---
## Code


{% highlight groovy %}
   buildscript {
       repositories {
         mavenCentral()
         jcenter()
       }
       dependencies {
           classpath 'org.fundacionjala.gradle.plugins.enforce:enforce-gradle-plugin:1.1.2'
       }
   }

   apply plugin: 'enforce'
   
   enforce {
       srcPath = 'src'
       deleteTemporaryFiles = true
       deleteSubComponents = ['*']
   }   
{% endhighlight %}


## Configuration parameters

Task | srcPath |
Description | Specifies the folder where we store our code.|
Examples | 'src' |
{: .table-quick-start-build-gradle }

Task | deleteTemporaryFiles
Description |  During the execution of a task is necessary to create some temporary folders in the build folder, using this parameter we can define that these folders are not delete.
Examples | true  or false
Note |  The default value of this variable is true
{: .table-quick-start-build-gradle }

Task | deleteSubComponents
Description | Selects that subcomponents of an object may be removed during the update and delete tasks.
Examples | ['  \* '] : Delete all subcomponents <br> [' !\* '] : Ignore all subcomponents <br>  ['fields', 'compactLayouts'] : Delete all fields and compactLayouts components. <br>  ['!fields', '!compactLayouts'] : Ignore all fields and compactLayouts components. <br>  ['fields', '!compactLayouts'] : Delete fields components and ignore compactLayouts components.
Note | Supported subcomponents <br>['fields', 'compactLayouts', 'recordTypes', 'validationRules']
{: .table-quick-start-build-gradle }
