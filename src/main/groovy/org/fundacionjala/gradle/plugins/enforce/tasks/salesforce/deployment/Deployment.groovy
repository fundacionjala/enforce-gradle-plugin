/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.interceptor.InterceptorManager
import org.fundacionjala.gradle.plugins.enforce.metadata.DeployMetadata
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.SalesforceTask
import org.fundacionjala.gradle.plugins.enforce.undeploy.PackageComponent
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.ClassifiedFile
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.FileValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager.PackageCombiner
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.filter.Filter
import org.fundacionjala.gradle.plugins.enforce.wsc.Connector

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
    public String showValidatedFiles = Constants.TRUE_OPTION

    public Filter filter
    public String taskFolderPath
    public String taskFolderName
    public String taskPackagePath
    public String taskDestructivePath
    ClassifiedFile classifiedFile
    protected boolean checkOnly

    /**
     * Sets description and group task
     * @param descriptionTask is description tasks
     * @param groupTask is the group typeName the task
     */
    Deployment(String descriptionTask, String groupTask) {
        super(descriptionTask, groupTask)
        checkOnly = false
        componentDeploy = new DeployMetadata()
        interceptorManager = new InterceptorManager()
        interceptorManager.encoding = project.property(Constants.FORCE_EXTENSION).encoding
        interceptorManager.buildInterceptors()
    }

    /**
     * Executes generic deploy action
     */
    public def executeDeploy(String sourcePath, String startMessage, String successMessage) {
        String fileName = new File(sourcePath).getName()
        logger.debug("Creating zip file at: $buildFolderPath$File.separator$fileName")
        componentDeploy.startMessage = startMessage
        componentDeploy.successMessage = successMessage
        String pathZipToDeploy = createZip(sourcePath, buildFolderPath, fileName)
        componentDeploy.setPath(pathZipToDeploy)
        logger.debug('Deploying components')
        String apiVersion = PackageComponent.getApiVersion(projectPackagePath) < Connector.API_VERSION?
                Connector.API_VERSION : PackageComponent.getApiVersion(projectPackagePath)
        componentDeploy.deploy(poll, waitTime, credential, apiVersion, checkOnly)
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
     * Combines package from build folder and package from source directory
     * @param buildPackagePath is path of package that is into build directory
     */
    public void combinePackageToUpdate(String buildPackagePath) {
        PackageCombiner.packageCombineToUpdate(projectPackagePath, buildPackagePath)
        if (excludes) {
            removeFilesExcluded(buildPackagePath)
        }
    }

    private void removeFilesExcluded(String buildPackagePath) {
        ArrayList<String> filesName = []
        filter.getFiles(excludes, Constants.EMPTY).each { File file ->
            String relativePath = Util.getRelativePath(file, projectPath)
            filesName.push(relativePath)
        }
        PackageCombiner.removeMembersFromPackage(buildPackagePath, filesName.unique())
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
        classifiedFile = new ClassifiedFile()
    }

    /**
     * Loads excludes parameter value
     */
    @Override
    void loadParameters() {
        loadCommonParameters()
    }

    /**
     * Gets a map with files classified as valid, invalid and not found files
     * @param includes is String type
     * @param excludes is String type
     * @return a map with files classified
     */
    void loadClassifiedFiles(String includes, String excludes) {
        ArrayList<File> filesFiltered = filter.getFiles(includes, excludes)
        classifiedFile = FileValidator.validateFiles(projectPath, filesFiltered)
        classifiedFile.ShowClassifiedFiles(showValidatedFiles == Constants.TRUE_OPTION, projectPath)
    }

    /**
     * Copies files into temporary folder of task
     * @param filesToCopy is an arrayList of files to copy
     */
    void copyFilesToTaskDirectory(ArrayList<File> filesToCopy) {
        fileManager.copy(projectPath, filesToCopy, taskFolderPath)
    }

    /**
     * Loads showValidatedFiles and excludes parameter
     */
    void loadCommonParameters() {
        if (Util.isValidProperty(parameters, Constants.PARAMETER_EXCLUDES) &&
                !Util.isEmptyProperty(parameters, Constants.PARAMETER_EXCLUDES)) {
            excludes = parameters[Constants.PARAMETER_EXCLUDES].toString()
        }

        if (Util.isValidProperty(parameters, Constants.PARAMETER_SHOW_VALIDATED_FILES)) {
            showValidatedFiles = parameters[Constants.PARAMETER_SHOW_VALIDATED_FILES].toString()
            return
        }

        if (project.enforce.showValidatedFiles) {
            showValidatedFiles = project.enforce.showValidatedFiles
        }
    }
}
