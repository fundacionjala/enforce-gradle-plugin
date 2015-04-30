package org.fundacionjala.gradle.plugins.enforce.filemonitor

import spock.lang.Shared
import spock.lang.Specification

class ObjectTrackerTest extends Specification {

    @Shared
    ObjectTracker oldObjectTracker

    @Shared
    ObjectTracker currentObjectTracker

    def setup() {
        oldObjectTracker = new ObjectTracker('fileHash')
        oldObjectTracker.subComponents = ['fieldAPIName':'fieldHash']
        currentObjectTracker = new ObjectTracker('fileHashDifferent')
        currentObjectTracker.subComponents = ['fieldAPIName':'fieldHashDifferent']
    }

    def "Test should be instance of ObjectTracker"() {
        expect:
        oldObjectTracker instanceof ObjectTracker
    }

    def "Test should return an instance of ObjectResultTracker" () {
        given:
            ObjectTracker currentObjectTracker = new ObjectTracker('fileHash')
        when:
            ResultTracker resultTracker = oldObjectTracker.compare(currentObjectTracker)
        then:
            resultTracker instanceof ObjectResultTracker
    }

    def "Test should be able to compare a component tracker if It has changed" () {
        when:
            ResultTracker resultTracker = oldObjectTracker.compare(currentObjectTracker)
        then:
            resultTracker.state == ComponentStates.CHANGED
    }

    def "Test should be able to comparate a component tracker if It hasn't changed" () {
        given:
            ObjectTracker oldObjectTracker = new ObjectTracker('lkiujhytgfr')
            ObjectTracker currentObjectTracker = new ObjectTracker('lkiujhytgfr')
        when:
            ResultTracker resultTracker = oldObjectTracker.compare(currentObjectTracker)
        then:
            resultTracker.state == ComponentStates.NOT_CHANGED
    }

    def "Test should return ResultTracker with a map that contains fields changed in a ObjectTracker" () {
        when:
            ObjectResultTracker objectResultTracker = (ObjectResultTracker)oldObjectTracker.compare(currentObjectTracker)
        then:
            objectResultTracker.state == ComponentStates.CHANGED
            objectResultTracker.subComponentsResult.containsKey('fieldAPIName')
            objectResultTracker.subComponentsResult.get('fieldAPIName') == ComponentStates.CHANGED
    }

    def "Test should return ResultTracker with a map that contains fields added in a ObjectTracker" () {
        given:
            String addedFieldAPIName = 'addedFieldAPiName'
            currentObjectTracker.subComponents.put(addedFieldAPIName, 'fieldHashAdded')
        when:
            ObjectResultTracker objectResultTracker = (ObjectResultTracker)oldObjectTracker.compare(currentObjectTracker)
        then:
            objectResultTracker.state == ComponentStates.CHANGED
            objectResultTracker.subComponentsResult.containsKey(addedFieldAPIName)
            objectResultTracker.subComponentsResult.get(addedFieldAPIName) == ComponentStates.ADDED
    }

    def "Test should return ResultTracker with a map that contains fields deleted in a ObjectTracker" () {
        given:
            currentObjectTracker.subComponents.remove('fieldAPIName')
        when:
            ObjectResultTracker objectResultTracker = (ObjectResultTracker)oldObjectTracker.compare(currentObjectTracker)
        then:
            objectResultTracker.state == ComponentStates.CHANGED
            objectResultTracker.subComponentsResult.containsKey('fieldAPIName')
            objectResultTracker.subComponentsResult.get('fieldAPIName') == ComponentStates.DELETED
    }

    def "Test shouldn't return ResultTracker with a map that contains fields deleted in a ObjectTracker" () {
        given:
            currentObjectTracker.subComponents.put('notChangedFieldName', 'notChangeHash')
            oldObjectTracker.subComponents.put('notChangedFieldName', 'notChangeHash')
        when:
            ObjectResultTracker objectResultTracker = (ObjectResultTracker)oldObjectTracker.compare(currentObjectTracker)
        then:
            !objectResultTracker.subComponentsResult.containsKey('notChangedFieldName')
    }

    def "Test should return a ResultTracker with a map that contains fields deleted, updated and added in a ObjectTracker" () {
        given:
            String addedFieldAPIName = 'addedFieldAPiName'
            String deletedFieldAPIName = 'fieldAPIName'
            String updatedFieldAPIName = 'updatedFieldAPiName'
            currentObjectTracker.subComponents.put(addedFieldAPIName, 'fieldHashAdded')
            currentObjectTracker.subComponents.put(updatedFieldAPIName, 'fieldHash')
            oldObjectTracker.subComponents.put(updatedFieldAPIName, 'fieldHashDifferent')
            currentObjectTracker.subComponents.remove(deletedFieldAPIName)
        when:
            ObjectResultTracker objectResultTracker = (ObjectResultTracker)oldObjectTracker.compare(currentObjectTracker)
        then:
            objectResultTracker.subComponentsResult.containsKey(addedFieldAPIName)
            objectResultTracker.subComponentsResult.get(addedFieldAPIName) == ComponentStates.ADDED

            objectResultTracker.subComponentsResult.containsKey(updatedFieldAPIName)
            objectResultTracker.subComponentsResult.get(updatedFieldAPIName) == ComponentStates.CHANGED

            objectResultTracker.subComponentsResult.containsKey(deletedFieldAPIName)
            objectResultTracker.subComponentsResult.get(deletedFieldAPIName) == ComponentStates.DELETED
    }
}
