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

import javax.swing.text.DefaultEditorKit.DefaultKeyTypedAction;

import org.apache.log4j.Logger;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.gestureAction.DefaultArcballAction;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.gestureAction.DefaultLassoAction;
import org.mt4j.input.gestureAction.DefaultPanAction;
import org.mt4j.input.gestureAction.DefaultRotateAction;
import org.mt4j.input.gestureAction.DefaultScaleAction;
import org.mt4j.input.gestureAction.DefaultSvgButtonClickAction;
import org.mt4j.input.gestureAction.DefaultZoomAction;
import org.mt4j.input.gestureAction.InertiaDragAction;
import org.mt4j.input.gestureAction.Rotate3DAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.globalProcessors.AbstractGlobalInputProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.input.inputProcessors.globalProcessors.GlobalUnistrokeProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.RawFiducialProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.RawFingerProcessor;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.math.Vector3D;
import org.mt4jx.input.gestureAction.DefaultDepthAction;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.FingerTapGrouping.FingerTapSelectionManager;

/**
 * Collects commonly used logging methods for debugging.
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class ToolsLogging {

    protected static Logger log = Logger.getLogger(ToolsLogging.class);

    /* ***********Constructors*********** */
    /**
     * Private constructor. Doesn't allow instances of this class.
     */
    private ToolsLogging() {
        //
    }

    /* ***********Class methods*********** */
    /**
     * Debug method for logging Position info of an mtRectangle
     * 
     * @param mtRectangle
     *            the rectangle
     */
    public static void getPositionInfoForRectangle(MTRectangle mtRectangle) {

        log.debug("Entering getPositionInfoForRectangle(mtRectangle=" + mtRectangle + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Get current position in relation to global
        Vector3D positionVector = mtRectangle
                .getPosition(TransformSpace.GLOBAL);

        log.debug("Position Vector of IdeaNodeView Global: " + positionVector); //$NON-NLS-1$

        // Get current position in relation to parent
        Vector3D positionVectorParent = mtRectangle
                .getPosition(TransformSpace.RELATIVE_TO_PARENT);

        log.debug("Position Vector of IdeaNodeView Relative to Parent: " + positionVectorParent); //$NON-NLS-1$

        // Get current position local
        Vector3D positionVectorLocal = mtRectangle
                .getPosition(TransformSpace.LOCAL);

        log.debug("Position Vector of IdeaNodeView Local: " + positionVectorLocal); //$NON-NLS-1$

        // Get global Matrix and decompose
        Vector3D translationStore = new Vector3D();
        Vector3D rotationStore = new Vector3D();
        Vector3D scaleStore = new Vector3D();
        mtRectangle.getGlobalMatrix().decompose(translationStore,
                rotationStore, scaleStore);
        log.debug("Translation Store: " + translationStore + "; Rotation Store: " + rotationStore + "; Scale Store: " + scaleStore); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        log.debug("Leaving getPositionInfoForRectangle()"); //$NON-NLS-1$

    }

    /**
     * Debug method for logging all input listeners registered to a certain
     * mtComponent.
     * 
     * @param mtComponent
     *            the component
     */
    public static void checkInputListeners(MTComponent mtComponent) {

        log.debug("Entering checkInputListeners(mtComponent=" + mtComponent + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        IMTInputEventListener[] listeners = mtComponent.getInputListeners();
        if (listeners.length == 0) {
            log.debug(mtComponent.getClass() + ": No Input Listeners"); //$NON-NLS-1$
        }
        for (int i = 0; i < listeners.length; i++) {
            log.debug(mtComponent.getClass()
                    + " EventListener Nr." + i + ":" + listeners.getClass().toString()); //$NON-NLS-1$ //$NON-NLS-2$

        }
        log.debug("Leaving checkInputListeners()"); //$NON-NLS-1$

    }

    /**
     * Debug method for logging all gesture listeners registered to a certain
     * mtComponent.
     * 
     * @param mtComponent
     *            the component
     */
    public static void checkGestureListeners(MTComponent mtComponent) {

        log.debug("Entering checkGestureListeners(mtComponent=" + mtComponent + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Remove the default drag listener from the text area
        IGestureEventListener[] l = mtComponent.getGestureListeners();
        if (l.length == 0) {
            log.debug(mtComponent.getClass() + ": No Gesture Listeners"); //$NON-NLS-1$
        }
        for (int i = 0; i < l.length; i++) {
            log.debug(mtComponent.getClass()
                    + " EventListener Nr." + i + ":" + l.getClass().toString()); //$NON-NLS-1$ //$NON-NLS-2$
            if (l[i].getClass().equals(DefaultDragAction.class)) {
                log.debug(mtComponent.getClass()
                        + " EventListener Nr." + i + ": DefaultDragAction.class"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (l[i].getClass().equals(DefaultLassoAction.class)) {
                log.debug(mtComponent.getClass()
                        + " EventListener Nr." + i + ": DefaultLassoAction.class"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (l[i].getClass().equals(DefaultScaleAction.class)) {
                log.debug(mtComponent.getClass()
                        + " EventListener Nr." + i + ": DefaultScaleAction.class"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (l[i].getClass().equals(DefaultArcballAction.class)) {
                log.debug(mtComponent.getClass()
                        + " EventListener Nr." + i + ": DefaultArcballAction.class"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (l[i].getClass().equals(DefaultDepthAction.class)) {
                log.debug(mtComponent.getClass()
                        + " EventListener Nr." + i + ": DefaultDepthAction.class"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (l[i].getClass().equals(DefaultKeyTypedAction.class)) {
                log.debug(mtComponent.getClass()
                        + " EventListener Nr." + i + ": DefaultKeyTypedAction.class"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (l[i].getClass().equals(DefaultPanAction.class)) {
                log.debug(mtComponent.getClass()
                        + " EventListener Nr." + i + ": DefaultPanAction.class"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (l[i].getClass().equals(DefaultRotateAction.class)) {
                log.debug(mtComponent.getClass()
                        + " EventListener Nr." + i + ": DefaultRotateAction.class"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            if (l[i].getClass().equals(DefaultSvgButtonClickAction.class)) {
                log.debug(mtComponent.getClass()
                        + " EventListener Nr." + i + ": DefaultSvgButtonClickAction.class"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            if (l[i].getClass().equals(DefaultZoomAction.class)) {
                log.debug(mtComponent.getClass()
                        + " EventListener Nr." + i + ": DefaultZoomAction.class"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            if (l[i].getClass().equals(InertiaDragAction.class)) {
                log.debug(mtComponent.getClass()
                        + " EventListener Nr." + i + ": InertiaDragAction.class"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            if (l[i].getClass().equals(Rotate3DAction.class)) {
                log.debug(mtComponent.getClass()
                        + " EventListener Nr." + i + ": Rotate3DAction.class"); //$NON-NLS-1$ //$NON-NLS-2$
            }

        }

        log.debug("Leaving checkGestureListeners()"); //$NON-NLS-1$

    }

    /**
     * Debug method for logging all global input processors registered to a
     * certain abstractScene.
     * 
     * @param abstractScene
     *            the scene
     */
    public static void checkGlobalInputProcessors(AbstractScene abstractScene) {

        log.debug("Entering checkGlobalInputProcessors(abstractScene=" + abstractScene + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        AbstractGlobalInputProcessor[] gI = abstractScene
                .getGlobalInputProcessors();

        if (gI.length == 0) {
            log.debug(abstractScene.getClass() + ": No Global InputProcessors"); //$NON-NLS-1$
        }
        for (int i = 0; i < gI.length; i++) {
            log.debug(abstractScene.getClass()
                    + " Global InputProcessor Nr." + i + ":" + gI.getClass().toString()); //$NON-NLS-1$ //$NON-NLS-2$
            if (gI[i].getClass().equals(CursorTracer.class)) {
                log.debug(abstractScene.getClass()
                        + " Global InputProcessor Nr." + i + ": CursorTracer.class"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (gI[i].getClass().equals(FingerTapSelectionManager.class)) {
                log.debug(abstractScene.getClass()
                        + " Global InputProcessor Nr." + i + ": FingerTapSelectionManager.class"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (gI[i].getClass().equals(GlobalUnistrokeProcessor.class)) {
                log.debug(abstractScene.getClass()
                        + " Global InputProcessor Nr." + i + ": GlobalUnistrokeProcessor.class"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (gI[i].getClass().equals(RawFiducialProcessor.class)) {
                log.debug(abstractScene.getClass()
                        + " Global InputProcessor Nr." + i + ": RawFiducialProcessor.class"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (gI[i].getClass().equals(RawFingerProcessor.class)) {
                log.debug(abstractScene.getClass()
                        + " Global InputProcessor Nr." + i + ": RawFingerProcessor.class"); //$NON-NLS-1$ //$NON-NLS-2$
            }

        }
        log.debug("Leaving checkGlobalInputProcessors()"); //$NON-NLS-1$

    }

    /**
     * Logs the current translation, rotation and scale for an abstract shape.
     * 
     * Adapted from http://nuigroup.com/forums/viewthread/11199/#66549
     * 
     * @param abstractShape
     *            the shape
     */
    public static void decomposeGlobalMatrixForAbstractShape(
            AbstractShape abstractShape) {

        log.debug("Entering decomposeGlobalMatrixForAbstractShape(abstractShape=" + abstractShape + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        Vector3D translationStore = new Vector3D();
        Vector3D rotationStore = new Vector3D();
        Vector3D scaleStore = new Vector3D();
        abstractShape.getGlobalMatrix().decompose(translationStore,
                rotationStore, scaleStore);
        log.debug("Translation Store: " + translationStore + "; Rotation Store: " + rotationStore + "; Scale Store: " + scaleStore); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        log.debug("Leaving decomposeGlobalMatrixForAbstractShape()"); //$NON-NLS-1$

    }

}
