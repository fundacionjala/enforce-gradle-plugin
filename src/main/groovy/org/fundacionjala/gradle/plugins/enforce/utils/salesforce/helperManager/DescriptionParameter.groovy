package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.helperManager

class DescriptionParameter {
    public String name
    public String description
    public ArrayList<String> examples

    DescriptionParameter(String name) {
        this.name = name
        examples = []
    }
}
