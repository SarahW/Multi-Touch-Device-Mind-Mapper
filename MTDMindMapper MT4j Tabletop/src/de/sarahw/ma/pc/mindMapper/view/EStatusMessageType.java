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
 * Enumeration representing the different types of GUI status messages (for
 * displaying corresponding icons and buttons etc.)
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public enum EStatusMessageType {

    /** Status message dialog signaling an error */
    STATUS_MSG_ERROR, //
    /** Status message dialog signaling a warning */
    STATUS_MSG_WARNING, //
    /** Status message dialog delivering information */
    STATUS_MSG_INFO, //
    /** Status message dialog asking a question */
    STATUS_MSG_QUESTION, //

}
