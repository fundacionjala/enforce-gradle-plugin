package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths


class OrgValidatorTest extends Specification {
    @Shared
    def credential

    @Shared
    String SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
            "fundacionjala", "gradle", "plugins","enforce","tasks", "salesforce", "resources").toString()

    @Shared
    ArrayList<File> allFiles
    ArrayList<File> validClassFiles
    ArrayList<File> invalidClassFiles
    ArrayList<File> validTriggerFiles
    ArrayList<File> invalidTriggerFiles
    ArrayList<File> unvalidatedDocumentFiles
    ArrayList<File> invalidNonexistentFiles
    ArrayList<File> validPagesFiles
    ArrayList<File> invalidPagesFiles

    @Shared
    Map<String, ArrayList<File>> mapExpected

    def setup() {
        credential = new Credential()
        credential.id = 'id'
        credential.username = 'salesforce2014.test@gmail.com'
        credential.password = '123qwe2014'
        credential.token = 'UO1Jx5vDQl97xCKkwXBH8tg3T'
        credential.loginFormat = LoginType.DEV.value()
        credential.type = 'normal'

        allFiles = []
        validClassFiles = []
        invalidClassFiles = []
        validTriggerFiles = []
        invalidTriggerFiles = []
        validPagesFiles = []
        invalidPagesFiles = []
        unvalidatedDocumentFiles = []
        invalidNonexistentFiles = []

        mapExpected = [:]

        validClassFiles.add(new File(Paths.get(SRC_PATH,'classes','Class2.cls').toString()))
        validClassFiles.add(new File(Paths.get(SRC_PATH,'classes','TemporalClass.cls').toString()))
        validClassFiles.add(new File(Paths.get(SRC_PATH,'classes','Lunes.cls').toString()))
        validClassFiles.add(new File(Paths.get(SRC_PATH,'classes','LunesTest.cls').toString()))
        validClassFiles.add(new File(Paths.get(SRC_PATH,'classes','MyClass1.cls').toString()))
        validClassFiles.add(new File(Paths.get(SRC_PATH,'classes','MyStringStack.cls').toString()))
        validClassFiles.add(new File(Paths.get(SRC_PATH,'classes','StringStack.cls').toString()))
        validClassFiles.add(new File(Paths.get(SRC_PATH,'classes','TestClass.cls').toString()))
        validClassFiles.add(new File(Paths.get(SRC_PATH,'classes','TestClass1.cls').toString()))
        validClassFiles.add(new File(Paths.get(SRC_PATH,'classes','myFavoriteClass.cls').toString()))

        invalidClassFiles.add(new File(Paths.get(SRC_PATH,'classes','fallClass1.cls').toString()))
        invalidClassFiles.add(new File(Paths.get(SRC_PATH,'classes','fallClass2.cls').toString()))
        invalidClassFiles.add(new File(Paths.get(SRC_PATH,'classes','fallClass3.cls').toString()))

        validTriggerFiles.add(new File(Paths.get(SRC_PATH,'triggers','trigger2.trigger').toString()))
        validTriggerFiles.add(new File(Paths.get(SRC_PATH,'triggers','LunesTrigger.trigger').toString()))

        invalidTriggerFiles.add(new File(Paths.get(SRC_PATH,'triggers','triggerFall1.trigger').toString()))
        invalidTriggerFiles.add(new File(Paths.get(SRC_PATH,'triggers','triggerFall2.trigger').toString()))
        invalidTriggerFiles.add(new File(Paths.get(SRC_PATH,'triggers','triggerFall3.trigger').toString()))

        validPagesFiles.add(new File(Paths.get(SRC_PATH,'pages','Page1.page').toString()))
        validPagesFiles.add(new File(Paths.get(SRC_PATH,'pages','Page2.page').toString()))
        validPagesFiles.add(new File(Paths.get(SRC_PATH,'pages','Page3.page').toString()))

        invalidPagesFiles.add(new File(Paths.get(SRC_PATH,'pages','PageFall1.page').toString()))
        invalidPagesFiles.add(new File(Paths.get(SRC_PATH,'pages','PageFall2.page').toString()))
        invalidPagesFiles.add(new File(Paths.get(SRC_PATH,'pages','PageFall3.page').toString()))

        unvalidatedDocumentFiles.add(new File(Paths.get(SRC_PATH,'documents','myDocuments','doc.txt').toString()))
        unvalidatedDocumentFiles.add(new File(Paths.get(SRC_PATH,'documents','myDocuments','doc2.txt').toString()))

        invalidNonexistentFiles.add(new File(Paths.get(SRC_PATH,'othercomponent','doc1.txt').toString()))
        invalidNonexistentFiles.add(new File(Paths.get(SRC_PATH,'othercomponent','doc2.txt').toString()))
        invalidNonexistentFiles.add(new File(Paths.get(SRC_PATH,'othercomponent','doc3.txt').toString()))

        mapExpected.put(Constants.VALID_FILE, new ArrayList<File>())
        mapExpected.put(Constants.FILES_NOT_FOUND, new ArrayList<File>())
        mapExpected.put(Constants.FILE_WITHOUT_VALIDATOR, new ArrayList<File>())
    }

    def showMaps(boolean show ,Map<String, ArrayList<File>> mapExpected,Map<String, ArrayList<File>> mapResult) {
        if (show) {
            println "======================================================== << EXPECTED >>"
            mapExpected.sort().each {key, ArrayList<File> listFiles->
                println key + "[ " + listFiles.size() +" ]"
                listFiles.sort().each { File file->
                    println "   " +  Util.getRelativePath(file,SRC_PATH)+"  "
                }
            }
            println "======================================================== << RESULT >>"
            mapResult.sort().each {key, ArrayList<File> listFiles->
                println key + "[ " + listFiles.size() +" ]"
                listFiles.sort().each { File file->
                    println "   " +  Util.getRelativePath(file,SRC_PATH)+"  "
                }
            }
            println "===================================================================="
        }

    }

    def "Test should returns a map that contains all valid class" () {
        given:
            mapExpected[Constants.VALID_FILE].addAll(validClassFiles)
            allFiles.addAll(validClassFiles)

        when:
            Map<String,ArrayList<File>> mapResponse = OrgValidator.validateFiles(credential, allFiles, SRC_PATH)

        then:
            mapResponse[Constants.VALID_FILE].sort() == mapExpected[Constants.VALID_FILE].sort()
            mapResponse[Constants.FILES_NOT_FOUND].sort() == mapExpected[Constants.FILES_NOT_FOUND].sort()
            mapResponse[Constants.FILE_WITHOUT_VALIDATOR].sort() == mapExpected[Constants.FILE_WITHOUT_VALIDATOR].sort()
    }

    def "Test should returns a map that contains all invalid class" () {
        given:
            mapExpected[Constants.FILES_NOT_FOUND].addAll(invalidClassFiles)
            allFiles.addAll(invalidClassFiles)

        when:
            Map<String,ArrayList<File>> mapResponse = OrgValidator.validateFiles(credential, allFiles, SRC_PATH)

        then:
            mapResponse[Constants.VALID_FILE].sort() == mapExpected[Constants.VALID_FILE].sort()
            mapResponse[Constants.FILES_NOT_FOUND].sort() == mapExpected[Constants.FILES_NOT_FOUND].sort()
            mapResponse[Constants.FILE_WITHOUT_VALIDATOR].sort() == mapExpected[Constants.FILE_WITHOUT_VALIDATOR].sort()
    }

    def "Test should returns a map that contains all valid and invalid trigger" () {
        given:
            mapExpected[Constants.VALID_FILE].addAll(validTriggerFiles)
            mapExpected[Constants.FILES_NOT_FOUND].addAll(invalidTriggerFiles)
            allFiles.addAll(validTriggerFiles)
            allFiles.addAll(invalidTriggerFiles)

        when:
            Map<String,ArrayList<File>> mapResponse = OrgValidator.validateFiles(credential, allFiles, SRC_PATH)

        then:
            mapResponse[Constants.VALID_FILE].sort() == mapExpected[Constants.VALID_FILE].sort()
            mapResponse[Constants.FILES_NOT_FOUND].sort() == mapExpected[Constants.FILES_NOT_FOUND].sort()
            mapResponse[Constants.FILE_WITHOUT_VALIDATOR].sort() == mapExpected[Constants.FILE_WITHOUT_VALIDATOR].sort()
    }

    def "Test should returns a map that contains all valid and invalid class and triggers" () {
        given:
            mapExpected[Constants.VALID_FILE].addAll(validClassFiles)
            mapExpected[Constants.VALID_FILE].addAll(validTriggerFiles)
            mapExpected[Constants.FILES_NOT_FOUND].addAll(invalidClassFiles)
            mapExpected[Constants.FILES_NOT_FOUND].addAll(invalidTriggerFiles)

            allFiles.addAll(validClassFiles)
            allFiles.addAll(validTriggerFiles)
            allFiles.addAll(invalidClassFiles)
            allFiles.addAll(invalidTriggerFiles)

        when:
            Map<String,ArrayList<File>> mapResponse = OrgValidator.validateFiles(credential, allFiles, SRC_PATH)
            showMaps(true,mapExpected,mapResponse)

        then:
            mapResponse[Constants.VALID_FILE].sort() == mapExpected[Constants.VALID_FILE].sort()
            mapResponse[Constants.FILES_NOT_FOUND].sort() == mapExpected[Constants.FILES_NOT_FOUND].sort()
            mapResponse[Constants.FILE_WITHOUT_VALIDATOR].sort() == mapExpected[Constants.FILE_WITHOUT_VALIDATOR].sort()
    }

    def "Test should returns a map that contains all valid, invalid and noneexistent files" () {
        given:
            mapExpected[Constants.VALID_FILE].addAll(validClassFiles)
            mapExpected[Constants.VALID_FILE].addAll(validTriggerFiles)
            mapExpected[Constants.FILE_WITHOUT_VALIDATOR].addAll(invalidNonexistentFiles)


            allFiles.addAll(validClassFiles)
            allFiles.addAll(validTriggerFiles)
            allFiles.addAll(invalidNonexistentFiles)

        when:
            Map<String,ArrayList<File>> mapResponse = OrgValidator.validateFiles(credential, allFiles, SRC_PATH)
            showMaps(true,mapExpected,mapResponse)

        then:
            mapResponse[Constants.VALID_FILE].sort() == mapExpected[Constants.VALID_FILE].sort()
            mapResponse[Constants.FILES_NOT_FOUND].sort() == mapExpected[Constants.FILES_NOT_FOUND].sort()
            mapResponse[Constants.FILE_WITHOUT_VALIDATOR].sort() == mapExpected[Constants.FILE_WITHOUT_VALIDATOR].sort()
    }

    def "Test should returns a map that contains all valid xistent files" () {
        given:
            mapExpected[Constants.VALID_FILE].addAll(validClassFiles)
            mapExpected[Constants.VALID_FILE].addAll(validTriggerFiles)
            mapExpected[Constants.VALID_FILE].addAll(validPagesFiles)
            mapExpected[Constants.FILES_NOT_FOUND].addAll(invalidClassFiles)
            mapExpected[Constants.FILES_NOT_FOUND].addAll(invalidTriggerFiles)
            mapExpected[Constants.FILES_NOT_FOUND].addAll(invalidPagesFiles)

            allFiles.addAll(validClassFiles)
            allFiles.addAll(validTriggerFiles)
            allFiles.addAll(validPagesFiles)
            allFiles.addAll(invalidClassFiles)
            allFiles.addAll(invalidTriggerFiles)
            allFiles.addAll(invalidPagesFiles)

        when:
            Map<String,ArrayList<File>> mapResponse = OrgValidator.validateFiles(credential, allFiles, SRC_PATH)
            showMaps(true,mapExpected,mapResponse)

        then:
            mapResponse[Constants.VALID_FILE].sort() == mapExpected[Constants.VALID_FILE].sort()
            mapResponse[Constants.FILES_NOT_FOUND].sort() == mapExpected[Constants.FILES_NOT_FOUND].sort()
            mapResponse[Constants.FILE_WITHOUT_VALIDATOR].sort() == mapExpected[Constants.FILE_WITHOUT_VALIDATOR].sort()
    }
}