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
 * addIdeaChild(IdeaNode, MindMap) in the class IdeaNode.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * @see IdeaNode#addIdeaChild(IdeaNode, MindMap)
 * 
 */
public enum EAddChildIdeaNodeResultCase {

    /** Idea node not added as a child because of an error */
    NO_ADD_ON_ERROR, //

    /** Idea node not added as a child because of a constraint */
    NO_ADD_ON_CONSTRAINT, //

    /**
     * Parent idea node is a single node and new idea node child is root node of
     * a map, therefore the old map has been modified
     */
    C1_1_P_SINGLE_C_MAP_MODIFY, //

    /**
     * Parent idea node is a single node and new idea node child a single idea
     * node, therefore a new map has been created
     */
    C1_2_P_SINGLE_C_SINGLE_CREATE_MAP, //

    /**
     * Parent idea node is part of a map and new idea node child is the parent
     * node in a map, therefore that latter map has been deleted
     */
    C2_1_P_MAP_C_MAPPARENT_DELETE_MAP, //

    /**
     * Parent idea node is part of a map and new idea node child is a single
     * idea node, therefore no map actions have been necessary
     */
    C2_2_P_MAP_C_SINGLE_NOTHING, //

}
