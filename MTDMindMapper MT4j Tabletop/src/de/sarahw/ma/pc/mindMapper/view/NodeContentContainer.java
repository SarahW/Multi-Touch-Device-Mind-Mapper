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
import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.StyleInfo;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;

/**
 * <p>
 * (Composite) container class for the contents of the IdeaNodeView.
 * </p>
 * 
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */
@SuppressWarnings("synthetic-access")
public class NodeContentContainer extends MTComponent {

    private static Logger                 log                              = Logger.getLogger(NodeContentContainer.class);

    /* *** NodeContentContainer constants *** */
    /** The default (bigger) font for the nodeContent */
    public static final int               DEFAULT_FONT_SIZE_BIG            = 20;
    /** The smaller font for the nodeContent */
    public static final int               DEFAULT_FONT_SIZE_SMALL          = 14;
    /** The nodeContent font file name */
    public static final String            FONT_FILE_NAME                   = "SansSerif";                                 //$NON-NLS-1$

    /** The nodeContent font file color */
    private static final MTColor          NODE_CONTENT_FONT_COLOR          = MTColor.BLACK;
    /** The nodeContent style info */
    private static final StyleInfo        NODE_CONTENT_STYLE_INFO          = new StyleInfo(
                                                                                   MTColor.WHITE,
                                                                                   MTColor.WHITE,
                                                                                   true,
                                                                                   true,
                                                                                   true,
                                                                                   1.0f,
                                                                                   GL10.GL_TRIANGLE_FAN,
                                                                                   (short) 0);

    /** The maximum number of lines in the nodeContentContainer text fields */
    private static final int              NODE_CONTENT_MAX_NUMBER_OF_LINES = 2;

    // private static final String           DEBUG_IDEANODE_TEXT              = "Test";                                      //$NON-NLS-1$

    /* *** Application *** */
    /** The multitouch application instance */
    private MTApplication                 abstractMTapplication;

    /* *** Relationships *** */
    /** The parent ideaNodeView to the nodeContentContainer */
    private IdeaNodeView                  parentIdeaNodeView;

    /* *** Text *** */
    /** The text field with font switch rotated by 180 degrees */
    private MTTextFieldVarLinesFontSwitch nodeTextAreaNorth;
    /** The text field with font switch */
    private MTTextFieldVarLinesFontSwitch nodeTextAreaSouth;
    /** The nodeContent text to set */
    private String                        nodeContentTextToSet;

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new NodeContentContainer in the given pApplet
     * with a given text.
     * 
     * @param pApplet
     *            the application instance
     * @param parent
     *            the parent IdeaNodView
     * @param text
     *            the text
     */
    public NodeContentContainer(PApplet pApplet, IdeaNodeView parent,
            String text) {
        super(pApplet);

        log.debug("Executing NodeContentContainer(pApplet=" + pApplet //$NON-NLS-1$
                + ", parent=" + parent + ", text=" + text + ")");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 

        // Store application reference
        this.abstractMTapplication = (MTApplication) pApplet;

        // Store parentIdeaNodeView reference
        this.parentIdeaNodeView = parent;

        // Set NodeContent text
        setNodeContentTextToSet(text);

        // Initialize NodeContenView
        initialize();
    }

    /* ********Getters & Setters******** */
    /**
     * Returns the initially set nodeContentText.
     * 
     * @return the nodeContentTextToSet
     */
    public String getNodeContentTextToSet() {
        return this.nodeContentTextToSet;
    }

    /**
     * Sets the initially to set nodeContentText.
     * 
     * @param nodeContentTextToSet
     *            the nodeContentTextToSet to set
     */
    private void setNodeContentTextToSet(String nodeContentTextToSet) {
        this.nodeContentTextToSet = nodeContentTextToSet;
    }

    /**
     * Returns the application.
     * 
     * @return the abstractMTapplication
     */
    public MTApplication getAbstractMTapplication() {
        return this.abstractMTapplication;
    }

    /**
     * Returns the parent IdeaNodeView of the NodeContentContainer.
     * 
     * @return the parentIdeaNodeView
     */
    public IdeaNodeView getParentIdeaNodeView() {
        return this.parentIdeaNodeView;
    }

    /**
     * Returns the nodeTextAreaNorth.
     * 
     * @return the nodeTextAreaNorth
     */
    public MTTextFieldVarLinesFontSwitch getNodeTextAreaNorth() {
        return this.nodeTextAreaNorth;
    }

    /**
     * Returns the nodeTextAreaSouth.
     * 
     * @return the nodeTextAreaSouth
     */
    public MTTextFieldVarLinesFontSwitch getNodeTextAreaSouth() {
        return this.nodeTextAreaSouth;
    }

    /* *************Delegates************** */
    /**
     * Returns the text from one of the text areas.
     * 
     * @return the text from a MTTextAreaTwoLines
     */
    public String getNodeContentText() {
        return this.nodeTextAreaNorth.getText();
    }

    /* *********Utility methods********* */
    /**
     * Initializes the NodeContentContainer object. Sets composite flag,
     * adds/removes listeners and adds text areas.
     */
    private void initialize() {

        log.debug("Entering initialize()"); //$NON-NLS-1$

        // TESTING: Initialize nodeContentTextToSet
        // if (this.nodeContentTextToSet.length() == 0) {
        // setNodeContentTextToSet(DEBUG_IDEANODE_TEXT);
        // }

        // Remove scale processors and listeners
        ToolsEventHandling.removeScaleProcessorsAndListeners(this);

        // Set composite true
        this.setComposite(true);

        // Add double tap gesture listener
        addTapGestureListener(this);

        // Add two text fields to the NodeContentContainer
        addTextAreas();

        log.debug("Leaving initialize()"); //$NON-NLS-1$

    }

    /**
     * Adds two text areas to the NodeContentContainer, mirrored at the axis.
     * 
     */
    private void addTextAreas() {

        log.debug("Entering addTextAreas()"); //$NON-NLS-1$

        // Create two MTTextFields restricted to two lines
        this.nodeTextAreaNorth = new MTTextFieldVarLinesFontSwitch(
                this.abstractMTapplication, 0, 0,
                this.parentIdeaNodeView.getIdeaNodeWidth(),
                (this.parentIdeaNodeView.getIdeaNodeHeight()) * 0.5f,
                NODE_CONTENT_MAX_NUMBER_OF_LINES,
                this.parentIdeaNodeView.getIdeaNodeFontDefault(),
                this.parentIdeaNodeView.getIdeaNodeFontSmall());

        this.nodeTextAreaSouth = new MTTextFieldVarLinesFontSwitch(
                this.abstractMTapplication, 0, 0,
                this.parentIdeaNodeView.getIdeaNodeWidth(),
                (this.parentIdeaNodeView.getIdeaNodeHeight()) * 0.5f,
                NODE_CONTENT_MAX_NUMBER_OF_LINES,
                this.parentIdeaNodeView.getIdeaNodeFontDefault(),
                this.parentIdeaNodeView.getIdeaNodeFontSmall());

        // Set widthLocal
        float ideaNodeWidth = this.parentIdeaNodeView.getIdeaNodeWidth();
        this.nodeTextAreaNorth.setWidthLocal(ideaNodeWidth
                - (ideaNodeWidth * 0.15f));
        this.nodeTextAreaSouth.setWidthLocal(ideaNodeWidth
                - (ideaNodeWidth * 0.15f));

        // Set heightLocal
        float ideaNodeHeightHalf = this.parentIdeaNodeView.getIdeaNodeHeight() / 2f;
        this.nodeTextAreaNorth.setHeightLocal(ideaNodeHeightHalf
                - (ideaNodeHeightHalf * 0.2f));
        this.nodeTextAreaSouth.setHeightLocal(ideaNodeHeightHalf
                - (ideaNodeHeightHalf * 0.2f));

        // Add text fields to NodeContentContainer
        this.addChild(this.nodeTextAreaNorth);
        this.addChild(this.nodeTextAreaSouth);

        // Move southern TextArea to the bottom of the IdeaNodeView
        this.nodeTextAreaSouth.setAnchor(PositionAnchor.UPPER_LEFT);
        this.nodeTextAreaSouth.setPositionRelativeToParent(new Vector3D(
                (ideaNodeWidth * 0.05f), this.nodeTextAreaNorth
                        .getHeightXY(TransformSpace.LOCAL)
                        + (ideaNodeHeightHalf * 0.1f), 0));
        this.nodeTextAreaSouth.setAnchor(PositionAnchor.CENTER);

        // Move northern TextArea for TEXTAREA_N_SPACE_R
        this.nodeTextAreaNorth.setAnchor(PositionAnchor.UPPER_LEFT);
        this.nodeTextAreaNorth.setPositionRelativeToParent(new Vector3D(
                (ideaNodeWidth * 0.05f), (ideaNodeHeightHalf * 0.1f), 0));
        this.nodeTextAreaNorth.setAnchor(PositionAnchor.CENTER);

        // Rotate northern TextArea for 180 degrees around center
        this.nodeTextAreaNorth.rotateZ(
                this.nodeTextAreaNorth.getCenterPointGlobal(), 180);

        // Set text
        this.nodeTextAreaNorth.setText(this.nodeContentTextToSet);
        // this.nodeTextAreaNorth.setText(str);
        this.nodeTextAreaSouth.setText(this.nodeContentTextToSet);

        // Set style info
        this.nodeTextAreaNorth.setStyleInfo(NODE_CONTENT_STYLE_INFO);
        this.nodeTextAreaSouth.setStyleInfo(NODE_CONTENT_STYLE_INFO);

        // Set font color
        this.nodeTextAreaNorth.setFontColor(NODE_CONTENT_FONT_COLOR);
        this.nodeTextAreaSouth.setFontColor(NODE_CONTENT_FONT_COLOR);

        // Set padding
        this.nodeTextAreaNorth.setInnerPaddingLeft(5);
        this.nodeTextAreaNorth.setInnerPaddingTop(2);
        this.nodeTextAreaSouth.setInnerPaddingLeft(5);
        this.nodeTextAreaSouth.setInnerPaddingTop(2);

        log.debug("Leaving addTextAreas()"); //$NON-NLS-1$

    }

    /* *********Listener methods********* */
    /**
     * Adds a new tap gesture listener for double taps on the parent
     * IdeaNodeView.
     * 
     * @param nodeContentView
     *            the NodeContentContainer
     */
    private void addTapGestureListener(NodeContentContainer nodeContentView) {

        log.debug("Entering addTapGestureListener(nodeContentView=" + nodeContentView + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Create new TapProcessor which allows processing of bubble up events
        TapProcessor tp = new TapProcessor(this.abstractMTapplication, 18.0f,
                true);

        // Register new Tap Input Processor for double-tapping
        nodeContentView.registerInputProcessor(tp);

        // Add double tap Gesture Listener
        nodeContentView.addGestureListener(TapProcessor.class,
                new IGestureEventListener() {

                    @Override
                    public boolean processGestureEvent(
                            MTGestureEvent gestureEvent) {

                        log.trace("Leaving processGestureEvent(gestureEvent=" + gestureEvent + ")"); //$NON-NLS-1$ //$NON-NLS-2$

                        TapEvent tapEvent = (TapEvent) gestureEvent;

                        // Double Tap handling
                        if (tapEvent.isDoubleTap()) {
                            log.debug("Recognized Gesture: DOUBLE TAP on IdeaNodeView"); //$NON-NLS-1$

                            // Add a new keyboard to the tapped text field
                            NodeContentContainer.this.getParentIdeaNodeView()
                                    .addKeyboardToTextField(tapEvent);

                            log.trace("Leaving processGestureEvent(): true"); //$NON-NLS-1$

                            return true;
                        }

                        log.trace("Leaving processGestureEvent(): false, no double tap detected"); //$NON-NLS-1$
                        return false;
                    }

                });

        log.debug("Leaving addTapGestureListener()"); //$NON-NLS-1$

    }

}
