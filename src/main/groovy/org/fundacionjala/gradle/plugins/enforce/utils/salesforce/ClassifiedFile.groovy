package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

import org.fundacionjala.gradle.plugins.enforce.utils.Util

class ClassifiedFile {
    ArrayList<File> invalidFiles
    ArrayList<File> validFiles
    ArrayList<File> filesNotFound
    ArrayList<File> filesWithoutMetadata

    private final String INVALID_FILES_TAG = "Invalid files"
    private final String FILES_NOT_FOUND_TAG = " Files not found"
    private final String FILES_WITHOUT_METADATA_TAG = "Files without metadata"
    private final String LINE = "***********************************************************************"

    ClassifiedFile() {
        invalidFiles = []
        validFiles = []
        filesNotFound = []
        filesWithoutMetadata = []
    }

    void ShowClassifiedFiles(Boolean show = true, String projectPath) {
        if (show) {
            if (!invalidFiles.isEmpty()) {
                showFiles(invalidFiles, INVALID_FILES_TAG, projectPath)
            }
            if (!filesNotFound.isEmpty()) {
                showFiles(filesNotFound, FILES_NOT_FOUND_TAG, projectPath)
            }
            if (!filesWithoutMetadata.isEmpty()) {
                showFiles(filesWithoutMetadata, FILES_WITHOUT_METADATA_TAG, projectPath)
            }
        }
    }

    private void showFiles(ArrayList<File> files, String fileType, String projectPath) {
        println LINE
        println "\t\t\t${fileType}"
        println LINE
        files.each {File file ->
            println Util.getRelativePath(file, projectPath)
        }
    }
}
