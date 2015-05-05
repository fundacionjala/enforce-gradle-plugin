package org.fundacionjala.gradle.plugins.enforce.filemonitor

import com.twmacinta.util.MD5
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.file.Paths

class ComponentMonitor {
    private final String FILE_TRACKING = '.fileTracker.data'
    public Map<String, ComponentHash> currentFileHashCode
    public Map<String, ComponentHash> recoveryFileHashCode
    public String srcProject
    public String fileName
    ComponentSerializer componentSerializer

    public ComponentMonitor(String srcProject) {
        this.srcProject = srcProject
        this.componentSerializer = new ComponentSerializer(srcProject)
        currentFileHashCode = [:]
        recoveryFileHashCode = [:]
    }

    public Map<String, ResultTracker> getComponentChanged(ArrayList<File> arrayFiles) throws Exception {
        recoveryFileHashCode = componentSerializer.read()
        currentFileHashCode = getComponentsSignature(arrayFiles)

        return getFilesChanged(recoveryFileHashCode, currentFileHashCode)
    }

    public Map<String, ResultTracker> getFilesChanged(Map<String, ComponentHash> oldFiles, Map<String, ComponentHash> currentFiles) {
        Map<String, ResultTracker> result = [:]
        currentFiles.each { String relativePath, ComponentHash currentComponentHash ->
            ResultTracker resultTracker
            if (!oldFiles.containsKey(relativePath)) {
                resultTracker = new ResultTracker(ComponentStates.ADDED)
                result.put(relativePath, resultTracker)
            }

            if (oldFiles.containsKey(relativePath) && currentFiles.containsKey(relativePath)) {
                ComponentHash oldComponentHash = oldFiles.get(relativePath)
                resultTracker = oldComponentHash.compare(currentComponentHash)
                if(resultTracker.state == ComponentStates.CHANGED) {
                    result.put(relativePath, resultTracker)
                }
            }
        }

        oldFiles.each { String relativePath, ComponentHash oldComponentHash->
            if(!currentFiles.containsKey(relativePath)) {
                ResultTracker resultTracker = new ResultTracker(ComponentStates.DELETED)
                result.put(relativePath, resultTracker)
            }
        }
        return result
    }

    public Map<String, ComponentHash> getComponentsSignature(ArrayList<File> files) {
        Map<String, ComponentHash> result = [:]
        files.each {File file->
            ComponentHash componentHash
            String signature = MD5.asHex(MD5.getHash(file))
            String relativePath = getPathRelative(file)
            if (Util.getFileExtension(file) == Constants.OBJECT_EXTENSION) {
                ObjectParser objectParser = new ObjectParser()
                componentHash = new ObjectHash(relativePath, signature, objectParser.parseByObjectXML(file))
            } else {
                componentHash = new ComponentHash(relativePath, signature)
            }
            result.put(relativePath, componentHash)
        }
        return result
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

    public void setSrcProject(String srcProject) {
        fileName = Paths.get(srcProject, FILE_TRACKING).toString()
        this.srcProject = srcProject
    }

    /**
     * Verify if file directory exists
     * @return boolean result of file(pathFile).exists
     */
    public boolean verifyFileMap() {
        return new File(fileName).exists()
    }

    /**
     * Start file monitor
     * @param arrayFiles create a file tracking according array files
     */
    public void saveCurrentComponents(ArrayList<File> arrayFiles) {
        currentFileHashCode = getComponentsSignature(arrayFiles)
        componentSerializer.save(currentFileHashCode)
    }

    /**
     * Update changed value hasCode file
     * @throws IOException if no cant write in the disk
     */
    public Map getFoldersFiltered(ArrayList<String> folders, Map<String, ResultTracker> mapFilesChanged) {
        Map foldersFiltered = [:]

        mapFilesChanged.each { fileName, resultTracker ->

            String parentFile = new File(fileName).getParentFile().getName()
            folders.each { nameFolder ->

                if (parentFile == nameFolder) {
                    foldersFiltered.put(fileName, resultTracker)
                }
            }
        }

        return foldersFiltered
    }

    /**
     * Save only elements changed in the map
     * @param mapFileChanged is the files changed
     */
    public void saveMapUpdated(Map<String, ResultTracker> mapFileChanged) {
        mapFileChanged.each { String fileName, ResultTracker resultTracker ->
            if (resultTracker.state == ComponentStates.DELETED) {
                recoveryFileHashCode.remove(fileName)
            }
            else {
                recoveryFileHashCode.put(fileName, currentFileHashCode.get(fileName))
            }
        }

        componentSerializer.save(recoveryFileHashCode)
    }
}
