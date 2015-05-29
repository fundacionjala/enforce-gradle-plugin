/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.unittest.report

import java.nio.file.Paths

/**
 * Gets all resources for analysis report coverage
 */
class ReportResourceManager {
    private final String RESOURCE_TEMPLATE = '/index.html'
    private final String RESOURCE_TEMPLATE_FILE = '/file.html'
    private final String RESOURCE_TEMPLATE_FILE_ERROR = '/errorPage.html'
    private final String RESOURCE_JS = '/scripts/'
    private final String RESOURCE_STYLE = '/styles/'
    private final String RESOURCE_FONTS = '/fonts/'

    private final ArrayList<String> scriptFiles = ['clipboard.swf', 'shBrushJava.js', 'shCore.js',
                                                   'shLegacy.js', 'angular.min.js', 'bootstrap.min.js', 'jquery.min.js', 'ng-google-chart.js']
    private final ArrayList<String> stylesFiles = ['shCore.css', 'shThemeDefault.css', 'dashboard.css',
                                                   'bootstrap.min.css', 'bootstrap.css.map','reports.css']
    private final ArrayList<String> fontsFiles = ['glyphicons-halflings-regular.eot', 'glyphicons-halflings-regular.svg',
                                                  'glyphicons-halflings-regular.ttf', 'glyphicons-halflings-regular.woff',
                                                  'glyphicons-halflings-regular.woff2']

    public String pathReport

    /**
     * Gets a template for generate a index page
     * @return content of template index
     */
    public String getIndexTemplate() {
        return this.class.getResource(RESOURCE_TEMPLATE).text
    }

    /**
     * Gets a template for generate a file page
     * @return content of template file
     */
    public String getFileTemplate() {
        return this.class.getResource(RESOURCE_TEMPLATE_FILE).text
    }

    /**
     * Gets a template for generate a file page error
     * @return content of template file
     */
    public String getFileTemplateError() {
        return this.class.getResource(RESOURCE_TEMPLATE_FILE_ERROR).text
    }

    /**
     * Copy all the resource script
     */
    public void copyResourceScripts() {

        String pathFilesScripts = Paths.get(pathReport, RESOURCE_JS).toString()

        File folderScripts = new File(pathFilesScripts)
        folderScripts.mkdir()

        scriptFiles.each { fileName ->

            FileOutputStream fileJS = new FileOutputStream(Paths.get(pathFilesScripts, fileName).toString())
            byte[] byteJs = this.class.getResource("${RESOURCE_JS}${fileName}").getBytes()
            fileJS.write(byteJs)
            fileJS.close()

        }
    }

    /**
     * Copy all the resource Style
     */
    public void copyResourceStyles() {

        String pathFilesStyles = Paths.get(pathReport, RESOURCE_STYLE).toString()
        File folderStyles = new File(pathFilesStyles)
        folderStyles.mkdir()

        stylesFiles.each { fileName ->
            FileOutputStream styleFile = new FileOutputStream(Paths.get(pathFilesStyles, fileName).toString())
            byte[] byteStyle = this.class.getResource("${RESOURCE_STYLE}${fileName}").getBytes()
            styleFile.write(byteStyle)
            styleFile.close()
        }
    }

    /**
     * Copy all the resource Fonts
     */
    public void copyFonts() {

        String pathFilesFonts = Paths.get(pathReport, RESOURCE_FONTS).toString()
        File folderStyles = new File(pathFilesFonts)
        folderStyles.mkdir()

        fontsFiles.each { fileName ->
            FileOutputStream fileFonts = new FileOutputStream(Paths.get(pathFilesFonts, fileName).toString())
            byte[] byteStyle = this.class.getResource("${RESOURCE_FONTS}${fileName}").getBytes()
            fileFonts.write(byteStyle)
            fileFonts.close()
        }
    }
}
