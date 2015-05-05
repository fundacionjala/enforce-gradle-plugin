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
        String result = '';
        subComponentsResult.each { field,  fieldState ->
            result += field + ' -> '+ fieldState.value() + '\n\t'
        }
        return state.value() + '\n\t' + result
    }
}
