package org.fundacionjala.gradle.plugins.enforce.testselector

import org.apache.commons.lang.StringUtils
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.ToolingAPI
import org.gradle.api.Project
import org.gradle.api.file.FileTree

@Singleton(lazy = true)
class TestSelectorModerator {

    private ArrayList<String> testClassNameList
    private Map testSelectorCacheMap
    private Project project

    /**
     * Evaluates the params to build an instance of the ITestSelector
     * @param project, the Program instance
     * @param toolingAPI, the ToolingAPI instance
     * @param pathClasses, the class path to load all the test classes
     * @return an instance of the ITestSelector
     */
    public ITestSelector getTestSelector(Project project, ToolingAPI toolingAPI, String pathClasses) {
        String wildCard
        ITestSelector instance
        this.project = project
        testClassNameList = getAllTestClassNames(pathClasses)
        if (project.properties.containsKey(Constants.CLASS_PARAM)) {
            wildCard = project.properties[Constants.CLASS_PARAM].toString()
            if (wildCard) {
                instance = new TestSelectorByDefault(testClassNameList, wildCard)
            } else {
                throw new Exception("Enter valid parameter ${Constants.CLASS_PARAM}")
            }
//        } else if () { //look for more params
//            throw new Exception("Enter valid parameter ${Constants.CLASS_PARAM}")
        } else { //run all tests
            instance = new TestSelectorByDefault(testClassNameList, null)
        }
        return instance
    }

    /**
     * Loads all the test classes from the class folder path
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

    /**
     * Clears the cache Map to re-load it newly
     */
    public void clearCacheMap() {
        testSelectorCacheMap = [:]
    }
}
