/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.utils

import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.regex.Pattern

/**
 * validate and copy files
 */
class ManagementFile {

    private final String PACKAGE_XML = 'package.xml'
    private final String METADATA_EXTENSION = '-meta.xml'
    private
    final String ERROR_GETTING_SOURCE_CODE_PATH = "ManagementFile: It's necessary send in constructor source path of user code"
    private File sourcePath
    private final String DOES_NOT_EXIT = 'does not exist'
    private final String SLASH = '/'
    public static final COMPONENTS_HAVE_SUB_FOLDER = ['reports', 'dashboards', 'documents']
    ArrayList<File> validFiles

    /**
     * Initializes a new instance of ManagementFile.
     * @param sourcePath contains the path of user source code
     */
    ManagementFile(String sourcePath) {
        this.sourcePath = new File(sourcePath)
    }

    /**
     * Validates if package is the right
     * @param file contains the package to validate
     * @return true or false, depends if it is the right
     */
    public Boolean validatePackage(File file) {
        if (!this.sourcePath) {
            throw new Exception(ERROR_GETTING_SOURCE_CODE_PATH)
        }
        return file.getParentFile().getName() == this.sourcePath.getName()
    }

    /**
     * Gets valid files by salesforce from path source
     * @param pathSource is the path for each files are validated
     * @returnarray of the files valid
     */
    public ArrayList<File> getValidElements(String pathSource) {
        ArrayList<File> arrayValidFiles = new ArrayList<File>()
        File sourceFolder = new File(pathSource)
        if (sourceFolder.exists()) {
            sourceFolder.eachFile { File folder ->
                if (folder.isDirectory()) {
                    folder.eachFile { file ->
                        if (validateFileByFolder(folder.getName(), file.getName())) {
                            arrayValidFiles.push(file)
                            File xmlFile = getValidateXmlFile(file)
                            if (xmlFile) {
                                arrayValidFiles.push(xmlFile)
                            }
                        } else if (COMPONENTS_HAVE_SUB_FOLDER.contains(folder.getName())) {
                            if (file.isDirectory()) {
                                arrayValidFiles.addAll(getFilesByReportFolder(folder.getName(), file))
                            }
                        }
                    }
                }
                if (folder.getName() == PACKAGE_XML) {
                    arrayValidFiles.add(folder)
                }
            }
        }
        return arrayValidFiles
    }

    /**
     * Gets valid files by a folder
     * @param parentName is the folder parent name
     * @param file is the Folder from gets valid files
     * @return an array of valid files
     */
    private ArrayList<File> getFilesByReportFolder(String parentName, File file) {
        ArrayList<File> result = [:]
        file.eachFile { File reportFile ->
            File xmlReportFile = getValidateXmlFile(file)
            if (xmlReportFile) {
                result.push(xmlReportFile)
            }

            if (validateFileByFolder(parentName, reportFile.getName())) {
                result.push(reportFile)
            }
        }
        return result;
    }

    /**
     * Gets valid files by salesforce from path source excluding type files inserted
     * @param pathSource contains the path of source code
     * @param filesToExclude contains types of files to exclude
     * @return an array of files filtered
     */
    public ArrayList<File> getValidElements(String pathSource, ArrayList<String> filesToExclude) {
        ArrayList<File> arrayFiles = getValidElements(pathSource)
        ArrayList<File> auxiliaryElements = (ArrayList<File>) arrayFiles.clone()
        filesToExclude.each { typeToExclude ->
            arrayFiles.each { file ->
                if (file.getName().endsWith(".${typeToExclude}")) {
                    auxiliaryElements.remove(file)
                }
            }
        }
        return auxiliaryElements
    }

    /**
     * Obtains xml file validated
     * @param file is type file
     * @return xml file if exist else null
     */
    public File getValidateXmlFile(File file) {
        File xmlFile = new File("${file.getAbsolutePath().toString()}${METADATA_EXTENSION}")
        if (xmlFile.exists()) {
            return xmlFile
        }

        return null
    }

    /**
     * Validates the file based in the folder name who belongs, following the saleforce definitions
     * @param folderName
     * @param file
     * @return
     */
    public boolean validateFileByFolder(String folderName, String file) {
        if (folderName == MetadataComponents.DOCUMENTS.getDirectory()) {
            return true
        }
        String componentExtension = MetadataComponents.getExtensionByFolder(folderName)
        if (!componentExtension) {
            return false
        }
        return file.endsWith(componentExtension)
    }

    /**
     * Copies array file in the path copy
     * @parem basePath is to get the relative path of the project based for basePath
     * @param arrayFiles the files should be copy in the path copy
     * @param pathCopy is the parent path
     */
    private void copyArrayFiles(String basePath, ArrayList<File> arrayFiles, String pathCopy) {

        validFiles = arrayFiles
        if (new File(pathCopy).exists()) {
            arrayFiles.each { file ->
                String pathFolder = pathCopy
                String fileName = file.getName()
                if (!fileName.equals(PACKAGE_XML)) {
                    String relativePath = file.getAbsolutePath().replace(basePath, '')
                    String folderPath = relativePath.replace(file.getName(), '')
                    createFolder(pathFolder, folderPath)
                    pathFolder = Paths.get(pathFolder, folderPath).toString()
                }
                Files.copy(file.toPath(), Paths.get(pathFolder, file.getName()), StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    /**
     * Creates a Folder or Folders children based in the basePath source path.
     * @param basePath is the base path where It will create the folder
     * @param folderPath contains the folder and folder children to create them.
     */
    private void createFolder(String basePath, String folderPath) {
        String path = basePath
        String[] subFolders = folderPath.split(Paths.get(SLASH).toString())

        if (subFolders.size() == 0) {
            path = Paths.get(path, folderPath).toString()
            new File(path).mkdir()
            return
        }

        subFolders.each { String folderName ->
            if (!folderName.isEmpty()) {
                path = Paths.get(path, folderName).toString()
                new File(path).mkdir()
            }
        }
    }

    /**
     * Copies files valid for salesforce
     * @param pathFrom is the path source
     * @param pathTo is the path copy
     */
    public void copy(String pathFrom, String pathTo) {
        if (!new File(pathFrom).exists()) {
            throw new Exception("${pathFrom} ${DOES_NOT_EXIT}")
        }
        if (!new File(pathTo).exists()) {
            throw new Exception("${pathTo} ${DOES_NOT_EXIT}")
        }
        copyArrayFiles(pathFrom, getValidElements(pathFrom), pathTo)
    }

    /**
     * Copies files valid for salesforce
     * @param pathFrom is the path source
     * @param pathTo is the path copy
     */
    public void copyFiles(String pathFrom, String pathTo) {
        if (!new File(pathFrom).exists()) {
            throw new Exception("${pathFrom} ${DOES_NOT_EXIT}")
        }
        if (!new File(pathTo).exists()) {
            throw new Exception("${pathTo} ${DOES_NOT_EXIT}")
        }
        copyArrayFiles(pathFrom, getValidElements(pathFrom), pathTo)
    }

    /**
     * Copies files valid for salesforce
     * @param basePath is the common absolute path between fileFrom and pathTo parameters
     * @param fileFrom is the array files
     * @param pathTo is the path copy
     */
    void copy(String basePath, ArrayList<File> fileFrom, String pathTo) {
        copyArrayFiles(basePath, fileFrom, pathTo)
    }

    /**
     * Creates a new directory according path
     * @param path is the path for new directory
     */
    public static void createNewDirectory(String path) {
        File dir = new File(path)

        if (dir.exists()) {
            dir.deleteDir()
        }
        dir.mkdir()
    }

    /**
     * If the directory doesn't  exist it will create
     * @param path is the path for new directory
     */
    public static void createDirectory(String path) {
        File dir = new File(path)
        if (!dir.exists()) {
            dir.mkdir()
        }
    }

    /**
     * If the directories doesn't  exist it will create
     * @param path is the path for new directory
     */
    public static void createDirectories(String path) {
        File dir = new File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    /**
     * Iterates only folders and put in the array folders not deployed
     * @param Path is the source path
     * @return array of the folders not deployed
     */
    public ArrayList<String> getFoldersNotDeploy(String path) {

        ArrayList<String> foldersNotDeploy = new ArrayList<String>()
        File sourceFolder = new File(path)

        if (sourceFolder.exists()) {
            sourceFolder.eachFile { file ->
                if (file.isDirectory() && !MetadataComponents.validFolder(file.getName())) {
                    foldersNotDeploy.push(file.getName())
                }
            }
        }

        return foldersNotDeploy
    }

    /**
     * Gets array valid files by folders
     * @param sourcePath is type String
     * @param folders ys type ArrayList
     * @return files validated by folders
     */
    public ArrayList<File> getFilesByFolders(String sourcePath, ArrayList<String> folders) {

        ArrayList<File> filesByFolder = new ArrayList<File>()
        folders.each { folderName ->
            File folder = new File(Paths.get(sourcePath, folderName).toString())
            if (folder.exists()) {
                folder.eachFile { file ->
                    if (validateFileByFolder(folderName, file.getName())) {
                        filesByFolder.push(file)
                        File xmlFile = new File("${file.getAbsolutePath().toString()}${METADATA_EXTENSION}")
                        if (xmlFile.exists()) {
                            filesByFolder.push(xmlFile)
                        }
                    } else if (COMPONENTS_HAVE_SUB_FOLDER.contains(folder.getName())) {
                        if (file.isDirectory()) {
                            filesByFolder.addAll(getFilesByReportFolder(folder.getName(), file))
                        }
                    }
                }
            }
        }
        return filesByFolder
    }

    /**
     * Gets files from source directory path directory by regular expression
     * @param regex the regular expression
     */
    public ArrayList<File> getFilesByRegex(String regex) {
        ArrayList<File> files = new ArrayList<File>()
        if (!sourcePath.isDirectory()) {
            throw new Exception("The provided directory name ${sourcePath.name} is not a directory.")
        }

        Pattern filePattern = ~/${regex}/
        def findFilenameClosure =
                { file ->
                    if (filePattern.matcher(file.name).find()) {
                        files.add(file)
                    }
                }
        sourcePath.eachFileRecurse(findFilenameClosure)
        return files
    }

    /**
     * Gets files from source directory path directory by extension
     * @param sourcePath the source directory path
     * @param fileExtension the file extension in the source directory
     */
    public ArrayList<File> getFilesByFileExtension(String fileExtension) {
        String fileSubStr = ".*.${fileExtension}\$"
        return getFilesByRegex(fileSubStr)
    }

    /**
     * Gets subdirectories from source directory
     */
    public ArrayList<File> getSubdirectories() {
        ArrayList<File> subdirectories = new ArrayList<File>()
        sourcePath.eachDir { file ->
            subdirectories.add(file)
        }
        return subdirectories
    }
}