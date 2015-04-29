package org.fundacionjala.gradle.plugins.enforce.filemonitor

import java.nio.file.Paths

class ComponentSerializer {

    private final String FILE_TRACKING = '.fileTracker.data'
    public String srcProject
    public String fileName

    public ComponentSerializer(String srcProject) {
        this.srcProject = srcProject
        fileName = Paths.get(srcProject, FILE_TRACKING).toString()
    }

    void save(Map<String, ComponentTracker> components) throws IOException {
        ObjectOutputStream oos
        oos = new ObjectOutputStream(new FileOutputStream(fileName))
        oos.writeObject(components)
        oos.close()
    }
}
