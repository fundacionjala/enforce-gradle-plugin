package org.fundacionjala.gradle.plugins.enforce.filemonitor

import spock.lang.Shared
import spock.lang.Specification

class ObjectTrackerTest extends Specification {

    @Shared
    ObjectTracker objectTracker

    def setup() {
        objectTracker = new ObjectTracker()
    }

    def "Test should be instance of ObjectTracker"() {
        expect:
        objectTracker instanceof ObjectTracker
    }
}
