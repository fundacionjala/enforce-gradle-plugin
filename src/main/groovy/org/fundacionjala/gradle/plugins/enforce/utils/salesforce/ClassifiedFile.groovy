package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

class ClassifiedFile {
    ArrayList<File> invalidFiles
    ArrayList<File> validFiles
    ArrayList<File> notFoundFiles
    ArrayList<File> filesWithoutXml

    ClassifiedFile() {
        invalidFiles = []
        validFiles = []
        notFoundFiles = []
        filesWithoutXml = []
    }
}
