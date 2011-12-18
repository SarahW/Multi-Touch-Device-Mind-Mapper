/***********************************************************************
 * Copyright (c) 2011 Sarah Will
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

/**
 * <p>
 * Enumeration representing the bluetooth connection list change actions that
 * are to be communicated to an observer.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public enum EBluetoothConnectionListState {

    /** The connection list has been set anew */
    CONNECTION_LIST_SET, //

    /** A new connection has been added to the bluetooth stack */
    CONNECTION_ADDED, //

    /** A connection has been removed from the bluetooth stack */
    CONNECTION_REMOVED, //

}
