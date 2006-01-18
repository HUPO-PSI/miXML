/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package psidev.psi.mi.validator.framework.ontology.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Simple representation of an OBO term
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: OntologyTerm.java,v 1.1 2006/01/18 17:00:21 skerrien Exp $
 * @since <pre>04-Jan-2006</pre>
 */
public class OntologyTerm {

    private String id;

    private String shortName;
    private String fullName;

    private Collection<OntologyTerm> parents = new HashSet<OntologyTerm>( 2 );
    private Collection<OntologyTerm> children = new HashSet<OntologyTerm>( 2 );

    private boolean obsolete = false;
    private String obsoleteMessage;

    /////////////////////////////
    // Constructor

    public OntologyTerm() {
    }

    public OntologyTerm( String id ) {
        setId( id );
    }

    //////////////////////
    // Getters

    public String getId() {
        return id;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public Collection getParents() {
        return parents;
    }

    public Collection getChildren() {
        return children;
    }

    public boolean isObsolete() {
        return obsolete;
    }

    public String getObsoleteMessage() {
        return obsoleteMessage;
    }

    ///////////////////
    // Setters

    public void setId( String id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "ID can't be null" );
        }

        this.id = id;
    }

    public void setShortName( String shortName ) {
        if ( shortName != null ) {
            shortName = shortName.trim();
        }
        this.shortName = shortName;
    }

    public void setFullName( String fullName ) {
        if ( fullName != null ) {
            fullName = fullName.trim();
        }
        this.fullName = fullName;
    }

    public void addParent( OntologyTerm parent ) {
        parents.add( parent );
    }

    public void addChild( OntologyTerm child ) {
        children.add( child );
    }

    public void setObsolete( boolean obsolete ) {
        this.obsolete = obsolete;
    }

    public void setObsoleteMessage( String obsoleteMessage ) {
        this.obsoleteMessage = obsoleteMessage;
    }

    /////////////////////////
    // Equals / hashCode
    // we only use id and it should not be null

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final OntologyTerm ontologyTerm = (OntologyTerm) o;

        if ( !id.equals( ontologyTerm.id ) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return id.hashCode();
    }

    //////////////////////////
    // Display

    public String toString() {
        final StringBuffer sb = new StringBuffer( 128 );
        sb.append( "OboTerm" );
        sb.append( "{id='" ).append( id ).append( '\'' );
        sb.append( ", shortName='" ).append( shortName ).append( '\'' );
        sb.append( ", fullName='" ).append( fullName ).append( '\'' );

        sb.append( ", parents=" );
        for ( Iterator iterator = parents.iterator(); iterator.hasNext(); ) {
            OntologyTerm ontologyTerm = (OntologyTerm) iterator.next();
            sb.append( ontologyTerm.getShortName() ).append( "(" ).append( ontologyTerm.getId() ).append( ")" );
            if ( iterator.hasNext() ) {
                sb.append( ", " );
            }
        }

        sb.append( ", children=" );
        for ( Iterator iterator = children.iterator(); iterator.hasNext(); ) {
            OntologyTerm ontologyTerm = (OntologyTerm) iterator.next();
            sb.append( ontologyTerm.getShortName() ).append( "(" ).append( ontologyTerm.getId() ).append( ")" );
            if ( iterator.hasNext() ) {
                sb.append( ", " );
            }
        }

        sb.append( ", obsolete=" ).append( obsolete );
        sb.append( ", obsoleteMessage='" ).append( obsoleteMessage ).append( '\'' );

        return sb.toString();
    }

    ////////////////////////////
    // Utility

    /**
     * @param term
     * @param children
     */
    private void getAllChildren( OntologyTerm term, Collection<OntologyTerm> children ) {

        children.add( term );

        for ( Iterator iterator = term.getChildren().iterator(); iterator.hasNext(); ) {
            OntologyTerm child = (OntologyTerm) iterator.next();
            getAllChildren( child, children );
        }
    }

    public Set<OntologyTerm> getAllChildren() {
        Set<OntologyTerm> children = new HashSet<OntologyTerm>();

        // build the union of all children's children
        for ( Iterator iterator = this.children.iterator(); iterator.hasNext(); ) {
            OntologyTerm child = (OntologyTerm) iterator.next();
            getAllChildren( child, children );
        }

        return children;
    }

    public boolean isChildOf( OntologyTerm parent ) {

        if ( this.equals( parent ) ) {
            return true;
        }

        // we check that this term is a child of the given parent
        for ( Iterator iterator = parents.iterator(); iterator.hasNext(); ) {
            OntologyTerm _parent = (OntologyTerm) iterator.next();

            if ( _parent.isChildOf( parent ) ) {
                return true;
            }
        }

        return false;
    }

    public boolean isParentOf( OntologyTerm child ) {
        return child.isChildOf( this );
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public boolean hasParent() {
        return !parents.isEmpty();
    }
}