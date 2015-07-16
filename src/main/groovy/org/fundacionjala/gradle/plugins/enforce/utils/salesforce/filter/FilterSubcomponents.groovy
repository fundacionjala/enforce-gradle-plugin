package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.filter

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

/**
 * Filter the subcomponents for delete, according with the configuration in the deleteSubComponents parameters
 */
class FilterSubcomponents {

    public static FilterSubcomponents filter = new FilterSubcomponents()
    private ArrayList<Objects> components

    /**
     * Gets a FilterSubcomponents instance of type singleTon
     * @return an unique Object FilterSubcomponents
     */
    public static FilterSubcomponents getFilter() {
        return filter
    }

    /**
     * Gets a list with the filtered files
     * @param files is a list with the files to filter
     * @param delete SubComponents is a list with wildcards and  options to customize the filtered
     * @return an ArrayList with the filtered files
     */
    public static ArrayList<File> filter(ArrayList<File> filetToFilter, ArrayList<String>  deleteSubComponents) {
        FilterSubcomponents filter = FilterSubcomponents.getFilter()
        filter.components = filter.listEnabledComponents(deleteSubComponents)
        ArrayList<File> filteredFiles = []

        filetToFilter.each { File file ->
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
        def parentName = file.getParentFile().getName()
        def component = MetadataComponents.getComponentByFolder(parentName)
        if(components.contains(component)) {
            return true
        }
        return false
    }
}
