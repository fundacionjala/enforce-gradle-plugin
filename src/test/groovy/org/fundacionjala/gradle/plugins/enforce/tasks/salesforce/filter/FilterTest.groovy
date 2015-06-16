package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.filter

import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class FilterTest extends Specification{
    @Shared
        Filter filter
    @Shared
        String projectPath = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
                             "fundacionjala", "gradle", "plugins","enforce","tasks", "salesforce", "resources").toString()
    @Shared
        Project project

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        filter = new Filter(project, projectPath)
    }

    def "Test should be instance of Filter class"() {
        expect:
            filter instanceof Filter
    }

    def "Test should exclude a file by file"() {
        given:
            ArrayList<File> files = [new File(Paths.get(projectPath, 'classes', 'class1.cls').toString()),
                                     new File(Paths.get(projectPath, 'classes', 'class1.cls-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger').toString()),
                                     new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger-meta.xml').toString())]
            String criterion = "classes${File.separator}class1.cls"
        when:
            def arrayFiltered = filter.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger').toString()),
                                 new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger-meta.xml').toString())]
    }

    def "Test should exclude a files by folder"() {
        given:
            ArrayList<File> files = [new File(Paths.get(projectPath, 'classes', 'class1.cls').toString()),
                                     new File(Paths.get(projectPath, 'classes', 'class1.cls-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger').toString()),
                                     new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Account.object').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Object2__c.object').toString())]
            String criterion = "classes"
        when:
            def arrayFiltered = filter.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger').toString()),
                                     new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Account.object').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Object2__c.object').toString())].sort()
    }

    def "Test should exclude a file when you sent as criterion a wilcard"() {
        given:
            ArrayList<File> files = [new File(Paths.get(projectPath, 'classes', 'class1.cls').toString()),
                                     new File(Paths.get(projectPath, 'classes', 'class1.cls-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger').toString()),
                                     new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Account.object').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Object2__c.object').toString())]
            String criterion = "*${File.separator}class1.cls"
        when:
            def arrayFiltered = filter.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger').toString()),
                                     new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Account.object').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Object2__c.object').toString())].sort()
    }

    def "Test should exclude a files when you sent as criterion a wilcard equal to classes/*"() {
        given:
            ArrayList<File> files = [new File(Paths.get(projectPath, 'classes', 'class1.cls').toString()),
                                     new File(Paths.get(projectPath, 'classes', 'class1.cls-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger').toString()),
                                     new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Account.object').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Object2__c.object').toString())]
            String criterion = "objects${File.separator}**"
        when:
            def arrayFiltered = filter.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(projectPath, 'classes', 'class1.cls').toString()),
                                     new File(Paths.get(projectPath, 'classes', 'class1.cls-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger').toString()),
                                     new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger-meta.xml').toString())].sort()
    }

    def "Test should exclude a list of files"() {
        given:
            ArrayList<File> files = [new File(Paths.get(projectPath, 'classes', 'class1.cls').toString()),
                                     new File(Paths.get(projectPath, 'classes', 'class1.cls-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger').toString()),
                                     new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Account.object').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Object2__c.object').toString())]
            String criterion = "classes${File.separator}class1.cls,triggers${File.separator}LunesTrigger.trigger"
        when:
            def arrayFiltered = filter.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(projectPath, 'objects', 'Account.object').toString()),
                                 new File(Paths.get(projectPath, 'objects', 'Object1__c.object').toString()),
                                 new File(Paths.get(projectPath, 'objects', 'Object2__c.object').toString())]
    }

    def "Test should exclude a list of files with it xml file"() {
        given:
            ArrayList<File> files = [new File(Paths.get(projectPath, 'classes', 'class1.cls').toString()),
                                     new File(Paths.get(projectPath, 'classes', 'class1.cls-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger.trigger').toString()),
                                     new File(Paths.get(projectPath, 'triggers', 'LunesTrigger.trigger-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Account.object').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(projectPath, 'objects', 'Object2__c.object').toString())]
            String criterion = "classes${File.separator}class1.cls,triggers${File.separator}LunesTrigger.trigger"
        when:
            def arrayFiltered = filter.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(projectPath, 'objects', 'Account.object').toString()),
                                 new File(Paths.get(projectPath, 'objects', 'Object1__c.object').toString()),
                                 new File(Paths.get(projectPath, 'objects', 'Object2__c.object').toString())]
    }

    def "Test should exclude files by wildcard sent 'classes/**'"() {
        given:
            Filter filter = new Filter(project, Paths.get(projectPath, 'src').toString())
            ArrayList<File> files = [new File(Paths.get(projectPath, 'src', 'classes', 'Class1.cls').toString()),
                                     new File(Paths.get(projectPath, 'src', 'classes', 'Class1.cls-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'src', 'classes', 'class2.cls').toString()),
                                     new File(Paths.get(projectPath, 'src', 'classes', 'class2.cls-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                     new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString())]
            String criterion = "classes${File.separator}**"
        when:
            def arrayFiltered = filter.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                 new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString())].sort()
    }

    def "Test should exclude files by wildcard sent '**/*.object'"() {
        given:
            ArrayList<File> files = [new File(Paths.get(projectPath, 'src', 'classes', 'Class1.cls').toString()),
                                     new File(Paths.get(projectPath, 'src', 'classes', 'Class1.cls-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                     new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'src', 'objects', 'Account.object').toString()),
                                     new File(Paths.get(projectPath, 'src', 'objects', 'Object1__c.object').toString())]
            String criterion = "**${File.separator}*.object"
        when:
            def arrayFiltered = filter.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                     new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'src', 'classes', 'Class1.cls-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'src', 'classes', 'Class1.cls').toString())].sort()
    }

    def "Test should exclude files by wildcard sent '**/*.cls'"() {
        given:
            ArrayList<File> files = [new File(Paths.get(projectPath, 'src', 'classes', 'Class1.cls').toString()),
                                     new File(Paths.get(projectPath, 'src', 'classes', 'Class1.cls-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                     new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'src', 'objects', 'Object1__c.object').toString())]
            String criterion = "**${File.separator}*.cls"
        when:
            def arrayFiltered = filter.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                     new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'src', 'objects', 'Object1__c.object').toString())].sort()

    }

    def "Test should exclude files by wildcard sent '**/*Account*/**'"() {
        given:
            ArrayList<File> files = [new File(Paths.get(projectPath, 'src', 'classes', 'Class1.cls').toString()),
                                     new File(Paths.get(projectPath, 'src', 'classes', 'Class1.cls-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                     new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'src', 'objects', 'Account.object').toString()),
                                     new File(Paths.get(projectPath, 'src', 'objects', 'Object1__c.object').toString())]
            String criterion = "**${File.separator}*Account*${File.separator}**"
        when:
            def arrayFiltered = filter.excludeFilesByCriterion(files, criterion)
        then:
            arrayFiltered.sort() == [new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger').toString()),
                                     new File(Paths.get(projectPath, 'src', 'triggers', 'Trigger1.trigger-meta.xml').toString()),
                                     new File(Paths.get(projectPath, 'src', 'objects', 'Object1__c.object').toString()),
                                     new File(Paths.get(projectPath, 'src', 'classes', 'Class1.cls').toString()),
                                     new File(Paths.get(projectPath, 'src', 'classes', 'Class1.cls-meta.xml').toString())].sort()
    }

    def "Test should return class name that was excluded"() {
        given:
            String criterion = "classes${File.separator}class1.cls"
        when:
            ArrayList<String> result = filter.getFilesExcludes(criterion)
        then:
            result.size() == 1
            result == ["classes${File.separator}class1.cls"]
    }

    def "Test should return objects that were excluded"() {
        given:
            String criterion = "objects"
            String object2 = "objects${File.separator}Object2__c.object"
            String object1 = "objects${File.separator}Object1__c.object"
            String object3 = "objects${File.separator}Account.object"
        when:
            ArrayList<String> result = filter.getFilesExcludes(criterion)
        then:
            result.sort() == [object1, object2, object3].sort()
    }

    def "Test should return Account object that were excluded"() {
        given:
            String criterion = "**/Account.object"
            String accountObject1 = "objects${File.separator}Account.object"
        when:
            ArrayList<String> result = filter.getFilesExcludes(criterion)
        then:
            result.sort() == [accountObject1].sort()
    }

    def "Test should return Document component that was excluded"() {
        given:
            String criterion = "documents"

            String document1 = "documents${File.separator}myDocuments${File.separator}doc.txt"
            String document2 = "documents${File.separator}myDocuments${File.separator}doc2.txt"
        when:
            ArrayList<String> result = filter.getFilesExcludes(criterion)
        then:
            result.sort() == [document1, document2].sort()
    }

    def "Test should return Report component that was excluded"() {
        given:
            String criterion = "reports"
        when:
            ArrayList<String> result = filter.getFilesExcludes(criterion)
        then:
            result.sort() == ["reports${File.separator}myreports${File.separator}reportTest.report"].sort()
    }
}
