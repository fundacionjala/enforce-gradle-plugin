package org.fundacionjala.gradle.plugins.enforce.filemonitor

class ObjectTracker extends ComponentTracker {

    public Map<String, String> subComponents

    public ObjectTracker(String hash) {
        super(hash)
        subComponents = [:]
    }

    @Override
    public ResultTracker compare(ComponentTracker componentTracker) {
        ObjectResultTracker objectResultTracker = new ObjectResultTracker()
        objectResultTracker.state = ComponentStates.NOT_CHANGED.value()
        if (componentTracker.hash != hash) {
            objectResultTracker.state = ComponentStates.CHANGED.value()
            ObjectTracker objectTracker = (ObjectTracker) componentTracker
            objectResultTracker.subComponentsResult = getChangedFields(objectTracker)
        }
        return objectResultTracker
    }

    private Map<String, String> getChangedFields(ObjectTracker objectTracker) {
        Map<String, String> result = [:]
        objectTracker.subComponents.each { String fieldAPIName, String fieldHash ->
            if (!this.subComponents.containsKey(fieldAPIName)) {
                result.put(fieldAPIName, ComponentStates.ADDED.value())
            }

            if (this.subComponents.containsKey(fieldAPIName) &&
                    !this.subComponents.get(fieldAPIName).toString().equals(fieldHash)) {
                result.put(fieldAPIName, ComponentStates.CHANGED.value())
            }
        }

        this.subComponents.each { String fieldAPIName, String fieldHash ->
            if(!objectTracker.subComponents.containsKey(fieldAPIName)) {
                result.put(fieldAPIName, ComponentStates.DELETED.value());
            }
        }

        return result
    }
}
