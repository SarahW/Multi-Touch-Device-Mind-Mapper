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
import org.mt4j.components.interfaces.IMTComponent;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.MTGestureEvent;

import processing.core.PApplet;

/**
 * <p>
 * (Composite) container class for a MTKeyboard that is not supposed to process
 * input events up the hierarchy to a parent that accepts bubbled up events,
 * e.g. a Keyboard that is added to an IdeaNodeView.
 * </p>
 * 
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */
public class KeyboardContainer extends MTComponent {

    private static Logger log = Logger.getLogger(KeyboardContainer.class);

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new KeyboardContainer.
     * 
     * 
     * @param pApplet
     *            the application instance
     */
    public KeyboardContainer(PApplet pApplet) {
        super(pApplet);

        // Initialize KeyboardContainer
        initialize();

    }

    /* **********Object methods********** */
    /**
     * Initializes the KeyboardContainer.
     * 
     */
    private void initialize() {
        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Do nothing

        log.debug("Leaving initialize()"); //$NON-NLS-1$

    }

    /* ********Overridden methods******** */
    /**
     * <p>
     * Overridden method processInputEvent from MTComponent. Default event
     * processing is not allowed for this component as it figures as an
     * "event blocker" between the MTKeyboard and a parent component.
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
     * @see IMTComponent#processInputEvent(MTInputEvent)
     * 
     * 
     */
    @Override
    public boolean processInputEvent(MTInputEvent inEvt) {
        log.trace("Entering processInputEvent()"); //$NON-NLS-1$

        log.trace("Process a new input event" + inEvt //$NON-NLS-1$ 
                + " for this " + this + ", target " //$NON-NLS-1$  //$NON-NLS-2$ 
                + inEvt.getTarget() + "Phase: " //$NON-NLS-1$ 
                + inEvt.getEventPhase());

        // If we are not yet bubbling at the target is this IdeaNodeView
        // set phase AT_TARGET
        if (inEvt.getEventPhase() != MTInputEvent.BUBBLING_PHASE
                && inEvt.getTarget().equals(this) /* && inEvt.bubbles() */) {

            log.trace("Input event is at target!"); //$NON-NLS-1$ 
            inEvt.setEventPhase(MTInputEvent.AT_TARGET);
        }

        // MTComponent theParent = this.getParent();

        if (this.isEnabled()) {
            // THIS IS A HACK TO ALLOW Global GESTURE PROCESSORS to send
            // MTGEstureevents TO WORK
            // see overridden method in superclass
            if (inEvt instanceof MTGestureEvent) {
                log.trace("Input event is instance of MTGestureEvent"); //$NON-NLS-1$ 

                // Modification: No event processing!
                // this.processGestureEvent((MTGestureEvent) inEvt);

            } else {
                log.trace("Input event is NOT an instance of MTGestureEvent"); //$NON-NLS-1$ 

                // Modification: No event processing!
                // this.dispatchInputEvent(inEvt);

            }
        }

        // If we have bubbles, the propagation has not been stopped
        // and we are at the target component set phase BUBBLING_PHASE
        if (inEvt.getBubbles() && !inEvt.isPropagationStopped()
                && inEvt.getEventPhase() == MTInputEvent.AT_TARGET) {
            inEvt.setEventPhase(MTInputEvent.BUBBLING_PHASE);
            log.trace("Event is in bubbling phase"); //$NON-NLS-1$ 
        }

        // If we have a bubbled event, propagate to the parent component
        if (inEvt.getBubbles() && !inEvt.isPropagationStopped()
                && inEvt.getEventPhase() == MTInputEvent.BUBBLING_PHASE) {
            log.trace("We have bubbles, but we don't redirect to the parent!"); //$NON-NLS-1$ 

            // Modification: No bubbling!
            // inEvt.setCurrentTarget(theParent);
            // theParent.processInputEvent(inEvt);

        }
        log.trace("Leaving processInputEvent()"); //$NON-NLS-1$ 

        return false;
    }

}
