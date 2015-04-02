/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.utils

import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel
import java.nio.file.Paths

/**
 * Managements zip files to create and delete zip files in base its path
 */
class ZipFileManager {

    private final int MEMORY_SPACE_FLUSH = 8092
    private final int END_BUFFER = -1
    private final String NAME_ZIP = 'deploy.zip'
    private final String OVERWRITE = 'true'
    private final String FLUSHING_EXCEPTION = 'Error while flushing zip File'
    AntBuilder ant

    ZipFileManager() {
        ant = new AntBuilder()
    }

    /**
     * Create a zip file
     * @param pathBuild is the source path file zip
     * @param sourceDeploy is the folder source project user
     * @return a path of the zip file
     */
    String createZipDeploy(String pathBuild, String sourceDeploy) {

        String pathZipFile = Paths.get(pathBuild, NAME_ZIP).toString()

        ant.zip(destfile: pathZipFile,
                basedir: sourceDeploy)

        return pathZipFile
    }

    /**
     * Unzip a file zip in the directory specified
     * @param zipPath is the path file zip
     * @param folderUnZip is the destination unzip
     */
    public void unZip(String zipPath, String folderUnZip) {

        ant.unzip(src: zipPath,
                dest: folderUnZip,
                overwrite: OVERWRITE)
    }

    /**
     * Unzip a file zip in the directory specified
     * @param zipPath is the path file zip
     * @param folderUnZip is the destination unzip
     */
    public void unzipZipRetrieved(String zipPath, String folderUnZip) {
        ant.unzip(src: zipPath,
                dest: folderUnZip,
                overwrite: OVERWRITE)
    }

    /**
     * Flushes zip file in a path and with the typeName assigned
     * @param zipFile contains bytes collection
     * @param destinePath contains the path where zip file will be flushed
     * @param zipFileName contains the typeName of zip file
     */
    public void flushZipFile(byte[] zipFile, String destinePath, String zipFileName) {
        ByteArrayInputStream zipStream = new ByteArrayInputStream(zipFile)
        File resultsFile = new File(Paths.get(destinePath, zipFileName).toString())
        FileOutputStream outputStream = new FileOutputStream(resultsFile)
        try {
            ReadableByteChannel source = Channels.newChannel(zipStream)
            FileChannel destine = outputStream.getChannel()
            flushOperation(source, destine)
        } catch (Exception exception) {
            println("${FLUSHING_EXCEPTION}\n ${exception}")
        } finally {
            outputStream.close()
        }
    }

    /**
     * Flushes bytes from a memory to Disk using an in-memory buffer.
     * @param source contains the source direction of chanel to read it
     * @param destine contains the destine direction of chanel to write it
     * @throws IOException an exception if actions is not possible for IO rasons
     */
    private void flushOperation(ReadableByteChannel source, WritableByteChannel destine) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(MEMORY_SPACE_FLUSH)
        while (source.read(buffer) != END_BUFFER) {
            buffer.flip()
            while (buffer.hasRemaining()) {
                destine.write(buffer)
            }
            buffer.clear()
        }
    }
}
