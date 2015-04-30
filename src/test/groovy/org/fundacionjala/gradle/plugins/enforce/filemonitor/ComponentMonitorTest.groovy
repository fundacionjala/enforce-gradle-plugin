package org.fundacionjala.gradle.plugins.enforce.filemonitor

import com.twmacinta.util.MD5
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class ComponentMonitorTest extends Specification{

    @Shared
    ComponentMonitor componentMonitor

    @Shared
    Map<String, ComponentTracker> oldFiles = [:]

    @Shared
    Map<String, ComponentTracker> currentFiles  = [:]

    @Shared
    String srcProjectPath =  Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala", "gradle",
            "plugins","enforce", "filemonitor", "resources").toString()

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
            result.get(fileNameClass).state == ComponentStates.CHANGED
    }

    def "Test should return a map with files were added" () {
        given:
            String newClassPath = 'src/classes/NewClass.cls'
            currentFiles.put(newClassPath, new ComponentTracker(newClassPath, 'akshdkjashdkj'))
        when:
            Map result = componentMonitor.getFilesChanged(oldFiles, currentFiles)
        then:
            result.containsKey(newClassPath)
            result.get(newClassPath).state == ComponentStates.ADDED
    }

    def "Test should return a map with files were deleted" () {
        given:
            String deletedClassPath = 'src/classes/DeletedClass.cls'
            oldFiles.put(deletedClassPath, new ComponentTracker(deletedClassPath, 'akshdkjashdkj'))
        when:
            Map result = componentMonitor.getFilesChanged(oldFiles, currentFiles)
        then:
            result.containsKey(deletedClassPath)
            result.get(deletedClassPath).state == ComponentStates.DELETED
    }

    def "Test should return a map with updated, added and deleted files" () {
        given:
            String updateClassPath = 'src/classes/UpdatedClass.cls'
            String newClassPath = 'src/classes/NewClass.cls'
            String deletedClassPath = 'src/classes/DeletedClass.cls'
            oldFiles.put(updateClassPath, new ComponentTracker(updateClassPath, 'oldClassHash'))
            currentFiles.put(updateClassPath, new ComponentTracker(updateClassPath, 'updatedClassHash'))
            currentFiles.put(newClassPath, new ComponentTracker(newClassPath, 'newClassHash'))
            oldFiles.put(deletedClassPath, new ComponentTracker(deletedClassPath, 'deletedClassHash'))
        when:
            Map result = componentMonitor.getFilesChanged(oldFiles, currentFiles)
        then:
            result.containsKey(updateClassPath)
            result.get(updateClassPath).state == ComponentStates.CHANGED

            result.containsKey(deletedClassPath)
            result.get(deletedClassPath).state == ComponentStates.DELETED

            result.containsKey(newClassPath)
            result.get(newClassPath).state == ComponentStates.ADDED
    }

    def "Test shouldn't save the files that weren't updated, added or deleted" () {
        given:
            String classPath = 'src/classes/NotChangedClass.cls'
            ComponentTracker componentTracker = new ComponentTracker(classPath, 'sameHash')
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
            ObjectTracker objectTracker = new ObjectTracker(objectPath, 'filedHash')
            oldFiles.put(objectPath, objectTracker)
            currentFiles.put(objectPath, objectTracker)
        when:
            Map result = componentMonitor.getFilesChanged(oldFiles, currentFiles)
        then:
            !result.containsKey(objectPath)
    }

    def "Test should return a map with components and their hash value" () {
        given:
            String firstFile = Paths.get(srcProjectPath, 'classes', 'Class1.cls')
            String secondFile = Paths.get(srcProjectPath, 'classes', 'Class2.cls')
            ArrayList<File> files = [new File(firstFile), new File(secondFile)]
            def class1Hash = MD5.asHex(MD5.getHash(files[0]))
            def class2Hash = MD5.asHex(MD5.getHash(files[1]))
            componentMonitor.srcProject = 'resources'
        when:
            Map<String, ComponentTracker> result = componentMonitor.getComponentsSignature(files)
        then:
            result.containsKey('resources/classes/Class1.cls')
            result.containsKey('resources/classes/Class2.cls')
            result.get('resources/classes/Class1.cls').hash == class1Hash
            result.get('resources/classes/Class2.cls').hash == class2Hash
    }

    def "Test should have the values as instance of ObjectTracker" () {
        given:
            def object1 = Paths.get(srcProjectPath, 'objects', 'Object1__c.object').toString()
            def object2 = Paths.get(srcProjectPath, 'objects', 'Object2__c.object').toString()
            ArrayList<File> files = [new File(object1), new File(object2)]
            componentMonitor.srcProject = 'resources'
        when:
            Map<String, ComponentTracker> result = componentMonitor.getComponentsSignature(files)
        then:
            result.containsKey('resources/objects/Object1__c.object')
            result.containsKey('resources/objects/Object2__c.object')
            result.get('resources/objects/Object1__c.object') instanceof ObjectTracker
            result.get('resources/objects/Object2__c.object') instanceof ObjectTracker
    }

    def "Test should return a map with fields and their hash value" () {
        given:
        def object1 = Paths.get(srcProjectPath, 'objects', 'Object1__c.object').toString()
        def object2 = Paths.get(srcProjectPath, 'objects', 'Object2__c.object').toString()
        ArrayList<File> files = [new File(object1), new File(object2)]
        componentMonitor.srcProject = 'resources'
        when:
        Map<String, ComponentTracker> result = componentMonitor.getComponentsSignature(files)
        then:
        result.containsKey('resources/objects/Object1__c.object')
        result.containsKey('resources/objects/Object2__c.object')
        result.get('resources/objects/Object1__c.object').subComponents.containsKey('Field1__c')
        result.get('resources/objects/Object2__c.object').subComponents.containsKey('Field1__c')
    }
}
