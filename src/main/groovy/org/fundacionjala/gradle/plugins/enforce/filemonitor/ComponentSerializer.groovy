package org.fundacionjala.gradle.plugins.enforce.filemonitor

class ComponentSerializer {

    public Map<String, ComponentTracker> components
    public srcProject

    public ComponentSerializer(String srcProject){
        components = [:]
        this.srcProject = srcProject
    }

    public void loadComponents(ArrayList<File> files) {

    }
}
