package org.fundacionjala.gradle.plugins.enforce.filemonitor

import spock.lang.Shared
import spock.lang.Specification

class ComponentTrackerTest extends Specification {

    @Shared
    ComponentTracker componentTracker

    def setup() {
        componentTracker = new ComponentTracker()
    }

    def "Should be Initialize ComponentTracker object"() {
        expect:
        componentTracker instanceof ComponentTracker
    }

}
