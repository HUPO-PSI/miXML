/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package psidev.psi.mi.validator.framework.ontology.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import psidev.psi.mi.validator.framework.ontology.model.OntologyTerm;

import java.util.Collection;

/**
 * OboTerm Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: OntologyTermTest.java,v 1.1 2006/01/18 17:01:11 skerrien Exp $
 * @since <pre>01/04/2006</pre>
 */
public class OntologyTermTest extends TestCase {

    public OntologyTermTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();

        buildSimpleDag();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( OntologyTermTest.class );
    }

    // Utility

    OntologyTerm mi0;
    OntologyTerm mi1;
    OntologyTerm mi2;
    OntologyTerm mi3;
    OntologyTerm mi4;
    OntologyTerm mi5;
    OntologyTerm mi6;

    private void buildSimpleDag() {

        /*
         *                  MI:0000
         *             /       |     \
         *     MI:0001     MI:0002     MI:0003
         *           \     /               |
         *           MI:0004           MI:0005
         *               |
         *           MI:0006
         *
         */

        mi0 = new OntologyTerm( "MI:0000" );
        mi1 = new OntologyTerm( "MI:0001" );
        mi2 = new OntologyTerm( "MI:0002" );
        mi3 = new OntologyTerm( "MI:0003" );
        mi4 = new OntologyTerm( "MI:0004" );
        mi5 = new OntologyTerm( "MI:0005" );
        mi6 = new OntologyTerm( "MI:0006" );

        // build hierarchy
        mi0.addChild( mi1 );
        mi1.addParent( mi0 );

        mi0.addChild( mi2 );
        mi2.addParent( mi0 );

        mi0.addChild( mi3 );
        mi3.addParent( mi0 );

        mi1.addChild( mi4 );
        mi4.addParent( mi1 );

        mi2.addChild( mi4 );
        mi4.addParent( mi2 );

        mi3.addChild( mi5 );
        mi5.addParent( mi3 );

        mi4.addChild( mi6 );
        mi6.addParent( mi4 );
    }

    /////////////////////
    // Tests

    public void testGetId() {
        OntologyTerm term = new OntologyTerm( "MI" );
        assertEquals( "MI", term.getId() );
    }

    public void testGetShortName() {
        OntologyTerm term = new OntologyTerm( "MI" );
        term.setShortName( "short" );
        assertEquals( "short", term.getShortName() );
    }

    public void testGetFullName() {
        OntologyTerm term = new OntologyTerm( "MI" );
        term.setFullName( "long" );
        assertEquals( "long", term.getFullName() );
    }

    public void testGetParents() {
        Collection parents = mi4.getParents();
        assertEquals( 2, parents.size() );
        assertTrue( parents.contains( mi1 ) );
        assertTrue( parents.contains( mi2 ) );

        parents = mi0.getParents();
        assertTrue( parents.isEmpty() );
    }

    public void testGetChildren() {
        Collection children = mi0.getChildren();
        assertEquals( 3, children.size() );
        assertTrue( children.contains( mi1 ) );
        assertTrue( children.contains( mi2 ) );
        assertTrue( children.contains( mi3 ) );

        children = mi6.getChildren();
        assertTrue( children.isEmpty() );
    }

    public void testIsObsolete() {
        OntologyTerm term = new OntologyTerm( "MI" );
        assertFalse( term.isObsolete() );

        term.setObsolete( true );
        assertTrue( term.isObsolete() );
    }

    public void testGetObsoleteMessage() {
        //TODO: Test of getObsoleteMessage should go here...
    }

//    public void testSetId() {
//        //TODO: Test of setId should go here...
//    }
//
//    public void testSetShortName() {
//        //TODO: Test of setShortName should go here...
//    }
//
//    public void testSetFullName() {
//        //TODO: Test of setFullName should go here...
//    }
//
//    public void testAddParent() {
//        //TODO: Test of addParent should go here...
//    }
//
//    public void testAddChild() {
//        //TODO: Test of addChild should go here...
//    }
//
//    public void testSetObsolete() {
//        //TODO: Test of setObsolete should go here...
//    }
//
//    public void testSetObsoleteMessage() {
//        //TODO: Test of setObsoleteMessage should go here...
//    }

    public void testEquals() {
        assertEquals( mi1, mi1 );
        assertNotSame( mi0, mi1 );
    }

    public void testToString() {
        OntologyTerm term = new OntologyTerm( "MI" );
        assertNotNull( term.toString() );

        term.setShortName( "" );
        assertNotNull( term.toString() );
        term.setFullName( "" );
        assertNotNull( term.toString() );
        term.setObsolete( true );
        assertNotNull( term.toString() );
    }

    public void testGetAllChildren() {
        Collection childrenOfMi0 = mi0.getAllChildren();
        assertEquals( 6, childrenOfMi0.size() );

        assertFalse( childrenOfMi0.contains( mi0 ) );

        assertTrue( childrenOfMi0.contains( mi1 ) );
        assertTrue( childrenOfMi0.contains( mi2 ) );
        assertTrue( childrenOfMi0.contains( mi3 ) );
        assertTrue( childrenOfMi0.contains( mi4 ) );
        assertTrue( childrenOfMi0.contains( mi5 ) );
        assertTrue( childrenOfMi0.contains( mi6 ) );
    }

    public void testIsChildOf() {

        assertTrue( mi0.isChildOf( mi0 ) );
        assertTrue( mi1.isChildOf( mi0 ) );
        assertTrue( mi2.isChildOf( mi0 ) );
        assertTrue( mi3.isChildOf( mi0 ) );
        assertTrue( mi4.isChildOf( mi0 ) );
        assertTrue( mi5.isChildOf( mi0 ) );
        assertTrue( mi6.isChildOf( mi0 ) );

        assertTrue( mi4.isChildOf( mi1 ) );
        assertTrue( mi4.isChildOf( mi2 ) );

        assertTrue( mi5.isChildOf( mi3 ) );

        assertTrue( mi6.isChildOf( mi4 ) );
        assertTrue( mi6.isChildOf( mi1 ) );
        assertTrue( mi6.isChildOf( mi2 ) );

        // negative cases
        assertFalse( mi0.isChildOf( mi1 ) );
        assertFalse( mi0.isChildOf( mi2 ) );
        assertFalse( mi0.isChildOf( mi3 ) );
        assertFalse( mi0.isChildOf( mi4 ) );
        assertFalse( mi0.isChildOf( mi5 ) );
        assertFalse( mi0.isChildOf( mi6 ) );
    }

    public void testIsParentOf() {
        assertTrue( mi0.isParentOf( mi0 ) );
        assertTrue( mi0.isParentOf( mi1 ) );
        assertTrue( mi0.isParentOf( mi2 ) );
        assertTrue( mi0.isParentOf( mi3 ) );
        assertTrue( mi0.isParentOf( mi4 ) );
        assertTrue( mi0.isParentOf( mi5 ) );
        assertTrue( mi0.isParentOf( mi6 ) );

        assertTrue( mi1.isParentOf( mi4 ) );
        assertTrue( mi1.isParentOf( mi6 ) );

        assertTrue( mi2.isParentOf( mi4 ) );
        assertTrue( mi2.isParentOf( mi6 ) );

        assertTrue( mi3.isParentOf( mi5 ) );

        assertTrue( mi4.isParentOf( mi6 ) );

        // Negative cases
        assertFalse( mi1.isParentOf( mi0 ) );
        assertFalse( mi2.isParentOf( mi0 ) );
        assertFalse( mi3.isParentOf( mi0 ) );
        assertFalse( mi4.isParentOf( mi0 ) );
        assertFalse( mi5.isParentOf( mi0 ) );
        assertFalse( mi6.isParentOf( mi0 ) );
    }

    public void testHasParent() {
        assertTrue( mi1.hasParent() );
        assertFalse( mi0.hasParent() );
    }

    public void testHasChildren() {
        assertTrue( mi0.hasChildren() );
        assertFalse( mi6.hasChildren() );
    }
}
