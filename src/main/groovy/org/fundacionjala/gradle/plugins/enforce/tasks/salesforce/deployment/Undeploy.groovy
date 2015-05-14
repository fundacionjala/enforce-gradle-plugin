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
    private final String START_MESSAGE_TRUNCATE = 'Starting undeploy...'
    private final String SUCCESS_MESSAGE_TRUNCATE = 'All components truncated were successfully uploaded'
    private final String SUCCESS_MESSAGE_DELETE = 'The files were successfully deleted'
    private final String FILE_NAME_DESTRUCTIVE = "destructiveChanges.xml"
    private final String CUSTOM_FIELD_NAME = 'CustomField'
    private final String WORK_FLOW_RULE_NAME = 'WorkflowRule'
    private final String DIR_UN_DEPLOY = "undeploy"
    private List includesComponents
    private FileTree files
    private ArrayList<String> workflowNames
    private ArrayList<File> workflowFiles
    private QueryBuilder queryBuilder
    public final String LOOKUP_NAME = 'Lookup'
    public ToolingAPI toolingAPI
    public PackageComponent packageComponent
    public ArrayList<File> filesToTruncate
    public String folderUnDeploy
    public String unDeployPackagePath
    public SmartFilesValidator smartFilesValidator
    InterceptorManager componentManager
    List<String> standardComponents

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    Undeploy() {
        super('This task removes all components in your organization according to local repository', Constants.DEPLOYMENT)
        filesToTruncate = new ArrayList<File>()
        componentManager = new InterceptorManager()
        componentManager.buildInterceptors()
        interceptorsToExecute = org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.INTERCEPTORS.values().toList()
        standardComponents = MetadataComponent.COMPONENTS.values().toList()
        workflowNames = []
    }

    @Override
    void runTask() {
        initializeQueries(getJsonQueries())
        setupFilesToUnDeploy()
        deployTruncatedComponents()
        deployToDeleteComponents()
    }

    /**
     * Initializes queries to filter files to deploy
     */
    def initializeQueries(ArrayList<String> jsonQueries) {
        smartFilesValidator = new SmartFilesValidator(jsonQueries)
    }

    /**
     * Setups files to UnDeploy
     * Creates undeploy directory
     * Copies package from source code directory to undeploy directory
     * Validates salesForce's components
     * Truncates files
     */
    def setupFilesToUnDeploy() {
        folderUnDeploy = Paths.get(buildFolderPath, DIR_UN_DEPLOY).toString()
        unDeployPackagePath = Paths.get(folderUnDeploy, PACKAGE_NAME).toString()
        createDeploymentDirectory(folderUnDeploy)
        Files.copy(Paths.get(projectPath, PACKAGE_NAME), Paths.get(folderUnDeploy, PACKAGE_NAME), StandardCopyOption.REPLACE_EXISTING)
        packageComponent = new PackageComponent(unDeployPackagePath)
        filesToTruncate = fileManager.getFilesByFolders(projectPath, packageComponent.truncatedDirectories).sort()
        filesToTruncate = smartFilesValidator.filterFilesAccordingOrganization(filesToTruncate)
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
        componentDeploy.startMessage = START_MESSAGE_TRUNCATE
        componentDeploy.successMessage = SUCCESS_MESSAGE_TRUNCATE
        executeDeploy(folderUnDeploy)
    }

    /**
     * Deploys to delete all components from package.xml
     */
    def deployToDeleteComponents() {
        addNewStandardObjects()
        createDeploymentDirectory(folderUnDeploy)
        writePackage(unDeployPackagePath, [])
        includesComponents = packageComponent.components
        def excludeComponents = getComponentsWithWildcard(standardComponents)
        workflowNames = packageComponent.components.grep(~/.*.workflow$/) as ArrayList<String>
        excludeComponents.addAll(workflowNames)
        files = project.fileTree(dir: projectPath, includes: includesComponents, excludes: excludeComponents)
        ArrayList<File> filesFiltered = smartFilesValidator.filterFilesAccordingOrganization(files.getFiles().sort() as ArrayList<File>)
        filesFiltered = excludeFiles(filesFiltered)
        preparePackage(Paths.get(folderUnDeploy, FILE_NAME_DESTRUCTIVE).toString(), filesFiltered)
        includesComponents = getComponentsWithWildcard(standardComponents).grep(~/.*.object$/)
        files = project.fileTree(dir: projectPath, includes: includesComponents)
        ArrayList<File> objectFiles = files.getFiles().sort()
        objectFiles = excludeFiles(objectFiles)
        savePackage()
        updatePackage(CUSTOM_FIELD_NAME, getFields(objectFiles), Paths.get(folderUnDeploy, FILE_NAME_DESTRUCTIVE).toString())
        if (!workflowNames.empty) {
            workflowFiles = project.fileTree(dir: projectPath, includes: workflowNames).toList()
            workflowFiles = excludeFiles(workflowFiles)
            updatePackage(WORK_FLOW_RULE_NAME, getRules(workflowFiles), Paths.get(folderUnDeploy, FILE_NAME_DESTRUCTIVE).toString())
        }
        componentDeploy.startMessage = ""
        componentDeploy.successMessage = SUCCESS_MESSAGE_DELETE
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
        def queries = queryBuilder.createQueryFromPackage(Paths.get(projectPath, PACKAGE_NAME).toString())
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
            if (type == LOOKUP_NAME && PackageComponent.existObject(objectFile.parent, objReference)) {
                customFields.add("${objectName}.${field.fullName.text()}")
            }
        }
        return customFields
    }
}
