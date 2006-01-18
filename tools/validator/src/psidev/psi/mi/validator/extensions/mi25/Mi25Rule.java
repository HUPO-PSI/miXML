package psidev.psi.mi.validator.extensions.mi25;

import psidev.psi.mi.validator.framework.Rule;
import psidev.psi.mi.validator.framework.ValidatorException;

import java.util.Map;

/**
 * <b> PSI-MI 2.5.1 Specific Rule </b>.
 * <p/>
 * That class is only meant to be extended, it give basic fonctionalities for building PSI-MI 2.5.1 specific rules. </p>
 *
 * @since 04.01.2006; 15:36:52
 * @version $Id: Mi25Rule.java,v 1.1 2006/01/18 16:55:43 skerrien Exp $
 * @author Matthias Oesterheld
 */
public abstract class Mi25Rule extends Rule {

    public Mi25Rule( Map ontologies ) {
        super( ontologies );
    }

    public Mi25Ontology getMiOntology() throws ValidatorException {
        return new Mi25Ontology( getOntology( "MI" ) ); //TODO: Insert correct ID
    }
}