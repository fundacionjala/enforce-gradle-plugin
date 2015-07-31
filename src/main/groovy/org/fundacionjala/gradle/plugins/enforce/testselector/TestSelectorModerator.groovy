/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.testselector

import org.apache.commons.lang.StringUtils
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.unittest.RunTestTaskConstants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.ToolingAPI
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.logging.Logger

class TestSelectorModerator {

    private ArrayList<String> testClassNameList = []
    private Project project
    private Logger logger
    private String pathClasses
    private ToolingAPI toolingAPI

    /**
     * TestSelectorModerator class constructor
     * @param project instance reference of the current Project
     * @param toolingAPI instance reference of the current ToolingAPI
     * @param pathClasses class path location
     */
    public TestSelectorModerator(Project project, ToolingAPI toolingAPI, String pathClasses) {
        this.project = project
        this.toolingAPI = toolingAPI
        this.pathClasses = pathClasses
    }

    /**
     * Gets all class names that match with the wildcard
     * @param path is the path classes location information
     * @param wildCard is the property sets from user
     */
    public ArrayList<String> getClassNames(String path, String wildCard) {
        FileTree tree = project.fileTree(dir: path)
        tree.include wildCard
        ArrayList<String> classNames = new ArrayList<String>()
        tree.each { File file ->
            if (file.path.endsWith(".${MetadataComponents.CLASSES.getExtension()}") &&
                    StringUtils.containsIgnoreCase(file.text, RunTestTaskConstants.IS_TEST)) {
                classNames.add(Util.getFileName(file.name))
            }
        }
        return classNames
    }

    /**
     * Sets the logger to allow display messages
     * @param logger instance reference of the current Logger
     */
    public void setLogger(Logger logger) {
        this.logger = logger
    }

    /**
     * Collects and returns all test class names for each available TestSelector
     */
    public ArrayList<String> getTestClassNames() {
        ArrayList<String> allTestClassNameList = getClassNames(pathClasses, RunTestTaskConstants.WILDCARD_ALL_TEST)

        if (Util.isValidProperty(project, RunTestTaskConstants.CLASS_PARAM)) {
            this.testClassNameList = (new TestSelectorByDefault(allTestClassNameList,
                                        project.properties[RunTestTaskConstants.CLASS_PARAM].toString())).getTestClassNames()
        }

        if (Util.isValidProperty(project, RunTestTaskConstants.FILE_PARAM)) {
            String fileParamValue = project.properties[RunTestTaskConstants.FILE_PARAM].toString()
            if (this.testClassNameList.size() < allTestClassNameList.size()) {
                Boolean refreshClassAndTestMap = false
                if (project.properties.containsKey(RunTestTaskConstants.REFRESH_PARAM)) { //TODO: get this info from fileTraker[events: update, upload]
                    refreshClassAndTestMap = (project.properties[RunTestTaskConstants.REFRESH_PARAM].toString()).toBoolean()
                }
                ITestSelector selector = new TestSelectorByReference(allTestClassNameList,
                                            this.toolingAPI, fileParamValue, refreshClassAndTestMap)
                selector.setLogger(logger)
                this.testClassNameList.addAll(selector.getTestClassNames())
            }
        }
        else if (!Util.isValidProperty(project, RunTestTaskConstants.CLASS_PARAM)) {
            this.testClassNameList = (new TestSelectorByDefault(allTestClassNameList, null)).getTestClassNames()
        }

        return this.testClassNameList.unique()
    }

}
