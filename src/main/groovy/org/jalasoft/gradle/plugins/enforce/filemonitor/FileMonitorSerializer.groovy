/*
 * Copyright (c) Jalasoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.jalasoft.gradle.plugins.enforce.filemonitor

/**
 * Get and save a map with information of the file path and hashCode
 */
class FileMonitorSerializer extends TemplateFileMonitor {
    /**
     * Constructor of default if userInfo not put nameFile
     */
    public FileMonitorSerializer(String sourceNameFile) {
        super(sourceNameFile)
    }

    /**
     * Constructor of default if userInfo not put nameFile
     */
    public FileMonitorSerializer() {
        super()
    }

    /**
     * Save a map in the file binary
     * @param myMap
     * @throws IOException
     */
    @Override
    void saveMap(myMap) throws IOException {
        ObjectOutputStream oos
        oos = new ObjectOutputStream(new FileOutputStream(nameFile))
        oos.writeObject(myMap)
        oos.close()
    }

    /**
     * Read a map of the file binary
     * @param source is the path of the file binary
     * @return a Map of hashCode the files
     * @throws IOException if cannot write in the disk
     */
    @Override
    Map readMap(source) throws IOException {
        Map recoveryMap
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(source))
        recoveryMap = (Map) ois.readObject()
        ois.close()
        return recoveryMap
    }
}
