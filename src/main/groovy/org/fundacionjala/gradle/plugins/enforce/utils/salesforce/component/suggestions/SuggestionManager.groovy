package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.suggestions

import org.fundacionjala.gradle.plugins.enforce.utils.Constants

import java.nio.file.Paths

/**
 * Manager to generate suggestion when the task failed
 */
public class SuggestionManager {
    public static final String PROCESSING = 'Processing'
    public static String processStateDetail(String stateDetail) {
        StringBuilder result = new StringBuilder()
        if (stateDetail.startsWith(PROCESSING)) {
            String filePath = stateDetail.replaceFirst(PROCESSING, "").trim()
            String parentPath = Paths.get(filePath).getName(Constants.ZERO)
            if (parentPath != filePath) {
                String folder = parentPath
                result.append("Salesforce has reported an unexpected error:\n  A common cause is about $folder")
                result.append(' folder doesn´t have a respective XML file or it is not defined in the package.xml')
            }
        }

        return result.toString();
    }
}
