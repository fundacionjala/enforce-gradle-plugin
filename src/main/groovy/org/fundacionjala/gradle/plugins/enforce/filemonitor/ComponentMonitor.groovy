package org.fundacionjala.gradle.plugins.enforce.filemonitor

class ComponentMonitor {

    public ComponentMonitor() {

    }

    public Map<String, ResultTracker> getFilesChanged(Map<String, ComponentTracker> oldFiles, Map<String, ComponentTracker> currentFiles) {
        Map<String, ResultTracker> result = [:]
        currentFiles.each { String relativePath, ComponentTracker currentComponentTracker ->
            ResultTracker resultTracker = new ResultTracker()
            if (!oldFiles.containsKey(relativePath)) {
                resultTracker.state = ComponentStates.ADDED.value()
            } else {
                ComponentTracker oldComponentTracker = oldFiles.get(relativePath)
                resultTracker = oldComponentTracker.compare(currentComponentTracker)
            }
            result.put(relativePath, resultTracker)
        }

        oldFiles.each { String relativePath, ComponentTracker oldComponentTracker->
            if(!currentFiles.containsKey(relativePath)) {
                ResultTracker resultTracker = new ResultTracker()
                resultTracker.state = ComponentStates.DELETED.value()
                result.put(relativePath, resultTracker)
            }
        }

        return result
    }


}
