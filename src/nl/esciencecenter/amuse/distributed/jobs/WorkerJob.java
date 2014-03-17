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

import ibis.ipl.ReadMessage;
import ibis.ipl.WriteMessage;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import nl.esciencecenter.amuse.distributed.AmuseMessage;

/**
 * Description of a worker.
 * 
 * 
 * @author Niels Drost
 */
public class WorkerJob implements AmuseJob {

    private final String id;
    private final String executable;
    private final String stdoutFile;
    private final String stderrFile;
    private final String nodeLabel;

    private final int nrOfWorkers;
    private final int nrOfThreads;

    private final int startupTimeout;

    public WorkerJob(String id, String executable, String stdoutFile, String stderrFile, String nodeLabel,
            int nrOfWorkers, int nrOfThreads, int startupTimeout) {
        this.id = id;
        this.executable = executable;
        this.stdoutFile = stdoutFile;
        this.stderrFile = stderrFile;
        this.nodeLabel = nodeLabel;
        this.nrOfWorkers = nrOfWorkers;
        this.nrOfThreads = nrOfThreads;
        this.startupTimeout = startupTimeout;
    }

    /**
     * @param message
     *            a message containing all required fields of a worker description
     * @throws IOException
     *             if the message cannot be read
     */
    public WorkerJob(AmuseMessage message, String id) throws IOException {
        this.id = id;
        executable = message.getString(0);
        stdoutFile = message.getString(1);
        stderrFile = message.getString(2);

        if (message.getString(3).isEmpty()) {
            nodeLabel = null;
        } else {
            nodeLabel = message.getString(3);
        }

        nrOfWorkers = message.getInteger(0);
        nrOfThreads = message.getInteger(2);
        startupTimeout = message.getInteger(3);
    }
    
    //read input from message
    public WorkerJob(ReadMessage message) throws IOException {
        this.id = message.readString();
        this.executable = message.readString();
        this.stdoutFile = message.readString();
        this.stderrFile = message.readString();
        this.nodeLabel = message.readString();
        
        this.nrOfWorkers = message.readInt();
        this.nrOfThreads = message.readInt();
        
        this.startupTimeout = message.readInt();
    }
    
    public void writeInputTo(WriteMessage writeMessage) throws IOException {
        writeMessage.writeString(id);
        writeMessage.writeString(executable);
        writeMessage.writeString(stdoutFile);
        writeMessage.writeString(stderrFile);
        writeMessage.writeString(nodeLabel);
        writeMessage.writeInt(nrOfWorkers);
        writeMessage.writeInt(nrOfThreads);
        writeMessage.writeInt(startupTimeout);
    }
    
    public void writeOutputTo(WriteMessage writeMessage) throws IOException {
        //NOTHING
    }

    public void readOutputFrom(ReadMessage readMessage) throws IOException {
        //NOTHING
    }

    public String getID() {
        return id;
    }

    /**
     * Executable relative to AMUSE distribution root.
     */
    public String getExecutable() {
        return executable;
    }

    public String getStdoutFile() {
        return stdoutFile;
    }

    public String getStderrFile() {
        return stderrFile;
    }

    public String getNodeLabel() {
        return nodeLabel;
    }

    public int getNrOfWorkers() {
        return nrOfWorkers;
    }

    public int getNrOfThreads() {
        return nrOfThreads;
    }

    public int getStartupTimeout() {
        return startupTimeout;
    }
    
    @Override
    public String toString() {
        return "WorkerDescription [executable=" + executable + ", stdoutFile=" + stdoutFile + ", stderrFile=" + stderrFile
                + ", nodeLabel=" + nodeLabel + ", nrOfWorkers=" + nrOfWorkers + ", nrOfThreads="
                + nrOfThreads + ", startupTimeout=" + startupTimeout + "]";
    }
}
