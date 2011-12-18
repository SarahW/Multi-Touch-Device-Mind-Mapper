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
 * Abstract class representing a circular menu view with four buttons. Collects
 * all initialization and layout logic (some of that is already part of the
 * superclass AbstractCircularMenu) but must be implemented by a concrete menu
 * subclass to allow menu specific button behaviour, see abstract methods.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * @see de.sarahw.ma.pc.mindMapper.view.AbstractCircularMenu
 */
public abstract class AbstractCircularMenuFourButtons extends
        AbstractCircularMenu {

    private static Logger         log = Logger.getLogger(AbstractCircularMenuFourButtons.class);

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
    /** The button south east */
    private MTSvgButton           buttonSouthEast;
    /** The button south west */
    private MTSvgButton           buttonSouthWest;
    /** The button north west */
    private MTSvgButton           buttonNorthWest;

    /** The initial position for the button north east */
    private Vector3D              buttonNorthEastPosToSet;
    /** The initial position for the button south east */
    private Vector3D              buttonSouthEastPosToSet;
    /** The initial position for the button south west */
    private Vector3D              buttonSouthWestPosToSet;
    /** The initial position for the button north west */
    private Vector3D              buttonNorthWestPosToSet;

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new AbstractCircularMenuFourButtons
     * 
     * @param pApplet
     *            the application instance
     * @param centerPoint
     *            the center point of the menu
     * @param radius
     *            the radius of the circular menu
     * @param buttonList
     *            the SVGButtons for the menu, ordered by their position,
     *            starting at 12 o'clock and continuing clockwise (e.g. button
     *            NORTH_EAST, button SOUTH_EAST, ...)
     * 
     */
    public AbstractCircularMenuFourButtons(PApplet pApplet,
            Vector3D centerPoint, float radius, List<MTSvgButton> buttonList) {
        super(pApplet, centerPoint, radius);

        log.debug("Executing AbstractCircularMenuFourButtons(pApplet=" + pApplet + ", centerPoint=" //$NON-NLS-1$ //$NON-NLS-2$
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
     * Returns the button instance at the south east position.
     * 
     * @return the buttonSouthEast
     */
    public MTSvgButton getButtonSouthEast() {
        return this.buttonSouthEast;
    }

    /**
     * Returns the button instance at the south west position.
     * 
     * @return the buttonSouthWest
     */
    public MTSvgButton getButtonSouthWest() {
        return this.buttonSouthWest;
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
     * Returns the position for the button at the south east position,
     * calculated at initialization.
     * 
     * @return the buttonSouthEastPosToSet
     */
    public Vector3D getButtonSouthEastPosToSet() {
        return this.buttonSouthEastPosToSet;
    }

    /**
     * Returns the position for the button at the south west position,
     * calculated at initialization.
     * 
     * @return the buttonSouthWestPosToSet
     */
    public Vector3D getButtonSouthWestPosToSet() {
        return this.buttonSouthWestPosToSet;
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
     * Returns the current position of the northern handle marker via the
     * MenuHandleContainer, if it is set.
     * 
     * @return the position if the handle is set, else null
     */
    public Vector3D getTpNorthMarkerPosition() {
        return getTpMenuHandleMarkerContainer()
                .getTpNorthMarkerGlobalPosition();
    }

    /**
     * Returns the current position of the eastern handle marker via the
     * MenuHandleContainer, if it is set.
     * 
     * @return the position if the handle is set, else null
     */
    public Vector3D getTpEastMarkerPosition() {
        return getTpMenuHandleMarkerContainer().getTpEastMarkerGlobalPosition();
    }

    /**
     * Returns the current position of the southern handle marker via the
     * MenuHandleContainer, if it is set.
     * 
     * @return the position if the handle is set, else null
     */
    public Vector3D getTpSouthMarkerPosition() {
        return getTpMenuHandleMarkerContainer()
                .getTpSouthMarkerGlobalPosition();
    }

    /**
     * Returns the current position of the western handle marker via the
     * MenuHandleContainer, if it is set.
     * 
     * @return the position if the handle is set, else null
     */
    public Vector3D getTpWestMarkerPosition() {
        return getTpMenuHandleMarkerContainer().getTpWestMarkerGlobalPosition();
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
        if (!sendBackgroundToFront()) {
            log.error("Error while sending background to front"); //$NON-NLS-1$

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
     * Adds a MenuHandleContainer with four menu handles for the previously
     * calculated touch points.
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
     * Sends the menu background to the front as to cover the menu handles.
     * 
     * @return true if the background has been sent to the front successfully.
     */
    private boolean sendBackgroundToFront() {

        log.debug("Entering sendBackgroundToFront()"); //$NON-NLS-1$

        MTSvg background = (MTSvg) this
                .getChildByName(AbstractCircularMenu.MENU_BACKGROUND_NAME);

        if (background != null) {
            background.sendToFront();

            log.debug("Leaving sendBackgroundToFront(): true"); //$NON-NLS-1$
            return true;
        }
        log.error("Leaving sendBackgroundToFront(): false, background for menu not found. Could not send to front!"); //$NON-NLS-1$
        return false;

    }

    /**
     * Calculates the initial positions for the four buttons of the menu.
     * 
     * @return true, if the buttons' positions were successfully calculated
     */
    private boolean calculateSvgButtonsPositions() {

        log.debug("Entering calculateSvgButtonsPositions()"); //$NON-NLS-1$ 

        // Calculate positions via dummy rectangle
        MTRectangle dummyRectangle = new MTRectangle(this.mtApplication,
                this.getRadiusX(), this.getRadiusY());

        // Button north east
        // Set anchor lower left
        dummyRectangle.setAnchor(PositionAnchor.LOWER_LEFT);

        // Position at menu center
        dummyRectangle.setPositionGlobal(this.getCenterPointGlobal());

        this.buttonNorthEastPosToSet = dummyRectangle.getCenterPointGlobal();

        // Button south east
        // Set anchor upper left
        dummyRectangle.setAnchor(PositionAnchor.UPPER_LEFT);

        // Position at menu center
        dummyRectangle.setPositionGlobal(this.getCenterPointGlobal());

        this.buttonSouthEastPosToSet = dummyRectangle.getCenterPointGlobal();

        // Button south west
        // Set anchor upper left (no upper right available)
        dummyRectangle.setAnchor(PositionAnchor.UPPER_LEFT);

        // Position at touch point west
        dummyRectangle.setPositionGlobal(this.getTouchPointWest());

        this.buttonSouthWestPosToSet = dummyRectangle.getCenterPointGlobal();

        // Button north west
        // Set anchor lower right
        dummyRectangle.setAnchor(PositionAnchor.LOWER_RIGHT);

        // Position at center of menu
        dummyRectangle.setPositionGlobal(this.getCenterPointGlobal());

        this.buttonNorthWestPosToSet = dummyRectangle.getCenterPointGlobal();

        // Destroy dummy
        dummyRectangle.destroy();

        log.debug("Leaving calculateSvgButtonsPositions(): true"); //$NON-NLS-1$ 

        return true;

    }

    /**
     * Adds the four buttons from the button list to the menu at the positions
     * calculated before.
     * 
     * @return true, if the svg buttons have been successfully added to the menu
     */
    private boolean addSvgButtonsToMenu() {

        log.debug("Entering addSvgButtonsToMenu()"); //$NON-NLS-1$ 

        // Check if positions are set
        if (this.getButtonNorthEastPosToSet() != null
                && this.getButtonSouthEastPosToSet() != null
                && this.getButtonSouthWestPosToSet() != null
                && this.getButtonNorthWestPosToSet() != null) {

            // Check if button list is valid
            if (this.getButtonList().size() == 4) {

                // We get the buttons one by one
                // in the order: NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST
                this.buttonNorthEast = getButtonList().get(0);
                this.buttonSouthEast = getButtonList().get(1);
                this.buttonSouthWest = getButtonList().get(2);
                this.buttonNorthWest = getButtonList().get(3);

                // Set width as wide as Menu radius
                this.getButtonNorthEast().setWidthXYGlobal(this.getRadiusX());
                this.getButtonSouthEast().setWidthXYGlobal(this.getRadiusX());
                this.getButtonSouthWest().setWidthXYGlobal(this.getRadiusX());
                this.getButtonNorthWest().setWidthXYGlobal(this.getRadiusX());

                // Set position
                this.getButtonNorthEast().setPositionGlobal(
                        this.getButtonNorthEastPosToSet());
                this.getButtonSouthEast().setPositionGlobal(
                        this.getButtonSouthEastPosToSet());
                this.getButtonSouthWest().setPositionGlobal(
                        this.getButtonSouthWestPosToSet());
                this.getButtonNorthWest().setPositionGlobal(
                        this.getButtonNorthWestPosToSet());

                // Set bounds picking behaviour
                // Set BOUNDS_DON'T_USE so that bounds are not picked
                this.getButtonNorthEast().setBoundsPickingBehaviour(
                        AbstractShape.BOUNDS_DONT_USE);
                this.getButtonSouthEast().setBoundsPickingBehaviour(
                        AbstractShape.BOUNDS_DONT_USE);
                this.getButtonSouthWest().setBoundsPickingBehaviour(
                        AbstractShape.BOUNDS_DONT_USE);
                this.getButtonNorthWest().setBoundsPickingBehaviour(
                        AbstractShape.BOUNDS_DONT_USE);

                // Add to Circle Menu
                this.addChild(this.getButtonNorthEast());
                this.addChild(this.getButtonSouthEast());
                this.addChild(this.getButtonSouthWest());
                this.addChild(this.getButtonNorthWest());

                return true;

            }

            log.error("Leaving addSvgButtonsToMenu(): false,  Invalid number of buttons specified as parameters (must be four buttons)!"); //$NON-NLS-1$
            return false;

        }
        log.error("Leaving addSvgButtonsToMenu(): false, Invalid (null) button positions set, no buttons added!"); //$NON-NLS-1$
        return false;

    }

    /**
     * Adds a tap listener to every single button of the menu.
     * 
     * @return true, if tap listeners have been added to all buttons
     */
    private boolean addTapListenersToButtons() {

        log.debug("Entering addTapListersToButtons()"); //$NON-NLS-1$

        // Check if buttons are set
        if (this.getButtonNorthEast() != null
                && this.getButtonSouthEast() != null
                && this.getButtonSouthWest() != null
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

            this.getButtonSouthEast().addGestureListener(TapProcessor.class,
                    new IGestureEventListener() {
                        @Override
                        public boolean processGestureEvent(MTGestureEvent ge) {
                            TapEvent te = (TapEvent) ge;
                            if (te.isTapped()) {

                                onButtonSouthEastClicked();
                            }
                            return false;
                        }
                    });

            this.getButtonSouthWest().addGestureListener(TapProcessor.class,
                    new IGestureEventListener() {
                        @Override
                        public boolean processGestureEvent(MTGestureEvent ge) {
                            TapEvent te = (TapEvent) ge;
                            if (te.isTapped()) {

                                onButtonSouthWestClicked();
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

            log.debug("Leaving addTapListersToButtons(): true"); //$NON-NLS-1$
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
     * Abstract method invoked when the button at the south east position is
     * tapped. To be implemented by subclass.
     * 
     */
    abstract protected void onButtonSouthEastClicked();

    /**
     * Abstract method invoked when the button at the south west position is
     * tapped. To be implemented by subclass.
     * 
     */
    abstract protected void onButtonSouthWestClicked();

    /**
     * Abstract method invoked when the button at the north west position is
     * tapped. To be implemented by subclass.
     * 
     */
    abstract protected void onButtonNorthWestClicked();

}
