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
import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.StyleInfo;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;

/**
 * <p>
 * (Composite) container class for marker components, which (via their position)
 * mark the touch points of an component, for example of an IdeaNodeView that
 * child RelationViews can attach to. As a child component of another component
 * (e.g. IdeaNodeView) they'll be transformed as well and always represent the
 * calculated touch points via their global position.
 * </p>
 * 
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 */
public class MarkerContainer extends MTComponent {

    private static Logger          log                = Logger.getLogger(MarkerContainer.class);

    /* *** Main Menu constants *** */
    /**
     * The marker circle radius. Only required for debugging by setting noFill
     * false in MARKER_STYLE_INFO
     */
    private static final int       MARKER_COMP_RADIUS = 5;

    /** The marker circle style info */
    private static final StyleInfo MARKER_STYLE_INFO  = new StyleInfo(
                                                              MTColor.WHITE,
                                                              MTColor.WHITE,
                                                              true,
                                                              true,
                                                              true,
                                                              2.0f,
                                                              GL10.GL_TRIANGLE_FAN,
                                                              (short) 0);

    /* *** Application *** */
    /** The multitouch application instance */
    private AbstractMTApplication  abstractMTapplication;

    /* *** Marker Container *** */
    /** The parent component to the markerContainer */
    private MTComponent            parentComponent;

    /* *** Markers *** */
    /** The circle representing the touch point north marker */
    private MTEllipse              tpNorthMarker;
    /** The circle representing the touch point south marker */
    private MTEllipse              tpSouthMarker;
    /** The circle representing the touch point east marker */
    private MTEllipse              tpEastMarker;
    /** The circle representing the touch point west marker */
    private MTEllipse              tpWestMarker;
    /** The circle representing the touch point north east marker */
    private MTEllipse              tpNorthEastMarker;
    /** The circle representing the touch point north west marker */
    private MTEllipse              tpNorthWestMarker;
    /** The circle representing the touch point south east marker */
    private MTEllipse              tpSouthEastMarker;
    /** The circle representing the touch point south west marker */
    private MTEllipse              tpSouthWestMarker;

    /** The initial position of the touch point north */
    private Vector3D               tpNorthToSet;
    /** The initial position of the touch point south */
    private Vector3D               tpSouthToSet;
    /** The initial position of the touch point east */
    private Vector3D               tpEastToSet;
    /** The initial position of the touch point west */
    private Vector3D               tpWestToSet;
    /** The initial position of the touch point north east */
    private Vector3D               tpNorthEastToSet;
    /** The initial position of the touch point north west */
    private Vector3D               tpNorthWestToSet;
    /** The initial position of the touch point south east */
    private Vector3D               tpSouthEastToSet;
    /** The initial position of the touch point south west */
    private Vector3D               tpSouthWestToSet;

    /* ***********Constructors*********** */

    /**
     * Constructor. Instantiates a new MarkerContainer in the given pApplet with
     * markers for the given touch point positions (default touch points at
     * right angle to center).
     * 
     * @param pApplet
     *            the application instance
     * @param parent
     *            the component the MarkerContainer will be attached to
     * @param tpNorth
     *            the northern touch point to set
     * @param tpSouth
     *            the southern touch point to set
     * @param tpEast
     *            the eastern touch point to set
     * @param tpWest
     *            the western touch point to set
     * 
     */
    public MarkerContainer(PApplet pApplet, MTComponent parent,
            Vector3D tpNorth, Vector3D tpEast, Vector3D tpSouth, Vector3D tpWest) {
        super(pApplet);

        log.debug("Executing MarkerContainer(pApplet=" + pApplet + ", parent=" + parent //$NON-NLS-1$ //$NON-NLS-2$
                + ", tpNorth=" + tpNorth + ", tpEast=" + tpEast //$NON-NLS-1$ //$NON-NLS-2$
                + ", tpSouth=" + tpSouth + ", tpWest=" + tpWest + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // Store application reference
        this.abstractMTapplication = (MTApplication) pApplet;

        // Set touch points to set
        this.tpNorthToSet = tpNorth;
        this.tpSouthToSet = tpSouth;
        this.tpEastToSet = tpEast;
        this.tpWestToSet = tpWest;
        this.tpNorthEastToSet = null;
        this.tpNorthWestToSet = null;
        this.tpSouthEastToSet = null;
        this.tpSouthWestToSet = null;

        // Set parent
        this.parentComponent = parent;

        // Initialize MarkerContainer
        initialize();
    }

    /**
     * Constructor. Instantiates a new MarkerContainer in the given pApplet with
     * markers for the given touch point positions.
     * 
     * 
     * @param pApplet
     *            the application instance
     * @param parent
     *            the component the MarkerContainer will be attached to
     * @param tpNorth
     *            the northern touch point to set
     * @param tpSouth
     *            the southern touch point to set
     * @param tpEast
     *            the eastern touch point to set
     * @param tpWest
     *            the western touch point to set
     * @param tpNorthEast
     *            the north east touch point to set
     * @param tpNorthWest
     *            the north west touch point to set
     * @param tpSouthEast
     *            the south east touch point to set
     * @param tpSouthWest
     *            the south west touch point to set
     */
    public MarkerContainer(PApplet pApplet, MTComponent parent,
            Vector3D tpNorth, Vector3D tpNorthEast, Vector3D tpEast,
            Vector3D tpSouthEast, Vector3D tpSouth, Vector3D tpSouthWest,
            Vector3D tpWest, Vector3D tpNorthWest) {
        super(pApplet);

        log.debug("Executing MarkerContainer(pApplet=" + pApplet + ", parent=" + parent //$NON-NLS-1$ //$NON-NLS-2$
                + ", tpNorth=" + tpNorth + ", tpNorthEast=" + tpNorthEast //$NON-NLS-1$ //$NON-NLS-2$
                + ", tpEast=" + tpEast + ", tpSouthEast=" + tpSouthEast //$NON-NLS-1$ //$NON-NLS-2$
                + ", tpSouth=" + tpSouth + ", tpSouthWest=" //$NON-NLS-1$ //$NON-NLS-2$
                + tpSouthWest + ", tpWest=" + tpWest //$NON-NLS-1$
                + ", tpNorthWest=" + tpNorthWest + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Store application reference
        this.abstractMTapplication = (MTApplication) pApplet;

        // Set touch points to set
        this.tpNorthToSet = tpNorth;
        this.tpSouthToSet = tpSouth;
        this.tpEastToSet = tpEast;
        this.tpWestToSet = tpWest;
        this.tpNorthEastToSet = tpNorthEast;
        this.tpNorthWestToSet = tpNorthWest;
        this.tpSouthEastToSet = tpSouthEast;
        this.tpSouthWestToSet = tpSouthWest;

        // Set parent
        this.parentComponent = parent;

        // Initialize MarkerContainer
        initialize();
    }

    /* ********Getters & Setters******** */
    /**
     * Returns the current application instance.
     * 
     * @return the abstractMTapplication
     */
    public AbstractMTApplication getAbstractMTapplication() {
        return this.abstractMTapplication;
    }

    /**
     * Returns the northern touch point marker component.
     * 
     * @return the tpNorthMarker if set, otherwise null
     */
    public MTEllipse getTpNorthMarker() {
        return this.tpNorthMarker;
    }

    /**
     * Returns the southern touch point marker component.
     * 
     * @return the tpSouthMarker if set, otherwise null
     */
    public MTEllipse getTpSouthMarker() {
        return this.tpSouthMarker;
    }

    /**
     * Returns the eastern touch point marker component.
     * 
     * @return the tpEastMarker if set, otherwise null
     */
    public MTEllipse getTpEastMarker() {
        return this.tpEastMarker;
    }

    /**
     * Returns the western touch point marker component.
     * 
     * @return the tpWestMarker if set, otherwise null
     */
    public MTEllipse getTpWestMarker() {
        return this.tpWestMarker;
    }

    /**
     * Returns the north east touch point marker component.
     * 
     * @return the tpNorthEastMarker if set, otherwise null
     */
    public MTEllipse getTpNorthEastMarker() {
        return this.tpNorthEastMarker;
    }

    /**
     * Returns the north west touch point marker component.
     * 
     * @return the tpNorthWestMarker if set, otherwise null
     */
    public MTEllipse getTpNorthWestMarker() {
        return this.tpNorthWestMarker;
    }

    /**
     * Returns the south east touch point marker component.
     * 
     * @return the tpSouthEastMarker if set, otherwise null
     */
    public MTEllipse getTpSouthEastMarker() {
        return this.tpSouthEastMarker;
    }

    /**
     * Returns the south west touch point marker component.
     * 
     * @return the tpSouthWestMarker if set, otherwise null
     */
    public MTEllipse getTpSouthWestMarker() {
        return this.tpSouthWestMarker;
    }

    /**
     * Returns the parent IdeaNodeView.
     * 
     * @return the parentComponent if set, otherwise null
     */
    public MTComponent getParentComponent() {
        return this.parentComponent;
    }

    /* *************Delegates************** */
    /**
     * Returns the global center point of the northern touch point marker
     * component, if it is set.
     * 
     * @return the global center point of tpNorthMarker if it is set, otherwise
     *         null
     */
    public Vector3D getTpNorthMarkerGlobalPosition() {
        return (this.tpNorthMarker != null) ? this.tpNorthMarker
                .getCenterPointGlobal().getCopy() : null;
    }

    /**
     * Returns the global center point of the north east touch point marker
     * component, if it is set.
     * 
     * @return the global center point of tpNorthEastMarker if it is set,
     *         otherwise null
     */
    public Vector3D getTpNorthEastMarkerGlobalPosition() {
        return (this.tpNorthEastMarker != null) ? this.tpNorthEastMarker
                .getCenterPointGlobal().getCopy() : null;
    }

    /**
     * Returns the global center point of the north west touch point marker
     * component, if it is set.
     * 
     * @return the global center point of tpNorthWestMarker if it is set,
     *         otherwise null
     */
    public Vector3D getTpNorthWestMarkerGlobalPosition() {
        return (this.tpNorthWestMarker != null) ? this.tpNorthWestMarker
                .getCenterPointGlobal().getCopy() : null;
    }

    /**
     * Returns the global center point of the southern touch point marker
     * component, if it is set.
     * 
     * @return the global center point of tpSouthMarker if it is set, otherwise
     *         null
     */
    public Vector3D getTpSouthMarkerGlobalPosition() {
        return (this.tpSouthMarker != null) ? this.tpSouthMarker
                .getCenterPointGlobal().getCopy() : null;
    }

    /**
     * Returns the global center point of the south west touch point marker
     * component, if it is set.
     * 
     * @return the global center point of tpSouthWestMarker if it is set,
     *         otherwise null
     */
    public Vector3D getTpSouthWestMarkerGlobalPosition() {
        return (this.tpSouthWestMarker != null) ? this.tpSouthWestMarker
                .getCenterPointGlobal().getCopy() : null;
    }

    /**
     * Returns the global center point of the south east touch point marker
     * component, if it is set.
     * 
     * @return the global center point of tpSouthEastMarker if it is set,
     *         otherwise null
     */
    public Vector3D getTpSouthEastMarkerGlobalPosition() {
        return (this.tpSouthEastMarker != null) ? this.tpSouthEastMarker
                .getCenterPointGlobal().getCopy() : null;
    }

    /**
     * Returns the global center point of the eastern touch point marker
     * component, if it is set.
     * 
     * @return the global center point of tpEastMarker if it is set, otherwise
     *         null
     */
    public Vector3D getTpEastMarkerGlobalPosition() {
        return (this.tpEastMarker != null) ? this.tpEastMarker
                .getCenterPointGlobal().getCopy() : null;
    }

    /**
     * Returns the global center point of the western touch point marker
     * component, if it is set.
     * 
     * @return the global center point of tpWestMarker if it is set, otherwise
     *         null
     */
    public Vector3D getTpWestMarkerGlobalPosition() {
        return (this.tpWestMarker != null) ? this.tpWestMarker
                .getCenterPointGlobal().getCopy() : null;
    }

    /* **********Object methods********** */
    /**
     * Initializes the MarkerContainer by removing/adding listeners and adding
     * the marker components.
     * 
     */
    private void initialize() {

        log.debug("Entering initialize()"); //$NON-NLS-1$

        // Remove scale processors and listeners
        ToolsEventHandling.removeScaleProcessorsAndListeners(this);

        // Set composite true
        this.setComposite(true);

        // Add marker circles
        addMarkerComponents();

        log.debug("Leaving initialize()"); //$NON-NLS-1$

    }

    /**
     * Adds the marker circle components for the set touch points.
     * 
     */
    private void addMarkerComponents() {

        log.debug("Entering addMarkerComponents()"); //$NON-NLS-1$

        // Create touch points marker circles and add as children

        // Northern marker
        if (this.tpNorthToSet != null) {

            this.tpNorthMarker = addSingleMarker(this.tpNorthToSet);

        }

        // North east marker
        if (this.tpNorthEastToSet != null) {

            this.tpNorthEastMarker = addSingleMarker(this.tpNorthEastToSet);

        }

        // Eastern marker
        if (this.tpEastToSet != null) {

            this.tpEastMarker = addSingleMarker(this.tpEastToSet);
        }

        // South east marker
        if (this.tpSouthEastToSet != null) {

            this.tpSouthEastMarker = addSingleMarker(this.tpSouthEastToSet);
        }

        // Southern marker
        if (this.tpSouthToSet != null) {

            this.tpSouthMarker = addSingleMarker(this.tpSouthToSet);
        }

        // South west marker
        if (this.tpSouthWestToSet != null) {

            this.tpSouthWestMarker = addSingleMarker(this.tpSouthWestToSet);
        }

        // Western marker
        if (this.tpWestToSet != null) {

            this.tpWestMarker = addSingleMarker(this.tpWestToSet);
        }

        // North west marker
        if (this.tpNorthWestToSet != null) {

            this.tpNorthWestMarker = addSingleMarker(this.tpNorthWestToSet);
        }

        log.debug("Leaving addMarkerComponents()"); //$NON-NLS-1$

    }

    /**
     * Adds a single marker at the given position to the MarkerContainer.
     * !Dont't forget to assign the result to the associated member!
     * 
     * @param position
     *            position for the new marker component
     * @return the new marker component
     */
    protected MTEllipse addSingleMarker(Vector3D position) {

        log.debug("Entering addSingleMarker(position=" + position + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        MTEllipse marker = new MTEllipse(this.abstractMTapplication, position,
                MARKER_COMP_RADIUS, MARKER_COMP_RADIUS);

        marker.setStyleInfo(MARKER_STYLE_INFO);

        this.addChild(marker);

        log.debug("Leaving addSingleMarker(): " + marker); //$NON-NLS-1$

        return marker;

    }

}
