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
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.widgets.MTSvg;
import org.mt4j.input.gestureAction.DefaultRotateAction;
import org.mt4j.input.gestureAction.ICollisionAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateEvent;
import org.mt4j.util.math.Vector3D;

/**
 * <p>
 * Action performed on an IdeaNodeView and its parent RelationView on rotating.
 * Rotates the IdeaNodeView and updates the IdeaNodeView parent's RelationView
 * vertices. Modified version of Mt4j class DefaultRotateAction.
 * </p>
 * 
 * <p>
 * Modified 2011-08
 * </p>
 * 
 * @author Christopher Ruff
 * @author (Modified by) Sarah Will
 * @version 1.0
 * 
 * @see org.mt4j.input.gestureAction.DefaultRotateAction
 * 
 */
public class RotateIdeaNodeViewAction implements IGestureEventListener,
        ICollisionAction {

    private static Logger         log            = Logger.getLogger(RotateIdeaNodeViewAction.class);

    /* *** RotateIdeaNodeViewAction constants *** */
    /**
     * The border offset added to the min/max position that a component can be
     * translated to
     */
    private static final float    BORDER_OFFSET  = 0;

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication mtApplication;

    /* *** Default action members *** */
    /** The target. */
    private IMTComponent3D        target;

    /** The use custom target. */
    private boolean               useCustomTarget;

    /** The last event. */
    private MTGestureEvent        lastEvent;

    /** The gesture aborted. */
    private boolean               gestureAborted = false;

    /* *** Border limits *** */
    /** The minimum x position to drag a component to */
    private float                 minX;
    /** The maximum x position to drag a component to */
    private float                 minY;
    /** The minimum y position to drag a component to */
    private float                 maxX;
    /** The maximum y position to drag a component to */
    private float                 maxY;

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new default rotate action.
     * 
     * @param abstractMtApplication
     *            the application instance
     */
    public RotateIdeaNodeViewAction(AbstractMTApplication abstractMtApplication) {

        log.debug("Executing RotateIdeaNodeViewAction(abstractMtApplication=" //$NON-NLS-1$
                + abstractMtApplication + ")"); //$NON-NLS-1$

        // Set custom target false
        this.useCustomTarget = false;
        this.mtApplication = abstractMtApplication;
    }

    /**
     * Constructor. Instantiates a new default rotate action.
     * 
     * @param customTarget
     *            the custom target
     * @param abstractMtApplication
     *            the application instance
     */
    public RotateIdeaNodeViewAction(IMTComponent3D customTarget,
            AbstractMTApplication abstractMtApplication) {

        log.debug("Executing RotateIdeaNodeViewAction(customTarget=" + customTarget //$NON-NLS-1$
                + ", abstractMtApplication=" + abstractMtApplication + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Set references and custom target true
        this.target = customTarget;
        this.useCustomTarget = true;
        this.mtApplication = abstractMtApplication;
    }

    /* ********Getters & Setters******** */
    /**
     * Gets the gesture aborted state.
     * 
     * @return the gestured aborted state
     * @see ICollisionAction#gestureAborted()
     */
    @Override
    public boolean gestureAborted() {
        return this.gestureAborted;
    }

    /**
     * Sets the gesture aborted state.
     * 
     * @param aborted
     *            the aborted flag
     * @see ICollisionAction#setGestureAborted(boolean)
     */
    @Override
    public void setGestureAborted(boolean aborted) {
        this.gestureAborted = aborted;
    }

    /**
     * Gets the last gesture event.
     * 
     * @return the last gesture event
     * @see ICollisionAction#getLastEvent()
     */
    @Override
    public MTGestureEvent getLastEvent() {
        return this.lastEvent;
    }

    /* ********Overridden methods******** */
    /**
     * Implemented method from IGestureEventListener. Updates the parent
     * RelationView vertices if rotating events from a child IdeaNodeView are
     * processed. Partly adapted from MT4j class DefaultRotateAction
     * 
     * @param gestureEvent
     *            the gesture event
     * @return true if handled (not used yet!)
     * 
     * @see org.mt4j.input.inputProcessors.IGestureEventListener#processGestureEvent
     *      (org.mt4j.input.inputProcessors.MTGestureEvent)
     * 
     * @see org.mt4j.input.gestureAction.DefaultRotateAction
     */
    @Override
    public boolean processGestureEvent(MTGestureEvent gestureEvent) {

        log.trace("Entering processGestureEvent(gestureEvent=" + gestureEvent //$NON-NLS-1$
                + ")"); //$NON-NLS-1$

        if (gestureEvent instanceof RotateEvent) {
            RotateEvent rotateEvent = (RotateEvent) gestureEvent;
            this.lastEvent = rotateEvent;

            getScreenValues();

            if (!this.useCustomTarget) {
                this.target = rotateEvent.getTarget();
            }

            switch (rotateEvent.getId()) {
                case MTGestureEvent.GESTURE_STARTED:
                case MTGestureEvent.GESTURE_RESUMED:

                    // Send to front
                    if (this.target instanceof MTComponent) {
                        ((MTComponent) this.target).sendToFront();

                    }

                    break;
                case MTGestureEvent.GESTURE_UPDATED:
                    // Rotate
                    doAction(this.target, rotateEvent);

                    // Recalculate parent relation view vertices
                    recalcParentRelationViewVertices(this.target);

                    break;
                case MTGestureEvent.GESTURE_CANCELED:
                case MTGestureEvent.GESTURE_ENDED:

                    // Recalculate parent relation view vertices
                    recalcParentRelationViewVertices(this.target);

                    // Check position and reposition if required
                    checkPosition();

                    break;
                default:
                    break;
            }
        }
        log.trace("Leaving processGestureEvent(): false"); //$NON-NLS-1$
        return false;
    }

    /**
     * Gets the current values for the minimum and maximum x and y coordinates a
     * component can be positioned at.
     */
    private void getScreenValues() {

        log.trace("Entering getScreenValues()"); //$NON-NLS-1$
        // Get the min and max coordinates

        int screenWidth = this.mtApplication.getWidth();
        int screenHeight = this.mtApplication.getHeight();

        // log.debug("ScreenWidth is: " + screenWidth);
        // log.debug("ScreenHeight is: " + screenHeight);

        this.minX = 0 + BORDER_OFFSET;
        // log.debug("MinX is: " + this.minX);
        this.minY = 0 + BORDER_OFFSET;
        // log.debug("MinY is: " + this.minY);
        this.maxX = screenWidth - BORDER_OFFSET;
        // log.debug("MaxX is: " + this.maxX);
        this.maxY = screenHeight - BORDER_OFFSET;
        // log.debug("MaxY is: " + this.maxY);

        log.trace("Leaving getScreenValues()"); //$NON-NLS-1$

    }

    /**
     * Rotates the component and translates it. Adapted from Mt4j class
     * DefaultRotateAction.
     * 
     * @param comp
     *            the target component
     * @param re
     *            the rotate event
     * @see DefaultRotateAction
     */
    protected void doAction(IMTComponent3D comp, RotateEvent re) {

        log.trace("Entering doAction(comp=" + comp + ", re=" + re + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        if (!gestureAborted()) {
            comp.rotateZGlobal(re.getRotationPoint(), re.getRotationDegrees());
            if (comp.isGestureAllowed(DragProcessor.class)) {
                comp.translateGlobal(re.getTranslationVector());

                // Check position and reposition if required
                checkPosition();
            }

        }

        log.trace("Leaving doAction(comp=" + comp + ", re=" + re + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    }

    /**
     * Checks if the center position of the component is at a non visible part
     * of the screen and reposition the component if necessary.
     */
    private void checkPosition() {
        // Check if the center position is in a non visible part
        // of the screen
        if (this.target instanceof AbstractShape) {

            AbstractShape dragShape = (AbstractShape) this.target;

            Vector3D newPosition = ToolsScreen.alignPositionToBorders(
                    dragShape.getCenterPointGlobal(), this.minX, this.maxX,
                    this.minY, this.maxY);

            if (newPosition != null) {

                dragShape.setPositionGlobal(newPosition);
            }

        } else if ((this.target instanceof MarkerContainer)
                || (this.target instanceof NodeContentContainer)
                || (this.target instanceof MTSvg)) {

            MTComponent component = (MTComponent) this.target;

            if (component.getParent() instanceof IdeaNodeView) {

                Vector3D newPosition = ToolsScreen.alignPositionToBorders(
                        ((AbstractShape) component).getCenterPointGlobal(),
                        this.minX, this.maxX, this.minY, this.maxY);

                if (newPosition != null) {

                    ((AbstractShape) component).setPositionGlobal(newPosition);
                }
            }

        }

    }

    /**
     * Recalculates the target's parent shape vertices if it is a RelationView.
     * 
     * @param target
     *            the target component
     */
    private void recalcParentRelationViewVertices(IMTComponent3D target) {

        log.trace("Entering recalcParentRelationViewVertices(target=" + target + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Check if target is an IdeaNodeView
        if (target instanceof IdeaNodeView) {

            IdeaNodeView ideaNodeView = (IdeaNodeView) target;

            // Check if the IdeaNodeView has a RelationView parent
            if (ideaNodeView.getParent() instanceof RelationView) {

                RelationView parentRelationView = (RelationView) ideaNodeView
                        .getParent();

                // Update relationView touch points
                parentRelationView.recalculateTouchPoints();

            } else {
                log.trace("IdeaNodeView parent is not a RelationView, but" //$NON-NLS-1$
                        + ideaNodeView.getParent().getClass());
            }
        } else {
            log.trace("Target is not instanceof IdeaNodeView, but" + target.getClass()); //$NON-NLS-1$
        }

    }

}
