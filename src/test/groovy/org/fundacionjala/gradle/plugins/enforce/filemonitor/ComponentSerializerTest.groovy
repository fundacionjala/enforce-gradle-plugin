package org.fundacionjala.gradle.plugins.enforce.filemonitor

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class ComponentSerializerTest extends Specification {

    @Shared
    ComponentSerializer componentSerializer

    @Shared
    String srcProjectPath =  Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala", "gradle",
            "plugins","enforce", "filemonitor", "resources").toString()
    @Shared
    Map<String, String> subComponents

    @Shared
    String fileName

    def setup() {
        fileName = Paths.get(srcProjectPath, '.fileTracker.data')
        subComponents = [:]
        componentSerializer = new ComponentSerializer(fileName)
    }

    def "Test Should be Initialize ComponentManager object"() {
        expect:
        componentSerializer instanceof ComponentSerializer
    }

    def "Test should save a Map<String, ComponentTracker> object in a .fileTracker.data file" () {
        given:
            Map<String, ComponentHash> components = [:]
            components.put('src/classes/Class1.cls', new ComponentHash('src/classes/Class1.cls', 'classHash'))
            components.put('src/classes/Object1__c.object', new ObjectHash('src/classes/Object1__c.object', 'objectHash', subComponents))
        when:
            componentSerializer.save(components)
        then:
            new File(fileName).exists()
            new File(fileName).size() > 0
    }

    def "Test should read  a .fileTracker.data file and returns a Map<String, ComponentTracker> object" () {
        given:
            Map<String, ComponentHash> components = [:]
            components.put('src/classes/Class1.cls', new ComponentHash('src/classes/Class1.cls', 'classHash'))
            components.put('src/classes/Object1__c.object', new ObjectHash('src/classes/Object1__c.object', 'objectHash', subComponents))
        when:
            componentSerializer.save(components)
            Map<String, ComponentHash> result = componentSerializer.read()
        then:
            result.size() == components.size()
            result.each {String relativePath, ComponentHash componentTracker ->
                components.get(relativePath).hash == componentTracker.hash
            }
    }

    def "Test should read  a .fileTracker.data oldFormat file and returns a Map<String, ComponentTracker> object" () {
        given:
            Map<String, String> components = [:]
            String fileNameTest = Paths.get(srcProjectPath, '.fileTrackerOldFormat.data')
            componentSerializer = new ComponentSerializer(fileNameTest)
            components.put('src/classes/Class1.cls', 'Class1Hash')
            components.put('src/classes/Object1__c.object', 'Object1Hash')
            ObjectOutputStream oos
            oos = new ObjectOutputStream(new FileOutputStream(fileNameTest))
            oos.writeObject(components)
            oos.close()
        when:
            Map<String, ComponentHash> result = componentSerializer.read()
        then:
            result.containsKey('src/classes/Class1.cls')
            result.containsKey('src/classes/Object1__c.object')
            result.get('src/classes/Class1.cls').hash == 'Class1Hash'
            result.get('src/classes/Object1__c.object').hash == 'Object1Hash'
    }

    def "Test should read  a .fileTracker.data new format file and returns a Map<String, ComponentTracker> object" () {
        given:
            Map<String, ComponentHash> components = [:]
            String fileNameTest = Paths.get(srcProjectPath, '.fileTrackerOldFormat.data')
            componentSerializer = new ComponentSerializer(fileNameTest)
            components.put('src/classes/Class1.cls', new ComponentHash('src/classes/Class1.cls', 'classHash'))
            components.put('src/classes/Object1__c.object', new ObjectHash('src/classes/Object1__c.object', 'objectHash', subComponents))
        when:
            componentSerializer.save(components)
            Map<String, ComponentHash> result = componentSerializer.read()
        then:
            result.containsKey('src/classes/Class1.cls')
            result.containsKey('src/classes/Object1__c.object')
            result.get('src/classes/Class1.cls').hash == 'classHash'
            result.get('src/classes/Object1__c.object').hash == 'objectHash'
    }

    def cleanupSpec() {
        new File(fileName).delete()
        new File(Paths.get(srcProjectPath, '.fileTrackerOldFormat.data').toString()).delete()
    }
}
