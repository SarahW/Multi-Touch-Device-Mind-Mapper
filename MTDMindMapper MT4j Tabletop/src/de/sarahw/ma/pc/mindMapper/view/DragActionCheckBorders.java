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
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTSvg;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.gestureAction.ICollisionAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.util.math.Vector3D;

/**
 * <p>
 * Drag action performed on a application component (possible drag components
 * that are no AbstractShapes must be specified in checkPosition()). Modified
 * version of Mt4j class DefaultRotateAction that additionally checks if the new
 * position lies within the screen borders and repositions the component if
 * necessary.
 * </p>
 * 
 * <p>
 * Modified 2011-10-05
 * </p>
 * 
 * @author Christopher Ruff
 * @author (Modified by) Sarah Will
 * @version 1.0
 * 
 * @see org.mt4j.input.gestureAction.DefaultRotateAction
 * 
 */
public class DragActionCheckBorders implements IGestureEventListener,
        ICollisionAction {

    private static Logger         log            = Logger.getLogger(DragActionCheckBorders.class);

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication mtApplication;

    /* *** Default action members *** */
    /** The drag target. */
    private IMTComponent3D        dragTarget;

    /** The use custom target. */
    private boolean               useCustomTarget;

    /** The gesture aborted. */
    private boolean               gestureAborted = false;

    /** The last event. */
    private MTGestureEvent        lastEvent;

    /* *** Border limits *** */
    /** The minimum x position to drag a component to */
    private float                 minX           = -10;
    /** The maximum x position to drag a component to */
    private float                 maxX           = -10;
    /** The minimum y position to drag a component to */
    private float                 minY           = -10;
    /** The maximum y position to drag a component to */
    private float                 maxY           = -10;
    /**
     * The border offset added to the min/max position that a component can be
     * translated to
     */
    private float                 borderOffset;

    /* ***********Constructors*********** */
    /**
     * Instantiates a new drag DragActionCheckBorders.
     * 
     * @param abstractMtApplication
     *            the application instance
     * @param borderOffset
     *            the desired offset from the border that marks the
     *            maximum/minimum position a component can be positioned at.
     *            Default 0.
     */
    public DragActionCheckBorders(AbstractMTApplication abstractMtApplication,
            float borderOffset) {

        log.debug("Executing DragActionCheckBorders(abstractMtApplication=" //$NON-NLS-1$ 
                + abstractMtApplication + ", borderOffset=" + borderOffset //$NON-NLS-1$  
                + ")"); //$NON-NLS-1$ 

        // Set references
        this.useCustomTarget = false;
        this.mtApplication = abstractMtApplication;
        this.borderOffset = borderOffset;
    }

    /**
     * Instantiates a new DragActionCheckBorders.
     * 
     * @param dragTarget
     *            the drag target
     * @param abstractMtApplication
     *            the application instance
     * @param borderOffset
     *            the desired offset from the border that marks the
     *            maximum/minimum position a component can be positioned at.
     *            Default 0.
     */
    public DragActionCheckBorders(IMTComponent3D dragTarget,
            AbstractMTApplication abstractMtApplication, float borderOffset) {

        log.debug("Executing DragActionCheckBorders(dragTarget=" + dragTarget //$NON-NLS-1$ 
                + ", abstractMtApplication=" + abstractMtApplication //$NON-NLS-1$ 
                + ", borderOffset=" + borderOffset + ")"); //$NON-NLS-1$  //$NON-NLS-2$

        // Set references
        this.dragTarget = dragTarget;
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
     * Implemented method from IGestureEventListener. Translates the component
     * in the given direction, checks whether the screen borders have been
     * passed yet by the components center and repositions the component if
     * required. Partly adapted from MT4j class DefaultDragAction.
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
        if (gestureEvent instanceof DragEvent) {
            DragEvent dragEvent = (DragEvent) gestureEvent;
            this.lastEvent = dragEvent;

            if (!this.useCustomTarget) {
                this.dragTarget = dragEvent.getTarget();
            }

            switch (dragEvent.getId()) {
                case MTGestureEvent.GESTURE_STARTED:
                case MTGestureEvent.GESTURE_RESUMED:

                    // Get the current screen values
                    getScreenValues();

                    // Put target on top -> draw on top of others
                    if (this.dragTarget instanceof MTComponent) {

                        MTComponent baseComp = (MTComponent) this.dragTarget;

                        baseComp.sendToFront();

                    }

                    // Translate target
                    translate(this.dragTarget, dragEvent);
                    break;
                case MTGestureEvent.GESTURE_UPDATED:
                    // Translate target
                    translate(this.dragTarget, dragEvent);
                    break;
                case MTGestureEvent.GESTURE_CANCELED:
                    // Check position and reset if required
                    checkPositionAndReset();
                    break;
                case MTGestureEvent.GESTURE_ENDED:
                    // Check position and reset if required
                    checkPositionAndReset();
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    /**
     * Translates the component in the direction stored in the drag event.
     * 
     * @param comp
     *            the component to be translated
     * @param de
     *            the drag event
     */
    protected void translate(IMTComponent3D comp, DragEvent de) {
        if (!this.gestureAborted) {

            // Translate first
            comp.translateGlobal(de.getTranslationVect());

            // Then check position and reset if required
            checkPositionAndReset();

        }
    }

    /**
     * Gets the current values for the minimum and maximum x and y coordinates a
     * component can be positioned at.
     * 
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
     * 
     */
    private void checkPositionAndReset() {
        // Check if the center position is in a non visible part
        // of the screen

        // If the target is an abstractShape
        if (this.dragTarget instanceof AbstractShape
                && !(this.dragTarget.getName()
                        .equals(OverlayListBluetooth.QR_CODE_NAME))) {

            AbstractShape dragShape = (AbstractShape) this.dragTarget;

            // Check whether we need a newPosition for the component
            // because we passed the screen border
            Vector3D newPosition = ToolsScreen.alignPositionToBorders(
                    dragShape.getCenterPointGlobal(), this.minX, this.maxX,
                    this.minY, this.maxY);

            if (newPosition != null) {

                // Reposition component
                dragShape.setPositionGlobal(newPosition);
            }

            // If the target is no abstractShape but a child
            // component of a IdeaNodeView, AbstractCircularMenu or
            // AbstractOverlay
        } else if ((this.dragTarget instanceof MarkerContainer)
                || (this.dragTarget instanceof NodeContentContainer)
                || (this.dragTarget instanceof MTSvg)
                || ((this.dragTarget instanceof MTRectangle) && ((this.dragTarget
                        .getName().equals(OverlayListBluetooth.QR_CODE_NAME))))
                || (this.dragTarget instanceof MTTextArea)) {

            MTComponent component = (MTComponent) this.dragTarget;
            if ((component.getParent() instanceof IdeaNodeView)
                    || (component.getParent() instanceof AbstractCircularMenu)
                    || (component.getParent() instanceof AbstractOverlay)) {

                // Check whether we need a newPosition for the component
                // because we passed the screen border
                Vector3D newPosition = ToolsScreen.alignPositionToBorders(
                        ((AbstractShape) component).getCenterPointGlobal(),
                        this.minX, this.maxX, this.minY, this.maxY);

                if (newPosition != null) {

                    // Reposition component
                    ((AbstractShape) component).setPositionGlobal(newPosition);
                }
            }

        }

    }
}
