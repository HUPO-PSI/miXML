package psidev.psi.mi.validator.framework.ontology;

import psidev.psi.mi.validator.framework.ontology.model.OntologyTerm;

import java.util.*;

/**
 * Holder for an Ontology and provide basic search feature.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: OntologyImpl.java,v 1.1 2006/01/18 16:59:10 skerrien Exp $
 * @since <pre>04-Jan-2006</pre>
 */
public class OntologyImpl implements Ontology {

    ///////////////////////////////
    // Instance variables

    /**
     * Pool of all term contained in that ontology.
     */
    private Collection<OntologyTerm> ontologyTerms = new ArrayList<OntologyTerm>( 1024 );

    /**
     * Mapping of all OboTerm by their ID.
     */
    private Map<String, OntologyTerm> id2ontologyTerm = new HashMap<String, OntologyTerm>( 1024 );

    /**
     * Collection of root terms of that ontology. A root term is defined as follow: term having no parent.
     */
    private Collection<OntologyTerm> roots = null;

    /////////////////////////////
    // Public methods

    /**
     * Add a new Term in the pool. It will be indexed by its ID.
     *
     * @param term the OntologyTerm to add in that Ontology.
     */
    public void addTerm( OntologyTerm term ) {

        ontologyTerms.add( term );
        String id = term.getId();
        if ( id2ontologyTerm.containsKey( id ) ) {
            OntologyTerm old = (OntologyTerm) id2ontologyTerm.get( id );
            System.err.println( "WARNING: 2 Objects have the same ID (" + id + "), the old one is being replaced." );
            System.err.println( "         old: '" + old.getShortName() + "'" );
            System.err.println( "         new: '" + term.getShortName() + "'" );
        }

        id2ontologyTerm.put( id, term );

        flushRootsCache();
    }

    /**
     * Create a relashionship parent to child between two OntologyTerm.
     *
     * @param parentId The parent term.
     * @param childId  The child term.
     */
    public void addLink( String parentId, String childId ) {

        OntologyTerm child = (OntologyTerm) id2ontologyTerm.get( childId );
        OntologyTerm parent = (OntologyTerm) id2ontologyTerm.get( parentId );

        child.addParent( parent );
        parent.addChild( child );

        flushRootsCache();
    }

    /**
     * Remove the Root cache from memory.<br/> That method should be called every time the collection of OntologyTerm is
     * altered.
     */
    private void flushRootsCache() {
        if ( roots != null ) {
            // flush roots cache
            roots.clear();
            roots = null;
        }
    }

    /**
     * Answer the question: 'Has that ontology any term loaded ?'.
     *
     * @return true is there are any terms loaded, false otherwise.
     */
    public boolean hasTerms() {
        return ontologyTerms.isEmpty();
    }

    /**
     * Search for a OboTerm by its ID.
     *
     * @param id the identifier of the OntologyTerm we are looking for.
     *
     * @return a OntologyTerm or null if not found.
     */
    public OntologyTerm search( String id ) {
        return (OntologyTerm) id2ontologyTerm.get( id );
    }

    /**
     * Get the Root terms of the ontology. The way to get it is as follow: pick a term at random, and go to his highest
     * parent.
     *
     * @return a collection of Root term.
     */
    public Collection getRoots() {

        if ( roots != null ) {
            return roots;
        }

        // it wasn't precalculated, then do it here...
        roots = new HashSet<OntologyTerm>();

        for ( Iterator iterator = ontologyTerms.iterator(); iterator.hasNext(); ) {
            OntologyTerm ontologyTerm = (OntologyTerm) iterator.next();

            if ( ! ontologyTerm.hasParent() ) {
                roots.add( ontologyTerm );
            }
        }

        if ( roots.isEmpty() ) {
            return Collections.EMPTY_LIST;
        }

        return roots;
    }

    /**
     * Get all OboTerm.
     *
     * @return all Ontology term found in the Ontology.
     */
    public Collection getOntologyTerms() {
        return Collections.unmodifiableCollection( ontologyTerms );
    }

    /**
     * Go through the list of all CV Term and select those that are obsolete.
     *
     * @return a non null Collection of obsolete term.
     */
    public Collection<OntologyTerm> getObsoleteTerms() {

        Collection<OntologyTerm> obsoleteTerms = new ArrayList<OntologyTerm>();

        for ( Iterator iterator = getOntologyTerms().iterator(); iterator.hasNext(); ) {
            OntologyTerm ontologyTerm = (OntologyTerm) iterator.next();

            if ( ontologyTerm.isObsolete() ) {
                obsoleteTerms.add( ontologyTerm );
            }
        }

        return obsoleteTerms;
    }

    /////////////////////////////////
    // Utility - Display methods

    public void print() {

        System.out.println( ontologyTerms.size() + " terms to display." );

        Collection roots = getRoots();
        System.out.println( roots.size() + " root(s) found." );

        for ( Iterator iterator = roots.iterator(); iterator.hasNext(); ) {
            OntologyTerm root = (OntologyTerm) iterator.next();

            print( root );
        }
    }

    private void print( OntologyTerm term, String indent ) {

        System.out.println( indent + term.getId() + "   " + term.getShortName() + " (" + term.getFullName() + ")" );
        for ( Iterator iterator = term.getChildren().iterator(); iterator.hasNext(); ) {
            OntologyTerm ontologyTerm = (OntologyTerm) iterator.next();
            print( ontologyTerm, indent + "  " );
        }
    }

    public void print( OntologyTerm term ) {
        print( term, "" );
    }
}