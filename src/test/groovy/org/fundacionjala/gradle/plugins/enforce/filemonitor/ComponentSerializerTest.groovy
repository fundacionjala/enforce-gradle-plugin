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
}
