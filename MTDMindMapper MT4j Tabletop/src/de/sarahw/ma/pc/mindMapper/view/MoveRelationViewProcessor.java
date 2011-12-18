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
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;

import processing.core.PApplet;

/**
 * <p>
 * The Class MoveRelationViewProcessor. For multi touch drag/move behaviour on
 * RelationView components. Fires DragEvent gesture events, but only if the
 * IdeaNodeView is a single IdeaNodeView or the topmost IdeaNodeView in a
 * IdeaNodeView/RelationView hierarchy.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class MoveRelationViewProcessor extends DragProcessor {

    private static Logger log = Logger.getLogger(MoveRelationViewProcessor.class);

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new MoveRelationViewProcessor.
     * 
     * @param graphicsContext
     *            the application instance
     * 
     */
    public MoveRelationViewProcessor(PApplet graphicsContext) {
        super(graphicsContext);

        log.debug("Executing MoveRelationViewProcessor(graphicsContext=" //$NON-NLS-1$
                + graphicsContext + ")"); //$NON-NLS-1$

    }

    /**
     * Constructor. Instantiates a new MoveRelationViewProcessor.
     * 
     * @param graphicsContext
     *            the application instance
     * @param stopEventPropagation
     *            flag to stop event propagation
     */
    public MoveRelationViewProcessor(PApplet graphicsContext,
            boolean stopEventPropagation) {
        super(graphicsContext, stopEventPropagation);

        log.debug("Executing MoveRelationViewProcessor(graphicsContext=" //$NON-NLS-1$
                + graphicsContext + ", stopEventPropagation=" //$NON-NLS-1$
                + stopEventPropagation + ")"); //$NON-NLS-1$

    }

    /* ********Overridden methods******** */
    /**
     * Checks whether the conditions for processing the input event are
     * fulfilled.
     * 
     * @param inputEvt
     *            the input event
     * @return true if the processor is interested in the input event
     */
    @Override
    public boolean isInterestedIn(MTInputEvent inputEvt) {

        log.debug("Entering isInterestedIn(inputEvt=" + inputEvt + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        log.debug("Leaving isInterestedIn(): " //$NON-NLS-1$
                + (inputEvt instanceof AbstractCursorInputEvt
                        && inputEvt.hasTarget() && (!(inputEvt.getTarget() instanceof RelationView) && (!(inputEvt
                        .getTarget() instanceof RVAdornmentsContainer)))));

        // The processor only processes the event if its target is not
        // a RelationView or a RVAdornmentsContainer in a RelationView
        // because we only move the RelationView if a child IdeaNodeView is
        // dragged/rotated etc.
        return inputEvt instanceof AbstractCursorInputEvt
                && inputEvt.hasTarget()
                && (!(inputEvt.getTarget() instanceof RelationView) && (!(inputEvt
                        .getTarget() instanceof RVAdornmentsContainer)));

    }

}
