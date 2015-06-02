/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.interceptor.InterceptorManager
import org.fundacionjala.gradle.plugins.enforce.metadata.DeployMetadata
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.SalesforceTask
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.bundling.Zip

import java.nio.file.Paths

/**
 * Represent base class for needs deploy code in salesforce
 */
abstract class Deployment extends SalesforceTask {
    private final String NAME_TASK_ZIP = "createZip"
    DeployMetadata componentDeploy
    InterceptorManager componentManager
    List<String> interceptorsToExecute = []
    List<String> interceptors = []
    public final String EXCLUDES = 'excludes'
    public String excludes
    public final int FILE_NAME_POSITION = 1
    public final String SLASH = "/"
    public final String BACKSLASH = "\\\\"

    /**
     * Sets description and group task
     * @param descriptionTask is description tasks
     * @param groupTask is the group typeName the task
     */
    Deployment(String descriptionTask, String groupTask) {
        super(descriptionTask, groupTask)
        componentDeploy = new DeployMetadata()
        componentManager = new InterceptorManager()
        componentManager.buildInterceptors()
    }

    /**
     * Executes deploy action
     */
    def executeDeploy(String sourcePath) {
        String fileName = new File(sourcePath).getName()
        logger.debug("Creating zip file at: $buildFolderPath$File.separator$fileName")
        String pathZipToDeploy = createZip(sourcePath, buildFolderPath, fileName)
        componentDeploy.setPath(pathZipToDeploy)
        logger.debug('Deploying components')
        componentDeploy.deploy(poll, waitTime, credential)
        deleteTemporaryFiles()
    }

    /**
     * Truncates all metadata components
     * @param dirToTruncate the directory path to truncate
     */
    def truncateComponents(String dirToTruncate) {
        logger.debug('Loading files to truncate')
        componentManager.loadFiles(dirToTruncate)
        logger.debug('Adding interceptors')
        componentManager.addInterceptors(interceptorsToExecute)
        componentManager.addInterceptorsRegistered(project.property(Constants.FORCE_EXTENSION).interceptors as Map)
        logger.debug('Validating interceptors')
        componentManager.validateInterceptors()
        logger.debug('Executing truncate process')
        componentManager.executeTruncate()
    }

    /**
     * Adds a new interceptor for a specific metadata component
     * @param metadataComponent the name of metadata component
     * @param interceptorName the interceptor name
     * @param interceptorAction the interceptor closure
     */
    void interceptor(String metadataComponent, String interceptorName = '', Closure interceptorAction) {
        componentManager.addInterceptor(metadataComponent, interceptorName, interceptorAction)
    }

    /**
     * Adds new interceptor in the first position for a specific metadata group
     * @param metadataComponent the metadata group name
     * @param interceptorName the interceptor name
     * @param interceptorAction the new interceptor
     */
    void firstInterceptor(String componentName, String interceptorName = '', Closure interceptorAction) {
        componentManager.addFirstInterceptor(componentName, interceptorName, interceptorAction)
    }

    /**
     * Creates a deployment directory
     * @param pathDirectory is type String
     */
    void createDeploymentDirectory(String pathDirectory) {
        fileManager.createNewDirectory(pathDirectory)
    }

    /**
     * Excludes file or files using criterion
     * @param files to exclude
     * @param criterion to filter files
     */
    public ArrayList<File> excludeFilesByCriterion(ArrayList<File> files, String criterion) {
        if (criterion == null) {
            logger.error("${Constants.NULL_PARAM_EXCEPTION} criterion")
        }
        ArrayList<File> filesFiltered = new ArrayList<File>()
        ArrayList<File> sourceFiles = new ArrayList<File>()
        ArrayList<String> criterias = getCriterias(criterion)
        FileTree fileTree = project.fileTree(dir: projectPath, excludes: criterias)
        sourceFiles = fileTree.getFiles() as ArrayList<File>
        sourceFiles.each { File file ->
            if (files.contains(file)) {
                filesFiltered.push(file)
            }
        }
        return filesFiltered
    }

    /**
     * Returns files that were excluded
     * @param criterion is a exclude criterion
     * @return files excluded
     */
    public ArrayList<String> getFilesExcludes(String criterion) {
        ArrayList<String> filesName = new ArrayList<String>()
        ArrayList<File> sourceFiles = new ArrayList<File>()
        ArrayList<String> criterias = getCriterias(criterion)
        FileTree fileTree = project.fileTree(dir: projectPath, includes: criterias)
        sourceFiles = fileTree.getFiles() as ArrayList<File>
        sourceFiles.each { File file ->
            String relativePath = Util.getRelativePath(file, projectPath)
            String extension = Util.getFileExtension(file)
            if ( isValidRelativePath(relativePath)) {
                filesName.push(relativePath)
            }

            if (MetadataComponents.validExtension(extension)) {
                filesName.push(relativePath)
            }
        }
        return filesName.unique()
    }

    /**
     * Validates relative path
     * @param relativePath is a relative path of a component
     * @return true if is valid
     */
    public boolean isValidRelativePath(String relativePath) {
        boolean result = false
        String folderName = Util.getFirstPath(relativePath)
        if ( MetadataComponents.validFolder(folderName) && MetadataComponents.getExtensionByFolder(folderName) == ""){
            result = true
        }
        return  result
    }

    /**
     * Gets a criterias to exclde files
     * @param criterion is an String with criterias
     * @return an ArrayList of criterias
     */
    public ArrayList<String> getCriterias(String criterion) {
        ArrayList<String> criterias = new ArrayList<String>()
        criterion.split(Constants.COMMA).each { String critery ->
            critery = critery.replaceAll(BACKSLASH, SLASH)
            def criteriaSplitted = critery.split(SLASH)
            if (criteriaSplitted.size() == FILE_NAME_POSITION) {
                criterias.push("${critery}${File.separator}${Constants.WILDCARD}${Constants.WILDCARD}")
                return
            }
            criterias.push(critery)
            criterias.push("${critery}${Constants.META_XML}")
        }
        return criterias
    }

    /**
     * Excludes files
     * @param filesToFilter files that will be filter
     * @return ArrayList with files filter
     */
    public excludeFiles(ArrayList<File> filesToFilter) {
        if (filesToFilter == null) {
            logger.error("${Constants.NULL_PARAM_EXCEPTION} filesToFilter")
        }
        ArrayList<File> filesFiltered = filesToFilter.clone() as ArrayList<File>
        if (Util.isValidProperty(project, EXCLUDES) && !Util.isEmptyProperty(project, EXCLUDES)) {
            excludes = project.excludes as String
        }
        if (excludes) {
            validateParameter(excludes)
            filesFiltered = excludeFilesByCriterion(filesFiltered, excludes)
        }
        return filesFiltered
    }

    /**
     * Validates parameter's values
     * @param parameterValues are files name that will be excluded
     */
    public void validateParameter(String parameterValues) {
        parameterValues = parameterValues.replaceAll(BACKSLASH, SLASH)
        ArrayList<String> fileNames = new ArrayList<String>()
        ArrayList<String> folderNames = new ArrayList<String>()
        parameterValues.split(Constants.COMMA).each { String parameter ->
            if (parameter.contains(Constants.WILDCARD)) {
                return
            }
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
     * Validates folders name
     * @param foldersName is type array list contents folders name
     */
    public void validateFolders(ArrayList<String> foldersName) {
        ArrayList<String> invalidFolders = new ArrayList<String>()
        invalidFolders = Util.getInvalidFolders(foldersName)
        String errorMessage = ''
        if (!invalidFolders.empty) {
            errorMessage = "${Constants.INVALID_FOLDER}: ${invalidFolders}"
        }

        ArrayList<String> notExistFolders = new ArrayList<String>()
        notExistFolders = Util.getNotExistFolders(foldersName, projectPath)
        if (!notExistFolders.empty) {
            errorMessage += "\n${Constants.DOES_NOT_EXIST_FOLDER} ${notExistFolders}"
        }

        ArrayList<String> emptyFolders = new ArrayList<String>()
        emptyFolders = Util.getEmptyFolders(foldersName, projectPath)
        if (!emptyFolders.empty) {
            errorMessage += "\n${Constants.EMPTY_FOLDERS} ${emptyFolders}"
        }

        if (!errorMessage.isEmpty()) {
            throw new Exception(errorMessage)
        }
    }

    /**
     * Validates files name
     * @param filesName is type array list contents files name
     */
    public void validateFiles(ArrayList<String> filesName) {
        ArrayList<String> invalidFiles = new ArrayList<String>()
        ArrayList<String> notExistFiles = new ArrayList<String>()
        String errorMessage = ''
        filesName.each { String fileName ->
            def extension = Util.getFileExtension(new File(Paths.get(projectPath, fileName).toString()))
            if (!MetadataComponents.validExtension(extension)) {
                invalidFiles.push(fileName)
            }
            if (!new File(Paths.get(projectPath, fileName).toString()).exists()) {
                notExistFiles.push(fileName)
            }
        }
        if (!invalidFiles.isEmpty()) {
            errorMessage = "${Constants.INVALID_FILE}: ${invalidFiles}"
        }
        if (!notExistFiles.isEmpty()) {
            errorMessage += "\n${Constants.DOES_NOT_EXIST_FILES} ${notExistFiles}"
        }
        if (!errorMessage.isEmpty()) {
            throw new Exception(errorMessage)
        }
    }

    public void combinePackage(String buildPackagePath) {
        PackageCombiner.packageCombine(projectPackagePath, buildPackagePath)
        if (excludes) {
            PackageCombiner.removeMembersFromPackage(buildPackagePath, getFilesExcludes(excludes))
        }
    }
}
