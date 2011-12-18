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
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.widgets.MTSvg;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * <p>
 * (Composite) container class for menu handle components, which (via their
 * position) mark the positions of menu rotation/dragging handles. As a child
 * component of a menu they'll be transformed as well and always represent the
 * calculated touch points via their global position.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class MenuHandleContainer extends MarkerContainer {

    private static Logger       log                                     = Logger.getLogger(MenuHandleContainer.class);

    /* *** Menu handle container constants *** */
    /** The menu handle svg image name */
    private static final String MENU_HANDLE_IMG_NAME                    = "Menu_Handle.svg";                          //$NON-NLS-1$

    /** The menu handle width in percent of the menu width (radius*2) */
    private static final float  MENU_HANDLE_WIDTH_SCALE_TO_MENU_PERCENT = 0.3066f;

    /* *** Marker images *** */
    /** The svg image for the marker north */
    private MTSvg               tpNorthMarkerSvg;
    /** The svg image for the marker north east */
    private MTSvg               tpNorthEastMarkerSvg;
    /** The svg image for the marker east */
    private MTSvg               tpEastMarkerSvg;
    /** The svg image for the marker south east */
    private MTSvg               tpSouthEastMarkerSvg;
    /** The svg image for the marker south */
    private MTSvg               tpSouthMarkerSvg;
    /** The svg image for the marker south west */
    private MTSvg               tpSouthWestMarkerSvg;
    /** The svg image for the marker west */
    private MTSvg               tpWestMarkerSvg;
    /** The svg image for the marker north west */
    private MTSvg               tpNorthWestMarkerSvg;

    /* ***********Constructors*********** */

    /**
     * Constructor. Instantiates a new MenuHandleContainer with four handles.
     * 
     * @param pApplet
     *            the application instance
     * @param parent
     *            the parent menu
     * @param tpNorth
     *            the touch point north
     * @param tpEast
     *            the touch point east
     * @param tpSouth
     *            the touch point south
     * @param tpWest
     *            the touch point west
     */
    public MenuHandleContainer(PApplet pApplet, AbstractCircularMenu parent,
            Vector3D tpNorth, Vector3D tpEast, Vector3D tpSouth, Vector3D tpWest) {

        // Call superclass constructor
        super(pApplet, parent, tpNorth, tpEast, tpSouth, tpWest);

        log.debug("Executing MenuHandleContainer(pApplet=" + pApplet //$NON-NLS-1$
                + ", parent=" + parent + ", tpNorth" + tpNorth + ", tpEast=" //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
                + tpEast + ", tpSouth=" + tpSouth + ", tpWest=" + tpWest + ")"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        // Initialize
        initialize();

    }

    /**
     * Constructor. Instantiates a new MenuHandleContainer with a maximum of
     * eight handles. Handles that are not to be used are passed with null.
     * 
     * @param pApplet
     *            the application instance
     * @param parent
     *            the parent menu
     * @param tpNorth
     *            the touch point north
     * @param tpNorthEast
     *            the touch point north east
     * @param tpEast
     *            the touch point east
     * @param tpSouthEast
     *            the touch point south east
     * @param tpSouth
     *            the touch point south
     * @param tpSouthWest
     *            the touch point south west
     * @param tpWest
     *            the touch point west
     * @param tpNorthWest
     *            the touch point north west
     */
    public MenuHandleContainer(PApplet pApplet, AbstractCircularMenu parent,
            Vector3D tpNorth, Vector3D tpNorthEast, Vector3D tpEast,
            Vector3D tpSouthEast, Vector3D tpSouth, Vector3D tpSouthWest,
            Vector3D tpWest, Vector3D tpNorthWest) {

        // Call superclass constructor
        super(pApplet, parent, tpNorth, tpNorthEast, tpEast, tpSouthEast,
                tpSouth, tpSouthWest, tpWest, tpNorthWest);

        log.debug("Executing MenuHandleContainer(pApplet=" + pApplet //$NON-NLS-1$
                + ", parent=" + parent + ", tpNorth" + tpNorth + ", tpNorthEast=" //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
                + tpNorthEast + ", tpEast=" + tpEast + ", tpSouthEast=" //$NON-NLS-1$ //$NON-NLS-2$
                + tpSouthEast + ", tpSouth=" + tpSouth + ", tpSouthWest="//$NON-NLS-1$ //$NON-NLS-2$
                + tpSouthWest + ", tpWest=" + tpWest + ", tpNorthWest="//$NON-NLS-1$ //$NON-NLS-2$
                + tpNorthWest + ")");//$NON-NLS-1$

        // Initialize
        initialize();

    }

    /* ********Getters & Setters******** */

    /**
     * Returns the north marker svg component.
     * 
     * @return the tpNorthMarkerSvg if set, otherwise null
     */
    public MTSvg getTpNorthMarkerSvg() {
        return this.tpNorthMarkerSvg;
    }

    /**
     * Returns the north east marker svg component.
     * 
     * @return the tpNorthEastMarkerSvg if set, otherwise null
     */
    public MTSvg getTpNorthEastMarkerSvg() {
        return this.tpNorthEastMarkerSvg;
    }

    /**
     * Returns the east marker svg component.
     * 
     * @return the tpEastMarkerSvg if set, otherwise null
     */
    public MTSvg getTpEastMarkerSvg() {
        return this.tpEastMarkerSvg;
    }

    /**
     * Returns the south east marker svg component.
     * 
     * @return the tpSouthEastMarkerSvg if set, otherwise null
     */
    public MTSvg getTpSouthEastMarkerSvg() {
        return this.tpSouthEastMarkerSvg;
    }

    /**
     * Returns the south marker svg component.
     * 
     * @return the tpSouthMarkerSvg if set, otherwise null
     */
    public MTSvg getTpSouthMarkerSvg() {
        return this.tpSouthMarkerSvg;
    }

    /**
     * Returns the south west marker svg component.
     * 
     * @return the tpSouthWestMarkerSvg if set, otherwise null
     */
    public MTSvg getTpSouthWestMarkerSvg() {
        return this.tpSouthWestMarkerSvg;
    }

    /**
     * Returns the west marker svg component.
     * 
     * @return the tpWestMarkerSvg if set, otherwise null
     */
    public MTSvg getTpWestMarkerSvg() {
        return this.tpWestMarkerSvg;
    }

    /**
     * Returns the north west marker svg component.
     * 
     * @return the tpNorthWestMarkerSvg if set, otherwise null
     */
    public MTSvg getTpNorthWestMarkerSvg() {
        return this.tpNorthWestMarkerSvg;
    }

    /* **********Object methods********** */
    /**
     * 
     */
    private void initialize() {

        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Add handle marker svgs to every set touch point
        addHandleMarkerSvgs();

        log.debug("Leaving initialize()"); //$NON-NLS-1$

    }

    /**
     * Adds a handle marker SVG for every set touch point marker.
     * 
     */
    private void addHandleMarkerSvgs() {

        log.debug("Entering addHandleMarkerSvgs()"); //$NON-NLS-1$

        // Create touch points marker circles and add as children

        // Svg for Northern marker
        if (getTpNorthMarker() != null) {

            this.tpNorthMarkerSvg = addSingleHandleMarkerSvg(getTpNorthMarker());

        }

        // Svg for North east marker
        if (getTpNorthEastMarker() != null) {

            this.tpNorthEastMarkerSvg = addSingleHandleMarkerSvg(getTpNorthEastMarker());

        }

        // Svg for Eastern marker
        if (getTpEastMarker() != null) {

            this.tpEastMarkerSvg = addSingleHandleMarkerSvg(getTpEastMarker());
        }

        // Svg for South east marker
        if (getTpSouthEastMarker() != null) {

            this.tpSouthEastMarkerSvg = addSingleHandleMarkerSvg(getTpSouthEastMarker());
        }

        // Svg for Southern marker
        if (getTpSouthMarker() != null) {

            this.tpSouthMarkerSvg = addSingleHandleMarkerSvg(getTpSouthMarker());
        }

        // Svg for South west marker
        if (getTpSouthWestMarker() != null) {

            this.tpSouthWestMarkerSvg = addSingleHandleMarkerSvg(getTpSouthWestMarker());
        }

        // Svg for Western marker
        if (getTpWestMarker() != null) {

            this.tpWestMarkerSvg = addSingleHandleMarkerSvg(getTpWestMarker());
        }

        // Svg for North west marker
        if (getTpNorthWestMarker() != null) {

            this.tpNorthWestMarkerSvg = addSingleHandleMarkerSvg(getTpNorthWestMarker());
        }

        log.debug("Leaving addHandleMarkerSvgs()"); //$NON-NLS-1$

    }

    /**
     * Adds a single handle SVG as child to the specified marker. Dont't forget
     * to assign the result to the associated member!
     * 
     * 
     * @param marker
     *            the marker to add the MTSvg to
     * @return the handle MTSvg instance
     */
    private MTSvg addSingleHandleMarkerSvg(MTEllipse marker) {

        log.debug("Entering addSingleHandleMarkerSvg(marker=" + marker + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Create new SVG component
        MTSvg handleSvg = new MTSvg(getAbstractMTapplication(), MT4jSettings
                .getInstance().getDefaultSVGPath() + MENU_HANDLE_IMG_NAME);

        // Get parent menu width
        float parentMenuWidth = ((AbstractCircularMenu) this
                .getParentComponent()).getWidthXY(TransformSpace.GLOBAL);

        // Get marker global position
        Vector3D position = marker.getCenterPointGlobal();

        // Position at specified touch point marker center
        handleSvg.setPositionGlobal(position);

        // Set scale (parent Menu width / Menu handle scaling factor)
        handleSvg.setWidthXYGlobal(parentMenuWidth
                * MENU_HANDLE_WIDTH_SCALE_TO_MENU_PERCENT);

        marker.addChild(handleSvg);

        log.debug("Leaving addSingleHandleMarkerSvg(): " + handleSvg); //$NON-NLS-1$

        return handleSvg;
    }
}
