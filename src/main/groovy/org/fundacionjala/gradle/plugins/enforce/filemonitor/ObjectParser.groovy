package org.fundacionjala.gradle.plugins.enforce.filemonitor

import com.twmacinta.util.MD5
import groovy.util.slurpersupport.GPathResult

class ObjectParser {

    ArrayList<String> customFields = ['ActionOverride', 'BusinessProcess', 'CompactLayout' , 'CustomField',
                                      'FieldSet', 'HistoryRetentionPolicy', 'ListView', 'RecordType', 'SearchLayouts',
                                      'SharingReason', 'SharingRecalculation', 'ValidationRule', 'Weblink', 'fields', 'fieldSets']

    public ObjectParser() {

    }

    public Map<String, String> parseByObjectXML(File file) {
        Map<String, String> result = [:]
        XmlSlurper xmlSlurper = new XmlSlurper()
        GPathResult customObject = xmlSlurper.parseText(file.text)
        customFields.each {String customField ->
            Object fieldValue = customObject.getProperty(customField)
            if (!fieldValue.toString().isEmpty()) {
                fieldValue.each {subComponent ->
                    MD5 md5 = new MD5()
                    md5.Update(fieldValue.toString())
                    String fieldName = "${customField}/${subComponent.fullName}"
                    String signature = md5.asHex()
                    result.put(fieldName, signature)
                }
            }
        }
        return result
    }
}
