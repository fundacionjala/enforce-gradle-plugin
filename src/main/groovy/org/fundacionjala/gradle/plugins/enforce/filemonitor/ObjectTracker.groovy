package org.fundacionjala.gradle.plugins.enforce.filemonitor

class ObjectTracker extends ComponentTracker {

    public Map<String, String> subComponents

    public  ObjectTracker() {
        super()
        subComponents = [:]
    }
}
