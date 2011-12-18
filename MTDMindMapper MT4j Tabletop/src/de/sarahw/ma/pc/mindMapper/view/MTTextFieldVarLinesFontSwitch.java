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
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.MTTextField;
import org.mt4j.util.font.IFont;
import org.mt4j.util.font.IFontCharacter;

import processing.core.PApplet;

/**
 * <p>
 * This MTTextFieldVarLinesFontSwitch allows a variable number of lines of text
 * (up to TEXT_FIELD_VAR_MAX_LINES) and adds a line break after each line has
 * reached the width of the TextField. In addition to that, it switched to a
 * smaller font when the width of the first line is reached.
 * </p>
 * 
 * @author Sarah Will
 * 
 * @see MTTextArea
 * @see MTTextField
 * @see MTTextFieldVarLines
 * 
 */
public class MTTextFieldVarLinesFontSwitch extends MTTextFieldVarLines {

    private static Logger      log                     = Logger.getLogger(MTTextFieldVarLinesFontSwitch.class);

    /* *** Text field variable lines with font switch constants *** */
    /** The text field width offset */
    protected static final int TEXT_FIELD_WIDTH_OFFSET = 10;

    /* *** Fonts *** */
    /** The default (bigger) font */
    private IFont              defaultFont;
    /** The smaller font */
    private IFont              smallerFont;
    /** Flag that indicates that fontSwitch should not be performed */
    private boolean            lockSwitchFont          = false;

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new MTTTextFieldTwoLines.
     * 
     * @param applet
     *            the application instance
     * @param x
     *            the x position of the var line text field
     * @param y
     *            the y position of the var line text field
     * @param width
     *            the width of the var line text field
     * @param height
     *            the height of the var line text field
     * @param maxNumberOfLines
     *            the maximum number of lines in the variable TextField
     * @param fontDefault
     *            the default font
     * @param fontSmall
     *            the smaller font that we switch to once we reached the width
     *            of the text field in the first line
     */
    public MTTextFieldVarLinesFontSwitch(PApplet applet, float x, float y,
            float width, float height, int maxNumberOfLines, IFont fontDefault,
            IFont fontSmall) {
        super(applet, x, y, width, height, maxNumberOfLines, fontDefault);

        log.debug("Executing MTTextFieldVarLines(applet=" + applet //$NON-NLS-1$ 
                + ", x=" + x + ", y=" + y + ", width=" + width //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$ 
                + ", height=" + height + ", maxNumberOfLines=" + maxNumberOfLines + ", fontDefault=" + fontDefault //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
                + ", fontSmall=" + fontSmall + ")"); //$NON-NLS-1$ //$NON-NLS-2$ 

        // Set font references
        this.defaultFont = fontDefault;
        this.smallerFont = fontSmall;

    }

    /* ********Overridden methods******** */
    /**
     * Method called after a character is added to the
     * MTTextFieldVarLinesFontSwitch (e.g. via a keyboard). Checks which line we
     * are in (max. lines is maxNumberOfLines), begins a new line if necessary,
     * removes added character if we have reached the maxNumberOfLines limit and
     * switches the font if we have reached the character limit for one single
     * line.
     * 
     * @param character
     *            the added character
     * @see org.mt4j.components.visibleComponents.widgets.MTTextArea#characterAdded
     *      (org.mt4j.components.visibleComponents.font.IFontCharacter)
     */
    @Override
    protected void characterAdded(IFontCharacter character) {
        log.trace("Entering characterAdded(character=" + character.getUnicode() + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Special handling of line breaks
        if (character.getUnicode().equalsIgnoreCase("\n")) { //$NON-NLS-1$

            log.trace("Line break added!"); //$NON-NLS-1$

            float localWidth = this.getWidthXY(TransformSpace.LOCAL);
            localWidth += TEXT_FIELD_WIDTH_OFFSET;

            // We are now in the second line (getLineCount() == 1))
            if (getLineCount() < this.getMaxNumberOfLines()) {
                // switch to smaller font
                if (!this.lockSwitchFont && !this.isClearActive()
                        && this.getText().length() > 0
                        && this.getFont().equals(this.defaultFont)) {
                    log.trace("Switch to the smaller font for the character \n at last char position " //$NON-NLS-1$
                            + getLastCharEndPos() + " and local width " //$NON-NLS-1$
                            + localWidth);
                    this.setFont(this.smallerFont);

                }
            } else { // we are in "no more lines allowed" land, remove \n
                log.trace("No more line breaks allowed, remove!"); //$NON-NLS-1$
                this.removeLastCharacter();
            }

        } else {
            log.trace("We are at line " + getLineCount() + 1); //$NON-NLS-1$
            // If we are still in the first line (getLineCount()==0)
            if (getLineCount() < (this.getMaxNumberOfLines() - 1)) {

                // If the end of the line is reached, switch to smaller font or
                // start a new line with the new character if small font is
                // already
                // set
                float localWidth = this.getWidthXY(TransformSpace.LOCAL);
                localWidth += TEXT_FIELD_WIDTH_OFFSET;

                // We still have less than the maximum number of lines and the
                // default font, so
                // switch
                // the font
                if (!this.lockSwitchFont && !this.isClearActive()
                        && this.getText().length() > 0
                        && getLastCharEndPos() > localWidth
                        && this.getFont().equals(this.defaultFont)) {
                    log.trace("Switch to the smaller font for the character " //$NON-NLS-1$
                            + character.getUnicode()
                            + " at last char position " //$NON-NLS-1$
                            + getLastCharEndPos() + " and local width " //$NON-NLS-1$
                            + localWidth);
                    this.setFont(this.smallerFont);

                }
                // We still have less than the maximum number of lines and the
                // small font is
                // already
                // set, so we start a new line
                if (!this.isClearActive() && this.getText().length() > 0
                        && getLastCharEndPos() > localWidth
                        && this.getFont().equals(this.smallerFont)) {
                    log.trace("Start a new line with the character" + character.getUnicode() + " at last char position " + getLastCharEndPos() + " and local width " + localWidth); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    this.lockSwitchFont = true;
                    this.removeLastCharacter();

                    // Start new line and add character there
                    this.appendCharByUnicode("\n"); //$NON-NLS-1$
                    this.appendCharByUnicode(character.getUnicode());
                    this.lockSwitchFont = false;
                }
            } else { // We are in line maxNumberOfLines (or more, which should
                     // not be the case)
                float localWidth = this.getWidthXY(TransformSpace.LOCAL);
                localWidth += TEXT_FIELD_WIDTH_OFFSET;

                // If the end of the line is reached, don't add any characters
                if (this.getText().length() > 0
                        && getLastCharEndPos() > localWidth) {
                    log.trace("We have reached the end of line two, don't add any more characters"); //$NON-NLS-1$

                    // No more chars are to be added
                    this.removeLastCharacter();
                }
            }
        }

        log.trace("Leaving characterAdded()"); //$NON-NLS-1$
    }

    /**
     * Method called after a character has been removed from the
     * MTTextFieldVarLinesFontSwitch (e.g. via a keyboard). Switches the font if
     * we have reached the character limit for one line.
     * 
     * @param character
     *            the removed character
     * @see org.mt4j.components.visibleComponents.widgets.MTTextArea#characterRemoved
     *      (org.mt4j.components.visibleComponents.font.IFontCharacter)
     * 
     */
    @Override
    protected void characterRemoved(IFontCharacter character) {

        log.trace("Entering characterRemoved(character=" + character.getUnicode() + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // If we just removed character over the local width border,
        // and we are in line one with clear() not active switch to the default
        // font

        float localWidth = this.getWidthXY(TransformSpace.LOCAL);
        localWidth += TEXT_FIELD_WIDTH_OFFSET;

        if (!this.lockSwitchFont && !this.isClearActive() && getLineCount() < 1
                && this.getFont().equals(this.smallerFont)
                && getLastCharEndPos() <= localWidth) {
            log.trace("Set font to bigger font for character " //$NON-NLS-1$
                    + character.getUnicode() + " at last char position " //$NON-NLS-1$
                    + getLastCharEndPos() + " and local width " //$NON-NLS-1$
                    + localWidth);

            this.setFont(this.defaultFont);
        }

        log.trace("Leaving characterRemoved()"); //$NON-NLS-1$

    }

}
