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
 * ObserverNotificationObject class serving as a data wrapper for Observer
 * notifications. Contains a notification status (Enum) and the notification
 * content object.
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class ObserverNotificationObject {

    private static Logger log = Logger.getLogger(ObserverNotificationObject.class);

    /** The enum for qualifying the observer notification object. */
    private Object        enumStatus;

    /** The content of the observer notification object. */
    private Object        content;

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new ObserverNotfication with the given
     * enumStatus and notification content.
     * 
     * @param enumStatus
     *            the status enumeration that needs to be communicated to the
     *            Observer
     * @param content
     *            the content object that needs to be communicated to the
     *            Observer
     */
    public ObserverNotificationObject(Object enumStatus, Object content) {

        log.debug("Executing ObserverNotificationObject(enumStatus=" + enumStatus + //$NON-NLS-1$ 
                ", content= " + content); //$NON-NLS-1$

        if (enumStatus != null && enumStatus instanceof Enum && content != null) {
            setContent(content);
            setEnumStatus(enumStatus);
        } else {
            log.error("Wrong parameters for ObserverNotificationObject!"); //$NON-NLS-1$
        }
    }

    /* ********Getters & Setters******** */
    /**
     * Returns the notification status.
     * 
     * @return the enumStatus
     */
    public Object getEnumStatus() {
        return this.enumStatus;
    }

    /**
     * Sets the notification status.
     * 
     * @param enumStatus
     *            the enumStatus to set
     */
    private void setEnumStatus(Object enumStatus) {
        this.enumStatus = enumStatus;
    }

    /**
     * Returns the notification object.
     * 
     * @return the content
     */
    public Object getContent() {
        return this.content;
    }

    /**
     * Sets the notification object.
     * 
     * @param content
     *            the content to set
     */
    private void setContent(Object content) {
        this.content = content;
    }

}
