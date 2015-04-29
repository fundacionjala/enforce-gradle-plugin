package org.fundacionjala.gradle.plugins.enforce.filemonitor

import spock.lang.Specification

class ComponentSerializerTest extends Specification {

    ComponentSerializer componentManager

    def setup() {
        componentManager = new ComponentSerializer()
    }

    def "Test Should be Initialize ComponentManager object"() {
        expect:
        componentManager instanceof ComponentSerializer
    }

    def "Test should fill components map " () {
        given:
            def files = [new File('classes/Class1.cls'), new File('objects/Object1__c.object')]
        when:
            componentManager.loadComponents(files)
        then:
            componentManager.components.size == 2
    }
}
