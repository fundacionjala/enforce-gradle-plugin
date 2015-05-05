package org.fundacionjala.gradle.plugins.enforce.filemonitor

import java.nio.file.Paths

class ComponentSerializer {
    public String sourcePath

    public ComponentSerializer(String sourcePath) {
        this.sourcePath = sourcePath
    }


    void save(Map<String, ComponentHash> components) throws IOException {
        ObjectOutputStream oos
        oos = new ObjectOutputStream(new FileOutputStream(sourcePath))
        oos.writeObject(components)
        oos.close()
    }

    Map<String, ComponentHash> read() throws IOException {
        Map<String, ComponentHash> result
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(sourcePath))
        result = (Map<String, ComponentHash>) ois.readObject()
        ois.close()
        return result
    }
}
