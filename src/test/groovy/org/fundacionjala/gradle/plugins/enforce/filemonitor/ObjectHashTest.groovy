package org.fundacionjala.gradle.plugins.enforce.filemonitor

import spock.lang.Shared
import spock.lang.Specification

class ObjectHashTest extends Specification{
    @Shared
        ObjectHash objectHash
    @Shared
        String fileName = "Object1__c"
    @Shared
        String hash = "14se7d85fgt63c25d"
    @Shared
        Map<String, String> subComponents = [:]

    def setup() {
        subComponents.put('MyField__c', 'asdqwe13456')
        subComponents.put('myPrefix__MyField__c', 'wpolku980')
        objectHash = new ObjectHash(fileName, hash,  subComponents)
    }

    def "Test should return a map with fields that changed without the fields that have prefix" () {
        given:
            Map<String, String> subComponents = [:]
            subComponents.put('MyField__c', 'newHash')
            subComponents.put('myPrefix__MyField__c', 'anotherHash')
            ObjectHash componentHash = new ObjectHash(fileName, 'hashChanged', subComponents)
        when:
            ObjectResultTracker objectResultTracker = objectHash.compare(componentHash)
        then:
            objectResultTracker.subComponentsResult.containsKey('MyField__c')
            objectResultTracker.subComponentsResult.get('MyField__c') == ComponentStates.CHANGED
    }

    def "Test should return a map with fields that were added" () {
        given:
            Map<String, String> subComponents = [:]
            subComponents.put('MyCustomField__c', 'thisIsHash')
            ObjectHash componentHash = new ObjectHash(fileName, 'hashChanged', subComponents)
        when:
            ObjectResultTracker objectResultTracker = objectHash.compare(componentHash)
        then:
            objectResultTracker.subComponentsResult.containsKey('MyCustomField__c')
            objectResultTracker.subComponentsResult.get('MyCustomField__c') == ComponentStates.ADDED
    }

    def "Test should return a map with fields that were deleted" () {
        given:
            Map<String, String> subComponents = [:]
            subComponents.remove('MyField__c')
            ObjectHash componentHash = new ObjectHash(fileName, 'hashChanged', subComponents)
        when:
            ObjectResultTracker objectResultTracker = objectHash.compare(componentHash)
        then:
            objectResultTracker.subComponentsResult.containsKey('MyField__c')
            objectResultTracker.subComponentsResult.get('MyField__c') == ComponentStates.DELETED
    }

}
