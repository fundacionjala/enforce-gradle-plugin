package org.fundacionjala.gradle.plugins.enforce.filemonitor

class ComponentTracker implements Serializable, ComponentComparable<ComponentTracker> {

    public String hash

    public ComponentTracker (String hash) {
        this.hash = hash
    }

    public ComponentTracker () {
        this.hash = ""
    }

    public ResultTracker compare(ComponentTracker componentTracker) {
        ResultTracker resultTracker = new ResultTracker()
        resultTracker.state = ComponentStates.NOT_CHANGED
        if (componentTracker.hash != hash) {
            resultTracker.state = ComponentStates.CHANGED
        }
        return resultTracker
    }
}
