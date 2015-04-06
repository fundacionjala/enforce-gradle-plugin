---
layout: post
title: Upload only modified files with Mavensmate
categories: []
tags: []
published: True
comments: True
---


Mavesmate is a tool that helps you in your Salesforce development, but the bad news is that you need to deploy your changes one by one(and also with the Salesforce IDE). You can easily improve this scenario using the  `update` command to deploy your modified files in just a deploy. 

### Step 1
You need to setting up your EnForce environment into your Mavesmate project.

### Step 2
Then disable the Mavesmate `mm_compile_on_save` option. Just open your Mavesmate project and then go to  `MavesMate>Settings>User`.

```
"mm_compile_on_save" : false,
```

### Step 3
Create a build command for your Mavesmate project, it should look something like this. 


```
{
    "cmd": ["gradle", "update"],
    "working_dir": "${project_path}",
    "selector": "text.html,source.apex,text.xml",
    "shell": true
}
```

it needs to be stored in the Sublime user's packages, let say `sf-build-command.sublime-build` file.

- Windows: %APPDATA%\Sublime Text 3\Packages\User\sf-build-command.sublime-build
- OS X: ~/Library/Application Support/Sublime Text 3/Packages/User/sf-build-command.sublime-build
- Linux: ~/.config/sublime-text-3/packages/user/sf-build-command.sublime-build

Then go to `Tools>Build System` and select `sf-build-command`  by default.

**Note:** The `working_dir` property of your build command should point out to the folder where you have your `build.gradle` file, in this case the `build.gradle` file is in the project's root folder.

### Step 4

Open a console where you have your build.gradle script and then execute.

```
gradle status
```

It is just to start the EnForce file monitor that checks the file modifications.

### Step 5
Once you want to deploy your changes, just execute `Ctrl + b` to deploy them. 

Now you are able to deploy your modified files just in a deploy instead one by one and that it is awesome.