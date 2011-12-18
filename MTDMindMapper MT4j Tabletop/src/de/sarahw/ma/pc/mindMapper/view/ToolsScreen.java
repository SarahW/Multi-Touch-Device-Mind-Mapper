/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009, C.Ruff, Fraunhofer-Gesellschaft All rights
 * reserved.
 * 
 * Modifications Copyright (c) 2011 Sarah Will
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

import org.mt4j.util.math.Vector3D;

/**
 * Class containing commonly used utility methods regarding screen stuff.
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class ToolsScreen {

    // private static Logger log = Logger.getLogger(ToolsScreen.class);

    /* ***********Constructors*********** */
    /**
     * Private constructor. Doesn't allow instances of this class.
     */
    private ToolsScreen() {
        //
    }

    /* ***********Class methods*********** */

    /**
     * Checks all min and max x and y coordinates and returns a new position
     * vector if necessary
     * 
     * @param currentPosition
     *            the shape to be repositioned
     */
    public static Vector3D alignPositionToBorders(Vector3D currentPosition,
            float minX, float maxX, float minY, float maxY) {

        // Case 1: center point x and y are less than the min
        // (dragged beyond upper left corner)

        if (currentPosition.getX() < minX && currentPosition.getY() < minY) {

            return new Vector3D(minX, minY, currentPosition.getZ());

            // Case 2: center point x is less than min, y more than max
            // (dragged beyond lower left corner)
        } else if (currentPosition.getX() < minX
                && currentPosition.getY() > maxY) {

            return new Vector3D(minX, maxY, currentPosition.getZ());

            // Case 3: center point x is less than min, y ok
            // (dragged beyond left border)
        } else if (currentPosition.getX() < minX
                && currentPosition.getY() >= minY
                && currentPosition.getY() <= maxY) {
            // Left side
            return new Vector3D(minX, currentPosition.getY(),
                    currentPosition.getZ());

            // Case 4: center point x is more than max, y less than min
            // (Dragged beyond upper right corner)
        } else if (currentPosition.getX() > maxX
                && currentPosition.getY() < minY) {

            return new Vector3D(maxX, minY, currentPosition.getZ());

            // Case 5: center point x and y are more than max
            // (Dragged beyond lower right corner)
        } else if (currentPosition.getX() > maxX
                && currentPosition.getY() > maxY) {

            return new Vector3D(maxX, maxY, currentPosition.getZ());

            // Case 6: center point x is more than max, y is ok
            // (Dragged beyond right border)
        } else if (currentPosition.getX() > maxX
                && currentPosition.getY() >= minY
                && currentPosition.getY() <= maxY) {
            // Right side
            return new Vector3D(maxX, currentPosition.getY(),
                    currentPosition.getZ());

            // Case 7: center point x ok, y is less than min
            // (Dragged beyond upper border)
        } else if (currentPosition.getX() >= minX
                && currentPosition.getX() <= maxX
                && currentPosition.getY() < minY) {

            return new Vector3D(currentPosition.getX(), minY,
                    currentPosition.getZ());

            // Case 8: center point x ok, y is more than max
            // (Dragged beyond lower border)
        } else if (currentPosition.getX() >= minX
                && currentPosition.getX() <= maxX
                && currentPosition.getY() > maxY) {

            return new Vector3D(currentPosition.getX(), maxY,
                    currentPosition.getZ());

        } else {
            return null;
        }

    }

}
