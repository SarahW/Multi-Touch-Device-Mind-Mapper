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

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.StyleInfo;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTList;
import org.mt4j.components.visibleComponents.widgets.MTListCell;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * <p>
 * Class implementing AbstractOverlayDefaultList. Represents the overlay view
 * for showing the current bluetooth connections. Shows a list of all available
 * bluetooth connections. Selecting a list entry will ....
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * @see de.sarahw.ma.pc.mindMapper.view.AbstractOverlayDefaultList
 * 
 * 
 */
public class OverlayListBluetooth extends AbstractOverlayDefaultList {

    private static Logger          log                                               = Logger.getLogger(OverlayListBluetooth.class);

    /* *** List constants ** */
    /** The overlay content list height in percent of the overlay height */
    private static final float     LIST_HEIGHT_SCALE_TO_OVERLAY_PERCENT              = 0.461538461f;

    /* *** List cell constants *** */
    /** The list cell padding */
    private static final int       LIST_CELL_PADDING                                 = 2;
    /** The list cell fill color */
    private static final MTColor   LIST_CELL_FILL_COLOR                              = MindMapperColors.DARK_GREY_SLIGHT_TRANS;
    /** The list cell fill color on pressing the cell item */
    private static final MTColor   LIST_CELL_FILL_PRESSED_COLOR                      = MindMapperColors.LIGHT_GREY_SLIGHT_TRANS;
    /** The list cell font color */
    private static final MTColor   LIST_CELL_FONT_COLOR                              = MindMapperColors.BROKEN_WHITE;

    /** The list cell height in percent of the list height */
    private static final float     LIST_CELL_HEIGHT_TO_LIST_PERCENT                  = 0.3333333333333f;

    /** The maximum number of lines in the list cell text field */
    private static final int       LIST_CELL_MAX_LINE_NUMBER                         = 2;

    /** The cell text field width offset */
    private static final float     CELL_LABEL_WIDTH_OFFSET                           = 8f;

    /* *** QR Code constants *** */
    /** The QR code image path */
    private static final String    QR_CODE_IMG_PATH                                  = MT4jSettings
                                                                                             .getInstance()
                                                                                             .getDefaultImagesPath()
                                                                                             + "qrdroid_app_url.png";               //$NON-NLS-1$
    /** The qr code width in percent of the overlay height */
    private static final float     QR_CODE_WIDTH_SCALE_TO_OVERLAY_PERCENT            = 0.35f;

    /** The qr code y offset in percent of the overlay height */
    private static final float     QR_CODE_Y_OFFSET_TO_OVERLAY_PERCENT               = 0.63f;

    /** The name for the MTRectangle the code is added to as a texture */
    protected static final String  QR_CODE_NAME                                      = "QR_CODE";                                   //$NON-NLS-1$

    /* *** QR Code description constants *** */
    /** The qr code description maximum number of lines */
    private static final int       QR_CODE_DESCR_TEXT_MAX_NUMBER_OF_LINES            = 2;
    /** The qr code description width in percent of the overlay height */
    private static final float     QR_CODE_DESCR_TEXT_WIDTH_SCALE_TO_OVERLAY_PERCENT = 0.7983f;
    /** The qr code description y offset in percent of the overlay height */
    private static final float     QR_CODE_DESCR_TEXT_X_OFFSET_TO_OVERLAY_PERCENT    = 0.10f;
    /** The qr code description y offset in percent of the overlay height */
    private static final float     QR_CODE_DESCR_TEXT_Y_OFFSET_TO_OVERLAY_PERCENT    = 0.89f;
    /** The qr code description text font name */
    public static final String     QR_CODE_DESCR_TEXT_FONT_NAME                      = "SansSerif";                                 //$NON-NLS-1$
    /** The qr code description text font size */
    public static final int        QR_CODE_DESCR_TEXT_FONT_SIZE                      = 11;

    /** The qr code description font color */
    private static final MTColor   QR_CODE_DESCR_TEXT_FONT_COLOR                     = MindMapperColors.BROKEN_WHITE;

    /** The style info for the qr code description field */
    private static final StyleInfo QR_CODE_DESCR_TEXT_STYLE_INFO                     = new StyleInfo(
                                                                                             MTColor.WHITE,
                                                                                             MTColor.WHITE,
                                                                                             true,
                                                                                             true,
                                                                                             true,
                                                                                             1.0f,
                                                                                             GL10.GL_TRIANGLE_FAN,
                                                                                             (short) 0);

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication  mtApplication;
    /** The currently active mindMapScene */
    private MindMapScene           mindMapScene;

    /* *** Qr code description * ** */
    /** The qr code description text font */
    private IFont                  qrDescriptionFont;

    /**
     * Constructor. Instantiates a new OverlayListBluetooth instance.
     * 
     * @param pApplet
     *            the application instance
     * @param mindMapScene
     *            the mindMapScene the overlay is added to
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
     * @param listFontDefault
     *            the font for the list content
     * @param listFontSmaller
     *            the smaller font for the list content
     * @param qrCodeFont
     *            the font for the qr code description
     * 
     * 
     */
    public OverlayListBluetooth(PApplet pApplet, MindMapScene mindMapScene,
            float x, float y, float width, float height, IFont headlineFont,
            IFont listFontDefault, IFont listFontSmaller, IFont qrCodeFont) {
        super(pApplet, x, y, width, height, headlineFont, listFontDefault,
                listFontSmaller);

        log.debug("Executing OverlayListBluetooth(pApplet=" + pApplet //$NON-NLS-1$
                + ", mindMapScene=" + mindMapScene + ", x=" //$NON-NLS-1$ //$NON-NLS-2$
                + x + ", y=" + y + ", width=" + width + ", height=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + height
                + ", headlineFont=" + headlineFont + ", listFontDefault=" //$NON-NLS-1$ //$NON-NLS-2$
                + listFontDefault + ", listFontSmaller=" + //$NON-NLS-1$
                listFontSmaller + ", qrCodeFont=" + qrCodeFont + ")"); //$NON-NLS-1$  //$NON-NLS-2$ 

        // Set app reference
        this.mtApplication = (AbstractMTApplication) pApplet;
        this.mindMapScene = mindMapScene;

        // Set qr description font
        this.qrDescriptionFont = qrCodeFont;

        // Initialize
        initialize();

    }

    /* **********Object methods********** */
    private void initialize() {
        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Set headline text
        setHeadlineText();

        // Adjust list height
        adjustListHeight();

        // Add list cells
        addListCells();

        // Add QR Code
        addQrCode();

        // Add QR Code description
        addQrCodeDescription();

        log.debug("Leaving initialize()"); //$NON-NLS-1$
    }

    /**
     * Sets the OverlayListLoadMindMap headline text.
     * 
     */
    private void setHeadlineText() {

        log.debug("Entering setHeadlineText()"); //$NON-NLS-1$

        // Set headline text
        getHeadlineTextField()
                .setText(
                        Messages.getString("OverlayListBluetooth.setHeadlineText.connectedBtDevices.0")); //$NON-NLS-1$

        log.debug("Leaving setHeadlineText()"); //$NON-NLS-1$

    }

    /**
     * Adjusts the list height to make space for the QR code image and
     * description text field.
     * 
     */
    private void adjustListHeight() {
        log.debug("Entering adjustListHeight()"); //$NON-NLS-1$

        // Set list height
        MTList list = getOverlayList();
        list.setAnchor(PositionAnchor.UPPER_LEFT);

        // Get the first vertex of the bounding shape
        if (this.hasBounds()) {

            // Get overlay height
            float overlayHeight = this.getHeightXY(TransformSpace.GLOBAL);

            // Create new list
            MTList overlayList = new MTList(this.mtApplication, list
                    .getPosition(TransformSpace.GLOBAL).getX(), list
                    .getPosition(TransformSpace.GLOBAL).getY(),
                    list.getWidthXY(TransformSpace.GLOBAL), overlayHeight
                            * LIST_HEIGHT_SCALE_TO_OVERLAY_PERCENT);

            // Set style info
            overlayList.setStyleInfo(list.getStyleInfo());

            // Remove old list
            this.removeChild(list);

            // Add to overlay
            this.addChild(overlayList);

            // Set member
            setOverlayList(overlayList);

        } else {
            log.error("Error! The AbstractOverlayDefaultList has no bounds!"); //$NON-NLS-1$
        }

        log.debug("Leaving adjustListHeight()"); //$NON-NLS-1$

    }

    /**
     * Adds list cells to the overlay list as well as list cells behaviour.
     * 
     * @return true, if adding the list cells was successful
     */
    private boolean addListCells() {

        log.debug("Entering addListCells()"); //$NON-NLS-1$

        if (this.mindMapScene != null
                && this.getListContentDefaultFont() != null
                && this.getListContentSmallerFont() != null) {

            if (this.mindMapScene.getBtConnectionStringList() != null) {

                ArrayList<String> btConnectionsList = (ArrayList<String>) this.mindMapScene
                        .getBtConnectionStringList();

                // Get list width
                float listWidth = getOverlayList().getWidthXY(
                        TransformSpace.GLOBAL);

                float listHeight = getOverlayList().getHeightXY(
                        TransformSpace.GLOBAL);

                if (btConnectionsList.size() > 0) {

                    // Create a new list cell for every .mindMap save file
                    for (String connection : btConnectionsList) {

                        // Add cell for every save file
                        getOverlayList()
                                .addListElement(
                                        this.createListCell(
                                                connection,
                                                this.getListContentDefaultFont(),
                                                this.getListContentSmallerFont(),
                                                listWidth
                                                        - (LIST_CELL_PADDING * 2),
                                                (listHeight * LIST_CELL_HEIGHT_TO_LIST_PERCENT)
                                                        - (LIST_CELL_PADDING * 2),
                                                LIST_CELL_FILL_COLOR,
                                                LIST_CELL_FILL_PRESSED_COLOR,
                                                LIST_CELL_FONT_COLOR));
                    }
                } else {

                    // Add cell for "no devices connected"
                    getOverlayList()
                            .addListElement(
                                    this.createListCell(
                                            Messages.getString("OverlayListBluetooth.addListCells.noDevicesConnected.0"), //$NON-NLS-1$
                                            this.getListContentDefaultFont(),
                                            this.getListContentSmallerFont(),
                                            listWidth - (LIST_CELL_PADDING * 2),
                                            (listHeight * LIST_CELL_HEIGHT_TO_LIST_PERCENT)
                                                    - (LIST_CELL_PADDING * 2),
                                            LIST_CELL_FILL_COLOR,
                                            LIST_CELL_FILL_PRESSED_COLOR,
                                            LIST_CELL_FONT_COLOR));

                }

                log.debug("Leaving addListCells(): true"); //$NON-NLS-1$
                return true;

            }
            log.error("Leaving addListCells(): false, invalid bt connections list (null) in MindMapScene"); //$NON-NLS-1$
            // TODO close app
            return false;
        }
        log.error("Leaving addListCells(): false, invalid MindMapScene (null) or listFont (null) provided at initialization"); //$NON-NLS-1$
        // TODO close app
        return false;

    }

    /**
     * Creates a new list cell with the given label text.
     * 
     * @param label
     *            the text for the cell
     * @param fontBig
     *            the bigger font for the cell
     * @param fontSmall
     *            the smaller font for the cell
     * @param cellWidth
     *            the cell width
     * @param cellHeight
     *            the cell height
     * @param cellFillColor
     *            the cell fill color
     * @param cellPressedFillColor
     *            the cell color when pressed
     * @param cellFontColor
     *            the cell font color
     * @return the created cell
     */
    private MTListCell createListCell(final String label, IFont fontBig,
            IFont fontSmall, float cellWidth, float cellHeight,
            final MTColor cellFillColor, final MTColor cellPressedFillColor,
            final MTColor cellFontColor) {

        log.debug("Entering createListCell(label=" + label + ", fontBig=" //$NON-NLS-1$//$NON-NLS-2$
                + fontBig
                + ", fontSmall=" + fontSmall + ", cellWidth=" //$NON-NLS-1$//$NON-NLS-2$
                + cellWidth
                + ", cellHeight=" + cellHeight + ", cellFillColor=" //$NON-NLS-1$//$NON-NLS-2$
                + cellFillColor
                + ", cellPressedFillColor=" //$NON-NLS-1$
                + cellPressedFillColor
                + ", cellFontColor=" + cellFontColor + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        final MTListCell cell = new MTListCell(this.mtApplication, cellWidth,
                cellHeight);

        // cell.setChildClip(null); // FIXME TEST, no clipping for performance!

        cell.setFillColor(cellFillColor);

        MTTextFieldVarLinesFontSwitch listLabel = new MTTextFieldVarLinesFontSwitch(
                this.mtApplication, 0, 0, cellWidth - CELL_LABEL_WIDTH_OFFSET,
                cellHeight, LIST_CELL_MAX_LINE_NUMBER, fontBig, fontSmall);

        listLabel.setNoFill(true);
        listLabel.setNoStroke(true);
        listLabel.setText(label);
        listLabel.setFontColor(cellFontColor);

        // Set cell name the label, also (required for list selection later)
        cell.setName(label);
        cell.addChild(listLabel);

        listLabel.setPositionRelativeToParent(cell.getCenterPointLocal());

        log.debug("Leaving createListCell(): cell"); //$NON-NLS-1$
        return cell;
    }

    /**
     * Updates the cells of the list re-adding all cells.
     * 
     */
    protected void updateCells() {

        log.debug("Entering updateCells()"); //$NON-NLS-1$

        // Remove all cells
        getOverlayList().removeAllListElements();

        // Call add cells
        addListCells();

        log.debug("Leaving updateCells()"); //$NON-NLS-1$

    }

    /**
     * Adds a QR code image to the overlay which contains the Android
     * application download URL.
     * 
     */
    private void addQrCode() {
        log.debug("Entering addQrCode()"); //$NON-NLS-1$

        PImage qrcode = this.mtApplication.loadImage(QR_CODE_IMG_PATH);

        MTRectangle qrRec = new MTRectangle(this.mtApplication, qrcode);

        qrRec.setPickable(false);

        // Set name (required for modified drag action)
        qrRec.setName(QR_CODE_NAME);

        // Set width
        qrRec.setWidthXYGlobal(this.getWidthXY(TransformSpace.GLOBAL)
                * QR_CODE_WIDTH_SCALE_TO_OVERLAY_PERCENT);

        this.addChild(qrRec);

        // Position qr code
        this.setAnchor(PositionAnchor.UPPER_LEFT);
        qrRec.setAnchor(PositionAnchor.UPPER_LEFT);

        qrRec.setPositionGlobal(this.getPosition(TransformSpace.GLOBAL));

        qrRec.translate(new Vector3D(
                (this.getWidthXY(TransformSpace.GLOBAL) / 2f)
                        - (qrRec.getWidthXY(TransformSpace.GLOBAL) / 2f), this
                        .getHeightXY(TransformSpace.GLOBAL)
                        * QR_CODE_Y_OFFSET_TO_OVERLAY_PERCENT));

        // Reset anchor
        this.setAnchor(PositionAnchor.CENTER);

        log.debug("Leaving addQrCode()"); //$NON-NLS-1$

    }

    /**
     * Adds the textual description of the QR code.
     * 
     */
    private void addQrCodeDescription() {
        log.debug("Entering addQrCodeDescription()"); //$NON-NLS-1$

        MTTextFieldVarLines qrCodeDescription = new MTTextFieldVarLines(
                this.mtApplication, this.getWidthXY(TransformSpace.GLOBAL)
                        * QR_CODE_DESCR_TEXT_X_OFFSET_TO_OVERLAY_PERCENT,
                this.getHeightXY(TransformSpace.GLOBAL)
                        * QR_CODE_DESCR_TEXT_Y_OFFSET_TO_OVERLAY_PERCENT,
                this.getWidthXY(TransformSpace.GLOBAL)
                        * QR_CODE_DESCR_TEXT_WIDTH_SCALE_TO_OVERLAY_PERCENT,
                this.getWidthXY(TransformSpace.GLOBAL)
                        * QR_CODE_DESCR_TEXT_WIDTH_SCALE_TO_OVERLAY_PERCENT,
                QR_CODE_DESCR_TEXT_MAX_NUMBER_OF_LINES, this.qrDescriptionFont);

        qrCodeDescription.setPickable(false);

        qrCodeDescription.setStyleInfo(QR_CODE_DESCR_TEXT_STYLE_INFO);

        qrCodeDescription.setFontColor(QR_CODE_DESCR_TEXT_FONT_COLOR);

        // Set description text
        qrCodeDescription
                .setText(Messages
                        .getString("OverlayListBluetooth.addListCells.qrCodeDescription.00")); //$NON-NLS-1$

        // Add to overlay
        this.addChild(qrCodeDescription);

        log.debug("Leaving addQrCodeDescription()"); //$NON-NLS-1$

    }

    /* ********Overridden methods******** */
    /**
     * Overridden method destroy() from AbstractShape. Adds behaviour required
     * on destruction of the OverlayListBluetooth instance.
     * 
     * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#destroy()
     */
    @Override
    public void destroy() {

        log.debug("Entering destroy()"); //$NON-NLS-1$

        super.destroy();

        // Set in null in mindMap
        if (this.mindMapScene != null) {

            // Set bluetoothOverlay null
            this.mindMapScene.setBluetoothOverlay(null);

        } else {
            log.error("Error: MindMap provided at initialization is null!"); //$NON-NLS-1$
            // TODO: close app
        }

        log.debug("Leaving destroy()"); //$NON-NLS-1$
    }

}
