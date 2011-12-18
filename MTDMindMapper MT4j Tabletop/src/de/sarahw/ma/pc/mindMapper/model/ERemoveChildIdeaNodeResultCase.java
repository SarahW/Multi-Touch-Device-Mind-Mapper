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
 * Enumeration representing the possible return cases of the method
 * removeIdeaChild(IdeaNode, MindMap) in the class IdeaNode.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * @see IdeaNode#removeIdeaChild(IdeaNode, MindMap)
 * 
 */
public enum ERemoveChildIdeaNodeResultCase {

    /** Idea node child not removed because of an error. */
    NO_REMOVE_ON_ERROR, //

    /** Idea node child not removed because of a constraint. */
    NO_REMOVE_ON_CONSTRAINT, //

    /**
     * Parent idea node is a single node and the removed idea node child is a
     * single idea node, therefore the map they formed has been removed.
     */
    C1_1_P_SINGLE_C_SINGLE_REMOVE_MAP, //

    /**
     * Parent idea node is part of a map and the removed idea node child is a
     * single idea node, therefore no map action have been necessary.
     */
    C1_2_P_MAP_C_SINGLE_NOTHING, //

    /**
     * Parent idea node is a single idea node and the removed idea node child is
     * part of a map, therefore the latter map has been modified.
     */
    C2_1_P_SINGLE_C_MAP_MODIFY_MAP, //

    /**
     * Parent idea node is part of a map and the removed idea node child is part
     * of a map, therefore a new map formed by the removed idea node child has
     * been created.
     */
    C2_2_P_MAP_C_MAP_NEW_MAP, //

}
