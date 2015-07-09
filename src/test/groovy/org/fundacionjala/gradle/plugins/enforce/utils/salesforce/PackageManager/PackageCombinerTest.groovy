package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager.PackageCombiner
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class PackageCombinerTest extends Specification {

    @Shared
        def SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
                "fundacionjala", "gradle", "plugins","enforce","utils", "salesforce").toString()
    @Shared
        PackageCombiner packageCombiner

    def setup() {
        packageCombiner = new PackageCombiner()
    }

    def 'Test should combine two packages from project directory package and build directory package'() {
        given:
            String projectPackagePath = Paths.get(SRC_PATH, 'projectPackage.xml')
            String buildPackagePath = Paths.get(SRC_PATH, 'buildPackage.xml')
            String projectPackageContent = '''<?xml version='1.0' encoding='UTF-8'?>
                    <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                        <types>
                            <members>Object1__c.Field1</members>
                            <members>Object2__c.Field2</members>
                            <name>CustomField</name>
                        </types>
                        <version>32.0</version>
                    </Package>
                    '''
            String buildPackageContent ='''<?xml version='1.0' encoding='UTF-8'?>
                    <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                        <types>
                            <members>Object1__c</members>
                            <name>CustomObject</name>
                        </types>
                    <version>32.0</version></Package>
                    '''
            File projectPackageFile = new File(projectPackagePath)
            projectPackageFile.write(projectPackageContent)
            File buildPackageFile = new File(buildPackagePath)
            buildPackageFile.write(buildPackageContent)
        when:
            PackageCombiner.packageCombine(projectPackagePath, buildPackagePath)
            XMLUnit.ignoreWhitespace = true
            def xmlDiff = new Diff(buildPackageFile.text, projectPackageContent)
        then:
            new File(Paths.get(SRC_PATH, 'buildPackage.xml').toString()).exists()
            xmlDiff.similar()
    }

    def 'Test should combine two packages from project directory package and build directory package without *'() {
        given:
            String projectPackagePath = Paths.get(SRC_PATH, 'projectPackage.xml')
            String buildPackagePath = Paths.get(SRC_PATH, 'buildPackage.xml')
            String projectPackageContent = '''<?xml version='1.0' encoding='UTF-8'?>
                        <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                            <types>
                                <members>Object1__c.Field1</members>
                                <members>Object2__c.Field2</members>
                                <name>CustomField</name>
                            </types>
                            <types>
                                <members>*</members>
                                <name>ApexClass</name>
                            </types>
                            <version>32.0</version>
                        </Package>
                        '''
            String buildPackageContent ='''<?xml version='1.0' encoding='UTF-8'?>
                        <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                            <types>
                                <members>Object1__c</members>
                                <name>CustomObject</name>
                            </types>
                            <types>
                                <members>Class1</members>
                                <name>ApexClass</name>
                            </types>
                        <version>32.0</version></Package>
                        '''
            File projectPackageFile = new File(projectPackagePath)
            projectPackageFile.write(projectPackageContent)
            File buildPackageFile = new File(buildPackagePath)
            buildPackageFile.write(buildPackageContent)
            String packageContentExpect = '''<?xml version='1.0' encoding='UTF-8'?>
                        <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                            <types>
                                <members>Class1</members>
                                <name>ApexClass</name>
                            </types>
                            <types>
                                <members>Object1__c.Field1</members>
                                <members>Object2__c.Field2</members>
                                <name>CustomField</name>
                            </types>
                            <version>32.0</version>
                        </Package>
                        '''
        when:
            PackageCombiner.packageCombine(projectPackagePath, buildPackagePath)
            XMLUnit.ignoreWhitespace = true
            def xmlDiff = new Diff(buildPackageFile.text, packageContentExpect)
        then:
            new File(Paths.get(SRC_PATH, 'buildPackage.xml').toString()).exists()
            xmlDiff.similar()
    }

    def 'Test should combine two packages from project directory package and build directory package with documents'() {
        given:
            String projectPackagePath = Paths.get(SRC_PATH, 'projectPackage.xml')
            String buildPackagePath = Paths.get(SRC_PATH, 'buildPackage.xml')
            String projectPackageContent = '''<?xml version='1.0' encoding='UTF-8'?>
                            <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                <types>
                                    <members>myFolder</members>
                                    <members>myFolder/myFirstFile.txt</members>
                                    <members>myFolder/mySecondFile.txt</members>
                                    <name>Document</name>
                                </types>
                                <version>32.0</version>
                            </Package>
                            '''
            String buildPackageContent ='''<?xml version='1.0' encoding='UTF-8'?>
                            <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                <types>
                                    <members>myFolder/myFirstFile</members>
                                    <members>myFolder/mySecondFile</members>
                                    <name>Document</name>
                                </types>
                            <version>32.0</version></Package>
                            '''
            File projectPackageFile = new File(projectPackagePath)
            projectPackageFile.write(projectPackageContent)
            File buildPackageFile = new File(buildPackagePath)
            buildPackageFile.write(buildPackageContent)
            String packageContentExpect = '''<?xml version='1.0' encoding='UTF-8'?>
                            <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                <types>
                                    <members>myFolder</members>
                                    <members>myFolder/myFirstFile.txt</members>
                                    <members>myFolder/mySecondFile.txt</members>
                                    <name>Document</name>
                                </types>
                                <version>32.0</version>
                            </Package>
                            '''
        when:
            PackageCombiner.packageCombine(projectPackagePath, buildPackagePath)
            XMLUnit.ignoreWhitespace = true
            def xmlDiff = new Diff(buildPackageFile.text, packageContentExpect)
        then:
            new File(Paths.get(SRC_PATH, 'buildPackage.xml').toString()).exists()
            xmlDiff.similar()
    }

    def 'Test should remove components from package xml file'() {
        given:
            String packagePath = Paths.get(SRC_PATH, 'packageTest.xml')

            String packageContent = '''<?xml version='1.0' encoding='UTF-8'?>
                        <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                            <types>
                                <members>Object1__c</members>
                                <members>Object2__c</members>
                                <name>CustomObject</name>
                            </types>
                            <version>32.0</version>
                        </Package>
                        '''
            File packageFile = new File(packagePath)
            packageFile.write(packageContent)
            String packageExpect = '''<?xml version='1.0' encoding='UTF-8'?>
                            <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                <types>
                                    <members>Object2__c</members>
                                    <name>CustomObject</name>
                                </types>
                                <version>32.0</version>
                            </Package>
                            '''
        when:
            PackageCombiner.removeMembersFromPackage(packagePath, ['objects/Object1__c.object'])
            XMLUnit.ignoreWhitespace = true
            def xmlDiff = new Diff(packageFile.text, packageExpect)
        then:
            xmlDiff.similar()
    }

    def 'Test should remove documents component from package xml file'() {
        given:
            String packagePath = Paths.get(SRC_PATH, 'packageTestDocument.xml')
            String packageContent = '''<?xml version='1.0' encoding='UTF-8'?>
                            <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                <types>
                                    <members>MyDocuments</members>
                                    <members>MyDocuments/doc2.txt</members>
                                    <members>MyDocuments/doc1.txt</members>
                                    <name>Document</name>
                                </types>
                                <version>32.0</version>
                            </Package>
                            '''
            File packageFile = new File(packagePath)
            packageFile.write(packageContent)
            String packageExpect = '''<?xml version='1.0' encoding='UTF-8'?>
                                <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                    <types>
                                        <members>MyDocuments/doc1.txt</members>
                                        <name>Document</name>
                                    </types>
                                    <version>32.0</version>
                                </Package>
                                '''
        when:
            PackageCombiner.removeMembersFromPackage(packagePath, ['documents/MyDocuments/doc2.txt'])
            XMLUnit.ignoreWhitespace = true
            def xmlDiff = new Diff(packageFile.text, packageExpect)
        then:
            xmlDiff.similar()
    }

    def "Test should remove documents that doesn't have extension at package xml file"() {
        given:
            String packagePath = Paths.get(SRC_PATH, 'packageTestDocument.xml')
            String packageContent = '''<?xml version='1.0' encoding='UTF-8'?>
                                <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                    <types>
                                        <members>MyDocuments</members>
                                        <members>MyDocuments/doc2</members>
                                        <members>MyDocuments/doc1</members>
                                        <name>Document</name>
                                    </types>
                                    <version>32.0</version>
                                </Package>
                                '''
            File packageFile = new File(packagePath)
            packageFile.write(packageContent)
            String packageExpect = '''<?xml version='1.0' encoding='UTF-8'?>
                                    <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                        <types>
                                            <members>MyDocuments/doc1</members>
                                            <name>Document</name>
                                        </types>
                                        <version>32.0</version>
                                    </Package>
                                    '''
        when:
            PackageCombiner.removeMembersFromPackage(packagePath, ['documents/MyDocuments/doc2.txt'])
            XMLUnit.ignoreWhitespace = true
            def xmlDiff = new Diff(packageFile.text, packageExpect)
        then:
            xmlDiff.similar()
    }

    def 'Test should remove reports component from package xml file'() {
        given:
            String packagePath = Paths.get(SRC_PATH, 'packageTestReport.xml')
            String packageContent = '''<?xml version='1.0' encoding='UTF-8'?>
                                <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                    <types>
                                        <members>testFolder/testReport.report</members>
                                        <name>Report</name>
                                    </types>
                                    <version>32.0</version>
                                </Package>
                                '''
            File packageFile = new File(packagePath)
            packageFile.write(packageContent)
            String packageExpect = '''<?xml version='1.0' encoding='UTF-8'?>
                                    <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                        <version>32.0</version>
                                    </Package>
                                    '''
        when:
            PackageCombiner.removeMembersFromPackage(packagePath, ['reports/testFolder/testReport.report'])
            XMLUnit.ignoreWhitespace = true
            def xmlDiff = new Diff(packageFile.text, packageExpect)
        then:
            xmlDiff.similar()
    }

    def 'Test should remove CustomField subcomponent from package xml file'() {
        given:
            String packagePath = Paths.get(SRC_PATH, 'packageTestField.xml')
            String packageContent = '''<?xml version='1.0' encoding='UTF-8'?>
                                    <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                        <types>
                                            <members>Object1__c.MyCustomField1__c</members>
                                            <members>Object1__c.MyCustomField2__c</members>
                                            <members>Object2__c.MyCustomField2__c</members>
                                            <name>CustomField</name>
                                        </types>
                                        <types>
                                            <members>Object1__c.MyFieldSet1__c</members>
                                            <members>Object2__c.MyFieldSet2__c</members>
                                            <name>FieldSet</name>
                                        </types>
                                        <version>32.0</version>
                                    </Package>
                                    '''
            File packageFile = new File(packagePath)
            packageFile.write(packageContent)
            String packageExpect = '''<?xml version='1.0' encoding='UTF-8'?>
                                        <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                            <types>
                                                <members>Object2__c.MyCustomField2__c</members>
                                                <name>CustomField</name>
                                            </types>
                                            <types>
                                                <members>Object2__c.MyFieldSet2__c</members>
                                                <name>FieldSet</name>
                                            </types>
                                            <version>32.0</version>
                                        </Package>
                                        '''
        when:
            PackageCombiner.removeMembersFromPackage(packagePath, ['objects/Object1__c.object'])
            XMLUnit.ignoreWhitespace = true
            def xmlDiff = new Diff(packageFile.text, packageExpect)
        then:
            xmlDiff.similar()
    }

    def 'Test should take in account only fields of objects '() {
        given:
            String projectPackagePath = Paths.get(SRC_PATH, 'projectPackage.xml')
            String buildPackagePath = Paths.get(SRC_PATH, 'buildPackage.xml')
            String projectPackageContent = '''<?xml version='1.0' encoding='UTF-8'?>
                                <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                    <types>
                                        <members>Object1__c.MyCustomField1</members>
                                        <members>Object2__c.MyCustomField2</members>
                                        <members>Object3__c.MyCustomField3</members>
                                        <name>CustomField</name>
                                    </types>
                                    <types>
                                        <members>Object1__c.MyFieldSet1</members>
                                        <members>Object2__c.MyFieldSet2</members>
                                        <members>Object3__c.MyFieldSet3</members>
                                        <name>FieldSet</name>
                                    </types>
                                    <types>
                                        <members>Class1</members>
                                        <members>Class2</members>
                                        <members>Class3</members>
                                        <name>ApexClass</name>
                                    </types>
                                    <types>
                                        <members>Object4__c</members>
                                        <name>CustomObject</name>
                                    </types>
                                    <version>32.0</version>
                                </Package>
                                '''
            String buildPackageContent ='''<?xml version='1.0' encoding='UTF-8'?>
                                <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                    <types>
                                        <members>Object1__c</members>
                                        <members>Object4__c</members>
                                        <name>CustomObject</name>
                                    </types>
                                    <types>
                                        <members>Class1</members>
                                        <name>ApexClass</name>
                                    </types>
                                <version>32.0</version></Package>
                                '''
            File projectPackageFile = new File(projectPackagePath)
            projectPackageFile.write(projectPackageContent)
            File buildPackageFile = new File(buildPackagePath)
            buildPackageFile.write(buildPackageContent)
            String packageContentExpect = '''<?xml version='1.0' encoding='UTF-8'?>
                                <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                    <types>
                                        <members>Object4__c</members>
                                        <name>CustomObject</name>
                                    </types>
                                    <types>
                                        <members>Class1</members>
                                        <name>ApexClass</name>
                                    </types>
                                    <types>
                                        <members>Object1__c.MyCustomField1</members>
                                        <name>CustomField</name>
                                    </types>
                                    <types>
                                        <members>Object1__c.MyFieldSet1</members>
                                        <name>FieldSet</name>
                                    </types>
                                    <version>32.0</version>
                                </Package>
                                '''
        when:
            PackageCombiner.packageCombineToUpdate(projectPackagePath, buildPackagePath)
            XMLUnit.ignoreWhitespace = true
            def xmlDiff = new Diff(buildPackageFile.text, packageContentExpect)
        then:
            new File(Paths.get(SRC_PATH, 'buildPackage.xml').toString()).exists()
            xmlDiff.similar()
    }

    def 'Test should take in account documents with their folders name as member into package xml file '() {
        given:
            String projectPackagePath = Paths.get(SRC_PATH, 'projectPackage.xml')
            String buildPackagePath = Paths.get(SRC_PATH, 'package.xml')
            String projectPackageContent = '''<?xml version='1.0' encoding='UTF-8'?>
                                    <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                        <types>
                                            <members>Object1__c.MyCustomField1</members>
                                            <members>Object2__c.MyCustomField2</members>
                                            <members>Object3__c.MyCustomField3</members>
                                            <name>CustomField</name>
                                        </types>
                                        <types>
                                            <members>MyDocuments</members>
                                            <members>MyDocuments/doc2.txt</members>
                                            <members>MyDocuments/doc1.txt</members>
                                            <name>Document</name>
                                        </types>
                                        <types>
                                            <members>MyReports</members>
                                            <members>MyReports/reportTest</members>
                                            <name>Report</name>
                                        </types>
                                        <types>
                                            <members>Object1__c.MyFieldSet1</members>
                                            <members>Object2__c.MyFieldSet2</members>
                                            <members>Object3__c.MyFieldSet3</members>
                                            <name>FieldSet</name>
                                        </types>
                                        <types>
                                            <members>Class1</members>
                                            <members>Class2</members>
                                            <members>Class3</members>
                                            <name>ApexClass</name>
                                        </types>
                                        <types>
                                            <members>Object4__c</members>
                                            <name>CustomObject</name>
                                        </types>
                                        <version>32.0</version>
                                    </Package>
                                    '''
            String buildPackageContent ='''<?xml version='1.0' encoding='UTF-8'?>
                                    <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                        <types>
                                            <members>Object1__c</members>
                                            <members>Object4__c</members>
                                            <name>CustomObject</name>
                                        </types>
                                        <types>
                                            <members>MyDocuments/doc2.txt</members>
                                            <members>MyDocuments/doc1.txt</members>
                                            <name>Document</name>
                                        </types>
                                        <types>
                                            <members>Class1</members>
                                            <name>ApexClass</name>
                                        </types>
                                    <version>32.0</version></Package>
                                    '''
            File projectPackageFile = new File(projectPackagePath)
            projectPackageFile.write(projectPackageContent)
            File buildPackageFile = new File(buildPackagePath)
            buildPackageFile.write(buildPackageContent)
            String packageContentExpect = '''<?xml version='1.0' encoding='UTF-8'?>
                                    <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                        <types>
                                            <members>Object4__c</members>
                                            <name>CustomObject</name>
                                        </types>
                                        <types>
                                            <members>MyDocuments/doc2.txt</members>
                                            <members>MyDocuments/doc1.txt</members>
                                            <members>MyDocuments</members>
                                            <name>Document</name>
                                        </types>
                                        <types>
                                            <members>Class1</members>
                                            <name>ApexClass</name>
                                        </types>
                                        <types>
                                            <members>Object1__c.MyCustomField1</members>
                                            <name>CustomField</name>
                                        </types>
                                        <types>
                                            <members>Object1__c.MyFieldSet1</members>
                                            <name>FieldSet</name>
                                        </types>
                                        <version>32.0</version>
                                    </Package>
                                    '''
        when:
            PackageCombiner.packageCombineToUpdate(projectPackagePath, buildPackagePath)
            XMLUnit.ignoreWhitespace = true
            def xmlDiff = new Diff(buildPackageFile.text, packageContentExpect)
        then:
            new File(Paths.get(SRC_PATH, 'buildPackage.xml').toString()).exists()
            xmlDiff.similar()
    }

    def 'Test should take in account documents with their folders name when it is added '() {
        given:
            String projectPackagePath = Paths.get(SRC_PATH, 'projectPackage.xml')
            String buildPackagePath = Paths.get(SRC_PATH, 'package.xml')
            //the next variable is content of my project package
            String projectPackageContent = '''<?xml version='1.0' encoding='UTF-8'?>
                                        <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                            <types>
                                                <members>MyDocuments</members>
                                                <members>MyDocuments/doc2.txt</members>
                                                <members>MyDocuments/doc1.txt</members>
                                                <name>Document</name>
                                            </types>
                                            <version>32.0</version>
                                        </Package>
                                        '''
            //The next is the content of my build package
            String buildPackageContent ='''<?xml version='1.0' encoding='UTF-8'?>
                                        <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                            <types>
                                                <members>MyDocuments/doc3.txt</members>
                                                <name>Document</name>
                                            </types>
                                        <version>32.0</version></Package>
                                        '''
            File projectPackageFile = new File(projectPackagePath)
            projectPackageFile.write(projectPackageContent)
            File buildPackageFile = new File(buildPackagePath)
            buildPackageFile.write(buildPackageContent)
            String packageContentExpect = '''<?xml version='1.0' encoding='UTF-8'?>
                                        <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                            <types>
                                                <members>MyDocuments/doc3.txt</members>
                                                <members>MyDocuments</members>
                                                <name>Document</name>
                                            </types>
                                            <version>32.0</version>
                                        </Package>
                                        '''
        when:
            PackageCombiner.packageCombineToUpdate(projectPackagePath, buildPackagePath)
            XMLUnit.ignoreWhitespace = true
            def xmlDiff = new Diff(buildPackageFile.text, packageContentExpect)
        then:
            new File(Paths.get(SRC_PATH, 'buildPackage.xml').toString()).exists()
            xmlDiff.similar()
    }

    def "Test shouldn't take in account documents with their folders name when it is deleted "() {
        given:
            String projectPackagePath = Paths.get(SRC_PATH, 'projectPackage.xml')
            String destructivePath = Paths.get(SRC_PATH, 'destructiveChanges.xml')
            //the next variable is content of my project package
            String projectPackageContent = '''<?xml version='1.0' encoding='UTF-8'?>
                                            <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                                <types>
                                                    <members>MyDocuments</members>
                                                    <members>MyDocuments/doc2.txt</members>
                                                    <members>MyDocuments/doc1.txt</members>
                                                    <name>Document</name>
                                                </types>
                                                <version>32.0</version>
                                            </Package>
                                            '''
            //The next is the content of my build package
            String destructiveContent ='''<?xml version='1.0' encoding='UTF-8'?>
                                            <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                                <types>
                                                    <members>MyDocuments/doc1.txt</members>
                                                    <name>Document</name>
                                                </types>
                                            <version>32.0</version></Package>
                                            '''
            File projectPackageFile = new File(projectPackagePath)
            projectPackageFile.write(projectPackageContent)
            File buildPackageFile = new File(destructivePath)
            buildPackageFile.write(destructiveContent)
            String packageContentExpect = '''<?xml version='1.0' encoding='UTF-8'?>
                                            <Package xmlns='http://soap.sforce.com/2006/04/metadata'>
                                                <types>
                                                    <members>MyDocuments/doc1.txt</members>
                                                    <name>Document</name>
                                                </types>
                                                <version>32.0</version>
                                            </Package>
                                            '''
        when:
            PackageCombiner.packageCombineToUpdate(projectPackagePath, destructivePath)
            XMLUnit.ignoreWhitespace = true
            def xmlDiff = new Diff(buildPackageFile.text, packageContentExpect)
        then:
            new File(Paths.get(SRC_PATH, 'destructiveChanges.xml').toString()).exists()
            xmlDiff.similar()
    }

    def cleanupSpec() {
        new File(Paths.get(SRC_PATH, 'projectPackage.xml').toString()).delete()
        new File(Paths.get(SRC_PATH, 'package.xml').toString()).delete()
        new File(Paths.get(SRC_PATH, 'buildPackage.xml').toString()).delete()
        new File(Paths.get(SRC_PATH, 'destructiveChanges.xml').toString()).delete()
        new File(Paths.get(SRC_PATH, 'packageTest.xml').toString()).delete()
        new File(Paths.get(SRC_PATH, 'packageTestDocument.xml').toString()).delete()
        new File(Paths.get(SRC_PATH, 'packageTestReport.xml').toString()).delete()
        new File(Paths.get(SRC_PATH, 'packageTestField.xml').toString()).delete()
    }
}
