/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.undeploy

import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.wsc.Connector
import org.gradle.api.GradleException

/**
 * This class represents all components that are in a local org
 */
class PackageComponent {
    Map directories = [:]
    List components = []
    List componentsTruncated = []
    private final String METADATA_NAME = 'metadata.json'
    List truncatedDirectories = ['classes', 'objects', 'triggers', 'pages', 'components', 'workflows', 'tabs']

    /**
     * Loads all directories and components names of the local org from package path
     * @param packagePath the package path of the org
     */
    PackageComponent(String packagePath) {
        this(new File(packagePath))
    }

    /**
     * Loads all directories and components names of the local org from package file
     * @param packageFile the package file the org
     */
    PackageComponent(File packageFile) {
        directories = getComponentsFromPackage(packageFile)
        components = getDirectoryWildcards()
        componentsTruncated = getDirectoryTruncatedWildcards()
    }

    /**
     * Gets all wildcards of directories and its components from the directories structure
     * @return A string array with all wildcards of directories and its components
     */
    public ArrayList<String> getDirectoryWildcards() {
        List directoryWildcard = []
        directories.each { dir, components ->
            components.each { component ->
                String wildCard = "${dir}${File.separator}${component}"
                directoryWildcard.add(wildCard)
            }
        }
        return directoryWildcard
    }

    /**
     * Gets all wildcards of directories truncated and its components from the directories structure
     * @return A string array with all wildcards of directories truncated and its components
     */
    public ArrayList<String> getDirectoryTruncatedWildcards() {
        List directoryWildcard = []
        directories.each { dir, components ->
            if (isPackageTruncated(dir)) {
                components.each { component ->
                    String wildCard = "${dir}${File.separator}${component}"
                    directoryWildcard.add(wildCard)
                }
            }
        }
        return directoryWildcard
    }

    /**
     * Gets a map with all directory names with its components from package.xml content
     * @param packageContent the package.xml file content
     * @return a map with all directory names with its components
     */
    private Map getComponentsFromPackage(File packageFile) {

        def Package = new XmlParser().parse(packageFile)
        def files = []
        def directories = [:]
        Package.types.each { type ->
            type.members.each { memberName ->
                def fileExtension = MetadataComponents.getExtensionByName(type.name.text() as String)
                if (!memberName.text().equals("*")) {
                    files.add("${memberName.text()}.${fileExtension}")
                } else {
                    def wildCard = "*.${fileExtension}"
                    files.add(wildCard)
                }
            }

            directories.put(MetadataComponents.getDirectoryByName(type.name.text() as String), files.clone())
            files.clear()
        }
        return directories
    }

    /**
     * Verifies if a directory name is in the truncated list
     * @param dirName directory name from org source code
     * @return true if the truncated list contains the directory name
     */
    public boolean isPackageTruncated(String dirName) {
        return truncatedDirectories.contains(dirName)
    }

    /**
     * Gets the API version from org package.xml
     * @param packagePath the package.xml path
     */
    static String getApiVersion(String packagePath) {
        File packageFile = new File(packagePath)
        String version = Connector.API_VERSION
        if (packageFile.exists()) {
            def Package = new XmlParser().parseText(packageFile.text)
            if (!Package) {
                throw new GradleException("Package content is not valid")
            }
            version = Package.version.text()
        }
        return version
    }

    /**
     * Verifies if exist an object in a directory of objects
     * @param objectDirPath the object directory path
     * @param objectFileName the object file name
     */
    static boolean existObject(String objectDirPath, String objectFileName) {
        File dirObjects = new File(objectDirPath)
        boolean exist = false
        dirObjects.eachFile { objectFile ->
            if (objectFile.name == objectFileName) {
                exist = true
            }
        }
        return exist
    }

}
