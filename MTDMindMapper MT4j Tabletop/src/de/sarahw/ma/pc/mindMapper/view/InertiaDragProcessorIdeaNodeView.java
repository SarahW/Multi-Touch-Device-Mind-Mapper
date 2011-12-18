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
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;

import processing.core.PApplet;

/**
 * <p>
 * The Class InertiaDragProcessorIdeaNodeView. For multi touch drag inertia
 * behaviour on IdeaNodeView components. Fires DragEvent gesture events, but
 * only if the IdeaNodeView is a single IdeaNodeView or the topmost IdeaNodeView
 * in a IdeaNodeView/RelationView hierarchy.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class InertiaDragProcessorIdeaNodeView extends DragProcessor {

    private static Logger log = Logger.getLogger(InertiaDragProcessorIdeaNodeView.class);

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new DragProcessorIdeaNodeView.
     * 
     * @param graphicsContext
     *            the application instance
     * 
     */
    public InertiaDragProcessorIdeaNodeView(PApplet graphicsContext) {
        super(graphicsContext);
        log.debug("Excecuting DragProcessorIdeaNodeView(graphicsContext=" //$NON-NLS-1$
                + graphicsContext + ")"); //$NON-NLS-1$

    }

    /**
     * Constructor. Instantiates a new DragProcessorIdeaNodeView.
     * 
     * @param graphicsContext
     *            the application instance
     * @param stopEventPropagation
     *            flag to stop event propagation
     */
    public InertiaDragProcessorIdeaNodeView(PApplet graphicsContext,
            boolean stopEventPropagation) {
        super(graphicsContext, stopEventPropagation);

        log.debug("Excecuting DragProcessorIdeaNodeView(graphicsContext=" //$NON-NLS-1$
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

        log.trace("Entering isInterestedIn(inputEvt=" + inputEvt + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Input Event needs to be a AbstractCursorInputEvt
        if (inputEvt instanceof AbstractCursorInputEvt) {
            if (inputEvt.hasTarget()) {
                IMTComponent3D target = inputEvt.getTarget();

                IMTComponent3D currentTarget = inputEvt.getCurrentTarget();

                log.trace("Current target of DragProcessorMod:" + currentTarget); //$NON-NLS-1$

                // The target and current target must be MTComponents
                if (target instanceof MTComponent
                        && currentTarget instanceof MTComponent) {

                    MTComponent comp = (MTComponent) target;
                    MTComponent curComp = (MTComponent) currentTarget;

                    // The current component must be an IdeaNodeView
                    if (curComp instanceof IdeaNodeView) {

                        if (curComp.getParent() != null) {

                            // If we are at the topmost IdeaNodeView in the
                            // hierarchy or the IdeaNodeView is a single one
                            // the processor shall process all dragging events
                            // including the ones that originated at a child
                            // element
                            // like a RelationView and got bubbled up
                            if (curComp.getParent() instanceof MTCanvas) {

                                log.trace("Leaving isInterestedIn(): true"); //$NON-NLS-1$
                                return true;

                                // If we are not at the topmost IdeaNodeView or
                                // at a single IdeaNodeView the
                                // processor is only interested in events
                                // that originated from an IdeaNodeView, an
                                // NodeContentContainer or a MarkerContainer
                                // part of a single or topmost ideaNode
                            } else if ((comp instanceof NodeContentContainer)
                                    || (comp instanceof MarkerContainer)) {

                                if (comp.getParent() instanceof IdeaNodeView) {
                                    IdeaNodeView ideaNodeViewParent = (IdeaNodeView) comp
                                            .getParent();

                                    if (ideaNodeViewParent.getParent() instanceof MTCanvas) {
                                        log.trace("Leaving isInterestedIn(): true"); //$NON-NLS-1$
                                        return true;
                                    }
                                    log.trace("Leaving isInterestedIn(): false"); //$NON-NLS-1$
                                    return false;

                                }
                                log.trace("Leaving isInterestedIn(): false"); //$NON-NLS-1$
                                return false;

                            }
                            log.trace("Leaving isInterestedIn(): false"); //$NON-NLS-1$
                            return false;
                        }
                        log.trace("Leaving isInterestedIn(): false"); //$NON-NLS-1$
                        return false;
                    }
                    log.trace("Leaving isInterestedIn(): false"); //$NON-NLS-1$
                    return false;

                }
                log.trace("Leaving isInterestedIn(): false"); //$NON-NLS-1$
                return false;
            }
            log.trace("Leaving isInterestedIn(): false"); //$NON-NLS-1$
            return false;

        }
        log.trace("Leaving isInterestedIn(): false"); //$NON-NLS-1$
        return false;

    }

}
