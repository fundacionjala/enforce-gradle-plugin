package org.fundacionjala.gradle.plugins.enforce.filemonitor

import spock.lang.Specification

class ComponentManagerTest extends Specification {

    ComponentManager componentManager

    def setup() {
        componentManager = new ComponentManager()
    }

    def "Test Should be Initialize ComponentManager object"() {
        expect:
        componentManager instanceof ComponentManager
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
