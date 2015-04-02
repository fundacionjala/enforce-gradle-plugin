/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest.Apex

import groovy.json.JsonSlurper

/**
 * Created by Luis Sejas on 1/7/2015.
 */
class ApexClass {
    String id
    String name

    public static ArrayList<ApexClass> getApexClasses(String jsonClasses) {
        JsonSlurper jsonSlurper = new JsonSlurper()
        Object classesJson = jsonSlurper.parseText(jsonClasses)
        ArrayList<ApexClass> classes = new ArrayList<ApexClass>()
        classesJson.records.each { record ->
            ApexClass apexClass = new ApexClass(id: record.Id as String, name: record.Name as String)
            classes.push(apexClass)
        }
        return  classes
    }

    public static ArrayList<String> getIds(){

    }
 }
