/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.utils

import groovy.json.JsonSlurper
import org.apache.commons.lang.StringUtils
import groovy.util.logging.Slf4j
import org.fundacionjala.gradle.plugins.enforce.filemonitor.ComponentHash
import org.fundacionjala.gradle.plugins.enforce.undeploy.PackageComponent
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.ClassifiedFile
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.FileValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.MetadataComponents
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files.SalesforceValidator
import org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.files.SalesforceValidatorManager
import org.fundacionjala.gradle.plugins.enforce.wsc.Credential
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.QueryBuilder
import org.fundacionjala.gradle.plugins.enforce.wsc.rest.ToolingAPI
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.FileTree

import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Logger
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * A set methods of utility
 */
@Slf4j
class Util {
    private static final String PATTERN_EMAIL = '([\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)*(\\.[\\w-]+))'
    private static final String PATTERN_FILE_EXT = ~/[.][^.]+$/
    private static final int MAC_ADDRESS_SIZE = 12
    private static final String MAC_ADDRESS_BY_DEFAULT = '000000000000'
    private static final ZERO_NUMBER_PATH = 0

    /**
     * Gets only file typeName without the extension
     * @param fileName the file typeName
     */
    public static String getFileName(String fileName) {
        fileName.replaceFirst(PATTERN_FILE_EXT, '')
    }

    /**
     * Gets a developer name of full name
     * @param fullName is a tag of custom field
     * @return developerName of custom field
     */
    public static String getDeveloperName(String fullName) {
        int point1 = fullName.indexOf('.') + 1
        int point2 = fullName.indexOf("__c",point1)
        if(point2 < 0) {
            point2 = fullName.lastIndexOf('.')
        }
        return fullName.substring(point1, point2)
    }

    /**
     * Gets a developerName of member
     * @param member is member tag of package xml file
     * @return developerName of custom field
     */
    public static String getDeveloperNameByMember(String member, String name) {
        String result
        if (name == 'CustomField') {
            result = member.substring(member.indexOf('.') + 1, member.length() - 3)
        } else {
            result = member.substring(member.indexOf('.') + 1, member.length())
        }
        return result
    }

    /**
     * Verifies if the property exist and if it is not empty string
     * @param name the property typeName
     */
    public static boolean isValidProperty(Project project, String name) {
        project.hasProperty(name) && !project.properties[name].toString().equals("")
    }

    /**
     * Verifies if the property exist and if it is not empty string
     * @parameters to compare
     * @param name the property typeName
     */
    public static boolean isValidProperty(Map parameters, String name) {
        parameters.containsKey(name) && !parameters[name].toString().equals("")
    }

    /**
     * Verify if the property is empty
     * @param name the property typeName
     */
    public static isEmptyProperty(Project project, String name) {
        project.hasProperty(name) && project.properties[name].toString().equals('')
    }

    /**
     * Verify if the property is empty
     * @param name the property typeName
     */
    public static isEmptyProperty(Map parameters, String name) {
        parameters.hasProperty(name) && parameters[name].toString().equals('')
    }

    /**
     * Validates required parameters that should be entered via command line
     * @param project The project reference
     * @param paramNames A string list with the parameter names
     * */
    public static Boolean validateRequiredParameters(Project project, List<String> paramNames) {
        def valid = true
        def projectRef = project
        paramNames.each { paramName ->
            if (!isValidProperty(projectRef, paramName)) {
                println "Parameter -P${paramName} is required. "
                valid = false
            }
        }
        return valid
    }
    /**
     * Validates a email input
     * @param email is a string
     * @return true if email is valid else false
     */
    public static boolean validEmail(String email) {
        boolean result = false
        if (!email.contains(" ")) {
            Pattern pattern = Pattern.compile(PATTERN_EMAIL)
            Matcher matcher = pattern.matcher(email)
            result = matcher.find()
        }
        return result
    }
    /**
     * Gets mac address of your computer
     * @return String mac address
     */
    public static String getMacAddress() {
        String macAddress = MAC_ADDRESS_BY_DEFAULT
        ArrayList<String> interfaces = []

        NetworkInterface.getNetworkInterfaces().each { NetworkInterface element ->
            def elementEncoded = element.hardwareAddress?.encodeHex()
            interfaces.push(elementEncoded.toString())
        }
        interfaces.each { String mac ->
            if (mac && mac.size() == MAC_ADDRESS_SIZE) {
                macAddress = mac
            }
        }
        return macAddress

    }

    public static String formatDurationHMS(long milliseconds) {

        long second = (milliseconds / 1000)
        second = second % 60
        long minute = (milliseconds / (1000 * 60))
        minute = minute % 60
        long hour = (milliseconds / (1000 * 60 * 60))
        hour = hour % 24
        milliseconds = milliseconds % 1000
        String time = String.format("%02d:%02d:%02d:%02d", hour, minute, second, milliseconds)
        return time
    }

    public static byte[] getBytes(String value, String charsetName) {
        value.getBytes(Charset.forName(charsetName))
    }

    /**
     * Created a folder, if it is already created, it will be deleted that folder.
     * @param path contains the path of folder to create
     */
    public static void forceCreateFolder(String path) {
        File directory = new File(path)
        if (directory.exists()) {
            directory.deleteDir()
        }
        directory.mkdir()
    }

    /**
     * Logs an array list of strings
     * @param logger the instance Logger of java
     * @param folderNames an array list of strings
     */
    public static void logList(Logger logger, String message, ArrayList<String> stringArrayList) {
        logger.info("${message}:\n")
        stringArrayList.each { folder ->
            logger.info(folder)
        }
    }

    /**
     * Gets extension file
     * @param file is type File
     * @return extension
     */
    public static String getFileExtension(File file) {
        String name = file.getName()
        int lastIndexOf = name.lastIndexOf(".")
        if (lastIndexOf == -1) {
            return "" // empty extension
        }
        return name.substring(lastIndexOf + 1)
    }

    /**
     * Gets a path relative of the file
     * @param file is the file that is tracked
     * @return is a path relative
     */
    public static String getRelativePath(File file, String basePath, boolean normalizePath = true) {
        File root = new File(basePath)
        String relativePath = root.toPath().relativize(file.toPath()).toString()
        if (normalizePath) {
            return Paths.get(relativePath).toString()
        }
        return relativePath.replaceAll(Constants.BACK_SLASH, Constants.SLASH)
    }

    /**
     * Gets folders invalid
     * @param foldersName are folders name
     * @return an Array list with invalid folders
     */
    public static ArrayList<String> getInvalidFolders(ArrayList<String> foldersName) {
        ArrayList<String> invalidFolders = []
        foldersName.each { String folderName ->
            if (!MetadataComponents.validFolder(folderName) || folderName.contains('.')) {
                invalidFolders.push(folderName)
            }
        }
        return invalidFolders
    }

    /**
     * Gets folders empty
     * @param foldersName are folders name
     * @return an Array list with empty folders
     */
    public static ArrayList<String> getEmptyFolders(ArrayList<String> foldersName, String projectPath) {
        ArrayList<String> emptyFolders = []
        foldersName.each { String folderName ->
            File file = new File(Paths.get(projectPath, folderName).toString())
            if (file.isDirectory()) {
                if (file.exists() && file.list().length == 0) {
                    emptyFolders.push(folderName)
                }
            }
        }
        return emptyFolders
    }

    /**
     * Gets folders name that don't exist
     * @param foldersName is type String
     * @param projectPath is type String
     * @return an Array of folders name that don't exist
     */
    public static ArrayList<String> getNotExistFolders(ArrayList<String> foldersName, String projectPath) {
        ArrayList<String> notExistFolders = []
        foldersName.each { String folderName ->
            File file = new File(Paths.get(projectPath, folderName).toString())
            if (!file.exists()) {
                notExistFolders.push(folderName)
            }
        }
        return notExistFolders
    }

    /**
     * Gets the first part of the path
     * @param path is type String that contains a file's path
     * @return an String that contains the first part of the path
     */
    public static String getFirstPath(String path) {
        return Paths.get(path).getName(ZERO_NUMBER_PATH)
    }

    /**
     * Gets a object name from sub component member
     * @param subComponentMember is a sub component member
     * @return a object name
     */
    public static String getObjectName(String subComponentMember) {
        String objectName = subComponentMember.substring(0, subComponentMember.indexOf('.'))
        return objectName
    }

    /**
     * Returns true if the apiName belongs on a packaged code and is a custom component/subComponent. The apiName parameter can also be used for
     * CustomObjects, CustomFields, etc and any other kind of component/subComponent that follows SF API name format.
     * <br/>
     * EG:
     * <br/>
     * <ul>
     *   <li>myprefix__CustomObject__c</li>
     *   <li>myprefix__CustomFielName__c</li>
     * </ul>
     * @param apiName, An string value to be verified.
     * @return Boolean, It is true if apiName belongs on packaged code, otherwise false.
     **/
    public static Boolean isPackaged(String apiName) {
        return (apiName) ? (StringUtils.countMatches(apiName, "__") == 2) : false
    }

    /**
     * Gets the file charset
     * @param file the file to get the encoding
     * @return the charset
     */
    public static String getCharset(File file) {
        CharsetToolkit toolkit = new CharsetToolkit(file);
        Charset guessedCharset = toolkit.getCharset();
        return guessedCharset.displayName()
    }

    /**
     * Writes new file content using original encoding if it doesn't exist uses encoding from user
     * @param file the file to write new content
     * @param content the new content
     * @param charset the original encoding
     * @param encoding the encoding from user
     */
    public static void writeFile(File file, String content, String charset, String encoding){
        log.debug "[${file.name}]-->[charset:${charset}]"
        if (charset) {
            file.write(content, charset)
        } else {
            log.warn  "No encoding detected for ${file.name}. The encoding by default is ${encoding}."
            file.write(content, encoding)
        }
    }

    /**
     * Validates parameter's values
     * @param parameterValues are files name that will be excluded
     */
    public static void validateParameterContent(String parameterValues, String projectPath) {
        parameterValues = parameterValues.replaceAll(Constants.BACK_SLASH, Constants.SLASH)
        ArrayList<String> fileNames = []
        ArrayList<String> folderNames = []
        parameterValues.split(Constants.COMMA).each { String parameter ->
            if (parameter.contains(Constants.WILDCARD) || parameter == Constants.EMPTY) {
                return
            }
            if (parameter.contains(Constants.SLASH)) {
                fileNames.push(parameter)
            } else {
                folderNames.push(parameter)
            }
        }
        validateFolders(folderNames, projectPath)
        validateFiles(fileNames, projectPath)
    }

    /**
     * Validates folders name
     * @param foldersName is type array list contents folders name
     */
    public static void validateFolders(ArrayList<String> foldersName, String projectPath) {
        String errorMessage = ''
        ArrayList<String> invalidFolders = getInvalidFolders(foldersName)
        if (!invalidFolders.empty) {
            errorMessage = "${Constants.INVALID_FOLDER}: ${invalidFolders}"
        }

        ArrayList<String> notExistFolders = getNotExistFolders(foldersName, projectPath)
        if (!notExistFolders.empty) {
            errorMessage += "\n${Constants.DOES_NOT_EXIST_FOLDER} ${notExistFolders}"
        }

        if (!errorMessage.isEmpty()) {
            throw new Exception(errorMessage)
        }
    }

    /**
     * Validates files name
     * @param filesName is type array list contents files name
     */
    public static void validateFiles(ArrayList<String> filesName, String projectPath) {
        ArrayList<String> invalidFiles = []
        ArrayList<String> notExistFiles = []
        String errorMessage = ''
        filesName.each { String fileName ->
            File file = new File(Paths.get(projectPath, fileName).toString())
            String parentName = getFirstPath(fileName).toString()
            SalesforceValidator validator = SalesforceValidatorManager.getValidator(parentName)
            if (!validator.validateFile(file, parentName)) {
                invalidFiles.push(fileName)
            }
            if (!new File(Paths.get(projectPath, fileName).toString()).exists()) {
                notExistFiles.push(fileName)
            }
        }
        if (!invalidFiles.isEmpty()) {
            errorMessage = "${Constants.INVALID_FILE}: ${invalidFiles}"
        }
        if (!notExistFiles.isEmpty()) {
            errorMessage += "\n${Constants.DOES_NOT_EXIST_FILES} ${notExistFiles}"
        }
        if (!errorMessage.isEmpty()) {
            throw new Exception(errorMessage)
        }
    }

    /**
     * Gets rules from list of workflow
     * @param workflowList contains workflow files
     * @return an array strings which are rules
     */
    public static ArrayList<String> getRules(ArrayList<File> workflowList) {
        def rules = []
        workflowList.each { workflow ->
            rules.addAll(getWorkflowRules(workflow))
        }
        return rules
    }

    /**
     * Gets all rules from a workflow file
     * @param workflowFile analyzes a workflow at once
     * @return an array of rules in one workflow
     */
    public static ArrayList<String> getWorkflowRules(File workflowFile) {
        def Workflow = new XmlParser().parseText(workflowFile.text)
        def workflowName = getFileName(workflowFile.toPath().getFileName().toString())
        def workflowRules = []
        Workflow.rules.each { rule ->
            workflowRules.add("${workflowName}.${rule.fullName.text()}")
        }
        return workflowRules
    }

    /**
     * Gets all components included to truncate
     * @param components the components names
     * @return components
     */
    public static ArrayList<String> getComponentsWithWildcard(List components) {
        def includesComponents = []
        components.each { component ->
            includesComponents.add("**/${component}")
        }
        return includesComponents
    }

    /**
     * Gets fields from list of objects
     * @param objectFiles contains objects to be analyzed
     * @return a list of fields
     */
    public static ArrayList<String> getFields(ArrayList<File> objectFiles) {
        def customFields = []
        objectFiles.each { objFile ->
            customFields.addAll(getCustomFields(objFile))
        }
        return customFields
    }

    /**
     * Gets all custom fields from a object file
     * @return a list of fields from an specific object
     */
    public static ArrayList<String> getCustomFields(File objectFile) {
        if (!objectFile.exists()) {
            throw new GradleException("File no found at:${objectFile.absolutePath}")
        }
        def CustomObject = new XmlParser().parseText(objectFile.text)
        def customFields = []
        def objectName = getFileName(objectFile.getName())
        if (!CustomObject) {
            throw new Exception("Object content is not valid")
        }
        CustomObject.namedFilters.each { namedFilter ->
            customFields.add(namedFilter.field.text())
        }
        CustomObject.fields.each { field ->
            def type = field.type.text()
            String objReference = "${field.referenceTo.text()}.${MetadataComponents.OBJECTS.getExtension()}"
            if (type == Constants.LOOKUP_NAME && PackageComponent.existObject(objectFile.parent, objReference)) {
                customFields.add("${objectName}.${field.fullName.text()}")
            }
        }
        return customFields
    }

    /**
     * Gets queries from package.xml
     * @return jsonQueries
     */
    public static ArrayList<String> getJsonQueries(String projectPackagePath, Credential credential) {
        ToolingAPI toolingAPI = new ToolingAPI(credential)
        QueryBuilder queryBuilder = new QueryBuilder()
        ArrayList<String> jsonQueries = []
        def queries = queryBuilder.createQueryFromPackage(projectPackagePath)
        queries.each { query ->
            jsonQueries.push(toolingAPI.httpAPIClient.executeQuery(query as String))
        }
        return jsonQueries
    }

    /**
     * Gets includes value by folder of files updated
     * @param fileNames is type Array List
     * @param parameterValue is an String
     * @param projectPath is base path to get relative path of files
     * @return includes value as String
     */
    public static String getIncludesValueByFolderFromFilesUpdated(ArrayList<File> files, String parameterValue, String projectPath) {
        ArrayList<String> fileNames = []
        ArrayList<String> filesToInclude = []
        files.each {File file ->
            fileNames.push(getRelativePath(file, projectPath))
        }
        String includes = fileNames.join(', ')

        if (parameterValue != "") {
            ArrayList<String> parameterValues = parameterValue.split(',')
            fileNames.each {String fileName ->
                if (parameterValues.contains(getFirstPath(fileName))) {
                    filesToInclude.push(fileName)
                }
            }
            includes = filesToInclude.join(', ')
        }
        return includes
    }

    public static Map<String, ComponentHash> getFilesWithTheirRelativePaths(Map<String, ComponentHash> recoveryFileHashCode, String projectPath) {
        Map<String, ComponentHash> result = [:]
        recoveryFileHashCode.any {String fileName, ComponentHash componentHash ->
            if(!Paths.get(fileName).isAbsolute()) {
                result = recoveryFileHashCode
                return
            }
            ComponentHash newComponentHash = new ComponentHash()
            newComponentHash.fileName = getRelativePath(new File(fileName), projectPath)
            newComponentHash.hash = componentHash.hash
            result.put(newComponentHash.fileName, newComponentHash)
        }
        return result
    }

    /**
     * Gets a files array from a directory
     * @param directory the directory to get its files
     * @return a files array
     */
    public static File[] getFiles(File directory) {
        File[] arrayFiles = []
        if (directory && directory.isDirectory()) {
            arrayFiles = directory.listFiles()
        }
        return arrayFiles
    }

    /**
     * Validates parameter's values
     * @param parameterValues are files name that will be excluded
     */
    public static void validateContentParameter(String projectPath, String filesParameter) {
        if (filesParameter == null) {
            return
        }
        String parameterValues = filesParameter
        parameterValues = parameterValues.replaceAll(Constants.BACK_SLASH, Constants.SLASH)
        ArrayList<File> filesToRetrieve = []
        ArrayList<String> folderNames = []
        parameterValues.split(Constants.COMMA).each { String parameter ->
            if (parameter.contains(Constants.SLASH)) {
                filesToRetrieve.push(new File(Paths.get(projectPath, parameter).toString()))
            } else {
                folderNames.push(parameter)
            }
        }
        //Validates folders
        ArrayList<String> invalidFolders = getInvalidFolders(folderNames)
        if (!invalidFolders.empty) {
            throw new Exception("${Constants.INVALID_FOLDER}: ${invalidFolders}")
        }
        //validates files
        ClassifiedFile classifiedFile = FileValidator.validateFiles(projectPath, filesToRetrieve)
        if (!classifiedFile.invalidFiles.isEmpty()) {
            throw new Exception("${Constants.INVALID_FILE}: ${classifiedFile.invalidFiles}")
        }
    }

    /**
     * Seeks an Id in the object Json
     * @param Id is a identifier of class or trigger
     * @param json is the result of a query to salesforce
     * @return a name class or trigger
     */
    public static String getApexNameByJson(String Id, String json) {
        String nameApex = ""
        JsonSlurper jsonSlurper = new JsonSlurper()
        for (elementSalesforce in jsonSlurper.parseText(json).records) {
            if (elementSalesforce.Id == Id) {
                nameApex = elementSalesforce.Name
                break
            }
        }
        return nameApex
    }
}
