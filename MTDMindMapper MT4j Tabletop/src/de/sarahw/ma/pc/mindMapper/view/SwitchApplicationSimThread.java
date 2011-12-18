/***********************************************************************
 * Copyright (c) 2011 Sarah Will
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

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;

/**
 * <p>
 * Thread class for simulating ALT+TAB key press/release to switch applications
 * in Windows. Required when iconification of frame does not work.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class SwitchApplicationSimThread implements Runnable {

    private static Logger log = Logger.getLogger(SwitchApplicationSimThread.class);

    /* ***********Constructors*********** */
    /**
     * Constructor. Creates a new SwitchApplicationSimThread instance.
     */
    public SwitchApplicationSimThread() {
        super();
    }

    /* ***********Class methods*********** */
    /**
     * Executes the application switch by simulating key presses/releases to alt
     * and tab key.
     */
    public synchronized void execute() {

        log.debug("Entering execute()"); //$NON-NLS-1$

        try {
            Robot robot = new Robot();

            // Press alt
            robot.keyPress(KeyEvent.VK_ALT);

            // Press tab
            robot.keyPress(KeyEvent.VK_TAB);

            try {
                this.wait(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                log.warn("Error while trying to delay simulated key releases.\n" //$NON-NLS-1$
                        + "Key releases will proceed without delay." + e.getMessage()); //$NON-NLS-1$
            } finally {

                // Release tab
                robot.keyRelease(KeyEvent.VK_TAB);

                // Release alt
                robot.keyRelease(KeyEvent.VK_ALT);

            }

        } catch (AWTException ex) {
            ex.printStackTrace();
            log.error("Error while simulating key presses! : \n" + ex.getMessage()); //$NON-NLS-1$

        }

        log.debug("Leaving execute()"); //$NON-NLS-1$

    }

    /* ********Overridden methods******** */
    /** Executed on start() of this runnable class */
    @Override
    public void run() {

        execute();

    }

}
