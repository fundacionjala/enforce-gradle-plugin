/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.exceptions.deploy

import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by Ariel Patino on 1/6/2015.
 */
class DeployExceptionTest extends Specification{

    @Shared
    def infoDeploy = new InfoDeploy()

    @Shared
    def infoDeploy2 = new InfoDeploy()

    @Shared
    ArrayList<InfoDeploy> infoDeployArrayList

    def setup() {
        infoDeploy.fileName = 'class1.cls'
        infoDeploy.problem = 'unexpected token'
        infoDeploy.line = 6
        infoDeploy.column = 13

        infoDeploy2.fileName = 'class2.cls'
        infoDeploy2.problem = 'unexpected token'
        infoDeploy2.line = 12
        infoDeploy2.column = 26
        infoDeployArrayList = [infoDeploy, infoDeploy2]
    }

    def "Test string with json format should be accepted as a valid Json with JsonSlurper" () {
        given:
            def deployException = new DeployException('', infoDeployArrayList)
            def result = deployException.getMessageJsonFormat()
            JsonSlurper jsonSlurper = new JsonSlurper()
        when:
            jsonSlurper.parseText(result)
        then:
            noExceptionThrown()
    }

    def "Test should convert the infoDeployArrayList into a string with Json format" (){
        given:
            def deployException = new DeployException('', infoDeployArrayList)
            def result = deployException.getMessageJsonFormat()
        when:
            def object1Expected = '{"fileName":"class1.cls","problem":"unexpected token","line":6,"column":13}'
            def object2Expected = '{"fileName":"class2.cls","problem":"unexpected token","line":12,"column":26}'
        then:
            result == """{"errors":[${object1Expected},${object2Expected}]}"""
    }
}
