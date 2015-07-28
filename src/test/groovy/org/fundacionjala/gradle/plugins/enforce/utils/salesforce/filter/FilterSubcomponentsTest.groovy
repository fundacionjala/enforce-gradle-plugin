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
        filter = FilterSubcomponents.instance
    }

    def ArrayList<File> createVirtualSubcomponents(String type, int quantity) {
        ArrayList<File> files = []
        for(int i = 0; i < quantity; i++) {
            def nameFile = "cmp_" + type + ( i + 1 ) + ".sbc"
            files.add(new File(Paths.get(SRC_PATH, type, nameFile).toString()))
        }
        return files
    }

    def ArrayList<File> createVirtualComponents(String type, int quantity, String extension) {
        ArrayList<File> files = []
        for(int i = 0; i < quantity; i++) {
            def nameFile = "" + type + ( i + 1 ) + "." + extension
            files.add(new File(Paths.get(SRC_PATH, type, nameFile).toString()))
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

    def "Test should return subComponents['fields','validationRules']"() {
        given:
            ArrayList<String> wildCard = ['fields','validationRules']
            ArrayList<Object> expectedComponents =  []
            expectedComponents.add(MetadataComponents.getComponentByFolder("fields"))
            expectedComponents.add(MetadataComponents.getComponentByFolder("validationRules"))
        when:
            ArrayList<Object> resultComponents = filter.listEnabledComponents(wildCard)
        then:
            resultComponents.sort() == expectedComponents.sort()
    }

    def "Test should return all components subComponents['fields','validationRules']"() {
        given:
            ArrayList<String> wildCard = ['*','fields','validationRules']
            ArrayList<Object> expectedComponents =  MetadataComponents.COMPONENT.values()
        when:
            ArrayList<Object> resultComponents = filter.listEnabledComponents(wildCard)
        then:
            resultComponents.sort() == expectedComponents.sort()
    }

    def "Test should return all except ['fields','compactLayouts', 'recordTypes']"() {
        given:
            ArrayList<String> wildCard = ['*','!fields','!compactLayouts', '!recordTypes']
            ArrayList<Object> expectedComponents =  MetadataComponents.COMPONENT.values()
            expectedComponents.remove(MetadataComponents.getComponentByFolder("fields"))
            expectedComponents.remove(MetadataComponents.getComponentByFolder("compactLayouts"))
            expectedComponents.remove(MetadataComponents.getComponentByFolder("recordTypes"))
        when:
            ArrayList<Object> resultComponents = filter.listEnabledComponents(wildCard)
        then:
            resultComponents.sort() == expectedComponents.sort()
    }

    def "Test should return all except ['compactLayouts', 'recordTypes']"() {
        given:
            ArrayList<String> wildCard = ['!compactLayouts', '!recordTypes']
            ArrayList<Object> expectedComponents =  MetadataComponents.COMPONENT.values()
            expectedComponents.remove(MetadataComponents.getComponentByFolder("compactLayouts"))
            expectedComponents.remove(MetadataComponents.getComponentByFolder("recordTypes"))
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
            File file = new File(Paths.get(SRC_PATH, 'fields', "field_1.sbc").toString())
        when:
            FilterSubcomponents.filter([], wildCard, SRC_PATH)
            boolean result = filter.isValid(file)
        then:
            result
    }

    def "Test should return true if exist a valid file with the wildcard [fields] "() {
        given:
            ArrayList<String> wildCard = ['fields']
            File file = new File(Paths.get(SRC_PATH, 'fields', "field_1.sbc").toString())
        when:
            FilterSubcomponents.filter([], wildCard, SRC_PATH)
            boolean result = filter.isValid(file)
        then:
            result
    }

    def "Test should return false if exist a valid file with the wildcard ['*','!validationRules'] "() {
        given:
            ArrayList<String> wildCard = ['*','!validationRules']
            File file = new File(Paths.get(SRC_PATH, 'validationRules', "validation_1.sbc").toString())
        when:
            FilterSubcomponents.filter([], wildCard, SRC_PATH)
            boolean result = filter.isValid(file)
        then:
            !result
    }

    def "Test should return true if exist a valid file with the wildcard ['*','!fields','!compactLayouts', '!recordTypess'] "() {
        given:
            ArrayList<String> wildCard = ['*','!fields','!compactLayouts', '!recordTypess']
            File file = new File(Paths.get(SRC_PATH, 'validationRules', "validation_1.sbc").toString())
        when:
            FilterSubcomponents.filter([], wildCard, SRC_PATH)
            boolean result = filter.isValid(file)
        then:
            result
    }

    def "Test should return true if exist a valid file with the wildcard [] "() {
        given:
            ArrayList<String> wildCard = []
            File file = new File(Paths.get(SRC_PATH, 'validationRules', "validation_1.sbc").toString())
        when:
            FilterSubcomponents.filter([], wildCard, SRC_PATH)
            boolean result = filter.isValid(file)
        then:
            !result
    }

    def "Test should return all files with the wildcard ['*'] "() {
        given:
            ArrayList<File> filesToTest   = createVirtualSubcomponents('fields',5)
            ArrayList<File> expectedFiles = createVirtualSubcomponents('fields',5)
        
            project.enforce.deleteSubComponents = ['*']
            ArrayList<String> wildCard = project.enforce.deleteSubComponents
        when:
            ArrayList<File> resultFiles = FilterSubcomponents.filter(filesToTest, wildCard, SRC_PATH)
        then:
            resultFiles.sort()  == expectedFiles.sort()
    }

    def "Test should return all files with the wildcard ['fields'] "() {
        given:
            ArrayList<File> filesToTest   = createVirtualSubcomponents('fields',5)
            ArrayList<File> expectedFiles = createVirtualSubcomponents('fields',5)

            project.enforce.deleteSubComponents = ['fields']
            ArrayList<String> wildCard = project.enforce.deleteSubComponents
        when:
            ArrayList<File> resultFiles = FilterSubcomponents.filter(filesToTest, wildCard, SRC_PATH)
        then:
            resultFiles.sort()  == expectedFiles.sort()
    }

    def "Test should not return any files with the wildcard ['fields'] "() {
        given:
            ArrayList<File> filesToTest   = createVirtualSubcomponents('validationRules',5)
            ArrayList<File> expectedFiles = []

            project.enforce.deleteSubComponents = ['fields']
            ArrayList<String> wildCard = project.enforce.deleteSubComponents
        when:
            ArrayList<File> resultFiles = FilterSubcomponents.filter(filesToTest, wildCard, SRC_PATH)
        then:
            resultFiles.sort()  == expectedFiles.sort()
    }

    def "Test should return all fields files with the wildcard ['fields'] "() {
        given:
            ArrayList<File> filesToTest   = []
            filesToTest.addAll(createVirtualSubcomponents('fields',5))
            filesToTest.addAll(createVirtualSubcomponents('validationRules',5))
            ArrayList<File> expectedFiles = []
            expectedFiles.addAll(createVirtualSubcomponents('fields',5))

            project.enforce.deleteSubComponents = ['fields']
            ArrayList<String> wildCard = project.enforce.deleteSubComponents
        when:
            ArrayList<File> resultFiles = FilterSubcomponents.filter(filesToTest, wildCard, SRC_PATH)
        then:
            resultFiles.sort()  == expectedFiles.sort()
    }

    def "Test should return all validationRules files with the wildcard ['*','!fields','!compactLayouts', '!recordTypess'] "() {
        given:
            ArrayList<File> filesToTest   = []
            filesToTest.addAll(createVirtualSubcomponents('fields',5))
            filesToTest.addAll(createVirtualSubcomponents('validationRules',5))
            filesToTest.addAll(createVirtualSubcomponents('compactLayouts',5))
            ArrayList<File> expectedFiles = []
            expectedFiles.addAll(createVirtualSubcomponents('validationRules',5))

            project.enforce.deleteSubComponents = ['*','!fields','!compactLayouts', '!recordTypess']
            ArrayList<String> wildCard = project.enforce.deleteSubComponents
        when:
            ArrayList<File> resultFiles = FilterSubcomponents.filter(filesToTest, wildCard, SRC_PATH)
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
            ArrayList<File> resultFiles = FilterSubcomponents.filter(filesToTest, wildCard, SRC_PATH)
        then:
            resultFiles.sort()  == expectedFiles.sort()
    }

    def "Test should not return any files with de wildcard ['fields'] "() {
        given:
            ArrayList<File> filesToTest   = []
            filesToTest.addAll(createVirtualSubcomponents('fields',5))
            filesToTest.addAll(createVirtualSubcomponents('validationRules',5))
            filesToTest.addAll(createVirtualSubcomponents('compactLayouts',5))
            filesToTest.addAll(createVirtualComponents('classes',5,'cls'))
            filesToTest.addAll(createVirtualComponents('triggers',5,'trigger'))

            ArrayList<File> expectedFiles = []
            expectedFiles.addAll(createVirtualSubcomponents('fields',5))
            expectedFiles.addAll(createVirtualComponents('classes',5,'cls'))
            expectedFiles.addAll(createVirtualComponents('triggers',5,'trigger'))

            project.enforce.deleteSubComponents = ['fields']
            ArrayList<String> wildCard = project.enforce.deleteSubComponents
        when:
            ArrayList<File> resultFiles = FilterSubcomponents.filter(filesToTest, wildCard, SRC_PATH)
        then:
            resultFiles.sort()  == expectedFiles.sort()
    }

    def "Test should return all subcomponents,classes and triggers wildcard ['*'] "() {
        given:
            ArrayList<File> filesToTest   = []
            filesToTest.addAll(createVirtualSubcomponents('fields',5))
            filesToTest.addAll(createVirtualSubcomponents('validationRules',5))
            filesToTest.addAll(createVirtualSubcomponents('compactLayouts',5))
            filesToTest.addAll(createVirtualComponents('classes',5,'cls'))
            filesToTest.addAll(createVirtualComponents('triggers',5,'trigger'))

            ArrayList<File> expectedFiles = filesToTest.clone()

            project.enforce.deleteSubComponents = ['*']
            ArrayList<String> wildCard = project.enforce.deleteSubComponents
        when:
            ArrayList<File> resultFiles = FilterSubcomponents.filter(filesToTest, wildCard, SRC_PATH)
        then:
            resultFiles.sort()  == expectedFiles.sort()
    }

    def "Test should  return all subcomponents and object wildcard ['fields','validationRules'] "() {
        given:
            ArrayList<File> filesToTest   = []
            filesToTest.addAll(createVirtualSubcomponents('fields',5))
            filesToTest.addAll(createVirtualSubcomponents('validationRules',5))
            filesToTest.addAll(createVirtualSubcomponents('compactLayouts',5))
            filesToTest.addAll(createVirtualComponents('objects',5,'object'))

            ArrayList<File> expectedFiles = []
            expectedFiles.addAll(createVirtualSubcomponents('fields',5))
            expectedFiles.addAll(createVirtualSubcomponents('validationRules',5))
            expectedFiles.addAll(createVirtualComponents('objects',5,'object'))

            project.enforce.deleteSubComponents = ['fields','validationRules']
            ArrayList<String> wildCard = project.enforce.deleteSubComponents
        when:
            ArrayList<File> resultFiles = FilterSubcomponents.filter(filesToTest, wildCard, SRC_PATH)
        then:
            resultFiles.sort()  == expectedFiles.sort()
    }

    def "Test should  return all subcomponents and reports wildcard ['!fields'] "() {
        given:
            ArrayList<File> filesToTest   = []
            filesToTest.addAll(createVirtualSubcomponents('fields',5))
            filesToTest.addAll(createVirtualSubcomponents('validationRules',5))
            filesToTest.addAll(createVirtualSubcomponents('compactLayouts',5))
            filesToTest.addAll(createVirtualComponents('reports',5,'report'))

            ArrayList<File> expectedFiles = []
            expectedFiles.addAll(createVirtualSubcomponents('validationRules',5))
            expectedFiles.addAll(createVirtualSubcomponents('compactLayouts',5))
            expectedFiles.addAll(createVirtualComponents('reports',5,'report'))

            project.enforce.deleteSubComponents = ['!fields']
            ArrayList<String> wildCard = project.enforce.deleteSubComponents
        when:
            ArrayList<File> resultFiles = FilterSubcomponents.filter(filesToTest, wildCard, SRC_PATH)
        then:
            resultFiles.sort()  == expectedFiles.sort()
    }

    def "Test should  return all subcomponents and documents wildcard [] "() {
        given:
            ArrayList<File> filesToTest   = []
            filesToTest.addAll(createVirtualSubcomponents('fields',5))
            filesToTest.addAll(createVirtualSubcomponents('validationRules',5))
            filesToTest.addAll(createVirtualSubcomponents('compactLayouts',5))
            filesToTest.addAll(createVirtualComponents('documents',5,'txt'))

            ArrayList<File> expectedFiles = []
            expectedFiles.addAll(createVirtualComponents('documents',5,'txt'))

            project.enforce.deleteSubComponents = []
            ArrayList<String> wildCard = project.enforce.deleteSubComponents
        when:
            ArrayList<File> resultFiles = FilterSubcomponents.filter(filesToTest, wildCard, SRC_PATH)
        then:
            resultFiles.sort()  == expectedFiles.sort()
    }
}
