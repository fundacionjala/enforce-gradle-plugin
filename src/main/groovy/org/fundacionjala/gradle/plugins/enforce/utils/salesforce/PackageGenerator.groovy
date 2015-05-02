package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentStates
import org.fundacionjala.gradle.plugins.enforce.filemonitor.FileMonitorSerializer
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ObjectResultTracker
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ResultTracker
import org.fundacionjala.gradle.plugins.enforce.undeploy.SmartFilesValidator
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential

import java.nio.file.Paths

class PackageGenerator {

    PackageBuilder packageBuilder
    FileMonitorSerializer fileMonitorSerializer
    Map<String, ResultTracker> fileTrackerMap
    SmartFilesValidator smartFilesValidator
    Credential credential;

    public PackageGenerator() {
        packageBuilder = new PackageBuilder()
        fileMonitorSerializer = new FileMonitorSerializer()
    }

    public init(String projectPath, ArrayList<File> files, Credential credential) {
        this.credential = credential
        fileMonitorSerializer.setSrcProject(projectPath)
        if (!fileMonitorSerializer.verifyFileMap()) {
            fileMonitorSerializer.mapRefresh(files)
            return
        }
        fileTrackerMap = fileMonitorSerializer.getFileTrackerMap(files)
    }

    public  ArrayList<File> getFiles(){
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

    public void buildDestructive(String path) {
        FileWriter fileWriter = new FileWriter(path)
        this.buildDestructive(fileWriter)
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

    public ArrayList<File> getSubcomponents(ComponentStates subComponentStatus){
        ArrayList<File> filesResult = []
        File file
        fileTrackerMap.each {fileName, resultTracker->
            if(resultTracker instanceof ObjectResultTracker && resultTracker.state == ComponentStates.CHANGED.value() ) {
                resultTracker.subComponentsResult.each { subComponentName, statusField ->
                    if (statusField == subComponentStatus.value()) {
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

          smartFilesValidator = new SmartFilesValidator(SmartFilesValidator.getJsonQueries(files, credential))
          files  = smartFilesValidator.filterFilesAccordingOrganization(files)

          packageBuilder.createPackage(files)
          packageBuilder.write(writer)
        }

public void updateFileTrackerMap(ArrayList<String> folders){
  fileTrackerMap = fileMonitorSerializer.getFoldersFiltered(folders, fileTrackerMap)
}

public ArrayList<File> excludeFiles(ArrayList<File> files){
  ArrayList<File> excludedFiles =  []
  fileTrackerMap.each { fileName, resultTracker ->
      def fileChanged = new File(fileName.toString())
      if (!files.contains(fileChanged)) {
          fileTrackerMap.remove(fileName.toString())
          excludedFiles.push(fileChanged)
      }
  }
  return excludedFiles
}
}
