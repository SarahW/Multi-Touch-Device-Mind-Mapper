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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTSvg;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4jx.components.visibleComponents.widgets.MTSuggestionTextArea;

import processing.core.PApplet;

/**
 * Class containing commonly used utility methods operating on components.
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class ToolsComponent {

    private static Logger log = Logger.getLogger(ToolsComponent.class);

    /* ***********Constructors*********** */
    /**
     * Private constructor. Doesn't allow instances of this class.
     */
    private ToolsComponent() {
        //
    }

    /* ***********Class methods*********** */
    /**
     * <p>
     * Returns the current rotation of a component on the z plane in degrees.
     * </p>
     * <p>
     * See also <a
     * href="http://nuigroup.com/forums/viewthread/11199/#66549">http
     * ://nuigroup.com/forums/viewthread/11199/#66549</a>
     * </p>
     * 
     * @param component
     * @return the rotation of a component in degrees
     */
    public static float getRotationZInDegrees(MTComponent component) {

        log.debug("Entering getRotationZInDegrees(component=" + component + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Decompose global component matrix
        Vector3D translationStore = new Vector3D();
        Vector3D rotationStore = new Vector3D();
        Vector3D scaleStore = new Vector3D();
        component.getGlobalMatrix().decompose(translationStore, rotationStore,
                scaleStore);

        log.trace("Component rotation store: " + rotationStore);//$NON-NLS-1$

        // Get rotation on the z plane in radians
        float zRotationInRadians = rotationStore.getZ();

        log.trace("Component rotation store Z / in radians: " + zRotationInRadians); //$NON-NLS-1$

        // Convert from radians to degrees
        float zRotationInDegrees = zRotationInRadians * (180 / (float) Math.PI);

        log.trace("Component rotation store Z / in degrees: " + zRotationInDegrees);//$NON-NLS-1$

        log.debug("Leaving getRotationZInDegrees():" + zRotationInDegrees); //$NON-NLS-1$ 

        return zRotationInDegrees;
    }

    /**
     * <p>
     * Re-aligns the rotation of an AbstractShape to the rotation of another
     * AbstractShape. (i.e. parent-child)
     * </p>
     * 
     * <p>
     * Required for re-aligning the rotation of a RelationView when added as a
     * child of an IdeaNodeView.
     * </p>
     * 
     * @param originShape
     *            the parent AbstractShape
     * @param toBeReRotatedShape
     *            the child AbstractShape
     */
    public static void reRotateShape(AbstractShape originShape,
            AbstractShape toBeReRotatedShape) {

        if (originShape != null && toBeReRotatedShape != null) {

            log.debug("Entering reRotateShape(originShape " + originShape //$NON-NLS-1$
                    + ", toBeReRotatedShape=" + toBeReRotatedShape); //$NON-NLS-1$

            // Get global matrix for originShape
            Vector3D pTranslationStore = new Vector3D();
            Vector3D pRotationStore = new Vector3D();
            Vector3D pScaleStore = new Vector3D();
            originShape.getGlobalMatrix().decompose(pTranslationStore,
                    pRotationStore, pScaleStore);

            log.trace("OriginShape's rotation store is " + pRotationStore); //$NON-NLS-1$

            // Global matrix for toBeReRotatedShape
            Vector3D cTranslationStore = new Vector3D();
            Vector3D cRotationStore = new Vector3D();
            Vector3D cScaleStore = new Vector3D();
            toBeReRotatedShape.getGlobalMatrix().decompose(cTranslationStore,
                    cRotationStore, cScaleStore);

            log.trace("ToBeReRotatedShape's rotation store is " + cRotationStore); //$NON-NLS-1$

            // If both rotations do not match, realign
            if (!cRotationStore.equals(pRotationStore)) {

                // Get toBeReRotatedShape rotation in radians
                float cRotationInRadiansToSet = pRotationStore.getZ();

                // Convert toBeReRotatedShape rotation to degrees
                double cRotationInDegreesToSet = cRotationInRadiansToSet
                        * (180 / Math.PI);

                log.trace("ToBeReRotatedShape's rotation to set is " + cRotationInDegreesToSet + " degrees"); //$NON-NLS-1$ //$NON-NLS-2$

                // Re-rotate the child around its center point
                toBeReRotatedShape.rotateZGlobal(
                        toBeReRotatedShape.getCenterPointGlobal(),
                        -(float) cRotationInDegreesToSet);

                // DEBUG
                toBeReRotatedShape.getGlobalMatrix().decompose(
                        cTranslationStore, cRotationStore, cScaleStore);
                log.trace("Childs rotation store is " + cRotationStore); //$NON-NLS-1$

                cRotationInRadiansToSet = pRotationStore.getZ();

                cRotationInDegreesToSet = cRotationInRadiansToSet
                        * (180 / Math.PI);

                log.trace("Childs rotation is " + cRotationInDegreesToSet + " degrees"); //$NON-NLS-1$ //$NON-NLS-2$

                log.debug("Leaving reRotateShape()"); //$NON-NLS-1$

            }
        } else {
            log.error("Leaving reRotateShape(): invalid null input"); //$NON-NLS-1$
        }

    }

    /**
     * <p>
     * Re-aligns the rotation of a MTSvg image to the rotation an AbstractShape.
     * </p>
     * 
     * <p>
     * Required for re-aligning the rotation of the arrow head in the
     * RelationView
     * </p>
     * 
     * @param originShape
     *            the parent AbstractShape
     * @param toBeReRotatedSvg
     *            the child toBeReRotatedSvg
     */
    public static void reRotateSvg(AbstractShape originShape,
            MTSvg toBeReRotatedSvg) {

        if (originShape != null && toBeReRotatedSvg != null) {

            log.debug("Entering reRotateSvg(originShape " + originShape //$NON-NLS-1$
                    + ", toBeReRotatedSvg=" + toBeReRotatedSvg); //$NON-NLS-1$

            // Get global matrix for originShape
            Vector3D pTranslationStore = new Vector3D();
            Vector3D pRotationStore = new Vector3D();
            Vector3D pScaleStore = new Vector3D();
            originShape.getGlobalMatrix().decompose(pTranslationStore,
                    pRotationStore, pScaleStore);

            log.trace("OriginShape's rotation store is " + pRotationStore); //$NON-NLS-1$

            // Global matrix for toBeReRotatedShape
            Vector3D cTranslationStore = new Vector3D();
            Vector3D cRotationStore = new Vector3D();
            Vector3D cScaleStore = new Vector3D();
            toBeReRotatedSvg.getGlobalMatrix().decompose(cTranslationStore,
                    cRotationStore, cScaleStore);

            log.trace("ToBeReRotatedShape's rotation store is " + cRotationStore); //$NON-NLS-1$

            // If both rotations do not match, realign
            if (!cRotationStore.equals(pRotationStore)) {

                // Get toBeReRotatedShape rotation in radians
                float cRotationInRadiansToSet = pRotationStore.getZ();

                // Convert toBeReRotatedShape rotation to degrees
                double cRotationInDegreesToSet = cRotationInRadiansToSet
                        * (180 / Math.PI);

                log.trace("ToBeReRotatedShape's rotation to set is " + cRotationInDegreesToSet + " degrees"); //$NON-NLS-1$ //$NON-NLS-2$

                // Re-rotate the child around its center point
                toBeReRotatedSvg.rotateZGlobal(
                        toBeReRotatedSvg.getCenterPointGlobal(),
                        -(float) cRotationInDegreesToSet);

                // DEBUG
                toBeReRotatedSvg.getGlobalMatrix().decompose(cTranslationStore,
                        cRotationStore, cScaleStore);
                log.trace("Childs rotation store is " + cRotationStore); //$NON-NLS-1$

                cRotationInRadiansToSet = pRotationStore.getZ();

                cRotationInDegreesToSet = cRotationInRadiansToSet
                        * (180 / Math.PI);

                log.trace("MTSvg's rotation is " + cRotationInDegreesToSet + " degrees"); //$NON-NLS-1$ //$NON-NLS-2$

                log.debug("Leaving reRotateSvg()"); //$NON-NLS-1$

            }
        }
        log.error("Leaving reRotateSvg(): invalid null input"); //$NON-NLS-1$

    }

    /**
     * <p>
     * Gets the touch point for a given IdeaNodeView at the given touchPointLoc.
     * </p>
     * 
     * @param pApplet
     *            the application instance
     * @param ideaNodeView
     *            the ideaNodeView we want to calculate a touch point for
     * @param touchPointLoc
     *            the location of the touch point
     * @return the touch point position vector
     */
    public static Vector3D getIdeaNodeViewBoundsTouchPoint(PApplet pApplet,
            IdeaNodeView ideaNodeView, ETouchPointLocation touchPointLoc) {

        log.debug("Entering getIdeaNodeViewBoundsTouchPoint(pApplet=" + pApplet + ", ideaNodeView=" + ideaNodeView //$NON-NLS-1$ //$NON-NLS-2$
                + ", touchPointLoc = " + touchPointLoc + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (ideaNodeView != null && touchPointLoc != null) {

            // Get the current rotation
            float rotation = ToolsComponent.getRotationZInDegrees(ideaNodeView);

            // We only handle the four default rotations right after
            // initialization
            if (rotation == 90.0f || rotation == 0.0f || rotation == 180.0f
                    || rotation == 270.0f) {

                // Get the current center global
                Vector3D centerCopy = ideaNodeView.getCenterPointGlobal()
                        .getCopy();

                if (ideaNodeView.hasBounds()) {

                    // Get the shape's bounding shape vertices
                    Vector3D[] shapeBoundsVertices = ideaNodeView.getBounds()
                            .getVectorsGlobal();

                    List<Vector3D> intersectionVecList = new ArrayList<Vector3D>();

                    Vector3D touchPoint = null;
                    Vector3D touchPointDirection = null;

                    // Create direction vectors
                    Vector3D directionNorth = new Vector3D(centerCopy.getX(),
                            centerCopy.getY() - 1, centerCopy.getZ());
                    Vector3D directionNorthEast = new Vector3D(
                            centerCopy.getX() + 1, centerCopy.getY() - 1,
                            centerCopy.getZ());
                    Vector3D directionEast = new Vector3D(
                            centerCopy.getX() + 1, centerCopy.getY(),
                            centerCopy.getZ());
                    Vector3D directionSouthEast = new Vector3D(
                            centerCopy.getX() + 1, centerCopy.getY() + 1,
                            centerCopy.getZ());
                    Vector3D directionSouth = new Vector3D(centerCopy.getX(),
                            centerCopy.getY() + 1, centerCopy.getZ());
                    Vector3D directionSouthWest = new Vector3D(
                            centerCopy.getX() - 1, centerCopy.getY() + 1,
                            centerCopy.getZ());
                    Vector3D directionWest = new Vector3D(
                            centerCopy.getX() - 1, centerCopy.getY(),
                            centerCopy.getZ());
                    Vector3D directionNorthWest = new Vector3D(
                            centerCopy.getX() - 1, centerCopy.getY() - 1,
                            centerCopy.getZ());

                    // We set different touchPointDirection vectors for
                    // different touch point locations AND current rotation
                    switch (touchPointLoc) {
                        case NORTH:
                            switch ((int) rotation) {
                                case (0):
                                    touchPointDirection = directionNorth;
                                    break;
                                case (90):
                                    touchPointDirection = directionWest;
                                    break;
                                case (180):
                                    touchPointDirection = directionSouth;
                                    break;
                                case (270):
                                    touchPointDirection = directionEast;
                                    break;
                                default:
                                    // Do nothing, return on null later
                                    break;
                            }

                            break;
                        case NORTH_EAST:
                            switch ((int) rotation) {
                                case (0):

                                    touchPointDirection = directionNorthEast;
                                    break;
                                case (90):
                                    touchPointDirection = directionNorthWest;
                                    break;
                                case (180):
                                    touchPointDirection = directionSouthWest;
                                    break;
                                case (270):
                                    touchPointDirection = directionSouthEast;
                                    break;
                                default:
                                    // Do nothing, return on null later
                                    break;
                            }
                            break;
                        case EAST:
                            switch ((int) rotation) {
                                case (0):

                                    touchPointDirection = directionEast;
                                    break;
                                case (90):
                                    touchPointDirection = directionNorth;
                                    break;
                                case (180):
                                    touchPointDirection = directionWest;
                                    break;
                                case (270):
                                    touchPointDirection = directionSouth;
                                    break;
                                default:
                                    // Do nothing, return on null later
                                    break;
                            }

                            break;
                        case SOUTH_EAST:
                            switch ((int) rotation) {
                                case (0):

                                    touchPointDirection = directionSouthEast;
                                    break;
                                case (90):
                                    touchPointDirection = directionNorthEast;
                                    break;
                                case (180):
                                    touchPointDirection = directionNorthWest;
                                    break;
                                case (270):
                                    touchPointDirection = directionSouthWest;
                                    break;
                                default:
                                    // Do nothing, return on null later
                                    break;
                            }
                            break;
                        case SOUTH:
                            switch ((int) rotation) {
                                case (0):

                                    touchPointDirection = directionSouth;
                                    break;
                                case (90):
                                    touchPointDirection = directionEast;
                                    break;
                                case (180):
                                    touchPointDirection = directionNorth;
                                    break;
                                case (270):
                                    touchPointDirection = directionWest;
                                    break;
                                default:
                                    // Do nothing, return on null later
                                    break;
                            }

                            break;
                        case SOUTH_WEST:
                            switch ((int) rotation) {
                                case (0):

                                    touchPointDirection = directionSouthWest;
                                    break;
                                case (90):
                                    touchPointDirection = directionSouthEast;
                                    break;
                                case (180):
                                    touchPointDirection = directionNorthEast;
                                    break;
                                case (270):
                                    touchPointDirection = directionNorthWest;
                                    break;
                                default:
                                    // Do nothing, return on null later
                                    break;
                            }
                            break;
                        case WEST:
                            switch ((int) rotation) {
                                case (0):

                                    touchPointDirection = directionWest;
                                    break;
                                case (90):
                                    touchPointDirection = directionSouth;
                                    break;
                                case (180):
                                    touchPointDirection = directionEast;
                                    break;
                                case (270):
                                    touchPointDirection = directionNorth;
                                    break;
                                default:
                                    // Do nothing, return on null later
                                    break;
                            }

                            break;
                        case NORTH_WEST:
                            switch ((int) rotation) {
                                case (0):

                                    touchPointDirection = directionNorthWest;
                                    break;
                                case (90):
                                    touchPointDirection = directionSouthWest;
                                    break;
                                case (180):
                                    touchPointDirection = directionSouthEast;
                                    break;
                                case (270):
                                    touchPointDirection = directionNorthEast;
                                    break;
                                default:
                                    // Do nothing, return on null later
                                    break;
                            }
                            break;
                        default:
                            // Do nothing, return on null later
                            break;

                    }

                    if (touchPointDirection != null) {

                        // Create new ray with Shape center in touch
                        // point
                        // direction
                        Ray ray = new Ray(centerCopy, touchPointDirection);
                        log.trace("Ray for" + touchPointLoc + " is: " + ray); //$NON-NLS-1$ //$NON-NLS-2$

                        // Get intersection vertices for ray
                        intersectionVecList = getRayShapeIntersectionPoints(
                                pApplet, ideaNodeView, shapeBoundsVertices, ray);

                        // We should have only one intersection point
                        if (intersectionVecList.size() == 1) {

                            // The touchPointTemp is now at index 0
                            touchPoint = intersectionVecList.get(0);

                            return touchPoint;
                        }

                        log.error("Leaving getBoundsTouchPoint(): null, more or less than one refined touch points for this direction!"); //$NON-NLS-1$
                        return null;

                    }
                    log.error("Leaving getBoundsTouchPoint(): null, invalid touch point position param or rotation!"); //$NON-NLS-1$
                    return null;

                }
                log.error("Leaving getBoundsTouchPoint(): null, shape has no bounds!"); //$NON-NLS-1$
                return null;

            }
            log.error("Leaving getBoundsTouchPoint(): null, invalid input, rotation cannot be handled"); //$NON-NLS-1$
            return null;

        }
        log.error("Leaving getBoundsTouchPoint(): null, invalid null input"); //$NON-NLS-1$
        return null;
    }

    /**
     * <p>
     * Gets the touch point for a given AbstractCircularMenu at the given
     * touchPointLoc.
     * </p>
     * 
     * 
     * @param pApplet
     *            the current application instance
     * @param menu
     *            the menu we want to calculate the touch point for
     * @param touchPointLoc
     *            the location of the touch point
     * @return the position of the touch point
     */
    public static Vector3D getCircularMenuBoundsTouchPoint(PApplet pApplet,
            AbstractCircularMenu menu, ETouchPointLocation touchPointLoc) {

        log.debug("Entering getMTCircleMenuBoundsTouchPoint(pApplet=" + pApplet //$NON-NLS-1$
                + ", menu=" + menu + ", touchPointLoc = " + touchPointLoc + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        if (menu != null && touchPointLoc != null) {

            // Get the current rotation
            float rotation = ToolsComponent.getRotationZInDegrees(menu);

            // We only handle the default rotation 0.0
            if (rotation == 0.0f) {

                // Get the current center global
                Vector3D centerCopy = menu.getCenterPointGlobal().getCopy();

                if (menu.hasBounds()) {

                    // Get the shape's bounding shape vertices
                    Vector3D[] shapeBoundsVertices = menu.getBounds()
                            .getVectorsGlobal();

                    // DEBUG:
                    int i = 0;
                    for (Vector3D vec : shapeBoundsVertices) {
                        i++;
                        log.trace("Vector Nr. " + i + "is " + vec); //$NON-NLS-1$ //$NON-NLS-2$
                    }

                    List<Vector3D> intersectionVecList = new ArrayList<Vector3D>();

                    Vector3D touchPointTemp = null;
                    Vector3D touchPoint = null;
                    Vector3D touchPointDirection = null;

                    // Create direction vectors
                    Vector3D directionNorth = new Vector3D(centerCopy.getX(),
                            centerCopy.getY() - 1, centerCopy.getZ());

                    Vector3D directionEast = new Vector3D(
                            centerCopy.getX() + 1, centerCopy.getY(),
                            centerCopy.getZ());

                    Vector3D directionSouth = new Vector3D(centerCopy.getX(),
                            centerCopy.getY() + 1, centerCopy.getZ());

                    Vector3D directionWest = new Vector3D(
                            centerCopy.getX() - 1, centerCopy.getY(),
                            centerCopy.getZ());

                    switch (touchPointLoc) {
                        case NORTH:
                            touchPointDirection = directionNorth;

                            break;
                        case NORTH_EAST:
                            // We take the east direction and rotate the
                            // touch point later
                            touchPointDirection = directionEast;
                            break;
                        case EAST:
                            touchPointDirection = directionEast;
                            break;
                        case SOUTH_EAST:
                            // We take the south direction and rotate the
                            // touch point later
                            touchPointDirection = directionSouth;
                            break;
                        case SOUTH:
                            touchPointDirection = directionSouth;

                            break;
                        case SOUTH_WEST:
                            // We take the west direction and rotate the
                            // touch point later
                            touchPointDirection = directionWest;
                            break;
                        case WEST:
                            touchPointDirection = directionWest;

                            break;
                        case NORTH_WEST:
                            // We take the north direction and rotate the
                            // touch point later
                            touchPointDirection = directionNorth;

                            break;
                        default:
                            // Do nothing, return on null later
                            break;

                    }

                    if (touchPointDirection != null) {

                        // Create new ray with Shape center in touch
                        // point
                        // direction
                        Ray ray = new Ray(centerCopy, touchPointDirection);
                        log.trace("Ray for" + touchPointLoc + " is: " + ray); //$NON-NLS-1$ //$NON-NLS-2$

                        // Get intersection vertices for ray
                        intersectionVecList = getRayShapeIntersectionPoints(
                                pApplet, menu, shapeBoundsVertices, ray);

                        // We should have only one intersection point
                        if (intersectionVecList.size() == 1) {

                            // The touchPointTemp is now at index 0
                            touchPointTemp = intersectionVecList.get(0);

                            // Special handling for non-right-angle touch points
                            if (touchPointLoc == ETouchPointLocation.NORTH_EAST
                                    || touchPointLoc == ETouchPointLocation.SOUTH_EAST
                                    || touchPointLoc == ETouchPointLocation.SOUTH_WEST
                                    || touchPointLoc == ETouchPointLocation.NORTH_WEST) {

                                // Take the calculated touch point, create a
                                // dummy component there
                                MTEllipse dummyCircle = new MTEllipse(pApplet,
                                        touchPointTemp.getCopy(), 2, 2);

                                // Rotate for 45 degrees counterclockwise around
                                // the
                                // MTCircleMenu center
                                dummyCircle.rotateZGlobal(centerCopy, -45.0f);

                                // Take center position global, this is
                                // the correct touch point
                                touchPoint = dummyCircle.getCenterPointGlobal();

                                // Destroy dummy circle
                                dummyCircle.destroy();

                                log.debug("Leaving getMTCircleMenuBoundsTouchPoint(): " + touchPoint); //$NON-NLS-1$
                                return touchPoint;

                            }

                            // We take the temporary touch point from above
                            touchPoint = touchPointTemp;

                            log.debug("Leaving getMTCircleMenuBoundsTouchPoint(): " + touchPoint); //$NON-NLS-1$
                            return touchPoint;

                        }

                        log.error("Leaving getMTCircleMenuBoundsTouchPoint(): null, more or less than one refined touch points for this direction!"); //$NON-NLS-1$
                        return null;

                    }
                    log.error("Leaving getMTCircleMenuBoundsTouchPoint(): null, invalid touch point position param or rotation!"); //$NON-NLS-1$
                    return null;

                }
                log.error("Leaving getMTCircleMenuBoundsTouchPoint(): null, shape has no bounds!"); //$NON-NLS-1$
                return null;

            }
            log.error("Leaving getMTCircleMenuBoundsTouchPoint(): null, invalid input, rotation cannot be handled"); //$NON-NLS-1$
            return null;

        }
        log.error("Leaving getMTCircleMenuBoundsTouchPoint(): null, invalid null input"); //$NON-NLS-1$
        return null;
    }

    /**
     * Returns a list of position vectors where a given ray intersects a plane
     * created by the given vertices of a given shape (MTRectangle etc.).
     * 
     * @param pApplet
     *            the current application instance
     * @param shape
     *            the shape (MTRectangle etc.)
     * @param shapeBoundsVertices
     *            the bounding vertices of the shape
     * @param ray
     *            the ray we want to intersect the shape's plane with
     * @return the list of intersection points
     */
    private static List<Vector3D> getRayShapeIntersectionPoints(
            PApplet pApplet, AbstractShape shape,
            Vector3D[] shapeBoundsVertices, Ray ray) {

        log.debug("Entering getRayShapeIntersectionPoints(pApplet=" + pApplet + ", shape" + shape //$NON-NLS-1$ //$NON-NLS-2$
                + ", shapeBoundsVertices=" + shapeBoundsVertices //$NON-NLS-1$
                + ", ray=" + shapeBoundsVertices + ")"); //$NON-NLS-1$//$NON-NLS-2$

        Vector3D intersectionVecTemp = null;
        List<Vector3D> intersectionVecList = new ArrayList<Vector3D>();

        if (shape != null && shapeBoundsVertices.length > 0 && ray != null) {

            // Create four planes at a right angle to the rectangle plane
            // Check if the ray intersects one or more of these planes
            for (int i = 0; i < shapeBoundsVertices.length; i++) {

                // Create a point outside of the z-Plane
                Vector3D vert0 = shapeBoundsVertices[i].getCopy();
                vert0.setZ(-10);

                // Create planes for the first three edge vertices
                if (i < (shapeBoundsVertices.length - 1)) {

                    // Check plane for ray intersection
                    intersectionVecTemp = ToolsGeometry
                            .getRayPlaneIntersection(ray, new Vertex(
                                    shapeBoundsVertices[i]), new Vertex(
                                    shapeBoundsVertices[i + 1]), new Vertex(
                                    vert0));

                    // Add if we have a intersection point and it isn't already
                    // added
                    if (intersectionVecTemp != null
                            && !(intersectionVecList
                                    .contains(intersectionVecTemp))) {
                        intersectionVecList.add(intersectionVecTemp);
                    }
                    log.trace("I: " + i + " intersection vec: " + intersectionVecTemp); //$NON-NLS-1$ //$NON-NLS-2$
                } else { // for the last edge vertex of the IdeaNodeView,
                         // take the first vertex again to create a plane

                    // Check plane for ray intersection
                    intersectionVecTemp = ToolsGeometry
                            .getRayPlaneIntersection(ray, new Vertex(
                                    shapeBoundsVertices[i]), new Vertex(
                                    shapeBoundsVertices[0]), new Vertex(vert0));

                    // Add if we have a intersection point
                    if (intersectionVecTemp != null) {
                        intersectionVecList.add(intersectionVecTemp);
                    }
                    log.trace("I: " + i + " intersection vec: " + intersectionVecTemp); //$NON-NLS-1$//$NON-NLS-2$

                }

            }

            // There should be at least one intersection point
            if (intersectionVecList.size() > 0) {

                List<Vector3D> refinedIntersectionVecList = new ArrayList<Vector3D>();

                // We sort by distance from ray start, then we should have the
                // closest intersection at pos 0
                // Convert to array for sorting
                Object[] vectorList = intersectionVecList.toArray();
                Vector3D[] finalVectorList = new Vector3D[vectorList.length];

                for (int i = 0; i < vectorList.length; i++) {
                    if (vectorList[i] instanceof Vector3D) {
                        finalVectorList[i] = (Vector3D) vectorList[i];
                    }
                }

                // Call sorting method by distance ray start point
                Vector3D[] sortedVertices = ToolsVectors
                        .selectionSortVerticesDistance(finalVectorList,
                                ray.getRayStartPoint());

                // The closest vector is now at index 0
                refinedIntersectionVecList.add(sortedVertices[0]);

                if (refinedIntersectionVecList.size() > 0) {

                    // Return refined list
                    log.debug("Leaving getRayShapeIntersectionPoints() - refinedIntersectionVecList"); //$NON-NLS-1$
                    return refinedIntersectionVecList;
                }
                // We had intersections, but no refined intersection points
                log.warn("Leaving getRayShapeIntersectionPoints(): null, intersections found, but refined intersection points list is empty!"); //$NON-NLS-1$

                return null;

            }
            // We have not found any intersections
            log.debug("Leaving getRayShapeIntersectionPoints(): null, no intersections found!"); //$NON-NLS-1$

            return null;
        }
        log.error("Leaving getRayShapeIntersectionPoints(): null, invalid null input"); //$NON-NLS-1$
        return null;
    }

    /**
     * <p>
     * Calculate the Coordinates needed for placing the (keyboard) Rectangle.
     * </p>
     * 
     * <p>
     * Original method source:
     * {@link org.mt4jx.components.visibleComponents.widgets.MTSuggestionTextArea}
     * </p>
     * 
     * 
     * @param rect
     *            the Rectangle
     * @param ta
     *            the TextArea
     * @param xo
     *            the x-offset
     * @param yo
     *            the y-offset
     * @return the position as Vector3D
     * 
     * @see MTSuggestionTextArea
     * 
     */
    public static Vector3D calcPos(MTRectangle rect, MTPolygon ta, float xo,
            float yo) {

        log.debug("Entering calcPos(rect=" + rect + ", ta=" + ta + ", xo=" + xo + ", yp=" + yo + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

        Vector3D vec = new Vector3D((ta.getWidthXY(TransformSpace.LOCAL) / 2)
                + rect.getVerticesLocal()[0].getX() + xo,
                (ta.getHeightXY(TransformSpace.LOCAL) / 2)
                        + rect.getVerticesLocal()[0].getY() + yo);

        log.debug("Leaving calcPos(): " + vec); //$NON-NLS-1$ 
        return vec;

    }

}
