package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util

class Truncate extends Deployment {
    Map<String, String> parameters
    private final List<String> COMPONENTS_TO_TRUNCATE = ['classes', 'objects', 'triggers', 'pages', 'components', 'workflows', 'tabs']
    List<String> componentsToTruncate
    String files = COMPONENTS_TO_TRUNCATE.join(', ')
    public ArrayList<File> filesToTruncate

    /**
     * Sets description and group task
     * @param descriptionTask is description tasks
     * @param groupTask is the group typeName the task
     */
    Truncate() {
        super(Constants.TRUNCATE_DESCRIPTION, Constants.DEPLOYMENT)
        componentsToTruncate = COMPONENTS_TO_TRUNCATE
        taskFolderName = Constants.TRUNCATE_FOLDER_NAME
        files = ''
    }

    /**
     * Executes the truncate process
     */
    @Override
    void runTask() {
        createDeploymentDirectory(taskFolderPath)
        loadFilesToTruncate()
        copyFilesToTaskDirectory(filesToTruncate)
        writePackage(taskPackagePath, filesToTruncate)
        combinePackageToUpdate(taskPackagePath)
        truncateComponents()
        componentDeploy.startMessage = "Starting truncate process"
        componentDeploy.successMessage = "The files were successfully truncated"
        executeDeploy(taskFolderPath)
    }

    /**
     * Loads files that will be truncated
     */
    void loadFilesToTruncate() {
        filesToTruncate = getClassifiedFiles(files, excludes).get(Constants.VALID_FILE)
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
     * Gets all task parameters
     * @param properties the task properties
     * @return A map of all task parameters
     */
    void loadParameters() {
        if (Util.isValidProperty(project, Constants.PARAMETER_FILES)) {
            files = project.properties[Constants.PARAMETER_FILES].toString()
        }
        if (Util.isValidProperty(project, Constants.PARAMETER_EXCLUDES)) {
            excludes = project.properties[Constants.PARAMETER_EXCLUDES].toString()
        }
    }
}