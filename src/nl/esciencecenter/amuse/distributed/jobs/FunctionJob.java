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

import java.io.IOException;

/**
 * @author Niels Drost
 *
 */
public class FunctionJob implements AmuseJob {

    /**
     * @param writeMessage
     * @throws IOException
     */
    @Override
    public void writeInputTo(WriteMessage writeMessage) throws IOException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param writeMessage
     * @throws IOException
     */
    @Override
    public void writeOutputTo(WriteMessage writeMessage) throws IOException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param readMessage
     * @throws IOException
     */
    @Override
    public void readOutputFrom(ReadMessage readMessage) throws IOException {
        // TODO Auto-generated method stub
        
    }
}
