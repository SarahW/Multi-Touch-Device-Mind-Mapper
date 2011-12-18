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
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.bluetooth.LocalDevice;

import org.apache.log4j.Logger;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.PickResult;
import org.mt4j.components.PickResult.PickEntry;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.widgets.MTBackgroundImage;
import org.mt4j.components.visibleComponents.widgets.MTSvg;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.buttons.MTSvgButton;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeEvent;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.Direction;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.UnistrokeGesture;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.animation.Animation;
import org.mt4j.util.animation.AnimationEvent;
import org.mt4j.util.animation.IAnimation;
import org.mt4j.util.animation.IAnimationListener;
import org.mt4j.util.animation.MultiPurposeInterpolator;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import de.sarahw.ma.pc.btServer.BluetoothServer;
import de.sarahw.ma.pc.btServer.EBluetoothConnectionListState;
import de.sarahw.ma.pc.btServer.WaitBtThread;
import de.sarahw.ma.pc.mindMapper.ObserverNotificationObject;
import de.sarahw.ma.pc.mindMapper.model.AppModel;
import de.sarahw.ma.pc.mindMapper.model.EAddChildIdeaNodeResultCase;
import de.sarahw.ma.pc.mindMapper.model.EIdeaNodeCreator;
import de.sarahw.ma.pc.mindMapper.model.EIdeaState;
import de.sarahw.ma.pc.mindMapper.model.EMindMapChangeStatus;
import de.sarahw.ma.pc.mindMapper.model.EMindMapCollectionChangeStatus;
import de.sarahw.ma.pc.mindMapper.model.ERemoveChildIdeaNodeResultCase;
import de.sarahw.ma.pc.mindMapper.model.IdeaNode;
import de.sarahw.ma.pc.mindMapper.model.MindMap;
import de.sarahw.ma.pc.mindMapper.model.MindMapCollection;
import de.sarahw.ma.pc.mindMapper.model.Node;
import de.sarahw.ma.pc.mindMapper.model.NodeContent;
import de.sarahw.ma.pc.mindMapper.model.NodeData;
import de.sarahw.ma.pc.mindMapper.model.NodeMetaData;

/**
 * <p>
 * MindMapScene class. Represents the active view scene containing all GUI
 * components. View representation of MindMap model class.
 * </p>
 * 
 * <p>
 * Changes in the model are communicated via the Observer pattern.
 * </p>
 * 
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */
@SuppressWarnings("synthetic-access")
public class MindMapScene extends AbstractScene implements Observer {

    private static Logger               log                                       = Logger.getLogger(MindMapScene.class);

    /* *** Canvas constants *** */
    /** The canvas background svg image file name */
    private static final String         CANVAS_BG_FILE_NAME                       = "canvas_bg.svg";                                                           //$NON-NLS-1$
    /** The canvas background color */
    private static final MTColor        CANVAS_BG_COLOR                           = MindMapperColors.DARK_GREY;

    /* *** Unistroke Gesture constants *** */
    /** The color for a default unistroke gesture */
    private static final MTColor        GESTURE_VISUALIZATION_COLOR_DEFAULT       = MindMapperColors.VERY_LIGHT_GREY_SLIGHT_TRANS;
    /** The color for a processed unistroke gesture */
    protected static final MTColor      GESTURE_VISUALIZATION_COLOR_PROCESSED     = MindMapperColors.YELLOW_SLIGHT_TRANS;
    /** The color for a non-recognized unistroke gesture */
    protected static final MTColor      GESTURE_VISUALIZATION_COLOR_NOTRECOGNIZED = MindMapperColors.RED_SLIGHT_TRANS;
    /** The color for a recognized unistroke gesture */
    protected static final MTColor      GESTURE_VISUALIZATION_COLOR_RECOGNIZED    = MindMapperColors.GREEN_SLIGHT_TRANS;

    /**
     * The radius of the circle around the x gesture center that is used for
     * additional picking
     */
    private static final float          X_GESTURE_PICKING_RADIUS_SCALE_PERCENT    = 0.1f;

    /* *** Overlay constants *** */
    /** The maximum number of help overlay instances */
    private static final int            OVERLAY_HELP_INSTANCES_MAX                = 8;

    /* *** Status message constants *** */
    /** The maximum number of lines for a default status message */
    private static final int            STATUS_MSG_OK_MAXNUMOFLINES               = 2;
    /** The maximum number of lines for a status message with more text */
    private static final int            STATUS_MSG_OK_MORE_TEXT_MAXNUMOFLINES     = 3;

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication       abstractMTapplication;
    /** The application model instance */
    private AppModel                    modelReference;
    /** The application bluetooth server instance */
    private BluetoothServer             bluetoothServerReference;

    /* *** Special Characters / Unicode *** */
    /**
     * The string of special (unicode) characters that has to be pre-loaded from
     * the ideaNodeView fonts to ensure that special characters are correctly
     * drawn when sent from the remote device
     */
    private final String                specialCharsIdeaNodeView                  = "€¢ÄäÜüÖößÂâÅåÃãÆæÇçÊêËëÎîÏïÑñØøÕõÔôŒœÙùÚúÛûÿ`¿¡“”‘’«»£¤¥^°§©¬®±´¶×÷~™„…·"; //$NON-NLS-1$

    /**
     * The string of special (unicode) characters that has to be pre-loaded from
     * the OverlayBluetooth list font to ensure that special characters are
     * correctly drawn when the list is updated while the overlay is open
     */
    private final String                specialCharsOverlayBTList                 = "€¢ÄäÜüÖößÂâÅåÃãÆæÇçÊêËëÎîÏïÑñØøÕõÔôŒœÙùÚúÛûÿ`¿¡“”‘’«»£¤¥^°§©¬®±´¶×÷~™„…·"; //$NON-NLS-1$

    /* *** Calculated widths/heights/radiuses *** */
    /** The ideaNodeView width calculated from the application size */
    private float                       calculatedIdeaNodeWidth;
    /** The ideaNodeView height calculated from the application size */
    private float                       calculatedIdeaNodeHeight;
    /** The bigger menu radius calculated from the application size */
    private float                       calculatedMenuRadiusBigger;
    /** The smaller menu radius calculated from the application size */
    private float                       calculatedMenuRadiusSmaller;
    /** The status message height calculated from the application size */
    private float                       calculatedStatusMsgHeight;
    /** The status message width calculated from the application size */
    private float                       calculatedStatusMsgWidth;
    /** The overlayDefaultList width calculated from the application size */
    private float                       calculatedOverlayDefaultListWidth;
    /** The overlayDefaultList height calculated from the application size */
    private float                       calculatedOverlayDefaultListHeight;
    /** The overlayWideList width calculated from the application size */
    private float                       calculatedOverlayWideListWidth;
    /** The overlayWideList height calculated from the application size */
    private float                       calculatedOverlayWideListHeight;
    /** The overlayForm width calculated from the application size */
    private float                       calculatedOverlayFormWidth;
    /** The overlayForm height calculated from the application size */
    private float                       calculatedOverlayFormHeight;
    /** The overlayPlain width calculated from the application size */
    private float                       calculatedOverlayPlainWidth;
    /** The overlayPlain height calculated from the application size */
    private float                       calculatedOverlayPlainHeight;

    /* *** Fonts used throughout the mt application *** */
    /** The bigger font for the ideaNodeView text */
    private IFont                       ideaNodeViewFontBig;
    /** The smaller font for the ideaNodeView text */
    private IFont                       ideaNodeViewFontSmall;
    /** The small font for the statusMessageDialog text */
    private IFont                       statusMessageFontSmall;
    /** The big font for the statusMessageDialog text */
    private IFont                       statusMessageFontBig;
    /** The very small font for the statusMessageDialog text */
    private IFont                       statusMessageFontVerySmall;
    /** The medium font for the statusMessageDialog text */
    private IFont                       statusMessageFontMedium;
    /** The font for the overlayDefaultList headline text */
    private IFont                       overlayHeadlineFont;
    /** The bigger font for the overlayDefaultList content text */
    private IFont                       overlayListFontBig;
    /** The smaller font for the overlayDefaultList content text */
    private IFont                       overlayListFontSmall;
    /** The font for the overlayForm headline text */
    private IFont                       overlayFormHeadlineFont;
    /** The font for the overlayForm status message text */
    private IFont                       overlayFormStMsgFont;
    /** The font for the overlayForm form text */
    private IFont                       overlayFormTextFont;
    /** The font for the overlayListWide headline text */
    private IFont                       overlayListWideHeadlineFont;
    /** The bigger font for the overlayListWide content text */
    private IFont                       overlayListWideTextFontDefault;
    /** The smaller font for the overlayListWide content text */
    private IFont                       overlayListWideFontSmaller;
    /** The font for the overlayPlain content text */
    private IFont                       overlayPlainFont;
    /** The font for the bluetooth overlay qr code description */
    private IFont                       overlayBtListQrCodeDescriptionFont;

    /* *** MindMapScene widgets *** */
    /** The mainMenu instance */
    private MainMenu                    mainMenu;
    /** The windowMenu instance */
    private WindowMenu                  windowMenu;
    /** The "Load MindMap" overlay instance */
    private OverlayListLoadMindMap      loadMindMapOverlay;
    /** The "Help" overlay instances */
    private List<OverlayListHelp>       listOfHelpOverlays                        = new ArrayList<OverlayListHelp>();
    /** The "List of bluetooth devices" overlay instance */
    private OverlayListBluetooth        bluetoothOverlay;
    /** The "Save mindMap" overlay instance */
    private OverlayFormSave             saveMindMapOverlayForm;
    /** The statusMessageDialog instance */
    private AbstractStatusMessageDialog statusMessageBox;
    /** The "MindMap is loading..." overlay */
    private OverlayPlain                loadingOverlay;
    /** The "MindMap is being saved..." overlay */
    private OverlayPlain                savingOverlay;

    /* *** Helper polygon areas *** */
    /** The polygon representing the canvas area north */
    private MTPolygon                   canvasAreaNorth;
    /** The polygon representing the canvas area south */
    private MTPolygon                   canvasAreaSouth;
    /** The polygon representing the canvas area west */
    private MTPolygon                   canvasAreaWest;
    /** The polygon representing the canvas area east */
    private MTPolygon                   canvasAreaEast;

    /* *** Bluetooth *** */
    /** The name list of all currently connected remote bluetooth devices */
    private List<String>                btConnectionStringList                    = new ArrayList<String>();

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new MindMapScene.
     * 
     * @param mtApplication
     *            the application instance
     * @param name
     *            the name of the scene
     * @param model
     *            the model instance
     * @param btServer
     *            the btServer instance
     */
    public MindMapScene(AbstractMTApplication mtApplication, String name,
            AppModel model, BluetoothServer btServer) {
        super(mtApplication, name);

        log.debug("Executing MindMapScene(mtApplication=" + mtApplication //$NON-NLS-1$
                + ", name=" + name + ", model=" + model + ", btServer=" //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
                + btServer + ")"); //$NON-NLS-1$ 

        // Set Application reference
        this.abstractMTapplication = mtApplication;

        // Set references
        this.modelReference = model;
        this.bluetoothServerReference = btServer;

        // Initialize MindMapScene
        initialize();

    }

    /* ********Getters & Setters******** */

    /**
     * Returns the bluetooth server reference.
     * 
     * @return the bluetoothServerReference
     */
    public BluetoothServer getBluetoothServerReference() {
        return this.bluetoothServerReference;
    }

    /**
     * Returns the calculated IdeaNodeView width.
     * 
     * @return the calculatedIdeaNodeWidth
     */
    public float getCalculatedIdeaNodeWidth() {
        return this.calculatedIdeaNodeWidth;
    }

    /**
     * Returns the calculated IdeaNodeView height.
     * 
     * @return the calculatedIdeaNodeHeight
     */
    public float getCalculatedIdeaNodeHeight() {
        return this.calculatedIdeaNodeHeight;
    }

    /**
     * Returns the calculated radius for the bigger menu.
     * 
     * @return the calculatedMenuRadiusBigger
     */
    public float getCalculatedMenuRadiusBigger() {
        return this.calculatedMenuRadiusBigger;
    }

    /**
     * Returns the calculated radius for the smaller menu.
     * 
     * @return the calculatedMenuRadiusSmaller
     */
    public float getCalculatedMenuRadiusSmaller() {
        return this.calculatedMenuRadiusSmaller;
    }

    /**
     * Returns the calculated height for the status message box.
     * 
     * @return the calculatedStatusMsgHeight
     */
    public float getCalculatedStatusMsgHeight() {
        return this.calculatedStatusMsgHeight;
    }

    /**
     * Returns the calculated width for the status message box.
     * 
     * @return the calculatedStatusMsgWidth
     */
    public float getCalculatedStatusMsgWidth() {
        return this.calculatedStatusMsgWidth;
    }

    /**
     * Returns the calculated width for the general list overlay.
     * 
     * @return the calculatedOverlayDefaultListWidth
     */
    public float getCalculatedOverlayDefaultListWidth() {
        return this.calculatedOverlayDefaultListWidth;
    }

    /**
     * Returns the calculated height for the general list overlay.
     * 
     * @return the calculatedOverlayDefaultListHeight
     */
    public float getCalculatedOverlayDefaultListHeight() {
        return this.calculatedOverlayDefaultListHeight;
    }

    /**
     * Returns the calculated width for the wide list overlay.
     * 
     * @return the calculatedOverlayWideListWidth
     */
    public float getCalculatedOverlayWideListWidth() {
        return this.calculatedOverlayWideListWidth;
    }

    /**
     * Returns the calculated height for the wide list overlay.
     * 
     * @return the calculatedOverlayWideListHeight
     */
    public float getCalculatedOverlayWideListHeight() {
        return this.calculatedOverlayWideListHeight;
    }

    /**
     * Returns the calculated width for the overlay form.
     * 
     * @return the calculatedOverlayFormWidth
     */
    public float getCalculatedOverlayFormWidth() {
        return this.calculatedOverlayFormWidth;
    }

    /**
     * Returns the calculated height for the overlay form.
     * 
     * @return the calculatedOverlayFormHeight
     */
    public float getCalculatedOverlayFormHeight() {
        return this.calculatedOverlayFormHeight;
    }

    /**
     * Returns the calculated width for the overlay plain.
     * 
     * @return the calculatedOverlayPlainWidth
     */
    public float getCalculatedOverlayPlainWidth() {
        return this.calculatedOverlayPlainWidth;
    }

    /**
     * Returns the calculated height for the overlay plain.
     * 
     * @return the calculatedOverlayPlainWidth
     */
    public float getCalculatedOverlayPlainHeight() {
        return this.calculatedOverlayPlainHeight;
    }

    /**
     * Returns the default font (bigger) for the IdeaNodeView
     * 
     * @return the ideaNodeViewFontBig
     */
    public IFont getIdeaNodeViewFontBig() {
        return this.ideaNodeViewFontBig;
    }

    /**
     * Returns the smaller font for the IdeaNodeView
     * 
     * @return the ideaNodeViewFontSmall
     */
    public IFont getIdeaNodeViewFontSmall() {
        return this.ideaNodeViewFontSmall;
    }

    /**
     * Returns the smaller font for the StatusMessageDialog.
     * 
     * @return the statusMessageFontSmall
     */
    public IFont getStatusMessageFontSmall() {
        return this.statusMessageFontSmall;
    }

    /**
     * Returns the bigger font for the StatusMessageDialog.
     * 
     * @return the statusMessageFontBig
     */
    public IFont getStatusMessageFontBig() {
        return this.statusMessageFontBig;
    }

    /**
     * Returns the very small font for the StatusMessageDialog.
     * 
     * @return the statusMessageFontVerySmall
     */
    public IFont getStatusMessageFontVerySmall() {
        return this.statusMessageFontVerySmall;
    }

    /**
     * Returns the medium font for the StatusMessageDialog.
     * 
     * @return the statusMessageFontMedium
     */
    public IFont getStatusMessageFontMedium() {
        return this.statusMessageFontMedium;
    }

    /**
     * Returns the font for the Overlay list headline.
     * 
     * @return the overlayHeadlineFont
     */
    public IFont getOverlayHeadlineFont() {
        return this.overlayHeadlineFont;
    }

    /**
     * Returns the bigger font for Overlay list content.
     * 
     * @return the overlayListFontBig
     */
    public IFont getOverlayListFontBig() {
        return this.overlayListFontBig;
    }

    /**
     * Returns the smaller font for Overlay list content.
     * 
     * @return the overlayListFontSmall
     */
    public IFont getOverlayListFontSmall() {
        return this.overlayListFontSmall;
    }

    /**
     * Returns the save mindMap overlay status message font.
     * 
     * @return the overlayFormStMsgFont
     */
    public IFont getOverlayFormStMsgFont() {
        return this.overlayFormStMsgFont;
    }

    /**
     * Returns the save MindMap overlay form text font.
     * 
     * @return the overlayFormTextFont
     */
    public IFont getOverlayFormTextFont() {
        return this.overlayFormTextFont;
    }

    /**
     * Returns the font for the overlay form headline.
     * 
     * @return the overlayFormHeadlineFont
     */
    public IFont getOverlayFormHeadlineFont() {
        return this.overlayFormHeadlineFont;
    }

    /**
     * Returns the font for the overlay list wide headline.
     * 
     * @return the overlayListWideHeadlineFont
     */
    public IFont getOverlayListWideHeadlineFont() {
        return this.overlayListWideHeadlineFont;
    }

    /**
     * Returns the font for the overlay list wide text, default size.
     * 
     * @return the overlayListWideTextFontDefault
     */
    public IFont getOverlayListWideTextFontDefault() {
        return this.overlayListWideTextFontDefault;
    }

    /**
     * Returns the font for the overlay list wide text, smaller size.
     * 
     * @return the overlayListWideFontSmaller
     */
    public IFont getOverlayListWideFontSmaller() {
        return this.overlayListWideFontSmaller;
    }

    /**
     * Returns the font for the bluetooth overlay list qr code description.
     * 
     * @return the overlayBtListQrCodeDescriptionFont
     */
    public IFont getOverlayBtListQrCodeDescriptionFont() {
        return this.overlayBtListQrCodeDescriptionFont;
    }

    /**
     * Returns the font for the overlay plain.
     * 
     * @return the overlayPlainFont
     */
    public IFont getOverlayPlainFont() {
        return this.overlayPlainFont;
    }

    /**
     * Returns the main menu instance.
     * 
     * @return the mainMenu
     */
    public MainMenu getMainMenu() {
        return this.mainMenu;
    }

    /**
     * Returns the window menu instance.
     * 
     * @return the windowMenu
     */
    public WindowMenu getWindowMenu() {
        return this.windowMenu;
    }

    /**
     * Returns the loadMindMapOverlay, if it is currently part of the component
     * hierarchy.
     * 
     * @return the loadMindMapOverlay
     */
    public OverlayListLoadMindMap getLoadMindMapOverlay() {
        return this.loadMindMapOverlay;
    }

    /**
     * Sets the OverlayListLoadMindMap instance. Used to set to null after a
     * loadMindMap Overlay is closed.
     * 
     * @param loadMindMapOverlay
     *            the loadMindMapOverlay to set
     */
    public void setLoadMindMapOverlay(OverlayListLoadMindMap loadMindMapOverlay) {
        this.loadMindMapOverlay = loadMindMapOverlay;
    }

    /**
     * Returns the list of all currently active OverlayListHelp instances.
     * 
     * @return the listOfHelpOverlays
     */
    public List<OverlayListHelp> getListOfHelpOverlays() {
        return this.listOfHelpOverlays;
    }

    /**
     * Sets the OverlayListHelp list. Used to set after a help Overlay is
     * closed.
     * 
     * @param listOfHelpOverlays
     *            the listOfHelpOverlays to set
     */
    public void setListOfHelpOverlays(List<OverlayListHelp> listOfHelpOverlays) {
        this.listOfHelpOverlays = listOfHelpOverlays;
    }

    /**
     * Returns the bluetoothOverlay, if it is currently part of the component
     * hierarchy.
     * 
     * @return the bluetoothOverlay
     */
    public OverlayListBluetooth getBluetoothOverlay() {
        return this.bluetoothOverlay;
    }

    /**
     * Sets the OverlayListBluetooth instance. Used to set to null after a bt
     * Overlay is closed.
     * 
     * @param bluetoothOverlay
     *            the bluetoothOverlay to set
     */
    public void setBluetoothOverlay(OverlayListBluetooth bluetoothOverlay) {
        this.bluetoothOverlay = bluetoothOverlay;
    }

    /**
     * Returns the saveMindMapOverlayForm, if it is currently part of the
     * component hierarchy.
     * 
     * @return the saveMindMapOverlayForm
     */
    public OverlayFormSave getSaveMindMapOverlayForm() {
        return this.saveMindMapOverlayForm;
    }

    /**
     * Sets the OverlayFormSave instance. Used to set to null after a save
     * Overlay is closed.
     * 
     * 
     * @param saveMindMapOverlayForm
     *            the saveMindMapOverlayForm to set
     */
    public void setSaveMindMapOverlayForm(OverlayFormSave saveMindMapOverlayForm) {
        this.saveMindMapOverlayForm = saveMindMapOverlayForm;
    }

    /**
     * Returns the status message box if it is opened.
     * 
     * @return the statusMessageBox
     */
    public AbstractStatusMessageDialog getStatusMessageBox() {
        return this.statusMessageBox;
    }

    /**
     * Sets the status message box. Used to set to null when status message box
     * is closed.
     * 
     * @param statusMessageBox
     *            the statusMessageBox to set
     */
    public void setStatusMessageBox(AbstractStatusMessageDialog statusMessageBox) {
        this.statusMessageBox = statusMessageBox;
    }

    /**
     * Returns the active model instance.
     * 
     * @return the modelReference
     */
    public AppModel getModelReference() {
        return this.modelReference;
    }

    /**
     * Returns the bluetooth connection String list.
     * 
     * @return the btConnectionStringList
     */
    public List<String> getBtConnectionStringList() {
        return this.btConnectionStringList;
    }

    /* *********Object methods********* */
    /**
     * Initializes the MindMapScene. Sets background color and image, creates
     * and loads fonts for all components and removes/registers listeners.
     * 
     */
    private void initialize() {

        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Check OpenGL
        if (!MT4jSettings.getInstance().isOpenGlMode()) {
            log.warn("Warning: OpenGL mode is off!"); //$NON-NLS-1$
        } else {
            log.info("Info: OpenGL mode is on!"); //$NON-NLS-1$

        }

        // Set depth buffer disabled
        // to avoid display errors because everything is 2D
        this.getCanvas().setDepthBufferDisabled(true);

        // Calculate IdeaNodeView size from application width/height
        // executed once at the start of the application
        calculateIdeaNodeSize();

        // Calculate menu size
        calculateMenuSizes();

        // Calculate status message size
        calculateStatusMessageSize();

        // Calculate overlay size
        calculateOverlaySizes();

        // Set scene background color
        this.setClearColor(CANVAS_BG_COLOR);

        // Remove default listeners from canvas
        // this.getCanvas().unregisterAllInputProcessors();

        // Add cursor to scene
        this.registerGlobalInputProcessor(new CursorTracer(
                this.abstractMTapplication, this));

        // Create fonts for IdeaNodeView text areas
        createFonts();

        // Add dummy text area for special characters
        // (otherwise adding those characters will result in drawing errors!?)
        addDummyTextAreaSpecialChars();

        // DEBUG: check global Input processors
        // ToolsLogging.checkGlobalInputProcessors(this);

        // Add background image
        addBackgroundImage();

        // Add menus
        addMenusToCanvas();

        // Add saving/loading overlays
        addProcessOverlays();

        // Separate the canvas area in two polygons and two triangles for
        // automatic rotation of elements
        separateCanvasIntoAreas();

        // DEBUG: get gestures for canvas
        // ToolsLogging.checkGestureListeners(this.getCanvas());
        // ToolsLogging.checkInputListeners(this.getCanvas());

        // DEBUG/OLD: Add double tap gesture listener to canvas
        // addDoubleTapGestureListener(this.getCanvas());

        // Add rectangle gesture listener to canvas
        addGestureListener(this);

        // Add this as observer to the loaded model mindMap
        // (change when MindMapCollection is updated!)
        this.getModelReference().getLoadedMindMap().addObserver(this);

        // Add this as observer to the model mindMapCollection (stays fixed)
        this.getModelReference().getMindMapCollection().addObserver(this);

        // Add this as a observer to the bluetooth wait thread
        this.getBluetoothServerReference().getWaitThread().addObserver(this);

        // Get bluetooth status &
        // Show bluetooth status message
        showBluetoothStatusMessage(getBluetoothStatus());

        log.debug("Leaving initialize()"); //$NON-NLS-1$

    }

    /**
     * <p>
     * Adds (and removes) an invisible dummy text area that displays given
     * special characters. This is a workaround for a bug that quite possibly
     * originates from the mt4j framework when using unicode characters that are
     * not part of the default character set from BitmapFontFactoryProxy.
     * <p>
     * 
     * <p>
     * See http://nuigroup.com/forums/viewthread/12971/#66709
     * </p>
     * 
     */
    private void addDummyTextAreaSpecialChars() {
        log.debug("Entering addDummyTextAreaSpecialChars()"); //$NON-NLS-1$

        // Create dummy text field for bigger IdeaNodeView font
        MTTextArea dummyTextFontBig = new MTTextArea(
                this.abstractMTapplication, 0, 0, 100, 300,
                getIdeaNodeViewFontBig());

        dummyTextFontBig.setText(this.specialCharsIdeaNodeView);

        dummyTextFontBig.setVisible(false);

        dummyTextFontBig.destroy();

        // Create dummy text field for smaller IdeaNodeView font
        MTTextArea dummyTextFontSmall = new MTTextArea(
                this.abstractMTapplication, 0, 0, 100, 300,
                getIdeaNodeViewFontSmall());

        dummyTextFontSmall.setText(this.specialCharsIdeaNodeView);

        dummyTextFontSmall.setVisible(false);

        dummyTextFontSmall.destroy();

        // Create dummy text field for OverlayBluetooth list big font
        MTTextArea dummyTextBTFontBig = new MTTextArea(
                this.abstractMTapplication, 0, 0, 100, 300,
                getOverlayListFontBig());

        dummyTextBTFontBig.setText(this.specialCharsOverlayBTList);

        dummyTextBTFontBig.setVisible(false);

        dummyTextBTFontBig.destroy();

        // Create dummy text field for OverlayBluetooth list big font
        MTTextArea dummyTextBTFontSmall = new MTTextArea(
                this.abstractMTapplication, 0, 0, 100, 300,
                getOverlayListFontSmall());

        dummyTextBTFontSmall.setText(this.specialCharsOverlayBTList);

        dummyTextBTFontSmall.setVisible(false);

        dummyTextBTFontSmall.destroy();

        log.debug("Leaving addDummyTextAreaSpecialChars()"); //$NON-NLS-1$

    }

    /**
     * Calculates the IdeaNodeSize from the current application width at startup
     * time.
     * 
     */
    private void calculateIdeaNodeSize() {

        log.debug("Entering calculateIdeaNodeSize()"); //$NON-NLS-1$ 

        // Get the size factor by which to calculate the width and height
        float sizeFactor = (this.abstractMTapplication.getWidth() / IdeaNodeView.IDEANODE_WIDTH_DEFAULT)
                * IdeaNodeView.IDEANODE_WIDTH_SCALE_TO_APP_PERCENT;

        // Set IdeaNode width and height
        this.calculatedIdeaNodeWidth = IdeaNodeView.IDEANODE_WIDTH_DEFAULT
                * sizeFactor;
        log.info("IdeaNodeView width calculated to " + this.getCalculatedIdeaNodeWidth()); //$NON-NLS-1$

        this.calculatedIdeaNodeHeight = IdeaNodeView.IDEANODE_HEIGHT_DEFAULT
                * sizeFactor;
        log.info("IdeaNodeView height calculated to " + this.getCalculatedIdeaNodeHeight()); //$NON-NLS-1$

        log.debug("Leaving calculateIdeaNodeSize()"); //$NON-NLS-1$ 

    }

    /**
     * Calculates the MTCircleMenu size (bigger menu) from the current
     * application width at startup time.
     * 
     */
    private void calculateMenuSizes() {

        log.debug("Entering calculateMenuSizes()"); //$NON-NLS-1$ 

        // Get the size factor by which to calculate the width and height
        float sizeFactorBigger = (this.abstractMTapplication.getWidth() / MainMenu.MENU_RADIUS_MAX)
                * MainMenu.MENU_RADIUS_SCALE_TO_APP_PERCENT;

        float sizeFactorSmaller = (this.abstractMTapplication.getWidth() / WindowMenu.MENU_RADIUS_DEFAULT)
                * WindowMenu.MENU_RADIUS_SCALE_TO_APP_PERCENT;

        // Set MTCircularMenu (bigger one) radius
        this.calculatedMenuRadiusBigger = MainMenu.MENU_RADIUS_MAX
                * sizeFactorBigger;
        log.info("Bigger menu radius calculated to " //$NON-NLS-1$
                + this.getCalculatedMenuRadiusBigger());

        // Set MTCircularMenu (smaller one) radius
        this.calculatedMenuRadiusSmaller = WindowMenu.MENU_RADIUS_DEFAULT
                * sizeFactorSmaller;
        log.info("Smaller menu radius  calculated to " //$NON-NLS-1$
                + this.getCalculatedMenuRadiusSmaller());

        log.debug("Leaving calculateMenuSizes()"); //$NON-NLS-1$ 

    }

    /**
     * Calculates the size of of status messages from the current application
     * size at startup time.
     * 
     */
    private void calculateStatusMessageSize() {

        log.debug("Entering calculateStatusMessageSize()"); //$NON-NLS-1$

        // Get the size factor by which to calculate the width and height
        float sizeFactor = (this.abstractMTapplication.getWidth() / AbstractStatusMessageDialog.STATUS_MSG_WIDTH_DEFAULT)
                * AbstractStatusMessageDialog.STATUS_MSG_WIDTH_SCALE_TO_APP_PERCENT;

        // Set IdeaNode width and height
        this.calculatedStatusMsgWidth = AbstractStatusMessageDialog.STATUS_MSG_WIDTH_DEFAULT
                * sizeFactor;
        log.info("StatusMessageDialog width calculated to " + this.getCalculatedStatusMsgWidth()); //$NON-NLS-1$

        this.calculatedStatusMsgHeight = AbstractStatusMessageDialog.STATUS_MSG_HEIGHT_DEFAULT
                * sizeFactor;
        log.info("StatusMessageDialog height calculated to " + this.getCalculatedStatusMsgHeight()); //$NON-NLS-1$

        log.debug("Leaving calculateStatusMessageSize()"); //$NON-NLS-1$ 

    }

    /**
     * Calculates the size of overlays (Load MindMap, Save MindMap, Help,
     * Bluetooth menu) from the current application size at startup time as well
     * as the general overlay handle size.
     * 
     */
    private void calculateOverlaySizes() {

        log.debug("Entering calculateOverlaySizes()"); //$NON-NLS-1$

        // OverlayList Default (Load MindMap, BluetoothMenu)
        // Get the size factor by which to calculate the width and height
        float sizeFactorOverlayList = (this.abstractMTapplication.getWidth() / AbstractOverlayDefaultList.OVERLAY_WIDTH_MAX)
                * AbstractOverlayDefaultList.OVERLAY_WIDTH_SCALE_TO_APP_PERCENT;

        // Set default overlay list width and height
        this.calculatedOverlayDefaultListWidth = AbstractOverlayDefaultList.OVERLAY_WIDTH_MAX
                * sizeFactorOverlayList;
        log.info("General OverlayList width calculated to " + this.getCalculatedOverlayDefaultListWidth()); //$NON-NLS-1$

        this.calculatedOverlayDefaultListHeight = AbstractOverlayDefaultList.OVERLAY_HEIGHT_MAX
                * sizeFactorOverlayList;
        log.info("General OverlayList height calculated to " + this.getCalculatedOverlayDefaultListHeight()); //$NON-NLS-1$

        // OverlayList Wide (Help)
        // Get the size factor by which to calculate the width and height
        float sizeFactorOverlayWideList = (this.abstractMTapplication
                .getWidth() / AbstractOverlayWideList.OVERLAY_WIDTH_MAX)
                * AbstractOverlayWideList.OVERLAY_WIDTH_SCALE_TO_APP_PERCENT;

        // Set wide overlay list width and height
        this.calculatedOverlayWideListWidth = AbstractOverlayWideList.OVERLAY_WIDTH_MAX
                * sizeFactorOverlayWideList;
        log.info("Wide OverlayList width calculated to " + this.getCalculatedOverlayWideListWidth()); //$NON-NLS-1$

        this.calculatedOverlayWideListHeight = AbstractOverlayWideList.OVERLAY_HEIGHT_MAX
                * sizeFactorOverlayWideList;
        log.info("Wide OverlayList height calculated to " + this.getCalculatedOverlayWideListHeight()); //$NON-NLS-1$

        // OverlayForm (SaveMindMap)
        float sizeFactorOverlayForm = (this.abstractMTapplication.getWidth() / AbstractOverlayForm.OVERLAY_WIDTH_DEFAULT)
                * AbstractOverlayForm.OVERLAY_WIDTH_SCALE_TO_APP_PERCENT;

        // Set overlay form width and height
        this.calculatedOverlayFormWidth = AbstractOverlayForm.OVERLAY_WIDTH_DEFAULT
                * sizeFactorOverlayForm;
        log.info("OverlayForm width calculated to " + this.getCalculatedOverlayFormWidth()); //$NON-NLS-1$

        this.calculatedOverlayFormHeight = AbstractOverlayForm.OVERLAY_HEIGHT_DEFAULT
                * sizeFactorOverlayForm;
        log.info("OverlayForm height calculated to " + this.getCalculatedOverlayFormHeight()); //$NON-NLS-1$

        // Overlay Plain (saving and loading)
        // Get the size factor by which to calculate the width and height
        float sizeFactorOverlayPlain = (this.abstractMTapplication.getWidth() / OverlayPlain.OVERLAY_PLAIN_WIDTH_DEFAULT)
                * OverlayPlain.OVERLAY_PLAIN_WIDTH_SCALE_TO_APP_PERCENT;

        // Set overlay plain width and height
        this.calculatedOverlayPlainWidth = OverlayPlain.OVERLAY_PLAIN_WIDTH_DEFAULT
                * sizeFactorOverlayPlain;
        log.info("OverlayPlain width calculated to " + this.getCalculatedOverlayPlainWidth()); //$NON-NLS-1$

        this.calculatedOverlayPlainHeight = OverlayPlain.OVERLAY_PLAIN_HEIGHT_DEFAULT
                * sizeFactorOverlayForm;

        log.info("OverlayPlain height calculated to " + this.getCalculatedOverlayPlainHeight()); //$NON-NLS-1$

        log.debug("Leaving calculateOverlaySizes()"); //$NON-NLS-1$ 

    }

    /**
     * Creates and loads fonts for several components at startup time.
     * 
     * @return true, if all fonts could be created successfully
     */
    private boolean createFonts() {

        log.debug("Entering createFonts()"); //$NON-NLS-1$

        // Create new outline sans serif font for IdeaNodeView text areas with
        // one line
        IFont ideaNodeFont = FontManager.getInstance()
                .createFont(this.abstractMTapplication,
                        NodeContentContainer.FONT_FILE_NAME,
                        NodeContentContainer.DEFAULT_FONT_SIZE_BIG,
                        MTColor.BLACK, true);

        // Set font
        if (ideaNodeFont != null) {
            this.ideaNodeViewFontBig = ideaNodeFont;

        } else {
            log.error("Font " + NodeContentContainer.FONT_FILE_NAME + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Leaving createFonts(): false"); //$NON-NLS-1$
            return false;
        }

        // Create new outline sans serif font for IdeaNodeView text areas with
        // two lines
        IFont ideaNodeFontSmall = FontManager.getInstance().createFont(
                this.abstractMTapplication,
                NodeContentContainer.FONT_FILE_NAME,
                NodeContentContainer.DEFAULT_FONT_SIZE_SMALL, MTColor.BLACK,
                true);

        if (ideaNodeFontSmall != null) {
            this.ideaNodeViewFontSmall = ideaNodeFontSmall;

        } else {
            log.error("Font " + NodeContentContainer.FONT_FILE_NAME + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Leaving createFonts(): false"); //$NON-NLS-1$
            return false;
        }

        // Create new outlines sans serif font for StatusMessageDialog
        IFont statusMsgFontVerySmall = FontManager.getInstance().createFont(
                this.abstractMTapplication,
                AbstractStatusMessageDialog.STATUS_MSG_FONT_FILE_NAME,
                AbstractStatusMessageDialog.STATUS_MSG_FONT_SIZE_VERY_SMALL,
                MTColor.BLACK, true);

        if (statusMsgFontVerySmall != null) {
            this.statusMessageFontVerySmall = statusMsgFontVerySmall;

        } else {
            log.error("Font " + AbstractStatusMessageDialog.STATUS_MSG_FONT_FILE_NAME + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Leaving createFonts(): false"); //$NON-NLS-1$
            return false;
        }

        // Create new outlines sans serif font for StatusMessageDialog
        IFont statusMsgFontSmall = FontManager.getInstance().createFont(
                this.abstractMTapplication,
                AbstractStatusMessageDialog.STATUS_MSG_FONT_FILE_NAME,
                AbstractStatusMessageDialog.STATUS_MSG_FONT_SIZE_SMALL,
                MTColor.BLACK, true);

        if (statusMsgFontSmall != null) {
            this.statusMessageFontSmall = statusMsgFontSmall;

        } else {
            log.error("Font " + AbstractStatusMessageDialog.STATUS_MSG_FONT_FILE_NAME + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Leaving createFonts(): false"); //$NON-NLS-1$
            return false;
        }

        // Create new outlines sans serif font for StatusMessageDialog
        IFont statusMsgFontMedium = FontManager.getInstance().createFont(
                this.abstractMTapplication,
                AbstractStatusMessageDialog.STATUS_MSG_FONT_FILE_NAME,
                AbstractStatusMessageDialog.STATUS_MSG_FONT_SIZE_BIG,
                MTColor.BLACK, true);

        if (statusMsgFontMedium != null) {
            this.statusMessageFontMedium = statusMsgFontMedium;

        } else {
            log.error("Font " + AbstractStatusMessageDialog.STATUS_MSG_FONT_FILE_NAME + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Leaving createFonts(): false"); //$NON-NLS-1$
            return false;
        }

        // Create new outlines sans serif font for StatusMessageDialog
        IFont statusMsgFontBig = FontManager.getInstance().createFont(
                this.abstractMTapplication,
                AbstractStatusMessageDialog.STATUS_MSG_FONT_FILE_NAME,
                AbstractStatusMessageDialog.STATUS_MSG_FONT_SIZE_BIG,
                MTColor.BLACK, true);

        if (statusMsgFontBig != null) {
            this.statusMessageFontBig = statusMsgFontBig;

        } else {
            log.error("Font " + AbstractStatusMessageDialog.STATUS_MSG_FONT_FILE_NAME + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Leaving createFonts(): false"); //$NON-NLS-1$
            return false;
        }

        // Create new outline sans serif font for Overlay headline text field
        // with one line
        IFont overlayHeadlineFont = FontManager.getInstance().createFont(
                this.abstractMTapplication,
                AbstractOverlayDefaultList.HEADLINE_FONT_FILE_NAME,
                AbstractOverlayDefaultList.HEADLINE_FONT_SIZE, MTColor.BLACK,
                true);

        // Set font
        if (overlayHeadlineFont != null) {
            this.overlayHeadlineFont = overlayHeadlineFont;

        } else {
            log.error("Font " + AbstractOverlayDefaultList.HEADLINE_FONT_FILE_NAME + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Leaving createFonts(): false"); //$NON-NLS-1$
            return false;
        }

        // Create new outline sans serif font for Overlay loadMindMap list
        // content (smaller)

        IFont overlayListContentFontSmall = FontManager
                .getInstance()
                .createFont(
                        this.abstractMTapplication,
                        AbstractOverlayDefaultList.LIST_CONTENT_FONT_SMALL_FILE_NAME,
                        AbstractOverlayDefaultList.LIST_CONTENT_FONT_SMALL_SIZE,
                        MTColor.BLACK, true);

        // Set font
        if (overlayListContentFontSmall != null) {
            this.overlayListFontSmall = overlayListContentFontSmall;

        } else {
            log.error("Font " + AbstractOverlayDefaultList.LIST_CONTENT_FONT_SMALL_FILE_NAME + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Leaving createFonts(): false"); //$NON-NLS-1$
            return false;
        }

        // Create new outline sans serif font for Overlay loadMindMap list
        // content (bigger)
        IFont overlayListContentFontBig = FontManager.getInstance().createFont(
                this.abstractMTapplication,
                AbstractOverlayDefaultList.LIST_CONTENT_FONT_BIG_FILE_NAME,
                AbstractOverlayDefaultList.LIST_CONTENT_FONT_BIG_SIZE,
                MTColor.BLACK, true);

        // Set font
        if (overlayListContentFontBig != null) {
            this.overlayListFontBig = overlayListContentFontBig;

        } else {
            log.error("Font " + AbstractOverlayDefaultList.LIST_CONTENT_FONT_BIG_FILE_NAME + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Leaving createFonts(): false"); //$NON-NLS-1$
            return false;
        }

        // Create new outline sans serif font for Overlay save mindMap list
        // headline
        IFont overlayFormHeadlineFont = FontManager.getInstance().createFont(
                this.abstractMTapplication,
                AbstractOverlayForm.HEADLINE_FONT_FILE_NAME,
                AbstractOverlayForm.HEADLINE_FONT_SIZE, MTColor.BLACK, true);

        // Set font
        if (overlayFormHeadlineFont != null) {
            this.overlayFormHeadlineFont = overlayFormHeadlineFont;

        } else {
            log.error("Font " + AbstractOverlayForm.HEADLINE_FONT_FILE_NAME + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Leaving createFonts(): false"); //$NON-NLS-1$
            return false;
        }

        // Create new outline sans serif font for Overlay save mindMap list
        // text
        IFont overlayFormTextFont = FontManager.getInstance().createFont(
                this.abstractMTapplication,
                AbstractOverlayForm.FORM_TEXTFIELD_FONT_FILE_NAME,
                AbstractOverlayForm.FORM_TEXTFIELD_FONT_SIZE, MTColor.BLACK,
                true);

        // Set font
        if (overlayFormTextFont != null) {
            this.overlayFormTextFont = overlayFormTextFont;

        } else {
            log.error("Font " + AbstractOverlayForm.FORM_TEXTFIELD_FONT_FILE_NAME + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Leaving createFonts(): false"); //$NON-NLS-1$
            return false;
        }

        // Create new outline sans serif font for Overlay save mind map
        // status message

        IFont overlayFormStatusMessage = FontManager.getInstance().createFont(
                this.abstractMTapplication,
                OverlayFormSave.FORM_STATUS_MSG_FONT_FILE_NAME,
                OverlayFormSave.FORM_STATUS_MSG_FONT_SIZE, MTColor.BLACK, true);

        // Set font
        if (overlayFormStatusMessage != null) {
            this.overlayFormStMsgFont = overlayFormStatusMessage;

        } else {
            log.error("Font " + OverlayFormSave.FORM_STATUS_MSG_FONT_FILE_NAME + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Leaving createFonts(): false"); //$NON-NLS-1$
            return false;
        }

        // Create new outline sans serif font for Overlay save mindMap list
        // headline
        IFont overlayListWideHeadlineFont = FontManager.getInstance()
                .createFont(this.abstractMTapplication,
                        AbstractOverlayWideList.HEADLINE_FONT_FILE_NAME,
                        AbstractOverlayWideList.HEADLINE_FONT_SIZE,
                        MTColor.BLACK, true);

        // Set font
        if (overlayListWideHeadlineFont != null) {
            this.overlayListWideHeadlineFont = overlayListWideHeadlineFont;

        } else {
            log.error("Font " + AbstractOverlayWideList.HEADLINE_FONT_FILE_NAME + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Leaving createFonts(): false"); //$NON-NLS-1$
            return false;
        }

        // Create new outline sans serif font for Overlay save mindMap list
        // text
        IFont overlayListWideTextFontDefault = FontManager
                .getInstance()
                .createFont(
                        this.abstractMTapplication,
                        AbstractOverlayWideList.LIST_CONTENT_FONT_BIG_FILE_NAME,
                        AbstractOverlayWideList.LIST_CONTENT_FONT_BIG_SIZE,
                        MTColor.BLACK, true);

        // Set font
        if (overlayListWideTextFontDefault != null) {
            this.overlayListWideTextFontDefault = overlayListWideTextFontDefault;

        } else {
            log.error("Font " + AbstractOverlayWideList.LIST_CONTENT_FONT_BIG_FILE_NAME + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Leaving createFonts(): false"); //$NON-NLS-1$
            return false;
        }

        // Create new outline sans serif font for Overlay save mind map
        // status message

        IFont overlayListWideFontSmaller = FontManager
                .getInstance()
                .createFont(
                        this.abstractMTapplication,
                        AbstractOverlayWideList.LIST_CONTENT_FONT_SMALL_FILE_NAME,
                        AbstractOverlayWideList.LIST_CONTENT_FONT_SMALL_SIZE,
                        MTColor.BLACK, true);

        // Set font
        if (overlayListWideFontSmaller != null) {
            this.overlayListWideFontSmaller = overlayListWideFontSmaller;

        } else {
            log.error("Font " + AbstractOverlayWideList.LIST_CONTENT_FONT_SMALL_FILE_NAME + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Leaving createFonts(): false"); //$NON-NLS-1$
            return false;
        }

        // Create new outline sans serif font for Overlay bluetooth
        // list CR code description text
        IFont overlayBtQrCodeDescriptionText = FontManager.getInstance()
                .createFont(this.abstractMTapplication,
                        OverlayListBluetooth.QR_CODE_DESCR_TEXT_FONT_NAME,
                        OverlayListBluetooth.QR_CODE_DESCR_TEXT_FONT_SIZE,
                        MTColor.BLACK, true);

        // Set font
        if (overlayBtQrCodeDescriptionText != null) {
            this.overlayBtListQrCodeDescriptionFont = overlayBtQrCodeDescriptionText;

        } else {
            log.error("Font " + OverlayListBluetooth.QR_CODE_DESCR_TEXT_FONT_NAME + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Leaving createFonts(): false"); //$NON-NLS-1$
            return false;
        }

        // Create new outlines sans serif font for OverlayPlain
        IFont overlayPlainFont = FontManager.getInstance().createFont(
                this.abstractMTapplication,
                OverlayPlain.OVERLAY_PLAIN_FONT_FILE_NAME,
                OverlayPlain.OVERLAY_PLAIN_FONT_SIZE, MTColor.BLACK, true);

        if (overlayPlainFont != null) {
            this.overlayPlainFont = overlayPlainFont;

        } else {
            log.error("Font " + OverlayPlain.OVERLAY_PLAIN_FONT_FILE_NAME + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Leaving createFonts(): false"); //$NON-NLS-1$
            return false;
        }

        log.debug("Leaving createFonts(): true"); //$NON-NLS-1$
        return true;

    }

    /**
     * Adds a new background image to the canvas.
     */
    private void addBackgroundImage() {

        log.debug("Entering addBackgroundImage()"); //$NON-NLS-1$ 

        // Add background image
        MTSvg backgroundSvg = new MTSvg(this.abstractMTapplication,
                MT4jSettings.getInstance().getDefaultSVGPath()
                        + CANVAS_BG_FILE_NAME);

        MTBackgroundImage background = new MTBackgroundImage(
                this.abstractMTapplication, backgroundSvg, true, true);

        this.getCanvas().addChild(background);

        log.debug("Leaving addBackgroundImage()"); //$NON-NLS-1$ 

    }

    /**
     * Adds menus to the canvas at initialization.
     * 
     */
    private void addMenusToCanvas() {

        log.debug("Entering addMenusToCanvas()"); //$NON-NLS-1$

        // Create (four) buttons for main menu
        List<MTSvgButton> buttonListMain = new ArrayList<MTSvgButton>();

        // Create load button
        MTSvgButton loadButton = new MTSvgButton(this.abstractMTapplication,
                MainMenu.MENU_MAIN_BTN_LOAD_IMG_PATH);

        // Create save button
        MTSvgButton saveButton = new MTSvgButton(this.abstractMTapplication,
                MainMenu.MENU_MAIN_BTN_SAVE_IMG_PATH);

        // Create bluetooth button
        MTSvgButton blToothButton = new MTSvgButton(this.abstractMTapplication,
                MainMenu.MENU_MAIN_BTN_BTOOTH_IMG_PATH);

        // Create help button
        MTSvgButton helpButton = new MTSvgButton(this.abstractMTapplication,
                MainMenu.MENU_MAIN_BTN_HELP_IMG_PATH);

        // Add to buttons list
        buttonListMain.add(loadButton);
        buttonListMain.add(saveButton);
        buttonListMain.add(blToothButton);
        buttonListMain.add(helpButton);

        // Calculate placement for main menu
        // Get center point vector
        Vector3D centerOfApp = new Vector3D(
                this.abstractMTapplication.getWidth() / 2f,
                this.abstractMTapplication.getHeight() / 2f, 0);

        // Get half the center point vector
        Vector3D mainMenuPosition = centerOfApp.getCopy().getScaled(0.25f);

        // Add main menu
        MainMenu mainMenu = new MainMenu(this.abstractMTapplication, this,
                mainMenuPosition, getCalculatedMenuRadiusBigger(),
                buttonListMain);

        // Reposition
        mainMenu.setPositionGlobal(new Vector3D(mainMenu
                .getWidthXY(TransformSpace.GLOBAL), mainMenu
                .getHeightXY(TransformSpace.GLOBAL), 0));

        // Add to canvas
        this.getCanvas().addChild(mainMenu);

        // Create buttons for window menu
        List<MTSvgButton> buttonListWindow = new ArrayList<MTSvgButton>();

        // Create close button
        MTSvgButton closeButton = new MTSvgButton(this.abstractMTapplication,
                WindowMenu.MENU_WINDOW_BTN_CLOSE_IMG_PATH);

        // Create minimize button
        MTSvgButton minimizeButton = new MTSvgButton(
                this.abstractMTapplication,
                WindowMenu.MENU_WINDOW_BTN_MIN_IMG_PATH);

        // Add to buttons list
        buttonListWindow.add(closeButton);
        buttonListWindow.add(minimizeButton);

        // Calculate placement for window menu
        // Get half the center point
        Vector3D windowMenuPosition = centerOfApp.getCopy().getScaled(1.75f);

        // Add window menu
        WindowMenu windowMenu = new WindowMenu(this.abstractMTapplication,
                windowMenuPosition, getCalculatedMenuRadiusSmaller(),
                buttonListWindow, this);

        // Reposition
        windowMenu.setPositionGlobal(new Vector3D(this.abstractMTapplication
                .getWidth() - windowMenu.getWidthXY(TransformSpace.GLOBAL),
                this.abstractMTapplication.getHeight()
                        - windowMenu.getHeightXY(TransformSpace.GLOBAL), 0));

        // Add to canvas
        this.getCanvas().addChild(windowMenu);

        log.debug("Leaving addMenusToCanvas()"); //$NON-NLS-1$

    }

    /**
     * Adds overlays for processing actions like saving and loading. Visibility
     * false by default, set to visible when needed.
     * 
     */
    private void addProcessOverlays() {

        log.debug("Entering addProcessOverlays()"); //$NON-NLS-1$

        // Get center of app
        float x = this.abstractMTapplication.getWidth() / 2f;
        float y = this.abstractMTapplication.getHeight() / 2f;

        // Add loading overlay
        this.loadingOverlay = new OverlayPlain(
                this.abstractMTapplication,
                0,
                0,
                getCalculatedOverlayPlainWidth(),
                getCalculatedOverlayPlainHeight(),
                Messages.getString("MindMapScene.addProcessOverlays.LoadMindMap"), //$NON-NLS-1$
                getOverlayPlainFont(), this);

        this.loadingOverlay.setVisible(false);

        this.getCanvas().addChild(this.loadingOverlay);
        // Set at center
        this.loadingOverlay.setPositionGlobal(new Vector3D(x, y));

        // Add saving overlay
        this.savingOverlay = new OverlayPlain(
                this.abstractMTapplication,
                0,
                0,
                getCalculatedOverlayPlainWidth(),
                getCalculatedOverlayPlainHeight(),
                Messages.getString("MindMapScene.addProcessOverlays.SaveMindMap"), //$NON-NLS-1$
                getOverlayPlainFont(), this);

        this.savingOverlay.setVisible(false);

        this.getCanvas().addChild(this.savingOverlay);

        // Set at center
        this.savingOverlay.setPositionGlobal(new Vector3D(x, y));

        log.debug("Leaving addProcessOverlays()"); //$NON-NLS-1$

    }

    /**
     * Separates the canvas area of the Application in four areas. Required to
     * determine which area a position belongs to, used for automatically
     * rotating new IdeaViewNodes to a certain side of the Application.
     * 
     */
    private void separateCanvasIntoAreas() {

        log.debug("Entering separateCanvasIntoAreas()"); //$NON-NLS-1$

        // Create polygons to separate canvas area into four areas
        // Offscreen areas are also used because overlays may be created
        // with their center offscreen if the menu is close enough to
        // the screen border

        // Polygon with six vertices, north of canvas
        Vertex[] vertexNorth = new Vertex[] {
                new Vertex(0, 0, 0, 255, 0, 0, 255),
                new Vertex(-this.abstractMTapplication.getWidth() / 3f,
                        -this.abstractMTapplication.getHeight() / 2f, 0, 0,
                        255, 0, 255),
                new Vertex(this.abstractMTapplication.getWidth()
                        + this.abstractMTapplication.getWidth() / 3f,
                        -this.abstractMTapplication.getHeight() / 2f, 0, 0,
                        255, 0, 255),
                new Vertex(this.abstractMTapplication.getWidth(), 0, 0, 0, 255,
                        0, 255),
                new Vertex((this.abstractMTapplication.getWidth() / 3f) * 2,
                        this.abstractMTapplication.getHeight() / 2f, 0, 0, 255,
                        0, 255),
                new Vertex(this.abstractMTapplication.getWidth() / 3f,
                        this.abstractMTapplication.getHeight() / 2f, 0, 0, 255,
                        0, 255), new Vertex(0, 0, 0, 255, 0, 0, 255), };
        MTPolygon polygonNorth = new MTPolygon(this.abstractMTapplication,
                vertexNorth);

        // Polygon with six vertices, south of canvas
        Vertex[] vertexSouth = new Vertex[] {
                new Vertex(0, this.abstractMTapplication.getHeight(), 0, 255,
                        0, 0, 255),
                new Vertex(this.abstractMTapplication.getWidth() / 3f,
                        this.abstractMTapplication.getHeight() / 2f, 0, 0, 255,
                        0, 255),
                new Vertex((this.abstractMTapplication.getWidth() / 3f) * 2,
                        this.abstractMTapplication.getHeight() / 2f, 0, 0, 255,
                        0, 255),
                new Vertex(this.abstractMTapplication.getWidth(),
                        this.abstractMTapplication.getHeight(), 0, 0, 255, 0,
                        255),
                new Vertex(this.abstractMTapplication.getWidth()
                        + this.abstractMTapplication.getWidth() / 3f,
                        this.abstractMTapplication.getHeight()
                                + this.abstractMTapplication.getHeight() / 2f,
                        0, 255, 0, 0, 255),
                new Vertex(-this.abstractMTapplication.getWidth() / 3f,
                        this.abstractMTapplication.getHeight()
                                + this.abstractMTapplication.getHeight() / 2f,
                        0, 255, 0, 0, 255),
                new Vertex(0, this.abstractMTapplication.getHeight(), 0, 255,
                        0, 0, 255), };
        MTPolygon polygonSouth = new MTPolygon(this.abstractMTapplication,
                vertexSouth);

        // Polygon with five vertices, west of canvas
        Vertex[] vertexWest = new Vertex[] {
                new Vertex(0, 0, 0, 255, 0, 0, 255),
                new Vertex(this.abstractMTapplication.getWidth() / 3f,
                        this.abstractMTapplication.getHeight() / 2f, 0, 0, 255,
                        0, 255),
                new Vertex(0, this.abstractMTapplication.getHeight(), 0, 255,
                        0, 0, 255),
                new Vertex(-this.abstractMTapplication.getWidth() / 3f,
                        this.abstractMTapplication.getHeight()
                                + this.abstractMTapplication.getHeight() / 2f,
                        0, 255, 0, 0, 255),
                new Vertex(-this.abstractMTapplication.getWidth() / 3f,
                        -this.abstractMTapplication.getHeight() / 2f, 0, 0,
                        255, 0, 255), new Vertex(0, 0, 0, 255, 0, 0, 255), };
        MTPolygon polygonWest = new MTPolygon(this.abstractMTapplication,
                vertexWest);

        // Polygon with five vertices, east of canvas
        Vertex[] vertexEast = new Vertex[] {
                new Vertex(this.abstractMTapplication.getWidth(), 0, 0, 0, 255,
                        0, 255),
                new Vertex((this.abstractMTapplication.getWidth() / 3f) * 2,
                        this.abstractMTapplication.getHeight() / 2f, 0, 0, 255,
                        0, 255),
                new Vertex(this.abstractMTapplication.getWidth(),
                        this.abstractMTapplication.getHeight(), 0, 255, 0, 0,
                        255),
                new Vertex(this.abstractMTapplication.getWidth()
                        + this.abstractMTapplication.getWidth() / 3f,
                        this.abstractMTapplication.getHeight()
                                + this.abstractMTapplication.getHeight() / 2f,
                        0, 255, 0, 0, 255),
                new Vertex(this.abstractMTapplication.getWidth()
                        + this.abstractMTapplication.getWidth() / 3f,
                        -this.abstractMTapplication.getHeight() / 2f, 0, 255,
                        0, 0, 255),
                new Vertex(this.abstractMTapplication.getWidth(), 0, 0, 0, 255,
                        0, 255), };
        MTPolygon polygonEast = new MTPolygon(this.abstractMTapplication,
                vertexEast);

        // Remove all gesture and input listeners from polygons
        polygonNorth.removeAllGestureEventListeners();
        polygonSouth.removeAllGestureEventListeners();
        polygonWest.removeAllGestureEventListeners();
        polygonEast.removeAllGestureEventListeners();

        // Set members
        this.canvasAreaNorth = polygonNorth;
        this.canvasAreaSouth = polygonSouth;
        this.canvasAreaEast = polygonEast;
        this.canvasAreaWest = polygonWest;

        // DEBUG: Add to canvas to see if shape is correct, ! No canvas gestures
        // possible on area!
        // this.getCanvas().addChild(polygonNorth);
        // this.getCanvas().addChild(polygonSouth);
        // this.getCanvas().addChild(polygonWest);
        // this.getCanvas().addChild(polygonEast);

        log.debug("Leaving separateCanvasIntoAreas()"); //$NON-NLS-1$

    }

    /**
     * Gets the bluetooth status via the local device reference.
     * 
     * @return EBluetoothStatus the bluetooth status
     */
    private EBluetoothStatus getBluetoothStatus() {
        log.debug("Leaving getBluetoothStatus()"); //$NON-NLS-1$

        // Get bluetooth local device
        if (this.bluetoothServerReference != null) {

            LocalDevice localDevice = this.bluetoothServerReference
                    .getWaitThread().getLocalDevice();

            if (localDevice != null) {
                if (LocalDevice.isPowerOn()) {

                    return EBluetoothStatus.BLUETOOTH_AVAILABLE_ON;
                }
                return EBluetoothStatus.BLUETOOTH_AVAILABLE_OFF;

            }
            return EBluetoothStatus.BLUETOOTH_NOT_AVAILABLE;

        }
        log.error("BluetoothServerReference invalid! (null)"); //$NON-NLS-1$
        log.debug("Leaving getBluetoothStatus()"); //$NON-NLS-1$
        return EBluetoothStatus.BLUETOOTH_AVAILABLE_OFF;

    }

    /**
     * Shows a bluetooth connection status message depending on the bluetooth
     * status.
     */
    private void showBluetoothStatusMessage(EBluetoothStatus btStatus) {

        log.debug("Entering showBluetoothStatusMessage(btStatus=" + btStatus + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (btStatus != null) {

            switch (btStatus) {
                case BLUETOOTH_AVAILABLE_ON:
                    // Show info status message that bluetooth is on
                    showStatusMessageOK(
                            Messages.getString("MindMapScene.showBluetoothStatusMessage.0.btActive"), //$NON-NLS-1$
                            EStatusMessageType.STATUS_MSG_INFO,
                            getStatusMessageFontSmall(),
                            getStatusMessageFontBig(),
                            STATUS_MSG_OK_MAXNUMOFLINES);
                    break;
                case BLUETOOTH_AVAILABLE_OFF:
                    // Show info status message that bluetooth is off
                    showStatusMessageOK(
                            Messages.getString("MindMapScene.showBluetoothStatusMessage.1.btInactive"), //$NON-NLS-1$
                            EStatusMessageType.STATUS_MSG_WARNING,
                            getStatusMessageFontVerySmall(),
                            getStatusMessageFontMedium(),
                            STATUS_MSG_OK_MORE_TEXT_MAXNUMOFLINES);

                    break;

                case BLUETOOTH_NOT_AVAILABLE:
                    // Show info status message that bluetooth is not available
                    showStatusMessageOK(
                            (Messages
                                    .getString("MindMapScene.showBluetoothStatusMessage.2.btUnavailable")), //$NON-NLS-1$
                            EStatusMessageType.STATUS_MSG_ERROR,
                            getStatusMessageFontVerySmall(),
                            getStatusMessageFontMedium(),
                            STATUS_MSG_OK_MORE_TEXT_MAXNUMOFLINES);

                    // Show info status message that bluetooth is off
                    // showStatusMessageOKMoreText(
                    //        Messages.getString("MindMapScene.showBluetoothStatusMessage.1.btInactive"), //$NON-NLS-1$
                    // EStatusMessageType.STATUS_MSG_WARNING);

                    break;

                default:
                    break;
            }

        }

        log.debug("Leaving showBluetoothStatusMessage()"); //$NON-NLS-1$

    }

    /**
     * Creates a new IdeaNode in the model with the given parameters.
     * 
     * @param x
     *            the x position of the IdeaNode
     * @param y
     *            the y position of the IdeaNode
     * @param rotation
     *            the rotation of the IdeaNode
     * @param text
     *            the text of the IdeaNode
     * @param creator
     *            the creator of the IdeaNode
     * 
     * @return true, if creation of model idea node was successful
     */
    protected boolean createModelIdeaNode(float x, float y, float rotation,
            String text, EIdeaNodeCreator creator) {

        log.debug("Entering createModelIdeaNode(x=" + x + ", y=" + y //$NON-NLS-1$ //$NON-NLS-2$
                + ", rotation=" + rotation + ", text=" + text + ", creator=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + creator);

        // Create a new IdeaNode in the model with the given parameters
        IdeaNode newIdeaNode = new IdeaNode(new NodeData(new NodeContent(text),
                new NodeMetaData(x, y, rotation, creator)));

        log.debug("Leaving createModelIdeaNode(): "); //$NON-NLS-1$
        return getModelReference().getLoadedMindMap().addIdeaNode(newIdeaNode);

    }

    /**
     * Creates a new IdeaNodeView object at the given position. Called by the
     * update() method of this MindMapScene (= Observer subclass) when changes
     * in the model require the creation of a new IdeaNodeView from this
     * application.
     * 
     * @param modelIdeaNode
     *            the model IdeaNode
     * 
     * @return true, if the IdeaNodeView was successfully created
     */
    private boolean createIdeaNodeView(IdeaNode modelIdeaNode) {

        log.debug("Entering createIdeaNodeView(modelIdeaNode=" + modelIdeaNode + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Create new node with default position (0,0,0) (required for correct
        // repositioning)
        IdeaNodeView ideaNodeView = new IdeaNodeView(
                this.abstractMTapplication, 0, 0, getCalculatedIdeaNodeWidth(),
                getCalculatedIdeaNodeHeight(), getIdeaNodeViewFontBig(),
                getIdeaNodeViewFontSmall(), modelIdeaNode,
                modelIdeaNode.getIdeaText());

        // Set given position and default size
        Vector3D position = new Vector3D(modelIdeaNode.getIdeaPositionX(),
                modelIdeaNode.getIdeaPositionY());

        ideaNodeView.setPositionGlobal(position);

        // Rotate the IdeaNodeView depending on the canvas position
        ideaNodeView.rotateZ(ideaNodeView.getCenterPointGlobal(),
                getRotationForNewComponent(position));

        // Set name model Id
        ideaNodeView.setName((new Long(modelIdeaNode.getIdeaID()).toString()));

        // Add IdeaNodeView to canvas
        this.getCanvas().addChild(ideaNodeView);

        // Add animation listener
        ideaNodeView.addAnimationListenerDefault();

        // Open Keyboard via simulated double tap
        simulateDoubleTap(ideaNodeView);

        log.debug("Leaving createIdeaNodeView()"); //$NON-NLS-1$ 

        return true;

    }

    /**
     * Creates a new IdeaNodeView object at the application center. Called by
     * the update() method of this MindMapScene (= Observer subclass) when
     * changes in the model require the creation of a new IdeaNodeView upon
     * addition of a IdeaNode from an external BT device.
     * 
     * @param modelIdeaNode
     *            the model IdeaNode
     * 
     * @return true, if the IdeaNodeView was successfully created
     */
    private boolean createIdeaNodeViewFromBTDevice(IdeaNode modelIdeaNode) {

        log.debug("Entering createIdeaNodeViewFromBTDevice(modelIdeaNode=" + modelIdeaNode + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Create new node with default position (0,0,0) (required for correct
        // repositioning)
        IdeaNodeView ideaNodeView = new IdeaNodeView(
                this.abstractMTapplication, 0, 0, getCalculatedIdeaNodeWidth(),
                getCalculatedIdeaNodeHeight(), getIdeaNodeViewFontBig(),
                getIdeaNodeViewFontSmall(), modelIdeaNode,
                modelIdeaNode.getIdeaText());

        // Set at the middle of the screen
        Vector3D position = new Vector3D(
                this.abstractMTapplication.getWidth() / 2f,
                this.abstractMTapplication.getHeight() / 2f, 0);

        ideaNodeView.setPositionGlobal(position);

        // Not used right now, as we always position at the center
        // Rotate the IdeaNodeView depending on the canvas position
        // ideaNodeView.rotateZ(ideaNodeView.getCenterPointGlobal(),
        // getRotationForNewComponent(position));

        // Set name model Id
        ideaNodeView.setName((new Long(modelIdeaNode.getIdeaID()).toString()));

        // Add IdeaNodeView to canvas
        this.getCanvas().addChild(ideaNodeView);

        // Add animation listener
        ideaNodeView.addAnimationListenerCreationFromBT();

        log.debug("Leaving createIdeaNodeViewFromBTDevice()"); //$NON-NLS-1$ 

        return true;

    }

    /**
     * Deletes the given IdeaNodeView from its parent.
     * 
     * @param ideaNodeView
     *            the IdeaNodeView that needs to be deleted
     */
    private void deleteIdeaNodeView(IdeaNodeView ideaNodeView) {

        log.debug("Entering deleteIdeaNode(ideaNode=" + ideaNodeView + ")"); //$NON-NLS-1$//$NON-NLS-2$

        // Remove IdeaNodeView from parent
        ideaNodeView.getParent().removeChild(ideaNodeView);

        // Call destroy method to free memory
        ideaNodeView.destroy();

        log.debug("Leaving deleteIdeaNode()"); //$NON-NLS-1$

    }

    /**
     * <p>
     * Creates a directed parent-child relation in the model for the given
     * IdeaNodeViews.
     * </p>
     * 
     * @param parent
     *            the parent IdeaNodeView
     * @param child
     *            the child IdeaNodeView
     */
    protected EAddChildIdeaNodeResultCase createModelRelation(
            IdeaNodeView parent, IdeaNodeView child) {

        log.debug("Entering createModelRelation(parent=" + parent //$NON-NLS-1$
                + ", child=" + child + ")"); //$NON-NLS-1$//$NON-NLS-2$

        // Get the model MindMap
        MindMap loadedMindMap = MindMapScene.this.getModelReference()
                .getLoadedMindMap();

        // Create child relation in model
        EAddChildIdeaNodeResultCase result = parent.getModelIdeaNode()
                .addIdeaChild(child.getModelIdeaNode(), loadedMindMap);

        log.debug("Leaving createModelRelation(): " + result); //$NON-NLS-1$
        return result;

    }

    /**
     * Creates a new directed RelationView for the given parent and child
     * IdeaNodeViews. Called by the update() method of this MindMapScene (=
     * Observer subclass) when changes in the model require the creation of a
     * new RelationView.
     * 
     * @param parent
     *            the parent IdeaNodeView
     * @param child
     *            the child IdeaNodeView
     * @return a new RelationView
     */
    protected RelationView createRelationView(IdeaNodeView parent,
            IdeaNodeView child) {

        log.debug("Entering createRelationView(parent=" + parent + ", child=" + child); //$NON-NLS-1$ //$NON-NLS-2$

        log.trace("Parent center: " + parent.getCenterPointGlobal()); //$NON-NLS-1$
        log.trace("Child center: " + child.getCenterPointGlobal()); //$NON-NLS-1$

        RelationView relation = new RelationView(this.abstractMTapplication,
                parent, child);

        log.debug("Add new line " //$NON-NLS-1$
                + relation + "to IdeaNodeView " //$NON-NLS-1$
                + parent + "\n" //$NON-NLS-1$
                + "start point: " + parent.getCenterPointGlobal().getX() + "," //$NON-NLS-1$  //$NON-NLS-2$
                + parent.getCenterPointGlobal().getY() + ", end point " //$NON-NLS-1$
                + child.getCenterPointGlobal().getX() + "," //$NON-NLS-1$
                + child.getCenterPointGlobal().getY());

        // Add as child to parent node
        parent.addChild(relation);

        log.trace("Relation relative to parent position: " //$NON-NLS-1$
                + relation.getCenterPointRelativeToParent());
        log.trace("Relation global position: " //$NON-NLS-1$
                + relation.getCenterPointGlobal());
        log.trace("Relation local: " + relation.getCenterPointLocal()); //$NON-NLS-1$

        // Re-rotate child relation
        // TODO: check if still necessary??
        ToolsComponent.reRotateShape(parent, relation);

        // Re-position child relation
        // TODO: check if still necessary??
        relation.setPositionGlobal(relation.getCenterPointLocal());

        // Add child IdeaNodeView as new child of relation
        relation.addChild(child);

        log.debug("Leaving createRelationView()"); //$NON-NLS-1$

        return relation;

    }

    /**
     * Deletes the given RelationView from its parent.
     * 
     * @param relationView
     *            the RelationView object to delete
     */
    protected void removeRelationView(RelationView relationView) {

        log.debug("Entering removeRelationView(relationView=" + relationView + ")"); //$NON-NLS-1$//$NON-NLS-2$

        // Free all IdeaNodeView children by re-adding them to the canvas
        // No recursion needed, rest of children mustn't be affected!

        // Get all IdeaNodeView children of the RelationView
        MTComponent[] relationChildren = relationView.getChildren();
        List<IdeaNodeView> listOfIdeaNodes = new ArrayList<IdeaNodeView>();
        List<Vector3D> ideaNodePositions = new ArrayList<Vector3D>();

        // Copy child ideaNodeViews to list
        for (MTComponent comp : relationChildren) {
            if (comp instanceof IdeaNodeView) {
                listOfIdeaNodes.add((IdeaNodeView) comp);
            }
        }

        // Copy all ideaNodeView positions to list
        // (we store them cause they might change after they've been added to
        // canvas again, causing jumping)
        for (IdeaNodeView ideaNodeView : listOfIdeaNodes) {
            ideaNodePositions.add(ideaNodeView
                    .getPosition(TransformSpace.GLOBAL));
        }

        // Add all IdeaNodeViews as children of the canvas
        for (int i = 0; i < listOfIdeaNodes.size()
                && i < ideaNodePositions.size(); i++) {

            IdeaNodeView ideaNodeView = listOfIdeaNodes.get(i);

            // Add to canvas
            this.getCanvas().addChild(ideaNodeView);

            // Re-position
            ideaNodeView.setPositionGlobal(ideaNodePositions.get(i));

        }

        // Remove relationView from parent IdeaNodeView
        relationView.getParent().removeChild(relationView);

        // Call destroy method to free memory
        relationView.destroy();

        log.debug("Leaving removeRelationView()"); //$NON-NLS-1$

    }

    /**
     * <p>
     * Saves the current MindMapScene by calling the model serialization method
     * on the currently loaded model MindMap.
     * </p>
     * 
     * <p>
     * As of now, the position, rotation and the text of all IdeaNodeViews are
     * communicated to the model only right here once before saving the MindMap.
     * Future TODO: better sync by communicating changes when they have happened
     * > DragAction, RotationAction, Keyboard etc. (performance??)
     * </p>
     * 
     * @return true, if object serialization was successful
     */
    private boolean saveCurrentMindMapScene() {

        log.debug("Entering saveCurrentMindMapScene()"); //$NON-NLS-1$

        // Tell all IdeaNodeViews to update their model partners with position,
        // rotation and text info
        // TODO: Move to corresponding listeners? (Rotate, Drag, Text)

        // Get currently loaded mindMap
        // MindMap loadedMindMap = this.getModelReference().getLoadedMindMap();

        // Get all IdeaNodeViews from the component hierarchy
        ArrayList<MTComponent> allIdeaNodeViews = componentHierarchyToList(
                this.getCanvas(), EWalkHierarchyComponents.ONLY_IDEANODEVIEWS);

        // Update all model IdeaNodes corresponding to the IdeaNodeViews
        for (MTComponent comp : allIdeaNodeViews) {
            if (comp instanceof IdeaNodeView) {

                if (!((IdeaNodeView) comp).updateModelIdeaNode()) {

                    // TODO: close application? model and view no longer in sync
                    log.error("Leaving saveCurrentMindMapScene(): false, Model ideaNode for ideaNodeView " //$NON-NLS-1$
                            + comp + " could not be updated!"); //$NON-NLS-1$

                    return false;
                }

            }
        }

        log.debug("Leaving saveCurrentMindMapScene(): "); //$NON-NLS-1$

        // Return result of model save/update method
        return this.getModelReference().getLoadedMindMap().saveMindMap();

    }

    /**
     * Adds a new runnable at the beginning of the next rendering loop that
     * calls the loadMindMapFromDisc() method.
     * 
     * @param mindMapToLoadFile
     *            the File to load
     */
    protected void startLoadMindMapFromDiscProcess(final File mindMapToLoadFile) {

        log.debug("Entering startLoadMindMapFromDiscProcess(mindMapToLoadFile=" //$NON-NLS-1$
                + mindMapToLoadFile + ")"); //$NON-NLS-1$

        // Set loading overlay visible/send to front
        this.loadingOverlay.setVisible(true);
        this.loadingOverlay.sendToFront();

        // Register a new pre draw action to the scene
        this.registerPreDrawAction(new IPreDrawAction() {
            @Override
            public void processAction() {

                // Create a new runnable that calls the loading method
                getMTApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        // Deserialize specified file from
                        boolean result = MindMapScene.this
                                .loadMindMapFromDisc(mindMapToLoadFile);

                        MindMapScene.this.loadingOverlay.setVisible(false);

                        // Show a error message if loading was unsuccessful
                        // (Info message for successful loading will be shown by
                        // update() on Observer notification)
                        if (!result) {

                            // Show message box
                            log.error("Loading MindMap from disc not successful!"); //$NON-NLS-1$

                            String statusMsgNotOk = Messages
                                    .getString("MindMapScene.load.statusMsg.notOk.part1") //$NON-NLS-1$
                                    + mindMapToLoadFile.getName()
                                    + Messages
                                            .getString("MindMapScene.load.statusMsg.notOk.part2"); //$NON-NLS-1$

                            // Show error message mindMapScene
                            MindMapScene.this.showStatusMessageOK(
                                    statusMsgNotOk,
                                    EStatusMessageType.STATUS_MSG_ERROR,
                                    getStatusMessageFontVerySmall(),
                                    getStatusMessageFontMedium(),
                                    STATUS_MSG_OK_MORE_TEXT_MAXNUMOFLINES);

                        }

                        // If for whatever reason the status message is not
                        // open, unlock canvas
                        // Else unlocking is done by status message box
                        if (MindMapScene.this.getStatusMessageBox() == null) {
                            log.info("Message box not opened, unlock canvas"); //$NON-NLS-1$

                            MindMapScene.this.lockCanvas(false);
                        }
                    }
                });
            }

            @Override
            public boolean isLoop() {
                return false;
            }
        });

        log.debug("Leaving startLoadMindMapFromDiscProcess()"); //$NON-NLS-1$

    }

    /**
     * Starts the process to save the current mindMapScene/mindMap.
     * 
     */
    public void startSaveMindMapProcess() {

        log.debug("Entering startSaveMindMapProcess()"); //$NON-NLS-1$

        // Set loading overlay visible/send to front
        this.savingOverlay.setVisible(true);
        this.savingOverlay.sendToFront();

        // Register a new pre draw action to the scene
        this.registerPreDrawAction(new IPreDrawAction() {
            @Override
            public void processAction() {

                // Create a new runnable that calls the loading method
                getMTApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        String mindMapTitle = null;

                        if (saveCurrentMindMapScene()) {

                            MindMapScene.this.savingOverlay.setVisible(false);

                            mindMapTitle = MindMapScene.this
                                    .getModelReference()
                                    .getLoadedMindMapTitle();

                            String statusMsg = Messages
                                    .getString("MindMapScene.saveOrUpdate.statusMsg.ok.part1") + mindMapTitle + Messages.getString("MindMapScene.saveOrUpdate.statusMsg.ok.part2"); //$NON-NLS-1$ //$NON-NLS-2$

                            // Show info message
                            showStatusMessageOK(statusMsg,
                                    EStatusMessageType.STATUS_MSG_INFO,
                                    getStatusMessageFontVerySmall(),
                                    getStatusMessageFontMedium(),
                                    STATUS_MSG_OK_MORE_TEXT_MAXNUMOFLINES);

                            // If for whatever reason the status message is not
                            // open, unlock canvas
                            // Else unlocking is done by status message box
                            if (MindMapScene.this.getStatusMessageBox() == null) {
                                log.info("Message box not opened, unlock canvas"); //$NON-NLS-1$

                                MindMapScene.this.lockCanvas(false);
                            }

                        } else {

                            MindMapScene.this.savingOverlay.setVisible(false);

                            mindMapTitle = MindMapScene.this
                                    .getModelReference()
                                    .getLoadedMindMapTitle();

                            String statusMsgNotOk = (Messages
                                    .getString("MindMapScene.saveOrUpdate.statusMsg.notOk.part1") + mindMapTitle + Messages.getString("MindMapScene.saveOrUpdate.statusMsg.notOk.part2")); //$NON-NLS-1$ //$NON-NLS-2$

                            // Show error message
                            showStatusMessageOK(statusMsgNotOk,
                                    EStatusMessageType.STATUS_MSG_ERROR,
                                    getStatusMessageFontVerySmall(),
                                    getStatusMessageFontMedium(),
                                    STATUS_MSG_OK_MORE_TEXT_MAXNUMOFLINES);

                            // If for whatever reason the status message is not
                            // open, unlock canvas
                            // Else unlocking is done by status message box
                            if (MindMapScene.this.getStatusMessageBox() == null) {
                                log.debug("Message box not opened, unlock canvas"); //$NON-NLS-1$

                                MindMapScene.this.lockCanvas(false);
                            }

                        }
                    }
                });
            }

            @Override
            public boolean isLoop() {
                return false;
            }
        });

        log.debug("Leaving startSaveMindMapProcess()"); //$NON-NLS-1$

    }

    /**
     * Loads the selected MindMap from the disc via object deserialization.
     * 
     * @param mindMapToLoadFile
     *            the file that needs to be deserialized
     * @return true if loading is successful
     */
    private boolean loadMindMapFromDisc(File mindMapToLoadFile) {

        log.debug("Entering loadMindMapFromDisc(mindMapToLoadFile" + //$NON-NLS-1$
                mindMapToLoadFile + ")"); //$NON-NLS-1$

        // Load new MindMap from disc via model
        if (mindMapToLoadFile != null) {

            // Update file list
            this.getModelReference().getMindMapCollection()
                    .updateMindMapFilesList();

            // Load the specified MindMap file
            // Changes in the loaded MindMap will be picked up by this Observer
            // class
            if (this.getModelReference().getMindMapCollection()
                    .loadMindMap(mindMapToLoadFile)) {

                log.debug("Leaving loadMindMapSceneFromDisc(): true"); //$NON-NLS-1$

                return true;
            }
            log.error("Leaving loadMindMapSceneFromDisc(): false, Loading mindMapScene from disc not successful!"); //$NON-NLS-1$
            return false;
        }
        log.error("Leaving loadMindMapSceneFromDisc(): false, invalid null input!"); //$NON-NLS-1$
        return false;

    }

    /**
     * Loads a new "mindMapScene" if a new MindMap has been loaded in the model.
     * 
     * @param loadedMap
     *            the loaded model mindMap
     * @return true, if loading was successful
     */
    private boolean loadNewMindMapScene(MindMap loadedMap) {

        log.debug("Entering loadNewMindMapScene(loadedMap=" + loadedMap + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Get the loaded MindMap instance
        if (loadedMap != null) {

            // Delete all GUI components except canvas and menus
            MTComponent[] children = this.getCanvas().getChildren();
            for (MTComponent child : children) {
                if (child instanceof IdeaNodeView
                        || child instanceof RelationView
                        || child instanceof AbstractOverlay) {

                    // Remove from canvas
                    this.getCanvas().removeChild(child);

                    // Destroy child
                    child.destroy();
                }
            }

            // Re-set Scene name
            this.setName(loadedMap.getMindMapTitle());

            // Build GUI from loaded MindMap objects
            // Create IdeaNodeViews
            if (createIdeaNodeViewsFromFile(loadedMap)) {
                // Create Relations
                if (!createRelationsFromFile(loadedMap)) {
                    log.error("Leaving loadNewMindMapScene(): false, could not create RelationViews from file!"); //$NON-NLS-1$
                    // TODO: close application, no longer in sync?
                    return false;
                }

            } else {

                log.error("Leaving loadNewMindMapScene(): false, could not create IdeaNodeViews from file!"); //$NON-NLS-1$
                // TODO: close application, no longer in sync?
                return false;
            }

            log.debug("Leaving loadNewMindMapScene(): true"); //$NON-NLS-1$
            return true;
        }

        log.error("Leaving loadNewMindMapScene(): false, loadedMindMap null!"); //$NON-NLS-1$
        return false;

    }

    /**
     * Creates new IdeaNodeViews from a newly loaded model MindMap.
     * 
     * @param loadedMap
     *            the newly loaded model MindMap instance
     * @return true if the creation was successful
     */
    private boolean createIdeaNodeViewsFromFile(MindMap loadedMap) {

        log.debug("Entering createIdeaNodeViewsFromFile(" + //$NON-NLS-1$
                loadedMap + ")"); //$NON-NLS-1$

        if (loadedMap != null) {

            // Create a new IdeaNodeView for every IdeaNode
            ArrayList<IdeaNode> ideaNodeList = loadedMap
                    .getMindMapIdeaNodeList();

            if (ideaNodeList != null) {

                float x;
                float y;
                float rotation;
                String text;
                long id;
                int i = 0;

                // Create a new IdeaNodeView for every active IdeaNode
                for (IdeaNode ideaNode : ideaNodeList) {

                    // TODO: trash bin state for deleted IdeaNodes?
                    if (ideaNode.getIdeaState() != EIdeaState.DELETED) {

                        // Get data for ideaNode
                        x = ideaNode.getIdeaPositionX();
                        y = ideaNode.getIdeaPositionY();
                        rotation = ideaNode.getIdeaRotationInDegrees();
                        text = ideaNode.getIdeaText();
                        id = ideaNode.getIdeaID();

                        // Create new IdeaNodeView with default position (0,0,0)
                        // (required for correct repositioning)
                        IdeaNodeView ideaNodeView = new IdeaNodeView(
                                this.abstractMTapplication, 0, 0,
                                getCalculatedIdeaNodeWidth(),
                                getCalculatedIdeaNodeHeight(),
                                getIdeaNodeViewFontBig(),
                                getIdeaNodeViewFontSmall(), ideaNode, text);

                        // Set given position and default size
                        ideaNodeView.setPositionGlobal(new Vector3D(x, y));

                        // Rotate the IdeaNodeView for the stored rotation
                        ideaNodeView.rotateZ(
                                ideaNodeView.getCenterPointGlobal(), rotation);

                        // Set ideaNode id as name
                        // TODO: better name?
                        ideaNodeView.setName(new Long(id).toString());

                        // Add ideaNodeView to canvas
                        this.getCanvas().addChild(ideaNodeView);

                        i++;
                    } // for state DELETED do nothing for now
                }

                log.info("Created " + i + " IdeaNodeViews from file!"); //$NON-NLS-1$//$NON-NLS-2$

                log.debug("Leaving createIdeaNodeViewsFromFile(): true"); //$NON-NLS-1$
                return true;

            }
            log.error("Leaving createIdeaNodeViewsFromFile(): false, ideaNodeList is null!"); //$NON-NLS-1$
            return false;
        }
        log.error("Leaving createIdeaNodeViewsFromFile(): false, invalid null input!"); //$NON-NLS-1$
        return false;

    }

    /**
     * Creates new RelationViews from a newly loaded model MindMap.
     * 
     * @param loadedMap
     *            the newly loaded modelMindMap
     * @return true if the creation was successful
     */
    private boolean createRelationsFromFile(MindMap loadedMap) {

        log.debug("Entering createRelationsFromFile(loadedMap=" + //$NON-NLS-1$
                loadedMap + ")"); //$NON-NLS-1$

        if (loadedMap != null) {

            // Create a new RelationView for every parent-child IdeaNode
            // relation in the model
            ArrayList<IdeaNode> ideaNodeList = loadedMap
                    .getMindMapIdeaNodeList();

            ArrayList<MTComponent> ideaNodeViewList = getAllIdeaNodeViews();

            if (ideaNodeList != null && ideaNodeViewList != null) {

                log.trace("These are all model nodes: "); //$NON-NLS-1$
                int i = 0;
                for (IdeaNode node : ideaNodeList) {
                    i++;
                    log.trace("Node Nr. " + i + " is " + node + ", Id: " //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
                            + node.getIdeaID() + ", children: " //$NON-NLS-1$
                            + node.getChildren().size());
                }

                log.trace("These are all ideaNodeView nodes: "); //$NON-NLS-1$
                int j = 0;
                for (MTComponent ideaNodeView : ideaNodeViewList) {
                    if (ideaNodeView instanceof IdeaNodeView) {
                        j++;
                        log.trace("IdeaNodeView Nr. " //$NON-NLS-1$
                                + j
                                + " is " //$NON-NLS-1$
                                + ((IdeaNodeView) ideaNodeView).getName()
                                + ", model ideaNode: " //$NON-NLS-1$
                                + ((IdeaNodeView) ideaNodeView)
                                        .getModelIdeaNode()
                                + ", modelIdeaNode id" //$NON-NLS-1$
                                + ((IdeaNodeView) ideaNodeView)
                                        .getModelIdeaNode().getIdeaID());
                    }
                }

                int relationCount = 0;

                // Create RelationViews for every active IdeaNode
                for (IdeaNode ideaNode : ideaNodeList) {

                    // TODO: trash bin state for deleted IdeaNodes?
                    if (ideaNode.getIdeaState() != EIdeaState.DELETED) {
                        if (ideaNode.getChildren().size() > 0) {

                            ArrayList<Node<NodeData>> children = ideaNode
                                    .getChildren();

                            // Create RelationViews to all children
                            for (Node<NodeData> child : children) {

                                if (child instanceof IdeaNode) {

                                    // Find the corresponding IdeaNodeViews
                                    IdeaNodeView parentView = findIdeaNodeViewByIdeaNode(
                                            ideaNode, ideaNodeViewList);
                                    IdeaNodeView childView = findIdeaNodeViewByIdeaNode(
                                            (IdeaNode) child, ideaNodeViewList);

                                    if (parentView != null && childView != null) {

                                        // Create a new RelationView
                                        RelationView relationView = createRelationView(
                                                parentView, childView);

                                        if (relationView != null) {
                                            relationCount++;
                                            log.debug("Created new relation from file for " + parentView + " and " + childView); //$NON-NLS-1$ //$NON-NLS-2$

                                        } else {
                                            // TODO: Close application, no
                                            // longer in sync
                                            log.error("Leaving createRelationsFromFile(): false, Error while creating a relation from file for IdeaNode " //$NON-NLS-1$ 
                                                    + child
                                                    + ", relation view null!"); //$NON-NLS-1$
                                            return false;
                                        }

                                    } else {
                                        // TODO: Close application, no longer in
                                        // sync
                                        log.error("Leaving createRelationsFromFile(): false, Error while creating a relation from file for IdeaNode " //$NON-NLS-1$ 
                                                + child
                                                + ", IdeaNodeView(s) null!"); //$NON-NLS-1$
                                        return false;
                                    }
                                } else {

                                    log.error("Leaving createRelationsFromFile(): false, Error while creating a relation from file for IdeaNode " //$NON-NLS-1$ 
                                            + child
                                            + ", not an instance of IdeaNode!"); //$NON-NLS-1$
                                    // TODO: Close application, no longer in
                                    // sync
                                    return false;
                                }
                            }

                        } // no children, no relation creation!
                    } // for state DELETED, do nothing (should not occur!)
                }
                log.info("Created " + relationCount + " RelationViews from file!"); //$NON-NLS-1$ //$NON-NLS-2$
                log.debug("Leaving createRelationsFromFile(): true"); //$NON-NLS-1$
                return true;
            }
            log.error("Leaving createRelationsFromFile(): false, ideaNodeList is null!"); //$NON-NLS-1$
            return false;
        }
        log.error("Leaving createRelationsFromFile(): false, invalid null input!"); //$NON-NLS-1$
        return false;
    }

    /**
     * Shows a status message with the given statusMsg String for the given
     * statusType.
     * 
     * @param statusMsg
     *            the text of the status message
     * @param statusType
     *            the status message type
     * @param fontSmall
     *            the smaller status message font
     * @param fontBig
     *            the bigger status message font
     * @param numberOfLines
     *            the maximum number of lines
     * @return true, if all params are valid and the status msg could be added
     *         successfully
     * @see EStatusMessageType
     */
    protected boolean showStatusMessageOK(String statusMsg,
            EStatusMessageType statusType, IFont fontSmall, IFont fontBig,
            int numberOfLines) {

        log.debug("Entering showStatusMessage(statusMsg=" + statusMsg//$NON-NLS-1$
                + ", statusType=" + statusType + "fontSmall=" + fontSmall //$NON-NLS-1$ //$NON-NLS-2$
                + "fontBig=" + fontBig + "numberOfLines=" + numberOfLines + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        if (statusMsg != null && statusType != null && fontSmall != null
                && fontBig != null && numberOfLines > 0) {

            // New status message
            StatusMessageDialogOK statusMsgView = new StatusMessageDialogOK(
                    this.abstractMTapplication, this, 0, 0,
                    this.getCalculatedStatusMsgWidth(),
                    this.getCalculatedStatusMsgHeight(), statusType, statusMsg,
                    fontSmall, fontBig, numberOfLines);

            // Show status message by adding to canvas
            this.getCanvas().addChild(statusMsgView);

            // Position at the center of the screen
            statusMsgView.setPositionGlobal(new Vector3D(
                    this.abstractMTapplication.getWidth() / 2f,
                    this.abstractMTapplication.getHeight() / 2f, 0));

            setStatusMessageBox(statusMsgView);

            log.debug("Leaving showStatusMessage(): true"); //$NON-NLS-1$
            return true;
        }
        log.error("Leaving showStatusMessage(): false, invalid null input!"); //$NON-NLS-1$
        return false;

    }

    /**
     * Shows a dialog for asking the user if they really want to close the
     * application
     * 
     * @param statusMsg
     *            the text of the status message
     * @param statusType
     *            the status message type
     * @return true, if all params are valid and the status msg could be added
     *         successfully
     * @see EStatusMessageType
     */
    public boolean showStatusMessageCloseApplication(String statusMsg,
            EStatusMessageType statusType) {

        log.debug("Entering showStatusMessageCloseApplication(statusMsg=" + statusMsg//$NON-NLS-1$
                + ", statusType=" + statusType + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (statusMsg != null && statusType != null) {

            // New status message
            StatusMessageDialogCloseApp statusMsgView = new StatusMessageDialogCloseApp(
                    this.abstractMTapplication,
                    this,
                    0,
                    0,
                    this.getCalculatedStatusMsgWidth(),
                    this.getCalculatedStatusMsgHeight(),
                    statusType,
                    statusMsg,
                    this.statusMessageFontSmall,
                    this.statusMessageFontBig,
                    StatusMessageDialogCloseApp.STATUS_MSG_CLOSE_APP_MAXNUMOFLINES);

            // Show status message by adding to canvas
            this.getCanvas().addChild(statusMsgView);

            // Position at the center of the screen
            statusMsgView.setPositionGlobal(new Vector3D(
                    this.abstractMTapplication.getWidth() / 2f,
                    this.abstractMTapplication.getHeight() / 2f, 0));

            setStatusMessageBox(statusMsgView);

            log.debug("Leaving showStatusMessageCloseApplication(): true"); //$NON-NLS-1$
            return true;
        }
        log.error("Leaving showStatusMessageCloseApplication(): false, invalid null input!"); //$NON-NLS-1$
        return false;

    }

    /**
     * Shows a status message with the given statusMsg String for the given
     * statusType.
     * 
     * @param statusMsg
     *            the text of the status message
     * @param statusType
     *            the status message type
     * @return true, if all params are valid and the status msg could be added
     *         successfully
     * @see EStatusMessageType
     */
    public boolean showStatusMessageMinimizeApplication(String statusMsg,
            EStatusMessageType statusType) {

        log.debug("Entering showStatusMessageMinimizeApplication(statusMsg=" + statusMsg//$NON-NLS-1$
                + ", statusType=" + statusType + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (statusMsg != null && statusType != null) {

            // New status message
            StatusMessageDialogMinimizeApp statusMsgView = new StatusMessageDialogMinimizeApp(
                    this.abstractMTapplication,
                    this,
                    0,
                    0,
                    this.getCalculatedStatusMsgWidth(),
                    this.getCalculatedStatusMsgHeight(),
                    statusType,
                    statusMsg,
                    this.statusMessageFontSmall,
                    this.statusMessageFontBig,
                    StatusMessageDialogMinimizeApp.STATUS_MSG_MIN_APP_MAXNUMOFLINES);

            // Show status message by adding to canvas
            this.getCanvas().addChild(statusMsgView);

            // Position at the center of the screen
            statusMsgView.setPositionGlobal(new Vector3D(
                    this.abstractMTapplication.getWidth() / 2f,
                    this.abstractMTapplication.getHeight() / 2f, 0));

            setStatusMessageBox(statusMsgView);

            log.debug("Leaving showStatusMessageMinimizeApplication(): true"); //$NON-NLS-1$
            return true;
        }
        log.error("Leaving showStatusMessageMinimizeApplication(): false, invalid null input!"); //$NON-NLS-1$
        return false;

    }

    /**
     * <p>
     * Shows the overlay for loading a new MindMap if it is not already opened.
     * Position and rotation are dependent on the menu instance the overlay was
     * opened from.
     * </p>
     * 
     * @param callingMenu
     *            the instance of the menu the loading MindMap overlay was
     *            selected from
     * 
     * @return true if showing the OverlayListLoadMindMap was successful
     */
    protected boolean showOverlayListLoadMindMap(MainMenu callingMenu) {

        log.debug("Entering showOverlayListLoadMindMap(callingMenu=" //$NON-NLS-1$
                + callingMenu + ")"); //$NON-NLS-1$

        if (callingMenu != null) {

            if (this.getLoadMindMapOverlay() == null) {

                // Get overlay rotation from menu position
                Vector3D menuPosition = callingMenu.getCenterPointGlobal();
                float rotation = getRotationForNewComponent(menuPosition);

                // Calculate overlay position
                // TODO: temp position!
                Vector3D position = callingMenu.getCenterPointGlobal();

                // Create overlay
                OverlayListLoadMindMap loadMindMapOverlay = new OverlayListLoadMindMap(
                        this.abstractMTapplication, this, 0, 0,
                        this.getCalculatedOverlayDefaultListWidth(),
                        this.getCalculatedOverlayDefaultListHeight(),
                        this.getOverlayHeadlineFont(),
                        this.getOverlayListFontBig(),
                        this.getOverlayListFontSmall());

                // Re-position
                loadMindMapOverlay.setPositionGlobal(position);

                // Rotate
                loadMindMapOverlay.rotateZGlobal(
                        loadMindMapOverlay.getCenterPointGlobal(), rotation);

                this.loadMindMapOverlay = loadMindMapOverlay;

                // Add to canvas
                this.getCanvas().addChild(loadMindMapOverlay);

                log.debug("Leaving showOverlayListLoadMindMap(): true"); //$NON-NLS-1$
                return true;

            }
            log.info("Leaving showOverlayListLoadMindMap(): false, this (load MindMap) overlay is already active"); //$NON-NLS-1$
            return false;

        }
        log.error("Leaving showOverlayListLoadMindMap(): false, invalid null input!"); //$NON-NLS-1$
        return false;

    }

    /**
     * <p>
     * Shows the overlay of the bluetooth connections. Position and rotation are
     * dependent on the menu instance the overlay was opened from.
     * </p>
     * 
     * @param callingMenu
     *            the instance of the menu the loading MindMap overlay was
     *            selected from
     * 
     * @return true if showing the OverlayListBluetooth was successful
     */
    protected boolean showOverlayListBluetooth(MainMenu callingMenu) {

        log.debug("Entering showOverlayListBluetooth(callingMenu=" //$NON-NLS-1$
                + callingMenu + ")"); //$NON-NLS-1$

        if (callingMenu != null) {

            if (this.getBluetoothOverlay() == null) {

                // Get overlay rotation from menu position
                Vector3D menuPosition = callingMenu.getCenterPointGlobal();
                float rotation = getRotationForNewComponent(menuPosition);

                // Calculate overlay position
                // TODO: temp position!
                Vector3D position = callingMenu.getCenterPointGlobal();

                // Create overlay
                OverlayListBluetooth bluetoothOverlay = new OverlayListBluetooth(
                        this.abstractMTapplication, this, 0, 0,
                        this.getCalculatedOverlayDefaultListWidth(),
                        this.getCalculatedOverlayDefaultListHeight(),
                        this.getOverlayHeadlineFont(),
                        this.getOverlayListFontBig(),
                        this.getOverlayListFontSmall(),
                        this.getOverlayBtListQrCodeDescriptionFont());

                // Re-position
                bluetoothOverlay.setPositionGlobal(position);

                // Rotate
                bluetoothOverlay.rotateZGlobal(
                        bluetoothOverlay.getCenterPointGlobal(), rotation);

                this.bluetoothOverlay = bluetoothOverlay;

                // Add to canvas
                this.getCanvas().addChild(bluetoothOverlay);

                log.debug("Leaving showOverlayListBluetooth(): true"); //$NON-NLS-1$
                return true;

            }
            log.info("Leaving showOverlayListBluetooth(): false, this overlay (bluetooth)" //$NON-NLS-1$
                    + " already active"); //$NON-NLS-1$
            return false;

        }
        log.error("Leaving showOverlayListBluetooth(): false, invalid null input!"); //$NON-NLS-1$
        return false;

    }

    /**
     * <p>
     * Shows a overlay with a short introduction to the gestures and function of
     * the application. There may be multiple help overlays on the canvas with a
     * maximum of OVERLAY_HELP_INSTANCES_MAX. Position and rotation are
     * dependent on the menu instance the overlay was opened from.
     * </p>
     * 
     * @param callingMenu
     *            the instance of the menu the loading MindMap overlay was
     *            selected from
     * 
     * @return true if showing the OverlayListHelp was successful
     */
    protected boolean showOverlayListHelp(MainMenu callingMenu) {

        log.debug("Entering showOverlayListHelp(callingMenu=" //$NON-NLS-1$
                + callingMenu + ")"); //$NON-NLS-1$

        if (callingMenu != null) {

            // If we haven't reached the maximum number of help overlays
            if (this.getListOfHelpOverlays().size() < OVERLAY_HELP_INSTANCES_MAX) {

                // Get overlay rotation from menu position
                Vector3D menuPosition = callingMenu.getCenterPointGlobal();
                float rotation = getRotationForNewComponent(menuPosition);

                // Calculate overlay position
                // TODO: temp position!
                Vector3D position = callingMenu.getCenterPointGlobal();

                // Create overlay
                OverlayListHelp helpOverlay = new OverlayListHelp(
                        this.abstractMTapplication, this, 0, 0,
                        this.getCalculatedOverlayWideListWidth(),
                        this.getCalculatedOverlayWideListHeight(),
                        this.getOverlayListWideHeadlineFont(),
                        this.getOverlayListWideTextFontDefault(),
                        this.getOverlayListWideFontSmaller());

                // Re-position
                helpOverlay.setPositionGlobal(position);

                // Rotate
                helpOverlay.rotateZGlobal(helpOverlay.getCenterPointGlobal(),
                        rotation);

                // Add to member list
                this.listOfHelpOverlays.add(helpOverlay);

                // Add to canvas
                this.getCanvas().addChild(helpOverlay);

                log.debug("Leaving showOverlayListHelp(): true"); //$NON-NLS-1$
                return true;

            }
            log.info("Leaving showOverlayListHelp(): false, maximum number of help overlays reached"); //$NON-NLS-1$
            return false;

        }
        log.error("Leaving showOverlayListHelp(): false, invalid null input!"); //$NON-NLS-1$
        return false;

    }

    /**
     * <p>
     * Shows the overlay form for saving the MindMap.
     * </p>
     * 
     * @param callingMenu
     *            the instance of the menu the loading MindMap overlay was
     *            selected from
     * 
     * @return true if showing the OverlayFormSave was successful
     */
    protected boolean showOverlayFormSave(MainMenu callingMenu) {

        log.debug("Entering showOverlayFormSave(callingMenu=" //$NON-NLS-1$
                + callingMenu + ")"); //$NON-NLS-1$

        if (callingMenu != null) {

            if (this.getSaveMindMapOverlayForm() == null) {

                // Get overlay rotation from menu position
                Vector3D menuPosition = callingMenu.getCenterPointGlobal();
                float rotation = getRotationForNewComponent(menuPosition);

                // Calculate overlay position
                // TODO: temp position!
                Vector3D position = callingMenu.getCenterPointGlobal();

                // Create overlay
                OverlayFormSave saveMindMapOverlay = new OverlayFormSave(
                        this.abstractMTapplication, this, 0, 0,
                        this.getCalculatedOverlayFormWidth(),
                        this.getCalculatedOverlayFormHeight(),
                        this.getOverlayFormHeadlineFont(),
                        this.getOverlayFormTextFont(),
                        this.getOverlayFormStMsgFont());

                // Re-position
                saveMindMapOverlay.setPositionGlobal(position);

                // Rotate
                saveMindMapOverlay.rotateZGlobal(
                        saveMindMapOverlay.getCenterPointGlobal(), rotation);

                this.saveMindMapOverlayForm = saveMindMapOverlay;

                // Add to canvas
                this.getCanvas().addChild(saveMindMapOverlay);

                log.debug("Leaving showOverlayFormSave(): true"); //$NON-NLS-1$
                return true;

            }
            log.info("Leaving showOverlayFormSave(): false, this overlay (save) " //$NON-NLS-1$
                    + "is already active"); //$NON-NLS-1$
            return false;

        }
        log.error("Leaving showOverlayFormSave(): false, invalid null input!"); //$NON-NLS-1$
        return false;

    }

    /**
     * Returns a list of all IdeaNodeViews in the hierarchy. Searches the whole
     * component hierarchy by creating a list of all components by traversing
     * the component hierarchy recursively.
     * 
     * @return ArrayList with MTComponents/IdeaNodeViews
     */
    protected ArrayList<MTComponent> getAllIdeaNodeViews() {

        log.debug("Entering getAllIdeaNodeViews()"); //$NON-NLS-1$

        // Get a list of all IdeaNodeViews in the component hierarchy
        ArrayList<MTComponent> allIdeaNodeViews = componentHierarchyToList(
                this.getCanvas(), EWalkHierarchyComponents.ONLY_IDEANODEVIEWS);

        log.debug("Leaving getAllIdeaNodeViews()"); //$NON-NLS-1$

        return allIdeaNodeViews;
    }

    /**
     * Finds an IdeaNodeView by the given ideaNode in a list of MTComponents,
     * e.g. provided by getAllIdeaNodeViews()
     * 
     * @param ideaNode
     *            the ideaNode by which a IdeaNodeView has to be found
     * 
     * @param ideaNodeViewList
     *            a list of MTComponents
     * 
     * @return the found IdeaNodeView or null if it hasn't been found
     */
    protected IdeaNodeView findIdeaNodeViewByIdeaNode(IdeaNode ideaNode,
            ArrayList<MTComponent> ideaNodeViewList) {

        if (ideaNode != null && ideaNodeViewList != null) {

            log.debug("Entering findIdeaNodeViewByIdeaNode(ideaNode=" + //$NON-NLS-1$
                    ideaNode + ", ideaNodeViewList=" + ideaNodeViewList + ")"); //$NON-NLS-1$ //$NON-NLS-2$

            // Return the IdeaNodeView that belongs to the IdeaNode
            for (MTComponent comp : ideaNodeViewList) {
                if (comp instanceof IdeaNodeView) {
                    log.trace("Checking at IdeaNodeView " + comp //$NON-NLS-1$
                            + ((IdeaNodeView) comp).getModelIdeaNode());
                    if (((IdeaNodeView) comp).getModelIdeaNode().equals(
                            ideaNode)) {
                        return (IdeaNodeView) comp;
                    }
                }
            }
            log.debug("Leaving findIdeaNodeViewByIdeaNode(): null, IdeaNodeView could not be found!"); //$NON-NLS-1$
            return null;
        }
        log.error("Leaving findIdeaNodeViewByIdeaNode(): null, invalid null input!"); //$NON-NLS-1$

        return null;
    }

    /**
     * <p>
     * Returns the component hierarchy as a List of MTComponent objects. The
     * elements of the List are generated from a pre-order traversal of the
     * hierarchy dependent on the given EWalkHierarchyComponents type.
     * <p>
     * 
     * @param start
     *            the MTComponent at which to start the walk
     * @param componentsToWalk
     *            Enumeration describing a restriction which MTComponents to
     *            walk
     * @return a ArrayList<MTComponent> with all specified components of the
     *         component hierarchy.
     */
    private ArrayList<MTComponent> componentHierarchyToList(MTComponent start,
            EWalkHierarchyComponents componentsToWalk) {

        if (start != null && componentsToWalk != null) {

            log.debug("Entering componentHierarchyToList(start" + start //$NON-NLS-1$
                    + ", componentsToWalk=" + componentsToWalk + ")"); //$NON-NLS-1$ //$NON-NLS-2$

            ArrayList<MTComponent> list = new ArrayList<MTComponent>();
            switch (componentsToWalk) {
                case WALK_ALL:
                    walkComponentHierarchyAll(start, list);
                    break;
                case ONLY_IDEANODEVIEWS:
                    walkComponentHierarchyIdeaNodeViews(start, list);
                    break;
                case ONLY_RELATIONVIEWS:
                    walkComponentHierarchyRelationViews(start, list);
                    break;
                default:
                    break;
            }

            log.debug("Leaving componentHierarchyToList(): "); //$NON-NLS-1$
            return list;
        }

        log.error("Leaving componentHierarchyToList(): null, invalid null input!"); //$NON-NLS-1$
        return null;

    }

    /**
     * <p>
     * Walks the whole component hierarchy in pre-order style. This is a
     * recursive method, and is called from the componentHierarchyToList()
     * method with the root element as the first argument. It appends all
     * components to the second argument, which is passed by reference as it
     * recurses down the tree.
     * </p>
     * <p>
     * Adapted from <a href="http
     * ://sujitpal.blogspot.com/2006/05/java-data-structure-generic
     * -tree.html">http
     * ://sujitpal.blogspot.com/2006/05/java-data-structure-generic
     * -tree.html</a>
     * 
     * </p>
     * 
     * @param element
     *            the starting element.
     * @param list
     *            the output of the walk.
     * 
     */
    private void walkComponentHierarchyAll(MTComponent element,
            List<MTComponent> list) {

        log.trace("Entering walkComponentHierarchyAll(element=" + element //$NON-NLS-1$
                + ", list= )"); //$NON-NLS-1$ 

        // Add current element to list
        list.add(element);

        // Walk every child of the current element recursively
        for (MTComponent data : element.getChildren()) {
            walkComponentHierarchyAll(data, list);
        }
        log.trace("Leaving walkComponentHierarchyAll()"); //$NON-NLS-1$
    }

    /**
     * <p>
     * Walks the whole component hierarchy in pre-order style. This is a
     * recursive method, and is called from the componentHierarchyToList()
     * method with the root element as the first argument. It appends only
     * IdeaNodeViews to the second argument, which is passed by reference as it
     * recurses down the tree.
     * </p>
     * <p>
     * Adapted from <a href="http
     * ://sujitpal.blogspot.com/2006/05/java-data-structure-generic
     * -tree.html">http
     * ://sujitpal.blogspot.com/2006/05/java-data-structure-generic
     * -tree.html</a>
     * 
     * </p>
     * 
     * @param element
     *            the starting element.
     * @param list
     *            the output of the walk.
     * 
     */
    private void walkComponentHierarchyIdeaNodeViews(MTComponent element,
            List<MTComponent> list) {

        log.trace("Entering walkComponentHierarchyIdeaNodeViews(element=" + element //$NON-NLS-1$ 
                + ", list= )"); //$NON-NLS-1$ 

        // Add current element to list if it is an IdeaNodeView
        if (element instanceof IdeaNodeView) {
            if (list.add(element)) {
                //
            } else {
                log.error("Element IdeaNodeView" + element + " could not be added!"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        // Walk every child of the current element recursively
        for (MTComponent data : element.getChildren()) {
            walkComponentHierarchyIdeaNodeViews(data, list);
        }
        log.trace("Leaving walkComponentHierarchyIdeaNodeViews()"); //$NON-NLS-1$
    }

    /**
     * <p>
     * Walks the whole component hierarchy in pre-order style. This is a
     * recursive method, and is called from the componentHierarchyToList()
     * method with the root element as the first argument. It appends only
     * RelationViews to the second argument, which is passed by reference as it
     * recurses down the tree.
     * </p>
     * <p>
     * Adapted from <a href="http
     * ://sujitpal.blogspot.com/2006/05/java-data-structure-generic
     * -tree.html">http
     * ://sujitpal.blogspot.com/2006/05/java-data-structure-generic
     * -tree.html</a>
     * 
     * </p>
     * 
     * @param element
     *            the starting element.
     * @param list
     *            the output of the walk.
     * 
     */
    private void walkComponentHierarchyRelationViews(MTComponent element,
            List<MTComponent> list) {

        log.trace("Entering walkComponentHierarchyRelationViews(element=" + element + ", list= )"); //$NON-NLS-1$ //$NON-NLS-2$

        // Add current element to list if it is a RelationView
        if (element instanceof RelationView) {
            list.add(element);
        }

        // Walk every child of the current element recursively
        for (MTComponent data : element.getChildren()) {
            walkComponentHierarchyRelationViews(data, list);
        }
        log.trace("Leaving walkComponentHierarchyRelationViews()"); //$NON-NLS-1$
    }

    /* *********Utility methods********* */

    /**
     * Returns the required change of rotation in degrees for a newly created
     * canvas component (IdeaNodeView, Overlay, etc.) on the canvas, depending
     * on the screen area.
     * 
     * @param position
     *            the position of the component
     * @return the rotation in degrees
     */
    public float getRotationForNewComponent(Vector3D position) {

        if (position != null) {

            log.debug("Entering getRotationForNewComponent(position=" + position); //$NON-NLS-1$

            // Check canvas area and change rotation accordingly
            ECanvasArea canvasArea = checkCanvasArea(position);

            switch (canvasArea) {
                case CANVAS_NORTH:
                    // Rotation should be 180 Degrees
                    log.debug("Leaving getRotationForNewComponent(): 180.0f"); //$NON-NLS-1$  
                    return 180f;

                case CANVAS_SOUTH:
                    // Rotation should be 0 Degrees, don't rotate
                    log.debug("Leaving getRotationForNewComponent(): 0.0f"); //$NON-NLS-1$  
                    return 0;

                case CANVAS_WEST:
                    // Rotation should be 90 Degrees
                    log.debug("Leaving getRotationForNewComponent(): 90.0f"); //$NON-NLS-1$  
                    return 90f;

                case CANVAS_EAST:
                    // Rotation should be 270 Degrees
                    log.debug("Leaving getRotationForNewComponent(): 270.0f"); //$NON-NLS-1$  
                    return 270f;

                default:
                    log.error("Leaving getRotationForNewComponent(): 360f, Position " //$NON-NLS-1$
                            + position + "is in invalid canvas area!"); //$NON-NLS-1$
                    return 360f;

            }
        }
        log.error("Leaving getRotationForNewComponent(): 0.0f, invalid null input!"); //$NON-NLS-1$
        return 360f;

    }

    /**
     * <p>
     * Returns the ECanvasArea for the specified position.
     * </p>
     * 
     * @param position
     *            the position
     * @return the ECanvasArea
     */
    private ECanvasArea checkCanvasArea(Vector3D position) {

        if (position != null) {

            log.debug("Entering checkCanvasArea(position=" + position + ")"); //$NON-NLS-1$ //$NON-NLS-2$

            // Check which canvas area the position belongs to
            if (ToolsGeometry.isPoint2DInPolygon(position.getX(),
                    position.getY(), this.canvasAreaNorth.getVerticesGlobal())) {
                log.trace("Position " + position + " is located the northern area of the canvas."); //$NON-NLS-1$//$NON-NLS-2$

                log.debug("Leaving checkCanvasArea(): " + ECanvasArea.CANVAS_NORTH); //$NON-NLS-1$
                return ECanvasArea.CANVAS_NORTH;
            }

            if (ToolsGeometry.isPoint2DInPolygon(position.getX(),
                    position.getY(), this.canvasAreaSouth.getVerticesGlobal())) {
                log.trace("Position " + position + " is located the southern area of the canvas."); //$NON-NLS-1$//$NON-NLS-2$

                log.debug("Leaving checkCanvasArea(): " + ECanvasArea.CANVAS_SOUTH); //$NON-NLS-1$
                return ECanvasArea.CANVAS_SOUTH;
            }

            if (ToolsGeometry.isPoint2DInPolygon(position.getX(),
                    position.getY(), this.canvasAreaEast.getVerticesGlobal())) {
                log.trace("Position " + position + " is located the eastern area of the canvas."); //$NON-NLS-1$//$NON-NLS-2$

                log.debug("Leaving checkCanvasArea(): " + ECanvasArea.CANVAS_EAST); //$NON-NLS-1$
                return ECanvasArea.CANVAS_EAST;
            }

            if (ToolsGeometry.isPoint2DInPolygon(position.getX(),
                    position.getY(), this.canvasAreaWest.getVerticesGlobal())) {
                log.trace("Position " + position + " is located the western area of the canvas."); //$NON-NLS-1$//$NON-NLS-2$

                log.debug("Leaving checkCanvasArea(): " + ECanvasArea.CANVAS_WEST); //$NON-NLS-1$
                return ECanvasArea.CANVAS_WEST;
            }

            log.error("Position " + position + " is located in no area of the canvas!"); //$NON-NLS-1$//$NON-NLS-2$
            log.error("Leaving checkCanvasArea(): CANVAS_NONE_ERROR " + ECanvasArea.CANVAS_NONE_ERROR); //$NON-NLS-1$

            return ECanvasArea.CANVAS_NONE_ERROR;
        }
        log.error("Leaving checkCanvasArea():  CANVAS_NONE_ERROR " //$NON-NLS-1$
                + ECanvasArea.CANVAS_NONE_ERROR + ", invalid null input"); //$NON-NLS-1$

        return ECanvasArea.CANVAS_NONE_ERROR;
    }

    /**
     * <p>
     * Sets the enabled flag of the canvas and all its children. Used when
     * storing or loading a MindMap to/from disc to make sure no user actions
     * are possible anymore as well as after a StatusMessageDialog has been
     * added to the Canvas (except here we don't disable the StatusMessageDialog
     * and its immediate children)
     * </p>
     * 
     * @param lock
     *            the locking flag
     */
    protected void lockCanvas(boolean lock) {

        log.debug("Entering lockCanvas(lock=" + lock + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // TODO: add info overlay? (Loading, spinning animation)??

        if (lock) {

            log.info("Canvas locked!"); //$NON-NLS-1$

            List<MTComponent> canvasAllChildren = componentHierarchyToList(
                    this.getCanvas(), EWalkHierarchyComponents.WALK_ALL);

            // Check if we lock because of a status message or not
            if (this.statusMessageBox != null) {

                // Disable all components except the StatusMessageDialog
                // and its immediate children
                for (MTComponent comp : canvasAllChildren) {

                    if (!(comp instanceof AbstractStatusMessageDialog)) {
                        if (comp.getParent() != null) {
                            if (!(comp.getParent() instanceof AbstractStatusMessageDialog)) {
                                comp.setEnabled(false);
                            } else {
                                // Status Message child might have been locked
                                // by saving etc. before
                                comp.setEnabled(true);
                            }
                        }
                    } else {
                        // Status Message might have been locked by saving etc.
                        // before
                        comp.setEnabled(true);
                    }

                }
            } else { // we don't lock cause of a status message (loading,
                     // saving..)

                // Disable all components
                for (MTComponent comp : canvasAllChildren) {

                    comp.setEnabled(false);
                }

            }

        } else { // unlock

            log.info("Canvas unlocked!"); //$NON-NLS-1$

            List<MTComponent> canvasAllChildren = componentHierarchyToList(
                    this.getCanvas(), EWalkHierarchyComponents.WALK_ALL);

            for (MTComponent comp : canvasAllChildren) {
                comp.setEnabled(true);
            }

        }

        log.debug("Leaving lockCanvas()"); //$NON-NLS-1$

    }

    /* *********Listener methods********* */
    /**
     * Adds a double tap gesture listener to the given component (canvas) to
     * create a new IdeaNode in the model. Currently not used.
     * 
     * @param mtComp
     *            the canvas component
     */
    @SuppressWarnings("unused")
    private void addDoubleTapGestureListener(MTComponent mtComp) {

        log.trace("Entering addDoubleTapGestureListener(mtComp=" + mtComp + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Unregister all Input processors
        mtComp.unregisterAllInputProcessors();

        // Register new Tap Input Processor for double-tapping
        mtComp.registerInputProcessor(new TapProcessor(
                this.abstractMTapplication, 18.0f, true));

        // Add double tap Gesture Listener
        mtComp.addGestureListener(TapProcessor.class,
                new IGestureEventListener() {

                    @Override
                    public boolean processGestureEvent(
                            MTGestureEvent gestureEvent) {
                        log.trace("Entering processGestureEvent(gestureEvent=" //$NON-NLS-1$
                                + gestureEvent + ")"); //$NON-NLS-1$ 

                        TapEvent tapEvent = (TapEvent) gestureEvent;

                        // Check if double tap
                        if (tapEvent.isDoubleTap()) {

                            log.debug("Recognized Gesture: DOUBLE TAP on canvas"); //$NON-NLS-1$

                            // Get double tap position
                            float x = tapEvent.getCursor().getCurrentEvtPosX();
                            float y = tapEvent.getCursor().getCurrentEvtPosY();
                            Vector3D position = new Vector3D(x, y);

                            // Get rotation
                            float rotation = getRotationForNewComponent(position);

                            // Create a new model IdeaNode
                            // (Changes in the model will be picked up by the
                            // Observer!)
                            if (createModelIdeaNode(x, y, rotation,
                                    "", EIdeaNodeCreator.MULTITOUCH_TABLE)) { //$NON-NLS-1$

                            } else {
                                log.warn("Model IdeaNode for the position " + position + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
                            }

                        }
                        log.trace("Leaving processGestureEvent(): false (not handled yet!)"); //$NON-NLS-1$
                        return false;
                    }
                });

        log.trace("Leaving addDoubleTapGestureListener()"); //$NON-NLS-1$

    }

    /**
     * <p>
     * Adds a gesture listener to the canvas for recognizing rectangle, circle,
     * arrow, and x shapes.
     * </p>
     * <p>
     * Drawing a rectangle or a circle will create a new IdeaNodeView at the
     * final drawing position. Drawing an X on a IdeanNodeView will delete that
     * IdeaNodeView. Drawing an arrow over two IdeaNodeViews will connect the
     * two with a new RelationView.
     * </p>
     * 
     * @param scene
     *            the scene to add the listener to (canvas of the scene)
     */
    private void addGestureListener(AbstractScene scene) {

        log.debug("Entering addGestureListener(scene=" + scene + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Create new unistroke processor
        GlobalUnistrokeProcessorMod usProcessor = new GlobalUnistrokeProcessorMod(
                this.abstractMTapplication, scene.getCanvas());

        // Add templates for circle, rectangle, x and arrow gestures
        usProcessor.addTemplate(UnistrokeGesture.CIRCLE, Direction.CLOCKWISE);
        usProcessor.addTemplate(UnistrokeGesture.CIRCLE,
                Direction.COUNTERCLOCKWISE);
        usProcessor
                .addTemplate(UnistrokeGesture.RECTANGLE, Direction.CLOCKWISE);
        usProcessor.addTemplate(UnistrokeGesture.RECTANGLE,
                Direction.COUNTERCLOCKWISE);
        usProcessor.addTemplate(UnistrokeGesture.X, Direction.CLOCKWISE);
        usProcessor.addTemplate(UnistrokeGesture.X, Direction.COUNTERCLOCKWISE);
        usProcessor.addTemplate(UnistrokeGesture.ARROW, Direction.CLOCKWISE);
        usProcessor.addTemplate(UnistrokeGesture.ARROW,
                Direction.COUNTERCLOCKWISE);

        // Register unistroke processor on mtComponent
        scene.registerGlobalInputProcessor(usProcessor);

        usProcessor.addProcessorListener(this.getCanvas());

        // Add unistroke gesture listener to mtComponent
        scene.getCanvas().addGestureListener(GlobalUnistrokeProcessorMod.class,
                new IGestureEventListener() {

                    @Override
                    public boolean processGestureEvent(
                            MTGestureEvent gestureEvent) {

                        log.debug("Entering processGestureEvent(gestureEvent=" //$NON-NLS-1$ 
                                + gestureEvent + ")"); //$NON-NLS-1$ 

                        // Get gesture event
                        UnistrokeEvent usEvent = (UnistrokeEvent) gestureEvent;

                        // Get gesture visualization polygon
                        MTPolygon gestureVisualization = usEvent
                                .getVisualization();

                        // Set gesture visualization color
                        gestureVisualization
                                .setStrokeColor(GESTURE_VISUALIZATION_COLOR_DEFAULT);

                        switch (usEvent.getId()) {
                            case MTGestureEvent.GESTURE_STARTED:
                                // Visualize gesture on canvas
                                getCanvas().addChild(gestureVisualization);
                                break;

                            case MTGestureEvent.GESTURE_UPDATED:
                                // Do nothing
                                break;

                            case MTGestureEvent.GESTURE_ENDED:

                                // Get gesture
                                UnistrokeGesture gestureResult = usEvent
                                        .getGesture();

                                log.info("Recognized gesture: " + gestureResult); //$NON-NLS-1$

                                if (gestureResult
                                        .equals(UnistrokeGesture.CIRCLE)
                                        || gestureResult
                                                .equals(UnistrokeGesture.RECTANGLE)) {

                                    // Gesture circle of rectangle:
                                    // create a new IdeaNode for this
                                    // position

                                    // Show animation
                                    showGestureFeedback(
                                            gestureVisualization,
                                            EGestureRecognitionFeedback.GESTURE_PROCESSED);

                                    // Process the circle or rectangle
                                    // gesture
                                    processCircleRectangleGesture(usEvent);

                                    break;

                                } else if (gestureResult
                                        .equals(UnistrokeGesture.X)) {

                                    // Gesture x: remove picked idea node or
                                    // relation

                                    // Process the X Gesture
                                    processXGesture(usEvent);
                                    break;

                                }

                                if ((gestureResult
                                        .equals(UnistrokeGesture.ARROW))) {

                                    // Gesture arrow: create a new
                                    // directed relation if two
                                    // IdeaNodeViews have been picked

                                    // Process arrow gesture
                                    processArrowGesture(usEvent);

                                    break;

                                }

                                // Show animation
                                showGestureFeedback(
                                        gestureVisualization,
                                        EGestureRecognitionFeedback.GESTURE_NOT_RECOGNIZED);

                                break;
                            default:
                                log.error("Unrecognized gesture state!"); //$NON-NLS-1$
                                break;
                        }

                        log.debug("Leaving processGestureEvent(): true"); //$NON-NLS-1$

                        return true;

                    }

                });
        log.debug("Leaving addGestureListener()"); //$NON-NLS-1$ 

    }

    /**
     * Shows colored feedback if a gesture has been recognized, processed or not
     * recognized.
     * 
     * @param gestureVisualization
     *            the gesture visualization polygon
     * @param feedback
     *            the feedback
     */
    protected void showGestureFeedback(final MTPolygon gestureVisualization,
            final EGestureRecognitionFeedback feedback) {

        log.debug("Entering showGestureFeedback(gestureVisualization=" //$NON-NLS-1$
                + gestureVisualization + ", feedback=" + feedback + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Create a new Animation
        IAnimation gestureVisualAnim = new Animation("GestureFeedback", //$NON-NLS-1$
                new MultiPurposeInterpolator(500, 1, 500, 0.2f, 0.5f, 1), this);

        // Add animation listener
        gestureVisualAnim.addAnimationListener(new IAnimationListener() {
            @Override
            public void processAnimationEvent(AnimationEvent ae) {

                MTPolygon gesture = gestureVisualization;

                switch (feedback) {
                    case GESTURE_NOT_RECOGNIZED:
                        gesture.setStrokeColor(GESTURE_VISUALIZATION_COLOR_NOTRECOGNIZED);
                        break;
                    case GESTURE_RECOGNIZED:
                        gesture.setStrokeColor(GESTURE_VISUALIZATION_COLOR_RECOGNIZED);
                        break;
                    case GESTURE_PROCESSED:
                        gesture.setStrokeColor(GESTURE_VISUALIZATION_COLOR_PROCESSED);
                        break;
                    default:
                        log.error("Invalid EGestureRecognitionFeedback enum assigned"); //$NON-NLS-1$
                        break;

                }

                switch (ae.getId()) {
                    case AnimationEvent.ANIMATION_STARTED:

                        // Add visualization
                        MindMapScene.this.getCanvas().addChild(gesture);
                        break;

                    case AnimationEvent.ANIMATION_UPDATED:

                        break;
                    case AnimationEvent.ANIMATION_ENDED:

                        // Remove visualization
                        MindMapScene.this.getCanvas().removeChild(gesture);

                        break;
                    default:
                        break;
                }// switch
            }

        });

        // Start animation
        gestureVisualAnim.start();

        log.debug("Leaving showGestureFeedback()"); //$NON-NLS-1$ 

    }

    /**
     * Processes a circle or rectangle gesture for creating a new IdeaNode in
     * the model (which then triggers the creation of a corresponding
     * IdeaNodeView via the Observer pattern)
     * 
     * @param usEvent
     *            the UnistrokeEvent for the circle or rectangle gesture
     * 
     * @return true, if a IdeaNode could be created
     */
    protected boolean processCircleRectangleGesture(UnistrokeEvent usEvent) {

        log.debug("Entering processCircleRectangleGesture(usEvent=" + usEvent + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Get gesture position
        float x = usEvent.getCursor().getCurrentEvtPosX();
        float y = usEvent.getCursor().getCurrentEvtPosY();
        Vector3D position = new Vector3D(x, y);

        // Get rotation
        float rotation = getRotationForNewComponent(position);

        // Create a new model IdeaNode
        // (Changes in the model will be picked up
        // by the Observer!)
        if (createModelIdeaNode(x, y, rotation,
                "", EIdeaNodeCreator.MULTITOUCH_TABLE)) { //$NON-NLS-1$

            log.debug("Leaving processCircleRectangleGesture(): true"); //$NON-NLS-1$
            return true;
        }
        log.warn("IdeaNodeView for the position " + position + " could not be created!"); //$NON-NLS-1$ //$NON-NLS-2$
        log.warn("Leaving processCircleRectangleGesture(): false"); //$NON-NLS-1$

        return false;

    }

    /**
     * <p>
     * Processes an X gesture for deleting an IdeaNode or a relation in the
     * model (which then triggers the deletion of the corresponding IdeaNodeView
     * or RelationView via the Observer pattern)
     * </p>
     * <p>
     * TODO: Better return value than boolean??
     * </p>
     * 
     * @param usEvent
     *            the UnistrokeEvent for the X Gesture
     * 
     * @return true, if an IdeaNodeView or RelationView has been picked and
     *         deleted
     */
    protected boolean processXGesture(UnistrokeEvent usEvent) {

        log.debug("Entering processXGesture(usEvent" + usEvent + ")"); //$NON-NLS-1$//$NON-NLS-2$

        // Get gesture visualization polygon
        MTPolygon gestureVisualization = usEvent.getVisualization();

        // Get vertices of gesture polygon
        Vertex[] vertices = gestureVisualization.getVerticesGlobal();
        ArrayList<String> vectors = new ArrayList<String>();
        for (Vertex v : vertices) {

            vectors.add(v.toString());
        }

        // TRACE:
        log.trace("X gesture vertices: " //$NON-NLS-1$
                + vectors);

        // Get center of gesture visualization
        Vector3D gestCenter = gestureVisualization.getCenterPointGlobal();
        log.trace("X Gesture center: " + gestCenter); //$NON-NLS-1$

        // Get pick via target (MTCanvas)
        if (usEvent.hasTarget()) {
            // Get target
            if (usEvent.getTarget() instanceof MTCanvas) {
                MTCanvas canvas = (MTCanvas) usEvent.getTarget();

                List<PickEntry> pickEntryListWholeGesture = new ArrayList<PickEntry>();
                List<PickResult> pickResultList = new ArrayList<PickResult>();

                // Check if gesture vertices are valid
                if (vertices.length > 1) {

                    // Create a circle around the gesture center
                    // with a radius of the distance2D between
                    // the first two vertices of the X-Gesture
                    Vector3D vertexOne = vertices[0].getCopy();
                    Vector3D vertexTwo = gestCenter.getCopy();
                    float distance = ToolsGeometry.distance2D(vertexOne,
                            vertexTwo);

                    MTEllipse pickingCircle = null;

                    // Check if the radius is bigger than an IdeaNodeView,
                    // then we have to make as small, or else no
                    // IdeaNodeViews will get picked (by the bounding vertices)
                    if ((distance * X_GESTURE_PICKING_RADIUS_SCALE_PERCENT) > (this
                            .getCalculatedIdeaNodeHeight() * 0.25f)) {

                        log.trace("Smaller circle with radius " //$NON-NLS-1$
                                + (this.getCalculatedIdeaNodeHeight() * 0.25f));

                        pickingCircle = new MTEllipse(
                                this.abstractMTapplication, gestCenter,
                                this.getCalculatedIdeaNodeHeight() * 0.25f,
                                this.getCalculatedIdeaNodeHeight() * 0.25f);
                    } else {

                        log.trace("Default circle with radius " + distance //$NON-NLS-1$
                                * X_GESTURE_PICKING_RADIUS_SCALE_PERCENT);

                        pickingCircle = new MTEllipse(
                                this.abstractMTapplication,
                                gestCenter,
                                distance
                                        * X_GESTURE_PICKING_RADIUS_SCALE_PERCENT,
                                distance
                                        * X_GESTURE_PICKING_RADIUS_SCALE_PERCENT);
                    }

                    // DEBUG
                    // this.getCanvas().addChild(pickingCircle);

                    // Check if circle has bounds
                    if (pickingCircle.hasBounds()) {

                        // Get the circle bounding shape vertices (=rectangle)
                        Vector3D[] pickingVertices = pickingCircle.getBounds()
                                .getVectorsGlobal();

                        // Pick for all gesture vertices (circle: four)
                        for (Vector3D v : pickingVertices) {

                            pickEntryListWholeGesture.addAll(canvas.pick(
                                    v.getX(), v.getY()).getPickList());

                            pickResultList.add(canvas.pick(v.getX(), v.getY()));
                        }

                        // Add picks for gesture center
                        pickEntryListWholeGesture.addAll(canvas.pick(
                                gestCenter.getX(), gestCenter.getY())
                                .getPickList());

                        int pickResultNr = 0;
                        int pickEntryNr = 0;

                        // Get pick entries for all pick results
                        for (PickResult pr : pickResultList) {
                            List<PickEntry> pickEntryList = pr.getPickList();
                            for (PickEntry pe : pickEntryList) {

                                log.trace("PR" //$NON-NLS-1$
                                        + pickResultNr
                                        + ": Pick Entry " //$NON-NLS-1$
                                        + pickEntryNr
                                        + " for component " //$NON-NLS-1$
                                        + pe.hitObj
                                        + " has camera distance " //$NON-NLS-1$
                                        + pe.cameraDistance
                                        + " and distance to pick " //$NON-NLS-1$
                                        + pr.getDistanceOfPickedObj(pe.hitObj));

                                pickEntryNr++;
                            }
                            pickResultNr++;
                        }

                        List<IdeaNodeView> ideaNodeList = new ArrayList<IdeaNodeView>();
                        List<RelationView> relationViewList = new ArrayList<RelationView>();

                        for (PickEntry pe : pickEntryListWholeGesture) {

                            // Pick all ideaNodeViews
                            if (pe.hitObj instanceof IdeaNodeView) {
                                log.debug("X Gesture has pick IdeaNodeView: " + pe.hitObj); //$NON-NLS-1$
                                if (!ideaNodeList.contains(pe.hitObj)) {
                                    ideaNodeList.add((IdeaNodeView) pe.hitObj);

                                }
                                continue;

                            }

                            // If a NodeContentContainer has been picked, add
                            // the parent
                            // IdeaNodeView to the list
                            if (pe.hitObj instanceof NodeContentContainer) {
                                log.debug("X Gesture has pick NodeContentContainer: " + pe.hitObj); //$NON-NLS-1$

                                if (!ideaNodeList
                                        .contains(((NodeContentContainer) pe.hitObj)
                                                .getParentIdeaNodeView())) {
                                    ideaNodeList
                                            .add(((NodeContentContainer) pe.hitObj)
                                                    .getParentIdeaNodeView());

                                }
                                continue;
                            }

                            // If a MarkerContainer has been picked, add the
                            // parent
                            // IdeaNodeView to the list
                            if (pe.hitObj instanceof MarkerContainer) {
                                log.debug("X Gesture has pick MarkerContainer: " + pe.hitObj); //$NON-NLS-1$

                                if (((MarkerContainer) pe.hitObj)
                                        .getParentComponent() instanceof IdeaNodeView) {
                                    if (!ideaNodeList
                                            .contains(((MarkerContainer) pe.hitObj)
                                                    .getParentComponent())) {
                                        ideaNodeList
                                                .add((IdeaNodeView) ((MarkerContainer) pe.hitObj)
                                                        .getParentComponent());

                                    }
                                }
                                continue;
                            }

                            // If a RVAdornmentsContainer has been picked, add
                            // RelationView to the list
                            if (pe.hitObj instanceof RVAdornmentsContainer) {
                                log.debug("X Gesture has pick RVAdornmentsContainer: " + pe.hitObj); //$NON-NLS-1$

                                if (!relationViewList
                                        .contains(((RVAdornmentsContainer) pe.hitObj)
                                                .getParentRelationView())) {
                                    relationViewList
                                            .add(((RVAdornmentsContainer) pe.hitObj)
                                                    .getParentRelationView());

                                }
                                continue;
                            }

                            // If a RelationView has been picked, add to the
                            // list
                            if (pe.hitObj instanceof RelationView) {
                                log.debug("X Gesture has pick RelationView: " + pe.hitObj); //$NON-NLS-1$

                                if (!relationViewList.contains(pe.hitObj)) {
                                    relationViewList
                                            .add((RelationView) pe.hitObj);
                                }
                                continue;
                            }

                        }

                        // TRACE:

                        log.debug("X Gesture pick has following IdeaNodeList:"); //$NON-NLS-1$
                        int ideaNodeNr = 0;
                        for (IdeaNodeView node : ideaNodeList) {
                            ideaNodeNr++;
                            log.debug("NodeNr" //$NON-NLS-1$
                                    + ideaNodeNr + ": " //$NON-NLS-1$
                                    + node + ", text: " //$NON-NLS-1$
                                    + node.getNodeContentText());

                        }

                        log.debug("X Gesture pick has following RelationView list:"); //$NON-NLS-1$
                        int relationNr = 0;
                        for (RelationView relation : relationViewList) {
                            log.debug("RelationNr" //$NON-NLS-1$
                                    + relationNr + ": " //$NON-NLS-1$
                                    + relation);
                            relationNr++;
                        }
                        /* End of tracing */

                        // Store pick object
                        IdeaNodeView ideaNodeView = null;
                        RelationView relationView = null;

                        // Check lists for content
                        if (relationViewList.size() > 0) {

                            // If we only have relationViews, check if we
                            // have exactly one RelationView, else do nothing
                            if (ideaNodeList.size() < 1) {

                                if (relationViewList.size() == 1) {
                                    // Relation view is now at index 0
                                    relationView = relationViewList.get(0);
                                } else {
                                    // We picked multiple RelationViews, do
                                    // nothing
                                }

                            } else { // We also have IdeaNodeViews picked

                                // Right now, do nothing

                                // TODO: maybe pick the relationView? We might
                                // have a very short RelationView otherwise
                                // impossible to delete (without picking nodes,
                                // too)
                                // Or we picked a IdeaNodeView and accidently
                                // picked
                                // a relationView, too

                            }

                        } else { // No relation views picked

                            if (ideaNodeList.size() > 0) {

                                if (ideaNodeList.size() == 1) {
                                    // Idea view is now at index 0
                                    ideaNodeView = ideaNodeList.get(0);
                                } else {
                                    // We picked multiple IdeaNodeViews, do
                                    // nothing
                                }

                            } else {
                                // We picked neither RelationViews nor
                                // IdeaNodeViews
                                // Do nothing
                            }

                        }

                        // **
                        // Favor RelationViews for deleting
                        if (relationView != null) {

                            // Get parent and child IdeaNodeViews
                            IdeaNodeView parentNodeCV = relationView
                                    .getParentIdeaNodeView();
                            IdeaNodeView childNodeCV = relationView
                                    .getChildIdeaNodeView();

                            if (parentNodeCV != null && childNodeCV != null) {

                                // Get current mindMap and corresponding
                                // model IdeaNodes
                                MindMap mindMap = getModelReference()
                                        .getLoadedMindMap();
                                IdeaNode parentModelNode = parentNodeCV
                                        .getModelIdeaNode();
                                IdeaNode childModelNode = childNodeCV
                                        .getModelIdeaNode();

                                if (parentModelNode != null
                                        && childModelNode != null) {

                                    // Remove selected relation from model by
                                    // removing the relation's child IdeaNode
                                    // from its parent IdeaNode
                                    ERemoveChildIdeaNodeResultCase result = parentModelNode
                                            .removeIdeaChild(childModelNode,
                                                    mindMap);
                                    if (result
                                            .equals(ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_CONSTRAINT)
                                            || result
                                                    .equals(ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_ERROR)) {

                                        // Show animation
                                        showGestureFeedback(
                                                gestureVisualization,
                                                EGestureRecognitionFeedback.GESTURE_RECOGNIZED);

                                        log.error("Leaving processXGesture(): false, RelationView could not be removed in model!"); //$NON-NLS-1$
                                        // TODO: Message?
                                        return false;
                                    }
                                    // Show animation
                                    showGestureFeedback(
                                            gestureVisualization,
                                            EGestureRecognitionFeedback.GESTURE_PROCESSED);

                                    log.debug("Leaving processXGesture(): true"); //$NON-NLS-1$
                                    return true;

                                }

                                log.error("Leaving processXGesture(): false, One of the model idea nodes could not be found!!"); //$NON-NLS-1$
                                // TODO: Close application? No longer in sync
                                return false;

                            }

                            log.error("Leaving processXGesture(): false, The relation view misses a parent or a child IdeaNodeView!"); //$NON-NLS-1$
                            // TODO: Close application? No longer a valid view!
                            return false;

                        }

                        log.debug("No relation view picked to delete!"); //$NON-NLS-1$

                        if (ideaNodeView != null) {

                            // Delete from model
                            if (!getModelReference().getLoadedMindMap()
                                    .removeIdeaNode(
                                            ideaNodeView.getModelIdeaNode())) {

                                log.error("Leaving processXGesture(): IdeaNode could not be deleted!!"); //$NON-NLS-1$
                                // TODO: Message?
                                return false;

                            }
                            // Show animation
                            showGestureFeedback(
                                    gestureVisualization,
                                    EGestureRecognitionFeedback.GESTURE_PROCESSED);

                            log.debug("Leaving processXGesture(): true"); //$NON-NLS-1$
                            return true;

                        }
                        // Show animation
                        showGestureFeedback(gestureVisualization,
                                EGestureRecognitionFeedback.GESTURE_RECOGNIZED);

                        log.debug("Leaving processXGesture(): No IdeaNode picked to delete!"); //$NON-NLS-1$
                        return false;
                    }

                    log.error("Leaving processXGesture(): false, circle has no bounds!"); //$NON-NLS-1$
                    return false;
                }

                log.error("Leaving processXGesture(): false, gesture is missing vertices!"); //$NON-NLS-1$
                return false;
            }

            log.error("Leaving processXGesture(): false, no target!"); //$NON-NLS-1$
            return false;

        }

        log.error("Leaving processXGesture(): false, no target!"); //$NON-NLS-1$
        return false;

    }

    /**
     * <p>
     * Processes an arrow gesture for creating a parent-child relation for two
     * IdeaNodes in the model (which then triggers the creation of a
     * corresponding RelationView for both IdeaNodes via the Observer pattern)
     * <p>
     * 
     * <p>
     * TODO: better return value than boolean?
     * </p>
     * 
     * @param usEvent
     *            the Unistroke Event for the Arrow gesture
     * 
     * @return true, if two IdeaNodeViews have been picked and a relation has
     *         been successfully created in the model
     */
    protected boolean processArrowGesture(UnistrokeEvent usEvent) {

        log.debug("Entering processArrowGesture(usEvent=" + usEvent + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Get visualization
        MTPolygon gestureVisualization = usEvent.getVisualization();

        // Get vertices of gesture polygon
        Vertex[] vertices = gestureVisualization.getVerticesGlobal();
        ArrayList<String> vectors = new ArrayList<String>();
        for (Vertex v : vertices) {

            vectors.add(v.toString());
        }

        // TRACE:
        log.trace("Arrow gesture vertices: " //$NON-NLS-1$
                + vectors);
        Vector3D gestCenter = gestureVisualization.getCenterPointGlobal();
        log.trace("Arrow Gesture center: " //$NON-NLS-1$
                + gestCenter);

        // Pick via canvas
        if (usEvent.hasTarget()) {
            // Get target
            if (usEvent.getTarget() instanceof MTCanvas) {
                MTCanvas canvas = (MTCanvas) usEvent.getTarget();

                // Pick components for the whole arrow gesture
                List<PickEntry> pickEntryListWholeGesture = new ArrayList<PickEntry>();
                List<PickResult> pickResultList = new ArrayList<PickResult>();
                for (Vertex v : vertices) {

                    pickEntryListWholeGesture.addAll(canvas.pick(v.getX(),
                            v.getY()).getPickList());

                    pickResultList.add(canvas.pick(v.getX(), v.getY()));
                }

                int pickResultNr = 0;
                int pickEntryNr = 0;

                // Get pick entries for all pick results
                for (PickResult pr : pickResultList) {
                    List<PickEntry> pickEntryList = pr.getPickList();
                    for (PickEntry pe : pickEntryList) {

                        log.trace("PR" //$NON-NLS-1$
                                + pickResultNr + ": Pick Entry " //$NON-NLS-1$
                                + pickEntryNr + " for component " //$NON-NLS-1$
                                + pe.hitObj + " has camera distance " //$NON-NLS-1$
                                + pe.cameraDistance + " and distance to pick " //$NON-NLS-1$
                                + pr.getDistanceOfPickedObj(pe.hitObj));

                        pickEntryNr++;
                    }
                    pickResultNr++;
                }

                List<IdeaNodeView> ideaNodeList = new ArrayList<IdeaNodeView>();
                for (PickEntry pe : pickEntryListWholeGesture) {

                    // Pick all ideaNodeViews
                    if (pe.hitObj instanceof IdeaNodeView) {
                        log.trace("Arrow Gesture has pick IdeaNodeView: " + pe.hitObj); //$NON-NLS-1$
                        if (!ideaNodeList.contains(pe.hitObj)) {
                            ideaNodeList.add((IdeaNodeView) pe.hitObj);
                        }

                    }
                }
                log.debug("Arrow Gesture has picked the following IdeaNodeViews: " + ideaNodeList); //$NON-NLS-1$
                for (IdeaNodeView node : ideaNodeList) {
                    log.debug("IdeaNode: " + node.getName() + " at " + node.getPosition(TransformSpace.GLOBAL)); //$NON-NLS-1$//$NON-NLS-2$
                }

                // If two ideaNodes have been picked, create directed relation
                // in the model in picking order
                if (ideaNodeList.size() == 2) {

                    // The first IdeaNodeView in the
                    // list is the parent
                    IdeaNodeView parent = ideaNodeList.get(0);

                    // The second IdeaNodeView in
                    // the list is the child
                    IdeaNodeView child = ideaNodeList.get(1);

                    EAddChildIdeaNodeResultCase result = createModelRelation(
                            parent, child);

                    if (result
                            .equals(EAddChildIdeaNodeResultCase.NO_ADD_ON_CONSTRAINT)) {

                        log.warn("This relation cannot be added because of semantic constraints!"); //$NON-NLS-1$

                        // Show animation
                        showGestureFeedback(gestureVisualization,
                                EGestureRecognitionFeedback.GESTURE_RECOGNIZED);

                        // DO nothing
                        // TODO: signal that adding this relation is not
                        // allowed
                        log.warn("Leaving processArrowGesture(): false"); //$NON-NLS-1$
                        return false;
                    }
                    if (result
                            .equals(EAddChildIdeaNodeResultCase.NO_ADD_ON_ERROR)) {

                        log.error("Error while adding the relation between " //$NON-NLS-1$
                                + parent + " and" //$NON-NLS-1$
                                + child + "!"); //$NON-NLS-1$

                        // Show animation
                        showGestureFeedback(gestureVisualization,
                                EGestureRecognitionFeedback.GESTURE_RECOGNIZED);

                        // DO nothing
                        // TODO: signal that adding this relation is not
                        // allowed
                        return false;

                    }

                    // Show animation
                    showGestureFeedback(gestureVisualization,
                            EGestureRecognitionFeedback.GESTURE_PROCESSED);

                    log.trace("Leaving processArrowGesture(): true "); //$NON-NLS-1$
                    return true;

                }
                // Show animation
                showGestureFeedback(gestureVisualization,
                        EGestureRecognitionFeedback.GESTURE_RECOGNIZED);

                log.debug("Leaving processArrowGesture(): false, no two IdeaNodeViews have been picked!"); //$NON-NLS-1$
                return false;

            }
            log.error("Leaving processArrowGesture(): false, no target!"); //$NON-NLS-1$
            return false;

        }
        log.error("Leaving processArrowGesture(): false, no target!"); //$NON-NLS-1$
        return false;

    }

    /**
     * Simulates a double tap input to open the keyboard on IdeaNodeView
     * creation.
     * 
     * @param ideaNodeView
     *            the ideaNodeView on which a double tap is to be simulated
     */
    private void simulateDoubleTap(IdeaNodeView ideaNodeView) {

        if (ideaNodeView != null) {

            log.debug("Entering simulateDoubleTap(ideaNodeView=" + ideaNodeView + ")"); //$NON-NLS-1$ //$NON-NLS-2$

            // Create new double tap TapProcessor
            TapProcessor simulateTap = new TapProcessor(
                    this.abstractMTapplication, 18.0f, true);

            // Create fake cursor
            InputCursor simulateCursor = new InputCursor();

            // Create fake gesture event for double tap
            TapEvent gestEv = new TapEvent(simulateTap, TapEvent.DOUBLE_TAPPED,
                    ideaNodeView, simulateCursor,
                    ideaNodeView.getCenterPointGlobal(), TapEvent.DOUBLE_TAPPED);

            // Process fake double tap by ideaNodeView
            ideaNodeView.getNodeContentView().processInputEvent(gestEv);

            log.debug("Leaving simulateDoubleTap()"); //$NON-NLS-1$
            return;

        }
        log.error("Leaving simulateDoubleTap(): invalid null input!"); //$NON-NLS-1$

    }

    /* ********Overridden methods******** */
    /**
     * <p>
     * Invoked when a scene change takes place right before the scene the scene
     * which is changed to becomes the currently active scene. It can be used to
     * initialize a few settings or to register a keyboard listener for example.
     * </p>
     * 
     */
    @Override
    public void onEnter() {

        log.debug("Entering onEnter()"); //$NON-NLS-1$

        // Do nothing for now

        log.debug("Leaving onEnter()"); //$NON-NLS-1$
    }

    /**
     * Is called on the currently active scene when a scene change to another
     * scene occurs.
     * 
     */
    @Override
    public void onLeave() {
        log.debug("Entering onLeave()"); //$NON-NLS-1$

        // Do nothing for now

        log.debug("Leaving onLeave()"); //$NON-NLS-1$

    }

    /**
     * Updates the current MindMapScene as a result of changes in the model
     * depending on the given ObserverNotificationObject.
     * 
     * @param o
     *            the observed model object that has communicated a change
     * @param arg
     *            an added notification argument (ObserverNotificationObject)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void update(Observable o, Object arg) {

        log.debug("Entering update(o=" + o + ", arg=" + arg + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        /* MindMap updates */

        // Check if arguments are valid
        if (o instanceof MindMap && arg instanceof ObserverNotificationObject) {

            if (((ObserverNotificationObject) arg).getEnumStatus() instanceof EMindMapChangeStatus) {
                EMindMapChangeStatus status = (EMindMapChangeStatus) ((ObserverNotificationObject) arg)
                        .getEnumStatus();

                Object content = ((ObserverNotificationObject) arg)
                        .getContent();

                // Status dependent observer actions
                switch (status) {
                    case MIND_MAP_IDEA_NODE_ADDED:
                        // Add a new IdeaNodeView for this model ideaNode
                        if (content instanceof IdeaNode) {

                            IdeaNode modelIdeaNode = (IdeaNode) content;

                            // Create a new IdeaNodeView dependent on IdeaNode
                            // creator (MultitouchDevice or BTDevice)

                            switch (modelIdeaNode.getIdeaOwner()) {
                                case BLUETOOTH_SERVER:

                                    if (!createIdeaNodeViewFromBTDevice(modelIdeaNode)) {
                                        log.error("Wrong update parameters in Observer " + this + "!"); //$NON-NLS-1$ //$NON-NLS-2$
                                        // TODO: Close Application, fatal error,
                                        // view
                                        // and model no longer in synch

                                    }
                                    break;
                                case MULTITOUCH_TABLE:

                                    if (!createIdeaNodeView(modelIdeaNode)) {
                                        log.error("Wrong update parameters in Observer " + this + "!"); //$NON-NLS-1$ //$NON-NLS-2$
                                        // TODO: Close Application, fatal error,
                                        // view
                                        // and model no longer in synch

                                    }
                                    break;
                                default:

                                    log.error("Wrong owner enum set in model IdeaNode!"); //$NON-NLS-1$

                                    break;

                            }

                        } else {
                            log.error("Wrong update parameters in Observer " + this + "!"); //$NON-NLS-1$ //$NON-NLS-2$
                            // TODO: Close Application, fatal error, view and
                            // model no longer in synch
                        }

                        break;
                    case MIND_MAP_IDEA_NODE_LIST_SET:
                        // Currently not used
                        // TODO: disable in model!? too complex, not needed
                        break;
                    case MIND_MAP_IDEA_NODE_REMOVED:
                        // Remove corresponding IdeaNodeView from the canvas
                        // (all relations should have been deleted already in
                        // reaction to children lists!!)
                        // see removeIdeaChild() in MindMap
                        ArrayList<MTComponent> ideaNodeViewList = getAllIdeaNodeViews();

                        if (content instanceof IdeaNode) {

                            // Find the corresponding IdeaNodeView
                            IdeaNodeView ideaNodeViewToDelete = findIdeaNodeViewByIdeaNode(
                                    (IdeaNode) content, ideaNodeViewList);

                            if (ideaNodeViewToDelete != null) {

                                log.debug("Deleting IdeaNodeView: " //$NON-NLS-1$
                                        + ideaNodeViewToDelete);

                                // Delete from canvas
                                deleteIdeaNodeView(ideaNodeViewToDelete);

                            } else {
                                log.error("IdeaNodeView with ideaNode " + content + " could not be found!"); //$NON-NLS-1$//$NON-NLS-2$
                                // TODO: Close Application, fatal error, view
                                // and
                                // model no longer in synch
                            }

                        } else {
                            log.error("Wrong update parameters in Observer " + this + "!"); //$NON-NLS-1$ //$NON-NLS-2$
                            // TODO: Close Application, fatal error, view and
                            // model no longer in synch

                        }

                        break;
                    case MIND_MAP_MAP_ADDED:
                        // Do nothing for now
                        // We don't care about Maps in the View at the moment
                        break;
                    case MIND_MAP_MAP_REMOVED:
                        // Do nothing for now
                        // We don't care about Maps in the View at the moment
                        break;
                    case MIND_MAP_MAP_LIST_SET:
                        // Do nothing for now
                        // We don't care about Maps in the View at the moment
                        break;
                    case MIND_MAP_TITLE_SET:
                        // Set MindMapScene name
                        if (content instanceof String) {

                            this.setName((String) content);

                        }

                        break;
                    default:
                        log.error("Wrong update parameters in Observer " + this + "!"); //$NON-NLS-1$ //$NON-NLS-2$
                        // TODO: Close Application, fatal error, view and model
                        // no longer in synch

                        break;

                }

            } else {
                log.error("Wrong update parameters in Observer " + this + "!"); //$NON-NLS-1$ //$NON-NLS-2$
                // TODO: Close Application, fatal error, view and model no
                // longer in synch

            }

        } else {

            /* MindMapCollection updates */

            // Check if arguments are valid
            if (o instanceof MindMapCollection
                    && arg instanceof ObserverNotificationObject) {

                if (((ObserverNotificationObject) arg).getEnumStatus() instanceof EMindMapCollectionChangeStatus) {
                    EMindMapCollectionChangeStatus status = (EMindMapCollectionChangeStatus) ((ObserverNotificationObject) arg)
                            .getEnumStatus();

                    Object content = ((ObserverNotificationObject) arg)
                            .getContent();

                    // Status dependent observer actions
                    switch (status) {
                        case NEW_MIND_MAP_LOADED:

                            // Lock canvas
                            // lockCanvas(true);

                            // Add this as observer to the new mindMap
                            // TODO: Remove observer from old MindMap??

                            if (content instanceof MindMap
                                    && content.equals(this.getModelReference()
                                            .getLoadedMindMap())) {
                                ((MindMap) content).addObserver(this);

                                // Load IdeaNodeViews and RelationViews
                                if (loadNewMindMapScene((MindMap) content)) {
                                    final String statusMsg = Messages
                                            .getString("MindMapScene.load.statusMsg.ok.part1") + ((MindMap) content).getMindMapTitle() + Messages.getString("MindMapScene.load.statusMsg.ok.part2"); //$NON-NLS-1$ //$NON-NLS-2$

                                    log.debug("Loading mindMap successful!"); //$NON-NLS-1$

                                    this.registerPreDrawAction(new IPreDrawAction() {
                                        @Override
                                        public void processAction() {

                                            // Create a new runnable that calls
                                            // the
                                            // loading method
                                            getMTApplication().invokeLater(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            // Show info message
                                                            showStatusMessageOK(
                                                                    statusMsg,
                                                                    EStatusMessageType.STATUS_MSG_INFO,
                                                                    getStatusMessageFontVerySmall(),
                                                                    getStatusMessageFontMedium(),
                                                                    STATUS_MSG_OK_MORE_TEXT_MAXNUMOFLINES);
                                                        }
                                                    });
                                        }

                                        @Override
                                        public boolean isLoop() {
                                            return false;
                                        }
                                    });

                                } else {
                                    final String statusMsgNotOk = Messages
                                            .getString("MindMapScene.load.statusMsg.notOk.part1") + ((MindMap) content).getMindMapTitle() + Messages.getString("MindMapScene.load.statusMsg.notOk.part2"); //$NON-NLS-1$ //$NON-NLS-2$

                                    log.debug("Loading MindMap not successful!"); //$NON-NLS-1$

                                    this.registerPreDrawAction(new IPreDrawAction() {
                                        @Override
                                        public void processAction() {

                                            // Create a new runnable that calls
                                            // the
                                            // loading method
                                            getMTApplication().invokeLater(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            // Show error
                                                            // message
                                                            showStatusMessageOK(
                                                                    statusMsgNotOk,
                                                                    EStatusMessageType.STATUS_MSG_ERROR,
                                                                    getStatusMessageFontVerySmall(),
                                                                    getStatusMessageFontMedium(),
                                                                    STATUS_MSG_OK_MORE_TEXT_MAXNUMOFLINES);
                                                        }
                                                    });
                                        }

                                        @Override
                                        public boolean isLoop() {
                                            return false;
                                        }
                                    });

                                }

                            } else {
                                log.error("Wrong update parameters in Observer " + this + "!"); //$NON-NLS-1$ //$NON-NLS-2$
                                // TODO: Close Application, fatal error, view
                                // and
                                // model no
                                // longer in synch

                            }

                            // If for whatever reason the status message is not
                            // open, unlock canvas
                            // Else unlocking is done by status message box
                            if (MindMapScene.this.getStatusMessageBox() == null) {
                                log.debug("Message box not opened, unlock"); //$NON-NLS-1$
                                MindMapScene.this.lockCanvas(false);
                            }

                            break;
                        default:
                            log.error("Wrong update parameters in Observer " + this + "!"); //$NON-NLS-1$ //$NON-NLS-2$
                            // TODO: Close Application, fatal error, view and
                            // model
                            // no
                            // longer in synch

                            break;
                    }

                } else {
                    log.error("Wrong update parameters in Observer " + this + "!"); //$NON-NLS-1$ //$NON-NLS-2$
                    // TODO: Close Application, fatal error, view and model no
                    // longer in synch

                }

            } else if (o instanceof WaitBtThread
                    && arg instanceof ObserverNotificationObject) {

                if (((ObserverNotificationObject) arg).getEnumStatus() instanceof EBluetoothConnectionListState) {

                    EBluetoothConnectionListState status = (EBluetoothConnectionListState) ((ObserverNotificationObject) arg)
                            .getEnumStatus();

                    Object content = ((ObserverNotificationObject) arg)
                            .getContent();

                    // Status dependent observer actions
                    switch (status) {
                        // as of now
                        // fall through for all states
                        case CONNECTION_LIST_SET:
                        case CONNECTION_ADDED:
                        case CONNECTION_REMOVED:

                            if (content.getClass() == this.btConnectionStringList
                                    .getClass()) {

                                // Update bluetooth connection string list
                                this.btConnectionStringList = (ArrayList<String>) content;

                                // if bluetooth overlay is opened, update cells
                                if (this.bluetoothOverlay != null) {
                                    this.bluetoothOverlay.updateCells();
                                }

                            }

                            break;
                        default:
                            // do nothing
                            break;
                    }

                }
            }

        }
        log.debug("Leaving update()"); //$NON-NLS-1$
    }

}
