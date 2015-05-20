/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import com.sforce.soap.metadata.PackageTypeMembers
import groovy.util.logging.Log
import groovy.util.slurpersupport.GPathResult
import groovy.xml.DOMBuilder
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
import groovy.xml.dom.DOMCategory
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.wsc.Connector
import org.w3c.dom.Document
import org.w3c.dom.Element

import java.nio.file.Path
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
    public static final SLASH = '/'
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
            packageTypeMembersFound.members = packageTypes.toArray() as String[]
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
            MetadataComponents component = MetadataComponents.getComponentByRelativePath(folder as String)
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
        metaPackage.types = packageData
        metaPackage.version = Connector.API_VERSION
    }

    /** Creates a packages by folders
     * @param folders contains the name of folders to create the package
     */
    public void createPackageByFolder(ArrayList<String> folders) {

        ArrayList<PackageTypeMembers> packageData = []
        PackageTypeMembers packageTypeMembers
        ArrayList<String> invalidFolders = []
        folders.each { folder ->
            MetadataComponents component = MetadataComponents.getComponentByRelativePath(folder as String)
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
            String relativePath = file.getAbsolutePath().replace(basePath, '')
            String folderName = Paths.get(relativePath).getName(0)
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
            String relativePath = file.getAbsolutePath().replace(basePath, '')
            String parentName = Paths.get(relativePath).getName(0)
            if (parentName == folder && parentName != file.getName()) {
                if(ManagementFile.COMPONENTS_HAVE_SUB_FOLDER.contains(parentName)) {
                    members.addAll(generateMembersByFolderPath(relativePath))
                } else {
                    members.push(Util.getFileName(file.getName() as String))
                }
            }
        }
        return members.unique()
    }

    /**
     * Generates members from folder's relative path based by the sub Paths
     * @param folderPath is a relative path in the project
     * @return a ArrayList<String>
     */
    private ArrayList<String> generateMembersByFolderPath(String folderPath) {
        ArrayList<String> result = []
        Path  path = Paths.get(folderPath);
        StringBuilder member = new StringBuilder()
        for (int index = 1; index < path.getNameCount(); index++) {
            member.append(path.getName(index))
            result.push(Util.getFileName(member.toString() as String))
            member.append(Paths.get(SLASH).toString())
        }
        return result
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
}
