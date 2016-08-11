package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentMonitor
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentStates
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ObjectResultTracker
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ResultTracker
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.OrgValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.filter.FilterSubcomponents
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.gradle.api.Project

import java.nio.file.Paths

class PackageGenerator {
    public PackageBuilder packageBuilder
    public ComponentMonitor componentMonitor
    public Map<String, ResultTracker> fileTrackerMap
    public Credential credential
    public String projectPath
    public Project project

    public PackageGenerator() {
        packageBuilder = new PackageBuilder()
        componentMonitor = new ComponentMonitor()
    }

    public void init(String projectPath, ArrayList<File> files, Credential credential) {
        this.projectPath = projectPath
        this.credential = credential
        componentMonitor = new ComponentMonitor(projectPath)
        if (!componentMonitor.verifyFileMap()) {
            componentMonitor.saveCurrentComponents(files)
            return
        }
        fileTrackerMap = componentMonitor.getComponentChanged(files)
    }

    public void init(String projectPath, ArrayList<File> files, Credential credential, Project project) {
        this.projectPath = projectPath
        this.credential = credential
        this.project = project
        componentMonitor = new ComponentMonitor(projectPath)
        if (!componentMonitor.verifyFileMap()) {
            componentMonitor.saveCurrentComponents(files)
        }
        fileTrackerMap = componentMonitor.getComponentChanged(files)
    }

    public ArrayList<File> getFiles(String projectPath) {
        ArrayList<File> files = []
        fileTrackerMap.each { fileName, resultTracker ->
            files.add(new File(Paths.get(projectPath, fileName).toString()))
        }
        return files
    }

    public void buildPackage(String path) {
        FileWriter fileWriter = new FileWriter(path)
        this.buildPackage(fileWriter)
    }

    public void saveFileTrackerMap(){
        componentMonitor.saveMapUpdated(fileTrackerMap)
    }

    public void buildDestructive(String path) {
        FileWriter fileWriter = new FileWriter(path)
        this.buildDestructive(fileWriter)
    }

    public ArrayList<File> getFiles(ComponentStates status) {
        ArrayList<File> filesPackage = []
        fileTrackerMap.each { fileName, resultTracker ->
            if (resultTracker.state == status) {
                filesPackage.add(new File(Paths.get(projectPath, fileName).toString()))
            }
        }
        return filesPackage
    }

    public ArrayList<File> getSubComponents(ComponentStates subComponentStatus) {
        ArrayList<File> filesResult = []
        File file
        fileTrackerMap.each { fileName, resultTracker ->
            if (resultTracker instanceof ObjectResultTracker && resultTracker.state == ComponentStates.CHANGED) {
                resultTracker.subComponentsResult.each { subComponentName, statusField ->
                    if (statusField == subComponentStatus) {
                        String fileSubComponentName = "${Paths.get(subComponentName).parent.fileName}/${Util.getFileName(Paths.get(fileName).fileName.toString())}.${Paths.get(subComponentName).fileName}.sbc"
                        String path = Paths.get(projectPath, fileSubComponentName.toString()).toString();
                        file = new File(path)
                        filesResult.add(file)
                    }
                }
            }
        }
        return filesResult
    }

    public void buildPackage(Writer writer) {
        ArrayList<File> files = getFiles(ComponentStates.ADDED) + getFiles(ComponentStates.CHANGED) + getSubComponents(ComponentStates.ADDED) + getSubComponents(ComponentStates.CHANGED)
        files.sort()
        packageBuilder.createPackage(files, projectPath)
        packageBuilder.write(writer)
    }

    public void buildDestructive(Writer writer) {
        ArrayList<File> files = getFiles(ComponentStates.DELETED) + getSubComponents(ComponentStates.DELETED)
        files = FilterSubcomponents.filter(files, project.enforce.deleteSubComponents, projectPath)
        if(!project.properties.get(Constants.PARAMETER_VALIDATE_ORG).equals(Constants.FALSE_OPTION)) {
            files = OrgValidator.getValidFiles(credential, files, projectPath)
        }
        packageBuilder.createPackage(files, projectPath)
        packageBuilder.write(writer)
    }

    /**
     * Updates the file tracker map according to the filtered files
     * @param filteredFiles the filtered files
     */
    public void updateFileTracker(ArrayList<File> filteredFiles) {
        if (fileTrackerMap == null) {
            loadFileTrackerMap()
            return
        }
        Map<String, ResultTracker> fileTrackerMapClone = fileTrackerMap.clone() as Map<String, ResultTracker>
        fileTrackerMapClone.each { fileName, resultTracker ->
            File fileChanged = new File(fileName.toString())
            ArrayList<File> foundFile = filteredFiles.findAll { file->
                file.name == fileChanged.name
            }
            if (foundFile.size() == Constants.ZERO) {
                fileTrackerMap.remove(fileName.toString())
            }
        }
    }

    /**
     * Loads fileTrackerMap
     */
    private void loadFileTrackerMap() {
        ManagementFile managementFile = new ManagementFile(projectPath)
        ArrayList<File> sourceComponents = managementFile.getValidElements(projectPath, ['xml'])
        componentMonitor.saveCurrentComponents(sourceComponents)
        fileTrackerMap = componentMonitor.getComponentChanged(sourceComponents)
    }
}
