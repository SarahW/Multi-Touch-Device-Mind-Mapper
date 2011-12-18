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
 * Enumeration representing the different areas of the canvas which all result
 * in different rotation angles for newly created IdeaNodeViews.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public enum ECanvasArea {

    /** The polygon comprising the northern canvas area */
    CANVAS_NORTH, //
    /** The polygon comprising the eastern canvas area */
    CANVAS_EAST, //
    /** The polygon comprising the southern canvas area */
    CANVAS_SOUTH, //
    /** The polygon comprising the western canvas area */
    CANVAS_WEST, //
    /** An area on the screen that is not part of the polygons */
    CANVAS_NONE_ERROR, //

}
