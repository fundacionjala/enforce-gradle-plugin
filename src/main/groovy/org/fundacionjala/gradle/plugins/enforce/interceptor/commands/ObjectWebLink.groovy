/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.interceptor.commands

import groovy.util.logging.Slf4j
import org.fundacionjala.gradle.plugins.enforce.utils.Util

import java.nio.charset.StandardCharsets
import java.util.regex.Matcher

/**
 * Implements the truncated algorithm to replace a "web link" by default for all "web links"
 * that matches with the regex in an object file
 */
@Slf4j
class ObjectWebLink {
    private final String TAG_URL = '<url>%s</url>'
    private final String WEB_LINK_REGEX = /<webLinks>.*([^\n]*?\n+?)*?.*<\/webLinks>/
    private final String LINK_TYPE_REGEX = /<linkType>(([^\n]*?\n+?)*?.*)<\/linkType>/
    private final String URL_REGEX = /<url>(([^\n]*?\n+?)*?.*)<\/url>/
    private final String URL_JS_BY_DEFAULT = "alert('')"
    private final String URL_BY_DEFAULT = "http://www.google.com"
    private final int CONTENT_MATCHED_INDEX = 0
    private final int TYPE_INDEX = 1
    String encoding

    ObjectWebLink() {
        this.encoding = StandardCharsets.UTF_8.displayName()
    }

    /**
     * A closure to replace a "web link" by default for all "web links"
     * that matches with the regex in an object file
     */
    Closure execute = { file ->
        if (!file) return
        String charset = Util.getCharset(file)
        String objectWebLink = file.text
        Matcher webLinkMatcher = objectWebLink =~ WEB_LINK_REGEX
        webLinkMatcher.each { webLinkIt->
            String webLink = webLinkIt[CONTENT_MATCHED_INDEX]
            String url
            String type
            Matcher urlMatcher = webLink =~ URL_REGEX
            urlMatcher.each { urlIt->
                url = urlIt[CONTENT_MATCHED_INDEX]
            }
            Matcher typeMatcher = webLink =~ LINK_TYPE_REGEX
            typeMatcher.each { typeIt->
                type = typeIt[TYPE_INDEX]
            }
            if (url) {
                type = type.trim()
                String target = webLink
                String replacement
                switch (type) {
                    case LinkType.JAVASCRIPT.value():
                        replacement = webLink.replace(url, String.format(TAG_URL, URL_JS_BY_DEFAULT))
                        objectWebLink = objectWebLink.replace(target, replacement)
                        break
                    case LinkType.URL.value():
                        replacement = webLink.replace(url, String.format(TAG_URL, URL_BY_DEFAULT))
                        objectWebLink = objectWebLink.replace(target, replacement)
                        break
                }
            }
        }
        Util.writeFile(file, objectWebLink, charset, encoding)
    }

}
