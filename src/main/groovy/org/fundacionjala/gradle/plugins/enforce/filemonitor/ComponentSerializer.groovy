package org.fundacionjala.gradle.plugins.enforce.filemonitor

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.json.StringEscapeUtils
import groovy.json.internal.LazyMap
import org.fundacionjala.gradle.plugins.enforce.utils.Util

public class ComponentSerializer implements Serializable{
    public String sourcePath
    private final String BACK_SLASH = "\\\\"
    private final String DOUBLE_BACK_SLASH = "\\\\\\\\"
    private final String FILE_NAME_LABEL = 'fileName'
    private final String HASH_LABEL = 'hash'
    private final String SUB_COMPONENT_LABEL = 'subComponents'

    public ComponentSerializer(String sourcePath) {
        this.sourcePath = sourcePath
    }

    /**
     * Saves a map into .fileTracker.data file
     * @param components is map with filName and its componentHash
     */
    void save(Map<String, ComponentHash> components) throws IOException {
        File fileTracker = new File(sourcePath)
        JsonBuilder jsonBuilder = new JsonBuilder()
        jsonBuilder(components)
        String content = jsonBuilder.toString().replaceAll(DOUBLE_BACK_SLASH, BACK_SLASH)
        content = content.replaceAll(BACK_SLASH, DOUBLE_BACK_SLASH)
        fileTracker.text = content
    }

    /**
     * Reads a .fileTracker.data file from source project
     * @return a map with .fileTracker.data file values
     */
    public Map<String, ComponentHash> read() throws IOException {
        Map<String, ComponentHash> result = [:]
        try {
            JsonSlurper jsonSlurper = new JsonSlurper()
            jsonSlurper.parseText(new File(sourcePath).text)
            result = readJsonTrackerFile()

        } catch (Exception exception) {
            result = readOldFormat()
        }
        return result
    }

    /**
     * Reads a .fileTracker.data file that has old format
     * @return a map with values from .fileTracker.data file
     */
    private Map<String, ComponentHash> readOldFormat() throws IOException {
        Map<String, ComponentHash> result
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(sourcePath))
        result = convertToComponentHash((Map<String, String>) ois.readObject())
        ois.close()
        return result
    }

    /**
     * Converts a map from Map<String, String> to Map<String, ComponentHash>
     * @param fileTrackerMap is a map that has filename as key and its hash as value
     * @return a map with fileName and its componentHash
     */
    private Map<String, ComponentHash> convertToComponentHash(Map<String, String> fileTrackerMap) {
        Map<String, ComponentHash> result = [:]
        fileTrackerMap.each {String fileName, String fileHash ->
            ComponentHash componentHash = new ComponentHash(fileName, fileHash)
            result.put(fileName, componentHash)
        }
        return result
    }

    /**
     * Reads .fileTracker.data file that has a json format.
     * @return a map with values from .fileTracker.data file
     */
    private Map<String, ComponentHash> readJsonTrackerFile() {
        Map<String, ComponentHash> result = [:]
        JsonSlurper jsonSlurper = new JsonSlurper()
        LazyMap lazyMap = jsonSlurper.parseText(new File(sourcePath).text)
        lazyMap.each { String componentName, LazyMap componentMap ->
            String fileName = componentMap.get(FILE_NAME_LABEL)
            String fileHash = componentMap.get(HASH_LABEL)
            ComponentHash componentHash
            if (componentMap.containsKey(SUB_COMPONENT_LABEL)) {
                componentHash = new ObjectHash(fileName, fileHash, componentMap.get(SUB_COMPONENT_LABEL) as Map<String, String>)
            } else {
                componentHash = new ComponentHash(fileName, fileHash)
            }
            result.put(fileName, componentHash)
        }
        return result
    }
}
