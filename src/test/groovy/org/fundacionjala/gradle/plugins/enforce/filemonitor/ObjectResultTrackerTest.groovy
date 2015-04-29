package org.fundacionjala.gradle.plugins.enforce.filemonitor

import spock.lang.Shared
import spock.lang.Specification

class ObjectResultTrackerTest extends Specification {

    @Shared
    ObjectResultTracker objectResultTracker

    def setup() {
        objectResultTracker = new ObjectResultTracker()
    }

    def "Test should be an instance of ObjectResult" () {
        expect:
        objectResultTracker instanceof ObjectResultTracker
    }
}
