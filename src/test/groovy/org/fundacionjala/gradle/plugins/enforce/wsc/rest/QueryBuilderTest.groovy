/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc.rest

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class QueryBuilderTest extends Specification {

    @Shared
        QueryBuilder queryBuilder

    @Shared
        def wscPath = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala", "gradle",
            "plugins","enforce", "wsc").toString()

    def setupSpec() {
        queryBuilder = new QueryBuilder()
    }

    def "Test should be an instance of QueryBuilder class" () {
        expect:
            queryBuilder instanceof QueryBuilder
    }

    def "Test should return an exception if package doesn't exist" () {
        given:
            def packagePath = Paths.get(wscPath, 'package.xml').toString()
        when:
            queryBuilder.createQueryFromPackage(packagePath)
        then:
            thrown(Exception)
    }

    def "Test should get components from package xml" () {
        given:
            def packagePath = Paths.get(wscPath, 'resources', 'packageTest.xml').toString()
        when:
            def componentsObtained = queryBuilder.getComponents(new FileReader(packagePath))
            def componentsName = new ArrayList<String>()
            componentsObtained.each { component ->
                componentsName.add(component.name)
            }
            componentsName = componentsName.sort()
        then:
            componentsName.get(0) == 'ApexClass'
            componentsName.get(1) == 'ApexComponent'
            componentsName.get(2) == 'ApexPage'
            componentsName.get(3) == 'ApexTrigger'
            componentsName.get(4) == 'CustomObject'
            componentsName.get(5) == 'StaticResource'
    }

    def "Test should return an array with queries that have fields" () {
        given:
            def packagePath = Paths.get(wscPath, 'resources', 'packageWithFields.xml').toString()
        when:
            def queries = queryBuilder.createQueryFromPackage(packagePath)
        then:
            queries.sort() == ['SELECT Name FROM ApexClass', 'SELECT Name FROM ApexComponent', 'SELECT Name FROM CustomField'].sort()
    }

    def "Test should return an array with queries" () {
        given:
            def packagePath = Paths.get(wscPath, 'resources', 'packageTest.xml').toString()
        when:
            def queries = queryBuilder.createQueryFromPackage(packagePath)
        then:
            queries.sort() == ['SELECT Name FROM ApexClass', 'SELECT Name FROM ApexComponent', 'SELECT Name FROM ApexPage', 'SELECT Name FROM ApexTrigger',
                               'SELECT Name FROM StaticResource'].sort()
    }

    def "Test should get component from a file" () {
        when:
        def result = queryBuilder.getComponent(new File(Paths.get(wscPath, 'resources', 'pages','pageMock.page').toString()))
        then:
        result == 'ApexPage'
    }

    def "Test should create queries to those files" () {
        given:
        ArrayList<File> files = new ArrayList<File>()
        files.push(new File(Paths.get(wscPath, 'resources', 'pages','pageMock.page').toString()))
        files.push(new File(Paths.get(wscPath, 'resources', 'classes','classMock.cls').toString()))
        files.push(new File(Paths.get(wscPath, 'resources', 'invalidFolder','classMock.cls').toString()))
        when:
        def result = queryBuilder.createQueriesFromListOfFiles(files)
        then:
        result == ["""SELECT Name FROM ApexPage WHERE Name = 'pageMock'""", """SELECT Name FROM ApexClass WHERE Name = 'classMock'"""]
    }


    def "Test should return true if component is into default components" () {
        given:
            def component = 'ApexClass'
        when:
            def result = queryBuilder.isDefaultComponent(component)
        then:
            result
    }

    def "Test should return false if component isn't into default components" () {
        given:
            def component = 'CustomObject'
        when:
            def result = queryBuilder.isDefaultComponent(component)
        then:
            !result
    }
}
