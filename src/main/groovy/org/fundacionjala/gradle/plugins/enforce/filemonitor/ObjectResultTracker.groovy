package org.fundacionjala.gradle.plugins.enforce.filemonitor

/**
 * This class contains the state when you compare two objectHash objects
 * if the state is CHANGED then the subComponentsResult map contains
 * the state for all sub Components.
 */
class ObjectResultTracker extends ResultTracker {
    Map<String, String> subComponentsResult

    public ObjectResultTracker(ComponentStates state) {
        super(state)
        subComponentsResult = [:]
    }
}
