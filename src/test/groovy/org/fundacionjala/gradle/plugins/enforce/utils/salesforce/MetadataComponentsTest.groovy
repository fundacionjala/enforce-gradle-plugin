/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import spock.lang.Specification

class MetadataComponentsTest extends Specification {

    def "Test get a component"() {
        expect:
            MetadataComponents.getComponent("tabs") instanceof MetadataComponents

    }

    def "Test get a member"() {
        expect:
            MetadataComponents.getComponent("tabs").getTypeName() == "CustomTab"
    }

    def "Test get a extension"() {
        expect:
            MetadataComponents.getComponent("tabs").getExtension() == "tab"
    }

    def "Test get a PermissionSet extension"() {
        expect:
            MetadataComponents.getComponent("permissionsets").getExtension() == "permissionset"
    }

    def "Test get a Community extension"() {
        expect:
            MetadataComponents.getComponent("Communities").getExtension() == "community"
    }

    def "Test get a Scontrol extension"() {
        expect:
            MetadataComponents.getComponent("scontrols").getExtension() == "scf"
    }

    def "Test valid extension"() {
        expect:
            MetadataComponents.validExtension("tab")
    }

    def "Test invalid extension"() {
        expect:
            !MetadataComponents.validExtension("other")
    }

    def "Test valid folder"() {
        expect:
            MetadataComponents.validFolder("classes")
    }

    def "Test invalid folder"() {
        expect:
            !MetadataComponents.validFolder("other")
    }

    def "Test enum name is equals to directory name"() {
        expect:
            MetadataComponents.COMPONENT.each {key, value->
                if(value.extension != 'sbc') {
                    assert key == value.directory.toUpperCase()
                }
            }
    }

    def "Test get a CustomPermission extension"() {
        expect:
            MetadataComponents.getComponent("customPermissions").getExtension() == "customPermission"
    }

    def "Test a component by folder"() {
        expect:
            MetadataComponents.getComponentByPath("classes").typeName == "ApexClass"
    }

    def "Test a component by folder has contains sub folder"() {
        expect:
            MetadataComponents.getComponentByPath("reports/reportTest").typeName == "Report"
    }
}
