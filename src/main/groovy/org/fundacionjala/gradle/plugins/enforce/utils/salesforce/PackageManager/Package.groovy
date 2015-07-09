/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.PackageManager

/**
 * Represents a package XML file of a organization
 */
class Package extends com.sforce.soap.metadata.Package {
    public static final String XMLNS = 'http://soap.sforce.com/2006/04/metadata'
    public static final String VERSION = '1.0'
    public static final String ENCODING = 'UTF-8'
    public static final String API_VERSION = '32.0'
}
