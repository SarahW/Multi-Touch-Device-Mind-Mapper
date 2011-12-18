/*
 * Copyright (C) 2011 Luu Gia Thuy (original source)
 * 
 * Copyright (C) 2011 Sarah Will (modifications)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package de.sarahw.ma.phone;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * <p>
 * The BluetoothCommandService. Handles bluetooth connection and input/output
 * Stream operations.
 * </p>
 * <p>
 * Bluetooth code adapted from
 * https://github.com/luugiathuy/Remote-Bluetooth-Android
 * (BluetoothCommandService.java)
 * </p>
 * 
 * <p>
 * Modified 2011-09-18
 * </p>
 * 
 * @author Luu Gia Thuy (BluetoothCommandService.java)
 * @author Sarah Will (BluetoothCommandService.java modifications, additional
 *         code)
 * 
 */
@SuppressWarnings("synthetic-access")
public class BluetoothCommandService {
    // Debugging
    private static final String    TAG                    = "BluetoothCommandService";                              //$NON-NLS-1$

    // Unique UUID for this application
    /**
     * The unique application UUID matching the one defined in the class
     * de.sarahw.ma.pc.btServer.WaitBtThread on the remote device
     * 
     * fae3fa60-e2ce-11e0-9572-0800200c9a66
     */
    private static final UUID      MY_UUID                = UUID.fromString("fae3fa60-e2ce-11e0-9572-0800200c9a66"); //$NON-NLS-1$

    // Constants that indicate the current connection state
    /** The constant indicating that the connection has no state as of now */
    public static final int        STATE_NONE             = 0;
    /**
     * The constant indicating that the connection is listening for incoming
     * connection as of now
     */
    public static final int        STATE_LISTEN           = 1;
    /**
     * The constant indicating that the connection is now initiating an outgoing
     * connection
     */
    public static final int        STATE_CONNECTING       = 2;
    /**
     * The constant indicating that there is now a connection to a remote device
     */
    public static final int        STATE_CONNECTED        = 3;

    // Constant that indicates a closed connection command
    /** A hash value that signals that the connection has been closed. */
    public static final String     CONNECTION_CLOSED_HASH = "318ec526e76502a583acd94f49817cf2";                     //$NON-NLS-1$

    // Member fields
    /** The bluetooth adapter instance */
    private final BluetoothAdapter mAdapter;
    /** The handler that sends messages back to the activity */
    private final Handler          mHandler;
    /**
     * The connectThread running while attempting to make an outgoing connection
     */
    private ConnectThread          mConnectThread;
    /**
     * The connectedThread running during a connection with a remote device
     */
    private ConnectedThread        mConnectedThread;

    /** The state indicating the current connection state */
    private int                    mState;

    /* ***********Constructors*********** */
    /**
     * Constructor. Prepares a new Bluetooth session.
     * 
     * @param context
     *            The UI Activity Context
     * @param handler
     *            A Handler to send messages back to the UI Activity
     */
    public BluetoothCommandService(Context context, Handler handler) {

        if (Debugging.D) {
            Log.d(TAG,
                    "Executing BluetoothCommandService(context=" + context + ",handler=" + //$NON-NLS-1$ //$NON-NLS-2$
                            handler + ")"); //$NON-NLS-1$
        }
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mState = STATE_NONE;
        // mConnectionLostCount = 0;
        this.mHandler = handler;
    }

    /* ********Getters & Setters******** */
    /**
     * Set the current state of the chat connection
     * 
     * @param state
     *            An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (Debugging.D) {
            Log.d(TAG, "setState() " + this.mState + " -> " + state); //$NON-NLS-1$ //$NON-NLS-2$
        }
        this.mState = state;

        // Give the new state to the Handler so the UI Activity can update
        this.mHandler.obtainMessage(TabBarActivity.MESSAGE_STATE_CHANGE, state,
                -1).sendToTarget();
    }

    /**
     * Return the current connection state.
     * 
     * @return the mState
     */
    public synchronized int getState() {
        return this.mState;
    }

    /**
     * Return the connectedThread.
     * 
     * @return the mConnectedThread
     */
    public ConnectedThread getmConnectedThread() {
        return this.mConnectedThread;
    }

    /* **********Object methods********** */
    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        if (Debugging.D) {
            Log.d(TAG, "Entering start()"); //$NON-NLS-1$
        }

        // Cancel any thread attempting to make a connection
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        setState(STATE_LISTEN);

        if (Debugging.D) {
            Log.d(TAG, "Leaving start()"); //$NON-NLS-1$
        }
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * 
     * @param device
     *            The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        if (Debugging.D) {
            Log.d(TAG, "Entering connect(device=" + device + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Cancel any thread attempting to make a connection
        if (this.mState == STATE_CONNECTING) {
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        this.mConnectThread = new ConnectThread(device);
        this.mConnectThread.start();
        setState(STATE_CONNECTING);

        if (Debugging.D) {
            Log.d(TAG, "Leaving connect()"); //$NON-NLS-1$ 
        }
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * 
     * @param socket
     *            The BluetoothSocket on which the connection was made
     * @param device
     *            The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket,
            BluetoothDevice device) {
        if (Debugging.D) {
            Log.d(TAG, "Entering connected(socket=" + socket //$NON-NLS-1$
                    + ", device=" + device + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Cancel the thread that completed the connection
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        this.mConnectedThread = new ConnectedThread(socket);
        this.mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = this.mHandler
                .obtainMessage(TabBarActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(TabBarActivity.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        this.mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);

        if (Debugging.D) {
            Log.d(TAG, "Leaving connected()"); //$NON-NLS-1$ 
        }
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (Debugging.D) {
            Log.d(TAG, "Entering stop()"); //$NON-NLS-1$
        }

        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        setState(STATE_NONE);

        if (Debugging.D) {
            Log.d(TAG, "Leaving stop()"); //$NON-NLS-1$
        }
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * 
     * @param out
     *            The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {

        if (Debugging.D) {
            Log.d(TAG, "Entering write()"); //$NON-NLS-1$
        }
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (this.mState != STATE_CONNECTED)
                return;
            r = this.mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);

        if (Debugging.D) {
            Log.d(TAG, "Leaving write()"); //$NON-NLS-1$
        }
    }

    /**
     * SendMessage to the ConnectedThread in an unsynchronized manner
     * 
     * @param msg
     *            The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void writeMessage(String msg) {

        if (Debugging.D) {
            Log.d(TAG, "Entering writeMessage(msg=" + msg + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (this.mState != STATE_CONNECTED)
                return;
            r = this.mConnectedThread;
        }
        // Perform the write unsynchronized
        r.writeMessageBuffered(msg);

        if (Debugging.D) {
            Log.d(TAG, "Leaving writeMessage()"); //$NON-NLS-1$ 
        }
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {

        if (Debugging.D) {
            Log.d(TAG, "Entering connectionFailed()"); //$NON-NLS-1$
        }
        setState(STATE_LISTEN);

        // Send a failure message back to the Activity
        Message msg = this.mHandler
                .obtainMessage(TabBarActivity.MESSAGE_TOAST_LONG);
        Bundle bundle = new Bundle();
        bundle.putString(
                TabBarActivity.TOAST,
                "Kann nicht mit Ger\u00E4t verbinden.\n\nStellen Sie sicher, dass das Ger\u00E4t eine Verbindung erm\u00F6glicht und versuchen Sie ggf. erneut eine Verbindung herzustellen.");
        msg.setData(bundle);
        this.mHandler.sendMessage(msg);

        if (Debugging.D) {
            Log.d(TAG, "Leaving connectionFailed()"); //$NON-NLS-1$
        }
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {

        if (Debugging.D) {
            Log.d(TAG, "Entering connectionLost()"); //$NON-NLS-1$
        }

        setState(STATE_LISTEN);
        // Send a failure message back to the Activity
        Message msg = this.mHandler
                .obtainMessage(TabBarActivity.MESSAGE_TOAST_SHORT);
        Bundle bundle = new Bundle();
        bundle.putString(TabBarActivity.TOAST, "Device connection was lost"); //$NON-NLS-1$
        msg.setData(bundle);
        this.mHandler.sendMessage(msg);

        // }

        if (Debugging.D) {
            Log.d(TAG, "Leaving connectionLost()"); //$NON-NLS-1$
        }
    }

    /* **********Inner classes********** */
    /**
     * This thread runs while attempting to make an outgoing connection with a
     * device. It runs straight through; the connection either succeeds or
     * fails.
     */
    private class ConnectThread extends Thread {

        /** The bluetooth socket instance */
        private final BluetoothSocket mmSocket;
        /** The bluetooth device instance */
        private final BluetoothDevice mmDevice;

        /* ***********Constructors*********** */
        /**
         * Instantiates a new ConnectThread.
         * 
         * @param device
         *            the BluetoothDevice
         */
        public ConnectThread(BluetoothDevice device) {

            if (Debugging.D) {
                Log.d(TAG, "Executing ConnectThread(device=" + //$NON-NLS-1$
                        device + ")"); //$NON-NLS-1$
            }
            this.mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e); //$NON-NLS-1$
            }
            this.mmSocket = tmp;
        }

        /* **********Object methods********** */
        /**
         * Cancel by closing the BluetoothSocket
         */
        public void cancel() {

            if (Debugging.D) {
                Log.d(TAG, "Entering cancel()"); //$NON-NLS-1$
            }

            try {
                BluetoothCommandService.this
                        .writeMessage(CONNECTION_CLOSED_HASH);
                this.mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e); //$NON-NLS-1$
            }

            if (Debugging.D) {
                Log.d(TAG, "Leaving cancel()"); //$NON-NLS-1$
            }
        }

        /* ********Overridden methods******** */
        /**
         * Run method of Thread class ConnectThread.
         * 
         */
        @Override
        public void run() {
            Log.i(TAG, "BEGIN mConnectThread"); //$NON-NLS-1$
            setName("ConnectThread"); //$NON-NLS-1$

            // Always cancel discovery because it will slow down a connection
            BluetoothCommandService.this.mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                this.mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                // Close the socket
                try {
                    BluetoothCommandService.this
                            .writeMessage(CONNECTION_CLOSED_HASH);
                    this.mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG,
                            "unable to close() socket during connection failure", //$NON-NLS-1$
                            e2);
                }
                // Start the service over to restart listening mode
                BluetoothCommandService.this.start();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothCommandService.this) {
                BluetoothCommandService.this.mConnectThread = null;
            }

            // Start the connected thread
            connected(this.mmSocket, this.mmDevice);
        }

    }

    /**
     * This thread runs during a connection with a remote device. It handles all
     * incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {

        /** The bluetooth socket instance */
        private final BluetoothSocket mmSocket;
        /** The input stream of the connection */
        private final InputStream     mmInStream;
        /** The output stream of the connection */
        private final OutputStream    mmOutStream;

        /* ***********Constructors*********** */
        /**
         * Instantiates a new ConnectedThread.
         * 
         * @param socket
         *            the BluetoothSocket
         * 
         */
        public ConnectedThread(BluetoothSocket socket) {
            if (Debugging.D) {
                Log.d(TAG, "create ConnectedThread"); //$NON-NLS-1$
            }
            this.mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e); //$NON-NLS-1$
            }

            this.mmInStream = tmpIn;
            this.mmOutStream = tmpOut;
        }

        /* **********Object methods********** */
        /**
         * Write to the connected OutStream.
         * 
         * @param buffer
         *            The bytes to write
         */
        public void write(byte[] buffer) {

            if (Debugging.D) {
                Log.d(TAG, "Entering write()"); //$NON-NLS-1$
            }

            try {
                this.mmOutStream.write(buffer);

            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e); //$NON-NLS-1$
            }

            if (Debugging.D) {
                Log.d(TAG, "Leaving write()"); //$NON-NLS-1$
            }
        }

        /**
         * Write a String to the connected OutputStream.
         * 
         * @param msg
         *            the text string to write
         */
        public void writeMessageBuffered(String msg) {

            if (Debugging.D) {
                Log.d(TAG, "Entering writeMessageWriter(msg=" + msg + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            try {

                StringBuffer buffer = new StringBuffer(msg);
                buffer.append(0);

                Writer out = new OutputStreamWriter(this.mmOutStream, "UTF-8"); //$NON-NLS-1$

                // out.write(buffer.toString());
                out.write(msg);
                out.flush();

            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }

            if (Debugging.D) {
                Log.d(TAG, "Leaving writeMessage()"); //$NON-NLS-1$
            }
        }

        /**
         * Cancel by sending the message to the OutputStream and closing the
         * BluetoothSocket.
         */
        public void cancel() {

            if (Debugging.D) {
                Log.d(TAG, "Entering cancel()"); //$NON-NLS-1$
            }

            try {
                this.writeMessageBuffered(CONNECTION_CLOSED_HASH);
                this.mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e); //$NON-NLS-1$
            }

            if (Debugging.D) {
                Log.d(TAG, "Leaving cancel()"); //$NON-NLS-1$
            }
        }

        /* ********Overridden methods******** */
        /**
         * Run method of Thread class ConnectThread.
         * 
         */
        @Override
        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread"); //$NON-NLS-1$
            byte[] buffer = new byte[1024];

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    int bytes = this.mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    BluetoothCommandService.this.mHandler.obtainMessage(
                            TabBarActivity.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e); //$NON-NLS-1$
                    connectionLost();
                    cancel();
                    break;
                }
            }

        }

    }
}
