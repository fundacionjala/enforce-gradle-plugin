package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files

import groovy.json.JsonSlurper
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.org.OrgInterfaceValidator
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.QueryBuilder
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.ToolingAPI

import java.nio.file.Paths

/**
 * This class defines how validate a basic  salesforce component
 */
public class BasicOrgComponentsValidator implements OrgInterfaceValidator{

    /**
     * Validates the components defined in  Salesforce organization
     * @param credential  contains the data needed to connect with the API sales force
     * @param filesToValidate is a list of files that need to validate
     * @param folderComponent is a component type that we need validate
     * @param projectPath our repository
     */
    @Override
    public Map<String,ArrayList<File>> validateFiles(Credential credential, ArrayList<File> filesToVerify, String folderComponent, String projectPath) {

        Map<String,ArrayList<File>> mapFiles = [:]
        mapFiles.put(Constants.VALID_FILE, new ArrayList<File>())
        mapFiles.put(Constants.DOES_NOT_EXIST_FILES, new ArrayList<File>())
        mapFiles.put(Constants.FILE_WITHOUT_VALIDATOR, new ArrayList<File>())

        ArrayList<File> orgFiles = getFilesIntoOrg(credential, folderComponent, projectPath)

        filesToVerify.findAll{ File file ->
            !file.getAbsolutePath().endsWith(Constants.META_XML)
        }.each { File file ->
            if(orgFiles.containsAll(file)) {
                mapFiles[Constants.VALID_FILE].add(file)
            }
            else {
                mapFiles[Constants.DOES_NOT_EXIST_FILES].add(file)
            }
        }
        return mapFiles
    }

    /**
     * Return files into Sales Force organization
     * @param credential  contains the data needed to connect with the API sales force
     * @param folderComponent is a component type that we need validate
     * @param path orn repository
     */
    public ArrayList<File> getFilesIntoOrg(Credential credential, String folderComponent, String projectPath) {

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
            def nameFile = "${jsonResulSet.records[i]['Name']}.${extensionComponent}"
            orgFiles.add(new File(Paths.get(projectPath,folderComponent, nameFile ).toString()))
        }
        return orgFiles
    }
}
