package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files

import groovy.json.JsonSlurper
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.org.OrgInterfaceValidator
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.QueryBuilder
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.ToolingAPI

import java.nio.file.Paths


public class BasicOrgComponentsValidator implements OrgInterfaceValidator{

    @Override
    public Map<String,ArrayList<File>> validateFiles(Credential credential, ArrayList<File> filesToVerify, String folderComponent, String path) {

        Map<String,ArrayList<File>> mapFiles = [:]
        mapFiles.put(Constants.VALID_FILE, new ArrayList<File>())
        mapFiles.put(Constants.DOES_NOT_EXIST_FILES, new ArrayList<File>())
        mapFiles.put(Constants.FILE_WHITOUT_VALIDATOR, new ArrayList<File>())

        ArrayList<File> orgFiles = getFilesIntoOrg(credential, folderComponent, path)

        filesToVerify.findAll{ File file ->
            !file.getAbsolutePath().endsWith("-meta.xml")
        }.each { File file ->
            if(orgFiles.containsAll(file)) {
                mapFiles[Constants.VALID_FILE].add(file)
                mapFiles[Constants.VALID_FILE].add(new File(file.getAbsolutePath()+"-meta.xml"))
            }
            else {
                mapFiles[Constants.DOES_NOT_EXIST_FILES].add(file)
                mapFiles[Constants.DOES_NOT_EXIST_FILES].add(new File(file.getAbsolutePath()+"-meta.xml"))
            }
        }
        return mapFiles
    }

    public ArrayList<File> getFilesIntoOrg(Credential credential, String folderComponent, String path) {

        ToolingAPI toolingAPI = new ToolingAPI(credential)
        QueryBuilder queryBuilder = new QueryBuilder()
        JsonSlurper jsonSlurper = new JsonSlurper()

        ArrayList<File> orgFiles = []
        def typeComponent = MetadataComponents.getComponent(folderComponent).getTypeName()
        def extensionComponent = MetadataComponents.getComponent(folderComponent).getExtension()
        def sqlString = queryBuilder.createQueryFromBasicComponent(typeComponent)
        def resultSet =  toolingAPI.httpAPIClient.executeQuery(sqlString)
        def jsonResulSet = jsonSlurper.parseText(resultSet as String)

        for(def i = 0; i < jsonResulSet.records.size(); i++) {
            def nameFile = jsonResulSet.records[i]['Name']+'.'+extensionComponent
            orgFiles.add(new File(Paths.get(path,folderComponent, nameFile ).toString()))
        }
        return orgFiles
    }
}
