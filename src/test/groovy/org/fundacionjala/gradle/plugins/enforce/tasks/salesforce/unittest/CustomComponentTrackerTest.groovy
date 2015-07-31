package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.unittest

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentStates
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ResultTracker
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.runtesttask.CustomComponentTracker
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class CustomComponentTrackerTest extends Specification {
    @Shared
    def SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala",
            "gradle", "plugins","enforce", "tasks", "salesforce", "resources").toString()
    @Shared
        CustomComponentTracker customComponentTracker
    @Shared
        ArrayList<File> components
    @Shared
        String projectPath


    def setup() {
        File classFile = new File(Paths.get(SRC_PATH, 'src', 'classes', 'Class1.cls').toString())
        File objectFile = new File(Paths.get(SRC_PATH, 'src', 'objects', 'Object1__c.object').toString())
        components = [classFile, objectFile]
        projectPath = Paths.get(SRC_PATH, 'src').toString()
        customComponentTracker = new CustomComponentTracker(projectPath)
    }

    def "Test should save customComponentTracker"() {
        given:
            String projectPath = Paths.get(SRC_PATH, 'src')
        when:
            CustomComponentTracker.saveCustomComponent(projectPath)
        then:
            new File(Paths.get(SRC_PATH, 'src', '.customComponentTracker.data').toString()).exists()
    }

    def "Test should get files by extension from customComponentTrackerMap"() {
        given:
            ArrayList<String> extensions = ['cls', 'object']
            Map<String, ResultTracker> fileTrackerMap = ["classes${File.separator}Class1.cls": new ResultTracker(ComponentStates.ADDED),
                                                         "classes${File.separator}Class2.cls": new ResultTracker(ComponentStates.CHANGED),
                                                         "objects${File.separator}Object1__c.object": new ResultTracker(ComponentStates.ADDED),
                                                         "triggers${File.separator}Trigger1.trigger": new ResultTracker(ComponentStates.CHANGED)]
            customComponentTracker.customComponentTrackerMap = fileTrackerMap
        when:
            ArrayList<String> result = customComponentTracker.getFilesNameByExtension(extensions)
        then:
            result.sort() == ['Class1.cls', 'Class2.cls', 'Object1__c.object'].sort()
    }

    def "Test should get files by extension from customComponentTrackerMap just files added and files cahnged"() {
        given:
            ArrayList<String> extensions = ['cls', 'object']
            Map<String, ResultTracker> fileTrackerMap = ["classes${File.separator}Class1.cls": new ResultTracker(ComponentStates.DELETED),
                                                         "classes${File.separator}Class2.cls": new ResultTracker(ComponentStates.CHANGED),
                                                         "objects${File.separator}Object1__c.object": new ResultTracker(ComponentStates.DELETED),
                                                         "triggers${File.separator}Trigger1.trigger": new ResultTracker(ComponentStates.ADDED)]
            customComponentTracker.customComponentTrackerMap = fileTrackerMap
        when:
            ArrayList<String> result = customComponentTracker.getFilesNameByExtension(extensions)
        then:
            result == ['Class2.cls']
    }

    def cleanup() {
        new File(Paths.get(SRC_PATH, 'src', '.customComponentTracker.data').toString()).delete()
    }
}
