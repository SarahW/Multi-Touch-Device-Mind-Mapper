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
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * <p>
 * Class representing the window menu view. Implements the superclass
 * AbstractCircularMenuThreeButtons, which already provides all initialization
 * and layout logic. WindowMenu merely implements the button action methods
 * specific to its button content and defines menu specific values.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
@SuppressWarnings("synthetic-access")
public class WindowMenu extends AbstractCircularMenuTwoButtons {

    private static Logger         log                              = Logger.getLogger(WindowMenu.class);

    /* *** Window Menu constants *** */
    // (width/height optimized for application resolution 1920*1080px)
    // Child elements are positioned with relative values (percent)

    /** The maximum menu radius *2 */
    public static final float     MENU_RADIUS_DEFAULT              = 150.0f;
    /** The window menu radius size in percent of the application width */
    public static final float     MENU_RADIUS_SCALE_TO_APP_PERCENT = 0.0390625f;

    // OLD VALUES:
    // public static final float MENU_RADIUS_DEFAULT = 112.0f;
    // public static final float MENU_RADIUS_SCALE_TO_APP_PERCENT = 0.02916f;

    /* *** Button constants *** */
    /** Close button svg image path */
    public static final String    MENU_WINDOW_BTN_CLOSE_IMG_PATH   = MT4jSettings
                                                                           .getInstance()
                                                                           .getDefaultSVGPath()
                                                                           + "Button_2North.svg";       //$NON-NLS-1$
    /** Minimize/Iconify button svg image path */
    public static final String    MENU_WINDOW_BTN_MIN_IMG_PATH     = MT4jSettings
                                                                           .getInstance()
                                                                           .getDefaultSVGPath()
                                                                           + "Button_2South.svg";       //$NON-NLS-1$

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication mtApplication;
    /** The current active mindMapScene */
    private MindMapScene          mindMapScene;

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new WindowMenu
     * 
     * @param pApplet
     *            the application instance
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
    public WindowMenu(PApplet pApplet, Vector3D centerPoint, float radius,
            List<MTSvgButton> buttonList, MindMapScene mindMapScene) {
        super(pApplet, centerPoint, radius, buttonList);

        log.debug("Executing WindowMenu(pApplet=" + pApplet + ", centerPoint=" //$NON-NLS-1$ //$NON-NLS-2$
                + centerPoint + ", radius=" + radius + ", buttonList=" //$NON-NLS-1$ //$NON-NLS-2$
                + buttonList + ", mindMapScene=" + mindMapScene + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Set application reference
        this.mtApplication = (AbstractMTApplication) pApplet;
        this.mindMapScene = mindMapScene;

    }

    /* ********Overridden methods******** */
    /**
     * Implements superclass abstract method. Invoked when the button at the
     * north position is tapped. Opens status message dialog asking the user if
     * they want to close the application.
     * 
     */
    @Override
    protected void onButtonNorthClicked() {
        log.debug("Entering onButtonNorthClicked()"); //$NON-NLS-1$

        // Register a new pre draw action to the scene
        WindowMenu.this.mindMapScene
                .registerPreDrawAction(new IPreDrawAction() {

                    @Override
                    public void processAction() {

                        // Create a new runnable that shows the close app
                        // question dialog
                        WindowMenu.this.mtApplication
                                .invokeLater(new Runnable() {
                                    @Override
                                    public void run() {

                                        // Status message asking if we really
                                        // want to close
                                        WindowMenu.this.mindMapScene
                                                .showStatusMessageCloseApplication(
                                                        Messages.getString("WindowMenu.onButtonNorthClicked.doYouReallyWantToClose.0"), //$NON-NLS-1$
                                                        EStatusMessageType.STATUS_MSG_QUESTION);
                                    }

                                });
                    }

                    @Override
                    public boolean isLoop() {
                        return false;
                    }
                });

        log.debug("Leaving onButtonNorthClicked()"); //$NON-NLS-1$

    }

    /**
     * Implements superclass abstract method. Invoked when the button at the
     * south position is tapped. Minimizes the application to tray.
     * 
     */
    @Override
    protected void onButtonSouthClicked() {

        // Register a new pre draw action to the scene
        WindowMenu.this.mindMapScene
                .registerPreDrawAction(new IPreDrawAction() {

                    @Override
                    public void processAction() {

                        // Create a new runnable that shows the minimize app
                        // question dialog
                        WindowMenu.this.mtApplication
                                .invokeLater(new Runnable() {
                                    @Override
                                    public void run() {

                                        // Status message asking if we really
                                        // want to close
                                        WindowMenu.this.mindMapScene
                                                .showStatusMessageMinimizeApplication(
                                                        Messages.getString("WindowMenu.onButtonSouthClicked.doYouReallyWantToMinimize.00"), //$NON-NLS-1$
                                                        EStatusMessageType.STATUS_MSG_QUESTION);
                                    }

                                });
                    }

                    @Override
                    public boolean isLoop() {
                        return false;
                    }
                });

    }

}
