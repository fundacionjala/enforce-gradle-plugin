/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager

import com.sforce.soap.metadata.PackageTypeMembers
import groovy.util.logging.Log
import groovy.util.slurpersupport.GPathResult
import groovy.xml.DOMBuilder
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
import groovy.xml.dom.DOMCategory
import org.fundacionjala.gradle.plugins.enforce.undeploy.PackageComponent
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.wsc.Connector
import org.w3c.dom.Document
import org.w3c.dom.Element

import java.nio.file.Paths

/**
 *  Builds a Package instance from package XML and write a package XML file from a package instance
 */
@Log
class PackageBuilder {
    private static final String XMLNS = 'http://soap.sforce.com/2006/04/metadata'
    private static final String VERSION = '1.0'
    private static final String ENCODING = 'UTF-8'
    private final String TAG_NAME = 'name'
    private final String TAG_MEMBERS = 'members'
    private final String TAG_TYPES = 'types'
    private final String WILDCARD = '*'
    Package metaPackage
    PackageBuilder() {
        metaPackage = new Package()
    }

    /**
     * Reads all the data from a reader that represents a package file
     * @param reader for reading character streams
     */
    public void read(Reader reader) {
        GPathResult Package = new XmlSlurper().parse(reader)
        ArrayList<PackageTypeMembers> packageData = new ArrayList<PackageTypeMembers>()

        Package.types.each { type ->
            List<String> members = new ArrayList<String>()
            PackageTypeMembers packageTypeMembers = new PackageTypeMembers()
            type.members.each { memberName ->
                members.add(memberName)
            }
            packageTypeMembers.members = members.toArray() as String[]
            packageTypeMembers.name = type.name.text()
            packageData.add(packageTypeMembers)
        }

        metaPackage.types = packageData.toArray() as PackageTypeMembers[]
        metaPackage.version = Package.version.text()
    }

    /**
     * Writes the package data on a writer with package XML format
     * @param writer for writing to character streams
     */
    public void write(Writer writer) {
        MarkupBuilder xml = new MarkupBuilder(writer)
        xml.mkp.xmlDeclaration(version: Package.VERSION, encoding: Package.ENCODING)

        xml.Package(xmlns: Package.XMLNS) {
            metaPackage.types.each { packageTypeMembers ->
                types() {
                    packageTypeMembers.members.each { memberName ->
                        members(memberName)
                    }
                    mkp.yieldUnescaped "\n\t<${TAG_NAME}>${packageTypeMembers.name}</${TAG_NAME}>"
                }
            }
            xml.version(metaPackage.version)
        }
    }

    /**
     * Updates the package object with an array members and a type name
     * @param name represents a type name in package xml file
     * @param members an array list of members to update
     */
    public void update(String name, ArrayList<String> members) {
        if (members.isEmpty()) {
            return
        }
        ArrayList<PackageTypeMembers> packageTypeMembers = metaPackage.types.toList()

        PackageTypeMembers packageTypeMembersFound = null
        packageTypeMembers.find { packageTypeMembersIt ->
            if (packageTypeMembersIt.name == name) {
                packageTypeMembersFound = packageTypeMembersIt
                return true
            }
        }
        if (!packageTypeMembersFound) {
            packageTypeMembers.addAll(getPackageTypeMembers(name, members))
            metaPackage.types = packageTypeMembers.toArray() as PackageTypeMembers[]
        } else {
            List packageTypes = packageTypeMembersFound.members.toList()
            packageTypes.addAll(members)
            packageTypes = packageTypes.unique()
            packageTypeMembersFound.members = packageTypes.toArray() as String[]
        }
        metaPackage.version = metaPackage.version ?: Package.API_VERSION
    }

    public void removeMembers(String name, ArrayList<String> membersToRemove) {
        if (membersToRemove.isEmpty()) {
            return
        }
        ArrayList<PackageTypeMembers> packageTypeMembers = metaPackage.types.toList()

        PackageTypeMembers packageTypeMembersFound = null
        packageTypeMembers.find { packageTypeMembersIt ->
            if (packageTypeMembersIt.name == name) {
                packageTypeMembersFound = packageTypeMembersIt
                return true
            }
        }
        if (packageTypeMembersFound) {
            List packageTypes = packageTypeMembersFound.members.toList()
            packageTypes.removeAll(membersToRemove)
            packageTypeMembersFound.members = packageTypes.toArray() as String[]
            if (packageTypes.size() <= 0) {
                packageTypeMembers.remove(packageTypeMembersFound)
            }
            metaPackage.types = packageTypeMembers.toArray() as PackageTypeMembers[]
        }
        metaPackage.version = metaPackage.version ?: Package.API_VERSION
    }

    /**
     * Updates the package object with an array members and a type name
     * @param name represents a type name in package xml file
     * @param members an array list of members to update
     * @param packageXmlFile the package.xml file
     */
    public void update(String typeName, ArrayList<String> membersList, File packageXmlFile) {
        if (membersList.isEmpty()) {
            return
        }
        Document document = DOMBuilder.newInstance().parseText(packageXmlFile.text)
        Element packageXml = document.documentElement
        use(DOMCategory) {
            def nodeName = null
            def typeNameItem = packageXml.types.find {
                nodeName = it.getElementsByTagName(TAG_NAME)
                if (nodeName.text() == typeName) {
                    return true
                }
            }
            if (!typeNameItem) {
                def version = packageXml.version.text()
                packageXml.removeChild(packageXml.version.item(0))
                def typesNode = packageXml.appendNode(TAG_TYPES)
                membersList.each { member ->
                    typesNode.appendNode(TAG_MEMBERS, member)
                }
                typesNode.appendNode(TAG_NAME, typeName)
                packageXml.appendNode('version', version)
            } else {
                membersList.each { member ->
                    typeNameItem.appendNode(TAG_MEMBERS, member)
                }
                typeNameItem.removeChild(nodeName.item(0))
                typeNameItem.appendNode(TAG_NAME, typeName)
            }
        }
        packageXmlFile.text = XmlUtil.serialize(packageXml)
    }

    /**
     * Gets an array list of package member objects from type name and member list
     * @param name represents a type name in package xml file
     * @param members an array list of member
     */
    private ArrayList<PackageTypeMembers> getPackageTypeMembers(String name, ArrayList<String> members) {
        ArrayList<PackageTypeMembers> packageTypeMembers = new ArrayList<PackageTypeMembers>()
        PackageTypeMembers packageTypeMember = new PackageTypeMembers()
        packageTypeMember.members = members.toArray() as String[]
        packageTypeMember.name = name
        packageTypeMembers.push(packageTypeMember)

        return packageTypeMembers
    }

    /**
     * Creates package from an array files
     * @param files contains files which will be the body of package
     */
    public void createPackage(ArrayList<File> files, String basePath='') {
        ArrayList<String> folders = selectFolders(files, basePath)
        ArrayList<PackageTypeMembers> packageData = []
        PackageTypeMembers packageTypeMembers
        ArrayList<String> invalidFolders = []
        folders.each { folder ->
            MetadataComponents component = MetadataComponents.getComponentByPath(folder as String)
            if (component) {
                packageTypeMembers = new PackageTypeMembers()
                ArrayList<String> filesMembers = selectFilesMembers(folder, files, basePath)
                packageTypeMembers.members = filesMembers ?: [WILDCARD]
                packageTypeMembers.name = component.getTypeName()
                packageData.push(packageTypeMembers)
            } else {
                invalidFolders.add(folder)
            }
        }
        if (!invalidFolders.isEmpty()) {
            Util.logList(log, Constants.UNSUPPORTED_FOLDERS, invalidFolders)
        }
        String packagePath = Paths.get(basePath, Constants.PACKAGE_FILE_NAME).toString()
        metaPackage.types = packageData
        metaPackage.version = PackageComponent.getApiVersion(packagePath) < Connector.API_VERSION?
                              Connector.API_VERSION : PackageComponent.getApiVersion(packagePath)
    }

    /** Creates a packages by folders
     * @param folders contains the name of folders to create the package
     */
    public void createPackageByFolder(ArrayList<String> folders) {
        ArrayList<PackageTypeMembers> packageData = []
        PackageTypeMembers packageTypeMembers
        ArrayList<String> invalidFolders = []
        folders.each { folder ->
            MetadataComponents component = MetadataComponents.getComponentByPath(folder as String)
            if (component) {
                packageTypeMembers = new PackageTypeMembers()
                packageTypeMembers.members = WILDCARD
                packageTypeMembers.name = component.getTypeName()
                packageData.push(packageTypeMembers)
            } else {
                invalidFolders.add(folder)
            }
        }
        if (!invalidFolders.isEmpty()) {
            Util.logList(log, Constants.UNSUPPORTED_FOLDERS, invalidFolders)
        }
        metaPackage.types = packageData
        metaPackage.version = Connector.API_VERSION
    }

    /**
     * Filters folders from array of files
     * @param files contains all files
     * @return folders
     */
    public ArrayList<String> selectFolders(ArrayList<File> files, String basePath) {
        ArrayList<String> folders = []
        files.each { File file ->
            String relativePath = Util.getRelativePath(file, basePath)
            String folderName = Util.getFirstPath(relativePath)
            if(!folders.contains(folderName)) {
                folders.push(folderName)
            }
        }
        return folders
    }

    /**
     * Selects all files inside folder required
     * @param folder contains the folder to get all files
     * @param files contains the list of files
     * @return all files inside folders
     */
    private ArrayList<String> selectFilesMembers(String folder, ArrayList<File> files, String basePath) {
        ArrayList<String> members = []
        files.each { file ->
            String relativePath = Util.getRelativePath(file, basePath)
            String parentName = Util.getFirstPath(relativePath)
            String fileName = Util.getRelativePath(file, Paths.get(basePath, parentName).toString(), false)
            if (parentName == folder && !fileName.isEmpty()) {
                members.push(Util.getFileName(fileName as String))
            }
        }
        return members.unique()
    }

    /**
     * Write the xml file
     * @param arrayPaths is the paths of files with the xml is generated
     * @return
     */
    def writeInstalledPackageXML(String packageVersion, String packagePassword, Writer writer) {
        MarkupBuilder xml = new MarkupBuilder(writer)
        xml.mkp.xmlDeclaration(version: VERSION, encoding: ENCODING)
        xml.InstalledPackage(xmlns: XMLNS) {
            if (packageVersion) {
                mkp.yieldUnescaped "\n\t<versionNumber>" + packageVersion + "</versionNumber>"
            }
            if (packagePassword) {
                mkp.yieldUnescaped "\n\t<password>" + packagePassword + "</password>"
            }
        }
    }

    /**
     * Updates package xml from source package xml with retrieved files
     * @param retrievedPackagePath is type String
     * @param packageSrcPath is type String
     */
    public static void updatePackageXml(String retrievedPackagePath, String packageSrcPath) {
        PackageBuilder source = new PackageBuilder()
        PackageBuilder retrieved = new PackageBuilder()
        retrieved.read(new FileReader(retrievedPackagePath))
        source.read(new FileReader(packageSrcPath))
        def packageFile = new File(packageSrcPath)
        retrieved.metaPackage.types.each { type ->
            String name = type.name
            ArrayList<String> members = type.members
            ArrayList<PackageTypeMembers> packageTypeMembers = source.metaPackage.types.toList()
            PackageTypeMembers packageTypeMembersFound = null
            ArrayList<String> memberType
            packageTypeMembers.find { packageTypeMembersIt ->
                memberType = packageTypeMembersIt.members
                if (packageTypeMembersIt.name == name) {
                    packageTypeMembersFound = packageTypeMembersIt
                    return
                }
            }
            if (!packageTypeMembersFound) {
                if (memberType.contains(Constants.WILDCARD)) {
                    members = [Constants.WILDCARD]
                }
            } else {
                packageTypeMembersFound.members.each { member ->
                    if (members.contains(member)) {
                        members.remove(member)
                    }
                    if (member == Constants.WILDCARD) {
                        members = []
                    }
                }
            }
            source.update(name, members, packageFile)
        }
    }
}
