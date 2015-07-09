/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.managepackage

import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment.Deployment
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager.PackageBuilder

import java.nio.file.Paths

/**
 * Installs a manage package to a target org, it does not install custom components(profiles, custom objects, etc)
 * that are used externally.
 */
class InstallPackageTask extends Deployment {
    public static final String TASK_DIR = "installpkg"
    public static final String TASK_GROUP = "Package Management"
    public static final String TASK_DESCRIPTION = "Installs a package to an organization."
    public static final String PKG_NAMESPACE = "pkg.namespace"
    public static final String PKG_VERSION = "pkg.version"
    public static final String PKG_PASSWORD = "pkg.password"
    private List<String> requiredParams = ["pkg.namespace", "pkg.version"]
    private String installPkgRootDir
    private String installedPkgsCompDir
    private String packageNamespace
    private String packageVersion
    private String packagePassword

    InstallPackageTask() {
        super(TASK_DESCRIPTION, TASK_GROUP)
    }

    @Override
    void runTask() {
        if (Util.validateRequiredParameters(project, requiredParams)) {
            setup()
            createPackage()
            executeDeploy(installPkgRootDir, "", "")
            logger.quiet("Install package '${packageNamespace}' v${packageVersion} success.")
        } else {
            throw new Exception("There are missing required parameters.")
        }
    }

    /**
     * Setups to start install package task
     */
    @Override
    void setup() {
        this.installPkgRootDir = Paths.get(buildFolderPath, TASK_DIR).toString()
        this.installedPkgsCompDir = "${this.installPkgRootDir}${File.separator}" +
                "${MetadataComponents.INSTALLEDPACKAGES.directory}"
        this.packageNamespace = project.property(PKG_NAMESPACE)
        this.packageVersion = project.property(PKG_VERSION)
        if (Util.isValidProperty(project, PKG_PASSWORD)) {
            this.packagePassword = project.property(PKG_PASSWORD)
        }
        Util.forceCreateFolder(this.installPkgRootDir)
        Util.forceCreateFolder(this.installedPkgsCompDir)
    }

    /**
     * Creates a package xml file with a content by default
     */
    void createPackage() {
        File pkgFile = this.generatedInstallPackageFile()
        ArrayList<File> filesToDeploy = new ArrayList<File>()
        filesToDeploy.add(pkgFile)
        writePackage(Paths.get(this.installPkgRootDir, Constants.PACKAGE_FILE_NAME).toString(), filesToDeploy)
    }

    /**
     * Generates a package xml file
     * @return a package xml file
     */
    def generatedInstallPackageFile() {
        def pkgFileName = "${this.installedPkgsCompDir}${File.separator}${this.packageNamespace}" +
                          ".${MetadataComponents.INSTALLEDPACKAGES.extension}"
        def fileWriter = new FileWriter(pkgFileName)
        PackageBuilder xml = new PackageBuilder()
        xml.writeInstalledPackageXML(this.packageVersion, this.packagePassword, fileWriter)
        return new File(pkgFileName)
    }
}