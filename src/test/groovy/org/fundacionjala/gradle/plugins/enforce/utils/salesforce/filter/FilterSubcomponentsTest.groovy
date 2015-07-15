package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.filter

import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class FilterSubcomponentsTest extends Specification{

    @Shared
    String SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
            "fundacionjala", "gradle", "plugins","enforce","tasks", "salesforce", "resources").toString()
    @Shared
    Project project

    @Shared
    FilterSubcomponents filter

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        filter = FilterSubcomponents.getFilter()
    }

    def ArrayList<File> createVirtualSubcomponents(String type, int quantity) {
        ArrayList<File> files = []
        for(int i = 0; i < quantity; i++) {
            def nameFile = "cmp_" + type + ( i + 1 ) + ".sbc"
            files.add(new File(Paths.get(SRC_PATH,'src', type, nameFile).toString()))
        }
        return files
    }

    def "Test should return all Components"() {
        given:
            ArrayList<String> wildCard = ['*']
            ArrayList<Object> expectedComponents =  MetadataComponents.COMPONENT.values()
        when:
            ArrayList<Object> resultComponents = filter.listEnabledComponents(wildCard)
        then:
            resultComponents.sort() == expectedComponents.sort()
    }

    def "Test should return subComponents['CustomField','ValidationRule']"() {
        given:
            ArrayList<String> wildCard = ['CustomField','ValidationRule']
            ArrayList<Object> expectedComponents =  []
            expectedComponents.add(MetadataComponents.getComponentByName("CustomField"))
            expectedComponents.add(MetadataComponents.getComponentByName("ValidationRule"))
        when:
            ArrayList<Object> resultComponents = filter.listEnabledComponents(wildCard)
        then:
            resultComponents.sort() == expectedComponents.sort()
    }

    def "Test should return all components subComponents['CustomField','ValidationRule']"() {
        given:
            ArrayList<String> wildCard = ['*','CustomField','ValidationRule']
            ArrayList<Object> expectedComponents =  MetadataComponents.COMPONENT.values()
        when:
            ArrayList<Object> resultComponents = filter.listEnabledComponents(wildCard)
        then:
            resultComponents.sort() == expectedComponents.sort()
    }

    def "Test should return all except ['CustomField','CompactLayout', 'RecordType']"() {
        given:
            ArrayList<String> wildCard = ['*','!CustomField','!CompactLayout', '!RecordType']
            ArrayList<Object> expectedComponents =  MetadataComponents.COMPONENT.values()
            expectedComponents.remove(MetadataComponents.getComponentByName("CustomField"))
            expectedComponents.remove(MetadataComponents.getComponentByName("CompactLayout"))
            expectedComponents.remove(MetadataComponents.getComponentByName("RecordType"))
        when:
            ArrayList<Object> resultComponents = filter.listEnabledComponents(wildCard)
        then:
            resultComponents.sort() == expectedComponents.sort()
    }

    def "Test should return all except ['CompactLayout', 'RecordType']"() {
        given:
            ArrayList<String> wildCard = ['!CompactLayout', '!RecordType']
            ArrayList<Object> expectedComponents =  MetadataComponents.COMPONENT.values()
            expectedComponents.remove(MetadataComponents.getComponentByName("CompactLayout"))
            expectedComponents.remove(MetadataComponents.getComponentByName("RecordType"))
        when:
            ArrayList<Object> resultComponents = filter.listEnabledComponents(wildCard)
        then:
            resultComponents.sort() == expectedComponents.sort()
    }

    def "Test should return a Clean ArrayList"() {
        given:
            ArrayList<String> wildCard = []
            ArrayList<Object> expectedComponents =  []
        when:
            ArrayList<Object> resultComponents = filter.listEnabledComponents(wildCard)
        then:
            resultComponents.sort() == expectedComponents.sort()
    }

    def "Test should return true if exist a valid file with the wildcard [*] "() {
        given:
            ArrayList<String> wildCard = ['*']
            File file = new File(Paths.get(SRC_PATH,'src', 'fields', "field_1.sbc").toString())
        when:
            FilterSubcomponents.filter([], wildCard)
            boolean result = filter.isValid(file)
        then:
            result == true
    }

    def "Test should return true if exist a valid file with the wildcard [CustomField] "() {
        given:
            ArrayList<String> wildCard = ['CustomField']
            File file = new File(Paths.get(SRC_PATH,'src', 'fields', "field_1.sbc").toString())
        when:
            FilterSubcomponents.filter([], wildCard)
            boolean result = filter.isValid(file)
        then:
            result == true
    }

    def "Test should return false if exist a valid file with the wildcard ['*','!ValidationRule'] "() {
        given:
            ArrayList<String> wildCard = ['*','!ValidationRule']
            File file = new File(Paths.get(SRC_PATH,'src', 'validationRules', "validation_1.sbc").toString())
        when:
            FilterSubcomponents.filter([], wildCard)
            boolean result = filter.isValid(file)
        then:
            result == false
    }

    def "Test should return true if exist a valid file with the wildcard ['*','!CustomField','!CompactLayout', '!RecordType'] "() {
        given:
            ArrayList<String> wildCard = ['*','!CustomField','!CompactLayout', '!RecordType']
            File file = new File(Paths.get(SRC_PATH,'src', 'validationRules', "validation_1.sbc").toString())
        when:
            FilterSubcomponents.filter([], wildCard)
            boolean result = filter.isValid(file)
        then:
            result == true
    }

    def "Test should return true if exist a valid file with the wildcard [] "() {
        given:
            ArrayList<String> wildCard = []
            File file = new File(Paths.get(SRC_PATH,'src', 'validationRules', "validation_1.sbc").toString())
        when:
            FilterSubcomponents.filter([], wildCard)
            boolean result = filter.isValid(file)
        then:
            result == false
    }

    def "Test should return all files with the wildcard ['*'] "() {
        given:
            ArrayList<File> filesToTest   = createVirtualSubcomponents('fields',5)
            ArrayList<File> expectedFiles = createVirtualSubcomponents('fields',5)
        
            project.enforce.deleteSubComponents = ['*']
            ArrayList<String> wildCard = project.enforce.deleteSubComponents
        when:
            ArrayList<File> resultFiles = FilterSubcomponents.filter(filesToTest, wildCard)
        then:
            resultFiles.sort()  == expectedFiles.sort()
    }

    def "Test should return all files with the wildcard ['CustomField'] "() {
        given:
            ArrayList<File> filesToTest   = createVirtualSubcomponents('fields',5)
            ArrayList<File> expectedFiles = createVirtualSubcomponents('fields',5)

            project.enforce.deleteSubComponents = ['CustomField']
            ArrayList<String> wildCard = project.enforce.deleteSubComponents
        when:
            ArrayList<File> resultFiles = FilterSubcomponents.filter(filesToTest, wildCard)
        then:
            resultFiles.sort()  == expectedFiles.sort()
    }

    def "Test should not return any files with the wildcard ['CustomField'] "() {
        given:
            ArrayList<File> filesToTest   = createVirtualSubcomponents('validationRules',5)
            ArrayList<File> expectedFiles = []

            project.enforce.deleteSubComponents = ['CustomField']
            ArrayList<String> wildCard = project.enforce.deleteSubComponents
        when:
            ArrayList<File> resultFiles = FilterSubcomponents.filter(filesToTest, wildCard)
        then:
            resultFiles.sort()  == expectedFiles.sort()
    }

    def "Test should return all CustomField files with the wildcard ['CustomField'] "() {
        given:
            ArrayList<File> filesToTest   = []
            filesToTest.addAll(createVirtualSubcomponents('fields',5))
            filesToTest.addAll(createVirtualSubcomponents('validationRules',5))
            ArrayList<File> expectedFiles = []
            expectedFiles.addAll(createVirtualSubcomponents('fields',5))

            project.enforce.deleteSubComponents = ['CustomField']
            ArrayList<String> wildCard = project.enforce.deleteSubComponents
        when:
            ArrayList<File> resultFiles = FilterSubcomponents.filter(filesToTest, wildCard)
        then:
            resultFiles.sort()  == expectedFiles.sort()
    }

    def "Test should return all ValidationRules files with the wildcard ['*','!CustomField','!CompactLayout', '!RecordType'] "() {
        given:
            ArrayList<File> filesToTest   = []
            filesToTest.addAll(createVirtualSubcomponents('fields',5))
            filesToTest.addAll(createVirtualSubcomponents('validationRules',5))
            filesToTest.addAll(createVirtualSubcomponents('compactLayouts',5))
            ArrayList<File> expectedFiles = []
            expectedFiles.addAll(createVirtualSubcomponents('validationRules',5))

            project.enforce.deleteSubComponents = ['*','!CustomField','!CompactLayout', '!RecordType']
            ArrayList<String> wildCard = project.enforce.deleteSubComponents
        when:
            ArrayList<File> resultFiles = FilterSubcomponents.filter(filesToTest, wildCard)
        then:
            resultFiles.sort()  == expectedFiles.sort()
    }

    def "Test should not return any files with de wildcard [] "() {
        given:
            ArrayList<File> filesToTest   = []
            filesToTest.addAll(createVirtualSubcomponents('fields',5))
            filesToTest.addAll(createVirtualSubcomponents('validationRules',5))
            filesToTest.addAll(createVirtualSubcomponents('compactLayouts',5))
            ArrayList<File> expectedFiles = []

            project.enforce.deleteSubComponents = []
            ArrayList<String> wildCard = project.enforce.deleteSubComponents
        when:
            ArrayList<File> resultFiles = FilterSubcomponents.filter(filesToTest, wildCard)
        then:
            resultFiles.sort()  == expectedFiles.sort()
    }
}
