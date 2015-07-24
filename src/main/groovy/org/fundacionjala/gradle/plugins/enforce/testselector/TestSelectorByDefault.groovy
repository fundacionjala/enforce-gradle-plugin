package org.fundacionjala.gradle.plugins.enforce.testselector

import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents

class TestSelectorByDefault extends TestSelector {

    private String wildCards

    public TestSelectorByDefault(ArrayList<String> testClassNameList, String wildCard) {
        super(testClassNameList)
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
