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
package nl.esciencecenter.amuse.distributed.remote;

import ibis.ipl.Ibis;
import ibis.ipl.ReadMessage;
import ibis.ipl.ReceivePortIdentifier;
import ibis.ipl.WriteMessage;

import java.io.File;
import java.io.IOException;

import nl.esciencecenter.amuse.distributed.AmuseConfiguration;
import nl.esciencecenter.amuse.distributed.jobs.AmuseJobDescription;
import nl.esciencecenter.amuse.distributed.jobs.FileTransfers;
import nl.esciencecenter.amuse.distributed.jobs.ScriptJobDescription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Niels Drost
 * 
 */
public class ScriptJobRunner extends JobRunner {

    private final ScriptJobDescription description;
    private Exception error = null;

    public ScriptJobRunner(AmuseJobDescription description, AmuseConfiguration configuration, ReceivePortIdentifier resultPort,
            Ibis ibis, File tmpDir, ReadMessage message) throws Exception {
        super(description, configuration, resultPort, ibis, tmpDir);

        this.description = (ScriptJobDescription) description;

        FileTransfers.readDirectory(sandbox, message);

        if (this.description.getInputDir() != null) {
            FileTransfers.readDirectory(sandbox, message);
        }

        message.finish();

        //start a thread to start handling amuse requests
        setName("Script Job Runner for " + description);
        setDaemon(true);
        start();
    }

    private synchronized void setError(Exception error) {
        this.error = error;
    }

    public synchronized Exception getError() {
        return error;
    }

    @Override
    public void run() {
        Exception error = null;

        try {

            File outputDir = new File(sandbox, description.getOutputDir());

            outputDir.mkdirs();

            File outputFile = new File(outputDir, "script-output.txt");
            outputFile.createNewFile();

        } catch (Exception e) {
            error = e;
        }

        sendResult(error);
    }

    @Override
    void writeResultData(WriteMessage writeMessage) throws IOException {
        File outputDir = new File(sandbox, description.getOutputDir());
        
        FileTransfers.writeDirectory(outputDir, writeMessage);
        
    }

}
