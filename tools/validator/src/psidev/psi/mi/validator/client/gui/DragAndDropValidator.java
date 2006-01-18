/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package psidev.psi.mi.validator.client.gui;

import psidev.psi.mi.validator.client.gui.dnd.DragAndDropComponent;
import psidev.psi.mi.validator.client.gui.dnd.FilesDroppedListener;
import psidev.psi.mi.validator.client.gui.view.validator.ValidatorTable;
import psidev.psi.mi.validator.client.gui.view.validator.ValidatorTableRow;
import psidev.psi.mi.validator.extensions.mi25.Mi25Validator;
import psidev.psi.mi.validator.framework.MessageLevel;
import psidev.psi.mi.validator.framework.Validator;
import psidev.psi.mi.validator.framework.ValidatorException;
import psidev.psi.mi.validator.util.Log4jConfigurator;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: DragAndDropValidator.java,v 1.1 2006/01/18 16:51:16 skerrien Exp $
 * @since <pre>17-Jan-2006</pre>
 */
public class DragAndDropValidator {

    /**
     * Runs a sample program that shows dropped files
     */
    public static void main( String[] args ) throws FileNotFoundException, ValidatorException {

        if( args.length == 0 || args.length > 2 ) {
            System.err.println( "Usage: DragAndDropValidator <validator_config_file> [log level]" );
            System.exit( 1 );
        }

        // setup Logging.
        Log4jConfigurator.configure();

        String configFile = args[ 0 ];
        final MessageLevel level;
        if( args.length == 2 ) {
            level = MessageLevel.forName( args[ 1 ] );
            System.out.println( "User requested log level: " + level );
        } else {
            level = MessageLevel.WARN; // default
            System.out.println( "Message Level set to default: " + level );
        }

        FileInputStream configStream = new FileInputStream( configFile );
        Validator validator = new Mi25Validator( configStream );

        //Create and set up the window.
        JFrame frame = new javax.swing.JFrame( "PSI-MI 2.5 Drag & Drop Validator" );

        //Create and set up the content pane.
        final ValidatorTable validatorTable = new ValidatorTable( validator );
        validatorTable.setOpaque( true ); //content panes must be opaque
        frame.setContentPane( validatorTable );

        // Handle the dropped files
        PrintStream out = null; // could be System.out
        new DragAndDropComponent( out, validatorTable, new FilesDroppedListener() {

            public void filesDropped( java.io.File[] files ) {

                for ( int i = 0; i < files.length; i++ ) {

                    ValidatorTableRow row = new ValidatorTableRow( files[ i ], level, true );

                    validatorTable.addTableRow( row );
                }   // end for: through each dropped file
            }   // end filesDropped
        } ); // end FileDrop.Listener

        // Set up the window.
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setBounds( 100, 100, 50, 50 ); // the size of the frame is defined by its internal component.

        frame.pack();
        frame.setVisible( true );
    }   // end main
}