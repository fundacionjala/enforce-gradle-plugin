package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.deployment

import org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.FileValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.filter.Filter

import java.nio.file.Paths

class Truncate extends Deployment {
    Filter filter
    ArrayList<String> parameterNames
    ArrayList<File> filteredFiles
    List<String> directoriesToTruncate
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
        parameterNames = [Constants.PARAMETER_FILES, Constants.PARAMETER_EXCLUDES]
        directoriesToTruncate = ['classes', 'objects', 'triggers', 'pages', 'components', 'workflows', 'tabs']
        files = ''
        excludes = ''
    }

    /**
     * Executes the truncate process
     */
    @Override
    void runTask() {
        setup()
        copyValidFiles()
        truncateComponents()
        executeDeploy(pathTruncate)
    }

    /**
     * Gets all valid files for salesforce platform
     * @return A list of all valid files
     */
    ArrayList<File> getValidFiles() {
        filteredFiles = filter.getFiles(parameterNames, getParameters(project.properties))
        Map<String, ArrayList<File>> allFiles = FileValidator.validateFiles(projectPath, filteredFiles)
        ArrayList<File> validFiles = allFiles[Constants.VALID_FILE]
        return validFiles
    }

    /**
     * Copy all valid files from source code to build directory
     */
    void copyValidFiles() {
        ArrayList<File> validFiles = getValidFiles()
        fileManager.copy(projectPath, validFiles, pathTruncate)
        writePackage(pathPackage, validFiles)
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
     * Gets all task parameters
     * @param properties the task properties
     * @return A map of all task parameter
     */
    Map<String, String> getParameters(Map<String, String> properties) {
        Map<String, String> parameters = new HashMap<String, String>()
        if (Util.isValidProperty(properties, Constants.PARAMETER_FILES)) {
            files = properties[Constants.PARAMETER_FILES]
        } else {
            files = directoriesToTruncate.join(', ')
        }

        if (Util.isValidProperty(properties, Constants.PARAMETER_EXCLUDES)) {
            excludes = properties[Constants.PARAMETER_FILES]
        }
        parameters.put(Constants.PARAMETER_FILES, files)
        parameters.put(Constants.PARAMETER_EXCLUDES, excludes)
        return parameters
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
}
