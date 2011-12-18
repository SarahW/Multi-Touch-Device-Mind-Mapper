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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for class {@link de.sarahw.ma.pc.mindMapper.model.NodeMetaData}
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */
@SuppressWarnings("nls")
public class TestNodeMetaData {

    /** Test nodeMetaData */
    NodeMetaData testNodeMetaData;

    /* *********Fixture methods********* */
    /**
     * Sets up the fixture. Instantiates a new NodeMetaData.
     */
    @Before
    public void setUp() {
        this.testNodeMetaData = new NodeMetaData(234, 454, 45.0f,
                EIdeaNodeCreator.BLUETOOTH_SERVER);
    }

    /**
     * Tears down the fixture. Sets all members null.
     */
    @After
    public void tearDown() {
        this.testNodeMetaData = null;
    }

    /* ***********Test methods*********** */
    /**
     * Test method for checking the correct initialization of the members.
     */
    @Test
    public void testMembersAfterInitialization() {

        // Check if NodeMetaData is not null
        assertNotNull("Error: NodeMetaData has not been initialized correctly",
                this.testNodeMetaData);

        // Check position
        assertTrue("Error: xPosition has not been initialized correctly",
                this.testNodeMetaData.getPositionX() == 234f);

        assertTrue("Error: yPosition has not been initialized correctly",
                this.testNodeMetaData.getPositionY() == 454f);

        // Check idea angle
        assertTrue("Error: Idea angle has not been initialized correctly",
                this.testNodeMetaData.getIdeaRotationInDegrees() == 45.0f);

        // Check idea owner
        assertTrue(
                "Error: IdeaOwnerID has not been initialized correctly",
                this.testNodeMetaData.getIdeaOwner().equals(
                        EIdeaNodeCreator.BLUETOOTH_SERVER));

        // Check idea State
        assertTrue("Error: ideaState has not been initialized correctly",
                this.testNodeMetaData.getIdeaState().equals(EIdeaState.ACTIVE));

    }

    /**
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.NodeMetaData#setIdeaRotationInDegrees(float)}
     * .
     */
    @Test
    public void testSetIdeaRotationInDegrees() {
        // Set correct angle minimum
        this.testNodeMetaData.setIdeaRotationInDegrees(-181.0f);
        assertTrue("Error: Angle should be -181.0f",
                this.testNodeMetaData.getIdeaRotationInDegrees() == -181.0f);

        // Set correct angle maximum
        this.testNodeMetaData.setIdeaRotationInDegrees(181.0f);
        assertTrue("Error: Angle should be 181.0f",
                this.testNodeMetaData.getIdeaRotationInDegrees() == 181.0f);

        // Set incorrect angle
        this.testNodeMetaData.setIdeaRotationInDegrees(181.1f);
        assertTrue("Error: Angle should be 181.0f",
                this.testNodeMetaData.getIdeaRotationInDegrees() == 181.0f);

        // Set incorrect angle
        this.testNodeMetaData.setIdeaRotationInDegrees(-181.1f);
        assertTrue("Error: Angle should be -181.1f",
                this.testNodeMetaData.getIdeaRotationInDegrees() == -181.0f);

    }

    /**
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.NodeMetaData#setIdeaOwner(EIdeaNodeCreator)}
     * .
     */
    @Test
    public void testSetIdeaOwnerID() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.NodeMetaData#setPositionX(float)}
     * .
     */
    @Test
    public void testSetPositionX() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.NodeMetaData#setPositionY(float)}
     * .
     */
    @Test
    public void testSetPositionY() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.NodeMetaData#hashCode()}.
     */
    @Test
    public void testHashCode() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.NodeMetaData#toString()}.
     */
    @Test
    public void testToString() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.NodeMetaData#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject() {
        fail("Not yet implemented");
    }

}
