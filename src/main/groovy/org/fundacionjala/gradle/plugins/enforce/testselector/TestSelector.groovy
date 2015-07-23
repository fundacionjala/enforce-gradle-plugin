package org.fundacionjala.gradle.plugins.enforce.testselector

import com.sforce.soap.apex.*
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.apache.commons.lang.StringUtils
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.gradle.api.file.FileTree
import org.gradle.api.Project
import java.io.File
import java.util.ArrayList

import org.gradle.api.file.FileTree
import org.gradle.api.logging.LogLevel
import org.gradle.logging.ProgressLoggerFactory
import java.nio.file.Paths

public abstract class TestSelector implements ITestSelector {

    private Project project
    protected ArrayList<String> testClassNameList

    protected TestSelector(String pathClasses, Project project) {
        this.project = project
        testClassNameList = getAllTestClassNames(pathClasses)
    }

    /**
     * Loads all the test class from the class folder path
     * @param pathClasses contains the class folder path
     * @return ArrayList with all the test class names
     */
    private ArrayList<String> getAllTestClassNames(String pathClasses) {
        FileTree tree = project.fileTree(dir: pathClasses)
        ArrayList<String> classNames = new ArrayList<String>()
        tree.each { File file ->
            if (file.path.endsWith(".${MetadataComponents.CLASSES.getExtension()}") &&
                    StringUtils.containsIgnoreCase(file.text, Constants.IS_TEST)) {
                classNames.add(Util.getFileName(file.name))
            }
        }
        return classNames
    }

    public abstract ArrayList<String> getTestClassNames();
}
