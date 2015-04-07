/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest.Apex

/**
 * Wrapper information about apex test result
 */
class ApexRunTestResult {

    String className
    String apexClassId
    String ApexLogId
    String AsyncApexJobId
    String message = ""
    String methodName
    String outcome
    String QueueItemId
    String stackTrace = ""
    String TestTimestamp
}
