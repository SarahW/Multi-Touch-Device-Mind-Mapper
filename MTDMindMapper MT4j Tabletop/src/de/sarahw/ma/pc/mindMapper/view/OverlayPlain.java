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
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.StyleInfo;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.IFont;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;

/**
 * Class OverlayPlain representing a simple overlay containing a text field.
 * Used for displaying "saving" and "loading" statuses, added to the canvas at
 * initialization and set visible when needed.
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class OverlayPlain extends AbstractOverlay {

    protected static Logger        log                                           = Logger.getLogger(OverlayPlain.class);

    /* *** OverlayPlain constants *** */
    // (width/height optimized for application resolution 1920*1080px)
    // Child elements are positioned with relative values (percent)

    /** The maximum overlay width in pixel * 2 */
    public static final float      OVERLAY_PLAIN_WIDTH_DEFAULT                   = 400.0f;
    /** The overlay width in percent of the application width */
    public static final float      OVERLAY_PLAIN_WIDTH_SCALE_TO_APP_PERCENT      = 0.25f;
    /** The maximum overlay width in pixel * 2 */
    public static final float      OVERLAY_PLAIN_HEIGHT_DEFAULT                  = 200.0f;

    /* *** OverlayPlain background constants *** */
    /** The overlay (background) fill color */
    private static final MTColor   OVERLAY_PLAIN_FILL_COLOR                      = MindMapperColors.VERY_DARK_GREY_SLIGHT_TRANS; ;
    /** The overlay (background) stroke color */
    private static final MTColor   OVERLAY_PLAIN_STROKE_COLOR                    = MindMapperColors.BLACK_SLIGHT_TRANS;          ;
    /** The overlay (background) style info */
    private static final StyleInfo OVERLAY_PLAIN_STYLE_INFO                      = new StyleInfo(
                                                                                         OVERLAY_PLAIN_FILL_COLOR,
                                                                                         OVERLAY_PLAIN_STROKE_COLOR,
                                                                                         true,
                                                                                         false,
                                                                                         false,
                                                                                         1.0f,
                                                                                         GL10.GL_TRIANGLE_FAN,
                                                                                         (short) 0);

    /* *** OverlayPlain text constants *** */
    /** The overlay font file name */
    public static final String     OVERLAY_PLAIN_FONT_FILE_NAME                  = "SansSerif";                                 //$NON-NLS-1$
    /** The overlay font size */
    public static final int        OVERLAY_PLAIN_FONT_SIZE                       = 30;
    /** The overlay font color */
    private static final MTColor   OVERLAY_PLAIN_FONT_COLOR                      = MindMapperColors.BROKEN_WHITE;
    /** The maximum number of lines of the text field */
    private static final int       OVERLAY_MAX_NUMBER_OF_LINES                   = 0;
    /** The text field inner padding left in percent of the text field width */
    private static final float     OVERLAY_PLAIN_TEXT_INNER_PADDING_LEFT_PERCENT = 0.1f;
    /** The text field inner padding top in percent of the text field height */
    private static final float     OVERLAY_PLAIN_TEXT_INNER_PADDING_TOP_PERCENT  = 0.3f;

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication  mtApplication;
    /** The currently active mindMapScene */
    private MindMapScene           mindMapScene;

    /* *** Text *** */
    /** The overlay plain text to set at initialization */
    private String                 textToSet;
    /** The overlay plain text field */
    private MTTextFieldVarLines    statusTextField;
    /** The overlay plain text font */
    private IFont                  overlayPlainFont;

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new OverlayPlain.
     * 
     * @param pApplet
     *            the application instance
     * @param x
     *            the overlay x position
     * @param y
     *            the overlay y position
     * @param width
     *            the overlay width
     * @param height
     *            the overlay height
     * @param text
     *            the overlay text
     * @param font
     *            the overlay font
     * @param mindMapScene
     *            the mindMapScene
     */
    public OverlayPlain(PApplet pApplet, float x, float y, float width,
            float height, String text, IFont font, MindMapScene mindMapScene) {
        super(pApplet, x, y, width, height);

        log.debug("Executing OverlayPlain(pApplet=" + pApplet + ", x=" + x //$NON-NLS-1$ //$NON-NLS-2$
                + ", y=" + y + ", width=" + width + ", height=" + height //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                + ", text=" + text + ")"); //$NON-NLS-1$//$NON-NLS-2$

        // Set app reference and fonts
        this.mtApplication = (AbstractMTApplication) pApplet;
        this.textToSet = text;
        this.overlayPlainFont = font;
        this.mindMapScene = mindMapScene;

        // Initialize
        initialize();

    }

    /* ********Getters & Setters******** */

    /**
     * Returns the text to set.
     * 
     * @return the textToSet
     */
    public String getTextToSet() {
        return this.textToSet;
    }

    /* **********Object methods********** */
    /**
     * Initializes a new OverlayPlain
     * 
     */

    private void initialize() {

        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Remove scaling processor and listener
        ToolsEventHandling.removeScaleProcessorsAndListeners(this);

        // Set style info
        setStyleInfo(OVERLAY_PLAIN_STYLE_INFO);

        // Set pickable false
        setPickable(false);

        // Add text field
        addTextField();

        log.debug("Leaving initialize()"); //$NON-NLS-1$

    }

    /**
     * Adds a text field with the text specified at initialization.
     * 
     */
    private void addTextField() {

        log.debug("Entering addTextField()"); //$NON-NLS-1$

        if (this.textToSet != null) {

            // Create two MTTextFields restricted to two lines
            this.statusTextField = new MTTextFieldVarLines(this.mtApplication,
                    0, 0, this.getWidthXYGlobal(), this.getHeightXYGlobal(),
                    OVERLAY_MAX_NUMBER_OF_LINES, this.overlayPlainFont);

            this.statusTextField.unregisterAllInputProcessors();
            this.statusTextField.setPickable(false);
            this.statusTextField.setNoFill(true);
            this.statusTextField.setNoStroke(true);
            this.statusTextField.setText(this.textToSet);

            // Set font color
            this.statusTextField.setFontColor(OVERLAY_PLAIN_FONT_COLOR);

            // Set padding
            this.statusTextField.setInnerPaddingLeft((new Float(
                    this.statusTextField.getWidthXY(TransformSpace.GLOBAL)
                            * OVERLAY_PLAIN_TEXT_INNER_PADDING_LEFT_PERCENT)
                    .intValue()));
            this.statusTextField.setInnerPaddingTop((new Float(
                    this.statusTextField.getHeightXY(TransformSpace.GLOBAL)
                            * OVERLAY_PLAIN_TEXT_INNER_PADDING_TOP_PERCENT)
                    .intValue()));

            // Add to overlay
            this.addChild(this.statusTextField);

            // Set position
            this.statusTextField.setPositionGlobal(this.getCenterPointGlobal());

        } else {
            log.error("Text provided at initialization invalid (null)"); //$NON-NLS-1$
        }

        log.debug("Leaving addTextField()"); //$NON-NLS-1$
    }

    /* ********Overridden methods******** */
    /**
     * Overrides the method setVisible() from MTComponent. Used to set the
     * components visibility and lock the canvas depending on it.
     * 
     * @see org.mt4j.components.MTComponent#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {

        log.debug("Entering setVisible()"); //$NON-NLS-1$

        // Set visibility
        super.setVisible(visible);

        if (this.mindMapScene != null) {

            if (this.isVisible()) {
                // Lock canvas
                this.mindMapScene.lockCanvas(true);

            } else {
                // Unlock canvas
                this.mindMapScene.lockCanvas(false);

            }
        }

        log.debug("Leaving setVisible()"); //$NON-NLS-1$
    }

}
