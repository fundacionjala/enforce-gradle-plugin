package org.fundacionjala.gradle.plugins.enforce.filemonitor

import com.twmacinta.util.MD5
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.file.Paths

class ComponentMonitor {

    //Map<String, ComponentTracker> currentFileHashCode
    //Map<String, ComponentTracker> recoveryFileHashCode
    public String srcProject

    public ComponentMonitor() {

    }

    /*public final Map<String, ResultTracker> getFileChangedExclude(ArrayList<File> arrayFiles) throws Exception {
        recoveryFileHashCode = readMap(nameFile)
        currentFileHashCode = loadSignatureForFilesInDirectory(arrayFiles)

        return mapFilesChanged
    }*/

    public Map<String, ResultTracker> getFilesChanged(Map<String, ComponentTracker> oldFiles, Map<String, ComponentTracker> currentFiles) {
        Map<String, ResultTracker> result = [:]
        currentFiles.each { String relativePath, ComponentTracker currentComponentTracker ->
            ResultTracker resultTracker
            if (!oldFiles.containsKey(relativePath)) {
                resultTracker = new ResultTracker(ComponentStates.ADDED)
                result.put(relativePath, resultTracker)
            }

            if (oldFiles.containsKey(relativePath) && currentFiles.containsKey(relativePath)) {
                ComponentTracker oldComponentTracker = oldFiles.get(relativePath)
                resultTracker = oldComponentTracker.compare(currentComponentTracker)
                if(resultTracker.state == ComponentStates.CHANGED) {
                    result.put(relativePath, resultTracker)
                }
            }
        }

        oldFiles.each { String relativePath, ComponentTracker oldComponentTracker->
            if(!currentFiles.containsKey(relativePath)) {
                ResultTracker resultTracker = new ResultTracker(ComponentStates.DELETED)
                result.put(relativePath, resultTracker)
            }
        }
        return result
    }

    public Map<String, ComponentTracker> getComponentsSignature(ArrayList<File> files) {
        Map<String, ComponentTracker> result = [:]
        files.each {File file->
            ComponentTracker componentTracker
            String signature = MD5.asHex(MD5.getHash(file))
            String relativePath = getPathRelative(file)

            if (Util.getFileExtension(file) == Constants.OBJECT_EXTENSION) {
                componentTracker = new ObjectTracker(relativePath, signature)
            } else {
                componentTracker = new ComponentTracker(relativePath, signature)
            }
            result.put(relativePath, componentTracker)
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
}
