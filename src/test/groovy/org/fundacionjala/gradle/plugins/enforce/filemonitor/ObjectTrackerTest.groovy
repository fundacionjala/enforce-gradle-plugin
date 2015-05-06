package org.fundacionjala.gradle.plugins.enforce.filemonitor

import spock.lang.Shared
import spock.lang.Specification

class ObjectTrackerTest extends Specification {

    @Shared
    ObjectHash oldObjectTracker

    @Shared
    ObjectHash currentObjectTracker

    @Shared
    Map<String, String> subComponents

    def setup() {
        subComponents = [:]
        oldObjectTracker = new ObjectHash('fieldAPIName', 'fileHash',subComponents)
        oldObjectTracker.subComponents = ['fieldAPIName':'fieldHash']
        currentObjectTracker = new ObjectHash('fieldAPIName', 'fileHashDifferent', subComponents)
        currentObjectTracker.subComponents = ['fieldAPIName':'fieldHashDifferent']
    }

    def "Test should be instance of ObjectTracker"() {
        expect:
        oldObjectTracker instanceof ObjectHash
    }

    def "Test should return an instance of ObjectResultTracker" () {
        given:
            ObjectHash currentObjectTracker = new ObjectHash('src/classes/Class1.cls', 'fileHash', subComponents)
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
            ObjectHash oldObjectTracker = new ObjectHash('src/classes/Class1.cls', 'lkiujhytgfr',subComponents)
            ObjectHash currentObjectTracker = new ObjectHash('src/classes/Class1.cls', 'lkiujhytgfr', subComponents)
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
