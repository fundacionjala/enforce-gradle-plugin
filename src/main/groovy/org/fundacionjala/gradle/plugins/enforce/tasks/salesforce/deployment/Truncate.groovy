package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util

class Truncate extends Deployment {
    private final List<String> COMPONENTS_TO_TRUNCATE = ['classes', 'objects', 'triggers', 'pages', 'components', 'workflows', 'tabs']
    private static final String START_TRUNCATE_PROCCESS_MESSAGE = "Starting truncate process"
    private static final String SUCCESS_TRUNCATE_MESSAGE = "The files were successfully truncated"
    private static final String TRUNCATE_DESCRIPTION = 'This task truncates classes, objects, triggers, pages, components, workflows and tabs from your code'
    private static final String TRUNCATE_FOLDER_NAME = 'truncate'

    List<String> componentsToTruncate
    String files = COMPONENTS_TO_TRUNCATE.join(', ')
    public ArrayList<File> filesToTruncate

    /**
     * Sets description and group task
     * @param descriptionTask is description tasks
     * @param groupTask is the group typeName the task
     */
    Truncate() {
        super(TRUNCATE_DESCRIPTION, Constants.DEPLOYMENT)
        componentsToTruncate = COMPONENTS_TO_TRUNCATE
        taskFolderName = TRUNCATE_FOLDER_NAME
        files = ''
    }

    /**
     * Executes the truncate process
     */
    @Override
    void runTask() {
        createDeploymentDirectory(taskFolderPath)
        loadClassifiedFiles(files, excludes)
        loadFilesToTruncate()
        copyFilesToTaskDirectory(filesToTruncate)
        writePackage(taskPackagePath, filesToTruncate)
        combinePackageToUpdate(taskPackagePath)
        truncateComponents()
        executeDeploy(taskFolderPath, START_TRUNCATE_PROCCESS_MESSAGE, SUCCESS_TRUNCATE_MESSAGE)
    }

    /**
     * Loads files that will be truncated
     */
    void loadFilesToTruncate() {
        filesToTruncate = classifiedFile.validFiles
    }

    /**
     * Truncates classes, objects, triggers, pages, components, workflows and tabs
     */
    public void truncateComponents() {
        interceptorsToExecute = Interceptor.INTERCEPTORS.values().toList()
        interceptorsToExecute += interceptors
        logger.debug("Truncating components at: $taskFolderPath")
        truncateComponents(taskFolderPath)
    }

    /**
     * Loads truncate task parameters
     */
    void loadParameters() {
        if (Util.isValidProperty(parameters, Constants.PARAMETER_FILES)) {
            files = parameters[Constants.PARAMETER_FILES].toString()
        }
        if (Util.isValidProperty(parameters, Constants.PARAMETER_EXCLUDES)) {
            excludes = parameters[Constants.PARAMETER_EXCLUDES].toString()
        }
    }
}