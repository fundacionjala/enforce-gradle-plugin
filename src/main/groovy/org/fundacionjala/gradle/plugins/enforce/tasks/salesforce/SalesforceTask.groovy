/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce

import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialManager
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialMessage
import org.fundacionjala.gradle.plugins.enforce.exceptions.deploy.DeployException
import org.fundacionjala.gradle.plugins.enforce.tasks.ForceTask
import org.fundacionjala.gradle.plugins.enforce.tasks.credentialmanager.CredentialParameterValidator
import org.fundacionjala.gradle.plugins.enforce.utils.AnsiColor
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageBuilder
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.gradle.api.GradleException
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Zip

import java.nio.file.Paths

/**
 * Base class for deployment tasks
 */
abstract class SalesforceTask extends ForceTask {
    private final String NAME_TASK_ZIP = "createZip"
    private final String CREDENTIAL_NAME = "credentials.dat"
    private final String SAVE_PACKAGE_ERROR = "path package not defined, you need prepare package first"
    private final String UPDATE_PACKAGE_ERROR = "you need to prepare package first"
    public final String PACKAGE_NAME = "package.xml"
    private final String DIR_USER = "user.home"
    private final String BUILD_FOLDER_NAME = "build"
    private CredentialManager credentialManagement
    private String packageLoaded
    private PackageBuilder packageBuilder
    public final String CREDENTIAL_ID = "credentialId"
    public String buildFolderPath
    public Credential credential
    public String credentialId
    public int poll
    public int waitTime
    public ArrayList<String> arrayPaths
    public String projectPackagePath

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    SalesforceTask(String description, String group) {
        super(description, group)
        packageBuilder = new PackageBuilder()
        credential = new Credential()
        credentialManagement = new CredentialManager()
        credentialId = CredentialMessage.DEFAULT_CREDENTIAL_NAME.value()
        def pathProject = Paths.get(project.projectDir.absolutePath, CREDENTIAL_NAME).toString()
        def pathHome = Paths.get(System.properties[DIR_USER].toString(), CREDENTIAL_NAME).toString()
        arrayPaths = [pathProject, pathHome]
        buildFolderPath = Paths.get(project.projectDir.path, BUILD_FOLDER_NAME).toString()
    }

    /**
     * Credential load according to an ID  and sources paths
     */
    def loadCredential() {
        boolean byParameter = true
        if (Util.isValidProperty(project, CREDENTIAL_ID)) {
            credentialId = project.credentialId
        }

        if (!CredentialParameterValidator.haveParameters(project)) {
            credential = credentialManagement.getCredentialToAuthenticate(credentialId, arrayPaths)
            byParameter = false
        }

        if (byParameter && CredentialParameterValidator.validateFieldsCredential(project)) {
            credential = CredentialParameterValidator.getCredentialInserted(project, CredentialMessage.NORMAL.value())
        }

        if (!credential) {
            throw new GradleException(CredentialMessage.MESSAGE_EXCEPTION_CREDENTIAL_NOT_FOUND.value())
        }
        showCredential()
    }

    /**
     * Shows Credentials
     */
    def showCredential() {
        print AnsiColor.ANSI_CYAN.value()
        println("___________________________________________  ")
        println("\tUsername: ${credential.username}           ")
        println("\tLogin type: ${credential.loginFormat}")
        println("___________________________________________  ")
        println AnsiColor.ANSI_RESET.value()
        logger.debug('after show credentials')
    }

    /**
     * Writes the packages requested
     * @param packagePath is path when package xml will be to create
     * @param files is an array of files
     */
    void writePackage(String packagePath, ArrayList<File> files) {
        FileWriter fileWriter = new FileWriter(packagePath)
        files = files.grep({ file->
            !file.name.endsWith(Constants.META_XML_NAME)
        })
        packageBuilder.createPackage(files, projectPath)
        packageBuilder.write(fileWriter)
        fileWriter.close()
    }

    /**
     * Prepares the packages requested
     * @param packagePath is path when package xml will be to create
     * @param files is an array of files
     */
    void preparePackage(String packagePath, ArrayList<File> files) {
        this.packageLoaded = packagePath
        packageBuilder.createPackage(files, projectPath)
    }
    
    /**
     * Saves package created
     */
    void savePackage() {
        if (!this.packageLoaded) {
            throw new DeployException(SAVE_PACKAGE_ERROR,[])
        }
        FileWriter fileWriter = new FileWriter(this.packageLoaded)
        packageBuilder.write(fileWriter)
        fileWriter.close()
    }

    /**
     * Updates the package prepared
     */
    void updatePackage(String nameOfType,ArrayList<String> members, String pathPackage) {
        if (packageBuilder == null && !this.packageLoaded) {
            throw new DeployException(UPDATE_PACKAGE_ERROR, [])
        }
        File file = new File(pathPackage)
        packageBuilder.update(nameOfType, members, file)
    }



    /**
     * Creates a zip file
     * @param destination is folder where will create zip
     * @param fileName is name of file zip
     * @param sourcePath is folder will compress
     * @return a path zip  was created
     */
    String createZip(String sourcePath, String destination, String fileName) {
        File folderDestination = new File(destination)

        if (!folderDestination.exists()) {
            throw new Exception("Cannot find the folder: $destination ")
        }

        String fileNameZip = "${fileName}.zip"
        File fileZip = new File(Paths.get(destination, fileNameZip).toString())
        if (fileZip.exists()) {
            fileZip.delete()
        }

        project.task(NAME_TASK_ZIP, type: Zip, overwrite: true) {
            destinationDir new File(destination)
            archiveName fileNameZip
            from sourcePath
        }.execute()

        return fileZip.getAbsolutePath()
    }

    /**
     * Unzips a file to specific destination
     * @param zipPath the file zip path
     * @param folderUnZip the folder where will be unzipped
     */
    public void unZip(String zipPath, String folderUnZip) {
        project.copy {
            def zipFile = project.file(zipPath)
            def outputDir = project.file(folderUnZip)
            from project.zipTree(zipFile)
            into outputDir
        }
    }

    /**
     * Deletes a directory excluding another specific directory
     * @param dirToDelete the directory to delete
     * @param dirToExclude the directory to exclude
     */
    public void deleteDir(String dirToDelete, String dirToExclude){
        String tempDirPath = System.getProperty(Constants.TEMP_DIR_PATH)
        def tempDir = new File(Paths.get(tempDirPath,"${Constants.TEMP_FOLDER_NAME}${Long.toString(System.nanoTime())}").toString())
        if(!tempDir.mkdir()){
            throw new IOException("Could not create temp directory: ${tempDir.getAbsolutePath()}");
        }
        project.copy {
            from dirToExclude
            into tempDir.absolutePath
        }
        project.delete project.files(dirToDelete)
        project.copy {
            from tempDir.absolutePath
            into dirToExclude
        }
    }

    /**
     * Deletes all temporary files excluding the log files
     */
    public void deleteTemporaryFiles(){
        if(project.enforce.deleteTemporaryFiles) {
            deleteDir(buildFolderPath, Paths.get(buildFolderPath, Constants.LOGS_FOLDER_NAME).toString())
        }
    }


    /**
     * Load credential, gets version api and execute the method run
     */
    @Override
    void executeTask() {
        poll = project.enforce.poll
        waitTime = project.enforce.waitTime
        loadCredential()
        logger.debug('Finished load credential')
        fileManager.createDirectory(buildFolderPath)
        logger.debug('Created directory at: ' + buildFolderPath)
        projectPackagePath = Paths.get(projectPath, PACKAGE_NAME)
        runTask()
    }

    /**
     * Abstract method: When implement a method can select steps for deployment
     */
    abstract void runTask()
}
