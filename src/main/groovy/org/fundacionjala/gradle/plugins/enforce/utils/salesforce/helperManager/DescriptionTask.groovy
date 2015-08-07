package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.helperManager

class DescriptionTask {
    public String name
    public String description
    public ArrayList<DescriptionParameter> parameters

    DescriptionTask(String name) {
        this.name = name
        this.parameters = []
    }

    def show() {
        println "*********************************************"
        println "Task : " + name + ""
        println "Description : "
        println "  " + description + ""
        println "*********************************************"
    }
}
