package org.fundacionjala.gradle.plugins.enforce.testselector

import org.fundacionjala.gradle.plugins.enforce.unittest.Apex.ApexClasses
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.Util
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.ToolingAPI
import org.gradle.api.Project

@Singleton(lazy = true)
class TestSelectorModerator {
    private Map testSelectorCacheMap

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
        if (project.properties.containsKey(Constants.CLASS_PARAM)) {
            wildCard = project.properties[Constants.CLASS_PARAM].toString()
            if (wildCard) {
                instance = new TestSelectorByDefault(pathClasses, project, wildCard)
            } else {
                throw new Exception("Enter valid parameter ${Constants.CLASS_PARAM}")
            }
//        } else if () { //look for more params
//            throw new Exception("Enter valid parameter ${Constants.CLASS_PARAM}")
        } else { //run all tests
            instance = new TestSelectorByDefault(pathClasses, project, null)
        }
        return instance
    }

    /**
     * Clears the cache Map to re-load it newly
     */
    public void clearCacheMap() {
        testSelectorCacheMap = [:]
    }
}
