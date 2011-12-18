/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009, C.Ruff, Fraunhofer-Gesellschaft All rights
 * reserved.
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
import org.mt4j.input.gestureAction.ICollisionAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateEvent;
import org.mt4j.util.math.Vector3D;

/**
 * The Class DefaultRotateAction.
 * 
 * @author Christopher Ruff
 */
public class RotateActionCheckBorders implements IGestureEventListener,
        ICollisionAction {

    private static Logger         log            = Logger.getLogger(RotateActionCheckBorders.class);

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
    private float                 maxY;
    /** The maximum y position to drag a component to */
    private float                 maxX;
    /**
     * The border offset added to the min/max position that a component can be
     * translated to
     */
    private float                 borderOffset;

    /**
     * Instantiates a new rotate action that checks the borders.
     * 
     * @param abstractMtApplication
     *            the application instance
     * @param borderOffset
     *            the desired offset from the border that marks the
     *            maximum/minimum position a component can be positioned at.
     *            Default 0
     */
    public RotateActionCheckBorders(
            AbstractMTApplication abstractMtApplication, float borderOffset) {

        log.debug("Executing RotateActionCheckBorders(abstractMtApplication=" //$NON-NLS-1$ 
                + abstractMtApplication + ", borderOffset=" + borderOffset //$NON-NLS-1$  
                + ")"); //$NON-NLS-1$ 

        // Set references
        this.useCustomTarget = false;

        this.mtApplication = abstractMtApplication;
        this.borderOffset = borderOffset;
    }

    /**
     * Instantiates a new rotate action that checks the borders.
     * 
     * @param customTarget
     *            the custom target
     * 
     * @param abstractMtApplication
     *            the application instance
     * @param borderOffset
     *            the desired offset from the border that marks the
     *            maximum/minimum position a component can be positioned at.
     *            Default 0
     */
    public RotateActionCheckBorders(IMTComponent3D customTarget,
            AbstractMTApplication abstractMtApplication, float borderOffset) {

        log.debug("Executing RotateActionCheckBorders(dragTarget=" + customTarget //$NON-NLS-1$ 
                + ", abstractMtApplication=" + abstractMtApplication //$NON-NLS-1$ 
                + ", borderOffset=" + borderOffset + ")"); //$NON-NLS-1$  //$NON-NLS-2$

        // Set references
        this.target = customTarget;
        this.useCustomTarget = true;

        this.mtApplication = abstractMtApplication;
        this.borderOffset = borderOffset;
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
     * Implemented method from IGestureEventListener. Rotates the component in
     * by the given degrees and translates it in the given direction if
     * applicable, checks whether the screen borders have been passed yet by the
     * components center and repositions the component if required. Partly
     * adapted from MT4j class DefaultRotateAction.
     * 
     * @param gestureEvent
     *            the gesture event
     * @return true if handled (not used yet!)
     * 
     * @see org.mt4j.input.inputProcessors.IGestureEventListener#processGestureEvent
     *      (org.mt4j.input.inputProcessors.MTGestureEvent)
     */
    @Override
    public boolean processGestureEvent(MTGestureEvent gestureEvent) {
        if (gestureEvent instanceof RotateEvent) {
            RotateEvent rotateEvent = (RotateEvent) gestureEvent;
            this.lastEvent = rotateEvent;

            // Get the current screen values
            getScreenValues();

            if (!this.useCustomTarget) {
                this.target = rotateEvent.getTarget();
            }

            switch (rotateEvent.getId()) {
                case MTGestureEvent.GESTURE_STARTED:
                case MTGestureEvent.GESTURE_RESUMED:
                    if (this.target instanceof MTComponent) {
                        ((MTComponent) this.target).sendToFront();

                    }
                    break;
                case MTGestureEvent.GESTURE_UPDATED:
                    // Rotate (& translate)
                    doAction(this.target, rotateEvent);
                    break;
                case MTGestureEvent.GESTURE_CANCELED:
                case MTGestureEvent.GESTURE_ENDED:
                    // Check position and reposition if required
                    checkPosition();
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    /**
     * Rotates the component, translates it if necessary and checks the final
     * position.
     * 
     * @param comp
     *            the component
     * @param re
     *            the rotate event
     */
    protected void doAction(IMTComponent3D comp, RotateEvent re) {
        if (!gestureAborted()) {
            comp.rotateZGlobal(re.getRotationPoint(), re.getRotationDegrees());
            if (comp.isGestureAllowed(DragProcessor.class)) {
                comp.translateGlobal(re.getTranslationVector());

                // Check position and reposition if required
                checkPosition();
            }
        }
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

        this.minX = 0 + this.borderOffset;
        // log.debug("MinX is: " + this.minX);
        this.minY = 0 + this.borderOffset;
        // log.debug("MinY is: " + this.minY);
        this.maxX = screenWidth - this.borderOffset;
        // log.debug("MaxX is: " + this.maxX);
        this.maxY = screenHeight - this.borderOffset;
        // log.debug("MaxY is: " + this.maxY);

        log.trace("Leaving getScreenValues()"); //$NON-NLS-1$

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
            if ((component.getParent() instanceof IdeaNodeView)
                    || (component.getParent() instanceof AbstractCircularMenu)
                    || (component.getParent() instanceof AbstractOverlay)) {

                Vector3D newPosition = ToolsScreen.alignPositionToBorders(
                        ((AbstractShape) component).getCenterPointGlobal(),
                        this.minX, this.maxX, this.minY, this.maxY);

                if (newPosition != null) {

                    ((AbstractShape) component).setPositionGlobal(newPosition);
                }
            }

        }

    }

}
