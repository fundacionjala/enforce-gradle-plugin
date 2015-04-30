package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentStates
import org.fundacionjala.gradle.plugins.enforce.filemonitor.FileMonitorSerializer
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ObjectResultTracker
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ResultTracker
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.file.Paths

class PackageGenerator {

    PackageBuilder packageBuilder
    FileMonitorSerializer fileMonitorSerializer
    Map<String, ResultTracker> fileTrackerMap

    public PackageGenerator() {
        packageBuilder = new PackageBuilder()
        fileMonitorSerializer = new FileMonitorSerializer()
    }

    public init(String projectPath, ArrayList<File> files) {
        fileMonitorSerializer.setSrcProject(projectPath)
        if (!fileMonitorSerializer.verifyFileMap()) {
            fileMonitorSerializer.mapRefresh(files)
            return
        }
        fileTrackerMap = fileMonitorSerializer.getFileTrackerMap(files)
    }

    public void buildPackage(String path) {
        FileWriter fileWriter = new FileWriter(path)
        this.buildPackage(fileWriter)
    }

    public ArrayList<File> getFiles(ComponentStates status) {
        ArrayList<File> filesPackage = []
        fileTrackerMap.each { fileName, resultTracker ->
            if (resultTracker.state == status.value()) {
                filesPackage.add(new File(fileName))
            }
        }
        return filesPackage
    }

    public ArrayList<File> getSubcomponents(ComponentStates status){
        ArrayList<File> filesResult = []
        File file
        fileTrackerMap.each {fileName, resultTracker->
            if(resultTracker instanceof ObjectResultTracker) {
                resultTracker.subComponentsResult.each { subComponentName, statusField ->
                    if (statusField == status.value()) {
                        file = new File("${Paths.get(subComponentName).parent.fileName}/${Util.getFileName(Paths.get(fileName).fileName.toString())}.${Paths.get(subComponentName).fileName}.sbc")
                        filesResult.add(file)
                    }
                }
            }
        }
        return filesResult
    }

    public void buildPackage(Writer writer) {
        ArrayList<File> files = getFiles(ComponentStates.ADDED) + getFiles(ComponentStates.CHANGED) + getSubcomponents(ComponentStates.ADDED) + getSubcomponents(ComponentStates.CHANGED)
        packageBuilder.createPackage(files)
        packageBuilder.write(writer)
    }

    public void buildDestructive(Writer writer) {
        ArrayList<File> files = getFiles(ComponentStates.DELETED) + getSubcomponents(ComponentStates.DELETED)
        packageBuilder.createPackage(files)
        packageBuilder.write(writer)
    }
}
