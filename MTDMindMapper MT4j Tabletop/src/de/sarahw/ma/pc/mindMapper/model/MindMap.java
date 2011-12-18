/***********************************************************************
 * Copyright (c) 2011 Sarah Will
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

package de.sarahw.ma.pc.mindMapper.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.apache.log4j.Logger;

import de.sarahw.ma.pc.mindMapper.ObserverNotificationObject;
import de.sarahw.ma.pc.mindMapper.ToolsTimestamp;

/**
 * <p>
 * Represents a collection of all Map and IdeaNodeView objects of a current
 * mindMapping session.
 * </p>
 * 
 * <p>
 * Contains methods to add and remove IdeaNodes and Maps as well as save a
 * MindMap via object serialization.
 * </p>
 * 
 * <p>
 * Will be serialized upon object Serialization.
 * </p>
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */

public class MindMap extends Observable implements Serializable {

    private static Logger     log                 = Logger.getLogger(MindMap.class);

    /** The serial version UID -7908634461693811066L */
    private static final long serialVersionUID    = -7908634461693811066L;

    /** The ID of the mindMap */
    // TODO: problematic because two mindMaps can have the same ID ?
    private long              mindMapId;

    /** The title of the mindMap */
    private String            mindMapTitle;

    /** The list of ideaNodes contained in the mindMap */
    private List<IdeaNode>    mindMapIdeaNodeList = new ArrayList<IdeaNode>();

    /** The list of maps contained in the mindMap */
    private List<Map>         mindMapList         = new ArrayList<Map>();

    /* ***********Constructors*********** */
    /**
     * Default constructor. Instantiates a new MindMap.
     */
    public MindMap() {
        super();
        log.debug("Executing MindMap()"); //$NON-NLS-1$ 

        // Set mindMap title to current time stamp
        setMindMapTitle(ToolsTimestamp.clearTimestamp(ToolsTimestamp
                .fetchTimestamp()));

        // Set unique object id
        setMindMapId(this.hashCode());
    }

    /* ********Getters & Setters******** */
    /**
     * Returns the unique mindMapId for the MindMap.
     * 
     * @return the mindMapId
     */
    public long getMindMapId() {
        log.trace("Entering getMindMapId()"); //$NON-NLS-1$
        log.trace("Leaving getMindMapId(): " + this.mindMapId); //$NON-NLS-1$
        return this.mindMapId;
    }

    /**
     * Sets the unique mindMapId for the MindMap.
     * 
     * @param mindMapId
     *            the mindMapId to set
     */
    private void setMindMapId(long mindMapId) {
        log.trace("Entering setMindMapId(mindMapId=" + mindMapId + ")"); //$NON-NLS-1$//$NON-NLS-2$
        this.mindMapId = mindMapId;

        log.trace("Leaving setMindMapId()"); //$NON-NLS-1$
    }

    /**
     * Returns the title of the MindMap.
     * 
     * @return the mindMapTitle
     */
    public String getMindMapTitle() {
        log.trace("Entering getMindMapTitle()"); //$NON-NLS-1$
        log.trace("Leaving getMindMapTitle(): " + this.mindMapTitle); //$NON-NLS-1$
        return this.mindMapTitle;
    }

    /**
     * Sets the title of the MindMap.
     * 
     * @param mindMapTitle
     *            the mindMapTitle to set
     */
    public void setMindMapTitle(String mindMapTitle) {
        log.trace("Entering setMindMapTitle(mindMapTitle:" + mindMapTitle + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        if (mindMapTitle.length() > 0
                && mindMapTitle.length() <= MindMapSerializer.MAX_FILENAME_LENGTH) {
            this.mindMapTitle = mindMapTitle;

            log.debug("Communicate changes to observer for setMindMapTitle()"); //$NON-NLS-1$
            communicateChangesToObserver(new ObserverNotificationObject(
                    EMindMapChangeStatus.MIND_MAP_TITLE_SET, getMindMapTitle()));
            log.trace("Leaving setMindMapTitle()"); //$NON-NLS-1$
        }
        log.trace("Leaving setMindMapTitle(), param String too long"); //$NON-NLS-1$   
    }

    /**
     * Returns the list of all single IdeaNodes in the MindMap.
     * 
     * @return the list of all single IdeaNodes in the MindMap
     */
    public ArrayList<IdeaNode> getMindMapIdeaNodeList() {
        log.trace("Entering getMindMapIdeaNodeList()"); //$NON-NLS-1$
        log.trace("Leaving getMindMapIdeaNodeList()"); //$NON-NLS-1$
        return (ArrayList<IdeaNode>) this.mindMapIdeaNodeList;
    }

    /**
     * Returns the list of all Maps in the MindMap.
     * 
     * @return the list of all Maps in the MindMap
     */
    protected ArrayList<Map> getMindMapList() {
        log.trace("Entering getMindMapList()"); //$NON-NLS-1$
        log.trace("Leaving getMindMapList()"); //$NON-NLS-1$
        return (ArrayList<Map>) this.mindMapList;

    }

    /* **********Object methods********** */
    /**
     * <p>
     * Adds an IdeaNodeView to the mindMapIdeaNodeList of the MindMap.
     * </p>
     * 
     * @param ideaNode
     *            the IdeaNodeView to add to the MindMap
     * @return true if the IdeaNodeView has been added successfully
     */
    public boolean addIdeaNode(IdeaNode ideaNode) {

        log.debug("Entering addIdeaNode(ideaNode=" + ideaNode + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        if (ideaNode != null) {

            if (this.mindMapIdeaNodeList.add(ideaNode)) {

                log.debug("Communicate changes to observer for addIdeaNode()"); //$NON-NLS-1$
                communicateChangesToObserver(new ObserverNotificationObject(
                        EMindMapChangeStatus.MIND_MAP_IDEA_NODE_ADDED, ideaNode));

                log.debug("Leaving addIdeaNode(): true"); //$NON-NLS-1$
                return true;
            }
            log.error("Leaving addIdeaNode(): false, IdeaNode could not be added to List"); //$NON-NLS-1$
            return false;

        }
        log.error("Leaving addIdeaNode(): false, invalid null input"); //$NON-NLS-1$
        return false;

    }

    /**
     * <p>
     * Called when removing {@link IdeaNode#removeIdeaChild(IdeaNode, MindMap)}
     * or adding an IdeaNode {@link IdeaNode#addIdeaChild(IdeaNode, MindMap)}
     * makes it necessary to add a new Map to the MindMap.
     * </p>
     * 
     * <p>
     * Adds a Map to the mindMapList of the MindMap. Handled exclusively by the
     * IdeaNodeView class upon IdeaNodeView adding and removal.
     * </p>
     * 
     * @param map
     *            the Map to add to the MindMap
     * @return true if the Map has been added successfully
     * 
     * @see IdeaNode#removeIdeaChild(IdeaNode, MindMap)
     * @see IdeaNode#addIdeaChild(IdeaNode, MindMap)
     * 
     */
    protected boolean addMap(Map map) {

        log.debug("Entering addMap(map=" + map + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        if (map != null) {
            // check if the map has a root node
            if (map.getRootNode() != null) {
                if (this.mindMapList.add(map)) {
                    log.debug("Leaving addMap(): true"); //$NON-NLS-1$
                    return true;
                }
                log.debug("Leaving addMap(): false, Map could not be added to List"); //$NON-NLS-1$
                return false;
            }
            log.error("Leaving addMap(): false, map has no root node"); //$NON-NLS-1$
            return false;
        }
        log.error("Leaving addMap(): false, invalid null input"); //$NON-NLS-1$
        return false;
    }

    /**
     * <p>
     * Removes an IdeanNode from the mindMapIdeaNodesList of the MindMap after
     * the IdeaNodeView has been checked for Map membership and Map structures
     * have been updated accordingly.
     * </p>
     * 
     * @param ideaNode
     *            the IdeaNodeView to remove from the MindMap
     * @return true if the IdeaNodeView has been removed and maps have been
     *         updated successfully
     */
    public boolean removeIdeaNode(IdeaNode ideaNode) {

        log.debug("Entering removeIdeaNode(ideaNode=" + ideaNode + ")"); //$NON-NLS-1$//$NON-NLS-2$
        if (ideaNode != null) {

            // check if IdeaNodeView is part of a Map and delete relations
            // accordingly
            if (!checkAndUpdateMapStructuresOnRemove(ideaNode)) {
                log.error("Leaving removeIdeaNode(): false, on error"); //$NON-NLS-1$
                // TODO send message?
                return false;
            }
            // remove ideaNode from list
            if (this.mindMapIdeaNodeList.remove(ideaNode)) {
                ideaNode.setIdeaState(EIdeaState.DELETED);

                log.debug("Communicate changes to observer for removeIdeaNode()"); //$NON-NLS-1$
                communicateChangesToObserver(new ObserverNotificationObject(
                        EMindMapChangeStatus.MIND_MAP_IDEA_NODE_REMOVED,
                        ideaNode));

                log.debug("Leaving removeIdeaNode(): true"); //$NON-NLS-1$
                return true;
            }
            log.error("Leaving removeIdeaNode(): false, IdeaNodeView could not be removed from List"); //$NON-NLS-1$
            return false;

        }
        log.error("Leaving removeIdeaNode(): false, invalid null input"); //$NON-NLS-1$
        return false;
    }

    /**
     * <p>
     * Called when removing {@link IdeaNode#removeIdeaChild(IdeaNode, MindMap)}
     * or adding an IdeaNodeView
     * {@link IdeaNode#addIdeaChild(IdeaNode, MindMap)} makes it necessary to
     * remove a certain Map from the MindMap.
     * </p>
     * 
     * <p>
     * Removes a Map from the mindMapList of the MindMap.
     * </p>
     * 
     * @param map
     *            the Map to remove from the MindMap
     * @return true if the Map has been removed successfully
     * 
     * @see IdeaNode#removeIdeaChild(IdeaNode, MindMap)
     * @see IdeaNode#addIdeaChild(IdeaNode, MindMap)
     * 
     */
    protected boolean removeMap(Map map) {

        log.debug("Entering removeMap(map=" + map + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        if (map != null) {
            if (this.mindMapList.remove(map)) {
                log.debug("Leaving removeMap(): true"); //$NON-NLS-1$
                return true;
            }
            log.error("Leaving removeMap(): false, Map could not be removed from List"); //$NON-NLS-1$
            return false;

        }
        log.error("Leaving removeMap(): false, invalid null input"); //$NON-NLS-1$
        return false;
    }

    /**
     * Gets the current number of single IdeaNodes in the MindMap.
     * 
     * @return the number of single IdeaNodes in the MindMap
     */
    protected int getNumberOfIdeaNodes() {

        log.debug("Entering getNumberOfIdeaNodes()"); //$NON-NLS-1$

        int numberOfSingleIdeas = 0;
        for (int i = 0; i < this.mindMapIdeaNodeList.size(); i++) {
            if (this.mindMapIdeaNodeList.get(i).getIsChild() == false
                    && this.mindMapIdeaNodeList.get(i).isParent() == false) {
                numberOfSingleIdeas++;
            }
        }
        log.debug("Leaving getNumberOfIdeaNodes(): " + numberOfSingleIdeas); //$NON-NLS-1$
        return numberOfSingleIdeas;
    }

    /**
     * Gets the current number of Maps in the MindMap.
     * 
     * @return the number of Maps
     */
    protected int getNumberOfMaps() {
        log.debug("Entering getNumberOfMaps()"); //$NON-NLS-1$
        log.debug("Leaving getNumberOfMaps(): " + this.mindMapList.size()); //$NON-NLS-1$
        return this.mindMapList.size();
    }

    /**
     * Finds a Map object by its root IeaNode.
     * 
     * @param idea
     *            the IdeaNodeView which is root node of the Map
     * @return the Map object with the given IdeaNodeView as root or null if it
     *         hasn't been found
     */
    public Map findMapByRootNode(IdeaNode idea) {

        log.debug("Entering findMapByRootNode(idea=" + idea + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        if (idea != null) {
            // Search the list of Maps for a Map with the given root node
            for (int i = 0; i < getMindMapList().size(); i++) {
                if (getMindMapList().get(i).getRootElement() == idea) {

                    log.debug("Leaving findMapByRootNode(): " + getMindMapList().get(i)); //$NON-NLS-1$ 
                    return getMindMapList().get(i);
                }
            }
            log.error("Leaving findMapByRootNode(): null, object not found"); //$NON-NLS-1$
            return null;
        }
        log.error("Leaving findMapByRootNode(): null, invalid null input"); //$NON-NLS-1$
        return null;

    }

    /**
     * Finds a Map object by a child IeaNode.
     * 
     * @param idea
     *            the IdeaNodeView which is a child of the Map
     * @return the Map object with the given IdeaNodeView as child or null if it
     *         hasn't been found
     */
    protected Map findMapByChildNode(IdeaNode idea) {

        log.debug("Entering findMapByChildNode(idea=" + idea + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        if (idea != null) {

            // search the list of Maps for a Map with the given child node
            for (int i = 0; i < getMindMapList().size(); i++) {
                ArrayList<Node<NodeData>> listOfNodes = getMindMapList().get(i)
                        .toList();
                for (int j = 0; i < listOfNodes.size(); j++) {
                    if (listOfNodes.get(j) == idea) {

                        log.debug("Leaving findMapByChildNode(): " + getMindMapList().get(i)); //$NON-NLS-1$
                        return getMindMapList().get(i);
                    }
                }
            }
            log.error("Leaving findMapByChildNode(): null, object not found"); //$NON-NLS-1$
            return null;
        }
        log.error("Leaving findMapByChildNode(): null, invalid null input"); //$NON-NLS-1$
        return null;
    }

    /**
     * Finds an IdeaNodeView object by a given ideaNodeID.
     * 
     * @param ideaNodeID
     *            the desired IdeaNodeView
     * @return the IdeaNodeView with the given ideaNodeID or null if it hasn't
     *         been found
     */
    protected IdeaNode findIdeaNodeByID(long ideaNodeID) {

        log.debug("Entering findIdeaNodeByID(idea=" + ideaNodeID + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Search the list of IdeaNodes for the IdeaNodeView with the given ID
        for (int i = 0; i < getMindMapIdeaNodeList().size(); i++) {
            if (getMindMapIdeaNodeList().get(i).getIdeaID() == ideaNodeID) {

                log.debug("Leaving findIdeaNodeByID(): " + getMindMapIdeaNodeList().get(i)); //$NON-NLS-1$
                return getMindMapIdeaNodeList().get(i);
            }
        }

        log.error("Leaving findIdeaNodeByID(): null, object not found"); //$NON-NLS-1$
        return null;
    }

    /**
     * Saves the current MindMap to disc via object serialization.
     * 
     * @return result of saving
     */
    public boolean saveMindMap() {

        log.debug("Entering saveMindMap()"); //$NON-NLS-1$

        // USED FOR DEBUGGING:
        // setMindMapTitle("TestMindMapGUI"); //$NON-NLS-1$

        log.debug("Leaving saveMindMap(): result of serialize() "); //$NON-NLS-1$
        return MindMapSerializer.writeMindMapToDisc(this);

    }

    /* **********Utility methods********** */
    /**
     * <p>
     * Submethod called when a IdeaNodeView from the MindMap is to be removed by
     * {@link MindMap#removeIdeaNode(IdeaNode)}.
     * </p>
     * 
     * <p>
     * Checks if the ideaNodeToBeRemoved belongs to a Map structure and updates
     * the Map accordingly.
     * </p>
     * 
     * Cases:
     * <p>
     * (1) Check parents:<br/>
     * &nbsp;&nbsp; (1.1) The ideaNodeToBeRemoved has a parent node: sever
     * parent relations by removing ideaNodeToBeRemoved as a child of the parent
     * IdeaNodeView. <br/>
     * &nbsp;&nbsp; (1.1) The ideaNodeToBeRemoved has no parent node: nothing to
     * do.
     * </p>
     * <p>
     * (2) Check children:<br/>
     * &nbsp;&nbsp; (2.1) The ideaNodeToBeRemoved has one child or more
     * children: sever children relations by removing every child of
     * ideaNodeToBeRemoved from its children list.<br/>
     * &nbsp;&nbsp; (2.2) The ideaNodeToBeRemoved has no children: nothing to
     * do.
     * </p>
     * 
     * @param ideaNodeToBeRemoved
     *            the IdeaNodeView that is to be removed
     * @return true if any relations to parent or child nodes have been
     *         successfully removed
     * 
     * @see MindMap#removeIdeaNode(IdeaNode)
     */
    private boolean checkAndUpdateMapStructuresOnRemove(
            IdeaNode ideaNodeToBeRemoved) {

        log.debug("Entering checkAndUpdateMapStructuresOnRemove(ideaNodeToBeRemoved=" + ideaNodeToBeRemoved + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        if (ideaNodeToBeRemoved != null) {
            // if the ideaNodeToBeRemoved is a child to another IdeaNodeView,
            // remove the relation
            if (ideaNodeToBeRemoved.getIsChild() == true) {
                log.debug(" Case 1.1: ideaNodeToBeRemoved has a parent, remove as child"); //$NON-NLS-1$ 
                // find parent node by ID

                IdeaNode parent = findIdeaNodeByID(ideaNodeToBeRemoved
                        .getParentID());
                if (parent == null) {
                    log.error("Leaving checkAndUpdateMapStructuresOnRemove(): false; IdeaNodeView with ID " + ideaNodeToBeRemoved.getParentID() + " could not be found!"); //$NON-NLS-1$ //$NON-NLS-2$
                    return false;
                }

                // Remove child
                ERemoveChildIdeaNodeResultCase result = parent.removeIdeaChild(
                        ideaNodeToBeRemoved, this);
                if ((result
                        .equals(ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_ERROR))
                        || (result
                                .equals(ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_CONSTRAINT))) {

                    log.error(" Leaving checkAndUpdateMapStructuresOnRemove(): false; on error existing relation could not be removed"); //$NON-NLS-1$ 
                    return false;
                }

            } else {
                // IdeaNodeView has no parents, nothing to do
                log.debug(" Case 1.2: ideaNodeToBeRemoved has no parent, no map operations necessary"); //$NON-NLS-1$ 
            }

            // if the ideaNodeToBeRemoved has children, remove all relations
            ArrayList<Node<NodeData>> listOfChildren = ideaNodeToBeRemoved
                    .getChildren();

            if ((listOfChildren != null) && (listOfChildren.size() > 0)) {

                log.debug(" Case 2.1: ideaNodeToBeRemoved has " + listOfChildren.size() + " children, remove all relations"); //$NON-NLS-1$ //$NON-NLS-2$

                // delete every child from children list
                ERemoveChildIdeaNodeResultCase result;
                while (listOfChildren.size() > 0) {

                    // Remove child
                    result = ideaNodeToBeRemoved.removeIdeaChild(
                            (IdeaNode) listOfChildren.get(0), this);
                    if ((result
                            .equals(ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_ERROR))
                            || (result
                                    .equals(ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_CONSTRAINT))) {
                        log.error(" Leaving checkAndUpdateMapStructuresOnRemove(): false; on error existing relation could not be removed"); //$NON-NLS-1$ 
                        return false;
                    }
                }

            } else {
                // IdeaNodeView has no children, nothing to do
                log.debug(" Case 2.2: ideaNodeToBeRemoved has no children, no map operations necessary"); //$NON-NLS-1$ 
            }

            log.debug("Leaving checkAndUpdateMapStructuresOnRemove(): true"); //$NON-NLS-1$ 
            return true;
        }
        log.debug("Leaving checkAndUpdateMapStructuresOnRemove(): false, invalid null input"); //$NON-NLS-1$ 
        return false;
    }

    /**
     * Communicates changes to all registered Observers.
     * 
     * @param object
     *            the observer notification object
     */
    private void communicateChangesToObserver(ObserverNotificationObject object) {

        log.debug("Entering communicateChangesToObserver()"); //$NON-NLS-1$

        setChanged();
        notifyObservers(object);

        log.debug("Leaving communicateChangesToObserver()"); //$NON-NLS-1$
    }

    /* ********Overridden methods******** */
    /**
     * Returns a String representation of an MindMap object.
     * 
     * @return String representation of MindMap.
     */
    @Override
    public String toString() {
        return "MindMap [mindMapId=" + getMindMapId() + ", mindMapTitle=" //$NON-NLS-1$//$NON-NLS-2$
                + getMindMapTitle() + "]"; //$NON-NLS-1$
    }

    /**
     * Returns a hash code value for the MindMap.
     * 
     * @return a hash code value for this MindMap.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((getMindMapIdeaNodeList() == null) ? 0
                        : getMindMapIdeaNodeList().hashCode());
        result = prime
                * result
                + ((getMindMapList() == null) ? 0 : getMindMapList().hashCode());
        result = prime
                * result
                + ((getMindMapTitle() == null) ? 0 : getMindMapTitle()
                        .hashCode());
        return result;
    }

    /**
     * Compares the specified object with this MindMap for equality. Returns
     * true if and only if the specified object is also a MindMap with the same
     * elements.
     * 
     * @return true if the specified object is equal to this MindMap.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MindMap)) {
            return false;
        }
        MindMap other = (MindMap) obj;
        if (getMindMapId() != other.getMindMapId()) {
            return false;
        }
        if (getMindMapIdeaNodeList() == null) {
            if (other.getMindMapIdeaNodeList() != null) {
                return false;
            }
        } else if (!getMindMapIdeaNodeList().equals(
                other.getMindMapIdeaNodeList())) {
            return false;
        }
        if (getMindMapList() == null) {
            if (other.getMindMapList() != null) {
                return false;
            }
        } else if (!getMindMapList().equals(other.getMindMapList())) {
            return false;
        }
        if (getMindMapTitle() == null) {
            if (other.getMindMapTitle() != null) {
                return false;
            }
        } else if (!getMindMapTitle().equals(other.getMindMapTitle())) {
            return false;
        }
        return true;
    }

}
