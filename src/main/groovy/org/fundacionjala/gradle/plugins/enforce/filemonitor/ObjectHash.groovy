package org.fundacionjala.gradle.plugins.enforce.filemonitor

class ObjectHash extends ComponentHash {

    public Map<String, String> subComponents

    public ObjectHash(String fileName, String hash, Map<String, String> subComponents) {
        super(fileName, hash)
        this.subComponents = subComponents
    }

    @Override
    public ResultTracker compare(ComponentHash componentHash) {
        ObjectResultTracker objectResultTracker = new ObjectResultTracker()
        objectResultTracker.state = ComponentStates.NOT_CHANGED
        if (componentHash.hash != hash) {
            objectResultTracker.state = ComponentStates.CHANGED
            ObjectHash objectHash = (ObjectHash) componentHash
            objectResultTracker.subComponentsResult = getChangedFields(objectHash)
        }
        return objectResultTracker
    }

    private Map<String, String> getChangedFields(ObjectHash objectHash) {
        Map<String, String> result = [:]
        objectHash.subComponents.each { String fieldAPIName, String fieldHash ->
            if (!this.subComponents.containsKey(fieldAPIName)) {
                result.put(fieldAPIName, ComponentStates.ADDED)
            }

            if (this.subComponents.containsKey(fieldAPIName) &&
                    !this.subComponents.get(fieldAPIName).toString().equals(fieldHash)) {
                result.put(fieldAPIName, ComponentStates.CHANGED)
            }
        }

        this.subComponents.each { String fieldAPIName, String fieldHash ->
            if(!objectHash.subComponents.containsKey(fieldAPIName)) {
                result.put(fieldAPIName, ComponentStates.DELETED);
            }
        }

        return result
    }
}
