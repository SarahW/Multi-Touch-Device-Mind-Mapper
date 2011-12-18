/***********************************************************************
 * Copyright (c) 2011 Luu Gia Thuy
 * 
 * Modifications Copyright (c) 2011 Sarah Will
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 ***********************************************************************/

package de.sarahw.ma.pc.btServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.microedition.io.StreamConnection;

import org.apache.log4j.Logger;

import de.sarahw.ma.pc.mindMapper.model.AppModel;
import de.sarahw.ma.pc.mindMapper.model.EIdeaNodeCreator;
import de.sarahw.ma.pc.mindMapper.model.IdeaNode;
import de.sarahw.ma.pc.mindMapper.model.NodeContent;
import de.sarahw.ma.pc.mindMapper.model.NodeData;
import de.sarahw.ma.pc.mindMapper.model.NodeMetaData;

/**
 * <p>
 * Sets up a new thread to wait for connections from clients and handle the
 * signal.
 * </p>
 * 
 * <p>
 * Adapted from https://github.com/luugiathuy/Remote-Bluetooth-Android
 * </p>
 * 
 * <p>
 * Modified 2011-08-15 <br>
 * - added connection close behaviour <br>
 * - added readInputStreamString() method
 * </p>
 * 
 * @author Luu Gia Thuy
 * @author (Modified by) Sarah Will
 * 
 * @version 1.0
 * 
 */
public class ProcessConnectionThread implements Runnable {

    private static Logger       log                    = Logger.getLogger(ProcessConnectionThread.class);

    /** A hash value that signals that the connection has been closed. */
    private static final String CONNECTION_CLOSED_HASH = "318ec526e76502a583acd94f49817cf2";             //$NON-NLS-1$

    /** The (stream) connection instance */
    private StreamConnection    mConnection;

    /** The application model instance. */
    private AppModel            model;

    /** The thread that handles connection requests. */
    private WaitBtThread        waitThread;

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new ProcessConnectionThread for the given
     * StreamConnection object.
     * 
     */
    public ProcessConnectionThread(StreamConnection connection, AppModel model,
            WaitBtThread waitThread) {

        log.debug("Executing ProcessConnectionThread(connection=" + connection //$NON-NLS-1$
                + ", model=" + model + ", waitThread=" + waitThread + ")"); //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$

        // Set references
        this.model = model;
        this.mConnection = connection;
        this.waitThread = waitThread;
    }

    /* ********Overridden methods******** */
    /**
     * Creates thread to process the input stream.
     * 
     */
    @Override
    public void run() {
        log.debug("Entering run()"); //$NON-NLS-1$ 
        try {
            // prepare to receive data
            InputStream inputStream = this.mConnection.openInputStream();

            log.debug("Waiting for input."); //$NON-NLS-1$

            while (true) {

                // Read the string from the input stream
                String text = readInputStreamStringUnicode(inputStream);

                // If the connection has been closed
                // remove the connection from the list
                if (text.equals(CONNECTION_CLOSED_HASH)) {

                    log.debug("Connection closed. Finish process."); //$NON-NLS-1$

                    this.waitThread.removeConnectionFromList(this.mConnection);
                    break;
                }

                // Check if the length does not exceed the max length
                if (text.length() <= NodeContent.IDEA_TXT_LNGTH_MAX) {

                    // Create a new model idea node with the text
                    // from the input stream
                    createNewIdeaNode(text);

                } else {
                    log.error("Input text from remote device is too long! No idea node created."); //$NON-NLS-1$
                }

            }
        } catch (Exception e) {
            log.warn("Exception encountered while opening file stream" + e.getMessage()); //$NON-NLS-1$

            log.debug("Connection closed. Finish process."); //$NON-NLS-1$

            this.waitThread.removeConnectionFromList(this.mConnection);
            e.printStackTrace();
        }
        log.debug("Leaving run()"); //$NON-NLS-1$ 
    }

    /* *********Utility methods********* */
    /**
     * Create a new IdeaNodeView from the received text.
     * 
     * @param text
     *            the ideaText from the client
     */
    private void createNewIdeaNode(String text) {

        log.debug("Entering createNewIdeaNode(text=" + text + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (text.length() > 0) {

            if (this.model != null) {

                // Add new IdeaNode to the model
                // changes will be picked up by the observer in the view
                IdeaNode newIdeaNode = new IdeaNode(new NodeData(
                        new NodeContent(text), new NodeMetaData(0, 0, 0.0f,
                                EIdeaNodeCreator.BLUETOOTH_SERVER)));

                this.model.getLoadedMindMap().addIdeaNode(newIdeaNode);

            } else {
                log.error("The model reference is invalid! (null)"); //$NON-NLS-1$
            }
        }
        log.debug("Leaving createNewIdeaNode()"); //$NON-NLS-1$ 
    }

    /**
     * <p>
     * Converts the given InputStream to a Unicode String.
     * </p>
     * 
     * @param inputStream
     *            the InputStream
     * 
     * @return the string read from the input stream
     */
    public String readInputStreamStringUnicode(InputStream inputStream) {

        log.debug("Entering readInputStreamStringUnicode(inputStream=" + inputStream + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        StringBuffer strBuffer = new StringBuffer();

        try {
            log.debug("Create new BufferedReader: "); //$NON-NLS-1$

            InputStreamReader instr = new InputStreamReader(inputStream, "UTF8"); //$NON-NLS-1$
            BufferedReader in = new BufferedReader((instr));

            try {

                int bytesRead = -1;

                while (true) {

                    log.debug("First read try: "); //$NON-NLS-1$

                    // Read stream
                    bytesRead = in.read();

                    log.debug("Bytes read: " + bytesRead); //$NON-NLS-1$

                    // If we are not at the end of the stream yet
                    // (connection closed etc.)
                    if (bytesRead != -1) {

                        strBuffer.append((char) bytesRead);

                        log.trace("Current string: " + strBuffer.toString()); //$NON-NLS-1$

                        if (in.ready()) {

                            do {

                                bytesRead = in.read();
                                // bytesRead = instr.read(inputBuffer);
                                log.trace("Bytes read inside: " + bytesRead); //$NON-NLS-1$

                                if (bytesRead != -1) {
                                    strBuffer.append((char) bytesRead);
                                    log.trace("Current string: " + strBuffer.toString()); //$NON-NLS-1$
                                }
                            } while (bytesRead > -1 && in.ready());

                        }

                        log.debug("We are at the end of the stream!"); //$NON-NLS-1$

                        log.debug("Leaving readInputStreamStringUnicode():" + strBuffer.toString()); //$NON-NLS-1$
                        return strBuffer.toString();
                    }

                    log.debug("Leaving readInputStreamStringUnicode(): connection closed"); //$NON-NLS-1$
                    // We got no input stream anymore
                    // TODO ! we have to make sure we never send no data !?
                    return CONNECTION_CLOSED_HASH;

                }
            } catch (IOException e) {
                log.debug("InputStream exception" + e.getMessage()); //$NON-NLS-1$

                try {
                    // Close socket connection
                    this.mConnection.close();
                } catch (IOException e1) {

                    e1.printStackTrace();
                    log.error("Exception while closing bluetooth connection:" + e.getMessage()); //$NON-NLS-1$

                }

                log.debug("Leaving readInputStreamStringUnicode(): connection closed"); //$NON-NLS-1$

                // We got no input stream anymore
                return CONNECTION_CLOSED_HASH;
            }

        } catch (UnsupportedEncodingException e2) {

            e2.printStackTrace();

            log.debug("Leaving readInputStreamStringUnicode(): encoding not supported"); //$NON-NLS-1$

            // Call default string reader
            return readInputStreamString(inputStream);
        }

    }

    /**
     * <p>
     * Converts the given InputStream to a String with default encoding.
     * </p>
     * 
     * @param inputStream
     *            the InputStream
     * 
     * @return the string read from the input stream
     */
    public String readInputStreamString(InputStream inputStream) {

        log.debug("Entering readInputStreamString()"); //$NON-NLS-1$

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        String ideaText = ""; //$NON-NLS-1$
        try {

            int bytesRead = -1;

            while (true) {
                ideaText = ""; //$NON-NLS-1$

                log.trace("First read try: "); //$NON-NLS-1$

                // Read stream
                bytesRead = inputStream.read(buffer);
                log.trace("Bytes read: " + bytesRead); //$NON-NLS-1$

                // If we are not at the end of the stream yet
                // (connection closed etc.)
                if (bytesRead != -1) {

                    while ((bytesRead == bufferSize)
                            && (buffer[bufferSize - 1] != 0)) {
                        ideaText = ideaText + new String(buffer, 0, bytesRead);
                        bytesRead = inputStream.read(buffer);
                        log.debug("Bytes read in loop: " + bytesRead); //$NON-NLS-1$
                        log.debug("Current text without last " + ideaText); //$NON-NLS-1$

                    }

                    log.trace("We are at the end of the stream!"); //$NON-NLS-1$

                    ideaText = ideaText + new String(buffer, 0, bytesRead - 1);

                    log.debug("Leaving readInputStreamString():" + ideaText); //$NON-NLS-1$
                    return ideaText;
                }

                log.debug("Leaving readInputStreamString(): connection closed"); //$NON-NLS-1$
                // We got no input stream anymore
                // TODO ! we have to make sure we never send no data
                return CONNECTION_CLOSED_HASH;

            }
        } catch (IOException e) {
            log.debug("InputStream exception" + e.getMessage()); //$NON-NLS-1$

            try {
                this.mConnection.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                log.error("Exception while closing bluetooth connection:" + e.getMessage()); //$NON-NLS-1$

            }

            log.debug("Leaving readInputStreamString(): connection closed"); //$NON-NLS-1$

            // We got no input stream anymore
            return CONNECTION_CLOSED_HASH;
        }

    }

}
