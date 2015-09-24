/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.testselector

import org.apache.commons.lang.StringUtils
import org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.unittest.RunTestTaskConstants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.IArtifactGenerator
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.logging.Logger

import java.nio.file.Paths

class TestSelectorModerator {

    private ArrayList<String> testClassNameList = []
    private Project project
    private Logger logger
    private String pathClasses
    private IArtifactGenerator artifactGenerator
    private Boolean async
    private ArrayList<String> allTestClassNameList
    private ArrayList<String> allApexClassNameList

    /**
     * TestSelectorModerator class constructor
     * @param project instance reference of the current Project
     * @param artifactGenerator instance reference of the current HttpAPIClient
     * @param pathClasses class path location
     */
    public TestSelectorModerator(Project project, IArtifactGenerator artifactGenerator, String pathClasses, Boolean async) {
        this.project = project
        this.artifactGenerator = artifactGenerator
        this.pathClasses = pathClasses
        this.async = async
    }

    /**
     * Gets all class names that match with the wildcard
     * @param path is the path classes location information
     * @param wildCard is the property sets from user
     */
    public void fillClassNames(String path, String wildCard) {
        this.allTestClassNameList = []
        this.allApexClassNameList = []

        FileTree tree = project.fileTree(dir: path)
        tree.include wildCard
        tree.each { File file ->
            if (file.path.endsWith(".${MetadataComponents.CLASSES.getExtension()}")) {
                if (StringUtils.containsIgnoreCase(file.text, RunTestTaskConstants.IS_TEST)) {
                    this.allTestClassNameList.add(Util.getFileName(file.name))
                }
                else {
                    this.allApexClassNameList.add(Util.getFileName(file.name))
                }
            }
        }
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

        makeClassNamesAvailable()//TODO: load on deman

        if (Util.isEmptyProperty(project, RunTestTaskConstants.CLASS_PARAM)) {
            throw new Exception("${RunTestTaskConstants.ENTER_VALID_PARAMETER} "
                    + "${RunTestTaskConstants.CLASS_PARAM}")
        } else if (Util.isValidProperty(project, RunTestTaskConstants.CLASS_PARAM)) {
            this.testClassNameList = (new TestSelectorByDefault(getAllTestClassNameList(),
                                        project.properties[RunTestTaskConstants.CLASS_PARAM].toString())).getTestClassNames()
        }

        if (Util.isEmptyProperty(project, RunTestTaskConstants.FILE_PARAM)) {
            throw new Exception("${RunTestTaskConstants.ENTER_VALID_PARAMETER} "
                    + "${RunTestTaskConstants.FILE_PARAM}")
        } else if (Util.isValidProperty(project, RunTestTaskConstants.FILE_PARAM)) {

            String fileParamValue = project.properties[RunTestTaskConstants.FILE_PARAM].toString()
            logger.debug("ENFORCE - fileParamValue -----> ${fileParamValue}")
            if (this.testClassNameList.size() < getAllTestClassNameList().size()) {
                ArrayList<String> testClassNames = []
                ITestSelector selector

                if (!Util.isEmptyProperty(project, RunTestTaskConstants.REMOTE_PARAM)
                        && (project.properties[RunTestTaskConstants.REMOTE_PARAM].toString()).toBoolean()) {
                    Boolean refreshClassAndTestMap = false
                    if (project.properties.containsKey(RunTestTaskConstants.REFRESH_PARAM)) {
                        refreshClassAndTestMap = (project.properties[RunTestTaskConstants.REFRESH_PARAM].toString()).toBoolean()
                    }
                    selector = new TestSelectorByReferenceSFDC(Paths.get((project.enforce.srcPath as String)).toString(), getAllTestClassNameList(),
                            this.artifactGenerator, fileParamValue, refreshClassAndTestMap)
                    selector.setLogger(logger)
                }
                else {
                    selector = new TestSelectorByReferenceLocal(pathClasses, getAllApexClassNameList(), getAllTestClassNameList(), fileParamValue)
                    selector.setLogger(logger)
                }

                testClassNames = selector.getTestClassNames()
                if (testClassNames.isEmpty() && fileParamValue != RunTestTaskConstants.WILD_CARD_SIGN &&
                        fileParamValue != RunTestTaskConstants.RUN_ALL_UPDATED_PARAM_VALUE) {
                    throw new Exception("${RunTestTaskConstants.THERE_ARE_NOT_TEST_CLASSES} ${fileParamValue}")
                }
                this.testClassNameList.addAll(testClassNames)
                logger.debug("ENFORCE - this.testClassNameList with -Pfiles -----> ${this.testClassNameList}")
            }
        }
        else if (!Util.isValidProperty(project, RunTestTaskConstants.CLASS_PARAM)) {
            if (this.async) {
                this.testClassNameList = (new TestSelectorByDefault(getAllTestClassNameList(), null)).getTestClassNames()
                logger.debug("ENFORCE - this.testClassNameList when test parameter is invalid and async -----> ${this.testClassNameList}")
            } else {
                this.testClassNameList = null
                logger.debug("ENFORCE - this.testClassNameList when test parameter is invalid and is not async -----> ${this.testClassNameList}")
            }
        }

        if (this.testClassNameList && !this.testClassNameList.isEmpty()) {
            this.testClassNameList.unique()
        }
        logger.debug("ENFORCE - this.testClassNameList end -----> ${this.testClassNameList}")
        return this.testClassNameList
    }

    /**
     * Returns all available test classes on demand
     */
    private ArrayList<String> getAllTestClassNameList() {
        return this.allTestClassNameList
    }

    /**
     * Returns all available Apex classes on demand
     */
    private ArrayList<String> getAllApexClassNameList() {
        return this.allApexClassNameList
    }

    private void makeClassNamesAvailable() {
        if (!this.allTestClassNameList || !this.allApexClassNameList) {
            println "filling classNames"
            fillClassNames(this.pathClasses, RunTestTaskConstants.WILDCARD_ALL_TEST)
        }
    }

}
