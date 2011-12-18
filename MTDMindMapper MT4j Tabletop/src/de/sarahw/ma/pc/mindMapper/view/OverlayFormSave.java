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
import org.mt4j.components.StateChange;
import org.mt4j.components.StateChangeEvent;
import org.mt4j.components.StateChangeListener;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.StyleInfo;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.widgets.buttons.MTSvgButton;
import org.mt4j.components.visibleComponents.widgets.keyboard.MTKeyboard;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GL10;
import org.mt4jx.components.visibleComponents.widgets.MTSuggestionTextArea;

import processing.core.PApplet;
import de.sarahw.ma.pc.mindMapper.ToolsGeneral;
import de.sarahw.ma.pc.mindMapper.model.AppModel;
import de.sarahw.ma.pc.mindMapper.model.MindMap;
import de.sarahw.ma.pc.mindMapper.model.MindMapSerializer;

/**
 * 
 * <p>
 * Class implementing AbstractOverlayForm. Represents the overlay form view for
 * saving the current MindMap. Shows text entry field with the current MindMap
 * name to edit for saving, a save and a cancel button. Text can be edited
 * (within the constraints of file name conventions), tapping the save button
 * will save the current MindMap to a .mindMap file on disc with the given file
 * name. Tapping the cancel button will close the overlay form.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * @see de.sarahw.ma.pc.mindMapper.view.AbstractOverlayForm
 * 
 */
@SuppressWarnings("synthetic-access")
public class OverlayFormSave extends AbstractOverlayForm {

    private static Logger          log                                      = Logger.getLogger(OverlayFormSave.class);

    /* *** Overlay form status message constants *** */
    /** The overlay form status message width in percent of the overlay width */
    private static final float     FORM_STATUS_MSG_WIDTH_TO_OVERLAY_PERCENT = 0.382231f;
    /**
     * The overlay form status message height in percent of the status message
     * width
     */
    private static final float     FORM_STATUS_MSG_HEIGHT_TO_TFW_PERCENT    = 0.3978f;

    /** The overlay form status message x offset in percent of the overlay width */
    private static final float     FORM_STATUS_MSG_X_OFFSET_PERCENT         = 0.076446f;
    /** The overlay form status message y offset in percent of the overlay width */
    private static final float     FORM_STATUS_MSG_Y_OFFSET_PERCENT         = 0.68581f;

    /** The overlay form status message inner padding top */
    private static final int       FORM_STATUS_MSG_INNER_PADDING_TOP        = 0;
    /** The overlay form status message inner padding left */
    private static final int       FORM_STATUS_MSG_INNER_PADDING_LEFT       = 0;

    /** The overlay form status message font file name */
    public static final String     FORM_STATUS_MSG_FONT_FILE_NAME           = "SansSerif";                            //$NON-NLS-1$

    /** The overlay form status message font size */
    public static final int        FORM_STATUS_MSG_FONT_SIZE                = 13;
    // OLD VALUE:
    // public static final int FORM_STATUS_MSG_FONT_SIZE = 11;

    /** The overlay form status message font color */
    private static final MTColor   FORM_STATUS_MSG_FONT_COLOR               = MTColor.MAROON;

    /** The maximum number of lines for the status message */
    private static final int       FORM_STATUS_MSG_MAX_NUMBER_OF_LINES      = 2;

    /** The overlay form style info */
    private static final StyleInfo FORM_STATUS_MSG_STYLE_INFO               = new StyleInfo(
                                                                                    MTColor.WHITE,
                                                                                    MTColor.WHITE,
                                                                                    true,
                                                                                    true,
                                                                                    true,
                                                                                    1.0f,
                                                                                    GL10.GL_TRIANGLE_FAN,
                                                                                    (short) 0);

    /* *** OK Button constants *** */
    /** The OK Button svg image path */
    private static final String    OK_BUTTON_IMG_PATH                       = "Button_PlainDark_OK.svg";              //$NON-NLS-1$

    /** The OK Button width in percent of the overlay width */
    private static final float     OK_BUTTON_WIDTH_SCALE_TO_OVERLAY_PERCENT = 0.384297f;
    /** The OK Button x offset in percent of the overlay width */
    private static final float     OK_BUTTON_X_OFFSET_PERCENT               = 0.53822f;
    /** The OK Button y offset in percent of the overlay height */
    private static final float     OK_BUTTON_Y_OFFSET_PERCENT               = 0.6875f;

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication  mtApplication;
    /** The currently active mindMapScene */
    private MindMapScene           mindMapScene;

    /* *** Form *** */
    /** The keyboard instance attached to the form text field */
    private MTKeyboard             formTextFieldKeyboard;

    /** The status message text field (with variable lines) */
    private MTTextFieldVarLines    formTextFieldStatusMsg;

    /** The status message text field font */
    private IFont                  formStatusMessageFont;

    /**
     * Constructor. Instantiates a new OverlayFormSave instance.
     * 
     * @param pApplet
     *            the application instance
     * @param mindMapScene
     *            the MindMapScene the overlay is added to
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
     * @param formDefaultFont
     *            the default font for the form text field
     * @param formStatusMessageFont
     *            the status message font
     */
    public OverlayFormSave(PApplet pApplet, MindMapScene mindMapScene, float x,
            float y, float width, float height, IFont headlineFont,
            IFont formDefaultFont, IFont formStatusMessageFont) {
        super(pApplet, x, y, width, height, headlineFont, formDefaultFont);

        log.debug("Executing OverlayFormSave(pApplet=" + pApplet //$NON-NLS-1$
                + ", mindMapScene=" + mindMapScene + ", x=" //$NON-NLS-1$ //$NON-NLS-2$
                + x + ", y=" + y + ", width=" + width + ", height=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + height + ", headlineFont" + headlineFont //$NON-NLS-1$
                + ", formDefaultFont" + formDefaultFont //$NON-NLS-1$
                + ", statusMessageFont=" + formStatusMessageFont + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Set app reference
        this.mtApplication = (AbstractMTApplication) pApplet;
        this.mindMapScene = mindMapScene;
        this.formStatusMessageFont = formStatusMessageFont;

        // Initialize
        initialize();

    }

    /* ********Getters & Setters******** */

    /**
     * Returns the form status message text field.
     * 
     * @return the formTextFieldStatusMsg
     */
    public MTTextFieldVarLines getFormTextFieldStatusMsg() {
        return this.formTextFieldStatusMsg;
    }

    /* **********Object methods********** */
    private void initialize() {

        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Set headline text
        setHeadlineText();

        // Add form content / tap behaviour
        addFormText();
        addFormDoubleTapListener();

        // Add status message text field
        addStatusMessageTextField();

        // Add save button
        addSaveButton();

        // Simulate double tap
        simulateDoubleTapOnTextField();

        log.debug("Leaving initialize()"); //$NON-NLS-1$
    }

    /**
     * Sets the OverlayFormSave headline text.
     * 
     */
    private void setHeadlineText() {

        log.debug("Entering setHeadlineText()"); //$NON-NLS-1$

        // Set headline text
        getHeadlineTextField()
                .setText(
                        Messages.getString("OverlayFormSave.setHeadlineText.headlineText0")); //$NON-NLS-1$

        log.debug("Leaving setHeadlineText()"); //$NON-NLS-1$

    }

    /**
     * Adds the current MindMap name to the form text field.
     * 
     */
    private void addFormText() {

        log.debug("Entering addFormText()"); //$NON-NLS-1$

        // Get current scene name
        String appName = this.mindMapScene.getName();

        if (this.getFormTextField() != null) {

            // Set the form text field text
            this.getFormTextField().setText(appName);

        } else {
            log.error("Error: text field instance null"); //$NON-NLS-1$
        }

        log.debug("Leaving addFormText()"); //$NON-NLS-1$

    }

    /**
     * Adds listener to the form text field.
     * 
     */
    private void addFormDoubleTapListener() {

        log.debug("Entering addFormListener()"); //$NON-NLS-1$

        if (this.getFormTextField() != null) {

            // Remove all input processors
            // this.getFormTextField().unregisterAllInputProcessors();

            // Create new TapProcessor
            TapProcessor doubleTapProcessor = new TapProcessor(
                    this.mtApplication, 18.0f, true);

            // Register new Tap Input Processor on text field
            this.getFormTextField().registerInputProcessor(doubleTapProcessor);

            // Add double tap Gesture Listener
            this.getFormTextField().addGestureListener(TapProcessor.class,
                    new IGestureEventListener() {

                        @Override
                        public boolean processGestureEvent(
                                MTGestureEvent gestureEvent) {

                            log.debug("Leaving processGestureEvent(gestureEvent=" + gestureEvent + ")"); //$NON-NLS-1$ //$NON-NLS-2$

                            TapEvent tapEvent = (TapEvent) gestureEvent;

                            // Double Tap handling
                            if (tapEvent.isDoubleTap()) {
                                log.debug("Recognized Gesture: DOUBLE TAP on OverlayFormSave text field"); //$NON-NLS-1$

                                // Add a new keyboard to the tapped text field
                                OverlayFormSave.this.addKeyboardToTextField();

                                log.trace("Leaving processGestureEvent(): true"); //$NON-NLS-1$

                                return true;
                            }

                            log.debug("Leaving processGestureEvent(): false, no double tap detected"); //$NON-NLS-1$
                            return false;
                        }

                    });
        } else {
            log.error("Error: text field instance null"); //$NON-NLS-1$
        }
        log.debug("Leaving addFormListener()"); //$NON-NLS-1$

    }

    /**
     * Adds a status message text field.
     * 
     */
    private void addStatusMessageTextField() {

        log.debug("Entering addStatusMessageTextField()"); //$NON-NLS-1$

        // Get the first vertex of the bounding shape
        if (this.hasBounds()) {

            if (this.formStatusMessageFont != null) {

                Vector3D upperLeftVertex = (this.getBounds().getVectorsGlobal())[0]
                        .getCopy();

                // Get OverlayWidht
                float overlayWidth = this.getWidthXY(TransformSpace.GLOBAL);
                float overlayHeight = this.getHeightXY(TransformSpace.GLOBAL);

                // Create new list
                MTTextFieldVarLines formTextFieldStatusMsg = new MTTextFieldVarLines(
                        this.mtApplication,
                        upperLeftVertex.getX(),
                        upperLeftVertex.getY(),
                        overlayWidth * FORM_STATUS_MSG_WIDTH_TO_OVERLAY_PERCENT,
                        (overlayWidth * FORM_STATUS_MSG_WIDTH_TO_OVERLAY_PERCENT)
                                * FORM_STATUS_MSG_HEIGHT_TO_TFW_PERCENT,
                        FORM_STATUS_MSG_MAX_NUMBER_OF_LINES,
                        this.formStatusMessageFont);

                // Set style info
                formTextFieldStatusMsg.setStyleInfo(FORM_STATUS_MSG_STYLE_INFO);

                // Set font color
                formTextFieldStatusMsg.setFontColor(FORM_STATUS_MSG_FONT_COLOR);

                // Remove all default gesture listeners
                ToolsEventHandling
                        .removeScaleProcessorsAndListeners(formTextFieldStatusMsg);
                ToolsEventHandling
                        .removeDragProcessorsAndListeners(formTextFieldStatusMsg);
                ToolsEventHandling
                        .removeRotateProcessorsAndListeners(formTextFieldStatusMsg);

                // Set inner padding padding x-offset
                formTextFieldStatusMsg
                        .setInnerPaddingLeft(FORM_STATUS_MSG_INNER_PADDING_LEFT);
                formTextFieldStatusMsg
                        .setInnerPaddingTop(FORM_STATUS_MSG_INNER_PADDING_TOP);

                // Add to overlay
                this.addChild(formTextFieldStatusMsg);

                // Reposition to correct position
                formTextFieldStatusMsg.translate(new Vector3D(overlayWidth
                        * FORM_STATUS_MSG_X_OFFSET_PERCENT, overlayHeight
                        * FORM_STATUS_MSG_Y_OFFSET_PERCENT, 0));

                // Set member
                this.formTextFieldStatusMsg = formTextFieldStatusMsg;

            } else {
                log.error("Error! The status message font specified at initialization is invalid! (null)"); //$NON-NLS-1$
            }

        } else {
            log.error("Error! The OverlayFormSave has no bounds!"); //$NON-NLS-1$
        }

        log.debug("Leaving addStatusMessageTextField()"); //$NON-NLS-1$

    }

    /**
     * Adds a save button.
     * 
     */
    private void addSaveButton() {

        log.debug("Entering addSaveButton()"); //$NON-NLS-1$

        if (this.hasBounds()) {

            // Get upper left vertex
            Vector3D upperLeftVertex = (this.getBounds().getVectorsGlobal())[0]
                    .getCopy();

            // Create save Button
            MTSvgButton saveButton = new MTSvgButton(this.mtApplication,
                    MT4jSettings.getInstance().getDefaultSVGPath()
                            + OK_BUTTON_IMG_PATH);

            // Get overlay width and height
            float overlayWidth = this.getWidthXY(TransformSpace.GLOBAL);
            float overlayHeight = this.getHeightXY(TransformSpace.GLOBAL);

            // Set width
            saveButton.setWidthXYGlobal(overlayWidth
                    * OK_BUTTON_WIDTH_SCALE_TO_OVERLAY_PERCENT);

            // Remove all default gesture listeners
            ToolsEventHandling.removeScaleProcessorsAndListeners(saveButton);
            ToolsEventHandling.removeDragProcessorsAndListeners(saveButton);
            ToolsEventHandling.removeRotateProcessorsAndListeners(saveButton);

            // Set bounds picking behaviour
            saveButton
                    .setBoundsPickingBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);

            // Add tap gesture listener
            addTapGestureListener(saveButton);

            // Add to OverlayFormSave instance
            this.addChild(saveButton);

            // Position at upper vertex
            saveButton.setPositionGlobal(upperLeftVertex);

            // Reposition to correct position
            saveButton.translate(new Vector3D(
                    (overlayWidth * OK_BUTTON_X_OFFSET_PERCENT)
                            + saveButton.getWidthXYGlobal() / 2f,
                    (overlayHeight * OK_BUTTON_Y_OFFSET_PERCENT)
                            + saveButton.getHeightXYGlobal() / 2f, 0));

            // Add text field to button
            // TODO? Right now, we have a graphical button

        } else {
            log.error("Error! The OverlayFormSave has no bounds!"); //$NON-NLS-1$
        }

        log.debug("Leaving addSaveButton()"); //$NON-NLS-1$

    }

    /**
     * Simulates a double tap input on the Form text field to open the keyboard
     * at the end of the OverlayFormSave initialization. creation.
     * 
     */
    private void simulateDoubleTapOnTextField() {

        log.debug("Entering simulateDoubleTapOnTextField()"); //$NON-NLS-1$

        if (this.getFormTextField() != null) {

            // Create new double tap TapProcessor
            TapProcessor simulateTap = new TapProcessor(this.mtApplication,
                    18.0f, true);

            // Create fake cursor
            InputCursor simulateCursor = new InputCursor();

            // Create fake gesture event for double tap
            TapEvent gestEv = new TapEvent(simulateTap, TapEvent.DOUBLE_TAPPED,
                    this.getFormTextField(), simulateCursor, this
                            .getFormTextField().getCenterPointGlobal(),
                    TapEvent.DOUBLE_TAPPED);

            // Process fake double tap by ideaNodeView
            this.getFormTextField().processInputEvent(gestEv);

            log.debug("Leaving simulateDoubleTapOnTextField()"); //$NON-NLS-1$
            return;

        }
        log.error("Leaving simulateDoubleTapOnTextField(): invalid null input!"); //$NON-NLS-1$

    }

    /**
     * Resets the OverlayFormSave form text field after a keyboard has been
     * closed/destroyed.
     * 
     */
    public void resetFormTextField() {
        log.debug("Entering resetFormTextField()"); //$NON-NLS-1$

        // Remove carets from text areas
        this.getFormTextField().setEnableCaret(false);

        log.debug("Leaving resetFormTextField()"); //$NON-NLS-1$
    }

    /**
     * Adds a new tap gesture listener to the save button.
     * 
     * @param button
     *            the SVG Button instance
     */
    private void addTapGestureListener(MTSvgButton button) {

        log.debug("Entering addTapGestureListener(button=" + button + ")"); //$NON-NLS-1$ //$NON-NLS-2$ 

        button.addGestureListener(TapProcessor.class,
                new IGestureEventListener() {

                    @Override
                    public boolean processGestureEvent(MTGestureEvent ge) {
                        TapEvent te = (TapEvent) ge;
                        if (te.isTapped()) {
                            log.trace("Save Button has been tapped!"); //$NON-NLS-1$
                            onSaveButtonTapped();
                        }
                        return false;
                    }

                });

        log.debug("Leaving addTapGestureListener()"); //$NON-NLS-1$

    }

    /**
     * Method called when the OK/Save button has been tapped. Checks the
     * filename for invalid characters or invalid length and saves the current
     * loaded mindMap under the specified file name.
     */
    protected void onSaveButtonTapped() {

        log.debug("Entering onSaveButtonTapped()"); //$NON-NLS-1$

        // Check if text is too long
        if (this.getFormTextField().getText().length() > MindMapSerializer.MAX_FILENAME_LENGTH) {

            log.debug("The filename is too long, maximum length: " + MindMapSerializer.MAX_FILENAME_LENGTH); //$NON-NLS-1$

            // Show status message "Text too long"
            this.getFormTextFieldStatusMsg()
                    .setText(
                            Messages.getString("OverlayFormSave.onSaveButtonTapped.textTooLong.part0") //$NON-NLS-1$
                                    + MindMapSerializer.MAX_FILENAME_LENGTH
                                    + Messages
                                            .getString("OverlayFormSave.onSaveButtonTapped.textTooLong.part1")); //$NON-NLS-1$

        } else if (!ToolsGeneral.isValid(this.getFormTextField().getText())) {

            log.debug("The filename is not valid, invalid characters!"); //$NON-NLS-1$

            // Show status message "invalid chars"
            this.getFormTextFieldStatusMsg()
                    .setText(
                            Messages.getString("OverlayFormSave.onSaveButtonTapped.invalidChars.0")); //$NON-NLS-1$

        } else {

            // Clear status message
            this.getFormTextFieldStatusMsg().setText(""); //$NON-NLS-1$

            // Save MindMap
            log.debug("MindMap saved with " + this.getFormTextField().getText()); //$NON-NLS-1$

            // Get the model
            AppModel model = this.mindMapScene.getModelReference();

            if (model != null) {

                // Get the currently loaded mindMap
                MindMap loadedMindMap = model.getLoadedMindMap();

                if (loadedMindMap != null) {

                    // Set mindMap title anew
                    loadedMindMap.setMindMapTitle(this.getFormTextField()
                            .getText());

                    // Start mindMap saving process
                    this.mindMapScene.startSaveMindMapProcess();

                    // Close this Overlay
                    this.destroy();

                } else {
                    log.error("Error: loaded mind map reference is null!"); //$NON-NLS-1$
                    // TODO close app
                }

            } else {
                log.error("Error: model reference is null!"); //$NON-NLS-1$
                // TODO close app
            }

        }

        // Check if text contains characters that are not allowed for file names

        log.debug("Leaving onSaveButtonTapped()"); //$NON-NLS-1$

    }

    /**
     * Adds a MTKeyboard to the form text field.
     * 
     */
    protected void addKeyboardToTextField() {

        log.debug("Entering addKeyboardToTextField()"); //$NON-NLS-1$

        // Only add if keyboard isn't already opened
        if (this.formTextFieldKeyboard == null) {

            // Add carets to text areas
            this.getFormTextField().setEnableCaret(true);

            // Add a new keyboard as child to the OverlayFormSave
            CustomKeyboard keyboard = new CustomKeyboard(this.mtApplication,
                    CustomKeyboard.KEYBOARD_STANDARD_WIDTH);
            this.addChild(keyboard);

            // Add text input listeners for the text field
            keyboard.addTextInputListener(this.getFormTextField());

            // Add state changed listener for the state keyboard destroyed
            keyboard.addStateChangeListener(StateChange.COMPONENT_DESTROYED,
                    new KeyboardListener());

            // Set keyboard position, center under OverlayFormSave
            keyboard.setPositionRelativeToParent(ToolsComponent.calcPos(
                    this,
                    keyboard,
                    -(((keyboard.getWidthXY(TransformSpace.LOCAL)) / 2f) - ((this
                            .getWidthXY(TransformSpace.LOCAL) / 2f))), this
                            .getHeightXY(TransformSpace.LOCAL)));

            // Store keyboard reference
            this.formTextFieldKeyboard = keyboard;

        } else {
            log.info("Keyboard already opened."); //$NON-NLS-1$
        }

        log.debug("Leaving addKeyboardToTextField()"); //$NON-NLS-1$

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
                OverlayFormSave.this.formTextFieldKeyboard = null;
                log.debug("State changed for ideaNodeKeyboard! Destroyed!"); //$NON-NLS-1$
                resetFormTextField();
            }

            log.debug("Leaving stateChanged()"); //$NON-NLS-1$ 

        }

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

        // Set in null in mindMap
        if (this.mindMapScene != null) {

            // Set saveOverlay null
            this.mindMapScene.setSaveMindMapOverlayForm(null);

        } else {
            log.error("Error: MindMap provided at initialization is null!"); //$NON-NLS-1$
            // TODO: close app
        }

        super.destroy();

        log.debug("Leaving destroy()"); //$NON-NLS-1$

    }

}
