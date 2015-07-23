package org.fundacionjala.gradle.plugins.enforce.testselector

import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.gradle.api.Project

class TestSelectorByDefault extends TestSelector {

    private String wildCards

    public TestSelectorByDefault(String pathClasses, Project project, String wildCard) {
        super(pathClasses, project)
        this.wildCards = wildCard ? wildCard.replace("*", "").replace(".${MetadataComponents.CLASSES.getExtension()}", "") : null
    }

    /**
     * Returns the list of test classes according the wildCards form the parameters
     * @return an resultTracker with status
     */
    @Override
    ArrayList<String> getTestClassNames() {
        if (this.wildCards) {
            ArrayList<String> testClassList = new ArrayList<String>()
            testClassNameList.each { testClassName ->
                this.wildCards.tokenize(',').each { wildCard ->
                    if (testClassName.contains(wildCard)) {
                        testClassList.add(testClassName)
                    }
                }
            }
            return testClassList
        }
        return testClassNameList
    }
}
