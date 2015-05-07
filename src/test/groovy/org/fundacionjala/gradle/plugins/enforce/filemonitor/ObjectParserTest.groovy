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

    def "Test should return an ObjectTracker that contain a map with field name and its hash" () {
        given:
            String objectPath = Paths.get(srcProjectPath, 'objects', 'Object1__c.object')
            String field1__cHash = 'b8535e7f90c928ae8f6c7606367fbaf1'
        when:
            Map<String, String> result = objectParser.parseByObjectXML(new File (objectPath))
        then:
            result.containsKey('fields/Field1__c')
            result.get('fields/Field1__c') == field1__cHash
    }

    def "Test should return ObjectTracker that contain a map with fields name and their hash" () {
        given:
            String objectPath = Paths.get(srcProjectPath, 'objects', 'Evernote__Contact_Note__c.object')
            String enforce_FieldsetHash = '1f2148ab348e55398083d8e8775250a7'
            String enforce_Number_Field__c = '9f150323f784c692936aedd92cd69914'
            String enforce_User__c = 'c3bcafaeddf610a4c6950760399877fa'
        when:
            Map<String, String> result = objectParser.parseByObjectXML(new File (objectPath))
        then:
            result.containsKey('fieldSets/Enforce_Fieldset')
            result.containsKey('fields/Enforce_Number_Field__c')
            result.containsKey('fields/Enforce_User__c')

            result.get('fieldSets/Enforce_Fieldset') == enforce_FieldsetHash
            result.get('fields/Enforce_Number_Field__c') == enforce_Number_Field__c
            result.get('fields/Enforce_User__c') == enforce_User__c
    }
}
