package org.fundacionjala.gradle.plugins.enforce.filemonitor

import com.twmacinta.util.MD5
import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChild
import groovy.xml.StreamingMarkupBuilder
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
        componentMonitor = new ComponentMonitor(srcProjectPath)
        ComponentHash oldClassTracker = new ComponentHash()
        oldClassTracker.hash = 'qweasdzxc'
        ComponentHash currentClassTracker = new ComponentHash()
        currentClassTracker.hash = 'rtyfghvbn'
        oldFiles.put(fileNameClass, oldClassTracker)
        currentFiles.put(fileNameClass, currentClassTracker)
        def fileTrackerPath = Paths.get(srcProjectPath, '.fileTracker.data').toString()
        ComponentSerializer componentSerializer = new ComponentSerializer(fileTrackerPath)
        componentMonitor.componentSerializer = componentSerializer
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
            componentMonitor.srcProject = srcProjectPath
        when:
            Map<String, ComponentHash> result = componentMonitor.getComponentsSignature(files)
        then:
            result.containsKey(Paths.get('classes','Class1.cls').toString())
            result.containsKey(Paths.get('classes','Class2.cls').toString())
            result.get(Paths.get('classes','Class1.cls').toString()).hash == class1Hash
            result.get(Paths.get('classes','Class2.cls').toString()).hash == class2Hash
    }

    def "Test should have the values as instance of ObjectTracker" () {
        given:
            def object1 = Paths.get(srcProjectPath, 'objects', 'Object1__c.object').toString()
            def object2 = Paths.get(srcProjectPath, 'objects', 'Object2__c.object').toString()
            ArrayList<File> files = [new File(object1), new File(object2)]
            componentMonitor.srcProject = srcProjectPath
        when:
            Map<String, ComponentHash> result = componentMonitor.getComponentsSignature(files)
        then:
            result.containsKey(Paths.get('objects', 'Object1__c.object').toString())
            result.containsKey(Paths.get('objects', 'Object2__c.object').toString())
            result.get(Paths.get('objects', 'Object1__c.object').toString()) instanceof ObjectHash
            result.get(Paths.get('objects', 'Object2__c.object').toString()) instanceof ObjectHash
    }

    def "Test should return a map with fields and their hash value" () {
        given:
            def object1 = Paths.get(srcProjectPath, 'objects', 'Object1__c.object').toString()
            def object2 = Paths.get(srcProjectPath, 'objects', 'Object2__c.object').toString()
            ArrayList<File> files = [new File(object1), new File(object2)]
            componentMonitor.srcProject = srcProjectPath
        when:
            Map<String, ComponentHash> result = componentMonitor.getComponentsSignature(files)
        then:
            result.containsKey(Paths.get('objects', 'Object1__c.object').toString())
            result.containsKey(Paths.get('objects', 'Object2__c.object').toString())
            result.get(Paths.get('objects', 'Object1__c.object').toString()).subComponents.containsKey('fields/Field1__c')
            result.get(Paths.get('objects', 'Object2__c.object').toString()).subComponents.containsKey('fields/Field1__c')
    }

    def "Test should return a map without changes" () {
        given:
            def object1 = Paths.get(srcProjectPath, 'objects', 'Object1__c.object').toString()
            def object2 = Paths.get(srcProjectPath, 'objects', 'Object2__c.object').toString()
            ArrayList<File> files = [new File(object1), new File(object2)]
            Map<String, ComponentHash> componentsHash = componentMonitor.getComponentsSignature(files)
            componentMonitor.componentSerializer.save(componentsHash)
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
            componentMonitor.srcProject = srcProjectPath
            Map<String, ComponentHash> componentsHash = componentMonitor.getComponentsSignature(files)
            ComponentSerializer componentSerializer = new ComponentSerializer(componentMonitor.fileName)
            componentSerializer.save(componentsHash)
            ArrayList<File> currentFiles = [new File(object2)];
        when:
            Map<String, ResultTracker> result = componentMonitor.getComponentChanged(currentFiles)
        then:
            result.containsKey(Paths.get('objects', 'Object1__c.object').toString())
            result.get(Paths.get('objects', 'Object1__c.object').toString()).state == ComponentStates.DELETED
    }

    def "Test should return a map with files that have had added" () {
        given:
            def object1 = Paths.get(srcProjectPath, 'objects', 'Object1__c.object').toString()
            def object2 = Paths.get(srcProjectPath, 'objects', 'Object2__c.object').toString()
            def everNoteObject = Paths.get(srcProjectPath, 'objects', 'Evernote__Contact_Note__c.object').toString()
            ArrayList<File> files = [new File(object1), new File(object2)]
            Map<String, ComponentHash> componentsHash = componentMonitor.getComponentsSignature(files)
            ComponentSerializer componentSerializer = new ComponentSerializer(componentMonitor.fileName)
            componentSerializer.save(componentsHash)
            files.add(new File(everNoteObject))
        when:
            Map<String, ResultTracker> result = componentMonitor.getComponentChanged(files)
        then:
            String everNoteObjectPath = Paths.get('objects', 'Evernote__Contact_Note__c.object')
            result.containsKey(everNoteObjectPath)
            result.get(everNoteObjectPath).state == ComponentStates.ADDED
    }

    def "Test should return a map with files that have had changed" () {
        given:
            def everNoteChanged = Paths.get(srcProjectPath, 'objects', 'EverNoteChanged__c.object').toString()
            def object2 = Paths.get(srcProjectPath, 'objects', 'Object2__c.object').toString()
            def everNoteChangedContent = new File(Paths.get(srcProjectPath, 'objects', 'Evernote__Contact_Note__c.object').toString()).text
            new File(everNoteChanged).write(everNoteChangedContent)
            ArrayList<File> files = [new File(everNoteChanged), new File(object2)]

            Map<String, ComponentHash> componentsHash = componentMonitor.getComponentsSignature(files)
            componentMonitor.componentSerializer.save(componentsHash)
            XmlSlurper xmlSlurper = new XmlSlurper()
            GPathResult  objectParsed = xmlSlurper.parseText(new File(everNoteChanged).text)
            objectParsed.enableEnhancedLookup = true
            XmlUtil xmlUtil = new XmlUtil()
            String xmlString = xmlUtil.serialize(objectParsed)
            new File(everNoteChanged).write(xmlString)
        when:
            Map<String, ResultTracker> result = componentMonitor.getComponentChanged(files)
        then:
            String everNoteChangedPath = Paths.get('objects', 'EverNoteChanged__c.object')
            result.containsKey(everNoteChangedPath)
            result.get(everNoteChangedPath).state == ComponentStates.CHANGED
    }

    def "Test should return a map with fields that have had deleted" () {
        given:
            def object1 = Paths.get(srcProjectPath, 'objects', 'Object1__c.object').toString()
            def object1FieldDeleted = Paths.get(srcProjectPath, 'objects', 'Object1FieldDeleted__c.object').toString()
            def object1Content = new File(object1).text
            File object1FieldDeletedFile = new File(object1FieldDeleted)
            object1FieldDeletedFile.write(object1Content)
            ArrayList<File> files = [new File(object1), new File(object1FieldDeleted)]
            Map<String, ComponentHash> componentsHash = componentMonitor.getComponentsSignature(files)
            componentMonitor.componentSerializer.save(componentsHash)
            def xmlSlurper = new XmlSlurper()
            def objectParsed = xmlSlurper.parse(object1FieldDeletedFile)
            def list = objectParsed.fields.list()
            list.remove(0)
            objectParsed.fields = list
            object1FieldDeletedFile.withWriter { outWriter ->
                XmlUtil.serialize( new StreamingMarkupBuilder().bind{ mkp.yield objectParsed }, outWriter )
            }
        when:
            Map<String, ResultTracker> result = componentMonitor.getComponentChanged(files)
        then:
            String objectFieldDeleted = Paths.get('objects', 'Object1FieldDeleted__c.object')
            result.containsKey(objectFieldDeleted)
            result.get(objectFieldDeleted).state == ComponentStates.CHANGED
            result.get(objectFieldDeleted).subComponentsResult.containsKey('fields/Field1__c')
            result.get(objectFieldDeleted).subComponentsResult.get('fields/Field1__c') == ComponentStates.DELETED
    }

    def "Test should return a map with fields that have had added" () {
        given:
            def object1 = Paths.get(srcProjectPath, 'objects', 'Object1__c.object').toString()
            def object1FieldAdded = Paths.get(srcProjectPath, 'objects', 'Object1FieldAdded__c.object').toString()
            def object1Content = new File(object1).text
            new File(object1FieldAdded).write(object1Content)
            ArrayList<File> files = [new File(object1), new File(object1FieldAdded)]
            Map<String, ComponentHash> componentsHash = componentMonitor.getComponentsSignature(files)
            componentMonitor.componentSerializer.save(componentsHash)
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
            String object1FieldAddedPath = Paths.get('objects', 'Object1FieldAdded__c.object')
            result.containsKey(object1FieldAddedPath)
            result.get(object1FieldAddedPath).state == ComponentStates.CHANGED
            result.get(object1FieldAddedPath).subComponentsResult.containsKey('fieldSets/Enforce_Fieldset')
            result.get(object1FieldAddedPath).subComponentsResult.get('fieldSets/Enforce_Fieldset') == ComponentStates.ADDED
    }

    def "Test should return a map with fields that have had changed" () {
        given:
            def object1 = Paths.get(srcProjectPath, 'objects', 'Object1__c.object').toString()
            def objectFieldChanged = Paths.get(srcProjectPath, 'objects', 'Object1FieldChanged__c.object').toString()
            def object1Content = new File(object1).text
            new File(objectFieldChanged).write(object1Content)
            ArrayList<File> files = [new File(object1), new File(objectFieldChanged)]
            Map<String, ComponentHash> componentsHash = componentMonitor.getComponentsSignature(files)
            componentMonitor.componentSerializer.save(componentsHash)
            def xmlSlurper = new XmlSlurper()
            def objectParsed = xmlSlurper.parse(new File(objectFieldChanged))
            objectParsed.fields[0].length = 5
            XmlUtil xmlUtil = new XmlUtil()
            String xmlString = xmlUtil.serialize(objectParsed)
            new File(objectFieldChanged).write(xmlString)
        when:
            Map<String, ResultTracker> result = componentMonitor.getComponentChanged(files)
        then:
            String object1FieldChangedPath = Paths.get('objects', 'Object1FieldChanged__c.object')
            result.containsKey(object1FieldChangedPath)
            result.get(object1FieldChangedPath).state == ComponentStates.CHANGED
            result.get(object1FieldChangedPath).subComponentsResult.containsKey('fields/Field1__c')
            result.get(object1FieldChangedPath).subComponentsResult.get('fields/Field1__c') == ComponentStates.CHANGED
    }

    def "Test select files changed according folders"() {
        given:
            def arrayFolders = ["classes"]
            def mapFilesChanged = ["classes/class1.cls": "file Changed", "triggers/trigger1.trigger": "file Changed"]
        when:
            def mapResult = componentMonitor.getFoldersFiltered(arrayFolders, mapFilesChanged)
        then:
            mapResult == ["classes/class1.cls": "file Changed"]
    }

    def "Test verify exist map"() {
        when:
            String fileTrackerFile = Paths.get(srcProjectPath, '.fileTrackerTest.data')
            new File(fileTrackerFile).write('test')
            componentMonitor.fileName = fileTrackerFile
        then:
            componentMonitor.verifyFileMap()
            new File(fileTrackerFile).delete()
    }

    def "Test verify not exist map "() {
        when:
            String fileTrackerFile = Paths.get(srcProjectPath, '.fileTrackerTest.data')
            componentMonitor.fileName = fileTrackerFile
        then:
            !componentMonitor.verifyFileMap()
    }

    def "Test set a project path"() {
        given:
            def sourceProject = "src"
            def fileName = '.fileTracker.data'
        when:
            componentMonitor.setSrcProject(sourceProject)
        then:
            componentMonitor.srcProject == sourceProject
            componentMonitor.fileName == Paths.get(sourceProject, fileName).toString()
    }

    def "Test should update the map with subComponents"() {
        given:
            String class1Path = Paths.get('src', 'classes', 'Class1.cls')
            String class2Path = Paths.get('src', 'classes', 'Class2.cls')
            String class3Path = Paths.get('src', 'classes', 'Class3.cls')
            String newClassPath = Paths.get('src', 'classes', 'NewClass.cls')
            Map<String, ComponentHash> recoveryFileHashCode = [:]
                recoveryFileHashCode.put(class1Path, new ComponentHash(class1Path, 'hashClass1'))
                recoveryFileHashCode.put(class2Path, new ComponentHash(class1Path, 'hashClass2'))
                recoveryFileHashCode.put(class3Path, new ComponentHash(class1Path, 'hashClass3'))

            Map<String, ComponentHash> currentFileHashCode = [:]
                currentFileHashCode.put(class1Path, new ComponentHash(newClassPath,'hashClassChanged'))
                currentFileHashCode.put(newClassPath, new ComponentHash(newClassPath,'hashNewClass'))
                currentFileHashCode.put(class3Path, new ComponentHash(newClassPath,'hashClass3'))


            Map<String, ResultTracker> mapFilesChanged = [:]
                mapFilesChanged.put(class1Path, new ResultTracker(ComponentStates.CHANGED))
                mapFilesChanged.put(class2Path, new ResultTracker(ComponentStates.DELETED))
                mapFilesChanged.put(newClassPath, new ResultTracker(ComponentStates.ADDED))

            componentMonitor.recoveryFileHashCode = recoveryFileHashCode
            componentMonitor.currentFileHashCode = currentFileHashCode

        when:
            componentMonitor.saveMapUpdated(mapFilesChanged)
        then:
            !componentMonitor.recoveryFileHashCode.containsKey(class2Path)
            componentMonitor.recoveryFileHashCode.containsKey(class1Path)
            componentMonitor.recoveryFileHashCode.containsKey(class3Path)
            componentMonitor.recoveryFileHashCode.containsKey(newClassPath)
            componentMonitor.recoveryFileHashCode.get(class1Path).hash == 'hashClassChanged'
            componentMonitor.recoveryFileHashCode.get(class3Path).hash == 'hashClass3'
            componentMonitor.recoveryFileHashCode.get(newClassPath).hash == 'hashNewClass'
    }

    def "Test should update the map to save it "() {
        given:
            String object1Path = Paths.get('src', 'objects', 'object1__c.object')
            Map<String, ComponentHash> recoveryFileHashCode = [:]
            Map<String, String> subComponentsRecovery = [:]
            subComponentsRecovery.put('fields/Field1__c', 'field1Hash')
            subComponentsRecovery.put('fields/Field2__c', 'field2Hash')
            subComponentsRecovery.put('fields/Field3__c', 'field3Hash')
            recoveryFileHashCode.put(object1Path, new ObjectHash(object1Path, 'ObjectHash', subComponentsRecovery))

            Map<String, ComponentHash> currentFileHashCode = [:]
            Map<String, String> subComponentsCurrent = [:]
            subComponentsCurrent.put('fields/Field1__c', 'field1HashChanged')
            subComponentsCurrent.put('fields/Field3__c', 'field3Hash')
            subComponentsCurrent.put('fields/Field4__c', 'field4Hash')
            currentFileHashCode.put(object1Path, new ObjectHash(object1Path, 'objectHashChanged', subComponentsCurrent))


            Map<String, ResultTracker> mapFilesChanged = [:]
            Map<String, ComponentStates> objectResultSubComponents = [:]
            objectResultSubComponents.put('fields/Field1__c', ComponentStates.CHANGED)
            objectResultSubComponents.put('fields/Field2__c', ComponentStates.DELETED)
            objectResultSubComponents.put('fields/Field4__c', ComponentStates.ADDED)
            ObjectResultTracker objectResultTracker = new ObjectResultTracker(ComponentStates.CHANGED)
            objectResultTracker.subComponentsResult = objectResultSubComponents
            mapFilesChanged.put(object1Path, objectResultTracker)

            componentMonitor.recoveryFileHashCode = recoveryFileHashCode
            componentMonitor.currentFileHashCode = currentFileHashCode

        when:
            componentMonitor.saveMapUpdated(mapFilesChanged)
        then:
            componentMonitor.recoveryFileHashCode.containsKey(object1Path)
            componentMonitor.recoveryFileHashCode.get(object1Path).hash == 'objectHashChanged'
            componentMonitor.recoveryFileHashCode.get(object1Path).subComponents.containsKey('fields/Field1__c')
            !componentMonitor.recoveryFileHashCode.get(object1Path).subComponents.containsKey('fields/Field2__c')
            componentMonitor.recoveryFileHashCode.get(object1Path).subComponents.containsKey('fields/Field3__c')
            componentMonitor.recoveryFileHashCode.get(object1Path).subComponents.containsKey('fields/Field4__c')
            componentMonitor.recoveryFileHashCode.get(object1Path).subComponents.get('fields/Field1__c') == 'field1HashChanged'
            componentMonitor.recoveryFileHashCode.get(object1Path).subComponents.get('fields/Field3__c') == 'field3Hash'
            componentMonitor.recoveryFileHashCode.get(object1Path).subComponents.get('fields/Field4__c') == 'field4Hash'


    }

    def cleanupSpec() {
        new File(Paths.get(srcProjectPath, 'objects', 'EverNoteChanged__c.object').toString()).delete()
        new File(Paths.get(srcProjectPath, 'objects', 'Object1FieldDeleted__c.object').toString()).delete()
        new File(Paths.get(srcProjectPath, 'objects', 'Object1FieldAdded__c.object').toString()).delete()
        new File(Paths.get(srcProjectPath, 'objects', 'Object1FieldChanged__c.object').toString()).delete()
        new File(Paths.get('resources').toString()).delete()
    }
}
