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
    public final String DOCUMENTATION = "documentation"
    public static String WILDCARD_TASK = "*task*"
    public static String SPACE = "    "
    public static int maxLineSize = 80
    public static String startLine = "****************************************************************************************************************************************************************"
    public static String spaceLine = "                                                                                                                                                                "

    public void chargeTasks () {
        tasks = [:]

        def jsonTask =  this.getClass().getResource("${FOLDER_RESOURCES}${FILE_TASK}").text
        LazyMap mapTaskData = new JsonSlurper().parseText(jsonTask)
        mapTaskData.each { key, value ->
            DescriptionTask descriptionTask = new DescriptionTask(key)
            descriptionTask.description = value[DESCRIPTION]
            descriptionTask.documentation = value[DOCUMENTATION]
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
            println(startLine.substring(0,maxLineSize))
            println("")
            printCenterln("${task.name} task")
            println("")
            println(startLine.substring(0,maxLineSize))
            println("")
            println("Description   :")
            println(SPACE,task.description)
            println("")
            println("Documentation : ")
            println(SPACE,task.documentation)
            println("")
            println("Parameters :")

            task.parameters.each {String parameterName ->
                def parameter = helper.parameters[parameterName]
                if(parameter) {
                    println(SPACE,"-P${parameter.name} : ${parameter.description}")
                    parameter.examples.each {String example->
                        example = example.replace(WILDCARD_TASK,parameterName)
                        println("${SPACE}${SPACE}","> ${example}")
                    }
                }
                else {
                    println(SPACE,"-P${parameter} : This parameter dont have a description and examples.")
                }
            }
        }
        else {
            println(startLine.substring(0,maxLineSize))
            printCenterln("${taskName} task don't have a help manual. ")
            println(startLine.substring(0,maxLineSize))
        }
        println("")
        println(startLine.substring(0,maxLineSize))
    }

    def  static println(String text) {
        println("",text)
    }
    def  static printCenterln(String text) {
        int halfspace = (maxLineSize-text.size())/2
        def spaces = spaceLine.substring(0,halfspace)
        println(spaces,text)
    }
    def  static println(String space,String text) {
        def lineSize = maxLineSize - space.size()
        while(text.size()>lineSize) {
            log.println("${space}${text.substring(0,lineSize)} ")
            text = text.substring(lineSize)
        }
        log.println("${space}${text}")
    }
}
