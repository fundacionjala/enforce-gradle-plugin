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
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.FileValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager.PackageCombiner
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.filter.Filter
import org.gradle.api.file.FileTree

import java.nio.file.Paths

/**
 * Represent base class for needs deploy code in salesforce
 */
abstract class Deployment extends SalesforceTask {
    public DeployMetadata componentDeploy
    public InterceptorManager interceptorManager
    public List<String> interceptorsToExecute = []
    public List<String> interceptors = []
    public String excludes = ""
    public final int FILE_NAME_POSITION = 1

    public Filter filter
    public String taskFolderPath
    public String taskFolderName
    public String taskPackagePath
    public String taskDestructivePath

    /**
     * Sets description and group task
     * @param descriptionTask is description tasks
     * @param groupTask is the group typeName the task
     */
    Deployment(String descriptionTask, String groupTask) {
        super(descriptionTask, groupTask)
        componentDeploy = new DeployMetadata()
        interceptorManager = new InterceptorManager()
        interceptorManager.encoding = project.property(Constants.FORCE_EXTENSION).encoding
        interceptorManager.buildInterceptors()
    }

    /**
     * Executes deploy action
     */
    def executeDeploy(String sourcePath, String startMessage, String successMessage) {
        String fileName = new File(sourcePath).getName()
        logger.debug("Creating zip file at: $buildFolderPath$File.separator$fileName")
        componentDeploy.startMessage = startMessage
        componentDeploy.successMessage = successMessage
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
        interceptorManager.loadFiles(dirToTruncate)
        logger.debug('Adding interceptors')
        interceptorManager.addInterceptors(interceptorsToExecute)
        interceptorManager.addInterceptorsRegistered(project.property(Constants.FORCE_EXTENSION).interceptors as Map)
        logger.debug('Validating interceptors')
        interceptorManager.validateInterceptors()
        logger.debug('Executing truncate process')
        interceptorManager.executeTruncate()
    }

    /**
     * Adds a new interceptor for a specific metadata component
     * @param metadataComponent the name of metadata component
     * @param interceptorName the interceptor name
     * @param interceptorAction the interceptor closure
     */
    void interceptor(String metadataComponent, String interceptorName = '', Closure interceptorAction) {
        interceptorManager.addInterceptor(metadataComponent, interceptorName, interceptorAction)
    }

    /**
     * Adds new interceptor in the first position for a specific metadata group
     * @param metadataComponent the metadata group name
     * @param interceptorName the interceptor name
     * @param interceptorAction the new interceptor
     */
    void firstInterceptor(String componentName, String interceptorName = '', Closure interceptorAction) {
        interceptorManager.addFirstInterceptor(componentName, interceptorName, interceptorAction)
    }

    /**
     * Creates a deployment directory
     * @param pathDirectory is type String
     */
    void createDeploymentDirectory(String pathDirectory) {
        fileManager.createNewDirectory(pathDirectory)
    }

    /**
     * Returns files that were excluded
     * @param criterion is a exclude criterion
     * @return files excluded
     */
    public ArrayList<String> getFilesExcludes(String criterion) {
        ArrayList<String> filesName = []
        ArrayList<File> sourceFiles = []
        ArrayList<String> criterias = getCriterias(criterion)
        FileTree fileTree = project.fileTree(dir: projectPath, includes: criterias)
        sourceFiles = fileTree.getFiles() as ArrayList<File>
        sourceFiles.each { File file ->
            String relativePath = Util.getRelativePath(file, projectPath)
            String extension = Util.getFileExtension(file)
            if ( Util.isValidRelativePath(relativePath)) {
                filesName.push(relativePath)
            }

            if (MetadataComponents.validExtension(extension)) {
                filesName.push(relativePath)
            }
        }
        return filesName.unique()
    }

    /**
     * Gets a criterias to exclde files
     * @param criterion is an String with criterias
     * @return an ArrayList of criterias
     */
    public ArrayList<String> getCriterias(String criterion) {
        ArrayList<String> criterias = new ArrayList<String>()
        criterion.split(Constants.COMMA).each { String critery ->
            critery = critery.replaceAll(Constants.BACK_SLASH, Constants.SLASH)
            def criteriaSplitted = critery.split(Constants.SLASH)
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
     * Combines package that was updated from build folder and package from source directory
     * @param buildPackagePath is path of package that is into build directory
     */
    public void combinePackage(String buildPackagePath) {
        PackageCombiner.packageCombine(projectPackagePath, buildPackagePath)
        if (excludes) {
            PackageCombiner.removeMembersFromPackage(buildPackagePath, getFilesExcludes(excludes))
        }
    }

    /**
     * Combines package from build folder and package from source directory
     * @param buildPackagePath is path of package that is into build directory
     */
    public void combinePackageToUpdate(String buildPackagePath) {
        PackageCombiner.packageCombineToUpdate(projectPackagePath, buildPackagePath)
        if (excludes) {
            PackageCombiner.removeMembersFromPackage(buildPackagePath, getFilesExcludes(excludes))
        }
    }

    /**
     * Gets a map with parameters as key and their contents as values
     * @param parameterNames is an ArrayList with parameters name
     * @return a Map with parameters and their values
     */
    public Map<String, String> getParameterWithTheirsValues(ArrayList<String> parameterNames) {
        Map<String, String> parameterValues = [:]
        parameterNames.each { String parameterName ->
            if (Util.isValidProperty(parameters, parameterName) && !Util.isEmptyProperty(parameters, parameterName)) {
                parameterValues.put(parameterName, parameters[parameterName].toString())
            }
        }
        return parameterValues
    }

    /**
     * Sets task path as: deploy, undeploy, update, upload, delete, truncate folder names
     * Sets package path of build directory
     * Sets destructive path of build directory
     * Creates an instance of Filter class
     */
    @Override
    void setup() {
        taskFolderPath = Paths.get(buildFolderPath, taskFolderName).toString()
        taskPackagePath = Paths.get(taskFolderPath, Constants.PACKAGE_FILE_NAME).toString()
        taskDestructivePath = Paths.get(taskFolderPath, Constants.FILE_NAME_DESTRUCTIVE).toString()
        filter = new Filter(project, projectPath)
    }

    /**
     * Loads excludes parameter value
     */
    @Override
    void loadParameters() {
        if (Util.isValidProperty(parameters, Constants.PARAMETER_EXCLUDES) && !Util.isEmptyProperty(parameters, Constants.PARAMETER_EXCLUDES)) {
            excludes = parameters[Constants.PARAMETER_EXCLUDES].toString()
        }
    }

    /**
     * Gets a map with files classified as valid, invalid and not found files
     * @param includes is String type
     * @param excludes is String type
     * @return a map with files classified
     */
    Map<String, ArrayList<File>> getClassifiedFiles(String includes, String excludes) {
        ArrayList<File> filesFiltered = filter.getFiles(includes, excludes)
        Map<String, ArrayList> filesValidated = FileValidator.validateFiles(projectPath, filesFiltered)
        return filesValidated
    }

    /**
     * Copies files into temporary folder of task
     * @param filesToCopy is an arrayList of files to copy
     */
    void copyFilesToTaskDirectory(ArrayList<File> filesToCopy) {
        fileManager.copy(projectPath, filesToCopy, taskFolderPath)
    }
}