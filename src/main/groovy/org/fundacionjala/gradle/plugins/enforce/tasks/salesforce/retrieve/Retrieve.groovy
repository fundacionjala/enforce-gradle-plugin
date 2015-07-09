/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.retrieve

import com.sforce.soap.metadata.PackageTypeMembers
import org.fundacionjala.gradle.plugins.enforce.utils.AnsiColor
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageBuilder

import java.nio.file.Paths

/**
 * Retrieves elements from organization according parameters inserted by user
 */
class Retrieve extends Retrieval {
    private static final String GROUP_OF_TASK = "Retrieve"
    private final String DESTINATION_FOLDER = 'destination'
    private String option
    public String files
    public String destination
    public String all = Constants.FALSE
    public final int CODE_TO_EXIT = 0

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    Retrieve() {
        super(Constants.RETRIEVE_DESCRIPTION_OF_TASK, GROUP_OF_TASK)
    }

    @Override
    void runTask() {
        verifyDestinationFolder()
        ManagementFile.createDirectories(projectPath)
        validateContentParameter()
        if (!hasPackage() && !files) {
           retrieveWithoutPackageXml()
        } else {
            if(files){
                showInfoMessage()
                createPackageFromFiles()
            }else{
                showWarningMessage()
                if (option == Constants.YES_OPTION) {
                    loadFromPackage()
                } else {
                    logger.warn(Constants.RETRIEVE_MESSAGE_CANCELED)
                    System.exit(CODE_TO_EXIT)
                }
            }
            retrieveWithPackageXml()
        }
        deleteTemporaryFiles()
    }

    /**
     * Creates the package xml file from files parameter
     */
    private void createPackageFromFiles(){
        ArrayList<File> filesRetrieve = new ArrayList<File>()
        ArrayList<String> arrayNameArchives = files.split(Constants.COMMA)
        arrayNameArchives.each { nameFile ->
            filesRetrieve.push(new File(Paths.get(projectPath, nameFile).toString()))
        }
        packageBuilder.createPackage(filesRetrieve, projectPath)
    }

    /**
     * Loads the package structure file from package xml
     */
    private void loadFromPackage(){
        FileReader packageFileReader = new FileReader(packageFromSourcePath)
        packageBuilder.read(packageFileReader)
    }

    /**
     * Loads the destination parameter
     */
    private void verifyDestinationFolder() {
        if (destination) {
            projectPath = Paths.get(destination).isAbsolute() ? destination :
                    Paths.get(project.projectDir.absolutePath, destination).toString()
            return
        }

        if (Util.isValidProperty(project, DESTINATION_FOLDER)) {
            String targetPath = project.property(DESTINATION_FOLDER) as String
            projectPath = Paths.get(targetPath).isAbsolute() ? targetPath :
                    Paths.get(project.projectDir.absolutePath, targetPath).toString()
        }
    }

    /**
     * Gets a files array from a directory
     * @param directory the directory to get its files
     * @return a files array
     */
    File[] getFiles(File directory) {
        File[] arrayFiles = []
        if (directory && directory.isDirectory()) {
            arrayFiles = directory.listFiles()
        }
        return arrayFiles
    }

    /**
     * Executes the logic to retrieve without update the package.xml file
     */
    void retrieveWithoutPackageXml() {
        createPackageByFolders()
        runRetrieve()
        copyAllFiles()
        resetFileTracking()
    }

    /**
     * Executes the logic to retrieve updating the package.xml file
     */
    void retrieveWithPackageXml() {
        runRetrieve()
        copyFilesWithoutPackage()
        updatePackageXml(packageFromBuildPath, packageFromSourcePath)
        File unpackage = new File(unPackageFolder)
        unpackage.deleteDir()
    }

    /**
     * Verifies if exist package xml into project path
     * @return true if exist a package xml file else return false
     */
    def hasPackage() {
        return new File(packageFromSourcePath).exists()
    }

    /**
     * Executes retrieve
     * Shows warnings messages
     * Saves Zip file in build folder
     */
    void runRetrieve() {
        executeRetrieve(packageBuilder.metaPackage)
        showWarningsMessages()
        saveOnDiskFileUnzipped(retrieveMetadata.getZipFileRetrieved())
    }

    /**
     * Creates a Xml file which contains important data to be retrieved
     */
    void createPackage() {
        if (files) {
            ArrayList<File> filesRetrieve = new ArrayList<File>()
            ArrayList<String> arrayNameArchives = files.split(Constants.COMMA)
            arrayNameArchives.each { nameFile ->
                filesRetrieve.push(new File(Paths.get(projectPath, nameFile).toString()))
            }
            packageBuilder.createPackage(filesRetrieve, projectPath)
        } else {
            FileReader packageFileReader = new FileReader(packageFromSourcePath)
            packageBuilder.read(packageFileReader)
        }
    }

    /**
     * Creates a package xml based in a list called 'foldersToDownload'
     */
    void createPackageByFolders() {
        String parameterFolder = project.enforce.foldersToDownload
        ArrayList<String> arrayFolders = parameterFolder.split(Constants.COMMA)
        packageBuilder.createPackageByFolder(arrayFolders)
    }

    /**
     * Copies all files from build/retrieve path to project path.
     */
    public void copyAllFiles() {
        fileManager.copyFiles(unPackageFolder, projectPath)
        new File(unPackageFolder).deleteDir()
    }

    /**
     * Copies files retrieved from build/unpackage to source directory.
     */
    public void copyFilesWithoutPackage() {
        ArrayList<File> filesToCopy = fileManager.getValidElements(unPackageFolder)
        if (hasPackage()) {
            filesToCopy.remove(new File(packageFromBuildPath))
        }
        fileManager.copy(unPackageFolder, filesToCopy, projectPath)
    }

    /**
     * Updates package xml from source package xml with retrieved files
     * @param retrievedPackagePath is type String
     * @param packageSrcPath is type String
     */
    public void updatePackageXml(String retrievedPackagePath, String packageSrcPath) {
        PackageBuilder source = new PackageBuilder()
        PackageBuilder retrieved = new PackageBuilder()
        retrieved.read(new FileReader(retrievedPackagePath))
        source.read(new FileReader(packageSrcPath))
        def packageFile = new File(packageSrcPath)
        retrieved.metaPackage.types.each { type ->
            String name = type.name
            ArrayList<String> members = type.members
            ArrayList<PackageTypeMembers> packageTypeMembers = source.metaPackage.types.toList()
            PackageTypeMembers packageTypeMembersFound = null
            ArrayList<String> memberType
            packageTypeMembers.find { packageTypeMembersIt ->
                memberType = packageTypeMembersIt.members
                if (packageTypeMembersIt.name == name) {
                    packageTypeMembersFound = packageTypeMembersIt
                    return
                }
            }
            if (!packageTypeMembersFound) {
                if (memberType.contains(Constants.WILDCARD)) {
                    members = [Constants.WILDCARD]
                }
            } else {
                packageTypeMembersFound.members.each { member ->
                    if (members.contains(member)) {
                        members.remove(member)
                    }
                    if (member == Constants.WILDCARD) {
                        members = []
                    }
                }
            }
            source.update(name, members, packageFile)
        }
    }

    /**
     * Resets file tracking because all project download are new.
     */
    void resetFileTracking() {
        project.tasks.reset
    }

    /**
     * Shows a warning message to replace files from source directory.
     */
    void showWarningMessage() {
        File[] arrayFiles = getFiles(new File(projectPath))
        if (arrayFiles.size() > 0 && all == Constants.FALSE) {
            logger.error(Constants.RETRIEVE_MESSAGE_WARNING)
            option = System.console().readLine("${'  '}${Constants.RETRIEVE_QUESTION_TO_CONTINUE}")
        } else {
            option = Constants.YES_OPTION
        }
    }

    /**
     * Validates parameter's values
     * @param parameterValues are files name that will be excluded
     */
    public void validateContentParameter() {
        if (files == null) {
            return
        }
        String parameterValues = files
        parameterValues = parameterValues.replaceAll(Constants.BACK_SLASH, Constants.SLASH)
        ArrayList<File> filesToRetrieve = new ArrayList<File>()
        ArrayList<String> folderNames = new ArrayList<String>()
        parameterValues.split(Constants.COMMA).each { String parameter ->
            if (parameter.contains(Constants.SLASH)) {
                filesToRetrieve.push(new File(Paths.get(projectPath,parameter).toString()))
            } else {
                folderNames.push(parameter)
            }
        }
        validateFolders(folderNames)
        validateFiles(filesToRetrieve)
    }

    /**
     * Shows files and folders replaced
     */
    public void showInfoMessage() {
        if (files == null) {
            return
        }
        ArrayList<String> elements = files.split(Constants.COMMA) as ArrayList<String>
        ArrayList<String> replacedElements = new ArrayList<String>()
        elements.each { String elementName ->
            def element = new File(Paths.get(projectPath, elementName).toString())
            if (!element.exists() || (element.isDirectory() && element.size() == 0)) {
                return
            }
            replacedElements.push(elementName)
        }
        if (!replacedElements.isEmpty()) {
            print AnsiColor.ANSI_CYAN.value()
            print "Info:"
            print AnsiColor.ANSI_RESET.value()
            println " ${replacedElements} will be replaced"
        }
    }
}

