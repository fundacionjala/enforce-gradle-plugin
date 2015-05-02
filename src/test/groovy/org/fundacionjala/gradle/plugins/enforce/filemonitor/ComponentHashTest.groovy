package org.fundacionjala.gradle.plugins.enforce.filemonitor

import spock.lang.Shared
import spock.lang.Specification

class ComponentHashTest extends Specification {

    @Shared
    ComponentHash componentTracker

    def setup() {
        componentTracker = new ComponentHash()
    }

    def "Test should be Initialize ComponentTracker object"() {
        expect:
        componentTracker instanceof ComponentHash
    }

    def "Test should be able to comparate a component tracker if It has changed" () {
        given:
            ComponentHash componentTrackerOld = new ComponentHash('src/classes/Class1.cls', 'lkiujhytgfr')
            ComponentHash componentTrackerNew = new ComponentHash('src/classes/Class1.cls', 'qweasdzxcrt')
        when:
            ResultTracker resultTracker = componentTrackerOld.compare(componentTrackerNew)
        then:
            resultTracker.state == ComponentStates.CHANGED
    }

    def "Test should be able to comparate a component tracker if It hasn't changed" () {
        given:
            ComponentHash componentTrackerOld = new ComponentHash('src/classes/Class1.cls', 'lkiujhytgfr')
            ComponentHash componentTrackerNew = new ComponentHash('src/classes/Class1.cls', 'lkiujhytgfr')
        when:
            ResultTracker resultTracker = componentTrackerOld.compare(componentTrackerNew)
        then:
            resultTracker.state == ComponentStates.NOT_CHANGED
    }
}
