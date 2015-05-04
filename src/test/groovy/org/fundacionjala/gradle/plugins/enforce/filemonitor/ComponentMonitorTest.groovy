package org.fundacionjala.gradle.plugins.enforce.filemonitor

import com.twmacinta.util.MD5
import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChild
import groovy.xml.XmlUtil
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class ComponentMonitorTest extends Specification{

    @Shared
    ComponentMonitor componentMonitor

    @Shared
    Map<String, ComponentHash> oldFiles = [:]

    @Shared
    Map<String, ComponentHash> currentFiles  = [:]

    @Shared
    String srcProjectPath =  Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala", "gradle",
            "plugins","enforce", "filemonitor", "resources").toString()

    @Shared
    String fileNameClass = 'src/classes/Class1.cls'

    @Shared
    Map<String, String> subComponents

    def setup() {
        subComponents = [:]
        componentMonitor = new ComponentMonitor('resources')
        ComponentHash oldClassTracker = new ComponentHash()
        oldClassTracker.hash = 'qweasdzxc'
        ComponentHash currentClassTracker = new ComponentHash()
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
            currentFiles.put(newClassPath, new ComponentHash(newClassPath, 'akshdkjashdkj'))
        when:
            Map result = componentMonitor.getFilesChanged(oldFiles, currentFiles)
        then:
            result.containsKey(newClassPath)
            result.get(newClassPath).state == ComponentStates.ADDED
    }

    def "Test should return a map with files were deleted" () {
        given:
            String deletedClassPath = 'src/classes/DeletedClass.cls'
            oldFiles.put(deletedClassPath, new ComponentHash(deletedClassPath, 'akshdkjashdkj'))
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
            oldFiles.put(updateClassPath, new ComponentHash(updateClassPath, 'oldClassHash'))
            currentFiles.put(updateClassPath, new ComponentHash(updateClassPath, 'updatedClassHash'))
            currentFiles.put(newClassPath, new ComponentHash(newClassPath, 'newClassHash'))
            oldFiles.put(deletedClassPath, new ComponentHash(deletedClassPath, 'deletedClassHash'))
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
            ComponentHash componentTracker = new ComponentHash(classPath, 'sameHash')
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
            ObjectHash objectTracker = new ObjectHash(objectPath, 'filedHash', subComponents)
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
            Map<String, ComponentHash> result = componentMonitor.getComponentsSignature(files)
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
            Map<String, ComponentHash> result = componentMonitor.getComponentsSignature(files)
        then:
            result.containsKey('resources/objects/Object1__c.object')
            result.containsKey('resources/objects/Object2__c.object')
            result.get('resources/objects/Object1__c.object') instanceof ObjectHash
            result.get('resources/objects/Object2__c.object') instanceof ObjectHash
    }

    def "Test should return a map with fields and their hash value" () {
        given:
            def object1 = Paths.get(srcProjectPath, 'objects', 'Object1__c.object').toString()
            def object2 = Paths.get(srcProjectPath, 'objects', 'Object2__c.object').toString()
            ArrayList<File> files = [new File(object1), new File(object2)]
            componentMonitor.srcProject = 'resources'
        when:
            Map<String, ComponentHash> result = componentMonitor.getComponentsSignature(files)
        then:
            result.containsKey('resources/objects/Object1__c.object')
            result.containsKey('resources/objects/Object2__c.object')
            result.get('resources/objects/Object1__c.object').subComponents.containsKey('fields/Field1__c')
            result.get('resources/objects/Object2__c.object').subComponents.containsKey('fields/Field1__c')
    }

    def "Test should return a map without changes" () {
        given:
            def object1 = Paths.get(srcProjectPath, 'objects', 'Object1__c.object').toString()
            def object2 = Paths.get(srcProjectPath, 'objects', 'Object2__c.object').toString()
            ArrayList<File> files = [new File(object1), new File(object2)]
            Map<String, ComponentHash> componentsHash = componentMonitor.getComponentsSignature(files)
            ComponentSerializer componentSerializer = new ComponentSerializer('resources')
            componentSerializer.save(componentsHash)
        when:
            Map<String, ResultTracker> result = componentMonitor.getComponentChanged(files)
        then:
            result.isEmpty()
    }

    def "Test should return a map with files that have had deleted" () {
        given:
            def object1 = Paths.get(srcProjectPath, 'objects', 'Object1__c.object').toString()
            def object2 = Paths.get(srcProjectPath, 'objects', 'Object2__c.object').toString()
            ArrayList<File> files = [new File(object1), new File(object2)]
            Map<String, ComponentHash> componentsHash = componentMonitor.getComponentsSignature(files)
            ComponentSerializer componentSerializer = new ComponentSerializer('resources')
            componentSerializer.save(componentsHash)
            files.remove(0)
        when:
            Map<String, ResultTracker> result = componentMonitor.getComponentChanged(files)
        then:
            result.containsKey('resources/objects/Object1__c.object')
            result.get('resources/objects/Object1__c.object').state == ComponentStates.DELETED
    }

    def "Test should return a map with files that have had added" () {
        given:
            def object1 = Paths.get(srcProjectPath, 'objects', 'Object1__c.object').toString()
            def object2 = Paths.get(srcProjectPath, 'objects', 'Object2__c.object').toString()
            def everNoteObject = Paths.get(srcProjectPath, 'objects', 'Evernote__Contact_Note__c.object').toString()
            ArrayList<File> files = [new File(object1), new File(object2)]
            Map<String, ComponentHash> componentsHash = componentMonitor.getComponentsSignature(files)
            ComponentSerializer componentSerializer = new ComponentSerializer('resources')
            componentSerializer.save(componentsHash)
            files.add(new File(everNoteObject))
        when:
            Map<String, ResultTracker> result = componentMonitor.getComponentChanged(files)
        then:
            result.containsKey('resources/objects/Evernote__Contact_Note__c.object')
            result.get('resources/objects/Evernote__Contact_Note__c.object').state == ComponentStates.ADDED
    }

    def "Test should return a map with files that have had changed" () {
        given:
            def everNoteChanged = Paths.get(srcProjectPath, 'objects', 'EverNoteChanged__c.object').toString()
            def object2 = Paths.get(srcProjectPath, 'objects', 'Object2__c.object').toString()
            def everNoteChangedContent = new File(Paths.get(srcProjectPath, 'objects', 'Evernote__Contact_Note__c.object').toString()).text
            new File(everNoteChanged).write(everNoteChangedContent)
            ArrayList<File> files = [new File(everNoteChanged), new File(object2)]
            Map<String, ComponentHash> componentsHash = componentMonitor.getComponentsSignature(files)
            ComponentSerializer componentSerializer = new ComponentSerializer('resources')
            componentSerializer.save(componentsHash)
            XmlSlurper xmlSlurper = new XmlSlurper()
            GPathResult  objectParsed = xmlSlurper.parseText(new File(everNoteChanged).text)
            objectParsed.enableEnhancedLookup = true
            XmlUtil xmlUtil = new XmlUtil()
            String xmlString = xmlUtil.serialize(objectParsed)
            new File(everNoteChanged).write(xmlString)
        when:
            Map<String, ResultTracker> result = componentMonitor.getComponentChanged(files)
        then:
            result.containsKey('resources/objects/EverNoteChanged__c.object')
            result.get('resources/objects/EverNoteChanged__c.object').state == ComponentStates.CHANGED
    }

    def "Test should return a map with fields that have had deleted" () {
        given:
            def object1 = Paths.get(srcProjectPath, 'objects', 'Object1__c.object').toString()
            def object1FieldDeleted = Paths.get(srcProjectPath, 'objects', 'Object1FieldDeleted__c.object').toString()
            def object1Content = new File(object1).text
            new File(object1FieldDeleted).write(object1Content)
            ArrayList<File> files = [new File(object1), new File(object1FieldDeleted)]
            Map<String, ComponentHash> componentsHash = componentMonitor.getComponentsSignature(files)
            ComponentSerializer componentSerializer = new ComponentSerializer('resources')
            componentSerializer.save(componentsHash)
            def xmlSlurper = new XmlSlurper()
            def objectParsed = xmlSlurper.parse(new File(object1FieldDeleted))
            def list = objectParsed.fields.list()
            list.remove(0)
            objectParsed.fields = list
            XmlUtil xmlUtil = new XmlUtil()
            String xmlString = xmlUtil.serialize(objectParsed)
            new File(object1FieldDeleted).write(xmlString)
        when:
            Map<String, ResultTracker> result = componentMonitor.getComponentChanged(files)
        then:
            result.containsKey('resources/objects/Object1FieldDeleted__c.object')
            result.get('resources/objects/Object1FieldDeleted__c.object').state == ComponentStates.CHANGED
            result.get('resources/objects/Object1FieldDeleted__c.object').subComponentsResult.containsKey('fields/Field1__c')
            result.get('resources/objects/Object1FieldDeleted__c.object').subComponentsResult.get('fields/Field1__c') == ComponentStates.DELETED
    }
    def "Test should return a map with fields that have had added" () {
        given:
            def object1 = Paths.get(srcProjectPath, 'objects', 'Object1__c.object').toString()
            def object1FieldAdded = Paths.get(srcProjectPath, 'objects', 'Object1FieldAdded__c.object').toString()
            def object1Content = new File(object1).text
            new File(object1FieldAdded).write(object1Content)
            ArrayList<File> files = [new File(object1), new File(object1FieldAdded)]
            Map<String, ComponentHash> componentsHash = componentMonitor.getComponentsSignature(files)
            ComponentSerializer componentSerializer = new ComponentSerializer('resources')
            componentSerializer.save(componentsHash)
            def xmlSlurper = new XmlSlurper()
            def objectParsed = xmlSlurper.parse(new File(object1FieldAdded))
            def fieldSetValue = "${'<fieldSets><fullName>Enforce_Fieldset</fullName>'}${'<description>Enforce_Fieldset</description></fieldSets>'}"
            NodeChild newNode = new XmlSlurper().parseText(fieldSetValue)
            objectParsed.appendNode(newNode)
            XmlUtil xmlUtil = new XmlUtil()
            String xmlString = xmlUtil.serialize(objectParsed)
            new File(object1FieldAdded).write(xmlString)
        when:
            Map<String, ResultTracker> result = componentMonitor.getComponentChanged(files)
        then:
            result.containsKey('resources/objects/Object1FieldAdded__c.object')
            result.get('resources/objects/Object1FieldAdded__c.object').state == ComponentStates.CHANGED
            result.get('resources/objects/Object1FieldAdded__c.object').subComponentsResult.containsKey('fieldSets/Enforce_Fieldset')
            result.get('resources/objects/Object1FieldAdded__c.object').subComponentsResult.get('fieldSets/Enforce_Fieldset') == ComponentStates.ADDED
    }

    def "Test should return a map with fields that have had changed" () {
        given:
            def object1 = Paths.get(srcProjectPath, 'objects', 'Object1__c.object').toString()
            def objectFieldChanged = Paths.get(srcProjectPath, 'objects', 'Object1FieldChanged__c.object').toString()
            def object1Content = new File(object1).text
            new File(objectFieldChanged).write(object1Content)
            ArrayList<File> files = [new File(object1), new File(objectFieldChanged)]
            Map<String, ComponentHash> componentsHash = componentMonitor.getComponentsSignature(files)
            ComponentSerializer componentSerializer = new ComponentSerializer('resources')
            componentSerializer.save(componentsHash)
            def xmlSlurper = new XmlSlurper()
            def objectParsed = xmlSlurper.parse(new File(objectFieldChanged))
            objectParsed.fields[0].length = 5
            XmlUtil xmlUtil = new XmlUtil()
            String xmlString = xmlUtil.serialize(objectParsed)
            new File(objectFieldChanged).write(xmlString)
        when:
            Map<String, ResultTracker> result = componentMonitor.getComponentChanged(files)
        then:
            result.containsKey('resources/objects/Object1FieldChanged__c.object')
            result.get('resources/objects/Object1FieldChanged__c.object').state == ComponentStates.CHANGED
            result.get('resources/objects/Object1FieldChanged__c.object').subComponentsResult.containsKey('fields/Field1__c')
            result.get('resources/objects/Object1FieldChanged__c.object').subComponentsResult.get('fields/Field1__c') == ComponentStates.CHANGED
    }

    def cleanupSpec() {
        new File(Paths.get(srcProjectPath, 'objects', 'EverNoteChanged__c.object').toString()).delete()
        new File(Paths.get(srcProjectPath, 'objects', 'Object1FieldDeleted__c.object').toString()).delete()
        new File(Paths.get(srcProjectPath, 'objects', 'Object1FieldAdded__c.object').toString()).delete()
        new File(Paths.get(srcProjectPath, 'objects', 'Object1FieldChanged__c.object').toString()).delete()
    }
}
