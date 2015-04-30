package org.fundacionjala.gradle.plugins.enforce.filemonitor

import com.twmacinta.util.MD5
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

    def "Test should return an ObjectTracker that contain a map with filed name and its hash" () {
        given:
            String objectPath = Paths.get(srcProjectPath, 'objects', 'Object1__c.object')
            String customField = "${'<fields><fullName>Field1__c</fullName><externalId>false</externalId>'}${'<label>Field1</label><length>10</length><required>false</required>'}${'<type>Text</type><unique>false</unique></fields>'}"
            MD5 md5 = new MD5()
            md5.Update(customField)
            def fieldHash = md5.asHex()
        when:
            Map<String, String> result = objectParser.parseByObjectXML(new File (objectPath))
        then:
            result.containsKey('fields/field1__c')
            result.get('fields/field1__c') == fieldHash
    }
}
