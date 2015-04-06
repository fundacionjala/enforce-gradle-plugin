---
layout: gradle
title: Package management
permalink: /docs/package-management/
---
## Management package

Those tasks are able to install and unisntall packages.

## Install package task

This task installs a manage package based on the following parameters:

* -Ppkg.namespace = Package namespace is required parameter.
* -Ppkg.version = Package version is required parameter.
* -Ppkg.password = Package password is not required and depends on source package settings(maybe the package does not require a password).

This task uses the same credentials used in other tasks, so bellow we have an example about [Apex Lang](https://code.google.com/p/apex-lang/) package installation.

command:

	$ gradle installPackage -Ppkg.namespace=al -Ppkg.version=1.18

output:

```bash
    :installPackage
    ___________________________________________
            Username: john.smith@gmail.com
            Login type: login
    ___________________________________________

          [zip] Building zip: \users\john.smith\my_sfdc_project\build\deploy.zip
    Starting deploy...
    [==================================================]   100%
    The files were successfully deployed
    Install package 'al' v1.18 success.

    BUILD SUCCESSFUL

    Total time: 4 mins 28.956 secs
```

Once that task is completed a result message will be displayed.

##  Uninstall package task

This task uninstalls a manage package based on the following parameters:

* -Ppkg.namespace = Package namespace is required parameter.

Bellow we have an example about [Apex Lang](https://code.google.com/p/apex-lang/) package installation.

command:

	$ gradle uninstallPackage -Ppkg.namespace=al

output:

```bash
    :uninstallPackage
    ___________________________________________
            Username: john.smith@gmail.com
            Login type: login

    ___________________________________________

    Verifying installed package 'al' ...
    Starting retrieve...
    Waiting for retrieve result...
    Retrieve result completed
        [unzip] Expanding: \users\john.smith\my_sfdc_project\build\installedpkgsresult\installedPkgs.zip into \users\john.smith\my_sfdc_project\build\installedpkgsresult
    Installed package 'al' found.
          [zip] Building zip: \users\john.smith\my_sfdc_project\build\deploy.zip
    Starting deploy...
    [==================================================]   100%
    The files were successfully deployed
    Uninstall package 'al' success.

    BUILD SUCCESSFUL

    Total time: 2 mins 24.547 secs

```
Once that task is completed a result message will be displayed.

If there target org does not have the related package installed the command will do nothing, becuase it verified an installed package before to start the process, bellow we have an execution example.

	$ gradle uninstallPackage -Ppkg.namespace=al

output:

```bash
    :uninstallPackage
    ___________________________________________
            Username: john.smith@gmail.com
            Login type: login
    ___________________________________________

    Verifying installed package 'al' ...
    Starting retrieve...
    Waiting for retrieve result...
    Retrieve result completed
    WARNING: Entity of type 'InstalledPackage' named 'al' cannot be found
    Installed package 'al' not found.

    BUILD SUCCESSFUL

    Total time: 15.179 secs

```
As you can see an information message is displayed to notify that there is not an installed package.

#### Limitations
* It does not uninstall custom components associated to the package. I.E. Profiles, PermissionSets, Etc.
* If there is org-to-org configuration over the related package, it could not be possible to be uninstalled due dependencies.