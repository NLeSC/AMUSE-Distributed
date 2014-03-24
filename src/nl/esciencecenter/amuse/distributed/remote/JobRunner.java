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
import ibis.ipl.ReceivePortIdentifier;
import ibis.ipl.SendPort;
import ibis.ipl.WriteMessage;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.esciencecenter.amuse.distributed.AmuseConfiguration;
import nl.esciencecenter.amuse.distributed.DistributedAmuse;
import nl.esciencecenter.amuse.distributed.jobs.AmuseJobDescription;

/**
 * @author Niels Drost
 * 
 */
public abstract class JobRunner extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(JobRunner.class);

    protected Process process = null;

    protected final AmuseJobDescription description;
    protected final AmuseConfiguration amuseConfiguration;
    protected final Ibis ibis;
    protected final ReceivePortIdentifier resultPort;
    protected final File tmpDir;
    protected final File sandbox;

    /**
     * @param jobID
     * @param amuseConfiguration
     * @param resultPort
     * @param ibis
     * @param tmpDir
     */
    public JobRunner(AmuseJobDescription description, AmuseConfiguration amuseConfiguration, ReceivePortIdentifier resultPort,
            Ibis ibis, File tmpDir) {
        this.description = description;
        this.amuseConfiguration = amuseConfiguration;
        this.resultPort = resultPort;
        this.ibis = ibis;
        this.tmpDir = tmpDir;

        this.sandbox = new File(tmpDir, Integer.toString(description.getID()));
    }

    protected synchronized void nativeKill() {
        if (process == null) {
            return;
        }

        try {
            Field f = process.getClass().getDeclaredField("pid");
            f.setAccessible(true);

            Object pid = f.get(process);

            ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", "kill -9 " + pid.toString());

            builder.redirectError(Redirect.INHERIT);
            //builder.redirectInput();
            builder.redirectOutput(Redirect.INHERIT);

            logger.info("Killing process using command: " + Arrays.toString(builder.command().toArray()));

            Process killProcess = builder.start();

            killProcess.getOutputStream().close();

            int exitcode = killProcess.waitFor();

            logger.info("native kill done, result is " + exitcode);

        } catch (Throwable t) {
            logger.error("Error on (forcibly) killing process", t);
        }
    }

    protected void sendResult(Exception error) {
        logger.debug("worker done. Sending result to main amuse node.");

        //send result message to job
        try {
            SendPort sendPort = ibis.createSendPort(DistributedAmuse.MANY_TO_ONE_PORT_TYPE);

            sendPort.connect(resultPort);

            WriteMessage message = sendPort.newMessage();

            message.writeObject(error);
            
            writeResultData(message);
            
            message.finish();

            sendPort.close();

        } catch (IOException e) {
            logger.error("Failed to report status to main node", e);
        }

    }
    
    abstract void writeResultData(WriteMessage message) throws IOException ;

}
