package org.fundacionjala.gradle.plugins.enforce.filemonitor

import spock.lang.Shared
import spock.lang.Specification

class ObjectResultTrackerTest extends Specification {

    @Shared
    ObjectResultTracker objectResultTracker

    def setup() {
        objectResultTracker = new ObjectResultTracker(ComponentStates.CHANGED)
    }

    def "Test should be an instance of ObjectResult" () {
        expect:
        objectResultTracker instanceof ObjectResultTracker
    }

    def "Test should return a description for a ObjectResultTracker" () {
        given:
            Map<String, ComponentStates> subComponents = [:];
            subComponents.put('field1', ComponentStates.ADDED)
            subComponents.put('field2', ComponentStates.DELETED)
            subComponents.put('field3', ComponentStates.CHANGED)
            objectResultTracker.subComponentsResult = subComponents
            String expected = 'Changed\n' +
                    '\tfield1 -> Added\n' +
                    '\tfield2 -> Deleted\n' +
                    '\tfield3 -> Changed'
        when:
            String result = objectResultTracker.toString()
        then:
            expected == result
    }
}
