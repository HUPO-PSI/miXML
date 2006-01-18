/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package psidev.psi.mi.validator.framework.ontology;

import psidev.psi.mi.validator.framework.ontology.model.OntologyTerm;
import org.apache.log4j.Logger;
import uk.ac.ebi.ook.loader.impl.AbstractLoader;
import uk.ac.ebi.ook.loader.impl.PSILoader;
import uk.ac.ebi.ook.loader.parser.OBOFormatParser;
import uk.ac.ebi.ook.model.implementation.TermBean;
import uk.ac.ebi.ook.model.interfaces.TermRelationship;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

/**
 * Wrapper class that hides the way OLS handles OBO files.
 *
 * @author Samuel Kerrien
 * @version $Id: OboLoader.java,v 1.1 2006/01/18 16:59:10 skerrien Exp $
 * @since <pre>30-Sep-2005</pre>
 */
public class OboLoader extends AbstractLoader {

    /////////////////////////////
    // AbstractLoader's methods

    protected void configure() {
        /**
         * ensure we get the right logger
         */
        logger = Logger.getLogger( OboLoader.class );

        parser = new OBOFormatParser();
        ONTOLOGY_DEFINITION = "PSI MI";
        FULL_NAME = "PSI Molecular Interactions";
        SHORT_NAME = "PSI-MI";
        FQCN = PSILoader.class.getName();
    }

    protected void parse( Object params ) {
        try {
            Vector v = new Vector();
            v.add( (String) params );
            ( (OBOFormatParser) parser ).configure( v );
            parser.parseFile();

        } catch ( Exception e ) {
            logger.fatal( "Parse failed: " + e.getMessage(), e );
        }
    }

    protected void printUsage() {
        // done to comply to AbstractLoader requirements
    }

    //////////////////////////////
    // User's methods

    private OntologyImpl buildOntology() {

        OntologyImpl ontology = new OntologyImpl();

        // 1. convert and index all terms (note: at this stage we don't handle the hierarchy)
        for ( Iterator iterator = ontBean.getTerms().iterator(); iterator.hasNext(); ) {
            TermBean term = (TermBean) iterator.next();

            // convert term into a OboTerm
            OntologyTerm ontologyTerm = new OntologyTerm( term.getIdentifier() );

            // try to split the name into short and long name
            int index = term.getName().indexOf( ':' );
            if ( index != -1 ) {
                // found it !
                String name = term.getName();
                String shortName = name.substring( 0, index ).trim();
                String longName = name.substring( index + 1, name.length() ).trim();

                ontologyTerm.setShortName( shortName );
                ontologyTerm.setFullName( longName );

            } else {
                // not found
                ontologyTerm.setShortName( term.getName() );
                ontologyTerm.setFullName( term.getName() );
            }

            ontologyTerm.setObsolete( term.isObsolete() );

            // TODO OboTerm.setObsoleteMessage( );

            ontology.addTerm( ontologyTerm );
        }

        // 2. build hierarchy based on the relations of the Terms
        for ( Iterator iterator = ontBean.getTerms().iterator(); iterator.hasNext(); ) {
            TermBean term = (TermBean) iterator.next();

            if ( term.getRelationships() != null ) {
                for ( Iterator iterator1 = term.getRelationships().iterator(); iterator1.hasNext(); ) {
                    TermRelationship relation = (TermRelationship) iterator1.next();

                    ontology.addLink( relation.getObjectTerm().getIdentifier(),
                                      relation.getSubjectTerm().getIdentifier() );
                }
            }
        }

        return ontology;
    }


    /**
     * Parse the given OBO file and build a representation of the DAG into an IntactOntology.
     *
     * @param file the input file. It has to exist and to be readable, otherwise it will break.
     *
     * @return a non null IntactOntology.
     */
    public OntologyImpl parseOboFile( File file ) {

        if ( ! file.exists() ) {
            throw new IllegalArgumentException( file.getAbsolutePath() + " doesn't exist." );
        }

        if ( ! file.canRead() ) {
            throw new IllegalArgumentException( file.getAbsolutePath() + " could not be read." );
        }

        //setup vars
        configure();

        //parse obo file
        parse( file.getAbsolutePath() );

        //process into relations
        process();

        return buildOntology();
    }
}