/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.filemonitor

import com.twmacinta.util.MD5

import java.nio.file.Paths

/**
 * This class is a template class to monitor the files changed in a directory
 */
public abstract class TemplateFileMonitor {

    private final String NEW_FILE = "New file"
    private final String FILE_TRACKING = '.fileTracker.data'
    public static final String DELETE_FILE = "Deleted file"
    public static final String CHANGED_FILE = "Changed file"

    String nameFile
    Map currentFileHashCode
    Map recoveryFileHashCode
    protected Map<String, String> fileSignatures
    Map<String, String> mapFilesChanged
    String srcProject


    public void setSrcProject(String srcProject) {
        nameFile = Paths.get(srcProject, FILE_TRACKING).toString()
        this.srcProject = srcProject
    }

    /**
     * Constructor of default if userInfo not put nameFile
     */
    public TemplateFileMonitor(String source) {
        nameFile = source
        srcProject = Paths.get(source).getParent().toString()
        fileSignatures = new HashMap<String, String>()
        mapFilesChanged = new HashMap<String, String>()
    }

    /**
     * Constructor of default
     */
    public TemplateFileMonitor() {
        fileSignatures = new HashMap<String, String>()
        mapFilesChanged = new HashMap<String, String>()
    }

    /**
     * Get a state of file
     * @return string of file example of files is changed or delete
     */
    public static String getStateDelete() {
        return DELETE_FILE
    }

    /**
     * Verify if file directory exists
     * @return boolean result of file(pathFile).exists
     */
    public boolean verifyFileMap() {
        return new File(nameFile).exists()
    }

    /**
     * Loads a signature for each file in the directory and its subdirectories
     * @param arrayFiles is a elements Files  for calculate codeHas
     * @return a Map with file path as key and signature as value of a file
     */
    public Map loadSignatureForFilesInDirectory(ArrayList<File> arrayFiles) throws Exception {
        arrayFiles.each {
            File file ->
                String signature = MD5.asHex(MD5.getHash(file))
                String relativePath = getPathRelative(file)
                fileSignatures.put(relativePath, signature)
        }
        return fileSignatures
    }

    /**
     * Gets a path relative of the file
     * @param file is the file that is tracked
     * @return is a path relative
     */
    public String getPathRelative(File file) {
        String nameFile = file.getName()
        String folderFile = file.getParentFile().getName()
        Paths.get(srcProject, folderFile, nameFile).toString()
    }

    /**
     * Compared mapFilePros with currentMapFile
     * CurrentMapFile iterates and search the items on the map mapFilePros
     * If not found is considered as a new file
     * @param mapFilePros is a map of file binary
     * @param currentMapFile is a map that was created with method loadSignatureForFilesInDirectory
     */
    public void findNewFiles(mapFilePros, currentMapFile) {
        for (Object entry : currentMapFile.entrySet()) {
            Map.Entry thisEntry = (Map.Entry) entry
            if (!mapFilePros.get(thisEntry.getKey())) {
                mapFilesChanged.put(thisEntry.getKey().toString(), NEW_FILE)
            }
        }
    }

    /**
     * Compared mapFilePros with currentMapFile
     * CurrentMapFile iterates and search the items on the map
     * If found mapFilePros comparing the hashCode if the other is considered modified file
     * @param mapFilePros is a map of file binary
     * @param currentMapFile is a map that was created with method loadSignatureForFilesInDirectory
     */
    public void findChangedFiles(mapFilePros, currentMapFile) {
        Object currentElement

        for (Object entry : currentMapFile.entrySet()) {
            Map.Entry thisEntry = (Map.Entry) entry
            currentElement = mapFilePros.get(thisEntry.getKey())
            if (currentElement && !currentElement.equals(thisEntry.getValue())) {
                mapFilesChanged.put(thisEntry.getKey().toString(), CHANGED_FILE)
            }
        }
    }

    /**
     * Compared currentMapFile with mapFilePros
     * MapFilePros iterates and search the items on the map currentMapFile
     * If not found is considered as a delete file
     * @param mapFilePros is a map of file binary
     * @param currentMapFile is a map that was created with method loadSignatureForFilesInDirectory
     */
    public void findDeleteFiles(mapFilePros, currentMapFile) {
        for (Object entry : mapFilePros.entrySet()) {
            Map.Entry thisEntry = (Map.Entry) entry
            if (!currentMapFile.get(thisEntry.getKey())) {
                mapFilesChanged.put(thisEntry.getKey().toString(), DELETE_FILE)
            }
        }
    }

    /**
     * Get a two maps using methods readMap and loadSignatureForFilesInDirectory then
     * These two maps call a method compareMaps
     * @param arrayFiles is the array files monitor
     * @return a map content a elements different between recoveryFileHashCode and currentFileHashCode
     */
    public final Map<String, String> getFileChangedExclude(ArrayList<File> arrayFiles) throws Exception {
        recoveryFileHashCode = readMap(nameFile)
        currentFileHashCode = loadSignatureForFilesInDirectory(arrayFiles)
        findNewFiles(recoveryFileHashCode, currentFileHashCode)
        findChangedFiles(recoveryFileHashCode, currentFileHashCode)
        findDeleteFiles(recoveryFileHashCode, currentFileHashCode)

        return mapFilesChanged
    }

    /**
     * Start file monitor
     * @param arrayFiles create a file tracking according array files
     */
    public void mapRefresh(ArrayList<File> arrayFiles) {
        currentFileHashCode = loadSignatureForFilesInDirectory(arrayFiles)
        saveMap(currentFileHashCode)
    }

    /**
     * Update changed value hasCode file
     * @throws IOException if no cant write in the disk
     */
    public Map getFoldersFiltered(ArrayList<String> folders, Map<String, ResultTracker> mapFilesChanged) {
        Map auxiliaryMap = [:]

        mapFilesChanged.each { fileName, resultTracker ->

            String parentFile = new File(fileName).getParentFile().getName()
            folders.each { nameFolder ->

                if (parentFile == nameFolder) {
                    auxiliaryMap.put(fileName, resultTracker)
                }
            }
        }

        return auxiliaryMap
    }

    /**
     * Save only elements changed in the map
     * @param mapFileChanged is the files changed
     */
    public void saveMapUpdated(Map mapFileChanged) {
        mapFileChanged.each { fileName, status ->
            if (status == DELETE_FILE) {
                recoveryFileHashCode.remove(fileName)
            } else {
                recoveryFileHashCode.put(fileName, currentFileHashCode.get(fileName))
            }
        }
        saveMap(recoveryFileHashCode)
    }

    /**
     * Save information about codeHas in the map
     * @param myMap it is the map that will be saved
     */
    public abstract void saveMap(myMap) throws IOException

    /**
     * Get information about codeHas of the files
     * @param source is the path of tracking file
     * @return a map with information names files and codeHash
     */
    public abstract Map readMap(source) throws IOException, ClassNotFoundException

    public Map<String, ResultTracker> getFileTrackerMap(ArrayList<File> files) {

    }
}
