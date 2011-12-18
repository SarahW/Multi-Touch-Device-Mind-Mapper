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
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.StyleInfo;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
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
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;

/**
 * <p>
 * Subclass of the MT4j framework MTKeyboard. Enabled the keyboard to be
 * initialized with a custom width and adds a custom close button as well as
 * different style info.
 * </p>
 * 
 * @author Christopher Ruff (superclass)
 * @author Sarah Will (subclass derivate)
 * @version 1.0
 * 
 */
public class CustomKeyboard extends MTKeyboard {

    protected static Logger        log                          = Logger.getLogger(CustomKeyboard.class);

    /* *** Custom keyboard constants *** */
    /** The keyboard standard width in pixel */
    public static final float      KEYBOARD_STANDARD_WIDTH      = 700;

    /** The keyboard fill color */
    private static final MTColor   KEYBOARD_FILL_COLOR          = MindMapperColors.GREY_SEMI_TRANS;
    /** The keyboard stroke color */
    private static final MTColor   KEYBOARD_STROKE_COLOR        = MindMapperColors.DARK_GREY_SEMI_TRANS;
    /** The keyboard stroke weight */
    private static final float     KEYBOARD_STROKE_WEIGHT       = 1.0f;
    /** The keyboard style info */
    private static final StyleInfo CUSTOM_KEYBOARD_STYLE_INFO   = new StyleInfo(
                                                                        KEYBOARD_FILL_COLOR,
                                                                        KEYBOARD_STROKE_COLOR,
                                                                        true,
                                                                        false,
                                                                        false,
                                                                        KEYBOARD_STROKE_WEIGHT,
                                                                        GL10.GL_TRIANGLE_FAN,
                                                                        (short) 0);

    /* *** Close button constants *** */
    /** Close button svg image path */
    private static final String    CUSTOM_CLOSE_BUTTON_IMG_PATH = MT4jSettings
                                                                        .getInstance()
                                                                        .getDefaultSVGPath()
                                                                        + "Button_Close.svg";            //$NON-NLS-1$

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication  mtApplication;

    /* *** Keyboard *** */
    /**
     * The desired offset from the border that adds to the maximum/minimum
     * position the keyboard can be positioned at.
     */
    private float                  dragCheckBorderOffset        = 0;
    /** The custom initial keyboard width */
    private float                  customKeyboardWidthToSet     = 0.0f;

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new CustomKeyboard.
     * 
     * @param pApplet
     *            the application instance
     * @param width
     *            the custom width
     */
    public CustomKeyboard(PApplet pApplet, float width) {
        super(pApplet);

        log.debug("Executing CustomKeyboard(pApplet=" + pApplet + ", width=" //$NON-NLS-1$//$NON-NLS-2$
                + width + ")"); //$NON-NLS-1$

        // Set members
        this.mtApplication = (AbstractMTApplication) pApplet;
        this.customKeyboardWidthToSet = width;

        // Initialize
        initialize();

    }

    /* ********Getters & Setters******** */
    /**
     * Returns the keyboard width to set.
     * 
     * @return the customKeyboardWidthToSet
     */
    public float getCustomKeyboardWidthToSet() {
        return this.customKeyboardWidthToSet;
    }

    /* **********Object methods********** */
    /**
     * Initializes the custom keyboard.
     * 
     */
    private void initialize() {

        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Remove scale processor
        ToolsEventHandling.removeScaleProcessorsAndListeners(this);

        // Calculate border offset
        calculateBorderOffset();

        // Adds custom drag processor/listener
        addCustomDragListener();

        // Adds custom rotate processor/listener
        addCustomRotateListener();

        // Scale to width
        scaleToWidth();

        // Remove the default close button
        addCustomCloseButton();

        // Set style info
        this.setStyleInfo(CUSTOM_KEYBOARD_STYLE_INFO);

        log.debug("Leaving initialize()"); //$NON-NLS-1$

    }

    /**
     * Calculates the border offset for the keyboard
     */
    private void calculateBorderOffset() {
        log.debug("Entering calculateBorderOffset()"); //$NON-NLS-1$

        // Three thirds of the keyboard height
        this.dragCheckBorderOffset = 0 - (this
                .getHeightXY(TransformSpace.GLOBAL) / 4f);

        log.debug("New keyboard border offset: " + this.dragCheckBorderOffset); //$NON-NLS-1$

        log.debug("Leaving calculateBorderOffset()"); //$NON-NLS-1$

    }

    /**
     * Adds a custom drag listener that prevents dragging the overlay beyond the
     * screen borders.
     */
    private void addCustomDragListener() {

        log.debug("Entering addCustomDragListener()"); //$NON-NLS-1$

        ToolsEventHandling.removeDragProcessorsAndListeners(this);

        DragProcessor dp = new DragProcessor(getRenderer());
        // Add new modified drag processor
        this.registerInputProcessor(new DragProcessor(this.mtApplication));

        this.addGestureListener(DragProcessor.class,
                new DragActionCheckBorders(this.mtApplication,
                        this.dragCheckBorderOffset));

        this.addGestureListener(DragProcessor.class,
                new InertiaDragActionCheckBorders(this.mtApplication,
                        this.dragCheckBorderOffset));

        dp.setBubbledEventsEnabled(true);

        log.debug("Leaving addCustomDragListener()"); //$NON-NLS-1$

    }

    /**
     * Adds a custom rotate listener that prevents dragging the keyboard beyond
     * the screen borders through rotating.
     */
    private void addCustomRotateListener() {

        log.debug("Entering addCustomRotateListener()"); //$NON-NLS-1$

        ToolsEventHandling.removeRotateProcessorsAndListeners(this);

        RotateProcessor rp = new RotateProcessor(getRenderer());

        // Add new modified rotate processor
        this.registerInputProcessor(new RotateProcessor(this.mtApplication));

        this.addGestureListener(RotateProcessor.class,
                new RotateActionCheckBorders(this.mtApplication,
                        this.dragCheckBorderOffset));

        rp.setBubbledEventsEnabled(true);

        log.debug("Leaving addCustomRotateListener()"); //$NON-NLS-1$

    }

    /**
     * Scales the custom keyboard to the width assigned at initialization.
     */
    private void scaleToWidth() {

        log.debug("Entering scaleToWidth()"); //$NON-NLS-1$

        if (this.customKeyboardWidthToSet != 0.0f) {

            this.setWidthXYGlobal(this.customKeyboardWidthToSet);

        } else {
            log.error("Invalid custom keyboard width initialized"); //$NON-NLS-1$
        }
        log.debug("Leaving scaleToWidth()"); //$NON-NLS-1$

    }

    /**
     * Removes the default close button and adds a custom close button.
     * 
     */
    private void addCustomCloseButton() {

        log.debug("Entering removeDefaultCloseButton()"); //$NON-NLS-1$

        // Get children
        MTComponent svgButton = this.getChildByName("SVG: " //$NON-NLS-1$
                + MT4jSettings.getInstance().getDefaultSVGPath()
                + "keybClose.svg"); //$NON-NLS-1$

        if (svgButton != null) {
            if (svgButton instanceof MTSvgButton) {

                Vector3D oldButtonCenterPoint = ((MTSvgButton) svgButton)
                        .getCenterPointGlobal();
                float oldButtonWidth = ((MTSvgButton) svgButton)
                        .getWidthXYGlobal();

                // Remove old button
                this.removeChild(svgButton);

                // Create new button
                MTSvgButton newButton = new MTSvgButton(this.mtApplication,
                        CUSTOM_CLOSE_BUTTON_IMG_PATH);

                // Set width
                newButton.setWidthXYGlobal(oldButtonWidth);

                // Set position
                newButton.setPositionGlobal(oldButtonCenterPoint);

                // Set picking behaviour
                newButton
                        .setBoundsPickingBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);

                // Add gesture listener
                newButton.addGestureListener(TapProcessor.class,
                        new IGestureEventListener() {
                            @SuppressWarnings("synthetic-access")
                            @Override
                            public boolean processGestureEvent(MTGestureEvent ge) {
                                TapEvent te = (TapEvent) ge;
                                if (te.isTapped()) {
                                    CustomKeyboard.this.onCloseButtonClicked();
                                }
                                return false;
                            }
                        });

                // Add to custom keyboard
                this.addChild(newButton);

            } else {
                log.error("Found button is not a instance of MTSvgButton"); //$NON-NLS-1$
            }

        } else {
            log.error("Default close button not found!"); //$NON-NLS-1$
        }

        log.debug("Leaving removeDefaultCloseButton()"); //$NON-NLS-1$

    }

}
