package org.fundacionjala.gradle.plugins.enforce.filemonitor

import spock.lang.Shared
import spock.lang.Specification

class ComponentMonitorTest extends Specification{

    @Shared
    ComponentMonitor componentMonitor

    @Shared
    Map<String, ComponentTracker> oldFiles = [:]

    @Shared
    Map<String, ComponentTracker> currentFiles  = [:]

    @Shared
    ComponentTracker classTracker

    @Shared
    ObjectTracker objectTracker

    @Shared
    String fileNameClass = 'src/classes/Class1.cls'

    def setup() {
        componentMonitor = new ComponentMonitor()
        ComponentTracker oldClassTracker = new ComponentTracker()
        oldClassTracker.hash = 'qweasdzxc'
        ComponentTracker currentClassTracker = new ComponentTracker()
        currentClassTracker.hash = 'rtyfghvbn'
        oldFiles.put(fileNameClass, oldClassTracker)
        currentFiles.put(fileNameClass, currentClassTracker)
    }

    def "Test should return a map with files changed" () {
        when:
            Map result = componentMonitor.getFilesChanged(oldFiles, currentFiles)
        then:
            result.containsKey(fileNameClass)
            result.get(fileNameClass).state == 'Changed'
    }

    def "Test should return a map with files were added" () {
        given:
            String newClassPath = 'src/classes/NewClass.cls'
            currentFiles.put(newClassPath, new ComponentTracker('akshdkjashdkj'))
        when:
            Map result = componentMonitor.getFilesChanged(oldFiles, currentFiles)
        then:
            result.containsKey(newClassPath)
            result.get(newClassPath).state == 'Added'
    }

    def "Test should return a map with files were deleted" () {
        given:
            String deletedClassPath = 'src/classes/DeletedClass.cls'
            oldFiles.put(deletedClassPath, new ComponentTracker('akshdkjashdkj'))
        when:
            Map result = componentMonitor.getFilesChanged(oldFiles, currentFiles)
        then:
            result.containsKey(deletedClassPath)
            result.get(deletedClassPath).state == 'Deleted'
    }

    def "Test should return a map with updated, added and deleted files" () {
        given:
            String updateClassPath = 'src/classes/UpdatedClass.cls'
            String newClassPath = 'src/classes/NewClass.cls'
            String deletedClassPath = 'src/classes/DeletedClass.cls'
            oldFiles.put(updateClassPath, new ComponentTracker('oldClassHash'))
            currentFiles.put(updateClassPath, new ComponentTracker('updatedClassHash'))
            currentFiles.put(newClassPath, new ComponentTracker('newClassHash'))
            oldFiles.put(deletedClassPath, new ComponentTracker('deletedClassHash'))
        when:
            Map result = componentMonitor.getFilesChanged(oldFiles, currentFiles)
        then:
            result.containsKey(updateClassPath)
            result.get(updateClassPath).state == 'Changed'

            result.containsKey(deletedClassPath)
            result.get(deletedClassPath).state == 'Deleted'

            result.containsKey(newClassPath)
            result.get(newClassPath).state == 'Added'
    }

    def "Test shouldn't save the files that weren't updated, added or deleted" () {
        given:
            String classPath = 'src/classes/NotChangedClass.cls'
            ComponentTracker componentTracker = new ComponentTracker('sameHash')
            oldFiles.put(classPath, componentTracker)
            currentFiles.put(classPath, componentTracker)
        when:
            Map result = componentMonitor.getFilesChanged(oldFiles, currentFiles)
        then:
            !result.containsKey(classPath)
    }

    def "Test should return a map that only contains the updated, deleted and added ObjectTrackers" () {
        given:
            String objectPath = 'src/objects/Object1__c.object'
            ObjectTracker objectTracker = new ObjectTracker('filedHash')
            oldFiles.put(objectPath, objectTracker)
            currentFiles.put(objectPath, objectTracker)
        when:
            Map result = componentMonitor.getFilesChanged(oldFiles, currentFiles)
        then:
            !result.containsKey(objectPath)
    }


}
