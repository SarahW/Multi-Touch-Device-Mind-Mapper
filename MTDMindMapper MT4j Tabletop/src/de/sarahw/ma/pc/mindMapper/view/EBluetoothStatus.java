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

package de.sarahw.ma.pc.mindMapper.view;

/**
 * <p>
 * Enumeration representing the bluetooth power/availability statuses.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public enum EBluetoothStatus {

    /** Bluetooth is available on this device but switched off */
    BLUETOOTH_AVAILABLE_OFF, //
    /** Bluetooth is available on this device and switched on */
    BLUETOOTH_AVAILABLE_ON, //
    /** Bluetooth is not available on this device */
    BLUETOOTH_NOT_AVAILABLE, //

}
