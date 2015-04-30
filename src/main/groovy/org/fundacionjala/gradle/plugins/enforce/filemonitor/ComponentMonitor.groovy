package org.fundacionjala.gradle.plugins.enforce.filemonitor

class ComponentMonitor {

    //Map<String, ComponentTracker> currentFileHashCode
    //Map<String, ComponentTracker> recoveryFileHashCode

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
}
