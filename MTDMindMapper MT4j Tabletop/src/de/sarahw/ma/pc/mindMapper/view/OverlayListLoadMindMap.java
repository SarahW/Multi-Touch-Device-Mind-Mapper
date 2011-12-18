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

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.widgets.MTListCell;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.IFont;

import processing.core.PApplet;
import de.sarahw.ma.pc.mindMapper.model.AppModel;
import de.sarahw.ma.pc.mindMapper.model.MindMapCollection;
import de.sarahw.ma.pc.mindMapper.model.MindMapSerializer;

/**
 * <p>
 * Class implementing the AbstractOverlayDefaultList. Represents the overlay
 * view for loading a new MindMap. Shows a list of all available .mindMap files.
 * Selecting a list entry will load the selected .mindMap file.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * @see de.sarahw.ma.pc.mindMapper.view.AbstractOverlayDefaultList
 * 
 */
public class OverlayListLoadMindMap extends AbstractOverlayDefaultList {

    private static Logger         log                              = Logger.getLogger(OverlayListLoadMindMap.class);

    /* *** List cell constants *** */
    /** The list cell padding */
    private static final int      LIST_CELL_PADDING                = 2;
    /** The list cell fill color */
    private static final MTColor  LIST_CELL_FILL_COLOR             = MindMapperColors.DARK_GREY_SLIGHT_TRANS;
    /** The list cell fill color on pressing the cell item */
    private static final MTColor  LIST_CELL_FILL_PRESSED_COLOR     = MindMapperColors.LIGHT_GREY_SLIGHT_TRANS;
    /** The list cell font color */
    private static final MTColor  LIST_CELL_FONT_COLOR             = MindMapperColors.BROKEN_WHITE;

    /** The list cell height in percent of the list height */
    private static final float    LIST_CELL_HEIGHT_TO_LIST_PERCENT = 0.20f;

    /** The maximum number of lines in the list cell text field */
    private static final int      LIST_CELL_MAX_LINE_NUMBER        = 2;

    /** The cell text field width offset */
    private static final float    CELL_LABEL_WIDTH_OFFSET          = 8f;

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication mtApplication;
    /** The currently active mindMapScene */
    private MindMapScene          mindMapScene;

    /**
     * Constructor. Instantiates a new OverlayListLoadMindMap.
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
     */
    public OverlayListLoadMindMap(PApplet pApplet, MindMapScene mindMapScene,
            float x, float y, float width, float height, IFont headlineFont,
            IFont listFontDefault, IFont listFontSmaller) {
        super(pApplet, x, y, width, height, headlineFont, listFontDefault,
                listFontSmaller);

        log.debug("Executing OverlayListLoadMindMap(pApplet=" + pApplet //$NON-NLS-1$
                + ", mindMapScene=" + mindMapScene + ", x=" //$NON-NLS-1$ //$NON-NLS-2$
                + x + ", y=" + y + ", width=" + width + ", height=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + height
                + ", headlineFont=" + headlineFont + ", listFontDefault=" //$NON-NLS-1$ //$NON-NLS-2$
                + listFontDefault + ", listFontSmaller=" + //$NON-NLS-1$
                listFontSmaller + ")"); //$NON-NLS-1$ 

        // Set app reference, mindMapScene reference and font
        this.mtApplication = (AbstractMTApplication) pApplet;
        this.mindMapScene = mindMapScene;

        // Initialize
        initialize();

    }

    /* ********Getters & Setters******** */

    /**
     * Returns the mindMapScene of the OverlayListLoadMindMap
     * 
     * @return the mindMapScene
     */
    public MindMapScene getMindMapScene() {
        return this.mindMapScene;
    }

    /* **********Object methods********** */

    /**
     * Initializes the OverlayListLoadMindMap.
     * 
     */
    private void initialize() {
        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Set headline text
        setHeadlineText();

        // Add list cells
        addListCells();

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
                        Messages.getString("OverlayListLoadMindMap.setHeadlineText.headlineText.0")); //$NON-NLS-1$

        log.debug("Leaving setHeadlineText()"); //$NON-NLS-1$

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

            // Get the .mindMap file name list from the model
            AppModel model = this.mindMapScene.getModelReference();

            if (model != null) {

                MindMapCollection mindMapCollection = model
                        .getMindMapCollection();

                if (mindMapCollection != null) {

                    // Update files list
                    mindMapCollection.updateMindMapFilesList();

                    // Get files list
                    List<File> fileList = mindMapCollection
                            .getMindMapCollectionFiles();

                    // Get list width
                    float listWidth = getOverlayList().getWidthXY(
                            TransformSpace.GLOBAL);

                    float listHeight = getOverlayList().getHeightXY(
                            TransformSpace.GLOBAL);

                    // Create a new list cell for every .mindMap save file
                    for (File file : fileList) {

                        String fileNameWOExtension;
                        String fileName = file.getName();

                        if (fileName.endsWith(MindMapSerializer.FILE_EXTENSION)) {
                            fileNameWOExtension = fileName.substring(
                                    0,
                                    fileName.length()
                                            - MindMapSerializer.FILE_EXTENSION
                                                    .length());
                        } else {
                            // We assume there are only .mindMap files in the
                            // list
                            // because
                            // mindMapCollection.updateMindMapFilesList() makes
                            // sure of that
                            fileNameWOExtension = fileName;
                        }

                        // Add cell for every save file
                        getOverlayList()
                                .addListElement(
                                        this.createListCell(
                                                fileNameWOExtension,
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

                    log.debug("Leaving addListCells(): true"); //$NON-NLS-1$
                    return true;
                }
                log.error("Leaving addListCells(): false, invalid mindMapCollection (null) referenced in model"); //$NON-NLS-1$
                // TODO close app
                return false;
            }
            log.error("Leaving addListCells(): false, invalid model (null) referenced in MindMapScene"); //$NON-NLS-1$
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

        cell.unregisterAllInputProcessors();

        cell.registerInputProcessor(new TapProcessor(this.mtApplication, 15));

        cell.addGestureListener(TapProcessor.class,
                new IGestureEventListener() {
                    @SuppressWarnings("synthetic-access")
                    @Override
                    public boolean processGestureEvent(MTGestureEvent ge) {
                        TapEvent te = (TapEvent) ge;
                        switch (te.getTapID()) {
                            case TapEvent.TAP_DOWN:
                                cell.setFillColor(cellPressedFillColor);
                                break;
                            case TapEvent.TAP_UP:
                                cell.setFillColor(cellFillColor);
                                break;
                            case TapEvent.TAPPED:
                                log.debug("OverlayListLoadMenu tapped cell with text: " //$NON-NLS-1$
                                        + label);

                                cell.setFillColor(cellFillColor);

                                // Load selected mindMap
                                // Get the model reference
                                AppModel model = OverlayListLoadMindMap.this
                                        .getMindMapScene().getModelReference();

                                // Find file by name (we unfortunately have get
                                // no list index from the MTList/MTListCells)
                                File mindMapToLoadFile = model
                                        .getMindMapCollection().getFileByName(
                                                cell.getName());

                                if (mindMapToLoadFile != null) {
                                    MindMapScene mindMapScene = OverlayListLoadMindMap.this
                                            .getMindMapScene();
                                    if (mindMapScene != null) {

                                        mindMapScene
                                                .startLoadMindMapFromDiscProcess(mindMapToLoadFile);

                                    } else {
                                        log.error("Error: invalid MindMapScene instance assigned at initialization (null)"); //$NON-NLS-1$
                                        // TODO: close app
                                    }

                                } else {
                                    log.error("Error: selected file not found!"); //$NON-NLS-1$
                                    // TODO: Status message? Refresh list!
                                }

                                // Close overlay
                                // OverlayListLoadMindMap.this.destroy();

                                break;
                        }
                        return false;
                    }

                });

        log.debug("Leaving createListCell(): cell"); //$NON-NLS-1$
        return cell;
    }

    /* ********Overridden methods******** */

    /**
     * Overriden method destroy() from AbstractShape. Adds behaviour required on
     * destruction of the OverlayListLoadMindMap instance.
     * 
     * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#destroy()
     */
    @Override
    public void destroy() {

        log.debug("Entering destroy()"); //$NON-NLS-1$

        super.destroy();

        // Set in null in mindMap
        if (this.mindMapScene != null) {

            // Set loadMindMapOverlay null
            this.mindMapScene.setLoadMindMapOverlay(null);

        } else {
            log.error("Error: MindMap provided at initialization is null!"); //$NON-NLS-1$
            // TODO: close app
        }

        log.debug("Leaving destroy()"); //$NON-NLS-1$
    }

}
