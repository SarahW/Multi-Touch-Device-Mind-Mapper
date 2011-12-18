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
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.widgets.MTSvg;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * <p>
 * Abstract class representing a circular menu view. This collects members and
 * methods common to all circular menus.
 * </p>
 * 
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 * @see de.sarahw.ma.pc.mindMapper.view.AbstractCircularMenuFourButtons
 * @see de.sarahw.ma.pc.mindMapper.view.AbstractCircularMenuThreeButtons
 * 
 */
public abstract class AbstractCircularMenu extends MTEllipse {

    private static Logger         log                   = Logger.getLogger(AbstractCircularMenu.class);

    /* *** Menu constants *** */
    /** The offset of the background to the menu size in percent of the radius */
    private static final float    MENU_BG_OFFSET_SCALE  = 15.0f;

    /** The menu background name */
    protected static final String MENU_BACKGROUND_NAME  = "menuBackground";                            //$NON-NLS-1$

    /** The menu background svg image path name */
    private static final String   MENU_BG_IMG_FILE_PATH = MT4jSettings
                                                                .getInstance()
                                                                .getDefaultSVGPath()
                                                                + "Menu_Bg.svg";                       //$NON-NLS-1$

    /**
     * The desired offset from the border that adds to the maximum/minimum
     * position the menu can be positioned at.
     */
    protected static final float  CHECK_BORDER_OFFSET   = 0;

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication mtApplication;

    /* *** Touch points *** */
    /** The touch point north */
    private Vector3D              touchPointNorth;
    /** The touch point north east */
    private Vector3D              touchPointNorthEast;
    /** The touch point east */
    private Vector3D              touchPointEast;
    /** The touch point south east */
    private Vector3D              touchPointSouthEast;
    /** The touch point south */
    private Vector3D              touchPointSouth;
    /** The touch point south west */
    private Vector3D              touchPointSouthWest;
    /** The touch point west */
    private Vector3D              touchPointWest;
    /** The touch point north west */
    private Vector3D              touchPointNorthWest;

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new AbstractCircularMenu
     * 
     * @param pApplet
     *            the application instance
     * @param centerPoint
     *            the center point of the menu
     * @param radius
     *            the radius of the circular menu
     * 
     */
    public AbstractCircularMenu(PApplet pApplet, Vector3D centerPoint,
            float radius) {
        super(pApplet, centerPoint, radius, radius);

        log.debug("Executing AbstractCircularMenu(pApplet=" + pApplet + ", centerPoint=" //$NON-NLS-1$ //$NON-NLS-2$
                + centerPoint + ", radius=" + radius + //$NON-NLS-1$
                ")"); //$NON-NLS-1$

        // Set application instance
        this.mtApplication = (AbstractMTApplication) pApplet;

        // Initialize
        initialize();
    }

    /* ********Getters & Setters******** */
    /**
     * Returns the northern touch point for the AbstractCircularMenu calculated
     * at initialization.
     * 
     * @return the touchPointNorth
     */
    public Vector3D getTouchPointNorth() {
        return this.touchPointNorth;
    }

    /**
     * Returns the calculated north east touch point for the
     * AbstractCircularMenu calculated at initialization.
     * 
     * @return the touchPointNorthEast
     */
    public Vector3D getTouchPointNorthEast() {
        return this.touchPointNorthEast;
    }

    /**
     * Returns the eastern touch point for the AbstractCircularMenu calculated
     * at initialization.
     * 
     * @return the touchPointEast
     */
    public Vector3D getTouchPointEast() {
        return this.touchPointEast;
    }

    /**
     * Returns the calculated south west touch point for the
     * AbstractCircularMenu calculated at initialization.
     * 
     * @return the touchPointSouthEast
     */
    public Vector3D getTouchPointSouthEast() {
        return this.touchPointSouthEast;
    }

    /**
     * Returns the southern touch point for the AbstractCircularMenu calculated
     * at initialization.
     * 
     * @return the touchPointSouth
     */
    public Vector3D getTouchPointSouth() {
        return this.touchPointSouth;
    }

    /**
     * Returns the calculated south west touch point for the
     * AbstractCircularMenu calculated at initialization.
     * 
     * @return the touchPointSouthWest
     */
    public Vector3D getTouchPointSouthWest() {
        return this.touchPointSouthWest;
    }

    /**
     * Returns the calculated western touch point for the AbstractCircularMenu
     * calculated at initialization.
     * 
     * 
     * @return the touchPointWest
     */
    public Vector3D getTouchPointWest() {
        return this.touchPointWest;
    }

    /**
     * Returns the calculated north west touch point for the
     * AbstractCircularMenu calculated at initialization.
     * 
     * @return the touchPointNorthWest
     */
    public Vector3D getTouchPointNorthWest() {
        return this.touchPointNorthWest;
    }

    /* *********Object methods********* */
    /**
     * Initializes the AbstractCircularMenu.
     * 
     */
    private void initialize() {

        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Remove scaling gesture listener
        ToolsEventHandling.removeScaleProcessorsAndListeners(this);

        // Add custom drag processor/listener
        addCustomDragListener();

        // Add custom rotate processor/listener
        addCustomRotateListener();

        // Modify to allow bubbling from HandleMarkers
        ToolsEventHandling.allowBubbledEventsForAllProcessors(this);

        // Calculate touch points
        calculateHandleTouchPoints();

        // Add background
        addBackground();

        log.debug("Leaving initialize()"); //$NON-NLS-1$

    }

    /**
     * Adds a custom drag listener that prevents dragging the menu beyond the
     * screen borders.
     * 
     */
    private void addCustomDragListener() {

        log.debug("Entering addCustomDragListener()"); //$NON-NLS-1$

        // Remove default drag processors and listeners
        ToolsEventHandling.removeDragProcessorsAndListeners(this);

        // Add new modified drag processor
        this.registerInputProcessor(new DragProcessor(this.mtApplication));

        this.addGestureListener(DragProcessor.class,
                new DragActionCheckBorders(this.mtApplication,
                        CHECK_BORDER_OFFSET));

        log.debug("Leaving addCustomDragListener()"); //$NON-NLS-1$

    }

    /**
     * Adds a custom rotate listener that prevents dragging the menu beyond the
     * screen borders through rotating.
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

    /**
     * Calculates the default touch points for the menu.
     * 
     */
    private void calculateHandleTouchPoints() {

        log.debug("Entering calculateHandleTouchPoints()"); //$NON-NLS-1$

        // Calculate all default touch points
        this.touchPointNorth = ToolsComponent.getCircularMenuBoundsTouchPoint(
                this.mtApplication, this, ETouchPointLocation.NORTH);

        this.touchPointNorthEast = this.touchPointSouth = ToolsComponent
                .getCircularMenuBoundsTouchPoint(this.mtApplication, this,
                        ETouchPointLocation.NORTH_EAST);

        this.touchPointEast = ToolsComponent.getCircularMenuBoundsTouchPoint(
                this.mtApplication, this, ETouchPointLocation.EAST);

        this.touchPointSouthEast = ToolsComponent
                .getCircularMenuBoundsTouchPoint(this.mtApplication, this,
                        ETouchPointLocation.SOUTH_EAST);

        this.touchPointSouth = ToolsComponent.getCircularMenuBoundsTouchPoint(
                this.mtApplication, this, ETouchPointLocation.SOUTH);

        this.touchPointSouthWest = ToolsComponent
                .getCircularMenuBoundsTouchPoint(this.mtApplication, this,
                        ETouchPointLocation.SOUTH_WEST);

        this.touchPointWest = ToolsComponent.getCircularMenuBoundsTouchPoint(
                this.mtApplication, this, ETouchPointLocation.WEST);

        this.touchPointNorthWest = ToolsComponent
                .getCircularMenuBoundsTouchPoint(this.mtApplication, this,
                        ETouchPointLocation.NORTH_WEST);

        log.debug("Leaving calculateHandleTouchPoints()"); //$NON-NLS-1$

    }

    /**
     * Adds a background MTSvg image to the AbstractCircularMenu.
     */
    private void addBackground() {

        log.debug("Entering addBackground()"); //$NON-NLS-1$

        // Add "background" circle (slightly bigger than the
        // AbstractCircularMenu circle)
        MTSvg background = new MTSvg(this.mtApplication, MENU_BG_IMG_FILE_PATH);

        // Position at center of this Menu
        background.setPositionGlobal(this.getCenterPointGlobal());

        // Set scale (2*radius of MenuBig + (radius / BGoffsetScale)
        background.setWidthXYGlobal((this.getWidthXY(TransformSpace.GLOBAL))
                + (this.getRadiusX() / MENU_BG_OFFSET_SCALE));

        log.trace("Background width is now " + background.getWidthXYGlobal()); //$NON-NLS-1$

        // Set background pickable false so that it doesn't get dragged away
        // when touched
        background.setPickable(false);

        // Set name for easier manipulation by subclasses
        background.setName(MENU_BACKGROUND_NAME);

        // Add to menu
        this.addChild(background);

        log.debug("Leaving addBackground()"); //$NON-NLS-1$

    }

}
