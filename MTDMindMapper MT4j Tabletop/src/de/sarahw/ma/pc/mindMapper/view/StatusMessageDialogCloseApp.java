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
import org.mt4j.util.font.IFont;

import processing.core.PApplet;

/**
 * 
 * <p>
 * StatusMessageDialogCloseApp class. Represents a status message view (subclass
 * of AbstractStatusMessageDialogYesNo) that contains two MTTextFieldVarLines
 * components with a status message text, two OK buttons and two CANCEL buttons
 * mirrored at the axis to close the status message overlay and invoke specific
 * actions.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class StatusMessageDialogCloseApp extends
        AbstractStatusMessageDialogYesNo {

    private static Logger      log                                = Logger.getLogger(StatusMessageDialogCloseApp.class);

    /* *** Status message constants *** */
    /** The maximum number of lines for the "Close application?" status message */
    protected static final int STATUS_MSG_CLOSE_APP_MAXNUMOFLINES = 2;

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new status message for closing the app at the
     * given position with the given height and width etc.
     * 
     * @param pApplet
     *            the application instance
     * @param x
     *            the x Position
     * @param y
     *            the y Position
     * @param width
     *            the width
     * @param height
     *            the height
     * @param statusType
     *            the status type
     * @param text
     *            the status message text
     * @param fontSmall
     *            the smaller status message font
     * @param fontBig
     *            the bigger status message font
     * @param numberOfLines
     *            the maximum number of lines in the status message
     */
    public StatusMessageDialogCloseApp(PApplet pApplet,
            MindMapScene mindMapScene, float x, float y, float width,
            float height, EStatusMessageType statusType, String text,
            IFont fontSmall, IFont fontBig, int numberOfLines) {
        super(pApplet, mindMapScene, x, y, width, height, statusType, text,
                fontSmall, fontBig, numberOfLines);

        log.debug("Executing StatusMessageDialogCloseApp(pApplet=" + pApplet + ", x=" //$NON-NLS-1$ //$NON-NLS-2$
                + x
                + ", mindMapScene=" + mindMapScene + ", y=" + y + ", width=" + width + ", \nheight=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + height
                + ", statusType=" //$NON-NLS-1$
                + statusType
                + ", text=" + text + ", fontSmall=" + fontSmall //$NON-NLS-1$//$NON-NLS-2$
                + ", fontBig=" + fontBig + ", numberOfLines=" + numberOfLines + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    }

    /* *********Utility methods********* */
    /**
     * Implements superclass abstract method. Invoked when the OK button is
     * tapped. This button closes the application.
     */
    @Override
    protected void onOKButtonTappedAction() {

        log.debug("Entering onOKButtonTappedAction()"); //$NON-NLS-1$
        // Close application, default close action is handled by superclass

        System.exit(0);

        log.debug("Leaving onOKButtonTappedAction()"); //$NON-NLS-1$

    }

    /**
     * Implements superclass abstract method. Invoked when the Cancel button is
     * tapped. This button does nothing. (Closing is handled by superclass
     * already).
     */
    @Override
    protected void onCancelButtonTappedAction() {

        log.debug("Entering onCancelButtonTappedAction()"); //$NON-NLS-1$
        // Do nothing, default close action is handled by superclass

        log.debug("Leaving onCancelButtonTappedAction()"); //$NON-NLS-1$

    }

}
