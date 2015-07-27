package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.filter

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

/**
 * Filter the subcomponents for delete, according with the configuration in the deleteSubComponents parameters
 */
@Singleton
class FilterSubcomponents {
    private ArrayList<Objects> components
    private ArrayList<String> supportedSubcomponents = ['fields', 'compactLayouts', 'recordTypes', 'validationRules']
    private String projectPath


    /**
     * Gets a list with the filtered files
     * @param filesToFilter is a list with the files to filter
     * @param delete SubComponents is a list with wildcards and  options to customize the filtered
     * @return an ArrayList with the filtered files
     */
    public static ArrayList<File> filter(ArrayList<File> filesToFilter, ArrayList<String>  deleteSubComponents, String projectPath) {
        FilterSubcomponents filter = FilterSubcomponents.instance
        filter.projectPath = projectPath
        filter.components = filter.listEnabledComponents(deleteSubComponents)
        ArrayList<File> filteredFiles = []

        filesToFilter.each { File file ->
            if(filter.isValid(file)) {
                filteredFiles.add(file)
            }
        }
        return filteredFiles
    }

    /**
     * Gets a list with the Components that which must be removed
     * @param delete SubComponents is a list with wildcards and  options to customize the filtered
     * @return an ArrayList with the Components that which must be removed
     */
    public ArrayList<Object> listEnabledComponents(ArrayList<String>  deleteSubComponents){
        ArrayList<Object> enabledComponents= []
        ArrayList<String> listOfComponentsToAdd = []
        ArrayList<String> listOfComponentsToRemove = []
        deleteSubComponents.each { String wildcard ->
            if(wildcard.startsWith("!") && wildcard.length() > Constants.ZERO) {

                listOfComponentsToRemove.add(wildcard.substring(1))
            }
            else if(wildcard.length() > Constants.ZERO) {
                listOfComponentsToAdd.add(wildcard)
            }
        }
        if(listOfComponentsToAdd.contains('*') || listOfComponentsToAdd.isEmpty()) {
            enabledComponents.addAll(getAllComponents())
        }
        listOfComponentsToAdd.each { String typeComponent ->
            def component = MetadataComponents.getComponentByFolder(typeComponent)
            if(component) {
                enabledComponents.add(component)
            }
        }
        listOfComponentsToRemove.each { String typeComponent ->
            def component = MetadataComponents.getComponentByFolder(typeComponent)
            if(component) {
                enabledComponents.remove(component)
            }
        }
        if(deleteSubComponents.isEmpty()) {
            enabledComponents.clear()
        }
        return enabledComponents.unique()
    }

    /**
     * Gets all components registered in our MetadataComponents
     */
    public ArrayList<Object> getAllComponents() {
        ArrayList<Object> enabledComponents= []
        MetadataComponents.COMPONENT.each {key,component ->
            enabledComponents.add(component)
        }
        return enabledComponents
    }

    /**
     * Get a boolean that indicates if a file can be deleted
     * @param file to be evaluated
     * @return a boolean that indicates if a file can be deleted
     */
    public boolean isValid(File file) {
        def relativePath = Util.getRelativePath(file, projectPath)
        def folderPath  = Util.getFirstPath(relativePath)
        def component = MetadataComponents.getComponentByFolder(folderPath)
        if(components.contains(component) || !supportedSubcomponents.contains(component.getDirectory())) {
            return true
        }
        return false
    }
}
