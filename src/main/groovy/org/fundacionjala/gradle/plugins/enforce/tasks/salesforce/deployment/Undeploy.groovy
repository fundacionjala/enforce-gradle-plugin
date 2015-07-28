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
    private static final String UN_DEPLOY_DESCRIPTION = 'This task removes all components in your organization according to local repository'
    private static final String START_MESSAGE_TRUNCATE = 'Starting truncate process...'
    private static final String SUCCESS_MESSAGE_TRUNCATE = 'All components truncated were successfully uploaded!'
    private static final String SUCCESS_MESSAGE_DELETE = 'The files were successfully deleted!'
    private static final String START_MESSAGE_UNDEPLOY = 'Starting undeploy process...'
    private static final String DIR_UN_DEPLOY = "undeploy"
    private static final String FILE_NOT_FOUND = "these files can't be deleted from your organization, because these weren't found!"

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
        super(UN_DEPLOY_DESCRIPTION, Constants.DEPLOYMENT)
        filesToTruncate = new ArrayList<File>()
        componentManager = new InterceptorManager()
        componentManager.buildInterceptors()
        interceptorsToExecute = org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.INTERCEPTORS.values().toList()
        standardComponents = MetadataComponent.COMPONENTS.values().toList()
        taskFolderName = DIR_UN_DEPLOY
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
        loadClassifiedFiles(COMPONENTS_TO_TRUNCATE.join(','), excludes)
        loadFilesToTruncate()
        copyFilesToTaskDirectory(filesToTruncate)
        addInterceptor()
        writePackage(taskPackagePath, filesToTruncate)
        combinePackage(taskPackagePath)
        executeDeploy(taskFolderPath, START_MESSAGE_TRUNCATE, SUCCESS_MESSAGE_TRUNCATE)
    }

    /**
     * Loads files that will truncate using components to truncate by default.
     * Components to truncate : 'classes', 'objects', 'triggers', 'pages', 'components', 'workflows', 'tabs'
     */
    public void loadFilesToTruncate() {
        filesToTruncate = classifiedFile.validFiles
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
        executeDeploy(taskFolderPath, START_MESSAGE_UNDEPLOY, SUCCESS_MESSAGE_DELETE)
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

        loadClassifiedFiles(includes.join(', '), "${filesToExclude.join(', ')}")
        ArrayList<File> filesToWriteAtDestructive = getValidFilesFromOrg(classifiedFile.validFiles)

        writePackage(taskDestructivePath, filesToWriteAtDestructive as ArrayList<File>)
        updatePackage(Constants.CUSTOM_FIELD, getCustomFieldsFromStandardObject(), taskDestructivePath)
    }

    /**
     * Gets custom fields from standard objects
     * @return an arrayList with custom field names
     */
    private ArrayList<String> getCustomFieldsFromStandardObject() {
        ArrayList<File> standardObjects = []
        standardComponents.grep(~/.*.object$/).each { String objectName ->
            File standardObject = new File(Paths.get(projectPath, Constants.OBJECTS_FOLDER, objectName).toString())
            if (standardObject.exists()) {
                standardObjects.push(standardObject)
            }
        }
        return Util.getFields(standardObjects)
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
            throw new Exception("${notFoundFiles} ${FILE_NOT_FOUND}")
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

    /**
     * Loads files classified at ClassifiedFile class
     */
    public void loadClassifiedFiles(String includes, String excludes) {
        ArrayList<File> filesFiltered = filter.getFiles(includes, excludes)
        classifiedFile = FileValidator.validateFiles(projectPath, filesFiltered)
    }
}