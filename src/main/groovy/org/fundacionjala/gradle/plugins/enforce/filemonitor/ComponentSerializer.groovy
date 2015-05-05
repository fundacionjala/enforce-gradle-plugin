package org.fundacionjala.gradle.plugins.enforce.filemonitor

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap

public class ComponentSerializer implements Serializable{
    public String sourcePath

    public ComponentSerializer(String sourcePath) {
        this.sourcePath = sourcePath
    }

    void save(Map<String, ComponentHash> components) throws IOException {
        File fileTracker = new File(sourcePath)
        def jsonBuilder = new JsonBuilder()
        jsonBuilder(components)
        fileTracker.text = jsonBuilder.toPrettyString()
    }

    public Map<String, ComponentHash> read() throws IOException {
        Map<String, ComponentHash> result = [:]
        JsonSlurper jsonSlurper = new JsonSlurper()
        LazyMap lazyMap = jsonSlurper.parseText(new File(sourcePath).text)
        lazyMap.each { String componentName, LazyMap componentMap ->
            String fileName = componentMap.get('fileName')
            String fileHash = componentMap.get('hash')
            ComponentHash componentHash
            if (componentMap.containsKey('subComponents')) {
                componentHash = new ObjectHash(fileName, fileHash, componentMap.get('subComponents') as Map<String, String>)
            } else {
                componentHash = new ComponentHash(fileName, fileHash)
            }
            result.put(fileName, componentHash)
        }
        return result
    }
}
