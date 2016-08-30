/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.interceptors

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class PageInterceptorTest extends Specification {
    @Shared
    String ROOT_PATH = System.properties['user.dir']

    @Shared
    String RESOURCE_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/pages"
    @Shared
    String TRUNCATED_PATH = "${ROOT_PATH}/src/test/groovy/org/fundacionjala/gradle/plugins/enforce/interceptor/resources/interceptor"

    def setup() {
        new AntBuilder().copy(todir: TRUNCATED_PATH) {
            fileset(dir: RESOURCE_PATH) {
            }
        }
    }

    def "Should load pages from source path"() {
        given:
        PageInterceptor pageInterceptor = new PageInterceptor()
        String path = Paths.get(RESOURCE_PATH).toString()
        when:
        pageInterceptor.loadFiles(path)
        then:
        pageInterceptor.files.size() == 4

    }

    def "Should execute the commands of page truncator"() {
        given:
        PageInterceptor pageInterceptor = new PageInterceptor()
        String path = Paths.get(TRUNCATED_PATH).toString()
        pageInterceptor.interceptorsToExecute = [org.fundacionjala.gradle.plugins.enforce.interceptor.Interceptor.TRUNCATE_PAGES.id]
        when:

        pageInterceptor.loadFiles(path)
        pageInterceptor.loadInterceptors()
        pageInterceptor.executeInterceptors()
        then:
        pageInterceptor.files.each { file ->
            assert !file.text.contains("h1") || !file.text.contains("script") ||
                    !file.text.contains("Quote__c") || !file.text.contains("-->")
        }
    }

    def cleanup() {
        new File(TRUNCATED_PATH).deleteDir()
    }
}
