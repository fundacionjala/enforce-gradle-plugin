package org.fundacionjala.gradle.plugins.enforce.filemonitor

class ObjectResultTracker extends ResultTracker {
    Map<String, String> subComponentsResult

    public ObjectResultTracker(String state) {
        super(state)
    }
}
