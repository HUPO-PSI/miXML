/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package psidev.psi.mi.validator.extensions.mi25.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import psidev.psi.mi.validator.extensions.mi25.Mi25Ontology;
import psidev.psi.mi.validator.framework.ontology.Ontology;
import psidev.psi.mi.validator.framework.ontology.OntologyImpl;

/**
 * Mi25Ontology Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: Mi25OntologyTest.java,v 1.1 2006/01/18 16:55:44 skerrien Exp $
 * @since <pre>01/05/2006</pre>
 */
public class Mi25OntologyTest extends TestCase {

    public Mi25OntologyTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( Mi25OntologyTest.class );
    }

    /////////////////////
    // Tests

    public void testIsValidIdentifier() {

        Ontology o = new OntologyImpl();
        Mi25Ontology ontology = new Mi25Ontology( o );

        assertTrue( ontology.isValidIdentifier( "MI:0000" ) );
        assertTrue( ontology.isValidIdentifier( "MI:9999" ) );

        assertFalse( ontology.isValidIdentifier( "FOO:BAR" ) );
        assertFalse( ontology.isValidIdentifier( "MI:" ) );
        assertFalse( ontology.isValidIdentifier( "MI:0" ) );
        assertFalse( ontology.isValidIdentifier( "MI:00" ) );
        assertFalse( ontology.isValidIdentifier( "MI:000" ) );
        assertFalse( ontology.isValidIdentifier( "MI:00000" ) );
        assertFalse( ontology.isValidIdentifier( null ) );
    }
}
