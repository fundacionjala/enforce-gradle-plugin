/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.wsc.rest

import com.sforce.soap.metadata.PackageTypeMembers
import groovy.util.logging.Log
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager.PackageBuilder

/**
 * Builds queries from package xml file
 */
@Log
class QueryBuilder {

    private final String SELECT_NAME = 'SELECT Name FROM'
    private final String SELECT_FULL_NAME = 'SELECT FullName FROM'
    private final String WHERE_NAME = 'WHERE Name ='
    private final String WHERE_DEVELOPER_NAME = 'WHERE DeveloperName ='
    private final String WHERE_VALIDATION_NAME = 'WHERE ValidationName ='
    private final String THERE_IS_NOT_PACKAGE = "There isn't a package xml file in this path: "
    public static final ArrayList<String> defaultComponents = ['ApexClass', 'ApexComponent', 'ApexPage', 'ApexTrigger',
                                                               'StaticResource', 'Profile', 'EmailTemplate']

    public static final ArrayList<String> componentCompactLayout = ['CompactLayout']
    public static final ArrayList<String> defaultSubComponents = ['CustomField', 'CompactLayout', 'RecordType','ValidationRule']
    public static final ArrayList<String> validationRule = ['ValidationRule']

    public static final Map<String,ArrayList<String>> mapComponents =
            [
                    'defaultComponent' : defaultComponents,
                    'defaultSubComponent' : defaultSubComponents,
                    'validationRule' : validationRule
            ]
    /**
     * Gets queries of components from package xml file
     * @param packagePath is type String
     * @return array of queries
     */
    public ArrayList<String> createQueryFromPackage(String packagePath) {
        if (!new File(packagePath).exists()) {
            throw new Exception("${THERE_IS_NOT_PACKAGE}${packagePath}")
        }
        ArrayList<String> queries = new ArrayList<String>()
        getComponents(new FileReader(packagePath)).each { typeMembers ->
            if(componentCompactLayout.contains(typeMembers.name)) {
                typeMembers.members.each { member ->
                    if (member != '*') {
                        queries.add("""${SELECT_FULL_NAME} ${typeMembers.name} ${WHERE_DEVELOPER_NAME} '${
                            Util.getDeveloperNameByMember(member, typeMembers.name as String)
                        }'""")
                    }
                }
            } else
            if (isDefaultComponent(typeMembers.name)) {
                queries.add("${SELECT_NAME} ${typeMembers.name}")
            }
        }
        return queries
    }

    /**
     * Gets queries of components from component type
     * @param typecomponent
     * @return query
     */
    public String createQueryFromBasicComponent(String componentType) {
        return "${SELECT_NAME} ${componentType}"
    }

    /**
     * Gets queries that selected a subcomponent
     * @param typecomponent of our subcomponent
     * @param file that needs validate.
     * @return query
     */
    public String createQueryGetSubomponent(String typecomponent, File file) {
        String query
        if(typecomponent == 'CustomField') {
            query = """${SELECT_FULL_NAME} ${typecomponent} ${WHERE_DEVELOPER_NAME} '${Util.getDeveloperName(file.getName())}'"""
        }
        else if(typecomponent == 'CompactLayout') {
            query = """${SELECT_FULL_NAME} ${typecomponent} ${WHERE_DEVELOPER_NAME} '${Util.getDeveloperName(file.getName())}'"""
        }
        else if(typecomponent == 'ValidationRule') {
            query = """${SELECT_FULL_NAME} ${typecomponent} ${WHERE_VALIDATION_NAME} '${Util.getDeveloperName(file.getName())}'"""
        }
        else if(typecomponent == 'RecordType') {
            query = """${SELECT_FULL_NAME} ${typecomponent} ${WHERE_NAME} '${Util.getDeveloperName(file.getName())}'"""
        }
        return query

    }

    /**
     * Gets queries of components from list of files
     * @param ArrayList of files to execute query
     * @return array of queries String format
     */
    public ArrayList<String> createQueriesFromListOfFiles(ArrayList<File> files) {
        if(files == null){
            throw new NullPointerException(String.format(Constants.NULL_PARAM_EXCEPTION, "files"))
        }
        ArrayList<String> queries = new ArrayList<String>()
        ArrayList<String> invalidFolders = []
        files.each { file ->
            String folderName = file.getParentFile().getName()
            MetadataComponents component = MetadataComponents.getComponentByPath(folderName)
            if (component && isDefaultComponent(component.getTypeName())) {
                String componentGroup = getGroupComponent(component.getTypeName())
                String query = ""
                if(componentGroup.equals("defaultComponent")) {
                    query = """${SELECT_NAME} ${component.getTypeName()} ${WHERE_NAME} '${Util.getFileName(file.getName())}'"""
                }
                else if(componentGroup.equals("defaultSubComponent")) {
                    query = """${SELECT_FULL_NAME} ${component.getTypeName()} ${WHERE_DEVELOPER_NAME} '${Util.getDeveloperName(file.getName())}'"""
                }
                else if(componentGroup.equals("validationRule")) {
                    query = """${SELECT_FULL_NAME} ${component.getTypeName()} ${WHERE_VALIDATION_NAME} '${Util.getDeveloperName(file.getName())}'"""
                }
                queries.add(query)
            } else {
                invalidFolders.add(folderName)
            }
        }
        if (!invalidFolders.isEmpty()) {
            Util.logList(log, Constants.UNSUPPORTED_FOLDERS, invalidFolders)
        }
        return queries
    }

    /**
     * Gets sales force's component
     * @param reader is type file to look get type name
     * @return String which contain type name of file
     */
    public String getComponent(File file) {
        return MetadataComponents.getComponentByPath(file.getParentFile().getName()).getTypeName()
    }

    /**
     * Gets sales force's components from package xml file
     * @param reader is type Reader
     * @return arrayList with sales force's components
     */
    public ArrayList<PackageTypeMembers> getComponents(Reader reader) {
        PackageBuilder packageBuilder = new PackageBuilder()
        packageBuilder.read(reader)
        return packageBuilder.metaPackage.types
    }

    /**
     * Verifies if a component is into components map
     * @param component is type String
     * @return true if there is a component else false
     */

    public boolean isDefaultComponent(String component) {
        boolean result = false
        mapComponents.each { group, components ->
            if(components.contains(component)) {
                result = true
            }
        }
        return result
    }

    /**
     * Selected the validation group that belongs a component
     * @param component is type String
     * @return the group of component name
     */
    public String getGroupComponent(String component) {
        String result = "default"
        mapComponents.each { group, components ->
            if(components.contains(component)) {
                result = group
            }
        }
        return result
    }
}
