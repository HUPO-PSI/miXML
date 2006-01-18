package psidev.psi.mi.validator.framework.ontology;

import psidev.psi.mi.validator.framework.ontology.model.OntologyTerm;

import java.util.Collection;

/**
 * <b> Decorator for project specific Ontologies </b>.
 * <p/>
 * That class is a Decorator Pattern.<br/> The goal of that class is to allow a developper to extend that class and give
 * specific behaviour to a specialised Ontology.<br/> eg. PsiMiOntology would for instance have a method that allow to
 * get the OntologyTerm corresponding to Interactor Type (which is known to have the id MI:0313), that way,
 * PsiMiOntology encapsulate the knowledge that is specific to the ontology. </p>
 *
 * @author Matthias Oesterheld
 * @version $Id: AbstractOntology.java,v 1.1 2006/01/18 16:59:10 skerrien Exp $
 * @since 04.01.2006; 18:37:05
 */
public abstract class AbstractOntology implements Ontology {

    protected Ontology ontology;

    public AbstractOntology( Ontology ontology ) {
        this.ontology = ontology;
    }

    public boolean hasTerms() {
        return ontology.hasTerms();
    }

    public OntologyTerm search( String id ) {
        return ontology.search( id );
    }

    public Collection getRoots() {
        return ontology.getRoots();
    }

    public Collection getOntologyTerms() {
        return ontology.getOntologyTerms();
    }

    public Collection<OntologyTerm> getObsoleteTerms() {
        return ontology.getObsoleteTerms();
    }
}