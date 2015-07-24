package org.fundacionjala.gradle.plugins.enforce.testselector

public abstract class TestSelector implements ITestSelector {


    protected ArrayList<String> testClassNameList

    protected TestSelector(ArrayList<String> testClassNameList) {
        this.testClassNameList = testClassNameList
    }

    public abstract ArrayList<String> getTestClassNames();
}
