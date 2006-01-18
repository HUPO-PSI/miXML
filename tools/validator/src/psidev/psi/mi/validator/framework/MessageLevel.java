/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package psidev.psi.mi.validator.framework;

/**
 * Give log level facilities, the level are alike Log4J's.<br/> We chose not to import Log4J to keep it simple.<br/>
 * These levels are hierarchical: FATAL > ERROR > WARN > INFO > DEBUG
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: MessageLevel.java,v 1.1 2006/01/18 16:57:59 skerrien Exp $
 * @since <pre>11-Jan-2006</pre>
 */
public enum MessageLevel {

    ///////////////////////////////////////
    // Declaration of the Enums' values

    // TODO match the Log4J spec by adding ALL, OFF, TRACE
    //      cf. http://logging.apache.org/log4j/docs/api/org/apache/log4j/Level.html

    /**
     * The DEBUG Level designates fine-grained informational events that are most useful to debug an application.
     */
    DEBUG( "DEBUG" ),

    /**
     * The INFO level designates informational messages that highlight the progress of the application at coarse-grained level.
     */
    INFO( "INFO" ),

    /**
     * The WARN level designates potentially harmful situations.
     */
    WARN( "WARN" ),

    /**
     * The ERROR level designates error events that might still allow the application to continue running.
     */
    ERROR( "ERROR" ),

    /**
     * The FATAL level designates very severe error events that will presumably lead the application to abort.
     */
    FATAL( "FATAL" );

    /////////////////////////////////
    // Constructor

    private final String name;

    private MessageLevel( String name ) {
        this.name = name;
    }

    public static MessageLevel forName( String level ) {
        if ( INFO.name.equalsIgnoreCase( level ) ) {
            return INFO;
        } else if ( DEBUG.name.equalsIgnoreCase( level ) ) {
            return DEBUG;
        } else if ( WARN.name.equalsIgnoreCase( level ) ) {
            return WARN;
        } else if ( ERROR.name.equalsIgnoreCase( level ) ) {
            return ERROR;
        } else if ( FATAL.name.equalsIgnoreCase( level ) ) {
            return FATAL;
        } else {
            throw new IllegalArgumentException( "That log level (" + level + ") didn't match any of the supported ones." );
        }
    }

    /////////////////////////////
    // Comparison utility

    public boolean isHigher( MessageLevel aLevel ) {
        return ( this.compareTo( aLevel ) > 0 );
    }

    public boolean isSame( MessageLevel aLevel ) {
        return ( this.compareTo( aLevel ) == 0 );
    }

    public boolean isLower( MessageLevel aLevel ) {
        return ( this.compareTo( aLevel ) < 0 );
    }

    /////////////////////////////////
    // Object's overload.

    public String toString() {
        return name;
    }
}