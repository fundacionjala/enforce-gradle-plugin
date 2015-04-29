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
            ComponentTracker componentTrackerOld = new ComponentTracker('lkiujhytgfr')
            ComponentTracker componentTrackerNew = new ComponentTracker('qweasdzxcrt')
        when:
            ResultTracker resultTracker = componentTrackerOld.compare(componentTrackerNew)
        then:
            resultTracker.state == 'Changed'
    }

    def "Test should be able to comparate a component tracker if It hasn't changed" () {
        given:
            ComponentTracker componentTrackerOld = new ComponentTracker('lkiujhytgfr')
            ComponentTracker componentTrackerNew = new ComponentTracker('lkiujhytgfr')
        when:
            ResultTracker resultTracker = componentTrackerOld.compare(componentTrackerNew)
        then:
            resultTracker.state == 'Not changed'
    }
}
