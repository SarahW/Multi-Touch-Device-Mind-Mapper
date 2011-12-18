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
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.widgets.MTSvg;
import org.mt4j.components.visibleComponents.widgets.keyboard.MTKeyboard;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;

/**
 * *
 * <p>
 * AbstractStatusMessageDialog class. Represents a abstract status message view
 * (subclass of MTRoundRectangle) that contains two MTTextFieldVarLines
 * components with a status message text. Buttons and close behaviour must be
 * added by implementing subclass.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public abstract class AbstractStatusMessageDialog extends MTRoundRectangle {

    private static Logger                 log                                          = Logger.getLogger(AbstractStatusMessageDialog.class);

    /* *** Status message dialog constants *** */
    // (width/height optimized for application resolution 1920*1080px)
    // Child elements are positioned with relative values (percent)

    /** The maximum status message dialog width in pixels * 2 */
    public static final float             STATUS_MSG_WIDTH_DEFAULT                     = 800.0f;

    /** The status message dialog width in percent of the application width */
    public static final float             STATUS_MSG_WIDTH_SCALE_TO_APP_PERCENT        = 0.40f;

    /** The maximum status message dialog height in pixels * 2 */
    public static final float             STATUS_MSG_HEIGHT_DEFAULT                    = 400.0f;

    /** The number of segments for the round rectangle drawing */
    private static final int              STATUS_MSG_SEGMENTS_DEFAULT                  = 50;

    /** The round rectangle arc width */
    private static final float            STATUS_MSG_ARC_WIDTH                         = 20;
    /** The round rectangle arc height */
    private static final float            STATUS_MSG_ARC_HEIGHT                        = 20;

    /** The status message (background) fill color */
    private static final MTColor          STATUS_MSG_FILL_COLOR                        = MindMapperColors.VERY_DARK_GREY_SLIGHT_TRANS;
    /** The status message (background) stroke color */
    private static final MTColor          STATUS_MSG_STROKE_COLOR                      = MindMapperColors.BLACK_SLIGHT_TRANS;
    /** The status message style info */
    private static final StyleInfo        STATUS_MSG_STYLE_INFO                        = new StyleInfo(
                                                                                               STATUS_MSG_FILL_COLOR,
                                                                                               STATUS_MSG_STROKE_COLOR,
                                                                                               true,
                                                                                               false,
                                                                                               false,
                                                                                               1.0f,
                                                                                               GL10.GL_TRIANGLE_FAN,
                                                                                               (short) 0);
    /* *** Status message text field constants *** */
    /** The status message text font color */
    private static final MTColor          STATUS_MSG_FONT_COLOR                        = MindMapperColors.BROKEN_WHITE;

    /** The status message text font file name */
    public static final String            STATUS_MSG_FONT_FILE_NAME                    = "SansSerif";                                        //$NON-NLS-1$

    /** The status message font size for the very small font */
    public static final int               STATUS_MSG_FONT_SIZE_VERY_SMALL              = 20;
    /** The status message font size for the small font */
    public static final int               STATUS_MSG_FONT_SIZE_SMALL                   = 30;
    /** The status message font size for the medium font */
    public static final int               STATUS_MSG_FONT_SIZE_MEDIUM                  = 40;
    /** The status message font size for the big font */
    public static final int               STATUS_MSG_FONT_SIZE_BIG                     = 50;

    /**
     * The status mesage text field height in percent of the status message
     * height
     */
    private static final float            STATUS_MSG_TEXT_HEIGHT_SCALE_TO_SMSG_PERCENT = 0.25f;

    /**
     * The status message text y offset in percent of the status message
     * height/4
     */
    private static final float            STATUS_MSG_TEXT_Y_OFFSET_PERCENT             = 0.1f;
    /**
     * The status message text y offset in percent of the status message width
     */
    private static final float            STATUS_MSG_TEXT_X_OFFSET_PERCENT             = 0.05f;

    /** The status message text field style info */
    private static final StyleInfo        STATUS_MSG_TEXT_STYLE_INFO                   = new StyleInfo(
                                                                                               STATUS_MSG_FILL_COLOR,
                                                                                               STATUS_MSG_STROKE_COLOR,
                                                                                               true,
                                                                                               true,
                                                                                               true,
                                                                                               1.0f,
                                                                                               GL10.GL_TRIANGLE_FAN,
                                                                                               (short) 0);

    /* *** Status message icon constants *** */
    /**
     * The status message icon area width in percent of the status message width
     */
    private static final float            ICON_AREA_WIDTH_SCALE_TO_STMSG_PERCENT       = 0.125f;
    /**
     * The status message icon height in percent of the status message text
     * field height
     */
    private static final float            ICON_HEIGHT_SCALE_TO_TEXT_PERCENT            = 0.83f;

    /** The svg image path for the warning icon */
    private static final String           ICON_WARNING_IMG_PATH                        = MT4jSettings
                                                                                               .getInstance()
                                                                                               .getDefaultSVGPath()
                                                                                               + "Icon_Warning.svg";                         //$NON-NLS-1$
    /** The svg image path for the information icon */
    private static final String           ICON_INFO_IMG_PATH                           = MT4jSettings
                                                                                               .getInstance()
                                                                                               .getDefaultSVGPath()
                                                                                               + "Icon_Info.svg";                            //$NON-NLS-1$
    /** The svg image path for the error icon */
    private static final String           ICON_ERROR_IMG_PATH                          = MT4jSettings
                                                                                               .getInstance()
                                                                                               .getDefaultSVGPath()
                                                                                               + "Icon_Error.svg";                           //$NON-NLS-1$
    /** The svg image path for the question icon */
    private static final String           ICON_QUESTION_IMG_PATH                       = MT4jSettings
                                                                                               .getInstance()
                                                                                               .getDefaultSVGPath()
                                                                                               + "Icon_Question.svg";                        //$NON-NLS-1$ 
    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication         mtApplication;
    /** The current active mindMapScene */
    private MindMapScene                  mindMapScene;

    /* *** Status Message text fields *** */
    /** The status message text field north featuring font switching */
    private MTTextFieldVarLinesFontSwitch statusTextFieldNorth;
    /** The status message text field south featuring font switching */
    private MTTextFieldVarLinesFontSwitch statusTextFieldSouth;
    /** The maximum number of lines for the status message text fields */
    private int                           numberOfLines;
    /** The status message text, set on initialization */
    private String                        statusTextToSet;
    /** The status message default (bigger) font */
    private IFont                         statusMsgFontBig;
    /** The status message smaller font */
    private IFont                         statusMsgFontSmall;

    /** The status message type (error, warning, info, question) */
    private EStatusMessageType            statusType;

    /* ***********Constructors*********** */

    /**
     * Constructor. Instantiates a new status message at the given position with
     * the given height and width etc.
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
    public AbstractStatusMessageDialog(PApplet pApplet,
            MindMapScene mindMapScene, float x, float y, float width,
            float height, EStatusMessageType statusType, String text,
            IFont fontSmall, IFont fontBig, int numberOfLines) {
        super(pApplet, x, y, 0, width, height, STATUS_MSG_ARC_WIDTH,
                STATUS_MSG_ARC_HEIGHT, STATUS_MSG_SEGMENTS_DEFAULT);

        log.debug("Executing AbstractStatusMessageDialog(pApplet=" + pApplet //$NON-NLS-1$ 
                + ", x=" + x + ", mindMapScene=" + mindMapScene + ", y=" + y //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
                + ", width=" + width + ", \nheight=" + height + ", statusType=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + statusType + ", text=" + text + ", fontSmall=" + fontSmall //$NON-NLS-1$ //$NON-NLS-2$
                + ", fontBig=" + fontBig + ", numberOfLines=" + numberOfLines //$NON-NLS-1$ //$NON-NLS-2$
                + ")"); //$NON-NLS-1$ 

        // Store application reference
        this.mtApplication = (AbstractMTApplication) pApplet;
        this.mindMapScene = mindMapScene;

        // Store status type, text and fonts
        this.statusType = statusType;
        this.statusTextToSet = text;
        this.statusMsgFontBig = fontBig;
        this.statusMsgFontSmall = fontSmall;
        this.numberOfLines = numberOfLines;

        // Initialize StatusMessageDialog
        initialize();

    }

    /* ********Getters & Setters******** */

    /**
     * Returns the current application instance.
     * 
     * @return the mtApplication
     */
    public AbstractMTApplication getMtApplication() {
        return this.mtApplication;
    }

    /**
     * Returns the current mindMapScene instance.
     * 
     * @return the mindMapScene
     */
    public MindMapScene getMindMapScene() {
        return this.mindMapScene;
    }

    /**
     * Returns the northern status message text field.
     * 
     * @return the statusTextFieldNorth
     */
    public MTTextFieldVarLinesFontSwitch getStatusTextFieldNorth() {
        return this.statusTextFieldNorth;
    }

    /**
     * Returns the southern status message text field.
     * 
     * @return the statusTextFieldSouth
     */
    public MTTextFieldVarLinesFontSwitch getStatusTextFieldSouth() {
        return this.statusTextFieldSouth;
    }

    /**
     * Returns the status type of the status message.
     * 
     * @return the statusType
     */
    public EStatusMessageType getStatusType() {
        return this.statusType;
    }

    /**
     * Return the bigger status message font.
     * 
     * @return the statusMsgFontBig
     */
    public IFont getStatusMsgFontBig() {
        return this.statusMsgFontBig;
    }

    /**
     * Returns the smaller status message font.
     * 
     * @return the statusMsgFontSmall
     */
    public IFont getStatusMsgFontSmall() {
        return this.statusMsgFontSmall;
    }

    /* *************Delegates************** */

    /**
     * Returns the current status message text via one of the text fields.
     * 
     * @return the current status message text
     */
    public String getStatusMessageFieldText() {
        return this.statusTextFieldNorth.getText();
    }

    /* *********Utility methods********* */
    /**
     * Initializes the StatusMessageDialog object. Adds/Removes event listeners,
     * and adds text fields and buttons.
     */
    private void initialize() {

        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Set style info
        this.setStyleInfo(STATUS_MSG_STYLE_INFO);

        // Remove all default gesture listeners
        ToolsEventHandling.removeScaleProcessorsAndListeners(this);
        ToolsEventHandling.removeDragProcessorsAndListeners(this);
        ToolsEventHandling.removeRotateProcessorsAndListeners(this);

        // Add text fields
        addTextFields();

        // Add icon
        addStatusMsgIcon();

        // Lock canvas
        this.mindMapScene.lockCanvas(true);

        log.debug("Leaving initialize()"); //$NON-NLS-1$

    }

    /**
     * Adds two MTTextFieldVarLines mirrored at the axis.
     * 
     */
    private void addTextFields() {

        log.debug("Entering addTextFields()"); //$NON-NLS-1$

        // Create two MTTextFields restricted to two lines
        this.statusTextFieldNorth = new MTTextFieldVarLinesFontSwitch(
                this.mtApplication, 0, 0, this.getWidthXYGlobal(),
                (this.getHeightXYGlobal())
                        * STATUS_MSG_TEXT_HEIGHT_SCALE_TO_SMSG_PERCENT,
                this.numberOfLines, this.statusMsgFontBig,
                this.statusMsgFontSmall);

        this.statusTextFieldSouth = new MTTextFieldVarLinesFontSwitch(
                this.mtApplication, 0, 0, this.getWidthXYGlobal(),
                (this.getHeightXYGlobal())
                        * STATUS_MSG_TEXT_HEIGHT_SCALE_TO_SMSG_PERCENT,
                this.numberOfLines, this.statusMsgFontBig,
                this.statusMsgFontSmall);

        // Remove all default gesture listeners
        ToolsEventHandling
                .removeScaleProcessorsAndListeners(this.statusTextFieldNorth);
        ToolsEventHandling
                .removeDragProcessorsAndListeners(this.statusTextFieldNorth);
        ToolsEventHandling
                .removeRotateProcessorsAndListeners(this.statusTextFieldNorth);

        // Remove all default gesture listeners
        ToolsEventHandling
                .removeScaleProcessorsAndListeners(this.statusTextFieldSouth);
        ToolsEventHandling
                .removeDragProcessorsAndListeners(this.statusTextFieldSouth);
        ToolsEventHandling
                .removeRotateProcessorsAndListeners(this.statusTextFieldSouth);

        // Set width (minus offset and minus icon width)
        float statusMsgWidth = this.getWidthXYGlobal();
        this.statusTextFieldNorth.setWidthLocal(statusMsgWidth
                - (statusMsgWidth * 0.1f)
                - (Math.round(statusMsgWidth
                        * ICON_AREA_WIDTH_SCALE_TO_STMSG_PERCENT)));

        this.statusTextFieldSouth.setWidthLocal(statusMsgWidth
                - (statusMsgWidth * 0.1f)
                - (Math.round(statusMsgWidth
                        * ICON_AREA_WIDTH_SCALE_TO_STMSG_PERCENT)));

        // Set heightLocal
        float statusMsgHeightQuarter = this.getHeightXYGlobal() / 4f;
        this.statusTextFieldNorth.setHeightLocal(statusMsgHeightQuarter
                - (statusMsgHeightQuarter * STATUS_MSG_TEXT_Y_OFFSET_PERCENT));
        this.statusTextFieldSouth.setHeightLocal(statusMsgHeightQuarter
                - (statusMsgHeightQuarter * STATUS_MSG_TEXT_Y_OFFSET_PERCENT));

        // Set style info
        this.statusTextFieldNorth.setStyleInfo(STATUS_MSG_TEXT_STYLE_INFO);
        this.statusTextFieldSouth.setStyleInfo(STATUS_MSG_TEXT_STYLE_INFO);

        // Add text fields to NodeContentContainer
        this.addChild(this.statusTextFieldNorth);
        this.addChild(this.statusTextFieldSouth);

        // Move southern TextArea to the bottom of the IdeaNodeView
        this.statusTextFieldSouth.setAnchor(PositionAnchor.UPPER_LEFT);
        this.statusTextFieldSouth
                .setPositionRelativeToParent(new Vector3D(
                        (statusMsgWidth * STATUS_MSG_TEXT_X_OFFSET_PERCENT)
                                + (Math.round(statusMsgWidth
                                        * ICON_AREA_WIDTH_SCALE_TO_STMSG_PERCENT)),
                        this.statusTextFieldNorth
                                .getHeightXY(TransformSpace.LOCAL)
                                + (statusMsgHeightQuarter * STATUS_MSG_TEXT_Y_OFFSET_PERCENT)
                                + (statusMsgHeightQuarter), 0));
        this.statusTextFieldSouth.setAnchor(PositionAnchor.CENTER);

        // Move northern TextArea
        this.statusTextFieldNorth.setAnchor(PositionAnchor.UPPER_LEFT);
        this.statusTextFieldNorth.setPositionRelativeToParent(new Vector3D(
                (statusMsgWidth * STATUS_MSG_TEXT_X_OFFSET_PERCENT),
                (statusMsgHeightQuarter * STATUS_MSG_TEXT_Y_OFFSET_PERCENT)
                        + statusMsgHeightQuarter, 0));
        this.statusTextFieldNorth.setAnchor(PositionAnchor.CENTER);

        // Rotate northern TextArea for 180 degrees around center
        this.statusTextFieldNorth.rotateZ(
                this.statusTextFieldNorth.getCenterPointGlobal(), 180);

        // Set text
        this.statusTextFieldNorth.setText(this.statusTextToSet);
        this.statusTextFieldSouth.setText(this.statusTextToSet);

        // Set font color
        this.statusTextFieldNorth.setFontColor(STATUS_MSG_FONT_COLOR);
        this.statusTextFieldSouth.setFontColor(STATUS_MSG_FONT_COLOR);

        // Set padding
        this.statusTextFieldNorth.setInnerPaddingLeft(2);
        this.statusTextFieldNorth.setInnerPaddingTop(8);
        this.statusTextFieldSouth.setInnerPaddingLeft(2);
        this.statusTextFieldSouth.setInnerPaddingTop(8);

        log.debug("Leaving addTextFields()"); //$NON-NLS-1$

    }

    /**
     * Adds a status message icon depending on the statusType.
     */
    private void addStatusMsgIcon() {

        log.debug("Entering addStatusMsgIcon()"); //$NON-NLS-1$

        if (this.statusType != null) {

            switch (this.statusType) {
                case STATUS_MSG_ERROR:
                    addStatusMsgIconFromPath(ICON_ERROR_IMG_PATH);
                    break;
                case STATUS_MSG_INFO:
                    addStatusMsgIconFromPath(ICON_INFO_IMG_PATH);
                    break;
                case STATUS_MSG_WARNING:
                    addStatusMsgIconFromPath(ICON_WARNING_IMG_PATH);
                    break;
                case STATUS_MSG_QUESTION:
                    addStatusMsgIconFromPath(ICON_QUESTION_IMG_PATH);
                    break;
                default:
                    break;
            }

        } else {
            log.error("Status Message type not correctly initialized!"); //$NON-NLS-1$
        }

        log.debug("Leaving addStatusMsgIcon()"); //$NON-NLS-1$

    }

    /**
     * Adds the error icon to the status message.
     * 
     * @param iconPath
     *            the icon file path
     * 
     */
    private void addStatusMsgIconFromPath(String iconPath) {
        log.debug("Entering addStatusMsgIconError()"); //$NON-NLS-1$

        // Get status message width
        float statusMsgWidth = this.getWidthXY(TransformSpace.GLOBAL);
        float statusMsgHeight = this.getHeightXY(TransformSpace.GLOBAL);
        float statusMsgHeightQuarter = statusMsgHeight / 4f;
        float statusTextFieldNorthHeight = this.statusTextFieldNorth
                .getHeightXY(TransformSpace.GLOBAL);
        float statusTextFieldNorthWidth = this.statusTextFieldNorth
                .getWidthXY(TransformSpace.GLOBAL);
        float iconAreaWidth = statusMsgWidth
                * ICON_AREA_WIDTH_SCALE_TO_STMSG_PERCENT;

        // Create a new Icon north
        MTSvg iconNorth = new MTSvg(this.mtApplication, iconPath);

        // Remove all default gesture listeners
        ToolsEventHandling.removeDragProcessorsAndListeners(iconNorth);
        ToolsEventHandling.removeScaleProcessorsAndListeners(iconNorth);
        ToolsEventHandling.removeRotateProcessorsAndListeners(iconNorth);

        // Set width
        iconNorth.setHeightXYGlobal(statusTextFieldNorthHeight
                * ICON_HEIGHT_SCALE_TO_TEXT_PERCENT);

        // Rotate 180 degrees
        iconNorth.rotateZ(iconNorth.getCenterPointGlobal(), 180);

        // Add to status message box
        this.addChild(iconNorth);

        // Reposition

        // Re-position
        if (this.hasBounds()) {

            // Bounding shape upper left vertex
            Vector3D upperLeftVertex = (this.getBounds().getVectorsGlobal())[0]
                    .getCopy();

            // Set at upper left position vertex
            iconNorth.setPositionGlobal(upperLeftVertex.getCopy());

            // Position at the upper right
            iconNorth.translate(new Vector3D(
                    (statusMsgWidth * STATUS_MSG_TEXT_X_OFFSET_PERCENT)
                            + statusTextFieldNorthWidth + (iconAreaWidth / 2f),
                    (statusMsgHeightQuarter * STATUS_MSG_TEXT_Y_OFFSET_PERCENT)
                            + statusMsgHeightQuarter
                            + (statusTextFieldNorthHeight / 2f), 0));

            // Create a new Icon south
            MTSvg iconSouth = new MTSvg(this.mtApplication, iconPath);

            // Remove all default gesture listeners
            ToolsEventHandling.removeDragProcessorsAndListeners(iconSouth);
            ToolsEventHandling.removeScaleProcessorsAndListeners(iconSouth);
            ToolsEventHandling.removeRotateProcessorsAndListeners(iconSouth);

            // Set width
            iconSouth.setHeightXYGlobal(this.statusTextFieldSouth
                    .getHeightXY(TransformSpace.GLOBAL)
                    * ICON_HEIGHT_SCALE_TO_TEXT_PERCENT);

            // Add to status message box
            this.addChild(iconSouth);

            // Reposition
            // Set at upper left position vertex
            iconSouth.setPositionGlobal(upperLeftVertex.getCopy());

            // Position at the upper right
            iconSouth
                    .translate(new Vector3D(
                            (statusMsgWidth * STATUS_MSG_TEXT_X_OFFSET_PERCENT)
                                    + (iconAreaWidth / 2f),
                            statusTextFieldNorthHeight
                                    + (statusMsgHeightQuarter * STATUS_MSG_TEXT_Y_OFFSET_PERCENT)
                                    + statusMsgHeightQuarter
                                    + (statusTextFieldNorthHeight / 2f), 0));

        } else {
            log.error("Error! This StatusMessageDialog has no bounds"); //$NON-NLS-1$
        }

        log.debug("Leaving addStatusMsgIconError()"); //$NON-NLS-1$

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
     * 
     * @param width
     *            the width
     * @return true, if width > 0
     * 
     * @see MTKeyboard#setWidthXYRelativeToParent(float)
     */
    protected boolean setWidthRelativeToParent(float width) {

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

    /* ********Overridden methods******** */
    /**
     * Overridden method destroy() from AbstractShape. Adds behaviour required
     * on destruction of the StatusMessageInstance instance.
     * 
     * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#destroy()
     */
    @Override
    public void destroy() {

        log.debug("Entering destroy()"); //$NON-NLS-1$

        // Set in null in mindMap
        if (this.mindMapScene != null) {

            // Set Canvas and children enabled
            AbstractStatusMessageDialog.this.mindMapScene.lockCanvas(false);

            // Set reference null
            this.mindMapScene.setStatusMessageBox(null);

        } else {
            log.error("Error: MindMap provided at initialization is null!"); //$NON-NLS-1$
            // TODO: close app
        }

        // Destroy
        super.destroy();

        log.debug("Leaving destroy()"); //$NON-NLS-1$

    }

}
