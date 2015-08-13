package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.runtesttask

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentMonitor
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentStates
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ResultTracker
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.file.Paths

class CustomComponentTracker {
    public static Map<String, ResultTracker> customComponentTrackerMap
    static String fileTrackerName = '.customComponentTracker.data'
    private static String XML_EXTENSION = 'xml'
    private static String PROJECT_DIRECTORY_DOES_NOT_EXIT = "doesn't exist!"

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
        if (!new File(projectPath).exists()) {
            throw new Exception("${projectPath} ${PROJECT_DIRECTORY_DOES_NOT_EXIT}")
        }
        String fileTrackerPath = Paths.get(projectPath, fileTrackerName)
        ComponentMonitor componentMonitor = new ComponentMonitor(projectPath, fileTrackerPath)
        ArrayList<File> components = getFilesFromProjectPath(projectPath)
        if (!componentMonitor.verifyFileMap()) {
            componentMonitor.saveCurrentComponents(components)
        } else {
            customComponentTrackerMap = componentMonitor.getComponentChanged(components)
        }
    }

    /**
     * Gets valid files from project directory
     * @param projectPath is type String
     * @return an arrayList of valid files
     */
    private static ArrayList<File> getFilesFromProjectPath(String projectPath) {
        ManagementFile managementFile = new ManagementFile(projectPath)
        return managementFile.getValidElements(projectPath, [XML_EXTENSION])
    }
}