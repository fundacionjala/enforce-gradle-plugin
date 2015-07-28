/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.file.Paths

/**
 * Represents all directories types on salesforce
 */
public enum MetadataComponents {
    PERMISSIONSETS("PermissionSet", "permissionset", "permissionsets"),
    COMMUNITIES("Community", "community", "communities"),
    SCONTROLS("Scontrol", "scf", "scontrols", Constants.CONTAINS_XML_FILE),
    SAMLSSOCONFIGS("SamlSsoConfig", "samlssoconfig", "samlssoconfigs"),
    WORKFLOWS("Workflow", "workflow", "workflows"),
    SETTINGS("Settings", "settings", "settings"),
    FLEXIPAGES("FlexiPage", "flexipage", "flexipages"),
    REMOTESITESETTINGS("RemoteSiteSetting", "remoteSite", "remoteSiteSettings"),
    ASSIGNMENTRULES("AssignmentRules", "assignmentRules", "assignmentRules"),
    OBJECTTRANSLATIONS("CustomObjectTranslation", "objectTranslation", "objectTranslations"),
    APPROVALPROCESSES("ApprovalProcess", "approvalProcess", "approvalProcesses"),
    SHARINGRULES("SharingRules", ".sharingRules", "sharingRules"),
    APPLICATIONS("CustomApplication", "app", "applications"),
    WEBLINKS("CustomPageWebLink", "weblink", "weblinks"),
    DASHBOARDS("Dashboard", "dashboard", "dashboards"),
    OBJECTS("CustomObject", "object", "objects"),
    GROUPS("Group", "group", "groups"),
    STATICRESOURCES("StaticResource", "resource", "staticresources", Constants.CONTAINS_XML_FILE),
    ESCALATIONRULES("EscalationRules", "escalationRules", "escalationRules"),
    REPORTS("Report", "report", "reports"),
    HOMEPAGECOMPONENTS("HomePageComponent", "homePageComponent", "homePageComponents"),
    LABELS("CustomLabels", "labels", "labels"),
    CONNECTEDAPPS("ConnectedApp", "connectedapp", "connectedApps"),
    FLOWS("Flow", "flow", "flows"),
    AUTHPROVIDERS("AuthProvider", "authprovider", "authproviders"),
    INSTALLEDPACKAGES("InstalledPackage", "installedPackage", "installedPackages"),
    EMAIL("EmailTemplate", "email", "email", Constants.CONTAINS_XML_FILE),
    ROLES("Role", "role", "roles"),
    COMPONENTS("ApexComponent", "component", "components", Constants.CONTAINS_XML_FILE),
    CUSTOMAPPLICATIONCOMPONENTS("CustomApplicationComponent", "customApplicationComponent", "customApplicationComponents"),
    LAYOUTS("Layout", "layout", "layouts"),
    HOMEPAGELAYOUTS("HomePageLayout", "homePageLayout", "homePageLayouts"),
    ANALYTICSNAPSHOTS("AnalyticSnapshot", "analyticsnapshot", "analyticSnapshots"),
    AUTORESPONSERULES("AutoResponseRules", "autoResponseRules", "autoResponseRules"),
    DATACATEGORYGROUPS("DataCategoryGroup", "datacategorygroup", "datacategorygroups"),
    CLASSES("ApexClass", "cls", "classes", Constants.CONTAINS_XML_FILE),
    SITES("CustomSite", "site", "sites"),
    DOCUMENTS("Document", "", "documents", Constants.CONTAINS_XML_FILE),
    PAGES("ApexPage", "page", "pages", Constants.CONTAINS_XML_FILE),
    LETTERHEAD("Letterhead", "letter", "letterhead"),
    REPORTTYPES("ReportType", "reportType", "reportTypes"),
    SYNONYMDICTIONARIES("SynonymDictionary", "synonymDictionary", "synonymDictionaries"),
    POSTTEMPLATES("PostTemplate", "postTemplate", "postTemplates"),
    QUICKACTIONS("QuickAction", "quickAction", "quickActions"),
    CALLCENTERS("CallCenter", "callCenter", "callCenters"),
    QUEUES("Queue", "queue", "queues"),
    TRIGGERS("ApexTrigger", "trigger", "triggers", Constants.CONTAINS_XML_FILE),
    PROFILES("Profile", "profile", "profiles"),
    TABS("CustomTab", "tab", "tabs"),
    TRANSLATIONS("Translations", "translation", "translations"),
    FIELDS("CustomField", "sbc", "fields"),
    COMPACTLAYOUTS("CompactLayout", "sbc", "compactLayouts"),
    RECORDTYPES("RecordType", "sbc", "recordTypes"),
    VALIDATIONRULES("ValidationRule", "sbc", "validationRules"),
    BUSINESSPROCESSES("BusinessProcess", "sbc", "businessProcesses"),
    FIELDSETS("FieldSet", "sbc", "fieldSets"),
    LISTVIEWS("ListView", "sbc", "listViews"),
    SHARINGREASON("SharingReason", "sbc", "SharingReason"),
    OBJECTWEBLINKS("Weblink", "sbc", "webLinks"),
    CUSTOMPERMISSIONS("CustomPermission", "customPermission", "customPermissions")


    public final static Map<String, MetadataComponents> COMPONENT;

    static {
        COMPONENT = new HashMap<String, MetadataComponents>();

        for (MetadataComponents input : values()) {
            COMPONENT.put(input.name(), input);
        }
    }

    private final String typeName
    private final String extension
    private final String directory
    private final boolean containsXMLFile

    MetadataComponents(String typeName, String extension, String directory, boolean containsXMLFile = false) {
        this.typeName = typeName
        this.extension = extension
        this.directory = directory
        this.containsXMLFile = containsXMLFile
    }

    String getTypeName() {
        return typeName
    }

    String getExtension() {
        return extension
    }

    String getDirectory() {
        return directory
    }

    boolean containsXMLFile() {
        return containsXMLFile
    }

    public static MetadataComponents getComponent(String name) {
        return COMPONENT.get(name.toUpperCase())
    }

    /**
     * Gets a MetadataComponent by path
     * @param path is the relative path in the project
     * @return a MetadataComponent
     */
    public static MetadataComponents getComponentByPath(String path) {
        MetadataComponents metadataComponent
        String folder = Util.getFirstPath(path)
        for (MetadataComponents component : values()) {
            if (component.getDirectory() == folder) {
                metadataComponent = component
                break
            }
        }
        return metadataComponent
    }

    /**
     * Gets a MetadataComponent by name
     * @param name is component name
     * @return a MetadataComponent object
     */
    public static MetadataComponents getComponentByName(String name) {

        MetadataComponents metadataComponent
        for (MetadataComponents component : values()) {
            if (component.getTypeName() == name) {
                metadataComponent = component
                break
            }
        }
        return metadataComponent
    }

    /**
     * gets a component by folder
     * @param folder is folder component
     * @return a metadataComponent object
     */
    public static getComponentByFolder(String folder) {
        MetadataComponents metadataComponent
        for (MetadataComponents component : values()) {
            if (component.getDirectory() == folder) {
                metadataComponent = component
                break
            }
        }
        return metadataComponent
    }

    /**
     * Gets a MetadataComponent by extension
     * @param extension is component extension
     * @return a MetadataComponent object
     */
    public static MetadataComponents getComponentByExtension(String extension) {

        MetadataComponents metadataComponent
        for (MetadataComponents component : values()) {
            if (component.getExtension() == extension) {
                metadataComponent = component
                break
            }
        }
        return metadataComponent
    }

    /**
     * Gets a extension of component by folder
     * @param folder is a component folder
     * @return a extension of component
     */
    public static String getExtensionByFolder(String folder) {

        String extensionByFolder
        for (MetadataComponents input : values()) {
            if (input.getDirectory() == folder) {
                extensionByFolder = input.getExtension()
                break
            }
        }
        return extensionByFolder
    }

    /**
     * Gets a extension of component by name
     * @param name is component name
     * @return a extension o component
     */
    public static String getExtensionByName(String name) {
        String extensionByName
        for (MetadataComponents input : values()) {
            if (input.getTypeName() == name) {
                extensionByName = input.getExtension()
                break
            }
        }
        return extensionByName
    }

    /**
     * Gets a directory of component by name
     * @param name is a component name
     * @return a directory of component
     */
    public static String getDirectoryByName(String name) {
        String directory
        for (MetadataComponents input : values()) {
            if (input.getTypeName() == name) {
                directory = input.getDirectory()
                break
            }
        }
        return directory
    }

    /**
     * Validates a component extension
     * @param extension is a component extension
     * @return true if extension is valid
     */
    public static boolean validExtension(String extension) {
        for (MetadataComponents input : values()) {
            if (input.getExtension() == extension) {
                return true
            }
        }
        return false
    }

    /**
     * Validates a component folder
     * @param folderName is a component folder
     * @return true if folder is valid
     */
    public static boolean validFolder(String folderName) {
        for (MetadataComponents input : values()) {
            if (input.name() == folderName.toUpperCase()) {
                return true
            }
        }
        return false
    }
}