/*
 * Copyright 2013 Netherlands eScience Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.esciencecenter.amuse.distributed.jobs;

import ibis.ipl.Ibis;
import ibis.ipl.ReadMessage;
import ibis.ipl.WriteMessage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.esciencecenter.amuse.distributed.DistributedAmuseException;

/**
 * @author Niels Drost
 * 
 */
public class ScriptJob implements AmuseJob {

    private static final Logger logger = LoggerFactory.getLogger(ScriptJob.class);

    public static final int BUFFER_SIZE = 0;

    private final String scriptName;
    private final String arguments;
    private final String scriptDir;
    private final String inputDir;
    private final String outputDir;
    private final String nodeLabel;

    public ScriptJob(String scriptName, String arguments, String scriptDir, String inputDir, String outputDir, String nodeLabel)
            throws DistributedAmuseException {

        this.scriptName = scriptName;
        this.arguments = arguments;
        this.scriptDir = scriptDir;
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.nodeLabel = nodeLabel;
    }

    private void writeFile(File file, WriteMessage writeMessage, ByteBuffer buffer) throws IOException {
        writeMessage.writeString(file.getPath());
        writeMessage.writeLong(file.length());
        try (FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
            while (true) {
                //write file in 1000 byte chunks in case it is big
                buffer.clear();

                int read = channel.read(buffer);

                if (read == -1) {
                    return;
                }

                buffer.flip();
                writeMessage.writeByteBuffer(buffer);
            }
        }

    }

    private void addFiles(File file, ArrayList<File> result) {
        if (file.isFile()) {
            result.add(file);
            logger.debug("Added file: " + file);
        } else if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                addFiles(child, result);
            }
        }
    }

    private void writeDirectory(String filename, WriteMessage writeMessage, ByteBuffer buffer) throws IOException {
        File directory = new File(filename);

        if (!directory.isDirectory()) {
            throw new IOException("Directory \"" + filename + "\" not found");
        }

        ArrayList<File> files = new ArrayList<File>();

        //recursively add all files
        addFiles(directory, files);

        writeMessage.writeInt(files.size());
        for (File file : files) {
            writeFile(file, writeMessage, buffer);
        }
    }

    private void readFile(File directory, ReadMessage readMessage, ByteBuffer buffer) throws IOException {
        String filename = readMessage.readString();
        long size = readMessage.readLong();

        File file = new File(directory, filename);

        logger.debug("Reading file " + file);

        try (FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {

            long bytesLeft = size;

            while (bytesLeft > 0) {
                buffer.clear();
                buffer.limit((int) Math.min(buffer.capacity(), bytesLeft));

                readMessage.readByteBuffer(buffer);

                buffer.flip();

                while (buffer.hasRemaining()) {
                    int written = channel.write(buffer);
                    bytesLeft -= written;
                }
            }
        }
    }

    private void readDirectory(String filename, ReadMessage readMessage, ByteBuffer buffer) throws IOException {
        File directory = new File(filename);

        if (!directory.isDirectory()) {
            directory.mkdir();
        }

        if (!directory.isDirectory()) {
            throw new IOException("Directory \"" + filename + "\" could not be created");
        }

        int count = readMessage.readInt();

        for (int i = 0; i < count; i++) {
            readFile(directory, readMessage, buffer);
        }

    }

    /**
     * @param writeMessage
     * @throws IOException
     */
    @Override
    public void writeInputTo(WriteMessage writeMessage) throws IOException {
        writeMessage.writeString(scriptName);
        writeMessage.writeString(arguments);
        writeMessage.writeString(scriptDir);
        writeMessage.writeString(inputDir);
        writeMessage.writeString(outputDir);
        writeMessage.writeString(nodeLabel);

        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        writeDirectory(scriptDir, writeMessage, buffer);
        writeDirectory(inputDir, writeMessage, buffer);
    }
    
    /**
     * @param writeMessage
     * @throws IOException
     */
    @Override
    public void writeOutputTo(WriteMessage writeMessage) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void readOutputFrom(ReadMessage readMessage) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        readDirectory(outputDir, readMessage, buffer);
    }

    public String getScriptName() {
        return this.getScriptName();
    }

    public String getArguments() {
        return this.getArguments();
    }

    public String getScriptDir() {
        return this.getScriptDir();
    }

    public String getInputDir() {
        return this.getInputDir();
    }

    public String getOutputDir() {
        return this.getOutputDir();
    }



}
