package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.helperManager

import groovy.util.logging.Log

@Log
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
        log.println("  -P${name} : ${description}")
        examples.each {String example->
            example = example.replace(WILDCARD_TASK,task)
            log.println("      > ${example}")
        }
    }
}
