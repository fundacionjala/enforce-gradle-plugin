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
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageBuilder

/**
 * Builds queries from package xml file
 */
@Log
class QueryBuilder {

    private final String SELECT_NAME = 'SELECT Name FROM'
    private final String SELECT_FULL_NAME = 'SELECT FullName FROM'
    private final String WHERE_NAME = 'WHERE Name ='
    private final String WHERE_FULL_NAME = 'WHERE DeveloperName ='
    private final String THERE_IS_NOT_PACKAGE = "There isn't a package xml file in this path: "
    public static final ArrayList<String> defaultComponents = ['ApexClass', 'ApexComponent', 'ApexPage', 'ApexTrigger', 'StaticResource',
                                                               'Profile', 'EmailTemplate', 'CustomField', 'CompactLayout', 'RecordType','ValidationRule']

    public static final ArrayList<String> defaultSubComponents = ['CustomField', 'CompactLayout', 'RecordType','ValidationRule']
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
            if(defaultSubComponents.contains(typeMembers.name)) {
                typeMembers.members.each { member ->
                    if (member != '*') {
                        queries.add("""${SELECT_FULL_NAME} ${typeMembers.name} ${WHERE_FULL_NAME} '${
                            Util.getDeveloperNameByMember(member)
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
            MetadataComponents component = MetadataComponents.getComponentByFolder(folderName)
            if (component && isDefaultComponent(component.getTypeName())) {
                String query = component.getExtension() != 'sbc'?
                    """${SELECT_NAME} ${component.getTypeName()} ${WHERE_NAME} '${
                    Util.getFileName(file.getName())}'""" :
                    """${SELECT_FULL_NAME} ${component.getTypeName()} ${WHERE_FULL_NAME} '${
                        Util.getDeveloperName(file.getName())
                    }'"""
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
        return MetadataComponents.getComponentByFolder(file.getParentFile().getName()).getTypeName()
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
     * Verifies if a component is into default components
     * @param component is type String
     * @return true if there is a component else false
     */
    public boolean isDefaultComponent(String component) {
        boolean result = false
        for (String componentObtained in defaultComponents) {
            if (componentObtained == component) {
                result = true
                break
            }
        }
        return result
    }
}
