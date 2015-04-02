/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest.Apex

import groovy.json.JsonSlurper

/**
 * Process information about apex classes
 */
class ApexClasses {
    Map<String, ApexClass> apexClassMap

    /**
     * initializes the map
     */
    public ApexClasses() {
        apexClassMap = [:]
    }

    /**
     * Load the information in the map with id and ApexClass classes
     * @param jsonClasses is a object Json contain information about id and name classes
     * @param classes is an array contain classes names
     */
    public void load(String jsonClasses, ArrayList<String> classes) {
        JsonSlurper jsonSlurper = new JsonSlurper()
        Object classesJson = jsonSlurper.parseText(jsonClasses)
        classesJson.records.each { record ->
            if (classes.contains(record.Name as String)) {
                ApexClass apexClass = new ApexClass(id: record.Id as String, name: record.Name as String)
                apexClassMap.put(record.Id as String, apexClass)
            }
        }
    }

    /**
     * Verify object json the field records is not empty
     * @param objectJson is a object json
     * @return a boolean result
     */
    public static boolean checkForRecords(String objectJson) {
        boolean existRecords = true
        JsonSlurper jsonSlurper = new JsonSlurper()
        Object resultJson = jsonSlurper.parseText(objectJson)
        if(!resultJson.records.size()){
            existRecords = false
        }
        return existRecords
    }

    public List<String> getIds() {
        apexClassMap.keySet().toList()
    }

    public ApexClass getClass(String id) {
        apexClassMap.get(id)
    }
}
