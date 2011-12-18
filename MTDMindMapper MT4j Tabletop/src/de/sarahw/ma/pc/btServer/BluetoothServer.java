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

import org.apache.log4j.Logger;

import de.sarahw.ma.pc.mindMapper.model.AppModel;

/**
 * <p>
 * BluetoothServer for starting the btServer.
 * </p>
 * 
 * <p>
 * Implements the Singleton-Pattern.
 * </p>
 * 
 * <p>
 * Modified 2011-08 <br>
 * </p>
 * 
 * @author Luu Gia Thuy
 * @author (Modified by) Sarah Will
 * 
 * @version 1.0
 * 
 * 
 */
public class BluetoothServer {

    private static Logger          log = Logger.getLogger(BluetoothServer.class);

    /** The Bluetooth Server instance */
    private static BluetoothServer instance;

    /** The thread that handles connection requests. */
    private WaitBtThread           waitThread;

    /* ***********Constructors*********** */
    /**
     * <p>
     * Default private constructor. Instantiates a new BluetoothServer object.
     * 
     * @param model
     *            the application model instance
     *            </p>
     */
    private BluetoothServer(AppModel model) {

        log.debug("Executing BluetoothServer(model" + model + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Start wait thread
        this.waitThread = new WaitBtThread(model);
        Thread waitBtThread = new Thread(this.waitThread);
        waitBtThread.start();
    }

    /* ********Getters & Setters******** */
    /**
     * <p>
     * Returns an instance of BluetoothServer. >> Singleton-Pattern
     * </p>
     * 
     * @param model
     *            the application model instance
     * @return instance of BluetoothServer
     */
    public synchronized static BluetoothServer getInstance(AppModel model) {

        log.debug("Entering getInstance(model" + model + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (instance == null) {

            instance = new BluetoothServer(model);
        }

        log.debug("Leaving getInstance(): instance"); //$NON-NLS-1$
        return instance;
    }

    /**
     * Returns the bluetooth server wait thread.
     * 
     * @return the waitThread
     */
    public synchronized WaitBtThread getWaitThread() {
        return this.waitThread;
    }

}
