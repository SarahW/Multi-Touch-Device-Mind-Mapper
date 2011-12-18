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
 * This MTTextFieldVarLines allows a variable number of lines of text (up to
 * TEXT_FIELD_VAR_MAX_LINES) and adds a line break after each line has reached
 * the width of the TextField.
 * </p>
 * 
 * <p>
 * Extends the MT4j framework class MTTextArea, modification of the MT4j
 * framework class MTTextField (mt4j-desktop).
 * </p>
 * 
 * <p>
 * Modified 2011-08
 * </p>
 * 
 * @author Christopher Ruff
 * @author (Modified by) Sarah Will
 * 
 * 
 * @see MTTextArea
 * @see MTTextField
 * 
 */
public class MTTextFieldVarLines extends MTTextArea {

    private static Logger      log                      = Logger.getLogger(MTTextFieldVarLines.class);

    /* *** Text field variable lines constants *** */
    /** The text field width offset */
    protected static final int TEXT_FIELD_WIDTH_OFFSET  = 0;
    /** The maximum allowed number of lines */
    protected static final int TEXT_FIELD_VAR_MAX_LINES = 50;

    /* *** MTTextFieldVarLines *** */
    /** Flag that indicated if a clearing of the text field is currently active */
    private boolean            clearActive              = false;

    /** The maximum number of lines set at initialization */
    private int                maxNumberOfLines;

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
     */
    public MTTextFieldVarLines(PApplet applet, float x, float y, float width,
            float height, int maxNumberOfLines, IFont fontDefault) {
        super(applet, x, y, width, height, fontDefault);

        log.debug("Executing MTTextFieldVarLines(applet=" + applet //$NON-NLS-1$ 
                + ", x=" + x + ", y=" + y + ", width=" + width //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$ 
                + ", height=" + height + ", fontDefault=" + fontDefault //$NON-NLS-1$ //$NON-NLS-2$ 
                + ")"); //$NON-NLS-1$ 

        // Set max number of lines
        this.setMaxNumberOfLines(maxNumberOfLines);
    }

    /* ********Getters & Setters******** */

    /**
     * Returns the max number of lines.
     * 
     * @return the maxNumberOfLines
     */
    public int getMaxNumberOfLines() {
        return this.maxNumberOfLines;
    }

    /**
     * Sets the max number of lines. If maxNumberOfLines >
     * TEXT_FIELD_VAR_MAX_LINES, the latter is set.
     * 
     * @param maxNumberOfLines
     *            the maxNumberOfLines to set
     */
    public void setMaxNumberOfLines(int maxNumberOfLines) {

        log.debug("Entering setMaxNumberOfLines(=" + maxNumberOfLines + ")"); //$NON-NLS-1$//$NON-NLS-2$

        if (maxNumberOfLines > TEXT_FIELD_VAR_MAX_LINES) {

            log.warn("maxNumberOfLines > TEXT_FIELD_VAR_MAX_LINES, set to TEXT_FIELD_VAR_MAX_LINES(" + TEXT_FIELD_VAR_MAX_LINES + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            this.maxNumberOfLines = TEXT_FIELD_VAR_MAX_LINES;

        } else {

            this.maxNumberOfLines = maxNumberOfLines;
        }

        log.debug("Leaving setMaxNumberOfLines()"); //$NON-NLS-1$

    }

    /**
     * Returns if clear currently active.
     * 
     * @return the clearActive
     */
    public boolean isClearActive() {
        return this.clearActive;
    }

    /* ********Overridden methods******** */
    /**
     * Method called after a character is added to the MTTextFieldVarLines (e.g.
     * via a keyboard). Checks which line we are in (max. lines is
     * maxNumberOfLines), begins a new line if necessary, removes added
     * character if we have reached the maxNumberOfLines limit.
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

            // We still have more lines
            if (getLineCount() < this.maxNumberOfLines) {

                // Do nothing
                log.trace("Line break added!"); //$NON-NLS-1$

            } else { // we are in "no more lines allowed" land, remove \n

                log.trace("No more line breaks allowed, remove!"); //$NON-NLS-1$
                this.removeLastCharacter();
            }

        } else {
            log.trace("We are at line " + getLineCount() + 1); //$NON-NLS-1$
            // If we are still in the first line (getLineCount()==0)
            if (getLineCount() < (this.maxNumberOfLines - 1)) {

                // If the end of the line is reached,
                // start a new line with the new character
                float localWidth = this.getWidthXY(TransformSpace.LOCAL);
                localWidth += TEXT_FIELD_WIDTH_OFFSET;

                // We still have less than the maximum number of lines, so we
                // start a new line

                if (!this.clearActive && this.getText().length() > 0
                        && getLastCharEndPos() > localWidth) {
                    log.trace("Start a new line with the character" + character.getUnicode() + " at last char position " + getLastCharEndPos() + " and local width " + localWidth); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

                    this.removeLastCharacter();

                    // Start new line and add character there
                    this.appendCharByUnicode("\n"); //$NON-NLS-1$
                    this.appendCharByUnicode(character.getUnicode());

                }

            } else { // We are in line maxNumberOfLines (or more, which should
                     // not be the case)
                float localWidth = this.getWidthXY(TransformSpace.LOCAL);
                localWidth += TEXT_FIELD_WIDTH_OFFSET;

                // If the end of the line is reached, don't add any characters
                if (this.getText().length() > 0
                        && getLastCharEndPos() > localWidth) {
                    log.trace("We have reached the end of line maximum, don't add any more characters"); //$NON-NLS-1$

                    // No more chars are to be added
                    this.removeLastCharacter();
                }
            }
        }

        log.trace("Leaving characterAdded()"); //$NON-NLS-1$
    }

    /**
     * Resets the text field, clears all characters.
     * 
     */
    @Override
    public void clear() {
        this.clearActive = true;
        int i = 0;
        while (super.getCharacters().length != 0) {
            i++;
            this.removeLastCharacter();

        }
        log.trace("Clear has had " + i + "iterations"); //$NON-NLS-1$//$NON-NLS-2$
        this.clearActive = false;
    }

    /* *********Utility methods********* */
    /**
     * Gets the last character end position.
     * 
     * @return the last character end position
     */
    protected float getLastCharEndPos() {
        return this.getMaxLineWidth() + this.getScrollTextX();
    }

}
