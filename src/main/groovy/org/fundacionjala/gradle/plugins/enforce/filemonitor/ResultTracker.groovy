package org.fundacionjala.gradle.plugins.enforce.filemonitor

class ResultTracker {

    public ComponentStates state

    public ResultTracker() {

    }

    public ResultTracker(ComponentStates state) {
        this.state = state
    }
}
