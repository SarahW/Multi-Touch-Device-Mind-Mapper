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
import org.mt4j.components.TransformSpace;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.interfaces.IMTController;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.widgets.MTSvg;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.util.math.Vector3D;

/**
 * <p>
 * Inertia drag action performed on a application component (possible inertia
 * drag components that are no AbstractShapes must be specified in
 * checkPosition()). Modified version of Mt4j class InertiaDragAction that
 * additionally checks if the new position lies within the screen borders and
 * stops the inertia action/repositions the component if necessary.
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
public class InertiaDragActionCheckBorders implements IGestureEventListener {

    private static Logger         log = Logger.getLogger(InertiaDragActionCheckBorders.class);

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication mtApplication;

    /* *** Default action members *** */
    /** The limit. */
    private float                 limit;

    /** The damping. */
    private float                 damping;

    /** The integration time. */
    private int                   integrationTime;

    /** The drag target */
    private IMTComponent3D        target;

    /* *** Border limits *** */
    /** The minimum x position to drag a component to */
    private float                 minX;
    /** The maximum x position to drag a component to */
    private float                 minY;
    /** The minimum y position to drag a component to */
    private float                 maxX;
    /** The maximum y position to drag a component to */
    private float                 maxY;
    /** The border offset */
    private float                 borderOffset;

    /* ***********Constructors*********** */
    /**
     * Instantiates a new inertia drag action with a screen border check.
     * 
     * @param abstractMtApplication
     *            the application instance
     * @param borderOffset
     *            the desired offset from the border that marks the
     *            maximum/minimum position a component can be positioned at.
     *            Default 0.
     * 
     */
    public InertiaDragActionCheckBorders(
            AbstractMTApplication abstractMtApplication, float borderOffset) {
        this(125, 0.85f, 25, abstractMtApplication, borderOffset);

        log.debug("Executing InertiaDragActionCheckBorders(abstractMtApplication=" //$NON-NLS-1$ 
                + abstractMtApplication + ", borderOffset=" + borderOffset //$NON-NLS-1$  
                + ")"); //$NON-NLS-1$ 

    }

    /**
     * Instantiates a new inertia drag action with a screen border check.
     * 
     * @param integrationTime
     *            the integration time
     * @param damping
     *            the damping
     * @param maxVelocityLength
     *            the max velocity length
     * @param abstractMtApplication
     *            the application instance
     * @param borderOffset
     *            the desired offset from the border that marks the
     *            maximum/minimum position a component can be positioned at.
     *            Default 0.
     */
    public InertiaDragActionCheckBorders(int integrationTime, float damping,
            float maxVelocityLength,
            AbstractMTApplication abstractMtApplication, float borderOffset) {

        log.debug("Executing InertiaDragActionCheckBorders(integrationTime=" //$NON-NLS-1$
                + integrationTime + ", damping=" + damping //$NON-NLS-1$
                + ", maxVelocityLength=" + maxVelocityLength //$NON-NLS-1$
                + ", abstractMtApplication=" + abstractMtApplication //$NON-NLS-1$
                + ", borderOffset=" + borderOffset + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Set references
        this.integrationTime = integrationTime;
        this.limit = maxVelocityLength;
        this.damping = damping;

        this.mtApplication = abstractMtApplication;
        this.borderOffset = borderOffset;
    }

    /* ********Overridden methods******** */
    /**
     * Implemented method from IGestureEventListener. Adds a new
     * InertiaController to the component when the gesture has ended. Partly
     * adapted from MT4j class InertiaDragAction.
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
        this.target = gestureEvent.getTarget();
        if (this.target instanceof MTComponent) {

            // Get the current screen values
            getScreenValues();

            MTComponent comp = (MTComponent) this.target;
            DragEvent de = (DragEvent) gestureEvent;
            IMTController oldController;
            switch (de.getId()) {
                case MTGestureEvent.GESTURE_STARTED:
                    break;
                case MTGestureEvent.GESTURE_RESUMED:
                    break;
                case MTGestureEvent.GESTURE_UPDATED:
                    break;
                case MTGestureEvent.GESTURE_CANCELED:
                    break;
                case MTGestureEvent.GESTURE_ENDED:
                    Vector3D vel = de.getDragCursor().getVelocityVector(
                            this.integrationTime);
                    vel.scaleLocal(0.9f); // Test - integrate over longer time
                                          // but scale down velocity vec
                    vel = vel.getLimited(this.limit);
                    oldController = comp.getController();
                    comp.setController(new InertiaController(comp, vel,
                            oldController));
                    break;
                default:
                    break;
            }
        }
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
     * of the screen and then returns a new corrected position. Returns null if
     * no repositioning is necessary because the component is still within the
     * screen boundaries.
     * 
     * @return the new position vector if the component needs to be repositioned
     */
    private Vector3D getPositionAtBorderCrossing() {

        // Check if the center position is in a non visible part
        // of the screen

        // If the target is an abstractShape
        if (this.target instanceof AbstractShape) {

            AbstractShape dragShape = (AbstractShape) this.target;

            // Check whether we need a newPosition for the component
            // because we passed the screen border
            return ToolsScreen.alignPositionToBorders(
                    dragShape.getCenterPointGlobal(), this.minX, this.maxX,
                    this.minY, this.maxY);

            // If the target is no abstractShape but a child
            // component of a IdeaNodeView, AbstractCircularMenu or
            // AbstractOverlay
        } else if ((this.target instanceof MarkerContainer)
                || (this.target instanceof NodeContentContainer)
                || (this.target instanceof MTSvg)) {

            MTComponent component = (MTComponent) this.target;
            if ((component.getParent() instanceof IdeaNodeView)
                    || (component.getParent() instanceof AbstractCircularMenu)
                    || (component.getParent() instanceof AbstractOverlay)) {

                // Check whether we need a newPosition for the component
                // because we passed the screen border
                return ToolsScreen.alignPositionToBorders(
                        ((AbstractShape) component).getCenterPointGlobal(),
                        this.minX, this.maxX, this.minY, this.maxY);
            }
            return null;

        }
        return null;

    }

    /**
     * Checks if the center position of the component is at a non visible part
     * of the screen and repositions the component if necessary.
     * 
     */
    public void checkPositionAndSet() {

        // If the target is an abstractShape
        if (this.target instanceof AbstractShape) {

            AbstractShape dragShape = (AbstractShape) this.target;

            // Check whether we need a newPosition for the component
            // because we passed the screen border
            Vector3D newPosition = ToolsScreen.alignPositionToBorders(
                    dragShape.getCenterPointGlobal(), this.minX, this.maxX,
                    this.minY, this.maxY);

            if (newPosition != null) {

                // log.debug("Setting position" + newPosition); //$NON-NLS-1$
                // Reposition component
                dragShape.setPositionGlobal(newPosition);
            }

            // If the target is no abstractShape but a child
            // component of a IdeaNodeView, AbstractCircularMenu or
            // AbstractOverlay
        } else if ((this.target instanceof MarkerContainer)
                || (this.target instanceof NodeContentContainer)
                || (this.target instanceof MTSvg)) {

            MTComponent component = (MTComponent) this.target;
            if ((component.getParent() instanceof IdeaNodeView)
                    || (component.getParent() instanceof AbstractCircularMenu)
                    || (component.getParent() instanceof AbstractOverlay)) {

                // Check whether we need a newPosition for the component
                // because we passed the screen border
                Vector3D newPosition = ToolsScreen.alignPositionToBorders(
                        ((AbstractShape) component).getCenterPointGlobal(),
                        this.minX, this.maxX, this.minY, this.maxY);

                if (newPosition != null) {

                    //  log.debug("Setting position" + newPosition); //$NON-NLS-1$

                    // Reposition component
                    ((AbstractShape) component).setPositionGlobal(newPosition);
                }
            }

        }

    }

    /* **********Inner classes********** */
    /**
     * The Class InertiaController.
     * 
     */
    private class InertiaController implements IMTController {

        /** The target. */
        private MTComponent   target;

        /** The start velocity vec. */
        private Vector3D      startVelocityVec;

        /** The old controller. */
        private IMTController oldController;

        /** The animation time. */
        private int           animationTime        = 1000;

        /** The current animation time. */
        private int           currentAnimationTime = 0;

        /** The move per milli. */
        private float         movePerMilli;

        /** The move vect norm. */
        private Vector3D      moveVectNorm;

        /** The move vect. */
        private Vector3D      moveVect;

        /**
         * Instantiates a new inertia controller.
         * 
         * @param target
         *            the target
         * @param startVelocityVec
         *            the start velocity vec
         * @param oldController
         *            the old controller
         */
        public InertiaController(MTComponent target, Vector3D startVelocityVec,
                IMTController oldController) {
            super();
            this.target = target;
            this.startVelocityVec = startVelocityVec;
            this.oldController = oldController;

        }

        /**
         * Update method called automatically.
         * 
         * @see org.mt4j.components.interfaces.IMTController#update(long)
         */
        @SuppressWarnings("synthetic-access")
        @Override
        public void update(long timeDelta) {

            // If the velocity vector is small enough or the component
            // has reached the screen borders stop the inertia action
            if ((Math.abs(this.startVelocityVec.x) < 0.05f && Math
                    .abs(this.startVelocityVec.y) < 0.05f)
                    || InertiaDragActionCheckBorders.this
                            .getPositionAtBorderCrossing() != null) {

                this.startVelocityVec.setValues(Vector3D.ZERO_VECTOR);
                this.target.setController(this.oldController);

                // Check the position and reposition if necessary
                checkPositionAndSet();
                return;
            }
            this.startVelocityVec
                    .scaleLocal(InertiaDragActionCheckBorders.this.damping);

            Vector3D vec = new Vector3D(this.startVelocityVec);
            vec.transformDirectionVector(this.target.getGlobalInverseMatrix()); // Transform
            // direction
            // vector
            // into
            // component
            // local
            // coordinates
            this.target.translate(vec, TransformSpace.LOCAL);

            // target.translateGlobal(startVelocityVec);
            // */

            if (this.oldController != null) {
                this.oldController.update(timeDelta);
            }
        }
    }

}
