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
import org.mt4j.components.visibleComponents.widgets.buttons.MTSvgButton;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * <p>
 * Class representing the main menu. Implements the superclass
 * AbstractCircularMenuFourButtons, which already provides all initialization
 * and layout logic. MainMenu merely implements the button action methods
 * specific to its button content and defines menu specific values.
 * </p>
 * 
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class MainMenu extends AbstractCircularMenuFourButtons {

    private static Logger         log                              = Logger.getLogger(MainMenu.class);

    /* *** Main Menu constants *** */
    // (width/height optimized for application resolution 1920*1080px)
    // Child elements are positioned with relative values (percent)

    /** The maximum menu radius *2 */
    public static final float     MENU_RADIUS_MAX                  = 200.0f;
    /** The main menu radius size in percent of the application width */
    public static final float     MENU_RADIUS_SCALE_TO_APP_PERCENT = 0.052083333f;

    // OLD VALUES:
    // public static final float MENU_RADIUS_MAX = 150.0f;
    // public static final float MENU_RADIUS_SCALE_TO_APP_PERCENT = 0.0390625f;

    /* *** Button constants *** */
    /** Load button svg image path */
    public static final String    MENU_MAIN_BTN_LOAD_IMG_PATH      = MT4jSettings
                                                                           .getInstance()
                                                                           .getDefaultSVGPath()
                                                                           + "Button_4NE_Load.svg";   //$NON-NLS-1$
    /** Save button svg image path */
    public static final String    MENU_MAIN_BTN_SAVE_IMG_PATH      = MT4jSettings
                                                                           .getInstance()
                                                                           .getDefaultSVGPath()
                                                                           + "Button_4SE_Save.svg";   //$NON-NLS-1$
    /** Bluetooth button svg image path */
    public static final String    MENU_MAIN_BTN_BTOOTH_IMG_PATH    = MT4jSettings
                                                                           .getInstance()
                                                                           .getDefaultSVGPath()
                                                                           + "Button_4SW_BT.svg";     //$NON-NLS-1$
    /** Help button svg image path */
    public static final String    MENU_MAIN_BTN_HELP_IMG_PATH      = MT4jSettings
                                                                           .getInstance()
                                                                           .getDefaultSVGPath()
                                                                           + "Button_4NW_Help.svg";   //$NON-NLS-1$

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication mtApplication;
    /** The current active mindMapScene */
    private MindMapScene          mindMapScene;

    /* ***********Constructors*********** */

    /**
     * Constructor. Instantiates a new MainMenu
     * 
     * @param pApplet
     *            the application instance
     * @param mindMapScene
     *            the mindMapScene the menu is created at
     * @param centerPoint
     *            the menu center point
     * @param radius
     *            the circular menu radius
     * @param buttonList
     *            the SVGButtons for the menu, ordered by their position,
     *            starting with NORTH and continuing clockwise (e.g. button
     *            NORTH_EAST, button SOUTH_EAST, ...)
     * 
     */
    public MainMenu(PApplet pApplet, MindMapScene mindMapScene,
            Vector3D centerPoint, float radius, List<MTSvgButton> buttonList) {
        super(pApplet, centerPoint, radius, buttonList);

        log.debug("Executing MainMenu(pApplet=" + pApplet + ", mindMapScene=" //$NON-NLS-1$ //$NON-NLS-2$
                + mindMapScene + ", centerPoint=" //$NON-NLS-1$ 
                + centerPoint + ", radius=" + radius + ", buttonList=" //$NON-NLS-1$ //$NON-NLS-2$
                + buttonList + ")"); //$NON-NLS-1$

        // Set application and mindMapScene reference
        this.mtApplication = (AbstractMTApplication) pApplet;
        this.mindMapScene = mindMapScene;

        // Initialization is handled by the superclasses!

    }

    /* ********Overridden methods******** */
    /**
     * Implements superclass abstract method. Invoked when the button at the
     * north east position is tapped. This is the button that opens the
     * "Load MindMap" Overlay.
     * 
     */
    @Override
    protected void onButtonNorthEastClicked() {
        log.debug("Entering onButtonNorthEastClicked()"); //$NON-NLS-1$

        if (this.mindMapScene != null) {

            // Show load overlay on mindMapScene
            this.mindMapScene.showOverlayListLoadMindMap(this);

        } else {
            log.error("Error: MindMapScene provided at initialization is null!"); //$NON-NLS-1$
        }

        log.debug("Leaving onButtonNorthEastClicked()"); //$NON-NLS-1$

    }

    /**
     * Implements superclass abstract method. Invoked when the button at the
     * south east position is tapped. This is the button that opens the
     * "Save MindMap" Overlay.
     * 
     */
    @Override
    protected void onButtonSouthEastClicked() {
        log.debug("Entering onButtonSouthEastClicked()"); //$NON-NLS-1$

        if (this.mindMapScene != null) {

            // Show save overlay on mindMapScene
            this.mindMapScene.showOverlayFormSave(this);

        } else {
            log.error("Error: MindMapScene provided at initialization is null!"); //$NON-NLS-1$
        }

        log.debug("Leaving onButtonSouthEastClicked()"); //$NON-NLS-1$

    }

    /**
     * Implements superclass abstract method. Invoked when the button at the
     * south west position is tapped. This is the button that opens the
     * "Bluetooth Connections" Overlay.
     */
    @Override
    protected void onButtonSouthWestClicked() {
        log.debug("Entering onButtonSouthWestClicked()"); //$NON-NLS-1$

        if (this.mindMapScene != null) {

            // Show bluetooth overlay on mindMapScene
            this.mindMapScene.showOverlayListBluetooth(this);

        } else {
            log.error("Error: MindMapScene provided at initialization is null!"); //$NON-NLS-1$
        }

        log.debug("Leaving onButtonSouthWestClicked()"); //$NON-NLS-1$

    }

    /**
     * Implements superclass abstract method. Invoked when the button at the
     * north west position is tapped. This is the button that opens the
     * "Help/Instructions" Overlay.
     */
    @Override
    protected void onButtonNorthWestClicked() {
        log.debug("Entering onButtonNorthWestClicked()"); //$NON-NLS-1$

        if (this.mindMapScene != null) {

            // Show help overlay on mindMapScene
            this.mindMapScene.showOverlayListHelp(this);

        } else {
            log.error("Error: MindMapScene provided at initialization is null!"); //$NON-NLS-1$
        }

        log.debug("Leaving onButtonNorthWestClicked()"); //$NON-NLS-1$

    }

}
