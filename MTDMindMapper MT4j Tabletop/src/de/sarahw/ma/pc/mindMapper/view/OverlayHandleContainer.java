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
 * 
 */
public class OverlayHandleContainer extends MarkerContainer {

    private static Logger         log                     = Logger.getLogger(OverlayHandleContainer.class);

    /* *** Overlay handle container contstants *** */
    /** The overlay handle svg image name */
    private static final String   OVERLAY_HANDLE_IMG_NAME = "Overlay_Handle.svg";                          //$NON-NLS-1$

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication mtApplication;

    /* *** Handles *** */
    /** The handle width set at initialization */
    private float                 handleWidth;

    /** The handle image at north east */
    private MTSvg                 tpNorthEastMarkerSvg;
    /** The handle image at south east */
    private MTSvg                 tpSouthEastMarkerSvg;
    /** The handle image at south west */
    private MTSvg                 tpSouthWestMarkerSvg;
    /** The handle image at north west */
    private MTSvg                 tpNorthWestMarkerSvg;

    /* ***********Constructors*********** */

    /**
     * Constructor. Instantiates a new OverlayHandleContainer instance with four
     * handles.
     * 
     * @param pApplet
     *            the application instance
     * @param parent
     *            the parent abstract overlay
     * @param handleWidth
     *            the calculated handle width
     * @param tpNorthEast
     *            the touch point north east
     * @param tpSouthEast
     *            the touch point south east
     * @param tpSouthWest
     *            the touch point south west
     * @param tpNorthWest
     *            the touch point north west
     */
    public OverlayHandleContainer(PApplet pApplet, AbstractOverlay parent,
            float handleWidth, Vector3D tpNorthEast, Vector3D tpSouthEast,
            Vector3D tpSouthWest, Vector3D tpNorthWest) {

        // Call superclass constructor
        super(pApplet, parent, null, tpNorthEast, null, tpSouthEast, null,
                tpSouthWest, null, tpNorthWest);

        log.debug("Executing MenuHandleContainer(pApplet=" + pApplet //$NON-NLS-1$
                + ", parent=" + parent + ", handleWidth=" + handleWidth + " tpNorthEast=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + tpNorthEast + ", tpSouthEast=" //$NON-NLS-1$
                + tpSouthEast + ", tpSouthWest="//$NON-NLS-1$ 
                + tpSouthWest + ", tpNorthWest="//$NON-NLS-1$ 
                + tpNorthWest + ")");//$NON-NLS-1$

        // Set app reference and handle width
        this.mtApplication = (AbstractMTApplication) pApplet;
        this.handleWidth = handleWidth;

        // Initialize
        initialize();

    }

    /* ********Getters & Setters******** */

    /**
     * Returns the north east marker svg component.
     * 
     * @return the tpNorthEastMarkerSvg if set, otherwise null
     */
    public MTSvg getTpNorthEastMarkerSvg() {
        return this.tpNorthEastMarkerSvg;
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
     * Returns the south west marker svg component.
     * 
     * @return the tpSouthWestMarkerSvg if set, otherwise null
     */
    public MTSvg getTpSouthWestMarkerSvg() {
        return this.tpSouthWestMarkerSvg;
    }

    /**
     * Returns the north west marker svg component.
     * 
     * @return the tpNorthWestMarkerSvg if set, otherwise null
     */
    public MTSvg getTpNorthWestMarkerSvg() {
        return this.tpNorthWestMarkerSvg;
    }

    /* *********Utility methods********* */
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

        // Svg for North east marker
        if (getTpNorthEastMarker() != null) {

            this.tpNorthEastMarkerSvg = addSingleHandleMarkerSvg(getTpNorthEastMarker());

        }

        // Svg for South east marker
        if (getTpSouthEastMarker() != null) {

            this.tpSouthEastMarkerSvg = addSingleHandleMarkerSvg(getTpSouthEastMarker());
        }

        // Svg for South west marker
        if (getTpSouthWestMarker() != null) {

            this.tpSouthWestMarkerSvg = addSingleHandleMarkerSvg(getTpSouthWestMarker());
        }

        // Svg for North west marker
        if (getTpNorthWestMarker() != null) {

            this.tpNorthWestMarkerSvg = addSingleHandleMarkerSvg(getTpNorthWestMarker());
        }

        log.debug("Leaving addHandleMarkerSvgs()"); //$NON-NLS-1$

    }

    /**
     * <p>
     * Adds a single handle SVG as child to the specified marker.
     * </p>
     * <p>
     * Don't forget to assign the result to the associated member!
     * </p>
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
                .getInstance().getDefaultSVGPath() + OVERLAY_HANDLE_IMG_NAME);

        // Get marker global position
        Vector3D position = marker.getCenterPointGlobal();

        // Position at specified touch point marker center
        handleSvg.setPositionGlobal(position);

        // Set scale (parent Overlay width / Overlay handle scaling factor)
        handleSvg.setWidthXYGlobal(this.handleWidth);

        marker.addChild(handleSvg);

        log.debug("Leaving addSingleHandleMarkerSvg(): " + handleSvg); //$NON-NLS-1$

        return handleSvg;
    }
}
