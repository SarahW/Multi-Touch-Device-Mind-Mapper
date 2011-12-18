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

import org.apache.log4j.Logger;
import org.mt4j.util.math.Vector3D;

/**
 * Collects commonly used methods for vector stuff.
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class ToolsVectors {

    protected static Logger log = Logger.getLogger(ToolsVectors.class);

    /* ***********Constructors*********** */
    /**
     * Private constructor. Doesn't allow instances of this class.
     */
    private ToolsVectors() {
        //
    }

    /**
     * Sorts the given Vector array for by distance to the given position.
     * 
     * @param vertices
     *            the list of vertices that is to be sorted
     * @param position
     *            the position
     * @return a new copy of the given list sorted by the distance to the given
     *         position
     */
    public static Vector3D[] selectionSortVerticesDistance(Vector3D[] vertices,
            Vector3D position) {

        // Copy to new array
        Vector3D[] newVertices = new Vector3D[vertices.length];
        for (int k = 0; k < vertices.length; k++) {
            // Copy to new array
            newVertices[k] = vertices[k].getCopy();
        }

        // Sort new array
        for (int i = 0; i < newVertices.length - 1; i++) {
            for (int j = i + 1; j < newVertices.length; j++) {
                if (newVertices[i].distance2D(position) > newVertices[j]
                        .distance2D(position)) {
                    // Swap elements
                    Vector3D temp = newVertices[i].getCopy();
                    newVertices[i] = newVertices[j];
                    newVertices[j] = temp;
                }
            }
        }
        return newVertices;
    }

    /**
     * Sorts the given RelationView array by the distance to the given position.
     * 
     * 
     * @param relViewArray
     *            the list of RelationViews that is to be sorted
     * @param position
     *            the position
     * @return a new copy of the given list sorted by the distance to the given
     *         position
     */
    public static RelationView[] selectionSortRelationViewDistance(
            RelationView[] relViewArray, Vector3D position) {

        RelationView[] newRelViewArray = new RelationView[relViewArray.length];
        for (int k = 0; k < relViewArray.length; k++) {
            // Copy to new array
            newRelViewArray[k] = relViewArray[k];
        }

        // Sort new array
        for (int i = 0; i < newRelViewArray.length - 1; i++) {
            for (int j = i + 1; j < newRelViewArray.length; j++) {
                if (newRelViewArray[i].getCenterPointGlobal().distance2D(
                        position) > newRelViewArray[j].getCenterPointGlobal()
                        .distance2D(position)) {
                    // Swap elements
                    RelationView temp = newRelViewArray[i];
                    newRelViewArray[i] = newRelViewArray[j];
                    newRelViewArray[j] = temp;
                }
            }
        }
        return newRelViewArray;

    }

    /**
     * Sorts the given IdeaNodeView array by the distance to the given position.
     * 
     * 
     * @param ideaNodeViewArray
     *            the list of IdeaNodeViews that is to be sorted
     * @param position
     *            the position
     * @return a new copy of the given list sorted by the distance to the given
     *         position
     */
    public static IdeaNodeView[] selectionSortIdeaNodeViewDistance(
            IdeaNodeView[] ideaNodeViewArray, Vector3D position) {

        IdeaNodeView[] newIdeaNodeViewArray = new IdeaNodeView[ideaNodeViewArray.length];
        for (int k = 0; k < ideaNodeViewArray.length; k++) {
            // Copy to new array
            newIdeaNodeViewArray[k] = ideaNodeViewArray[k];
        }

        // Sort new array
        for (int i = 0; i < newIdeaNodeViewArray.length - 1; i++) {
            for (int j = i + 1; j < newIdeaNodeViewArray.length; j++) {
                if (newIdeaNodeViewArray[i].getCenterPointGlobal().distance2D(
                        position) > newIdeaNodeViewArray[j]
                        .getCenterPointGlobal().distance2D(position)) {
                    // Swap elements
                    IdeaNodeView temp = newIdeaNodeViewArray[i];
                    newIdeaNodeViewArray[i] = newIdeaNodeViewArray[j];
                    newIdeaNodeViewArray[j] = temp;
                }
            }
        }
        return newIdeaNodeViewArray;

    }

}
