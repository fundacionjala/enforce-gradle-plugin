package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentStates
import org.fundacionjala.gradle.plugins.enforce.filemonitor.FileMonitorSerializer
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ObjectResultTracker
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ResultTracker
import spock.lang.Shared
import spock.lang.Specification

class PackageGeneratorTest extends Specification {
    @Shared
    String ROOT_PATH = System.properties['user.dir']

    @Shared
    String RESOURCE_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/utils/resources"

    def "Test should load a mock of file tracker map"() {
        given:
        PackageGenerator packageGenerator = new PackageGenerator()
        packageGenerator.fileMonitorSerializer = Mock(FileMonitorSerializer)
        packageGenerator.fileMonitorSerializer.verifyFileMap() >> true
        def newFilePath = "classes/File.cls"
        Map<String, ResultTracker> fileTrackerMap = [:]
        fileTrackerMap.put(newFilePath, new ResultTracker(ComponentStates.ADDED.value()))
        packageGenerator.fileMonitorSerializer.getFileTrackerMap(_) >> fileTrackerMap
        when:
        packageGenerator.init('', [])
        then:
        packageGenerator.fileTrackerMap.get(newFilePath).state == ComponentStates.ADDED.value()
    }


    def "Test should build a package from new and changed files"() {
        given:
        PackageGenerator packageGenerator = new PackageGenerator()
        Map<String, ResultTracker> fileTrackerMap = [:]
        def newFilePath1 = "classes/File.cls"
        def newFilePath2 = "classes/Util.cls"
        def newFilePath3 = "objects/ObjectFile.object"
        def newFilePath4 = "objects/ObjectUtil.object"
        fileTrackerMap.put(newFilePath1, new ResultTracker(ComponentStates.ADDED.value()))
        fileTrackerMap.put(newFilePath2, new ResultTracker(ComponentStates.CHANGED.value()))
        fileTrackerMap.put(newFilePath3, new ObjectResultTracker(ComponentStates.ADDED.value()))
        fileTrackerMap.put(newFilePath4, new ObjectResultTracker(ComponentStates.CHANGED.value()))
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
            packageGenerator.fileMonitorSerializer = Mock(FileMonitorSerializer)

            def newFilePathObject1 = "objects/ObjectFile.object"
            def newFilePathObject2 = "objects/ObjectUtil.object"

            Map<String, String> subComponentResult1 = [:]
            subComponentResult1.put("fields/fieldOne",ComponentStates.CHANGED.value())
            subComponentResult1.put("fields/fieldTwo",ComponentStates.ADDED.value())
            subComponentResult1.put("fields/fieldThree",ComponentStates.CHANGED.value())

            ObjectResultTracker objectResultTracker1 = new ObjectResultTracker(ComponentStates.CHANGED.value())
            objectResultTracker1.subComponentsResult = subComponentResult1;

            Map<String, String> subComponentResult2 = [:]
            subComponentResult2.put("fields/fieldFour",ComponentStates.CHANGED.value())
            subComponentResult2.put("fields/fieldFive",ComponentStates.ADDED.value())

            ObjectResultTracker objectResultTracker2 = new ObjectResultTracker(ComponentStates.CHANGED.value())
            objectResultTracker2.subComponentsResult = subComponentResult2

            Map<String, ResultTracker> fileTrackerMap = [:]
            fileTrackerMap.put(newFilePathObject1,objectResultTracker1)
            fileTrackerMap.put(newFilePathObject2,objectResultTracker2)

            packageGenerator.fileTrackerMap = fileTrackerMap

        when:
            def fieldsAdded  = packageGenerator.getSubcomponents(ComponentStates.ADDED)
            def fieldsChanged = packageGenerator.getSubcomponents(ComponentStates.CHANGED)

        then:
            fieldsAdded[0].name ==  "ObjectFile.fieldTwo"
            fieldsAdded[1].name ==  "ObjectUtil.fieldFive"

            fieldsChanged[0].name == "ObjectFile.fieldOne"
            fieldsChanged[1].name == "ObjectFile.fieldThree"
            fieldsChanged[2].name == "ObjectUtil.fieldFour"
    }

    def "Test should build a package for subcomponents"() {
        given:
        PackageGenerator packageGenerator = new PackageGenerator()
        Map<String, ResultTracker> fileTrackerMap = [:]
        def newFilePath1 = "classes/File.cls"
        def newFilePath2 = "classes/Util.cls"
        def newFilePath3 = "objects/ObjectFile.object"
        def newFilePath4 = "objects/ObjectUtil.object"
        fileTrackerMap.put(newFilePath1, new ResultTracker(ComponentStates.ADDED.value()))
        fileTrackerMap.put(newFilePath2, new ResultTracker(ComponentStates.CHANGED.value()))
        Map<String, String> subComponentResult = [:]
        subComponentResult.put("fields/fieldOne",ComponentStates.CHANGED.value())
        subComponentResult.put("fields/fieldTwo",ComponentStates.ADDED.value())
        subComponentResult.put("fields/fieldThree",ComponentStates.CHANGED.value())
        ObjectResultTracker objectResultTracker = new ObjectResultTracker(ComponentStates.ADDED.value())
        objectResultTracker.subComponentsResult = subComponentResult
        fileTrackerMap.put(newFilePath3, objectResultTracker)
        fileTrackerMap.put(newFilePath4, new ObjectResultTracker(ComponentStates.CHANGED.value()))

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

        packageGenerator.packageBuilder.metaPackage.types[2].members[0] == "ObjectFile.fieldTwo"
        packageGenerator.packageBuilder.metaPackage.types[2].members[1] == "ObjectFile.fieldOne"
        packageGenerator.packageBuilder.metaPackage.types[2].members[2] == "ObjectFile.fieldThree"
        packageGenerator.packageBuilder.metaPackage.types[2].name == "CustomField"
    }

}
