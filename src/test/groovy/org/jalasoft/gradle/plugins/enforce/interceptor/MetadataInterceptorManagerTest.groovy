/*
 * Copyright (c) Jalasoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.jalasoft.gradle.plugins.enforce.interceptor

import spock.lang.Specification

class MetadataInterceptorManagerTest extends Specification {

    def "Should create seven truncators from source path"(){
        given:
        InterceptorManager interceptorManager = new InterceptorManager()
        when:
        interceptorManager.buildInterceptors()
        then:
        interceptorManager.interceptors.size() == 7
    }
}
