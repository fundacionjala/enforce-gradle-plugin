package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager

import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentMonitor
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentStates
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ObjectResultTracker
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ResultTracker
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class PackageGeneratorTest extends Specification {

    @Shared
    Project project

    @Shared
    def SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
            "fundacionjala", "gradle", "plugins","enforce","tasks", "salesforce", "resources").toString()

    @Shared
    Credential credential

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        project.enforce.srcPath = SRC_PATH

        credential = new Credential()
        credential.id = 'id'
        credential.username = 'salesforce2014.test@gmail.com'
        credential.password = '123qwe2014'
        credential.token = 'UO1Jx5vDQl97xCKkwXBH8tg3T'
        credential.loginFormat = LoginType.DEV.value()
        credential.type = 'normal'
    }

    def "Test should build a package from new and changed files"() {
        given:
            PackageGenerator packageGenerator = new PackageGenerator()
            Map<String, ResultTracker> fileTrackerMap = [:]
            def newFilePath1 = "classes/File.cls"
            def newFilePath2 = "classes/Util.cls"
            def newFilePath3 = "objects/ObjectFile.object"
            def newFilePath4 = "objects/ObjectUtil.object"
            fileTrackerMap.put(newFilePath1, new ResultTracker(ComponentStates.ADDED))
            fileTrackerMap.put(newFilePath2, new ResultTracker(ComponentStates.CHANGED))
            fileTrackerMap.put(newFilePath3, new ObjectResultTracker(ComponentStates.ADDED))
            fileTrackerMap.put(newFilePath4, new ObjectResultTracker(ComponentStates.CHANGED))
            packageGenerator.projectPath = SRC_PATH
            packageGenerator.fileTrackerMap = fileTrackerMap
            def stringWriter = new StringWriter()
        when:
            packageGenerator.buildPackage(stringWriter)
        then:
            packageGenerator.packageBuilder.metaPackage.types[0].members[0] == "File"
            packageGenerator.packageBuilder.metaPackage.types[0].members[1] == "Util"
            packageGenerator.packageBuilder.metaPackage.types[0].name == "ApexClass"
            packageGenerator.packageBuilder.metaPackage.types[1].members[0] == "ObjectFile"
            packageGenerator.packageBuilder.metaPackage.types[1].members[1] == "ObjectUtil"
            packageGenerator.packageBuilder.metaPackage.types[1].name == "CustomObject"
    }

    def "Test should get the subcomponentes according to status field"() {
        given:
            PackageGenerator packageGenerator = new PackageGenerator()
            packageGenerator.componentMonitor = Mock(ComponentMonitor)

            def newFilePathObject1 = "objects/ObjectFile.object"
            def newFilePathObject2 = "objects/ObjectUtil.object"

            Map<String, ComponentStates> subComponentResult1 = [:]
            subComponentResult1.put("fields/fieldOne",ComponentStates.CHANGED)
            subComponentResult1.put("fields/fieldTwo",ComponentStates.ADDED)
            subComponentResult1.put("fields/fieldThree",ComponentStates.CHANGED)

            ObjectResultTracker objectResultTracker1 = new ObjectResultTracker(ComponentStates.CHANGED)
            objectResultTracker1.subComponentsResult = subComponentResult1;

            Map<String, ComponentStates> subComponentResult2 = [:]
            subComponentResult2.put("fields/fieldFour",ComponentStates.CHANGED)
            subComponentResult2.put("fields/fieldFive",ComponentStates.ADDED)

            ObjectResultTracker objectResultTracker2 = new ObjectResultTracker(ComponentStates.CHANGED)
            objectResultTracker2.subComponentsResult = subComponentResult2

            Map<String, ResultTracker> fileTrackerMap = [:]
            fileTrackerMap.put(newFilePathObject1,objectResultTracker1)
            fileTrackerMap.put(newFilePathObject2,objectResultTracker2)
            packageGenerator.projectPath = SRC_PATH
            packageGenerator.fileTrackerMap = fileTrackerMap

        when:
            def fieldsAdded  = packageGenerator.getSubComponents(ComponentStates.ADDED)
            def fieldsChanged = packageGenerator.getSubComponents(ComponentStates.CHANGED)

        then:
            fieldsAdded[0].name ==  "ObjectFile.fieldTwo.sbc"
            fieldsAdded[1].name ==  "ObjectUtil.fieldFive.sbc"

            fieldsChanged[0].name == "ObjectFile.fieldOne.sbc"
            fieldsChanged[1].name == "ObjectFile.fieldThree.sbc"
            fieldsChanged[2].name == "ObjectUtil.fieldFour.sbc"
    }

    def "Test should build a package for subcomponents"() {
        given:
            PackageGenerator packageGenerator = new PackageGenerator()
            Map<String, ResultTracker> fileTrackerMap = [:]
            def newFilePathAdded = "classes/File.cls"
            def newFilePathAdded2 = "classes/Util.cls"
            def newObjectPathChanged = "objects/ObjectFile.object"
            def newObjectPathAdded   = "objects/ObjectUtil.object"
            fileTrackerMap.put(newFilePathAdded, new ResultTracker(ComponentStates.ADDED))
            fileTrackerMap.put(newFilePathAdded2, new ResultTracker(ComponentStates.CHANGED))

            Map<String, ComponentStates> subComponentResult = [:]
            subComponentResult.put("fields/fieldOne",ComponentStates.CHANGED)
            subComponentResult.put("fields/fieldTwo",ComponentStates.ADDED)
            subComponentResult.put("fields/fieldThree",ComponentStates.CHANGED)

            ObjectResultTracker objectResultTrackerChanged = new ObjectResultTracker(ComponentStates.CHANGED)
            objectResultTrackerChanged.subComponentsResult = subComponentResult
            fileTrackerMap.put(newObjectPathChanged, objectResultTrackerChanged)
            fileTrackerMap.put(newObjectPathAdded  , new ObjectResultTracker(ComponentStates.ADDED))
            packageGenerator.projectPath = SRC_PATH
            packageGenerator.fileTrackerMap = fileTrackerMap
            def stringWriter = new StringWriter()

        when:
            packageGenerator.buildPackage(stringWriter)

        then:
            packageGenerator.packageBuilder.metaPackage.types[0].members[0] == "File"
            packageGenerator.packageBuilder.metaPackage.types[0].members[1] == "Util"
            packageGenerator.packageBuilder.metaPackage.types[0].name == "ApexClass"

            packageGenerator.packageBuilder.metaPackage.types[1].members[0] == "ObjectFile.fieldOne"
            packageGenerator.packageBuilder.metaPackage.types[1].members[1] == "ObjectFile.fieldThree"
            packageGenerator.packageBuilder.metaPackage.types[1].members[2] == "ObjectFile.fieldTwo"
            packageGenerator.packageBuilder.metaPackage.types[1].name == "CustomField"


            packageGenerator.packageBuilder.metaPackage.types[2].members[0] == "ObjectFile"
            packageGenerator.packageBuilder.metaPackage.types[2].members[1] == "ObjectUtil"
            packageGenerator.packageBuilder.metaPackage.types[2].name == "CustomObject"
    }

    def "Test should build a package from deleted files"() {
        given:
            PackageGenerator packageGenerator = new PackageGenerator()

            Map<String, ComponentStates> subComponentResult = [:]
            subComponentResult.put("fields/fieldOne",ComponentStates.DELETED)
            subComponentResult.put("fields/fieldTwo",ComponentStates.DELETED)

            ObjectResultTracker objectResultTracker = new ObjectResultTracker(ComponentStates.CHANGED);
            objectResultTracker.subComponentsResult = subComponentResult;

            def newClassPath2 = "classes/Class2.cls"
            def newClassPath3 = "classes/Class3.cls"
            def newClassPath4 = "classes/Class4.cls"
            def newFieldPath1 = "fields/Object1__c.Field1__c.sbc"
            def newFieldPath2 = "fields/Object1__c.Field2__c.sbc"
            def newFieldPath3 = "fields/Object1__c.Field4__c.sbc"
            def newFieldPath4 = "fields/Object1__c.Field5__c.sbc"
            def newObjectPath1 = "objects/Object1__c"
            def newObjectPath2 = "objects/Object2__c"
            def newObjectPath3 = "objects/Object3__c"

            Map<String, ResultTracker> fileTrackerMap = [:]
            fileTrackerMap.put(newClassPath2, new ResultTracker(ComponentStates.DELETED))
            fileTrackerMap.put(newClassPath3, new ResultTracker(ComponentStates.DELETED))
            fileTrackerMap.put(newClassPath4, new ResultTracker(ComponentStates.DELETED))
            fileTrackerMap.put(newFieldPath1, new ResultTracker(ComponentStates.DELETED))
            fileTrackerMap.put(newFieldPath2, new ResultTracker(ComponentStates.DELETED))
            fileTrackerMap.put(newFieldPath3, new ResultTracker(ComponentStates.DELETED))
            fileTrackerMap.put(newFieldPath4, new ResultTracker(ComponentStates.DELETED))
            fileTrackerMap.put(newObjectPath1, new ResultTracker(ComponentStates.DELETED))
            fileTrackerMap.put(newObjectPath2, new ResultTracker(ComponentStates.DELETED))
            fileTrackerMap.put(newObjectPath3, new ResultTracker(ComponentStates.DELETED))

            packageGenerator.projectPath = SRC_PATH
            packageGenerator.credential = credential

        when:
            packageGenerator.init(SRC_PATH, [], credential, project)
            packageGenerator.fileTrackerMap = fileTrackerMap
            packageGenerator.buildDestructive(new StringWriter())

        then:
            packageGenerator.packageBuilder.metaPackage.types[0].members[0] == "Class2"
            packageGenerator.packageBuilder.metaPackage.types[0].name == "ApexClass"

            packageGenerator.packageBuilder.metaPackage.types[1].members[0] == "Object1__c.Field1__c"
            packageGenerator.packageBuilder.metaPackage.types[1].name == "CustomField"

            packageGenerator.packageBuilder.metaPackage.types[2].members[0] == "Object1__c"
            packageGenerator.packageBuilder.metaPackage.types[2].members[1] == "Object2__c"
            packageGenerator.packageBuilder.metaPackage.types[2].members[2] == "Object3__c"
            packageGenerator.packageBuilder.metaPackage.types[2].name == "CustomObject"
    }

    def "Test should build a package from deleted report files"() {
        given:
            PackageGenerator packageGenerator = new PackageGenerator()

            def newObjectPathDeleted = "reports/MyReportFolder/Report1.report"
            Map<String, ResultTracker> fileTrackerMap = [:]
            fileTrackerMap.put(newObjectPathDeleted, new ResultTracker(ComponentStates.DELETED))
            packageGenerator.projectPath = SRC_PATH
            packageGenerator.credential = credential

        when:
            packageGenerator.init(SRC_PATH, [], credential, project)
            packageGenerator.fileTrackerMap = fileTrackerMap
            packageGenerator.buildDestructive(new StringWriter())

        then:
            packageGenerator.packageBuilder.metaPackage.types[0].members[0] == "MyReportFolder/Report1"
            packageGenerator.packageBuilder.metaPackage.types[0].name == "Report"
    }

    def "Test should exclude a file called Class1.cls from fileTrackerMap"() {
        given:
            PackageGenerator packageGenerator = new PackageGenerator()
            Map<String, ResultTracker> fileTrackerMap = ['classes/Class1.cls': new ResultTracker(ComponentStates.ADDED),
                                                         'classes/Class2.cls': new ResultTracker(ComponentStates.CHANGED)]
            packageGenerator.fileTrackerMap = fileTrackerMap
            ArrayList<File> files = [new File('classes/Class2.cls')]
        when:
            packageGenerator.updateFileTracker(files)
        then:
            packageGenerator.fileTrackerMap['classes/Class2.cls'].state == ComponentStates.CHANGED
    }
}
