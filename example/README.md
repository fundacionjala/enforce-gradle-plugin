---
Steps to work with EnForce plugin
---

This is an example that shows how to implement a Gradle file for a SalesForce projects, which is able to upload project code to development organization removing the deprecated annotation.

## 1. Setup your environment

### Requirements
 * [Java JDK or JRE, version 7 or higher](http://java.com/en/).
 * [Gradle version 2.0 or higher](https://gradle.org/docs/current/userguide/installation.html).
 * Internet access ([configure Gradle if you are using a Proxy](https://gradle.org/docs/current/userguide/build_environment.html)).
 * Having an account in SalesForce organization.

## 2. Using EnForce plugin

You should create a file called build.gradle as:

```groovy
   buildscript {
       repositories {
           mavenLocal()
           mavenCentral()
       	   jcenter()
       }
       dependencies {
           classpath 'org.fundacionjala.gradle.plugins.enforce:enforce-gradle-plugin:1.0.1'
       }
   }

    import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment.Upload

   apply plugin: 'enforce'

   enforce {
     srcPath = 'src'
   }

   task uploadDevOrg(type:Upload) {
      interceptors = ['removeDeprecateAnnotation']
   }

```

Notice that ***srcPath*** property points to the folder where the source code is located.

To upload source code to development organization, execute:

```
   $ gradle upload
```
