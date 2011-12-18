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

import java.util.List;

import org.apache.log4j.Logger;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.StyleInfo;
import org.mt4j.components.visibleComponents.widgets.MTListCell;
import org.mt4j.components.visibleComponents.widgets.MTSvg;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;

/**
 * <p>
 * Class implementing AbstractOverlayDefaultList. Represents the overlay view
 * for showing a short introduction to the used gestures and functions of the
 * application. No selections possible as of now.
 * </p>
 * 
 * <p>
 * Help images illustrations provided by Gestureworks (www.gestureworks.com),
 * shared under a CreativeCommons Attribution-ShareAlike 3.0 United States (CC
 * BY-SA 3.0) license (<a
 * href="http://creativecommons.org/licenses/by-sa/3.0/us/"
 * >http://creativecommons.org/licenses/by-sa/3.0/us/</a>). For detailed info on
 * modifications and license see image source folder.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class OverlayListHelp extends AbstractOverlayWideList {

    private static Logger          log                                         = Logger.getLogger(OverlayListHelp.class);

    /* *** List cell constants *** */
    /** The list cell padding */
    private static final int       LIST_CELL_PADDING                           = 0;
    /** The list cell fill color */
    private static final MTColor   LIST_CELL_FILL_COLOR                        = MindMapperColors.DARK_GREY_SLIGHT_TRANS;
    /** The list cell font color */
    private static final MTColor   LIST_CELL_FONT_COLOR                        = MindMapperColors.BROKEN_WHITE;
    /** The list cell stroke weight */
    private static final float     LIST_CELL_STROKE_WEIGHT                     = 0f;
    /** The list cell stroke style info */
    private static final StyleInfo LIST_CELL_STYLE_INFO                        = new StyleInfo(
                                                                                       LIST_CELL_FILL_COLOR,
                                                                                       LIST_CELL_FILL_COLOR,
                                                                                       true,
                                                                                       true,
                                                                                       false,
                                                                                       LIST_CELL_STROKE_WEIGHT,
                                                                                       GL10.GL_TRIANGLE_FAN,
                                                                                       (short) 0);

    /** The list cell height in percent of the list height */
    private static final float     LIST_CELL_HEIGHT_TO_LIST_PERCENT            = 0.60f;

    /* *** List cell text field constants *** */
    /** The list cell text field width in percent of the cell width */
    private static final float     LIST_CELL_TXTFIELD_WIDTH_SCALE_PERCENT      = 0.72f;
    /** The list cell text field height in percent of the cell height */
    private static final float     LIST_CELL_TXTFIELD_HEIGHT_SCALE_PERCENT     = 0.75f;
    /** The list cell text field y offset in percent of the cell height */
    private static final float     LIST_CELL_TXTFIELD_Y_OFFSET_TO_CELL_PERCENT = 0.125f;
    /** The maximum number of lines in a cell text field */
    private static final int       LIST_CELL_MAX_LINE_NUMBER                   = 4;

    /** The help text for creating an ideaNode */
    private static final String    HELP_TXT_01_CREATE_IDEA                     = Messages

                                                                                       .getString("OverlayListHelp.HelpTexts.0.CreateIdea");          //$NON-NLS-1$
    /** The help text for editing an ideaNode */
    private static final String    HELP_TXT_02_EDIT_IDEA                       = Messages
                                                                                       .getString("OverlayListHelp.HelpTexts.1.EditIdea");            //$NON-NLS-1$
    /** The help text for creating a relation between two ideaNodes */
    private static final String    HELP_TXT_03_CREATE_RELATION                 = Messages
                                                                                       .getString("OverlayListHelp.HelpTexts.2.CreateRelation");      //$NON-NLS-1$
    /** The help text for deleting an ideaNode or a relation */
    private static final String    HELP_TXT_04_DELETE                          = Messages
                                                                                       .getString("OverlayListHelp.HelpTexts.3.DeleteIdeaOrRelation"); //$NON-NLS-1$
    /** The help text for dragging elements */
    private static final String    HELP_TXT_05_DRAG_ELEMENTS                   = Messages
                                                                                       .getString("OverlayListHelp.HelpTexts.4.DragElement");         //$NON-NLS-1$
    /** The help text for rotating elements */
    private static final String    HELP_TXT_06_ROTATE_ELEMENTS                 = Messages
                                                                                       .getString("OverlayListHelp.HelpTexts.5.RotateElement");       //$NON-NLS-1$

    /** The list of all help texts */
    private static final String[]  HELP_TEXTS_LIST                             = new String[] {
            HELP_TXT_01_CREATE_IDEA, HELP_TXT_02_EDIT_IDEA,
            HELP_TXT_03_CREATE_RELATION, HELP_TXT_04_DELETE,
            HELP_TXT_05_DRAG_ELEMENTS, HELP_TXT_06_ROTATE_ELEMENTS,           };

    /* *** List cell image constants *** */
    /** The list cell image width in percent of the cell width */
    private static final float     LIST_CELL_IMG_WIDTH_SCALE_TO_CELL_PERCENT   = 0.27f;
    /** The list cell image height in percent of the cell height */
    private static final float     LIST_CELL_IMG_HEIGHT_SCALE_TO_CELL_PERCENT  = 0.9f;

    /** The gesture svg image path for creating an ideaNode */
    private static final String    HELP_IMG_01_CREATE_IDEA_PATH                = MT4jSettings
                                                                                       .getInstance()
                                                                                       .getDefaultSVGPath()
                                                                                       + "gestureworks" //$NON-NLS-1$
                                                                                       + AbstractMTApplication.separator
                                                                                       + "stroke_shape_rectangle_gestureworks.svg";                   //$NON-NLS-1$
    /** The gesture svg image path for editing an ideaNode */
    private static final String    HELP_IMG_02_EDIT_IDEA_PATH                  = MT4jSettings
                                                                                       .getInstance()
                                                                                       .getDefaultSVGPath()
                                                                                       + "gestureworks" //$NON-NLS-1$
                                                                                       + AbstractMTApplication.separator
                                                                                       + "one_finger_double_tap_gestureworks.svg";                    //$NON-NLS-1$
    /** The gesture svg image path for creating a relation between two ideaNodes */
    private static final String    HELP_IMG_03_CREATE_RELATION_PATH            = MT4jSettings
                                                                                       .getInstance()
                                                                                       .getDefaultSVGPath()
                                                                                       + "gestureworks" //$NON-NLS-1$
                                                                                       + AbstractMTApplication.separator
                                                                                       + "stroke_symbol_arrow_right_gestureworks_MOD.svg";            //$NON-NLS-1$

    /** The gesture svg image path for deleting ideaNodes and relations */
    private static final String    HELP_IMG_04_DELETE_PATH                     = MT4jSettings
                                                                                       .getInstance()
                                                                                       .getDefaultSVGPath()
                                                                                       + "gestureworks" //$NON-NLS-1$
                                                                                       + AbstractMTApplication.separator
                                                                                       + "stroke_symbol_multiply_gestureworks.svg";                   //$NON-NLS-1$

    /** The gesture svg image path for dragging elements */
    private static final String    HELP_IMG_05_DRAG_ELEMENTS_PATH              = MT4jSettings
                                                                                       .getInstance()
                                                                                       .getDefaultSVGPath()
                                                                                       + "gestureworks" //$NON-NLS-1$
                                                                                       + AbstractMTApplication.separator
                                                                                       + "drag_media_gestureworks.svg";                               //$NON-NLS-1$
    /** The gesture svg image path for rotating elements */
    private static final String    HELP_IMG_06_ROTATE_ELEMENTS_PATH            = MT4jSettings
                                                                                       .getInstance()
                                                                                       .getDefaultSVGPath()
                                                                                       + "gestureworks" //$NON-NLS-1$
                                                                                       + AbstractMTApplication.separator
                                                                                       + "media_rotate_gestureworks.svg";                             //$NON-NLS-1$
    /** The list of all gesture svg image paths */
    private static final String[]  HELP_IMAGES_PATH_LIST                       = new String[] {
            HELP_IMG_01_CREATE_IDEA_PATH, HELP_IMG_02_EDIT_IDEA_PATH,
            HELP_IMG_03_CREATE_RELATION_PATH, HELP_IMG_04_DELETE_PATH,
            HELP_IMG_05_DRAG_ELEMENTS_PATH, HELP_IMG_06_ROTATE_ELEMENTS_PATH  };

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication  mtApplication;
    /** The currently active mindMapScene */
    private MindMapScene           mindMapScene;

    /**
     * Constructor. Instantiates a new OverlayListHelp instance.
     * 
     * @param pApplet
     *            the application instance
     * @param mindMapScene
     *            the MindMapScene the overlay is created on
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
     * 
     */
    public OverlayListHelp(PApplet pApplet, MindMapScene mindMapScene, float x,
            float y, float width, float height, IFont headlineFont,
            IFont listFontDefault, IFont listFontSmaller) {
        super(pApplet, x, y, width, height, headlineFont, listFontDefault,
                listFontSmaller);

        log.debug("Executing OverlayListBluetooth(pApplet=" + pApplet //$NON-NLS-1$
                + ", mindMapScene=" + mindMapScene + ", x=" //$NON-NLS-1$ //$NON-NLS-2$
                + x + ", y=" + y + ", width=" + width + ", height=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + height
                + ", headlineFont=" + headlineFont + ", listFontDefault=" //$NON-NLS-1$ //$NON-NLS-2$
                + listFontDefault + ", listFontSmaller=" + //$NON-NLS-1$
                listFontSmaller + ")"); //$NON-NLS-1$

        // Set app reference and mindMapScene
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
                        Messages.getString("OverlayListHelp.setHeadlineText.headlineText0")); //$NON-NLS-1$

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
                && this.getListContentDefaultFont() != null) {

            // Get list width
            float listWidth = getOverlayList()
                    .getWidthXY(TransformSpace.GLOBAL);

            float listHeight = getOverlayList().getHeightXY(
                    TransformSpace.GLOBAL);

            // Create a new list cell for every .mindMap save file
            for (int i = 0; i < HELP_TEXTS_LIST.length
                    && i < HELP_IMAGES_PATH_LIST.length; i++) {

                // Add cell for every help text and image
                getOverlayList().addListElement(
                        this.createListCell(HELP_TEXTS_LIST[i],
                                this.getListContentSmallerFont(),
                                HELP_IMAGES_PATH_LIST[i], listWidth
                                        - ((LIST_CELL_PADDING) + 2),
                                (listHeight * LIST_CELL_HEIGHT_TO_LIST_PERCENT)
                                        - (LIST_CELL_PADDING + 2),
                                LIST_CELL_STYLE_INFO, LIST_CELL_FONT_COLOR));
            }

            log.debug("Leaving addListCells(): true"); //$NON-NLS-1$
            return true;

        }
        log.error("Leaving addListCells(): false, invalid MindMapScene (null) or listFont (null) provided at initialization"); //$NON-NLS-1$
        // TODO close app
        return false;

    }

    /**
     * Creates a new cell to be added to the list with a help text and a help
     * image.
     * 
     * @param label
     *            the string to be shown in the cell
     * @param font
     *            the font used for the cell
     * @param imagePath
     *            the path to the help image shown in the cell
     * @param cellWidth
     *            the cell width
     * @param cellHeight
     *            the cell height
     * @param styleInfo
     *            the cell style info
     * @param cellFontColor
     *            the cell font color
     * 
     * @return the final cell
     */
    private MTListCell createListCell(final String label, IFont font,
            String imagePath, float cellWidth, float cellHeight,
            StyleInfo styleInfo, MTColor cellFontColor) {

        log.debug("Entering createListCell(label=" + label + ", font=" + font //$NON-NLS-1$//$NON-NLS-2$
                + ", imagePath=" + imagePath + "\n, cellWidth=" + cellWidth //$NON-NLS-1$//$NON-NLS-2$
                + ", cellHeight=" + cellHeight + ", styleInfo=" + styleInfo //$NON-NLS-1$//$NON-NLS-2$
                + ")"); //$NON-NLS-1$

        final MTListCell cell = new MTListCell(this.mtApplication, cellWidth,
                cellHeight);

        // cell.setChildClip(null); // FIXME TEST, no clipping for performance!

        // Set style info
        cell.setStyleInfo(styleInfo);

        float textFieldWidth = cellWidth
                * LIST_CELL_TXTFIELD_WIDTH_SCALE_PERCENT;

        float textFieldHeight = cellHeight
                * LIST_CELL_TXTFIELD_HEIGHT_SCALE_PERCENT;

        MTTextFieldVarLines listLabel = new MTTextFieldVarLines(
                this.mtApplication, 0, 0, textFieldWidth, textFieldHeight,
                LIST_CELL_MAX_LINE_NUMBER, font);

        listLabel.setNoFill(true);
        listLabel.setNoStroke(true);
        listLabel.setText(label);
        listLabel.setFontColor(cellFontColor);

        // Set cell name the label
        cell.setName(label);
        cell.addChild(listLabel);

        // Position upper left
        listLabel.setAnchor(PositionAnchor.UPPER_LEFT);
        listLabel.setPositionGlobal((cell.getVerticesGlobal())[0]);

        // Reposition
        listLabel.translate(new Vector3D(0, cellHeight
                * LIST_CELL_TXTFIELD_Y_OFFSET_TO_CELL_PERCENT, 0));

        // Add image
        MTSvg helpImage = new MTSvg(this.mtApplication, imagePath);

        // Set width
        helpImage.setWidthXYGlobal(cellWidth
                * LIST_CELL_IMG_WIDTH_SCALE_TO_CELL_PERCENT);

        // If height is still greater
        // LIST_CELL_IMG_HEIGHT_SCALE_TO_CELL_PERCENT, set
        if (helpImage.getHeightXYGlobal() > (cellHeight * LIST_CELL_IMG_HEIGHT_SCALE_TO_CELL_PERCENT)) {
            helpImage.setHeightXYGlobal(cellHeight
                    * LIST_CELL_IMG_HEIGHT_SCALE_TO_CELL_PERCENT);
        }

        // Position upper left
        helpImage.setPositionGlobal((cell.getVerticesGlobal())[0]);

        // Reposition
        helpImage.translate(new Vector3D(textFieldWidth
                + (helpImage.getWidthXYGlobal() / 2f),
                (cellHeight * LIST_CELL_TXTFIELD_Y_OFFSET_TO_CELL_PERCENT)
                        + (textFieldHeight / 2f), 0));

        // Add to cell
        cell.addChild(helpImage);

        // Unregister all input processors
        cell.unregisterAllInputProcessors();

        log.debug("Leaving createListCell(): cell"); //$NON-NLS-1$
        return cell;
    }

    /* ********Overridden methods******** */

    /**
     * Overriden method destroy() from AbstractShape. Adds behaviour required on
     * destruction of the OverlayListHelp instance.
     * 
     * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#destroy()
     */
    @Override
    public void destroy() {

        log.debug("Entering destroy()"); //$NON-NLS-1$

        super.destroy();

        // Set in null in mindMap
        if (this.mindMapScene != null) {

            // Get list of help overlays
            List<OverlayListHelp> newList = this.mindMapScene
                    .getListOfHelpOverlays();

            // Remove this overlay
            newList.remove(this);

            // Set the new list
            this.mindMapScene.setListOfHelpOverlays(newList);

        } else {
            log.error("Error: MindMap provided at initialization is null!"); //$NON-NLS-1$
            // TODO: close app
        }

        log.debug("Leaving destroy()"); //$NON-NLS-1$
    }

}
