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
import org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;

/**
 * Class containing commonly used utility methods for removing and adding
 * listeners and processors, (dis-)allowing event bubbling etc.
 * 
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class ToolsEventHandling {

    protected static Logger log = Logger.getLogger(ToolsEventHandling.class);

    /* ***********Constructors*********** */
    /**
     * Private constructor. Doesn't allow instances of this class.
     */
    private ToolsEventHandling() {
        //
    }

    /* ***********Class methods*********** */
    /**
     * Unregisters all ScaleProcessors from a given component and removes all
     * scale gesture listeners.
     * 
     * @param component
     *            the component
     */
    public static void removeScaleProcessorsAndListeners(MTComponent component) {

        log.trace("Entering removeScaleProcessorsAndListeners(component=" + component + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        for (AbstractComponentProcessor ip : component.getInputProcessors()) {
            if (ip instanceof ScaleProcessor) {
                component.unregisterInputProcessor(ip);
            }
        }
        component.removeAllGestureEventListeners(ScaleProcessor.class);

        log.trace("Leaving removeScaleProcessorsAndListeners()"); //$NON-NLS-1$

    }

    /**
     * Unregisters all DragProcessors from a given component and removes all
     * drag gesture listeners.
     * 
     * @param component
     *            the component
     */
    public static void removeDragProcessorsAndListeners(MTComponent component) {

        log.trace("Entering removeDragProcessorsAndListeners(component=" + component + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        for (AbstractComponentProcessor ip : component.getInputProcessors()) {
            if (ip instanceof DragProcessor) {
                component.unregisterInputProcessor(ip);
            }
        }
        component.removeAllGestureEventListeners(DragProcessor.class);

        log.trace("Leaving removeDragProcessorsAndListeners()"); //$NON-NLS-1$

    }

    /**
     * Unregisters all RotateProcessors from a given component and removes all
     * rotate gesture listeners.
     * 
     * @param component
     *            the component
     */
    public static void removeRotateProcessorsAndListeners(MTComponent component) {

        log.trace("Entering removeRotateProcessorsAndListeners(component=" + component + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        for (AbstractComponentProcessor ip : component.getInputProcessors()) {
            if (ip instanceof RotateProcessor) {
                component.unregisterInputProcessor(ip);
            }
        }
        component.removeAllGestureEventListeners(RotateProcessor.class);

        log.trace("Leaving removeRotateProcessorsAndListeners()"); //$NON-NLS-1$

    }

    /**
     * Allows bubbled events for all processors on a given component.
     * 
     * @param component
     *            the component
     */
    public static void allowBubbledEventsForAllProcessors(MTComponent component) {

        log.trace("Entering allowBubbledEventsForAllProcessors(component=" + component + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        for (AbstractComponentProcessor ap : component.getInputProcessors()) {
            if (ap instanceof AbstractCursorProcessor) {
                ((AbstractCursorProcessor) ap).setBubbledEventsEnabled(true);
            }
        }

        log.trace("Leaving allowBubbledEventsForAllProcessors()"); //$NON-NLS-1$

    }

    /**
     * Disallows bubbled events for all processors on a given component.
     * 
     * @param component
     *            the component
     */
    public static void disallowBubbledEventsForAllProcessors(
            MTComponent component) {

        log.trace("Entering disallowBubbledEventsForAllProcessors(component=" + component + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        for (AbstractComponentProcessor ap : component.getInputProcessors()) {
            if (ap instanceof AbstractCursorProcessor) {
                ((AbstractCursorProcessor) ap).setBubbledEventsEnabled(false);
            }
        }

        log.trace("Leaving disallowBubbledEventsForAllProcessors()"); //$NON-NLS-1$

    }

}
