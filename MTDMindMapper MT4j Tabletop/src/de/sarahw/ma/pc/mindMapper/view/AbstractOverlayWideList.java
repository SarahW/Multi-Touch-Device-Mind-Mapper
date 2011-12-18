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
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.widgets.MTList;
import org.mt4j.components.visibleComponents.widgets.MTSvg;
import org.mt4j.components.visibleComponents.widgets.buttons.MTSvgButton;
import org.mt4j.components.visibleComponents.widgets.keyboard.MTKeyboard;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.animation.Animation;
import org.mt4j.util.animation.AnimationEvent;
import org.mt4j.util.animation.IAnimation;
import org.mt4j.util.animation.IAnimationListener;
import org.mt4j.util.animation.MultiPurposeInterpolator;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;

/**
 * <p>
 * Abstract class representing a generic overlay containing rotation handles, a
 * button for closing the overlay, a headline text field and a scrollable
 * MTList. Concrete list behaviour/content must be added by an implementing
 * subclass.
 * </p>
 * 
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
@SuppressWarnings("synthetic-access")
public abstract class AbstractOverlayWideList extends AbstractOverlay {

    protected static Logger        log                                                = Logger.getLogger(AbstractOverlayWideList.class);

    /* *** Overlay constants *** */
    // (width/height optimized for application resolution 1920*1080px)
    // Child elements are positioned with relative values (percent)

    /** The maximum overlay width in pixels * 2 */
    public static final float      OVERLAY_WIDTH_MAX                                  = 793.0f;
    /** The maximum overlay height in pixels * 2 */
    public static final float      OVERLAY_HEIGHT_MAX                                 = 496.0f;
    /** The overlay width in percent of the application width */
    public static final float      OVERLAY_WIDTH_SCALE_TO_APP_PERCENT                 = 0.20651041666f;

    // OLD VALUES:
    // public static final float OVERLAY_WIDTH_MAX = 595.0f;
    // public static final float OVERLAY_HEIGHT_MAX = 372.0f;
    // public static final float OVERLAY_WIDTH_SCALE_TO_APP_PERCENT = 0.154947f;

    /* *** Overlay background constants *** */
    /** The overlay background image file path */
    private static final String    OVERLAY_BG_IMG_FILE_PATH                           = MT4jSettings
                                                                                              .getInstance()
                                                                                              .getDefaultSVGPath()
                                                                                              + "Overlay_BgPlainWide.svg";              //$NON-NLS-1$

    /** The overlay background name */
    protected static final String  OVERLAY_BACKGROUND_NAME                            = "overlayBackground";                            //$NON-NLS-1$

    /** The overlay background width offset in percent of the overlay width */
    private static final float     OVERLAY_BG_WIDTH_OFFSET_TO_OVERLAY_PERCENT         = 0.009436f;

    /* *** Overlay handle constants *** */
    /**
     * The overlay handle touch point shift to center amount in percent of the
     * overlay width
     */
    private static final float     OVERLAY_HANDLE_TOUCH_POINT_SHIFT_TO_CENTER_PERCENT = 0.04f;

    /** The overlay handle width in percent of the overlay width */
    private static final float     OVERLAY_HANDLE_WIDTH_TO_OVERLAY_PERCENT            = 0.1512605f;

    /* *** Close button constants *** */
    /** The close button svg image path */
    private static final String    BUTTON_CLOSE_IMG_PATH                              = MT4jSettings
                                                                                              .getInstance()
                                                                                              .getDefaultSVGPath()
                                                                                              + "Button_Close.svg";                     //$NON-NLS-1$

    /**
     * The close button x offset from the touch point north east in percent of
     * the close button width
     */
    private static final float     BUTTON_CLOSE_X_OFFSET_FROM_TPNE_PERCENT            = 0.40f;

    /**
     * The close button y offset from the touch point north east in percent of
     * the close button width
     */
    private static final float     BUTTON_CLOSE_Y_OFFSET_FROM_TPNE_PERCENT            = 0.40f;

    /** The close button width in percent of the overlay width */
    private static final float     BUTTON_CLOSE_WIDTH_SCALE_TO_OVERLAY_PERCENT        = 0.104201f;

    /* *** Headline constants *** */
    /** The headline width in percent of the overlay width */
    private static final float     HEADLINE_WIDTH_SCALE_TO_OVERLAY_PERCENT            = 0.73140f;
    /** The headline height in percent of the overlay height */
    private static final float     HEADLINE_HEIGHT_SCALE_TO_OVERLAY_PERCENT           = 0.21505f;
    /** The headline x offset in percent of the overlay width */
    private static final float     HEADLINE_OFFSET_TO_OVERLAY_PERCENT                 = 0.06218f;

    /** The headline font size */
    public static final int        HEADLINE_FONT_SIZE                                 = 24;
    // OLD VALUE:
    // public static final int HEADLINE_FONT_SIZE = 18;

    /** The headline font file name */
    public static final String     HEADLINE_FONT_FILE_NAME                            = "SansSerif";                                    //$NON-NLS-1$
    /** The headline font color */
    private static final MTColor   HEADLINE_FONT_COLOR                                = MindMapperColors.BROKEN_WHITE;

    /** The maximum number of lines in the headline text field */
    private static final int       HEADLINE_MAX_NUMBER_OF_LINES                       = 1;

    /** The headline inner padding left in percent of the headline width */
    private static final float     HEADLINE_INNER_PADDING_LEFT_PERCENT                = 0.02f;
    /** The headline inner padding top in percent of the headline width */
    private static final float     HEADLINE_INNER_PADDING_TOP_PERCENT                 = 0.075f;

    /** The headline style info (fill & stroke color, drawing options etc.) */
    private static final StyleInfo HEADLINE_STYLE_INFO                                = new StyleInfo(
                                                                                              MTColor.WHITE,
                                                                                              MTColor.WHITE,
                                                                                              true,
                                                                                              true,
                                                                                              true,
                                                                                              1.0f,
                                                                                              GL10.GL_TRIANGLE_FAN,
                                                                                              (short) 0);

    /* *** Overlay content list constants *** */
    /** The overlay content list width in percent of the overlay width */
    private static final float     LIST_WIDTH_SCALE_TO_OVERLAY_PERCENT                = 0.87f;
    /** The overlay content list height in percent of the overlay height */
    private static final float     LIST_HEIGHT_SCALE_TO_OVERLAY_PERCENT               = 0.74f;
    /** The overlay content list x offset in percent of the overlay width */
    private static final float     LIST_X_OFFSET_TO_OVERLAY_PERCENT                   = 0.063f;
    /** The overlay content list x offset in percent of the overlay height */
    private static final float     LIST_Y_OFFSET_TO_OVERLAY_PERCENT                   = 0.23f;

    /**
     * The overlay content list style info (fill & stroke color, drawing options
     * etc.)
     */
    private static final StyleInfo LIST_STYLE_INFO                                    = new StyleInfo(
                                                                                              MTColor.WHITE,
                                                                                              MTColor.WHITE,
                                                                                              true,
                                                                                              true,
                                                                                              true,
                                                                                              1.0f,
                                                                                              GL10.GL_TRIANGLE_FAN,
                                                                                              (short) 0);
    /** The overlay content list font name for the bigger font */
    public static final String     LIST_CONTENT_FONT_BIG_FILE_NAME                    = "SansSerif";                                    //$NON-NLS-1$ 
    /** The overlay content list font size for the bigger font */
    public static final int        LIST_CONTENT_FONT_BIG_SIZE                         = 17;
    // OLD VALUE:
    // public static final int LIST_CONTENT_FONT_BIG_SIZE = 15;

    /** The overlay content list font name for the smaller font */
    public static final String     LIST_CONTENT_FONT_SMALL_FILE_NAME                  = "SansSerif";                                    //$NON-NLS-1$

    /** The overlay content list font size for the smaller font */
    public static final int        LIST_CONTENT_FONT_SMALL_SIZE                       = 13;
    // OLD VALUE:
    // public static final int LIST_CONTENT_FONT_SMALL_SIZE = 11;

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication  mtApplication;

    /* *** Overlay handles *** */
    /** The container for the overlay handle svg images */
    private OverlayHandleContainer tpOverlayHandleContainer;

    /** The overlay handle touch point north east */
    private Vector3D               touchPointNorthEast;
    /** The overlay handle touch point south east */
    private Vector3D               touchPointSouthEast;
    /** The overlay handle touch point south west */
    private Vector3D               touchPointSouthWest;
    /** The overlay handle touch point north west */
    private Vector3D               touchPointNorthWest;

    /* *** Headline *** */
    /** The headline text field */
    private MTTextFieldVarLines    headlineTextField;
    private IFont                  headlineFont;

    /* *** Content list *** */
    /** The overlay list */
    private MTList                 overlayList;
    /** The overlay list default (bigger) font */
    private IFont                  listContentDefaultFont;
    /** The overlay list smaller font */
    private IFont                  listContentSmallerFont;

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new AbstractOverlayWideList.
     * 
     * @param pApplet
     *            the application instance
     * @param x
     *            the x position of the overlay
     * @param y
     *            the y position of the overlay
     * @param width
     *            the width of the overlay
     * @param height
     *            the height of the overlay
     * @param headlineFont
     *            the font for the headline
     * @param listContentDefaultFont
     *            the default font for the list content
     * @param listContentSmallerFont
     *            the smaller font for the list content
     */
    public AbstractOverlayWideList(PApplet pApplet, float x, float y,
            float width, float height, IFont headlineFont,
            IFont listContentDefaultFont, IFont listContentSmallerFont) {
        super(pApplet, x, y, width, height);

        log.debug("Executing AbstractOverlayWideList(pApplet=" + pApplet + ", x=" //$NON-NLS-1$ //$NON-NLS-2$
                + x + ", y=" + y + ", width=" + width + ", height=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + height + ", headlineFont=" + headlineFont //$NON-NLS-1$
                + ", listContentDefaultFont" + listContentDefaultFont //$NON-NLS-1$
                + ", listContentSmallerFont=" + listContentSmallerFont + ")"); //$NON-NLS-1$ //$NON-NLS-2$ 

        // Set app reference and fonts
        this.mtApplication = (AbstractMTApplication) pApplet;
        this.headlineFont = headlineFont;
        this.listContentDefaultFont = listContentDefaultFont;
        this.listContentSmallerFont = listContentSmallerFont;

        // Initialize
        initialize();

    }

    /* ********Getters & Setters******** */
    /**
     * Returns the north east touch point for the AbstractOverlayWideList
     * calculated at initialization.
     * 
     * @return the touchPointNorthEast
     */
    public Vector3D getTouchPointNorthEast() {
        return this.touchPointNorthEast;
    }

    /**
     * Returns the south east touch point for the AbstractOverlayWideList
     * calculated at initialization.
     * 
     * @return the touchPointSouthEast
     */
    public Vector3D getTouchPointSouthEast() {
        return this.touchPointSouthEast;
    }

    /**
     * Returns the south west touch point for the AbstractOverlayWideList
     * calculated at initialization.
     * 
     * @return the touchPointSouthWest
     */
    public Vector3D getTouchPointSouthWest() {
        return this.touchPointSouthWest;
    }

    /**
     * Returns the north west touch point for the AbstractOverlayWideList
     * calculated at initialization.
     * 
     * @return the touchPointNorthWest
     */
    public Vector3D getTouchPointNorthWest() {
        return this.touchPointNorthWest;
    }

    /**
     * Returns the overlay MTList instance.
     * 
     * @return the overlayList
     */
    public MTList getOverlayList() {
        return this.overlayList;
    }

    /**
     * Returns the headline text field instance.
     * 
     * @return the headlineTextField
     */
    public MTTextFieldVarLines getHeadlineTextField() {
        return this.headlineTextField;
    }

    /**
     * Returns the font for the headline.
     * 
     * @return the headlineFont
     */
    public IFont getHeadlineFont() {
        return this.headlineFont;
    }

    /**
     * Returns the list content default font.
     * 
     * @return the listContentDefaultFont
     */
    public IFont getListContentDefaultFont() {
        return this.listContentDefaultFont;
    }

    /**
     * Returns the list content smaller font.
     * 
     * @return the listContentSmallerFont
     */
    public IFont getListContentSmallerFont() {
        return this.listContentSmallerFont;
    }

    /* **********Object methods********** */
    /**
     * Initializes a new AbstractOverlayWideList
     * 
     */
    private void initialize() {

        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Calculate touch points
        calculateHandleTouchPoints();

        // Add handles
        addOverlayHandles();

        // Add background image
        addBackground();

        // Add headline
        addHeadlineTextField();

        // Add close button
        addCloseButton();

        // Add list
        addOverlayList();

        log.debug("Leaving initialize()"); //$NON-NLS-1$

    }

    /**
     * Calculates the default touch points for the menu.
     * 
     */
    private void calculateHandleTouchPoints() {
        log.debug("Entering calculateHandleTouchPoints()"); //$NON-NLS-1$

        // Calculate all default touch points
        // We get the overlay vertices
        if (this.hasBounds()) {

            Vector3D tpNorthWestTemp;
            Vector3D tpNorthEastTemp;
            Vector3D tpSouthEastTemp;
            Vector3D tpSouthWestTemp;

            Vector3D[] vertices = this.getBounds().getVectorsGlobal();

            if (vertices.length == 4) {

                // We get all four vertices in the order NORTH_WEST, NORTH_EAST,
                // ...
                tpNorthWestTemp = vertices[0].getCopy();
                tpNorthEastTemp = vertices[1].getCopy();
                tpSouthEastTemp = vertices[2].getCopy();
                tpSouthWestTemp = vertices[3].getCopy();

                float overlayWidth = this.getWidthXY(TransformSpace.GLOBAL);
                float shiftToCenter = overlayWidth
                        * OVERLAY_HANDLE_TOUCH_POINT_SHIFT_TO_CENTER_PERCENT;

                // We shift the position vertices to the center for a better
                // look
                Vector3D toCenterFromNorthWest = new Vector3D(
                        tpNorthWestTemp.getX() + shiftToCenter,
                        tpNorthWestTemp.getY() + shiftToCenter,
                        tpNorthWestTemp.getZ());

                Vector3D toCenterFromNorthEast = new Vector3D(
                        tpNorthEastTemp.getX() - shiftToCenter,
                        tpNorthEastTemp.getY() + shiftToCenter,
                        tpNorthEastTemp.getZ());

                Vector3D toCenterFromSouthEast = new Vector3D(
                        tpSouthEastTemp.getX() - shiftToCenter,
                        tpSouthEastTemp.getY() - shiftToCenter,
                        tpSouthEastTemp.getZ());

                Vector3D toCenterFromSouthWest = new Vector3D(
                        tpSouthWestTemp.getX() + shiftToCenter,
                        tpSouthWestTemp.getY() - shiftToCenter,
                        tpSouthWestTemp.getZ());

                // Set positions
                this.touchPointNorthWest = toCenterFromNorthWest;
                this.touchPointNorthEast = toCenterFromNorthEast;
                this.touchPointSouthEast = toCenterFromSouthEast;
                this.touchPointSouthWest = toCenterFromSouthWest;

            } else {
                log.error("Error: wrong number of bounding shape vertices! Must be four!"); //$NON-NLS-1$
            }

        } else {
            log.error("Error: the overlay (MTRectangle has no bounds!"); //$NON-NLS-1$
        }

        log.debug("Leaving calculateHandleTouchPoints()"); //$NON-NLS-1$

    }

    /**
     * Adds a OverlayHandleContainer with four menu handles for the previously
     * calculated touch points.
     * 
     * @return true if adding handle touch point markers was successful
     */
    private boolean addOverlayHandles() {

        log.debug("Entering addOverlayHandles()"); //$NON-NLS-1$ 

        // If all required touch points are valid
        if (this.getTouchPointNorthEast() != null
                && this.getTouchPointSouthEast() != null
                && this.getTouchPointSouthWest() != null
                && this.getTouchPointNorthWest() != null) {

            // Add new marker container with touch points
            this.tpOverlayHandleContainer = new OverlayHandleContainer(
                    this.mtApplication, this,
                    this.getWidthXY(TransformSpace.GLOBAL)
                            * OVERLAY_HANDLE_WIDTH_TO_OVERLAY_PERCENT,
                    this.getTouchPointNorthEast(),
                    this.getTouchPointSouthEast(),
                    this.getTouchPointSouthWest(),
                    this.getTouchPointNorthWest());

            // Add default drag/rotate listeners to handle container
            // that sends events to this AbstractOverlayWideList
            ToolsEventHandling
                    .removeDragProcessorsAndListeners(this.tpOverlayHandleContainer);

            this.tpOverlayHandleContainer
                    .registerInputProcessor(new DragProcessor(
                            this.mtApplication));
            this.tpOverlayHandleContainer.addGestureListener(
                    DragProcessor.class, new DragActionCheckBorders(this,
                            this.mtApplication, CHECK_BORDER_OFFSET));

            ToolsEventHandling
                    .removeRotateProcessorsAndListeners(this.tpOverlayHandleContainer);

            this.tpOverlayHandleContainer
                    .registerInputProcessor(new RotateProcessor(
                            this.mtApplication));
            this.tpOverlayHandleContainer.addGestureListener(
                    RotateProcessor.class, new RotateActionCheckBorders(this,
                            this.mtApplication, CHECK_BORDER_OFFSET));

            // Add to this menu
            this.addChild(this.tpOverlayHandleContainer);

            log.debug("Leaving addOverlayHandles(): " //$NON-NLS-1$
                    + "Intersection points are: North east: " + this.getTouchPointNorthEast() //$NON-NLS-1$
                    + ", south east: " + this.getTouchPointSouthEast() //$NON-NLS-1$
                    + ", south west: " //$NON-NLS-1$
                    + this.getTouchPointSouthWest() + ", north west: " //$NON-NLS-1$
                    + this.getTouchPointNorthWest());

            return true;
        }
        log.error("Leaving addOverlayHandles(): " //$NON-NLS-1$
                + "false, touch points not successfully set!"); //$NON-NLS-1$
        return false;

    }

    /**
     * Adds a background MTSvg image to the AbstractOverlayWideList.
     * 
     */
    private void addBackground() {

        log.debug("Entering addBackground()"); //$NON-NLS-1$

        // Add "background" rectangle (slightly bigger than the MTCircleMenu
        // circle)
        MTSvg background = new MTSvg(this.mtApplication,
                OVERLAY_BG_IMG_FILE_PATH);

        // Position at center of this Menu
        background.setPositionGlobal(this.getCenterPointGlobal());

        // Set width (width of the Overlay + (width of the Overlay /
        // BGoffsetScale)
        background
                .setWidthXYGlobal((this.getWidthXY(TransformSpace.GLOBAL))
                        + ((this.getWidthXY(TransformSpace.GLOBAL) * OVERLAY_BG_WIDTH_OFFSET_TO_OVERLAY_PERCENT)));

        log.trace("Background width is now " + background.getWidthXYGlobal()); //$NON-NLS-1$

        // Set background pickable false so that it doesn't get dragged away
        // when touched
        background.setPickable(false);

        // Set name for easier manipulation by subclasses
        background.setName(OVERLAY_BACKGROUND_NAME);

        // Add to menu
        this.addChild(background);

        log.debug("Leaving addBackground()"); //$NON-NLS-1$

    }

    /**
     * Adds an empty headline text field to be filled by the implementing
     * subclass.
     */
    private void addHeadlineTextField() {
        log.debug("Entering addHeadlineTextField()"); //$NON-NLS-1$

        // Get the first vertex of the bounding shape
        if (this.hasBounds()) {

            if (this.headlineFont != null) {
                Vector3D upperLeftVertex = (this.getBounds().getVectorsGlobal())[0]
                        .getCopy();

                float overlayWidth = this.getWidthXY(TransformSpace.GLOBAL);
                float overlayHeight = this.getWidthXY(TransformSpace.GLOBAL);

                // Add a new one line text field
                MTTextFieldVarLines textField = new MTTextFieldVarLines(
                        this.mtApplication, upperLeftVertex.getX(),
                        upperLeftVertex.getY(), overlayWidth
                                * HEADLINE_WIDTH_SCALE_TO_OVERLAY_PERCENT,
                        overlayHeight
                                * HEADLINE_HEIGHT_SCALE_TO_OVERLAY_PERCENT,
                        HEADLINE_MAX_NUMBER_OF_LINES, this.headlineFont);

                // Set anchor upper left
                textField.setAnchor(PositionAnchor.UPPER_LEFT);

                // Reposition
                textField.translate(new Vector3D(overlayWidth
                        * HEADLINE_OFFSET_TO_OVERLAY_PERCENT, 0, 0));

                // Set padding
                textField.setInnerPaddingLeft((new Float(textField
                        .getWidthXY(TransformSpace.GLOBAL)
                        * HEADLINE_INNER_PADDING_LEFT_PERCENT).intValue()));
                textField.setInnerPaddingTop((new Float(textField
                        .getWidthXY(TransformSpace.GLOBAL)
                        * HEADLINE_INNER_PADDING_TOP_PERCENT).intValue()));

                // Set style info
                textField.setStyleInfo(HEADLINE_STYLE_INFO);

                // Set headline color
                textField.setFontColor(HEADLINE_FONT_COLOR);

                // Set pickable false
                textField.setPickable(false);

                this.headlineTextField = textField;

                // Add to overlay
                this.addChild(textField);

            } else {
                log.error("Error! The HeadlineFont has not been initialized correctly!"); //$NON-NLS-1$
            }

        } else {
            log.error("Error! The AbstractOverlayWideList has no bounds!"); //$NON-NLS-1$
        }

        log.debug("Leaving addHeadlineTextField()"); //$NON-NLS-1$

    }

    /**
     * Adds a close button.
     * 
     */
    private void addCloseButton() {

        log.debug("Entering addCloseButton()"); //$NON-NLS-1$

        // Create close button
        MTSvgButton closeButton = new MTSvgButton(this.mtApplication,
                BUTTON_CLOSE_IMG_PATH);

        // Remove all default gesture listeners
        ToolsEventHandling.removeScaleProcessorsAndListeners(closeButton);
        ToolsEventHandling.removeDragProcessorsAndListeners(closeButton);
        ToolsEventHandling.removeRotateProcessorsAndListeners(closeButton);

        // Set width
        float parentWidth = this.getWidthXY(TransformSpace.GLOBAL);
        closeButton.setWidthXYGlobal(parentWidth
                * BUTTON_CLOSE_WIDTH_SCALE_TO_OVERLAY_PERCENT);

        this.addChild(closeButton);

        // Position at touch point north east
        closeButton.setPositionGlobal(this.getTouchPointNorthEast());

        // Reposition
        closeButton
                .translate(new Vector3D(
                        -(closeButton.getWidthXYGlobal() * BUTTON_CLOSE_X_OFFSET_FROM_TPNE_PERCENT),
                        closeButton.getHeightXYGlobal()
                                * BUTTON_CLOSE_Y_OFFSET_FROM_TPNE_PERCENT, 0));

        // Set bounding behaviour
        closeButton.setBoundsPickingBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);

        // Add tap gesture listener
        addTapGestureListener(closeButton);

        log.debug("Leaving addCloseButton()"); //$NON-NLS-1$

    }

    /**
     * Adds the overlay list, to be filled with list cells by the implementing
     * subclass!
     * 
     */
    private void addOverlayList() {

        log.debug("Entering addOverlayList()"); //$NON-NLS-1$

        // Get the first vertex of the bounding shape
        if (this.hasBounds()) {

            Vector3D upperLeftVertex = (this.getBounds().getVectorsGlobal())[0]
                    .getCopy();

            // Get OverlayWidht
            float overlayWidth = this.getWidthXY(TransformSpace.GLOBAL);
            float overlayHeight = this.getHeightXY(TransformSpace.GLOBAL);

            // Create new list
            MTList overlayList = new MTList(this.mtApplication,
                    upperLeftVertex.getX(), upperLeftVertex.getY(),
                    overlayWidth * LIST_WIDTH_SCALE_TO_OVERLAY_PERCENT,
                    overlayHeight * LIST_HEIGHT_SCALE_TO_OVERLAY_PERCENT);

            // Set style info
            overlayList.setStyleInfo(LIST_STYLE_INFO);

            // Add to overlay
            this.addChild(overlayList);

            // Reposition to correct position
            overlayList.translate(new Vector3D(overlayWidth
                    * LIST_X_OFFSET_TO_OVERLAY_PERCENT, overlayHeight
                    * LIST_Y_OFFSET_TO_OVERLAY_PERCENT, 0));

            // Set member
            this.overlayList = overlayList;

        } else {
            log.error("Error! The AbstractOverlayWideList has no bounds!"); //$NON-NLS-1$
        }

        log.debug("Leaving addOverlayList()"); //$NON-NLS-1$

    }

    /**
     * <p>
     * Sets the width of the component relative to the parent. Used for
     * animation behavior on close.
     * </p>
     * 
     * <p>
     * Adapted from
     * {@link org.mt4j.components.visibleComponents.widgets.keyboard.MTKeyboard}
     * </p>
     * 
     * @param width
     *            the width
     * @return true, if width > 0
     * 
     * @see MTKeyboard#setWidthXYRelativeToParent(float)
     */
    private boolean setWidthRelativeToParent(float width) {

        log.debug("Entering setWidthRelativeToParent(width=" + width + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (width > 0) {
            Vector3D centerPoint;
            if (this.hasBounds()) {
                centerPoint = this.getBounds().getCenterPointLocal();
                centerPoint.transform(this.getLocalMatrix());
            } else {
                centerPoint = this.getCenterPointGlobal();
                centerPoint.transform(this.getGlobalInverseMatrix());
            }
            this.scale(
                    width
                            * (1 / this
                                    .getWidthXY(TransformSpace.RELATIVE_TO_PARENT)),
                    width
                            * (1 / this
                                    .getWidthXY(TransformSpace.RELATIVE_TO_PARENT)),
                    1, centerPoint);

            log.debug("Leaving setWidthRelativeToParent(): true"); //$NON-NLS-1$
            return true;
        }

        log.debug("Leaving setWidthRelativeToParent(): false"); //$NON-NLS-1$ 
        return false;
    }

    /* *********Listener methods********* */
    /**
     * Adds a new tap gesture listener to a svg button for the close action.
     * 
     * @param svgButton
     *            the SVG Button instance
     */
    private void addTapGestureListener(MTSvgButton svgButton) {

        log.debug("Entering addTapGestureListener(svgButton=" + svgButton + ")"); //$NON-NLS-1$ //$NON-NLS-2$ 

        svgButton.addGestureListener(TapProcessor.class,
                new IGestureEventListener() {

                    @Override
                    public boolean processGestureEvent(MTGestureEvent ge) {
                        TapEvent te = (TapEvent) ge;
                        if (te.isTapped()) {
                            log.trace("Close Button has been tapped!"); //$NON-NLS-1$
                            onCloseButtonTapped();
                        }
                        return false;
                    }

                });

        log.debug("Leaving addTapGestureListener()"); //$NON-NLS-1$

    }

    /**
     * <p>
     * Shows a disappearing animation and closes the AbstractOverlayWideList
     * instance.
     * </p>
     * 
     * 
     * <p>
     * Adapted from
     * {@link org.mt4j.components.visibleComponents.widgets.keyboard.MTKeyboard}
     * </p>
     * 
     * @see MTKeyboard#close()
     */
    private void onCloseButtonTapped() {

        log.debug("Entering onCloseButtonTapped()"); //$NON-NLS-1$  

        // Get width
        float width = this.getWidthXY(TransformSpace.RELATIVE_TO_PARENT);

        // Create new animation
        IAnimation overlayCloseAnim = new Animation(
                "Status Message Fade", new MultiPurposeInterpolator(width, 1, 300, 0.2f, 0.5f, 1), this); //$NON-NLS-1$

        // Add Animation listener to animation
        overlayCloseAnim.addAnimationListener(new IAnimationListener() {

            @Override
            public void processAnimationEvent(AnimationEvent ae) {
                switch (ae.getId()) {
                    case AnimationEvent.ANIMATION_STARTED:
                    case AnimationEvent.ANIMATION_UPDATED:

                        // Get animation value
                        float currentVal = ae.getAnimation().getValue();

                        // Set StatusMessageDialog width the animation value
                        setWidthRelativeToParent(currentVal);

                        break;
                    case AnimationEvent.ANIMATION_ENDED:

                        // Set AbstractOverlayWideList instance invisible and
                        // destroy
                        setVisible(false);
                        destroy();

                        break;
                    default:
                        break;
                }
            }
        });

        // Start Animation
        overlayCloseAnim.start();

        log.debug("Leaving onCloseButtonTapped()"); //$NON-NLS-1$  

    }

}
