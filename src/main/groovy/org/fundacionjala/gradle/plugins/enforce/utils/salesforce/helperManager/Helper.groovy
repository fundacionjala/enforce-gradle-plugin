package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.helperManager

import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.helperManager.DescriptionParameter
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.helperManager.DescriptionTask

import java.nio.file.Paths


class Helper {
    public static Map<String,DescriptionParameter> parameters
    public static Map<String,ArrayList<String>> tasks
    public static String FORLDER_RESOURCES = '/helper/'
    public static String FILE_TASK = 'TaskData.dat'
    public static String FILE_PARAMETERS = 'ParameterData.dat'

    public static void chargeTasks () {
        tasks = [:]

        def jsonTask =  this.getResource("${FORLDER_RESOURCES}${FILE_TASK}").text
        LazyMap mapTaskData = new JsonSlurper().parseText(jsonTask)
        mapTaskData.each { key, value ->
            DescriptionTask descriptionTask = new DescriptionTask(key)
            descriptionTask.description = value["description"]
            descriptionTask.parameters  = value["parameters"]
            tasks.put(key,descriptionTask)
        }
    }

    public static void chargeParameters() {
        parameters = [:]
        def jsonTask =  this.getResource("${FORLDER_RESOURCES}${FILE_PARAMETERS}").text
        LazyMap mapTaskData = new JsonSlurper().parseText(jsonTask)
        mapTaskData.each { key, value ->
            DescriptionParameter descriptionParameter = new DescriptionParameter(key)
            descriptionParameter.description = value["description"]
            descriptionParameter.examples  = value["examples"]
            parameters.put(key,descriptionParameter)
        }

    }

    public static void showHelp(String taskName) {
        chargeTasks()
        chargeParameters()

        if(tasks[taskName]) {
            DescriptionTask task = tasks[taskName]

            println "Task : " + task.name + ""
            println "Description : " + task.description + ""
            println "Parameters :"

            task.parameters.each {String parameter ->
                if(parameters[parameter]) {
                    parameters[parameter].show(taskName)
                }
                else {
                    println "  -P" + parameter + " : This parameter dont have a description and examples."
                }

            }
        }
        else {
            println taskName + " task don't have a help manual. "
        }

    }
}
