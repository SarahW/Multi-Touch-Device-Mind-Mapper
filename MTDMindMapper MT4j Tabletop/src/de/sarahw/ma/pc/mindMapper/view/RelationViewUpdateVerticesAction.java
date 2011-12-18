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
import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.gestureAction.ICollisionAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;

/**
 * <p>
 * Action performed on RelationViews on dragging a child IdeaNodeView of a
 * parent RelationView. Updates the RelationView vertices.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class RelationViewUpdateVerticesAction implements IGestureEventListener,
        ICollisionAction {

    private static Logger  log            = Logger.getLogger(RelationViewUpdateVerticesAction.class);

    /* *** Default action members *** */
    /** The drag target. */
    private IMTComponent3D target;

    /** The use custom target. */
    private boolean        useCustomTarget;

    /** The gesture aborted. */
    private boolean        gestureAborted = false;

    /** The last event. */
    private MTGestureEvent lastEvent;

    /* ***********Constructors*********** */
    /**
     * Instantiates a new default RelationViewUpdateVerticesAction.
     */
    public RelationViewUpdateVerticesAction() {

        log.debug("Executing RelationViewUpdateVerticesAction()"); //$NON-NLS-1$

        // Set custom target false
        this.useCustomTarget = false;
    }

    /**
     * Instantiates a new default RelationViewUpdateVerticesAction with a custom
     * target.
     * 
     * @param target
     *            the custom drag target
     */
    public RelationViewUpdateVerticesAction(IMTComponent3D target) {

        log.debug("Executing RelationViewUpdateVerticesAction(target=" + target //$NON-NLS-1$
                + ")"); //$NON-NLS-1$

        // Set references and custom target true
        this.target = target;
        this.useCustomTarget = true;
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
     * Implemented method from IGestureEventListener. Updates the RelationView
     * vertices if dragging events from a child IdeaNodeView are processed.
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

        log.trace("Entering processGestureEvent(gestureEvent=" + gestureEvent //$NON-NLS-1$
                + ")"); //$NON-NLS-1$

        if (gestureEvent instanceof DragEvent) {

            log.trace("Gesture event is a drag event"); //$NON-NLS-1$
            DragEvent dragEvent = (DragEvent) gestureEvent;
            this.lastEvent = dragEvent;

            if (!this.useCustomTarget) {
                this.target = dragEvent.getTarget();
            }

            switch (dragEvent.getId()) {
                case MTGestureEvent.GESTURE_STARTED:
                case MTGestureEvent.GESTURE_RESUMED:
                    // Put target on top -> draw on top of others
                    if (this.target instanceof MTComponent) {
                        MTComponent baseComp = (MTComponent) this.target;

                        baseComp.sendToFront();

                    }

                    // Recalculate the relation view vertices
                    recalcRelationViewVertices(this.target);

                    break;
                case MTGestureEvent.GESTURE_UPDATED:

                    // Recalculate the relation view vertices
                    recalcRelationViewVertices(this.target);

                    break;
                case MTGestureEvent.GESTURE_CANCELED:

                    // Recalculate the relation view vertices
                    recalcRelationViewVertices(this.target);
                    break;
                case MTGestureEvent.GESTURE_ENDED:

                    // Recalculate the relation view vertices
                    recalcRelationViewVertices(this.target);
                    break;
                default:
                    break;
            }
        }
        log.trace("Leaving processGestureEvent(): false"); //$NON-NLS-1$
        return false;
    }

    /* *********Object methods********* */
    /**
     * Recalculates the target's shape vertices if it is a RelationView.
     * 
     * @param target
     *            the target component
     */
    private void recalcRelationViewVertices(IMTComponent3D target) {

        log.trace("Entering recalcRelationViewVertices(target=" + target + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (target instanceof RelationView) {

            log.trace("The target is a RelationView"); //$NON-NLS-1$

            RelationView relView = (RelationView) target;

            // Get the child IdeaNodeView
            IdeaNodeView ideaNodeView = relView.getChildIdeaNodeView();

            if (ideaNodeView != null) {

                // Check if we even have a child IdeaNodeView
                if (relView.getChildIndexOf(ideaNodeView) != -1) {

                    // Recalculate the touch points
                    relView.recalculateTouchPoints();
                }

            }
        } else {
            log.error("The target is not a RelationView!"); //$NON-NLS-1$
        }
        log.trace("Leaving recalcRelationViewVertices()"); //$NON-NLS-1$

    }

}
