package org.fundacionjala.gradle.plugins.enforce.filemonitor

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class ObjectParserTest extends Specification {

    @Shared
    ObjectParser objectParser

    @Shared
    String srcProjectPath =  Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala", "gradle",
            "plugins","enforce", "filemonitor", "resources").toString()

    def setup() {
        objectParser = new ObjectParser()
    }

    def "Test should be an instance of ObjectParser" () {
        expect:
        objectParser instanceof ObjectParser
    }

    def "Testshould return an ObjectTracker that contain a map with field name and its hash" () {
        given:
            String objectPath = Paths.get(srcProjectPath, 'objects', 'Object1__c.object')
        when:
            Map<String, String> result = objectParser.parseByObjectXML(new File (objectPath))
        then:
            result.containsKey('fields/Field1__c')
            result.get('fields/Field1__c') != null
    }

    def "Test should return ObjectTracker that contain a map with fields name and their hash" () {
        given:
            String objectPath = Paths.get(srcProjectPath, 'objects', 'Evernote__Contact_Note__c.object')
        when:
            Map<String, String> result = objectParser.parseByObjectXML(new File (objectPath))
        then:
            result.containsKey('fieldSets/Enforce_Fieldset')
            result.containsKey('fields/Enforce_Number_Field__c')
            result.containsKey('fields/Enforce_User__c')
            result.get('fieldSets/Enforce_Fieldset') != null
            result.get('fields/Enforce_Number_Field__c') != null
            result.get('fields/Enforce_User__c') != null
    }
}
