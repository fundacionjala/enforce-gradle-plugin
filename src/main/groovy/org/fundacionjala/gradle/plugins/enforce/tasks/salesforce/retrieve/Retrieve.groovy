/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.retrieve

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager.PackageBuilder

import java.nio.file.Paths

/**
 * Retrieves elements from organization according parameters inserted by user
 */
class Retrieve extends Retrieval {
    private static final String RETRIEVE_DESCRIPTION_OF_TASK = 'This task recover specific files from an organization'
    private static
    final String RETRIEVE_MESSAGE_WARNING = 'Warning: All files will be downloaded according to the package'
    private static final String RETRIEVE_MESSAGE_CANCELED = 'Retrieve task was canceled!!'
    private static final String RETRIEVE_QUESTION_TO_CONTINUE = 'Do you want to continue? (y/n) : '
    private static final String GROUP_OF_TASK = "Retrieve"
    private final String DESTINATION_FOLDER = 'destination'
    private String option
    public String destination
    public final int CODE_TO_EXIT = 0

    ArrayList<File> filesToRetrieve

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    Retrieve() {
        super(RETRIEVE_DESCRIPTION_OF_TASK, GROUP_OF_TASK)
        filesToRetrieve = []
    }

    @Override
    void runTask() {
        verifyDestinationFolder()
        loadFilesToRetrieve()
        ManagementFile.createDirectories(projectPath)
        Util.validateContentParameter(projectPath, files)
        !hasPackage() && !files ? retrieveWithoutPackageXml() : retrieveWithPackageXml()
        deleteTemporaryFiles()
    }

    /**
     *
     */
    public void loadFilesToRetrieve() {
        if (files) {
            files.split(Constants.COMMA).each { nameFile ->
                filesToRetrieve.push(new File(Paths.get(projectPath, nameFile).toString()))
            }
        }
    }

    /**
     * Creates the package xml file from files parameter
     */
    private void createPackageFromFiles() {
        if (files) {
            showInfoMessage()
            packageBuilder.createPackage(filesToRetrieve, projectPath)
        } else {
            showWarningMessage()
            if (option == Constants.YES_OPTION) {
                loadFromPackage()
            } else {
                logger.warn(RETRIEVE_MESSAGE_CANCELED)
                System.exit(CODE_TO_EXIT)
            }
        }
    }

    /**
     * Loads the package structure file from package xml
     */
    private void loadFromPackage() {
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
        createPackageFromFiles()
        runRetrieve()
        copyFilesWithoutPackage()
        PackageBuilder.updatePackageXml(packageFromBuildPath, packageFromSourcePath)
        File unpackage = new File(unPackageFolder)
        unpackage.deleteDir()
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
     * Resets file tracking because all project download are new.
     */
    void resetFileTracking() {
        project.tasks.reset
    }

    /**
     * Shows a warning message to replace files from source directory.
     */
    void showWarningMessage() {
        File[] arrayFiles = Util.getFiles(new File(projectPath))
        if (!super.isIntegrationMode() && (arrayFiles.size() > Constants.ZERO) && all == Constants.FALSE) {
            logger.error(RETRIEVE_MESSAGE_WARNING)
            option = System.console().readLine("  ${RETRIEVE_QUESTION_TO_CONTINUE}")
        } else {
            option = Constants.YES_OPTION
        }
    }

    /**
     * Shows files and folders replaced
     */
    public void showInfoMessage() {
        if (files == null) {
            return
        }
        ArrayList<String> replacedElements = new ArrayList<String>()
        filesToRetrieve.each { File file ->
            if (!file.exists() || (file.isDirectory() && file.size() == Constants.ZERO)) {
                return
            }
            replacedElements.push(file.name)
        }
        if (!replacedElements.isEmpty()) {
            logger.info("Info: ${replacedElements} will be replaced")
        }
    }

    /**
     * Verifies if exist package xml into project path
     * @return true if exist a package xml file else return false
     */
    def hasPackage() {
        return new File(Paths.get(projectPath, Constants.PACKAGE_FILE_NAME).toString()).exists()
    }
}