package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class PackageUtilTest extends Specification {

    @Shared
    String RESOURCE_PATH = Paths.get(System.getProperty("user.dir"), 'src', 'test', 'groovy', 'org', 'fundacionjala',
            'gradle', 'plugins','enforce', 'utils', 'resources').toString()

    def "Test should return all folders from the files of resources" () {
        given:
            def listFiles = [new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                             new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                             new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                             new File(Paths.get(RESOURCE_PATH, 'objects', 'Object1__c.object').toString()),
                             new File(Paths.get(RESOURCE_PATH, 'objects', 'Object1__c.object').toString()),
                             new File(Paths.get(RESOURCE_PATH, 'web', 'InvalidClass.cls').toString()),
                             new File(Paths.get(RESOURCE_PATH, 'web', 'InvalidClass.cls').toString())]
        when:
            def listResult = PackageUtil.selectFolders(listFiles, RESOURCE_PATH)
        then:
            listResult == ['classes', 'objects', 'web']
    }

    def "Test should return only a folder even there there are more files" () {
        given:
           def listFiles = [new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                             new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                             new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                             new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString())]
        when:
            def listResult = PackageUtil.selectFolders(listFiles, RESOURCE_PATH)
        then:
            listResult == ['classes']
    }

    def "Test should return only a folder when base path is empty" () {
        given:
            def listFiles = [new File(Paths.get('installedPackages', 'al.installedPackage').toString())]
        when:
            def listResult = PackageUtil.selectFolders(listFiles, '')
        then:
            listResult == ['installedPackages']
    }

    def "Test should return all names of files without extension inside a folder" () {
        given:
            def listFiles = [new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                             new File(Paths.get(RESOURCE_PATH, 'classes', 'class1.cls').toString()),
                             new File(Paths.get(RESOURCE_PATH, 'objects', 'Object1__c.object').toString()),
                             new File(Paths.get(RESOURCE_PATH, 'web', 'InvalidClass.cls').toString()),
                             new File(Paths.get(RESOURCE_PATH, 'web', 'InvalidClass.cls').toString())]
        when:
            def listResult = PackageUtil.selectFilesMembers('objects', listFiles, RESOURCE_PATH)
        then:
            listResult == ['Object1__c']

    }

    def "Test should return all names inside a report folder" () {
        given:
            def listFiles = [new File(Paths.get(RESOURCE_PATH, 'reports', 'reportTest', 'AccountReport').toString()),
                             new File(Paths.get(RESOURCE_PATH, 'reports', 'reportTest' , 'OpportunityReport').toString()),
                             new File(Paths.get(RESOURCE_PATH, 'reports', 'report2Test', 'Account2Report').toString())]
        when:
            def listResult = PackageUtil.selectFilesMembers('reports', listFiles, RESOURCE_PATH)
        then:
            listResult == ['reportTest/AccountReport',
                           'reportTest/OpportunityReport',
                           'report2Test/Account2Report']
    }

    def "Test should return all names inside a folder when base path is empty" () {
        given:
            def listFiles = [new File(Paths.get('installedPackages', 'al.installedPackage').toString())]
        when:
            def listResult = PackageUtil.selectFilesMembers('installedPackages', listFiles, '')
        then:
            listResult == ["al"]
    }
}
