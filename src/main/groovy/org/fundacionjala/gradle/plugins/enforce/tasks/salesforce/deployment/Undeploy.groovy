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
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponent
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.QueryBuilder
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.ToolingAPI
import org.gradle.api.GradleException
import org.gradle.api.file.FileTree

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 * Undeploys an org using metadata API
 */
class Undeploy extends Deployment {
    private List includesComponents
    private FileTree files
    private ArrayList<String> workflowNames
    private ArrayList<File> workflowFiles
    private QueryBuilder queryBuilder

    public ToolingAPI toolingAPI
    public PackageComponent packageComponent
    public ArrayList<File> filesToTruncate
    public String folderUnDeploy
    public String unDeployPackagePath
    public String unDeployDestructivePath
    public SmartFilesValidator smartFilesValidator
    InterceptorManager componentManager
    List<String> standardComponents

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
        workflowNames = []
    }

    @Override
    void runTask() {
        setupFilesToUnDeploy()
        initializeQueries(getJsonQueries())
        truncateFiles()
        deployTruncatedComponents()
        addNewStandardObjects()
        createDeploymentDirectory(folderUnDeploy)
        deployToDeleteComponents()
    }

    /**
     * Creates undeploy folder into build directory
     * Sets destructive path to build directory
     */
    def setupFilesToUnDeploy() {
        folderUnDeploy = Paths.get(buildFolderPath, Constants.DIR_UN_DEPLOY).toString()
        createDeploymentDirectory(folderUnDeploy)
        unDeployPackagePath = Paths.get(folderUnDeploy, PACKAGE_NAME).toString()
        unDeployDestructivePath = Paths.get(folderUnDeploy, Constants.FILE_NAME_DESTRUCTIVE).toString()
    }

    /**
     * Initializes queries to filter files to deploy
     */
    def initializeQueries(ArrayList<String> jsonQueries) {
        smartFilesValidator = new SmartFilesValidator(jsonQueries)
    }

    /**
     * Truncates files from project directory and copy into build directory
     */
    def truncateFiles() {
        Files.copy(Paths.get(projectPath, PACKAGE_NAME), Paths.get(folderUnDeploy, PACKAGE_NAME), StandardCopyOption.REPLACE_EXISTING)
        packageComponent = new PackageComponent(unDeployPackagePath)
        filesToTruncate = fileManager.getFilesByFolders(projectPath, packageComponent.truncatedDirectories).sort()
        filesToTruncate = smartFilesValidator.filterFilesAccordingOrganization(filesToTruncate, projectPath)
        filesToTruncate = excludeFiles(filesToTruncate)
        fileManager.copy(projectPath, filesToTruncate, folderUnDeploy)
        interceptorsToExecute += interceptors
        truncateComponents(folderUnDeploy)
    }

    /**
     * Deploys all truncated components
     */
    def deployTruncatedComponents() {
        writePackage(unDeployPackagePath, filesToTruncate)
        componentDeploy.startMessage = Constants.START_MESSAGE_TRUNCATE
        componentDeploy.successMessage = Constants.SUCCESS_MESSAGE_TRUNCATE
        combinePackage(unDeployPackagePath)
        executeDeploy(folderUnDeploy)
    }

    /**
     * Deploys to delete all components from package.xml
     */
    def deployToDeleteComponents() {
        writePackage(unDeployPackagePath, [])
        includesComponents = packageComponent.components
        def excludeComponents = getComponentsWithWildcard(standardComponents)
        workflowNames = packageComponent.components.grep(~/.*.workflow$/) as ArrayList<String>
        excludeComponents.addAll(workflowNames)
        files = project.fileTree(dir: projectPath, includes: includesComponents, excludes: excludeComponents)
        ArrayList<File> filesFiltered = smartFilesValidator.filterFilesAccordingOrganization(files.getFiles().sort() as ArrayList<File>, projectPath)
        filesFiltered = excludeFiles(filesFiltered)
        preparePackage(unDeployDestructivePath, filesFiltered)
        includesComponents = getComponentsWithWildcard(standardComponents).grep(~/.*.object$/)
        files = project.fileTree(dir: projectPath, includes: includesComponents)
        ArrayList<File> objectFiles = files.getFiles().sort()
        objectFiles = excludeFiles(objectFiles)
        savePackage()
        updatePackage(Constants.CUSTOM_FIELD_NAME, getFields(objectFiles), unDeployDestructivePath)
        if (!workflowNames.empty) {
            workflowFiles = project.fileTree(dir: projectPath, includes: workflowNames).toList()
            workflowFiles = excludeFiles(workflowFiles)
            updatePackage(Constants.WORK_FLOW_RULE_NAME, getRules(workflowFiles), unDeployDestructivePath)
        }
        componentDeploy.startMessage = ""
        componentDeploy.successMessage = Constants.SUCCESS_MESSAGE_DELETE
        combinePackage(unDeployDestructivePath)
        executeDeploy(folderUnDeploy)
    }

    /**
     * Adds new standard objects from user property
     */
    def addNewStandardObjects() {
        if (Util.isValidProperty(project, Constants.FORCE_EXTENSION) &&
                project[Constants.FORCE_EXTENSION].standardObjects) {
            standardComponents += project[Constants.FORCE_EXTENSION].standardObjects
        }
    }

    /**
     * Gets rules from list of workflow
     * @param workflowList contains workflow files
     * @return an array strings which are rules
     */
    def getRules(ArrayList<File> workflowList) {
        def rules = []
        workflowList.each { workflow ->
            rules.addAll(getWorkflowRules(workflow))
        }
        return rules
    }

    /**
     * Gets all rules from a workflow file
     * @param workflowFile analyzes a workflow at once
     * @return an array of rules in one workflow
     */
    def getWorkflowRules(File workflowFile) {
        def Workflow = new XmlParser().parseText(workflowFile.text)
        def workflowName = Util.getFileName(workflowFile.toPath().getFileName().toString())
        def workflowRules = []
        Workflow.rules.each { rule ->
            workflowRules.add("${workflowName}.${rule.fullName.text()}")
        }
        return workflowRules
    }

    /**
     * Gets queries from package.xml
     * @return jsonQueries
     */
    def getJsonQueries() {
        toolingAPI = new ToolingAPI(credential)
        queryBuilder = new QueryBuilder()
        ArrayList<String> jsonQueries = []
        def queries = queryBuilder.createQueryFromPackage(projectPackagePath)
        queries.each { query ->
            jsonQueries.push(toolingAPI.httpAPIClient.executeQuery(query as String))
        }
        return jsonQueries
    }

    /**
     * Gets all components included to truncate
     * @param components the components names
     * @return components
     */
    def getComponentsWithWildcard(List components) {
        def includesComponents = []
        components.each { component ->
            includesComponents.add("**/${component}")
        }
        return includesComponents
    }

    /**
     * Gets fields from list of objects
     * @param objectFiles contains objects to be analyzed
     * @return a list of fields
     */
    def getFields(ArrayList<File> objectFiles) {
        def customFields = []
        objectFiles.each { objFile ->
            customFields.addAll(getCustomFields(objFile))
        }
        return customFields
    }

    /**
     * Gets all custom fields from a object file
     * @return a list of fields from an specific object
     */
    def getCustomFields(File objectFile) {
        if (!objectFile.exists()) {
            throw new GradleException("File no found at:${objectFile.absolutePath}")
        }
        def CustomObject = new XmlParser().parseText(objectFile.text)
        def customFields = []
        def objectName = Util.getFileName(objectFile.getName())
        if (!CustomObject) {
            throw new GradleException("Object content is not valid")
        }
        CustomObject.namedFilters.each { namedFilter ->
            customFields.add(namedFilter.field.text())
        }
        CustomObject.fields.each { field ->
            def type = field.type.text()
            String objReference = "${field.referenceTo.text()}.${MetadataComponents.OBJECTS.getExtension()}"
            if (type == Constants.LOOKUP_NAME && PackageComponent.existObject(objectFile.parent, objReference)) {
                customFields.add("${objectName}.${field.fullName.text()}")
            }
        }
        return customFields
    }
}
