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
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.widgets.buttons.MTSvgButton;
import org.mt4j.components.visibleComponents.widgets.keyboard.MTKeyboard;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.animation.Animation;
import org.mt4j.util.animation.AnimationEvent;
import org.mt4j.util.animation.IAnimation;
import org.mt4j.util.animation.IAnimationListener;
import org.mt4j.util.animation.MultiPurposeInterpolator;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * 
 * <p>
 * AbstractStatusMessageDialogYesNo class. Represents an abstract status message
 * view (subclass of AbstractStatusMessageDialog) that contains two
 * MTTextFieldVarLines components with a status message text, two YES buttons
 * and two NO buttons mirrored at the axis to close the status message overlay.
 * Specific button behaviour (except closing the overlay) must be implemented by
 * a subclass.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
@SuppressWarnings("synthetic-access")
public abstract class AbstractStatusMessageDialogYesNo extends
        AbstractStatusMessageDialog {

    private static Logger       log                                            = Logger.getLogger(AbstractStatusMessageDialogYesNo.class);

    /* *** OK Button constants *** */
    /** OK Button svg image name */
    private static final String BUTTON_OK_IMG_NAME                             = "Button_PlainDark_YES.svg";                              //$NON-NLS-1$

    /**
     * OK Button south y offset from text in percent of the status message
     * height
     */
    private static final float  BUTTON_OK_SOUTH_Y_OFFSET_FROM_TEXT_PERCENT     = 0.2f;

    /**
     * OK Button south y offset from text in percent of the status message
     * height (half the status message height)
     */
    private static final float  BUTTON_OK_SOUTH_Y_OFFSET_HALF_PERCENT          = 0.5f;

    /** OK Button north y offset top in percent of the status message height */
    private static final float  BUTTON_OK_NORTH_Y_OFFSET_TOP_PERCENT           = 0.15f;

    /** OK Button width in percent of status message width */
    private static final float  BUTTON_OK_WIDTH_SCALE_TO_STATUSMSG_PERCENT     = 0.155f;
    // OLD VALUE:
    // private static final float BUTTON_OK_WIDTH_SCALE_TO_STATUSMSG_PERCENT =
    // 0.1162f;

    /* *** Cancel Button constants *** */
    /** Cancel button svm image name */
    private static final String BUTTON_CANCEL_IMG_NAME                         = "Button_PlainDark_NO.svg";                               //$NON-NLS-1$

    /**
     * Cancel Button south y offset from text in percent of the status message
     * height
     */
    private static final float  BUTTON_CANCEL_SOUTH_Y_OFFSET_FROM_TEXT_PERCENT = 0.2f;

    /**
     * Cancel Button south y offset from text in percent of the status message
     * height (half the status message height)
     */
    private static final float  BUTTON_CANCEL_SOUTH_Y_OFFSET_HALF_PERCENT      = 0.5f;

    /** Cancel Button north y offset top in percent of the status message height */
    private static final float  BUTTON_CANCEL_NORTH_Y_OFFSET_TOP_PERCENT       = 0.15f;

    /** Cancel Button width in percent of status message width */
    private static final float  BUTTON_CANCEL_WIDTH_SCALE_TO_STATUSMSG_PERCENT = 0.155f;

    // OLD VALUE:
    // private static final float BUTTON_CANCEL_WIDTH_SCALE_TO_STATUSMSG_PERCENT
    // = 0.1162f;

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
    public AbstractStatusMessageDialogYesNo(PApplet pApplet,
            MindMapScene mindMapScene, float x, float y, float width,
            float height, EStatusMessageType statusType, String text,
            IFont fontSmall, IFont fontBig, int numberOfLines) {
        super(pApplet, mindMapScene, x, y, width, height, statusType, text,
                fontSmall, fontBig, numberOfLines);

        log.debug("Executing AbstractStatusMessageDialogYesNo(pApplet=" + pApplet + ", x=" //$NON-NLS-1$ //$NON-NLS-2$
                + x
                + ", mindMapScene=" + mindMapScene + ", y=" + y + ", width=" + width + ", \nheight=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + height
                + ", statusType=" //$NON-NLS-1$
                + statusType
                + ", text=" + text + ", fontSmall=" + fontSmall //$NON-NLS-1$//$NON-NLS-2$
                + ", fontBig=" + fontBig + ", numberOfLines=" + numberOfLines + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // Initialize StatusMessageDialog
        initialize();

    }

    /* *********Utility methods********* */
    /**
     * Initializes the StatusMessageDialog object. Adds/Removes event listeners,
     * and adds text fields and buttons.
     */
    private void initialize() {

        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Add OK and CANCEL buttons
        addOKButtons();

        addCancelButtons();

        log.debug("Leaving initialize()"); //$NON-NLS-1$

    }

    /**
     * Adds OK buttons mirrored at the axis as well as tap processors /
     * listeners.
     * 
     */
    private void addOKButtons() {

        log.debug("Entering addOKButtons()"); //$NON-NLS-1$

        // Create button north
        MTSvgButton btOkSvgNorth = new MTSvgButton(getMtApplication(),
                MT4jSettings.getInstance().getDefaultSVGPath()
                        + BUTTON_OK_IMG_NAME);

        // Remove all default gesture listeners
        ToolsEventHandling.removeScaleProcessorsAndListeners(btOkSvgNorth);
        ToolsEventHandling.removeDragProcessorsAndListeners(btOkSvgNorth);
        ToolsEventHandling.removeRotateProcessorsAndListeners(btOkSvgNorth);

        // Set width
        btOkSvgNorth.setWidthXYGlobal(this.getWidthXY(TransformSpace.GLOBAL)
                * BUTTON_OK_WIDTH_SCALE_TO_STATUSMSG_PERCENT);

        // Transform
        // btOkSvgNorth.scale(0.8f, 0.8f, 1, new Vector3D(0, 0, 0));
        btOkSvgNorth.setBoundsPickingBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);

        // Add tap gesture listener
        addOKTapGestureListener(btOkSvgNorth);

        // Rotate 180 degrees
        btOkSvgNorth.rotateZ(btOkSvgNorth.getCenterPointGlobal(), 180);

        // Add to StatusMessageDialog instance
        this.addChild(btOkSvgNorth);

        float statusMessageHeight = this.getHeightXY(TransformSpace.GLOBAL);

        // Correct position
        btOkSvgNorth
                .setPositionGlobal(new Vector3D(
                        (this.getCenterPointGlobal().getX() + this
                                .getWidthXY(TransformSpace.GLOBAL) / 8f),
                        (statusMessageHeight * BUTTON_OK_NORTH_Y_OFFSET_TOP_PERCENT),
                        0));

        // Create button south
        MTSvgButton btOkSvgSouth = new MTSvgButton(getMtApplication(),
                MT4jSettings.getInstance().getDefaultSVGPath()
                        + BUTTON_OK_IMG_NAME);

        // Remove all default gesture listeners
        ToolsEventHandling.removeScaleProcessorsAndListeners(btOkSvgSouth);
        ToolsEventHandling.removeDragProcessorsAndListeners(btOkSvgSouth);
        ToolsEventHandling.removeRotateProcessorsAndListeners(btOkSvgSouth);

        // Set width
        btOkSvgSouth.setWidthXYGlobal(this.getWidthXY(TransformSpace.GLOBAL)
                * BUTTON_OK_WIDTH_SCALE_TO_STATUSMSG_PERCENT);

        // Transform
        // btOkSvgSouth.scale(0.8f, 0.8f, 1, new Vector3D(0, 0, 0));
        btOkSvgSouth.setBoundsPickingBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);

        // Add tap gesture listener
        addOKTapGestureListener(btOkSvgSouth);

        // Add to this StatusMessageDialog instance
        this.addChild(btOkSvgSouth);

        // Correct position
        btOkSvgSouth
                .setPositionGlobal(new Vector3D(
                        (this.getCenterPointGlobal().getX() - this
                                .getWidthXY(TransformSpace.GLOBAL) / 8f),
                        (statusMessageHeight * BUTTON_OK_NORTH_Y_OFFSET_TOP_PERCENT)
                                + (statusMessageHeight * BUTTON_OK_SOUTH_Y_OFFSET_HALF_PERCENT)
                                + (statusMessageHeight * BUTTON_OK_SOUTH_Y_OFFSET_FROM_TEXT_PERCENT),
                        0));

        log.debug("Leaving addOKButtons()"); //$NON-NLS-1$

    }

    /**
     * Adds cancel buttons mirrored at the axis as well as tap processors /
     * listeners.
     */
    private void addCancelButtons() {

        log.debug("Entering addCancelButtons()"); //$NON-NLS-1$

        // Create button north
        MTSvgButton btOkSvgNorth = new MTSvgButton(getMtApplication(),
                MT4jSettings.getInstance().getDefaultSVGPath()
                        + BUTTON_CANCEL_IMG_NAME);

        // Remove all default gesture listeners
        ToolsEventHandling.removeScaleProcessorsAndListeners(btOkSvgNorth);
        ToolsEventHandling.removeDragProcessorsAndListeners(btOkSvgNorth);
        ToolsEventHandling.removeRotateProcessorsAndListeners(btOkSvgNorth);

        // Set width
        btOkSvgNorth.setWidthXYGlobal(this.getWidthXY(TransformSpace.GLOBAL)
                * BUTTON_CANCEL_WIDTH_SCALE_TO_STATUSMSG_PERCENT);

        // Transform
        // btOkSvgNorth.scale(0.8f, 0.8f, 1, new Vector3D(0, 0, 0));
        btOkSvgNorth.setBoundsPickingBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);

        // Add tap gesture listener
        addCancelTapGestureListener(btOkSvgNorth);

        // Rotate 180 degrees
        btOkSvgNorth.rotateZ(btOkSvgNorth.getCenterPointGlobal(), 180);

        // Add to StatusMessageDialog instance
        this.addChild(btOkSvgNorth);

        float statusMessageHeight = this.getHeightXY(TransformSpace.GLOBAL);

        // Correct position
        btOkSvgNorth
                .setPositionGlobal(new Vector3D(
                        (this.getCenterPointGlobal().getX() - this
                                .getWidthXY(TransformSpace.GLOBAL) / 8f),
                        (statusMessageHeight * BUTTON_CANCEL_NORTH_Y_OFFSET_TOP_PERCENT),
                        0));

        // Create button south
        MTSvgButton btOkSvgSouth = new MTSvgButton(getMtApplication(),
                MT4jSettings.getInstance().getDefaultSVGPath()
                        + BUTTON_CANCEL_IMG_NAME);

        // Remove all default gesture listeners
        ToolsEventHandling.removeScaleProcessorsAndListeners(btOkSvgSouth);
        ToolsEventHandling.removeDragProcessorsAndListeners(btOkSvgSouth);
        ToolsEventHandling.removeRotateProcessorsAndListeners(btOkSvgSouth);

        // Set width
        btOkSvgSouth.setWidthXYGlobal(this.getWidthXY(TransformSpace.GLOBAL)
                * BUTTON_CANCEL_WIDTH_SCALE_TO_STATUSMSG_PERCENT);

        // Transform
        // btOkSvgSouth.scale(0.8f, 0.8f, 1, new Vector3D(0, 0, 0));
        btOkSvgSouth.setBoundsPickingBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);

        // Add tap gesture listener
        addCancelTapGestureListener(btOkSvgSouth);

        // Add to this StatusMessageDialog instance
        this.addChild(btOkSvgSouth);

        // Correct position
        btOkSvgSouth
                .setPositionGlobal(new Vector3D(
                        (this.getCenterPointGlobal().getX() + this
                                .getWidthXY(TransformSpace.GLOBAL) / 8f),
                        (statusMessageHeight * BUTTON_CANCEL_NORTH_Y_OFFSET_TOP_PERCENT)
                                + (statusMessageHeight * BUTTON_CANCEL_SOUTH_Y_OFFSET_HALF_PERCENT)
                                + (statusMessageHeight * BUTTON_CANCEL_SOUTH_Y_OFFSET_FROM_TEXT_PERCENT),
                        0));

        log.debug("Leaving addCancelButtons()"); //$NON-NLS-1$

    }

    /* *********Listener methods********* */
    /**
     * Adds a new tap gesture listener to a svg button for the OK action.
     * 
     * @param svgButton
     *            the SVG Button instance
     */
    private void addOKTapGestureListener(MTSvgButton svgButton) {

        log.debug("Entering addTapGestureListener(svgButton=" + svgButton + ")"); //$NON-NLS-1$ //$NON-NLS-2$ 

        svgButton.addGestureListener(TapProcessor.class,
                new IGestureEventListener() {

                    @Override
                    public boolean processGestureEvent(MTGestureEvent ge) {
                        TapEvent te = (TapEvent) ge;
                        if (te.isTapped()) {
                            log.trace("OK Button has been tapped!"); //$NON-NLS-1$
                            onOkButtonTapped();
                        }
                        return false;
                    }

                });

        log.debug("Leaving addTapGestureListener()"); //$NON-NLS-1$

    }

    /**
     * Adds a new tap gesture listener to a svg button for the OK action.
     * 
     * @param svgButton
     *            the SVG Button instance
     */
    private void addCancelTapGestureListener(MTSvgButton svgButton) {

        log.debug("Entering addTapGestureListener(svgButton=" + svgButton + ")"); //$NON-NLS-1$ //$NON-NLS-2$ 

        svgButton.addGestureListener(TapProcessor.class,
                new IGestureEventListener() {
                    @Override
                    public boolean processGestureEvent(MTGestureEvent ge) {
                        TapEvent te = (TapEvent) ge;
                        if (te.isTapped()) {
                            log.trace("OK Button has been tapped!"); //$NON-NLS-1$
                            onCancelButtonTapped();
                        }
                        return false;
                    }

                });

        log.debug("Leaving addTapGestureListener()"); //$NON-NLS-1$

    }

    /**
     * <p>
     * Shows a disappearing animation, closes the StatusMessageDialog instance
     * and calls the OK button tapped action.
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
    private void onOkButtonTapped() {

        log.debug("Entering onOkButtonTapped()"); //$NON-NLS-1$  

        // Get width
        float width = this.getWidthXY(TransformSpace.RELATIVE_TO_PARENT);

        // Create new animation
        IAnimation statusMsgClickAnim = new Animation(
                "Status Message Fade", new MultiPurposeInterpolator(width, 1, 300, 0.2f, 0.5f, 1), this); //$NON-NLS-1$

        // Add Animation listener to animation
        statusMsgClickAnim.addAnimationListener(new IAnimationListener() {
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

                        // Set StatusMessageDialog instance invisible and
                        // destroy
                        setVisible(false);

                        onOKButtonTappedAction();
                        destroy();

                        break;
                    default:
                        break;
                }
            }
        });

        // Start Animation
        statusMsgClickAnim.start();

        log.debug("Leaving onOkButtonTapped()"); //$NON-NLS-1$  

    }

    /**
     * <p>
     * Shows a disappearing animation, closes the StatusMessageDialog instance
     * and calls the cancel button action.
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
    protected void onCancelButtonTapped() {
        log.debug("Entering onCancelButtonTapped()"); //$NON-NLS-1$  

        // Get width
        float width = this.getWidthXY(TransformSpace.RELATIVE_TO_PARENT);

        // Create new animation
        IAnimation statusMsgClickAnim = new Animation(
                "Status Message Fade", new MultiPurposeInterpolator(width, 1, 300, 0.2f, 0.5f, 1), this); //$NON-NLS-1$

        // Add Animation listener to animation
        statusMsgClickAnim.addAnimationListener(new IAnimationListener() {
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

                        // Set StatusMessageDialog instance invisible and
                        // destroy
                        setVisible(false);

                        // Call on cancel button tapped action
                        onCancelButtonTappedAction();

                        destroy();

                        break;
                    default:
                        break;
                }
            }
        });

        // Start Animation
        statusMsgClickAnim.start();

        log.debug("Leaving onCancelButtonTapped()"); //$NON-NLS-1$  

    }

    /* *********Abstract methods********* */
    /**
     * Abstract method invoked when the OK button is tapped. To be implemented
     * by subclass.
     * 
     */
    abstract protected void onOKButtonTappedAction();

    /**
     * Abstract method invoked when the cancel button is tapped. To be
     * implemented by subclass.
     * 
     */
    abstract protected void onCancelButtonTappedAction();

}
