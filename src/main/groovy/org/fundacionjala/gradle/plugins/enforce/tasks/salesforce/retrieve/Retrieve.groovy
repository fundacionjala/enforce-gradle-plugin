/*
 * Copyright (c) Fundación Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.retrieve

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import com.sforce.soap.metadata.PackageTypeMembers
import org.fundacionjala.gradle.plugins.enforce.utils.AnsiColor
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageBuilder

import java.nio.file.Paths

/**
 * Retrieves elements from organization according parameters inserted by user
 */
class Retrieve extends Retrieval {
    private static final String GROUP_OF_TASK = "Retrieve"
    private static final String DESCRIPTION_OF_TASK = 'This task recover specific files from an organization'
    private static final String MESSAGE_WARNING = 'Warning: Your files will be replaced if there are.'
    private static final String MESSAGE_CANCELED = 'Retrieve task was canceled!!'
    private static final String QUESTION_TO_CONTINUE = 'Do you want to continue? (y/n) : '
    private final String FILES_RETRIEVE = 'files'
    private final String DESTINATION_FOLDER = 'destination'
    private final String COMMA = ','
    private final String YES = 'y'
    private String option
    public static final String WILDCARD = '*'
    public String files
    public String destination
    public final String SLASH = '/'
    public final String BACKSLASH = '\\\\'

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    Retrieve() {
        super(DESCRIPTION_OF_TASK, GROUP_OF_TASK)
    }

    @Override
    void runTask() {
        verifyDestinationFolder()
        ManagementFile.createDirectories(projectPath)
        verifyFiles()
        validateContentParameter()
        if (!hasPackage() && !files) {
            showWarningMessage()
            if (option == YES) {
                retrieveWithoutPackageXml()
            } else {
                logger.warn(MESSAGE_CANCELED)
            }
        } else {
            retrieveWithPackageXml()
        }
        showInfoMessage()
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
     * Loads the files parameter
     */
    private void verifyFiles() {
        if (!files) {
            if (Util.isValidProperty(project, FILES_RETRIEVE)) {
                files = project.property(FILES_RETRIEVE) as String
            }
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
        createPackage()
        runRetrieve()
        copyFilesWithoutPackage()
        updatePackageXml(Paths.get(unPackageFolder, Constants.PACKAGE_FILE_NAME).toString(), Paths.get(projectPath, Constants.PACKAGE_FILE_NAME).toString())
        File unpackage = new File(unPackageFolder)
        unpackage.deleteDir()
    }

    /**
     * Verifies if exist package xml into project path
     * @return true if exist a package xml file else return false
     */
    def hasPackage() {
        return new File(Paths.get(projectPath, Constants.PACKAGE_FILE_NAME).toString()).exists()
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
            ArrayList<String> arrayNameArchives = files.split(COMMA)
            arrayNameArchives.each { nameFile ->
                filesRetrieve.push(new File(nameFile))
            }
            packageBuilder.createPackage(filesRetrieve)
        } else {
            FileReader packageFileReader = new FileReader(Paths.get(projectPath, Constants.PACKAGE_FILE_NAME).toString())
            packageBuilder.read(packageFileReader)
        }
    }

    /**
     * Creates a package xml based in a list called 'foldersToDownload'
     */
    void createPackageByFolders() {
        String parameterFolder = project.enforce.foldersToDownload
        ArrayList<String> arrayFolders = parameterFolder.split(COMMA)
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
            filesToCopy.remove(new File(Paths.get(unPackageFolder, Constants.PACKAGE_FILE_NAME).toString()))
        }
        fileManager.copy(filesToCopy, projectPath)
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
                if (memberType.contains(WILDCARD)) {
                    members = [WILDCARD]
                }
            } else {
                packageTypeMembersFound.members.each { member ->
                    if (members.contains(member)) {
                        members.remove(member)
                    }
                    if (member == WILDCARD) {
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
        if (arrayFiles.size() > 0) {
            logger.error(MESSAGE_WARNING)
            option = System.console().readLine("${'  '}${QUESTION_TO_CONTINUE}")
        } else {
            option = YES
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
        parameterValues = parameterValues.replaceAll(BACKSLASH, SLASH)
        ArrayList<String> fileNames = new ArrayList<String>()
        ArrayList<String> folderNames = new ArrayList<String>()
        parameterValues.split(Constants.COMMA).each { String parameter ->
            if (parameter.contains(SLASH)) {
                fileNames.push(parameter)
            } else {
                folderNames.push(parameter)
            }
        }
        validateFolders(folderNames)
        validateFiles(fileNames)
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
            println replacedElements.size() == 1 ? " ${replacedElements} was replaced" : " ${replacedElements} were replaced"
        }
    }
}

