package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.filter

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.gradle.api.Project
import org.gradle.api.file.FileTree

class Filter {
    private Project project
    private String projectPath

    Filter(Project project, String projectPath) {
        this.project = project
        this.projectPath = projectPath
    }

    /**
     * Excludes file or files using criterion
     * @param files to exclude
     * @param criterion to filter files
     */
    public ArrayList<File> excludeFilesByCriterion(ArrayList<File> files, String criterion) {
        if (criterion == null) {
            logger.error("${Constants.NULL_PARAM_EXCEPTION} criterion")
        }
        ArrayList<File> filesFiltered = new ArrayList<File>()
        ArrayList<File> sourceFiles = new ArrayList<File>()
        ArrayList<String> criterias = getCriterias(criterion)
        FileTree fileTree = project.fileTree(dir: projectPath, excludes: criterias)
        sourceFiles = fileTree.getFiles() as ArrayList<File>
        sourceFiles.each { File file ->
            if (files.contains(file)) {
                filesFiltered.push(file)
            }
        }
        return filesFiltered
    }

    /**
     * Validates relative path
     * @param relativePath is a relative path of a component
     * @return true if is valid
     */
    public boolean isValidRelativePath(String relativePath) {
        boolean result = false
        String folderName = Util.getFirstPath(relativePath)
        if ( MetadataComponents.validFolder(folderName) && MetadataComponents.getExtensionByFolder(folderName) == ""){
            result = true
        }
        return  result
    }

    /**
     * Gets a criterias to exclde files
     * @param criterion is an String with criterias
     * @return an ArrayList of criterias
     */
    public static ArrayList<String> getCriterias(String criterion) {
        ArrayList<String> criterias = new ArrayList<String>()
        criterion.split(Constants.COMMA).each { String critery ->
            critery = critery.replaceAll(Constants.BACK_SLASH, Constants.SLASH)
            def criteriaSplitted = critery.split(Constants.SLASH)
            if (criteriaSplitted.size() == 1) {
                criterias.push("${critery}${File.separator}${Constants.WILDCARD}${Constants.WILDCARD}")
                return
            }
            criterias.push(critery)
            criterias.push("${critery}${Constants.META_XML}")
        }
        return criterias
    }

    /**
     * Excludes files
     * @param filesToFilter files that will be filter
     * @return ArrayList with files filter
     */
    public ArrayList<File> excludeFiles(ArrayList<File> filesToFilter) {
        if (filesToFilter == null) {
            logger.error("${Constants.NULL_PARAM_EXCEPTION} filesToFilter")
        }
        String excludes
        ArrayList<File> filesFiltered = filesToFilter.clone() as ArrayList<File>
        if (Util.isValidProperty(project, Constants.PARAMETER_EXCLUDES) && !Util.isEmptyProperty(project, Constants.PARAMETER_EXCLUDES)) {
            excludes = project[Constants.PARAMETER_EXCLUDES].toString()
        }
        if (excludes) {
            //validateParameter(excludes) -------> validate content parameter
            filesFiltered = excludeFilesByCriterion(filesFiltered, excludes)
        }
        return filesFiltered
    }

    /**
     * Returns files that were excluded
     * @param criterion is a exclude criterion
     * @return files excluded
     */
    public ArrayList<String> getFilesExcludes(String criterion) {
        ArrayList<String> filesName = new ArrayList<String>()
        ArrayList<File> sourceFiles = new ArrayList<File>()
        ArrayList<String> criterias = getCriterias(criterion)
        FileTree fileTree = project.fileTree(dir: projectPath, includes: criterias)
        sourceFiles = fileTree.getFiles() as ArrayList<File>
        sourceFiles.each { File file ->
            String relativePath = Util.getRelativePath(file, projectPath)
            String extension = Util.getFileExtension(file)
            if ( isValidRelativePath(relativePath)) {
                filesName.push(relativePath)
            }

            if (MetadataComponents.validExtension(extension)) {
                filesName.push(relativePath)
            }
        }
        return filesName.unique()
    }
}