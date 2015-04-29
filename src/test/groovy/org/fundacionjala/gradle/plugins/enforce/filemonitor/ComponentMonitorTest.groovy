package org.fundacionjala.gradle.plugins.enforce.filemonitor

import spock.lang.Shared
import spock.lang.Specification

class ComponentMonitorTest extends Specification{

    @Shared
    ComponentMonitor componentMonitor

    @Shared
    Map<String, ComponentTracker> oldFiles = [:]

    @Shared
    Map<String, ComponentTracker> currentFiles  = [:]

    @Shared
    ComponentTracker classTracker

    @Shared
    ObjectTracker objectTracker

    def setup() {
        componentMonitor = new ComponentMonitor()
        ComponentTracker oldClassTracker = new ComponentTracker()
        oldClassTracker.hash = 'qweasdzxc'
        ComponentTracker currentClassTracker = new ComponentTracker()
        currentClassTracker.hash = 'rtyfghvbn'
        oldFiles.put('src/classes/Class1.cls', oldClassTracker)
        currentFiles.put('src/classes/Class1.cls', currentClassTracker)
    }

    def "Test should return a map with files changed" () {
        when:
            Map result = componentMonitor.getFilesChanged(oldFiles, currentFiles)
        then:
            result.containsKey('src/classes/Class1.cls')
            result.get('src/classes/Class1.cls').state == 'Changed'
    }

    def "Test should return a map with files were added" () {
        given:
            String newClassPath = 'src/classes/NewClass.cls'
            currentFiles.put(newClassPath, new ComponentTracker('akshdkjashdkj'))
        when:
            Map result = componentMonitor.getFilesChanged(oldFiles, currentFiles)
        then:
            result.containsKey(newClassPath)
            result.get(newClassPath).state == 'Added'
    }

    def "Test should return a map with files were deleted" () {
        given:
            String deletedClassPath = 'src/classes/DeletedClass.cls'
            oldFiles.put(deletedClassPath, new ComponentTracker('akshdkjashdkj'))
        when:
            Map result = componentMonitor.getFilesChanged(oldFiles, currentFiles)
        then:
            result.containsKey(deletedClassPath)
            result.get(deletedClassPath).state == 'Deleted'
    }
}
