package org.fundacionjala.gradle.plugins.enforce.filemonitor

class ComponentTracker implements Serializable {

    public String hash

    public ComponentTracker (String hash) {
        this.hash = hash
    }

    public ComponentTracker () {
        this.hash = ""
    }

    public ResultTracker compare(ComponentTracker componentTracker) {
        ResultTracker resultTracker = new ResultTracker()
        resultTracker.state = ComponentStates.NOT_CHANGED.value()
        if (componentTracker.hash != hash) {
            resultTracker.state = ComponentStates.CHANGED.value()
        }
        return resultTracker
    }
}
