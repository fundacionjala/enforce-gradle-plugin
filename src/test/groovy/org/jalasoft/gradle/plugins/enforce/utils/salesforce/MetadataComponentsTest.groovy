/*
 * Copyright (c) Jalasoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.jalasoft.gradle.plugins.enforce.utils.salesforce

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
            assert key == value.directory.toUpperCase()
        }
    }

    def "Test a component by folder"() {
        expect:
        MetadataComponents.getComponentByFolder("classes").typeName == "ApexClass"
    }
}
