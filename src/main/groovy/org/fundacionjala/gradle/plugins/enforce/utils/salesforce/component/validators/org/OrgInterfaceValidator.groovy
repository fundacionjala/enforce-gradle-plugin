package org.fundacionjala.gradle.plugins.enforce.utils.salesforce.component.validators.org

import org.fundacionjala.gradle.plugins.enforce.wsc.Credential

/**
 * Created by marcelo_oporto on 25-06-15.
 */
public interface OrgInterfaceValidator {
    Map<String,ArrayList<File>> validateFiles(Credential credential, ArrayList<File> filesToVerify, String folderComponent, String path)
}
