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

package de.sarahw.ma.pc.mindMapper;

import org.apache.log4j.Logger;

/**
 * <p>
 * Collects commonly used time stamp operations.
 * </p>
 * 
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */
public class ToolsTimestamp {

    private static Logger log = Logger.getLogger(ToolsTimestamp.class);

    /* ***********Constructors*********** */
    /**
     * Private constructor. Doesn't allow instances of this class.
     */
    private ToolsTimestamp() {
        //
    }

    /* ***********Class methods*********** */
    /**
     * Fetches the current system time stamp.
     * 
     * @return current system time stamp as String
     */
    public static String fetchTimestamp() {

        log.debug("Entering fetchTimestamp()"); //$NON-NLS-1$

        String timestamp = new java.sql.Timestamp(
                new java.util.Date().getTime()).toString();

        log.debug("Leaving fetchTimestamp(): " + timestamp); //$NON-NLS-1$
        return timestamp;
    }

    /**
     * Modifies a time stamp to be file name compliant.
     * 
     * @return modified time stamp without spaces or colons as String
     */
    public static String clearTimestamp(String timestamp) {

        log.debug("Entering clearTimestamp(timestamp=" + timestamp + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        String modifiedTimestamp = timestamp;

        // remove all non standard characters
        modifiedTimestamp = modifiedTimestamp.replace(":", ""); //$NON-NLS-1$//$NON-NLS-2$
        modifiedTimestamp = modifiedTimestamp.replace(" ", "_"); //$NON-NLS-1$//$NON-NLS-2$
        modifiedTimestamp = modifiedTimestamp.replace(".", "_"); //$NON-NLS-1$//$NON-NLS-2$

        log.debug("Leaving clearTimestamp(): " + modifiedTimestamp); //$NON-NLS-1$
        return modifiedTimestamp;
    }

}
