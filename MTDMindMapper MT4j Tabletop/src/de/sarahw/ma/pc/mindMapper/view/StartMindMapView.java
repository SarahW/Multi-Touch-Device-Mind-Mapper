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
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.IPreDrawAction;

import de.sarahw.ma.pc.btServer.BluetoothServer;
import de.sarahw.ma.pc.mindMapper.model.AppModel;

/**
 * Main class of the View component. Starts a new MindMap scene. TODO:
 * Singleton!
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
@SuppressWarnings("synthetic-access")
public class StartMindMapView extends MTApplication {

    private static Logger          log              = Logger.getLogger(StartMindMapView.class);

    /** The serial version UID 1L */
    private static final long      serialVersionUID = 1L;

    /* *** Application *** */
    /** The application model reference */
    private static AppModel        modelReference;

    /** The application view reference */
    private static AppView         viewReference;

    /** The bluetooth server reference */
    private static BluetoothServer btReference;

    /* *********Class methods********* */
    /**
     * <p>
     * Initializes the processings settings. Reads settings from Settings.txt
     * and creates a new MTApplication. Has to be called before anything else!
     * </p>
     * 
     * @param view
     *            the application view instance
     */
    public static void initializeStartLoadingView(AppView view) {

        viewReference = view;

        // Initialize mt application
        initialize();
    }

    /* ********Overridden methods******** */
    /**
     * <p>
     * Processings setup. Used to define some initial settings.
     * </p>
     * 
     * <p>
     * Creates scene and adds it to the Application.
     * </p>
     * 
     * <p>
     * Invoked automatically, this is called once when the applet is started.
     * </p>
     */
    @Override
    public void startUp() {
        log.debug("Entering startUp()"); //$NON-NLS-1$

        // Add the loading scene to the application
        AbstractScene loadingScene = new LoadingScene(this, viewReference,
                LoadingScene.LOADING_SCENE_TITLE);
        addScene(loadingScene);

        // After loading start the initialization thread
        // which initializes the model, the bluetooth
        // server and the MindMapScene
        startInitializationThread();

        log.debug("Leaving startUp()"); //$NON-NLS-1$ 

    }

    /**
     * Initializes the model and the bluetooth server in a separate thread.
     * Loads the MindMapScene (in the Animation Thread) after the bluetooth
     * server has been initialized and changes the scene from the loading scene
     * to the mind map scene when the latter is done loading.
     * 
     */
    private void startInitializationThread() {

        log.debug("Entering startInitializationThread()"); //$NON-NLS-1$

        Thread initializationThread = new Thread(new Runnable() {

            @Override
            public void run() {

                // Initialize model
                StartMindMapView.modelReference = AppModel.getInstance();

                // Initialize bluetooth server
                StartMindMapView.btReference = BluetoothServer
                        .getInstance(StartMindMapView.modelReference);

                // Get loaded mindMap title
                final String loadedMindMapTitle = StartMindMapView.modelReference
                        .getLoadedMindMapTitle();

                StartMindMapView.this
                        .registerPreDrawAction(new IPreDrawAction() {

                            @Override
                            public void processAction() {
                                StartMindMapView.this
                                        .invokeLater(new Runnable() {

                                            @Override
                                            public void run() {

                                                final AbstractScene mindMapScene = new MindMapScene(
                                                        StartMindMapView.this,
                                                        loadedMindMapTitle,
                                                        StartMindMapView.modelReference,
                                                        StartMindMapView.btReference);

                                                log.debug("new MindMapScene: " + mindMapScene.getName()); //$NON-NLS-1$

                                                StartMindMapView.this
                                                        .addScene(mindMapScene);

                                                log.debug("Scenes: " + StartMindMapView.this.getSceneCount()); //$NON-NLS-1$

                                                log.debug("Changing the scene: " + mindMapScene.getName()); //$NON-NLS-1$

                                                // When the scene is done
                                                // loading, change the scene
                                                StartMindMapView.this
                                                        .changeScene(mindMapScene);

                                            }
                                        });

                            }

                            @Override
                            public boolean isLoop() {
                                // TODO Auto-generated method stub
                                return false;
                            }

                        });

            }

        });

        // Start thread
        initializationThread.start();

        log.debug("Leaving startInitializationThread()"); //$NON-NLS-1$

    }

}
