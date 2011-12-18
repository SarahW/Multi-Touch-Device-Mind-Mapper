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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import org.apache.log4j.Logger;

import com.intel.bluetooth.RemoteDeviceHelper;

import de.sarahw.ma.pc.mindMapper.ObserverNotificationObject;
import de.sarahw.ma.pc.mindMapper.model.AppModel;

/**
 * <p>
 * Sets up a new thread to wait for connection from a client and handle the
 * signal.
 * </p>
 * 
 * <p>
 * Adapted from https://github.com/luugiathuy/Remote-Bluetooth-Android
 * </p>
 * 
 * <p>
 * Modified 2011-08-15 <br>
 * - added project specific details/logging <br>
 * - added connectionList/connectionStringList and associated methods <br>
 * - added Observable extension and methods <br>
 * </p>
 * 
 * @author Luu Gia Thuy
 * @author (Modified by) Sarah Will
 * 
 * @version 1.0
 * 
 */
public class WaitBtThread extends Observable implements Runnable {

    private static Logger            log                  = Logger.getLogger(WaitBtThread.class);

    /** The application model instance */
    private AppModel                 model;

    /** The local bluetooth device */
    private LocalDevice              localDevice;

    /** The stream connection notifier which handles incoming connections */
    private StreamConnectionNotifier notifier;

    /** The list of active stream connections */
    private List<StreamConnection>   connectionList       = new ArrayList<StreamConnection>();

    /** The list of connected devices */
    private List<String>             connectionStringList = new ArrayList<String>();

    /* ***********Constructors*********** */
    /**
     * Default constructor. Instantiates a new WaitBtThread.
     * 
     * @param model
     *            the application model instance
     */
    public WaitBtThread(AppModel model) {

        log.debug("Executing WaitBtThread(model=" + model + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Set model reference
        this.model = model;
    }

    /* ********Getters & Setters******** */
    /**
     * Sets the connectionList.
     * 
     * @param connectionList
     *            the connectionList to set
     */
    public synchronized void setConnectionList(
            List<StreamConnection> connectionList) {

        log.trace("Entering setConnectionList(connectionList=" + connectionList + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        this.connectionList = connectionList;

        // Update the connection string list
        updateConnectionStringList();

        // Communicate changed string list to observer
        communicateChangesToObserver(new ObserverNotificationObject(
                EBluetoothConnectionListState.CONNECTION_LIST_SET,
                this.connectionStringList));

        log.trace("Leaving setConnectionList()"); //$NON-NLS-1$

    }

    /**
     * Returns the localDevice.
     * 
     * @return the localDevice
     */
    public synchronized LocalDevice getLocalDevice() {
        return this.localDevice;
    }

    /* **********Utility methods********** */
    /**
     * Waiting for connection from devices.
     * 
     */
    private void waitForConnection() {

        log.debug("Entering waitForConnection()"); //$NON-NLS-1$

        // retrieve the local Bluetooth device object
        this.localDevice = null;
        setConnectionList(new ArrayList<StreamConnection>());

        // setup the server to listen for connections / set discoverable
        try {
            this.localDevice = LocalDevice.getLocalDevice();
            this.localDevice.setDiscoverable(DiscoveryAgent.GIAC);

            UUID uuid = new UUID("fae3fa60e2ce11e095720800200c9a66", false); // fae3fa60-e2ce-11e0-9572-0800200c9a66 //$NON-NLS-1$

            String url = "btspp://localhost:" + uuid.toString() //$NON-NLS-1$
                    + ";name=RemoteBluetooth" + ";authenticate=true;encrypt=true;master=true"; //$NON-NLS-1$//$NON-NLS-2$

            // Open bt connection
            this.notifier = (StreamConnectionNotifier) Connector.open(url);

        } catch (Exception e) {

            e.printStackTrace();
            log.warn("Leaving waitForConnection()#setDiscoverable; Exception encountered: \n" + e.toString()); //$NON-NLS-1$

            return;
        }

        // waiting for connection
        while (true) {
            try {
                log.debug("Waiting for BT connection..."); //$NON-NLS-1$

                StreamConnection connection = this.notifier.acceptAndOpen();

                if (connection != null) {

                    // Add the new connection to the list
                    // required for the bluetooth overlay view
                    addConnectionToList(connection);

                }

                // Start a new process thread for the new connection
                Thread processThread = new Thread(new ProcessConnectionThread(
                        connection, this.model, this));

                processThread.start();

            } catch (Exception e) {
                e.printStackTrace();
                log.warn("Leaving waitForConnection()#waiting; Exception encountered: \n" + e.toString()); //$NON-NLS-1$

                return;
            }

        }

    }

    /**
     * Adds a new connection to the connection list and updates the
     * connectionStringList accordingly.
     * 
     * @param connection
     *            the new connection to be added
     */
    public synchronized void addConnectionToList(StreamConnection connection) {

        log.debug("Entering addConnection(connection=" + connection //$NON-NLS-1$
                + ")"); //$NON-NLS-1$

        // Add to list if it hasn't been added yet
        if (!this.connectionList.contains(connection)) {

            this.connectionList.add(connection);

            // Update the connection string list
            updateConnectionStringList();

            // Communicate connection string list changes to observer
            communicateChangesToObserver(new ObserverNotificationObject(
                    EBluetoothConnectionListState.CONNECTION_ADDED,
                    this.connectionStringList));
        } else {
            log.debug("The connectionList already contains this connection!"); //$NON-NLS-1$
        }

        log.debug("Leaving addConnection()"); //$NON-NLS-1$

    }

    /**
     * Removes a connection from the connection list and updates the
     * connectionStringList accordingly.
     * 
     * @param connection
     *            the connection to be removed
     */
    public synchronized void removeConnectionFromList(
            StreamConnection connection) {

        log.debug("Entering removeConnection(connection=" + connection //$NON-NLS-1$
                + ")"); //$NON-NLS-1$

        // Remove from list if contained
        if (this.connectionList.contains(connection)) {

            this.connectionList.remove(connection);

            // Update the connection string list
            updateConnectionStringList();

            // Communicate connection string list changes to observer
            communicateChangesToObserver(new ObserverNotificationObject(
                    EBluetoothConnectionListState.CONNECTION_REMOVED,
                    this.connectionStringList));
        } else {
            log.debug("The connectionList does not contain this connection!"); //$NON-NLS-1$
        }

        log.debug("Leaving removeConnection()"); //$NON-NLS-1$
    }

    /**
     * Updates the connectionStringList by getting all current connection remote
     * devices friendlyNames and bluetooth addresses (anew).
     * 
     */
    private synchronized void updateConnectionStringList() {

        log.debug("Entering updateConnectionStringList()"); //$NON-NLS-1$

        RemoteDevice remoteDevice = null;

        if (this.connectionList != null) {

            // Clear list
            this.connectionStringList = new ArrayList<String>();

            // Get remote device for every connection
            for (StreamConnection connection : this.connectionList) {

                try {
                    remoteDevice = RemoteDeviceHelper
                            .getRemoteDevice(connection);
                } catch (IOException e) {

                    e.printStackTrace();
                    log.error("Exception while getting remote device: " + e.getMessage()); //$NON-NLS-1$

                }

                if (remoteDevice != null) {

                    // Get friendly name and bluetooth address
                    String remoteDeviceFriendlyName = ""; //$NON-NLS-1$
                    try {
                        remoteDeviceFriendlyName = remoteDevice
                                .getFriendlyName(false);
                    } catch (IOException e) {
                        e.printStackTrace();

                        log.warn("Exception while getting friendly name: " + e.getMessage()); //$NON-NLS-1$

                    }

                    // Add to connection string list
                    this.connectionStringList
                            .add(remoteDeviceFriendlyName.isEmpty() ? Messages
                                    .getString("WaitBtThread.updateConnectionStringList.unknownDevice.0") //$NON-NLS-1$
                                    : remoteDeviceFriendlyName
                                            + "\n" + remoteDevice.getBluetoothAddress()); //$NON-NLS-1$

                }

            }
        } else {
            log.error("Leaving updateConnectionStringList(), connectionList invalid! (null) "); //$NON-NLS-1$
            return;
        }

        log.debug("Leaving updateConnectionStringList()"); //$NON-NLS-1$

    }

    /**
     * Communicates changes to all registered Observers.
     * 
     * @param object
     *            the observer notification object
     * 
     */
    protected void communicateChangesToObserver(
            ObserverNotificationObject object) {
        log.debug("Entering communicateChangesToObserver()"); //$NON-NLS-1$

        setChanged();
        notifyObservers(object);

        log.debug("Leaving communicateChangesToObserver()"); //$NON-NLS-1$
    }

    /* ********Overridden methods******** */
    /**
     * Creates thread to wait for connection from client(s).
     * 
     */
    @Override
    public void run() {
        log.debug("Entering run()"); //$NON-NLS-1$

        waitForConnection();

        log.debug("Leaving run()"); //$NON-NLS-1$
    }

}
