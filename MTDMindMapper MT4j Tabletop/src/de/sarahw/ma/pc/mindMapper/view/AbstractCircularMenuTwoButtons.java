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

import java.util.List;

import org.apache.log4j.Logger;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.components.visibleComponents.widgets.MTSvg;
import org.mt4j.components.visibleComponents.widgets.buttons.MTSvgButton;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * <p>
 * Abstract class representing a circular menu view with two buttons. Collects
 * all initialization and layout logic (some of that is already part of the
 * superclass AbstractCircularMenu) but must be implemented by a concrete menu
 * subclass to allow menu specific button behaviour, see abstract methods.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * @see de.sarahw.ma.pc.mindMapper.view.AbstractCircularMenu
 * 
 */
public abstract class AbstractCircularMenuTwoButtons extends
        AbstractCircularMenu {

    private static Logger         log = Logger.getLogger(AbstractCircularMenuTwoButtons.class);

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication mtApplication;

    /* *** Menu handles *** */
    /** The container for the menu handle svg images */
    private MenuHandleContainer   tpMenuHandleContainer;

    /* *** Buttons *** */
    /** The list of all svg buttons */
    private List<MTSvgButton>     buttonList;

    /** The button north */
    private MTSvgButton           buttonNorth;
    /** The button south */
    private MTSvgButton           buttonSouth;

    /** The initial position for the button north */
    private Vector3D              buttonNorthPosToSet;
    /** The initial position for the button south */
    private Vector3D              buttonSouthPosToSet;

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new AbstractCircularMenuTwoButtons
     * 
     * @param pApplet
     *            the application instance
     * @param centerPoint
     *            the center point of the menu
     * @param radius
     *            the radius of the circular menu
     * @param buttonList
     *            the two SVGButtons for the menu, ordered by their position,
     *            starting with NORTH and continuing clockwise (= button NORTH,
     *            button SOUTH)
     * 
     */
    public AbstractCircularMenuTwoButtons(PApplet pApplet,
            Vector3D centerPoint, float radius, List<MTSvgButton> buttonList) {
        super(pApplet, centerPoint, radius);

        log.debug("Executing AbstractCircularMenuTwoButtons(pApplet=" + pApplet + ", centerPoint=" //$NON-NLS-1$ //$NON-NLS-2$
                + centerPoint + ", radius=" + radius + ", buttonList=" //$NON-NLS-1$ //$NON-NLS-2$
                + buttonList + ")"); //$NON-NLS-1$

        // Set application instance
        this.mtApplication = (AbstractMTApplication) pApplet;

        // Set button list
        this.buttonList = buttonList;

        // Initialize
        initialize();
    }

    /* ********Getters & Setters******** */
    /**
     * Returns the list of buttons in this MTCircleMenu.
     * 
     * @return the buttonList
     */
    public List<MTSvgButton> getButtonList() {
        return this.buttonList;
    }

    /**
     * Returns the button instance at the north position.
     * 
     * @return the buttonNorth
     */
    public MTSvgButton getButtonNorth() {
        return this.buttonNorth;
    }

    /**
     * Returns the button instance at the south position.
     * 
     * @return the buttonSouth
     */
    public MTSvgButton getButtonSouth() {
        return this.buttonSouth;
    }

    /**
     * Returns the position for the button at the north position, calculated at
     * initialization.
     * 
     * @return the buttonNorthPosToSet
     */
    public Vector3D getButtonNorthPosToSet() {
        return this.buttonNorthPosToSet;
    }

    /**
     * Returns the position for the button at the south position, calculated at
     * initialization.
     * 
     * @return the buttonSouthPosToSet
     */
    public Vector3D getButtonSouthPosToSet() {
        return this.buttonSouthPosToSet;
    }

    /**
     * Returns the handle marker container instance.
     * 
     * @return the tpMenuHandleContainer
     */
    public MenuHandleContainer getTpMenuHandleMarkerContainer() {
        return this.tpMenuHandleContainer;
    }

    /* *************Delegates************** */

    /**
     * Returns the current position of the north handle marker via the
     * MenuHandleContainer, if it is set.
     * 
     * @return the position if the handle is set, else null
     */
    public Vector3D getTpNorthMarkerPosition() {
        return getTpMenuHandleMarkerContainer()
                .getTpNorthMarkerGlobalPosition();
    }

    /**
     * Returns the current position of the south handle marker via the
     * MenuHandleContainer, if it is set.
     * 
     * @return the position if the handle is set, else null
     */
    public Vector3D getTpSouthMarkerPosition() {
        return getTpMenuHandleMarkerContainer()
                .getTpSouthMarkerGlobalPosition();
    }

    /* *********Object methods********* */

    /**
     * Initializes the AbstractCircularMenuFourButtons.
     * 
     */
    private void initialize() {

        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Add (rotation and drag) handles
        if (!addMenuHandles()) {
            log.error("Error while adding touch points!"); //$NON-NLS-1$
        }

        // Send background to front
        MTSvg background = (MTSvg) this
                .getChildByName(AbstractCircularMenu.MENU_BACKGROUND_NAME);

        if (background != null) {
            background.sendToFront();
        } else {
            log.error("Background for menu not found. Could not send to front!"); //$NON-NLS-1$
        }

        // Calculate buttons positions
        if (!calculateSvgButtonsPositions()) {
            log.error("Error while calculating buttons' positions"); //$NON-NLS-1$
        }

        // Add svg buttons
        if (!addSvgButtonsToMenu()) {
            log.error("Error while adding SVG buttons to the menu!"); //$NON-NLS-1$
        }

        // Add tap listeners to the buttons
        addTapListenersToButtons();

        log.debug("Leaving initialize()"); //$NON-NLS-1$

    }

    /**
     * Adds a MenuHandleContainer with four (at the moment, two look weird!)
     * menu handles for the previously calculated touch points.
     * 
     * @return true if adding handle touch point markers was successful
     */
    private boolean addMenuHandles() {

        log.debug("Entering addMenuHandles()"); //$NON-NLS-1$

        // If all required touch points are valid
        if (this.getTouchPointNorth() != null
                && this.getTouchPointSouth() != null
                && this.getTouchPointEast() != null
                && this.getTouchPointWest() != null) {

            // Add new marker container with touch points
            this.tpMenuHandleContainer = new MenuHandleContainer(
                    this.mtApplication, this, this.getTouchPointNorth(),
                    this.getTouchPointEast(), this.getTouchPointSouth(),
                    this.getTouchPointWest());

            // Add to this menu
            this.addChild(this.tpMenuHandleContainer);

            log.debug("Leaving addMenuHandles(): " //$NON-NLS-1$
                    + "Intersection points are: North: " + this.getTouchPointNorth() //$NON-NLS-1$
                    + ", east: " + this.getTouchPointEast() //$NON-NLS-1$
                    + ", south: " //$NON-NLS-1$
                    + this.getTouchPointSouth() + ", west: " //$NON-NLS-1$
                    + this.getTouchPointWest());

            return true;
        }
        log.error("Leaving addMenuHandles(): " //$NON-NLS-1$
                + "false, touch points not successfully set!"); //$NON-NLS-1$
        return false;
    }

    /**
     * Calculates the initial positions for the two buttons of the menu.
     * 
     * @return true, if the buttons' positions were successfully calculated
     */
    private boolean calculateSvgButtonsPositions() {

        log.debug("Entering calculateSvgButtonsPositions()"); //$NON-NLS-1$

        // Calculate positions via dummy rectangle
        MTRectangle dummyRectangle = new MTRectangle(this.mtApplication,
                this.getRadiusX() * 2, this.getRadiusY());

        // Button south
        // Set anchor upper left
        dummyRectangle.setAnchor(PositionAnchor.UPPER_LEFT);

        // Position at menu center
        dummyRectangle.setPositionGlobal(this.getTouchPointWest());

        this.buttonSouthPosToSet = dummyRectangle.getCenterPointGlobal();

        // Button north west
        // Set anchor upper left (no upper right available)
        dummyRectangle.setAnchor(PositionAnchor.LOWER_LEFT);

        // Position at touch point west
        dummyRectangle.setPositionGlobal(this.getTouchPointWest());

        // Get position from dummy
        this.buttonNorthPosToSet = dummyRectangle.getCenterPointGlobal();

        // Destroy dummy
        dummyRectangle.destroy();

        log.debug("Leaving calculateSvgButtonsPositions(): true"); //$NON-NLS-1$ 

        return true;

    }

    /**
     * Adds the three buttons from the button list to the menu at the positions
     * calculated before.
     * 
     * @return true, if the svg buttons have been successfully added to the menu
     */
    private boolean addSvgButtonsToMenu() {

        log.debug("Entering addSvgButtonsToMenu()"); //$NON-NLS-1$

        // Check if positions are set
        if (this.getButtonNorthPosToSet() != null
                && this.getButtonSouthPosToSet() != null) {

            // Check if button list is valid
            if (this.getButtonList().size() == 2) {

                // We get the buttons one by one
                // in the order: NORTH, SOUTH
                this.buttonNorth = getButtonList().get(0);
                this.buttonSouth = getButtonList().get(1);

                this.buttonNorth.setWidthXYGlobal(this.getRadiusX() * 2);
                this.buttonSouth.setWidthXYGlobal(this.getRadiusX() * 2);

                // Set position // ! Not the final positions! We still need
                // to rotate
                this.buttonNorth.setPositionGlobal(this
                        .getButtonNorthPosToSet());

                this.buttonSouth.setPositionGlobal(this
                        .getButtonSouthPosToSet());

                // Set bounds picking behaviour
                this.buttonNorth
                        .setBoundsPickingBehaviour(AbstractShape.BOUNDS_DONT_USE);
                this.buttonSouth
                        .setBoundsPickingBehaviour(AbstractShape.BOUNDS_DONT_USE);

                // Add to Circle Menu
                this.addChild(this.buttonNorth);
                this.addChild(this.buttonSouth);

                log.debug("Leaving addSvgButtonsToMenu(): true"); //$NON-NLS-1$
                return true;
            }
            log.error("Leaving addSvgButtonsToMenu(): false,  Invalid number of buttons specified as parameters (must be four buttons)!"); //$NON-NLS-1$
            return false;

        }
        log.error("Leaving addSvgButtonsToMenu(): false; Invalid (null) button positions set, no buttons added!"); //$NON-NLS-1$
        return false;

    }

    /**
     * Adds a tap listener to every single button of the menu.
     * 
     * @return true, if tap listeners have been added to all buttons
     */
    private boolean addTapListenersToButtons() {

        log.debug("Entering addTapListenersToButtons()"); //$NON-NLS-1$

        // Check if buttons are set
        if (this.getButtonNorth() != null && this.getButtonSouth() != null) {

            // Add gesture listeners
            this.getButtonNorth().addGestureListener(TapProcessor.class,
                    new IGestureEventListener() {
                        @Override
                        public boolean processGestureEvent(MTGestureEvent ge) {
                            TapEvent te = (TapEvent) ge;

                            if (te.isTapped()) {

                                onButtonNorthClicked();
                            }
                            return false;
                        }
                    });

            this.getButtonSouth().addGestureListener(TapProcessor.class,
                    new IGestureEventListener() {
                        @Override
                        public boolean processGestureEvent(MTGestureEvent ge) {
                            TapEvent te = (TapEvent) ge;
                            if (te.isTapped()) {

                                onButtonSouthClicked();
                            }
                            return false;
                        }
                    });

            log.debug("Leaving addTapListenersToButtons(): true"); //$NON-NLS-1$
            return true;
        }
        log.error("Leaving addTapListersToButtons(): false,  buttons have not been initialized correctly!"); //$NON-NLS-1$
        return false;
    }

    /* *********Abstract methods********* */

    /**
     * Abstract method invoked when the button at the north position is tapped.
     * To be implemented by subclass.
     * 
     */
    abstract protected void onButtonNorthClicked();

    /**
     * Abstract method invoked when the button at the south position is tapped.
     * To be implemented by subclass.
     * 
     */
    abstract protected void onButtonSouthClicked();

}
