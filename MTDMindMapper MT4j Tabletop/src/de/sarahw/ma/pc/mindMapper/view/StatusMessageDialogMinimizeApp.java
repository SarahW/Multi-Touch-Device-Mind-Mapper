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

import java.awt.Frame;
import java.awt.Toolkit;

import org.apache.log4j.Logger;
import org.mt4j.AbstractMTApplication;
import org.mt4j.util.font.IFont;

import processing.core.PApplet;

/**
 * 
 * <p>
 * StatusMessageDialogMinimizeApp class. Represents a status message view
 * (subclass of AbstractStatusMessageDialogYesNo) that contains two
 * MTTextFieldVarLines components with a status message text, two OK buttons and
 * two CANCEL buttons mirrored at the axis to minimize the status message
 * overlay and invoke specific actions.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class StatusMessageDialogMinimizeApp extends
        AbstractStatusMessageDialogYesNo {

    private static Logger         log                              = Logger.getLogger(StatusMessageDialogMinimizeApp.class);

    /* *** Status message constants *** */
    /**
     * The maximum number of lines for the "Minimize application?" status
     * message
     */
    protected static final int    STATUS_MSG_MIN_APP_MAXNUMOFLINES = 2;

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication mtApplication;

    /** The awt frame of the application */
    private Frame                 applicationFrame;
    /**
     * Flag that indicates if the window state iconified is supported by the
     * toolkit
     */
    private boolean               iconifiedSupported;
    /** Flag that indicated if iconfication works or not */
    private boolean               iconifiedWorking                 = true;

    /**
     * Static flag that indicates if the minimizing workaround via alt+tab
     * should always be used. If set to false, the check if iconified has worked
     * is only called if minimize action has been called a second time. Set
     * manually!
     */
    private static boolean        alwaysUseWorkaround              = true;

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new status message for minimizing the app at
     * the given position with the given height and width etc.
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
    public StatusMessageDialogMinimizeApp(PApplet pApplet,
            MindMapScene mindMapScene, float x, float y, float width,
            float height, EStatusMessageType statusType, String text,
            IFont fontSmall, IFont fontBig, int numberOfLines) {
        super(pApplet, mindMapScene, x, y, width, height, statusType, text,
                fontSmall, fontBig, numberOfLines);

        log.debug("Executing StatusMessageDialogMinimizeApp(pApplet=" + pApplet + ", x=" //$NON-NLS-1$ //$NON-NLS-2$
                + x
                + ", mindMapScene=" + mindMapScene + ", y=" + y + ", width=" + width + ", \nheight=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + height
                + ", statusType=" //$NON-NLS-1$
                + statusType
                + ", text=" + text + ", fontSmall=" + fontSmall //$NON-NLS-1$//$NON-NLS-2$
                + ", fontBig=" + fontBig + ", numberOfLines=" + numberOfLines + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // Set references
        this.mtApplication = (AbstractMTApplication) pApplet;

        // Get frame
        this.applicationFrame = this.mtApplication.frame;

        // Set toolkit states supported flag
        setToolkitStatesSupportedFlag();

    }

    /* *********Utility methods********* */
    /**
     * Checks whether the required frame states are supported by the toolkit.
     */
    private void setToolkitStatesSupportedFlag() {
        log.debug("Entering checkSupportedToolkitStates()"); //$NON-NLS-1$

        if (this.applicationFrame != null) {
            Toolkit tk = this.applicationFrame.getToolkit();
            if (!(tk.isFrameStateSupported(Frame.ICONIFIED))) {
                this.iconifiedSupported = false;
                log.warn("Your window manager doesn't support the state ICONIFIED."); //$NON-NLS-1$
            } else {
                this.iconifiedSupported = true;
                log.debug("Your window manager supports the state ICONIFIED."); //$NON-NLS-1$
            }
        } else {
            this.iconifiedSupported = false;
            log.error("Error, application cannot be checked for toolskit support, no enclosing frame!"); //$NON-NLS-1$
        }

        log.debug("Leaving checkSupportedToolkitStates()"); //$NON-NLS-1$

    }

    /* ********Overridden methods******** */
    /**
     * Implements superclass abstract method. Invoked when the OK button is
     * tapped. This button minimizes the application.
     */
    @Override
    protected void onOKButtonTappedAction() {
        log.debug("Entering onOKButtonTappedAction()"); //$NON-NLS-1$
        // Minimize application, default close action is handled by superclass

        if (this.applicationFrame != null) {

            final Frame frame = this.applicationFrame;

            if (this.iconifiedSupported && this.iconifiedWorking
                    && !alwaysUseWorkaround) {

                int state = frame.getExtendedState();
                int stateCopy = state;

                // Check if the frame is already iconified
                // This tells us if a former iconification has
                // worked or not
                if (state == (stateCopy &= ~Frame.ICONIFIED)) {

                    this.iconifiedWorking = true;

                    log.trace("Frame is not in a iconfied state"); //$NON-NLS-1$
                    log.trace("state:" + state); //$NON-NLS-1$
                    log.trace("stateCopy:" + stateCopy); //$NON-NLS-1$
                    log.trace("iconified working:" + this.iconifiedWorking); //$NON-NLS-1$

                    // Set the iconified bit
                    final int stateFinal = (state |= Frame.ICONIFIED);

                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {

                            frame.setExtendedState(stateFinal);

                        }
                    });

                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {

                            frame.invalidate();
                        }
                    });
                } else {
                    // Frame has been iconified but the user
                    // was able to tap minimize in this
                    // state which tells us that the iconification
                    // has not followed through with minimizing
                    // the frame to the tray
                    // (error that occurs on some platforms
                    // without any apparent reason)

                    log.trace("Frame is already in a iconfied state"); //$NON-NLS-1$
                    log.trace("state:" + state); //$NON-NLS-1$
                    log.trace("stateCopy:" + stateCopy); //$NON-NLS-1$

                    this.iconifiedWorking = false;

                    // Remove the iconified bit
                    final int stateFinal = (state &= ~Frame.ICONIFIED);

                    log.trace("new stateFinal:" + stateFinal); //$NON-NLS-1$

                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {

                            frame.setExtendedState(stateFinal);

                        }
                    });

                    // Workaround:
                    // Simulate Alt-Tab
                    // to get out of the application
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {

                            Thread t = new Thread(
                                    new SwitchApplicationSimThread());
                            t.start();

                        }
                    });

                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {

                            frame.invalidate();
                        }
                    });

                }
            } else {
                log.warn("Warning, application cannot be minimized as planned, iconified state not supported or not working! Using alt+tab to minimize."); //$NON-NLS-1$

                int state = frame.getExtendedState();
                int stateCopy = state;

                // Revoke state change if one accidentally occurred
                if (state != (stateCopy &= ~Frame.ICONIFIED)) {

                    log.debug("state:" + state); //$NON-NLS-1$

                    // Remove the iconified bit
                    final int stateFinal = (state &= ~Frame.ICONIFIED);

                    log.debug("new stateFinal:" + stateFinal); //$NON-NLS-1$

                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {

                            frame.setExtendedState(stateFinal);

                        }
                    });

                }

                // Workaround:
                // Simulate Alt-Tab
                // to get out of the application
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        Thread t = new Thread(new SwitchApplicationSimThread());
                        t.start();

                    }
                });

                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        frame.invalidate();
                    }
                });
            }
        } else {
            log.error("Error, application cannot be minimized, no enclosing frame!"); //$NON-NLS-1$
        }

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
