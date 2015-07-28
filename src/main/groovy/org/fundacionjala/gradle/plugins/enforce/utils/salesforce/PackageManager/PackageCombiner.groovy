package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager

import com.sforce.soap.metadata.PackageTypeMembers
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

class PackageCombiner {
    private static final ArrayList<String> SUB_COMPONENTS = ['CustomField', 'FieldSet', 'ValidationRule',
                                                             'CompactLayout', 'SharingReason', 'RecordType',
                                                             'WebLink', 'SharingRecalculation', 'SearchLayout',
                                                             'ListView', 'HistoryRetentionPolicy', 'BusinessProcess',
                                                             'ActionOverride']
    private static final ArrayList<String> COMPONENTS_WITH_SUB_FOLDERS = ['Document', 'Report', 'Dashboard']
    private static final String CUSTOM_OBJECT = 'CustomObject'

    /**
     * Combines package generated into build directory with package from project directory
     * @param projectPackagePath is a path of package from project directory
     * @param buildPackagePath is a path of package from build directory
     */
    public static void packageCombine(String projectPackagePath, String buildPackagePath) {
        File packageFile = new File(projectPackagePath)
        if (packageFile.exists()) {
            PackageBuilder buildPackage = mergePackage(projectPackagePath, buildPackagePath)
            ArrayList<String> membersToRemove = getObjectMembersToDelete(buildPackage)
            buildPackage.removeMembers(CUSTOM_OBJECT, membersToRemove)
            writePackage(buildPackage, buildPackagePath)
        }
    }

    /**
     * Removes components from package xml file
     * @param packagePath is package path
     * @param excludedFiles is an ArrayList with files name that were excluded
     */
    public static void removeMembersFromPackage(String packagePath, ArrayList<String> excludedFiles) {
        PackageBuilder packageBuilder = new PackageBuilder()
        FileReader packageFileReader = new FileReader(packagePath)
        packageBuilder.read(packageFileReader)
        Map<String, ArrayList<String>> componentToDelete = [:]

        excludedFiles.each { String fileName ->
            String componentType = getComponentType(fileName)
            String componentName = getComponentName(fileName)

            if (!componentToDelete.containsKey(componentType)) {
                componentToDelete.put(componentType, [componentName])
            }

            if (componentToDelete.containsKey(componentType) &&
                    !componentToDelete.get(componentType).contains(componentName)) {
                componentToDelete.get(componentType).push(componentName)
            }
            String parentName = Util.getFirstPath(componentName)
            if (parentName != componentName) {
                componentToDelete.get(componentType).push(parentName)
                componentToDelete.get(componentType).push(Util.getFileName(componentName))
            }
        }

        Map<String, ArrayList<String>> components = [:]

        if (componentToDelete.containsKey(CUSTOM_OBJECT)) {
            components = getComponentsToDelete(componentToDelete, packageBuilder)
        } else {
            components = componentToDelete
        }

        components.each { String componentType, ArrayList<String> componentNames ->
            packageBuilder.removeMembers(componentType, componentNames)
        }
        writePackage(packageBuilder, packagePath)
    }

    /**
     * Combines package file from build directory with package from project directory
     * @param projectPackagePath is the path of package from project directory
     * @param buildPackagePath is the path of package from build directory
     */
    public static void packageCombineToUpdate(String projectPackagePath, String buildPackagePath) {
        PackageBuilder packageFromBuildFolder = getPackageBuilder(buildPackagePath)
        PackageBuilder packageFromSourceFolder = getPackageBuilder(projectPackagePath)
        Map <String, ArrayList<String>> membersByNameOfPackageBuild = getMembersByNameType(packageFromBuildFolder)
        ArrayList<String> objectToDelete = []

        getMembersByNameType(packageFromSourceFolder).each { String name, ArrayList<String> members ->
            if (COMPONENTS_WITH_SUB_FOLDERS.contains(name) && membersByNameOfPackageBuild.containsKey(name)
                    && buildPackagePath.contains(Constants.PACKAGE_FILE_NAME)) {
                ArrayList<String> membersToUpdate = []
                membersByNameOfPackageBuild.get(name).each {String membersFromPackageBuild ->
                    String folderName = Util.getFirstPath(membersFromPackageBuild)
                    if (members.contains(folderName)) {
                        membersToUpdate.push(folderName)
                    }
                }
                packageFromBuildFolder.update(name, membersToUpdate.unique())
            }

            if (SUB_COMPONENTS.contains(name) && membersByNameOfPackageBuild.containsKey(CUSTOM_OBJECT)) {
                members.each { String member ->
                    String objectName = Util.getObjectName(member)
                    if (membersByNameOfPackageBuild.get(CUSTOM_OBJECT).contains(objectName)) {
                        packageFromBuildFolder.update(name, [member])
                        objectToDelete.push(objectName)
                    }
                }
            }
        }

        packageFromBuildFolder.removeMembers(CUSTOM_OBJECT, objectToDelete)
        writePackage(packageFromBuildFolder, buildPackagePath)
    }

    /**
     * Gets an instance of PackageBuilder class
     * @param packagePath is package path
     * @return an instance of PackageBuilder class
     */
    private static PackageBuilder getPackageBuilder(String packagePath) {
        PackageBuilder packageBuilder = new PackageBuilder()
        FileReader packageBuilderReader = new FileReader(packagePath)
        packageBuilder.read(packageBuilderReader)
        return packageBuilder
    }

    /**
     * Gets members of SubComponents
     * @param packageBuilder is of type PackageBuilder
     * @return an ArrayList with members of subComponents
     */
    private static ArrayList<String> getMembersOfSubComponents(PackageBuilder packageBuilder) {
        ArrayList<String> membersOfSubComponent = new ArrayList<String>()
        packageBuilder.metaPackage.types.each { PackageTypeMembers type ->
            if (SUB_COMPONENTS.contains(type.name)) {
                membersOfSubComponent.addAll(type.members as ArrayList<String>)
            }
        }
        return membersOfSubComponent
    }

    /**
     * Gets an array list of members of objects component
     * @param packageBuilder is type packageBuilder
     * @return an ArrayList with members of objects
     */
    private static ArrayList<String> getMembersOfComponent(PackageBuilder packageBuilder, String componentType) {
        ArrayList<String> membersOfCustomObject = new ArrayList<String>()
        packageBuilder.metaPackage.types.each { PackageTypeMembers type ->
            if (type.name == componentType) {
                membersOfCustomObject.addAll(type.members as ArrayList<String>)
            }
        }
        return membersOfCustomObject
    }

    /**
     * Gets a map with name and its members
     * @param packageBuilder is of type PackageBuilder
     * @return a map
     */
    private static Map<String, ArrayList<String>> getMembersByNameType(PackageBuilder packageBuilder) {
        Map<String, ArrayList<String>> membersByNameType = [:]

        packageBuilder.metaPackage.types.each { PackageTypeMembers type ->
            membersByNameType.put(type.name as String, type.members as ArrayList<String>)
        }
        return membersByNameType
    }

    /**
     * Gets components to delete with its sub components
     * @param componentToDelete is a map with components to delete
     * @param componentsFromPackage is a map with all components from package xml file
     * @return a map with components that will delete
     */
    private static Map<String, ArrayList<String>> getComponentsToDelete(Map<String, ArrayList<String>> componentToDelete,
                                                                       PackageBuilder packageBuilder) {

        Map<String, ArrayList<String>> components = componentToDelete.clone()
        Map<String, ArrayList<String>> componentsFromPackage = getMembersByNameType(packageBuilder)

        componentsFromPackage.each { String name, ArrayList<String> members ->
            if (SUB_COMPONENTS.contains(name)) {
                members.each { String member ->
                    String objectName = Util.getObjectName(member)
                    if (components.get(CUSTOM_OBJECT).contains(objectName)) {
                        if (!components.containsKey(name)) {
                            components.put(name, [member])
                        } else {
                            components.get(name).push(member)
                        }
                    }
                }
            }
        }
        return components
    }

    /**
     * Gets a component name without extension
     * @param fileName is component name
     * @return a component name
     */
    private static String getComponentName(String fileName) {
        String componentName = fileName
        if (fileName.contains(Constants.SLASH)) {
            componentName = fileName.substring(fileName.indexOf(Constants.SLASH) + 1, fileName.length())
        }
        if (!componentName.contains(Constants.SLASH)) {
            componentName = Util.getFileName(componentName)
        }
        return componentName
    }

    /**
     * Gets componentType
     * @param fileName is component name
     * @return component type
     */
    private static String getComponentType(String fileName) {
        String folderName = Util.getFirstPath(fileName)
        return MetadataComponents.getComponentByFolder(folderName).getTypeName()
    }

    /**
     * Joins two packages from build directory and project directory
     * @param projectPackagePath is path of package from project directory
     * @param buildPackagePath is path of package from build directory
     * @return a PackageBuilder object merged
     */
    private static PackageBuilder mergePackage(String projectPackagePath, String buildPackagePath) {
        PackageBuilder projectPackage = getPackageBuilder(projectPackagePath)
        PackageBuilder buildPackage = getPackageBuilder(buildPackagePath)

        projectPackage.metaPackage.types.each { PackageTypeMembers type ->
            if (!type.members.toList().contains(Constants.WILDCARD)) {
                buildPackage.metaPackage.types.each { PackageTypeMembers buildType ->
                    buildPackage.removeMembers(type.name as String, buildType.members as ArrayList<String>)
                }
                buildPackage.update(type.name as String, type.members as ArrayList<String>)
            }
        }
        return buildPackage
    }

    /**
     * Gets objects that will be deleted
     * @param membersOfCustomField is a list of members of custom field
     * @param membersOfCustomObject is a list of members of custom object
     * @return an ArrayList with members that will delete
     */
    private static ArrayList<String> getObjectMembersToDelete(PackageBuilder packageBuilder) {
        ArrayList<String> membersOfCustomObject = getMembersOfComponent(packageBuilder, CUSTOM_OBJECT)
        ArrayList<String> membersToRemove = new ArrayList<String>()
        getMembersOfSubComponents(packageBuilder).each { String customFieldMember ->
            String objectName = Util.getObjectName(customFieldMember)
            if (membersOfCustomObject.contains(objectName)) {
                membersToRemove.add(objectName)
            }
        }
        return membersToRemove.unique()
    }

    /**
     * Writes a package combined
     * @param packageBuilder is a package combined
     * @param packageCombinedPath is path where packaged combined will be saved
     */
    private static void writePackage(PackageBuilder packageBuilder, String packageCombinedPath) {
        FileWriter fileWriter = new FileWriter(packageCombinedPath)
        packageBuilder.write(fileWriter)
        fileWriter.close()
    }
}