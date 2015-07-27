/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.managepackage

import org.fundacionjala.gradle.plugins.enforce.metadata.RetrieveMetadata
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment.Deployment
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.ZipFileManager
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager.PackageBuilder

import java.nio.file.Paths

/**
 * Uninstalls an installed package from an org, it does not uninstall custom components(profiles, custom objects, etc)
 * that are used externally. If there is not an installed package, the process just ends.
 */
class UninstallPackageTask extends Deployment {
    public static final String TASK_DIR = "uninstallpkg"
    public static final String TASK_GROUP = "Package Management"
    public static final String TASK_DESCRIPTION = "Uninstalls a package from an organization."
    public static final String PKG_NAMESPACE = "pkg.namespace"
    public static final String RETRIEVE_INSTALLEDPKGS = "retrieveInstalledpkgs"
    public static final String INSTALLEDPKGSRESULT = "installedpkgsresult"
    public static final String UNPACKAGED_DIR = "unpackaged"
    private List<String> requiredParams = ["pkg.namespace"]
    private String installedPkgsZipFile = "installedPkgs.zip"
    private String uninstallPkgRootDir
    private String installedPkgsCompDir
    private String packageNamespace
    private String retrieveInstalledPkgsRootDir
    private String retrieveInstalledPkgsCompDir
    private String installedPkgsResultRootDir
    private String installedPkgsResultCompDir
    private String unpackagedPkgsDir

    UninstallPackageTask() {
        super(TASK_DESCRIPTION, TASK_GROUP)
    }

    @Override
    void runTask() {
        if (Util.validateRequiredParameters(project, requiredParams)) {
            setup()
            logger.quiet("Verifying installed package '${packageNamespace}' ...")
            if (verifyPackageInstallation()) {
                logger.quiet("Installed package '${packageNamespace}' found.")
                createPackage()
                executeDeploy(uninstallPkgRootDir, "", "")
                logger.quiet("Uninstall package '${packageNamespace}' success.")
            } else {
                logger.quiet("Installed package '${packageNamespace}' not found.")
            }
        } else {
            throw new Exception("There are missing required fields.")
        }
    }

    /**
     * Setups to start uninstall package task
     */
    @Override
    void setup() {
        this.uninstallPkgRootDir = Paths.get(buildFolderPath, TASK_DIR).toString()
        this.installedPkgsCompDir = Paths.get(this.uninstallPkgRootDir,
                MetadataComponents.INSTALLEDPACKAGES.directory).toString()
        this.retrieveInstalledPkgsRootDir = Paths.get(buildFolderPath, RETRIEVE_INSTALLEDPKGS).toString()
        this.retrieveInstalledPkgsCompDir = Paths.get(this.retrieveInstalledPkgsRootDir,
                MetadataComponents.INSTALLEDPACKAGES.directory).toString()
        this.installedPkgsResultRootDir = Paths.get(buildFolderPath, INSTALLEDPKGSRESULT).toString()
        this.unpackagedPkgsDir = "${UNPACKAGED_DIR}${File.separator}${MetadataComponents.INSTALLEDPACKAGES.directory}"
        this.installedPkgsResultCompDir = Paths.get(this.installedPkgsResultRootDir, this.unpackagedPkgsDir).toString()

        this.packageNamespace = project.property(PKG_NAMESPACE)
        Util.forceCreateFolder(this.uninstallPkgRootDir)
        Util.forceCreateFolder(this.installedPkgsCompDir)
        Util.forceCreateFolder(this.retrieveInstalledPkgsRootDir)
        Util.forceCreateFolder(this.retrieveInstalledPkgsCompDir)
        Util.forceCreateFolder(this.installedPkgsResultRootDir)
    }

    /**
    * Verifies that if user's organization is already installed.
    **/
    def verifyPackageInstallation() {
        File tempPkgFile = this.generatedInstallPackageFile(this.retrieveInstalledPkgsCompDir)
        ArrayList<File> tempFiles = new ArrayList<File>()
        tempFiles.add(tempPkgFile)
        writePackage(Paths.get(this.retrieveInstalledPkgsRootDir, Constants.PACKAGE_FILE_NAME).toString(), tempFiles)
        PackageBuilder packageBuilder = new PackageBuilder()
        RetrieveMetadata retrieveMetadata = new RetrieveMetadata(packageBuilder.metaPackage)
        retrieveMetadata.executeRetrieve(poll, waitTime, credential)
        if (retrieveMetadata.getWarningsMessages().empty) {
            ZipFileManager zipFileManager = new ZipFileManager()
            zipFileManager.flushZipFile(retrieveMetadata.getZipFileRetrieved(), this.installedPkgsResultRootDir,
                    this.installedPkgsZipFile)
            String zipFilePath = Paths.get(this.installedPkgsResultRootDir, this.installedPkgsZipFile).toString()
            zipFileManager.unzipZipRetrieved(zipFilePath, this.installedPkgsResultRootDir)
        } else {
            retrieveMetadata.getWarningsMessages().each { message ->
                logger.warn(message)
            }
        }
        String expectedInstalledPgkFileName = "${this.packageNamespace}.${MetadataComponents.INSTALLEDPACKAGES.extension}"
        String expectedInstalledPgkFilePath = Paths.get(retrieveInstalledPkgsRootDir, MetadataComponents.INSTALLEDPACKAGES.directory, expectedInstalledPgkFileName).toString()
        Boolean exists = new File(expectedInstalledPgkFilePath).exists()
        return exists
    }

    /**
     * Creates destructive xml file and package xml file
     */
    void createPackage() {
        File pkgFile = this.generatedInstallPackageFile(this.installedPkgsCompDir)
        ArrayList<File> filesToUndeploy = new ArrayList<File>()
        filesToUndeploy.add(pkgFile)
        writePackage(Paths.get(this.uninstallPkgRootDir, Constants.PACKAGE_FILE_NAME).toString(), new ArrayList<File>())
        writePackage(Paths.get(this.uninstallPkgRootDir, Constants.DESTRUCTIVE_FILE_NAME).toString(), filesToUndeploy)
    }

    /**
     * Generates a package xml file
     * @param sourceFolder is type String
     * @return a package xml file
     */
    def generatedInstallPackageFile(String sourceFolder) {
        String pkgFileName = "${sourceFolder}${File.separator}${this.packageNamespace}." +
                             "${MetadataComponents.INSTALLEDPACKAGES.extension}"

        FileWriter fileWriter = new FileWriter(pkgFileName)
        PackageBuilder xml = new PackageBuilder()
        xml.writeInstalledPackageXML(null, null, fileWriter)
        return new File(pkgFileName)
    }
}