package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.filter

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
        String SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
            "fundacionjala", "gradle", "plugins","enforce","tasks", "salesforce", "resources").toString()
    @Shared
        Project project
    @Shared
        ArrayList<File> allFiles
        ArrayList<File> classFiles
        ArrayList<File> triggerFiles
        ArrayList<File> objectFiles
        ArrayList<File> dashboardFiles
        ArrayList<File> dashboardsFolder1
        ArrayList<File> documentFiles
        ArrayList<File> documentFolder1
        ArrayList<File> documentFolder2

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        filter = new Filter(project, SRC_PATH)

        allFiles = []
        classFiles = []
        triggerFiles = []
        objectFiles = []
        dashboardFiles = []
        dashboardsFolder1 = []
        documentFiles = []
        documentFolder1 = []
        documentFolder2 = []

        classFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls').toString()))
        classFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls-meta.xml').toString()))
        classFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class2.cls').toString()))
        classFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class2.cls-meta.xml').toString()))
        classFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class3.cls').toString()))
        classFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class3.cls-meta.xml').toString()))
        classFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class4.cls').toString()))
        classFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class4.cls-meta.xml').toString()))
        classFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class5.cls').toString()))
        classFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class5.cls-meta.xml').toString()))
        allFiles.addAll(classFiles)

        triggerFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','triggers','Trigger1.trigger').toString()))
        triggerFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','triggers','Trigger1.trigger-meta.xml').toString()))
        triggerFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','triggers','Trigger2.trigger').toString()))
        triggerFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','triggers','Trigger2.trigger-meta.xml').toString()))
        triggerFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','triggers','Trigger3.trigger').toString()))
        triggerFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','triggers','Trigger3.trigger-meta.xml').toString()))
        triggerFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','triggers','Trigger4.trigger').toString()))
        triggerFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','triggers','Trigger4.trigger-meta.xml').toString()))
        triggerFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','triggers','Trigger5.trigger').toString()))
        triggerFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','triggers','Trigger5.trigger-meta.xml').toString()))
        allFiles.addAll(triggerFiles)


        objectFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','objects','Object1__c.object').toString()))
        objectFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','objects','Object2__c.object').toString()))
        objectFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','objects','Object3__c.object').toString()))
        objectFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','objects','Object4__c.object').toString()))
        objectFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','objects','Object5__c.object').toString()))
        allFiles.addAll(objectFiles)

        dashboardsFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','dashboards','DashboardFolder1','DashboardTest_1_1.dashboards').toString()))
        dashboardsFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','dashboards','DashboardFolder1','DashboardTest_1_2.dashboards').toString()))
        dashboardsFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','dashboards','DashboardFolder1','DashboardTest_1_3.dashboards').toString()))
        dashboardsFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','dashboards','DashboardFolder1','DashboardTest_1_4.dashboards').toString()))
        dashboardsFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','dashboards','DashboardFolder1','DashboardTest_1_5.dashboards').toString()))
        dashboardsFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','dashboards','DashboardFolder2','DashboardTest_2_1.dashboards').toString()))
        dashboardsFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','dashboards','DashboardFolder2','DashboardTest_2_2.dashboards').toString()))
        dashboardsFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','dashboards','DashboardFolder2','DashboardTest_2_3.dashboards').toString()))
        dashboardsFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','dashboards','DashboardFolder2','DashboardTest_2_4.dashboards').toString()))
        dashboardsFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','dashboards','DashboardFolder2','DashboardTest_2_5.dashboards').toString()))
        allFiles.addAll(dashboardsFolder1)

        dashboardFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','dashboards','DashboardFolder1-meta.xml').toString()))
        dashboardFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','dashboards','DashboardFolder2-meta.xml').toString()))
        allFiles.addAll(dashboardFiles)

        documentFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder1','DocumentTest_1_1.txt').toString()))
        documentFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder1','DocumentTest_1_2.docx').toString()))
        documentFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder1','DocumentTest_1_3.html').toString()))
        documentFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder1','DocumentTest_1_4.rar').toString()))
        documentFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder1','DocumentTest_1_5.jpg').toString()))
        documentFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder1','DocumentTest_1_1.txt-meta.xml').toString()))
        documentFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder1','DocumentTest_1_2.docx-meta.xml').toString()))
        documentFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder1','DocumentTest_1_3.html-meta.xml').toString()))
        documentFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder1','DocumentTest_1_4.rar-meta.xml').toString()))
        documentFolder1.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder1','DocumentTest_1_5.jpg-meta.xml').toString()))
        allFiles.addAll(documentFolder1)

        documentFolder2.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder2','DocumentTest_2_1.txt').toString()))
        documentFolder2.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder2','DocumentTest_2_2.docx').toString()))
        documentFolder2.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder2','DocumentTest_2_3.html').toString()))
        documentFolder2.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder2','DocumentTest_2_4.rar').toString()))
        documentFolder2.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder2','DocumentTest_2_5.jpg').toString()))
        documentFolder2.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder2','DocumentTest_2_1.txt-meta.xml').toString()))
        documentFolder2.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder2','DocumentTest_2_2.docx-meta.xml').toString()))
        documentFolder2.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder2','DocumentTest_2_3.html-meta.xml').toString()))
        documentFolder2.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder2','DocumentTest_2_4.rar-meta.xml').toString()))
        documentFolder2.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder2','DocumentTest_2_5.jpg-meta.xml').toString()))
        allFiles.addAll(documentFolder2)

        documentFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder1-meta.xml').toString()))
        documentFiles.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder2-meta.xml').toString()))
        allFiles.addAll(documentFiles)

        ArrayList<File> folders = new ArrayList<File>()
        folders.add(new File(Paths.get(SRC_PATH,'src_temporary').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'src_temporary','classes').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'src_temporary','triggers').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'src_temporary','objects').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'src_temporary','dashboards').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'src_temporary','dashboards','DashboardFolder1').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'src_temporary','dashboards','DashboardFolder2').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'src_temporary','documents').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder1').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder2').toString()))

        folders.each { folder->
            new File(folder.getAbsolutePath()).mkdir()
        }

        allFiles.each { file->
            new File(file.getAbsolutePath()).createNewFile()
        }
    }

    def "Test should return all classes from project path"() {
        given:
            String includes =  'classes'
            String excludes = ""
        when:
            ArrayList<File> result = filter.getFiles(includes, excludes)
        then:
            result.sort() == [new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString()),
                              new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString())].sort()
    }

    def "Test should return the Class1 file" () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String includes =  "classes${File.separator}Class1.cls"
            ArrayList<File> filesExpected = []
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls-meta.xml').toString()))
        when:
            ArrayList<File> result = myFilter.getFiles(includes, "")
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return the Class1,Class2 files" () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String includes = "classes${File.separator}Class1.cls,classes${File.separator}Class2.cls"
            ArrayList<File> filesExpected = []
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class2.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class2.cls-meta.xml').toString()))
        when:
            ArrayList<File> result = myFilter.getFiles(includes, "")
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all classes with the criteria [files:classes/**] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String includes = "classes${File.separator}**"
            ArrayList<File> filesExpected = []
            filesExpected.addAll(classFiles)
        when:
            ArrayList<File> result = myFilter.getFiles(includes, "")
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all classes with the criteria [files:classes/*.cls] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String includes = "classes${File.separator}*.cls"
            ArrayList<File> filesExpected = []
            filesExpected.addAll(classFiles)
        when:
            ArrayList<File> result = myFilter.getFiles(includes, "")
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all classes excluding class 3 and 4 with the criteria [files:classes/*.cls] [classes/Class3.cls,classes/Class4.cls] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String includes ="classes${File.separator}*.cls"
            String excludes ="classes${File.separator}Class3.cls,classes${File.separator}Class4.cls"
            ArrayList<File> filesExpected = []
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class2.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class2.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class5.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class5.cls-meta.xml').toString()))
        when:
            ArrayList<File> result = myFilter.getFiles(includes, excludes)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all components [files:] [excludes:] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<File> filesExpected = allFiles.clone()
        when:
            ArrayList<File> result = myFilter.getFiles("", "")
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all classes excluding Class1.cls [excludes:classes/Class1.cls]  " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String excludes = "classes${File.separator}Class1.cls"
            ArrayList<File> filesExpected = allFiles.clone()
            filesExpected.remove(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls').toString()))
            filesExpected.remove(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls-meta.xml').toString()))
        when:
            ArrayList<File> result = myFilter.getFiles("", excludes)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return cero components [files:classes/*.cls] [excludes:classes/*.cls]  " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String includes = "classes${File.separator}*.cls"
            String excludes = "classes${File.separator}*.cls"
            ArrayList<File> filesExpected = []
        when:
            ArrayList<File> result = myFilter.getFiles(includes, excludes)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all components less classes [excludes:triggers,objects]  " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String excludes = "triggers,objects"
            ArrayList<File> filesExpected = []
            filesExpected.addAll(classFiles)
            filesExpected.addAll(documentFiles)
            filesExpected.addAll(documentFolder1)
            filesExpected.addAll(documentFolder2)
            filesExpected.addAll(dashboardFiles)
            filesExpected.addAll(dashboardsFolder1)
        when:
            ArrayList<File> result = myFilter.getFiles("", excludes)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all components less classes [excludes:triggers,objects,object,object/Object1__c.object]  " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String excludes = "triggers,objects,object${File.separator}Object1__c.object"
            ArrayList<File> filesExpected = []
            filesExpected.addAll(classFiles)
            filesExpected.addAll(documentFiles)
            filesExpected.addAll(documentFolder1)
            filesExpected.addAll(documentFolder2)
            filesExpected.addAll(dashboardFiles)
            filesExpected.addAll(dashboardsFolder1)
        when:
            ArrayList<File> result = myFilter.getFiles("", excludes)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all components less classes [excludes:triggers,objects,object,object/Object1__c.object] without parameters " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String excludes = "triggers,objects,object${File.separator}Object1__c.object"
            ArrayList<File> filesExpected = allFiles.clone()
            filesExpected.remove(new File(Paths.get(SRC_PATH, 'src_temporary', 'objects', 'Object1__c.object').toString()))
            filesExpected.remove(new File(Paths.get(SRC_PATH, 'src_temporary', 'objects', 'Object2__c.object').toString()))
            filesExpected.remove(new File(Paths.get(SRC_PATH, 'src_temporary', 'objects', 'Object3__c.object').toString()))
            filesExpected.remove(new File(Paths.get(SRC_PATH, 'src_temporary', 'objects', 'Object4__c.object').toString()))
            filesExpected.remove(new File(Paths.get(SRC_PATH, 'src_temporary', 'objects', 'Object5__c.object').toString()))
            filesExpected.remove(new File(Paths.get(SRC_PATH, 'src_temporary', 'triggers', 'Trigger1.trigger').toString()))
            filesExpected.remove(new File(Paths.get(SRC_PATH, 'src_temporary', 'triggers', 'Trigger1.trigger-meta.xml').toString()))
            filesExpected.remove(new File(Paths.get(SRC_PATH, 'src_temporary', 'triggers', 'Trigger2.trigger').toString()))
            filesExpected.remove(new File(Paths.get(SRC_PATH, 'src_temporary', 'triggers', 'Trigger2.trigger-meta.xml').toString()))
            filesExpected.remove(new File(Paths.get(SRC_PATH, 'src_temporary', 'triggers', 'Trigger3.trigger').toString()))
            filesExpected.remove(new File(Paths.get(SRC_PATH, 'src_temporary', 'triggers', 'Trigger3.trigger-meta.xml').toString()))
            filesExpected.remove(new File(Paths.get(SRC_PATH, 'src_temporary', 'triggers', 'Trigger4.trigger').toString()))
            filesExpected.remove(new File(Paths.get(SRC_PATH, 'src_temporary', 'triggers', 'Trigger4.trigger-meta.xml').toString()))
            filesExpected.remove(new File(Paths.get(SRC_PATH, 'src_temporary', 'triggers', 'Trigger5.trigger').toString()))
            filesExpected.remove(new File(Paths.get(SRC_PATH, 'src_temporary', 'triggers', 'Trigger5.trigger-meta.xml').toString()))
        when:
            ArrayList<File> result = myFilter.getFiles("", excludes)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all documents [files:documents/**] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String includes = "documents${File.separator}**"
            ArrayList<File> filesExpected = []
            filesExpected.addAll(documentFiles)
            filesExpected.addAll(documentFolder1)
            filesExpected.addAll(documentFolder2)
        when:
            ArrayList<File> result = myFilter.getFiles(includes, "")
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return  documents in folder documents1 [files:documents/DocumentsFolder1/**] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String includes = "documents${File.separator}DocumentsFolder1${File.separator}**"
            ArrayList<File> filesExpected = []
            filesExpected.addAll(documentFolder1)
        when:
            ArrayList<File> result = myFilter.getFiles(includes, "")
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return  documents in folder documents2 [files:documents/DocumentsFolder2/*.*] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String includes = "documents${File.separator}DocumentsFolder2${File.separator}*.*"
            ArrayList<File> filesExpected = []
            filesExpected.addAll(documentFolder2)
        when:
            ArrayList<File> result = myFilter.getFiles(includes, "")
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return documents in folder documents1 [files:documents/DocumentsFolder1] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String includes = "documents${File.separator}DocumentsFolder1"
            ArrayList<File> filesExpected = []
            filesExpected.addAll(documentFolder1)
        when:
            ArrayList<File> result = myFilter.getFiles(includes, "")
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all components less documents in folder documents1 [exclude:documents/DocumentsFolder1/**] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String excludes = "documents${File.separator}DocumentsFolder1${File.separator}**"
            ArrayList<File> filesExpected = allFiles.clone()
            filesExpected.removeAll(documentFolder1)
        when:
            ArrayList<File> result = myFilter.getFiles("", excludes)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return  all components less documents in folder documents1 [exclude:documents/DocumentsFolder1] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String excludes = "documents${File.separator}DocumentsFolder1"
            ArrayList<File> filesExpected = allFiles.clone()
            filesExpected.removeAll(documentFolder1)
        when:
            ArrayList<File> result = myFilter.getFiles("", excludes)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return  the documentes less documents in folder documents1 [files:documents/DocumentsFolder1/DocumentTest_1_1.txt] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String includes =  "documents${File.separator}DocumentsFolder1${File.separator}DocumentTest_1_1.txt"
            ArrayList<File> filesExpected = []
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder1','DocumentTest_1_1.txt').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder1','DocumentTest_1_1.txt-meta.xml').toString()))
        when:
            ArrayList<File> result = myFilter.getFiles(includes, "")
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all folder documents less documents1 folder  [files:documents/**] [excludes: documents/DocumentsFolder1/**] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String includes = "documents${File.separator}**"
            String excludes = "documents${File.separator}DocumentsFolder1${File.separator}**"
            ArrayList<File> filesExpected = []
            filesExpected.addAll(documentFiles)
            filesExpected.addAll(documentFolder2)
        when:
            ArrayList<File> result = myFilter.getFiles(includes, excludes)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all files less .fileTracker.data file" () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            File fileTracker = new File(Paths.get(SRC_PATH, 'src_temporary', '.fileTracker.data').toString())
            fileTracker.write('This is a fileTracker file content')
        when:
            ArrayList<File> result = myFilter.getFiles("", "")
        then:
            result.sort() == allFiles.sort()
    }

    def "Test should return a criteria when you send a wildcard " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String criterion = "*${File.separator}*.cls"
        when:
            ArrayList<String> result = myFilter.getCriteria(criterion)
        then:
            result.sort() == ["*${File.separator}*.cls", "*${File.separator}*.cls-meta.xml"].sort()
    }

    def "Test should return a criteria when you send a folder name" () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String criterion = "classes,objects"
        when:
            ArrayList<String> result = myFilter.getCriteria(criterion)
        then:
            result.sort() == ["classes${File.separator}**", "objects${File.separator}**"].sort()
    }

    def "Test should return a criteria when you send a sub folder name" () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String criterion = "documents${File.separator}DocumentsFolder1"
        when:
            ArrayList<String> result = myFilter.getCriteria(criterion)
        then:
            result.sort() == ["documents${File.separator}DocumentsFolder1${File.separator}**"].sort()
    }

    def "Test should return a criteria when you send a folders name with spaces" () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String criterion = "documents, classes , objects"
        when:
            ArrayList<String> result = myFilter.getCriteria(criterion)
        then:
            result.sort() == ["documents${File.separator}**", "classes${File.separator}**", "objects${File.separator}**"].sort()
    }

    def "Test should return a criteria when you send a reports, or documents with their extensions" () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            String criterion = "reports${File.separator}MyReports${File.separator}newReport.report"
        when:
            ArrayList<String> result = myFilter.getCriteria(criterion)
        then:
            result.sort() == ["reports${File.separator}MyReports${File.separator}newReport.report",
                              "reports${File.separator}MyReports${File.separator}newReport.report-meta.xml"].sort()
    }

    def cleanupSpec() {
        new File(Paths.get(SRC_PATH,'src_temporary').toString()).deleteDir()
    }
}
