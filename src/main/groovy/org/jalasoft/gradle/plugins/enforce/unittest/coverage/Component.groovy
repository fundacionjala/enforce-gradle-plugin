/*
 * Copyright (c) Jalasoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.jalasoft.gradle.plugins.enforce.unittest.coverage

/**
 * This class content component of salesForce
 */
class Component {
    String type
    String path
    String extension
    ArrayList<CoverageElement> elements

    public Component(String type, String path, String extension, ArrayList<CoverageElement> elements) {
        this.type = type
        this.path = path
        this.extension = extension
        this.elements = elements
    }
}
