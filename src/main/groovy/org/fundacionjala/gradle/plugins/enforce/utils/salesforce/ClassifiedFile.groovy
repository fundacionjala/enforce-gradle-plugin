package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

class ClassifiedFile {
    ArrayList<File> invalidFiles
    ArrayList<File> validFiles
    ArrayList<File> notFoundFiles
    ArrayList<File> filesWithoutXml

    private final String INVALID_FILES_TAG = "Invalid files"
    private final String NOT_FOUND_FILES_TAG = "Not found files"
    private final String WITHOUT_XML_FILES_TAG = "Without xml files"
    private final String LINE = "***********************************************************************"

    ClassifiedFile() {
        invalidFiles = []
        validFiles = []
        notFoundFiles = []
        filesWithoutXml = []
    }

    void ShowClassifiedFiles(Boolean show = true) {
        if (show) {
            if (!invalidFiles.isEmpty()) {
                showFiles(invalidFiles, INVALID_FILES_TAG)
            }
            if (!notFoundFiles.isEmpty()) {
                showFiles(notFoundFiles, NOT_FOUND_FILES_TAG)
            }
            if (!filesWithoutXml.isEmpty()) {
                showFiles(filesWithoutXml, WITHOUT_XML_FILES_TAG)
            }
        }
    }

    private void showFiles(ArrayList<File> files, String fileType) {
        println LINE
        println "\t\t\t${fileType}"
        println LINE
        files.each {File file ->
            println file
        }
    }
}
