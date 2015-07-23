package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.org

import groovy.json.JsonSlurper
import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.QueryBuilder
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.ToolingAPI

/**
 * This class defines how validate a basic salesforce subcomponent
 */
public class BasicOrgSubcomponentsValidator implements OrgInterfaceValidator{

    public QueryBuilder queryBuilder
    public JsonSlurper jsonSlurper

    BasicOrgSubcomponentsValidator(){
        queryBuilder = new QueryBuilder()
        jsonSlurper = new JsonSlurper()
    }

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

        ArrayList<File> orgFiles = getSubcomponentsInOrg(credential, filesToVerify, folderComponent)

        filesToVerify.each { File file ->
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
     * Validates the subcomponents defined in  Salesforce organization
     * @param credential  contains the data needed to connect with the API sales force
     * @param filesToValidate is a list of files that need to validate
     * @param typeSubcomponent is a subcomponent type that we need validate
     * @return a list with the component validated in the Salesforce organization
     */
    public ArrayList<File> getSubcomponentsInOrg(Credential credential, ArrayList<File> filesToValidate, String typeSubcomponent) {
        ArrayList<File> subcomponentsInOrg = []
        filesToValidate.each { File file ->
            if(existFileInOrg(credential, file, typeSubcomponent)) {
                subcomponentsInOrg.add(file)
            }
        }
        return subcomponentsInOrg
    }

    /**
     * Validates the subcomponents defined in Salesforce organization
     * @param credential  contains the data needed to connect with the API sales force
     * @param filesToValidate is a list of files that need to validate
     * @param typeSubcomponent is a subcomponent type that we need validate
     * @return a boolean value that indicate if a subComponent exist in Salesforce organization
     */
    public boolean existFileInOrg(Credential credential, File filesToValidate, String typeSubcomponent) {
        ToolingAPI toolingAPI = new ToolingAPI(credential)
        def typeComponent = MetadataComponents.getComponent(typeSubcomponent).getTypeName()
        def extensionComponent = MetadataComponents.getComponent(typeSubcomponent).getExtension()

        def sqlString = queryBuilder.createQueryGetSubomponent(typeComponent, filesToValidate)
        def resultSet =  toolingAPI.httpAPIClient.executeQuery(sqlString)
        def jsonResulSet = jsonSlurper.parseText(resultSet as String)
        for(def i = 0; i < jsonResulSet.records.size(); i++) {
            String fullName = "${jsonResulSet.records[i]['FullName']}.${extensionComponent}"
            if(fullName.equals(filesToValidate.getName())) {
                return true
            }
        }
        return false
    }
}
