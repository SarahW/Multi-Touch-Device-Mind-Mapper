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

import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.sarahw.ma.pc.mindMapper.ObserverNotificationObject;

/**
 * <p>
 * Non-generic subclass of Node<NodeData> Represents a single node of an idea,
 * which contains content of the type NodeData.
 * </p>
 * 
 * <p>
 * May contain a list of children of IdeaNode objects to form a Map.
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
 * @see de.sarahw.ma.pc.mindMapper.model.Map
 * 
 */

public class IdeaNode extends Node<NodeData> {

    private static Logger       log              = Logger.getLogger(IdeaNode.class);

    /** The serial version UID -9146795148351052192L */
    private static final long   serialVersionUID = -9146795148351052192L;

    /** Constant representing the state "no parent" */
    protected static final long NO_PARENT        = -1;

    /** The total count of all created IdeaNodes */
    // TODO: On removal?
    private static int          ideaCount        = 0;

    /** The ID of the parent ideaNode, if applicable */
    // TODO: problematic because two IdeaNodes can have the same ID ?
    private long                parentID;

    /** The ID of the IdeaNode (hashValue) */
    // TODO: problematic because two IdeaNodes can have the same ID ?
    private long                ideaID;

    /** Flag indicating if the ideaNode is a child of another ideaNode */
    private boolean             isChild;

    /* ***********Constructors*********** */
    /**
     * Private default constructor. Currently unused.
     */
    @SuppressWarnings("unused")
    private IdeaNode() {
        super();
        log.debug("Executing IdeaNode()"); //$NON-NLS-1$
    }

    /**
     * Instantiates a new IdeaNode with the given NodeData.
     * 
     * @param nodeData
     *            the data of the ideaNode
     */
    public IdeaNode(NodeData nodeData) {
        super(nodeData);

        log.trace("Executing IdeaNode(nodeData=" + nodeData + ")"); //$NON-NLS-1$//$NON-NLS-2$

        ideaCount++;
        setIdeaID(this.hashCode());
        setParentID(NO_PARENT);
        setIsChild(false);

    }

    /* ********Getters & Setters******** */
    /**
     * Returns the current number of ideaNode objects.
     * 
     * @return the ideaCount
     */
    public static int getIdeaCount() {
        log.trace("Entering getIdeaCount()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaCount(): " + ideaCount); //$NON-NLS-1$
        return ideaCount;

    }

    /**
     * Sets the number of ideaNode objects.
     * 
     * @param count
     *            the number of ideaNodes
     */
    private static void setIdeaCount(int count) {
        log.trace("Entering setIdeaCount(count=" + count + ")"); //$NON-NLS-1$ //$NON-NLS-2$ 
        ideaCount = count;
        log.trace("Leaving setIdeaCount()"); //$NON-NLS-1$
    }

    /**
     * Returns the unique ID for the ideaNode object.
     * 
     * @return the ideaID
     */
    public long getIdeaID() {
        log.trace("Entering getIdeaID()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaID(): " + this.ideaID); //$NON-NLS-1$
        return this.ideaID;
    }

    /**
     * Sets the unique ID for the ideaNode object.
     * 
     * @param ideaID
     *            the ID for the ideaNode object.
     */
    private void setIdeaID(long ideaID) {
        log.trace("Entering setIdeaID(ideaID=" + ideaID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        this.ideaID = ideaID;
        log.trace("Leaving setIdeaID()"); //$NON-NLS-1$
    }

    /**
     * Returns status of the IdeaNode as child to another IdeaNode.
     * 
     * @return isChild true/false
     */
    public boolean getIsChild() {
        log.trace("Entering getIsChild()"); //$NON-NLS-1$
        log.trace("Leaving getIsChild(): " + this.isChild); //$NON-NLS-1$
        return this.isChild;

    }

    /**
     * Sets status of the IdeaNode as child to another IdeaNode.
     * 
     * @param isChild
     *            true/false
     */
    private void setIsChild(boolean isChild) {
        log.trace("Entering setIsChild(isChild=" + isChild + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        this.isChild = isChild;
        log.trace("Leaving setIsChild()"); //$NON-NLS-1$
    }

    /**
     * Returns the ID of the parent IdeaNode if applicable. If no parent node is
     * specified value is -1.
     * 
     * @return the parentID
     */
    public long getParentID() {
        log.trace("Entering getParentID()"); //$NON-NLS-1$
        log.trace("Leaving getParentID(): " + this.parentID); //$NON-NLS-1$
        return this.parentID;
    }

    /**
     * Sets the ID of the parent IdeaNode.
     * 
     * @param parentID
     *            the parentID to set
     */
    private void setParentID(long parentID) {
        log.trace("Entering setParentID(parentID=" + parentID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        this.parentID = parentID;
        log.trace("Leaving getParentID()"); //$NON-NLS-1$
    }

    /* *************Delegates************** */
    /**
     * Returns the idea text within the NodeData.
     * 
     * @return getData().getIdeaText()
     * @see de.sarahw.ma.pc.mindMapper.model.NodeData#getIdeaText()
     */
    public String getIdeaText() {
        log.trace("Entering getIdeaText()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaText()"); //$NON-NLS-1$
        return getData().getIdeaText();
    }

    /**
     * <p>
     * Sets the idea text within the NodeData.
     * </p>
     * 
     * <p>
     * Notifies registered observers of the change.
     * </p>
     * 
     * @param ideaText
     *            ideaText within NodeData
     * @return getData().setIdeaText(ideaText)
     * @see de.sarahw.ma.pc.mindMapper.model.NodeData#setIdeaText(java.lang.String)
     */
    public boolean setIdeaText(String ideaText) {
        log.trace("Entering setIdeaText(ideaText=" + ideaText + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        log.trace("Leaving setIdeaText()"); //$NON-NLS-1$
        return getData().setIdeaText(ideaText);

    }

    /**
     * Returns the idea rotation in degrees.
     * 
     * @return getData().getIdeaRotationInDegrees()
     * @see de.sarahw.ma.pc.mindMapper.model.NodeData#getIdeaRotationInDegrees()
     */
    public float getIdeaRotationInDegrees() {
        log.trace("Entering getIdeaRotationInDegrees()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaRotationInDegrees()"); //$NON-NLS-1$
        return getData().getIdeaRotationInDegrees();
    }

    /**
     * <p>
     * Sets the idea rotation in degrees
     * </p>
     * 
     * 
     * @param ideaRotationInDegrees
     *            ideaRotationInDegrees within NodeData
     * @return getData().setIdeaRotationInDegrees(ideaRotationInDegrees)
     * @see de.sarahw.ma.pc.mindMapper.model.NodeData#setIdeaRotationInDegrees(float)
     */
    public boolean setIdeaRotationInDegrees(float ideaRotationInDegrees) {
        log.trace("Entering setIdeaRotationInDegrees(ideaRotationInDegrees=" + ideaRotationInDegrees + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        log.trace("Entering setIdeaRotationInDegrees()"); //$NON-NLS-1$
        return getData().setIdeaRotationInDegrees(ideaRotationInDegrees);

    }

    /**
     * Returns the ideaOwner within NodeData.
     * 
     * @return getData().getIdeaOwner()
     * @see de.sarahw.ma.pc.mindMapper.model.NodeData#getIdeaOwner()
     */
    public EIdeaNodeCreator getIdeaOwner() {
        log.trace("Entering getIdeaOwner()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaOwner()"); //$NON-NLS-1$
        return getData().getIdeaOwner();
    }

    /**
     * Sets the ideaOwnerId within NodeData.
     * 
     * @param ideaOwner
     *            EIdeaNodeCreator within NodeData
     * @see de.sarahw.ma.pc.mindMapper.model.NodeData#setIdeaOwner(EIdeaNodeCreator)
     */
    protected void setIdeaOwner(EIdeaNodeCreator ideaOwner) {
        log.trace("Entering setIdeaOwner(ideaOwner=" + ideaOwner + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        getData().setIdeaOwner(ideaOwner);
        log.trace("Leaving setIdeaOwner()"); //$NON-NLS-1$
    }

    /**
     * Returns the ideaPosition x within NodeData.
     * 
     * @return getData().getIdeaPositionX()
     * @see de.sarahw.ma.pc.mindMapper.model.NodeData#getIdeaPositionX()
     */
    public float getIdeaPositionX() {
        log.trace("Entering getIdeaPositionX()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaPositionX()"); //$NON-NLS-1$
        return getData().getIdeaPositionX();
    }

    /**
     * <p>
     * Sets the ideaPosition x within NodeMetaData.
     * </p>
     * 
     * 
     * @param x
     *            ideaPosition x within NodeData
     * @see de.sarahw.ma.pc.mindMapper.model.NodeData#setIdeaPositionX
     */
    public boolean setIdeaPositionX(float x) {
        log.trace("Entering setIdeaPositionX(x" + x + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        log.trace("Leaving setIdeaPositionX()"); //$NON-NLS-1$
        return getData().setIdeaPositionX(x);

    }

    /**
     * Returns the ideaPosition y via NodeData.
     * 
     * @return getData().getIdeaPositionY()
     * @see de.sarahw.ma.pc.mindMapper.model.NodeData#getIdeaPositionY()
     */
    public float getIdeaPositionY() {
        log.trace("Entering getIdeaPositionY()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaPositionY()"); //$NON-NLS-1$
        return getData().getIdeaPositionY();
    }

    /**
     * <p>
     * Sets the ideaPosition y within NodeMetaData via NodeData.
     * </p>
     * 
     * 
     * @param y
     *            ideaPosition y within NodeMetaData
     * @return getData().setIdeaPositionY(y)
     * @see de.sarahw.ma.pc.mindMapper.model.NodeData#setIdeaPositionY
     */
    public boolean setIdeaPositionY(float y) {
        log.trace("Entering setIdeaPositionY(y" + y + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        log.trace("Leaving setIdeaPositionY()"); //$NON-NLS-1$
        return getData().setIdeaPositionY(y);

    }

    /**
     * Gets the ideaState via NodeData.
     * 
     * @return getData().getIdeaState()
     * @see de.sarahw.ma.pc.mindMapper.model.NodeData#getIdeaState()
     */
    public EIdeaState getIdeaState() {
        log.trace("Entering getIdeaState()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaState()"); //$NON-NLS-1$
        return getData().getIdeaState();
    }

    /**
     * Sets the ideaState within NodeMetaData via NodeData.
     * 
     * @param ideaState
     *            ideaState within NodeData
     * @see de.sarahw.ma.pc.mindMapper.model.NodeData#setIdeaState(de.sarahw.ma.pc.mindMapper.model.EIdeaState)
     */
    public void setIdeaState(EIdeaState ideaState) {
        log.trace("Entering setIdeaState(ideaState=" + ideaState + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        getData().setIdeaState(ideaState);
        log.trace("Leaving setIdeaState()"); //$NON-NLS-1$
    }

    /* ****Superclass access methods**** */
    /**
     * <p>
     * Returns the children of the IdeaNode via the superclass Node<NodeData>.
     * </p>
     * 
     * @return the children of IdeaNode
     */
    @Override
    public ArrayList<Node<NodeData>> getChildren() {
        log.trace("Entering getChildren()"); //$NON-NLS-1$
        log.trace("Leaving getChildren(): list of children"); //$NON-NLS-1$
        return super.getChildren();
    }

    /**
     * Returns the NodeData of the IdeaNode via the superclass Node<NodeData>.
     * 
     * @return the NodeData of the IdeaNode
     */
    @Override
    public NodeData getData() {
        log.trace("Entering getData()"); //$NON-NLS-1$
        log.trace("Leaving getData()"); //$NON-NLS-1$
        return super.getData();
    }

    /**
     * <p>
     * Sets the NodeData of the IdeaNode via the superclass Node<NodeData>.
     * </p>
     * 
     * <p>
     * Notifies registered observers of the change.
     * </p>
     * 
     * @param nodeData
     *            the NodeData of the IdeaNode
     */
    @Override
    protected void setData(NodeData nodeData) {
        log.trace("Entering setData(nodeData=" + nodeData + ")"); //$NON-NLS-1$ //$NON-NLS-2$ 
        super.setData(nodeData);

        log.trace("Leaving setData()"); //$NON-NLS-1$
    }

    /**
     * <p>
     * Adds a new child IdeaNode to the IdeaNode object via the superclass
     * Node<NodeData> after both IdeaNodes have been checked for Map membership
     * and Map structures have been updated accordingly by
     * checkAndUpdateMapStructuresOnAdd().
     * </p>
     * 
     * <p>
     * Notifies registered observers of the change.
     * </p>
     * 
     * @param newChild
     *            the child IdeaNode to be added
     * @param mindMap
     *            the MindMap instance containing the IdeaNode
     * 
     * @return the EAddChildIdeaNodeResultCase
     * 
     * @see #checkAndUpdateMapStructuresOnAdd
     */
    public EAddChildIdeaNodeResultCase addIdeaChild(IdeaNode newChild,
            MindMap mindMap) {

        log.debug("Entering addChild(newChild:" + newChild + ", mindMap:" + mindMap + "), Context: " + this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if (newChild != null && mindMap != null) {
            // Constraint: IdeaNode don't add and return if
            // newChild already has a parent
            if (newChild.getIsChild()) {
                log.warn("Leaving addChild() false; on error, newChild already has a parent!"); //$NON-NLS-1$
                // TODO message to view to show a error overlay?
                return EAddChildIdeaNodeResultCase.NO_ADD_ON_CONSTRAINT;
            }

            // newChild is parent of this IdeaNode
            if (newChild.getChildren().contains(this)) {
                log.warn("Leaving addChild(): false, on error, newChild is parent of this IdeaNode!"); //$NON-NLS-1$
                // TODO message to view to show a error overlay?
                return EAddChildIdeaNodeResultCase.NO_ADD_ON_CONSTRAINT;
            }

            // newChild is already part of the Map that this IdeaNode is in
            if (this.isChild) {
                // Search for new IdeaNode in the Map for this IdeaNode
                Map resultingMap = mindMap.findMapByChildNode(this);
                if (resultingMap != null) {

                    if (resultingMap.containsIdeaNode(newChild)) {
                        log.debug("Leaving addChild(): false, newChild is part of the Map that this IdeaNode is part of"); //$NON-NLS-1$
                        return EAddChildIdeaNodeResultCase.NO_ADD_ON_CONSTRAINT;
                    }

                } else {
                    log.error("Leaving addChild() false; on error"); //$NON-NLS-1$
                    return EAddChildIdeaNodeResultCase.NO_ADD_ON_ERROR;
                }
            }

            // Check for constraints and update Maps accordingly
            EAddChildIdeaNodeResultCase result = checkAndUpdateMapStructuresOnAdd(
                    newChild, mindMap);
            if (result.equals(EAddChildIdeaNodeResultCase.NO_ADD_ON_ERROR)) {
                log.error("Leaving addChild() false; on error"); //$NON-NLS-1$
                // TODO send message?
                return EAddChildIdeaNodeResultCase.NO_ADD_ON_ERROR;
            }

            if (result.equals(EAddChildIdeaNodeResultCase.NO_ADD_ON_CONSTRAINT)) {
                log.info("Leaving addChild() false; on error"); //$NON-NLS-1$
                // TODO send message?
                return EAddChildIdeaNodeResultCase.NO_ADD_ON_CONSTRAINT;
            }

            // Add newChild to this ideaNode
            if (super.addChild(newChild)) {

                // Set properties
                newChild.setIsChild(true);
                newChild.setParentID(this.ideaID);

                // Communicate changes to observers
                communicateChangesToObserver(new ObserverNotificationObject(
                        EIdeaNodeChangedStatus.IDEA_NODE_CHILD_ADDED, newChild));

                log.debug("Leaving addChild()"); //$NON-NLS-1$
                return result;

            } // if adding fails
            log.debug("Leaving addChild() false; on error"); //$NON-NLS-1$
            // TODO send message?
            return EAddChildIdeaNodeResultCase.NO_ADD_ON_ERROR;

        }
        log.debug("Leaving addChild(): invalid null input"); //$NON-NLS-1$
        return EAddChildIdeaNodeResultCase.NO_ADD_ON_ERROR;
    }

    /**
     * <p>
     * Removes an IdeanNode child from the IdeaNode object via the superclass
     * Node<NodeData> after both IdeaNodes have been checked for Map membership
     * and Map structures have been updated accordingly by
     * checkAndUpdateMapStructuresOnRemove().
     * </p>
     * 
     * <p>
     * Notifies registered observers of the change.
     * </p>
     * 
     * 
     * @param toBeRemovedChild
     *            the IdeaNode child to be removed
     * @param mindMap
     *            the MindMap instance containing the IdeaNode
     * @return the ERemoveChildIdeaNodeResultCase
     * 
     * @see #checkAndUpdateMapStructuresOnRemove
     */
    public ERemoveChildIdeaNodeResultCase removeIdeaChild(
            IdeaNode toBeRemovedChild, MindMap mindMap) {

        log.debug("Entering removeIdeaChild(toBeRemovedChild: " + toBeRemovedChild + ", mindMap: " + mindMap + ") Context: " + this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if (toBeRemovedChild != null && mindMap != null) {
            // Constraint: toBeRemovedChild must be a child of this IdeaNode
            // if (!checkIfIdeaNodeIsChild(toBeRemovedChild)) {
            if (!(this.getChildren().contains(toBeRemovedChild))) {

                log.error("Leaving removeIdeaChild() false; on error: toBeRemovedChild is not a child of this IdeaNode!"); //$NON-NLS-1$
                return ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_CONSTRAINT;
            }

            // check for Map constraints and update Maps accordingly
            ERemoveChildIdeaNodeResultCase result = checkAndUpdateMapStructuresOnRemove(
                    toBeRemovedChild, mindMap);
            if (result
                    .equals(ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_ERROR)) {
                log.debug("Leaving removeIdeaChild(): false,  on error"); //$NON-NLS-1$
                // TODO send message?
                return result;
            }

            if (super.removeChild(toBeRemovedChild)) {

                toBeRemovedChild.setIsChild(false);
                toBeRemovedChild.setParentID(NO_PARENT);

                // Communicate changes to observers
                communicateChangesToObserver(new ObserverNotificationObject(
                        EIdeaNodeChangedStatus.IDEA_NODE_CHILD_REMOVED,
                        toBeRemovedChild));

                log.debug("Leaving removeIdeaChild(): true"); //$NON-NLS-1$
                return result;

            } // if removing fails

            log.debug("Leaving addChild() false; on error"); //$NON-NLS-1$
            // TODO send message?
            return ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_ERROR;

        }
        log.debug("Leaving removeIdeaChild(): false, invalid null input"); //$NON-NLS-1$
        return ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_ERROR;

    }

    /**
     * Returns the number of immediate children of this Node<T> via the
     * superclass Node<NodeContentContainer>.
     * 
     * @return the number of immediate children.
     */
    @Override
    protected int getNumberOfChildren() {

        log.debug("Entering getNumberOfChildren()"); //$NON-NLS-1$
        log.debug("Leaving getNumberOfChildren(): " + super.getNumberOfChildren()); //$NON-NLS-1$
        return super.getNumberOfChildren();
    }

    /**
     * Returns if the IdeaNode is parent to another IdeaNode.
     * 
     * @return isParent
     */
    public boolean isParent() {
        log.debug("Entering isParent()"); //$NON-NLS-1$
        if (getChildren() != null && getChildren().size() > 0) {
            log.debug("Leaving isParent(): true"); //$NON-NLS-1$
            return true;
        }
        log.debug("Leaving isParent(): false"); //$NON-NLS-1$
        return false;
    }

    /* *********Utility methods********* */
    /**
     * <p>
     * Submethod called when adding a new child IdeaNode to a IdeaNode object by
     * {@link #addIdeaChild(IdeaNode, MindMap)}.
     * </p>
     * 
     * <p>
     * Checks for already existing Maps that contain either the newChild or this
     * IdeaNode object and updates the Map(s) in the given MindMap accordingly.
     * </p>
     * 
     * Cases:
     * <p>
     * (1) This IdeaNode is a single idea node <br/>
     * &nbsp;&nbsp;(1.1) newChild is already a root node of a Map: set root node
     * to this IdeaNode <br/>
     * &nbsp;&nbsp;(1.2) newChild is a single IdeaNode: add a new Map to the
     * MindMap
     * </p>
     * <p>
     * (2) This IdeaNode is a root node OR a child in a Map <br/>
     * &nbsp;&nbsp;(2.1) newChild is already a root node of a Map: delete that
     * Map from MindMap, as newChild will be part of the Map that contains this
     * IdeaNode <br/>
     * &nbsp;&nbsp;(2.2) newChild is a single IdeaNode: no Map operations
     * necessary, as newChild will be part of the Map that contains this
     * IdeaNode
     * </p>
     * 
     * 
     * @param newChild
     *            the child IdeaNode to be added
     * @param mindMap
     *            the MindMap instance containing the IdeaNodes
     * @return EAddChildIdeaNodeResultCase the EAddChildIdeaNodeResultCase
     * 
     * @see de.sarahw.ma.pc.mindMapper.model.IdeaNode#addIdeaChild(IdeaNode,
     *      MindMap)
     */
    private EAddChildIdeaNodeResultCase checkAndUpdateMapStructuresOnAdd(
            IdeaNode newChild, MindMap mindMap) {

        log.debug("Entering checkAndUpdateMapStructuresOnAdd(newChild:" + newChild + ", mindMap: )" + mindMap + " Context: " + this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if (newChild != null && mindMap != null) {

            // Case 1: this IdeaNode is a single IdeaNode
            if (this.isParent() == false && this.getIsChild() == false) {

                log.debug(" Case 1: The IdeaNode is a single IdeaNode"); //$NON-NLS-1$
                // Case 1.1: if newChild is already a rootNode of a Map, set
                // that
                // rootNode
                // anew
                if (newChild.isParent() == true) {

                    log.debug(" Case 1.1: newChild is root node of a Map, modify existing Map"); //$NON-NLS-1$

                    // find the Map which has newChild as root node
                    Map mapWithChildAsRootNode = mindMap
                            .findMapByRootNode(newChild);
                    if (mapWithChildAsRootNode == null) {
                        log.error("Leaving checkAndUpdateMapStructuresOnAdd(): Map with root node newChild could not be found!"); //$NON-NLS-1$
                        // TODO message to view to show a error overlay?
                        return EAddChildIdeaNodeResultCase.NO_ADD_ON_ERROR;
                    }
                    mapWithChildAsRootNode.setRootNode(this);

                    log.debug("Leaving checkAndUpdateMapStructuresOnAdd(): EAddChildIdeaNodeResultCase.C1_1_P_SINGLE_C_MAP_MODIFY"); //$NON-NLS-1$

                    return EAddChildIdeaNodeResultCase.C1_1_P_SINGLE_C_MAP_MODIFY;

                } // newChild is a single IdeaNode

                log.debug(" Case 1.2: newChild is a single IdeaNode, create new Map"); //$NON-NLS-1$
                // create a new map with this IdeaNode as root node
                mindMap.addMap(new Map(this));

                log.debug("Leaving checkAndUpdateMapStructuresOnAdd(): EAddChildIdeaNodeResultCase.C1_2_P_SINGLE_C_SINGLE_CREATE_MAP"); //$NON-NLS-1$

                return EAddChildIdeaNodeResultCase.C1_2_P_SINGLE_C_SINGLE_CREATE_MAP;

            } // this IdeaNode is part of a map either as parent or
              // child

            log.debug(" Case 2: this IdeaNode is part of a Map"); //$NON-NLS-1$

            // if newChild is already a rootNode of a Map
            // delete that Map
            // cause newChild will be part of the map containing this
            // IdeaNode
            if (newChild.isParent() == true) {

                log.debug(" Case 2.1: newChild is root node of a Map, delete that Map"); //$NON-NLS-1$        
                // find the Map which has newChild as root node
                Map mapWithChildAsRootNode = mindMap
                        .findMapByRootNode(newChild);

                if (mapWithChildAsRootNode == null) {
                    log.error("Leaving checkAndUpdateMapStructuresOnAdd(): Map with root node newChild could not be found!"); //$NON-NLS-1$ 
                    return EAddChildIdeaNodeResultCase.NO_ADD_ON_ERROR;
                }

                mindMap.removeMap(mapWithChildAsRootNode);

                log.debug("Leaving checkAndUpdateMapStructuresOnAdd(): EAddChildIdeaNodeResultCase.C2_1_P_MAP_C_MAPPARENT_DELETE_MAP"); //$NON-NLS-1$

                return EAddChildIdeaNodeResultCase.C2_1_P_MAP_C_MAPPARENT_DELETE_MAP;

            } // newChild is a single IdeaNode
              // no map operations necessary
            log.debug(" Case 2.2: newChild is a single IdeaNode \nNo map operations necessary"); //$NON-NLS-1$

            log.debug("Leaving checkAndUpdateMapStructuresOnAdd(): EAddChildIdeaNodeResultCase.C2_2_P_MAP_C_SINGLE_NOTHING"); //$NON-NLS-1$

            return EAddChildIdeaNodeResultCase.C2_2_P_MAP_C_SINGLE_NOTHING;

        }
        log.debug("Leaving checkAndUpdateMapStructuresOnAdd(): false, invalid null input"); //$NON-NLS-1$
        return EAddChildIdeaNodeResultCase.NO_ADD_ON_ERROR;
    }

    /**
     * <p>
     * Submethod called when removing a child IdeaNode from a IdeaNode children
     * list by {@link #removeIdeaChild(IdeaNode, MindMap)}.
     * </p>
     * 
     * <p>
     * Checks the existing Map structure that this IdeaNode and the
     * toBeRemovedChild belong to and updates the Map in the given MindMap
     * accordingly.
     * </p>
     * 
     * Cases:
     * <p>
     * (1) The toBeRemovedChild has no children<br/>
     * &nbsp;&nbsp;(1.1) This IdeaNode has no other child than toBeRemovedChild
     * AND is no child itself (= is the root node of the Map they are in):
     * Remove the Map that the two IdeaNodes form.<br/>
     * &nbsp;&nbsp;(1.2) This IdeaNode has more children besides the
     * toBeRemovedChild OR is itself a child of another IdeaNode: No map
     * operations necessary.
     * </p>
     * <p>
     * (2) The toBeRemovedChild has one or more children <br/>
     * &nbsp;&nbsp;(2.1)This IdeaNode has no other child than toBeRemovedChild
     * AND is no child itself (= is the root node of the Map they are in):
     * Change Map root node to toBeRemovedChild. <br/>
     * &nbsp;&nbsp;(2.2) This IdeaNode has more children besides the
     * toBeRemovedChild OR is itself a child of another IdeaNode: Create a new
     * Map with the toBeRemovedChild as the root node.
     * </p>
     * 
     * 
     * @param toBeRemovedChild
     *            the child IdeaNode to be removed
     * @param mindMap
     *            the MindMap instance containing the IdeaNodes
     * @return the ERemoveChildIdeaNodeResultCase
     * 
     * @see de.sarahw.ma.pc.mindMapper.model.IdeaNode#removeIdeaChild(IdeaNode,
     *      MindMap)
     */
    private ERemoveChildIdeaNodeResultCase checkAndUpdateMapStructuresOnRemove(
            IdeaNode toBeRemovedChild, MindMap mindMap) {

        log.debug("Entering checkAndUpdateMapStructuresOnRemove(toBeRemovedChild= " + toBeRemovedChild + ", mindMap=" + mindMap + ") Context: " + this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$        
        if (toBeRemovedChild != null && mindMap != null) {

            // the toBeRemovedChild is not a parent of other IdeaNodes
            if (!toBeRemovedChild.isParent()) {

                log.debug(" Case 1:  toBeRemovedChild has no children"); //$NON-NLS-1$ 
                // this IdeaNode has no other child than toBeRemovedChild
                // AND is
                // no
                // child
                if (this.getNumberOfChildren() == 1
                        && this.getIsChild() == false) {

                    log.debug(" Case 1.1:  this IdeaNode has no other child and is no child itself, remove Map"); //$NON-NLS-1$ 
                    // remove the Map that contains this IdeaNode and the
                    // toBeRemovedChild
                    // find Map by root node
                    Map obsoleteMap = mindMap.findMapByRootNode(this);
                    if (obsoleteMap == null) {
                        log.error("Leaving checkAndUpdateMapStructuresOnRemove(): Map with root node this IdeaNode could not be found!"); //$NON-NLS-1$ 
                        return ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_ERROR;
                    }

                    if (mindMap.removeMap(obsoleteMap)) {
                        log.debug("Leaving checkAndUpdateMapStructuresOnRemove(): true"); //$NON-NLS-1$
                        return ERemoveChildIdeaNodeResultCase.C1_1_P_SINGLE_C_SINGLE_REMOVE_MAP;
                    }
                    log.error("Leaving checkAndUpdateMapStructuresOnRemove(): Map could not be removed!"); //$NON-NLS-1$ 
                    return ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_ERROR;

                } // this IdeaNode either has more than one children
                  // or
                  // is a
                  // child itself
                  // no map operations necessary.
                log.debug(" Case 1.2:  this IdeaNode either has more than one children or is a child itself \nNo map operations necessary"); //$NON-NLS-1$

                return ERemoveChildIdeaNodeResultCase.C1_2_P_MAP_C_SINGLE_NOTHING;

            } // the child to be removed has children itself

            log.debug(" Case 2:  toBeRemovedChild has children"); //$NON-NLS-1$ 
            // this IdeaNode has no other child than toBeRemovedChild
            // AND is
            // no
            // child
            if ((this.getNumberOfChildren() == 1 && this.getIsChild() == false)) {

                log.debug(" Case 2.1:  this IdeaNode has no other child and is no child itself, modify Map "); //$NON-NLS-1$ 
                // change Map root node from this IdeaNode to
                // toBeRemovedChild
                Map obsoleteMap = mindMap.findMapByRootNode(this);

                if (obsoleteMap == null) {
                    log.error("Leaving checkAndUpdateMapStructuresOnRemove(): Map with root node this IdeaNode could not be found!"); //$NON-NLS-1$ 
                    return ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_ERROR;
                }

                if (obsoleteMap.setRootNode(toBeRemovedChild)) {

                    log.debug("Leaving checkAndUpdateMapStructuresOnRemove(): true"); //$NON-NLS-1$
                    return ERemoveChildIdeaNodeResultCase.C2_1_P_SINGLE_C_MAP_MODIFY_MAP;
                }
                log.error("Leaving checkAndUpdateMapStructuresOnRemove(): Setting the root node anew not successful!"); //$NON-NLS-1$ 
                return ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_ERROR;

            } // this IdeaNode either has more than one children
              // or is
              // a
              // child itself

            log.debug("Case 2.1:  this IdeaNode either has more than one children or is a child itself, create new Map for newChild"); //$NON-NLS-1$

            // create a new Map with toBeRemovedChild as root node
            if (mindMap.addMap(new Map(toBeRemovedChild))) {
                log.debug("Leaving checkAndUpdateMapStructuresOnRemove(): true"); //$NON-NLS-1$
                return ERemoveChildIdeaNodeResultCase.C2_2_P_MAP_C_MAP_NEW_MAP;

            }
            log.error("Leaving checkAndUpdateMapStructuresOnRemove(): Map could not be created!"); //$NON-NLS-1$ 
            return ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_ERROR;

        }
        log.debug("Leaving checkAndUpdateMapStructuresOnRemove(): false, invalid null input"); //$NON-NLS-1$
        return ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_ERROR;
    }

    /**
     * Communicates changes to all registered Observers.
     * 
     * @param object
     *            the observer notification object
     */
    protected void communicateChangesToObserver(
            ObserverNotificationObject object) {
        log.debug("Entering communicateChangesToObserver()"); //$NON-NLS-1$
        setChanged();
        notifyObservers(object);
        log.debug("Leaving communicateChangesToObserver()"); //$NON-NLS-1$
    }

    /* ********Overridden methods******** */
    /**
     * Returns a String representation of an IdeaNode object and its values.
     * 
     * @return String representation of IdeaNode.
     */
    @Override
    public String toString() {
        return "IdeaNode [parentID=" + getParentID() + ", ideaID=" + getIdeaID() //$NON-NLS-1$//$NON-NLS-2$
                + ", isChild=" + getIsChild() + "]"; //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Returns a hash code value for the IdeaNode.
     * 
     * @return a hash code value for this IdeaNode.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (this.isChild ? 1231 : 1237);
        result = prime * result
                + (int) (this.parentID ^ (this.parentID >>> 32));
        return result;
    }

    /**
     * Compares the specified object with this IdeaNode for equality. Returns
     * true if and only if the specified object is also a IdeaNode with the same
     * elements.
     * 
     * @return true if the specified object is equal to this IdeaNode.
     */
    @Override
    public boolean equals(Object obj) {
        log.trace("Entering equals(obj= )"); //$NON-NLS-1$
        if (this == obj) {
            log.trace("Leaving equals(): true; same reference"); //$NON-NLS-1$
            return true;
        }

        if (!super.equals(obj)) {
            log.trace("Leaving equals(): false; equals() false for superclass"); //$NON-NLS-1$
            return false;
        }
        if (!(obj instanceof IdeaNode)) {
            log.trace("Leaving equals(): false; no istanceof IdeaNode"); //$NON-NLS-1$
            return false;
        }
        IdeaNode other = (IdeaNode) obj;
        if (this.ideaID != other.ideaID) {
            log.trace("Leaving equals(): false; Different ideaID! this: " + this.ideaID + ", other: " + other.ideaID); //$NON-NLS-1$//$NON-NLS-2$
            return false;
        }
        if (this.isChild != other.isChild) {
            log.trace("Leaving equals(): false; Different isChild! this: " + this.isChild + ", other: " + other.isChild); //$NON-NLS-1$//$NON-NLS-2$
            return false;
        }
        if (this.parentID != other.parentID) {
            log.trace("Leaving equals(): false; Different parentID! this: " + this.parentID + ", other: " + other.parentID); //$NON-NLS-1$//$NON-NLS-2$
            return false;
        }
        log.trace("Leaving equals(): true; same object"); //$NON-NLS-1$
        return true;
    }

}
