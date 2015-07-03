package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.filter

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.gradle.api.Project
import org.gradle.api.file.FileTree

import java.nio.file.Paths

class Filter {
    private Project project
    private String projectPath
    ArrayList<String> excludeFiles

    Filter(Project project, String projectPath) {
        this.project = project
        this.projectPath = projectPath
        this.excludeFiles = []
    }

    /**
     * Gets a criteria to folders files and wildcards
     * @param criterion is an String with criteria
     * @return an ArrayList of criteria
     */
    public ArrayList<String> getCriteria(String criterion) {
        ArrayList<String> criteria = new ArrayList<String>()
        criterion.split(Constants.COMMA).each { String critery ->
            critery = critery.trim()
            if (!critery.contains(Constants.WILDCARD)){
                File fileFromProjectDirectory = new File(Paths.get(projectPath, critery).toString())
                if (fileFromProjectDirectory.isDirectory()) {
                    criteria.push("${critery}${File.separator}${Constants.WILDCARD}${Constants.WILDCARD}")
                    return
                }
            }
            criteria.push(critery)
            criteria.push("${critery}${Constants.META_XML}")
        }
        return criteria
    }

    /**
     * Gets files from project directory by parameter and all by default
     * @param parametersName is an array list with parameters names
     * @param properties is a map with parameter name as key and its content as value
     * @return an array list of files
     */
    public ArrayList<File> getFiles(String includes, String excludes) {
        ArrayList<String> criteriaToExclude = [Constants.FILE_TRACKER_NAME]
        ArrayList<String> criteriaToInclude = []
        criteriaToExclude.addAll(excludeFiles)
        if(excludes && !excludes.isEmpty()) {
            criteriaToExclude.addAll(getCriteria(excludes))
        }

        if(includes && !includes.isEmpty()) {
            criteriaToInclude = getCriteria(includes)
        }
        FileTree fileTree = project.fileTree(dir: projectPath, includes: criteriaToInclude, excludes: criteriaToExclude)
        return fileTree.getFiles() as ArrayList<File>
    }

}