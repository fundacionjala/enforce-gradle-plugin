package org.fundacionjala.gradle.plugins.enforce.filemonitor

import com.twmacinta.util.MD5
import groovy.util.slurpersupport.GPathResult

class ObjectParser {

    ArrayList<String> customFields = ['ActionOverride', 'BusinessProcess', 'CompactLayout' , 'CustomField',
                                      'FieldSet', 'HistoryRetentionPolicy', 'ListView', 'RecordType', 'SearchLayouts',
                                      'SharingReason', 'SharingRecalculation', 'ValidationRule', 'Weblink', 'fields']

    public ObjectParser() {

    }

    public Map<String, String> parseByObjectXML(File file) {
        Map<String, String> result = [:]
        XmlSlurper xmlSlurper = new XmlSlurper()
        GPathResult customObject = xmlSlurper.parseText(file.text)
        customFields.each {String customField ->
            Object fieldValue = customObject.getProperty(customField)
            if (fieldValue) { //improve this part
                MD5 md5 = new MD5()
                md5.Update(fieldValue.toString())
                String fieldName = "${customField}/${fieldValue.fullName}"
                String signature = md5.asHex()
                println fieldName
                println signature
                result.put(fieldName, signature)
            }

        }
        return result
    }
}
