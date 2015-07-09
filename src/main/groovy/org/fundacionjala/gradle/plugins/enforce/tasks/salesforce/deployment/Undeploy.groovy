/*
* Copyright (c) Fundacion Jala. All rights reserved.
* Licensed under the MIT license. See LICENSE file in the project root for full license information.
*/

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.interceptor.InterceptorManager
import org.fundacionjala.gradle.plugins.enforce.undeploy.PackageComponent
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.FileValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponent
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.OrgValidator

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
* Undeploys an org using metadata API
*/
class Undeploy extends Deployment {
    public PackageComponent packageComponent
    public ArrayList<File> filesToTruncate
    InterceptorManager componentManager
    List<String> standardComponents
    private final List<String> COMPONENTS_TO_TRUNCATE = ['classes', 'objects', 'triggers', 'pages', 'components', 'workflows', 'tabs']

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    Undeploy() {
        super(Constants.UN_DEPLOY_DESCRIPTION, Constants.DEPLOYMENT)
        filesToTruncate = new ArrayList<File>()
        componentManager = new InterceptorManager()
        componentManager.buildInterceptors()
        interceptorsToExecute = org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.INTERCEPTORS.values().toList()
        standardComponents = MetadataComponent.COMPONENTS.values().toList()
        taskFolderName = Constants.DIR_UN_DEPLOY
    }

    @Override
    void runTask() {
        truncate()
        addNewStandardObjects()
        unDeploy()
    }

    /**
     * Steps to deploy code truncated
     */
    public void truncate() {
        createDeploymentDirectory(taskFolderPath)
        loadFilesToTruncate()
        copyFilesToTaskDirectory(filesToTruncate)
        addInterceptor()
        writePackage(taskPackagePath, filesToTruncate)
        combinePackage(taskPackagePath)
        componentDeploy.startMessage = Constants.START_MESSAGE_TRUNCATE
        componentDeploy.successMessage = Constants.SUCCESS_MESSAGE_TRUNCATE
        executeDeploy(taskFolderPath)
    }

    /**
     * Loads files that will truncate using components to truncate by default.
     * Components to truncate : 'classes', 'objects', 'triggers', 'pages', 'components', 'workflows', 'tabs'
     */
    public void loadFilesToTruncate() {
        String includes = COMPONENTS_TO_TRUNCATE.join(', ')
        Map <String, ArrayList<File>> filesClassified = getClassifiedFiles(includes, excludes)

        String exceptionMessage = Util.getExceptionMessage(filesClassified)
        if (!exceptionMessage.isEmpty()) {
            throw new Exception(exceptionMessage)
        }
        filesToTruncate = filesClassified.get(Constants.VALID_FILE)
    }

    /**
     * Adds interceptors
     */
    public void addInterceptor() {
        interceptorsToExecute += interceptors
        truncateComponents(taskFolderPath)
    }

    /**
     * Steps to delete files from your organization
     */
    public void unDeploy() {
        createDeploymentDirectory(taskFolderPath)
        deployToDeleteComponents()
        combinePackage(taskDestructivePath)
        componentDeploy.startMessage = Constants.START_MESSAGE_UNDEPLOY
        componentDeploy.successMessage = Constants.SUCCESS_MESSAGE_DELETE
        executeDeploy(taskFolderPath)
    }

    /**
     * Deploys to delete all components from package.xml
     */
    public void deployToDeleteComponents() {
        Files.copy(Paths.get(projectPackagePath), Paths.get(taskPackagePath), StandardCopyOption.REPLACE_EXISTING)
        packageComponent = new PackageComponent(taskPackagePath)
        writePackage(taskPackagePath, [])

        ArrayList<String> includes = packageComponent.components
        ArrayList<String> filesToExclude = Util.getComponentsWithWildcard(standardComponents)

        includes.addAll(Util.getComponentsWithWildcard(standardComponents).grep(~/.*.object$/))
        filesToExclude.addAll(packageComponent.components.grep(~/.*.workflow$/) as ArrayList<String>)
        filesToExclude.add(excludes)

        ArrayList<File> filesFiltered = getClassifiedFiles(includes.join(', '), "${filesToExclude.join(', ')}").get(Constants.VALID_FILE)
        ArrayList<File> filesToWriteAtDestructive = getValidFilesFromOrg(filesFiltered)

        writePackage(taskDestructivePath, filesToWriteAtDestructive as ArrayList<File>)
    }

    /**
     * Gets valid files from org if there aren't files it show an exception message
     * @param files is an ArrayList of files
     * @return an ArrayList with files validates from your org
     */
    public ArrayList<File> getValidFilesFromOrg(ArrayList<File> files) {
        Map <String, ArrayList<File>> filesClassified = OrgValidator.validateFiles(credential, files, projectPath)
        ArrayList<String> notFoundFiles = []

        ArrayList<File> validFiles = filesClassified.get(Constants.VALID_FILE)
        validFiles.addAll(filesClassified.get(Constants.FILE_WITHOUT_VALIDATOR))

        filesClassified.get(Constants.DOES_NOT_EXIST_FILES).each { File file ->
            notFoundFiles.push(file.name)
        }

        if (!notFoundFiles.isEmpty()) {
            throw new Exception("${notFoundFiles} ${Constants.FILE_NOT_FOUND}")
        }
        return validFiles.sort()
    }

    /**
     * Adds new standard objects from user property
     */
    public void addNewStandardObjects() {
        if (Util.isValidProperty(project, Constants.FORCE_EXTENSION) &&
                project[Constants.FORCE_EXTENSION].standardObjects) {
            standardComponents += project[Constants.FORCE_EXTENSION].standardObjects
        }
    }
}