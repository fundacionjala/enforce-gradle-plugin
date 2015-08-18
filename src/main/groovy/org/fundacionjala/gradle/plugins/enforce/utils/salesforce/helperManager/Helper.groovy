package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.helperManager

import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import groovy.util.logging.Log
import org.fundacionjala.gradle.plugins.enforce.utils.Constants

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
    public static int TWO = 2
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
            printLine(startLine.substring(Constants.ZERO,maxLineSize))
            printLine("")
            printCenterLine("${task.name} task")
            printLine("")
            printLine(startLine.substring(Constants.ZERO,maxLineSize))
            printLine("")
            printLine("Description   :")
            printLine(SPACE,task.description)
            printLine("")
            printLine("Documentation : ")
            printLine(SPACE,task.documentation)
            printLine("")
            printLine("Parameters :")

            task.parameters.each {String parameterName ->
                def parameter = helper.parameters[parameterName]
                if(parameter) {
                    printLine(SPACE,"-P${parameter.name} : ${parameter.description}")
                    parameter.examples.each {String example->
                        example = example.replace(WILDCARD_TASK,task.name)
                        printLine("${SPACE}${SPACE}","> ${example}")
                    }
                }
                else {
                    printLine(SPACE,"-P${parameter} : This parameter dont have a description and examples.")
                }
            }
        }
        else {
            printLine(startLine.substring(Constants.ZERO,maxLineSize))
            printCenterLine("${taskName} task don't have a help manual. ")
            printLine(startLine.substring(Constants.ZERO,maxLineSize))
        }
        printLine("")
        printLine(startLine.substring(Constants.ZERO,maxLineSize))
    }

    def static printLine(String text) {
        printLine("",text)
    }
    def static printCenterLine(String text) {
        int halfspace = (maxLineSize-text.size())/TWO
        def spaces = spaceLine.substring(Constants.ZERO,halfspace)
        printLine(spaces,text)
    }
    def static printLine(String space,String text) {
        def lineSize = maxLineSize - space.size()
        while(text.size()>lineSize) {
            log.println("${space}${text.substring(Constants.ZERO,lineSize)} ")
            text = text.substring(lineSize)
        }
        log.println("${space}${text}")
    }
}
