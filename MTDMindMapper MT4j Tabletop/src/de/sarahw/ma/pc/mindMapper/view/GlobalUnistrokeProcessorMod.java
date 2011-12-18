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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mt4j.components.MTCanvas;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeContext;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeEvent;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.Direction;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.Recognizer;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.UnistrokeGesture;
import org.mt4j.input.inputProcessors.globalProcessors.AbstractGlobalInputProcessor;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * <p>
 * This is a modification of the Mt4j framework class GlobalUnistrokeProcessor.
 * This version only processes input events that are targeted at the canvas,
 * which means, that by moving another component (i.e. a IdeaNodeView) the
 * canvas won't process the movement as a Unistroke gesture.
 * </p>
 * 
 * <p>
 * TODO: Kinda ugly, as this does not conform with the "Global" characteristics
 * of the InputProcessor, ideally we'd have a AbstractComponentProcessor /
 * UnistrokeProcessor for the canvas that allows multiple Unistroke Gestures at
 * once! But for now, this works, too.
 * </p>
 * 
 * <p>
 * Modified 2011-09
 * </p>
 * 
 * @author Christopher Ruff
 * @author (Modified by) Sarah Will
 * 
 * @see org.mt4j.input.inputProcessors.globalProcessors.GlobalUnistrokeProcessor
 * 
 */
public class GlobalUnistrokeProcessorMod extends AbstractGlobalInputProcessor {

    protected static Logger                    log = Logger.getLogger(GlobalUnistrokeProcessorMod.class);

    /* *** Application *** */
    /** The multitouch application instance */
    private PApplet                            pApplet;
    /** The mind map scene canvas */
    private MTCanvas                           canvas;

    /** The plane normal */
    private Vector3D                           planeNormal;
    /** A point in the plane */
    private Vector3D                           pointInPlane;
    /** The recognizer */
    private Recognizer                         recognizer;
    /** The UnistrokeUtils instance */
    private UnistrokeUtils                     unistrokeUtils;

    /** The cursor/unistroke context map */
    private Map<InputCursor, UnistrokeContext> cursorToContext;

    /**
     * Constructor. Instantiates a new GlobalUnistrokeProcessorMod.
     * 
     * @param pApplet
     *            the application instance
     * @param canvas
     *            the canvas of the current scene
     */
    public GlobalUnistrokeProcessorMod(PApplet pApplet, MTCanvas canvas) {

        log.debug("Executing GlobalUnistrokeProcessorMod(pApplet=" + pApplet //$NON-NLS-1$
                + ", canvas=" + canvas + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Set references
        this.pApplet = pApplet;
        this.canvas = canvas;

        // Test - Calculate the normal of the plane we will be dragging at
        // (useful if camera isn't default)
        this.planeNormal = canvas.getViewingCamera().getPosition()
                .getSubtracted(canvas.getViewingCamera().getViewCenterPos())
                .normalizeLocal();
        this.pointInPlane = canvas.getViewingCamera().getViewCenterPos();
        this.unistrokeUtils = new UnistrokeUtils();
        this.recognizer = this.unistrokeUtils.getRecognizer();
        this.cursorToContext = new HashMap<InputCursor, UnistrokeContext>();
    }

    /* **********Object methods********** */
    /**
     * Adds a unistroke gesture template (with direction) to the processor.
     * 
     * @param gesture
     *            the unistroke gesture
     * @param direction
     *            the gesture direction
     */
    public void addTemplate(UnistrokeGesture gesture, Direction direction) {

        log.trace("Entering addTemplate(gesture=" + gesture + ", direction=" //$NON-NLS-1$ //$NON-NLS-2$
                + direction + ")"); //$NON-NLS-1$

        this.recognizer.addTemplate(gesture, direction);

        log.trace("Leaving addTemplate()"); //$NON-NLS-1$
    }

    /* ********Overridden methods******** */

    /**
     * Overriden method from AbstractGlobalInputProcessor. Only processes events
     * that target the canvas.
     * 
     * @param inputEvent
     *            the input event
     */
    @Override
    public void processInputEvtImpl(MTInputEvent inputEvent) {

        log.trace("Entering processInputEvtImpl()" + inputEvent); //$NON-NLS-1$

        if (inputEvent instanceof AbstractCursorInputEvt
                && inputEvent.hasTarget()) {

            // Modification by Sarah Will
            // We only process the event if its target is the canvas
            if (inputEvent.getTarget() instanceof MTCanvas) {

                log.trace("The input event has target canvas"); //$NON-NLS-1$
                AbstractCursorInputEvt ce = (AbstractCursorInputEvt) inputEvent;

                InputCursor inputCursor = ce.getCursor();
                log.trace("Current cursor target: " //$NON-NLS-1$
                        + inputCursor.getCurrentTarget());
                log.trace("General cursor target: " + inputCursor.getTarget()); //$NON-NLS-1$

                switch (ce.getId()) {
                    case AbstractCursorInputEvt.INPUT_STARTED: {

                        log.trace("Unistroke input started"); //$NON-NLS-1$

                        UnistrokeContext context = new UnistrokeContext(
                                this.pApplet, this.planeNormal,
                                this.pointInPlane, inputCursor,
                                this.recognizer, this.unistrokeUtils,
                                this.canvas);
                        if (!context.isGestureAborted()) {
                            this.cursorToContext.put(inputCursor, context);

                            context.update(inputCursor);

                            // FIXME ?? 3 times? REMOVE?
                            context.update(inputCursor);
                            context.update(inputCursor);
                            context.update(inputCursor);

                            this.fireInputEvent(new UnistrokeEvent(this,
                                    MTGestureEvent.GESTURE_STARTED,
                                    this.canvas, context.getVisualizer(),
                                    UnistrokeGesture.NOGESTURE, inputCursor));
                        }

                    }
                        break;
                    case AbstractCursorInputEvt.INPUT_UPDATED: {

                        log.trace("Unistroke input updated"); //$NON-NLS-1$

                        UnistrokeContext context = this.cursorToContext
                                .get(inputCursor);
                        if (context != null) {
                            context.update(inputCursor);
                            this.fireInputEvent(new UnistrokeEvent(this,
                                    MTGestureEvent.GESTURE_UPDATED,
                                    this.canvas, context.getVisualizer(),
                                    UnistrokeGesture.NOGESTURE, inputCursor));
                        }

                    }
                        break;
                    case AbstractCursorInputEvt.INPUT_ENDED: {

                        log.trace("Unistroke input ended"); //$NON-NLS-1$

                        UnistrokeContext context = this.cursorToContext
                                .remove(inputCursor);
                        if (context != null) {
                            context.update(inputCursor);
                            this.fireInputEvent(new UnistrokeEvent(this,
                                    MTGestureEvent.GESTURE_ENDED, this.canvas,
                                    context.getVisualizer(), context
                                            .recognizeGesture(), inputCursor));
                            context.getVisualizer().destroy();
                        }
                    }
                        break;
                    default:
                        break;
                }
            } else {
                log.trace("The input event has target " + inputEvent.getTarget()); //$NON-NLS-1$
            }
        }

        log.trace("Leaving processInputEvtImpl()" + inputEvent); //$NON-NLS-1$

    }
}
