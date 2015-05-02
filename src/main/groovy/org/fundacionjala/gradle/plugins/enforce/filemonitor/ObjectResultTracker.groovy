package org.fundacionjala.gradle.plugins.enforce.filemonitor

class ObjectResultTracker extends ResultTracker {
    Map<String, String> subComponentsResult

    public ObjectResultTracker(ComponentStates state) {
        super(state)
        subComponentsResult = [:]
    }
}
