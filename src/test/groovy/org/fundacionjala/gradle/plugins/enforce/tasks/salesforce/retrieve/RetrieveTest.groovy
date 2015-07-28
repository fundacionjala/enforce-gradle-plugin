/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.tasks.salesforce.retrieve

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.fundacionjala.gradle.plugins.enforce.EnforcePlugin
import org.fundacionjala.gradle.plugins.enforce.credentialmanagement.CredentialManager
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.LoginType
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared

import java.nio.file.Paths

class RetrieveTest extends spock.lang.Specification {
    @Shared
        Retrieve retrieve
    @Shared
        Project project
    @Shared
        CredentialManager credentialManager
    @Shared
        def path = Paths.get(System.getProperty("user.dir"), "src", "test", "groovy", "org", "fundacionjala", "gradle",
                "plugins","enforce", "tasks", "salesforce").toString()
    @Shared
        Credential credential

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: EnforcePlugin)
        retrieve = project.task('retrieveTest', type: Retrieve)
        retrieve.fileManager = new ManagementFile(Paths.get(path, 'retrieve', 'resources').toString())
        credentialManager = Mock(CredentialManager)
        new File(Paths.get(path, 'retrieve', 'resources').toString()).mkdir()

        credential = new Credential()
        credential.id = 'id'
        credential.username = 'salesforce2014.test@gmail.com'
        credential.password = '123qwe2014'
        credential.token = 'UO1Jx5vDQl97xCKkwXBH8tg3T'
        credential.loginFormat = LoginType.DEV.value()
        credential.type = 'normal'
    }

    def "Test should get a credential from user's home directory or project directory when you send an id credential" () {
        given:
            String credentialId = "idNormal"
            ArrayList<String> paths = [Paths.get(path, "resources", "CredentialsProject.dat").toString(), Paths.get(path, "resources", "CredentialsUser.dat").toString()]
            Credential credential = new Credential()
            credential.id = 'idNormal'
            credential.username = 'juan@email.com'
            credential.password = 'password'
            credential.token = 'token'
            credential.loginFormat = LoginType.TEST.value()
            credential.type = 'normal'
        when:
            retrieve.credentialId = credentialId
            retrieve.arrayPaths = paths
            credentialManager.getCredentialToAuthenticate(credentialId, paths) >> credential
            retrieve.loadCredential()
        then:
            retrieve.credential.id  == credential.id
            retrieve.credential.username  == credential.username
            retrieve.credential.password  == credential.password
            retrieve.credential.token  == credential.token
            retrieve.credential.loginFormat  == credential.loginFormat
            retrieve.credential.type  == credential.type
    }

    def "Test should get default credential when you doesn't an credentialId" () {
        given:
            String credentialId = ""
            ArrayList<String> paths = [Paths.get(path, "resources", "CredentialsProject.dat").toString(), Paths.get(path, "resources", "CredentialsUser.dat").toString()]
            Credential credential = new Credential()
            credential.id = 'default'
            credential.username = 'username@email.com'
            credential.password = 'password'
            credential.token = 'token'
            credential.loginFormat = LoginType.TEST.value()
            credential.type = 'normal'
        when:
            retrieve.credentialId = credentialId
            retrieve.arrayPaths = paths
            credentialManager.getCredentialToAuthenticate(credentialId, paths) >> credential
            retrieve.loadCredential()
        then:
            retrieve.credential.id  == credential.id
            retrieve.credential.username  == credential.username
            retrieve.credential.password  == credential.password
            retrieve.credential.token  == credential.token
            retrieve.credential.loginFormat  == credential.loginFormat
            retrieve.credential.type  == credential.type
    }

    def "Test should copy sales force's files into project directory without package.xml" () {
        given:
            def sourcePath = Paths.get(path, 'retrieve', 'resources', 'src').toString()
            new File(sourcePath).mkdir()
            new File(Paths.get(sourcePath, 'objects').toString()).mkdir()
            def unPackagedPath = Paths.get(path, 'retrieve', 'resources', 'unpackaged').toString()
            new File(unPackagedPath).mkdir()
            new File(Paths.get(unPackagedPath, 'objects').toString()).mkdir()
            new File(Paths.get(unPackagedPath, 'objects', 'Account.object').toString()).createNewFile()
            new File(Paths.get(unPackagedPath, 'package.xml').toString()).createNewFile()
            retrieve.projectPath = Paths.get(path, 'retrieve', 'resources', 'src').toString()
            retrieve.unPackageFolder = unPackagedPath
        when:
            retrieve.setup()
            retrieve.copyFilesWithoutPackage()
        then:
            new File(Paths.get(retrieve.projectPath, 'objects', 'Account.object').toString()).exists()
    }

    def "Test should return an path absolute" () {
        given:
            def projectPath = Paths.get(path, "user").toString()
        when:
            def pathObtained = retrieve.getSfdcPathProject(projectPath)
        then:
            pathObtained == projectPath
    }

    def "Test should return an new path if an path sent is relative" () {
        given:
            def projectPath = 'src'
        when:
            def pathObtained = retrieve.getSfdcPathProject(projectPath)
        then:
            pathObtained == Paths.get(project.projectDir.absolutePath, projectPath).toString()
    }

    def "Test should return an Exception if path sent is null" () {
        when:
            retrieve.getSfdcPathProject(null)
        then:
            thrown(Exception)
    }

    def "Test should return an Exception if path sent is empty" () {
        when:
            retrieve.getSfdcPathProject('')
        then:
            thrown(Exception)
    }

    def "Test should return ann path" () {
        given:
            def projectPath = Paths.get(path, 'src', '.').toString()
        when:
            def pathObtained = retrieve.getSfdcPathProject(projectPath)
        then:
            pathObtained == projectPath
    }

    def cleanupSpec() {
        new File(Paths.get(path, 'retrieve', 'resources').toString()).deleteDir()
    }
}




