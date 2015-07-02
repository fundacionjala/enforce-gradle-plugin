/*
* Copyright (c) Fundacion Jala. All rights reserved.
* Licensed under the MIT license. See LICENSE file in the project root for full license information.
*/

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.interceptor.InterceptorManager
import org.fundacionjala.gradle.plugins.enforce.undeploy.PackageComponent
import org.fundacionjala.gradle.plugins.enforce.undeploy.SmartFilesValidator
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.FileValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponent
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.filter.Filter

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
* Undeploys an org using metadata API
*/
class Undeploy extends Deployment {
    public PackageComponent packageComponent
    public ArrayList<File> filesToTruncate
    public String folderUnDeploy
    public String unDeployPackagePath
    public String unDeployDestructivePath
    public SmartFilesValidator smartFilesValidator
    InterceptorManager componentManager
    List<String> standardComponents
    private Filter filter
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
    }

    @Override
    void runTask() {
        setup()
        loadParameter()
        truncate()
        addNewStandardObjects()
        unDeploy()
    }

    /**
     * Sets folder undeploy path
     * Sets destructive path at build/undeploy directory
     * Sets package path at build/undeploy directory
     * Creates an instance of SmartFilesValidator class
     * Creates an instance of Filter class
     */
    public void setup() {
        folderUnDeploy = Paths.get(buildFolderPath, Constants.DIR_UN_DEPLOY).toString()
        unDeployPackagePath = Paths.get(folderUnDeploy, PACKAGE_NAME).toString()
        unDeployDestructivePath = Paths.get(folderUnDeploy, Constants.FILE_NAME_DESTRUCTIVE).toString()
        smartFilesValidator = new SmartFilesValidator(Util.getJsonQueries(projectPackagePath, credential))
        filter = new Filter(project, projectPath)
    }

    /**
     * Steps to deploy code truncated
     */
    public void truncate() {
        createDeploymentDirectory(folderUnDeploy)
        loadFilesToTruncate()
        copyFilesToTruncate()
        addInterceptor()
        writePackage(unDeployPackagePath, filesToTruncate)
        combinePackage(unDeployPackagePath)
        setDeployStatusMessages(Constants.START_MESSAGE_TRUNCATE, Constants.SUCCESS_MESSAGE_TRUNCATE, Constants.EMPTY)
        executeDeploy(folderUnDeploy)
    }

    /**
     * Loads files that will truncate using components to truncate by default.
     * Components to truncate : 'classes', 'objects', 'triggers', 'pages', 'components', 'workflows', 'tabs'
     */
    public void loadFilesToTruncate() {
        String includes = COMPONENTS_TO_TRUNCATE.join(', ')
        Map <String, ArrayList<File>> filesClassified = getFilesValidated(includes, excludes)

        String exceptionMessage = Util.getExceptionMessage(filesClassified)
        if (!exceptionMessage.isEmpty()) {
            throw new Exception(exceptionMessage)
        }
        filesToTruncate = filesClassified.get(Constants.VALID_FILE)
    }

    /**
     * Copies files to build/undeploy directory
     */
    public void copyFilesToTruncate() {
        fileManager.copy(projectPath, filesToTruncate, folderUnDeploy)
    }

    /**
     * Adds interceptors
     */
    public void addInterceptor() {
        interceptorsToExecute += interceptors
        truncateComponents(folderUnDeploy)
    }

    /**
     * Steps to delete files from your organization
     */
    public void unDeploy() {
        createDeploymentDirectory(folderUnDeploy)
        deployToDeleteComponents()
        combinePackage(unDeployDestructivePath)
        setDeployStatusMessages(Constants.START_MESSAGE_UNDEPLOY,Constants.SUCCESS_MESSAGE_DELETE, Constants.EMPTY)
        executeDeploy(folderUnDeploy)
    }

    /**
     * Deploys to delete all components from package.xml
     */
    public void deployToDeleteComponents() {
        Files.copy(Paths.get(projectPackagePath), Paths.get(unDeployPackagePath), StandardCopyOption.REPLACE_EXISTING)
        packageComponent = new PackageComponent(unDeployPackagePath)
        writePackage(unDeployPackagePath, [])

        ArrayList<String> includes = packageComponent.components
        ArrayList<String> filesToExclude = Util.getComponentsWithWildcard(standardComponents)

        includes.addAll(Util.getComponentsWithWildcard(standardComponents).grep(~/.*.object$/))
        filesToExclude.addAll(packageComponent.components.grep(~/.*.workflow$/) as ArrayList<String>)
        filesToExclude.add(excludes)

        ArrayList<File> filesFiltered = getFilesValidated(includes.join(', '), "${filesToExclude.join(', ')}").get(Constants.VALID_FILE)
        filesFiltered = smartFilesValidator.filterFilesAccordingOrganization(filesFiltered, projectPath)
        writePackage(unDeployDestructivePath, filesFiltered as ArrayList<File>)
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
     * Loads parameters of this task
     */
    public void loadParameter() {
        Map <String, String> parameters = getParameterWithTheirsValues([Constants.PARAMETER_EXCLUDES])
        if (parameters.containsKey(Constants.PARAMETER_EXCLUDES)) {
            excludes = parameters.get(Constants.PARAMETER_EXCLUDES)
        }
    }

    /**
     * Gets a Map with files classified by valid, invalid and not found
     * @param includes is a String
     * @param excludes is a String
     * @return a Map
     */
    private Map<String, ArrayList<File>> getFilesValidated(String includes, String excludes) {
        ArrayList<File> filesFiltered = filter.getFiles(includes, excludes)
        Map<String, ArrayList> filesValidated = FileValidator.validateFiles(projectPath, filesFiltered)
        return filesValidated
    }

    /**
     * Sets deploy status messages
     * @param startMessage is deploy start message
     * @param successMessage is deploy success message
     * @param errorMessage is deploy error message
     */
    private void setDeployStatusMessages(String startMessage, String successMessage, String errorMessage) {
        componentDeploy.startMessage = startMessage
        componentDeploy.successMessage = successMessage
        componentDeploy.errorMessage = errorMessage
    }
}