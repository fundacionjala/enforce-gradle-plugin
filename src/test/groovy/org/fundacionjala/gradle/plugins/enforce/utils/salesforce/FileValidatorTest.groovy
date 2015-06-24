package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class FileValidatorTest extends Specification {
    @Shared
    String projectPath

    @Shared
    String SRC_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org",
            "fundacionjala", "gradle", "plugins","enforce","tasks", "salesforce", "resources","src_temporary").toString()

    @Shared
    ArrayList<File> allFiles

    @Shared
    Map<String, ArrayList<File>> mapExpected

    def setup() {
        def userDir = System.getProperty("user.dir")
        projectPath = Paths.get(userDir, 'src/test/groovy/org/fundacionjala/gradle/plugins/enforce/utils/resources').toString()

        allFiles = []
        mapExpected = [:]
        mapExpected.put(Constants.INVALID_FILE_BY_FOLDER,[])
        mapExpected.put(Constants.VALID_FILE,[])
        mapExpected.put(Constants.DOES_NOT_EXIST_FILES,[])
        mapExpected.put(Constants.FILE_WHITOUT_XML,[])

        ArrayList<File> folders = new ArrayList<File>()
        folders.add(new File(Paths.get(SRC_PATH).toString()))
        folders.add(new File(Paths.get(SRC_PATH,'classes').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'triggers').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'objects').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'dashboards').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'dashboards','DashboardFolder1').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'dashboards','DashboardFolder2').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'documents').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'documents','DocumentsFolder1').toString()))
        folders.add(new File(Paths.get(SRC_PATH,'documents','DocumentsFolder2').toString()))

        folders.each { folder->
            new File(folder.getAbsolutePath()).mkdir()
        }
    }

    def crateFolderWithXml(String folder,String subFolder, int initFiles, int endFiles, String ext, boolean createFile ,boolean createXml) {
        ArrayList<File> files = []
        for(int i = initFiles; i <= endFiles; i++) {
            if(createFile) {
                String fileName = folder + "_" + i + "." + ext
                File file = new File(Paths.get(SRC_PATH, folder, subFolder, fileName).toString())
                file.createNewFile()
                files.add(file)
            }
            if(createXml) {
                String fileName = folder + "_" + i + "." + ext + "-meta.xml"
                File fileXml = new File(Paths.get(SRC_PATH, folder, subFolder, fileName).toString())
                fileXml.createNewFile()
                files.add(fileXml)
            }
        }
        allFiles.addAll(files)
        return files
    }

    def crateFolderWithXml(String folder, int initFiles, int endFiles, String ext, boolean createFile ,boolean createXml) {
        ArrayList<File> files = []
        for(int i = initFiles; i <= endFiles; i++) {
            if(createFile) {
                String fileName = folder + "_" + i + "." + ext
                File file = new File(Paths.get(SRC_PATH,folder,fileName).toString())
                file.createNewFile()
                files.add(file)
            }
            if(createXml) {
                String fileName = folder + "_" + i + "." + ext + "-meta.xml"
                File fileXml = new File(Paths.get(SRC_PATH,folder,fileName).toString())
                fileXml.createNewFile()
                files.add(fileXml)
            }
        }
        allFiles.addAll(files)
        return files
    }

    def addFolderExpected(String group, String folder, int initFiles, int endFiles, String ext, boolean createFile ,boolean createXml) {
        ArrayList<File> files = []
        for(int i = initFiles; i <= endFiles; i++) {
            if(createFile) {
                String fileName = folder + "_" + i + "." + ext
                File file = new File(Paths.get(SRC_PATH, folder, fileName).toString())
                files.add(file)
            }
            if(createXml) {
                String fileName = folder + "_" + i + "." + ext + "-meta.xml"
                File fileXml = new File(Paths.get(SRC_PATH, folder, fileName).toString())
                files.add(fileXml)
            }
        }
        if( mapExpected[group] != null ) {
            mapExpected[group].addAll(files)
        }
        else {
            mapExpected.put(group, files)
        }

    }

    def addFolderExpected(String group, String folder, String subFolder, int initFiles, int endFiles, String ext, boolean createFile ,boolean createXml) {
        ArrayList<File> files = []
        for(int i = initFiles; i <= endFiles; i++) {
            if(createFile) {
                String fileName = folder + "_" + i + "." + ext
                File file = new File(Paths.get(SRC_PATH, folder, subFolder, fileName).toString())
                files.add(file)
            }
            if(createXml) {
                String fileName = folder + "_" + i + "." + ext + "-meta.xml"
                File fileXml = new File(Paths.get(SRC_PATH, folder, subFolder, fileName).toString())
                files.add(fileXml)
            }
        }
        if( mapExpected[group] != null ) {
            mapExpected[group].addAll(files)
        }
        else {
            mapExpected.put(group, files)
        }
    }

    def createAllFiles() {
        crateFolderWithXml('classes', 1, 5, 'cls', true , true)
        crateFolderWithXml('triggers', 1, 5, 'trigger', true , true)
        crateFolderWithXml('objects', 1, 5, 'object', true , false)
        crateFolderWithXml('documents', 'DocumentsFolder1', 1, 5, 'txt', true , true)
        crateFolderWithXml('documents', 'DocumentsFolder2', 1, 5, 'txt', true , true)
        crateFolderWithXml('dashboards', 'DashboardFolder1', 1, 5, 'dashboard', true , true)
        crateFolderWithXml('dashboards', 'DashboardFolder2', 1, 5, 'dashboard', true , true)
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


    def "Test should returns all invalid files" () {
        given:
        File invalidFolderByDocument = new File(Paths.get(projectPath, 'reports/mydocs/doc1.doc').toString())
        File invalidExtensionFile = new File(Paths.get(projectPath, 'classes/Class1.cls-meta.xml').toString())
        File invalidFileWithoutExtension = new File(Paths.get(projectPath, 'classes/Class1').toString())
        ArrayList<File> files = []
        files.add(new File(Paths.get(projectPath, 'documents/mydocs/doc1.doc').toString()))
        files.add(new File(Paths.get(projectPath, 'documents/mydocs/image.png').toString()))
        files.add(invalidFolderByDocument)
        files.add(new File(Paths.get(projectPath, 'reports/myreports/report1.report').toString()))
        files.add(new File(Paths.get(projectPath, 'objects/obj__c.object').toString()))
        files.add(new File(Paths.get(projectPath, 'classes/Class1.cls').toString()))
        files.add(invalidExtensionFile)
        files.add(invalidFileWithoutExtension)
        Map<String, ArrayList<File>> expected = [:]
        ArrayList<File> invalidFiles = []
        invalidFiles.add(invalidFolderByDocument)
        invalidFiles.add(invalidFileWithoutExtension)
        invalidFiles.add(invalidExtensionFile)
        expected.put(Constants.INVALID_FILE_BY_FOLDER, invalidFiles)
        when:
        Map<String, ArrayList<File>> result = FileValidator.validateFiles(projectPath, files)
        then:
        result[Constants.INVALID_FILE_BY_FOLDER].sort() == expected[Constants.INVALID_FILE_BY_FOLDER].sort()
    }

    def "Test should returns a map that contains all files by states: invalid, not_found, and valid" () {
        given:
            //invalid files
            File invalidFolderFile = new File(Paths.get(projectPath, 'web/InvalidClass.cls').toString())
            File invalidObjectExtension = new File(Paths.get(projectPath, 'objects/InvalidObject__c.obj').toString())
            File invalidExtensionFile = new File(Paths.get(projectPath, 'classes/other.data').toString())

            // valid files
            File validDoc = new File(Paths.get(projectPath, 'documents/mydocs/doc1.doc').toString())
            File validPng = new File(Paths.get(projectPath, 'documents/mydocs/image.png').toString())
            File validReport = new File(Paths.get(projectPath, 'reports/testFolder/testReport.report').toString())
            File validObj = new File(Paths.get(projectPath, 'objects/Object1__c.object').toString())
            File validClass = new File(Paths.get(projectPath, 'classes/class1.cls').toString())

            // not found files
            File notFoundDoc = new File(Paths.get(projectPath, 'documents/mydocs/notFoundDoc.doc').toString())
            File notFoundReport = new File(Paths.get(projectPath, 'reports/testFolder/notFoundReport.report').toString())
            File notFoundClass = new File(Paths.get(projectPath, 'classes/notFoundClass1.cls').toString())

            ArrayList<File> files = []
            files.add(validDoc)
            files.add(validPng)
            files.add(invalidObjectExtension)
            files.add(invalidFolderFile)
            files.add(validReport)
            files.add(validObj)
            files.add(validClass)
            files.add(invalidExtensionFile)
            files.add(notFoundReport)
            files.add(notFoundClass)
            files.add(notFoundDoc)

            Map<String, ArrayList<File>> expected = [:]
            ArrayList<File> invalidFiles = []
            invalidFiles.add(invalidObjectExtension)
            invalidFiles.add(invalidExtensionFile)
            invalidFiles.add(invalidFolderFile)
            ArrayList<File> notFoundFiles = []
            notFoundFiles.add(notFoundClass)
            notFoundFiles.add(notFoundDoc)
            notFoundFiles.add(notFoundReport)

            ArrayList<File> validFiles = []
            validFiles.add(validPng)
            validFiles.add(validClass)
            validFiles.add(validDoc)
            validFiles.add(validObj)
            validFiles.add(validReport)
            expected.put(Constants.INVALID_FILE_BY_FOLDER, invalidFiles)
            expected.put(Constants.VALID_FILE, validFiles)
            expected.put(Constants.DOES_NOT_EXIST_FILES, notFoundFiles)

        when:
            Map<String, ArrayList<File>> result = FileValidator.validateFiles(projectPath, files)
        then:
            result[Constants.INVALID_FILE_BY_FOLDER].sort() == expected[Constants.INVALID_FILE_BY_FOLDER].sort()
            result[Constants.VALID_FILE].sort() == expected[Constants.VALID_FILE].sort()
            result[Constants.DOES_NOT_EXIST_FILES].sort() == expected[Constants.DOES_NOT_EXIST_FILES].sort()
    }

    def "Test should returns a map that contains [ VALID_FILES: 5 class files ]" () {
        given:
            crateFolderWithXml('classes', 1, 5, 'cls', true , true)
            addFolderExpected(Constants.VALID_FILE,'classes', 1, 5, 'cls', true , true)

        when:
            Map<String, ArrayList<File>> result = FileValidator.validateFiles(SRC_PATH, allFiles)
             
        then:
            result[Constants.INVALID_FILE_BY_FOLDER].sort() == mapExpected[Constants.INVALID_FILE_BY_FOLDER].sort()
            result[Constants.DOES_NOT_EXIST_FILES].sort() == mapExpected[Constants.DOES_NOT_EXIST_FILES].sort()
            result[Constants.FILE_WHITOUT_XML].sort() == mapExpected[Constants.FILE_WHITOUT_XML].sort()
            result[Constants.VALID_FILE].sort() == mapExpected[Constants.VALID_FILE].sort()
    }

    def "Test should returns a map that contains [ INVALID_FILE_BY_FOLDER: 5, VALID_FILE: 5 class files ]" () {
        given:
            crateFolderWithXml('classes', 1, 3, 'cls', true , true)
            crateFolderWithXml('classes', 1, 3, 'trigger', true , true)
            addFolderExpected(Constants.VALID_FILE,'classes', 1, 3, 'cls', true , true)
            addFolderExpected(Constants.INVALID_FILE_BY_FOLDER,'classes', 1, 3, 'trigger', true , true)

        when:
            Map<String, ArrayList<File>> result = FileValidator.validateFiles(SRC_PATH, allFiles)
             
        then:
            result[Constants.INVALID_FILE_BY_FOLDER].sort() == mapExpected[Constants.INVALID_FILE_BY_FOLDER].sort()
            result[Constants.DOES_NOT_EXIST_FILES].sort() == mapExpected[Constants.DOES_NOT_EXIST_FILES].sort()
            result[Constants.FILE_WHITOUT_XML].sort() == mapExpected[Constants.FILE_WHITOUT_XML].sort()
            result[Constants.VALID_FILE].sort() == mapExpected[Constants.VALID_FILE].sort()
    }

    def "Test should returns a map that contains [ INVALID_FILE_BY_FOLDER: 5, VALID_FILE: 5 triggers files ]" () {
        given:
            crateFolderWithXml('triggers', 1, 2, 'cls', true , true)
            crateFolderWithXml('triggers', 1, 2, 'trigger', true , true)
            addFolderExpected(Constants.VALID_FILE,'triggers', 1, 2, 'trigger', true , true)
            addFolderExpected(Constants.INVALID_FILE_BY_FOLDER,'triggers', 1, 2, 'cls', true , true)

        when:
            Map<String, ArrayList<File>> result = FileValidator.validateFiles(SRC_PATH, allFiles)
             
        then:
            result[Constants.INVALID_FILE_BY_FOLDER].sort() == mapExpected[Constants.INVALID_FILE_BY_FOLDER].sort()
            result[Constants.DOES_NOT_EXIST_FILES].sort() == mapExpected[Constants.DOES_NOT_EXIST_FILES].sort()
            result[Constants.FILE_WHITOUT_XML].sort() == mapExpected[Constants.FILE_WHITOUT_XML].sort()
            result[Constants.VALID_FILE].sort() == mapExpected[Constants.VALID_FILE].sort()
    }

    def "Test should returns a map that contains [ DOES_NOT_EXIST_FILES: 5 class files ]" () {
        given:
            crateFolderWithXml('classes', 1, 5, 'cls', true , true)
            allFiles.each {it.delete()}
            addFolderExpected(Constants.DOES_NOT_EXIST_FILES,'classes', 1, 5, 'cls', true , true)

        when:
            Map<String, ArrayList<File>> result = FileValidator.validateFiles(SRC_PATH, allFiles)
             
        then:
            result[Constants.INVALID_FILE_BY_FOLDER].sort() == mapExpected[Constants.INVALID_FILE_BY_FOLDER].sort()
            result[Constants.DOES_NOT_EXIST_FILES].sort() == mapExpected[Constants.DOES_NOT_EXIST_FILES].sort()
            result[Constants.FILE_WHITOUT_XML].sort() == mapExpected[Constants.FILE_WHITOUT_XML].sort()
            result[Constants.VALID_FILE].sort() == mapExpected[Constants.VALID_FILE].sort()
    }

    def "Test should returns a map that contains [ FILE_WHITOUT_XML: 5 class files ]" () {
        given:
            crateFolderWithXml('classes', 1, 5, 'cls', true , false)
            addFolderExpected(Constants.FILE_WHITOUT_XML,'classes', 1, 5, 'cls', true , false)

        when:
            Map<String, ArrayList<File>> result = FileValidator.validateFiles(SRC_PATH, allFiles)
             
        then:
            result[Constants.INVALID_FILE_BY_FOLDER].sort() == mapExpected[Constants.INVALID_FILE_BY_FOLDER].sort()
            result[Constants.DOES_NOT_EXIST_FILES].sort() == mapExpected[Constants.DOES_NOT_EXIST_FILES].sort()
            result[Constants.FILE_WHITOUT_XML].sort() == mapExpected[Constants.FILE_WHITOUT_XML].sort()
            result[Constants.VALID_FILE].sort() == mapExpected[Constants.VALID_FILE].sort()
    }

    def "Test should returns a map that contains [ XML_WHITOUT_FILE: 5 tiggers files ]" () {
        given:
            crateFolderWithXml('triggers', 1, 3, 'trigger', true , true)
            crateFolderWithXml('triggers', 4, 5, 'trigger', false , true)
            addFolderExpected(Constants.VALID_FILE,'triggers', 1, 3, 'trigger', true , true)
            addFolderExpected(Constants.INVALID_FILE_BY_FOLDER,'triggers', 4, 5, 'trigger', false , true)

        when:
            Map<String, ArrayList<File>> result = FileValidator.validateFiles(SRC_PATH, allFiles)
             
        then:
            result[Constants.INVALID_FILE_BY_FOLDER].sort() == mapExpected[Constants.INVALID_FILE_BY_FOLDER].sort()
            result[Constants.DOES_NOT_EXIST_FILES].sort() == mapExpected[Constants.DOES_NOT_EXIST_FILES].sort()
            result[Constants.FILE_WHITOUT_XML].sort() == mapExpected[Constants.FILE_WHITOUT_XML].sort()
            result[Constants.VALID_FILE].sort() == mapExpected[Constants.VALID_FILE].sort()
    }

    def "Test should returns a map that contains [ XML_WHITOUT_FiLE: 5 tiggers files ]2" () {
        given:
            crateFolderWithXml('documents','DocumentsFolder1', 1, 2, 'txt', true , true)
            crateFolderWithXml('documents','DocumentsFolder1', 3, 4, 'txt', true , true)
            addFolderExpected(Constants.VALID_FILE,'documents','DocumentsFolder1', 1, 2, 'txt', true , true)
            addFolderExpected(Constants.FILE_WHITOUT_XML,'documents','DocumentsFolder1', 3, 4, 'txt', true , false)

        when:
            Map<String, ArrayList<File>> result = FileValidator.validateFiles(SRC_PATH, allFiles)
             
        then:
            result[Constants.INVALID_FILE_BY_FOLDER].sort() == mapExpected[Constants.INVALID_FILE_BY_FOLDER].sort()
            result[Constants.DOES_NOT_EXIST_FILES].sort() == mapExpected[Constants.DOES_NOT_EXIST_FILES].sort()
            result[Constants.FILE_WHITOUT_XML].sort() == mapExpected[Constants.FILE_WHITOUT_XML].sort()
            result[Constants.VALID_FILE].sort() == mapExpected[Constants.VALID_FILE].sort()
    }

    def cleanupSpec() {
        new File(Paths.get(SRC_PATH).toString()).deleteDir()
    }
}
