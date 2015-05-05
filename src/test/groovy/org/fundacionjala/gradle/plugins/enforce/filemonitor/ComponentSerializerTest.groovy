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

    def cleanupSpec() {
        new File(fileName).delete()
    }
}
