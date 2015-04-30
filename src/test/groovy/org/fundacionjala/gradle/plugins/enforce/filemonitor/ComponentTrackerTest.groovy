package org.fundacionjala.gradle.plugins.enforce.filemonitor

import spock.lang.Shared
import spock.lang.Specification

class ComponentTrackerTest extends Specification {

    @Shared
    ComponentTracker componentTracker

    def setup() {
        componentTracker = new ComponentTracker()
    }

    def "Test should be Initialize ComponentTracker object"() {
        expect:
        componentTracker instanceof ComponentTracker
    }

    def "Test should be able to comparate a component tracker if It has changed" () {
        given:
            ComponentTracker componentTrackerOld = new ComponentTracker('src/classes/Class1.cls', 'lkiujhytgfr')
            ComponentTracker componentTrackerNew = new ComponentTracker('src/classes/Class1.cls', 'qweasdzxcrt')
        when:
            ResultTracker resultTracker = componentTrackerOld.compare(componentTrackerNew)
        then:
            resultTracker.state == ComponentStates.CHANGED
    }

    def "Test should be able to comparate a component tracker if It hasn't changed" () {
        given:
            ComponentTracker componentTrackerOld = new ComponentTracker('src/classes/Class1.cls', 'lkiujhytgfr')
            ComponentTracker componentTrackerNew = new ComponentTracker('src/classes/Class1.cls', 'lkiujhytgfr')
        when:
            ResultTracker resultTracker = componentTrackerOld.compare(componentTrackerNew)
        then:
            resultTracker.state == ComponentStates.NOT_CHANGED
    }
}
