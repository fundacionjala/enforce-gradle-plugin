package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentMonitor
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentStates
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ObjectResultTracker
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ResultTracker
import org.fundacionjala.gradle.plugins.enforce.undeploy.SmartFilesValidator
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential

import java.nio.file.Paths

class PackageGenerator {
    PackageBuilder packageBuilder
    ComponentMonitor componentMonitor
    Map<String, ResultTracker> fileTrackerMap
    SmartFilesValidator smartFilesValidator
    Credential credential
    String projectPath

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

    public ArrayList<File> getFiles() {
        ArrayList<File> files = []
        fileTrackerMap.each { fileName, resultTracker ->
            files.add(new File(fileName))
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
        packageBuilder.createPackage(files, projectPath)
        packageBuilder.write(writer)
    }

    public void buildDestructive(Writer writer) {
        ArrayList<File> files = getFiles(ComponentStates.DELETED) + getSubComponents(ComponentStates.DELETED)
        smartFilesValidator = new SmartFilesValidator(SmartFilesValidator.getJsonQueries(files, credential))
        buildDestructive(writer, smartFilesValidator)
    }

    public void buildDestructive(Writer writer, SmartFilesValidator smartFilesValidator) {
        ArrayList<File> files = getFiles(ComponentStates.DELETED) + getSubComponents(ComponentStates.DELETED)
        files = smartFilesValidator.filterFilesAccordingOrganization(files)
        packageBuilder.createPackage(files, projectPath)
        packageBuilder.write(writer)
    }

    public void updateFileTrackerMap(ArrayList<String> folders) {
        fileTrackerMap = componentMonitor.getFoldersFiltered(folders, fileTrackerMap)
    }

    public ArrayList<File> excludeFiles(ArrayList<File> files) {
        Map<String, ResultTracker> fileTrackerMapClone = fileTrackerMap.clone() as Map<String, ResultTracker>
        ArrayList<File> excludedFiles = []
        fileTrackerMapClone.each { fileName, resultTracker ->
            File fileChanged = new File(fileName.toString())
            if (!files.contains(fileChanged)) {
                fileTrackerMap.remove(fileName.toString())
                excludedFiles.push(fileChanged)
            }
        }
        return excludedFiles
    }
}
