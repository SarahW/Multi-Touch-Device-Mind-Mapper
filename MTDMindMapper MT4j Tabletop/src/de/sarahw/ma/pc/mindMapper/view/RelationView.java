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
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.StyleInfo;
import org.mt4j.components.visibleComponents.shapes.GeometryInfo;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;

/**
 * <p>
 * Class representing a directed relation view between two IdeaNodeViews. Every
 * RelationView is connected to a parent IdeaNodeView and a child IdeaNodeView.
 * Contains a RVAdornementsContainer with the arrow base and arrow head graphics
 * that are drawn depending on the IdeaNodeView(s) size and rotation.
 * </p>
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */
public class RelationView extends MTLine {

    private static Logger          log                     = Logger.getLogger(RelationView.class);

    /* *** RelationView constants *** */
    /** The RelationView line thickness (stroke weight) */
    private static final float     RELATION_LINE_THICKNESS = 4.0f;
    /** The RelationView line fill color */
    private static final MTColor   RELATION_COLOR          = MTColor.BLACK;
    /** The RelationView line stroke color */
    private static final MTColor   RELATION_STROKE_COLOR   = MTColor.BLACK;
    /** The RelationView line style info */
    private static final StyleInfo RELATION_STYLE_INFO     = new StyleInfo(
                                                                   RELATION_COLOR,
                                                                   RELATION_STROKE_COLOR,
                                                                   true,
                                                                   false,
                                                                   false,
                                                                   RELATION_LINE_THICKNESS,
                                                                   GL10.GL_TRIANGLE_FAN,
                                                                   (short) 0);
    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication  mtApplication;

    /* *** Relationships *** */
    /** The parent ideaNodeView to the relationView */
    private IdeaNodeView           parentNode;
    /** The child ideaNodeView to the relationView */
    private IdeaNodeView           childNode;

    /* *** RelationView adornments *** */
    /**
     * The container holding the relationView adornments (circle anchor and
     * arrow point)
     */
    private RVAdornmentsContainer  rvAdornmentsContainer;

    /* ***********Constructors*********** */
    /**
     * Instantiates a new RelationView for the given parent and child
     * IdeaNodeViews.
     * 
     * @param pApplet
     *            the application instance
     * @param parent
     *            the parent IdeaNodeView
     * @param child
     *            the child IdeaNodeView
     */
    public RelationView(PApplet pApplet, IdeaNodeView parent, IdeaNodeView child) {

        super(pApplet, new Vertex(parent.getCenterPointGlobal()), new Vertex(
                child.getCenterPointGlobal()));

        log.debug("Executing RelationView(pApplet=" + pApplet + ", parent= " //$NON-NLS-1$ //$NON-NLS-2$
                + parent + ", child= " + child); //$NON-NLS-1$

        // Store application reference
        this.mtApplication = (AbstractMTApplication) pApplet;

        // Store idea node references
        this.parentNode = parent;
        this.childNode = child;

        // Initialize RelationView
        initialize();

    }

    /* ********Getters & Setters******** */
    /**
     * Returns the parent IdeaNodeView of this RelationView.
     * 
     * @return the parentNode
     */
    public IdeaNodeView getParentIdeaNodeView() {
        return this.parentNode;
    }

    /**
     * Returns the child IdeaNodeView of this RelationView.
     * 
     * @return the childNode
     */
    public IdeaNodeView getChildIdeaNodeView() {
        return this.childNode;
    }

    /**
     * Returns the RVAdornmentsContainer instance.
     * 
     * @return the rvAdornmentsContainer
     */
    public RVAdornmentsContainer getRvAdornmentsContainer() {
        return this.rvAdornmentsContainer;
    }

    /* *********Object methods********* */
    /**
     * Initializes the RelationView. Adds/Removes listeners.
     */
    private void initialize() {

        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Remove scale processors and listeners
        ToolsEventHandling.removeScaleProcessorsAndListeners(this);

        // Add custom dragProcessor
        addCustomDragProcessor();

        // Set style info
        this.setStyleInfo(RELATION_STYLE_INFO);

        // Calculate and set Touch Points
        setTouchPoints();

        log.debug("Leaving initialize()"); //$NON-NLS-1$

    }

    /**
     * Sets the RelationView touch points at parent and child IdeaNodeView.
     * 
     */
    private void setTouchPoints() {

        log.debug("Entering setTouchPoints()"); //$NON-NLS-1$

        // Get parent and child IdeaNodeView
        IdeaNodeView childIdeaNodeView = getChildIdeaNodeView();
        IdeaNodeView parentIdeaNodeView = getParentIdeaNodeView();

        // If child and parent IdeaNodeView are valid
        if (childIdeaNodeView != null && parentIdeaNodeView != null) {

            // Calculate parent and child IdeaNodeView touch points
            Vector3D pTouchPoint = calculateTouchPoint(parentIdeaNodeView,
                    childIdeaNodeView);
            Vector3D cTouchPoint = calculateTouchPoint(childIdeaNodeView,
                    parentIdeaNodeView);

            if (pTouchPoint != null && cTouchPoint != null) {

                // Set vertices anew
                transformRelationViewVertices(pTouchPoint, cTouchPoint);

                // Add adornments
                addRelationViewAdornments(parentIdeaNodeView,
                        childIdeaNodeView, pTouchPoint, cTouchPoint);
            } else {
                log.error("Error! Touch points invalid!"); //$NON-NLS-1$
                // TODO: close application
            }

        } else {
            log.error("Error! RelationView has not been initialized properly, needs parent and child IdeaNodeView!"); //$NON-NLS-1$
            // TODO Exit application
        }
        log.debug("Leaving setTouchPoints()"); //$NON-NLS-1$

    }

    /**
     * Calculates the touch point of the RelationView MTLine with the parent or
     * child IdeaNodeView (out of four possible touch points).
     * 
     * @param ideaNodeViewToTouch
     *            the ideaNodeView we want to get the touch point of the
     *            RelationView for (parent or child IdeaNodeView only!!)
     * @param otherIdeaNodeView
     *            the other IdeaNodeView connected to the ideaNodeViewToTouch by
     *            the RelationView
     * 
     * 
     * @return the touch point
     */
    protected Vector3D calculateTouchPoint(IdeaNodeView ideaNodeViewToTouch,
            IdeaNodeView otherIdeaNodeView) {

        log.debug("Entering calculateTouchPoint(ideaNodeViewToTouch=" //$NON-NLS-1$
                + ideaNodeViewToTouch
                + ", otherIdeaNodeView" + otherIdeaNodeView + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (ideaNodeViewToTouch != null && otherIdeaNodeView != null) {

            // Get center points
            Vector3D ideaNodeViewCenter = ideaNodeViewToTouch
                    .getCenterPointGlobal().getCopy();
            Vector3D otherIdeaNodeCenter = otherIdeaNodeView
                    .getCenterPointGlobal().getCopy();

            // Create new ray from parent to child center
            Ray ray = new Ray(ideaNodeViewCenter, otherIdeaNodeCenter);
            log.trace("Ray is: " + ray); //$NON-NLS-1$

            // Get IdeaNodeView rectangle bounds (four vertices)
            if (ideaNodeViewToTouch.hasBounds()) {

                log.trace("Has other node also bounds this point? " + otherIdeaNodeView.hasBounds()); //$NON-NLS-1$

                // Get the vectors for the bounding shape of the ideaNodeView (=
                // rectangle)
                Vector3D[] ideaNodeBoundsVertices = ideaNodeViewToTouch
                        .getBounds().getVectorsGlobal();

                Vector3D intersectionVecTemp = null;
                List<Vector3D> intersectionVecList = new ArrayList<Vector3D>();

                // Create four planes at a right angle to the rectangle plane
                // Check if the ray (which equals the RelationView line)
                // intersects one or more of these planes
                for (int i = 0; i < ideaNodeBoundsVertices.length; i++) {

                    // Create a vector located outside the 2D z-Plane
                    Vector3D vert0 = ideaNodeBoundsVertices[i].getCopy();
                    vert0.setZ(-10);

                    // Create planes for the first three vertices
                    // And add the intersection vector to the list
                    if (i < (ideaNodeBoundsVertices.length - 1)) {

                        intersectionVecTemp = ToolsGeometry
                                .getRayPlaneIntersection(
                                        ray,
                                        new Vertex(ideaNodeBoundsVertices[i]
                                                .getCopy()),
                                        new Vertex(
                                                ideaNodeBoundsVertices[i + 1]
                                                        .getCopy()),
                                        new Vertex(vert0));
                        if (intersectionVecTemp != null) {
                            intersectionVecList.add(intersectionVecTemp);
                        }
                        log.trace("I: " + i + " intersection vec: " + intersectionVecTemp); //$NON-NLS-1$ //$NON-NLS-2$

                    } else { // for the last vertex, take the first to create a
                             // plane
                        intersectionVecTemp = ToolsGeometry
                                .getRayPlaneIntersection(
                                        ray,
                                        new Vertex(ideaNodeBoundsVertices[i]
                                                .getCopy()),
                                        new Vertex(ideaNodeBoundsVertices[0]
                                                .getCopy()), new Vertex(vert0));
                        if (intersectionVecTemp != null) {
                            intersectionVecList.add(intersectionVecTemp);
                        }
                        log.trace("I: " + i + " intersection vec: " + intersectionVecTemp); //$NON-NLS-1$//$NON-NLS-2$

                    }

                }

                // We should have at least one intersection point in the list
                if (intersectionVecList.size() > 0) {

                    // Convert to array for sorting
                    Object[] vectorList = intersectionVecList.toArray();
                    Vector3D[] finalVectorList = new Vector3D[vectorList.length];

                    for (int i = 0; i < vectorList.length; i++) {
                        if (vectorList[i] instanceof Vector3D) {
                            finalVectorList[i] = (Vector3D) vectorList[i];
                        }
                    }

                    // Call sorting method by distance to parent center
                    // !!Also for child IdeaNodeView, or else we may get
                    // the opposite touch point
                    Vector3D[] sortedVertices = ToolsVectors
                            .selectionSortVerticesDistance(finalVectorList,
                                    getParentIdeaNodeView()
                                            .getCenterPointGlobal());

                    // The closest vector is now at index 0
                    Vector3D finalIntersectionVector = sortedVertices[0];

                    /*
                     * // DEBUG: for (Vector3D vec : finalVectorList) { // Add
                     * circle to parent IdeaNodeView touch point MTEllipse
                     * circle = new MTEllipse(this.mtApplication, vec, 5, 5);
                     * circle.setStyleInfo(this.circleStyleInfo);
                     * this.addChild(circle); }
                     */

                    log.trace("Sorted Vector list:"); //$NON-NLS-1$
                    int i = 0;
                    for (Vector3D vec : sortedVertices) {
                        log.trace("I: " + i + ", vector: " + vec); //$NON-NLS-1$ //$NON-NLS-2$
                    }

                    if (finalIntersectionVector != null) {

                        // Get the ideaNodeViewToIntersect Touch points
                        Vector3D tpNorth = ideaNodeViewToTouch
                                .getTpNorthMarkerPosition();
                        Vector3D tpSouth = ideaNodeViewToTouch
                                .getTpSouthMarkerPosition();
                        Vector3D tpEast = ideaNodeViewToTouch
                                .getTpEastMarkerPosition();
                        Vector3D tpWest = ideaNodeViewToTouch
                                .getTpWestMarkerPosition();

                        Vector3D[] touchPointVertices = { tpNorth, tpSouth,
                                tpEast, tpWest };

                        // Get the closest touch point to the intersection
                        // point
                        Vector3D[] sortedTouchPoints = ToolsVectors
                                .selectionSortVerticesDistance(
                                        touchPointVertices,
                                        finalIntersectionVector);

                        // The closest touch point is at index 0

                        log.debug("Leaving calculateTouchPoint(): Intersection touch point is: " + touchPointVertices[0]); //$NON-NLS-1$
                        return sortedTouchPoints[0];
                    }
                    log.error("Leaving calculateTouchPoint(): We have no final touch point!?"); //$NON-NLS-1$
                    return null;

                }
                log.error("Leaving calculateTouchPoint():false, ideaNodeView has no bounds!"); //$NON-NLS-1$
                // TODO exit application
                return null;
            }
            log.error("Leaving calculateTouchPoint():false, we don't have a touch point!"); //$NON-NLS-1$
            return null;
        }
        log.error("Leaving calculateTouchPoint(): false, invalid null input"); //$NON-NLS-1$
        return null;

    }

    /**
     * Transforms the RelationView vertices to the new vertices.
     * 
     * @param startVector
     *            the parent IdeaNodeView touch point
     * @param endVector
     *            the child IdeaNodeView touch point
     * 
     * @return true, if transformation is successful
     */
    protected boolean transformRelationViewVertices(Vector3D startVector,
            Vector3D endVector) {

        log.debug("Entering transformRelationViewVertices(startVector=" //$NON-NLS-1$
                + startVector + ", endVector=" + endVector + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (startVector != null && endVector != null) {

            // Get geometry info for this RelationView (not bounds!!)
            Vector3D[] vertices = this.getGeometryInfo().getVertices();

            // We should have two vertices (start and end point)
            if (vertices.length == 2) {

                // this.setVertices(new Vertex[] { new Vertex(startVector),
                // new Vertex(endVector) });

                Vector3D startLocal = this.globalToLocal(startVector);
                Vector3D endLocal = this.globalToLocal(endVector);

                // Transform this RelationView
                this.setGeometryInfo(new GeometryInfo(this.mtApplication,
                        new Vertex[] { new Vertex(startLocal),
                                new Vertex(endLocal) }));

                log.debug("Leaving transformRelationViewVertices(): true"); //$NON-NLS-1$
                return true;

            }
            log.error("Leaving transformRelationViewVertices(): false, invalid null input!"); //$NON-NLS-1$
            return false;
        } else if (startVector == null && endVector != null) {

            // Get geometry info for this RelationView (not bounds!!)
            Vector3D[] vertices = this.getGeometryInfo().getVertices();

            // We should have two vertices (start and end point)
            if (vertices.length == 2) {

                // this.setVertices(new Vertex[] { new Vertex(startVector),
                // new Vertex(endVector) });

                Vector3D startLocal = vertices[0];
                Vector3D endLocal = this.globalToLocal(endVector);

                // Transform this RelationView
                this.setGeometryInfo(new GeometryInfo(this.mtApplication,
                        new Vertex[] { new Vertex(startLocal),
                                new Vertex(endLocal) }));

                log.debug("Leaving transformRelationViewVertices(): true"); //$NON-NLS-1$
                return true;

            }
        }

        log.error("Leaving transformRelationViewVertices(): false, Relation View" + //$NON-NLS-1$
                "should have two vertices!"); //$NON-NLS-1$
        return false;

    }

    /**
     * Adds relation view adornments to the RelationView.
     * 
     * 
     * @param parentIdeaNodeView
     *            the parent IdeaNodeView
     * @param childIdeaNodeView
     *            the child IdeaNodeView
     * @param pTouchPoint
     *            the parent IdeaNodeView touch point
     * @param cTouchPoint
     *            the child IdeaNodeView touch point
     */
    private void addRelationViewAdornments(IdeaNodeView parentIdeaNodeView,
            IdeaNodeView childIdeaNodeView, Vector3D pTouchPoint,
            Vector3D cTouchPoint) {

        log.debug("Entering addRelationViewAdornments(pTouchPoint=" + pTouchPoint //$NON-NLS-1$ 
                + ", cTouchPoint=" + cTouchPoint); //$NON-NLS-1$ 

        // Add new adornment container as child
        this.rvAdornmentsContainer = new RVAdornmentsContainer(
                this.mtApplication, this, parentIdeaNodeView,
                childIdeaNodeView, pTouchPoint, cTouchPoint);

        this.addChild(this.rvAdornmentsContainer);

        log.debug("Leaving addRelationViewAdornments()"); //$NON-NLS-1$

    }

    /**
     * Updates the RelationView end point (touch point to child IdeaNodeView).
     * 
     */
    protected void updateEndPoint() {

        log.debug("Entering updateEndPoint()"); //$NON-NLS-1$

        // Get geometry info for this RelationView (not
        // bounds!!)
        Vector3D[] vertices = this.getGeometryInfo().getVertices();

        // We should have two vertices (start and end point)
        if (vertices.length == 2) {

            Vector3D startPoint = vertices[0];

            Vector3D endPoint = vertices[1];

            // Check which IdeaNodeView touch Point this is closest to and
            // set that touch point as new end point

            if (endPoint != null && startPoint != null) {

                // Get the child IdeaNodeView Touch points
                Vector3D ctpNorth = this.childNode.getTpNorthMarkerPosition();
                Vector3D ctpSouth = this.childNode.getTpSouthMarkerPosition();
                Vector3D ctpEast = this.childNode.getTpEastMarkerPosition();
                Vector3D ctpWest = this.childNode.getTpWestMarkerPosition();

                Vector3D[] touchPointVerticesChild = { ctpNorth, ctpSouth,
                        ctpEast, ctpWest };

                // Get the closest touch point to the intersection
                // point
                Vector3D[] sortedTouchPointsChild = ToolsVectors
                        .selectionSortVerticesDistance(touchPointVerticesChild,
                                endPoint);

                // The closest touch point is at index 0
                // Transform the RelationView
                this.transformRelationViewVertices(null,
                        sortedTouchPointsChild[0].getCopy());

                log.trace("Update touchpoints: child at" //$NON-NLS-1$
                        + sortedTouchPointsChild[0]);

                // Send rvContainer to front again
                // Send all RVAdornmentsContainer children to front
                this.getRvAdornmentsContainer().sendToFront();

                // Set the arrow head anew
                RVAdornmentsContainer container = this
                        .getRvAdornmentsContainer();

                container.getArrowImage().setPositionGlobal(
                        sortedTouchPointsChild[0]);

                // Re-roate arrow head
                container.reRotateArrowHead(startPoint,
                        sortedTouchPointsChild[0]);

                container.getArrowImage().setPositionGlobal(
                        sortedTouchPointsChild[0]);

            }

        }

        log.debug("Leaving updateEndPoint()"); //$NON-NLS-1$

    }

    /**
     * Recalculates the RelationView touch points to child and parent
     * IdeaNodeView. Called when child IdeaNodeView is dragged or rotated.
     * 
     */
    public void recalculateTouchPoints() {

        log.debug("Entering recalculateTouchPoints()"); //$NON-NLS-1$

        // Get parent and child IdeaNodeView
        IdeaNodeView childIdeaNodeView = getChildIdeaNodeView();
        IdeaNodeView parentIdeaNodeView = getParentIdeaNodeView();

        // If child and parent IdeaNodeView are valid
        if (childIdeaNodeView != null && parentIdeaNodeView != null) {

            // Calculate parent and child IdeaNodeView touch points
            Vector3D pTouchPoint = calculateTouchPoint(parentIdeaNodeView,
                    childIdeaNodeView);
            Vector3D cTouchPoint = calculateTouchPoint(childIdeaNodeView,
                    parentIdeaNodeView);

            if (pTouchPoint != null && cTouchPoint != null) {

                // Set vertices anew
                transformRelationViewVertices(pTouchPoint, cTouchPoint);

                // Send rvContainer to front again
                // Send all RVAdornmentsContainer children to front
                this.getRvAdornmentsContainer().sendToFront();

                // Set the arrow head anew
                RVAdornmentsContainer container = this
                        .getRvAdornmentsContainer();
                container.getArrowImage().setPositionGlobal(cTouchPoint);

                // Set the arrow base anew
                container.getCircleImage().setPositionGlobal(pTouchPoint);

                // Re-roate arrow head
                container.reRotateArrowHead(pTouchPoint, cTouchPoint);
                container.getArrowImage().setPositionGlobal(cTouchPoint);
            }
        }

        log.debug("Entering recalculateTouchPoints()"); //$NON-NLS-1$

    }

    /**
     * Updates the RelationView touch points to parent and child IdeaNodeViews.
     * Not used as of now (slghtly ugly visual effects). Use
     * recalculateTouchPoints() instead!
     * 
     */
    protected void updateTouchPoints() {

        log.debug("Entering updateTouchPoints()"); //$NON-NLS-1$

        // Get geometry info for this RelationView (not
        // bounds!!)
        Vector3D[] vertices = this.getGeometryInfo().getVertices();

        // We should have two vertices (start and end point)
        if (vertices.length == 2) {

            Vector3D startPoint = vertices[0];

            Vector3D endPoint = vertices[1];

            // Check which IdeaNodeView touch Point this is closest to and
            // set that touch point as new end point

            if (endPoint != null && startPoint != null) {

                // Get the parent IdeaNodeView Touch points
                Vector3D ptpNorth = this.parentNode.getTpNorthMarkerPosition();
                Vector3D ptpSouth = this.parentNode.getTpSouthMarkerPosition();
                Vector3D ptpEast = this.parentNode.getTpEastMarkerPosition();
                Vector3D ptpWest = this.parentNode.getTpWestMarkerPosition();

                Vector3D[] touchPointVerticesParent = { ptpNorth, ptpSouth,
                        ptpEast, ptpWest };

                // Get the closest touch point to the intersection
                // point
                Vector3D[] sortedTouchPointsParent = ToolsVectors
                        .selectionSortVerticesDistance(
                                touchPointVerticesParent, startPoint);

                // Get the child IdeaNodeView Touch points
                Vector3D ctpNorth = this.childNode.getTpNorthMarkerPosition();
                Vector3D ctpSouth = this.childNode.getTpSouthMarkerPosition();
                Vector3D ctpEast = this.childNode.getTpEastMarkerPosition();
                Vector3D ctpWest = this.childNode.getTpWestMarkerPosition();

                Vector3D[] touchPointVerticesChild = { ctpNorth, ctpSouth,
                        ctpEast, ctpWest };

                // Get the closest touch point to the intersection
                // point
                Vector3D[] sortedTouchPointsChild = ToolsVectors
                        .selectionSortVerticesDistance(touchPointVerticesChild,
                                endPoint);

                // The closest touch point is at index 0
                // Transform the RelationView
                this.transformRelationViewVertices(sortedTouchPointsParent[0],
                        sortedTouchPointsChild[0]);

                log.debug("Update touchpoints: child at" //$NON-NLS-1$
                        + sortedTouchPointsChild[0]);
                log.debug("Update touchpoints: parent at" //$NON-NLS-1$
                        + sortedTouchPointsParent[0]);

                // Send rvContainer to front again
                // Send all RVAdornmentsContainer children to front
                this.getRvAdornmentsContainer().sendToFront();

                // Set the arrow head anew
                RVAdornmentsContainer container = this
                        .getRvAdornmentsContainer();

                container.getArrowImage().setPositionGlobal(
                        sortedTouchPointsChild[0]);

                // Set the arrow base anew
                container.getCircleImage().setPositionGlobal(
                        sortedTouchPointsParent[0]);

                // Re-roate arrow head
                container.reRotateArrowHead(sortedTouchPointsParent[0],
                        sortedTouchPointsChild[0]);

            }

        }
        log.debug("Leaving updateTouchPoints()"); //$NON-NLS-1$

    }

    /* *********Listener methods********* */
    /**
     * Adds a custom drag/move processor that updates that only processes drag
     * events that are targeted at a child IdeaNodeView. Drag events targeted at
     * a RelationView are bubbled up the hierarchy and processed by the topmost
     * IdeaNodeView parent, which then transforms all its children components.
     * 
     */
    private void addCustomDragProcessor() {

        log.debug("Entering addCustomDragProcessor()"); //$NON-NLS-1$

        // Remove default drag processor
        ToolsEventHandling.removeDragProcessorsAndListeners(this);

        // Add Move Processor with custom gesture action
        MoveRelationViewProcessor moveProcessor = new MoveRelationViewProcessor(
                this.mtApplication);
        moveProcessor.setBubbledEventsEnabled(true);

        this.registerInputProcessor(moveProcessor);

        this.addGestureListener(MoveRelationViewProcessor.class,
                new RelationViewUpdateVerticesAction());

        log.debug("Leaving addCustomDragProcessor()"); //$NON-NLS-1$

    }

    /* ********Overridden methods******** */
    /**
     * <p>
     * Overridden method processInputEvent from MTComponent. As of now, events
     * are only processed if the target is a RelationView.
     * </p>
     * 
     * <p>
     * Warning: the overridden method in MTComponent has a lot of TODO s and
     * FIXME s left, that have not been repeated here - beware when updating the
     * framework version!!
     * </p>
     * 
     * @param inEvt
     *            the input Event to be processed
     * @return false (no handling implemented yet)
     * 
     * @see org.mt4j.components.interfaces.IMTComponent#processInputEvent(org.mt4j.input.inputData.MTInputEvent)
     */
    @Override
    public boolean processInputEvent(MTInputEvent inEvt) {

        log.debug("Entering processInputEvent()"); //$NON-NLS-1$

        log.debug("Process a new input event" + inEvt + " for this " //$NON-NLS-1$  //$NON-NLS-2$ 
                + this + ", target " + inEvt.getTarget() + "Phase: " //$NON-NLS-1$  //$NON-NLS-2$ 
                + inEvt.getEventPhase());

        // If we are not yet bubbling at the target is this RelationView
        // set phase AT_TARGET
        if (inEvt.getEventPhase() != MTInputEvent.BUBBLING_PHASE
                && inEvt.getTarget().equals(this) /* && inEvt.bubbles() */) {

            log.debug("Input event is at target!"); //$NON-NLS-1$ 
            inEvt.setEventPhase(MTInputEvent.AT_TARGET);
        }

        if (this.isEnabled()) {
            // THIS IS A HACK TO ALLOW Global GESTURE PROCESSORS to send
            // MTGEstureevents TO WORK
            if (inEvt instanceof MTGestureEvent) {
                log.debug("Input event is instance of MTGestureEvent"); //$NON-NLS-1$ 

                // Override: SAWI: Only process if the target is a RelationView

                if (!(inEvt.getTarget() instanceof RelationView)) {
                    this.processGestureEvent((MTGestureEvent) inEvt);
                }
            } else {
                log.debug("Input event is NOT an instance of MTGestureEvent"); //$NON-NLS-1$ 

                // Override: SAWI: Only process if the target is a RelationView!

                // Fire the same input event to all of this components' input
                // listeners
                if (!(inEvt.getTarget() instanceof RelationView)) {
                    this.dispatchInputEvent(inEvt);
                }
            }
        }

        // If we have bubbles, the propagation has not been stopped
        // and we are at the target component set phase BUBBLING_PHASE
        if (inEvt.getBubbles() && !inEvt.isPropagationStopped()
                && inEvt.getEventPhase() == MTInputEvent.AT_TARGET) {
            inEvt.setEventPhase(MTInputEvent.BUBBLING_PHASE);
            log.debug("Event is in bubbling phase"); //$NON-NLS-1$ 
        }

        // If we have a bubbled event, propagate to the parent component
        if (inEvt.getBubbles() && !inEvt.isPropagationStopped()
                && inEvt.getEventPhase() == MTInputEvent.BUBBLING_PHASE) {
            MTComponent theParent = this.getParent();

            // We only bubble up if the target was a RelationView
            // (for transforming by top node parent IdeaNodeView)
            // otherwise we stop propagation
            if (theParent != null
                    && (inEvt.getTarget() instanceof RelationView)) {
                log.debug("We have bubbles, so we redirect to the parent!"); //$NON-NLS-1$ 

                inEvt.setCurrentTarget(theParent);
                theParent.processInputEvent(inEvt);

            } else {
                log.debug("We have bubbles but the event is targeted at a child IdeaNodeView"); //$NON-NLS-1$ 

                inEvt.stopPropagation();
            }
        }
        log.debug("Leaving processInputEvent()"); //$NON-NLS-1$ 

        return false;
    }

    /**
     * <p>
     * Overridden method addChild() from MTComponent. We additionally send all
     * RVAdornmentsContainers to the front (should be only one) to ensure that
     * the arrow head is always on top of the child IdeaNodeView.
     * </p>
     * 
     * @param tangibleComp
     *            the child component to add
     * @see org.mt4j.components.MTComponent#addChild(org.mt4j.components.MTComponent)
     */
    @Override
    public void addChild(MTComponent tangibleComp) {

        log.debug("Entering addChild(tangibleComp=" + tangibleComp + ")"); //$NON-NLS-1$ //$NON-NLS-2$ 

        // Add child as usual
        super.addChild(tangibleComp);

        // Send all RVAdornmentsContainer children to front
        MTComponent[] children = this.getChildren();

        for (MTComponent child : children) {
            if (child instanceof RVAdornmentsContainer) {
                child.sendToFront();
            }
        }

        log.debug("Leaving addChild()"); //$NON-NLS-1$
    }

}
