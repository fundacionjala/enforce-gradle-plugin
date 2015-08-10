package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.helperManager

import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import spock.lang.Specification

class HelperTest extends Specification {

    def "The test should verify if TaskData.dat have a content with a json format"() {
        given:
            String FOLDER_RESOURCES = '/helper/'
            String FILE_TASK = 'TaskData.dat'

        when:
            def jsonTask = this.getClass().getResource("${FOLDER_RESOURCES}${FILE_TASK}").text
            LazyMap mapTaskData = new JsonSlurper().parseText(jsonTask)
        then:
            mapTaskData != null
    }

    def "The test should verify if ParameterData.dat have a content with a json format"() {
        given:
            String FOLDER_RESOURCES = '/helper/'
            String FILE_PARAMETERS = 'ParameterData.dat'

        when:
            def jsonTask = this.getClass().getResource("${FOLDER_RESOURCES}${FILE_PARAMETERS}").text
            LazyMap mapTaskData = new JsonSlurper().parseText(jsonTask)
        then:
            mapTaskData != null
    }
}
