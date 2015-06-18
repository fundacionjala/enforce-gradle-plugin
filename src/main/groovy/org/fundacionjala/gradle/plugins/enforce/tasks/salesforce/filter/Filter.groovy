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
     * Gets a criteria to folders files and wildcards
     * @param criterion is an String with criteria
     * @return an ArrayList of criteria
     */
    public static ArrayList<String> getCriteria(String criterion) {
        ArrayList<String> criteria = new ArrayList<String>()
        criterion.split(Constants.COMMA).each { String critery ->
            if (!critery.contains('.')) {
                criteria.push("${critery}${File.separator}${Constants.WILDCARD}${Constants.WILDCARD}")
                return
            }
            criteria.push(critery)
            criteria.push("${critery}${Constants.META_XML}")
        }
        return criteria
    }

    /**
     * Gets content parameters
     * @param parametersName is an array lis with parameters names
     * @param properties is a map with parameters names and its values
     * @return a map with parameter name as key and its content as value
     */
    public static Map<String,String> getContentParameter(ArrayList<String> parametersName, Map<String, String> properties) {
        Map<String,String> result = [:]
        parametersName.each {String parameter ->
            if (Util.isValidProperty(properties, parameter) && !Util.isEmptyProperty(properties, parameter)) {
                result.put(parameter, properties[parameter].toString())
            }
        }
        return result
    }

    /**
     * Gets files from project directory by parameter and all by default
     * @param parametersName is an array list with parameters names
     * @param properties is a map with parameter name as key and its content as value
     * @return an array list of files
     */
    public ArrayList<File> getFiles(ArrayList<String> parametersName, Map<String, String> properties) {
        Map<String, String> contentParameterMap = getContentParameter(parametersName, properties)
        ArrayList<String> criteriaToExclude = []
        ArrayList<String> criteriaToInclude = []

        if(contentParameterMap.containsKey(Constants.PARAMETER_EXCLUDES)) {
            criteriaToExclude = getCriteria(contentParameterMap.get(Constants.PARAMETER_EXCLUDES))
        }

        if(contentParameterMap.containsKey(Constants.PARAMETER_FILES)) {
            criteriaToInclude = getCriteria(contentParameterMap.get(Constants.PARAMETER_FILES))
        }
        FileTree fileTree = project.fileTree(dir: projectPath, includes: criteriaToInclude, excludes: criteriaToExclude)

        return fileTree.getFiles() as ArrayList<File>
    }
}