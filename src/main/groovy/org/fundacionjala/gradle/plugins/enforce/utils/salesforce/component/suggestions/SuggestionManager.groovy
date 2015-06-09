package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.suggestions

import org.fundacionjala.gradle.plugins.enforce.utils.Constants
import org.fundacionjala.gradle.plugins.enforce.utils.ManagementFile

import java.nio.file.Paths

/**
 * Created by alex_ventura on 09-06-15.
 */
public class SuggestionManager {
    public static String PROCESSING = 'Processing'
    public static String processStateDetail(String stateDetail) {
        StringBuilder result = new StringBuilder()
        if (stateDetail.startsWith(PROCESSING)) {
            String filePath = stateDetail.replaceFirst(PROCESSING, "").trim()
            String parentPath = Paths.get(filePath).getName(0)
            if (parentPath != filePath) {
                String folder = parentPath
                result.append("Make sure the $folder")
                result.append(' folder has xml file and it is defined in the package.xml')
            }
        }

        return result.toString();
    }
}
