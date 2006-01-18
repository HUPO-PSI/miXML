package psidev.psi.mi.validator.framework.ontology;

import psidev.psi.mi.validator.framework.ontology.model.OntologyTerm;

import java.util.Collection;

/**
 * <b> Behaviour of an Ontology </b>.
 * <p/>
 *
 * @author Matthias Oesterheld
 * @version $Id: Ontology.java,v 1.1 2006/01/18 16:59:10 skerrien Exp $
 * @since 04.01.2006; 18:34:54
 */
public interface Ontology {
    boolean hasTerms();

    OntologyTerm search( String id );

    Collection getRoots();

    Collection getOntologyTerms();

    Collection<OntologyTerm> getObsoleteTerms();
}
