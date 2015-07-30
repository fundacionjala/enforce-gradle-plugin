package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.runtesttask

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentMonitor
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentSerializer
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentStates
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ResultTracker
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.file.Paths

class CustomComponentTracker {
    public static String fileTrackerName =  Paths.get(System.properties['user.home'].toString(), '.customComponentTracker.data')
    public static Map<String, ResultTracker> customComponentTrackerMap
    public static ComponentMonitor componentMonitor
    private static String XML_EXTENSION = 'xml'

    CustomComponentTracker(String projectPath) {
        customComponentTrackerMap = [:]
        saveCustomComponent(projectPath)
    }

    /**
     * Gets array list of files name that were add or changed by extension
     * @param extensions are extensions of SF components
     * @return an array list with files name
     */
    public ArrayList<String> getFilesNameByExtension(ArrayList<String> extensions) {
        ArrayList<String> files = []
        customComponentTrackerMap.each { fileName, resultTracker ->
            if (resultTracker.state != ComponentStates.DELETED) {
                String extension = Util.getFileExtension(new File(fileName))
                if (extensions.contains(extension)) {
                    files.add(new File(fileName).getName())
                }
            }
        }
        return files
    }

    /**
     * Save json file at home directory by default
     * @param projectPath is String type
     */
    public static void saveCustomComponent(String projectPath) {
        componentMonitor = getInstanceOfComponentMonitor(projectPath)
        ArrayList<File> components = getFilesFromProjectPath(projectPath)
        if (!componentMonitor.verifyFileMap()) {
            componentMonitor.saveCurrentComponents(components)
        } else {
            customComponentTrackerMap = componentMonitor.getComponentChanged(components)
        }
    }

    /**
     * Gets an instance of componentMonitor
     * @param projectPath is type String
     * @return an instance of ComponentMonitor
     */
    private static ComponentMonitor getInstanceOfComponentMonitor(String projectPath) {
        ComponentMonitor componentMonitor = new ComponentMonitor()
        componentMonitor.setSrcProject(projectPath)
        componentMonitor.componentSerializer = new ComponentSerializer(fileTrackerName)
        componentMonitor.fileName = fileTrackerName
        return componentMonitor
    }

    /**
     * Gets valid files from project directory
     * @param projectPath is type String
     * @return an arrayList of valid files
     */
    private static getFilesFromProjectPath(String projectPath) {
        ManagementFile managementFile = new ManagementFile(projectPath)
        return managementFile.getValidElements(projectPath, [XML_EXTENSION])
    }
}
