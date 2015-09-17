/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks

import org.fundacionjala.gradle.plugins.enforce.exceptions.CredentialException
import org.fundacionjala.gradle.plugins.enforce.exceptions.deploy.DeployException
import org.fundacionjala.gradle.plugins.enforce.tasks.exception.GradleDeployException
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.helperManager.Helper
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.logging.StandardOutputListener
import org.gradle.api.tasks.TaskAction
import org.gradle.logging.internal.LoggingOutputInternal

import java.nio.file.Paths

/**
 * Base class for default task
 */
abstract class ForceTask extends DefaultTask {
    private final String XML_EXTENSION = 'xml'
    private final String AFFIRMATIVE = 'yes'
    private final String DATE_FORMAT = 'yyyy-MM-dd_HH-mm-ss'
    private final String PATH_BUILD_LOG = '/build/logs'
    private final String FILE_NAME = 'buildLog.log'
    private final String CLASS_PATH_ERROR = 'org.gradle.color.error'
    private final String COLOR_ERROR = 'RED'
    private final String PARAMETER_HELP = 'help'

    public String projectPath
    public ManagementFile fileManager
    public ArrayList<String> excludeFilesToMonitor = []

    /**
     * Sets description and group task
     * @param description is description tasks
     * @param group is the group typeName the task
     */
    ForceTask(String description, String group) {
        this.description = description
        this.group = group
        excludeFilesToMonitor.push(XML_EXTENSION)
    }

    /**
     * Execute a closure
     */
    @TaskAction
    void start() {
        if(!showHelper()) {
            startLogger()
            withExceptionHandling {
                executeTask()
            }
        }
    }

    /**
     * Prints a help manual about a task on the terminal
     * @return true if the task use de parameter help
     */
    public boolean showHelper() {
        boolean parameterExist = false
        if(project.properties.containsKey(PARAMETER_HELP)) {
            Helper.showHelp(this.getName())
            parameterExist = true
        }
        return parameterExist
    }

    /**
     * Starts the logger to write in file
     */
    private void startLogger() {
        def date = new Date().format(DATE_FORMAT)
        def buildLogDir = "${project.rootDir}${PATH_BUILD_LOG}"
        project.mkdir("${buildLogDir}")
        def buildLog = new File("${buildLogDir}/${date}_${FILE_NAME}")
        System.setProperty(CLASS_PATH_ERROR, COLOR_ERROR)

        project.gradle.services.get(LoggingOutputInternal).addStandardOutputListener(new StandardOutputListener() {
            void onOutput(CharSequence output) {
                buildLog << output
            }
        })

        project.gradle.services.get(LoggingOutputInternal).addStandardErrorListener(new StandardOutputListener() {
            void onOutput(CharSequence output) {
                buildLog << output
            }
        })
    }

    /**
     * Execute Closure with exception handler
     * @param runTask is the closure to the execution
     */
    private void withExceptionHandling(Closure runTask) {
        try {
            projectPath = getSfdcPathProject(project.enforce.srcPath as String)
            fileManager = new ManagementFile(projectPath)
            runTask()
        }
        catch (CredentialException credentialException) {
            logger.error("Credential exception")
            throw new GradleException(credentialException.message, credentialException)
        }
        catch (DeployException deployException) {
            logger.error("Failed execute deployment task ")
            String message
            if (this.isIntegrationMode()) {
                message = deployException.getMessageJsonFormat()
            } else {
                message = deployException.message
            }
            throw new GradleDeployException(message, deployException)
        }
        catch (Exception exception) {
            logger.error("Failed execute task")
            throw new GradleException(exception.message, exception)
        }
    }

    public Boolean isIntegrationMode() {
        return  (project.enforce.integration == AFFIRMATIVE)
    }

    /**
     * Gets an absolute path project
     * @return an absolute path
     */
    public String getSfdcPathProject(String pathProject) {
        if (pathProject.isEmpty()) {
            throw new Exception('Path sent cannot be empty')
        }
        if (!new File(pathProject).isAbsolute()) {
            if (!pathProject.contains(Constants.POINT)) {
                pathProject = Paths.get(project.projectDir.absolutePath, pathProject).toString()
            } else {
                pathProject = Paths.get(project.projectDir.absolutePath).toString()
            }

        }
        return pathProject
    }

    /**
     * Abstract method: Method execute as a closure also can extend method
     */
    abstract void executeTask()
}
