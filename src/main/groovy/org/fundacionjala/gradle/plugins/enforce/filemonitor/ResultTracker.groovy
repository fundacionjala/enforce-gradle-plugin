package org.fundacionjala.gradle.plugins.enforce.filemonitor

/**
 * This class contains the state when you compare two componentHash objects
 */
class ResultTracker {

    public ComponentStates state

    public ResultTracker() {

    }

    public ResultTracker(ComponentStates state) {
        this.state = state
    }

    @Override
    public String toString() {
        return state.value()
    }
}
