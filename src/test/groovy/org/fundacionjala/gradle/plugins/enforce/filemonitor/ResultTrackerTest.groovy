package org.fundacionjala.gradle.plugins.enforce.filemonitor

import spock.lang.Shared
import spock.lang.Specification

class ResultTrackerTest extends Specification {

    @Shared
    ResultTracker resultTracker

    def setup() {
        resultTracker = new ResultTracker()
    }

    def "Test should be an instance of ResultTracker" () {
        expect:
        resultTracker instanceof ResultTracker
    }

    def "Test should verify if the state is saved in the ResultTracker object" () {
        when:
            ResultTracker result = new ResultTracker(ComponentStates.ADDED)
        then:
            result.state == ComponentStates.ADDED
    }
}
