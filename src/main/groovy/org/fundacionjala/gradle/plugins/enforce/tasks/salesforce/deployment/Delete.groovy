/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentStates
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageGenerator

import java.nio.file.Paths

/**
 * Updates an org using metadata API
 */
class Delete extends Deployment {
    private static final String DESCRIPTION_OF_TASK = "This task deploys just the files that were changed"
    private final String FOLDERS_DEPLOY = "folders"
    private final String DIR_UPDATE_FOLDER = "delete"
    private final String FILE_NAME_DESTRUCTIVE = "destructiveChanges.xml"
    private final String NOT_FILES_CHANGED = "There are not files changed"
    public String pathUpdate
    ArrayList<File> filesToCopy
    ArrayList<File> filesToUpdate
    String folders
    ArrayList<File> filesExcludes
    PackageGenerator packageGenerator

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    Delete() {
        super(DESCRIPTION_OF_TASK, Constants.DEPLOYMENT)
        filesToCopy = new ArrayList<File>()
        filesToUpdate = new ArrayList<File>()
        filesExcludes = new ArrayList<File>()
        packageGenerator = new PackageGenerator()
        interceptorsToExecute = [org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.REMOVE_DEPRECATE.id]
    }

    /**
     * Executes the task
     */
    @Override
    void runTask() {
        pathUpdate = Paths.get(buildFolderPath, DIR_UPDATE_FOLDER).toString()
        createDeploymentDirectory(pathUpdate)
        loadFilesChanged()
        verifyParameter()

        createDestructive()
        createPackage()
        copyFilesChanged()
   //     truncate(pathUpdate)

  /*      executeDeploy(pathUpdate)
        packageGenerator.saveFileTrackerMap()*/

    }

    def truncate(String pathToTruncate) {
        interceptorsToExecute += interceptors
        truncateComponents(pathToTruncate)
    }

    /**
     * Creates packages to all files which has been changed
     */
    def createPackage() {
        println "\n** CreatePackage()"
        println "fileTrackerMap : "+packageGenerator.fileTrackerMap
        packageGenerator.fileTrackerMap.each { nameFile, resultTracker ->
            println "--  "+resultTracker.state+ " == "+ComponentStates.DELETED
            if (resultTracker.state == ComponentStates.DELETED) {
                filesToCopy.add(new File(nameFile))
            }
        }
        println "filesToCopy : "+filesToCopy
        packageGenerator.buildPackage(Paths.get(pathUpdate, PACKAGE_NAME).toString())
    }

    /**
     * Creates package to all files which has been deleted
     */
    def createDestructive() {
        println "\n** CreateDestructive "
        packageGenerator.buildDestructive(Paths.get(pathUpdate, FILE_NAME_DESTRUCTIVE).toString())
    }

    /**
     * Loads all files which has been changed on filesChanged
     */
    def loadFilesChanged() {
        packageGenerator.init(projectPath, credential)
    }

    /**
     * Verifies if there is files changed in folders inserted by user
     */
    def verifyParameter() {
        if (Util.isValidProperty(project, "folders")) {
            folders = project.folders
        }
        if (folders) {
            ArrayList<String> foldersName = folders.split(Constants.COMMA)

            ArrayList<String> invalidFolders = Util.getInvalidFolders(foldersName)
            validateFolders(foldersName)
            if (!invalidFolders.empty) {
                throw new Exception("${Constants.INVALID_FOLDER}: ${invalidFolders}")
            }
            ArrayList<File> validatedFiles = fileManager.getValidElements(projectPath, excludeFilesToMonitor)
            packageGenerator.listFileToDelete(foldersName,validatedFiles)
        }
    }

    /**
     * Copies files using fileManager
     */
    def copyFilesChanged() {
        println "\n** CopyFilesChanged()"
        println "filesCopy : "+filesToCopy
        filesToCopy.each { file ->
            File xmlFile = fileManager.getValidateXmlFile(file)
            if (xmlFile) {
                filesToUpdate.push(xmlFile)
            }
            filesToUpdate.push(file)
        }
        println "filesToUpdate : "+filesToUpdate
        println "pathUpdate : "+pathUpdate
        fileManager.copy(projectPath,filesToUpdate, pathUpdate)
    }

    /**
     * Prints files changed
     */
    def showFilesChanged() {
        if (packageGenerator.fileTrackerMap.size() > 0) {
            logger.quiet("*********************************************")
            logger.quiet("              Status Files Changed             ")
            logger.quiet("*********************************************")
            packageGenerator.fileTrackerMap.each { nameFile, status ->
                logger.quiet("${Paths.get(nameFile).getFileName().toString()}${" - "}${status.toString()}")
            }
            logger.quiet("*********************************************")
        }
    }

    /**
     * Shows files excluded if there are more than five this shows just a message.
     * @return
     */
    def showFilesExcludes() {
        if (filesExcludes.empty) {
            return
        }
        if (filesExcludes.size() < 5) {
            logger.quiet("*********************************************")
            logger.quiet("              Files excluded                 ")
            logger.quiet("*********************************************")
            filesExcludes.each { File file ->
                logger.quiet("${file.getName()}${" - "} excluded")
            }
            logger.quiet("*********************************************")
        } else {
            logger.quiet("${filesExcludes.size()}${' files were excluded\n'}")
        }
    }

    /**
     * ExcludeFiles from filesExcludes map
     */
    private void excludeFilesFromFilesChanged() {
        ArrayList<File> filesFiltered = excludeFiles(packageGenerator.getFiles())
        filesExcludes = packageGenerator.excludeFiles(filesFiltered)
    }
}