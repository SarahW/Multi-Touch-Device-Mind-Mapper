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
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
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
 * Abstract class representing a circular menu view with three buttons. Collects
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
public abstract class AbstractCircularMenuThreeButtons extends
        AbstractCircularMenu {

    private static Logger         log                                = Logger.getLogger(AbstractCircularMenuThreeButtons.class);

    /* *** Button constants *** */
    /** The x offset for menu buttons in percent of the menu radius */
    private static final float    BUTTON_X_OFFSET_TO_MENU_PERCENT    = 1 / 12.0f;

    /** The scaling of the button width in percent of the menu width total */
    private static final float    BUTTON_WIDTH_SCALE_TO_MENU_PERCENT = 1 / 6.0f;

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication mtApplication;

    /* *** Menu handles *** */
    /** The container for the menu handle svg images */
    private MenuHandleContainer   tpMenuHandleContainer;

    /* *** Buttons *** */
    /** The list of all svg buttons */
    private List<MTSvgButton>     buttonList;

    /** The button north east */
    private MTSvgButton           buttonNorthEast;
    /** The button south */
    private MTSvgButton           buttonSouth;
    /** The button north west */
    private MTSvgButton           buttonNorthWest;

    /** The initial position for the button north east */
    private Vector3D              buttonNorthEastPosToSet;
    /** The initial position for the button south */
    private Vector3D              buttonSouthPosToSet;
    /** The initial position for the button north west */
    private Vector3D              buttonNorthWestPosToSet;

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new AbstractCircularMenuThreeButtons
     * 
     * @param pApplet
     *            the application instance
     * @param centerPoint
     *            the center point of the menu
     * @param radius
     *            the radius of the circular menu
     * @param buttonList
     *            the three SVGButtons for the menu, ordered by their position,
     *            starting with NORTH and continuing clockwise (= button
     *            NORTH_EAST, button SOUTH, button NORTH_WEST)
     * 
     */
    public AbstractCircularMenuThreeButtons(PApplet pApplet,
            Vector3D centerPoint, float radius, List<MTSvgButton> buttonList) {
        super(pApplet, centerPoint, radius);

        log.debug("Executing AbstractCircularMenuThreeButtons(pApplet=" + pApplet + ", centerPoint=" //$NON-NLS-1$ //$NON-NLS-2$
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
     * Returns the button instance at the north east position.
     * 
     * @return the buttonNorthEast
     */
    public MTSvgButton getButtonNorthEast() {
        return this.buttonNorthEast;
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
     * Returns the button instance at the north west position.
     * 
     * @return the buttonNorthWest
     */
    public MTSvgButton getButtonNorthWest() {
        return this.buttonNorthWest;
    }

    /**
     * Returns the position for the button at the north east position,
     * calculated at initialization.
     * 
     * @return the buttonNorthEastPosToSet
     */
    public Vector3D getButtonNorthEastPosToSet() {
        return this.buttonNorthEastPosToSet;
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
     * Returns the position for the button at the north west position,
     * calculated at initialization.
     * 
     * @return the buttonNorthWestPosToSet
     */
    public Vector3D getButtonNorthWestPosToSet() {
        return this.buttonNorthWestPosToSet;
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
     * Returns the current position of the south east handle marker via the
     * MenuHandleContainer, if it is set.
     * 
     * @return the position if the handle is set, else null
     */
    public Vector3D getTpSouthEastMarkerPosition() {
        return getTpMenuHandleMarkerContainer()
                .getTpSouthEastMarkerGlobalPosition();
    }

    /**
     * Returns the current position of the south west handle marker via the
     * MenuHandleContainer, if it is set.
     * 
     * @return the position if the handle is set, else null
     */
    public Vector3D getTpSouthWestMarkerPosition() {
        return getTpMenuHandleMarkerContainer()
                .getTpSouthWestMarkerGlobalPosition();
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
     * Adds a MenuHandleContainer with three menu handles for the previously
     * calculated touch points.
     * 
     * @return true if adding handle touch point markers was successful
     */
    private boolean addMenuHandles() {

        log.debug("Entering addMenuHandles()"); //$NON-NLS-1$

        // If all required touch points are valid
        if (this.getTouchPointNorth() != null
                && this.getTouchPointSouthWest() != null
                && this.getTouchPointSouthEast() != null) {

            Vector3D correctedTpSouthWest;
            Vector3D correctedTpSouthEast;

            // Rotate south west and south east touch point to correct position
            // for three buttons (we need the positions at 120 and 240 degrees)
            MTEllipse dummyEllipse = new MTEllipse(this.mtApplication, this
                    .getTouchPointSouthWest().getCopy(), 2, 2);

            // Corrected South west is the current south west + 15 degrees
            // Rotate around the Menu center
            dummyEllipse.rotateZGlobal(this.getCenterPointGlobal(), 15.0f);
            correctedTpSouthWest = dummyEllipse.getCenterPointGlobal();

            // Set dummy circle position anew
            dummyEllipse.setPositionGlobal(this.getTouchPointSouthEast()
                    .getCopy());

            // Corrected South east is current south east - 15 degrees
            // Rotate around the Menu center
            dummyEllipse.rotateZGlobal(this.getCenterPointGlobal(), -15.0f);
            correctedTpSouthEast = dummyEllipse.getCenterPointGlobal();

            // Destroy dummy ellipse
            dummyEllipse.destroy();

            // Add new marker container with touch points
            // unused touch points are null
            this.tpMenuHandleContainer = new MenuHandleContainer(
                    this.mtApplication, this, this.getTouchPointNorth(), null,
                    null, correctedTpSouthEast, null, correctedTpSouthWest,
                    null, null);

            // Add to this MTCircleMenu
            this.addChild(this.tpMenuHandleContainer);

            log.debug("Leaving addMenuHandles(): " //$NON-NLS-1$
                    + "Intersection points are: North: " //$NON-NLS-1$
                    + this.getTouchPointNorth()
                    + ", (corrected) south east: " //$NON-NLS-1$
                    + correctedTpSouthEast
                    + ", (corrected) south west: " + correctedTpSouthWest); //$NON-NLS-1$ 

            return true;
        }
        log.error("Leaving addMenuHandles(): " //$NON-NLS-1$
                + "false, touch points not successfully set!"); //$NON-NLS-1$
        return false;
    }

    /**
     * Calculates the initial positions for the three buttons of the menu.
     * 
     * @return true, if the buttons' positions were successfully calculated
     */
    private boolean calculateSvgButtonsPositions() {

        log.debug("Entering calculateSvgButtonsPositions()"); //$NON-NLS-1$

        // Button south (figures as starting point for the other buttons,
        // as well -> will be rotated later!)
        // New dummy
        MTRectangle dummyRectangle = new MTRectangle(this.mtApplication,
                this.getRadiusX() * 2, this.getRadiusY());

        // Set anchor upper left
        dummyRectangle.setAnchor(PositionAnchor.UPPER_LEFT);

        // Position at touch point west
        dummyRectangle.setPositionGlobal(this.getTouchPointWest());

        // Get the global center point of the dummy
        Vector3D dummyCenter = dummyRectangle.getCenterPointGlobal();

        // Get global position of menu and add offset to y
        Vector3D dummyCenterPlusOffset = dummyCenter.getCopy();
        float dummyYPos = dummyCenter.getY();
        dummyCenterPlusOffset.setY(dummyYPos
                + (this.getRadiusX() * BUTTON_X_OFFSET_TO_MENU_PERCENT));

        this.buttonSouthPosToSet = dummyCenterPlusOffset;
        this.buttonNorthEastPosToSet = dummyCenterPlusOffset;
        this.buttonNorthWestPosToSet = dummyCenterPlusOffset;

        // DEBUG:
        // this.addChild(dummyRectangle);

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

        // positions are set
        if (this.getButtonNorthEastPosToSet() != null
                && this.getButtonNorthWestPosToSet() != null
                && this.getButtonSouthPosToSet() != null) {

            // Check if button list is valid
            if (this.getButtonList().size() == 3) {

                // We get the buttons one by one
                // in the order: NORTH_EAST, SOUTH, NORTH_WEST
                this.buttonNorthEast = getButtonList().get(0);
                this.buttonSouth = getButtonList().get(1);
                this.buttonNorthWest = getButtonList().get(2);

                // Because of the non-quadratic shape of the buttons, we need
                // some
                // calculation of a relative width here
                // Set button width radius*2 - (radius*2- (1/6) * radius*2)
                float buttonWidth = (this.getRadiusX() * 2)
                        - ((this.getRadiusX() * 2) * BUTTON_WIDTH_SCALE_TO_MENU_PERCENT);
                log.debug("Radius*2 = " + this.getRadiusX() * 2); //$NON-NLS-1$
                log.debug("Calculated Button width =" + buttonWidth); //$NON-NLS-1$

                this.buttonNorthEast.setWidthXYGlobal(buttonWidth);
                this.buttonNorthWest.setWidthXYGlobal(buttonWidth);
                this.buttonSouth.setWidthXYGlobal(buttonWidth);

                // Set position
                // ! Not the final positions! We still need to rotate
                this.buttonNorthEast.setPositionGlobal(this
                        .getButtonNorthEastPosToSet());
                this.buttonNorthWest.setPositionGlobal(this
                        .getButtonNorthWestPosToSet());
                this.buttonSouth.setPositionGlobal(this
                        .getButtonSouthPosToSet());

                // Rotate buttons north east and north west to correct position
                this.buttonNorthWest.rotateZGlobal(this.getCenterPointGlobal(),
                        120.0f);
                this.buttonNorthEast.rotateZGlobal(this.getCenterPointGlobal(),
                        -120.0f);

                // Set bounds picking behaviour
                this.buttonNorthEast
                        .setBoundsPickingBehaviour(AbstractShape.BOUNDS_DONT_USE);
                this.buttonNorthWest
                        .setBoundsPickingBehaviour(AbstractShape.BOUNDS_DONT_USE);
                this.buttonSouth
                        .setBoundsPickingBehaviour(AbstractShape.BOUNDS_DONT_USE);

                // Add to Circle Menu
                this.addChild(this.buttonNorthEast);
                this.addChild(this.buttonNorthWest);
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
        if (this.getButtonNorthEast() != null && this.getButtonSouth() != null
                && this.getButtonNorthWest() != null) {

            // Add gesture listeners
            this.getButtonNorthEast().addGestureListener(TapProcessor.class,
                    new IGestureEventListener() {
                        @Override
                        public boolean processGestureEvent(MTGestureEvent ge) {
                            TapEvent te = (TapEvent) ge;
                            if (te.isTapped()) {

                                onButtonNorthEastClicked();
                            }
                            return false;
                        }
                    });

            this.getButtonNorthWest().addGestureListener(TapProcessor.class,
                    new IGestureEventListener() {
                        @Override
                        public boolean processGestureEvent(MTGestureEvent ge) {
                            TapEvent te = (TapEvent) ge;
                            if (te.isTapped()) {

                                onButtonNorthWestClicked();
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
     * Abstract method invoked when the button at the north east position is
     * tapped. To be implemented by subclass.
     * 
     */
    abstract protected void onButtonNorthEastClicked();

    /**
     * Abstract method invoked when the button at the south position is tapped.
     * To be implemented by subclass.
     * 
     */
    abstract protected void onButtonSouthClicked();

    /**
     * Abstract method invoked when the button at the north west position is
     * tapped. To be implemented by subclass.
     * 
     */
    abstract protected void onButtonNorthWestClicked();

}
