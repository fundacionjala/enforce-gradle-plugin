package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.FileValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.filter.Filter

import java.nio.file.Paths

class Truncate extends Deployment {
    Filter filter
    Map<String, String> parameters
    ArrayList<File> filteredFiles
    private
    final List<String> COMPONENTS_TO_TRUNCATE = ['classes', 'objects', 'triggers', 'pages', 'components', 'workflows', 'tabs']
    List<String> componentsToTruncate
    String pathTruncate
    String pathPackage
    String files

    /**
     * Sets description and group task
     * @param descriptionTask is description tasks
     * @param groupTask is the group typeName the task
     */
    Truncate() {
        super(Constants.TRUNCATE_DESCRIPTION, Constants.DEPLOYMENT)
        componentsToTruncate = COMPONENTS_TO_TRUNCATE
        files = ''
        excludes = ''
    }

    /**
     * Executes the truncate process
     */
    @Override
    void runTask() {
        setup()
        loadParameters(project.properties as Map<String, String>)
        copyValidFiles(validFiles)
        writePackage(pathPackage, validFiles)
        truncateComponents()
        executeDeploy(pathTruncate)
    }

    /**
     * Gets all valid files for salesforce platform
     * @return A list of all valid files
     */
    ArrayList<File> getValidFiles() {
        filteredFiles = filter.getFiles(files, excludes)
        Map<String, ArrayList<File>> allFiles = FileValidator.validateFiles(projectPath, filteredFiles)
        ArrayList<File> validFiles = allFiles[Constants.VALID_FILE]
        return validFiles
    }

    /**
     * Copy all valid files from source code to build directory
     */
    void copyValidFiles(ArrayList<File> validFiles) {
        fileManager.copy(projectPath, validFiles, pathTruncate)
    }

    /**
     * Creates new instances for task
     * Sets messages status
     * Sets paths to truncate
     * Creates truncate directory
     */
    void setup() {
        filter = new Filter(project, projectPath)
        componentDeploy.startMessage = "Starting truncate process"
        componentDeploy.successMessage = "The files were successfully truncated"
        pathTruncate = Paths.get(buildFolderPath, Constants.TRUNCATE_FOLDER_NAME)
        createDeploymentDirectory(pathTruncate)
        pathPackage = Paths.get(buildFolderPath, Constants.TRUNCATE_FOLDER_NAME, Constants.PACKAGE_FILE_NAME)
    }

    /**
     * Truncates classes, objects, triggers, pages, components, workflows and tabs
     */
    public void truncateComponents() {
        interceptorsToExecute = Interceptor.INTERCEPTORS.values().toList()
        interceptorsToExecute += interceptors
        logger.debug("Truncating components at: $pathTruncate")
        truncateComponents(pathTruncate)
    }

    /**
     * Gets all task parameters
     * @param properties the task properties
     * @return A map of all task parameters
     */
    void loadParameters(Map<String, String> properties) {
        if (Util.isValidProperty(properties, Constants.PARAMETER_FILES)) {
            files = properties[Constants.PARAMETER_FILES]
        } else {
            files = COMPONENTS_TO_TRUNCATE.join(', ')
        }

        if (Util.isValidProperty(properties, Constants.PARAMETER_EXCLUDES)) {
            excludes = properties[Constants.PARAMETER_EXCLUDES]
        }
    }
}
