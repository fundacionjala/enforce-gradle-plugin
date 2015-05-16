package org.fundacionjala.gradle.plugins.enforce.filemonitor

public class ComponentHash implements ComponentComparable<ComponentHash> {
    String fileName
    String hash

    /**
     * Sets component name and component hash
     * @param fileName is a component name
     * @param hash is a hash of a component
     */
    public ComponentHash(String fileName, String hash) {
        this.fileName = fileName
        this.hash = hash
    }

    /**
     * Initializes component name and component hash
     */
    public ComponentHash() {
        this.hash = ""
        this.fileName = ""
    }

    /**
     * Compares components status
     * @param componentHash is the hash of components
     * @return an resultTracker with status
     */
    public ResultTracker compare(ComponentHash componentHash) {
        ResultTracker resultTracker = new ResultTracker()
        resultTracker.state = ComponentStates.NOT_CHANGED
        if (componentHash.hash != hash) {
            resultTracker.state = ComponentStates.CHANGED
        }
        return resultTracker
    }
}
