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

/***
 * @author Sarah Will
 * @version 1.0
 */

import org.apache.log4j.Logger;
import org.mt4j.AbstractMTApplication;
import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.widgets.MTSvg;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * Container class for RelationView adornment elements.
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class RVAdornmentsContainer extends MTComponent {

    private static Logger         log                                        = Logger.getLogger(MarkerContainer.class);

    /* *** Adornment images constants *** */
    /** The circle anchor svg image file name */
    private static final String   CIRCLE_IMG_FILE_NAME                       = "arrowBase.svg";                        //$NON-NLS-1$
    /** The point arrow svg image file name */
    private static final String   ARROW_IMG_FILE_NAME                        = "arrowHead.svg";                        //$NON-NLS-1$

    /** The circle image width in percent of the ideaNodeView width */
    private static final float    CIRCLE_IMG_WIDTH_SCALE_TO_IDEANODE_PERCENT = 0.06f;
    /** The arrow image width in percent of the ideaNodeView width */
    private static final float    ARROW_IMG_WIDTH_SCALE_TO_IDEANODE_PERCENT  = 0.14f;

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication abstractMTapplication;

    /* *** Relationships *** */
    /** The parent relationView */
    private RelationView          parentRelationView;
    /** The parent IdeaNodeView of the parent relationView */
    private IdeaNodeView          parentIdeaNodeView;
    /** The child IdeaNodeView of the parent relationView */
    private IdeaNodeView          childIdeaNodeView;

    /* *** Touch points *** */
    /** The initial touchPoint that connects the the parent IdeaNodeView */
    private Vector3D              pTouchPointToSet;
    /** The initial touchPoint that connects the the child IdeaNodeView */
    private Vector3D              cTouchPointToSet;

    /* *** Images *** */
    /** The arrow svg image */
    private MTSvg                 arrowImage;
    /** The circle svg image */
    private MTSvg                 circleImage;
    /** The arrow original rotation at initialization in degrees */
    private float                 arrowHeadOriginalRotation;

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new RVAdornmentsContainer.
     * 
     * @param pApplet
     *            the application instance
     * @param parent
     *            the relationView parent
     * @param parentIdeaNodeView
     *            the parent IdeaNodeView of the RelationView parent
     * @param childIdeaNodeView
     *            the child IdeaNodeView of the RelationView parent
     * @param pTouchPoint
     *            the touch point connecting the parent IdeaNodeView
     * @param cTouchPoint
     *            the touch point connecting the child IdeaNodeView
     */
    public RVAdornmentsContainer(PApplet pApplet, RelationView parent,
            IdeaNodeView parentIdeaNodeView, IdeaNodeView childIdeaNodeView,
            Vector3D pTouchPoint, Vector3D cTouchPoint) {
        super(pApplet);

        log.debug("Execute RVAdornmentsContainer(pApplet=" + pApplet //$NON-NLS-1$
                + "parent=" + parent + ", parentIdeaNodeView" + parentIdeaNodeView //$NON-NLS-1$//$NON-NLS-2$
                + ", childIdeaNodeView" + childIdeaNodeView + "pTouchPoint=" + pTouchPoint //$NON-NLS-1$ //$NON-NLS-2$
                + ", cTouchPoint=" + cTouchPoint + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Store application reference
        this.abstractMTapplication = (MTApplication) pApplet;

        // Set parent relation view
        this.parentRelationView = parent;

        // Set relation view child and parent IdeaNodeViews
        this.parentIdeaNodeView = parentIdeaNodeView;
        this.childIdeaNodeView = childIdeaNodeView;

        // Store touch points
        this.pTouchPointToSet = pTouchPoint.getCopy();
        this.cTouchPointToSet = cTouchPoint.getCopy();

        // Initialize RelationView Adornments
        initialize();
    }

    /* ********Getters & Setters******** */
    /**
     * Returns the parent RelationView.
     * 
     * @return the parentRelationView
     */
    public RelationView getParentRelationView() {
        return this.parentRelationView;
    }

    /**
     * Returns the parent IdeaNodeView.
     * 
     * @return the parentIdeaNodeView
     */
    public IdeaNodeView getParentIdeaNodeView() {
        return this.parentIdeaNodeView;
    }

    /**
     * Returns the child IdeaNodeView.
     * 
     * @return the childIdeaNodeView
     */
    public IdeaNodeView getChildIdeaNodeView() {
        return this.childIdeaNodeView;
    }

    /**
     * Returns the arrow SVG image instance.
     * 
     * @return the arrowImage
     */
    public MTSvg getArrowImage() {
        return this.arrowImage;
    }

    /**
     * Returns the circle SVG image instance.
     * 
     * @return the circleImage
     */
    public MTSvg getCircleImage() {
        return this.circleImage;
    }

    /**
     * Returns the original arrowHead Rotation
     * 
     * @return the arrowHeadOriginalRotation
     */
    public float getArrowHeadOriginalRotation() {
        return this.arrowHeadOriginalRotation;
    }

    /* *********Object methods********* */
    /**
     * Initializes the RelationViewAdornmentParent object.
     * 
     */
    private void initialize() {

        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Remove scale processors and listeners
        ToolsEventHandling.removeScaleProcessorsAndListeners(this);

        // Set composite true
        this.setComposite(true);

        // Add adornments
        addAdornments();

        log.debug("Leaving initialize()"); //$NON-NLS-1$

    }

    /**
     * Adds adornments for the RelationView at the set touch points.
     * 
     * @return true, if adding the adornment was successful
     */
    private boolean addAdornments() {

        log.debug("Entering addAdornments()"); //$NON-NLS-1$

        // Get RelationView parent and child
        if (this.pTouchPointToSet != null && this.cTouchPointToSet != null
                && getParentIdeaNodeView() != null
                && getChildIdeaNodeView() != null) {

            // Get child IdeaNodeView width
            float parentIdeaNodeWidth = getParentIdeaNodeView().getWidthXY(
                    TransformSpace.GLOBAL);

            // Set circle image for child touch point
            this.circleImage = new MTSvg(this.abstractMTapplication,
                    MT4jSettings.getInstance().getDefaultSVGPath()
                            + CIRCLE_IMG_FILE_NAME);

            // Set to size dependent on IdeaNodeSize
            this.circleImage.setWidthXYGlobal(parentIdeaNodeWidth
                    * CIRCLE_IMG_WIDTH_SCALE_TO_IDEANODE_PERCENT);

            this.circleImage.setPositionGlobal(this.pTouchPointToSet);
            this.addChild(this.circleImage);

            // Get parent IdeaNodeView width
            float childIdeaNodeWidth = getChildIdeaNodeView().getWidthXY(
                    TransformSpace.GLOBAL);

            // Set circle image for child touch point
            this.arrowImage = new MTSvg(this.abstractMTapplication,
                    MT4jSettings.getInstance().getDefaultSVGPath()
                            + ARROW_IMG_FILE_NAME);

            // Set to size dependent on IdeaNodeSize
            this.arrowImage.setWidthXYGlobal(childIdeaNodeWidth
                    * ARROW_IMG_WIDTH_SCALE_TO_IDEANODE_PERCENT);

            this.arrowImage.setPositionGlobal(this.cTouchPointToSet);
            this.addChild(this.arrowImage);

            reRotateArrowHead(this.pTouchPointToSet, this.cTouchPointToSet);

            this.arrowHeadOriginalRotation = ToolsComponent
                    .getRotationZInDegrees(this.arrowImage);

            // ToolsComponent.reRotateSvg(relationView, arrowImage);
            log.debug("Leaving addAdornments(): true "); //$NON-NLS-1$
            return true;

        }
        log.error("Leaving addAdornments(): false, RelationView not initialized correctly!"); //$NON-NLS-1$
        return false;

    }

    /**
     * Re-rotates the arrow head for the given start and end point of the
     * relationView.
     * 
     * @param startPoint
     *            the start point of the relationView parent
     * @param endPoint
     *            the end point of the relationView parent
     */
    protected void reRotateArrowHead(Vector3D startPoint, Vector3D endPoint) {

        // Re-rotate arrow head
        Ray ray = new Ray(startPoint, endPoint);
        Vector3D relViewDirectionVecNormal = ray.getRayDirectionNormalized();

        log.trace("Relation view direction vector normal: " + relViewDirectionVecNormal); //$NON-NLS-1$

        Vector3D normalVecNorth = new Vector3D(0, -1,
                relViewDirectionVecNormal.getZ());

        float angleBetweenVectorsRadians = ToolsGeometry.angleBetween(
                normalVecNorth, relViewDirectionVecNormal);

        // Convert toBeReRotatedShape rotation to degrees
        double angleBetweenVectorDegrees = angleBetweenVectorsRadians
                * (180 / Math.PI);

        log.trace("Angle between the both: " + angleBetweenVectorDegrees); //$NON-NLS-1$

        if (relViewDirectionVecNormal.getX() > 0) {
            // Rotate the arrow head as much as the angle
            this.arrowImage.rotateZ(
                    this.arrowImage.getCenterPointGlobal(),
                    (float) angleBetweenVectorDegrees
                            - ToolsComponent
                                    .getRotationZInDegrees(this.arrowImage));
        } else {
            this.arrowImage.rotateZ(
                    this.arrowImage.getCenterPointGlobal(),
                    360.0f
                            - (float) angleBetweenVectorDegrees
                            - ToolsComponent
                                    .getRotationZInDegrees(this.arrowImage));

        }

    }

}
