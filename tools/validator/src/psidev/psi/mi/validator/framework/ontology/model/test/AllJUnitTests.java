/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package psidev.psi.mi.validator.framework.ontology.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * All tests related to the ontology.model package.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: AllJUnitTests.java,v 1.1 2006/01/18 17:01:11 skerrien Exp $
 * @since <pre>16-Jan-2006</pre>
 */
public class AllJUnitTests extends TestCase {

    public AllJUnitTests( String name ) {

        super( name );
    }

    public static Test suite() {

        TestSuite suite = new TestSuite();

        //only one class to test in this package (so far!)
        suite.addTest( OntologyTermTest.suite() );

        return suite;
    }
}