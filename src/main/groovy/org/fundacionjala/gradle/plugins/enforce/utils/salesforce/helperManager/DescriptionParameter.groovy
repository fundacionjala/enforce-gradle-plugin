package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.helperManager


class DescriptionParameter {
    public String name
    public String description
    public ArrayList<String> examples
    public static String WILDCARD_TASK = "*task*"

    DescriptionParameter(String name) {
        this.name = name
        examples = []
    }
    def show(String task) {
        println "  -P${name} : ${description}"
        examples.each {String example->
            example = example.replace(WILDCARD_TASK,task)
            println "      > ${example}"
        }
    }
}
