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
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;

import processing.core.PApplet;

/**
 * <p>
 * Abstract class representing a generic abstract overlay.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public abstract class AbstractOverlay extends MTRectangle {

    protected static Logger      log                 = Logger.getLogger(AbstractOverlay.class);

    /* *** Overlay constants *** */
    /**
     * The desired offset from the border that adds to the maximum/minimum
     * position the overlay can be positioned at.
     */
    protected static final float CHECK_BORDER_OFFSET = 0;

    /* *** Application *** */
    /** The multitouch application instance */
    AbstractMTApplication        mtApplication;

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new AbstractOverlay
     * 
     * @param pApplet
     *            the application instance
     * @param x
     *            the x position of the overlay
     * @param y
     *            the y position of the overlay
     * @param width
     *            the width of the overlay
     * @param height
     *            the height of the overlay
     */
    public AbstractOverlay(PApplet pApplet, float x, float y, float width,
            float height) {
        super(pApplet, x, y, width, height);

        log.debug("Executing AbstractOverlay(pApplet=" + pApplet + ", x=" //$NON-NLS-1$ //$NON-NLS-2$
                + x + ", y=" + y + ", width=" + width + ", height=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + height + ")"); //$NON-NLS-1$ 

        // Set app reference and fonts
        this.mtApplication = (AbstractMTApplication) pApplet;

        // Initialize
        initialize();

    }

    /* **********Object methods********** */
    /**
     * Initializes a new AbstractOverlay
     * 
     */
    private void initialize() {

        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Remove scaling processor and listener
        ToolsEventHandling.removeScaleProcessorsAndListeners(this);

        // Set custom drag listener
        addCustomDragListener();

        // Set custom rotate listener
        addCustomRotateListener();

        log.debug("Leaving initialize()"); //$NON-NLS-1$

    }

    /**
     * Adds a custom drag listener that prevents dragging the overlay beyond the
     * screen borders.
     * 
     */
    private void addCustomDragListener() {

        log.debug("Entering addCustomDragListener()"); //$NON-NLS-1$

        // Remove default drag processor and listeners
        ToolsEventHandling.removeDragProcessorsAndListeners(this);

        // Add new modified drag processor
        this.registerInputProcessor(new DragProcessor(this.mtApplication));

        this.addGestureListener(DragProcessor.class,
                new DragActionCheckBorders(this.mtApplication,
                        CHECK_BORDER_OFFSET));

        log.debug("Leaving addCustomDragListener()"); //$NON-NLS-1$

    }

    /**
     * Adds a custom rotate listener that prevents dragging the overlay beyond
     * the screen borders through rotating.
     * 
     */
    private void addCustomRotateListener() {

        log.debug("Entering addCustomRotateListener()"); //$NON-NLS-1$

        // Remove default rotate processors and listeners
        ToolsEventHandling.removeRotateProcessorsAndListeners(this);

        // Add new modified rotate processor
        this.registerInputProcessor(new RotateProcessor(this.mtApplication));

        this.addGestureListener(RotateProcessor.class,
                new RotateActionCheckBorders(this.mtApplication,
                        CHECK_BORDER_OFFSET));

        log.debug("Leaving addCustomRotateListener()"); //$NON-NLS-1$

    }

}
