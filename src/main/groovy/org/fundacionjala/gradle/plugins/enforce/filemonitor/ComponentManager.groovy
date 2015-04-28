package org.fundacionjala.gradle.plugins.enforce.filemonitor

import java.nio.file.Paths

class ComponentManager {

    public Map<String, ComponentTracker> components
    public srcProject

    public ComponentManager(String srcProject){
        components = [:]
        this.srcProject = srcProject
    }

    public void loadComponents(ArrayList<File> files) {

    }
}
