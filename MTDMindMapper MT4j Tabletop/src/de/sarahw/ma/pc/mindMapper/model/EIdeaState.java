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

package de.sarahw.ma.pc.mindMapper.model;

import java.io.Serializable;

/**
 * <p>
 * Enumeration represents the states an IdeaNodeView can have
 * </p>
 * 
 * <p>
 * Part of the serialization process as a member of the class NodeMetaData.
 * </p>
 * 
 * 
 * @author Sarah Will
 * @version 1.0
 * @see de.sarahw.ma.pc.mindMapper.model.NodeMetaData
 * 
 */

public enum EIdeaState implements Serializable {

    /** The IdeaNode is active (default state) */
    ACTIVE, //

    /** The IdeaNode has been deleted from the model mindMap */
    DELETED, //

}
