package org.fundacionjala.gradle.plugins.enforce.filemonitor

class ComponentTracker {

    public String hash

    public ComponentTracker (String hash) {
        this.hash = hash
    }

    public ComponentTracker () {
        this.hash = ""
    }
}
