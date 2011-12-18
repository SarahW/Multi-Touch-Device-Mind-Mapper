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

/**
 * <p>
 * Enumeration representing the possible observer notification cases for the
 * class IdeaNode.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 * @see IdeaNode
 * 
 */
public enum EIdeaNodeChangedStatus {

    /** A new child idea node has been added to the IdeaNode */
    IDEA_NODE_CHILD_ADDED, //

    /**
     * A child idea node has been removed from the children list of the IdeaNode
     */
    IDEA_NODE_CHILD_REMOVED, //

    /** The children list of the IdeaNode has been set anew */
    // TODO: currently not used or handled!
    IDEA_NODE_CHILD_LIST_SET, //
}
