/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import com.sforce.soap.metadata.PackageTypeMembers
import groovy.xml.StreamingMarkupBuilder
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class PackageBuilderTest extends Specification {

    @Shared
    String ROOT_PATH = System.properties['user.dir']

    @Shared
    String RESOURCE_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/utils/resources"

    @Shared
    String forceXml

    def setup(){
        forceXml = '''<?xml version='1.0' encoding='UTF-8'?>
        <Package xmlns ='http://soap.sforce.com/2006/04/metadata'>
          <types>
            <members>SmartsheetImport</members>
            <name>ApexComponent</name>
          </types>
          <!-- Students grades are updated bi-monthly -->
          <types>
            <members>SmartsheetDemoPage</members>
            <members>OpportunityToSmartsheet</members>
            <name>ApexPage</name>
          </types>
            <version>32.0</version>
        </Package>
        '''
    }

    def "Should reads the data from empty package xml"() {
        given:
        def metaPackage = new Package()
        def packageBuilder = new PackageBuilder(metaPackage: metaPackage)
        def forceXml = new StreamingMarkupBuilder().bind {
            Package() {

            }
        }
        when:
        packageBuilder.read(new StringReader(forceXml.toString()))
        then:
        metaPackage.version == ''
        metaPackage.types.toList().isEmpty()
    }

    def "Should reads and load the data from package xml"() {
        given:
        def metaPackage = new Package()
        def packageBuilder = new PackageBuilder(metaPackage: metaPackage)
        def forceXml = new StreamingMarkupBuilder().bind {
            mkp.xmlDeclaration(version: Package.VERSION, encoding: Package.ENCODING)
            Package(xmlns: Package.XMLNS) {
                types() {
                    members('class1')
                    mkp.yieldUnescaped '\n\t<name>ApexClass</name>'
                }
                types() {
                    members('trigger1')
                    members('trigger2')
                    mkp.yieldUnescaped '\n\t<name>ApexTrigger</name>'
                }
                version('32.0')
            }
        }
        when:
        packageBuilder.read(new StringReader(forceXml.toString()))
        then:
        metaPackage.version == '32.0'
        metaPackage.types[0].members[0] == 'class1'
        metaPackage.types[0].name == 'ApexClass'
        metaPackage.types[1].members[0] == 'trigger1'
        metaPackage.types[1].members[1] == 'trigger2'
        metaPackage.types[1].name == 'ApexTrigger'
    }

    def "Should write the package data to a writer"() {
        given:
        def stringWriter = new StringWriter()
        def metaPackage = new Package()
        def packageData = []
        def packageTypeMembers = new PackageTypeMembers()
        packageTypeMembers.members = ['class1']
        packageTypeMembers.name = 'ApexClass'
        packageData.add(packageTypeMembers)
        def packageTypeMembers1 = new PackageTypeMembers()
        packageTypeMembers1.members = ['trigger1', 'trigger2']
        packageTypeMembers1.name = 'ApexTrigger'
        packageData.add(packageTypeMembers1)

        metaPackage.types = packageData
        metaPackage.version = '32.0'
        def packageBuilder = new PackageBuilder(metaPackage: metaPackage)
        def forceXmlExpected = new StreamingMarkupBuilder().bind {
            mkp.xmlDeclaration(version: Package.VERSION, encoding: Package.ENCODING)
            Package(xmlns: Package.XMLNS) {
                types() {
                    members('class1')
                    mkp.yieldUnescaped '\n\t<name>ApexClass</name>'
                }
                types() {
                    members('trigger1')
                    members('trigger2')
                    mkp.yieldUnescaped '\n\t<name>ApexTrigger</name>'
                }
                version('32.0')
            }
        }
        when:
        packageBuilder.write(stringWriter)
        XMLUnit.ignoreWhitespace = true
        def xmlDiff = new Diff(forceXmlExpected.toString(), stringWriter.toString())
        then:
        xmlDiff.similar()
    }

    def "Should write the empty package data to a writer"() {
        given:
        def stringWriter = new StringWriter()
        def metaPackage = new Package()
        def packageData = []
        metaPackage.types = packageData
        metaPackage.version = '32.0'
        def packageBuilder = new PackageBuilder(metaPackage: metaPackage)
        def forceXmlExpected = new StreamingMarkupBuilder().bind {
            mkp.xmlDeclaration(version: Package.VERSION, encoding: Package.ENCODING)
            Package(xmlns: Package.XMLNS) {
                version('32.0')
            }
        }
        when:
        packageBuilder.write(stringWriter)
        XMLUnit.ignoreWhitespace = true
        def xmlDiff = new Diff(forceXmlExpected.toString(), stringWriter.toString())
        then:
        xmlDiff.similar()
    }

    def "Test should return all folders from the files of resources" () {
        given:
        def metaPackage = new Package()
        def packageBuilder = new PackageBuilder(metaPackage: metaPackage)
        def listFiles = [new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'objects', 'Object1__c.object').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'objects', 'Object1__c.object').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'web', 'InvalidClass.cls').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'web', 'InvalidClass.cls').toString())]
        def listResult
        when:
        listResult = packageBuilder.selectFolders(listFiles)
        then:
        listResult == ['classes', 'objects', 'web']
    }

    def "Test should return only a folder even there there are more files" () {
        given:
        def metaPackage = new Package()
        def packageBuilder = new PackageBuilder(metaPackage: metaPackage)
        def listFiles = [new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString())
        ]
        def listResult
        when:
        listResult = packageBuilder.selectFolders(listFiles)
        then:
        listResult == ['classes']

    }

    def "Test should return all names of files without extension inside a folder" () {
        given:
        def metaPackage = new Package()
        def packageBuilder = new PackageBuilder(metaPackage: metaPackage)
        def listFiles = [new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'objects', 'Object1__c.object').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'web', 'InvalidClass.cls').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'web', 'InvalidClass.cls').toString())]
        def listResult
        when:
        listResult = packageBuilder.selectFilesMembers('objects', listFiles)
        then:
        listResult == ['Object1__c']

    }

    def "Test should set values of package" () {
        given:
        def metaPackage = new Package()
        def packageBuilder = new PackageBuilder(metaPackage: metaPackage)
        def listFiles = [new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'objects', 'Object1__c.object').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'pages').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'invalidFolder', 'Object1__c.object').toString()),
                         new File(Paths.get(RESOURCE_PATH, 'web', 'Object1__c.object').toString())]
        when:
        packageBuilder.createPackage(listFiles)
        then:
        packageBuilder.metaPackage.types[0].members[0] == 'class1'
        packageBuilder.metaPackage.types[0].members[1] == 'class1'
        packageBuilder.metaPackage.types[0].name == 'ApexClass'
        packageBuilder.metaPackage.types[1].members[0] == 'Object1__c'
        packageBuilder.metaPackage.types[1].name == 'CustomObject'
        packageBuilder.metaPackage.types[2].members[0] == '*'
        packageBuilder.metaPackage.types[2].name == 'ApexPage'
    }

    def "Test should create a package with a wilcard as member inside the folder" () {
        given:
        def metaPackage = new Package()
        def packageBuilder = new PackageBuilder(metaPackage: metaPackage)
        def listFolders = ['classes','objects','invalidFolder','nodejs']
        when:
        packageBuilder.createPackageByFolder(listFolders)
        then:
        packageBuilder.metaPackage.types[0].members[0] == '*'
        packageBuilder.metaPackage.types[0].name == 'ApexClass'
        packageBuilder.metaPackage.types[1].members[0] == '*'
        packageBuilder.metaPackage.types[1].name == 'CustomObject'
    }

    def "Test should update the package structure" () {
        given:
        def packageBuilder = new PackageBuilder()
        def membersClasses = ['class1', 'class2', 'class3']
        def membersTwoClasses = ['class21', 'class22']
        def membersObjects = ['Obj1', 'obj2', 'Obj3']
        def membersOneObjects = ['Obj11']
        when:
        packageBuilder.update('ApexClass', membersClasses)
        packageBuilder.update('ApexClass', membersTwoClasses)
        packageBuilder.update('CustomObject', membersObjects)
        packageBuilder.update('CustomObject', membersOneObjects)
        then:
        packageBuilder.metaPackage.types[0].members[0] == 'class1'
        packageBuilder.metaPackage.types[0].members[1] == 'class2'
        packageBuilder.metaPackage.types[0].members[2] == 'class3'
        packageBuilder.metaPackage.types[0].members[3] == 'class21'
        packageBuilder.metaPackage.types[0].members[4] == 'class22'
        packageBuilder.metaPackage.types[0].name == 'ApexClass'

        packageBuilder.metaPackage.types[1].members[0] == 'Obj1'
        packageBuilder.metaPackage.types[1].members[1] == 'obj2'
        packageBuilder.metaPackage.types[1].members[2] == 'Obj3'
        packageBuilder.metaPackage.types[1].members[3] == 'Obj11'
        packageBuilder.metaPackage.types[1].name == 'CustomObject'
    }

    def "Test should update a package file adding members"(){
       given:
       def expectedForceXml = '''<?xml version='1.0' encoding='UTF-8'?>
        <Package xmlns ='http://soap.sforce.com/2006/04/metadata'>
          <types>
            <members>SmartsheetImport</members>
            <name>ApexComponent</name>
          </types>
          <!-- Students grades are updated bi-monthly -->
          <types>
            <members>SmartsheetDemoPage</members>
            <members>OpportunityToSmartsheet</members>
            <members>HttpClient</members>
            <members>TCP</members>
            <members>URL</members>
            <name>ApexPage</name>
          </types>
            <version>32.0</version>
        </Package>
        '''
       def filePackage = new File(RESOURCE_PATH,'packageUpdateMembers.xml')
       filePackage.text = forceXml
       def packageBuilder = new PackageBuilder()
       when:
       packageBuilder.update('ApexPage', ['HttpClient', 'TCP', 'URL'],filePackage)
       XMLUnit.ignoreWhitespace = true
       def xmlDiff = new Diff(filePackage.text, expectedForceXml)
       then:
       xmlDiff.similar()

    }

    def "Test should update a package file creating new members"(){
        given:
        def expectedForceXml = '''<?xml version='1.0' encoding='UTF-8'?>
        <Package xmlns ='http://soap.sforce.com/2006/04/metadata'>
          <types>
            <members>SmartsheetImport</members>
            <name>ApexComponent</name>
          </types>
          <!-- Students grades are updated bi-monthly -->
          <types>
            <members>SmartsheetDemoPage</members>
            <members>OpportunityToSmartsheet</members>
            <name>ApexPage</name>
          </types>
          <types>
            <members>HttpClient</members>
            <members>TCP</members>
            <members>URL</members>
            <name>CustomObject</name>
          </types>
          <version>32.0</version>
        </Package>
        '''
        def filePackage = new File(RESOURCE_PATH,'packageAddNewMembers.xml')
        filePackage.text = forceXml
        def packageBuilder = new PackageBuilder()
        when:
        packageBuilder.update('CustomObject', ['HttpClient', 'TCP', 'URL'], filePackage)
        XMLUnit.ignoreWhitespace = true
        def xmlDiff = new Diff(filePackage.text, expectedForceXml)
        then:
        xmlDiff.similar()

    }
}
