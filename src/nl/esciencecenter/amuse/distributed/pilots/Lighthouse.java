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
package nl.esciencecenter.amuse.distributed.pilots;

import ibis.ipl.Ibis;
import ibis.ipl.IbisIdentifier;
import ibis.ipl.ReceivePort;
import ibis.ipl.SendPort;
import ibis.ipl.WriteMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import nl.esciencecenter.amuse.distributed.DistributedAmuse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Niels Drost
 * 
 */
public class Lighthouse extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(Lighthouse.class);

    private final Ibis ibis;

    private final PilotSet pilotSet;

    public Lighthouse(Ibis ibis, PilotSet pilotSet) {
        this.ibis = ibis;
        this.pilotSet = pilotSet;

        setDaemon(true);
        setName("Lighthouse");
        start();
    }

    public void run() {
        while (true) {
            ArrayList<IbisIdentifier> addresses = new ArrayList<IbisIdentifier>();

            for (PilotManager pilot : pilotSet.getPilots()) {
                if (pilot.isRunning()) {

                    try {
                        if (logger.isTraceEnabled()) {
                            logger.trace("Sending ping signal to " + Arrays.toString(addresses.toArray(new IbisIdentifier[0])));
                        }

                        SendPort sendPort = ibis.createSendPort(DistributedAmuse.MANY_TO_ONE_PORT_TYPE);

                        sendPort.connect(pilot.getIbisIdentifier(), "pilot", 60000, true);

                        WriteMessage writeMessage = sendPort.newMessage();

                        //command
                        writeMessage.writeString("ping");

                        writeMessage.finish();

                        sendPort.close();
                    } catch (IOException e) {
                        logger.error("Failed to send ping signal to pilot" + pilot, e);
                    }

                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    //IGNORE
                }
                //return;
            }
        }
    }
}
