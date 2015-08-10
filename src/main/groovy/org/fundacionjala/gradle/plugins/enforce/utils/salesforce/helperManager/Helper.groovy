package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.helperManager

import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import groovy.util.logging.Log

@Log
@Singleton
class Helper {
    public  Map<String,DescriptionParameter> parameters
    public  Map<String,ArrayList<String>> tasks
    public final String FOLDER_RESOURCES = '/helper/'
    public final String FILE_TASK = 'TaskData.dat'
    public final String FILE_PARAMETERS = 'ParameterData.dat'
    public final String DESCRIPTION = "description"
    public final String PARAMETERS = "parameters"
    public final String EXAMPLES = "examples"

    public void chargeTasks () {
        tasks = [:]

        def jsonTask =  this.getClass().getResource("${FOLDER_RESOURCES}${FILE_TASK}").text
        LazyMap mapTaskData = new JsonSlurper().parseText(jsonTask)
        mapTaskData.each { key, value ->
            DescriptionTask descriptionTask = new DescriptionTask(key)
            descriptionTask.description = value[DESCRIPTION]
            descriptionTask.parameters  = value[PARAMETERS]
            tasks.put(key,descriptionTask)
        }
    }

    public void chargeParameters() {
        parameters = [:]
        def jsonTask =  this.getClass().getResource("${FOLDER_RESOURCES}${FILE_PARAMETERS}").text
        LazyMap mapTaskData = new JsonSlurper().parseText(jsonTask)
        mapTaskData.each { key, value ->
            DescriptionParameter descriptionParameter = new DescriptionParameter(key)
            descriptionParameter.description = value[DESCRIPTION]
            descriptionParameter.examples  = value[EXAMPLES]
            parameters.put(key,descriptionParameter)
        }

    }

    public static void showHelp(String taskName) {
        Helper helper = Helper.instance
        helper.chargeTasks()
        helper.chargeParameters()

        if(helper.tasks[taskName]) {
            DescriptionTask task = helper.tasks[taskName]
            log.println("")
            log.println("Task : ${task.name}")
            log.println("Description : ${task.description}")
            if(task.parameters.size() > 0) {
                log.println("Parameters :")
            }
            task.parameters.each {String parameter ->
                if(helper.parameters[parameter]) {
                    helper.parameters[parameter].show(taskName)
                }
                else {
                    log.println("  -P${parameter} : This parameter dont have a description and examples.")
                }
            }
        }
        else {
            log.println("${taskName} task don't have a help manual. ")
        }

    }
}
