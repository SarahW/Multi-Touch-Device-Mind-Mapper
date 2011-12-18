package de.sarahw.ma.pc.mindMapper.view;

import org.apache.log4j.Logger;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PImage;

/**
 * <p>
 * LoadingScene class. Represents the loading screen that is active while model
 * and the bluetooth server are being initialized and the MindMapScene is still
 * being loaded.
 * </p>
 * 
 * 
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */
public class LoadingScene extends AbstractScene {

    private static Logger         log                 = Logger.getLogger(LoadingScene.class);

    /* *** Scene constants *** */
    /** The loading scene title */
    public static final String    LOADING_SCENE_TITLE = "Loading Scene";                     //$NON-NLS-1$

    /* *** Logo image constants *** */

    /** The image path to the logo image */
    private static final String   LOAD_IMG_PATH       = MT4jSettings
                                                              .getInstance()
                                                              .getDefaultImagesPath()
                                                              + "AnimationImage_000.png";    //$NON-NLS-1$

    /* *** Canvas constants *** */
    /** The canvas background color */
    private static final MTColor  CANVAS_BG_COLOR     = MTColor.BLACK;

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication abstractMTapplication;
    /** The application view reference */
    private AppView               viewReference;

    /* *** LoadingScene Widgets *** */
    /** The rectangle to display the logo image */
    private MTRectangle           logoImageRectangle;

    /* ********Getters & Setters******** */
    /**
     * Returns the logo image rectangle
     * 
     * @return the logoImageRectangle
     */
    public MTRectangle getLogoImageRectangle() {
        return this.logoImageRectangle;
    }

    /* ***********Constructors*********** */
    /**
     * Constructor. Instantiates a new MindMapScene.
     * 
     * @param mtApplication
     *            the application instance
     * @param view
     *            the view instance
     * @param name
     *            the name of the scene
     */
    public LoadingScene(AbstractMTApplication mtApplication, AppView view,
            String name) {
        super(mtApplication, name);

        log.debug("Executing LoadingScene(mtApplication=" + mtApplication //$NON-NLS-1$
                + ", name=" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$ 

        // Set Application reference
        this.abstractMTapplication = mtApplication;
        this.viewReference = view;

        // Initialize
        initialize();

    }

    /* *********Object methods********* */
    /**
     * Initializes the LoadingScene.
     */
    private void initialize() {
        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Set background color
        setClearColor(CANVAS_BG_COLOR);

        // Remove all global input listeners
        removeAllInputListeners();

        // Add the loading image
        addLogoImage();

        log.debug("Leaving initialize()"); //$NON-NLS-1$

    }

    /**
     * Removes all gesture event listeners from the canvas.
     * 
     */
    private void removeAllInputListeners() {
        log.debug("Entering removeAllInputListeners()"); //$NON-NLS-1$

        this.getCanvas().removeAllGestureEventListeners();

        log.debug("Leaving removeAllInputListeners()"); //$NON-NLS-1$

    }

    /**
     * Adds the loading image to the canvas
     */
    private void addLogoImage() {
        log.debug("Entering addLogoImage()"); //$NON-NLS-1$

        PImage logo = this.abstractMTapplication.loadImage(LOAD_IMG_PATH);

        MTRectangle logoRec = new MTRectangle(this.abstractMTapplication, logo);

        ToolsEventHandling.removeDragProcessorsAndListeners(logoRec);
        ToolsEventHandling.removeScaleProcessorsAndListeners(logoRec);
        ToolsEventHandling.removeRotateProcessorsAndListeners(logoRec);

        logoRec.setStrokeColor(CANVAS_BG_COLOR);

        getCanvas().addChild(logoRec);

        logoRec.setPositionGlobal(new Vector3D(this.abstractMTapplication
                .getWidth() / 2f, this.abstractMTapplication.getHeight() / 2f));

        this.logoImageRectangle = logoRec;

        log.debug("Leaving addLogoImage()"); //$NON-NLS-1$

    }

}
