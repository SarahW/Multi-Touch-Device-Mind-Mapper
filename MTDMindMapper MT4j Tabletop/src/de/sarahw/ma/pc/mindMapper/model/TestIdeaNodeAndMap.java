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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for the classes {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode}
 * and {@link de.sarahw.ma.pc.mindMapper.model.Map}
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */
@SuppressWarnings("nls")
public class TestIdeaNodeAndMap {

    /** Test mindMap */
    private MindMap  testMindMap;
    /** First test ideaNode */
    private IdeaNode testIdeaNode1;
    /** Second test ideaNode */
    private IdeaNode testIdeaNode2;
    /** Third test ideaNode */
    private IdeaNode testIdeaNode3;
    /** Fourth test ideaNode */
    private IdeaNode testIdeaNode4;

    /* *********Fixture methods********* */
    /**
     * Sets up the fixture. Instantiates a new MindMap and adds two IdeaNodes.
     * This method is called before a test is executed.
     * 
     */
    @Before
    public void setUp() {
        // Create a new test mindMap
        MindMap newMindMap = new MindMap();
        this.testMindMap = newMindMap;

        // Create new test IdeaNodes and add to mindMap
        IdeaNode firstTestNode = new IdeaNode(new NodeData(new NodeContent(
                "Idee1"), //$NON-NLS-1$
                new NodeMetaData(112, 843, 23.0f,
                        EIdeaNodeCreator.BLUETOOTH_SERVER)));
        this.testIdeaNode1 = firstTestNode;

        IdeaNode secondTestNode = new IdeaNode(new NodeData(new NodeContent(
                "Idee2"), //$NON-NLS-1$
                new NodeMetaData(66, 134, 44.0f,
                        EIdeaNodeCreator.MULTITOUCH_TABLE)));

        this.testIdeaNode2 = secondTestNode;

        this.testMindMap.addIdeaNode(this.testIdeaNode1);
        this.testMindMap.addIdeaNode(this.testIdeaNode2);

    }

    /**
     * Tears down the fixture. Sets all members null. This method is called
     * after a test is executed.
     * 
     */
    @After
    public void tearDown() {
        this.testIdeaNode1 = null;
        this.testIdeaNode2 = null;
        this.testIdeaNode3 = null;
        this.testIdeaNode4 = null;
        this.testMindMap = null;
    }

    @Test
    public void testMembersAfterInitialization() {

        // Check if members are not null
        assertNotNull(
                "Error: testIdeaNode1 has not been initialized correctly",
                this.testIdeaNode1);
        assertNotNull(
                "Error: testIdeaNode2 has not been initialized correctly",
                this.testIdeaNode2);
        assertNotNull("Error: testMindMap has not been initialized correctly",
                this.testMindMap);

        // Check if NodeData for both test IdeaNodes is available
        assertNotNull("The IdeaNode1 has no NodeData!!",
                this.testIdeaNode1.getData());
        assertNotNull("The IdeaNode2 has no NodeData!!",
                this.testIdeaNode2.getData());

        // Check if children list is available and empty for both test IdeaNodes
        assertNotNull("Children for IdeaNode2 have not been initalized!!",
                this.testIdeaNode1.getChildren());
        assertTrue("Children for IdeaNode1 have been initialized wrong!",
                this.testIdeaNode1.getChildren().size() == 0);

        assertNotNull("Children for IdeaNode1 have not been initalized!!",
                this.testIdeaNode2.getChildren());
        assertTrue("Children for IdeaNode1 have been initialized wrong!",
                this.testIdeaNode2.getChildren().size() == 0);

        // Check if ideaCount for class IdeaNodeView is correct
        assertTrue("IdeaNodeView ideaCount is wrong!, should be 2 but is "
                + IdeaNode.getIdeaCount() + " instead!",
                IdeaNode.getIdeaCount() == 2);

        // Check if parentIDs are NO_PARENT
        assertTrue("ParentID has been assigned wrong to IdeaNode1",
                this.testIdeaNode1.getParentID() == IdeaNode.NO_PARENT);
        assertTrue("ParentID has been assigned wrong to IdeaNode2",
                this.testIdeaNode2.getParentID() == IdeaNode.NO_PARENT);

        // Check if ideaID is initialized and if the two differ
        assertTrue("ideaID for IdeaNode1 is not initialized!",
                this.testIdeaNode1.getIdeaID() != 0);
        assertTrue("ideaID for IdeaNode2 is not initialized!",
                this.testIdeaNode2.getIdeaID() != 0);
        assertTrue("ideaIDs for both IdeaNodes are the same!",
                this.testIdeaNode1.getIdeaID() != this.testIdeaNode2
                        .getIdeaID());

        // Check if isChild is initialized and false
        assertFalse(
                "isChild has been initialized wrong for IdeaNode1! Should be false",
                this.testIdeaNode1.getIsChild());
        assertFalse(
                "isChild has been initialized wrong IdeaNode2! Should be false",
                this.testIdeaNode2.getIsChild());

        // Check if values for the ideaText are correct
        assertTrue("ideaText for IdeaNode1 has been set wrong!",
                this.testIdeaNode1.getIdeaText().contentEquals("Idee1"));
        assertTrue("ideaText for IdeaNode2 has been set wrong!",
                this.testIdeaNode2.getIdeaText().contentEquals("Idee2"));

        // Check if values for the ideaPosition are correct
        assertTrue("Position x for IdeaNode1 has been set wrong!",
                this.testIdeaNode1.getIdeaPositionX() == 112);
        assertTrue("Position y for IdeaNode1 has been set wrong!",
                this.testIdeaNode1.getIdeaPositionY() == 843);

        assertTrue("Position x for IdeaNode2 has been set wrong!",
                this.testIdeaNode2.getIdeaPositionX() == 66);
        assertTrue("Position y for IdeaNode2 has been set wrong!",
                this.testIdeaNode2.getIdeaPositionY() == 134);

        // Check if values for the ideaAngleToEquator are correct
        assertTrue("ideaAngleToEquator for IdeaNode1 has been set wrong!",
                this.testIdeaNode1.getIdeaRotationInDegrees() == 23.0f);
        assertTrue("ideaAngleToEquator for IdeaNode2 has been set wrong!",
                this.testIdeaNode2.getIdeaRotationInDegrees() == 44.0f);

        // Check if values for idOwner are correct
        assertTrue(
                "ideaOwner for IdeaNode1 has been set wrong!",
                this.testIdeaNode1.getIdeaOwner().equals(
                        EIdeaNodeCreator.BLUETOOTH_SERVER));
        assertTrue(
                "ideaOwner for IdeaNode2 has been set wrong!",
                this.testIdeaNode2.getIdeaOwner().equals(
                        EIdeaNodeCreator.MULTITOUCH_TABLE));

        // Check mindMap members
        // Check if an mindMapID has been set
        assertTrue("Error: no mindMapID has been set",
                this.testMindMap.getMindMapId() != 0);

        // Check if title has been set
        assertNotNull("Error: no mindMapTitle has been set!",
                this.testMindMap.getMindMapTitle());

        // Check if lists have been initialized
        assertNotNull(
                "Error: mindMapIdeaNodeList has not been initialized correctly",
                this.testMindMap.getMindMapIdeaNodeList());
        assertNotNull(
                "Error: mindMapMapList has not been initialized correctly",
                this.testMindMap.getMindMapList());

        // Check if there are exactly 0 Maps in the MindMap
        assertTrue("Error: there are more than 0 Maps in the MindMap",
                this.testMindMap.getMindMapList().size() == 0);

        // Check if there are exactly 2 IdeaNodes in the MindMap
        assertTrue(
                "Error: there are more or less than 2 IdeaNodes in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().size() == 2);

        // Check if the IdeaNodes are stored in the MindMap mindMapIdeaNodeList
        assertTrue(
                "Error: testIdeaNode1 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode1));
        assertTrue(
                "Error: testIdeaNode2 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode2));

    }

    /**
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#hashCode()}.
     */
    @Test
    public void testHashCode() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#toString()}.
     */
    @Test
    public void testToString() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject() {
        fail("Not yet implemented");
    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#addIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case: Added IdeaNodeView is null
     * </p>
     * 
     * 
     */
    @Test
    public void testAddIdeaChildCaseIdeaNodeNull() {
        // Add null IdeaNodeView as child to testIdeaNode1
        assertTrue(
                "Error: it should not be able to add null IdeaNodes as children to an IdeaNodeView",
                this.testIdeaNode1.addIdeaChild(null, this.testMindMap) == EAddChildIdeaNodeResultCase.NO_ADD_ON_ERROR);

        // Check if testIdeaNode1 contains a null child
        assertFalse("Error: testIdeaNode1 must not contain a null child ",
                this.testIdeaNode1.getChildren().contains(null));

        // There must be exactly 0 Maps in the MindMap
        assertTrue("Error: there must not be a Map in the MindMap",
                this.testMindMap.getMindMapList().size() == 0);

    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#addIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case: Referenced MindMap is null
     * </p>
     * 
     * 
     */
    @Test
    public void testAddIdeaChildCaseMindMapNull() {
        // Add null IdeaNodeView as child to testIdeaNode1
        assertTrue(
                "Error: it should not be able to add IdeaNodes as children to an IdeaNodeView within a null MindMap",
                this.testIdeaNode1.addIdeaChild(this.testIdeaNode2, null) == EAddChildIdeaNodeResultCase.NO_ADD_ON_ERROR);

        // Check if testIdeaNode1 contains testIdeaNode2 as child
        assertFalse(
                "Error: testIdeaNode1 must not contain testIdeaNode2 as child ",
                this.testIdeaNode1.getChildren().contains(this.testIdeaNode2));

    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#addIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * as well as
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#checkAndUpdateMapStructuresOnAdd(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case: 1.2 <br/>
     * (1) This testIdeaNode1 is a single idea node <br/>
     * &nbsp;&nbsp;(1.2) testIdeaNode2 is a single IdeaNodeView: add a new Map
     * to the MindMap
     * </p>
     * 
     * 
     */
    @Test
    public void testAddIdeaChildCase1_2() {

        // Add testIdeaNode2 as a child of testIdeaNode2
        addIdea2asChildofIdea1();

    }

    /**
     * Subtestmethod that adds testIdeaNode2 as child to testIdeaNode1
     * 
     * Must be called right after the test initialization to work correctly.
     */
    private void addIdea2asChildofIdea1() {

        // Add testIdeaNode2 as child to testIdeaNode1
        assertTrue(
                "Error while adding testIdeaNode2 as child to testIdeaNode1",
                this.testIdeaNode1.addIdeaChild(this.testIdeaNode2,
                        this.testMindMap) == EAddChildIdeaNodeResultCase.C1_2_P_SINGLE_C_SINGLE_CREATE_MAP);

        // Check if testIdeaNode2 is a child of testIdeaNode1
        assertTrue(
                "Error while adding testIdeaNode2 as child to testIdeaNode1",
                this.testIdeaNode1.getChildren().contains(this.testIdeaNode2));

        // Check if isChild values are correct
        assertFalse("Error: testIdeaNode1 should not be a child of any node",
                this.testIdeaNode1.getIsChild());
        assertTrue("Error: testIdeaNode2 should be a child of testIdeaNode1",
                this.testIdeaNode2.getIsChild());

        // Check if return values for isParent() are correct
        assertTrue("Error: testIdeaNode1 should be a parent to testIdeaNode2",
                this.testIdeaNode1.isParent());
        assertFalse("Error: testIdeaNode2 should not be a parent to any node",
                this.testIdeaNode2.isParent());

        // Check if the parentID of testIdeaNode2 matches the ideaNodeID of
        // testIdeaNode1
        assertTrue(
                "Error: The parentID of testIdeaNode2 should be the ideaNodeID of testIdeaNode1",
                this.testIdeaNode2.getParentID() == this.testIdeaNode1
                        .getIdeaID());

        // There should be exactly one Map in the MindMap
        assertTrue("Error: There is less or more than one Map in the MindMap",
                this.testMindMap.getMindMapList().size() == 1);

        // Check if there this Map has testIdeaNode1 as the root node
        Map searchedMap = this.testMindMap.getMindMapList().get(0);
        assertNotNull(
                "Error: testIdeaNode1 has not been found as the root node of a Map in the MindMap",
                searchedMap);
        assertTrue(
                "Error: testIdeaNode1 has not been found as the root node of a Map in the MindMap",
                searchedMap.getRootNode().equals(this.testIdeaNode1));

        // Check if testIdeaNode2 is the sole child of testIdeaNode2
        assertTrue(
                "Error: testIdeaNode1 has more or less than one child in the found Map",
                searchedMap.getRootNode().getNumberOfChildren() == 1);

        IdeaNode childNode = (IdeaNode) searchedMap.getRootNode().getChildren()
                .get(0);
        assertTrue(
                "Error: the child of testIdeaNode1 in the Map is not testIdeaNode2",
                childNode.equals(this.testIdeaNode2));
    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#addIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * as well as
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#checkAndUpdateMapStructuresOnAdd(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case: 1.1 <br/>
     * (1) This IdeaNodeView is a single idea node <br/>
     * &nbsp;&nbsp;(1.1)newChild is already a root node of a Map: set root node
     * to this IdeaNodeView <br/>
     * </p>
     * 
     * 
     */
    @Test
    public void testAddIdeaChildCase1_1() {

        // Run subroutine for adding testIdeaNode2 as a child to testIdeaNode1
        addIdea2asChildofIdea1();

        // Add third Idea as a parent testIdeaNode1
        addIdea3asParentOfIdea1();

    }

    /**
     * Subtestmethod that adds a third test node as the parent of testIdeaNode1
     * 
     * Requires the calling of the following routines in this order beforehand
     * to work correctly: {@link #addIdea2asChildofIdea1()}
     */
    private void addIdea3asParentOfIdea1() {

        // Create a third test IdeaNodeView
        this.testIdeaNode3 = new IdeaNode(new NodeData(
                new NodeContent("Idee3"), //$NON-NLS-1$
                new NodeMetaData(45, 123, 1.0f,
                        EIdeaNodeCreator.MULTITOUCH_TABLE)));

        // Add testIdeaNode3 to testMindMap
        this.testMindMap.addIdeaNode(this.testIdeaNode3);

        // Add testIdeaNode1 as child of testIdeaNode3
        assertTrue(
                "Error while adding testIdeaNode1 as child to testIdeaNode3",
                this.testIdeaNode3.addIdeaChild(this.testIdeaNode1,
                        this.testMindMap) == EAddChildIdeaNodeResultCase.C1_1_P_SINGLE_C_MAP_MODIFY);

        // Check if testIdeaNode1 is a child of testIdeaNode3
        assertTrue("Error: testIdeaNode1 must be a child of testIdeaNode3",
                this.testIdeaNode3.getChildren().contains(this.testIdeaNode1));

        // Check if isChild values are correct
        assertTrue("Error: testIdeaNode1 should be a child of testIdeaNode3",
                this.testIdeaNode1.getIsChild());
        assertFalse(
                "Error: testIdeaNode3 should not be a child of any ideaNode",
                this.testIdeaNode3.getIsChild());
        assertTrue("Error: testIdeaNode2 should be a child of testIdeaNode1",
                this.testIdeaNode2.getIsChild());

        // Check if return values for isParent() are correct
        assertTrue("Error: testIdeaNode3 should be a parent to testIdeaNode1",
                this.testIdeaNode3.isParent());
        assertTrue("Error: testIdeaNode1 should be a parent to testIdeaNode2",
                this.testIdeaNode1.isParent());
        assertFalse(
                "Error: testIdeaNode2 should not be a parent to any IdeaNodeView",
                this.testIdeaNode2.isParent());

        // Check if the parentID of testIdeaNode1 matches the ideaNodeID of
        // testIdeaNode3
        assertTrue(
                "Error: The parentID of testIdeaNode1 should be the ideaNodeID of testIdeaNode3",
                this.testIdeaNode1.getParentID() == this.testIdeaNode3
                        .getIdeaID());

        // There should be exactly one Map in the MindMap
        assertTrue("Error: There is less or more than one Map in the MindMap",
                this.testMindMap.getMindMapList().size() == 1);

        // Check if this is a Map with testIdeaNode3 as the root node
        Map searchedMap = this.testMindMap.getMindMapList().get(0);
        assertNotNull(
                "Error: testIdeaNode3 has not been found as the root node of a Map in the MindMap",
                searchedMap);
        assertTrue(
                "Error: testIdeaNode3 has not been found as the root node of a Map in the MindMap",
                searchedMap.getRootNode().equals(this.testIdeaNode3));
    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#addIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * as well as
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#checkAndUpdateMapStructuresOnAdd(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case: 2(a).1 <br/>
     * (2) This IdeaNodeView is a root node OR (b) ...
     * {@link #testAddIdeaChildCase2_b_1()} <br/>
     * &nbsp;&nbsp;(2.1) newChild is already a root node of a Map: delete that
     * Map from MindMap, as newChild will be part of the Map that contains this
     * IdeaNodeView
     * </p>
     * 
     * 
     */
    @Test
    public void testAddIdeaChildCase2_a_1() {

        // Run test for adding testIdeaNode2 as a child to testIdeaNode1
        addIdea2asChildofIdea1();

        // Add two new IdeaNodes
        addIdea3andIdea4();

        // Add fourth Idea as child of third idea
        addIdea4asChildofIdea3();

        // Add third Idea as child of Idea one
        addIdea3AsChildofIdea1();

    }

    /**
     * Subtestmethod for adding a third and a fourth idea
     * 
     */
    private void addIdea3andIdea4() {

        // Create a third and fourth test IdeaNodeView
        this.testIdeaNode3 = new IdeaNode(new NodeData(
                new NodeContent("Idee3"), //$NON-NLS-1$
                new NodeMetaData(45, 123, 1.0f,
                        EIdeaNodeCreator.MULTITOUCH_TABLE)));

        this.testIdeaNode4 = new IdeaNode(new NodeData(
                new NodeContent("Idee4"), //$NON-NLS-1$
                new NodeMetaData(22, 678, 23.0f,
                        EIdeaNodeCreator.MULTITOUCH_TABLE)));

        // Add testIdeaNode3 to testMindMap
        this.testMindMap.addIdeaNode(this.testIdeaNode3);

        // Add testIdeaNode4 to testMindMap
        this.testMindMap.addIdeaNode(this.testIdeaNode4);

    }

    /**
     * Subtestmethod for adding testIdeaNode4 as a child of testIdeaNode3
     * 
     * Requires the calling of the following routines in this order beforehand
     * to work correctly: {@link #addIdea2asChildofIdea1()}
     * {@link #addIdea3andIdea4()}
     */
    private void addIdea4asChildofIdea3() {

        // Add testIdeaNode4 as child of testIdeaNode3
        assertTrue(
                "Error while adding testIdeaNode4 as child to testIdeaNode3",
                this.testIdeaNode3.addIdeaChild(this.testIdeaNode4,
                        this.testMindMap) == EAddChildIdeaNodeResultCase.C1_2_P_SINGLE_C_SINGLE_CREATE_MAP);

        // Check if testIdeaNode4 is stored as child of testIdeaNode3
        assertTrue("Error: testIdeaNode4 must be a child of testIdeaNode3",
                this.testIdeaNode3.getChildren().contains(this.testIdeaNode4));

        // Check if isChild values are correct
        assertTrue("Error: testIdeaNode4 should be a child of testIdeaNode3",
                this.testIdeaNode4.getIsChild());
        assertFalse(
                "Error: testIdeaNode3 should not be a child of any ideaNode",
                this.testIdeaNode3.getIsChild());

        // Check if return values for isParent() are correct
        assertTrue("Error: testIdeaNode3 should be a parent to testIdeaNode4",
                this.testIdeaNode3.isParent());
        assertFalse(
                "Error: testIdeaNode4 should not be a parent to any IdeaNodeView",
                this.testIdeaNode4.isParent());

        // Check if the parentID of testIdeaNode4 matches the ideaNodeID of
        // testIdeaNode3
        assertTrue(
                "Error: The parentID of testIdeaNode4 should be the ideaNodeID of testIdeaNode3",
                this.testIdeaNode4.getParentID() == this.testIdeaNode3
                        .getIdeaID());

        // There should be exactly two Maps in the MindMap
        assertTrue("Error: There is less or more than two Maps in the MindMap",
                this.testMindMap.getMindMapList().size() == 2);

        // Check if there is a Map with testIdeaNode1 as the root node
        Map searchedMap = this.testMindMap
                .findMapByRootNode(this.testIdeaNode1);
        assertNotNull(
                "Error: testIdeaNode1 has not been found as the root node of a Map in the MindMap",
                searchedMap);
        assertTrue(
                "Error: testIdeaNode1 has not been found as the root node of a Map in the MindMap",
                searchedMap.getRootNode().equals(this.testIdeaNode1));

        // Check if there is a second Map with testIdeaNode3 as the root
        // node
        Map searchedMap2 = this.testMindMap
                .findMapByRootNode(this.testIdeaNode3);
        assertNotNull(
                "Error: testIdeaNode3 has not been found as the root node of a Map in the MindMap",
                searchedMap2);
        assertTrue(
                "Error: testIdeaNode3 has not been found as the root node of a Map in the MindMap",
                searchedMap2.getRootNode().equals(this.testIdeaNode3));

    }

    /**
     * Subtestmethod to add testIdeaNode3 as child of testIdeaNode1
     * 
     * Requires the calling of the following routines in this order beforehand
     * to work correctly: {@link #addIdea2asChildofIdea1()}
     * {@link #addIdea3andIdea4()} {@link #addIdea4asChildofIdea3()}
     */
    private void addIdea3AsChildofIdea1() {
        // Add testIdeaNode3 as a child of testIdeaNode1
        assertTrue(
                "Error while adding testIdeaNode3 as child to testIdeaNode1",
                this.testIdeaNode1.addIdeaChild(this.testIdeaNode3,
                        this.testMindMap) == EAddChildIdeaNodeResultCase.C2_1_P_MAP_C_MAPPARENT_DELETE_MAP);

        // Check if testIdeaNode3 is a child of testIdeaNode1
        assertTrue("Error: testIdeaNode3 must be a child of testIdeaNode1",
                this.testIdeaNode1.getChildren().contains(this.testIdeaNode3));

        // Check if isChild values are correct
        assertTrue("Error: testIdeaNode3 should be a child of testIdeaNode1",
                this.testIdeaNode3.getIsChild());
        assertFalse(
                "Error: testIdeaNode1 should not be a child of any ideaNode",
                this.testIdeaNode1.getIsChild());

        // Check if return values for isParent() are correct
        assertTrue("Error: testIdeaNode3 should be a parent to testIdeaNode4",
                this.testIdeaNode3.isParent());
        assertTrue(
                "Error: testIdeaNode1 should be a parent to testIdeaNode3 and testIdeaNode2",
                this.testIdeaNode1.isParent());

        // Check if the parentID of testIdeaNode3 matches the ideaNodeID of
        // testIdeaNode1
        assertTrue(
                "Error: The parentID of testIdeaNode3 should be the ideaNodeID of testIdeaNode1",
                this.testIdeaNode3.getParentID() == this.testIdeaNode1
                        .getIdeaID());

        // There should be exactly one Maps in the MindMap
        assertTrue("Error: There is less or more than one Map in the MindMap",
                this.testMindMap.getMindMapList().size() == 1);

        // Check if there is a Map with testIdeaNode1 as the root node
        Map searchedMap3 = this.testMindMap.getMindMapList().get(0);
        assertNotNull(
                "Error: testIdeaNode1 has not been found as the root node of a Map in the MindMap",
                searchedMap3);
        assertTrue(
                "Error: testIdeaNode1 has not been found as the root node of a Map in the MindMap",
                searchedMap3.getRootNode().equals(this.testIdeaNode1));

    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#addIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * as well as
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#checkAndUpdateMapStructuresOnAdd(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case: 2(b).1 <br/>
     * (2) This IdeaNodeView is child node OR (a) ...
     * {@link #testAddIdeaChildCase2_a_1()} <br/>
     * &nbsp;&nbsp;(2.1) newChild is already a root node of a Map: delete that
     * Map from MindMap, as newChild will be part of the Map that contains this
     * IdeaNodeView
     * </p>
     * 
     * 
     */
    @Test
    public void testAddIdeaChildCase2_b_1() {

        // Run test for adding testIdeaNode2 as a child to testIdeaNode1
        addIdea2asChildofIdea1();

        // Add two new IdeaNodes
        addIdea3andIdea4();

        // Add fourth Idea as child of third idea
        addIdea4asChildofIdea3();

        // Add third Idea as child of Idea two
        addIdea3AsChildofIdea2();

    }

    /**
     * Subtestroutine for adding the third IdeaNodeView as a child of the second
     * IdeaNodeView
     * 
     * Requires the calling of following routines in this order beforehand to
     * run correctly <br/> {@link #addIdea2asChildofIdea1()} <br/>
     * {@link #addIdea3andIdea4()}<br/> {@link #addIdea4asChildofIdea3()}
     */
    private void addIdea3AsChildofIdea2() {
        // Add testIdeaNode3 as a child of testIdeaNode2
        assertTrue(
                "Error while adding testIdeaNode3 as child to testIdeaNode2",
                this.testIdeaNode2.addIdeaChild(this.testIdeaNode3,
                        this.testMindMap) == EAddChildIdeaNodeResultCase.C2_1_P_MAP_C_MAPPARENT_DELETE_MAP);

        // Check if testIdeaNode3 is a child of testIdeaNode2
        assertTrue("Error: testIdeaNode3 must be a child of testIdeaNode2",
                this.testIdeaNode2.getChildren().contains(this.testIdeaNode3));

        // Check if isChild values are correct
        assertTrue("Error: testIdeaNode3 should be a child of testIdeaNode2",
                this.testIdeaNode3.getIsChild());
        assertTrue("Error: testIdeaNode2 should be a child of testIdeaNode1",
                this.testIdeaNode2.getIsChild());
        assertTrue("Error: testIdeaNode4 should be a child of testIdeaNode3",
                this.testIdeaNode4.getIsChild());
        assertFalse(
                "Error: testIdeaNode1 should not be a child of any ideaNode",
                this.testIdeaNode1.getIsChild());

        // Check if return values for isParent() are correct
        assertTrue("Error: testIdeaNode3 should be a parent to testIdeaNode4",
                this.testIdeaNode3.isParent());
        assertTrue(
                "Error: testIdeaNode1 should be a parent to testIdeaNode3 and testIdeaNode2",
                this.testIdeaNode1.isParent());
        assertTrue("Error: testIdeaNode2 should be a parent to testIdeaNode3",
                this.testIdeaNode2.isParent());
        assertFalse("Error: testIdeaNode4 must not be a parent to any node",
                this.testIdeaNode4.isParent());

        // Check if the parentID of testIdeaNode3 matches the ideaNodeID of
        // testIdeaNode2
        assertTrue(
                "Error: The parentID of testIdeaNode3 should be the ideaNodeID of testIdeaNode2",
                this.testIdeaNode3.getParentID() == this.testIdeaNode2
                        .getIdeaID());

        // There should be exactly one Map in the MindMap
        assertTrue("Error: There is less or more than one Map in the MindMap",
                this.testMindMap.getMindMapList().size() == 1);

        // Check if there is a Map with testIdeaNode1 as the root node
        Map searchedMap3 = this.testMindMap.getMindMapList().get(0);
        assertNotNull(
                "Error: testIdeaNode1 has not been found as the root node of a Map in the MindMap",
                searchedMap3);
        assertTrue(
                "Error: testIdeaNode1 has not been found as the root node of a Map in the MindMap",
                searchedMap3.getRootNode().equals(this.testIdeaNode1));

    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#addIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * as well as
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#checkAndUpdateMapStructuresOnAdd(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case: 2(a).2 <br/>
     * (2) This IdeaNodeView a child in a Map OR (b) ...
     * {@link #testAddIdeaChildCase2_b_2()} <br/>
     * &nbsp;&nbsp;(2.2) newChild is a single IdeaNodeView: no Map operations
     * necessary, as newChild will be part of the Map that contains this
     * IdeaNodeView
     * </p>
     * 
     * 
     */
    @Test
    public void testAddIdeaChildCase2_a_2() {

        // Call routine for adding testIdeaNode2 as a child to testIdeaNode1
        addIdea2asChildofIdea1();

        // Add testIdeaNode3 as a child of testIdeaNode2
        addIdea3asChildofIdea2();

    }

    /**
     * Subtestroutine adding testIdeaNode3 as a child of testIdeaNode2.
     * 
     * Requires the calling of the following routines in this order beforehand
     * to work correctly: {@link #addIdea2asChildofIdea1()}
     * 
     */
    private void addIdea3asChildofIdea2() {

        // Create a third test IdeaNodeView
        this.testIdeaNode3 = new IdeaNode(new NodeData(
                new NodeContent("Idee3"), //$NON-NLS-1$
                new NodeMetaData(45, 123, 1.0f,
                        EIdeaNodeCreator.MULTITOUCH_TABLE)));

        // Add testIdeaNode3 as child of testIdeaNode2
        assertTrue(
                "Error while adding testIdeaNode3 as child to testIdeaNode2",
                this.testIdeaNode2.addIdeaChild(this.testIdeaNode3,
                        this.testMindMap) == EAddChildIdeaNodeResultCase.C2_2_P_MAP_C_SINGLE_NOTHING);

        // Check if testIdeaNode3 is stored as child of testIdeaNode2
        assertTrue("Error: testIdeaNode3 must be a child of testIdeaNode2",
                this.testIdeaNode2.getChildren().contains(this.testIdeaNode3));

        // Check if isChild values are correct
        assertTrue("Error: testIdeaNode3 should be a child of testIdeaNode3",
                this.testIdeaNode3.getIsChild());
        assertFalse(
                "Error: testIdeaNode1 should not be a child of any ideaNode",
                this.testIdeaNode1.getIsChild());
        assertTrue("Error: testIdeaNode2 should be a child of testIdeaNode1",
                this.testIdeaNode2.getIsChild());

        // Check if return values for isParent() are correct
        assertTrue("Error: testIdeaNode2 should be a parent to testIdeaNode3",
                this.testIdeaNode2.isParent());
        assertTrue("Error: testIdeaNode1 should be a parent to testIdeaNode2",
                this.testIdeaNode1.isParent());
        assertFalse(
                "Error: testIdeaNode3 should not be a parent to any IdeaNodeView",
                this.testIdeaNode3.isParent());

        // Check if the parentID of testIdeaNode3 matches the ideaNodeID of
        // testIdeaNode2
        assertTrue(
                "Error: The parentID of testIdeaNode3 should be the ideaNodeID of testIdeaNode3",
                this.testIdeaNode3.getParentID() == this.testIdeaNode2
                        .getIdeaID());

        // There should be exactly one Map in the MindMap
        assertTrue("Error: There is less or more than one Map in the MindMap",
                this.testMindMap.getMindMapList().size() == 1);

        // Check if this is a Map with testIdeaNode1 as the root node
        Map searchedMap = this.testMindMap.getMindMapList().get(0);
        assertNotNull(
                "Error: testIdeaNode1 has not been found as the root node of a Map in the MindMap",
                searchedMap);
        assertTrue(
                "Error: testIdeaNode1 has not been found as the root node of a Map in the MindMap",
                searchedMap.getRootNode().equals(this.testIdeaNode1));

    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#addIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * as well as
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#checkAndUpdateMapStructuresOnAdd(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case: 2(b).2 <br/>
     * (2) This IdeaNodeView a root node of a Map OR (a) ...
     * {@link #testAddIdeaChildCase2_a_2()} <br/>
     * &nbsp;&nbsp;(2.2) newChild is a single IdeaNodeView: no Map operations
     * necessary, as newChild will be part of the Map that contains this
     * IdeaNodeView
     * </p>
     * 
     * 
     */
    @Test
    public void testAddIdeaChildCase2_b_2() {

        // Call routine for adding testIdeaNode2 as a child to testIdeaNode1
        addIdea2asChildofIdea1();

        // Add testIdeaNode3 as a child of testIdeaNode1
        addIdea3asChildofIdea1();

    }

    /**
     * Subtestroutine for adding testIdeaNode3 as a child of testIdeaNode1
     * 
     * Requires the calling of the following routines in this order beforehand
     * to work correctly: {@link #addIdea2asChildofIdea1()}
     * 
     */
    private void addIdea3asChildofIdea1() {

        // Create a third test IdeaNodeView
        this.testIdeaNode3 = new IdeaNode(new NodeData(
                new NodeContent("Idee3"), //$NON-NLS-1$
                new NodeMetaData(45, 123, 1.0f,
                        EIdeaNodeCreator.MULTITOUCH_TABLE)));

        // Add testIdeaNode3 to MindMap
        this.testMindMap.addIdeaNode(this.testIdeaNode3);

        // Add testIdeaNode3 as child of testIdeaNode1
        assertTrue(
                "Error while adding testIdeaNode3 as child to testIdeaNode1",
                this.testIdeaNode1.addIdeaChild(this.testIdeaNode3,
                        this.testMindMap) == EAddChildIdeaNodeResultCase.C2_2_P_MAP_C_SINGLE_NOTHING);

        // Check if testIdeaNode3 is stored as child of testIdeaNode1
        assertTrue("Error: testIdeaNode3 must be a child of testIdeaNode1",
                this.testIdeaNode1.getChildren().contains(this.testIdeaNode3));

        // Check if isChild values are correct
        assertTrue("Error: testIdeaNode3 should be a child of testIdeaNode1",
                this.testIdeaNode3.getIsChild());
        assertFalse(
                "Error: testIdeaNode1 should not be a child of any ideaNode",
                this.testIdeaNode1.getIsChild());
        assertTrue("Error: testIdeaNode2 should be a child of testIdeaNode1",
                this.testIdeaNode2.getIsChild());

        // Check if return values for isParent() are correct
        assertTrue(
                "Error: testIdeaNode1 should be a parent to testIdeaNode2 and testIdeaNode3",
                this.testIdeaNode1.isParent());
        assertFalse(
                "Error: testIdeaNode3 should not be a parent to any IdeaNodeView",
                this.testIdeaNode3.isParent());
        assertFalse(
                "Error: testIdeaNode2 should not be a parent to any IdeaNodeView",
                this.testIdeaNode2.isParent());

        // Check if the parentID of testIdeaNode3 matches the ideaNodeID of
        // testIdeaNode1
        assertTrue(
                "Error: The parentID of testIdeaNode3 should be the ideaNodeID of testIdeaNode1",
                this.testIdeaNode3.getParentID() == this.testIdeaNode1
                        .getIdeaID());

        // There should be exactly one Map in the MindMap
        assertTrue("Error: There is less or more than one Map in the MindMap",
                this.testMindMap.getMindMapList().size() == 1);

        // Check if this is a Map with testIdeaNode1 as the root node
        Map searchedMap = this.testMindMap.getMindMapList().get(0);
        assertNotNull(
                "Error: testIdeaNode1 has not been found as the root node of a Map in the MindMap",
                searchedMap);
        assertTrue(
                "Error: testIdeaNode1 has not been found as the root node of a Map in the MindMap",
                searchedMap.getRootNode().equals(this.testIdeaNode1));

    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#addIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case: the new child is already a child
     * </p>
     * 
     * 
     */
    @Test
    public void testAddIdeaChildIsChildAlready() {

        // Run test for adding testIdeaNode2 as child of testIdeaNode1
        addIdea2asChildofIdea1();

        // Add testIdeaNode2 as a child of testIdeaNode1 again
        assertTrue(
                "Error: it should not be able to add testIdeaNode2 as child to testIdeaNode1 because it already is a child of testIdeaNode1",
                this.testIdeaNode1.addIdeaChild(this.testIdeaNode2,
                        this.testMindMap) == EAddChildIdeaNodeResultCase.NO_ADD_ON_CONSTRAINT);

        // Check if testIdeaNode1 contains exactly one child
        assertTrue("Error: testIdeaNode1 must only contain one child ",
                this.testIdeaNode1.getChildren().size() == 1);

        // Check if testIdeaNode1 contains testIdeaNode2 as child
        assertTrue("Error: testIdeaNode1 must contain testIdeaNode2 as child ",
                this.testIdeaNode1.getChildren().contains(this.testIdeaNode2));

        // Check if there is exactly one Map in the MindMap
        assertTrue("Error: there is more or less than one Map in the MindMap",
                this.testMindMap.getMindMapList().size() == 1);

        // Check if testIdeaNode1 is still the root node of the Map
        Map searchedMap = this.testMindMap.getMindMapList().get(0);
        assertTrue("Error: testIdeaNode1 is not the root node of the Map",
                searchedMap.getRootNode().equals(this.testIdeaNode1));
    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#addIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case: the new child already has a parent
     * </p>
     * 
     * 
     */
    @Test
    public void testAddIdeaChildHasParent() {

        // Run test for adding testIdeaNode2 as child of testIdeaNode1
        addIdea2asChildofIdea1();

        // Create a testIdeaNode3
        this.testIdeaNode3 = new IdeaNode(new NodeData(
                new NodeContent("Idee3"), //$NON-NLS-1$
                new NodeMetaData(22, 678, 23.0f,
                        EIdeaNodeCreator.BLUETOOTH_SERVER)));

        // Add testIdeaNode3 to testMindMap
        this.testMindMap.addIdeaNode(this.testIdeaNode3);

        // Add null testIdeaNode2 as child to testIdeaNode3
        assertTrue(
                "Error: it should not be able to add testIdeaNode2 as child to testIdeaNode3 because it already has a parent",
                this.testIdeaNode3.addIdeaChild(this.testIdeaNode2,
                        this.testMindMap) == EAddChildIdeaNodeResultCase.NO_ADD_ON_CONSTRAINT);

        // Check if testIdeaNode3 contains testIdeaNode2 as child
        assertFalse(
                "Error: testIdeaNode3 must not contain testIdeaNode2 as child ",
                this.testIdeaNode3.getChildren().contains(this.testIdeaNode2));

        // Check if there is exactly one Map in the MindMap
        assertTrue("Error: there is more or less than one Map in the MindMap",
                this.testMindMap.getMindMapList().size() == 1);

        // Check if testIdeaNode1 is still the root node of the Map
        Map searchedMap = this.testMindMap.getMindMapList().get(0);
        assertTrue("Error: testIdeaNode1 is not the root node of the Map",
                searchedMap.getRootNode().equals(this.testIdeaNode1));
    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#removeIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case: To be removed IdeaNodeView is not a child
     * </p>
     * 
     * 
     */
    @Test
    public void testRemoveIdeaChildCaseChildNotAChild() {

        // Run subroutine for adding testIdeaNode2 as child of testIdeaNode1
        addIdea2asChildofIdea1();

        // Create a testIdeaNode3
        this.testIdeaNode3 = new IdeaNode(new NodeData(
                new NodeContent("Idee3"), //$NON-NLS-1$
                new NodeMetaData(22, 678, 23.0f,
                        EIdeaNodeCreator.MULTITOUCH_TABLE)));

        // Add testIdeaNode3 to testMindMap
        this.testMindMap.addIdeaNode(this.testIdeaNode3);

        // Remove a testIdeaNode3 from testIdeaNode1
        assertTrue(
                "Error: it should not be able to remove testIdeaNode3 as children from an testIdeaNode1 because it's not a child of testIdeaNode1",
                this.testIdeaNode1.removeIdeaChild(this.testIdeaNode3,
                        this.testMindMap).equals(
                        ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_CONSTRAINT));

        // Check if testIdeaNode1 contains exactly one child
        assertTrue("Error: testIdeaNode1 must contain a child ",
                this.testIdeaNode1.getChildren().size() == 1);

        // Check if testIdeaNode1 contains testIdeaNode2 as child
        assertTrue("Error: testIdeaNode1 must contain testIdeaNode2 as child ",
                this.testIdeaNode1.getChildren().contains(this.testIdeaNode2));

        // There must be exactly 1 Maps in the MindMap
        assertTrue("Error: there is less or more than one Map in the MindMap",
                this.testMindMap.getMindMapList().size() == 1);

    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#removeIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case: Referenced MindMap is null on removal of a IdeaNodeView
     * </p>
     * 
     * 
     */
    @Test
    public void testRemoveIdeaChildCaseMindMapNull() {

        // Run subroutine for adding testIdeaNode2 as child of testIdeaNode1
        addIdea2asChildofIdea1();

        // Remove testIdeaNode2 as child of testIdeaNode1 with a null MindMap
        assertTrue(
                "Error: it should not be able to remove IdeaNodes as children to an IdeaNodeView within a null MindMap",
                this.testIdeaNode1
                        .removeIdeaChild(this.testIdeaNode2, null)
                        .equals(ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_ERROR));

        // Check if testIdeaNode1 contains exactly one child
        assertTrue("Error: testIdeaNode1 must contain a child ",
                this.testIdeaNode1.getChildren().size() == 1);

        // Check if testIdeaNode1 contains testIdeaNode2 as child
        assertTrue("Error: testIdeaNode1 must contain testIdeaNode2 as child ",
                this.testIdeaNode1.getChildren().contains(this.testIdeaNode2));

        // There must be exactly 1 Maps in the MindMap
        assertTrue("Error: there is less or more than one Map in the MindMap",
                this.testMindMap.getMindMapList().size() == 1);

    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#removeIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case: To be removed IdeaNodeView is null
     * </p>
     * 
     * 
     */
    @Test
    public void testRemoveIdeaChildCaseIdeaNodeNull() {

        // Run subroutine for adding testIdeaNode2 as child of testIdeaNode1
        addIdea2asChildofIdea1();

        // Remove a null IdeanNode from testIdeaNode1
        assertTrue(
                "Error: it should not be able to remove null IdeaNodes",
                this.testIdeaNode1
                        .removeIdeaChild(null, this.testMindMap)
                        .equals(ERemoveChildIdeaNodeResultCase.NO_REMOVE_ON_ERROR));

        // Check if testIdeaNode1 contains exactly one child
        assertTrue("Error: testIdeaNode1 must contain a child ",
                this.testIdeaNode1.getChildren().size() == 1);

        // Check if testIdeaNode1 contains testIdeaNode2 as child
        assertTrue("Error: testIdeaNode1 must contain testIdeaNode2 as child ",
                this.testIdeaNode1.getChildren().contains(this.testIdeaNode2));

        // There must be exactly 1 Maps in the MindMap
        assertTrue("Error: there is less or more than one Map in the MindMap",
                this.testMindMap.getMindMapList().size() == 1);

    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#removeIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * and
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#checkAndUpdateMapStructuresOnRemove(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case 1.1: <br/>
     * (1) The toBeRemovedChild has no children<br/>
     * &nbsp;&nbsp;(1.1) This IdeaNodeView has no other child than
     * toBeRemovedChild AND is no child itself (= is the root node of the Map
     * they are in): Remove the Map that the two IdeaNodes form.
     * </p>
     * 
     * 
     */
    @Test
    public void testRemoveIdeaChildCase1_1() {

        // Run subroutine for adding testIdeaNode2 as child of testIdeaNode1
        addIdea2asChildofIdea1();

        // remove IdeaNode2 again
        assertTrue(
                "Error while removing testIdeaNode2 as child of testIdeaNode1",
                this.testIdeaNode1
                        .removeIdeaChild(this.testIdeaNode2, this.testMindMap)
                        .equals(ERemoveChildIdeaNodeResultCase.C1_1_P_SINGLE_C_SINGLE_REMOVE_MAP));

        // Check if IdeaNode1 children list is empty
        assertTrue("Error: the children list of IdeaNode1 should be empty",
                this.testIdeaNode1.getChildren().size() == 0);

        // Check if isChild values are correct
        assertFalse("Error: testIdeaNode1 should not be a child of any node",
                this.testIdeaNode1.getIsChild());
        assertFalse("Error: testIdeaNode2 should not be a child of any node",
                this.testIdeaNode2.getIsChild());

        // Check if return values for isParent() are correct
        assertFalse("Error: testIdeaNode1 should not be a parent to any node",
                this.testIdeaNode1.isParent());
        assertFalse("Error: testIdeaNode2 should not be a parent to any node",
                this.testIdeaNode2.isParent());

        // Check if the parentID of testIdeaNode2 matches NO_PARENT
        assertTrue("Error: The parentID of testIdeaNode2 should be NO_PARENT",
                this.testIdeaNode2.getParentID() == IdeaNode.NO_PARENT);

        // There should be no Map in the MindMap
        assertTrue("Error: There is more than 0 Map in the MindMap",
                this.testMindMap.getMindMapList().size() == 0);

    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#removeIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * and
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#checkAndUpdateMapStructuresOnRemove(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case 1.2(a): <br/>
     * (1) The toBeRemovedChild has no children<br/>
     * &nbsp;&nbsp;(1.2) This IdeaNodeView is itself a child of another
     * IdeaNodeView OR ... (b) {@link #testRemoveIdeaChildCase1_2_b()} : No map
     * operations necessary.
     * </p>
     * 
     * 
     */
    @Test
    public void testRemoveIdeaChildCase1_2_a() {

        // Run subroutines for adding testIdeaNode2 as child of testIdeaNode1
        // and
        // testIdeaNode1 as child of a third node
        addIdea2asChildofIdea1();

        addIdea3asParentOfIdea1();

        // remove testIdeaNode2 as a child of testIdeaNode1
        assertTrue(
                "Error while removing testIdeaNode2 as child of testIdeaNode1",
                this.testIdeaNode1
                        .removeIdeaChild(this.testIdeaNode2, this.testMindMap)
                        .equals(ERemoveChildIdeaNodeResultCase.C1_2_P_MAP_C_SINGLE_NOTHING));

        // Check if IdeaNode1 children list is empty
        assertTrue("Error: the children list of IdeaNode1 should be empty",
                this.testIdeaNode1.getChildren().size() == 0);

        // Check if isChild values are correct
        assertTrue("Error: testIdeaNode1 should be a child of testIdeaNode3",
                this.testIdeaNode1.getIsChild());
        assertFalse("Error: testIdeaNode2 should not be a child of any node",
                this.testIdeaNode2.getIsChild());

        // Check if return values for isParent() are correct
        assertFalse("Error: testIdeaNode1 should not be a parent to any node",
                this.testIdeaNode1.isParent());
        assertFalse("Error: testIdeaNode2 should not be a parent to any node",
                this.testIdeaNode2.isParent());

        // Check if the parentID of testIdeaNode2 matches NO_PARENT
        assertTrue("Error: The parentID of testIdeaNode2 should be NO_PARENT",
                this.testIdeaNode2.getParentID() == IdeaNode.NO_PARENT);

        // There should be one Map in the MindMap
        assertTrue("Error: There is more or less than 1 Map in the MindMap",
                this.testMindMap.getMindMapList().size() == 1);

        // Check if the root node of the map is testIdeaNode3
        assertTrue(
                "Error: The root node of the Map should be testIdeaNode3",
                this.testMindMap.getMindMapList().get(0).getRootNode()
                        .equals(this.testIdeaNode3));

    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#removeIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * and
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#checkAndUpdateMapStructuresOnRemove(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case 1.2(b): <br/>
     * (1) The toBeRemovedChild has no children<br/>
     * &nbsp;&nbsp;(1.2) This IdeaNodeView IdeaNodeView has more children
     * besides the toBeRemovedChild OR ... (a)
     * {@link #testRemoveIdeaChildCase1_2_a()} : No map operations necessary.
     * </p>
     * 
     * 
     */
    @Test
    public void testRemoveIdeaChildCase1_2_b() {

        // Call routine for adding testIdeaNode2 as child of testIdeaNode1
        addIdea2asChildofIdea1();

        // Call routine for adding testIdeaNode3 as child of testIdeaNode1
        addIdea3asChildofIdea1();

        // remove testIdeaNode2 as a child of testIdeaNode1
        assertTrue(
                "Error while removing testIdeaNode2 as child of testIdeaNode1",
                this.testIdeaNode1
                        .removeIdeaChild(this.testIdeaNode2, this.testMindMap)
                        .equals(ERemoveChildIdeaNodeResultCase.C1_2_P_MAP_C_SINGLE_NOTHING));

        // Check if IdeaNode1 children list has one child
        assertTrue("Error: the children list of IdeaNode1 have one child",
                this.testIdeaNode1.getChildren().size() == 1);

        // Check if IdeaNode1 children list contains testIdeaNode3
        assertTrue(
                "Error: the children list of IdeaNode1 must contain testIdeaNode3",
                this.testIdeaNode1.getChildren().contains(this.testIdeaNode3));

        // Check if isChild values are correct
        assertTrue("Error: testIdeaNode3 should be a child of testIdeaNode1",
                this.testIdeaNode3.getIsChild());
        assertFalse("Error: testIdeaNode2 should not be a child of any node",
                this.testIdeaNode2.getIsChild());
        assertFalse("Error: testIdeaNode1 should not be a child of any node",
                this.testIdeaNode1.getIsChild());

        // Check if return values for isParent() are correct
        assertFalse("Error: testIdeaNode3 should not be a parent to any node",
                this.testIdeaNode3.isParent());
        assertFalse("Error: testIdeaNode2 should not be a parent to any node",
                this.testIdeaNode2.isParent());
        assertTrue("Error: testIdeaNode1 should be a parent to testIdeaNode3",
                this.testIdeaNode1.isParent());

        // Check if the parentID of testIdeaNode2 matches NO_PARENT
        assertTrue("Error: The parentID of testIdeaNode2 should be NO_PARENT",
                this.testIdeaNode2.getParentID() == IdeaNode.NO_PARENT);

        // There should be one Map in the MindMap
        assertTrue("Error: There is more or less than 1 Map in the MindMap",
                this.testMindMap.getMindMapList().size() == 1);

        // Check if the root node of the map is testIdeaNode1
        assertTrue(
                "Error: The root node of the Map should be testIdeaNode1",
                this.testMindMap.getMindMapList().get(0).getRootNode()
                        .equals(this.testIdeaNode1));

    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#removeIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * and
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#checkAndUpdateMapStructuresOnRemove(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case 2.1: <br/>
     * (2) The toBeRemovedChild has children <br/>
     * &nbsp;&nbsp;(2.1)This IdeaNodeView has no other child than
     * toBeRemovedChild AND is no child itself (= is the root node of the Map
     * they are in): Change Map root node to toBeRemovedChild.
     * </p>
     * 
     * 
     */
    @Test
    public void testRemoveIdeaChildCase2_1() {

        // Run subroutine for adding testIdeaNode2 as child of testIdeaNode1 and
        // testIdeaNode1 as child of a third node
        addIdea2asChildofIdea1();

        addIdea3asParentOfIdea1();

        // remove testIdeaNode1 as a child of testIdeaNode3
        assertTrue(
                "Error while removing testIdeaNode1 as child of testIdeaNode3",
                this.testIdeaNode3
                        .removeIdeaChild(this.testIdeaNode1, this.testMindMap)
                        .equals(ERemoveChildIdeaNodeResultCase.C2_1_P_SINGLE_C_MAP_MODIFY_MAP));

        // Check if testIdeaNode3 children list is empty
        assertTrue("Error: the children list of testIdeaNode3 should be empty",
                this.testIdeaNode3.getChildren().size() == 0);

        // Check if isChild values are correct
        assertTrue("Error: testIdeaNode2 should be a child of testIdeaNode1",
                this.testIdeaNode2.getIsChild());
        assertFalse("Error: testIdeaNode1 should not be a child of any node",
                this.testIdeaNode1.getIsChild());
        assertFalse("Error: testIdeaNode3 should not be a child of any node",
                this.testIdeaNode3.getIsChild());

        // Check if return values for isParent() are correct
        assertFalse("Error: testIdeaNode3 must not be a parent to any node",
                this.testIdeaNode3.isParent());
        assertTrue("Error: testIdeaNode1 must be a parent to testIdeaNode2",
                this.testIdeaNode1.isParent());
        assertFalse("Error: testIdeaNode2 must not be a parent to any node",
                this.testIdeaNode2.isParent());

        // Check if the parentID of testIdeaNode1 matches NO_PARENT
        assertTrue("Error: The parentID of testIdeaNode1 should be NO_PARENT",
                this.testIdeaNode1.getParentID() == IdeaNode.NO_PARENT);

        // There should be one Map in the MindMap
        assertTrue("Error: There is more or less than 1 Map in the MindMap",
                this.testMindMap.getMindMapList().size() == 1);

        // Check if testIdeaNode1 is the root node of the Map
        Map searchedMap = this.testMindMap.getMindMapList().get(0);
        assertTrue("Error: testIdeaNode1 is not the root node of the Map",
                searchedMap.getRootNode().equals(this.testIdeaNode1));

    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#removeIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * and
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#checkAndUpdateMapStructuresOnRemove(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case 2.2(a): <br/>
     * (2) The toBeRemovedChild has children <br/>
     * &nbsp;&nbsp;(2.2) This IdeaNodeView has more children than the
     * toBeRemovedChild OR ... (b) {@link #testRemoveIdeaChildCase2_2_b()} : :
     * Create a new Map with the toBeRemovedChild as the root node.
     * </p>
     * 
     * 
     */
    @Test
    public void testRemoveIdeaChildCase2_2_a() {

        // Run subroutines for adding testIdeaNode2 as a child to testIdeaNode1
        // as well as two other nodes
        addIdea2asChildofIdea1();

        addIdea3andIdea4();

        addIdea4asChildofIdea3();

        addIdea3AsChildofIdea1();

        // remove testIdeaNode3 as a child of testIdeaNode1
        assertTrue(
                "Error while removing testIdeaNode3 as child of testIdeaNode1",
                this.testIdeaNode1
                        .removeIdeaChild(this.testIdeaNode3, this.testMindMap)
                        .equals(ERemoveChildIdeaNodeResultCase.C2_2_P_MAP_C_MAP_NEW_MAP));

        // Check if testIdeaNode1 children has exactly one child
        assertTrue(
                "Error: the children list of testIdeaNode1 should contain one child",
                this.testIdeaNode1.getChildren().size() == 1);

        // Check if testIdeaNode1 children has testIdeaNode2 as child
        assertTrue(
                "Error: the children list of testIdeaNode1 should contain testIdeaNode2",
                this.testIdeaNode1.getChildren().contains(this.testIdeaNode2));

        // Check if testIdeaNode3 children has exactly one child
        assertTrue(
                "Error: the children list of testIdeaNode3 should contain one child",
                this.testIdeaNode3.getChildren().size() == 1);

        // Check if testIdeaNode3 children has testIdeaNode4 as child
        assertTrue(
                "Error: the children list of testIdeaNode3 should contain testIdeaNode4",
                this.testIdeaNode3.getChildren().contains(this.testIdeaNode4));

        // Check if isChild values are correct
        assertTrue("Error: testIdeaNode2 should be a child of testIdeaNode1",
                this.testIdeaNode2.getIsChild());
        assertFalse("Error: testIdeaNode1 should not be a child of any node",
                this.testIdeaNode1.getIsChild());
        assertFalse("Error: testIdeaNode3 should not be a child of any node",
                this.testIdeaNode3.getIsChild());
        assertTrue("Error: testIdeaNode4 should be a child of testIdeaNode3",
                this.testIdeaNode4.getIsChild());

        // Check if return values for isParent() are correct
        assertTrue("Error: testIdeaNode3 must be a parent to testIdeaNode4",
                this.testIdeaNode3.isParent());
        assertTrue("Error: testIdeaNode1 must be a parent to testIdeaNode2",
                this.testIdeaNode1.isParent());
        assertFalse("Error: testIdeaNode2 must not be a parent to any node",
                this.testIdeaNode2.isParent());
        assertFalse("Error: testIdeaNode4 must not be a parent to any node",
                this.testIdeaNode4.isParent());

        // Check if the parentID of testIdeaNode1 matches NO_PARENT
        assertTrue("Error: The parentID of testIdeaNode1 should be NO_PARENT",
                this.testIdeaNode1.getParentID() == IdeaNode.NO_PARENT);

        // Check if the parentID of testIdeaNode3 matches NO_PARENT
        assertTrue("Error: The parentID of testIdeaNode3 should be NO_PARENT",
                this.testIdeaNode1.getParentID() == IdeaNode.NO_PARENT);

        // There should be two Maps in the MindMap
        assertTrue("Error: There is more or less than 2 Maps in the MindMap",
                this.testMindMap.getMindMapList().size() == 2);

        // Check if testIdeaNode1 is the root node of one Map
        Map searchedMap = this.testMindMap
                .findMapByRootNode(this.testIdeaNode1);

        assertNotNull(
                "Error: Map with testIdeaNode1 as root node could not be found",
                searchedMap);

        // Check if testIdeaNode3 is the root node of one Map
        Map searchedMap2 = this.testMindMap
                .findMapByRootNode(this.testIdeaNode3);

        assertNotNull(
                "Error: Map with testIdeaNode3 as root node could not be found",
                searchedMap2);

    }

    /**
     * <p>
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#removeIdeaChild(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * and
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#checkAndUpdateMapStructuresOnRemove(de.sarahw.ma.pc.mindMapper.model.IdeaNode, de.sarahw.ma.pc.mindMapper.model.MindMap)}
     * </p>
     * 
     * <p>
     * Tested Case 2.2(b): <br/>
     * (2) The toBeRemovedChild has one or more children <br/>
     * &nbsp;&nbsp;(2.2) This IdeaNodeView is itself a child of another
     * IdeaNodeView: Create a new Map with the toBeRemovedChild as the root
     * node. OR ... (a) {@link #testRemoveIdeaChildCase2_2_a()}
     * </p>
     * 
     * 
     */
    @Test
    public void testRemoveIdeaChildCase2_2_b() {

        // Run test for adding testIdeaNode2 as a child to testIdeaNode1
        addIdea2asChildofIdea1();

        // Add two new IdeaNodes
        addIdea3andIdea4();

        // Add fourth Idea as child of third idea
        addIdea4asChildofIdea3();

        // Add third Idea as child of Idea two
        addIdea3AsChildofIdea2();

        // remove testIdeaNode3 as a child of testIdeaNode2
        assertTrue(
                "Error while removing testIdeaNode3 as child of testIdeaNode2",
                this.testIdeaNode2
                        .removeIdeaChild(this.testIdeaNode3, this.testMindMap)
                        .equals(ERemoveChildIdeaNodeResultCase.C2_2_P_MAP_C_MAP_NEW_MAP));

        // Check if testIdeaNode2 children has no longer a child
        assertTrue(
                "Error: the children list of testIdeaNode2 should contain no child",
                this.testIdeaNode2.getChildren().size() == 0);

        // Check if testIdeaNode1 children has testIdeaNode2 as child
        assertTrue(
                "Error: the children list of testIdeaNode1 should contain testIdeaNode2",
                this.testIdeaNode1.getChildren().contains(this.testIdeaNode2));

        // Check if testIdeaNode3 children has exactly one child
        assertTrue(
                "Error: the children list of testIdeaNode3 should contain one child",
                this.testIdeaNode3.getChildren().size() == 1);

        // Check if testIdeaNode3 children has testIdeaNode4 as child
        assertTrue(
                "Error: the children list of testIdeaNode3 should contain testIdeaNode4",
                this.testIdeaNode3.getChildren().contains(this.testIdeaNode4));

        // Check if isChild values are correct
        assertTrue("Error: testIdeaNode2 should be a child of testIdeaNode1",
                this.testIdeaNode2.getIsChild());
        assertFalse("Error: testIdeaNode1 should not be a child of any node",
                this.testIdeaNode1.getIsChild());
        assertFalse("Error: testIdeaNode3 should not be a child of any node",
                this.testIdeaNode3.getIsChild());
        assertTrue("Error: testIdeaNode4 should be a child of testIdeaNode3",
                this.testIdeaNode4.getIsChild());

        // Check if return values for isParent() are correct
        assertTrue("Error: testIdeaNode3 must be a parent to testIdeaNode4",
                this.testIdeaNode3.isParent());
        assertTrue("Error: testIdeaNode1 must be a parent to testIdeaNode2",
                this.testIdeaNode1.isParent());
        assertFalse("Error: testIdeaNode2 must not be a parent to any node",
                this.testIdeaNode2.isParent());
        assertFalse("Error: testIdeaNode4 must not be a parent to any node",
                this.testIdeaNode4.isParent());

        // Check if the parentID of testIdeaNode1 matches NO_PARENT
        assertTrue("Error: The parentID of testIdeaNode1 should be NO_PARENT",
                this.testIdeaNode1.getParentID() == IdeaNode.NO_PARENT);

        // Check if the parentID of testIdeaNode3 matches NO_PARENT
        assertTrue("Error: The parentID of testIdeaNode3 should be NO_PARENT",
                this.testIdeaNode1.getParentID() == IdeaNode.NO_PARENT);

        // There should be two Maps in the MindMap
        assertTrue("Error: There is more or less than 2 Maps in the MindMap",
                this.testMindMap.getMindMapList().size() == 2);

        // Check if testIdeaNode1 is the root node of one Map
        Map searchedMap = this.testMindMap
                .findMapByRootNode(this.testIdeaNode1);

        assertNotNull(
                "Error: Map with testIdeaNode1 as root node could not be found",
                searchedMap);

        // Check if testIdeaNode3 is the root node of one Map
        Map searchedMap2 = this.testMindMap
                .findMapByRootNode(this.testIdeaNode3);

        assertNotNull(
                "Error: Map with testIdeaNode3 as root node could not be found",
                searchedMap2);

    }

    /**
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.IdeaNode#isParent()}.
     */
    @Test
    public void testIsParent() {
        // Check if IdeaNode1/2 are parents, they should not be
        assertFalse("isParent() wrong result for IdeaNode1",
                this.testIdeaNode1.isParent());
        assertFalse("isParent() wrong result for IdeaNode2",
                this.testIdeaNode2.isParent());

        // Add IdeaNode1 as child to IdeaNode2 and check if it is stored in the
        // children list
        this.testIdeaNode1.addIdeaChild(this.testIdeaNode2, this.testMindMap);
        assertTrue("Error while adding IdeaNode2 as child to IdeaNode1",
                this.testIdeaNode1.getChildren().contains(this.testIdeaNode2));

        // Check isParent(), for IdeaNode1 it should return true, for IdeaNode2
        // false
        assertTrue("isParent() wrong result for IdeaNode1",
                this.testIdeaNode1.isParent());
        assertFalse("isParent() wrong result for IdeaNode2",
                this.testIdeaNode2.isParent());

        // Remove IdeaNode2 again
        this.testIdeaNode1
                .removeIdeaChild(this.testIdeaNode2, this.testMindMap);
        assertFalse("Error while removing IdeaNode3 as child of IdeaNode2",
                this.testIdeaNode1.getChildren().contains(this.testIdeaNode2));

        // Check if IdeaNode1/2 are parents, they should not be
        assertFalse("isParent() wrong result for IdeaNode1",
                this.testIdeaNode1.isParent());
        assertFalse("isParent() wrong result for IdeaNode2",
                this.testIdeaNode2.isParent());

    }

    /**
     * <p>
     * Test method for MindMap#removeIdeaNode(IdeaNode)}
     * </p>
     * 
     * <p>
     * Tested Case: To be removed node is null
     * </p>
     */
    @Test
    public void testRemoveIdeaNodeNull() {

        // Create dummy node
        IdeaNode dummyIdeaNode = null;

        // Add dummy node to MindMap
        assertFalse("Error: null IdeaNodes must not be added to a MindMap",
                this.testMindMap.addIdeaNode(dummyIdeaNode));

        // Remove dummy node from MindMap
        assertFalse(
                "Error: null IdeaNodes are no applicable argument for the removeIdeaNode() method!",
                this.testMindMap.removeIdeaNode(dummyIdeaNode));

        // Check if there are exactly 2 IdeaNodes in the MindMap
        assertTrue(
                "Error: there are more or less than 2 IdeaNodes in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().size() == 2);

        // Check if the IdeaNodes are stored in the MindMap mindMapIdeaNodeList
        assertTrue(
                "Error: testIdeaNode1 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode1));
        assertTrue(
                "Error: testIdeaNode2 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode2));

    }

    /**
     * <p>
     * Test method for MindMap#removeIdeaNode(IdeaNode)}
     * </p>
     * 
     * <p>
     * Tested Case: To be removed node is not part of the MindMap
     * </p>
     */
    @Test
    public void testRemoveIdeaNodeNotPartOfMindMap() {

        // Create new node
        IdeaNode newIdeaNode = new IdeaNode(new NodeData(
                new NodeContent("Idee"), //$NON-NLS-1$
                new NodeMetaData(22, 678, 23.0f,
                        EIdeaNodeCreator.MULTITOUCH_TABLE)));

        // Remove new node from MindMap
        assertFalse(
                "Error: removeIdeaNode must return false for this IdeaNodeView, it's not part of the MindMap!",
                this.testMindMap.removeIdeaNode(newIdeaNode));

        // Check if there are exactly 2 IdeaNodes in the MindMap
        assertTrue(
                "Error: there are more or less than 2 IdeaNodes in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().size() == 2);

        // Check if the IdeaNodes are stored in the MindMap mindMapIdeaNodeList
        assertTrue(
                "Error: testIdeaNode1 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode1));
        assertTrue(
                "Error: testIdeaNode2 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode2));

    }

    /**
     * <p>
     * Test method for MindMap#removeIdeaNode(IdeaNode)
     * </p>
     * 
     * <p>
     * Tested Case: (1) To be removed IdeaNodeView has parent, which (a) itself
     * is a single IdeaNodeView, and no children
     * </p>
     */
    @Test
    public void testRemoveIdeaNode_1_a() {

        // Run test for adding testIdeaNode2 as a child to testIdeaNode1
        addIdea2asChildofIdea1();

        // Remove testIdeaNode2 completely
        assertTrue("Error: testIdeaNode2 has not been removed correctly",
                this.testMindMap.removeIdeaNode(this.testIdeaNode2));

        // Check if testIdeaNode2's status is marked EIdeaState.DELETED
        assertTrue("Error: testIdeaNode2's state must be EIdeaState.DELETED!",
                this.testIdeaNode2.getIdeaState().equals(EIdeaState.DELETED));

        // Check if testIdeaNode2 is no longer a child of testIdeaNode1
        assertFalse(
                "Error: testIdeaNode2 must not be a child of testIdeaNode1 anymore!",
                this.testIdeaNode1.getChildren().contains(this.testIdeaNode2));

        // Check if there is exactly 1 IdeaNodeView left in the MindMap
        assertTrue(
                "Error: there are more or less than 1 IdeaNodes in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().size() == 1);

        // Check if the testIdeaNode1 is still stored in the MindMap
        // mindMapIdeaNodeList
        assertTrue(
                "Error: testIdeaNode1 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode1));
        assertFalse(
                "Error: testIdeaNode2 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode2));

        // Check if there are exactly 0 Maps in the MindMap
        assertTrue("Error: there are more than 0 Maps in the MindMap",
                this.testMindMap.getMindMapList().size() == 0);

    }

    /**
     * <p>
     * Test method for MindMap#removeIdeaNode(IdeaNode)
     * </p>
     * 
     * <p>
     * Tested Case: (1) To be removed IdeaNodeView has parent, which (b) itself
     * is a child, and no children
     * </p>
     */
    @Test
    public void testRemoveIdeaNode_1_b() {

        // Run test for adding testIdeaNode2 as a child to testIdeaNode1
        addIdea2asChildofIdea1();

        // Add a third IdeaNodeView as parent to testIdeaNode1
        addIdea3asParentOfIdea1();

        // Remove testIdeaNode2 completely
        assertTrue("Error: testIdeaNode2 has not been removed correctly",
                this.testMindMap.removeIdeaNode(this.testIdeaNode2));

        // Check if testIdeaNode2's status is marked EIdeaState.DELETED
        assertTrue("Error: testIdeaNode2's state must be EIdeaState.DELETED!",
                this.testIdeaNode2.getIdeaState().equals(EIdeaState.DELETED));

        // Check if testIdeaNode2 is no longer a child of testIdeaNode1
        assertFalse(
                "Error: testIdeaNode2 must not be a child of testIdeaNode1 anymore!",
                this.testIdeaNode1.getChildren().contains(this.testIdeaNode2));

        // Check if there are exactly 2 IdeaNodes left in the MindMap
        assertTrue(
                "Error: there are more or less than 2 IdeaNodes in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().size() == 2);

        // Check if the testIdeaNode1 and testIdeaNode3 are still stored in the
        // MindMap mindMapIdeaNodeList
        assertTrue(
                "Error: testIdeaNode1 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode1));
        assertTrue(
                "Error: testIdeaNode3 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode3));
        assertFalse(
                "Error: testIdeaNode2 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode2));

        // Check if there is exactly 1 Map in the MindMap
        assertTrue("Error: there are more or less than 1 Maps in the MindMap",
                this.testMindMap.getMindMapList().size() == 1);

        // Check if the root node of the Map is testIdeaNode3
        assertTrue(
                "Error: The root node of the remaining Map should be testIdeaNode3!",
                this.testMindMap.getMindMapList().get(0).getRootNode()
                        .equals(this.testIdeaNode3));

    }

    /**
     * <p>
     * Test method for MindMap#removeIdeaNode(IdeaNode)
     * </p>
     * 
     * <p>
     * Tested Case: (2) To be removed IdeaNodeView has one child, which (a)
     * itself is a single node
     * </p>
     */
    @Test
    public void testRemoveIdeaNode_2_a() {

        // Run test for adding testIdeaNode2 as a child to testIdeaNode1
        addIdea2asChildofIdea1();

        // Remove testIdeaNode1 completely
        assertTrue("Error: testIdeaNode1 has not been removed correctly",
                this.testMindMap.removeIdeaNode(this.testIdeaNode1));

        // Check if testIdeaNode1's status is marked EIdeaState.DELETED
        assertTrue("Error: testIdeaNode1's state must be EIdeaState.DELETED!",
                this.testIdeaNode1.getIdeaState().equals(EIdeaState.DELETED));

        // Check if testIdeaNode1 is no longer a parent of testIdeaNode2
        assertFalse(
                "Error: testIdeaNode2 must not be a child of testIdeaNode1 anymore!",
                this.testIdeaNode2.getIsChild());

        assertFalse(
                "Error: testIdeaNode2 must not be a child of testIdeaNode1 anymore!",
                this.testIdeaNode2.getParentID() == this.testIdeaNode1
                        .getIdeaID());

        // Check if there is exactly 1 IdeaNodeView left in the MindMap
        assertTrue(
                "Error: there are more or less than 1 IdeaNodes in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().size() == 1);

        // Check if the testIdeaNode2 and is still stored in the
        // MindMap mindMapIdeaNodeList
        assertTrue(
                "Error: testIdeaNode2 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode2));
        assertFalse(
                "Error: testIdeaNode1 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode1));

        // Check if there is exactly 0 Map in the MindMap
        assertTrue("Error: there are more or less than 0 Maps in the MindMap",
                this.testMindMap.getMindMapList().size() == 0);

    }

    /**
     * <p>
     * Test method for MindMap#removeIdeaNode(IdeaNode)
     * </p>
     * 
     * <p>
     * Tested Case: (2) To be removed IdeaNodeView has (b) more than one child,
     * which all are single nodes
     * </p>
     */
    @Test
    public void testRemoveIdeaNode_2_b() {

        // Run test for adding testIdeaNode2 as a child to testIdeaNode1
        addIdea2asChildofIdea1();

        // Add testIdeaNode3 as a child of testIdeaNode1
        addIdea3asChildofIdea1();

        // Remove testIdeaNode1 completely
        assertTrue("Error: testIdeaNode1 has not been removed correctly",
                this.testMindMap.removeIdeaNode(this.testIdeaNode1));

        // Check if testIdeaNode1's status is marked EIdeaState.DELETED
        assertTrue("Error: testIdeaNode1's state must be EIdeaState.DELETED!",
                this.testIdeaNode1.getIdeaState().equals(EIdeaState.DELETED));

        // Check if testIdeaNode1 is no longer a parent of testIdeaNode2 and
        // testIdeaNode3
        assertFalse(
                "Error: testIdeaNode2 must not be a child of testIdeaNode1 anymore!",
                this.testIdeaNode2.getIsChild());

        assertFalse(
                "Error: testIdeaNode2 must not be a child of testIdeaNode1 anymore!",
                this.testIdeaNode2.getParentID() == this.testIdeaNode1
                        .getIdeaID());

        assertFalse(
                "Error: testIdeaNode3 must not be a child of testIdeaNode1 anymore!",
                this.testIdeaNode3.getIsChild());

        assertFalse(
                "Error: testIdeaNode3 must not be a child of testIdeaNode1 anymore!",
                this.testIdeaNode3.getParentID() == this.testIdeaNode1
                        .getIdeaID());

        // Check if there are exactly 2 IdeaNodes left in the MindMap
        assertTrue(
                "Error: there are more or less than 2 IdeaNodes in the MindMap, in fact there are "
                        + this.testMindMap.getMindMapIdeaNodeList().size()
                        + " and the IdeaNodes are "
                        + this.testMindMap.getMindMapIdeaNodeList(),
                this.testMindMap.getMindMapIdeaNodeList().size() == 2);

        // Check if the testIdeaNode2 and is still stored in the
        // MindMap mindMapIdeaNodeList
        assertTrue(
                "Error: testIdeaNode2 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode2));
        assertTrue(
                "Error: testIdeaNode3 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode3));
        assertFalse(
                "Error: testIdeaNode1 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode1));

        // Check if there is exactly 0 Map in the MindMap
        assertTrue("Error: there are more or less than 0 Maps in the MindMap",
                this.testMindMap.getMindMapList().size() == 0);

    }

    /**
     * <p>
     * Test method for MindMap#removeIdeaNode(IdeaNode)
     * </p>
     * 
     * <p>
     * Tested Case: (2) To be removed IdeaNodeView has (c) more than one child,
     * at least one of them has a child itself
     * </p>
     */
    @Test
    public void testRemoveIdeaNode_2_c() {

        // Run test for adding testIdeaNode2 as a child to testIdeaNode1
        addIdea2asChildofIdea1();

        // Add two new IdeaNodes
        addIdea3andIdea4();

        // Add fourth Idea as child of third idea
        addIdea4asChildofIdea3();

        // Add third Idea as child of Idea one
        addIdea3AsChildofIdea1();

        // Remove testIdeaNode1 completely
        assertTrue("Error: testIdeaNode1 has not been removed correctly",
                this.testMindMap.removeIdeaNode(this.testIdeaNode1));

        // Check if testIdeaNode1's status is marked EIdeaState.DELETED
        assertTrue("Error: testIdeaNode1's state must be EIdeaState.DELETED!",
                this.testIdeaNode1.getIdeaState().equals(EIdeaState.DELETED));

        // Check if testIdeaNode1 is no longer a parent of testIdeaNode2 and
        // testIdeaNode3
        assertFalse(
                "Error: testIdeaNode2 must not be a child of testIdeaNode1 anymore!",
                this.testIdeaNode2.getIsChild());

        assertFalse(
                "Error: testIdeaNode2 must not be a child of testIdeaNode1 anymore!",
                this.testIdeaNode2.getParentID() == this.testIdeaNode1
                        .getIdeaID());

        assertFalse(
                "Error: testIdeaNode3 must not be a child of testIdeaNode1 anymore!",
                this.testIdeaNode3.getIsChild());

        assertFalse(
                "Error: testIdeaNode3 must not be a child of testIdeaNode1 anymore!",
                this.testIdeaNode3.getParentID() == this.testIdeaNode1
                        .getIdeaID());

        // Check if there are exactly 3 IdeaNodes left in the MindMap
        assertTrue(
                "Error: there are more or less than 3 IdeaNodes in the MindMap, in fact there are "
                        + this.testMindMap.getMindMapIdeaNodeList().size()
                        + " and the IdeaNodes are "
                        + this.testMindMap.getMindMapIdeaNodeList(),
                this.testMindMap.getMindMapIdeaNodeList().size() == 3);

        // Check if the testIdeaNode2, testIdeaNode3 and testIdeaNode 4 are
        // still stored in the
        // MindMap mindMapIdeaNodeList
        assertTrue(
                "Error: testIdeaNode2 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode2));
        assertTrue(
                "Error: testIdeaNode3 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode3));
        assertTrue(
                "Error: testIdeaNode4 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode4));
        assertFalse(
                "Error: testIdeaNode1 cannot be found in the MindMap",
                this.testMindMap.getMindMapIdeaNodeList().contains(
                        this.testIdeaNode1));

        // Check if there is exactly 1 Map in the MindMap
        assertTrue("Error: there are more or less than 0 Maps in the MindMap",
                this.testMindMap.getMindMapList().size() == 1);

        // Check if testIdeaNode3 is the root node of that Map
        assertTrue(
                "Error: the map's root node must be testIdeaNode3!",
                this.testMindMap.getMindMapList().get(0).getRootNode()
                        .equals(this.testIdeaNode3));

    }

}
