package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.filter

import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
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

    def "Test should be instance of Filter class"() {
        expect:
        filter instanceof Filter
    }

    def "Test should return a map with parameter as key and its content as value"() {
        given:
            ArrayList<String> parametersName = ['files']
            Map<String, String> properties = [:]
            properties.put('files', 'classes')
        when:
            Map<String, String> result = filter.getContentParameter(parametersName, properties)
        then:
            result.containsKey('files')
            result.get('files') == 'classes'
    }

    def "Test should return a map with pall parameters and their values"() {
        given:
            ArrayList<String> parametersName = ['files', 'excludes']
            Map<String, String> properties = [:]
            properties.put('files', 'classes,objects')
            properties.put('excludes', "*${File.separator}class1.cls,objects${File.separator}Object1__c.object")
        when:
            Map<String, String> result = filter.getContentParameter(parametersName, properties)
        then:
            result.containsKey('files')
            result.get('files') == 'classes,objects'
            result.containsKey('excludes')
            result.get('excludes') == "*${File.separator}class1.cls,objects${File.separator}Object1__c.object"
    }

    def "Test should return all classes from project path"() {
        given:
            ArrayList<String> parametersName = ['files']
            Map<String, String> properties = [:]
            properties.put('files', 'classes')
        when:
            ArrayList<File> result = filter.getFiles(parametersName, properties)
        then:
            result.sort() == [new File(Paths.get(SRC_PATH, 'classes', 'class1.cls').toString()),
                              new File(Paths.get(SRC_PATH, 'classes', 'class1.cls-meta.xml').toString())].sort()
    }

    def "Test should return the Class1 file" () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_FILES, 'classes/Class1.cls')
            ArrayList<File> filesExpected = []
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls-meta.xml').toString()))
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return the Class1,Class2 files" () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_FILES, 'classes/Class1.cls,classes/Class2.cls')
            ArrayList<File> filesExpected = []
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class2.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class2.cls-meta.xml').toString()))
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all classes with the criteria [files:classes/**] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_FILES, 'classes/**')
            ArrayList<File> filesExpected = []
            filesExpected.addAll(classFiles)
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all classes with the criteria [files:classes/*.cls] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_FILES, 'classes/*.cls')
            ArrayList<File> filesExpected = []
            filesExpected.addAll(classFiles)
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all classes [files:classes]  " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES,Constants.PARAMETER_EXCLUDES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_FILES, 'classes')
            ArrayList<File> filesExpected = []
            filesExpected.addAll(classFiles)
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all classes excluding class 3 and 4 with the criteria [files:classes/*.cls] [classes/Class3.cls,classes/Class4.cls] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES,Constants.PARAMETER_EXCLUDES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_FILES, 'classes/*.cls')
            properties.put(Constants.PARAMETER_EXCLUDES, 'classes/Class3.cls,classes/Class4.cls')
            ArrayList<File> filesExpected = []
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class2.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class2.cls-meta.xml').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class5.cls').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class5.cls-meta.xml').toString()))
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all components [files:] [excludes:] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = []
            Map<String, String> properties = [:]
            ArrayList<File> filesExpected = allFiles.clone()
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all classes excludig Class1.cls [excludes:classes/Class1.cls]  " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES,Constants.PARAMETER_EXCLUDES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_EXCLUDES, 'classes/Class1.cls')
            ArrayList<File> filesExpected = allFiles.clone()
            filesExpected.remove(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls').toString()))
            filesExpected.remove(new File(Paths.get(SRC_PATH,'src_temporary','classes','Class1.cls-meta.xml').toString()))
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return cero components [files:classes/*.cls] [excludes:classes/*.cls]  " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES,Constants.PARAMETER_EXCLUDES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_FILES, 'classes/*.cls')
            properties.put(Constants.PARAMETER_EXCLUDES, 'classes/*.cls')
            ArrayList<File> filesExpected = []
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all components less classes [excludes:triggers,objects]  " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES,Constants.PARAMETER_EXCLUDES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_EXCLUDES, 'triggers,objects')
            ArrayList<File> filesExpected = []
            filesExpected.addAll(classFiles)
            filesExpected.addAll(documentFiles)
            filesExpected.addAll(documentFolder1)
            filesExpected.addAll(documentFolder2)
            filesExpected.addAll(dashboardFiles)
            filesExpected.addAll(dashboardsFolder1)
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all components less classes [excludes:triggers,objects,object,object/Object1__c.object]  " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES,Constants.PARAMETER_EXCLUDES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_EXCLUDES, 'triggers,objects,object/Object1__c.object')
            ArrayList<File> filesExpected = []
            filesExpected.addAll(classFiles)
            filesExpected.addAll(documentFiles)
            filesExpected.addAll(documentFolder1)
            filesExpected.addAll(documentFolder2)
            filesExpected.addAll(dashboardFiles)
            filesExpected.addAll(dashboardsFolder1)
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all components less classes [excludes:triggers,objects,object,object/Object1__c.object] without parameters " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = []
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_EXCLUDES, 'triggers,objects,object/Object1__c.object')
            ArrayList<File> filesExpected = allFiles.clone()
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all components less classes [files:classes] without parameters " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = []
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_FILES, 'classes')
            ArrayList<File> filesExpected = allFiles.clone()
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all documents [files:documents/**] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES,Constants.PARAMETER_EXCLUDES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_FILES, 'documents/**')
            ArrayList<File> filesExpected = []
            filesExpected.addAll(documentFiles)
            filesExpected.addAll(documentFolder1)
            filesExpected.addAll(documentFolder2)
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return  documents in folder documents1 [files:documents/DocumentsFolder1/**] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES,Constants.PARAMETER_EXCLUDES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_FILES, 'documents/DocumentsFolder1/**')
            ArrayList<File> filesExpected = []
            filesExpected.addAll(documentFolder1)
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return  documents in folder documents2 [files:documents/DocumentsFolder2/*.*] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES,Constants.PARAMETER_EXCLUDES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_FILES, 'documents/DocumentsFolder2/*.*')
            ArrayList<File> filesExpected = []
            filesExpected.addAll(documentFolder2)
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return documents in folder documents1 [files:documents/DocumentsFolder1] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES,Constants.PARAMETER_EXCLUDES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_FILES, 'documents/DocumentsFolder1')
            ArrayList<File> filesExpected = []
            filesExpected.addAll(documentFolder1)
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all components less documents in folder documents1 [exclude:documents/DocumentsFolder1/**] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES,Constants.PARAMETER_EXCLUDES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_EXCLUDES, 'documents/DocumentsFolder1/**')
            ArrayList<File> filesExpected = allFiles.clone()
            filesExpected.removeAll(documentFolder1)
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return  all components less documents in folder documents1 [exclude:documents/DocumentsFolder1] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES,Constants.PARAMETER_EXCLUDES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_EXCLUDES, 'documents/DocumentsFolder1')
            ArrayList<File> filesExpected = allFiles.clone()
            filesExpected.removeAll(documentFolder1)
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return  the documentes less documents in folder documents1 [files:documents/DocumentsFolder1/DocumentTest_1_1.txt] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES,Constants.PARAMETER_EXCLUDES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_FILES, 'documents/DocumentsFolder1/DocumentTest_1_1.txt')
            ArrayList<File> filesExpected = []
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder1','DocumentTest_1_1.txt').toString()))
            filesExpected.add(new File(Paths.get(SRC_PATH,'src_temporary','documents','DocumentsFolder1','DocumentTest_1_1.txt-meta.xml').toString()))
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
    }

    def "Test should return all folder documents less documents1 folder  [files:documents/**] [excludes: documents/DocumentsFolder1/**] " () {
        given:
            Filter myFilter = new Filter(project, Paths.get(SRC_PATH, 'src_temporary').toString())
            ArrayList<String> parametersName = [Constants.PARAMETER_FILES,Constants.PARAMETER_EXCLUDES]
            Map<String, String> properties = [:]
            properties.put(Constants.PARAMETER_FILES, 'documents/**')
            properties.put(Constants.PARAMETER_EXCLUDES, 'documents/DocumentsFolder1/**')
            ArrayList<File> filesExpected = []
            filesExpected.addAll(documentFiles)
            filesExpected.addAll(documentFolder2)
        when:
            ArrayList<File> result = myFilter.getFiles(parametersName, properties)
        then:
            result.sort() == filesExpected.sort()
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
            String criterion = "documents${File.separator}myFolder"
        when:
            ArrayList<String> result = myFilter.getCriteria(criterion)
        then:
            result.sort() == ["documents${File.separator}myFolder${File.separator}**"].sort()
    }

    def cleanupSpec() {
        new File(Paths.get(SRC_PATH,'src_temporary').toString()).deleteDir()
    }
}
