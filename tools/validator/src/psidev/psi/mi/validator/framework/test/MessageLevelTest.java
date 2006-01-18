/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package psidev.psi.mi.validator.framework.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import psidev.psi.mi.validator.framework.MessageLevel;

/**
 * MessageLevel Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: MessageLevelTest.java,v 1.1 2006/01/18 16:59:10 skerrien Exp $
 * @since <pre>01/11/2006</pre>
 */
public class MessageLevelTest extends TestCase {
    public MessageLevelTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( MessageLevelTest.class );
    }


    /////////////////////
    // Tests
    public void testIsLower() {
        assertTrue( MessageLevel.DEBUG.isLower( MessageLevel.FATAL ) );
        assertTrue( MessageLevel.DEBUG.isLower( MessageLevel.ERROR ) );
        assertTrue( MessageLevel.DEBUG.isLower( MessageLevel.WARN ) );
        assertTrue( MessageLevel.DEBUG.isLower( MessageLevel.INFO ) );

        assertTrue( MessageLevel.INFO.isLower( MessageLevel.FATAL ) );
        assertTrue( MessageLevel.INFO.isLower( MessageLevel.ERROR ) );
        assertTrue( MessageLevel.INFO.isLower( MessageLevel.WARN ) );

        assertTrue( MessageLevel.WARN.isLower( MessageLevel.FATAL ) );
        assertTrue( MessageLevel.WARN.isLower( MessageLevel.ERROR ) );

        assertTrue( MessageLevel.ERROR.isLower( MessageLevel.FATAL ) );
    }

    public void testIsHigher() {
        assertFalse( MessageLevel.DEBUG.isHigher( MessageLevel.FATAL ) );
        assertFalse( MessageLevel.DEBUG.isHigher( MessageLevel.ERROR ) );
        assertFalse( MessageLevel.DEBUG.isHigher( MessageLevel.WARN ) );
        assertFalse( MessageLevel.DEBUG.isHigher( MessageLevel.INFO ) );

        assertFalse( MessageLevel.INFO.isHigher( MessageLevel.FATAL ) );
        assertFalse( MessageLevel.INFO.isHigher( MessageLevel.ERROR ) );
        assertFalse( MessageLevel.INFO.isHigher( MessageLevel.WARN ) );

        assertFalse( MessageLevel.WARN.isHigher( MessageLevel.FATAL ) );
        assertFalse( MessageLevel.WARN.isHigher( MessageLevel.ERROR ) );

        assertFalse( MessageLevel.ERROR.isHigher( MessageLevel.FATAL ) );
    }

    public void testIsSame() {
        assertTrue( MessageLevel.DEBUG.isSame( MessageLevel.DEBUG ) );
        assertTrue( MessageLevel.INFO.isSame( MessageLevel.INFO ) );
        assertTrue( MessageLevel.WARN.isSame( MessageLevel.WARN ) );
        assertTrue( MessageLevel.ERROR.isSame( MessageLevel.ERROR ) );
        assertTrue( MessageLevel.FATAL.isSame( MessageLevel.FATAL ) );

        assertFalse( MessageLevel.ERROR.isSame( MessageLevel.FATAL ) );
    }

    public void testToString() {
        assertNotNull( MessageLevel.DEBUG.toString() );
        assertNotNull( MessageLevel.INFO.toString() );
        assertNotNull( MessageLevel.WARN.toString() );
        assertNotNull( MessageLevel.ERROR.toString() );
        assertNotNull( MessageLevel.FATAL.toString() );
    }

    public void testForName() {
        assertEquals( MessageLevel.INFO, MessageLevel.forName( "info" ) );
        assertEquals( MessageLevel.INFO, MessageLevel.forName( "Info" ) );
        assertEquals( MessageLevel.INFO, MessageLevel.forName( "INFO" ) );

        assertEquals( MessageLevel.DEBUG, MessageLevel.forName( "debug" ) );
        assertEquals( MessageLevel.WARN, MessageLevel.forName( "warn" ) );
        assertEquals( MessageLevel.ERROR, MessageLevel.forName( "error" ) );
        assertEquals( MessageLevel.FATAL, MessageLevel.forName( "fatal" ) );
    }
}
