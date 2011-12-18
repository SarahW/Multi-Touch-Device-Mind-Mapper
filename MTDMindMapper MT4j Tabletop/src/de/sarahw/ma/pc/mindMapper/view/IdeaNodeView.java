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
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.StateChange;
import org.mt4j.components.StateChangeEvent;
import org.mt4j.components.StateChangeListener;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.interfaces.IMTComponent;
import org.mt4j.components.visibleComponents.StyleInfo;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTBackgroundImage;
import org.mt4j.components.visibleComponents.widgets.MTSvg;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.keyboard.MTKeyboard;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.animation.Animation;
import org.mt4j.util.animation.AnimationEvent;
import org.mt4j.util.animation.IAnimation;
import org.mt4j.util.animation.IAnimationListener;
import org.mt4j.util.animation.MultiPurposeInterpolator;
import org.mt4j.util.animation.ani.AniAnimation;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GL10;
import org.mt4jx.components.visibleComponents.widgets.MTSuggestionTextArea;

import processing.core.PApplet;
import de.sarahw.ma.pc.mindMapper.ObserverNotificationObject;
import de.sarahw.ma.pc.mindMapper.model.EIdeaNodeChangedStatus;
import de.sarahw.ma.pc.mindMapper.model.IdeaNode;
import de.sarahw.ma.pc.mindMapper.model.Node;
import de.sarahw.ma.pc.mindMapper.model.NodeData;

/**
 * <p>
 * Class representing the view of an IdeaNode. Contains a NodeContentContainer
 * object, which contains the NodeContent (currently a String).
 * </p>
 * 
 * <p>
 * IdeaNodeView graphic is a modified version of <a href=
 * "http://www.openclipart.org/detail/17620/blank-sticky-note-by-lemmling">http
 * ://www.openclipart.org/detail/17620/blank-sticky-note-by-lemmling</a>
 * </p>
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */
@SuppressWarnings("synthetic-access")
public class IdeaNodeView extends MTRectangle implements Observer {

    protected static Logger        log                                 = Logger.getLogger(IdeaNodeView.class);

    /* *** IdeaNodeView constants *** */
    // (width/height optimized for application resolution 1920*1080px)
    // Child elements are positioned with relative values (percent)

    /** The maximum ideaNodeView width in pixels * 2 */
    public static final float      IDEANODE_WIDTH_DEFAULT              = 266f;

    /** The maximum ideaNodeView height in pixels * 2 */
    public static final float      IDEANODE_HEIGHT_DEFAULT             = 190f;

    /** The overlay ideaNodeView in percent of the application width */
    public static final float      IDEANODE_WIDTH_SCALE_TO_APP_PERCENT = 0.0692708f;

    /** The ideaNodeView background image file path */
    private static final String    IDEANODE_BG_FILE_PATH               = MT4jSettings
                                                                               .getInstance()
                                                                               .getDefaultSVGPath()
                                                                               + "openclipart.org" + AbstractMTApplication.separator + "note_lightYellow_266.svg"; //$NON-NLS-1$ //$NON-NLS-2$

    /** The ideaNodeView styleInfo */
    private static final StyleInfo IDEA_NODE_STYLE_INFO                = new StyleInfo(
                                                                               MTColor.WHITE,
                                                                               MTColor.WHITE,
                                                                               true,
                                                                               true,
                                                                               true,
                                                                               1.0f,
                                                                               GL10.GL_TRIANGLE_FAN,
                                                                               (short) 0);

    /**
     * The desired offset from the border that adds to the maximum/minimum
     * position the ideaNodeView can be positioned at.
     */
    private static final float     CHECK_BORDER_OFFSET                 = 0;

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication  abstractMTapplication;

    /* *** IdeaNodeView *** */
    /** The final ideaNodeView width */
    private float                  ideaNodeWidth                       = 0.0f;
    /** The final ideaNodeView height */
    private float                  ideaNodeHeight                      = 0.0f;

    /** The ideaNodeView font default (bigger) */
    private IFont                  ideaNodeFontDefault;
    /** The ideaNodeView font small */
    private IFont                  ideaNodeFontSmall;

    /** The connected model ideaNode instance */
    private IdeaNode               modelIdeaNode;

    /** The connected model ideaNode children instances */
    private List<Node<NodeData>>   modelChildren;

    /** The nodeContentContainer that contains the text fields */
    private NodeContentContainer   nodeContentView;

    /** The keyboard instance attached to the ideaNodeView */
    private MTKeyboard             ideaNodeKeyboard;

    /** The initial ideaNode text */
    private String                 ideaNodeTextToSet;

    /* *** Touch points *** */
    /** The markerContainer for the relationView anchor touch points */
    private MarkerContainer        tpMarkerContainer;

    /** The touch point north for the relationView anchor */
    private Vector3D               touchPointNorth;
    /** The touch point east for the relationView anchor */
    private Vector3D               touchPointEast;
    /** The touch point south for the relationView anchor */
    private Vector3D               touchPointSouth;
    /** The touch point west for the relationView anchor */
    private Vector3D               touchPointWest;

    /* ***********Constructors*********** */

    /**
     * Constructor for creating a new IdeaNodeView object with a given text.
     * Sets the current application and fonts, calls initialize() for
     * Initialization.
     * 
     * @param applet
     *            the application instance
     * @param x
     *            the x position of the IdeaNodeView
     * @param y
     *            the y position of the IdeaNodeView
     * @param width
     *            the width of the IdeaNodeView
     * @param height
     *            the height of the IdeaNodeView
     * @param text
     *            the IdeaNodeView text
     */
    public IdeaNodeView(PApplet applet, float x, float y, float width,
            float height, IFont fontDefault, IFont fontSmall,
            IdeaNode modelIdeaNode, String text) {

        super(applet, x, y, 0, width, height);

        log.debug("Executing IdeaNodeView(applet=" + applet + ", x=" + x //$NON-NLS-1$ //$NON-NLS-2$
                + ", y=" + y + ", width=" + width + ", height=" + height //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
                + ", fontDefault=" + fontDefault + ", fontSmall=" + fontSmall //$NON-NLS-1$ //$NON-NLS-2$
                + ", modelIdeaNode=" + modelIdeaNode + ", text=" + text + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // Set application
        this.abstractMTapplication = (AbstractMTApplication) applet;

        // Set font
        this.ideaNodeFontDefault = fontDefault;
        this.ideaNodeFontSmall = fontSmall;

        // Set model ideaNode reference
        this.modelIdeaNode = modelIdeaNode;

        // Set model children reference
        this.modelChildren = modelIdeaNode.getChildren();

        // Store ideaNode text to set
        this.ideaNodeTextToSet = text;

        // Store dimensions
        this.ideaNodeWidth = width;
        this.ideaNodeHeight = height;

        // Initialize IdeaNodeView
        initialize();

    }

    /* ********Getters & Setters******** */
    /**
     * Returns the default font for the IdeaNodeView.
     * 
     * @return the ideaNodeFontDefault
     */
    public IFont getIdeaNodeFontDefault() {
        return this.ideaNodeFontDefault;
    }

    /**
     * Returns the smaller font for the IdeaNodeView.
     * 
     * @return the ideaNodeFontSmall
     */
    public IFont getIdeaNodeFontSmall() {
        return this.ideaNodeFontSmall;
    }

    /**
     * Returns the ideaNodeWidth for the IdeaNodeView.
     * 
     * @return the ideaNodeWidth
     */
    public float getIdeaNodeWidth() {
        return this.ideaNodeWidth;
    }

    /**
     * Returns the ideaNodeHeight for the IdeaNodeView.
     * 
     * @return the ideaNodeHeight
     */
    public float getIdeaNodeHeight() {
        return this.ideaNodeHeight;
    }

    /**
     * Returns the model IdeaNode instance that is represented by this
     * IdeaNodeView.
     * 
     * @return the modelIdeaNode
     */
    public IdeaNode getModelIdeaNode() {
        return this.modelIdeaNode;
    }

    /**
     * Returns the NodeContentContainer instance.
     * 
     * @return the nodeContentView
     */
    public NodeContentContainer getNodeContentView() {
        return this.nodeContentView;
    }

    /**
     * Returns the initially set text of this IdeaNode.
     * 
     * @return the ideaNodeTextToSet
     */
    public String getIdeaNodeTextToSet() {
        return this.ideaNodeTextToSet;
    }

    /**
     * Returns the active MTKeyboard instance.
     * 
     * @return the ideaNodeKeyboard, null if no keyboard is active at the moment
     */
    public MTKeyboard getIdeaNodeKeyboard() {
        return this.ideaNodeKeyboard;
    }

    /**
     * Returns the model IdeaNode children list of the IdeaNode represented by
     * this IdeaNodeView.
     * 
     * @return the modelChildren
     */
    public List<Node<NodeData>> getModelChildren() {
        return this.modelChildren;
    }

    /**
     * Returns the northern touch point for RelationViews calculated at
     * initialization. (For current touch point, see
     * {@link #getTpNorthMarkerPosition()} )
     * 
     * 
     * @return the touchPointNorth
     */
    public Vector3D getTouchPointNorth() {
        return this.touchPointNorth;
    }

    /**
     * Returns the eastern touch point for RelationViews calculated at
     * initialization. (For current touch point, see
     * {@link #getTpEastMarkerPosition()} )
     * 
     * 
     * @return the touchPointEast
     */
    public Vector3D getTouchPointEast() {
        return this.touchPointEast;
    }

    /**
     * Returns the southern touch point for RelationViews calculated at
     * initialization. (For current touch point, see
     * {@link #getTpSouthMarkerPosition()} )
     * 
     * @return the touchPointSouth
     */
    public Vector3D getTouchPointSouth() {
        return this.touchPointSouth;
    }

    /**
     * Returns the calculated western touch point for RelationViews calculated
     * at initialization. (For current touch point, see
     * {@link #getTpWestMarkerPosition()} )
     * 
     * 
     * @return the touchPointWest
     */
    public Vector3D getTouchPointWest() {
        return this.touchPointWest;
    }

    /**
     * Returns the touch point marker container instance.
     * 
     * @return the tpMarkerContainer
     */
    public MarkerContainer getTpMarkerContainer() {
        return this.tpMarkerContainer;
    }

    /* *************Delegates************** */

    /**
     * Returns the text of the IdeaNodeView via the NodeContentContainer.
     * 
     * @return the text from one of the text areas
     */
    public String getNodeContentText() {
        return getNodeContentView().getNodeContentText();
    }

    /**
     * Returns the current position of the northern touch point marker via the
     * MarkerContainer.
     * 
     * @return the position
     */
    public Vector3D getTpNorthMarkerPosition() {
        return getTpMarkerContainer().getTpNorthMarkerGlobalPosition();
    }

    /**
     * Returns the current position of the southern touch point marker via the
     * MarkerContainer.
     * 
     * @return the position
     */
    public Vector3D getTpSouthMarkerPosition() {
        return getTpMarkerContainer().getTpSouthMarkerGlobalPosition();
    }

    /**
     * Returns the current position of the western touch point marker via the
     * MarkerContainer.
     * 
     * @return the position
     */
    public Vector3D getTpWestMarkerPosition() {
        return getTpMarkerContainer().getTpWestMarkerGlobalPosition();
    }

    /**
     * Returns the current position of the eastern touch point marker via the
     * MarkerContainer.
     * 
     * @return the position
     */
    public Vector3D getTpEastMarkerPosition() {
        return getTpMarkerContainer().getTpEastMarkerGlobalPosition();
    }

    /* **********Object methods********** */
    /**
     * Initializes IdeaNodeView object. Sets the default height and width,
     * initializes and sets style infos, adds a background texture, sets
     * composite status, adds new NodeContentContainer, adds/removes gesture
     * listeners, opens keyboard.
     * 
     */
    private void initialize() {

        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Set calculated size
        reSetCalculatedSize();

        // Calculate touch points as markers for future reference
        calculateAndSetTouchPoints(this);

        // Set style info
        this.setStyleInfo(IDEA_NODE_STYLE_INFO);

        // Add background image
        addBackgroundImage();

        // Remove scaling gesture listener
        ToolsEventHandling.removeScaleProcessorsAndListeners(this);

        // Set custom drag processor
        addCustomDragProcessors();

        // Set custom rotate processor
        addCustomRotateProcessor();

        // Modify to allow bubbling from NodeContentContainer and Keyboard
        ToolsEventHandling.allowBubbledEventsForAllProcessors(this);

        // DEBUG: get Position Info
        // ToolsLogging.getPositionInfoForRectangle(this);

        // Add NodeContentContainer
        this.nodeContentView = new NodeContentContainer(
                this.abstractMTapplication, this, this.ideaNodeTextToSet);
        this.addChild(this.nodeContentView);

        // Add observer for watching model IdeaNode changes
        this.modelIdeaNode.addObserver(this);

        log.debug("Leaving initialize()"); //$NON-NLS-1$
    }

    /**
     * Sets the IdeaNodeView size calculated at application start. (Sizing at
     * initialization does not work properly)
     * 
     */
    private void reSetCalculatedSize() {
        log.debug("Entering reSetCalculatedSize()"); //$NON-NLS-1$

        if (getIdeaNodeWidth() > 0.0 && getIdeaNodeHeight() > 0.0) {

            // Set IdeaNodeView size
            this.setWidthLocal(getIdeaNodeWidth());
            this.setHeightLocal(getIdeaNodeHeight());
        } else {
            log.error("Invalid size assigned at initialization! (null)"); //$NON-NLS-1$
        }

        log.debug("Leaving reSetCalculatedSize()"); //$NON-NLS-1$

    }

    /**
     * Calculates the four touch points that a future child RelationView can
     * attach to on initialization(!) of the IdeaNodeView.
     * 
     * @param ideaNodeView
     *            the ideaNodeView we want to calculate the touch points for
     * 
     * @return true, if calculation and setting successful
     */
    private boolean calculateAndSetTouchPoints(IdeaNodeView ideaNodeView) {

        log.debug("Entering calculateAndSetTouchPoints(ideaNodeView=" + ideaNodeView + ""); //$NON-NLS-1$ //$NON-NLS-2$

        // Check if ideaNodeView is valid
        if (ideaNodeView != null) {

            Vector3D tpNorth = null;
            Vector3D tpSouth = null;
            Vector3D tpWest = null;
            Vector3D tpEast = null;

            tpNorth = ToolsComponent.getIdeaNodeViewBoundsTouchPoint(
                    this.abstractMTapplication, ideaNodeView,
                    ETouchPointLocation.NORTH);
            tpSouth = ToolsComponent.getIdeaNodeViewBoundsTouchPoint(
                    this.abstractMTapplication, ideaNodeView,
                    ETouchPointLocation.SOUTH);
            tpWest = ToolsComponent.getIdeaNodeViewBoundsTouchPoint(
                    this.abstractMTapplication, ideaNodeView,
                    ETouchPointLocation.WEST);
            tpEast = ToolsComponent.getIdeaNodeViewBoundsTouchPoint(
                    this.abstractMTapplication, ideaNodeView,
                    ETouchPointLocation.EAST);

            // If all touch points are valid
            if (tpNorth != null && tpSouth != null && tpEast != null
                    && tpWest != null) {

                log.debug("Leaving calculateAndSetTouchPoints(): Intersection points are: North: " //$NON-NLS-1$
                        + tpNorth
                        + ", south: " + tpSouth + ", west: " + tpWest + ", east: " + tpEast); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

                // Add new marker container with touch points
                this.tpMarkerContainer = new MarkerContainer(
                        this.abstractMTapplication, this, tpNorth, tpSouth,
                        tpEast, tpWest);

                // Add to this IdeaNodeView
                this.addChild(this.tpMarkerContainer);

                return true;
            }

        }
        log.error("Leaving calculateAndSetTouchPoints(): false, invalid null input"); //$NON-NLS-1$
        return false;

    }

    /**
     * Adds a background image to the IdeaNodeView.
     * 
     */
    private void addBackgroundImage() {

        log.debug("Entering addBackgroundImage()"); //$NON-NLS-1$

        // Create Svg image
        MTSvg backgroundSvg = new MTSvg(this.abstractMTapplication,
                IDEANODE_BG_FILE_PATH);
        log.trace("Background SVG height relToParent: " //$NON-NLS-1$
                + backgroundSvg.getHeightXYGlobal());
        log.trace("Background SVG width relToParent: " //$NON-NLS-1$
                + backgroundSvg.getWidthXYGlobal());

        // Create background image with svg
        MTBackgroundImage background = new MTBackgroundImage(
                this.abstractMTapplication, backgroundSvg, true, false);

        log.trace("Background height before relToParent: " //$NON-NLS-1$
                + background.getHeightXY(TransformSpace.GLOBAL));
        log.trace("Background width before relToParent: " //$NON-NLS-1$
                + background.getWidthXY(TransformSpace.GLOBAL));

        // Correct background size and position (keep this order!!)
        background.setHeightXYGlobal(getIdeaNodeHeight());
        background.setWidthXYGlobal(getIdeaNodeWidth());

        background.setPositionGlobal(this.getPosition(TransformSpace.GLOBAL));

        log.trace("Background height after relToParent: " //$NON-NLS-1$
                + background.getHeightXY(TransformSpace.GLOBAL));
        log.trace("Background width after relToParent: " //$NON-NLS-1$
                + background.getWidthXY(TransformSpace.GLOBAL));

        // Add background to IdeaNodeView
        this.addChild(background);

        log.debug("Leaving addBackgroundImage()"); //$NON-NLS-1$

    }

    /**
     * Resets the IdeaNodeView after a has been closed/destroyed.
     * 
     */
    private void resetIdeaNodeView() {

        log.debug("Entering resetIdeaNodeView()"); //$NON-NLS-1$

        // Remove carets from text areas
        this.nodeContentView.getNodeTextAreaNorth().setEnableCaret(false);
        this.nodeContentView.getNodeTextAreaSouth().setEnableCaret(false);

        log.debug("Leaving resetIdeaNodeView()"); //$NON-NLS-1$

    }

    /**
     * <p>
     * Checks which text area has been tapped and adds a new keyboard to the
     * tapped text area.
     * </p>
     * <p>
     * TODO: Rotate the keyboard depending on the tap position!
     * </p>
     * 
     * @param tapEvent
     *            the tap Event
     * @return true, if one of the text areas has been tapped
     */
    protected boolean addKeyboardToTextField(TapEvent tapEvent) {

        log.debug("Entering addKeyboardToTextField(tapEvent=" + tapEvent + ")"); //$NON-NLS-1$  //$NON-NLS-2$

        // Get the tapping location
        Vector3D vec = tapEvent.getLocationOnScreen();

        // Get text area references from nodeContentView
        MTTextArea textNorth = IdeaNodeView.this.nodeContentView
                .getNodeTextAreaNorth();
        MTTextArea textSouth = IdeaNodeView.this.nodeContentView
                .getNodeTextAreaSouth();

        // Check which text are has been tapped and open keyboard
        if (ToolsGeometry.isPoint2DInPolygon(vec.getX(), vec.getY(),
                textNorth.getVerticesGlobal())) {
            openKeyboard(textNorth, textSouth);

            log.debug("Leaving addKeyboardToTextField(): true"); //$NON-NLS-1$
            return true;
        }
        if (ToolsGeometry.isPoint2DInPolygon(vec.getX(), vec.getY(),
                textSouth.getVerticesGlobal())) {
            openKeyboard(textSouth, textNorth);

            log.debug("Leaving addKeyboardToTextField(): true"); //$NON-NLS-1$
            return true;
        }

        log.error("Leaving addKeyboardToTextField(): false, double tap not directed at any text field"); //$NON-NLS-1$
        return false;

    }

    /**
     * Opens a new keyboard instance on the primaryMtTextArea. Both text areas
     * are added for text input.
     * 
     * @param primaryMtTextArea
     *            the tapped text area
     * @param secondaryMtTextArea
     *            the other text area
     */
    private void openKeyboard(MTTextArea primaryMtTextArea,
            MTTextArea secondaryMtTextArea) {

        log.debug("Entering openKeyboard(primaryMtTextArea=" + primaryMtTextArea + ", secondaryMtTextArea=" //$NON-NLS-1$ //$NON-NLS-2$ 
                + secondaryMtTextArea + ")"); //$NON-NLS-1$ 

        if (IdeaNodeView.this.ideaNodeKeyboard == null
                && primaryMtTextArea != null && secondaryMtTextArea != null) {

            // Add carets to text areas
            primaryMtTextArea.setEnableCaret(true);
            secondaryMtTextArea.setEnableCaret(true);

            // Add a new keyboard as child to the IdeaNodeView
            CustomKeyboard keyboard = new CustomKeyboard(
                    IdeaNodeView.this.abstractMTapplication,
                    CustomKeyboard.KEYBOARD_STANDARD_WIDTH);

            // Add a new keyboardContainer
            KeyboardContainer keyboardContainer = new KeyboardContainer(
                    this.abstractMTapplication);

            addChild(keyboardContainer);

            // Add keyboard to keyboardContainer
            keyboardContainer.addChild(keyboard);

            // Add text input listeners for both MTTextAreas
            keyboard.addTextInputListener(primaryMtTextArea);
            keyboard.addTextInputListener(secondaryMtTextArea);

            // Add state changed listener for the state keyboard destroyed
            keyboard.addStateChangeListener(StateChange.COMPONENT_DESTROYED,
                    new KeyboardListener());

            // Set keyboard position, center under IdeaNodeView
            keyboard.setPositionRelativeToParent(ToolsComponent.calcPos(
                    primaryMtTextArea, keyboard, -(((keyboard
                            .getWidthXY(TransformSpace.LOCAL)) / 2f) - ((this
                            .getWidthXY(TransformSpace.LOCAL) / 2f))),
                    IdeaNodeView.this.getHeightXY(TransformSpace.LOCAL)));

            // TODO: Set keyboard rotation dependent on tap area?

            // Store keyboard reference
            this.ideaNodeKeyboard = keyboard;

        }
        log.debug("Leaving openKeyboard()"); //$NON-NLS-1$

    }

    /**
     * <p>
     * Updates the model IdeaNode with the current position, rotation and text
     * informations. Called before saving the current MindMap.
     * </p>
     * <p>
     * TODO: Modify those changes as Observers? Might be unwise with regards to
     * performance... ?
     * </p>
     * 
     * @return true, if update was successful
     */
    protected boolean updateModelIdeaNode() {

        log.debug("Entering updateModelIdeaNode()"); //$NON-NLS-1$

        log.trace("Text before update: " + this.getNodeContentText() //$NON-NLS-1$
                + ",  model: " + this.getModelIdeaNode().getIdeaText()); //$NON-NLS-1$

        // Update position
        Vector3D posVector = this.getPosition(TransformSpace.GLOBAL);

        if (!getModelIdeaNode().setIdeaPositionX(posVector.getX())) {

            log.error("Leaving updateModelIdeaNode(): false, IdeaNode xPosition not updated!"); //$NON-NLS-1$
            return false;
        }
        if (!getModelIdeaNode().setIdeaPositionY(posVector.getY())) {

            log.error("Leaving updateModelIdeaNode(): false, IdeaNode yPosition not updated!"); //$NON-NLS-1$
            return false;
        }
        // Update rotation in degrees
        if (!getModelIdeaNode().setIdeaRotationInDegrees(
                ToolsComponent.getRotationZInDegrees(this))) {

            log.error("Leaving updateModelIdeaNode(): false, IdeaNode rotation not updated!"); //$NON-NLS-1$
            return false;
        }

        // Update Text
        if (!getModelIdeaNode().setIdeaText(getNodeContentText())) {

            log.error("Leaving updateModelIdeaNode(): false, IdeaNode text not updated!"); //$NON-NLS-1$
            return false;
        }

        log.trace("Text after update: " + this.getNodeContentText() //$NON-NLS-1$
                + ",  model: " + this.getModelIdeaNode().getIdeaText()); //$NON-NLS-1$

        log.debug("Leaving updateModelIdeaNode(): true"); //$NON-NLS-1$

        return true;

    }

    /**
     * <p>
     * Sets the width of the component relative to the parent. Used for
     * animation behavior.
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
    private boolean setWidthRelativeToParentDefault(float width) {

        log.trace("Entering setWidthRelativeToParent(width=" + width + ")"); //$NON-NLS-1$ //$NON-NLS-2$

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

            log.trace("Leaving setWidthRelativeToParent(): true"); //$NON-NLS-1$
            return true;
        }

        log.trace("Leaving setWidthRelativeToParent(): false"); //$NON-NLS-1$ 
        return false;
    }

    /* *********Listener methods********* */
    /**
     * Adds a new animation listener to the IdeaNodeView and starts the
     * animation. Animation consists of maximizing the IdeaNodeView size from 1
     * to its default size.
     * 
     * */
    public void addAnimationListenerDefault() {

        log.debug("Entering addAnimationListenerDefault()"); //$NON-NLS-1$

        // Create new animation
        IAnimation creationAnim = new Animation("IdeaNodeView creation", //$NON-NLS-1$
                new MultiPurposeInterpolator(1,
                        this.getWidthXY(TransformSpace.GLOBAL), 300, 0.2f,
                        0.5f, 1), this);

        // Add Animation listener to animation
        creationAnim.addAnimationListener(new IAnimationListener() {

            @Override
            public void processAnimationEvent(AnimationEvent ae) {
                switch (ae.getId()) {
                    case AnimationEvent.ANIMATION_STARTED:

                    case AnimationEvent.ANIMATION_UPDATED:

                        // Get animation value
                        float currentVal = ae.getAnimation().getValue();

                        // Set StatusMessageDialog width the animation value
                        setWidthRelativeToParentDefault(currentVal);

                        break;
                    case AnimationEvent.ANIMATION_ENDED:

                        // Set the default width
                        IdeaNodeView.this
                                .setWidthXYGlobal(IdeaNodeView.this.ideaNodeWidth);

                        break;
                    default:
                        break;
                }
            }
        });

        // Start Animation
        creationAnim.start();

        log.debug("Leaving addAnimationListenerDefault()"); //$NON-NLS-1$

    }

    /**
     * Adds a new animation listener to the IdeaNodeView. Animation consists of
     * maximizing the IdeaNodeView size to three times the default size, at the
     * end of the animation the IdeaNodeView original size is set and a short
     * tweenTranslate animation performed (to ensure that IdeaNodeViews created
     * on the same position don't pile up)
     * 
     */
    public void addAnimationListenerCreationFromBT() {

        log.debug("Entering addAnimationListenerCreationFromBT()"); //$NON-NLS-1$

        // Create new animation
        IAnimation creationAnim = new Animation(
                "IdeaNodeView creation from BT device", //$NON-NLS-1$
                new MultiPurposeInterpolator(1,
                        (this.getWidthXY(TransformSpace.GLOBAL)) * 3, 600,
                        0.2f, 0.5f, 1), this);

        // Get random coordinates within a radius of the IdeaNodeView size
        final float x = ToolsMath.getRandom(
                -this.getWidthXY(TransformSpace.GLOBAL),
                this.getWidthXY(TransformSpace.GLOBAL));
        final float y = ToolsMath.getRandom(
                -this.getHeightXY(TransformSpace.GLOBAL),
                this.getHeightXY(TransformSpace.GLOBAL));

        // Add Animation listener to animation
        creationAnim.addAnimationListener(new IAnimationListener() {

            @Override
            public void processAnimationEvent(AnimationEvent ae) {
                switch (ae.getId()) {
                    case AnimationEvent.ANIMATION_STARTED:
                    case AnimationEvent.ANIMATION_UPDATED:

                        // Get animation value
                        float currentVal = ae.getAnimation().getValue();

                        // Set StatusMessageDialog width the animation value
                        setWidthRelativeToParentDefault(currentVal);

                        break;
                    case AnimationEvent.ANIMATION_ENDED:

                        // Set the default width
                        IdeaNodeView.this
                                .setWidthXYGlobal(IdeaNodeView.this.ideaNodeWidth);

                        // Tween translate to random coordinate so multiple
                        // IdeaNodeViews don't pile up
                        IdeaNodeView.this.tweenTranslateTo(IdeaNodeView.this
                                .getCenterPointGlobal().getX() + x,
                                IdeaNodeView.this.getCenterPointGlobal().getY()
                                        + y, 0, 400, AniAnimation.CIRC_OUT, 0);

                        break;
                    default:
                        break;
                }
            }
        });

        // Start Animation
        creationAnim.start();

        log.debug("Leaving addAnimationListenerCreationFromBT()"); //$NON-NLS-1$

    }

    /**
     * Adds a custom drag processor to the IdeaNodeView that ignores drag events
     * bubbled up from RelationViews or any other components in the Map
     * hierarchy that is not a part of an IdeaNodeView.
     * 
     */
    private void addCustomDragProcessors() {

        log.debug("Entering addCustomDragProcessors()"); //$NON-NLS-1$

        ToolsEventHandling.removeDragProcessorsAndListeners(this);

        // Add new modified drag processor
        DragProcessorIdeaNodeView dragProcessorMod = new DragProcessorIdeaNodeView(
                this.abstractMTapplication);

        this.registerInputProcessor(dragProcessorMod);

        this.addGestureListener(DragProcessorIdeaNodeView.class,
                new DragActionCheckBorders(this.abstractMTapplication,
                        CHECK_BORDER_OFFSET));

        InertiaDragProcessorIdeaNodeView dragProcessorModFlick = new InertiaDragProcessorIdeaNodeView(
                this.abstractMTapplication);

        this.registerInputProcessor(dragProcessorModFlick);

        this.addGestureListener(InertiaDragProcessorIdeaNodeView.class,
                new InertiaDragActionCheckBorders(200, .95f, 100,
                        this.abstractMTapplication, CHECK_BORDER_OFFSET));

        log.debug("Leaving addCustomDragProcessors()"); //$NON-NLS-1$

    }

    /**
     * Adds a custom rotate processor to the IdeaNodeView that recalculated the
     * parent RelationView's vertices (if applicable)
     * 
     */
    private void addCustomRotateProcessor() {

        log.debug("Entering addCustomRotateProcessor()"); //$NON-NLS-1$

        ToolsEventHandling.removeRotateProcessorsAndListeners(this);

        // Add new modified rotate processor
        RotateProcessor rotateProcessor = new RotateProcessor(
                this.abstractMTapplication);

        this.registerInputProcessor(rotateProcessor);

        this.addGestureListener(RotateProcessor.class,
                new RotateIdeaNodeViewAction(this.abstractMTapplication));

        log.debug("Leaving addCustomRotateProcessor()"); //$NON-NLS-1$

    }

    /* **********Inner classes********** */
    /**
     * <p>
     * The listener interface for receiving keyboard events. The class that is
     * interested in processing a keyboard event implements this interface, and
     * the object created with that class is registered with a component using
     * the component's <code>addKeyboardListener<code> method. When
     * the keyboard event occurs, that object's appropriate
     * method is invoked.
     * <p>
     * 
     * <p>
     * Original source: {@link org.mt4jx.components.visibleComponents.widgets.MTSuggestionTextArea}
     * </p>
     * 
     * <p>
     * Adds functionality to signal the IdeaNodeView to reset certain values.
     * </p>
     * 
     * @see MTSuggestionTextArea
     * 
     */
    private class KeyboardListener implements StateChangeListener {

        /**
         * <p>
         * Invoked when a state change occurs for the registered listeners.
         * </p>
         * <p>
         * Resets the IdeaNodeView after the keyboard has been destroyed.
         * </p>
         * 
         * @see StateChangeListener#stateChanged(StateChangeEvent)
         */

        @Override
        public void stateChanged(StateChangeEvent evt) {
            log.debug("Entering stateChanged(evt=" + evt + ")"); //$NON-NLS-1$ //$NON-NLS-2$

            if (evt.getState() == StateChange.COMPONENT_DESTROYED) {

                log.debug("State changed for ideaNodeKeyboard! Destroyed!"); //$NON-NLS-1$

                // Destroy parent KeyboardContainer
                MTComponent comp = IdeaNodeView.this.ideaNodeKeyboard
                        .getParent();

                if (comp instanceof KeyboardContainer) {
                    comp.destroy();
                }

                // Set reference in IdeaNodeView null
                IdeaNodeView.this.ideaNodeKeyboard = null;

                // Reset IdeaNodeView
                resetIdeaNodeView();
            }

            log.debug("Leaving stateChanged()"); //$NON-NLS-1$ 

        }
    }

    /* ********Overridden methods******** */
    /**
     * Updates the current IdeaNodeView as a result of changes in the model
     * depending on the given ObserverNotificationObject.
     * 
     * @param o
     *            the observed model object that has communicated a change
     * @param arg
     *            an added notification argument (ObserverNotificationObject)
     */
    @Override
    public void update(Observable o, Object arg) {
        log.debug("Entering update(o=" + o + ", arg=" + arg + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // Check if children list has changed
        if (o instanceof IdeaNode && arg instanceof ObserverNotificationObject) {
            IdeaNode parentIdeaNode = (IdeaNode) o;

            // Check if we have the correct model IdeaNode
            if (parentIdeaNode.equals(this.modelIdeaNode)
                    && ((ObserverNotificationObject) arg).getEnumStatus() instanceof EIdeaNodeChangedStatus) {
                EIdeaNodeChangedStatus status = (EIdeaNodeChangedStatus) ((ObserverNotificationObject) arg)
                        .getEnumStatus();

                Object content = ((ObserverNotificationObject) arg)
                        .getContent();

                switch (status) {

                    case IDEA_NODE_CHILD_ADDED:

                        // Add RelationView for parent and child IdeaNodeView
                        if (content instanceof IdeaNode) {

                            // Set model children reference list anew
                            // TODO: obsolete?
                            this.modelChildren = parentIdeaNode.getChildren();

                            // Get notification content (added child IdeanNode)
                            IdeaNode childIdeaNode = (IdeaNode) content;

                            // Find the corresponding IdeaNodeView
                            AbstractScene scene = (AbstractScene) this.abstractMTapplication
                                    .getCurrentScene();
                            if (scene instanceof MindMapScene) {

                                ArrayList<MTComponent> ideaNodeViewList = ((MindMapScene) scene)
                                        .getAllIdeaNodeViews();

                                IdeaNodeView childIdeaNodeView = ((MindMapScene) scene)
                                        .findIdeaNodeViewByIdeaNode(
                                                childIdeaNode, ideaNodeViewList);

                                if (childIdeaNodeView != null) {

                                    log.trace("Child text before relation making: View: " //$NON-NLS-1$
                                            + childIdeaNodeView
                                                    .getNodeContentText()
                                            + ",  model: " //$NON-NLS-1$
                                            + childIdeaNodeView
                                                    .getModelIdeaNode()
                                                    .getIdeaText());

                                    // Create a new RelatioView for the parent
                                    // and
                                    // child IdeaNodeView
                                    RelationView newRelation = ((MindMapScene) scene)
                                            .createRelationView(this,
                                                    childIdeaNodeView);

                                    log.trace("Child text after relation making: View: " //$NON-NLS-1$
                                            + childIdeaNodeView
                                                    .getNodeContentText()
                                            + ",  model: " //$NON-NLS-1$
                                            + childIdeaNodeView
                                                    .getModelIdeaNode()
                                                    .getIdeaText());

                                    if (newRelation == null) {
                                        log.error("Error! Relation view could not be created."); //$NON-NLS-1$ 
                                        // TODO: Close Application, fatal error,
                                        // view
                                        // and model no longer in synch

                                    }

                                } else {
                                    log.error("IdeaNodeView for ideaNode " + childIdeaNode //$NON-NLS-1$
                                            + " could not be found!"); //$NON-NLS-1$ 
                                    // TODO: Close Application, fatal error,
                                    // view and
                                    // model no
                                    // longer in synch
                                }

                            } else {
                                log.error("Initializing a IdeaNodeView on a wrong scene!"); //$NON-NLS-1$
                                // TODO: close application, fatal error

                            }

                        } else {
                            log.error("Wrong update parameters in Observer " + this + "!"); //$NON-NLS-1$ //$NON-NLS-2$
                            // TODO: Close Application, fatal error, view and
                            // model no
                            // longer in synch
                        }

                        break;
                    case IDEA_NODE_CHILD_REMOVED:

                        // Remove RelationView connecting the removed child
                        if (content instanceof IdeaNode) {

                            // Set model children list anew
                            // TODO: obsolete?
                            this.modelChildren = parentIdeaNode.getChildren();

                            // Get notification content (removed child IdeaNode)
                            IdeaNode childIdeaNode = (IdeaNode) content;

                            // Find the corresponding IdeaNodeView
                            AbstractScene scene = (AbstractScene) this.abstractMTapplication
                                    .getCurrentScene();
                            if (scene instanceof MindMapScene) {

                                ArrayList<MTComponent> ideaNodeViewList = ((MindMapScene) scene)
                                        .getAllIdeaNodeViews();

                                IdeaNodeView childIdeaNodeView = ((MindMapScene) scene)
                                        .findIdeaNodeViewByIdeaNode(
                                                childIdeaNode, ideaNodeViewList);

                                if (childIdeaNodeView != null) {

                                    // Get parent RelationView
                                    MTComponent parent = childIdeaNodeView
                                            .getParent();
                                    if (parent instanceof RelationView) {

                                        // Remove RelationView between parent
                                        // and
                                        // child IdeaNodeView
                                        ((MindMapScene) scene)
                                                .removeRelationView((RelationView) parent);

                                    } else {
                                        log.error("The child IdeaNodeView does not have a RelationView parent! No deletion possible!"); //$NON-NLS-1$
                                        // TODO: Close Application, fatal error,
                                        // view and
                                        // model no
                                        // longer in synch
                                    }

                                } else {
                                    log.error("IdeaNodeView for ideaNode " + childIdeaNode //$NON-NLS-1$
                                            + " could not be found!"); //$NON-NLS-1$ 
                                    // TODO: Close Application, fatal error,
                                    // view and
                                    // model no
                                    // longer in synch

                                }

                            }
                        }

                        break;
                    case IDEA_NODE_CHILD_LIST_SET:
                        // Add relations for all children
                        // TODO: disable in model or implement!
                        break;
                    default:
                        log.error("Wrong update parameters in Observer " + this + "!"); //$NON-NLS-1$ //$NON-NLS-2$
                        // TODO: Close Application, fatal error, view and model
                        // no
                        // longer in synch
                        break;
                }
            } else {
                log.error("Wrong update parameters in Observer " + this + "!"); //$NON-NLS-1$ //$NON-NLS-2$
                // TODO: Close Application, fatal error, view and model no
                // longer in synch
            }
        } else {
            log.error("Wrong update parameters in Observer " + this + "!"); //$NON-NLS-1$ //$NON-NLS-2$
            // TODO: Close Application, fatal error, view and model no
            // longer in synch

        }

        log.debug("Leaving update()"); //$NON-NLS-1$
    }

    /**
     * <p>
     * Overridden method processInputEvent from MTComponent. Currently all
     * events are processed and bubbled up (same method to the one in
     * MTComponent). Kept here for possible future changes.
     * </p>
     * 
     * <p>
     * Warning: the overridden method in MTComponent has a lot of TODO s and
     * FIXME s left, that have not been repeated here - beware when updating the
     * framework version!!
     * </p>
     * 
     * @param inEvt
     *            the input Event to be processed
     * @return false (no handling implemented yet)
     * 
     * @see IMTComponent#processInputEvent(MTInputEvent)
     * 
     * 
     */
    @Override
    public boolean processInputEvent(MTInputEvent inEvt) {
        log.trace("Entering processInputEvent()"); //$NON-NLS-1$

        log.trace("Process a new input event" + inEvt //$NON-NLS-1$ 
                + " for this " + this + ", target " //$NON-NLS-1$  //$NON-NLS-2$ 
                + inEvt.getTarget() + "Phase: " //$NON-NLS-1$ 
                + inEvt.getEventPhase());

        // If we are not yet bubbling at the target is this IdeaNodeView
        // set phase AT_TARGET
        if (inEvt.getEventPhase() != MTInputEvent.BUBBLING_PHASE
                && inEvt.getTarget().equals(this) /* && inEvt.bubbles() */) {

            log.trace("Input event is at target!"); //$NON-NLS-1$ 
            inEvt.setEventPhase(MTInputEvent.AT_TARGET);
        }

        MTComponent theParent = this.getParent();

        if (this.isEnabled()) {
            // THIS IS A HACK TO ALLOW Global GESTURE PROCESSORS to send
            // MTGEstureevents TO WORK
            // see overridden method in superclass
            if (inEvt instanceof MTGestureEvent) {
                log.trace("Input event is instance of MTGestureEvent"); //$NON-NLS-1$ 

                // Process
                if (theParent != null) {

                    this.processGestureEvent((MTGestureEvent) inEvt);
                }

            } else {
                log.trace("Input event is NOT an instance of MTGestureEvent"); //$NON-NLS-1$ 

                // Fire the same input event to all of this components'
                // input listeners
                if (theParent != null) {

                    this.dispatchInputEvent(inEvt);
                }

            }
        }

        // If we have bubbles, the propagation has not been stopped
        // and we are at the target component set phase BUBBLING_PHASE
        if (inEvt.getBubbles() && !inEvt.isPropagationStopped()
                && inEvt.getEventPhase() == MTInputEvent.AT_TARGET) {
            inEvt.setEventPhase(MTInputEvent.BUBBLING_PHASE);
            log.trace("Event is in bubbling phase"); //$NON-NLS-1$ 
        }

        // If we have a bubbled event, propagate to the parent component
        if (inEvt.getBubbles() && !inEvt.isPropagationStopped()
                && inEvt.getEventPhase() == MTInputEvent.BUBBLING_PHASE) {
            log.trace("We have bubbles, so we redirect to the parent!"); //$NON-NLS-1$ 

            if (theParent != null) {

                inEvt.setCurrentTarget(theParent);
                theParent.processInputEvent(inEvt);

            }
        }
        log.trace("Leaving processInputEvent()"); //$NON-NLS-1$ 

        return false;
    }

}
