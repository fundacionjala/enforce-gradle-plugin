/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest.Apex

class ApexTestItem {

    boolean complete

    ArrayList<ApexRunTestResult> apexTestResults

    public ApexTestItem() {
        apexTestResults = new ArrayList<ApexRunTestResult>()
    }
}
