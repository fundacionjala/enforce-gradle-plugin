/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor
import groovy.util.logging.Slf4j

import java.nio.charset.StandardCharsets

/**
 * This class provides a skeletal implementation of the interceptor for all metadata types supported
 */
@Slf4j
abstract class MetadataInterceptor {
    List<String> interceptorsToExecute
    List<File> files
    protected Map<String, Closure> interceptors
    protected final int INDEX_ZERO = 0
    String encoding

    /**
     * Initializes the class properties by default
     */
    MetadataInterceptor() {
        files = []
        interceptors = new LinkedHashMap<String, Closure>()
        interceptorsToExecute = []
        encoding = StandardCharsets.UTF_8.displayName()
    }

    Map<String, Closure> getInterceptors() {
        return interceptors
    }

    void setInterceptors(Map<String, Closure> interceptors) {
        this.interceptors = interceptors
    }

    /**
     * Adds a new interceptor
     * @param name the interceptor name
     * @param interceptor the interceptor to save
     */
    public void addInterceptor(String name, Closure interceptor) {
        interceptors.put(name, interceptor)
    }

    /**
     * Executes a interceptor in all files
     * @param interceptor the interceptor to execute
     * @param file the interceptor will be applied on this file
     */
    public void executeInterceptor(Closure interceptor, File file) {
        if (interceptor) {
            interceptor(file)
        }
    }

    /**
     * Executes interceptor specifics or all anonymous interceptors
     */
    public void executeInterceptors() {
        files.each { file ->
            interceptors.each { interceptorName, interceptor ->
                if (interceptorsToExecute.contains(interceptorName) ||
                        (!interceptorsToExecute.contains(interceptorName) &&
                                interceptorName.isNumber() && interceptorName.toInteger() == interceptor.hashCode())) {
                    log.debug "$interceptorName --> $file.name"
                    executeInterceptor(interceptor, file)
                }
            }
        }
    }

    /**
     * Adds a new interceptor in a index specific
     * @param index the index where will insert the new interceptor
     * @param interceptorName the interceptor name
     * @param interceptor the new interceptor
     */
    public void addInterceptor(int index, String interceptorName, Closure interceptor) {

        if (interceptors.containsKey(interceptorName)) {
            throw new Exception("The $interceptorName interceptor already exists")
        }
        if (index < INDEX_ZERO || index > interceptors.size()) {
            throw new Exception("Index out of range")
        }

        Map<String, Closure> interceptorsAux = new LinkedHashMap<String, Closure>()

        interceptors.eachWithIndex { keyName, valueCmd, i ->
            if (i < index) {
                interceptorsAux.put(keyName, valueCmd)
            }
        }
        interceptorsAux.put(interceptorName, interceptor)
        interceptorsAux.keySet().each { keyCmdName ->
            interceptors.remove(keyCmdName)
        }
        interceptorsAux.putAll(interceptors)
        interceptors = interceptorsAux
    }

    /**
     * Removes a interceptor from the interceptor name
     * @param name the interceptor name
     */
    public void removeInterceptor(String name) {
        interceptors.remove(name)
    }

    /**
     * Loads all files from a source path
     * @param sourcePath the source path of the metadata
     */
    public abstract void loadFiles(String sourcePath)

    /**
     * Loads all interceptors by default
     */
    public abstract void loadInterceptors()
}
