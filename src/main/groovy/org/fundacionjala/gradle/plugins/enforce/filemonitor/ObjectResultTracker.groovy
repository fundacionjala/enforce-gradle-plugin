package org.fundacionjala.gradle.plugins.enforce.filemonitor

/**
 * This class contains the state when you compare two objectHash objects
 * if the state is CHANGED then the subComponentsResult map contains
 * the state for all sub Components.
 */
class ObjectResultTracker extends ResultTracker {
    Map<String, ComponentStates> subComponentsResult

    public ObjectResultTracker(ComponentStates state) {
        super(state)
        subComponentsResult = [:]
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder()
        subComponentsResult.each { field,  fieldState ->
            result.append('\n\t')
            result.append(field)
            result.append(' -> ')
            result.append(fieldState.value())
        }
        return state.value() + result.toString()
    }
}
