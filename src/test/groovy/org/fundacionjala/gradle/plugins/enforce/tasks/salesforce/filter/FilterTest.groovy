package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.filter

import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class FilterTest extends Specification{
    @Shared
        Filter filter
    @Shared
        String projectPath = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
                             "fundacionjala", "gradle", "plugins","enforce","tasks", "salesforce", "resources").toString()
    @Shared
        Project project

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        filter = new Filter(project, projectPath)
    }

    def "Test should be instance of Filter class"() {
        expect:
            filter instanceof Filter
    }

    def "Test should return a map with parameter as key and its content as value"() {
        given:
            ArrayList<String> parametersName = ['files']
            Map<String, String> properties = [:]
            properties.put('files', 'classes')
        when:
            Map<String, String> result = filter.getContentParameter(parametersName, properties)
        then:
            result.containsKey('files')
            result.get('files') == 'classes'
    }

    def "Test should return a map with pall parameters and their values"() {
        given:
            ArrayList<String> parametersName = ['files', 'excludes']
            Map<String, String> properties = [:]
            properties.put('files', 'classes,objects')
            properties.put('excludes', "*${File.separator}class1.cls,objects${File.separator}Object1__c.object")
        when:
            Map<String, String> result = filter.getContentParameter(parametersName, properties)
        then:
            result.containsKey('files')
            result.get('files') == 'classes,objects'
            result.containsKey('excludes')
            result.get('excludes') == "*${File.separator}class1.cls,objects${File.separator}Object1__c.object"
    }

    def "Test should return all classes from project path"() {
        given:
            ArrayList<String> parametersName = ['files']
            Map<String, String> properties = [:]
            properties.put('files', 'classes')
        when:
            ArrayList<File> result = filter.getFiles(parametersName, properties)
        then:
            result.sort() == [new File(Paths.get(projectPath, 'classes', 'class1.cls').toString()),
                              new File(Paths.get(projectPath, 'classes', 'class1.cls-meta.xml').toString())].sort()
    }

    def "Test should return all components less classes" () {
        given:
            Filter myFilter = new Filter(project, Paths.get(projectPath, 'src').toString())
            ArrayList<String> parametersName = []
            Map<String, String> properties = [:]
            properties.put('excludes', 'classes')
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == [new File(Paths.get(projectPath, 'src', 'classes', 'Class1.cls').toString()),
                              new File(Paths.get(projectPath, 'src', 'classes', 'Class1.cls-meta.xml').toString()),
                              new File(Paths.get(projectPath, 'src', 'objects', 'Object1__c.object').toString()),
                              new File(Paths.get(projectPath, 'src', 'package.xml').toString()),
                              new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger').toString()),
                              new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString())].sort()
    }
}
