package org.fundacionjala.gradle.plugins.enforce.filemonitor

import org.fundacionjala.gradle.plugins.enforce.utils.Util

/**
 * This class represents the state of the object component and its sub components
 */
class ObjectHash extends ComponentHash {

    Map<String, String> subComponents

    public ObjectHash(String fileName, String hash, Map<String, String> subComponents) {
        super(fileName, hash)
        this.subComponents = subComponents
    }

    /**
     * Compares and returns all changes  given two ObjectHash
     * @param aomponentHash is a ObjectHash to compare all differences
     * @return a ObjectResultTracker if there are changes, contains all changes in its sub components
     */
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

    /**
     * Returns a map that contains all states of the changes, if the sub component was modified, deleted or added
     * @param objectHash is a ObjectHash to find all differences
     * @return a Map<String, String> that contains as key the custom field type/fullName and value
     * the hash code of the custom field's content
     */
    private Map<String, String> getChangedFields(ObjectHash objectHash) {
        Map<String, String> result = [:]
        objectHash.subComponents.each { String fieldAPIName, String fieldHash ->
            Boolean isPackaged = Util.isPackaged(fieldAPIName)
            if (!isPackaged && !this.subComponents.containsKey(fieldAPIName)) {
                result.put(fieldAPIName, ComponentStates.ADDED)
            }

            if (!isPackaged && this.subComponents.containsKey(fieldAPIName) &&
                    !this.subComponents.get(fieldAPIName).toString().equals(fieldHash)) {
                result.put(fieldAPIName, ComponentStates.CHANGED)
            }
        }

        this.subComponents.each { String fieldAPIName, String fieldHash ->
            if(!Util.isPackaged(fieldAPIName) && !objectHash.subComponents.containsKey(fieldAPIName)) {
                result.put(fieldAPIName, ComponentStates.DELETED)
            }
        }

        return result
    }
}
