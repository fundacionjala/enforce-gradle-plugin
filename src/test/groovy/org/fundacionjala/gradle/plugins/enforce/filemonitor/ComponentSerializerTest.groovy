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

    def setup() {
        componentSerializer = new ComponentSerializer(srcProjectPath)
    }

    def "Test Should be Initialize ComponentManager object"() {
        expect:
        componentSerializer instanceof ComponentSerializer
    }

    def "Test should save a Map<String, ComponentTracker> object in a .fileTracker.data file" () {
        given:
            Map<String, ComponentTracker> components = [:]
            components.put('src/classes/Class1.cls', new ComponentTracker('classHash'))
            components.put('src/classes/Object1__c.object', new ObjectTracker('objectHash'))
        when:
            componentSerializer.save(components)
        then:
            new File(componentSerializer.fileName).exists()
            new File(componentSerializer.fileName).size() > 0
    }

    def "Test should read  a .fileTracker.data file and returns a Map<String, ComponentTracker> object" () {
        given:
            Map<String, ComponentTracker> components = [:]
            components.put('src/classes/Class1.cls', new ComponentTracker('classHash'))
            components.put('src/classes/Object1__c.object', new ObjectTracker('objectHash'))
        when:
            componentSerializer.save(components)
            Map<String, ComponentTracker> result = componentSerializer.read(componentSerializer.fileName)
        then:
            result.size() == components.size()
            result.each {String relativePath, ComponentTracker componentTracker ->
                components.get(relativePath).hash == componentTracker.hash
            }
    }

    def cleanupSpec() {
        new File(componentSerializer.fileName).delete()
    }
}
