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
 * class MindMap.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * @see MindMap
 * 
 */
public enum EMindMapChangeStatus {

    /** The mindMap title has been set */
    MIND_MAP_TITLE_SET, //

    /** The mindMap ideaNode list has been set. */
    MIND_MAP_IDEA_NODE_LIST_SET, //

    /** The mindMap map list has been set. */
    MIND_MAP_MAP_LIST_SET, //

    /** An ideaNode has been added to the mindMap */
    MIND_MAP_IDEA_NODE_ADDED, //

    /** An ideaNode has been removed from the mindMap */
    MIND_MAP_IDEA_NODE_REMOVED, //

    /** A map has been added to the mindMap */
    MIND_MAP_MAP_ADDED, //

    /** A map has been removed from the mindMap */
    MIND_MAP_MAP_REMOVED, //

}
