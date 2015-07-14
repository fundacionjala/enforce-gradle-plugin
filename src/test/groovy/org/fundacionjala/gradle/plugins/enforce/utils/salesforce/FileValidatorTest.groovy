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
        mapExpected.put(Constants.FILE_WITHOUT_XML,[])

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
        folders.add(new File(Paths.get(SRC_PATH,'webs').toString()))

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

    def createFolderWithXml(String folder, int initFiles, int endFiles, String ext, boolean createFile ,boolean createXml) {
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
        createFolderWithXml('classes', 1, 5, 'cls', true , true)
        createFolderWithXml('triggers', 1, 5, 'trigger', true , true)
        createFolderWithXml('objects', 1, 5, 'object', true , false)
        crateFolderWithXml('documents', 'DocumentsFolder1', 1, 5, 'txt', true , true)
        crateFolderWithXml('documents', 'DocumentsFolder2', 1, 5, 'txt', true , true)
        crateFolderWithXml('dashboards', 'DashboardFolder1', 1, 5, 'dashboard', true , true)
        crateFolderWithXml('dashboards', 'DashboardFolder2', 1, 5, 'dashboard', true , true)
    }

    def "Test should returns a map that contains [ VALID_FILES: 5 class files ]" () {
        given:
            createFolderWithXml('classes', 1, 5, 'cls', true , true)
            addFolderExpected(Constants.VALID_FILE,'classes', 1, 5, 'cls', true , true)
        when:
            ClassifiedFile classifiedFile = FileValidator.validateFiles(SRC_PATH, allFiles)
        then:
            classifiedFile.invalidFiles.sort() == mapExpected[Constants.INVALID_FILE_BY_FOLDER].sort()
            classifiedFile.notFoundFiles.sort() == mapExpected[Constants.DOES_NOT_EXIST_FILES].sort()
            classifiedFile.filesWithoutXml.sort() == mapExpected[Constants.FILE_WITHOUT_XML].sort()
            classifiedFile.validFiles.sort() == mapExpected[Constants.VALID_FILE].sort()
    }

    def "Test should returns a map that contains [ INVALID_FILE_BY_FOLDER: 5, VALID_FILE: 5 class files ]" () {
        given:
            createFolderWithXml('classes', 1, 3, 'cls', true , true)
            createFolderWithXml('classes', 1, 3, 'trigger', true , true)
            addFolderExpected(Constants.VALID_FILE,'classes', 1, 3, 'cls', true , true)
            addFolderExpected(Constants.INVALID_FILE_BY_FOLDER,'classes', 1, 3, 'trigger', true , true)
        when:
            ClassifiedFile classifiedFile = FileValidator.validateFiles(SRC_PATH, allFiles)
        then:
            classifiedFile.invalidFiles.sort() == mapExpected[Constants.INVALID_FILE_BY_FOLDER].sort()
            classifiedFile.notFoundFiles.sort() == mapExpected[Constants.DOES_NOT_EXIST_FILES].sort()
            classifiedFile.filesWithoutXml.sort() == mapExpected[Constants.FILE_WITHOUT_XML].sort()
            classifiedFile.validFiles.sort() == mapExpected[Constants.VALID_FILE].sort()
    }

    def "Test should returns a map that contains [ INVALID_FILE_BY_FOLDER: 5, VALID_FILE: 5 triggers files ]" () {
        given:
            createFolderWithXml('triggers', 1, 2, 'cls', true , true)
            createFolderWithXml('triggers', 1, 2, 'trigger', true , true)
            addFolderExpected(Constants.VALID_FILE,'triggers', 1, 2, 'trigger', true , true)
            addFolderExpected(Constants.INVALID_FILE_BY_FOLDER,'triggers', 1, 2, 'cls', true , true)
        when:
            ClassifiedFile classifiedFile = FileValidator.validateFiles(SRC_PATH, allFiles)
        then:
            classifiedFile.invalidFiles.sort() == mapExpected[Constants.INVALID_FILE_BY_FOLDER].sort()
            classifiedFile.notFoundFiles.sort() == mapExpected[Constants.DOES_NOT_EXIST_FILES].sort()
            classifiedFile.filesWithoutXml.sort() == mapExpected[Constants.FILE_WITHOUT_XML].sort()
            classifiedFile.validFiles.sort() == mapExpected[Constants.VALID_FILE].sort()
    }

    def "Test should returns a map that contains [ DOES_NOT_EXIST_FILES: 5 class files ]" () {
        given:
            createFolderWithXml('classes', 1, 5, 'cls', true , true)
            allFiles.each {it.delete()}
            addFolderExpected(Constants.DOES_NOT_EXIST_FILES,'classes', 1, 5, 'cls', true , true)
        when:
            ClassifiedFile classifiedFile = FileValidator.validateFiles(SRC_PATH, allFiles)
        then:
            classifiedFile.invalidFiles.sort() == mapExpected[Constants.INVALID_FILE_BY_FOLDER].sort()
            classifiedFile.notFoundFiles.sort() == mapExpected[Constants.DOES_NOT_EXIST_FILES].sort()
            classifiedFile.filesWithoutXml.sort() == mapExpected[Constants.FILE_WITHOUT_XML].sort()
            classifiedFile.validFiles.sort() == mapExpected[Constants.VALID_FILE].sort()
    }

    def "Test should returns a map that contains [ FILE_WHITOUT_XML: 5 class files ]" () {
        given:
            createFolderWithXml('classes', 1, 5, 'cls', true , false)
            addFolderExpected(Constants.FILE_WITHOUT_XML,'classes', 1, 5, 'cls', true , false)
        when:
            ClassifiedFile classifiedFile = FileValidator.validateFiles(SRC_PATH, allFiles)
        then:
            classifiedFile.invalidFiles.sort() == mapExpected[Constants.INVALID_FILE_BY_FOLDER].sort()
            classifiedFile.notFoundFiles.sort() == mapExpected[Constants.DOES_NOT_EXIST_FILES].sort()
            classifiedFile.filesWithoutXml.sort() == mapExpected[Constants.FILE_WITHOUT_XML].sort()
            classifiedFile.validFiles.sort() == mapExpected[Constants.VALID_FILE].sort()
    }

    def "Test should returns a map that contains [ XML_WHITOUT_FILE: 5 tiggers files ]" () {
        given:
            createFolderWithXml('triggers', 1, 3, 'trigger', true , true)
            addFolderExpected(Constants.VALID_FILE,'triggers', 1, 3, 'trigger', true , true)
        when:
            ClassifiedFile classifiedFile = FileValidator.validateFiles(SRC_PATH, allFiles)
        then:
            classifiedFile.invalidFiles.sort() == mapExpected[Constants.INVALID_FILE_BY_FOLDER].sort()
            classifiedFile.notFoundFiles.sort() == mapExpected[Constants.DOES_NOT_EXIST_FILES].sort()
            classifiedFile.filesWithoutXml.sort() == mapExpected[Constants.FILE_WITHOUT_XML].sort()
            classifiedFile.validFiles.sort() == mapExpected[Constants.VALID_FILE].sort()
    }

    def "Test should returns a map that contains [ XML_WHITOUT_FiLE: 5 tiggers files ]2" () {
        given:
            crateFolderWithXml('documents','DocumentsFolder1', 1, 2, 'txt', true , true)
            crateFolderWithXml('documents','DocumentsFolder1', 3, 4, 'txt', true , false)
            addFolderExpected(Constants.VALID_FILE,'documents','DocumentsFolder1', 1, 2, 'txt', true , true)
            addFolderExpected(Constants.FILE_WITHOUT_XML,'documents','DocumentsFolder1', 3, 4, 'txt', true , false)
        when:
            ClassifiedFile classifiedFile = FileValidator.validateFiles(SRC_PATH, allFiles)
        then:
            classifiedFile.invalidFiles.sort() == mapExpected[Constants.INVALID_FILE_BY_FOLDER].sort()
            classifiedFile.notFoundFiles.sort() == mapExpected[Constants.DOES_NOT_EXIST_FILES].sort()
            classifiedFile.filesWithoutXml.sort() == mapExpected[Constants.FILE_WITHOUT_XML].sort()
            classifiedFile.validFiles.sort() == mapExpected[Constants.VALID_FILE].sort()
    }

    def "Test should returns invalid folders in the project source root" () {
        given:
            createFolderWithXml('webs', 1, 3, 'trigger', true , true)
            addFolderExpected(Constants.INVALID_FILE_BY_FOLDER,'webs', 1, 3, 'trigger', true , true)
        when:
            ClassifiedFile classifiedFile = FileValidator.validateFiles(SRC_PATH, allFiles)
        then:
            classifiedFile.invalidFiles.sort() == mapExpected[Constants.INVALID_FILE_BY_FOLDER].sort()
            classifiedFile.notFoundFiles.sort() == mapExpected[Constants.DOES_NOT_EXIST_FILES].sort()
            classifiedFile.filesWithoutXml.sort() == mapExpected[Constants.FILE_WITHOUT_XML].sort()
            classifiedFile.validFiles.sort() == mapExpected[Constants.VALID_FILE].sort()
    }

    def "Test should returns a package.xml valid file" () {
        given:
            createFolderWithXml('webs', 1, 3, 'trigger', true , true)
            addFolderExpected(Constants.INVALID_FILE_BY_FOLDER,'webs', 1, 3, 'trigger', true , true)
            File packageFile = new File(Paths.get(SRC_PATH, 'package.xml').toString())
            packageFile.createNewFile()
            allFiles.add(packageFile)
            mapExpected[Constants.VALID_FILE].add(packageFile)
        when:
            ClassifiedFile classifiedFile = FileValidator.validateFiles(SRC_PATH, allFiles)
        then:
            classifiedFile.invalidFiles.sort() == mapExpected[Constants.INVALID_FILE_BY_FOLDER].sort()
            classifiedFile.notFoundFiles.sort() == mapExpected[Constants.DOES_NOT_EXIST_FILES].sort()
            classifiedFile.filesWithoutXml.sort() == mapExpected[Constants.FILE_WITHOUT_XML].sort()
            classifiedFile.validFiles.sort() == mapExpected[Constants.VALID_FILE].sort()
    }

    def cleanup() {
      new File(Paths.get(SRC_PATH).toString()).deleteDir()
    }
}
