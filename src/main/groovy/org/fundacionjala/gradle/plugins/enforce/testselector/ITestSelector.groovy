/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.testselector;

import java.util.ArrayList;

public interface ITestSelector {
    ArrayList<String> getTestClassNames();
}
